(() => {
  function xmlMonster(dungeon, name){
    const loader=window.MonsterXmlLoader; const dn=dungeon?.monsterDungeonName||dungeon?.name;
    return loader?.state?.items?.find(m=>m.dungeonName===dn&&m.name===name)||null;
  }
  function isSpecialMonster(monster){
    const grade=String(monster?.grade||monster?.monsterGrade||'').toLowerCase();
    const form=String(monster?.form||monster?.monsterForm||'').toLowerCase();
    const placement=String(monster?.placement||'').toLowerCase();
    return grade==='boss'||grade==='midboss'||grade==='mini-boss'||grade==='miniboss'||grade==='elite'||form==='boss'||form==='midboss'||form==='mini-boss'||form==='miniboss'||form==='elite'||placement==='boss'||placement==='midboss'||placement==='mini-boss'||placement==='miniboss'||placement==='elite';
  }
  function isBoss(monsters){return (Array.isArray(monsters)?monsters:[]).some(m=>String(m?.placement||'').toLowerCase()==='boss'||String(m?.grade||m?.monsterGrade||'').toLowerCase()==='boss'||String(m?.form||m?.monsterForm||'').toLowerCase()==='boss');}
  function getArea(dungeon,area){return Array.isArray(dungeon?.areas)?dungeon.areas.find(x=>Number(x.area)===Number(area)):null;}
  function areaHasSpecial(dungeon, area){
    const a=getArea(dungeon,area); const encounters=Array.isArray(a?.encounters)?a.encounters:[];
    for(const e of encounters){
      const list=[...(Array.isArray(e?.enemies)?e.enemies:[]),...(Array.isArray(e?.monsters)?e.monsters:[])];
      if(list.some(x=>isSpecialMonster(typeof x==='string'?xmlMonster(dungeon,x):x)))return true;
    }
    return false;
  }
  function encounterToMonsters(dungeon,e){
    if(e?.enemies?.length)return e.enemies.map(x=>typeof x==='string'?xmlMonster(dungeon,x):x).filter(Boolean);
    if(e?.monsters?.length)return e.monsters.map(x=>xmlMonster(dungeon,x.name)||x).filter(Boolean);
    return [];
  }
  function resolveSpecial(dungeon, area){
    const a=getArea(dungeon,area); const encounters=Array.isArray(a?.encounters)?a.encounters:[];
    for(const e of encounters){const monsters=encounterToMonsters(dungeon,e);if(monsters.length&&monsters.some(isSpecialMonster))return monsters;}
    return [];
  }
  function resolve(dungeon, area, rng){
    const a=getArea(dungeon,area); const encounters=Array.isArray(a?.encounters)?a.encounters:[];
    const e=encounters.length?encounters[rng.int(0,encounters.length-1)]:null;
    const explicit=encounterToMonsters(dungeon,e);
    if(explicit.length)return explicit;
    const target=Math.max(1,Math.min(50,Number(dungeon?.levelMap?.[Number(area)-1])||Number(area)||1));
    const pool=window.MonsterXmlLoader?.getForDungeon?.(dungeon?.monsterDungeonName||dungeon?.name,target)||[];
    const normalPool=pool.filter(m=>!isSpecialMonster(m));
    const usable=normalPool.length?normalPool:pool;
    return usable.length?[rng.pick(usable)]:[];
  }
  window.EncounterResolver={resolve,resolveSpecial,isBoss,isSpecialMonster,areaHasSpecial};
})();
