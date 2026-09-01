(() => {
  const SHOP_PRICES = { iron_sword: 60, oak_staff: 70, hunter_dagger: 65, leather_armor: 80, adventurer_ring: 120, iron_armor: 180 };
  const starter = new Set(window.STARTER_EQUIPMENT || []);
  const owned = new Set(starter);

  function getItems() { return Object.values(window.EQUIPMENT || {}).filter(x => SHOP_PRICES[x.id] != null); }
  function getInventory() { return [...owned].map(id => window.EQUIPMENT?.[id]).filter(Boolean); }
  function syncInventory() { window.playerInventory = getInventory(); }

  function renderShop() {
    const root = document.getElementById('shop');
    if (!root) return;
    syncInventory();
    const gold = Number(window.run?.gold ?? 0);
    root.innerHTML = `<div class="currency-line">보유 골드 <b>${gold.toLocaleString()} G</b></div>` + getItems().map(item => {
      const has = owned.has(item.id);
      const stats = [`공 ${item.attack||0}`, `방 ${item.defense||0}`, `속 ${item.speed||0}`].join(' · ');
      return `<div class="shop-item"><div class="shop-item-main"><b>${item.name}</b><small>${stats} · ${item.rarity||'common'}</small></div><button type="button" data-buy-item="${item.id}" ${has || gold < SHOP_PRICES[item.id] ? 'disabled' : ''}>${has ? '보유' : `구매 ${SHOP_PRICES[item.id]}G`}</button></div>`;
    }).join('') || '<p>판매할 장비가 없습니다.</p>';
    root.querySelectorAll('[data-buy-item]').forEach(btn => btn.addEventListener('click', () => buyItem(btn.dataset.buyItem)));
  }

  function buyItem(id) {
    const item = window.EQUIPMENT?.[id], price = SHOP_PRICES[id];
    if (!item || owned.has(id) || !window.run || window.run.gold < price) return;
    window.run.gold -= price;
    owned.add(id);
    syncInventory();
    renderShop();
    window.activePartyPanel?.render?.();
    const status = document.getElementById('status');
    if (status) status.textContent = `${item.name} 구매 완료 · ${price}G`;
  }

  window.shopRender = renderShop;
  window.shopInventory = () => getInventory();
  window.shopBuy = buyItem;
  window.isEquipmentOwned = id => owned.has(id);
  window.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.tabs [data-tab]').forEach(btn => btn.addEventListener('click', () => { if (btn.dataset.tab === 'shopTab') renderShop(); }));
    syncInventory();
    renderShop();
  });
})();
