window.QUEST_DATA=window.QUEST_DATA||{};
window.QUEST_DATA.repeatable=[
 {id:'repeat.001',type:'repeatable',title:'숲의 정화',description:'고대의 숲의 몬스터를 처치하라.',reset:'daily',objectives:[{type:'kill',target:'wolf',count:10}],rewards:{gold:150,exp:250,itemId:'potion',quantity:1}},
 {id:'repeat.002',type:'repeatable',title:'고블린 토벌',description:'고블린을 처치하고 보급품을 확보하라.',reset:'daily',objectives:[{type:'kill',target:'goblin',count:15}],rewards:{gold:250,exp:400}},
 {id:'repeat.003',type:'repeatable',title:'유적 탐사',description:'고대 유적을 3층 이상 탐험하라.',reset:'weekly',objectives:[{type:'reach_floor',dungeon:'dungeon.ancient_ruins',floor:3,count:1}],rewards:{gold:600,exp:900,itemId:'relic',quantity:1}}
];
