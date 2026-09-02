(() => {
  function getButton(){return document.getElementById('startDungeon');}
  function isRunning(){return Boolean(window.run?.active);}
  function sync(){
    const b=getButton();
    if(!b)return;
    const running=isRunning();
    b.textContent=running?'탐험중지':'탐험시작';
    b.classList.toggle('exploration-stop',running);
    b.setAttribute('aria-label',running?'탐험중지':'탐험시작');
  }
  function stop(){
    const r=window.run;
    if(!r?.active)return;
    r.active=false;
    if(window.state?.adventure)window.state.adventure.active=false;
    const status=document.getElementById('status');
    if(status)status.textContent='탐험을 중지했습니다.';
    window.render?.();
    sync();
  }
  function install(){
    const b=getButton();
    if(!b)return;
    if(!b.dataset.explorationControl){
      b.dataset.explorationControl='1';
      b.addEventListener('click',event=>{
        if(!isRunning())return;
        event.preventDefault();
        event.stopImmediatePropagation();
        stop();
      },true);
    }
    sync();
    if(!document.getElementById('exploration-stop-style')){
      const style=document.createElement('style');
      style.id='exploration-stop-style';
      style.textContent='.exploration-stop{background:#fff!important;color:#111!important;border:1px solid #111!important;} .exploration-stop:hover{background:#f5f5f5!important;color:#111!important;}';
      document.head.appendChild(style);
    }
  }
  document.addEventListener('DOMContentLoaded',install);
  window.addEventListener('regions:ready',install);
  const originalRender=window.render;
  if(typeof originalRender==='function'&&!originalRender._explorationControl){
    const wrapped=function(...args){const result=originalRender.apply(this,args);sync();return result;};
    wrapped._explorationControl=true;
    window.render=wrapped;
  }
  install();
})();
