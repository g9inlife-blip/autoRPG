class BattleEngine {
  constructor({ rng, damageCalculator, maxRounds = 100, onEvent = () => {} }) { this.rng = rng; this.damageCalculator = damageCalculator; this.maxRounds = maxRounds; this.onEvent = onEvent; }
  run(players, enemies) {
    const state = { round: 0, result: 'ONGOING', players, enemies, events: [], startedAt: Date.now(), endedAt: null, cooldowns: {} };
    this.emit(state, 'BATTLE_START');
    while (state.round < this.maxRounds && state.result === 'ONGOING') {
      state.round++;
      for (const id of Object.keys(state.cooldowns)) state.cooldowns[id] = Math.max(0, state.cooldowns[id] - 1);
      const actors = [...players, ...enemies].filter(a => a.alive).sort((a,b) => (b.speed-a.speed) || (this.rng.next()-.5));
      for (const actor of actors) {
        if (!actor.alive || state.result !== 'ONGOING') continue;
        const targets = actor.team === 'player' ? enemies : players;
        const target = targets.filter(t => t.alive).sort((a,b) => a.hp-b.hp)[0];
        if (!target) break;
        const skill = this.chooseSkill(actor, state);
        if (skill) {
          state.cooldowns[actor.id] = skill.cooldown || 0;
          this.emit(state, 'SKILL_USE', { actorId: actor.id, actor: actor.name, skillId: skill.id, skill: skill.name, cooldown: skill.cooldown || 0 });
          this.resolveAttack(actor, target, state, skill);
          if (skill.extraHit && target.alive && state.result === 'ONGOING') this.resolveAttack(actor, target, state, skill);
        } else {
          this.resolveAttack(actor, target, state, null);
        }
        this.checkResult(state);
      }
    }
    if (state.result === 'ONGOING') state.result = 'DRAW';
    state.endedAt = Date.now(); this.emit(state, 'BATTLE_END', {result:state.result}); return state;
  }
  chooseSkill(actor, state) {
    const ids = Array.isArray(actor.skills) ? actor.skills : [];
    if (!ids.length || (state.cooldowns[actor.id] || 0) > 0) return null;
    const registry = window.SKILL_REGISTRY || {};
    const available = ids.map(id => registry[id]).filter(Boolean);
    return available.length ? available[0] : null;
  }
  resolveAttack(attacker, defender, state, skill=null) {
    attacker.stats.attacks++;
    const result = this.damageCalculator.resolve(attacker, defender);
    if (skill && result.hit) result.damage = Math.max(1, Math.floor(result.damage * (skill.multiplier || 1)));
    if (result.hit) {
      attacker.stats.hits++; if (result.critical) attacker.stats.criticals++;
      const beforeHp = defender.hp;
      defender.hp = Math.max(0, defender.hp-result.damage);
      attacker.stats.damageDealt += result.damage; defender.stats.damageTaken += result.damage;
      this.emit(state, 'DAMAGE', {attackerId:attacker.id,attacker:attacker.name,defenderId:defender.id,defender:defender.name,beforeHp,afterHp:defender.hp,critical:!!result.critical,damage:result.damage,result,skillId:skill?.id||null,skill:skill?.name||null});
      if (!defender.alive) { attacker.stats.kills++; this.emit(state, 'DEFEAT', {characterId:defender.id,character:defender.name,defeatedId:defender.id,defeatedTarget:defender.id,defeatedName:defender.name,side:defender.team,defeatedBy:attacker.name,skillId:skill?.id||null}); }
    } else { attacker.stats.misses++; this.emit(state, 'MISS', {attackerId:attacker.id,attacker:attacker.name,defenderId:defender.id,defender:defender.name,result,skillId:skill?.id||null,skill:skill?.name||null}); }
  }
  checkResult(state) { const playersAlive=state.players.some(p=>p.alive),enemiesAlive=state.enemies.some(e=>e.alive);if(!enemiesAlive)state.result='WIN';else if(!playersAlive)state.result='LOSE'; }
  emit(state,type,data={}) { const event={type,round:state.round,data};state.events.push(event);this.onEvent(event); }
}
window.BattleEngine = BattleEngine;
