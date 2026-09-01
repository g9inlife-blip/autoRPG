export class BattleEngine {
  constructor({ rng, damageCalculator, maxRounds = 100, onEvent = () => {} }) {
    this.rng = rng;
    this.damageCalculator = damageCalculator;
    this.maxRounds = maxRounds;
    this.onEvent = onEvent;
  }

  run(players, enemies) {
    const state = { round: 0, result: 'ONGOING', players, enemies, startedAt: Date.now(), endedAt: null };
    this.emit(state, 'BATTLE_START');

    while (state.round < this.maxRounds && state.result === 'ONGOING') {
      state.round += 1;
      const actors = [...players, ...enemies]
        .filter(a => a.alive)
        .sort((a, b) => (b.speed - a.speed) || (this.rng.next() - 0.5));

      for (const actor of actors) {
        if (!actor.alive || state.result !== 'ONGOING') continue;
        const targets = actor.team === 'player' ? enemies : players;
        const target = targets.filter(t => t.alive).sort((a, b) => a.hp - b.hp)[0];
        if (!target) break;
        this.resolveAttack(actor, target, state);
        this.checkResult(state);
      }
    }

    if (state.result === 'ONGOING') state.result = 'DRAW';
    state.endedAt = Date.now();
    this.emit(state, 'BATTLE_END');
    return state;
  }

  resolveAttack(attacker, defender, state) {
    attacker.stats.attacks += 1;
    const result = this.damageCalculator.resolve(attacker, defender);
    if (result.hit) {
      attacker.stats.hits += 1;
      if (result.critical) attacker.stats.criticals += 1;
      defender.hp = Math.max(0, defender.hp - result.damage);
      attacker.stats.damageDealt += result.damage;
      defender.stats.damageTaken += result.damage;
      this.emit(state, 'DAMAGE', { attacker: attacker.name, defender: defender.name, ...result });
      if (!defender.alive) attacker.stats.kills += 1;
    } else {
      attacker.stats.misses += 1;
      this.emit(state, 'MISS', { attacker: attacker.name, defender: defender.name, ...result });
    }
  }

  checkResult(state) {
    const playersAlive = state.players.some(p => p.alive);
    const enemiesAlive = state.enemies.some(e => e.alive);
    if (!enemiesAlive) state.result = 'WIN';
    else if (!playersAlive) state.result = 'LOSE';
  }

  emit(state, type, data = {}) {
    this.onEvent({ type, round: state.round, data });
  }
}
