window.DUNGEON_DATA = window.DUNGEON_DATA || {};
window.DUNGEON_DATA['dungeon.ancient_ruins'] = {
  id:'dungeon.ancient_ruins', name:'고대 유적', floors:8,
  encounters:[
    {type:'battle', enemies:[{id:'ruin-guardian',name:'유적 수호병',hp:360,attack:60,defense:38,speed:7}]},
    {type:'battle', enemies:[{id:'stone-golem',name:'석상 골렘',hp:520,attack:68,defense:55,speed:4}]},
    {type:'treasure',itemId:'relic',itemName:'고대의 유물',quantity:1,gold:250},
    {type:'battle', enemies:[{id:'ruin-mage',name:'유적 마도사',hp:300,attack:90,defense:24,speed:18}]}
  ],
  boss:{id:'ancient-guardian',name:'고대 유적의 수호자',hp:1200,attack:105,defense:60,speed:10}
};
