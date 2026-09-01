window.DIALOGUES={
  default:{battle_start:['전투 준비!','상대의 움직임을 살펴.'],attack:['간다!','내 공격을 받아라!','빈틈이 보여!','계속 몰아붙여!'],critical:['제대로 맞았다!','좋은 한 방이야!'],miss:['빗나갔어!','다음엔 맞춘다!'],ally_defeat:['괜찮아? 내가 버틸게!','한 명 쓰러졌어! 정신 차려!'],enemy_defeat:['해냈다!','한 놈 쓰러뜨렸어!'],victory:['끝났다. 모두 무사한가?','좋아, 계속 전진하자.'],defeat:['후퇴해야 해...','여기까지인가...']},
  personalities:{brave:{attack:['내가 길을 연다!','정면으로 간다!'],critical:['이게 내 전력이다!']},calm:{attack:['침착하게 처리하자.','계산대로야.'],critical:['예상한 결과야.']},reckless:{attack:['비켜! 내가 끝낸다!','전부 날려버리겠어!']}},
  bonds:{high:{ally_defeat:['내가 지켜줄게.','네가 쓰러지는 건 용납 못 해!'],attack:['호흡이 맞아!','역시 믿을 만해!']},low:{ally_defeat:['이런... 계획을 다시 짜야겠군.'],attack:['내 공격에 방해되지 마.']}}
};
window.DIALOGUE_RULES={default:{relation:'neutral',priority:0},bond:{relation:'bond',priority:50},event:{relation:'event',priority:100}};
