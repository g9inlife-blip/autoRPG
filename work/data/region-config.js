// Predefined world regions. Every dungeon name here must exist in monster_lvl1_50.xml.
window.REGION_CONFIG = {
  'region-frontier': {
    id:'region-frontier',
    name:'초원과 숲의 경계',
    description:'모험의 시작점과 야생 숲으로 이어지는 초반 권역.',
    dungeons:['시작의 초원','음산한 숲']
  },
  'region-mining-grave': {
    id:'region-mining-grave',
    name:'폐광과 망자의 땅',
    description:'광산과 오래된 지하묘지가 이어지는 중반 초입 권역.',
    dungeons:['버려진 광산','잊혀진 지하묘지']
  },
  'region-valley-volcano': {
    id:'region-valley-volcano',
    name:'폭풍 계곡과 화산지대',
    description:'강풍과 용암이 공존하는 고위험 권역.',
    dungeons:['통곡의 계곡','붉은 용암 동굴']
  },
  'region-ice-swamp': {
    id:'region-ice-swamp',
    name:'빙설과 독습지',
    description:'극한의 추위와 맹독성 환경이 공존하는 권역.',
    dungeons:['얼음의 성채','맹독의 늪지']
  },
  'region-final-fortress': {
    id:'region-final-fortress',
    name:'환영의 탑과 종말의 요새',
    description:'최종 단계의 강적이 기다리는 후반 권역.',
    dungeons:['환영의 탑','종말의 요새']
  }
};
