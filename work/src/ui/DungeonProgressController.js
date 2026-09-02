(() => {
  let timer = null;
  let started = false;

  function clearTimer(){if(timer){clearTimeout(timer);timer=null;}}
  function startButton(){return document.getElementById('startDungeon');}
  function button(){return document.getElementById('stepDungeon');}

  function syncButtons(){
    const start=startButton(),boss=button(),r=window.run;
    const active=Boolean(r?.active);
    if(start){start.textContent='던전 진행';start.disabled=active;}
    if(boss){boss.textContent='보스전';boss.disabled=!active||!r?.bossReady;boss.hidden=!active;}
  }

  function schedule(){
    clearTimer();
    const r=window.run;
    if(!r?.active||r.complete||r.failed){syncButtons();return;}
    timer=setTimeout(()=>{
      timer=null;
      const current=window.run;
      if(!current?.active||current.complete||current.failed){syncButtons();return;}
      window.stepDungeon?.();
      syncButtons();
      if(window.run?.active&&!window.run?.complete&&!window.run?.failed)schedule();
    },1000);
  }

  function renderBossBattle(battle){
    if(!battle)return;
    const panel=document.getElementById('battle');
    if(panel)panel.innerHTML=`<p>결과: <b>${battle.data?.result||'?'}</b> / ${Math.round(Number(battle.data?.round)||0)} Round</p><p>전투시간 ${Math.max(1,Math.round((battle.data?.events?.length||0)/10))}초 · 상세 로그 ${(battle.data?.events||[]).length}건</p>`;
  }

  function runBossBattle(){
    const r=window.run;
    if(!r?.active||!r.bossReady)return;
    clearTimer();
    try{
      const before=r.events.length;
      const result=r.fightBoss();
      const added=r.events.slice(before);
      if(typeof window.recordQuestEvents==='function')window.recordQuestEvents(added);
      const battle=added.find(e=>e.type==='BATTLE_END');
      if(battle){renderBossBattle(battle);window.updateDialogue?.(battle.data.events);}
      else window.updateDialogue?.(added);
      window.renderQuests?.();
      window.render?.();
      if(battle)renderBossBattle(battle);
      if(added.some(e=>e.type==='RUN_FAILED'))window.showRunResult?.(false);
      else if(added.some(e=>e.type==='RUN_COMPLETE')||result?.done)window.showRunResult?.(true);
    }catch(error){
      const status=document.getElementById('status');
      if(status)status.textContent='보스전 오류: '+error.message;
      console.error(error);
    }
    syncButtons();
    if(window.run?.active&&!window.run?.complete&&!window.run?.failed)schedule();
  }

  function install(){
    const originalStart=window.startDungeon;
    if(typeof originalStart==='function'&&!originalStart._autoDungeonProgress){
      const wrappedStart=function(...args){
        clearTimer();
        const result=originalStart.apply(this,args);
        started=true;
        syncButtons();
        schedule();
        return result;
      };
      wrappedStart._autoDungeonProgress=true;
      window.startDungeon=wrappedStart;
    }
    const b=button();
    if(b&&!b._bossButtonBound){
      b._bossButtonBound=true;
      b.addEventListener('click',e=>{e.preventDefault();e.stopImmediatePropagation();runBossBattle();},true);
    }
    if(window.run?.active&&!started)schedule();
    syncButtons();
  }

  document.addEventListener('DOMContentLoaded',install);
  window.addEventListener('regions:ready',install);
  const observer=new MutationObserver(()=>{if(document.getElementById('stepDungeon'))install();});
  observer.observe(document.documentElement,{childList:true,subtree:true});
})();
