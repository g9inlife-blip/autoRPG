export class DamageCalculator {
  constructor(rng) {
    this.rng = rng;
  }

  resolve(attacker, defender) {
    const roll = this.rng.int(1, 6) + this.rng.int(1, 6);
    const critical = roll === 12;
    const fumble = roll === 2;
    const hit = !fumble && (critical || this.rng.int(1, 100) <= 85);
    const raw = Math.max(1, attacker.attack + roll - defender.defense);
    const damage = hit ? (critical ? raw * 2 : raw) : 0;
    return { hit, critical, fumble, roll, damage };
  }
}
