class Character {
  constructor({ id, name, hp, attack, defense, speed = 10, team }) {
    this.id = id; this.name = name; this.maxHp = hp; this.hp = hp;
    this.attack = attack; this.defense = defense; this.speed = speed; this.team = team;
    this.stats = { damageDealt: 0, damageTaken: 0, attacks: 0, hits: 0, criticals: 0, misses: 0, kills: 0 };
  }
  get alive() { return this.hp > 0; }
}
function cloneCharacter(character) { return new Character({ id: character.id, name: character.name, hp: character.maxHp, attack: character.attack, defense: character.defense, speed: character.speed, team: character.team }); }
window.Character = Character;
window.cloneCharacter = cloneCharacter;
