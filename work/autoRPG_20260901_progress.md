# autoRPG_20260901 진행도 및 개발 방향 보강

작성일: 2026-09-01
기준 브랜치: `work/dungeon-run`
기준 저장소: `g9inlife-blip/autoRPG`

> 이 문서는 `autoRPG_20260901.md`의 기존 설계를 훼손하지 않고, 2026-09-01 현재 실제 구현 진행 상황과 이후 개발 방향을 기록하기 위한 보강 문서다.

## 1. 현재까지 구현된 범위

### 1.1 개발 영역

원본 소스를 직접 변경하지 않고 `work/` 영역을 새 게임 개발 영역으로 사용한다.

```text
work/
├─ index.html
├─ css/
│  ├─ base.css
│  └─ dungeon.css
├─ src/
│  ├─ core/
│  ├─ battle/
│  │  ├─ Character.js
│  │  ├─ DamageCalculator.js
│  │  └─ BattleEngine.js
│  └─ world/
│     └─ DungeonRun.js
└─ data/
   ├─ dungeon-registry.js
   ├─ dungeons.js              # 기존 호환 진입점
   └─ dungeons/
      ├─ ancient-forest.js
      ├─ goblin-cave.js
      └─ ancient-ruins.js
```

### 1.2 Dungeon 데이터 분리

던전 콘텐츠와 실행 상태를 분리했다.

```text
Dungeon       = 콘텐츠 정의
DungeonRun    = 실제 탐험 세션
```

현재 데이터 던전:

- `dungeon.ancient_forest` — 고대의 숲, 10층
- `dungeon.goblin_cave` — 고블린 동굴, 6층
- `dungeon.ancient_ruins` — 고대 유적, 8층

새 던전은 `work/data/dungeons/`에 데이터 파일을 추가하는 방식으로 확장한다.

### 1.3 DungeonRun

현재 탐험 세션에서 다음 정보를 관리한다.

- runId
- dungeon
- party
- 현재 층
- 탐험 활성 상태
- 완료 상태
- 골드
- 경험치
- 획득 Loot
- 탐험 이벤트
- 캐릭터별 누적 전투 통계

탐험 흐름은 다음과 같다.

```text
탐험 시작
 ↓
층 진입
 ↓
Encounter
 ├─ Battle
 └─ Treasure
 ↓
전투 통계 / Loot 기록
 ↓
다음 층
 ↓
Boss
 ↓
탐험 완료
```

### 1.4 전투 통계

현재 DungeonRun에 캐릭터별 누적 전투 데이터를 연결했다.

```text
damageDealt
damageTaken
attacks
hits
criticals
misses
kills
```

전투 시간 데이터를 기반으로 누적 DPS를 표시하는 방향으로 연결했다.

앞으로 다음 항목까지 확장한다.

```text
healingDone
skillsUsed
deaths
damageBySkill
damageByTarget
maxDps
```

### 1.5 HP 0 / 전투불능 이벤트

전투 중 대상의 HP가 0이 되면 `DEFEAT` 이벤트를 생성한다.

```text
DEFEAT
├─ characterId
├─ character
└─ defeatedBy
```

이를 이용해 전투 요약 로그를 다음처럼 생성한다.

```text
[4층] 고블린 2마리와 전투
(고블린 궁수 기절, 고블린 기절)
```

향후 `DEFEAT`를 범용 상태 이벤트로 확장하여 다음을 지원한다.

- 기절
- 사망
- 석화
- 전투불능
- 부활

### 1.6 로그 2단계 구조

로그는 요약과 상세를 분리한다.

```text
SummaryLog
    ↓ 클릭
DetailLog
```

예:

```text
[4층] 고블린 2마리와 전투 (고블린 궁수 기절)
```

클릭 시:

```text
전투 상세
- 기사 → 고블린 83 피해
- 마법사 → 고블린 궁수 142 피해
- 고블린 궁수 기절
- 기사 → 고블린 91 피해
- 전투 승리
```

### 1.7 전투 대사 시스템 방향

대사는 전투 코드에 직접 작성하지 않고 데이터 기반으로 관리한다.

현재 액션 분류:

```text
battle_start
attack
critical
miss
ally_defeat
enemy_defeat
victory
defeat
```

기본 대사 외에 향후 다음 우선순위를 사용한다.

```text
Event-specific
   ↓
Character Pair / Bond
   ↓
Personality
   ↓
Default
```

### 1.8 성격 / 유대도

캐릭터 데이터에 다음 개념을 추가하는 방향으로 설계한다.

```text
Character
├─ personality
└─ bonds
   ├─ characterId
   └─ value 0~100
```

예:

```text
기사 + 마법사 / 유대도 85
기사: "뒤는 맡길게."
마법사: "당신이라면 믿어도 되겠지."
```

유대도가 낮으면 다른 대사를 선택할 수 있다.

