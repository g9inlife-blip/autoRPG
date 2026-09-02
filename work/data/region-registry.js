(() => {
  const REGION_CONFIG = window.REGION_CONFIG || {};
  const state = { loaded:false, regions:{}, dungeonToRegion:{}, errors:[] };

  const slug = text => {
    const raw = String(text || '').trim();
    const ascii = raw.toLowerCase().replace(/[^a-z0-9]+/g,'-').replace(/^-|-$/g,'');
    if (ascii) return `region-${ascii}`;
    let hash=0;for(const ch of raw)hash=((hash<<5)-hash)+ch.charCodeAt(0)|0;
    return `region-${Math.abs(hash)}`;
  };

  function findDungeon(name,dungeons){
    return dungeons.find(d=>d.name===name || d.monsterDungeonName===name) || null;
  }

  function buildRegions(){
    const dungeons=Object.values(window.DUNGEON_DATA || {});
    const assigned=new Set();
    const regions={};
    const dungeonToRegion={};

    Object.values(REGION_CONFIG).forEach(config=>{
      const members=[];
      (config.dungeons || []).forEach(name=>{
        const dungeon=findDungeon(name,dungeons);
        if(!dungeon || assigned.has(dungeon.id))return;
        assigned.add(dungeon.id);
        members.push(dungeon);
      });
      if(!members.length)return;
      members.sort((a,b)=>a.levelStart-b.levelStart || a.name.localeCompare(b.name,'ko'));
      const id=config.id || slug(config.name);
      regions[id]={
        id,
        name:config.name,
        description:config.description || '',
        inn:{...(config.inn || {})},
        shop:{...(config.shop || {})},
        dungeons:members.map(d=>d.id)
      };
      members.forEach(d=>{
        dungeonToRegion[d.id]=id;
        d.regionId=id;
        d.regionName=config.name;
      });
    });

    const unassigned=dungeons.filter(d=>!assigned.has(d.id));
    if(unassigned.length){
      const id='region-unassigned';
      regions[id]={id,name:'미분류 지역',description:'지역 정보가 아직 연결되지 않은 던전',inn:{},shop:{},dungeons:[]};
      unassigned.sort((a,b)=>a.levelStart-b.levelStart || a.name.localeCompare(b.name,'ko')).forEach(d=>{
        d.regionId=id;
        d.regionName=regions[id].name;
        regions[id].dungeons.push(d.id);
        dungeonToRegion[d.id]=id;
      });
    }

    state.loaded=true;
    state.regions=regions;
    state.dungeonToRegion=dungeonToRegion;
    state.errors=[];
    window.REGION_DATA=regions;
    window.REGIONS=regions;
    window.REGION_STATE=state;
    window.regionUiSync?.();
    window.dispatchEvent(new CustomEvent('regions:ready'));
    return state;
  }

  function get(id){return state.regions?.[id] || null;}
  function getByDungeon(dungeonId){return get(state.dungeonToRegion?.[dungeonId]);}
  function getDungeons(regionId){
    const region=get(regionId);
    return (region?.dungeons || []).map(id=>window.DUNGEON_DATA?.[id]).filter(Boolean);
  }

  window.REGION_CONFIG=REGION_CONFIG;
  window.syncXmlRegions=buildRegions;
  window.RegionRegistry={state,build:buildRegions,get,getByDungeon,getDungeons};
})();
