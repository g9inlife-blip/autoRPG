const $ = id => document.getElementById(id);
let state;

function createBattle(seed) {
  const rng = new SeededRandom(seed);
  const party = [
    new Character({ id:'hero', name:'기사', hp:420, attack:62, defense:35, speed:12, team:'player' }),
    new Character({ id:'mage', name:'마법사', hp:260, attack:88, defense:18, speed:16, team:'player' })
  ];
  const enemies = [
    new Character({ id:'goblin-a', name:'고블린 A', hp:240, attack:48, defense:20, speed:10, team:'enemy' }),
    new Character({ id:'goblin-b', name:'고블린 B', hp:220, attack:44, defense:18, speed:9, team:'enemy' })
  ];
  const events=[];
  const engine=new BattleEngine({rng,damageCalculator:new DamageCalculator(rng),onEvent:e=>events.push(e)});
  return {party,enemies,events,result:engine.run(party,enemies)};
}
function dps(c){ return (c.stats.damageDealt / Math.max(1,c.stats.attacks)).toFixed(1); }
function formatEvent(e){
  if(e.type==='DAMAGE') return `${e.data.attacker} → ${e.data.defender} ${e.data.damage} 피해${e.data.critical?' CRITICAL':''}`;
  if(e.type==='MISS') return `${e.data.attacker} → ${e.data.defender} 빗나감`;
  return e.type;
}
function render(battle,seed){
  $('party').innerHTML=battle.party.map(c=>`<div class="row"><b>${c.name}</b><span>HP ${c.hp}/${c.maxHp}</span><span>DPS ${dps(c)}</span></div>`).join('');
  $('battle').innerHTML=`<p>Seed: <b>${seed}</b></p><p>결과: <b>${battle.result.result}</b> / ${battle.result.round} Round</p><p>같은 Seed는 같은 전투를 재현합니다.</p>`;
  const total=battle.party.reduce((n,c)=>n+c.stats.damageDealt,0);
  $('stats').innerHTML=`<p>파티 총 피해: <b>${total}</b></p><p>처치: <b>${battle.party.reduce((n,c)=>n+c.stats.kills,0)}</b></p>`+battle.party.map(c=>`<div>${c.name}: ${c.stats.damageDealt} damage / ${c.stats.hits} hits / ${c.stats.criticals} critical</div>`).join('');
  $('log').textContent=battle.events.map(e=>`[R${e.round}] ${formatEvent(e)}`).join('\n');
  $('status').textContent='엔진 정상 동작 · 버튼으로 재실행 가능';
}
function run(seed=Number($('seed').value)||12345){
  state=createInitialGameState(seed); const battle=createBattle(seed);
  state.battle={active:false,state:battle.result}; state.logs=battle.events; render(battle,seed);
}
$('runBattle').addEventListener('click',()=>run());
$('run100').addEventListener('click',()=>{
  const seed=Number($('seed').value)||12345; let wins=0;
  for(let i=0;i<100;i++) if(createBattle(seed+i).result.result==='WIN') wins++;
  $('battle').innerHTML=`<p>100회 시뮬레이션 완료</p><p>승리 <b>${wins}</b> / 100</p><p>패배 <b>${100-wins}</b> / 100</p>`;
});
$('reset').addEventListener('click',()=>run(12345));
try { run(); } catch(error) { $('status').textContent='오류: '+error.message; $('log').textContent=error.stack||String(error); console.error(error); }
