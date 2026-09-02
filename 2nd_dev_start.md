# autoRPG 2nd Development Start

작성일: 2026-09-02  
저장소: `g9inlife-blip/autoRPG`  
기준 브랜치: `work/dungeon-run`

> 이 문서는 2차 개발의 **현재 기준 문서**다. 이후 개발은 이 문서를 우선 기준으로 삼고, 실제 GitHub `work/dungeon-run`의 현재 코드와 XML을 대조하여 진행한다. 필요한 경우 실제 코드 수정과 GitHub 커밋까지 진행한다.
>
> `2nd_dev_hint.md`와 이전 MD들은 중간 개발 기록으로 참고한다.

## 1. 개발 원칙

- GitHub `work/dungeon-run`이 source of truth다.
- 모바일 우선 UI.
- 사용자-facing 용어는 **구역**. 내부 레거시 `floor` 변수는 호환성을 위해 남을 수 있다.
- XML 실제 값이 authoritative source다. XML에 실제 값이 있으면 성장 공식으로 덮어쓰지 않는다.
- 콘텐츠는 데이터 중심으로 확장하고 임시 하드코딩을 최소화한다.
- Dungeon 정의는 `work/monster_lvl1_50.xml`의 distinct `Dungeon` 이름에서 생성한다.
- 삭제된 `work/퀘스트 던전정리.xml` 및 삭제된 static Dungeon 정의를 다시 사용하지 않는다.
- 장비의 base item과 `instanceId`를 분리한다.
- 캐릭터/몬스터/장비 최대 레벨은 50.
- 주인공 커스텀 생성은 보류하고 현재는 dummy party를 사용한다.

핵심 원칙: **엔진은 규칙을 알고, 콘텐츠는 데이터가 결정한다.**

## 2. 현재 게임 구조

```text
GAME
├─ World: Town / Region / Dungeon / Unlock
├─ Story / Quest
├─ Adventure: DungeonRun / Encounter / Event / Battle
├─ Character
├─ Inventory / Equipment / Loot
├─ Statistics / DPS / Log
└─ Save / GameState
```

## 3. 핵심 데이터

### 몬스터
`work/monster_lvl1_50.xml`

```text
Monster: Name / Dungeon / Level / Grade / Form / HP / Attack / Defense / Exp
```

몬스터의 실제 능력치는 XML을 기준으로 한다.

### 장비
`work/equipment_data_lvl1_25.xml`  
`work/equipment_data_lvl26_50.xml`

```text
Item: id / Name / Grade / ReqLevel / ReqStat / Type / Value(Attack/Defense)
```

### 구역별 몬스터 배치
`work/dungeon_mon_place.txt`

Dungeon별 실제 구역/몬스터 배치의 기준 데이터다.

### Story
`work/story.xml`

Chapter → Dungeon → Dialogue 구조.

## 4. 핵심 엔진

### `work/src/world/MonsterXmlLoader.js`
몬스터 XML 파서. `state.loaded`, `state.byDungeon`, `state.items`, `getForDungeon(name,target?)`, `getByLevel(level)`을 제공한다.

### `work/src/world/EquipmentXmlLoader.js`
두 장비 XML을 읽고 실제 값을 보존하며 `EquipmentDatabase.rebuild(all)`을 호출한다.

### `work/src/world/EquipmentDatabase.js`
정규화된 읽기 전용 장비 DB. `getAll`, `getById`, `getByLevel`, `getByType`, `getByClass`, `getEquipable`, `rebuild`, `sync`.

### `work/data/dungeon-registry.js`
Monster XML의 distinct Dungeon 이름으로 Dungeon을 자동 생성한다. `name`, `monsterDungeonName`, `floors`, `areaCount`, `levelStart`, `levelEnd`, `levelMap`, XML/source/boss 정보를 가진다. placement loader를 이용해 `areaCount`를 결정한다.

### `work/src/world/DungeonRun.js`
실제 탐험 세션. `start → 구역 진행 → encounter → battle → loot/exp/gold → 다음 구역 → boss → complete` 흐름이다.

### `work/src/world/LootSystem.js`
현재 rarity 확률: Uncommon 60%, Common 25%, Rare 10%, Unique 4%, Legendary 1%. 강화: +0 80%, +1 15%, +2 5%. loot instance는 고유 `instanceId`를 가진다.

### `work/src/character/CharacterProgression.js`
max level 50, baseExp 100, growth 1.35, class별 성장 설정.

