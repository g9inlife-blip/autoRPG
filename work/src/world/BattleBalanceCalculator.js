(() => {
  const CONFIG = {
    // Boss HP is derived from expected party damage × target TTK.
    // TTK is intentionally configurable instead of reverse-fitting old HP.
    targetRoundsByDungeon: level => 10 + Math.floor(Math.max(1, Number(level) || 1) / 5),
    minBossHp: 1,
    maxModelHp: 5000000,
    validationRuns: 1000
  };

  function leveledCharacter(level, classId) {
    const f = new CharacterFactory();
    const base = window.CHARACTER_CLASSES?.[classId] || {};
    const c = f.create({
      id: `balance-${classId}-${level}`,
      name: classId,
      team: 'player',
      classId,
      hp: base.hp,
      maxHp: base.hp,
      attack: base.attack,
      defense: base.defense,
      speed: base.speed,
      attributes: { ...(base.attributes || {}) }
    });
    const target = Math.max(1, Math.min(50, Number(level) || 1));
    for (let n = 1; n < target; n++) window.CharacterProgression?.applyLevel?.(c);
    c.level = target;
    c.hp = c.maxHp;
    return c;
  }

  function equipBest(c, level) {
    const loader = window.EquipmentXmlLoader;
    if (!loader?.state?.loaded) return c;
    const score = {
      weapon: i => Number(i.attack) || 0,
      armor: i => Number(i.defense) || 0,
      accessory: i => (Number(i.attack) || 0) + (Number(i.defense) || 0)
    };
    for (const slot of ['weapon', 'armor', 'accessory']) {
      const pool = loader.pool(level, { slot }).filter(i => c.canEquip(i).ok);
      if (!pool.length) continue;
      pool.sort((a, b) => score[slot](b) - score[slot](a));
      c.equip(pool[0]);
    }
    return c;
  }

  function attackRange(level, classId, defenderDefense = 0) {
    const c = equipBest(leveledCharacter(level, classId), level);
    const atk = Number(c.attack) || 0;
    const d = Number(defenderDefense) || 0;
    return {
      minDamage: Math.max(1, atk + 2 - d),
      maxDamage: Math.max(1, atk + 12 - d),
      attack: atk,
      defense: Number(c.defense) || 0,
      hp: Number(c.maxHp) || Number(c.hp) || 0,
      skills: Array.isArray(c.skills) ? c.skills : []
    };
  }

  // Exact expected value for the current DamageCalculator's 2d6 / hit / crit rules.
  function expectedDamagePerAttack(attack, defense, multiplier = 1) {
    let total = 0;
    for (let d1 = 1; d1 <= 6; d1++) {
      for (let d2 = 1; d2 <= 6; d2++) {
        const roll = d1 + d2;
        if (roll === 2) continue; // fumble
        const raw = Math.max(1, Number(attack) + roll - Number(defense));
        if (roll === 12) {
          total += raw * 2 * multiplier;
        } else {
          total += raw * 0.85 * multiplier;
        }
      }
    }
    return total / 36;
  }

  function expectedDamagePerRound(character, defenderDefense) {
    const base = expectedDamagePerAttack(character.attack, defenderDefense, 1);
    const skillId = character.skills?.[0];
    const skill = window.SKILL_REGISTRY?.[skillId];
    if (!skill) return base;

    const cooldown = Math.max(0, Number(skill.cooldown) || 0);
    const skillHits = skill.extraHit ? 2 : 1;
    const skillDamage = expectedDamagePerAttack(
      character.attack,
      defenderDefense,
      Number(skill.multiplier) || 1
    ) * skillHits;
    // BattleEngine sets cooldown N, then decrements once each following round,
    // so a skill with cooldown N fires once every N+1 rounds.
    return (skillDamage + base * cooldown) / (cooldown + 1);
  }

  function partyReference(level, defenderDefense = 0) {
    const classes = ['warrior', 'mage', 'rogue'];
    const characters = classes.map(classId => {
      const r = attackRange(level, classId, defenderDefense);
      return {
        id: classId,
        classId,
        ...r,
        expectedDpr: expectedDamagePerRound(r, defenderDefense),
        referenceDamage: expectedDamagePerRound(r, defenderDefense)
      };
    });
    const partyDpr = characters.reduce((sum, c) => sum + c.expectedDpr, 0);
    return {
      level,
      defense: defenderDefense,
      characters,
      partyDpr,
      // Kept as an explicit alias for existing UI/API consumers.
      partyDamage: partyDpr
    };
  }

  function dungeonBoss(dungeon) {
    const areas = Array.isArray(dungeon?.areas) ? dungeon.areas : [];
    const last = Math.max(
      ...areas.map(a => Number(a.area) || 0),
      Number(dungeon?.areaCount) || Number(dungeon?.floors) || 0
    );
    const e = areas.find(a => Number(a.area) === last)?.encounters || [];
    const names = e.flatMap(x => x.enemies || x.monsters || [])
      .map(x => typeof x === 'string' ? x : x.name)
      .filter(Boolean);
    return names
      .map(name => window.MonsterXmlLoader?.state?.items?.find(
        m => m.dungeonName === (dungeon.monsterDungeonName || dungeon.name) && m.name === name
      ))
      .filter(Boolean)
      .find(m => String(m.grade).toLowerCase() === 'boss' || String(m.form).toLowerCase() === 'boss') || null;
  }

  function levelForDungeon(dungeon) {
    return Math.max(1, Math.min(50,
      Number(dungeon?.levelEnd || dungeon?.levelStart || dungeon?.levelMap?.slice(-1)[0] || 1)
    ));
  }

  function makeParty(level) {
    return ['warrior', 'mage', 'rogue']
      .map(classId => equipBest(leveledCharacter(level, classId), level));
  }

  function survivalReference(dungeon, party) {
    const boss = dungeonBoss(dungeon);
    if (!boss) return null;
    const attack = Number(boss.attack) || 0;
    const members = party.map(c => {
      const hp = Number(c.maxHp) || Number(c.hp) || 1;
      const defense = Number(c.defense) || 0;
      const dpr = expectedDamagePerAttack(attack, defense, 1);
      return {
        classId: c.classId,
        hp,
        defense,
        incomingDpr: dpr,
        estimatedSurvivalRounds: dpr > 0 ? hp / dpr : Infinity
      };
    });
    return {
      members,
      weakestSurvivalRounds: Math.min(...members.map(x => x.estimatedSurvivalRounds))
    };
  }

  function calculateBossHp(dungeon, { targetRounds = null } = {}) {
    const boss = dungeonBoss(dungeon);
    if (!boss) return null;
    const level = levelForDungeon(dungeon);
    const party = makeParty(level);
    const reference = partyReference(level, Number(boss.defense) || 0);
    const rounds = Math.max(1, Number(targetRounds) || CONFIG.targetRoundsByDungeon(level));
    const modelHp = Math.max(CONFIG.minBossHp, Math.round(reference.partyDpr * rounds));
    const survival = survivalReference(dungeon, party);
    return {
      dungeon: dungeon.name,
      level,
      targetRounds: rounds,
      boss: {
        id: boss.id,
        name: boss.name,
        oldHp: Number(boss.maxHp) || 0,
        attack: Number(boss.attack) || 0,
        defense: Number(boss.defense) || 0
      },
      reference,
      survival,
      modelHp: Math.min(CONFIG.maxModelHp, modelHp),
      balance: {
        expectedClearRounds: reference.partyDpr > 0 ? modelHp / reference.partyDpr : Infinity,
        survivalMarginRounds: survival ? survival.weakestSurvivalRounds - rounds : null,
        survivalRisk: !!survival && survival.weakestSurvivalRounds < rounds
      }
    };
  }

  function simulateBoss(dungeon, bossHp, runs = CONFIG.validationRuns, seed = 12345) {
    const boss = dungeonBoss(dungeon);
    if (!boss) return null;
    const results = { wins: 0, losses: 0, draws: 0, rounds: 0 };
    for (let i = 0; i < runs; i++) {
      const rng = new SeededRandom(Number(seed) + i * 1009);
      const party = makeParty(levelForDungeon(dungeon));
      const enemy = new Character({
        id: `balance-boss-${i}`,
        name: boss.name,
        hp: bossHp,
        maxHp: bossHp,
        attack: Number(boss.attack) || 0,
        defense: Number(boss.defense) || 0,
        speed: 10,
        team: 'enemy',
        level: Number(boss.level) || levelForDungeon(dungeon),
        monsterId: boss.id,
        monsterGrade: boss.grade,
        monsterForm: boss.form
      });
      const engine = new BattleEngine({ rng, damageCalculator: new DamageCalculator(rng) });
      const result = engine.run(party, [enemy]);
      if (result.result === 'WIN') results.wins++;
      else if (result.result === 'LOSE') results.losses++;
      else results.draws++;
      results.rounds += Number(result.round) || 0;
    }
    return {
      runs,
      wins: results.wins,
      losses: results.losses,
      draws: results.draws,
      winRate: results.wins / runs,
      avgRounds: results.rounds / runs
    };
  }

  // 90% is now validation/search, not the primary balancing method.
  // It starts from the mathematical model and expands only when necessary.
  function findBossHp90(dungeon, { runs = CONFIG.validationRuns, seed = 12345, maxHp = null } = {}) {
    const model = calculateBossHp(dungeon, { targetRounds: null });
    if (!model) return null;

    let lo = 1;
    let hi = Math.max(model.modelHp, Number(maxHp) || 0, 1);
    let hiSim = simulateBoss(dungeon, hi, runs, seed);
    let best = null;

    if (hiSim?.winRate >= 0.9) {
      best = { hp: hi, sim: hiSim };
      while (hi < CONFIG.maxModelHp) {
        const next = Math.min(CONFIG.maxModelHp, hi * 2);
        const sim = simulateBoss(dungeon, next, runs, seed);
        if (!sim || sim.winRate < 0.9) break;
        best = { hp: next, sim };
        hi = next;
      }
    } else {
      while (lo <= hi) {
        const mid = Math.floor((lo + hi) / 2);
        const sim = simulateBoss(dungeon, mid, runs, seed);
        if (sim?.winRate >= 0.9) {
          best = { hp: mid, sim };
          lo = mid + 1;
        } else {
          hi = mid - 1;
        }
      }
    }

    return {
      ...model,
      recommendedHp90: best?.hp || null,
      simulation90: best?.sim || null
    };
  }

  function calculateAll(dungeons, options = {}) {
    return (Array.isArray(dungeons) ? dungeons : Object.values(dungeons || {}))
      .filter(Boolean)
      .sort((a, b) => levelForDungeon(a) - levelForDungeon(b))
      .map(dungeon => calculateBossHp(dungeon, options));
  }

  function report(dungeon) {
    return calculateBossHp(dungeon);
  }

  window.BattleBalanceCalculator = {
    CONFIG,
    leveledCharacter,
    equipBest,
    attackRange,
    expectedDamagePerAttack,
    expectedDamagePerRound,
    partyReference,
    dungeonBoss,
    levelForDungeon,
    makeParty,
    survivalReference,
    calculateBossHp,
    calculateAll,
    simulateBoss,
    findBossHp90,
    report
  };
})();
