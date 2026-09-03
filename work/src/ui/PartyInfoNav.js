(()=>{
  function openPartyInfo(){
    if(typeof window.activateTab==='function') window.activateTab('innTab');
    requestAnimationFrame(()=>document.getElementById('party')?.scrollIntoView({behavior:'smooth',block:'start'}));
  }
  window.openPartyInfo=openPartyInfo;
  document.addEventListener('DOMContentLoaded',()=>document.getElementById('partyInfo')?.addEventListener('click',openPartyInfo));
})();
