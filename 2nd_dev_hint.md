# autoRPG 2nd Development Handoff

작성일: 2026-09-02
저장소: `g9inlife-blip/autoRPG`
브랜치: `work/dungeon-run`

> 새 채팅에서 이 파일과 `autoRPG_20260901.md`를 먼저 읽고 현재 GitHub 브랜치의 HEAD를 확인한 뒤 개발을 이어간다. 설명만 하지 말고 필요한 코드 수정과 GitHub 커밋까지 진행한다.

## 1. 개발 원칙

- GitHub `work/dungeon-run`이 기준이다. 로컬 상태를 기준으로 판단하지 않는다.
- 모바일 우선 UI. PC 최적화보다 모바일 가독성을 우선한다.
- 사용자-facing 용어는 `층`이 아니라 **`구역`**. 내부 레거시 `floor` 변수는 호환성 때문에 남을 수 있다.
- XML 실제 값이 authoritative source다. XML 값이 있으면 성장 공식으로 덮어쓰지 않는다.
- 콘텐츠는 데이터 중심으로 확장한다. 임시 하드코딩을 최소화한다.
- Dungeon 정의는 **오직 `monster_lvl1_50.xml`의 `Dungeon` 이름**에서 만든다.
- `work/퀘스트 던전정리.xml`은 삭제되었고 Dungeon 정의에 사용하지 않는다.
- 삭제된 static dungeon 정의도 다시 부활시키지 않는다.
- 장비는 base item과 `instanceId`를 분리한다. 동일 instance는 동시에 여러 캐릭터가 장착할 수 없지만 같은 base item의 다른 instance는 가능하다.
- 캐릭터/몬스터/장비 최대 레벨은 50.
- 주인공 커스텀 생성은 아직 보류하고 dummy 데이터로 확장 경계를 유지한다.

## 2. 현재 구조

```text
GAME
├─ World: Town / Region / Dungeon / Unlock
├─ Story / Quest
├─ Adventure: DungeonRun / Encounter / Event / Battle
├─ Character
├─ Inventory / Equipment / Loot
├─ Statistics / DPS / Log
└─ Save / GameState (장기 목표)
```

핵심 원칙: **엔진은 규칙을 알고 콘텐츠는 데이터가 결정한다.**

## 3. 핵심 XML / 데이터 파일

### `work/monster_lvl1_50.xml`
실제 몬스터 원본.

```text
Monster
├─ Name
├─ Dungeon
├─ Level
├─ Grade
├─ Form
├─ HP
├─ Attack
├─ Defense
└─ Exp
```

Dungeon 이름별/Level별로 몬스터를 관리한다.

### `work/equipment_data_lvl1_25.xml`
### `work/equipment_data_lvl26_50.xml`
실제 장비 원본.

```text
Item
├─ id
├─ Name
├─ Grade
├─ ReqLevel
├─ ReqStat
├─ Type
└─ Value(Attack/Defense)
```

### `work/dungeon_mon_place.txt`
Dungeon별 **구역별 몬스터 배치**의 기준 데이터. 실제 탐험 순서와 encounter를 연결한다.

### `work/story.xml`
Chapter → Dungeon → Dialogue 구조의 Story XML.

### JS 콘텐츠
- `work/data/region-config.js`: XML Dungeon 이름을 Region에 묶는 predefined grouping
- `work/data/region-registry.js`: 실제 generated Dungeon과 Region 연결
- `work/data/skills/skill-registry.js`: 스킬 데이터
- `work/data/quests/main-quests.js`: 메인 퀘스트
- `work/data/quests/repeatable-quests.js`: 반복 퀘스트
- `work/data/quests/quest-registry.js`: 퀘스트 등록
- `work/data/story/story-registry.js`: Story 데이터 등록
- `work/data/equipment/equipment-registry.js`: 장비 registry

## 4. 핵심 엔진 파일 정의

### `work/src/world/MonsterXmlLoader.js`
`monster_lvl1_50.xml`을 파싱한다.

```text
state.loaded
state.byDungeon
state.items
getForDungeon(name,target?)
getByLevel(level)
```

### `work/src/world/EquipmentXmlLoader.js`
장비 XML 두 파일을 읽고 실제 값을 보존한다.

