class QuestEngine {
  constructor({state,quests,onEvent=()=>{}}) { this.state=state; this.quests=quests; this.onEvent=onEvent; }
  getActive(type=null) { return this.quests.filter(q=>!type||q.type===type).filter(q=>this.isAvailable(q)); }
  isAvailable(q) { const p=this.state.quests?.[q.id]; return !p?.completed || q.type==='repeatable'; }
  progress(q, objectiveIndex=0) { return this.state.quests?.[q.id]?.progress?.[objectiveIndex]||0; }
  record(event) { if(!this.state.quests)this.state.quests={}; for(const q of this.quests){if(!this.isAvailable(q))continue;q.objectives.forEach((o,i)=>{if(this.matches(o,event)){const p=this.state.quests[q.id] ||= {progress:[],completed:false,completedAt:null};p.progress[i]=Math.min((p.progress[i]||0)+(event.count||1),o.count);}});this.tryComplete(q);} }
  matches(o,e){if(o.type!==e.type)return false;if(o.target&&o.target!==e.target)return false;if(o.dungeon&&o.dungeon!==e.dungeon)return false;if(o.floor&&e.floor<o.floor)return false;return true;}
  tryComplete(q){const p=this.state.quests[q.id];if(!p||p.completed||q.objectives.some((o,i)=>(p.progress?.[i]||0)<o.count))return false;p.completed=true;p.completedAt=Date.now();if(q.rewards){this.state.gold=(this.state.gold||0)+(q.rewards.gold||0);this.state.exp=(this.state.exp||0)+(q.rewards.exp||0);}this.onEvent({type:'QUEST_COMPLETE',questId:q.id,quest:q});return true;}
  resetRepeatable(id){const q=this.quests.find(x=>x.id===id);if(q?.type==='repeatable')delete this.state.quests[id];}
}
window.QuestEngine=QuestEngine;
