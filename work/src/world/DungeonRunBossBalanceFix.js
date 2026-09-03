(() => {
  const OriginalDungeonRun = window.DungeonRun;
  if (!OriginalDungeonRun) return;
  const originalFightBoss = OriginalDungeonRun.prototype.fightBoss;
  OriginalDungeonRun.prototype.fightBoss = function () {
    if (!this.active || !this.bossReady) return { done:false, bossReady:false };
    const bossArea = this.floor + 1;
    if (bossArea > this.areaCount()) return originalFightBoss.call(this);
    const calculator = window.BattleBalanceCalculator;
    if (typeof calculator?.calculateMonsterStats !== 'function') throw new Error('전투 밸런스 계산기가 로드되지 않았습니다. XML 원본 HP/ATK는 보스 전투에 사용하지 않습니다.');
    this.restorePartyForBoss();
    this.floor = bossArea;
    this.encounterIndex = 0;
    this.bossReady = false;
    this.emit('FLOOR_REACHED', {floor:this.floor,area:this.floor,areaCount:this.areaCount(),maxEncounters:1,bossBattle:true});
    const sourceEnemies = this.getXmlEnemies();
    if (!sourceEnemies?.length) { this.active=false; this.failed=true; this.emit('RUN_FAILED',{floor:this.floor,area:this.floor,encounter:1,round:0,result:'NO_BOSS',enemies:[],battleEvents:[]}); return {done:true,failed:true}; }
    const enemies = sourceEnemies.map((m,index) => {
      const balance = calculator.calculateMonsterStats(m);
      if (!balance || !Number.isFinite(Number(balance.hp)) || !Number.isFinite(Number(balance.attack))) throw new Error(`보스 밸런스 계산 실패: ${m?.name || m?.id || 'monster'}`);
      return new Character({id:`monster-${m.id}-${this.floor}-${this.encounterIndex}-${index}`,name:m.name,hp:balance.hp,maxHp:balance.maxHp,attack:balance.attack,defense:balance.defense,speed:10,team:'enemy',exp:m.exp,level:m.level,monsterLevel:m.level,monsterId:m.id,monsterGrade:m.grade,monsterForm:m.form,placement:m.placement,balanceSource:balance});
    });
    const battle = this.fight(enemies);
    const result = battle?.result?.result || battle?.result || 'DRAW';
    const battleResult = battle?.result || {};
    const battleEvents = Array.isArray(battleResult.events) ? battleResult.events : [];
    const battleEnemies = enemies.map(e=>({id:e.monsterId||e.id,name:e.name,level:e.monsterLevel||e.level||null,grade:e.monsterGrade||e.grade||null,form:e.monsterForm||e.form||null,placement:e.placement||null,hp:e.maxHp,attack:e.attack,defense:e.defense,balanceSource:e.balanceSource?{hp:e.balanceSource.hp,attack:e.balanceSource.attack,sourceHp:e.balanceSource.sourceHp,sourceAttack:e.balanceSource.sourceAttack,hpTtkRounds:e.balanceSource.hpTtkRounds,survivalTtkRounds:e.balanceSource.survivalTtkRounds}:null}));
    if (result !== 'WIN') { this.active=false; this.complete=false; this.failed=true; this.emit('RUN_FAILED',{floor:this.floor,area:this.floor,encounter:1,round:Number(battle?.round)||0,result,enemies:battleEnemies,battleEvents}); return {done:true,battle:this.currentBattle,failed:true,bossBattle:true}; }
    this.encounterIndex=1;
    this.emit('AREA_ENCOUNTER',{area:this.floor,encounter:1,maxEncounters:1,remaining:0});
    this.addEquipmentLoot({boss:true,level:this.currentAreaLevel(enemies)});
    this.addLoot('boss-cache','수호자의 보물',1,500);
    if (this.floor===this.areaCount()) { this.complete=true; this.active=false; this.emit('RUN_COMPLETE',{floor:this.floor,areaCount:this.areaCount()}); }
    return {done:this.complete,battle:this.currentBattle,bossBattle:true,area:this.floor,encounter:1,maxEncounters:1};
  };
})();
