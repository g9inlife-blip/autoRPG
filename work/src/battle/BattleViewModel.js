class BattleViewModel {
  constructor(battle){this.battle=battle||{};this.events=Array.isArray(this.battle.events)?this.battle.events:[];}
  grouped(){const groups=[];let current=null;for(const e of this.events){const type=e.type==='DAMAGE'||e.type==='MISS'?'ACTION':e.type;if(!current||current.type!==type){current={type,events:[]};groups.push(current);}current.events.push(e);}return groups;}
  hpSnapshots(){const map={};for(const e of this.events){if(e.type==='DAMAGE'){const name=e.data.defender;map[name]=Math.max(0,Number(e.data.defenderHp)||0);}}return map;}
  summary(){const defeated=this.events.filter(e=>e.type==='DEFEAT').map(e=>e.data.character);const damage=this.events.filter(e=>e.type==='DAMAGE').reduce((n,e)=>n+(Number(e.data.damage)||0),0);return{round:this.battle.result?.round||this.battle.round||0,result:this.battle.result?.result||this.battle.result||'DRAW',damage,defeated};}
}
window.BattleViewModel=BattleViewModel;
