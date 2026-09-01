export function createInitialGameState(seed = 12345) {
  return {
    version: 1,
    seed,
    player: {
      gold: 100,
      characters: [],
      inventory: [],
      resources: {}
    },
    world: {
      townId: 'town.start',
      dungeonId: null,
      floor: 0,
      flags: {}
    },
    adventure: {
      active: false,
      currentEvent: null
    },
    battle: {
      active: false,
      state: null
    },
    logs: []
  };
}
