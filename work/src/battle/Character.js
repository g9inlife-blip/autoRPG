class Character {
  constructor({ id, name, hp, maxHp, attack, defense, speed = 10, team }) {
    this.id=id; this.name=name; this.maxHp=maxHp ?? hp; this.hp=hp;
    this.baseStats={hp:this.maxHp,attack,defense,speed}; this.attack=attack; this.defense=defense; this.speed=speed; this.team=team;
    this.equipment={weapon:null,armor:null,accessory:null};
    this.stats={damageDealt:0,damageTaken:0,attacks:0,hits:0,criticals:0,misses:0,kills:0};
  }
  get alive(){return this.hp>0;}
  equip(item){if(!item||!item.slot)return false;this.equipment[item.slot]=item;this.recalculateStats();return true;}
  unequip(slot){const old=this.equipment[slot];if(!old)return null;this.equipment[slot]=null;this.recalculateStats();return old;}
  recalculateStats(){const oldMax=this.maxHp;const bonus=Object.values(this.equipment).filter(Boolean).reduce((a,x)=>({hp:a.hp+(x.hp||0),attack:a.attack+(x.attack||0),defense:a.defense+(x.defense||0),speed:a.speed+(x.speed||0)}),{hp:0,attack:0,defense:0,speed:0});this.maxHp=this.baseStats.hp+bonus.hp;this.attack=this.baseStats.attack+bonus.attack;this.defense=this.baseStats.defense+bonus.defense;this.speed=Math.max(1,this.baseStats.speed+bonus.speed);if(oldMax!==this.maxHp)this.hp=Math.min(this.maxHp,this.hp+(this.maxHp-oldMax));}
}
function cloneCharacter(character){const c=new Character({id:character.id,name:character.name,hp:character.maxHp,maxHp:character.maxHp,attack:character.baseStats?.attack??character.attack,defense:character.baseStats?.defense??character.defense,speed:character.baseStats?.speed??character.speed,team:character.team});c.classId=character.classId;c.level=character.level;c.exp=character.exp;c.personality=character.personality;c.bonds={...(character.bonds||{})};c.skills=[...(character.skills||[])];c.appearance={...(character.appearance||{})};Object.entries(character.equipment||{}).forEach(([slot,item])=>{if(item)c.equip(item);});return c;}
window.Character=Character;window.cloneCharacter=cloneCharacter;