### 현재 dummy party
`work/src/character/CharacterFactory.js`

```text
Warrior: HP450 ATK62 DEF35 SPD12 / STR14 INT8 AGI10 VIT18
Mage:    HP280 ATK78 DEF16 SPD11 / STR7 INT17 AGI12 VIT10
Rogue:   HP320 ATK55 DEF22 SPD18 / STR10 INT10 AGI16 VIT12
```

## 5. 2차 개발 최우선 목표: 전투 밸런스

현재 실제 전투에서 **시작의 초원 1~5레벨 구역의 보스에게 항상 또는 반복적으로 사망**하는 문제가 있다. 캐릭터를 레벨 20까지 올려도 해당 지역 보스를 안정적으로 이기지 못한다.

단순히 감으로 HP/ATK를 조정하지 않는다. 실제 XML 장비 풀, 캐릭터 전투 규칙, 스킬, BattleEngine을 기준으로 수치화하여 보스 HP를 결정한다.

## 6. 전투 밸런스 산출 기준

### 6.1 레벨별 캐릭터 Min/Max Damage

각 Dungeon은 레벨별 몬스터/구역 데이터가 있으므로 해당 레벨에서 캐릭터가 낼 수 있는 공격력을 자동 산출한다.

각 레벨/캐릭터/Dungeon에 대해:

```text
최소 공격력(Min Damage)
최대 공격력(Max Damage)
```

을 산출한다.

장비는 해당 캐릭터가 착용 가능한 **실제 XML 장비 풀**을 기준으로 한다. 해당 레벨에서 장비를 최대한 활용한 상태를 기준으로 한다. 다만 계산기는 반드시 실제 `Character`의 장비 적용 규칙과 동일해야 하며, 단순히 장비 수치를 합산해서는 안 된다.

### 6.2 3인 파티 기준 공격력

현재 파티는 3명이다. 1차 밸런스 기준은 사용자가 제안한 다음 공식을 그대로 사용한다.

```text
캐릭터 기준 공격력
= (최대 데미지 + 최소 데미지) / 1.5

3인 파티 기준 총 데미지
= 캐릭터 기준 공격력 × 2
```

즉 출발 기준값은:

```text
(Max + Min) / 1.5 × 2
```

이다.

이 값은 보스 HP 후보를 만드는 기준값이며, 최종 HP를 이론식 하나로 확정하지 않는다.

### 6.3 보스 처치 확률 90%

각 Dungeon 보스의 HP 후보를 만들고 **실제 BattleEngine으로 반복 시뮬레이션**한다.

목표:

```text
해당 Dungeon/레벨의 3인 파티
→ 해당 Dungeon 보스
→ 처치 성공률 >= 90%
```

산출 흐름:

```text
XML 캐릭터/장비 데이터
        ↓
레벨별 착용 가능 장비 풀
        ↓
캐릭터 Min/Max Damage
        ↓
(Max + Min) / 1.5
        ↓
× 2 = 3인 파티 기준값
        ↓
보스 HP 후보 생성
        ↓
실제 BattleEngine 반복 시뮬레이션
        ↓
승률 계산
        ↓
90% 이상을 만족하는 Boss HP 결정
```

**90%는 이론값이 아니라 실제 전투 시뮬레이션 승률로 검증한다.**

## 7. 계산에 포함해야 할 실제 전투 요소

### 캐릭터

- Level
- Base HP / ATK / DEF
- STR / INT / AGI / VIT
- SPD
- Class
- Skill
- Skill cooldown
- Critical
- 실제 공격 순서

### 장비

- ReqLevel
- ReqStat
- Type
- Grade
- Attack / Defense Value
- Enhancement
- Class 제한
- 슬롯
- base item / instanceId 분리

### 전투

- DamageCalculator
- BattleEngine
- 일반 공격
- 스킬 공격
- 다단히트
- 치명타
- 방어력 적용
- 공격 속도 / 행동 순서
- 전투 라운드
- 생존 여부
- 회복 스킬

현재 스킬:

```text
Warrior 강타      1.35x / CD 2
Mage 화염구       1.55x / CD 3
Rogue 연속 베기   1.20x / 2 hits / CD 2
```

Mage 회복 스킬이 실제 전투에도 적용되는지 코드 기준으로 확인한다.

## 8. Dungeon별 밸런스 산출표

최종적으로 10개 Dungeon 전체를 자동 산출한다.

