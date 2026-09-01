const $ = id => document.getElementById(id);
let state = createInitialGameState(12345);
let run = null;
let lastBattle = null;

function makeParty(){
  return [
    new Character({id:'hero',name:'기사',hp:420,attack:62,defense:35,speed:12,team:'player'}),
    new Character({id:'mage',name:'마법사',hp:260,attack:88,defense:18,speed:16,team:'player'})
  ];
}

function battleFactory(rng,party,enemies){
  const events=[];
  const engine=new BattleEngine({rng,damageCalculator:new DamageCalculator(rng),onEvent:e=>events.push(e)});
  const started=performance.now();
  const result=engine.run(party,enemies);
  const seconds=Math.max(.1,(performance.now()-started)/1000);
  party.forEach(c=>{ if(!c.stats.activeSeconds) c.stats.activeSeconds=0; c.stats.activeSeconds+=seconds; });
  return {party,enemies,events,result,seconds};
}

function formatEvent(e){
  if(e.type==='DAMAGE')return `${e.data.attacker} → ${e.data.defender} ${e.data.damage} 피해${e.data.critical?' CRITICAL':''}`;
  if(e.type==='MISS')return `${e.data.attacker} → ${e.data.defender} 빗나감`;
  if(e.type==='DEATH')return `${e.data.character||e.data.target||''} 쓰러짐`;
  return e.type;
}

function dps(c){
  const seconds=Math.max(.1,c.stats.activeSeconds||0);
  return seconds?((c.stats.damageDealt||0)/seconds).toFixed(1):'0.0';
}

function render(){
  const d=run?.dungeon;
  $('dungeon').innerHTML=d?`<h3>${d.name}</h3><p>층 <b>${run.floor}</b> / ${d.floors}</p><p>${run.complete?'탐험 완료':run.active?'탐험 중':'탐험 종료'}</p><p>Seed: ${state.seed}</p>`:'<p>던전을 선택하고 탐험을 시작하세요.</p>';
  const party=run?.party||[];
  $('party').innerHTML=party.map(c=>`<div class="row"><b>${c.name}</b><span>HP ${Math.max(0,c.hp)}/${c.maxHp}</span><span>누적 DPS ${dps(c)}</span><span>피해 ${c.stats.damageDealt||0}</span></div>`).join('')||'<p>대기 중</p>';
  $('battle').innerHTML=lastBattle?`<p>결과: <b>${lastBattle.result.result}</b> / ${lastBattle.result.round} Round</p><p>전투시간 ${lastBattle.seconds.toFixed(2)}초 · 상세 로그 ${lastBattle.events.length}건</p>`:'<p>아직 전투가 없습니다.</p>';
  $('stats').innerHTML=run?`<p>골드 <b>${run.gold}</b> · 경험치 <b>${run.exp}</b></p>`+party.map(c=>{const s=run.stats[c.id];return `<div>${c.name}: ${c.stats.damageDealt||0} 피해 / ${c.stats.hits||0} 적중 / ${c.stats.criticals||0} 치명타 / ${c.stats.kills||0} 처치 / 누적 DPS ${dps(c)}</div>`}).join(''):'<p>-</p>';
  $('loot').innerHTML=run?.loot.length?run.loot.map(x=>`<div class="loot"><span>${x.itemName} ×${x.quantity}</span><small>(층 ${x.floor})</small></div>`).join(''):'<p>획득 아이템 없음</p>';
  $('log').textContent=run?run.events.map((e,i)=>{if(e.type==='BATTLE')return `[${i+1}] ${e.floor}층 전투: ${e.result.result}\n`+e.events.map(x=>`  ${formatEvent(x)}`).join('\n');if(e.type==='LOOT')return `[${i+1}] ${e.floor}층 획득: ${e.itemName} ×${e.quantity}`;if(e.type==='FLOOR')return `[${i+1}] ${e.floor}층 진입`;if(e.type==='RUN_START')return `[${i+1}] 탐험 시작`;return `[${i+1}] 탐험 완료`;}).join('\n'):'탐험 로그가 없습니다.';
  $('status').textContent=run?.complete?'탐험 완료':run?.active?'탐험 진행 중 · 다음 진행 버튼으로 한 단계 진행':'준비';
  $('stepDungeon').disabled=!run||run.complete;
}

function startDungeon(){
  const seed=Number($('seed').value)||12345;
  state=createInitialGameState(seed); state.seed=seed;
  const dungeon=DUNGEONS[$('dungeonSelect').value]||DUNGEONS['dungeon.ancient_forest'];
  run=new DungeonRun({dungeon,party:makeParty(),rng:new SeededRandom(seed),battleFactory});
  run.start(); run.nextFloor(); lastBattle=null; render();
}

function stepDungeon(){
  if(!run){startDungeon();return;}
  if(run.complete)return;
  const before=run.events.length;
  run.step();
  const battleEvents=run.events.slice(before).filter(e=>e.type==='BATTLE');
  const last=battleEvents.at(-1);
  lastBattle=last?{result:last.result,events:last.events,seconds:last.seconds||0}:null;
  state.adventure={active:run.active,currentEvent:run.events.at(-1)?.type||null};
  render();
}

function populateDungeons(){
  const select=$('dungeonSelect');
  Object.values(DUNGEONS).forEach(d=>{const option=document.createElement('option');option.value=d.id;option.textContent=`${d.name} (${d.floors}층)`;select.appendChild(option);});
}

$('startDungeon').addEventListener('click',startDungeon);
$('stepDungeon').addEventListener('click',stepDungeon);
$('run100').addEventListener('click',()=>{const seed=Number($('seed').value)||12345;let wins=0;for(let i=0;i<100;i++){const b=battleFactory(new SeededRandom(seed+i),makeParty(),[new Character({id:'test-enemy',name:'훈련용 고블린',hp:220,attack:44,defense:18,speed:9,team:'enemy'})]);if(b.result.result==='WIN')wins++;} $('battle').innerHTML=`<p>100회 전투 완료</p><p>승리 <b>${wins}</b> / 100 · 승률 <b>${wins}%</b></p>`;});
$('reset').addEventListener('click',()=>{run=null;lastBattle=null;state=createInitialGameState(12345);$('seed').value=12345;render();});
try{populateDungeons();render();}catch(error){$('status').textContent='오류: '+error.message;$('log').textContent=error.stack||String(error);console.error(error);}