```text
window.XML_EQUIPMENT_POOL
window.XML_EQUIPMENT_BY_LEVEL
window.XML_EQUIPMENT_STATUS
```
`EquipmentDatabase.rebuild(all)` 호출.

### `work/src/world/EquipmentDatabase.js`
정규화된 읽기 전용 장비 DB.
주요 API: `getAll`, `getById`, `getByLevel`, `getByType`, `getByClass`, `getEquipable`, `rebuild`, `sync`.

### `work/data/dungeon-registry.js`
Monster XML의 distinct Dungeon 이름을 읽어 Dungeon을 자동 생성한다.
생성 데이터에는 `name`, `monsterDungeonName`, `floors`, `areaCount`, `levelStart`, `levelEnd`, `levelMap`, `xml`, `source`, boss 정보 등이 들어간다.
`window.DUNGEON_DATA`, `window.DUNGEONS`, `window.XML_DUNGEON_DATA`, `window.MONSTER_DATABASE`도 동기화한다.

### `work/src/world/DungeonRun.js`
실제 탐험 세션.

```text
start → 구역 진행 → encounter → battle → loot/exp/gold → 다음 구역 → boss → complete
```

새 던전 시작 시 장비/inventory/instanceId/XP는 보존하고 run-specific 상태만 초기화한다. Full reset은 party/inventory/equipment를 초기화한다.

### `work/src/world/LootSystem.js`
현재 rarity 확률:

```text
Uncommon 60%
Common   25%
Rare     10%
Unique    4%
Legendary 1%
```

강화:

```text
+0 80%
+1 15%
+2 5%
```

loot는 개별 `instanceId`를 만든다.

### `work/src/character/CharacterProgression.js`
최대 레벨 50, baseExp 100, growth 1.35, 직업별 성장 설정.

### `work/src/character/CharacterFactory.js`
현재 dummy 캐릭터 생성.

```text
Warrior: HP450 ATK62 DEF35 SPD12 / STR14 INT8 AGI10 VIT18
Mage:    HP280 ATK78 DEF16 SPD11 / STR7 INT17 AGI12 VIT10
Rogue:   HP320 ATK55 DEF22 SPD18 / STR10 INT10 AGI16 VIT12
```

### `work/src/battle/Character.js`
전투 Character 모델. MP/maxMP도 지원한다.

### `work/src/world/DungeonEvents.js`
Healing Spring 등 탐험 이벤트. Healing Spring은 현재 2%, HP/MP 약 30% 회복. 마지막 구역이면 종료. 마지막 구역 판정은 `areaCount()`를 사용한다.

### `work/src/battle/DialogueResolver.js`
대화 해결기. 현재 `window.DIALOGUES` 중심으로 남은 부분이 있어 Story XML과 완전 연결하는 작업이 향후 필요하다.

### `work/src/quest/QuestEngine.js`
JS Quest 데이터를 실행. Quest XML은 현재 사용하지 않는다.

## 5. 현재 Dungeon 구역 처리

과거 문제: Dungeon이 실제 배치와 관계없이 5층/5구역에서 종료됨.

현재 해결 방식:

`DungeonRun.areaCount()` 우선순위:

```text
dungeon.areaCount
↓
areas[].area 최대값
↓
legacy floors
```

`nextFloor()`, `step()`, summary 및 UI가 이 가변 구역 수를 사용한다.

`dungeon-registry.js`는 placement loader의 선언값과 실제 area 번호를 비교하여 `areaCount`를 결정한다.

마지막 작업 당시 기대 구역 수:

```text
시작의 초원 5
음산한 숲 5
버려진 광산 6
잊혀진 지하묘지 6
통곡의 계곡 7
붉은 용암 동굴 7
얼음의 성채 8
맹독의 늪지 8
환영의 탑 10
종말의 요새 10
```

새 채팅에서는 반드시 현재 `dungeon_mon_place.txt`와 코드로 다시 확인한다.

## 6. Region

현재 Region 구성:

```text
초원과 숲의 경계 = 시작의 초원 + 음산한 숲
폐광과 망자의 땅 = 버려진 광산 + 잊혀진 지하묘지
폭풍 계곡과 화산지대 = 통곡의 계곡 + 붉은 용암 동굴
빙설과 독습지 = 얼음의 성채 + 맹독의 늪지
환영의 탑과 종말의 요새 = 환영의 탑 + 종말의 요새
```

