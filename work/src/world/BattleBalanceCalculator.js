(() => {
  const CONFIG = {
    difficulty: {
      normal: { ttkRounds: 2.5, survivalMultiplier: 2.0 },
      rare: { ttkRounds: 4.0, survivalMultiplier: 1.5 },
      boss: { ttkRounds: 9.0, survivalMultiplier: 1.25 }
    },
    validationRuns: 1000,
    maxAttack: 100000,
    maxHp: 5000000
  };

  function gradeKey(monster) {
    const grade = String(monster?.grade || '').toLowerCase();
    const form = String(monster?.form || '').toLowerCase();
    if (grade === 'boss' || form === 'boss') return 'boss';
    if (grade === 'rare' || grade === 'elite' || form === 'rare' || form === 'elite') return 'rare';
    return 'normal';
  }

  function leveledCharacter(level, classId) {
    const f = new CharacterFactory();
    const base = window.CHARACTER_CLASSES?.[classId] || {};
    const c = f.create({
      id: `balance-${classId}-${level}`,
      name: classId,
      team: 'player', classId,
      hp: base.hp, maxHp: base.hp,
      attack: base.attack, defense: base.defense, speed: base.speed,
      attributes: { ...(base.attributes || {}) }
    });
    const target = Math.max(1, Math.min(50, Number(level) || 1));
    for (let n = 1; n < target; n++) window.CharacterProgression?.applyLevel?.(c);
    c.level = target;
    c.hp = c.maxHp;
    return c;
  }

  function equipmentScore(item, slot) {
    if (slot === 'weapon') return Number(item?.attack) || 0;
    if (slot === 'armor') return Number(item?.defense) || 0;
    return (Number(item?.attack) || 0) + (Number(item?.defense) || 0);
  }

  function equipBest(c, level) {
    return equipByMode(c, level, 'max');
  }

  // Build a fixed level profile from XML equipment data. This never reads the
  // player's current inventory/equipment, so runtime upgrades cannot rescale monsters.
  function equipByMode(c, level, mode = 'max') {
    const loader = window.EquipmentXmlLoader;
    if (!loader?.state?.loaded) return c;
    for (const slot of ['weapon', 'armor', 'accessory']) {
      const pool = loader.pool(level, { slot }).filter(i => c.canEquip(i).ok);
      if (!pool.length) continue;
      pool.sort((a, b) => {
        const delta = equipmentScore(a, slot) - equipmentScore(b, slot);
        return mode === 'min' ? delta : -delta;
      });
      c.equip(pool[0]);
    }
    return c;
  }

  function expectedDamagePerAttack(attack, defense, multiplier = 1) {
    let total = 0;
    for (let d1 = 1; d1 <= 6; d1++) {
      for (let d2 = 1; d2 <= 6; d2++) {
        const roll = d1 + d2;
        if (roll === 2) continue;
        const raw = Math.max(1, Number(attack) + roll - Number(defense));
        total += roll === 12 ? raw * 2 * multiplier : raw * 0.85 * multiplier;
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
      character.attack, defenderDefense, Number(skill.multiplier) || 1
    ) * skillHits;
    return (skillDamage + base * cooldown) / (cooldown + 1);
  }

  function partyProfile(level, defenderDefense = 0, mode = 'max') {
    const characters = ['warrior', 'mage', 'rogue'].map(classId => {
      const c = equipByMode(leveledCharacter(level, classId), level, mode);
      return {
        id: classId, classId,
        attack: Number(c.attack) || 0,
        defense: Number(c.defense) || 0,
        hp: Number(c.maxHp) || Number(c.hp) || 0,
        skills: Array.isArray(c.skills) ? c.skills : [],
        expectedDpr: expectedDamagePerRound(c, defenderDefense)
      };
    });
    return {
      level, mode, defense: defenderDefense, characters,
      partyDpr: characters.reduce((sum, c) => sum + c.expectedDpr, 0)
    };
  }

  function partyReference(level, defenderDefense = 0) {
    const min = partyProfile(level, defenderDefense, 'min');
    const max = partyProfile(level, defenderDefense, 'max');
    const balancedDpr = (min.partyDpr + max.partyDpr) / 2;
    return {
      level,
      defense: defenderDefense,
      min,
      max,
      partyDpr: balancedDpr,
      partyDamage: balancedDpr,
      minDpr: min.partyDpr,
      maxDpr: max.partyDpr
    };
  }

  function targetForMonster(monster) {
    return CONFIG.difficulty[gradeKey(monster)];
  }

  function solveAttackForSurvival(monster, party, targetRounds) {
    const desiredSurvival = targetRounds * targetForMonster(monster).survivalMultiplier;
    const weakest = party.reduce((a, b) => (a.hp < b.hp ? a : b));
    const desiredDpr = weakest.hp / Math.max(1, desiredSurvival);
    const defense = Number(weakest.defense) || 0;
    let lo = 1, hi = CONFIG.maxAttack;
    while (lo < hi) {
      const mid = Math.floor((lo + hi + 1) / 2);
      if (expectedDamagePerAttack(mid, defense) <= desiredDpr) lo = mid;
      else hi = mid - 1;
    }
    return {
      attack: Math.max(1, lo),
      desiredSurvivalRounds: desiredSurvival,
      weakestClassId: weakest.classId,
      weakestHp: weakest.hp,
      weakestDefense: defense,
      expectedIncomingDpr: expectedDamagePerAttack(Math.max(1, lo), defense),
      estimatedSurvivalRounds: weakest.hp / Math.max(0.0001, expectedDamagePerAttack(Math.max(1, lo), defense))
    };
  }

  function calculateMonsterStats(monster, options = {}) {
    if (!monster) return null;
    const level = Math.max(1, Math.min(50, Number(monster.level) || 1));
    const kind = gradeKey(monster);
    const rule = CONFIG.difficulty[kind];
    const party = partyReference(level, Number(monster.defense) || 0);
    const targetRounds = Math.max(1, Number(options.targetRounds) || rule.ttkRounds);
    // HP is fixed from the level's canonical min/max party DPS, not from the
    // currently equipped player. Midpoint keeps the target reachable by both ends.
    const hp = Math.max(1, Math.round(party.partyDpr * targetRounds));
    // Incoming damage is likewise solved from the fixed minimum-performance
    // level profile. A stronger player therefore simply clears this monster faster.
    const survival = solveAttackForSurvival(monster, party.min.characters, targetRounds);
    const result = {
      id: monster.id, name: monster.name, level,
      grade: monster.grade, form: monster.form, kind,
      targetRounds, survivalMultiplier: rule.survivalMultiplier,
      hp: Math.min(CONFIG.maxHp, hp), maxHp: Math.min(CONFIG.maxHp, hp),
      attack: survival.attack,
      defense: Number(monster.defense) || 0,
      exp: Number(monster.exp) || 0,
      sourceHp: Number(monster.maxHp ?? monster.hp) || 0,
      sourceAttack: Number(monster.attack) || 0,
      balance: {
        minPartyDpr: party.minDpr,
        maxPartyDpr: party.maxDpr,
        midpointPartyDpr: party.partyDpr,
        minExpectedClearRounds: party.partyDpr > 0 ? hp / party.maxDpr : Infinity,
        maxExpectedClearRounds: party.partyDpr > 0 ? hp / party.minDpr : Infinity,
        expectedClearRounds: party.partyDpr > 0 ? hp / party.partyDpr : Infinity,
        desiredSurvivalRounds: survival.desiredSurvivalRounds,
        estimatedSurvivalRounds: survival.estimatedSurvivalRounds,
        weakestClassId: survival.weakestClassId,
        weakestHp: survival.weakestHp,
        weakestDefense: survival.weakestDefense,
        survivalMarginRounds: survival.estimatedSurvivalRounds - targetRounds
      },
      profileSource: 'fixed-level-min-max-equipment'
    };
    return result;
  }

  function calculateDungeonMonsters(dungeon) {
    const name = dungeon?.monsterDungeonName || dungeon?.name;
    const items = window.MonsterXmlLoader?.state?.items || [];
    return items.filter(m => m.dungeonName === name).map(m => calculateMonsterStats(m));
  }

  function dungeonBoss(dungeon) {
    const name = dungeon?.monsterDungeonName || dungeon?.name;
    return (window.MonsterXmlLoader?.state?.items || [])
      .filter(m => m.dungeonName === name)
      .find(m => gradeKey(m) === 'boss') || null;
  }

  function levelForDungeon(dungeon) {
    return Math.max(1, Math.min(50,
      Number(dungeon?.levelEnd || dungeon?.levelStart || dungeon?.levelMap?.slice(-1)[0] || 1)
    ));
  }

  function makeParty(level, mode = 'max') {
    return ['warrior', 'mage', 'rogue'].map(classId => equipByMode(leveledCharacter(level, classId), level, mode));
  }

  function survivalReference(dungeon, party) {
    const boss = dungeonBoss(dungeon);
    if (!boss) return null;
    const b = calculateMonsterStats(boss);
    return {
      members: party.map(c => {
        const incomingDpr = expectedDamagePerAttack(b.attack, Number(c.defense) || 0);
        return {
          classId: c.classId, hp: Number(c.maxHp) || Number(c.hp) || 1,
          defense: Number(c.defense) || 0, incomingDpr,
          estimatedSurvivalRounds: incomingDpr > 0 ? (Number(c.maxHp) || Number(c.hp) || 1) / incomingDpr : Infinity
        };
      })
    };
  }

  function calculateBossHp(dungeon, options = {}) {
    const boss = dungeonBoss(dungeon);
    if (!boss) return null;
    const result = calculateMonsterStats(boss, options);
    const party = makeParty(result.level, 'max');
    const survival = survivalReference(dungeon, party);
    return {
      dungeon: dungeon.name, level: result.level,
      targetRounds: result.targetRounds,
      boss: {
        id: boss.id, name: boss.name,
        oldHp: Number(boss.maxHp) || 0,
        oldAttack: Number(boss.attack) || 0,
        oldDefense: Number(boss.defense) || 0,
        hp: result.hp, attack: result.attack, defense: result.defense
      },
      reference: partyReference(result.level, result.defense),
      survival,
      modelHp: result.hp,
      modelAttack: result.attack,
      modelDefense: result.defense,
      balance: result.balance
    };
  }

  function calculateAll(dungeons, options = {}) {
    return (Array.isArray(dungeons) ? dungeons : Object.values(dungeons || {}))
      .filter(Boolean)
      .sort((a, b) => levelForDungeon(a) - levelForDungeon(b))
      .map(d => calculateBossHp(d, options));
  }

  function simulateBoss(dungeon, bossHp, runs = CONFIG.validationRuns, seed = 12345, attack = null) {
    const boss = dungeonBoss(dungeon);
    if (!boss) return null;
    const model = calculateMonsterStats(boss);
    const results = { wins: 0, losses: 0, draws: 0, rounds: 0 };
    for (let i = 0; i < runs; i++) {
      const rng = new SeededRandom(Number(seed) + i * 1009);
      const party = makeParty(model.level, 'max');
      const enemy = new Character({
        id: `balance-boss-${i}`, name: boss.name, hp: bossHp, maxHp: bossHp,
        attack: attack == null ? model.attack : attack,
        defense: model.defense, speed: 10, team: 'enemy', level: model.level,
        monsterId: boss.id, monsterGrade: boss.grade, monsterForm: boss.form
      });
      const result = new BattleEngine({ rng, damageCalculator: new DamageCalculator(rng) }).run(party, [enemy]);
      if (result.result === 'WIN') results.wins++;
      else if (result.result === 'LOSE') results.losses++;
      else results.draws++;
      results.rounds += Number(result.round) || 0;
    }
    return { runs, wins: results.wins, losses: results.losses, draws: results.draws, winRate: results.wins / runs, avgRounds: results.rounds / runs };
  }

  function findBossHp90(dungeon, { runs = CONFIG.validationRuns, seed = 12345 } = {}) {
    const model = calculateBossHp(dungeon);
    if (!model) return null;
    let lo = 1, hi = Math.max(1, model.modelHp * 2), best = null;
    while (lo <= hi) {
      const mid = Math.floor((lo + hi) / 2);
      const sim = simulateBoss(dungeon, mid, runs, seed, model.modelAttack);
      if (sim?.winRate >= 0.9) { best = { hp: mid, sim }; lo = mid + 1; }
      else hi = mid - 1;
    }
    return { ...model, recommendedHp90: best?.hp || null, simulation90: best?.sim || null };
  }

  window.BattleBalanceCalculator = {
    CONFIG, gradeKey, leveledCharacter, equipBest, equipByMode,
    expectedDamagePerAttack, expectedDamagePerRound, partyProfile, partyReference,
    targetForMonster, calculateMonsterStats, calculateDungeonMonsters,
    dungeonBoss, levelForDungeon, makeParty, survivalReference,
    calculateBossHp, calculateAll, simulateBoss, findBossHp90
  };
})();
