# Character System

캐릭터 생성은 `CharacterFactory`를 통해 통일한다.

## 현재
- 주인공은 `DUMMY_PARTY`의 `hero-dummy`를 사용한다.
- 실제 커스텀 주인공 생성 UI/외형/스탯 배분은 아직 구현하지 않는다.
- 이후 커스텀 생성이 추가되어도 Battle/Quest/Story가 캐릭터 생성 방식을 직접 알 필요가 없도록 Factory 경계를 유지한다.

## 확장 예정
`create({ ... })` 입력에 다음을 단계적으로 추가할 수 있다.
- 이름
- 성별/종족/직업
- 외형 파츠
- 초기 스탯 배분
- 특성/성격
- 시작 스킬
- 성장 보정
- 주인공 전용 Story Flag

저장 포맷은 GameState의 player/party 데이터와 분리 가능한 형태를 유지한다.
