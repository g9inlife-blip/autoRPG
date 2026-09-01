# autoRPG 개발 인계 문서 — 2026-09-01

## 0. 목적

이 문서는 `g9inlife-blip/autoRPG`의 `work/dungeon-run` 브랜치에서 진행 중인 신규 오토RPG 개발의 현재 상태와 다음 개발 방향을 다른 GPT/개발자가 그대로 이어받을 수 있도록 기록한다.

핵심 원칙:
- 기존 autoRPG의 아이디어/구조를 참고하되 신규 게임은 `work/` 아래에서 독립적으로 확장한다.
- 게임 로직과 콘텐츠 데이터를 최대한 분리한다.
- 던전/몬스터/퀘스트/스토리/대사를 코드 수정 없이 데이터 추가만으로 확장할 수 있게 한다.
- 브라우저에서 바로 실행 가능한 HTML/JS 중심 구조를 유지하여 개발환경 제약을 최소화한다.
- Seed 기반 재현 가능한 전투/던전 테스트를 우선한다.

## 1. 현재 개발 브랜치

- Repository: `g9inlife-blip/autoRPG`
- Branch: `work/dungeon-run`
- 기준: 로컬 저장소는 사용하지 않고 GitHub 연결 상태를 기준으로 작업한다.

## 2. 현재 구현/추가된 구조

```text
work/
├─ index.html                         # 통합 게임/워크벤치 화면
├─ src/
│  ├─ app.js
│  ├─ core/
│  │  ├─ SeededRandom.js
│  │  ├─ GameState.js
│  │  ├─ EventBus.js                  # 게임 이벤트 발행/구독
│  │  └─ GameEventBridge.js            # Battle/Dungeon → Quest/Dialogue 연결
│  ├─ battle/
│  │  ├─ Character.js
│  │  ├─ DamageCalculator.js
│  │  ├─ BattleEngine.js
│  │  └─ DialogueResolver.js           # 조건 기반 대사 선택
│  ├─ world/
│  │  └─ DungeonRun.js
│  ├─ quest/
│  │  └─ QuestEngine.js
│  ├─ story/
│  │  └─ StoryEngine.js
│  └─ test/
│     └─ test-lab.html                 # 독립 로직 테스트
├─ data/
│  ├─ dungeon-registry.js
│  ├─ dungeons/
│  │  ├─ ancient-forest.js
│  │  ├─ goblin-cave.js
│  │  └─ ancient-ruins.js
│  ├─ quests/
│  │  ├─ main-quests.js
│  │  ├─ repeatable-quests.js
│  │  └─ quest-registry.js
│  ├─ story/
│  │  └─ story-registry.js
│  └─ dialogues.js
└─ css/
   └─ base.css
```

실제 파일명은 GitHub에서 확인 후 작업해야 하며, 파일을 덮어쓸 때는 반드시 최신 SHA를 먼저 가져온다.

## 3. 퀘스트 설계

퀘스트는 처음부터 두 계열로 분리했다.

### Main Quest

- `type: 'main'`
- 스토리 진행과 직접 연결
- 완료 후 `next` 또는 조건부 `branches`로 다음 퀘스트를 결정
- Story Flag와 연계 가능
- 던전 해금/스토리 분기에 사용

예시 흐름:

```text
main.001 숲의 부름
       ↓
main.002 고블린의 흔적
       ↓
   ┌───┴──────────────┐
   ↓                   ↓
goblin_village_saved   false
   ↓                   ↓
main.003a             main.003b
뜻밖의 동맹            적의 요새
```

### Repeatable Quest

- `type: 'repeatable'`
- Main Quest와 독립
- `reset: daily | weekly`를 데이터에 기록
- 일일/주간 콘텐츠로 확장
- 완료 상태를 초기화하여 재수행 가능

예시:
- 숲의 정화: daily
- 고블린 토벌: daily
- 유적 탐사: weekly

