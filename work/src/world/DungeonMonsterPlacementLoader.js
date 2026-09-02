(() => {
  const CONFIG={path:'dungeon_mon_place.txt'};
  const state={loaded:false,byDungeon:new Map(),errors:[]};
  const cleanName=s=>String(s||'').trim().replace(/\s*\((중간보스|보스)\)\s*$/,'').trim();
  function parse(text){
    const result=new Map(); let current=null;
    String(text||'').split(/\r?\n/).forEach(raw=>{
      const line=raw.trim(); if(!line)return;
      const header=line.match(/^(.+),\s*(\d+)구역$/);
      if(header){ current={name:header[1].trim(),areaCount:Number(header[2]),areas:[]}; result.set(current.name,current); return; }
      const area=line.match(/^(\d+)구역\s*:\s*(.+)$/);
      if(!area||!current)return;
      const encounters=[]; const re=/\[([^\]]+)\]/g; let m;
      while((m=re.exec(area[2]))) {
        const monsters=m[1].split(',').map(x=>x.trim()).filter(Boolean).map(rawName=>({name:cleanName(rawName),placement:rawName.includes('(보스)')?'boss':rawName.includes('(중간보스)')?'midBoss':'normal'}));
        if(monsters.length)encounters.push({monsters});
      }
      current.areas.push({area:Number(area[1]),encounters});
    });
    return result;
  }
  async function load(){
    try{const r=await fetch(CONFIG.path,{cache:'no-store'});if(!r.ok)throw new Error(`${r.status} ${r.statusText}`);state.byDungeon=parse(await r.text());state.loaded=true;state.errors=[];return state;}
    catch(e){state.loaded=false;state.errors=[e.message];return state;}
  }
  function getForDungeon(name){return state.byDungeon.get(String(name))||null;}
  function validate(monsterLoader){
    state.errors=[];
    if(!monsterLoader?.state?.loaded)return state.errors;
    for(const [dungeon,entry] of state.byDungeon){
      for(const area of entry.areas)for(const encounter of area.encounters)for(const mon of encounter.monsters){
        if(!monsterLoader.state.items.some(x=>x.dungeonName===dungeon&&x.name===mon.name))state.errors.push(`${dungeon} ${area.area}구역: XML 몬스터 없음 - ${mon.name}`);
      }
    }
    return state.errors;
  }
  window.DungeonMonsterPlacementLoader={CONFIG,state,load,getForDungeon,validate};
})();
