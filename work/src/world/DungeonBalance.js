(() => {
  const TRIALS_DEFAULT = 100;
  function createParty() {
    if (typeof window.makeParty === 'function') return window.makeParty();
    const factory = new CharacterFactory();
    return (window.DUMMY_PARTY || []).map(x => factory.create({...x, team:'player'}));
  }
  function getMonster(dungeon, floor, rng) {
    const loader = window.MonsterXmlLoader;
    if (!loader?.state?.loaded) return null;
    const name = dungeon?.monsterDungeonName || dungeon?.name;
    const level = Number(dungeon?.levelMap?.[floor - 1] || floor);
    let pool = loader.getForDungeon(name, level);
    if (!pool.length) pool = loader.getByLevel(level);
    if (!pool.length) return null;
    const bosses = pool.filter(m => m.grade === 'boss' || m.form === 'boss');
    const normal = pool.filter(m => m.grade !== 'boss' && m.form !== 'boss');
    return rng.pick(floor === dungeon.floors && bosses.length ? bosses : (normal.length ? normal : pool));
  }
  function battle(dungeon, floor, seed) {
    const rng = new SeededRandom(seed);
    const party = createParty();
    const m = getMonster(dungeon, floor, rng);
    if (!m) return null;
    const enemy = new Character({id:`balance-${m.id}-${seed}`,name:m.name,hp:m.maxHp,maxHp:m.maxHp,attack:m.attack,defense:m.defense,speed:10,team:'enemy',exp:m.exp,level:m.level,monsterId:m.id,monsterGrade:m.grade,monsterForm:m.form});
    const engine = new BattleEngine({rng,damageCalculator:new DamageCalculator(rng)});
    const result = engine.run(party,[enemy]);
    return {win:result.result==='WIN',dead:!party.some(c=>c.alive),rounds:result.round,monster:m};
  }
  function runDungeonBalanceTest(id, runs=TRIALS_DEFAULT, seed=12345) {
    const dungeon = window.DUNGEONS?.[id] || Object.values(window.DUNGEONS || {}).find(d=>d.id===id) || Object.values(window.DUNGEONS || {})[0];
    if (!dungeon) return null;
    const floors=[]; let fullClear=0;
    for(let i=0;i<runs;i++){
      let clear=true;
      for(let floor=1;floor<=dungeon.floors;floor++){
        const r=battle(dungeon,floor,Number(seed)+i*10000+floor);
        if(!r){clear=false;continue;}
        if(!floors[floor-1]) floors[floor-1]={floor,level:r.monster.level,name:r.monster.name,grade:r.monster.grade,hp:r.monster.hp,attack:r.monster.attack,defense:r.monster.defense,exp:r.monster.exp,wins:0,deaths:0,rounds:0,count:0};
        const x=floors[floor-1]; x.wins+=r.win?1:0;x.deaths+=r.dead?1:0;x.rounds+=r.rounds;x.count++;
        if(!r.win){clear=false;break;}
      }
      if(clear)fullClear++;
    }
    floors.forEach(x=>{x.winRate=Math.round(x.wins/x.count*100);x.deathRate=Math.round(x.deaths/x.count*100);x.avgRounds=Math.round(x.rounds/x.count*10)/10;delete x.wins;delete x.deaths;delete x.rounds;delete x.count;});
    return {dungeon,runs,clearRate:Math.round(fullClear/runs*100),floors};
  }
  window.runDungeonBalanceTest=runDungeonBalanceTest;
})();
