(()=>{
  const $=id=>document.getElementById(id);
  function ensurePartyBar(){
    if($('globalPartyInfo'))return;
    const toolbar=document.querySelector('.toolbar');
    if(!toolbar)return;
    const bar=document.createElement('section');
    bar.id='globalPartyInfo';
    bar.className='global-party-info';
    bar.innerHTML='<div class="global-party-title">파티 정보</div><div id="globalPartyCharacters" class="global-party-characters"></div>';
    toolbar.after(bar);
  }
  function render(){
    ensurePartyBar();
    const root=$('globalPartyCharacters');
    const panel=window.activePartyPanel;
    const party=panel?.party||window.run?.party||[];
    if(!root)return;
    if(!party.length){root.innerHTML='<span class="muted">파티 정보 없음</span>';return;}
    root.innerHTML=party.map(c=>`<button type="button" class="global-party-character" data-party-id="${c.id}"><span class="global-party-portrait">${c.appearance?.portrait||'?'}</span><span class="global-party-name">${c.name}</span><span class="global-party-meta">Lv.${Math.round(Number(c.level)||1)} · HP ${Math.round(Number(c.hp)||0)}/${Math.round(Number(c.maxHp)||0)}</span></button>`).join('');
    root.querySelectorAll('[data-party-id]').forEach(btn=>btn.onclick=()=>window.activePartyPanel?.showDetail(btn.dataset.partyId));
  }
  window.partyInfoNav={init:render,render};
  document.addEventListener('DOMContentLoaded',render);
})();
