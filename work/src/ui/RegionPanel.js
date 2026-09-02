(() => {
  function currentRegionId(){ return document.getElementById('regionSelect')?.value || Object.keys(window.REGIONS || {})[0] || null; }
  function areaCount(d){
    if(!d)return 0;
    const declared=Number(d.areaCount);
    if(Number.isFinite(declared)&&declared>0)return Math.floor(declared);
    const max=(d.areas||[]).reduce((n,a)=>Math.max(n,Number(a?.area)||0),0);
    return max||Number(d.floors)||0;
  }
  function runActive(){return Boolean(window.run?.active);}
  function sync(){
    if(runActive())return false;
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
      list.forEach(d=>{const o=document.createElement('option');o.value=d.id;o.textContent=`${d.name} (${d.levelStart}~${d.levelEnd}Lv / ${areaCount(d)}구역)`;dungeonSelect.appendChild(o);});
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
  function lockMessage(message){const status=document.getElementById('status');if(status)status.textContent=message;}
  window.regionUiSync=sync;
  window.regionUiUpdateContext=updateContext;
  window.addEventListener('regions:ready',sync);
  document.addEventListener('DOMContentLoaded',()=>{sync();document.getElementById('regionSelect')?.addEventListener('change',updateContext);document.getElementById('dungeonSelect')?.addEventListener('change',updateContext);});
  document.addEventListener('click',event=>{
    if(!runActive())return;
    const target=event.target?.closest?.('.tabs [data-tab],#regionSelect,#dungeonSelect,#startDungeon,#reset');
    if(!target)return;
    if(target.matches('.tabs [data-tab]')){event.preventDefault();event.stopImmediatePropagation();lockMessage('던전 진행 중에는 다른 화면으로 이동할 수 없습니다.');return;}
    if(target.matches('#startDungeon')){event.preventDefault();event.stopImmediatePropagation();lockMessage('던전 진행 중에는 다른 던전을 시작할 수 없습니다.');return;}
    if(target.matches('#reset')){event.preventDefault();event.stopImmediatePropagation();lockMessage('던전 진행 중에는 초기화할 수 없습니다.');return;}
  },true);
  document.addEventListener('pointerdown',event=>{
    if(!runActive())return;
    const target=event.target?.closest?.('#regionSelect,#dungeonSelect');
    if(target){event.preventDefault();event.stopImmediatePropagation();lockMessage('던전 진행 중에는 지역/던전을 변경할 수 없습니다.');}
  },true);
  const syncControls=()=>{
    const active=runActive();
    ['regionSelect','dungeonSelect','seed'].forEach(id=>{const el=document.getElementById(id);if(el)el.disabled=active;});
  };
  const originalRender=window.render;
  if(typeof originalRender==='function'&&!originalRender._regionNavigationLock){
    const wrapped=function(...args){const result=originalRender.apply(this,args);syncControls();return result;};
    wrapped._regionNavigationLock=true;window.render=wrapped;
  }
  syncControls();
})();