실제 Dungeon 이름은 Monster XML 이름과 정확히 일치해야 한다. 미할당 Dungeon은 `미분류 지역`.

## 7. 스킬

`work/data/skills/skill-registry.js`:

```text
Warrior 강타       1.35x / CD 2
Mage 화염구        1.55x / CD 3
Rogue 연속 베기   1.2x / 2 hits / CD 2
```

`DungeonBalancePanel.js`에는 Mage HEAL 처리도 있다: CD 3, party heal, max HP 15%, 85% 이하 우선.

## 8. UI / 로그

주요 UI:

- `work/index.html`
- `work/src/app.js`
- `work/src/ui/PartyPanel.js`
- `work/src/ui/ShopPanel.js`
- `work/src/ui/ExplorationLogPanel.js`
- `work/src/ui/DungeonBalancePanel.js`
- `work/src/ui/RegionPanel.js`

탭은 선택된 패널만 보인다.
Character detail popup은 compact/whitespace 최소화/외부 클릭 닫기/모바일 우선.

로그는 **2단계(depth 2)**다.

```text
대분류 SummaryLog
  ↓ 클릭
상세 DetailLog popup
```

전투 로그 예: `고블린 3마리와 전투` → 클릭 시 상세 전투로그.
문자열 `\\n`이 화면에 그대로 표시되지 않고 실제 줄바꿈으로 렌더링되는지 확인한다.

관련 최근 작업 대표 커밋: `f87ddf9bf994e6cced07a192f42ef32bc5320907`.

## 9. Shop

`work/src/ui/ShopPanel.js`가 Region 레벨 범위와 XML 장비를 이용해 상점을 만든다. 기본적으로 Common/Uncommon을 사용하며 XML에 가격이 없으면 계산 가격을 쓴다. Inn은 이후 구현.

## 10. Story / Quest / Event 장기 구조

Story:

```text
StoryNode → Choice → Branch → Flag
```

Quest:

```text
Story = 왜
Quest = 무엇을
Dungeon = 어디서
```

Objective 후보:
`kill`, `collect`, `explore`, `reach_area`, legacy `reach_floor`, `talk`, `choose`, `obtain_item`, `defeat_boss`, `trigger_event`.

장기 이벤트 구조:

```text
Condition → Rule Engine → Event → Effect
```

Effect 예: flag 변경, item/gold/exp 지급, dungeon unlock, quest 완료, story 이동, battle 시작.

과거 EventBus 기준 커밋:
`e759c098`
테스트 페이지 기준 커밋:
`e11c44f8`

## 11. 최근 주요 커밋

```text
6a5c8b6742ba459f0282591db9146dfbb475f7ed  DungeonRun 가변 areaCount
537e64f8d1d00d73f019b20a2623c25d6bfed322  dungeon-registry 가변 areaCount
2a80e2e4cc9a8ec79a8927a84409433bd75abd70  DungeonEvents areaCount
20bf333665ccbfcbe5646a6ca3ecc3f2777e81b0  RegionPanel 구역 표시
417ddc8f5ca3883ef3c4e2b36eeb268846fa5351  app.js 구역 표시/진행
0becd510e7b7b429fcc08e1621c2d5054b0bc48f  index.html cache version
```

마지막으로 확인한 HEAD: `0becd510e7b7b429fcc08e1621c2d5054b0bc48f`.
새 채팅에서는 반드시 현재 HEAD를 다시 확인한다.

## 12. Script/cache 주의

마지막 `index.html` cache query:

```text
dungeon-registry v20260902-9
DungeonRun v20260902-7
DungeonEvents v20260902-2
app v20260902-5
RegionPanel v20260902-2
placement loader v20260902-2
```

JS 수정 후 브라우저에서 구버전이 보이면 query version을 갱신한다.
`index.html`의 async XML loader → registry sync 순서를 함부로 변경하지 않는다.

## 13. 알려진 기술 부채

