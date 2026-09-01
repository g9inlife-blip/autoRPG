window.DIALOGUES = {
  battle_start: ['"전투 준비!"','"상대의 움직임을 살펴."'],
  attack: ['"간다!"','"내 공격을 받아라!"','"빈틈이 보여!"','"계속 몰아붙여!"'],
  critical: ['"제대로 맞았다!"','"좋은 한 방이야!"'],
  miss: ['"빗나갔어!"','"다음엔 맞춘다!"'],
  ally_defeat: ['"괜찮아? 내가 버틸게!"','"한 명 쓰러졌어! 정신 차려!"'],
  enemy_defeat: ['"해냈다!"','"한 놈 쓰러뜨렸어!"'],
  victory: ['"끝났다. 모두 무사한가?"','"좋아, 계속 전진하자."'],
  defeat: ['"후퇴해야 해..."','"여기까지인가..."']
};
window.DIALOGUE_RULES = {
  default: { relation:'neutral', priority:0 },
  bond: { relation:'bond', priority:50 },
  event: { relation:'event', priority:100 }
};
