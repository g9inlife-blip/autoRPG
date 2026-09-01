const DUNGEONS = {
  'dungeon.ancient_forest': {
    id: 'dungeon.ancient_forest', name: '고대의 숲', floors: 10,
    encounters: [
      { type: 'battle', enemies: [{ id:'wolf', name:'숲의 늑대', hp:180, attack:38, defense:15, speed:14 }] },
      { type: 'battle', enemies: [{ id:'goblin-a', name:'고블린', hp:210, attack:42, defense:18, speed:10 }, { id:'goblin-b', name:'고블린 궁수', hp:160, attack:48, defense:12, speed:13 }] },
      { type: 'treasure', itemId:'potion', itemName:'회복 물약', quantity:2, gold:80 },
      { type: 'battle', enemies: [{ id:'forest-boar', name:'숲멧돼지', hp:300, attack:55, defense:25, speed:8 }] }
    ],
    boss: { id:'forest-guardian', name:'숲의 수호자', hp:700, attack:72, defense:35, speed:11 }
  }
};
window.DUNGEONS = DUNGEONS;