1. `DungeonRun.getXmlEnemies()`의 placement 우선 처리와 registry monkey patch가 일부 중복될 수 있다. 안정화 후 한 곳으로 정리 가능.
2. `DungeonEvents.js`는 load 시점에 `DungeonRun.prototype.step`을 wrap하므로 script 순서를 조심한다.
3. `DialogueResolver.js`와 Story XML의 완전 연결이 남아 있다.
4. 보스 보상에 임시 `수호자의 보물` 500골드가 들어간 부분은 실제 데이터 기준으로 재검토한다.
5. XML에 가격이 생기면 Shop 계산 가격보다 XML 가격을 우선한다.
6. Healing Spring 2%는 의도된 희귀 확률. 임의 변경 금지.

## 14. 삭제/비사용

삭제됨:

```text
work/퀘스트 던전정리.xml
work/data/dungeons.js
work/data/dungeons/ancient-forest.js
work/data/dungeons/goblin-cave.js
work/data/dungeons/ancient-ruins.js
```

독립 static Dungeon 정의를 다시 만들지 않는다.

## 15. 앞으로의 우선순위

### P1: Dungeon + Battle 완성

```text
Region → Dungeon → 구역별 Placement → Encounter → Battle → Loot/EXP/Gold → 다음 구역 → Boss → Complete → Result
```

**Dungeon progression과 monster placement/level tuning은 같이 개발한다.**

### P2: BattleEngine 분리

장기 목표:

```text
BattleEngine
├─ TurnManager
├─ Initiative
├─ ActionResolver
├─ AttackResolver
├─ SkillResolver
├─ StatusResolver
├─ DamageCalculator
├─ DeathResolver
├─ EscapeResolver
├─ LootResolver
└─ BattleLog
```

대규모 리팩터링은 현재 플레이 루프가 안정된 뒤 점진적으로 한다.

### P3: Data-driven Dungeon 확장

Monster XML + placement + region config만으로 새 Dungeon을 추가할 수 있게 한다. 구역 수를 5로 고정하지 않는다.

### P4: Story → Flag → Unlock
### P5: Quest 확장
### P6: Inventory/Equipment UI 완성
장착/해제/판매/강화/비교/Equipability를 instanceId 기준으로 완성.

Equipability:
`Class AND Level AND Required Stats`.

### P7: GameState / Save / Load

장기 목표:

```text
GameState
├─ player
├─ world
├─ adventure
├─ battle
└─ logs
```

active DungeonRun도 저장/복구.

### P8: SeededRandom / 회귀 테스트

엔진에서 직접 `Math.random()`을 호출하지 않고 `RandomProvider → SeededRandom`으로 전환. 같은 seed + 같은 입력 = 같은 결과를 목표로 한다.

## 16. 밸런스 원칙

보스 난이도는 XML HP/Attack을 임의로 바꾸기보다 파티 생존성/회복/스킬/장비/전투 메커니즘으로 조절한다.

XML 실제 수치는 authoritative.

## 17. 새 채팅 시작 순서

```text
1. g9inlife-blip/autoRPG 확인
2. work/dungeon-run 확인
3. 현재 HEAD 확인
4. 2nd_dev_hint.md 읽기
5. autoRPG_20260901.md 읽기
6. index.html script 로딩 순서 확인
7. DungeonRun.js 확인
8. dungeon-registry.js 확인
9. MonsterXmlLoader.js 확인
10. dungeon_mon_place.txt 확인
11. 요청 기능이 어느 데이터/엔진/UI에 연결되는지 판단
12. 필요한 파일만 수정
13. GitHub commit
14. commit SHA 기록
```

새 채팅에서 사용할 시작 문장:

```text
GitHub의 g9inlife-blip/autoRPG work/dungeon-run에서 계속 개발한다. 2nd_dev_hint.md와 autoRPG_20260901.md를 먼저 읽고 현재 HEAD와 코드를 확인한 뒤 기존 구조를 유지하면서 요청한 기능을 실제 구현하고 GitHub에 커밋해줘. 사용자-facing 용어는 '구역', Dungeon 정의는 monster_lvl1_50.xml 기준, XML 실제 값은 authoritative source로 유지해줘.
```

## 18. 한 줄 요약

> `monster_lvl1_50.xml` → Dungeon 자동 생성 → `dungeon_mon_place.txt` 구역별 배치 → 가변 `DungeonRun` → Battle/Loot/EXP/Gold → Region/UI/로그가 연결된 상태다. 다음 핵심은 실제 전투 루프 완성, 그 다음 BattleEngine 분리와 Story/Quest/Unlock/EventBus/GameState/Save 확장이다.
