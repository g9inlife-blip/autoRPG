window.DUNGEON_DATA = window.DUNGEON_DATA || {};
window.DUNGEONS = window.DUNGEON_DATA;
window.syncXmlDungeons = function(){
  const loader=window.MonsterXmlLoader;
  const placement=window.DungeonMonsterPlacementLoader;
  if(!loader?.state?.loaded)return false;
  placement?.validate?.(loader);
  const groups=[...loader.state.byDungeon.entries()]
    .map(([name,items])=>{
      const levels=[...new Set(items.map(m=>Number(m.level)).filter(Number.isFinite))].sort((a,b)=>a-b);
      const bosses=items.filter(m=>String(m.grade||'').toLowerCase()==='boss'||String(m.form||'').toLowerCase()==='boss');
      const place=placement?.getForDungeon?.(name);
      return {name,items,levels,bosses,minLevel:levels[0],maxLevel:levels[levels.length-1],place};
    })
    .filter(g=>g.name&&g.levels.length)
    .sort((a,b)=>a.minLevel-b.minLevel||a.name.localeCompare(b.name,'ko'));
  const data={};
  groups.forEach((g,i)=>{
    const boss=g.bosses.length?g.bosses[g.bosses.length-1]:null;
    const id=`xml-dungeon-${String(i+1).padStart(2,'0')}`;
    const areas=(g.place?.areas||[]).map(area=>({
      area:area.area,
      encounters:area.encounters.map(encounter=>({
        type:'battle',
        enemies:encounter.monsters.map(mon=>{
          const xml=g.items.find(x=>x.name===mon.name);
          return xml?{...xml,placement:mon.placement}:{name:mon.name,placement:mon.placement};
        })
      }))
    }));
    const declaredAreaCount=Number(g.place?.areaCount)||0;
    const actualAreaCount=areas.reduce((max,area)=>Math.max(max,Number(area.area)||0),0);
    const areaCount=Math.max(declaredAreaCount,actualAreaCount);
    data[id]={
      id,name:g.name,monsterDungeonName:g.name,regionName:'',areaCount,
      floors:areaCount||g.levels.length,areas,levelStart:g.minLevel,levelEnd:g.maxLevel,
      levelMap:g.levels,encounters:[],xml:true,source:'monster_lvl1_50.xml',
      placementSource:'dungeon_mon_place.txt',
      boss:boss?{id:`boss-${boss.id}`,name:boss.name,hp:boss.maxHp,attack:boss.attack,defense:boss.defense,speed:10,exp:boss.exp,monsterId:boss.id,monsterGrade:boss.grade,monsterForm:boss.form}:null
    };
  });
  window.DUNGEON_DATA=data;window.DUNGEONS=data;window.XML_DUNGEON_DATA=data;window.MONSTER_DATABASE=loader.state.items;
  const select=document.getElementById('dungeonSelect');
  if(select){const old=select.value;select.innerHTML='';Object.values(data).forEach(d=>{const o=document.createElement('option');o.value=d.id;o.textContent=`${d.name} (${d.levelStart}~${d.levelEnd}Lv / ${d.areaCount}구역)`;select.appendChild(o);});if(data[old])select.value=old;}
  window.syncXmlRegions?.();
  return true;
};
