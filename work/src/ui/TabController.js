class TabController {
  constructor(){
    this.buttons=[...document.querySelectorAll('.tabs button[data-tab]')];
    this.panels=this.buttons.map(b=>document.getElementById(b.dataset.tab)).filter(Boolean);
    this.activeId='';
    this.bind();
  }
  activate(id){
    if(!this.panels.some(p=>p.id===id)) return;
    this.activeId=id;
    this.buttons.forEach(b=>{
      const active=b.dataset.tab===id;
      b.classList.toggle('active',active);
      b.setAttribute('aria-selected',active?'true':'false');
    });
    this.panels.forEach(p=>p.classList.toggle('active',p.id===id));
    if(id==='innTab'){
      if(typeof window.renderQuests==='function') window.renderQuests();
      if(window.partyPanel?.render) window.partyPanel.render();
    }
  }
  bind(){
    this.buttons.forEach(b=>b.addEventListener('click',e=>{e.preventDefault();this.activate(b.dataset.tab);}));
    const main=document.getElementById('mainQuestTab');
    const repeat=document.getElementById('repeatQuestTab');
    if(main) main.addEventListener('click',()=>{window.questUISetMode?.('main');window.renderQuests?.();});
    if(repeat) repeat.addEventListener('click',()=>{window.questUISetMode?.('repeat');window.renderQuests?.();});
    this.activate('dungeonTab');
  }
}
window.TabController=TabController;
window.addEventListener('DOMContentLoaded',()=>{window.tabController=new TabController();});
