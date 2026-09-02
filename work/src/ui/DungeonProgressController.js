(() => {
  let timer = null;
  let started = false;

  function clearTimer(){
    if(timer){clearTimeout(timer);timer=null;}
  }

  function button(){return document.getElementById('stepDungeon');}

  function syncButton(){
    const b=button();
    if(!b)return;
    const r=window.run;
    const active=Boolean(r?.active);
    b.textContent='보스전';
    b.disabled=!active || !r?.bossReady;
    b.hidden=!active;
  }

  function schedule(){
    clearTimer();
    const r=window.run;
    if(!r?.active || r.complete || r.failed){syncButton();return;}
    timer=setTimeout(()=>{
      timer=null;
      const current=window.run;
      if(!current?.active || current.complete || current.failed){syncButton();return;}
      if(current.bossReady){syncButton();return;}
      window.stepDungeon?.();
      syncButton();
      if(window.run?.active && !window.run?.complete && !window.run?.failed) schedule();
    },1000);
  }

  function runBossBattle(){
    const r=window.run;
    if(!r?.active || !r.bossReady)return;
    clearTimer();
    try{
      const before=r.events.length;
      const result=r.fightBoss();
      const added=r.events.slice(before);
      if(typeof window.recordQuestEvents==='function')window.recordQuestEvents(added);
      const battle=added.find(e=>e.type==='BATTLE_END');
      if(battle){
        window.lastBattle={result:{result:battle.data.result,round:battle.data.round},events:battle.data.events,seconds:Math.max(.1,battle.data.events.length/10)};
        window.updateDialogue?.(battle.data.events);
      } else window.updateDialogue?.(added);
      window.state&&(window.state.adventure={active:r.active,currentEvent:r.events.at(-1)?.type||null});
      window.renderQuests?.();
      window.render?.();
      if(added.some(e=>e.type==='RUN_FAILED'))window.showRunResult?.(false);
      else if(added.some(e=>e.type==='RUN_COMPLETE')||result?.done)window.showRunResult?.(true);
    }catch(error){
      const status=document.getElementById('status');
      if(status)status.textContent='보스전 오류: '+error.message;
      console.error(error);
    }
    syncButton();
    if(window.run?.active && !window.run?.complete && !window.run?.failed) schedule();
  }

  function install(){
    const originalStart=window.startDungeon;
    if(typeof originalStart==='function'&&!originalStart._autoDungeonProgress){
      const wrappedStart=function(...args){
        clearTimer();
        const result=originalStart.apply(this,args);
        started=true;
        syncButton();
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

    if(window.run?.active&&!started) schedule();
    syncButton();
  }

  document.addEventListener('DOMContentLoaded',install);
  window.addEventListener('regions:ready',install);
  const observer=new MutationObserver(()=>{if(document.getElementById('stepDungeon'))install();});
  observer.observe(document.documentElement,{childList:true,subtree:true});
})();
