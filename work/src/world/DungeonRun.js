class DungeonRun {
  constructor({ dungeon, party, rng, battleFactory }) {
    this.runId=`run-${Date.now()}`; this.dungeon=dungeon; this.party=party; this.rng=rng; this.battleFactory=battleFactory;
    this.floor=0; this.encounterIndex=0; this.active=false; this.complete=false; this.gold=0; this.exp=0; this.loot=[]; this.events=[]; this.startedAt=null; this.currentBattle=null;
    this.stats={}; party.forEach(c=>this.stats[c.id]={damage:0,damageTaken:0,hits:0,attacks:0,criticals:0,kills:0,activeSeconds:0});
  }
  start(){this.active=true;this.startedAt=Date.now();this.events.push({type:'RUN_START',floor:1});return this;}
  addBattleStats(battle){
    battle.party.forEach(c=>{const s=this.stats[c.id];if(!s)return;s.damage=c.stats.damageDealt;s.damageTaken=c.stats.damageTaken;s.hits=c.stats.hits;s.attacks=c.stats.attacks;s.criticals=c.stats.criticals;s.kills=c.stats.kills;s.activeSeconds+=Math.max(1,battle.result.round);});
  }
  fight(enemies){
    this.currentBattle=this.battleFactory(this.rng,this.party,enemies);const battle=this.currentBattle;this.addBattleStats(battle);
    this.events.push({type:'BATTLE',floor:this.floor,result:battle.result,events:battle.events,enemies:enemies.map(e=>e.name)});
    if(battle.result.result!=='WIN'){this.active=false;this.complete=true;return battle;}
    this.exp+=100*this.floor;return battle;
  }
  addLoot(itemId,itemName,quantity,gold=0){this.loot.push({itemId,itemName,quantity,floor:this.floor});this.gold+=gold;this.events.push({type:'LOOT',floor:this.floor,itemId,itemName,quantity,gold});}
  nextFloor(){if(!this.active)return{done:true};this.floor++;this.encounterIndex=0;if(this.floor>this.dungeon.floors){this.complete=true;this.active=false;this.events.push({type:'RUN_COMPLETE',floor:this.floor-1});return{done:true};}this.events.push({type:'FLOOR',floor:this.floor});return{done:false,floor:this.floor};}
  step(){
    if(!this.active)return{done:true,battle:null};
    if(this.floor===0)this.nextFloor();
    const encounter=this.floor===this.dungeon.floors?null:this.dungeon.encounters[this.rng.int(0,this.dungeon.encounters.length-1)];
    if(encounter?.type==='treasure')this.addLoot(encounter.itemId,encounter.itemName,encounter.quantity,encounter.gold);
    else if(encounter)this.fight(encounter.enemies.map(e=>new Character({...e,team:'enemy'})));
    if(this.active&&this.floor===this.dungeon.floors){const battle=this.fight([new Character({...this.dungeon.boss,team:'enemy'})]);if(battle.result.result==='WIN'){this.addLoot('boss-cache','수호자의 보물',1,500);this.complete=true;this.active=false;this.events.push({type:'RUN_COMPLETE',floor:this.floor});}}
    else if(this.active)this.nextFloor();
    return{done:this.complete,battle:this.currentBattle};
  }
  summary(){return{runId:this.runId,dungeonId:this.dungeon.id,floor:this.floor,complete:this.complete,gold:this.gold,exp:this.exp,loot:this.loot,stats:this.stats,events:this.events};}
}
window.DungeonRun=DungeonRun;
