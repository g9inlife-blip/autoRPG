class SeededRandom {
  constructor(seed = 12345) { this.seed = seed >>> 0; this.initialSeed = this.seed; }
  next() { let x = this.seed; x ^= x << 13; x ^= x >>> 17; x ^= x << 5; this.seed = x >>> 0; return this.seed / 4294967296; }
  int(min, max) { return Math.floor(this.next() * (max - min + 1)) + min; }
  pick(items) { return items.length ? items[this.int(0, items.length - 1)] : undefined; }
}
window.SeededRandom = SeededRandom;
