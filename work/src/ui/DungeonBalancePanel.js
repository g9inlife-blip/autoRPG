(() => {
  const TRIALS = 100;
  function freshParty() {
    if (typeof window.makeParty === 'function') return window.makeParty();
    const factory = new CharacterFactory();
    return (window.DUMMY_PARTY || []).map(x => factory.create({...x, team:'player'}));
  }
  function monsterFor(dungeon, floor, rng) {
    const loader = window.MonsterXmlLoader;
    if (!loader?.state?.loaded) return null;
    const name = dungeon?.monsterDungeonName || dungeon?.name;
    const level = Number(dungeon?.levelMap?.[floor - 1] || floor);
    let pool = loader.getForDungeon(name, level);
    if (!pool.length) pool = loader.getByLevel(level);
    if (!pool.length) return null;
    const boss = floor === dungeon.floors ? pool.filter(m => m.grade === 'boss' || m.form === 'boss') : pool.filter(m => m.grade !== 'boss' && m.form !== 'boss');
    return rng.pick(boss.length ? boss : pool);
  }
  function runOne(dungeon, floor, seed) {
    const rng = new SeededRandom(seed);
    const party = freshParty();
    const m = monsterFor(dungeon, floor, rng);
    if (!m) return null;
    const enemy = new Character({id:`balance-${m.id}-${seed}`,name:m.name,hp:m.maxHp,maxHp:m.maxHp,attack:m.attack,defense:m.defense,speed:10,team:'enemy',exp:m.exp,level:m.level,monsterId:m.id,monsterGrade:m.grade,monsterForm:m.form});
    const engine = new BattleEngine({rng,damageCalculator:new DamageCalculator(rng)});
    const result = engine.run(party,[enemy]);
    return {win:result.result === 'WIN', rounds:result.round, partyDead:!party.some(c => c.alive), monster:m};
  }
  function render(dungeon) {
    const modal = document.getElementById('logDetail');
    if (!modal || !dungeon) return;
    const rows = [];
    let clears = 0;
    for (let floor=1; floor<=dungeon.floors; floor++) {
      const results=[];
      for(let i=0;i<TRIALS;i++) { const r=runOne(dungeon,floor,Number(window.state?.seed||12345)+floor*1000+i); if(r) results.push(r); }
      if(!results.length) continue;
      const wins=results.filter(x=>x.win).length;
      clears+=wins===TRIALS?1:0;
      const avg=Math.round(results.reduce((n,x)=>n+x.rounds,0)/results.length*10)/10;
      const deaths=results.filter(x=>x.partyDead).length;
      const m=results[0].monster;
      const grade=String(m.grade||'').toUpperCase();
      rows.push(`<div class="detail-line"><b>${floor}층 Lv.${m.level} · ${m.name} [${grade}]</b><br>승률 ${wins}% · 평균 ${avg}R · 파티 전멸 ${deaths}%<br>HP ${Math.round(m.hp)} / ATK ${Math.round(m.attack)} / DEF ${Math.round(m.defense)} / EXP ${Math.round(m.exp)}</div>`);
    }
    modal.innerHTML=`<div class="modal-card"><button type="button" class="close" aria-label="닫기">×</button><h2>${dungeon.name} · 밸런스 테스트</h2><p>층별 ${TRIALS}회 실전 전투 시뮬레이션</p><div class="detail-body">${rows.join('')||'<p>몬스터 데이터를 불러오지 못했습니다.</p>'}</div>`;
    modal.hidden=false;
    modal.querySelector('.close').addEventListener('click',()=>modal.hidden=true);
    modal.addEventListener('click',ev=>{if(ev.target===modal)modal.hidden=true;},{once:true});
  }
  window.DungeonBalancePanel={render};
})();
