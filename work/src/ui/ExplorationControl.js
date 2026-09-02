(() => {
  const AUTO_MS = 5000;
  let autoTimer = null;

  function getStartButton(){return document.getElementById('startDungeon');}
  function getNextButton(){return document.getElementById('stepDungeon');}
  function isRunning(){return Boolean(window.run?.active);}
  function isBossReady(){return Boolean(window.run?.bossReady);}

  function ensureBossButton(){
    const start=getStartButton();
    if(!start)return null;
    let boss=document.getElementById('bossDungeon');
    if(!boss){
      boss=document.createElement('button');
      boss.id='bossDungeon';
      boss.type='button';
      boss.textContent='보스전';
      boss.setAttribute('aria-label','보스전');
      boss.className='boss-dungeon-button';
      start.parentNode?.insertBefore(boss,start.nextSibling);
      boss.addEventListener('click',event=>{
        event.preventDefault();
        event.stopImmediatePropagation();
        if(!isRunning()||!isBossReady())return;
        fightBoss();
      },true);
    }
    return boss;
  }

  function clearAuto(){
    if(autoTimer!==null){clearInterval(autoTimer);autoTimer=null;}
  }

  function scheduleAuto(){
    clearAuto();
    if(!isRunning()||isBossReady())return;
    autoTimer=setInterval(()=>{
      if(!isRunning()||isBossReady()){clearAuto();sync();return;}
      if(typeof window.stepDungeon==='function')window.stepDungeon();
      const r=window.run;
      if(!r?.active){clearAuto();sync();return;}
      if(r.bossReady){
        const bossArea=Number(r.bossReadyArea||r.events?.slice().reverse().find(e=>e.type==='BOSS_READY')?.data?.bossArea||r.events?.slice().reverse().find(e=>e.type==='BOSS_READY')?.data?.area||0);
        if(bossArea>0)r.bossReadyArea=bossArea;
        if(Number(r.floor)+1===Number(r.bossReadyArea||bossArea)){
          r.floor=1;
          r.encounterIndex=0;
          if(typeof window.render==='function')window.render();
        }
        scheduleAuto();
      }
    },AUTO_MS);
  }

  function fightBoss(){
    const r=window.run;
    if(!r?.active||!r.bossReady||typeof r.fightBoss!=='function')return;
    clearAuto();
    const before=r.events.length;
    const bossArea=Number(r.bossReadyArea||r.events?.slice().reverse().find(e=>e.type==='BOSS_READY')?.data?.bossArea||r.events?.slice().reverse().find(e=>e.type==='BOSS_READY')?.data?.area||0);
    if(bossArea>0)r.floor=bossArea-1;
    const result=r.fightBoss();
    const added=r.events.slice(before);
    if(typeof window.recordQuestEvents==='function')window.recordQuestEvents(added);
    const battle=added.find(e=>e.type==='BATTLE_END');
    if(battle){
      window.lastBattle={result:{result:battle.data.result,round:battle.data.round},events:battle.data.events,seconds:Math.max(.1,battle.data.events.length/10)};
      if(typeof window.updateDialogue==='function')window.updateDialogue(battle.data.events);
    }
    window.state && (window.state.adventure={active:r.active,currentEvent:r.events.at(-1)?.type||null});
    if(typeof window.renderQuests==='function')window.renderQuests();
    if(typeof window.render==='function')window.render();
    if(result?.failed||r.failed){
      if(typeof window.showRunResult==='function')window.showRunResult(false);
    }else if(result?.done||r.complete){
      if(typeof window.showRunResult==='function')window.showRunResult(true);
    }
    sync();
  }

  function stop(){
    clearAuto();
    const r=window.run;
    if(!r?.active)return;
    r.active=false;
    if(window.state?.adventure)window.state.adventure.active=false;
    const status=document.getElementById('status');
    if(status)status.textContent='탐험을 중지했습니다.';
    window.render?.();
    sync();
  }

  function sync(){
    const start=getStartButton();
    const next=getNextButton();
    const boss=ensureBossButton();
    const running=isRunning();
    const ready=isBossReady();
    if(start){
      start.textContent=running?'탐험중지':'탐험시작';
      start.classList.toggle('exploration-stop',running);
      start.setAttribute('aria-label',running?'탐험중지':'탐험시작');
    }
    if(next)next.hidden=true;
    if(boss){
      boss.hidden=!running;
      boss.disabled=!ready;
      boss.classList.toggle('boss-ready',ready);
      boss.title=ready?'보스전에 돌입합니다.':'보스전 준비 중';
    }
  }

  function install(){
    const start=getStartButton();
    if(!start)return;
    if(!start.dataset.explorationControl){
      start.dataset.explorationControl='1';
      start.addEventListener('click',event=>{
        if(isRunning()){
          event.preventDefault();
          event.stopImmediatePropagation();
          stop();
          return;
        }
        setTimeout(()=>{
          sync();
          if(isRunning())scheduleAuto();
        },0);
      },true);
    }
    ensureBossButton();
    sync();
    if(!document.getElementById('exploration-control-style')){
      const style=document.createElement('style');
      style.id='exploration-control-style';
      style.textContent='.exploration-stop{background:#fff!important;color:#111!important;border:1px solid #111!important;} .exploration-stop:hover{background:#f5f5f5!important;color:#111!important;} #bossDungeon.boss-ready{font-weight:700;} #bossDungeon:disabled{opacity:.55;cursor:default;}';
      document.head.appendChild(style);
    }
  }

  document.addEventListener('DOMContentLoaded',install);
  window.addEventListener('regions:ready',install);
  install();
})();
