class ExplorationLogPanel {
  constructor({ root, eventsProvider, onDetail=()=>{} }) { this.root=root; this.eventsProvider=eventsProvider; this.onDetail=onDetail; }
  render(){
    const hiddenTypes=new Set(['EXP_GAIN','LEVEL_UP','EXP_GAINED','LEVELUP','EXP_GAIN_LOG','LEVEL_UP_LOG','AREA_ENCOUNTER']);
    const events=(typeof this.eventsProvider==='function'?this.eventsProvider():[]).filter(e=>!hiddenTypes.has(String(e?.type||'').toUpperCase())&&!hiddenTypes.has(String(e?.action||'').toUpperCase()));
    if(!this.root)return;this.root.innerHTML='';
    events.slice().reverse().forEach(e=>{
      const row=document.createElement('button');row.type='button';row.className='exploration-log-row';const data=e.data||{};let summary='';
      if(e.type==='BATTLE_END'){const enemies=Array.isArray(data.enemies)?data.enemies:[];const names=enemies.map(x=>typeof x==='string'?x:(x?.name||x?.Name||x?.monsterName||x?.id||'적')).filter(Boolean);const label=names.join(', ')||'적';const downs=(data.events||[]).filter(x=>x?.type==='DEFEAT'||x?.action==='DEFEAT').map(x=>x.data?.character||x.data?.defeatedName||x.actor||x.name).filter(Boolean);summary=`[${e.floor}구역] ${label}와 전투`+(downs.length?` (${downs.join(', ')} 기절)`:` · ${data.reason==='TIMEOUT'?'전투가 길어져 이탈하였다':(data.result||'전투 종료')}`);}
      else if(e.type==='HEALING_SPRING')summary=`[${e.floor}구역] 치유샘물을 발견했다 · HP/MP 30% 회복`;
      else if(e.type==='LOOT')summary=`[${e.floor}구역] ${data.itemName||data.itemId||'아이템'} 획득 ×${data.quantity||1}`;
      else if(e.type==='FLOOR_REACHED')summary=`[${e.floor}구역] ${e.floor}구역 도착`;
      else if(e.type==='RUN_START')summary='탐험 시작';
      else if(e.type==='RUN_COMPLETE')summary=`[${e.floor}구역] 던전 탐험 완료`;
      else if(e.type==='RUN_FAILED'){const timedOut=Array.isArray(data.battleEvents)&&data.battleEvents.some(x=>x?.type==='BATTLE_TIMEOUT');summary=timedOut?`[${e.floor}구역] 전투가 길어져 이탈하였다`:`[${e.floor}구역] 던전 탐험 실패 · 파티 전멸`;}
      else summary=`[${e.floor}구역] ${e.type}`;
      row.innerHTML=`<span>${summary}</span><b>›</b>`;row.addEventListener('click',()=>{
        if(e.type==='RUN_FAILED'&&Array.isArray(data.battleEvents)&&data.battleEvents.length){
          this.onDetail({type:'BATTLE_END',floor:e.floor,data:{result:data.result||'LOSE',round:Number(data.round)||0,enemies:Array.isArray(data.enemies)?data.enemies:[],events:data.battleEvents}});
          return;
        }
        this.onDetail(e);
      });this.root.appendChild(row);
    });
  }
}
window.ExplorationLogPanel=ExplorationLogPanel;
