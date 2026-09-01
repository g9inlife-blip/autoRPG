class ExplorationLogPanel {
  constructor({ root, eventsProvider, onDetail=()=>{} }) { this.root=root; this.eventsProvider=eventsProvider; this.onDetail=onDetail; }
  render(){
    const events=typeof this.eventsProvider==='function'?this.eventsProvider():[];
    if(!this.root)return;
    this.root.innerHTML='';
    events.slice().reverse().forEach((e,i)=>{
      const row=document.createElement('button'); row.type='button'; row.className='exploration-log-row';
      const data=e.data||{};
      let summary='';
      if(e.type==='BATTLE_END'){
        const enemies=(data.enemies||[]).join(', ')||'적';
        const downs=(data.events||[]).filter(x=>x?.type==='DEFEAT'||x?.action==='DEFEAT').map(x=>x.actor||x.name).filter(Boolean);
        summary=`[${e.floor}층] ${enemies}와 전투`+(downs.length?` (${downs.join(', ')} 기절)`: ` · ${data.result||'전투 종료'}`);
      }else if(e.type==='LOOT') summary=`[${e.floor}층] ${data.itemName||data.itemId||'아이템'} 획득 ×${data.quantity||1}`;
      else if(e.type==='FLOOR_REACHED') summary=`[${e.floor}층] ${e.floor}층 도착`;
      else if(e.type==='RUN_START') summary='탐험 시작';
      else if(e.type==='RUN_COMPLETE') summary=`[${e.floor}층] 던전 탐험 완료`;
      else summary=`[${e.floor}층] ${e.type}`;
      row.innerHTML=`<span>${summary}</span><b>›</b>`;
      row.addEventListener('click',()=>this.onDetail(e));
      this.root.appendChild(row);
    });
  }
}
window.ExplorationLogPanel=ExplorationLogPanel;
