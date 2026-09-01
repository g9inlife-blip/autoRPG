(() => {
  // EquipmentDatabase is the normalized read-only view over supplied XML base items.
  // XML remains authoritative; this module never invents or overwrites XML stats.
  const state = { byId:new Map(), byLevel:new Map(), byType:new Map(), items:[] };

  function rebuild(items) {
    state.items = Array.isArray(items) ? items.slice() : [];
    state.byId = new Map(); state.byLevel = new Map(); state.byType = new Map();
    for (const item of state.items) {
      const id = String(item.sourceId ?? item.id);
      state.byId.set(id, item); state.byId.set(String(item.id), item);
      const level = Number(item.reqLevel) || 1;
      if (!state.byLevel.has(level)) state.byLevel.set(level, []);
      state.byLevel.get(level).push(item);
      const type = String(item.slot || item.type || '').toLowerCase();
      if (type) { if (!state.byType.has(type)) state.byType.set(type, []); state.byType.get(type).push(item); }
    }
    window.EQUIPMENT_DATABASE = state.items;
    return state;
  }
  const getAll = () => state.items.slice();
  const getById = id => state.byId.get(String(id)) || null;
  const getByLevel = level => (state.byLevel.get(Math.max(1, Math.min(50, Number(level)||1))) || []).slice();
  const getByType = type => (state.byType.get(String(type).toLowerCase()) || []).slice();
  function getByClass(classId) {
    const cls = String(classId || '').toLowerCase(); if (!cls) return [];
    return state.items.filter(item => {
      const classes = item.requirements?.classes;
      return !classes?.length || classes.map(String).map(x=>x.toLowerCase()).includes(cls);
    });
  }
  function getEquipable(character) {
    if (!character) return [];
    const level = Number(character.level ?? character.lv ?? 1) || 1;
    const stats = character.stats || {};
    const classId = character.classId || character.class || character.job;
    return state.items.filter(item => {
      if (Number(item.reqLevel) > level) return false;
      const classes = item.requirements?.classes;
      if (classes?.length && classId && !classes.map(String).map(x=>x.toLowerCase()).includes(String(classId).toLowerCase())) return false;
      return Object.entries(item.requirements?.stats || {}).every(([key,value]) => Number(stats[key]||0) >= Number(value));
    });
  }
  const sync = () => rebuild(window.XML_EQUIPMENT_POOL || []);
  window.EquipmentDatabase = { state, rebuild, sync, getAll, getById, getByLevel, getByType, getByClass, getEquipable };
})();