```text
기사: "내 공격에 방해되지 마."
마법사: "명령하지 마. 알아서 할 테니까."
```

### 1.9 전투 액션별 대사

대사는 액션마다 새로 선택한다.

```text
일반 공격 → attack
크리티컬 → critical
빗나감 → miss
아군 전투불능 → ally_defeat
적 전투불능 → enemy_defeat
승리 → victory
패배 → defeat
```

향후 스킬/상태 시스템 추가 시 다음을 확장한다.

```text
skill
heal
buff
debuff
dodge
counter
combo
revive
boss_appear
```

## 2. 현재 개발 화면

현재 `work/index.html`은 로직 검증용 Dungeon Workbench 역할을 한다.

```text
던전 선택
Seed
탐험 시작
다음 진행
100회 전투
초기화
```

표시 영역:

```text
던전
파티
현재 전투
누적 통계
획득 아이템
탐험 로그
```

목표는 이 Workbench를 유지하면서 실제 게임 UI를 별도의 Presentation Layer로 발전시키는 것이다.

## 3. 이미지 리소스 적용 방향

저장소의 `image/` 리소스를 콘텐츠 ID와 연결한다.

```text
CharacterData
├─ id
├─ name
└─ image

MonsterData
├─ id
├─ name
└─ image
```

UI는 이미지 파일명을 직접 하드코딩하지 않고 데이터의 `image` 값을 참조한다.

이를 통해 향후 디자인 변경 시 데이터/이미지만 교체할 수 있도록 한다.

## 4. 앞으로의 핵심 아키텍처

```text
                 GameState
                     │
        ┌────────────┼────────────┐
        ▼            ▼            ▼
      Story        Quest        World
        │            │            │
        └──────┬─────┴──────┬─────┘
               ▼            ▼
           Condition      Unlock
               │            │
               └──────┬─────┘
                      ▼
                 DungeonRun
                      │
                 Encounter
                 ┌────┼────┐
                 ▼    ▼    ▼
              Battle Event Loot
                 │
                 ▼
          Action / Status / Death
                 │
                 ▼
          BattleEvent stream
                 │
        ┌────────┴─────────┐
        ▼                  ▼
   Statistics         DialogueResolver
        │                  │
        └────────┬─────────┘
                 ▼
              UI Layer
```

핵심 원칙:

> 게임 엔진은 규칙을 처리하고, 데이터는 콘텐츠를 정의하며, UI는 상태를 표현한다.

## 5. Story / Quest 확장 방향

### Story

스토리는 그래프 구조로 만든다.

```text
StoryNode
├─ id
├─ type
├─ speaker
├─ text
├─ conditions[]
├─ choices[]
└─ effects[]
```

### Choice

```text
Choice
├─ text
├─ condition
├─ next
└─ effects
```

### Quest

```text
Quest
├─ id
├─ type
├─ prerequisites
├─ objectives[]
├─ rewards
├─ nextQuest
└─ branch
```

### Story → Quest → Dungeon

```text
스토리 진행
 ↓
Flag 변경
 ↓
퀘스트 활성화
 ↓
던전 Unlock
 ↓
던전 탐험
 ↓
Objective 갱신
 ↓
스토리 다음 노드
```

## 6. 이벤트 시스템

전투뿐 아니라 스토리/던전/퀘스트에서 동일한 이벤트 모델을 사용한다.

```text
Condition
 ↓
Event
 ↓
Effect
```

조건 예:

- flag
- item 보유
- quest 상태
- dungeon ID
- floor
- character 상태
- bond
- story 진행도

효과 예:

- flag 변경
- 아이템 지급
- 골드 지급
- 경험치 지급
- 퀘스트 시작/완료
- 던전 unlock
- 스토리 이동
- 전투 시작
- 대사 이벤트 발생

## 7. 대사 Resolver 확장 설계

최종적으로 다음 입력을 받아 대사를 선택한다.

```text
DialogueContext
├─ action
├─ speaker
├─ target
├─ party
├─ bonds
├─ personality
├─ dungeonId
├─ floor
├─ enemyIds
├─ storyFlags
└─ eventFlags
```

선택 우선순위:

```text
1. 특정 이벤트 대사
2. 특정 캐릭터 조합 대사
3. 유대도 조건 대사
4. 성격 조건 대사
5. 던전/몬스터 특수 대사
6. 기본 대사
```

이를 통해 콘텐츠 팀은 전투 엔진을 수정하지 않고 대사 데이터를 추가할 수 있다.

## 8. 전투 화면 최종 목표

