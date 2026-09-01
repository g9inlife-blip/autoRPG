window.EQUIPMENT = {
  iron_sword: {id:'iron_sword',name:'철검',slot:'weapon',attack:12,defense:0,speed:0,rarity:'common',requirements:{classes:['warrior','rogue'],stats:{STR:10}}},
  oak_staff: {id:'oak_staff',name:'참나무 지팡이',slot:'weapon',attack:15,defense:0,speed:-1,rarity:'common',requirements:{classes:['mage'],stats:{INT:12}}},
  hunter_dagger: {id:'hunter_dagger',name:'사냥꾼의 단검',slot:'weapon',attack:8,defense:0,speed:3,rarity:'common',requirements:{classes:['rogue'],stats:{AGI:12}}},
  leather_armor: {id:'leather_armor',name:'가죽 갑옷',slot:'armor',attack:0,defense:8,speed:0,rarity:'common',requirements:{classes:['warrior','rogue','mage'],stats:{}}},
  iron_armor: {id:'iron_armor',name:'철 갑옷',slot:'armor',attack:0,defense:15,speed:-2,rarity:'uncommon',requirements:{classes:['warrior'],stats:{STR:12,VIT:12}}},
  adventurer_ring: {id:'adventurer_ring',name:'모험가의 반지',slot:'accessory',attack:4,defense:4,speed:1,rarity:'uncommon',requirements:{classes:['warrior','mage','rogue'],stats:{}}}
};
window.STARTER_EQUIPMENT = ['iron_sword','oak_staff','hunter_dagger','leather_armor','adventurer_ring'];
window.EQUIPMENT_SLOTS = {weapon:'무기',armor:'방어구',accessory:'장신구'};