현재 기대 구역 수:

```text
시작의 초원       5
음산한 숲         5
버려진 광산       6
잊혀진 지하묘지   6
통곡의 계곡       7
붉은 용암 동굴    7
얼음의 성채       8
맹독의 늪지       8
환영의 탑        10
종말의 요새      10
```

실제 값은 현재 `monster_lvl1_50.xml`과 `dungeon_mon_place.txt`로 재확인한다.

최소 결과:

```text
Dungeon
구역
구역 레벨
보스 이름
보스 기존 HP
캐릭터별 Min Damage
캐릭터별 Max Damage
캐릭터별 기준 Damage
파티 기준 Damage
추천 Boss HP
시뮬레이션 횟수
처치 성공률
평균 전투 라운드
파티 전멸률
```

가능하면 `캐릭터별 DPS`, `총 피해량`, `총 피격 피해량`, `공격/적중/치명타 횟수`, `스킬 사용 횟수`, `생존율`도 산출한다.

## 9. DungeonRun과 Balance Simulator 통합

현재 가장 중요한 기술 부채다. `DungeonRun.getXmlEnemies()`와 `DungeonBalance.js`가 각각 encounter를 만들면 밸런스 결과와 실제 플레이가 달라질 수 있다.

따라서 공통 Encounter Resolver/Factory를 만들고 다음을 동일하게 처리한다.

```text
공통 Encounter Resolver / Factory
        ↓
DungeonRun
DungeonBalance
```

동일 기준:

- Dungeon
- 구역
- areaCount
- placement
- encounter
- monster selection
- level
- grade
- form
- boss 판정

**시뮬레이터에서 이기는데 실제 게임에서 죽는 상황을 허용하지 않는다.**

## 10. 전투 밸런스 개발 순서

### Phase 1-A: 실제 전투 규칙 확인

1. `BattleEngine` 확인
2. `DamageCalculator` 확인
3. Character 장비 스탯 적용 경로 확인
4. Skill 적용 경로 확인
5. Critical / SPD / cooldown 확인
6. 시작의 초원 보스전 실패 원인 재현

### Phase 1-B: 공통 Encounter

1. DungeonRun encounter 정리
2. DungeonBalance encounter 정리
3. 공통 resolver/factory 작성
4. 실제 전투와 simulator가 동일한 몬스터/구역 규칙을 사용하도록 수정

### Phase 1-C: 공격력 산출기

1. 레벨별 캐릭터 생성
2. 해당 레벨의 착용 가능 XML 장비 탐색
3. 캐릭터별 최적 장비 조합 계산
4. Min/Max Damage 산출
5. `(Max + Min) / 1.5 × 2` 파티 기준값 계산
6. 각 Dungeon 보스 HP 후보 생성

### Phase 1-D: 실제 시뮬레이션

1. 실제 BattleEngine 반복 전투
2. HP 후보별 승률 계산
3. 90% 이상을 만족하는 HP 선택
4. 10개 Dungeon 전체 검증
5. 결과를 DungeonBalancePanel에서 확인

## 11. 밸런스 조정 원칙

우선순위:

1. **데이터/규칙 오류 확인**
2. **데이터 기반 계산**
3. **실제 BattleEngine 시뮬레이션**
4. **90% 기준을 만족하는 최소한의 보스 HP 조정**

확인 대상 예:

- 장비가 실제 전투에 적용되지 않음
- 레벨업 스탯이 적용되지 않음
- 스킬이 적용되지 않음
- 잘못된 레벨 몬스터 선택
- placement 오류
- 방어력 계산 이중 적용
- 실제 전투와 simulator의 공격 순서 차이

근거 없이 `HP 20% 감소`, `ATK 30% 증가` 같은 조정을 반복하지 않는다.

## 12. 장기 개발 로드맵

### Phase 1 — 전투 코어 / 밸런스

- Encounter 통합
- DungeonRun = Balance 동일 규칙
- 실제 장비 적용 검증
- Skill/Battle 연동
- DPS/Statistics
- 전투 로그
- 10개 Dungeon 밸런스 검증

### Phase 2 — 성장 / 장비

- EXP → Level → 실제 전투 스탯 연결
- Equipment equip / unequip
- 장비 슬롯 / 교체
- Level / Stat restriction
- Enhancement
- Inventory
- Shop
- Loot economy

### Phase 3 — 진행 / 퀘스트 / 이벤트

