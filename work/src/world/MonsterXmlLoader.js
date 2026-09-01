(() => {
  const CONFIG={minLevel:1,maxLevel:50,path:'monster_lvl1_50.xml'};
  const state={loaded:false,items:[],byDungeon:new Map(),byLevel:new Map(),errors:[]};
  const text=e=>e?.textContent?.trim()||'';
  function parse(xml){
    const doc=new DOMParser().parseFromString(xml,'application/xml');
    if(doc.querySelector('parsererror'))throw new Error('monster_lvl1_50.xml 파싱 실패');
    const out=[];
    doc.querySelectorAll('MonsterDatabase > Monster').forEach(m=>{
      const level=Number(text(m.querySelector('Level'))) || 1;if(level<1||level>50)return;
      const dungeonName=text(m.querySelector('Dungeon'));const hp=Number(text(m.querySelector('HP')))||0;
      out.push({id:m.getAttribute('id'),name:text(m.querySelector('Name')),dungeonName,level,grade:text(m.querySelector('Grade')).toLowerCase(),form:text(m.querySelector('Form')).toLowerCase(),hp,maxHp:hp,attack:Number(text(m.querySelector('Attack')))||0,defense:Number(text(m.querySelector('Defense')))||0,exp:Number(text(m.querySelector('Exp')))||0,source:'monster_lvl1_50.xml'});
    });return out;
  }
  async function load(){try{const r=await fetch(CONFIG.path,{cache:'no-store'});if(!r.ok)throw new Error(`${r.status} ${r.statusText}`);state.items=parse(await r.text());state.byDungeon=new Map();state.byLevel=new Map();state.items.forEach(m=>{if(!state.byDungeon.has(m.dungeonName))state.byDungeon.set(m.dungeonName,[]);state.byDungeon.get(m.dungeonName).push(m);if(!state.byLevel.has(m.level))state.byLevel.set(m.level,[]);state.byLevel.get(m.level).push(m);});state.loaded=true;state.errors=[];window.MONSTER_DATABASE=state.items;window.MONSTERS_BY_DUNGEON=state.byDungeon;window.MONSTERS_BY_LEVEL=state.byLevel;return state;}catch(e){state.loaded=false;state.errors=[e.message];return state;}}
  function getForDungeon(name,level=null){let out=state.byDungeon.get(String(name))||[];if(level!=null)out=out.filter(m=>m.level===Number(level));return out;}
  const getByLevel=level=>state.byLevel.get(Number(level))||[];
  const create=id=>{const m=state.items.find(x=>x.id===String(id));return m?{...m}:null;};
  window.MonsterXmlLoader={CONFIG,state,load,getForDungeon,getByLevel,create};
})();
