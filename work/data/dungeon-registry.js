window.DUNGEON_DATA = window.DUNGEON_DATA || {};
window.DUNGEONS = window.DUNGEON_DATA;
window.syncXmlDungeons = function(){
  const loader=window.MonsterXmlLoader;if(!loader?.state?.loaded)return false;
  const groups=[...loader.state.byDungeon.entries()].map(([name,items])=>{const levels=[...new Set(items.map(m=>m.level))].sort((a,b)=>a-b);const bosses=items.filter(m=>m.grade==='boss'||m.form==='boss');return{name,items,levels,bosses,minLevel:levels[0],maxLevel:levels[levels.length-1]};}).filter(g=>g.levels.length).sort((a,b)=>a.minLevel-b.minLevel||a.name.localeCompare(b.name));
  const quests=window.QuestXmlLoader?.state?.items||[];const questByName=new Map(quests.map(q=>[q.name,q]));
  const data={};groups.forEach((g,i)=>{const boss=g.bosses.length?g.bosses[g.bosses.length-1]:null;const quest=questByName.get(g.name);data[`xml-dungeon-${String(i+1).padStart(2,'0')}`]={id:`xml-dungeon-${String(i+1).padStart(2,'0')}`,name:g.name,monsterDungeonName:g.name,regionName:quest?.region||'',floors:g.levels.length,levelStart:g.minLevel,levelEnd:g.maxLevel,levelMap:g.levels,encounters:[],xml:true,boss:boss?{id:`boss-${boss.id}`,name:boss.name,hp:boss.maxHp,attack:boss.attack,defense:boss.defense,speed:10,exp:boss.exp,monsterId:boss.id,monsterGrade:boss.grade,monsterForm:boss.form}:null};});
  window.DUNGEON_DATA=data;window.DUNGEONS=data;window.XML_DUNGEON_DATA=data;window.MONSTER_DATABASE=loader.state.items;
  const select=document.getElementById('dungeonSelect');if(select){const old=select.value;select.innerHTML='';Object.values(data).forEach(d=>{const o=document.createElement('option');o.value=d.id;o.textContent=`${d.name} (${d.levelStart}~${d.levelEnd}Lv / ${d.floors}층)`;select.appendChild(o);});if(data[old])select.value=old;}
  const proto=window.DungeonRun?.prototype;if(proto&&!proto._xmlDungeonPatch){
    const originalGet=proto.getXmlEnemies;proto.getXmlEnemies=function(){const name=this.dungeon?.monsterDungeonName||this.dungeon?.name;const target=Number(this.dungeon?.levelMap?.[(Number(this.floor)||1)-1]??this.dungeon?.levelStart??this.floor)||1;let pool=loader.getForDungeon(name,target);if(!pool.length)pool=loader.getForDungeon(name);if(!pool.length)return originalGet.call(this);return pool;};
    const originalFight=proto.fight;proto.fight=function(enemies){const result=originalFight.call(this,enemies);const last=[...(this.events||[])].reverse().find(e=>e?.type==='BATTLE_END');if(last)last.data.enemies=(enemies||[]).map(e=>({id:e.monsterId||e.id,name:e.name,grade:e.monsterGrade||e.grade,level:e.level||e.monsterLevel||null}));return result;};
    proto._xmlDungeonPatch=true;
  }
  window.syncXmlRegions?.();
  return true;
};
