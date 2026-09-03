(()=>{
  const $=id=>document.getElementById(id);
  function ensurePartyBar(){
    if($('globalPartyInfo'))return;
    const tabs=document.querySelector('.tabs');
    if(!tabs)return;
    const bar=document.createElement('section');
    bar.id='globalPartyInfo';
    bar.className='global-party-info';
    bar.innerHTML='<div class="global-party-title">파티 정보</div><div id="globalPartyCharacters" class="global-party-characters"></div>';
    tabs.before(bar);
  }
  function render(){
    ensurePartyBar();
    const root=$('globalPartyCharacters');
    const panel=window.activePartyPanel;
    const party=panel?.party||window.run?.party||[];
    if(!root)return;
    if(!party.length){root.innerHTML='<span class="muted">파티 정보 없음</span>';return;}
    root.innerHTML=party.map(c=>`<button type="button" class="global-party-character ${c.alive?'':'down'}" data-party-id="${c.id}"><span class="global-party-portrait">${c.appearance?.portrait||'?'}</span><span class="global-party-name">${c.name}</span><span class="global-party-meta">Lv.${Math.round(Number(c.level)||1)} · HP ${Math.round(Number(c.hp)||0)}/${Math.round(Number(c.maxHp)||0)}</span></button>`).join('');
    root.querySelectorAll('[data-party-id]').forEach(btn=>btn.onclick=()=>window.activePartyPanel?.showDetail(btn.dataset.partyId));
  }
  function patch(){
    ensurePartyBar();
    const oldRender=window.render;
    if(typeof oldRender==='function'&&!oldRender.__partyInfoPatched){
      const wrapped=function(){const result=oldRender.apply(this,arguments);render();return result;};
      wrapped.__partyInfoPatched=true;
      window.render=wrapped;
    }
    const oldActivate=window.activateTab;
    if(typeof oldActivate==='function'&&!oldActivate.__partyInfoPatched){
      const wrapped=function(tabId){const result=oldActivate.apply(this,arguments);render();return result;};
      wrapped.__partyInfoPatched=true;
      window.activateTab=wrapped;
    }
    const oldPartyRender=window.PartyPanel?.prototype?.render;
    if(typeof oldPartyRender==='function'&&!oldPartyRender.__partyInfoPatched){
      const wrapped=function(){const result=oldPartyRender.apply(this,arguments);requestAnimationFrame(render);return result;};
      wrapped.__partyInfoPatched=true;
      window.PartyPanel.prototype.render=wrapped;
    }
    render();
  }
  window.partyInfoNav={init:patch,render};
  document.addEventListener('DOMContentLoaded',patch);
  patch();
})();
