(() => {
  function currentRegionId(){ return document.getElementById('regionSelect')?.value || Object.keys(window.REGIONS || {})[0] || null; }
  function sync(){
    const regionSelect=document.getElementById('regionSelect');
    const dungeonSelect=document.getElementById('dungeonSelect');
    const regions=Object.values(window.REGIONS || {});
    if(!regionSelect || !regions.length)return false;
    const oldRegion=regionSelect.value;
    regionSelect.innerHTML='';
    regions.forEach(r=>{const o=document.createElement('option');o.value=r.id;o.textContent=r.name;regionSelect.appendChild(o);});
    const regionId=regions.some(r=>r.id===oldRegion)?oldRegion:regions[0].id;
    regionSelect.value=regionId;
    const region=window.RegionRegistry?.get(regionId);
    if(dungeonSelect){
      const oldDungeon=dungeonSelect.value;
      const list=(region?.dungeons || []).map(id=>window.DUNGEONS?.[id]).filter(Boolean);
      dungeonSelect.innerHTML='';
      list.forEach(d=>{const o=document.createElement('option');o.value=d.id;o.textContent=`${d.name} (${d.levelStart}~${d.levelEnd}Lv / ${d.floors}층)`;dungeonSelect.appendChild(o);});
      if(list.some(d=>d.id===oldDungeon))dungeonSelect.value=oldDungeon;
      else if(list[0])dungeonSelect.value=list[0].id;
    }
    updateContext();
    return true;
  }
  function updateContext(){
    const region=window.RegionRegistry?.get(currentRegionId());
    const dungeons=(region?.dungeons || []).map(id=>window.DUNGEONS?.[id]).filter(Boolean);
    const range=dungeons.length?`${Math.min(...dungeons.map(d=>d.levelStart))}~${Math.max(...dungeons.map(d=>d.levelEnd))}Lv`:'-';
    const inn=document.getElementById('regionInnContext');if(inn)inn.innerHTML=`<b>${region?.name||'지역'}</b> · 던전 ${dungeons.length}개 · 권장 범위 ${range}`;
    const shop=document.getElementById('regionShopContext');if(shop)shop.innerHTML=`<b>${region?.name||'지역'}</b> · 지역 상점 데이터 ${Object.keys(region?.shop||{}).length?'설정됨':'기본 목록 사용'}`;
  }
  window.regionUiSync=sync;
  window.regionUiUpdateContext=updateContext;
  window.addEventListener('regions:ready',sync);
  document.addEventListener('DOMContentLoaded',()=>{sync();document.getElementById('regionSelect')?.addEventListener('change',()=>{sync();window.shopRender?.();window.render?.();});document.getElementById('dungeonSelect')?.addEventListener('change',updateContext);});
})();
