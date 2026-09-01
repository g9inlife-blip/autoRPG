class EventBus {
  constructor(){this.listeners={};}
  on(type,fn){(this.listeners[type] ||= []).push(fn);return ()=>this.off(type,fn);}
  off(type,fn){this.listeners[type]=(this.listeners[type]||[]).filter(x=>x!==fn);}
  emit(type,payload={}){for(const fn of this.listeners[type]||[])fn(payload);}
}
window.EventBus=EventBus;
