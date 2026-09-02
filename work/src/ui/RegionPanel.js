(() => {
  function currentRegionId(){ return document.getElementById('regionSelect')?.value || Object.keys(window.REGIONS || {})[0] || null; }
  function areaCount(d){if(!d)return 0;const declared=Number(d.areaCount);if(Number.isFinite(declared)&&declared>0)return Math.floor(declared);const max=(d.areas||[]).reduce((n,a)=>Math.max(n,Number(a?.area)||0),0);return max||Number(d.floors)||0;}
  function runActive(){return Boolean(window.run?.active);}
  function sync(){
    if(runActive())return false;
    const regionSelect=document.getElementById('regionSelect'),dungeonSelect=document.getElementById('dungeonSelect'),regions=Object.values(window.REGIONS||{});
    if(!regionSelect||!regions.length)return false;
    const oldRegion=regionSelect.value;regionSelect.innerHTML='';regions.forEach(r=>{const o=document.createElement('option');o.value=r.id;o.textContent=r.name;regionSelect.appendChild(o);});
    const regionId=regions.some(r=>r.id===oldRegion)?oldRegion:regions[0].id;regionSelect.value=regionId;const region=window.RegionRegistry?.get(regionId);
    if(dungeonSelect){const oldDungeon=dungeonSelect.value;const list=(region?.dungeons||[]).map(id=>window.DUNGEONS?.[id]).filter(Boolean);dungeonSelect.innerHTML='';list.forEach(d=>{const o=document.createElement('option');o.value=d.id;o.textContent=`${d.name} (${d.levelStart}~${d.levelEnd}Lv / ${areaCount(d)}구역)`;dungeonSelect.appendChild(o);});if(list.some(d=>d.id===oldDungeon))dungeonSelect.value=oldDungeon;else if(list[0])dungeonSelect.value=list[0].id;}
    updateContext();return true;
  }
  function updateContext(){const region=window.RegionRegistry?.get(currentRegionId());const dungeons=(region?.dungeons||[]).map(id=>window.DUNGEONS?.[id]).filter(Boolean);const range=dungeons.length?`${Math.min(...dungeons.map(d=>d.levelStart))}~${Math.max(...dungeons.map(d=>d.levelEnd))}Lv`:'-';const inn=document.getElementById('regionInnContext');if(inn)inn.innerHTML=`<b>${region?.name||'지역'}</b> · 던전 ${dungeons.length}개 · 권장 범위 ${range}`;const shop=document.getElementById('regionShopContext');if(shop)shop.innerHTML=`<b>${region?.name||'지역'}</b> · 지역 상점 데이터 ${Object.keys(region?.shop||{}).length?'설정됨':'기본 목록 사용'}`;}
  function updateEncounterProgress(){const line=document.querySelector('#dungeon .dungeon-info-line');if(!line)return;line.querySelector('[data-encounter-progress]')?.remove();const r=window.run;if(!r?.active)return;const max=typeof r.getAreaMaxEncounters==='function'?r.getAreaMaxEncounters(r.floor):5;const current=Math.min(Number(r.encounterIndex)||0,max);const span=document.createElement('span');span.dataset.encounterProgress='1';span.innerHTML=`전투 ${current}/${max}`;const sep=document.createElement('i');sep.className='sep';sep.textContent='·';line.appendChild(sep);line.appendChild(span);}
  function lockMessage(message){const status=document.getElementById('status');if(status)status.textContent=message;}
  window.regionUiSync=sync;window.regionUiUpdateContext=updateContext;window.regionUiUpdateEncounterProgress=updateEncounterProgress;window.addEventListener('regions:ready',sync);
  document.addEventListener('DOMContentLoaded',()=>{sync();document.getElementById('regionSelect')?.addEventListener('change',updateContext);document.getElementById('dungeonSelect')?.addEventListener('change',updateContext);});

  // 탐험 중에도 던전/여관/상점/기타 탭은 반드시 이동 가능하게 한다.
  // 기존 탭 이벤트가 탐험 상태 때문에 막더라도 여기서 직접 activateTab을 호출한다.
  document.addEventListener('click',event=>{
    const tab=event.target?.closest?.('.tabs [data-tab]');
    if(!tab)return;
    const tabId=tab.dataset.tab;
    if(!tabId)return;
    event.preventDefault();
    event.stopImmediatePropagation();
    if(typeof window.activateTab==='function')window.activateTab(tabId);
    else{
      document.querySelectorAll('.tabs [data-tab]').forEach(btn=>btn.classList.toggle('active',btn===tab));
      document.querySelectorAll('.tab-panel').forEach(panel=>panel.classList.toggle('active',panel.id===tabId));
    }
  },true);

  // 탐험 중에는 초기화/지역/던전 변경만 차단한다.
  document.addEventListener('click',event=>{if(!runActive())return;const target=event.target?.closest?.('#reset');if(target){event.preventDefault();event.stopImmediatePropagation();lockMessage('던전 진행 중에는 초기화할 수 없습니다.');}},true);
  document.addEventListener('change',event=>{if(!runActive())return;const target=event.target?.closest?.('#regionSelect,#dungeonSelect');if(!target)return;event.preventDefault();event.stopImmediatePropagation();lockMessage('던전 진행 중에는 지역/던전을 변경할 수 없습니다.');window.regionUiSync?.();},true);
  document.addEventListener('pointerdown',event=>{if(!runActive())return;const target=event.target?.closest?.('#regionSelect,#dungeonSelect');if(target){event.preventDefault();event.stopImmediatePropagation();lockMessage('던전 진행 중에는 지역/던전을 변경할 수 없습니다.');}},true);

  // 구매/판매 및 장비 변경 버튼만 차단. 화면 이동과 정보 열람은 차단하지 않는다.
  const mutationSelectors=['#shop button[data-action="buy"]','#shop button[data-action="sell"]','#shop button[data-action="equip"]','#shop button[data-action="unequip"]','#shop .shop-buy','#shop .shop-sell','#shop .buy-button','#shop .sell-button','#shop .equip-button','#shop .unequip-button','#shop [onclick*="buy"]','#shop [onclick*="sell"]','#shop [onclick*="equip"]','#shop [onclick*="unequip"]','#party button[data-action="equip"]','#party button[data-action="unequip"]','#party .equip-button','#party .unequip-button','#party [onclick*="equip"]','#party [onclick*="unequip"]'];
  document.addEventListener('click',event=>{if(!runActive())return;const target=event.target?.closest?.(mutationSelectors.join(','));if(!target)return;event.preventDefault();event.stopImmediatePropagation();lockMessage('던전 진행 중에는 구매/판매 및 장비 변경을 할 수 없습니다.');},true);
  const syncControls=()=>{const active=runActive();['regionSelect','dungeonSelect','seed'].forEach(id=>{const el=document.getElementById(id);if(el)el.disabled=active;});updateEncounterProgress();};
  syncControls();
  if(!document.querySelector('script[data-exploration-control]')){const script=document.createElement('script');script.src='src/ui/ExplorationControl.js?v=20260903-4';script.dataset.explorationControl='1';document.body.appendChild(script);}
})();
