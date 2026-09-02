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
    for (let floor=1; floor<=dungeon.floors; floor++) {
      const results=[];
      for(let i=0;i<TRIALS;i++) { const r=runOne(dungeon,floor,Number(window.state?.seed||12345)+floor*1000+i); if(r) results.push(r); }
      if(!results.length) continue;
      const wins=results.filter(x=>x.win).length;
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

// 전투 스킬 확장: 기존 공격 스킬 구조를 유지하면서 마법사 전용 파티 힐을 추가한다.
(() => {
  const registry = window.SKILL_REGISTRY || {};
  registry['party-heal'] = { id:'party-heal', name:'치유의 샘', classId:'mage', cooldown:3, healPercent:0.15, target:'party', description:'전투 중 아군 전체의 최대 HP 15%를 회복합니다.' };
  window.SKILL_REGISTRY = registry;
  if (window.CHARACTER_CLASSES?.mage) {
    window.CHARACTER_CLASSES.mage.skills = Array.from(new Set([...(window.CHARACTER_CLASSES.mage.skills || []), 'party-heal']));
  }
  if (!window.BattleEngine) return;
  const originalChooseSkill = BattleEngine.prototype.chooseSkill;
  const originalResolveAttack = BattleEngine.prototype.resolveAttack;
  BattleEngine.prototype.chooseSkill = function(actor, state) {
    if ((state.cooldowns[actor.id] || 0) > 0) return null;
    const ids = Array.isArray(actor.skills) ? actor.skills : [];
    const available = ids.map(id => registry[id]).filter(Boolean);
    if (!available.length) return originalChooseSkill.call(this, actor, state);
    const heal = available.find(skill => skill.target === 'party' && Number(skill.healPercent) > 0);
    if (heal && actor.team === 'player') {
      const allies = state.players.filter(c => c.alive);
      if (allies.some(c => Number(c.hp) < Number(c.maxHp) * 0.85)) return heal;
    }
    return available.find(skill => skill.target !== 'party') || originalChooseSkill.call(this, actor, state);
  };
  BattleEngine.prototype.resolveAttack = function(attacker, defender, state, skill=null) {
    if (skill?.target === 'party' && Number(skill.healPercent) > 0 && attacker.team === 'player') {
      const healed = state.players.filter(c => c.alive).map(c => {
        const beforeHp = Number(c.hp) || 0;
        const amount = Math.max(0, Math.ceil(Number(c.maxHp || 0) * Number(skill.healPercent)));
        c.hp = Math.min(c.maxHp, beforeHp + amount);
        return {id:c.id,name:c.name,beforeHp,afterHp:c.hp,amount:c.hp-beforeHp,maxHp:c.maxHp};
      });
      this.emit(state, 'HEAL', {actorId:attacker.id,actor:attacker.name,skillId:skill.id,skill:skill.name,percent:Number(skill.healPercent)*100,targets:healed});
      return;
    }
    return originalResolveAttack.call(this, attacker, defender, state, skill);
  };
})();
