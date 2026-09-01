(() => {
  const CONFIG={maxLevel:50,baseExp:100,growth:1.35,classGrowth:{warrior:{hp:32,attack:4,defense:2,speed:.15},mage:{hp:20,attack:5,defense:1,speed:.12},rogue:{hp:24,attack:4,defense:1.3,speed:.2}},attributeGrowth:{STR:1,INT:1,AGI:1,VIT:1}};
  function expRequired(level){const l=Math.max(1,Math.min(CONFIG.maxLevel,Number(level)||1));return Math.round(CONFIG.baseExp*Math.pow(l,CONFIG.growth));}
  function growthFor(c){return CONFIG.classGrowth[c?.classId]||CONFIG.classGrowth.warrior;}
  function applyLevel(c){const g=growthFor(c);c.baseStats=c.baseStats||{hp:c.maxHp,attack:c.attack,defense:c.defense,speed:c.speed};c.baseStats.hp+=g.hp;c.baseStats.attack+=g.attack;c.baseStats.defense+=g.defense;c.baseStats.speed+=g.speed;c.attributes=c.attributes||{};for(const [k,v] of Object.entries(CONFIG.attributeGrowth))c.attributes[k]=(Number(c.attributes[k])||0)+v;if(typeof c.recalculateStats==='function')c.recalculateStats();c.hp=c.maxHp;}
  function gainExp(c,amount){if(!c)return{gained:0,levels:0};let gained=Math.max(0,Number(amount)||0),levels=0;c.exp=Math.max(0,Number(c.exp)||0)+gained;c.level=Math.max(1,Number(c.level)||1);while(c.level<CONFIG.maxLevel&&c.exp>=expRequired(c.level)){c.exp-=expRequired(c.level);c.level++;levels++;applyLevel(c);}if(c.level>=CONFIG.maxLevel)c.exp=Math.min(c.exp,expRequired(CONFIG.maxLevel)-1);return{gained,levels,level:c.level,exp:c.exp,next:expRequired(c.level)};}
  window.CHARACTER_PROGRESSION_CONFIG=CONFIG;window.CharacterProgression={expRequired,gainExp,applyLevel};
})();