```text
┌──────────────────────────────────┐
│          고대의 숲 · 4층          │
│        탐험시간 00:12:43          │
├──────────────────────────────────┤
│                                  │
│ [기사 이미지]       [고블린 이미지]│
│ 기사                 고블린 ×2    │
│ HP ████████░░        HP ████░░░░  │
│ 누적 DPS 143.2       남은 HP 82   │
│                                  │
│            ⚔ 전투 중              │
├──────────────────────────────────┤
│ "내가 간다!"                      │
│ "좋아, 빈틈이 보여!"              │
├──────────────────────────────────┤
│ 기사    DPS 143.2   피해 1,432    │
│ 마법사  DPS 221.7   피해 2,217    │
├──────────────────────────────────┤
│ 획득 아이템                        │
│ 회복 물약 ×2                      │
│ 💰 380 Gold                       │
├──────────────────────────────────┤
│ [4층] 고블린 2마리와 전투          │
│       (고블린 궁수 기절)           │
└──────────────────────────────────┘
```

대사 영역은 2줄을 기본으로 유지하되, 실제 데이터는 여러 줄/여러 이벤트를 지원한다.

## 9. 테스트 우선 개발 원칙

개발환경 제약을 고려하여 Node/npm 등의 필수 의존성을 최소화하고 브라우저에서 `index.html`을 직접 실행할 수 있는 구조를 유지한다.

테스트 도구도 별도 페이지로 제공한다.

```text
work/test/
├─ battle-test.html
├─ dungeon-test.html
├─ dialogue-test.html
└─ story-test.html
```

테스트 항목:

- 동일 Seed 동일 결과
- 전투 승/패
- HP 0 / DEFEAT 이벤트
- DPS 누적
- Loot 누적
- 던전 층 진행
- 보스전
- Story 분기
- Quest 조건
- Bond 대사 선택
- Personality 대사 선택

## 10. 개발 우선순위

### Phase 1 — 현재

- DungeonRun 기반
- 다중 던전 데이터
- 전투 통계
- Loot
- HP 0 이벤트
- 기본 대사 데이터
- Workbench UI

### Phase 2 — 다음

- DialogueResolver 완성
- 전투 이벤트 → 대사 → UI 연결
- 2단계 전투 로그
- 전투불능 표시
- 실제 이미지 연결
- 실시간 전투 화면

### Phase 3

- 자동 탐험 타이머
- 실시간 DPS 갱신
- 현재 전투/전체 탐험 통계 분리
- 탐험 결과 화면
- 중단/재개
- 저장/복구

### Phase 4

- StoryGraph
- Condition / Effect
- QuestEngine
- Dungeon Unlock
- Story → Quest → Dungeon 연계

### Phase 5

- 캐릭터 Personality
- Character Bond
- 조건부 대사
- 이벤트성 대사
- 캐릭터 관계에 따른 대화 변화

### Phase 6

- 던전 대량 추가
- 콘텐츠 데이터 정리
- 회귀 테스트 자동화
- 밸런스 테스트
- UI 테마 교체 구조

## 11. 개발 시 지켜야 할 원칙

1. `main`에는 검증되지 않은 코드를 직접 넣지 않는다.
2. 새 기능은 `work/`에서 먼저 개발한다.
3. 콘텐츠 추가와 엔진 변경을 분리한다.
4. UI와 게임 로직을 분리한다.
5. 랜덤 판정은 SeededRandom을 통해서만 처리한다.
6. 전투 이벤트는 UI가 아니라 이벤트 스트림을 기준으로 만든다.
7. 대사는 DialogueResolver가 선택한다.
8. Story/Quest/Dungeon은 데이터 중심으로 확장한다.
9. 기존 기능을 깨지 않도록 호환 진입점을 유지한다.
10. 큰 변경은 기능별 커밋으로 나눈다.

## 12. 현재 상태 요약

```text
원본 autoRPG 분석
      ↓ 완료
새 게임 시스템 설계
      ↓ 완료
work 개발영역
      ↓ 완료
DungeonRun
      ↓ 구현 진행
다중 던전 데이터
      ↓ 구현
전투 통계 / Loot
      ↓ 구현
HP 0 / DEFEAT 이벤트
      ↓ 구현
기본 전투 대사
      ↓ 구현
Personality / Bond 대사 기반
      ↓ 설계 진행
DialogueResolver
      ↓ 다음 핵심 작업
실시간 전투 UI
      ↓ 다음 작업
Story / Quest / Unlock
      ↓ 이후
확장형 콘텐츠 시스템
```

## 13. 다음 즉시 작업

다음 구현 단위는 다음 순서로 진행한다.

```text
1. DialogueResolver 실제 연결
2. BattleEngine 이벤트를 DialogueContext로 변환
3. 액션마다 대사 2줄 갱신
4. DEFEAT 이벤트를 요약 로그에 반영
5. 요약 로그 클릭 → 상세 팝업
6. 실제 캐릭터/몬스터 이미지 연결
7. 현재 전투 UI 완성
8. 실시간 DPS / HP 갱신
9. 자동 탐험 루프
10. Story / Quest 시스템 연결
```

이후 모든 콘텐츠는 가능하면 `data` 추가만으로 확장할 수 있도록 유지한다.
