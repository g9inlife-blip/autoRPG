class CharacterFactory {
  constructor({classes=window.CHARACTER_CLASSES||{}, rng=null}={}){this.classes=classes;this.rng=rng;}
  createDummy(overrides={}){return this.create({id:overrides.id||`dummy-${Date.now()}`,name:overrides.name||'더미 용사',classId:overrides.classId||'warrior',...overrides});}
  create(spec){const base=this.classes[spec.classId]||{};const c=new Character({id:spec.id,name:spec.name,team:spec.team||'player',hp:spec.hp??base.hp??100,maxHp:spec.maxHp??spec.hp??base.hp??100,attack:spec.attack??base.attack??10,defense:spec.defense??base.defense??5,speed:spec.speed??base.speed??10});c.classId=spec.classId;c.level=spec.level??1;c.exp=spec.exp??0;c.personality=spec.personality??base.personality??'normal';c.bonds={...(base.bonds||{}),...(spec.bonds||{})};c.skills=[...(base.skills||[]),...(spec.skills||[])];c.appearance={...(base.appearance||{}),...(spec.appearance||{})};c.stats={attacks:0,hits:0,misses:0,criticals:0,damageDealt:0,damageTaken:0,kills:0};return c;}
}
window.CharacterFactory=CharacterFactory;
window.CHARACTER_CLASSES=window.CHARACTER_CLASSES||{warrior:{hp:450,attack:62,defense:35,speed:12,personality:'brave',skills:['slash']},mage:{hp:280,attack:78,defense:16,speed:11,personality:'calm',skills:['fireball']},rogue:{hp:320,attack:55,defense:22,speed:18,personality:'playful',skills:['quick-strike']}};
