(() => {
  const CONFIG = {
    difficulty: {
      // 공격 TTK 완화: 기존 생존목표의 4배. 일반 20R / 희귀 24R / 보스 45R.
      normal: { ttkRounds: 2.5, survivalMultiplier: 8.0 },
      rare: { ttkRounds: 4.0, survivalMultiplier: 6.0 },
      boss: { ttkRounds: 9.0, survivalMultiplier: 5.0 }
    },
    formationWeights: [0.60, 0.30, 0.10],
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

  function equipBest(c, level) { return equipByMode(c, level, 'max'); }

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
    const skillDamage = expectedDamagePerAttack(character.attack, defenderDefense, Number(skill.multiplier) || 1) * skillHits;
    return (skillDamage + base * cooldown) / (cooldown + 1);
  }

  function partyProfile(level, defenderDefense = 0, mode = 'max') {
    const characters = ['warrior', 'mage', 'rogue'].map(classId => {
      const c = equipByMode(leveledCharacter(level, classId), level, mode);
      return { id: classId, classId, attack: Number(c.attack) || 0, defense: Number(c.defense) || 0, hp: Number(c.maxHp) || Number(c.hp) || 0, skills: Array.isArray(c.skills) ? c.skills : [], expectedDpr: expectedDamagePerRound(c, defenderDefense) };
    });
    return {
      level, mode, defense: defenderDefense, characters,
      partyDpr: characters.reduce((sum, c) => sum + c.expectedDpr, 0),
      totalHp: characters.reduce((sum, c) => sum + c.hp, 0),
      averageDefense: characters.length ? characters.reduce((sum, c) => sum + c.defense, 0) / characters.length : 0
    };
  }

  function partyReference(level, defenderDefense = 0) {
    const min = partyProfile(level, defenderDefense, 'min');
    const max = partyProfile(level, defenderDefense, 'max');
    const balancedDpr = (min.partyDpr + max.partyDpr) / 2;
    return { level, defense: defenderDefense, min, max, partyDpr: balancedDpr, partyDamage: balancedDpr, minDpr: min.partyDpr, maxDpr: max.partyDpr, minHp: min.totalHp, maxHp: max.totalHp, minDefense: min.averageDefense, maxDefense: max.averageDefense };
  }

  function targetForMonster(monster) { return CONFIG.difficulty[gradeKey(monster)]; }

  // 실제 전투에서는 몬스터 한 마리가 한 턴에 파티원 한 명만 공격한다.
  // 전열 60 / 중열 30 / 후열 10을 적용하고 생존자가 줄면 남은 비율을 재정규화한다.
  function normalizedFormationWeights(partyLength) {
    const count = Math.max(1, Number(partyLength) || 1);
    const raw = CONFIG.formationWeights.slice(0, count);
    while (raw.length < count) raw.push(CONFIG.formationWeights[CONFIG.formationWeights.length - 1]);
    const total = raw.reduce((sum, weight) => sum + weight, 0);
    return raw.map(weight => weight / Math.max(0.0001, total));
  }

  function expectedIncomingDamagePerRound(attack, party) {
    const members = Array.isArray(party) ? party : [];
    if (!members.length) return 0;
    const weights = normalizedFormationWeights(members.length);
    return members.reduce((sum, c, index) => sum + weights[index] * expectedDamagePerAttack(attack, Number(c.defense) || 0), 0);
  }

  function solveAttackForProfile(party, targetRounds) {
    const members = Array.isArray(party) ? party : [];
    const target = Math.max(1, Number(targetRounds) || 1);
    const totalHp = members.reduce((sum, c) => sum + (Number(c.hp) || 0), 0);
    const weights = normalizedFormationWeights(members.length);
    let lo = 1, hi = CONFIG.maxAttack;
    while (lo < hi) {
      const mid = Math.floor((lo + hi + 1) / 2);
      const incomingDpr = expectedIncomingDamagePerRound(mid, members);
      const survivalRounds = totalHp / Math.max(0.0001, incomingDpr);
      if (survivalRounds >= target) lo = mid;
      else hi = mid - 1;
    }
    const attack = Math.max(1, lo);
    const incomingDpr = expectedIncomingDamagePerRound(attack, members);
    return { attack, totalHp, totalDefense: members.reduce((sum, c) => sum + (Number(c.defense) || 0), 0), averageDefense: members.length ? members.reduce((sum, c) => sum + (Number(c.defense) || 0), 0) / members.length : 0, formationWeights: weights, expectedIncomingDpr: incomingDpr, estimatedSurvivalRounds: totalHp / Math.max(0.0001, incomingDpr) };
  }

  function solveAttackForSurvival(monster, party, targetRounds) {
    const desiredSurvival = targetRounds * targetForMonster(monster).survivalMultiplier;
    const result = solveAttackForProfile(party, desiredSurvival);
    return { attack: result.attack, desiredSurvivalRounds: desiredSurvival, expectedIncomingDpr: result.expectedIncomingDpr, estimatedSurvivalRounds: result.estimatedSurvivalRounds, totalHp: result.totalHp, averageDefense: result.averageDefense, profile: result };
  }

  function solveAttackFromMinMax(minParty, maxParty, targetRounds, survivalMultiplier = 1) {
    const desired = Math.max(1, Number(targetRounds) || 1) * Math.max(1, Number(survivalMultiplier) || 1);
    const min = solveAttackForProfile(minParty, desired);
    const max = solveAttackForProfile(maxParty, desired);
    const attack = Math.max(1, Math.round((min.attack + max.attack) / 2));
    const evaluate = party => {
      const members = Array.isArray(party) ? party : [];
      const incomingDpr = expectedIncomingDamagePerRound(attack, members);
      const totalHp = members.reduce((sum, c) => sum + (Number(c.hp) || 0), 0);
      return { totalHp, expectedIncomingDpr: incomingDpr, estimatedSurvivalRounds: totalHp / Math.max(0.0001, incomingDpr) };
    };
    return { attack, desiredSurvivalRounds: desired, minAttack: min.attack, maxAttack: max.attack, minProfile: min, maxProfile: max, minResult: evaluate(minParty), maxResult: evaluate(maxParty) };
  }

  function calculateMonsterStats(monster, options = {}) {
    if (!monster) return null;
    const level = Math.max(1, Math.min(50, Number(monster.level) || 1));
    const kind = gradeKey(monster);
    const rule = CONFIG.difficulty[kind];
    const party = partyReference(level, Number(monster.defense) || 0);
    const targetRounds = Math.max(1, Number(options.targetRounds) || rule.ttkRounds);
    const hp = Math.max(1, Math.round(party.partyDpr * targetRounds));
    const survival = solveAttackFromMinMax(party.min.characters, party.max.characters, targetRounds, rule.survivalMultiplier);
    return {
      id: monster.id, name: monster.name, level, grade: monster.grade, form: monster.form, kind,
      targetRounds, survivalMultiplier: rule.survivalMultiplier,
      hp: Math.min(CONFIG.maxHp, hp), maxHp: Math.min(CONFIG.maxHp, hp), attack: survival.attack,
      defense: Number(monster.defense) || 0, exp: Number(monster.exp) || 0,
      sourceHp: Number(monster.maxHp ?? monster.hp) || 0, sourceAttack: Number(monster.attack) || 0,
      balance: {
        minPartyDpr: party.minDpr, maxPartyDpr: party.maxDpr, midpointPartyDpr: party.partyDpr,
        minHp: party.minHp, maxHp: party.maxHp, minDefense: party.minDefense, maxDefense: party.maxDefense,
        minExpectedClearRounds: party.maxDpr > 0 ? hp / party.maxDpr : Infinity,
        maxExpectedClearRounds: party.minDpr > 0 ? hp / party.minDpr : Infinity,
        expectedClearRounds: party.partyDpr > 0 ? hp / party.partyDpr : Infinity,
        desiredSurvivalRounds: survival.desiredSurvivalRounds,
        minAttackForTarget: survival.minAttack, maxAttackForTarget: survival.maxAttack,
        estimatedSurvivalRoundsMinProfile: survival.minResult.estimatedSurvivalRounds,
        estimatedSurvivalRoundsMaxProfile: survival.maxResult.estimatedSurvivalRounds,
        expectedIncomingDprMinProfile: survival.minResult.expectedIncomingDpr,
        expectedIncomingDprMaxProfile: survival.maxResult.expectedIncomingDpr,
        formationWeights: CONFIG.formationWeights,
        survivalMarginRounds: ((survival.minResult.estimatedSurvivalRounds + survival.maxResult.estimatedSurvivalRounds) / 2) - survival.desiredSurvivalRounds
      },
      profileSource: 'fixed-level-min-max-equipment-formation-weighted'
    };
  }

  function calculateDungeonMonsters(dungeon) {
    const name = dungeon?.monsterDungeonName || dungeon?.name;
    const items = window.MonsterXmlLoader?.state?.items || [];
    return items.filter(m => m.dungeonName === name).map(m => calculateMonsterStats(m));
  }

  function dungeonBoss(dungeon) {
    const name = dungeon?.monsterDungeonName || dungeon?.name;
    return (window.MonsterXmlLoader?.state?.items || []).filter(m => m.dungeonName === name).find(m => gradeKey(m) === 'boss') || null;
  }

  function levelForDungeon(dungeon) {
    return Math.max(1, Math.min(50, Number(dungeon?.levelEnd || dungeon?.levelStart || dungeon?.levelMap?.slice(-1)[0] || 1)));
  }

  function makeParty(level, mode = 'max') {
    return ['warrior', 'mage', 'rogue'].map(classId => equipByMode(leveledCharacter(level, classId), level, mode));
  }

  function survivalReference(dungeon, party) {
    const boss = dungeonBoss(dungeon);
    if (!boss) return null;
    const b = calculateMonsterStats(boss);
    return { members: party.map(c => {
      const incomingDpr = expectedDamagePerAttack(b.attack, Number(c.defense) || 0);
      return { classId: c.classId, hp: Number(c.maxHp) || Number(c.hp) || 1, defense: Number(c.defense) || 0, incomingDpr, estimatedSurvivalRounds: incomingDpr > 0 ? (Number(c.maxHp) || Number(c.hp) || 1) / incomingDpr : Infinity };
    }) };
  }

  function calculateBossHp(dungeon, options = {}) {
    const boss = dungeonBoss(dungeon);
    if (!boss) return null;
    const result = calculateMonsterStats(boss, options);
    const party = makeParty(result.level, 'max');
    const survival = survivalReference(dungeon, party);
    return { dungeon: dungeon.name, level: result.level, targetRounds: result.targetRounds, boss: { id: boss.id, name: boss.name, oldHp: Number(boss.maxHp) || 0, oldAttack: Number(boss.attack) || 0, oldDefense: Number(boss.defense) || 0, hp: result.hp, attack: result.attack, defense: result.defense }, reference: partyReference(result.level, result.defense), survival, modelHp: result.hp, modelAttack: result.attack, modelDefense: result.defense, balance: result.balance };
  }

  function calculateAll(dungeons, options = {}) {
    return (Array.isArray(dungeons) ? dungeons : Object.values(dungeons || {})).filter(Boolean).sort((a, b) => levelForDungeon(a) - levelForDungeon(b)).map(d => calculateBossHp(d, options));
  }

  function simulateBoss(dungeon, bossHp, runs = CONFIG.validationRuns, seed = 12345, attack = null) {
    const boss = dungeonBoss(dungeon);
    if (!boss) return null;
    const model = calculateMonsterStats(boss);
    const results = { wins: 0, losses: 0, draws: 0, rounds: 0 };
    for (let i = 0; i < runs; i++) {
      const rng = new SeededRandom(Number(seed) + i * 1009);
      const party = makeParty(model.level, 'max');
      const enemies = [{ ...boss, hp: bossHp ?? model.hp, maxHp: bossHp ?? model.hp, attack: attack ?? model.attack, team: 'enemy' }];
      for (const c of party) { c.team = 'player'; c.hp = c.maxHp; c.stats = { attacks:0, hits:0, criticals:0, kills:0, damageDealt:0, damageTaken:0, misses:0 }; }
      enemies[0].stats = { attacks:0, hits:0, criticals:0, kills:0, damageDealt:0, damageTaken:0, misses:0 };
      const engine = new BattleEngine({ rng, damageCalculator: new DamageCalculator(), maxRounds: 100 });
      const state = engine.run(party, enemies);
      if (state.result === 'WIN') results.wins++;
      else if (state.result === 'LOSE') results.losses++;
      else results.draws++;
      results.rounds += state.round;
    }
    results.averageRounds = results.rounds / Math.max(1, runs);
    results.winRate = results.wins / Math.max(1, runs);
    return results;
  }

  window.BattleBalanceCalculator = {
    CONFIG, expectedDamagePerAttack, expectedDamagePerRound, expectedIncomingDamagePerRound,
    partyProfile, partyReference, solveAttackForProfile, solveAttackForSurvival, solveAttackFromMinMax,
    calculateMonsterStats, calculateDungeonMonsters, calculateBossHp, calculateAll, simulateBoss,
    makeParty, survivalReference, equipBest, equipByMode
  };
})();
