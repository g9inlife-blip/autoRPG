window.STORY_NODES=[
 {id:'story.forest.001',title:'숲의 이상 현상',text:'숲 깊은 곳에서 이상한 기운이 느껴진다.',choices:[{id:'investigate',text:'조사한다',setFlags:['forest_investigated'],next:'story.forest.002'},{id:'avoid',text:'피한다',setFlags:['forest_avoided'],next:'story.forest.003'}]},
 {id:'story.forest.002',title:'숨겨진 흔적',text:'고블린들이 남긴 흔적을 발견했다.',choices:[{id:'help',text:'흔적을 따라간다',setFlags:['goblin_village_saved'],next:'story.goblin.001'}]},
 {id:'story.forest.003',title:'불길한 침묵',text:'숲은 잠시 조용해졌지만 불길함은 사라지지 않았다.',choices:[{id:'continue',text:'계속 탐험한다',next:'story.forest.004'}]},
 {id:'story.goblin.001',title:'뜻밖의 만남',text:'고블린 마을의 주민이 조심스럽게 다가온다.',choices:[{id:'talk',text:'대화한다',next:null}]}
];
