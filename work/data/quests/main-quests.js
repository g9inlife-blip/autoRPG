window.QUEST_DATA=window.QUEST_DATA||{};
window.QUEST_DATA.main=[
 {id:'main.001',type:'main',title:'숲의 부름',description:'고대의 숲에서 이상 현상의 원인을 찾아라.',objectives:[{type:'reach_floor',dungeon:'dungeon.ancient_forest',floor:3,count:1}],rewards:{gold:300,exp:500},next:['main.002']},
 {id:'main.002',type:'main',title:'고블린의 흔적',description:'숲 깊은 곳에서 발견된 고블린의 흔적을 조사하라.',objectives:[{type:'kill',target:'goblin',count:5}],rewards:{gold:500,exp:800},branches:[{condition:{flag:'goblin_village_saved',equals:true},next:'main.003a'},{condition:{flag:'goblin_village_saved',equals:false},next:'main.003b'}]},
 {id:'main.003a',type:'main',title:'뜻밖의 동맹',description:'도움을 받은 고블린들과 협력해 새로운 길을 찾아라.',objectives:[{type:'explore',target:'dungeon.goblin_cave',count:1}],rewards:{gold:700,exp:1200}},
 {id:'main.003b',type:'main',title:'적의 요새',description:'고블린들의 거점을 돌파하라.',objectives:[{type:'defeat_boss',target:'goblin-king',count:1}],rewards:{gold:1000,exp:1600}}
];
