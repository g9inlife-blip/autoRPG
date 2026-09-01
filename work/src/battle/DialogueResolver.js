class DialogueResolver {
  constructor({rng, dialogueData=window.DIALOGUES}){this.rng=rng;this.data=dialogueData||{default:{}};}
  pick(list){if(!list||!list.length)return '';return list[this.rng.int(0,list.length-1)];}
  resolve({action,actor,party=[],event=null,dungeon=null,flags={}}){
    const candidates=[];
    const personality=actor?.personality;
    if(flags.dialogue && this.data.events?.[flags.dialogue]) this.add(candidates,this.data.events[flags.dialogue],110);
    if(event?.dialogue)this.add(candidates,event.dialogue,100);
    if(personality)this.add(candidates,this.data.personalities?.[personality]?.[action],60);
    const bond=this.getBondLevel(actor,party);
    if(bond)this.add(candidates,this.data.bonds?.[bond]?.[action],70);
    if(dungeon?.dialogues?.[action])this.add(candidates,dungeon.dialogues[action],80);
    this.add(candidates,this.data.default?.[action],10);
    candidates.sort((a,b)=>b.priority-a.priority);
    return candidates[0]?this.pick(candidates[0].lines):'';
  }
  add(out,lines,priority){if(lines?.length)out.push({lines,priority});}
  getBondLevel(actor,party){if(!actor||!party?.length)return null;const bonds=party.filter(p=>p.id!==actor.id).map(p=>actor.bonds?.[p.id]??0);if(!bonds.length)return null;const value=Math.max(...bonds);return value>=80?'high':value<=20?'low':null;}
}
window.DialogueResolver=DialogueResolver;
