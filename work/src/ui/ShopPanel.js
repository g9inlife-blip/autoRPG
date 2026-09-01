(() => {
  const SHOP_PRICES = { iron_sword: 60, oak_staff: 70, hunter_dagger: 65, leather_armor: 80, adventurer_ring: 120 };
  const owned = new Set();

  function getItems() { return Object.values(window.EQUIPMENT || {}).filter(x => SHOP_PRICES[x.id] != null); }
  function renderShop() {
    const root = document.getElementById('shop');
    if (!root) return;
    const gold = Number(window.run?.gold ?? 0);
    root.innerHTML = getItems().map(item => {
      const has = owned.has(item.id);
      const stats = [`공 ${item.attack||0}`, `방 ${item.defense||0}`, `속 ${item.speed||0}`].join(' · ');
      return `<div class="shop-item"><div class="shop-item-main"><b>${item.name}</b><small>${stats}</small></div><button type="button" data-buy-item="${item.id}" ${has || gold < SHOP_PRICES[item.id] ? 'disabled' : ''}>${has ? '보유' : `구매 ${SHOP_PRICES[item.id]}G`}</button></div>`;
    }).join('') || '<p>판매할 장비가 없습니다.</p>';
    root.querySelectorAll('[data-buy-item]').forEach(btn => btn.addEventListener('click', () => buyItem(btn.dataset.buyItem)));
  }
  function buyItem(id) {
    const item = window.EQUIPMENT?.[id], price = SHOP_PRICES[id];
    if (!item || owned.has(id) || !window.run || window.run.gold < price) return;
    window.run.gold -= price;
    owned.add(id);
    window.playerInventory = [...owned].map(x => window.EQUIPMENT[x]).filter(Boolean);
    if (typeof window.render === 'function') window.render();
    renderShop();
    const status = document.getElementById('status');
    if (status) status.textContent = `${item.name} 구매 완료 · ${price}G`;
  }
  window.shopRender = renderShop;
  window.shopInventory = () => [...owned].map(id => window.EQUIPMENT?.[id]).filter(Boolean);
  window.shopBuy = buyItem;
  window.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.tabs [data-tab]').forEach(btn => btn.addEventListener('click', () => { if (btn.dataset.tab === 'shopTab') renderShop(); }));
    renderShop();
  });
})();
