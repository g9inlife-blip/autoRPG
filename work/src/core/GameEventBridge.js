class GameEventBridge {
  constructor({bus,questEngine,dialogueResolver,onDialogue=()=>{},onQuestUpdate=()=>{}}){this.bus=bus;this.questEngine=questEngine;this.dialogueResolver=dialogueResolver;this.onDialogue=onDialogue;this.onQuestUpdate=onQuestUpdate;this.unsub=[];this.bind();}
  bind(){['DAMAGE','MISS','DEFEAT','BATTLE_END','FLOOR_REACHED','LOOT'].forEach(type=>this.unsub.push(this.bus.on(type,e=>this.handle(type,e))));}
  handle(type,e){
    const questEvent=this.toQuestEvent(type,e); if(questEvent){this.questEngine.record(questEvent);this.onQuestUpdate(this.questEngine.getActive());}
    const action=this.toDialogueAction(type,e); if(action&&e.actor){const line=this.dialogueResolver.resolve({action,actor:e.actor,party:e.party||[],event:e.event,dungeon:e.dungeon,flags:e.flags||{}});if(line)this.onDialogue(line,e);}
  }
  toQuestEvent(type,e){if(type==='DEFEAT')return {type:'kill',target:e.defeatedTarget||e.target,count:1};if(type==='FLOOR_REACHED')return {type:'reach_floor',dungeon:e.dungeon,floor:e.floor,count:1};if(type==='LOOT')return {type:'collect',target:e.itemId,count:e.quantity||1};return null;}
  toDialogueAction(type,e){return ({DAMAGE:e.critical?'critical':'attack',MISS:'miss',DEFEAT:e.side==='player'?'ally_defeat':'enemy_defeat',BATTLE_END:e.result==='WIN'?'victory':e.result==='LOSE'?'defeat':null}[type])||null;}
  destroy(){this.unsub.forEach(fn=>fn());this.unsub=[];}
}
window.GameEventBridge=GameEventBridge;
