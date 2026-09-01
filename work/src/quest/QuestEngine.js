class QuestEngine {
  constructor({state,quests}) { this.state=state; this.quests=quests; }
  getActive(type=null) { return this.quests.filter(q=>!type||q.type===type).filter(q=>this.isAvailable(q)); }
  isAvailable(q) { const p=this.state.quests?.[q.id]; return !p?.completed || q.type==='repeatable'; }
  progress(q, objectiveIndex=0) { const p=this.state.quests?.[q.id]; return p?.progress?.[objectiveIndex]||0; }
  record(event) {
    if(!this.state.quests) this.state.quests={};
    for(const q of this.quests) {
      if(!this.isAvailable(q)) continue;
      q.objectives.forEach((o,i)=>{ if(this.matches(o,event)){ const p=this.state.quests[q.id] ||= {progress:[],completed:false,completedAt:null}; p.progress[i]=Math.min((p.progress[i]||0)+event.count,q.objectives[i].count); }});
      this.tryComplete(q);
    }
  }
  matches(o,e) { if(o.type!==e.type) return false; if(o.target && o.target!==e.target) return false; if(o.dungeon && o.dungeon!==e.dungeon) return false; if(o.floor && e.floor<o.floor) return false; return true; }
  tryComplete(q) { const p=this.state.quests[q.id]; if(!p||p.completed||q.objectives.some((o,i)=>(p.progress?.[i]||0)<o.count)) return false; p.completed=true;p.completedAt=Date.now(); return true; }
  resetRepeatable(id) { const q=this.quests.find(x=>x.id===id); if(q?.type==='repeatable') delete this.state.quests[id]; }
}
window.QuestEngine=QuestEngine;