- Region unlock
- Dungeon unlock
- Boss progression
- QuestEngine ↔ EventBus
- Event Condition / Effect
- 보상 데이터화

### Phase 4 — Story

- Story XML parser
- DialogueResolver ↔ Story XML
- Choice / Branch / Flag
- Story progression

### Phase 5 — Save / 완성

- GameState
- localStorage
- 자동 저장 / 복구
- UI polish
- 전체 밸런스 재검증

## 13. UI / 로그 요구사항

- 모바일 우선
- Character detail popup compact
- 불필요한 빈 여백 최소화
- 팝업 외부 클릭 시 닫기
- DPS 정보는 기존 빈 공간을 활용해 전체 높이를 줄인다
- 로그는 depth 2

```text
SummaryLog
  ↓ 클릭
DetailLog popup
```

예: `고블린 3마리와 전투` → 클릭 시 상세 전투 로그.

문자열 `\\n`은 화면에 그대로 출력하지 않고 실제 줄바꿈으로 렌더링한다.

## 14. Story / Quest / Event

Story:

```text
StoryNode → Choice → Branch → Flag
```

관계:

```text
Story = 왜
Quest = 무엇을
Dungeon = 어디서
```

Objective 후보:
`kill`, `collect`, `explore`, `reach_area`, `reach_floor(legacy)`, `talk`, `choose`, `obtain_item`, `defeat_boss`, `trigger_event`.

Event:

```text
Condition → Rule Engine → Event → Effect
```

Effect 예: flag, item, gold, exp, dungeon unlock, quest complete, story 이동, battle 시작.

## 15. 기술 부채 / 주의사항

1. `DungeonRun.getXmlEnemies()`의 placement 우선 처리와 registry monkey patch 중복 여부를 안정화 후 정리한다.
2. `DungeonEvents.js`는 load 시점에 `DungeonRun.prototype.step`을 wrap하므로 script order를 주의한다.
3. `DialogueResolver.js`와 Story XML의 완전 연결이 남아 있다.
4. Boss 보상의 임시 `수호자의 보물` 500골드는 데이터 기준으로 재검토한다.
5. XML price가 있으면 Shop 계산 가격보다 XML price를 우선한다.
6. Healing Spring 2%는 의도된 희귀 확률이므로 임의 변경하지 않는다.
7. JS 수정 후 `index.html` cache query version을 확인/갱신한다.

현재 알려진 cache 기준:

```text
dungeon-registry v20260902-9
DungeonRun v20260902-7
DungeonEvents v20260902-2
app v20260902-5
RegionPanel v20260902-2
placement loader v20260902-2
DungeonBalancePanel v20260902-2
```

실제 HEAD의 `index.html`이 다르면 현재 브랜치 값을 우선한다.

## 16. 다음 실제 작업

```text
[1] 현재 work/dungeon-run HEAD 확인
        ↓
[2] BattleEngine / DamageCalculator / Character / Equipment 적용 경로 분석
        ↓
[3] 시작의 초원 보스전 실제 실패 원인 재현
        ↓
[4] 공통 Encounter Resolver 구축
        ↓
[5] 레벨별 장비 풀 기반 Min/Max Damage 계산기 구축
        ↓
[6] (Max + Min) / 1.5 × 2 파티 기준값 산출
        ↓
[7] 실제 BattleEngine으로 보스 HP 후보별 반복 시뮬레이션
        ↓
[8] 90% 승률을 만족하는 Boss HP 결정
        ↓
[9] 10개 Dungeon 전체 밸런스 테이블 생성
        ↓
[10] 실제 DungeonRun으로 결과 재검증
```

### 완료 조건

- 시작의 초원 보스가 계산/시뮬레이션 근거로 밸런싱되어야 한다.
- 레벨 1~50에 대해 캐릭터별 장비 착용 기준 Min/Max Damage를 자동 산출할 수 있어야 한다.
- 3인 파티 기준값 `(Max + Min) / 1.5 × 2`를 자동 계산할 수 있어야 한다.
- 각 Dungeon 보스에 대해 90% 처치 확률을 실제 BattleEngine 반복 시뮬레이션으로 검증할 수 있어야 한다.
- Balance Simulator와 실제 DungeonRun의 encounter/battle 규칙이 동일해야 한다.
- 임의의 HP/ATK 조정이 아니라 데이터와 시뮬레이션 결과가 밸런스 변경의 근거가 되어야 한다.
