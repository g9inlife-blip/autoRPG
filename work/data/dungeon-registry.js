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
    data[id]={
      id,name:g.name,monsterDungeonName:g.name,regionName:'',
      floors:areas.length||g.levels.length,
      areas,
      levelStart:g.minLevel,levelEnd:g.maxLevel,
      levelMap:g.levels,encounters:[],xml:true,source:'monster_lvl1_50.xml',
      placementSource:'dungeon_mon_place.txt',
      boss:boss?{id:`boss-${boss.id}`,name:boss.name,hp:boss.maxHp,attack:boss.attack,defense:boss.defense,speed:10,exp:boss.exp,monsterId:boss.id,monsterGrade:boss.grade,monsterForm:boss.form}:null
    };
  });

  window.DUNGEON_DATA=data;window.DUNGEONS=data;window.XML_DUNGEON_DATA=data;window.MONSTER_DATABASE=loader.state.items;
  const select=document.getElementById('dungeonSelect');
  if(select){const old=select.value;select.innerHTML='';Object.values(data).forEach(d=>{const o=document.createElement('option');o.value=d.id;o.textContent=`${d.name} (${d.levelStart}~${d.levelEnd}Lv / ${d.floors}구역)`;select.appendChild(o);});if(data[old])select.value=old;}

  const proto=window.DungeonRun?.prototype;
  if(proto&&!proto._xmlDungeonPatch){
    const originalGet=proto.getXmlEnemies;
    proto.getXmlEnemies=function(){
      const name=this.dungeon?.monsterDungeonName||this.dungeon?.name;
      const area=this.dungeon?.areas?.find(x=>x.area===Number(this.floor));
      const encounter=area?.encounters?.length?area.encounters[this.rng.int(0,area.encounters.length-1)]:null;
      if(encounter?.enemies?.length){
        const out=encounter.enemies.map((m,index)=>{
          const xml=loader.state.items.find(x=>x.dungeonName===name&&x.name===m.name);
          return xml?{...xml,placement:m.placement,encounterIndex:index}:null;
        }).filter(Boolean);
        if(out.length)return out;
      }
      return originalGet.call(this);
    };
    const originalFight=proto.fight;
    proto.fight=function(enemies){
      const result=originalFight.call(this,enemies);
      const last=[...(this.events||[])].reverse().find(e=>e?.type==='BATTLE_END');
      if(last)last.data.enemies=(enemies||[]).map(e=>({id:e.monsterId||e.id,name:e.name,grade:e.monsterGrade||e.grade,level:e.level||e.monsterLevel||null,form:e.monsterForm||null}));
      return result;
    };
    proto._xmlDungeonPatch=true;
  }
  window.syncXmlRegions?.();
  return true;
};
