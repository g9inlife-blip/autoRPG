(() => {
  const SHOP_PRICES = { iron_sword:60, oak_staff:70, hunter_dagger:65, leather_armor:80, adventurer_ring:120, iron_armor:180 };
  const SELL_RATE = 0.5;
  const starter = new Set(window.STARTER_EQUIPMENT || []);
  const owned = new Map();
  for (const id of starter) owned.set(id, 1);
  function getItems(){return Object.values(window.EQUIPMENT||{}).filter(x=>SHOP_PRICES[x.id]!=null);}
  function getInventory(){return [...owned.entries()].map(([id,quantity])=>({...window.EQUIPMENT?.[id],quantity})).filter(x=>x.id);}
  function currentRun(){const r=window.run||window.activePartyPanel?.run||null;if(r&&!r.__shopGoldInitialized){if(Number(r.gold)===0)r.gold=100;r.__shopGoldInitialized=true;}return r;}
  function getGold(){return Number(currentRun()?.gold??0);}
  function syncInventory(){window.playerInventory=getInventory();}
  function isEquipped(id){return !!currentRun()?.party?.some(c=>Object.values(c.equipment||{}).some(item=>item?.id===id));}
  function renderShop(){
    const root=document.getElementById('shop');if(!root)return;syncInventory();const gold=getGold();
    const shopHtml=getItems().map(item=>{const qty=owned.get(item.id)||0,price=SHOP_PRICES[item.id],stats=[`공 ${item.attack||0}`,`방 ${item.defense||0}`,`속 ${item.speed||0}`].join(' · ');return `<div class="shop-item"><div class="shop-item-main"><b>${item.name}${qty?` ×${qty}`:''}</b><small>${stats} · ${item.rarity||'common'} · ${price}G</small></div><button type="button" data-buy-item="${item.id}" ${gold<price?'disabled':''}>구매</button></div>`;}).join('');
    const invHtml=getInventory().map(item=>{const price=Math.floor((SHOP_PRICES[item.id]||0)*SELL_RATE),locked=isEquipped(item.id);return `<div class="shop-item"><div class="shop-item-main"><b>${item.name} ×${item.quantity}</b><small>판매가 ${price}G${locked?' · 장착 중':''}</small></div><button type="button" data-sell-item="${item.id}" ${locked?'disabled':''}>매각</button></div>`;}).join('')||'<p class="muted">보유 장비가 없습니다.</p>';
    root.innerHTML=`<div class="currency-line">보유 골드 <b>${gold.toLocaleString()} G</b></div><h3>장비 구매</h3>${shopHtml||'<p>판매할 장비가 없습니다.</p>'}<h3>보유 장비 · 매각</h3>${invHtml}`;
    root.querySelectorAll('[data-buy-item]').forEach(btn=>btn.addEventListener('click',()=>buyItem(btn.dataset.buyItem)));root.querySelectorAll('[data-sell-item]').forEach(btn=>btn.addEventListener('click',()=>sellItem(btn.dataset.sellItem)));
  }
  function buyItem(id){const item=window.EQUIPMENT?.[id],price=SHOP_PRICES[id],run=currentRun(),gold=getGold();if(!item||price==null||!run||gold<price)return;run.gold=gold-price;owned.set(id,(owned.get(id)||0)+1);syncInventory();renderShop();window.activePartyPanel?.render?.();const s=document.getElementById('status');if(s)s.textContent=`${item.name} 구매 완료 · ${price}G`;}
  function sellItem(id){const item=window.EQUIPMENT?.[id],qty=owned.get(id)||0,price=Math.floor((SHOP_PRICES[id]||0)*SELL_RATE),run=currentRun();if(!item||qty<=0||price<=0||!run||isEquipped(id))return;owned.set(id,qty-1);if(owned.get(id)<=0)owned.delete(id);run.gold=getGold()+price;syncInventory();renderShop();window.activePartyPanel?.render?.();const s=document.getElementById('status');if(s)s.textContent=`${item.name} 매각 완료 · +${price}G`;}
  window.shopRender=renderShop;window.shopInventory=()=>getInventory();window.shopBuy=buyItem;window.shopSell=sellItem;window.isEquipmentOwned=id=>(owned.get(id)||0)>0;window.getEquipmentQuantity=id=>owned.get(id)||0;
  window.addEventListener('DOMContentLoaded',()=>{syncInventory();renderShop();});
})();
