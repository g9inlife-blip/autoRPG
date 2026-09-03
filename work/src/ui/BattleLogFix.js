(() => {
  function formatEvent(e){
    const d=e?.data||{}; const r=Math.round(Number(e?.round)||0);
    if(e?.type==='SKILL_USE')return `R${r} · ${d.actor||'?'} → ${d.skill||d.skillId||'스킬'} 사용${d.cooldown?` · 재사용 ${Math.round(Number(d.cooldown)||0)}R`:''}`;
    if(e?.type==='DAMAGE')return `R${r} · ${d.attacker||'?'} → ${d.defender||'?'} · ${Math.round(Number(d.damage)||0)} 피해${d.critical?' · CRITICAL':''}${d.skill?` · ${d.skill}`:''} · HP ${Math.round(Number(d.beforeHp)||0)}→${Math.round(Number(d.afterHp)||0)}`;
    if(e?.type==='MISS')return `R${r} · ${d.attacker||'?'} → ${d.defender||'?'} · 빗나감${d.skill?` · ${d.skill}`:''}`;
    if(e?.type==='DEFEAT')return `R${r} · ${d.character||d.defeatedName||'?'} 기절 · ${d.defeatedBy||'?'}의 공격`;
    if(e?.type==='BATTLE_START')return '전투 시작';
    if(e?.type==='BATTLE_TIMEOUT')return `R${r} · 전투가 길어져 이탈하였다`;
    if(e?.type==='BATTLE_END')return d.reason==='TIMEOUT'?'전투 종료 · 전투가 길어져 이탈하였다':`전투 종료 · ${d.result||'?'}`;
    return e?.type||'';
  }
  function enemyLabel(enemy){
    if(typeof enemy==='string')return enemy;
    const name=enemy?.name||enemy?.Name||enemy?.monsterName||enemy?.id||'?';
    const grade=enemy?.grade||enemy?.Grade||enemy?.rarity||enemy?.monsterGrade;
    return grade?`${name}(${grade})`:name;
  }
  function defeatedSummary(events){
    const names=(events||[]).filter(e=>e?.type==='DEFEAT').map(e=>e.data?.character||e.data?.defeatedName).filter(Boolean);
    return names.length?` (${names.join(', ')} 기절)`:'';
  }
  function getBattleEvents(data){
    const own=Array.isArray(data?.events)?data.events:[];
    const live=window.run?.currentBattle?.result?.events;
    return Array.isArray(live)&&live.length>own.length?live:own;
  }
  window.showLogDetail=function(e){
    const modal=document.getElementById('logDetail'); if(!modal)return;
    const d=e?.data||{}; let title='탐험 상세', body='';
    if(e?.type==='BATTLE_END'){
      const events=getBattleEvents(d);
      const timeout=d.reason==='TIMEOUT'||events.some(x=>x?.type==='BATTLE_TIMEOUT');
      const resultText=timeout?'전투가 길어져 이탈하였다':(d.result||'?');
      title=`[${e.floor}구역] 전투 상세`;
      body=`<p><b>결과:</b> ${resultText} / ${Math.round(Number(d.round)||0)} Round${defeatedSummary(events)}</p><p><b>적:</b> ${(d.enemies||[]).map(enemyLabel).join(', ')||'-'}</p><div class="detail-body">${events.map(x=>`<div class="detail-line">${formatEvent(x)}</div>`).join('')}</div>`;
    } else if(e?.type==='RUN_FAILED'){
      const events=getBattleEvents(d);
      const timeout=d.result==='LOSE'&&(d.reason==='TIMEOUT'||events.some(x=>x?.type==='BATTLE_TIMEOUT'));
      title=`[${e.floor}구역] 전투 상세`;
      body=`<p><b>결과:</b> ${timeout?'전투가 길어져 이탈하였다':(d.result||'LOSE')} / ${Math.round(Number(d.round)||0)} Round${defeatedSummary(events)}</p><p><b>적:</b> ${(d.enemies||[]).map(enemyLabel).join(', ')||'-'}</p><div class="detail-body">${events.map(x=>`<div class="detail-line">${formatEvent(x)}</div>`).join('')}</div>`;
    } else if(e?.type==='BATTLE_TIMEOUT'){
      title='전투 상세'; body=`<div class="detail-body">${formatEvent(e)}</div>`;
    } else if(e?.type==='LOOT'){
      title=`[${e.floor}구역] 획득 아이템`; body=`<p>${d.itemName||d.itemId||'아이템'} ×${d.quantity||1}</p><p>골드 +${Math.round(Number(d.gold)||0)}</p>`;
    } else if(e?.type==='FLOOR_REACHED'){
      title=`[${e.floor}구역] 구역 진입`; body='<p>새로운 구역으로 이동했습니다.</p>';
    } else if(e?.type==='RUN_START'){
      title='탐험 시작'; body='<p>던전 탐험을 시작했습니다.</p>';
    } else if(e?.type==='RUN_COMPLETE'){
      title='탐험 완료'; body=`<p>${Math.round(Number(e.floor)||0)}구역까지 탐험했습니다.</p>`;
    } else body=`<p>${formatEvent(e)}</p>`;
    modal.innerHTML=`<div class="modal-card"><button type="button" class="close" aria-label="닫기">×</button><h2>${title}</h2>${body}</div>`;
    modal.hidden=false;
    modal.querySelector('.close')?.addEventListener('click',()=>modal.hidden=true);
    modal.addEventListener('click',ev=>{if(ev.target===modal)modal.hidden=true;},{once:true});
  };
})();
