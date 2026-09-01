class PartyPanel {
  constructor({container,party,run}){this.container=container;this.party=party;this.run=run;window.activePartyPanel=this;}
  render(){if(!this.container)return;this.container.innerHTML=this.party.map(c=>{const s=this.run?.stats?.[c.id]||c.stats||{};const dps=this.run?.getDps?.(c.id)||0;const hp=Math.max(0,c.hp),max=Math.max(1,c.maxHp);return `<button class="party-card ${c.alive?'':'down'}" data-id="${c.id}"><div class="portrait">${c.appearance?.portrait||'?'}</div><div class="party-main"><b>${c.name}</b><span>${c.classId||'unknown'} · Lv.${c.level||1}</span><div class="hpbar"><i style="width:${hp/max*100}%"></i></div><small>HP ${hp}/${max} · DPS ${dps}</small><small>DMG ${s.damage||s.damageDealt||0} · 처치 ${s.kills||0} · 치명타 ${s.criticals||0}</small></div></button>`}).join('');this.container.querySelectorAll('.party-card').forEach(el=>el.onclick=()=>this.showDetail(el.dataset.id));}
  showDetail(id){const c=this.party.find(x=>x.id===id);if(!c)return;const s=this.run?.stats?.[id]||c.stats||{},dps=this.run?.getDps?.(id)||0,detail=document.getElementById('characterDetail');if(!detail)return;const slots=window.EQUIPMENT_SLOTS||{},items=Object.values(window.EQUIPMENT||{});const equipHtml=Object.entries(slots).map(([slot,label])=>{const current=c.equipment?.[slot];const opts=items.filter(x=>x.slot===slot).map(x=>`<option value="${x.id}" ${current?.id===x.id?'selected':''}>${x.name} (+공${x.attack||0} / +방${x.defense||0} / 속${x.speed||0})</option>`).join('');return `<label class="equip-row"><span>${label}</span><select onchange="window.equipCharacterItem('${c.id}','${slot}',this.value)"><option value="">없음</option>${opts}</select></label>`}).join('');detail.innerHTML=`<div class="modal-card"><button class="close" onclick="this.closest('.modal').hidden=true">×</button><h3>${c.name}</h3><p>${c.classId||'unknown'} · Lv.${c.level||1} · ${c.personality||'normal'}</p><section><b>현재 상태</b><dl><dt>HP</dt><dd>${c.hp}/${c.maxHp}</dd><dt>공격</dt><dd>${c.attack} <small>(기본 ${c.baseStats?.attack??c.attack})</small></dd><dt>방어</dt><dd>${c.defense} <small>(기본 ${c.baseStats?.defense??c.defense})</small></dd><dt>속도</dt><dd>${c.speed}</dd><dt>현재 DPS</dt><dd>${dps}</dd><dt>누적 Damage</dt><dd>${s.damage||s.damageDealt||0}</dd><dt>공격/명중/치명타</dt><dd>${s.attacks||0} / ${s.hits||0} / ${s.criticals||0}</dd><dt>피격 Damage</dt><dd>${s.damageTaken||0}</dd><dt>처치</dt><dd>${s.kills||0}</dd></dl></section><section><b>장비 장착</b>${equipHtml}</section></div>`;detail.hidden=false;}
}
window.equipCharacterItem=function(characterId,slot,itemId){const p=window.activePartyPanel,c=p?.party?.find(x=>x.id===characterId);if(!c)return;if(itemId&&window.EQUIPMENT?.[itemId])c.equip(window.EQUIPMENT[itemId]);else c.unequip(slot);p.render();p.showDetail(characterId);};
window.PartyPanel=PartyPanel;

window.addEventListener('DOMContentLoaded',()=>{
  const buttons=[...document.querySelectorAll('.tabs button[data-tab]')];
  const activate=id=>{
    buttons.forEach(b=>{const active=b.dataset.tab===id;b.classList.toggle('active',active);b.setAttribute('aria-selected',active?'true':'false');const panel=document.getElementById(b.dataset.tab);if(panel)panel.classList.toggle('active',active);});
    if(id==='innTab'&&typeof window.renderQuests==='function')window.renderQuests();
  };
  buttons.forEach(b=>b.addEventListener('click',e=>{e.preventDefault();activate(b.dataset.tab);}));
  const main=document.getElementById('mainQuestTab'),repeat=document.getElementById('repeatQuestTab');
  if(main)main.addEventListener('click',()=>{window.questUISetMode?.('main');window.renderQuests?.();});
  if(repeat)repeat.addEventListener('click',()=>{window.questUISetMode?.('repeat');window.renderQuests?.();});
  activate('dungeonTab');
});