### QuestEngine

주요 API:
- `getActive(type)` — main/repeatable별 활성 퀘스트 조회
- `progress(q, objectiveIndex)` — 목표 진행도 조회
- `record(event)` — 게임 이벤트를 퀘스트 진행으로 변환
- `matches(objective,event)` — 목표 조건 판정
- `tryComplete(q)` — 모든 목표 완료 여부 판정
- `resetRepeatable(id)` — 반복 퀘스트 상태 초기화

현재 지원 목표의 기반:
- `kill`
- `collect`
- `explore`
- `reach_floor`
- `defeat_boss`

향후 `talk`, `choose`, `obtain_item`, `trigger_event` 등을 추가한다.

## 4. Story 설계

`StoryEngine`은 Story Node + Choice + Flag 구조다.

```text
Story Node
  ↓
Choice
  ↓
setFlags
  ↓
Condition
  ↓
Next Node
```

예시:

```text
story.forest.001
├─ 조사한다 → forest_investigated=true
└─ 피한다   → forest_avoided=true
```

Flag는 향후 다음 시스템의 공통 상태로 사용한다.

- Story 분기
- Main Quest 분기
- Dungeon Unlock
- NPC 상태
- 몬스터 배치 변경
- Dialogue 조건
- 보상 변경

### 중요 방향

스토리 선택에 따라 같은 던전이라도:
- 몬스터 구성
- 대사
- 퀘스트
- 보상
- 이벤트
가 달라질 수 있도록 한다.

## 5. Dialogue 설계

`DialogueResolver`는 데이터 기반 조건 대사를 선택한다.

현재 고려 우선순위:

```text
특수 Story/Event Flag
        ↓
전투 이벤트 대사
        ↓
던전 전용 대사
        ↓
유대도 대사
        ↓
성격 대사
        ↓
기본 대사
```

현재 액션 개념:
- `battle_start`
- `attack`
- `critical`
- `miss`
- `ally_defeat`
- `enemy_defeat`
- `victory`
- `defeat`

확장 예정:
- `skill`
- `heal`
- `buff`
- `debuff`
- `dodge`
- `counter`
- `combo`
- `boss_appear`
- `ally_revive`

캐릭터 데이터에는 향후 다음을 둘 수 있다.

```js
{
  personality: 'brave',
  bonds: {
    mage: 85
  }
}
```

유대도는 현재 개념상 high >= 80, low <= 20이며 중간값은 기본/성격 대사를 사용한다. 이후 관계 종류를 우정/라이벌/원한 등으로 확장할 수 있다.

## 6. EventBus / EventBridge

`EventBus`는 시스템 간 직접 의존을 줄이기 위한 가벼운 이벤트 버스다.

```text
Battle / Dungeon
       ↓
     EventBus
       ↓
GameEventBridge
   ┌───┴────┐
   ↓        ↓
 Quest    Dialogue
 Engine   Resolver
```

현재 Bridge가 고려하는 이벤트:
- `DAMAGE`
- `MISS`
- `DEFEAT`
- `BATTLE_END`
- `FLOOR_REACHED`
- `LOOT`

Quest 이벤트 변환 예:
- `DEFEAT` → `{type:'kill', target, count:1}`
- `FLOOR_REACHED` → `{type:'reach_floor', dungeon, floor, count:1}`
- `LOOT` → `{type:'collect', target:itemId, count:quantity}`

Dialogue 변환 예:
- 일반 DAMAGE → `attack`
- critical DAMAGE → `critical`
- MISS → `miss`
- 아군 DEFEAT → `ally_defeat`
- 적군 DEFEAT → `enemy_defeat`
- BATTLE_END WIN/LOSE → `victory`/`defeat`

## 7. 전투 UI 목표

현재 요구사항의 핵심:

### HP 0 표시

전투가 끝났을 때 요약 로그에:

```text
[4층] 고블린 2마리와 전투 (xxx 기절, ㅇㅇㅇ 기절)
```

