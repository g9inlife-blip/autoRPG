window.DUNGEON_DATA = window.DUNGEON_DATA || {};
window.DUNGEON_DATA['dungeon.goblin_cave'] = {
  id:'dungeon.goblin_cave', name:'고블린 동굴', floors:6,
  encounters:[
    {type:'battle', enemies:[{id:'goblin',name:'고블린 전사',hp:230,attack:45,defense:16,speed:10}]},
    {type:'battle', enemies:[{id:'goblin-archer',name:'고블린 궁수',hp:170,attack:52,defense:10,speed:15}]},
    {type:'treasure',itemId:'ore',itemName:'철광석',quantity:3,gold:120},
    {type:'battle', enemies:[{id:'goblin-chief',name:'고블린 족장',hp:420,attack:62,defense:25,speed:9}]}
  ],
  boss:{id:'goblin-king',name:'고블린 왕',hp:900,attack:82,defense:40,speed:12}
};
