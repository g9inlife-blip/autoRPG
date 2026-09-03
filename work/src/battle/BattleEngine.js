class BattleEngine {
  constructor({ rng, damageCalculator, maxRounds = 60, onEvent = () => {} }) { this.rng = rng; this.damageCalculator = damageCalculator; this.maxRounds = maxRounds; this.onEvent = onEvent; }
  isAlive(actor) { return Number(actor?.hp) > 0; }
  run(players, enemies) {
    const state = { round: 0, result: 'ONGOING', players, enemies, events: [], startedAt: Date.now(), endedAt: null, cooldowns: {} };
    this.emit(state, 'BATTLE_START', { formation: this.getFormation(players), playerCount: players.length, enemyCount: enemies.length });
    while (state.round < this.maxRounds && state.result === 'ONGOING') {
      state.round++;
      for (const id of Object.keys(state.cooldowns)) state.cooldowns[id] = Math.max(0, state.cooldowns[id] - 1);
      const actors = [...players, ...enemies].filter(a => this.isAlive(a)).sort((a,b) => (Number(b.speed)||0)-(Number(a.speed)||0) || (this.rng.next()-.5));
      for (const actor of actors) {
        if (!this.isAlive(actor) || state.result !== 'ONGOING') continue;
        const targets = actor.team === 'player' ? enemies : players;
        const aliveTargets = targets.filter(t => this.isAlive(t));
        const target = actor.team === 'enemy' ? this.selectFormationTarget(aliveTargets, state) : aliveTargets.sort((a,b) => Number(a.hp)-Number(b.hp))[0];
        if (!target) break;
        const skill = this.chooseSkill(actor, state);
        if (skill) {
          state.cooldowns[actor.id] = skill.cooldown || 0;
          this.emit(state, 'SKILL_USE', { actorId: actor.id, actor: actor.name, skillId: skill.id, skill: skill.name, cooldown: skill.cooldown || 0 });
          this.resolveAttack(actor, target, state, skill);
          if (skill.extraHit && this.isAlive(target) && state.result === 'ONGOING') this.resolveAttack(actor, target, state, skill);
        } else this.resolveAttack(actor, target, state, null);
        this.checkResult(state);
      }
    }
    if (state.result === 'ONGOING') { state.result = 'LOSE'; state.endReason = 'TIMEOUT'; this.emit(state, 'BATTLE_TIMEOUT', { maxRounds: this.maxRounds, message: '전투가 60턴 이상 지속되어 이탈했습니다.' }); }
    state.endedAt = Date.now(); this.emit(state, 'BATTLE_END', { result: state.result, reason: state.endReason || null, maxRounds: this.maxRounds }); return state;
  }
  getFormation(players) { const positions=['frontline','middle','backline'],weights=[60,30,10]; return players.map((character,index)=>({characterId:character.id,character:character.name,position:positions[index]||'backline',targetWeight:weights[index]||10})); }
  selectFormationTarget(targets,state) { if(!targets.length)return null; const positions=['frontline','middle','backline'],weights=[60,30,10]; const candidates=targets.map(target=>{const index=state.players.indexOf(target);return{target,position:positions[index]||'backline',weight:weights[index]||10};}); const totalWeight=candidates.reduce((sum,c)=>sum+c.weight,0); let roll=this.rng.next()*totalWeight; for(const candidate of candidates){roll-=candidate.weight;if(roll<0)return candidate.target;} return candidates[candidates.length-1].target; }
  chooseSkill(actor,state) { const ids=Array.isArray(actor.skills)?actor.skills:[]; if(!ids.length||(state.cooldowns[actor.id]||0)>0)return null; const registry=window.SKILL_REGISTRY||{}; const available=ids.map(id=>registry[id]).filter(Boolean); return available.length?available[0]:null; }
  resolveAttack(attacker,defender,state,skill=null) {
    attacker.stats=attacker.stats||{attacks:0,hits:0,misses:0,criticals:0,damageDealt:0,damageTaken:0,kills:0};
    defender.stats=defender.stats||{attacks:0,hits:0,misses:0,criticals:0,damageDealt:0,damageTaken:0,kills:0};
    attacker.stats.attacks++;
    const result=this.damageCalculator.resolve(attacker,defender);
    if(skill&&result.hit)result.damage=Math.max(1,Math.floor(result.damage*(skill.multiplier||1)));
    if(result.hit){
      attacker.stats.hits++;if(result.critical)attacker.stats.criticals++;
      const beforeHp=defender.hp;defender.hp=Math.max(0,defender.hp-result.damage);
      attacker.stats.damageDealt+=result.damage;defender.stats.damageTaken+=result.damage;
      this.emit(state,'DAMAGE',{attackerId:attacker.id,attacker:attacker.name,defenderId:defender.id,defender:defender.name,beforeHp,afterHp:defender.hp,critical:!!result.critical,damage:result.damage,result,skillId:skill?.id||null,skill:skill?.name||null});
      if(!this.isAlive(defender)){attacker.stats.kills++;this.emit(state,'DEFEAT',{characterId:defender.id,character:defender.name,defeatedId:defender.id,defeatedTarget:defender.id,defeatedName:defender.name,side:defender.team,defeatedBy:attacker.name,skillId:skill?.id||null});}
    } else { attacker.stats.misses++; this.emit(state,'MISS',{attackerId:attacker.id,attacker:attacker.name,defenderId:defender.id,defender:defender.name,result,skillId:skill?.id||null,skill:skill?.name||null}); }
  }
  checkResult(state) { const playersAlive=state.players.some(p=>this.isAlive(p)),enemiesAlive=state.enemies.some(e=>this.isAlive(e)); if(!enemiesAlive)state.result='WIN'; else if(!playersAlive)state.result='LOSE'; }
  emit(state,type,data={}) { const event={type,round:state.round,data}; state.events.push(event); this.onEvent(event); }
}
window.BattleEngine=BattleEngine;