처럼 해당 전투에서 HP가 0이 된 캐릭터를 표시한다.

내부 이벤트에는 최소한 다음 정보가 있어야 한다.
- defeated ID
- defeated name
- side(player/enemy)
- attacker
- floor/battle context

### 실시간 대사 영역

전투 화면 하단에 약 2줄의 대사 영역을 둔다.

```text
┌───────────────────────────┐
│ 전투 로그                 │
├───────────────────────────┤
│ “내가 간다!”              │
│ “집중해. 우리가 해낼 수 있어.” │
└───────────────────────────┘
```

액션마다 갱신되며 향후:
- 캐릭터 유대도
- 성격
- Story Flag
- 던전
- 특정 몬스터
- 이벤트
에 따라 달라진다.

## 8. 던전 확장 방향

던전 수가 증가해도 엔진 수정이 최소화되도록 데이터 중심으로 만든다.

향후 던전 데이터는 최소한 다음을 고려한다.

```text
Dungeon
├ id
├ name
├ difficulty
├ floorCount
├ floorRules
├ encounterTable
├ eliteRate
├ boss
├ lootTable
├ storyHooks
└ dialogues
```

난이도는 단순 문자열뿐 아니라 multiplier/rate로 조정 가능하게 한다.

예:

```js
{
  hpMultiplier: 1.5,
  attackMultiplier: 1.2,
  defenseMultiplier: 1.1,
  eliteRate: 0.15,
  rareRate: 0.05
}
```

몬스터 배치는 고정 층/확률 테이블/이벤트 층/보스 층을 모두 지원하도록 확장한다.

## 9. 실시간 던전 탐험 정보

기존 요구사항상 탐험 요청 이후 할 일이 부족했기 때문에 다음 정보를 실시간으로 보여주는 것이 목표다.

- 현재 던전/층
- 탐험 진행 상태
- 현재 전투
- 파티원별 누적 Damage
- 파티원별 누적 DPS
- 전투 횟수
- 처치 수
- 획득 아이템
- 기절/사망 상태
- 최근 이벤트 로그
- 전투 상세 로그

로그는 2단계다.

```text
대분류 로그
  ↓ 클릭
상세 전투 로그 팝업
```

예:

```text
[4층] 고블린 2마리와 전투 (기사 기절)
```

클릭하면:

```text
전투 상세
- 기사 공격 62
- 고블린 공격 31
- 마법사 치명타 124
- 기사 HP 0
- 전투 종료
```

## 10. 테스트 환경

`work/src/test/test-lab.html`은 독립적인 브라우저 테스트 페이지다.

현재 목표 테스트:
- Story 분기
- Quest 진행
- Dialogue 우선순위
- Seed 재현성

기존/추가 테스트는 다음으로 확대한다.

```text
[전투 100회]
[던전 100회]
[메인 퀘스트 진행]
[반복 퀘스트 진행]
[스토리 분기 A]
[스토리 분기 B]
[유대도 낮음 대사]
[유대도 높음 대사]
[이벤트 대사]
[Seed 재현성]
[Loot 분포]
[DPS 계산]
[HP 0 이벤트]
```

테스트 페이지는 게임 UI와 독립적으로 로직만 검증할 수 있어야 한다.

## 11. 콘텐츠 입력 방식

향후 사용자가 제공할 수 있는 입력은 자유로운 문서 형식도 허용한다.

예:

```text
[던전] 폐허의 성
난이도: 중
10층
일반: 해골병사 40%, 해골궁수 30%, 망령 20%, 기사 10%
5층: 엘리트
10층: 저주받은 기사
```

이를 데이터 파일로 변환하여 적용한다.

몬스터 정보도:

```text
HP
공격력
방어력
속도
스킬
AI
드롭
```

등을 데이터화한다.

스토리/대사도:

```text
조건
캐릭터
액션
대사
유대도
성격
던전
Flag
```

