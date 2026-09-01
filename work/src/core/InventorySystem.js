(() => {
  function ensureInventory(state) {
    if (!state?.player) return [];
    if (!Array.isArray(state.player.inventory)) state.player.inventory = [];
    return state.player.inventory;
  }
  window.addLootInstance = function(item) {
    if (!item) return null;
    const inventory = ensureInventory(window.gameState);
    inventory.push(item);
    return item;
  };
  window.getInventory = function() {
    return ensureInventory(window.gameState);
  };
  window.removeInventoryInstance = function(instanceId) {
    const inventory = ensureInventory(window.gameState);
    const index = inventory.findIndex(x => x?.instanceId === instanceId);
    return index >= 0 ? inventory.splice(index, 1)[0] : null;
  };
  window.setGameStateForInventory = function(state) {
    window.gameState = state;
    ensureInventory(state);
    return state;
  };
})();
