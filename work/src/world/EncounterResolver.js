(() => {
  function xmlMonster(dungeon, name){
    const loader=window.MonsterXmlLoader; const dn=dungeon?.monsterDungeonName||dungeon?.name;
    return loader?.state?.items?.find(m=>m.dungeonName===dn&&m.name===name)||null;
  }
  function monsterKind(monster){
    const grade=String(monster?.grade||monster?.monsterGrade||'').toLowerCase();
    const form=String(monster?.form||monster?.monsterForm||'').toLowerCase();
    const placement=String(monster?.placement||'').toLowerCase();
    if(grade==='boss'||form==='boss'||placement==='boss')return 'boss';
    if(grade==='midboss'||grade==='mini-boss'||grade==='miniboss'||form==='midboss'||form==='mini-boss'||form==='miniboss'||placement==='midboss'||placement==='mini-boss'||placement==='miniboss')return 'midboss';
    if(grade==='elite'||form==='elite'||placement==='elite')return 'elite';
    return '';
  }
  function isSpecialMonster(monster){return Boolean(monsterKind(monster));}
  function isBoss(monsters){return (Array.isArray(monsters)?monsters:[]).some(m=>monsterKind(m)==='boss');}
  function isMidboss(monsters){return (Array.isArray(monsters)?monsters:[]).some(m=>monsterKind(m)==='midboss'||monsterKind(m)==='elite');}
  function isLimitedSpecial(monster){const kind=monsterKind(monster);return kind==='midboss'||kind==='elite';}
  function getArea(dungeon,area){return Array.isArray(dungeon?.areas)?dungeon.areas.find(x=>Number(x.area)===Number(area)):null;}
  function areaHasBoss(dungeon,area){
    const a=getArea(dungeon,area); const encounters=Array.isArray(a?.encounters)?a.encounters:[];
    for(const e of encounters){const list=[...(Array.isArray(e?.enemies)?e.enemies:[]),...(Array.isArray(e?.monsters)?e.monsters:[])];if(list.some(x=>isBoss([typeof x==='string'?xmlMonster(dungeon,x):x])))return true;}
    return false;
  }
  function areaHasSpecial(dungeon,area){return areaHasBoss(dungeon,area);}
  function encounterToMonsters(dungeon,e){
    if(e?.enemies?.length)return e.enemies.map(x=>typeof x==='string'?xmlMonster(dungeon,x):x).filter(Boolean);
    if(e?.monsters?.length)return e.monsters.map(x=>xmlMonster(dungeon,x.name)||x).filter(Boolean);
    return [];
  }
  function specialKey(monster){return String(monster?.id??monster?.name??'');}
  function seenState(){
    const r=window.run;
    if(!r)return null;
    if(r.__limitedSpecialRunId!==r.runId){r.__limitedSpecialRunId=r.runId;r.__limitedSpecialSeen={};}
    return r.__limitedSpecialSeen||(r.__limitedSpecialSeen={});
  }
  function areaLimitedSpecials(dungeon,area){
    const a=getArea(dungeon,area); const encounters=Array.isArray(a?.encounters)?a.encounters:[];
    const out=[]; const keys=new Set();
    for(const e of encounters){
      const monsters=encounterToMonsters(dungeon,e);
      const special=monsters.find(isLimitedSpecial);
      if(!special)continue;
      const key=specialKey(special);
      if(!keys.has(key)){keys.add(key);out.push({monsters,key});}
    }
    return out;
  }
  function markSeen(special){const state=seenState();if(state)state[special.key]=true;}
  function wasSeen(special){const state=seenState();return Boolean(state&&state[special.key]);}
  function resolveSpecial(dungeon,area){
    const a=getArea(dungeon,area); const encounters=Array.isArray(a?.encounters)?a.encounters:[];
    for(const e of encounters){const monsters=encounterToMonsters(dungeon,e);if(monsters.length&&isBoss(monsters))return monsters;}
    return [];
  }
  function resolve(dungeon,area,rng,options={}){
    const a=getArea(dungeon,area); const encounters=Array.isArray(a?.encounters)?a.encounters:[];
    const excludeMidboss=Boolean(options?.excludeMidboss);
    const limited=areaLimitedSpecials(dungeon,area);
    const unseen=limited.filter(x=>!wasSeen(x));
    const run=window.run;
    const maxEncounters=Math.max(1,Number(run?.getAreaMaxEncounters?.(area))||5);
    const encounterIndex=Math.max(0,Number(run?.encounterIndex)||0);
    const remaining=Math.max(1,maxEncounters-encounterIndex);
    let forcedSpecial=null;
    if(unseen.length){
      const mustPlace=unseen.length>=remaining;
      if(mustPlace||rng.int(0,remaining-1)<unseen.length)forcedSpecial=unseen[rng.int(0,unseen.length-1)];
    }
    if(forcedSpecial){markSeen(forcedSpecial);return forcedSpecial.monsters;}
    const candidates=encounters.filter(e=>{
      const monsters=encounterToMonsters(dungeon,e);
      if(!monsters.length)return false;
      if(excludeMidboss&&isMidboss(monsters))return false;
      const limitedMonster=monsters.find(isLimitedSpecial);
      return !limitedMonster||!wasSeen({key:specialKey(limitedMonster)});
    });
    const e=candidates.length?candidates[rng.int(0,candidates.length-1)]:null;
    const explicit=encounterToMonsters(dungeon,e);
    if(explicit.length){const limitedMonster=explicit.find(isLimitedSpecial);if(limitedMonster)markSeen({key:specialKey(limitedMonster)});return explicit;}
    const target=Math.max(1,Math.min(50,Number(dungeon?.levelMap?.[Number(area)-1])||Number(area)||1));
    const pool=window.MonsterXmlLoader?.getForDungeon?.(dungeon?.monsterDungeonName||dungeon?.name,target)||[];
    const normalPool=pool.filter(m=>!isSpecialMonster(m));
    const usable=normalPool.length?normalPool:pool;
    return usable.length?[rng.pick(usable)]:[];
  }
  function dungeonRunActive(){return !!window.run?.active;}
  function notifyLocked(){const s=document.getElementById('status');if(s)s.textContent='던전 진행 중에는 상점 이용 및 장비 변경을 할 수 없습니다.';}
  document.addEventListener('click',e=>{
    if(!dungeonRunActive())return;
    const target=e.target?.closest?.('[data-buy-item],[data-sell-instance],.character-formation [data-move]');
    if(!target)return;
    e.preventDefault();e.stopImmediatePropagation();notifyLocked();
  },true);
  document.addEventListener('focusin',e=>{
    if(!dungeonRunActive())return;
    if(e.target?.matches?.('.character-equipment select'))e.target.dataset.dungeonPreviousValue=e.target.value;
  },true);
  document.addEventListener('change',e=>{
    if(!dungeonRunActive())return;
    if(!e.target?.matches?.('.character-equipment select'))return;
    e.preventDefault();e.stopImmediatePropagation();
    if(e.target.dataset.dungeonPreviousValue!==undefined)e.target.value=e.target.dataset.dungeonPreviousValue;
    notifyLocked();
  },true);
  window.EncounterResolver={resolve,resolveSpecial,isBoss,isMidboss,isSpecialMonster,areaHasSpecial,areaHasBoss};
})();
