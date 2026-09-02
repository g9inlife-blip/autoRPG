(() => {
  function xmlMonster(dungeon, name){
    const loader=window.MonsterXmlLoader; const dn=dungeon?.monsterDungeonName||dungeon?.name;
    return loader?.state?.items?.find(m=>m.dungeonName===dn&&m.name===name)||null;
  }
  function resolve(dungeon, area, rng){
    const a=Array.isArray(dungeon?.areas)?dungeon.areas.find(x=>Number(x.area)===Number(area)):null;
    const encounters=Array.isArray(a?.encounters)?a.encounters:[];
    const e=encounters.length?encounters[rng.int(0,encounters.length-1)]:null;
    if(e?.enemies?.length)return e.enemies.map(x=>typeof x==='string'?xmlMonster(dungeon,x):x).filter(Boolean);
    if(e?.monsters?.length)return e.monsters.map(x=>xmlMonster(dungeon,x.name)||x).filter(Boolean);
    const target=Math.max(1,Math.min(50,Number(dungeon?.levelMap?.[Number(area)-1])||Number(area)||1));
    const pool=window.MonsterXmlLoader?.getForDungeon?.(dungeon?.monsterDungeonName||dungeon?.name,target)||[];
    return pool.length?[rng.pick(pool)]:[];
  }
  function isBoss(monsters){return (Array.isArray(monsters)?monsters:[]).some(m=>String(m?.placement||'').toLowerCase()==='boss'||String(m?.grade||m?.monsterGrade||'').toLowerCase()==='boss'||String(m?.form||m?.monsterForm||'').toLowerCase()==='boss');}
  window.EncounterResolver={resolve,isBoss};
})();
