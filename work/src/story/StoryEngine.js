class StoryEngine {
  constructor({state={},nodes=[]}){this.state=state;this.nodes=nodes;this.state.flags ||= {};}
  get(id){return this.nodes.find(n=>n.id===id);}
  choose(nodeId,choiceId){const node=this.get(nodeId);const choice=node?.choices?.find(c=>c.id===choiceId);if(!choice)return null;(choice.setFlags||[]).forEach(f=>this.state.flags[f]=true);return this.next(choice.next);}
  next(next){if(Array.isArray(next)){for(const n of next){if(this.condition(n.condition))return n.next;}}return typeof next==='string'?next:null;}
  condition(c){if(!c)return true;return Object.entries(c).every(([k,v])=>this.state.flags[k]===v);}
}
window.StoryEngine=StoryEngine;