로 변환할 수 있도록 한다.

## 12. 다음 개발 우선순위

### Phase A — 이벤트 연결 완성
1. BattleEngine에서 EventBus를 실제 emit
2. DungeonRun에서 FLOOR_REACHED/LOOT emit
3. BATTLE_END/DEFEAT payload 표준화
4. GameEventBridge 실제 초기화/연결

### Phase B — 전투 화면
5. HP 0 캐릭터 표시
6. 2줄 실시간 Dialogue UI
7. 액션별 대사 갱신
8. 대분류/상세 로그 팝업
9. DPS/Loot 실시간 갱신

### Phase C — Quest UI
10. Main Quest / Repeatable Quest 탭 분리
11. 목표 진행도 표시
12. 완료/보상 처리
13. Daily/Weekly reset

### Phase D — Story
14. Story 선택 UI
15. StoryEngine과 GameState flags 연결
16. Main Quest branch 조건 연결
17. Story Flag 기반 Dungeon Unlock
18. Story 선택에 따른 Dungeon/Dialogue 변화

### Phase E — 콘텐츠 확장
19. Monster Registry
20. Dungeon Encounter Table
21. Difficulty Preset
22. Loot Table
23. 캐릭터 Personality/Bond 데이터
24. 이벤트 대사 데이터

### Phase F — 테스트/밸런스
25. Test Lab 확대
26. Seed 재현성 검증
27. 전투 통계 100/1000/10000회
28. DPS 분포
29. Loot 확률 검증
30. 던전 클리어율/난이도 검증

## 13. 다른 GPT가 이어서 작업할 때 반드시 지킬 것

1. 작업 브랜치는 `work/dungeon-run`을 기준으로 한다.
2. 로컬 경로는 사용하지 않는다.
3. 기존 파일을 수정하기 전 GitHub에서 최신 파일과 SHA를 조회한다.
4. 동일 파일에 연속 수정할 경우 직전 update의 최신 SHA를 사용한다.
5. 콘텐츠 데이터와 엔진 로직을 가능한 한 분리한다.
6. 새로운 기능은 가능하면 `EventBus` 이벤트로 연결한다.
7. Quest/Story/Dialogue 로직을 BattleEngine에 직접 하드코딩하지 않는다.
8. 새 콘텐츠는 데이터 파일 추가로 해결할 수 있는지 먼저 검토한다.
9. 기능 추가 시 `test-lab.html`에 최소 1개의 재현 가능한 테스트를 추가한다.
10. 커밋 후 실제 파일 경로와 commit SHA를 기록한다.
11. 구현하지 않은 기능은 구현했다고 보고하지 않는다.
12. UI를 만들기 전에 로직의 데이터 계약/event payload를 먼저 확정한다.

## 14. 현재 상태 요약

현재 프로젝트는 '전투만 돌아가는 데모'에서 '던전/전투/퀘스트/스토리/대사 시스템을 독립 데이터와 이벤트로 연결하는 게임 기반'으로 넘어가는 단계다.

이미 추가된 핵심 기반:
- Main/Repeatable Quest 데이터 분리
- QuestEngine
- StoryEngine
- EventBus
- GameEventBridge
- DialogueResolver 확장
- 독립 Test Lab
- 통합 Workbench 구조

아직 가장 중요한 미완성 부분:
- 실제 BattleEngine/DungeonRun의 EventBus emit 연결
- 실제 UI 이벤트 반영
- Quest/Story UI
- 2단계 전투 로그
- DPS/Loot 실시간 화면
- 실제 Dungeon Unlock
- 반복 퀘스트 reset 스케줄
- 콘텐츠 Registry의 일관된 데이터 계약

따라서 다음 개발자는 **이벤트 payload 표준화 → 실제 엔진 연결 → UI 반영 → Test Lab 확장** 순서로 이어가는 것이 가장 안전하다.
