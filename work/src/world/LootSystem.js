(() => {
  // 모든 드랍 밸런스는 이 설정만 수정하면 되도록 분리한다.
  const CONFIG = {
    rarityWeights: { uncommon:60, common:25, rare:10, unique:4, legendary:1 },
    enhancementWeights: { 0:80, 1:15, 2:5 },
    equipmentDropChance: 0.35,
    bossDropChance: 1,
    optionCount: { common:0, uncommon:1, rare:1, unique:2, legendary:3 },
    rarityMultiplier: { common:1, uncommon:1.15, rare:1.35, unique:1.7, legendary:2.2 },
    enhancementMultiplier: { 0:1, 1:1.08, 2:1.18 },
    options: [
      {id:'might',name:'힘',stat:'attack',min:2,max:6},
      {id:'guard',name:'수호',stat:'defense',min:2,max:6},
      {id:'haste',name:'민첩',stat:'speed',min:1,max:2},
      {id:'vitality',name:'생명',stat:'hp',min:10,max:30}
    ],
    fallbackPool:['iron_sword','oak_staff','hunter_dagger','leather_armor','iron_armor','adventurer_ring']
  };
  const LABELS={common:'커먼',uncommon:'언커먼',rare:'레어',unique:'유니크',legendary:'전설'};
  function weightedPick(table,rng){const entries=Object.entries(table),total=entries.reduce((n,[,w])=>n+Math.max(0,Number(w)||0),0);if(total<=0)return entries[0]?.[0];let roll=rng.next()*total;for(const [key,w] of entries){roll-=Math.max(0,Number(w)||0);if(roll<0)return key;}return entries.at(-1)?.[0];}
  function basePool(pool){return (pool?.length?pool:CONFIG.fallbackPool).map(id=>window.EQUIPMENT?.[id]).filter(Boolean);}
  function generate({rng,baseId=null,pool=null,force=false,boss=false}={}){
    if(!rng)return null;if(!force&&!boss&&rng.next()>CONFIG.equipmentDropChance)return null;if(boss&&rng.next()>CONFIG.bossDropChance)return null;
    const base=baseId&&window.EQUIPMENT?.[baseId]||rng.pick(basePool(pool));if(!base)return null;
    const rarity=weightedPick(CONFIG.rarityWeights,rng),enhancement=weightedPick(CONFIG.enhancementWeights,rng);const mult=(CONFIG.rarityMultiplier[rarity]||1)*(CONFIG.enhancementMultiplier[enhancement]||1);
    const options=[],used=new Set(),count=CONFIG.optionCount[rarity]||0;for(let i=0;i<count&&used.size<CONFIG.options.length;i++){const candidates=CONFIG.options.filter(x=>!used.has(x.id));const option=rng.pick(candidates);if(!option)break;used.add(option.id);options.push({...option,value:rng.int(option.min,option.max)});}
    const round=n=>Math.round(Number(n||0)*mult), bonuses={attack:0,defense:0,speed:0,hp:0};for(const o of options)bonuses[o.stat]=(bonuses[o.stat]||0)+o.value;
    Object.keys(bonuses).forEach(k=>bonuses[k]=round(bonuses[k]));
    const baseIdValue=base.id;const uniqueName=rarity==='unique'?`유니크 ${base.name}`:rarity==='legendary'?`전설 ${base.name}`:base.name;
    const item={...base,id:`${baseIdValue}-${rarity}`,baseId:baseIdValue,instanceId:`loot_${baseIdValue}_${Date.now().toString(36)}_${rng.int(100000,999999)}`,name:uniqueName,rarity,rarityName:LABELS[rarity],enhancement:Number(enhancement),options,attack:round(base.attack)+bonuses.attack,defense:round(base.defense)+bonuses.defense,speed:round(base.speed)+bonuses.speed,hp:round(base.hp)+bonuses.hp,generated:true,requirements:base.requirements?JSON.parse(JSON.stringify(base.requirements)):undefined};
    return item;
  }
  window.LOOT_CONFIG=CONFIG;window.LootSystem={generate,rarityLabel:r=>LABELS[r]||r};
})();
