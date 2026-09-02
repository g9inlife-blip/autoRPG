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

  function buildRegions() {
    const quests = window.QuestXmlLoader?.state?.items || [];
    const dungeons = Object.values(window.DUNGEON_DATA || {});
    const questByName = new Map(quests.map(q => [q.name, q]));
    const groups = new Map();

    dungeons.forEach(dungeon => {
      const quest = questByName.get(dungeon.name);
      const regionName = String(quest?.region || '').trim() || '미분류 지역';
      if (!groups.has(regionName)) groups.set(regionName, []);
      groups.get(regionName).push(dungeon);
    });

    const names = [...groups.keys()].sort((a,b) => a.localeCompare(b,'ko'));
    const regions = {};
    const dungeonToRegion = {};

    names.forEach(name => {
      const id = slug(name);
      const config = REGION_CONFIG[id] || REGION_CONFIG[name] || {};
      const members = groups.get(name).slice().sort((a,b) => a.levelStart-b.levelStart || a.name.localeCompare(b.name,'ko'));
      regions[id] = {
        id,
        name,
        description: config.description || '',
        inn: { ...(config.inn || {}) },
        shop: { ...(config.shop || {}) },
        dungeons: members.map(d => d.id)
      };
      members.forEach(d => {
        dungeonToRegion[d.id] = id;
        d.regionId = id;
        d.regionName = name;
      });
    });

    Object.values(window.DUNGEON_DATA || {}).forEach(d => {
      if (d.regionId) return;
      const id = `region-unassigned`;
      if (!regions[id]) regions[id] = { id, name:'미분류 지역', description:'지역 정보가 아직 연결되지 않은 던전', inn:{}, shop:{}, dungeons:[] };
      d.regionId = id;
      d.regionName = regions[id].name;
      regions[id].dungeons.push(d.id);
      dungeonToRegion[d.id] = id;
    });

    state.loaded = true;
    state.regions = regions;
    state.dungeonToRegion = dungeonToRegion;
    state.errors = [];
    window.REGION_DATA = regions;
    window.REGIONS = regions;
    window.REGION_STATE = state;
    window.regionUiSync?.();
    window.dispatchEvent(new CustomEvent('regions:ready'));
    return state;
  }

  function get(id) { return state.regions?.[id] || null; }
  function getByDungeon(dungeonId) { return get(state.dungeonToRegion?.[dungeonId]); }
  function getDungeons(regionId) {
    const region = get(regionId);
    return (region?.dungeons || []).map(id => window.DUNGEON_DATA?.[id]).filter(Boolean);
  }

  window.REGION_CONFIG = REGION_CONFIG;
  window.syncXmlRegions = buildRegions;
  window.RegionRegistry = { state, build:buildRegions, get, getByDungeon, getDungeons };
})();
