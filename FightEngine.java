package com.shirobakama.autorpg2.adventure;

import android.content.Context;
import com.shirobakama.autorpg2.adventure.ActionEvaluation;
import com.shirobakama.autorpg2.adventure.Thrower;
import com.shirobakama.autorpg2.adventure.TrapEngine;
import com.shirobakama.autorpg2.entity.AdvancedTactics;
import com.shirobakama.autorpg2.entity.AdventureContext;
import com.shirobakama.autorpg2.entity.AttrType;
import com.shirobakama.autorpg2.entity.CommonLog;
import com.shirobakama.autorpg2.entity.Decision;
import com.shirobakama.autorpg2.entity.Enchant;
import com.shirobakama.autorpg2.entity.Enemy;
import com.shirobakama.autorpg2.entity.FightContext;
import com.shirobakama.autorpg2.entity.FightingLog;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.GameFlag;
import com.shirobakama.autorpg2.entity.Inventory;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.autorpg2.entity.ItemDrop;
import com.shirobakama.autorpg2.entity.LogEnemy;
import com.shirobakama.autorpg2.entity.Monster;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.entity.Skill;
import com.shirobakama.autorpg2.entity.Tactics;
import com.shirobakama.autorpg2.repo.ItemRepository;
import com.shirobakama.autorpg2.repo.SkillDb;
import com.shirobakama.autorpg2.repo.SkillRepository;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class FightEngine {
    private static final int ATTACKER_HIT_DICE_ADJUST = 3;
    private static final int ATTACKER_HIT_SLEEPING_BONUS = 8;
    private static final int MAX_ROUND = 100;
    private static final int MULTI_ATTACK_ADJUST_PER_TIMES = 1;
    private static final int MULTI_ATTACK_DICE_STEP = 2;
    private static final int MULTI_ATTACK_HIT_DICE_ADJUST = -1;
    private static final int RUNNER_BONUS = 4;
    private static final int SLEEP_TARGET_DECREMENT_PER_ROUND = 1;
    private static final int SURPRISE_ENEMY_BONUS = 0;
    private static final int SURPRISE_EXPECTED_BONUS = 0;
    private static final int SURPRISE_PLAYER_PENALTY = -4;
    protected static final String TAG = "fight-engn";
    private AdventureContext mAdv;
    private boolean mByAdvancedTactics;
    private Context mContext;
    private List<Enemy> mEnemies;
    private FightContext mFightContext;
    private GameContext mGame;
    private List<CommonLog.LogChar> mLastLogChars;
    private List<LogEnemy> mLastLogEnemies;
    private List<FightingLog> mLogs;
    private Random mRandom;
    private String mSentenceSeparator;
    private Thrower mThrower;
    private String mTitlePrefixByAdvancedTactics;
    private static final IdHolder ID_FLOG_DESC_ATTACK_FAIL_ARMOR = new IdHolder(C0380R.string.flog_desc_player_attack_fail_armor, C0380R.string.flog_desc_enemy_attack_fail_armor);
    private static final IdHolder ID_FLOG_DESC_ATTACK_FAIL_DODGE = new IdHolder(C0380R.string.flog_desc_player_attack_fail_dodge, C0380R.string.flog_desc_enemy_attack_fail_dodge);
    private static final IdHolder ID_FLOG_DESC_ATTACK_FAIL_SHILED = new IdHolder(C0380R.string.flog_desc_player_attack_fail_shiled, C0380R.string.flog_desc_enemy_attack_fail_shiled);
    private static final IdHolder ID_FLOG_DESC_ATTACK_HIT = new IdHolder(C0380R.string.flog_desc_player_attack_hit, C0380R.string.flog_desc_enemy_attack_hit);
    private static final IdHolder ID_FLOG_DESC_ATTACK_HIT_CRITICAL = new IdHolder(C0380R.string.flog_desc_player_attack_hit_critical, C0380R.string.flog_desc_enemy_attack_hit_critical);
    private static final IdHolder ID_FLOG_DESC_ATTACK_HIT_SHIELD = new IdHolder(C0380R.string.flog_desc_player_attack_hit_shield, C0380R.string.flog_desc_enemy_attack_hit_shield);
    private static final IdHolder ID_FLOG_DESC_ATTACK_HIT_NO_DAMAGE = new IdHolder(C0380R.string.flog_desc_player_attack_hit_no_damage, C0380R.string.flog_desc_enemy_attack_hit_no_damage);
    private static final IdHolder ID_FLOG_DESC_ATTACK_MULTI = new IdHolder(C0380R.string.flog_desc_player_attack_multi, C0380R.string.flog_desc_enemy_attack_multi);
    private static final IdHolder ID_FLOG_DESC_ATTACK_MULTI_FAIL_ARMOR = new IdHolder(C0380R.string.flog_desc_player_attack_multi_fail_armor, C0380R.string.flog_desc_enemy_attack_multi_fail_armor);
    private static final IdHolder ID_FLOG_DESC_ATTACK_MULTI_FAIL_DODGE = new IdHolder(C0380R.string.flog_desc_player_attack_multi_fail_dodge, C0380R.string.flog_desc_enemy_attack_multi_fail_dodge);
    private static final IdHolder ID_FLOG_DESC_ATTACK_MULTI_FAIL_SHILED = new IdHolder(C0380R.string.flog_desc_player_attack_multi_fail_shiled, C0380R.string.flog_desc_enemy_attack_multi_fail_shiled);
    private static final IdHolder ID_FLOG_DESC_ATTACK_MULTI_HIT = new IdHolder(C0380R.string.flog_desc_player_attack_multi_hit, C0380R.string.flog_desc_enemy_attack_multi_hit);
    private static final IdHolder ID_FLOG_DESC_ATTACK_MULTI_HIT_CRITICAL = new IdHolder(C0380R.string.flog_desc_player_attack_multi_hit_critical, C0380R.string.flog_desc_enemy_attack_multi_hit_critical);
    private static final IdHolder ID_FLOG_DESC_ATTACK_MULTI_HIT_SHIELD = new IdHolder(C0380R.string.flog_desc_player_attack_multi_hit_shield, C0380R.string.flog_desc_enemy_attack_multi_hit_shield);
    private static final IdHolder ID_FLOG_DESC_ATTACK_MULTI_HIT_NO_DAMAGE = new IdHolder(C0380R.string.flog_desc_player_attack_multi_hit_no_damage, C0380R.string.flog_desc_enemy_attack_multi_hit_no_damage);
    private static final IdHolder ID_FLOG_TITLE_ATTACK = new IdHolder(C0380R.string.flog_title_player_attack, C0380R.string.flog_title_enemy_attack);
    private static final IdHolder ID_FLOG_TITLE_ATTACK_CRITICAL = new IdHolder(C0380R.string.flog_title_player_attack_critical, C0380R.string.flog_title_enemy_attack_critical);
    private static final IdHolder ID_FLOG_TITLE_ATTACK_SKILL = new IdHolder(C0380R.string.flog_title_player_attack_skill, C0380R.string.flog_title_enemy_attack_skill);
    private static final IdHolder ID_FLOG_TITLE_DIED = new IdHolder(C0380R.string.flog_title_player_died, C0380R.string.flog_title_enemy_died);
    private static final IdHolder ID_FLOG_TITLE_NONE = new IdHolder(C0380R.string.flog_title_player_none, C0380R.string.flog_title_enemy_none);
    private static final IdHolder ID_FLOG_TITLE_RESIST_SLEEP_FAIL = new IdHolder(C0380R.string.flog_title_player_resist_sleep_fail, C0380R.string.flog_title_enemy_resist_sleep_fail);
    private static final IdHolder ID_FLOG_TITLE_RESIST_SLEEP_SUCCESS = new IdHolder(C0380R.string.flog_title_player_resist_sleep_success, C0380R.string.flog_title_enemy_resist_sleep_success);
    private static final IdHolder ID_FLOG_TITLE_SKILL_MAGIC = new IdHolder(C0380R.string.flog_title_player_skill_magic, C0380R.string.flog_title_enemy_skill_magic);
    private static final IdHolder ID_FLOG_TITLE_SKILL_NORMAL = new IdHolder(C0380R.string.flog_title_player_skill_normal, C0380R.string.flog_title_enemy_skill_normal);
    private static final IdHolder ID_FLOG_DESC_SKILL_STATUS_GROUP_ADD = new IdHolder(C0380R.string.flog_desc_skill_status_party_add, C0380R.string.flog_desc_skill_status_enemies_add);
    private static final IdHolder ID_FLOG_TITLE_WAKE_UP = new IdHolder(C0380R.string.flog_title_player_wake_up, C0380R.string.flog_title_enemy_wake_up);
    private static final IdHolder ID_FLOG_DESC_BACKSTUB_SUCCESS = new IdHolder(C0380R.string.flog_desc_player_backstub_success, C0380R.string.flog_desc_enemy_backstub_success);
    private static final IdHolder ID_FLOG_DESC_BACKSTUB_FAIL = new IdHolder(C0380R.string.flog_desc_player_backstub_fail, C0380R.string.flog_desc_enemy_backstub_fail);

    public enum FightResult {
        WIN,
        WIN_GOT_ITEM,
        LOSE,
        RUN
    }

    private void processRoundStart(int i) {
    }

    public List<FightingLog> getLogs() {
        return this.mLogs;
    }

    public FightEngine(Context context, GameContext gameContext, AdventureContext adventureContext, List<Monster> list, Calendar calendar, Random random, boolean z, Boolean bool, boolean z2) {
        this.mFightContext = new FightContext(list.size(), random);
        this.mContext = context;
        this.mGame = gameContext;
        this.mRandom = random;
        this.mAdv = adventureContext;
        FightContext fightContext = this.mFightContext;
        fightContext.monsters = list;
        fightContext.isWandering = z;
        fightContext.isExpected = bool;
        fightContext.isEvent = z2;
        this.mThrower = new Thrower(this.mRandom);
        gameContext.fightContext = this.mFightContext;
        this.mTitlePrefixByAdvancedTactics = this.mContext.getString(C0380R.string.flog_title_prefix_by_advanced_tactics);
        this.mSentenceSeparator = this.mContext.getString(C0380R.string.res_sentence_separator);
    }

    public FightResult execute() {
        this.mLogs = new ArrayList();
        processStart();
        if (!this.mFightContext.isEvent) {
            checkSurpriseAttack();
        }
        this.mFightContext.round = 1;
        while (true) {
            if (!processRound(this.mFightContext.round)) {
                break;
            }
            tickEnchants();
            this.mFightContext.round++;
            if (this.mFightContext.round > 100) {
                processMaxRoundExceeds();
                break;
            }
        }
        FightResult fightResultProcessEnd = processEnd();
        this.mGame.fightContext = null;
        return fightResultProcessEnd;
    }

    private void checkSurpriseAttack() {
        int i;
        int aliveCharCount = this.mGame.getAliveCharCount();
        int i2 = 0;
        for (int i3 = 0; i3 < 3; i3++) {
            for (Enchant enchant : this.mAdv.enchantsPlayers[i3]) {
                if (enchant.otherEffect == Enchant.OtherEffect.DETECT) {
                    i2 += enchant.value;
                }
            }
        }
        if (this.mFightContext.isExpected != null) {
            if (this.mFightContext.isExpected.booleanValue()) {
                if (EngineUtil.groupThrow(this.mRandom, this.mThrower, GameChar.Attribute.INT, this.mGame.characters, this.mEnemies, 0, false) >= aliveCharCount) {
                    this.mFightContext.playerSurprise = true;
                }
            } else {
                if (EngineUtil.groupThrow(this.mRandom, this.mThrower, GameChar.Attribute.INT, this.mEnemies, this.mGame.characters, 0 - i2, false) >= (this.mEnemies.size() + 1) / 2) {
                    this.mFightContext.enemySurprise = true;
                }
                if (!this.mFightContext.enemySurprise) {
                    addLogForField(true, null, CommonLog.LogType.SURPRISE, this.mContext.getString(C0380R.string.flog_title_enemy_nest_not_surprise), this.mContext.getString(C0380R.string.flog_desc_enemy_nest_not_surprise));
                    return;
                }
            }
        } else {
            if (EngineUtil.groupThrow(this.mRandom, this.mThrower, GameChar.Attribute.INT, this.mGame.characters, this.mEnemies, -4, false) >= aliveCharCount) {
                this.mFightContext.playerSurprise = true;
            }
            if (EngineUtil.groupThrow(this.mRandom, this.mThrower, GameChar.Attribute.INT, this.mEnemies, this.mGame.characters, 0 - i2, false) >= this.mEnemies.size()) {
                this.mFightContext.enemySurprise = true;
            }
            if (this.mFightContext.playerSurprise && this.mFightContext.enemySurprise) {
                FightContext fightContext = this.mFightContext;
                fightContext.playerSurprise = false;
                fightContext.enemySurprise = false;
            }
        }
        if (this.mFightContext.playerSurprise || this.mFightContext.enemySurprise) {
            CommonLog.LogType logType = CommonLog.LogType.SURPRISE;
            String string = this.mContext.getString(this.mFightContext.playerSurprise ? C0380R.string.flog_title_party_surprise : C0380R.string.flog_title_enemy_surprise);
            if (this.mFightContext.playerSurprise) {
                i = (this.mFightContext.isExpected == null || !this.mFightContext.isExpected.booleanValue()) ? C0380R.string.flog_desc_party_surprise_wandering : C0380R.string.flog_desc_party_surprise_expect;
            } else {
                i = (this.mFightContext.isExpected == null || this.mFightContext.isExpected.booleanValue()) ? C0380R.string.flog_desc_enemy_surprise_wandering : C0380R.string.flog_desc_enemy_surprise_expect;
            }
            addLogForField(this.mFightContext.playerSurprise, null, logType, string, this.mContext.getString(i));
        }
    }

    private void tickEnchants() {
        tickEnchants(this.mFightContext.enchantsField, true);
        tickEnchants(this.mFightContext.enchantsParty, true);
        tickEnchants(this.mFightContext.enchantsEnemyParty, false);
        for (int i = 0; i < this.mGame.characters.size(); i++) {
            if (this.mGame.characters.get(i).isAlive()) {
                tickEnchants(this.mFightContext.enchantsPlayers[i], true);
            }
        }
        for (int i2 = 0; i2 < this.mEnemies.size(); i2++) {
            if (this.mEnemies.get(i2).isAlive()) {
                tickEnchants(this.mFightContext.enchantsEnemies[i2], false);
            }
        }
    }

    private void tickEnchants(List<Enchant> list, boolean z) {
        String strName;
        HashSet hashSet = new HashSet(2);
        Iterator<Enchant> it = list.iterator();
        while (it.hasNext()) {
            Enchant next = it.next();
            if (next.term >= 0) {
                next.term--;
                if (next.term <= 0) {
                    if (next.causeSkillId != 0) {
                        strName = SkillRepository.getSkill(this.mContext, next.causeSkillId).name;
                    } else if (next.attribute != null) {
                        strName = next.attribute.name();
                    } else if (next.illegalStatus != null) {
                        strName = next.illegalStatus.getAsString(this.mContext);
                    } else {
                        strName = next.status != null ? next.status.name() : "??";
                    }
                    hashSet.add(strName);
                    it.remove();
                }
            }
        }
        Iterator it2 = hashSet.iterator();
        while (it2.hasNext()) {
            addLogForChar(z, null, CommonLog.LogType.LOSE_ENCHANT, this.mContext.getString(C0380R.string.flog_title_enchant_vanish, (String) it2.next()), null, z, (GameChar) null);
        }
    }

    private void processStart() {
        this.mEnemies = new ArrayList();
        Iterator<Monster> it = this.mFightContext.monsters.iterator();
        int i = 0;
        while (it.hasNext()) {
            Enemy enemy = new Enemy(i, it.next(), this.mContext.getString(C0380R.string.res_sentence_separator) + Enemy.createSuffix(i));
            i++;
            this.mEnemies.add(enemy);
        }
        FightContext fightContext = this.mFightContext;
        fightContext.enemies = this.mEnemies;
        Monster monster = fightContext.monsters.get(0);
        String string = this.mContext.getString(monster.type.getNumberStrId(), Integer.valueOf(this.mFightContext.monsters.size()));
        String str = monster.name;
        if (this.mAdv.extraLevel > 0) {
            str = str + "(+" + this.mAdv.extraLevel + ")";
        }
        processFlags(Decision.Timing.FIGHTING_START_FIGHT);
        addLogForField(true, null, CommonLog.LogType.FIGHT_ENCOUNTER, this.mContext.getString(C0380R.string.flog_title_encounter, str, string), this.mContext.getString(C0380R.string.flog_desc_encounter_1, string, Integer.valueOf(this.mEnemies.get(0).level)));
        this.mGame.getOrCreateFlag(GameFlag.Key.asMonsterEnconter(monster)).addOptionAsInt(1);
        Iterator<PlayerChar> it2 = this.mGame.characters.iterator();
        while (it2.hasNext()) {
            it2.next().illegalStatus.clear();
        }
    }

    private void processFlags(Decision.Timing timing) {
        LinkedList<FightingLog> linkedList = new LinkedList();
        FlagEngine.processInFighting(this.mContext, this.mRandom, this.mGame, timing, linkedList);
        for (FightingLog fightingLog : linkedList) {
            addInfoToFightingLog(fightingLog);
            addLog(fightingLog);
        }
    }

    private FightingLog addInfoToFightingLog(FightingLog fightingLog) {
        fightingLog.setLogCharacters(this.mGame.characters);
        fightingLog.setLogEnemies(this.mEnemies);
        return fightingLog;
    }

    private boolean processRound(int i) {
        Integer numValueOf;
        Integer numValueOf2;
        processRoundStart(i);
        TreeMap treeMap = new TreeMap();
        boolean z = i > 1 || !this.mFightContext.enemySurprise;
        boolean z2 = i > 1 || !this.mFightContext.playerSurprise;
        int i2 = 0;
        int i3 = 0;
        while (i2 < this.mGame.characters.size()) {
            PlayerChar playerChar = this.mGame.characters.get(i2);
            if (playerChar.isAlive()) {
                i3++;
                playerChar.calcStatusInFight(this.mContext, this.mGame, this.mFightContext, i2);
                playerChar.isForward = i2 <= 1 || i3 <= 2;
                if (z) {
                    do {
                        numValueOf2 = Integer.valueOf(evalOrder(this.mGame.characters.get(i2)));
                    } while (treeMap.containsKey(numValueOf2));
                    treeMap.put(numValueOf2, Integer.valueOf(i2 + 1));
                }
            }
            i2++;
        }
        for (int i4 = 0; i4 < this.mEnemies.size(); i4++) {
            Enemy enemy = this.mEnemies.get(i4);
            if (enemy.isAlive()) {
                enemy.calcStatusInFight(this.mContext, this.mGame, this.mFightContext, i4);
                if (z2) {
                    do {
                        numValueOf = Integer.valueOf(evalOrder(this.mEnemies.get(i4)));
                    } while (treeMap.containsKey(numValueOf));
                    treeMap.put(numValueOf, Integer.valueOf(-(i4 + 1)));
                }
            }
        }
        if (i == 1 && !this.mFightContext.enemySurprise && !this.mFightContext.playerSurprise) {
            processBackStub();
        }
        Iterator it = treeMap.values().iterator();
        while (it.hasNext()) {
            int iIntValue = ((Integer) it.next()).intValue();
            if (iIntValue < 0) {
                Enemy enemy2 = this.mEnemies.get((-iIntValue) - 1);
                if (!enemy2.running && enemy2.isAlive()) {
                    processEnemy(enemy2);
                }
            } else {
                PlayerChar playerChar2 = this.mGame.characters.get(iIntValue - 1);
                if (playerChar2.isAlive()) {
                    processPlayer(playerChar2);
                }
            }
            if (this.mFightContext.playerRunning || this.mGame.areAllCharsDied() || areAllEnemiesDiedOrRunning()) {
                return false;
            }
        }
        return true;
    }

    private void processBackStub() {
        processBackStubCharacters(null, this.mGame.characters, this.mEnemies);
        processBackStubCharacters(Tactics.TacticsValue.AGGRESSIVE, this.mEnemies, this.mGame.characters);
    }

    private void processBackStubCharacters(Tactics.TacticsValue tacticsValue, List<? extends GameChar> list, List<? extends GameChar> list2) {
        Skill availableSkill;
        Tactics.TacticsValue tacticsValue2;
        for (GameChar gameChar : list) {
            if (gameChar.isAlive() && (availableSkill = gameChar.getAvailableSkill(this.mContext, SkillDb.SKILL_ROGUE_BACK_STUB)) != null && availableSkill.canUse(gameChar)) {
                this.mByAdvancedTactics = false;
                if (tacticsValue == null) {
                    this.mByAdvancedTactics = AdvancedTactics.TacticsComposition.matchBackStub(this.mAdv.advancedFightTacticsForChar[gameChar.index], (PlayerChar) gameChar, this.mGame, this.mAdv, this.mFightContext);
                }
                if (!this.mByAdvancedTactics) {
                    if (tacticsValue == null) {
                        tacticsValue2 = this.mAdv.getCurrentTacticsForChar(gameChar.index).attackSkill;
                        if (tacticsValue2 == Tactics.TacticsValue.NONE) {
                            continue;
                        }
                    } else {
                        tacticsValue2 = tacticsValue;
                    }
                    if (gameChar.f94mp >= gameChar.maxMp / 2 || tacticsValue2 != Tactics.TacticsValue.MODERATE) {
                        if (gameChar.f94mp >= (gameChar.maxMp * 3) / 4 || tacticsValue2 != Tactics.TacticsValue.CONSERVATIVE) {
                        }
                    }
                }
                ArrayList arrayList = new ArrayList(list2.size());
                for (GameChar gameChar2 : list2) {
                    if (gameChar2.isAlive()) {
                        arrayList.add(gameChar2);
                    }
                }
                if (arrayList.isEmpty()) {
                    return;
                }
                gameChar.f94mp -= availableSkill.f108mp;
                GameChar gameChar3 = (GameChar) EngineUtil.getMemberRandom(arrayList, this.mRandom);
                Thrower.ThrowResult throwResultAttributeThrow = this.mThrower.attributeThrow(gameChar, GameChar.Attribute.INT, availableSkill.clazz);
                Thrower.ThrowResult throwResultAttributeThrow2 = this.mThrower.attributeThrow(gameChar3, GameChar.Attribute.INT, gameChar3.fightingSubClass);
                boolean z = throwResultAttributeThrow.critical || !throwResultAttributeThrow2.critical || !throwResultAttributeThrow.fumble || throwResultAttributeThrow2.fumble || throwResultAttributeThrow.value >= throwResultAttributeThrow2.value;
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(this.mAdv.getSkillUseAwareCustomized(this.mContext, gameChar, availableSkill));
                stringBuffer.append(this.mSentenceSeparator);
                if (z) {
                    stringBuffer.append(this.mContext.getString(ID_FLOG_DESC_BACKSTUB_SUCCESS.get(gameChar), gameChar3.name));
                } else {
                    stringBuffer.append(this.mContext.getString(ID_FLOG_DESC_BACKSTUB_FAIL.get(gameChar), gameChar3.name));
                }
                createAndAddSkillLog(gameChar, !gameChar.isPlayer(), gameChar3, null, availableSkill, stringBuffer.toString());
                if (z) {
                    processPhysicalAttack(gameChar, availableSkill, gameChar3);
                }
            }
        }
    }

    private boolean areAllEnemiesDiedOrRunning() {
        Iterator<Enemy> it = this.mEnemies.iterator();
        while (it.hasNext()) {
            if (it.next().isAlive()) {
                return false;
            }
        }
        return true;
    }

    private int evalOrder(GameChar gameChar) {
        return -((gameChar.agi * 100) + this.mRandom.nextInt(100));
    }

    private void processPlayer(PlayerChar playerChar) {
        PlayerActionEngine playerActionEngine = new PlayerActionEngine(this.mContext, this, this.mRandom, this.mAdv.getCurrentTacticsForChar(playerChar.index), playerChar, this.mGame.inventories);
        playerActionEngine.setParties(this.mGame.characters, this.mEnemies).setFightContext(this.mFightContext);
        FightActionEvaluation fightActionEvaluationAction = playerActionEngine.action();
        this.mByAdvancedTactics = fightActionEvaluationAction != null ? fightActionEvaluationAction.byAdvancedTactics() : false;
        if (fightActionEvaluationAction == null || fightActionEvaluationAction.action == ActionEvaluation.Action.NONE) {
            processNone(playerChar);
        } else if (fightActionEvaluationAction.action == ActionEvaluation.Action.ATTACK) {
            processPhysicalAttack(playerChar, null, fightActionEvaluationAction.targetChar);
        } else if (fightActionEvaluationAction.baseActions != null) {
            processSkillAddAttack(playerChar, fightActionEvaluationAction);
        } else if (fightActionEvaluationAction.action == ActionEvaluation.Action.SKILL_MAGIC) {
            processSkill(playerChar, fightActionEvaluationAction);
        } else if (fightActionEvaluationAction.action == ActionEvaluation.Action.USE_ITEM) {
            processPlayerItem(playerChar, fightActionEvaluationAction);
        } else if (fightActionEvaluationAction.action == ActionEvaluation.Action.EQUIP_ITEM) {
            processPlayerEquip(playerChar, fightActionEvaluationAction);
        } else if (fightActionEvaluationAction.action == ActionEvaluation.Action.RUNNING) {
            processPlayerRun(playerChar, fightActionEvaluationAction);
        } else {
            processNone(playerChar);
        }
        this.mByAdvancedTactics = false;
    }

    private void processNone(GameChar gameChar) {
        int i;
        Enchant specifiedIllegalState = gameChar.getSpecifiedIllegalState(GameChar.IllegalStatus.SLEEP);
        if (specifiedIllegalState != null) {
            processResistEnchant(gameChar, specifiedIllegalState);
            return;
        }
        if (gameChar.isPlayer() && gameChar.f94mp < gameChar.maxMp) {
            PlayerChar playerChar = (PlayerChar) gameChar;
            if (((playerChar.getSubLevel(GameChar.SubClass.SORCERER) > 0 || playerChar.getSubLevel(GameChar.SubClass.CLERIC) > 0) & ((playerChar.getWeapon() == null || playerChar.getWeapon().adjustments == null) ? false : true)) && (i = playerChar.getWeapon().adjustments.get(GameChar.Status.MAGIC_BONUS)) > 0) {
                processMeditation(playerChar, i);
                return;
            }
        }
        addLogForChar(gameChar.isPlayer(), gameChar, CommonLog.LogType.FIGHT_NONE, this.mContext.getString(ID_FLOG_TITLE_NONE.get(gameChar), gameChar.name), null, gameChar.isPlayer(), gameChar);
    }

    private void processMeditation(PlayerChar playerChar, int i) {
        String string;
        Thrower.ThrowResult throwResultGenericThrow = this.mThrower.genericThrow(playerChar, GameChar.Attribute.INT);
        throwResultGenericThrow.value += i;
        Thrower.ThrowResult throwResultGenericThrow2 = this.mThrower.genericThrow(this.mEnemies.get(0), GameChar.Attribute.VIT);
        if (throwResultGenericThrow.critical || throwResultGenericThrow2.fumble || throwResultGenericThrow.value >= throwResultGenericThrow2.value) {
            int iMax = (Math.max(playerChar.getSubLevel(GameChar.SubClass.SORCERER), playerChar.getSubLevel(GameChar.SubClass.CLERIC)) / 7) + 1;
            int i2 = iMax / 3;
            if (this.mRandom.nextInt(3) < iMax % 3) {
                i2++;
            }
            int iThrowDice = ((this.mThrower.throwDice(i2, 4) + i) + EngineUtil.getAttrBonus(this.mRandom, playerChar.intl)) - 3;
            if (iThrowDice < 1) {
                iThrowDice = 1;
            }
            if (iThrowDice > playerChar.maxMp - playerChar.f94mp) {
                iThrowDice = playerChar.maxMp - playerChar.f94mp;
            }
            playerChar.f94mp += iThrowDice;
            string = this.mContext.getString(C0380R.string.flog_desc_player_meditation_success, playerChar.getWeapon().getName(this.mContext), playerChar.name, Integer.valueOf(iThrowDice));
        } else {
            string = this.mContext.getString(C0380R.string.flog_desc_player_meditation_fail, playerChar.getWeapon().getName(this.mContext));
        }
        addLogForChar(true, playerChar, CommonLog.LogType.UNDEFINED, this.mContext.getString(C0380R.string.flog_title_player_meditation, playerChar.name), string, true, playerChar);
    }

    private void processResistEnchant(GameChar gameChar, Enchant enchant) {
        int i;
        enchant.value--;
        Thrower.ThrowResult throwResultGenericThrow = this.mThrower.genericThrow(gameChar, GameChar.Attribute.INT);
        if (throwResultGenericThrow.critical || (!throwResultGenericThrow.fumble && throwResultGenericThrow.value >= enchant.value)) {
            gameChar.illegalStatus.remove(enchant);
            i = ID_FLOG_TITLE_RESIST_SLEEP_SUCCESS.get(gameChar);
        } else {
            i = ID_FLOG_TITLE_RESIST_SLEEP_FAIL.get(gameChar);
        }
        addLogForChar(gameChar.isPlayer(), gameChar, CommonLog.LogType.FIGHT_RESIST, this.mContext.getString(i, gameChar.name), null, gameChar.isPlayer(), gameChar);
    }

    private void addLogForChar(boolean z, GameChar gameChar, CommonLog.LogType logType, String str, String str2, boolean z2, GameChar gameChar2) {
        addLog(createFightingLog(z, logType, gameChar, str, str2, null, z2, gameChar2, null, null));
    }

    private void addLogForField(boolean z, GameChar gameChar, CommonLog.LogType logType, String str, String str2) {
        addLog(createFightingLog(z, logType, gameChar, str, str2, null, true, null, null, null));
    }

    private void addLogForCharArray(boolean z, GameChar gameChar, CommonLog.LogType logType, String str, String str2, boolean z2, GameChar[] gameCharArr) {
        addLog(createFightingLog(z, logType, gameChar, str, str2, null, z2, null, null, gameCharArr));
    }

    private void addLogForChars(boolean z, GameChar gameChar, CommonLog.LogType logType, String str, String str2, boolean z2, List<? extends GameChar> list) {
        addLog(createFightingLog(z, logType, gameChar, str, str2, null, z2, null, list, null));
    }

    private void processPlayerRun(PlayerChar playerChar, FightActionEvaluation fightActionEvaluation) {
        if (EngineUtil.groupThrow(this.mRandom, this.mThrower, GameChar.Attribute.AGI, this.mGame.getAliveChars(), getAliveEnemies(), 4, true) >= 1) {
            addLogForField(true, playerChar, CommonLog.LogType.FIGHT_RUN, this.mContext.getString(C0380R.string.flog_title_run, this.mFightContext.monsters.get(0).name), this.mContext.getString(C0380R.string.flog_desc_run));
            this.mFightContext.playerRunning = true;
        } else {
            addLogForField(true, playerChar, CommonLog.LogType.FIGHT_RUN_FAILED, this.mContext.getString(C0380R.string.flog_title_run_failed, this.mFightContext.monsters.get(0).name), this.mContext.getString(C0380R.string.flog_desc_run_failed));
        }
    }

    private List<? extends GameChar> getAliveEnemies() {
        ArrayList arrayList = new ArrayList(this.mEnemies.size());
        for (Enemy enemy : this.mEnemies) {
            if (enemy.isAlive()) {
                arrayList.add(enemy);
            }
        }
        return arrayList;
    }

    private void processPlayerItem(PlayerChar playerChar, FightActionEvaluation fightActionEvaluation) {
        Inventory inventory = this.mGame.getInventory(fightActionEvaluation.inventoryId);
        Item baseItem = inventory.getBaseItem(this.mContext);
        Item.Effect hpRestoreEffect = baseItem.getHpRestoreEffect();
        if (hpRestoreEffect != null) {
            processPlayerItemHpRestore(playerChar, inventory, hpRestoreEffect);
            return;
        }
        Item.Effect mpRestoreEffect = baseItem.getMpRestoreEffect();
        if (mpRestoreEffect != null) {
            processPlayerItemMpRestore(playerChar, inventory, mpRestoreEffect);
        } else {
            processNone(playerChar);
        }
    }

    private void processPlayerItemHpRestore(PlayerChar playerChar, Inventory inventory, Item.Effect effect) {
        Item baseItem = inventory.getBaseItem(this.mContext);
        int iThrowDiceWithBonus = this.mThrower.throwDiceWithBonus(baseItem.diceNum, baseItem.diceFace, playerChar.getAttr(effect.attr)) + baseItem.attrBase;
        if (iThrowDiceWithBonus > playerChar.maxHp - playerChar.f93hp) {
            iThrowDiceWithBonus = playerChar.maxHp - playerChar.f93hp;
        }
        playerChar.f93hp += iThrowDiceWithBonus;
        this.mGame.inventories.remove(inventory);
        addLogForChar(true, playerChar, CommonLog.LogType.USE_ITEM, this.mContext.getString(C0380R.string.flog_title_player_item, playerChar.name, inventory.getName(this.mContext)), this.mContext.getString(C0380R.string.flog_desc_player_item_hp_restore, Integer.valueOf(iThrowDiceWithBonus)), true, playerChar);
    }

    private void processPlayerItemMpRestore(PlayerChar playerChar, Inventory inventory, Item.Effect effect) {
        Item baseItem = inventory.getBaseItem(this.mContext);
        int iThrowDiceWithBonus = this.mThrower.throwDiceWithBonus(baseItem.diceNum, baseItem.diceFace, playerChar.getAttr(effect.attr)) + baseItem.attrBase;
        if (iThrowDiceWithBonus > playerChar.maxMp - playerChar.f94mp) {
            iThrowDiceWithBonus = playerChar.maxMp - playerChar.f94mp;
        }
        playerChar.f94mp += iThrowDiceWithBonus;
        this.mGame.inventories.remove(inventory);
        addLogForChar(true, playerChar, CommonLog.LogType.USE_ITEM, this.mContext.getString(C0380R.string.flog_title_player_item, playerChar.name, inventory.getName(this.mContext)), this.mContext.getString(C0380R.string.flog_desc_player_item_mp_restore, Integer.valueOf(iThrowDiceWithBonus)), true, playerChar);
    }

    private void processPlayerEquip(PlayerChar playerChar, FightActionEvaluation fightActionEvaluation) {
        Inventory inventory;
        Inventory inventory2;
        Inventory inventory3;
        Inventory inventory4 = this.mGame.getInventory(fightActionEvaluation.inventoryId);
        if ((inventory4.equippedCharId == 0 || inventory4.equippedCharId == playerChar.f106id) && inventory4.canEquip(this.mContext, playerChar, this.mGame.inventories)) {
            inventory4.equippedCharId = playerChar.f106id;
            Inventory inventory5 = null;
            switch (inventory4.getBaseItem(this.mContext).type) {
                case WEAPON:
                    Inventory weapon = playerChar.getWeapon();
                    playerChar.weaponId = inventory4.f98id;
                    if (!inventory4.getBaseItem(this.mContext).twoHanded) {
                        inventory = null;
                        inventory2 = null;
                        inventory5 = weapon;
                        inventory3 = null;
                        break;
                    } else {
                        inventory = this.mGame.getInventory(playerChar.shieldId);
                        if (inventory != null) {
                            playerChar.shieldId = 0;
                        }
                        inventory2 = null;
                        inventory5 = weapon;
                        inventory3 = null;
                        break;
                    }
                case ARMOR:
                    inventory3 = this.mGame.getInventory(playerChar.armorId);
                    playerChar.armorId = inventory4.f98id;
                    inventory = null;
                    inventory2 = null;
                    break;
                case SHIELD:
                    Inventory inventory6 = this.mGame.getInventory(playerChar.shieldId);
                    playerChar.shieldId = inventory4.f98id;
                    Inventory weapon2 = playerChar.getWeapon();
                    if (weapon2 != null && weapon2.getBaseItem(this.mContext).twoHanded) {
                        playerChar.weaponId = 0;
                        inventory2 = null;
                        inventory5 = weapon2;
                        inventory = inventory6;
                        inventory3 = null;
                        break;
                    } else {
                        inventory = inventory6;
                        inventory3 = null;
                        inventory2 = null;
                        break;
                    }
                case RING:
                    Inventory inventory7 = this.mGame.getInventory(playerChar.ringId);
                    playerChar.ringId = inventory4.f98id;
                    inventory2 = inventory7;
                    inventory3 = null;
                    inventory = null;
                    break;
                default:
                    inventory4.equippedCharId = 0;
                    inventory3 = null;
                    inventory = null;
                    inventory2 = null;
                    break;
            }
            if (inventory5 != null) {
                inventory5.equippedCharId = 0;
            }
            if (inventory3 != null) {
                inventory3.equippedCharId = 0;
            }
            if (inventory != null) {
                inventory.equippedCharId = 0;
            }
            if (inventory2 != null) {
                inventory2.equippedCharId = 0;
            }
            playerChar.calcStatusInFight(this.mContext, this.mGame, this.mFightContext, playerChar.index);
            CommonLog.LogType logType = CommonLog.LogType.USE_ITEM;
            Context context = this.mContext;
            String string = context.getString(C0380R.string.flog_title_equip_item, inventory4.getName(context));
            Context context2 = this.mContext;
            addLogForChar(true, playerChar, logType, string, context2.getString(C0380R.string.flog_desc_equip_item, inventory4.getName(context2), playerChar.name), true, playerChar);
            return;
        }
        processNone(playerChar);
    }

    private void processSkill(GameChar gameChar, FightActionEvaluation fightActionEvaluation) {
        Skill skill = SkillRepository.getSkill(this.mContext, fightActionEvaluation.skillId);
        switch (skill.type) {
            case ADD_ATTACK:
                throw new IllegalStateException("Add attack skill is illegally called.");
            case CURE:
                processSkillCure(gameChar, fightActionEvaluation.targetChar, skill);
                return;
            case CURE_ALL:
                processSkillCureAll(gameChar, skill);
                return;
            case DAMAGE:
                processSkillDamage(gameChar, fightActionEvaluation.targetChar, skill);
                return;
            case DAMAGE_ALL:
                processSkillDamageAll(gameChar, skill);
                return;
            case MY_STATUS:
                processSkillMyStatus(gameChar, skill);
                return;
            case OTHER:
                processSkillOther(gameChar, fightActionEvaluation.targetChar, skill);
                return;
            case STATUS:
                processSkillStatus(gameChar, fightActionEvaluation.targetChar, skill);
                return;
            case STATUS_ALL:
                processSkillStatusAll(gameChar, skill);
                return;
            default:
                return;
        }
    }

    private void processSkillCure(GameChar gameChar, GameChar gameChar2, Skill skill) {
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(gameChar2);
        processSkillCureCommon(gameChar, arrayList, skill);
    }

    private void processSkillCureAll(GameChar gameChar, Skill skill) {
        if (gameChar.isPlayer()) {
            processSkillCureCommon(gameChar, this.mGame.characters, skill);
        } else {
            processSkillCureCommon(gameChar, this.mEnemies, skill);
        }
    }

    private void processSkillCureCommon(GameChar gameChar, List<? extends GameChar> list, Skill skill) {
        createAndAddSkillLog(gameChar, gameChar.isPlayer(), null, list, skill, EngineUtil.processSkillCure(gameChar, list, skill, this.mThrower, this.mRandom, this.mAdv, this.mContext));
    }

    private void createAndAddSkillLog(GameChar gameChar, boolean z, GameChar gameChar2, List<? extends GameChar> list, Skill skill, String str) {
        String string;
        CommonLog.LogType logType = skill.isMagic() ? CommonLog.LogType.MAGIC : skill.isMonsters() ? CommonLog.LogType.FIGHT_ENEMY_BREATH : CommonLog.LogType.FIGHT_USE_SKILL;
        if (skill.clazz != null) {
            string = this.mContext.getString((skill.isMagic() ? ID_FLOG_TITLE_SKILL_MAGIC : ID_FLOG_TITLE_SKILL_NORMAL).get(gameChar), gameChar.name, this.mAdv.getSkillNameAwareCustomized(gameChar, skill));
        } else {
            string = this.mContext.getString(skill.descriptionStringId, gameChar.name);
        }
        if (gameChar2 == null) {
            addLogForChars(gameChar.isPlayer(), gameChar, logType, string, str, z, list);
        } else {
            addLogForChar(gameChar.isPlayer(), gameChar, logType, string, str, z, gameChar2);
        }
    }

    private void processSkillDamage(GameChar gameChar, GameChar gameChar2, Skill skill) {
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(gameChar2);
        processSkillDamageCommon(gameChar, arrayList, skill);
    }

    private void processSkillDamageAll(GameChar gameChar, Skill skill) {
        if (gameChar.isPlayer()) {
            processSkillDamageCommon(gameChar, this.mEnemies, skill);
        } else {
            processSkillDamageCommon(gameChar, this.mGame.characters, skill);
        }
    }

    private void processSkillDamageCommon(GameChar gameChar, List<? extends GameChar> list, Skill skill) {
        Thrower.ThrowResult throwResultGenericThrow;
        int iThrowDiceWithBonus;
        gameChar.f94mp -= skill.f108mp;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.mAdv.getSkillUseAwareCustomized(this.mContext, gameChar, skill));
        stringBuffer.append(this.mSentenceSeparator);
        if (skill.clazz != null) {
            throwResultGenericThrow = this.mThrower.attributeThrow(gameChar, skill.baseAttr, skill.clazz);
        } else {
            throwResultGenericThrow = this.mThrower.genericThrow(gameChar, skill.baseAttr);
        }
        if (skill.isMagic()) {
            throwResultGenericThrow.value += gameChar.getStatus(GameChar.Status.MAGIC_BONUS);
        }
        ArrayList arrayList = new ArrayList(1);
        ArrayList<GameChar> arrayList2 = new ArrayList(1);
        if (!throwResultGenericThrow.fumble || skill.clazz == null) {
            if (throwResultGenericThrow.critical) {
                stringBuffer.append(this.mContext.getString(skill.isMagic() ? C0380R.string.flog_desc_magic_critical : C0380R.string.flog_desc_skill_critical));
                stringBuffer.append(this.mSentenceSeparator);
            }
            for (GameChar gameChar2 : list) {
                if (gameChar2.isAlive()) {
                    int status = gameChar2.getStatus(GameChar.Status.ARMOR_MAGIC_BONUS);
                    Thrower.ThrowResult throwResultGenericThrow2 = this.mThrower.genericThrow(gameChar2, skill.baseAttr);
                    if (skill.isMagic()) {
                        throwResultGenericThrow2.value += gameChar2.getStatus(GameChar.Status.ANTI_MAGIC_BONUS);
                        status += gameChar2.getStatus(GameChar.Status.MAGIC_DEFENSE);
                    }
                    boolean z = throwResultGenericThrow2.critical || (throwResultGenericThrow2.value >= throwResultGenericThrow.value && !throwResultGenericThrow.critical) || throwResultGenericThrow.fumble;
                    if (throwResultGenericThrow.critical) {
                        iThrowDiceWithBonus = skill.getMaxValue() + EngineUtil.getAttrBonus(this.mRandom, gameChar.getAttr(skill.baseAttr));
                    } else {
                        iThrowDiceWithBonus = skill.attrBase + this.mThrower.throwDiceWithBonus(skill.diceNum, skill.diceFace, gameChar.getAttr(skill.baseAttr));
                    }
                    if (skill.isMagic() && gameChar.getStatus(GameChar.Status.MAGIC_DAMAGE_BONUS) != 0) {
                        iThrowDiceWithBonus += gameChar.getStatus(GameChar.Status.MAGIC_DAMAGE_BONUS) * skill.diceNum;
                    }
                    int i = iThrowDiceWithBonus - ((status + (skill.diceNum * status)) / 2);
                    if (skill.attrType != null) {
                        if (gameChar2.isWeakFor(skill.attrType)) {
                            i *= 2;
                        } else if (gameChar2.isResistFor(skill.attrType)) {
                            i /= 2;
                        } else if (gameChar2.isImmuneFor(skill.attrType)) {
                            i = 0;
                        }
                    }
                    if (z) {
                        i = skill.clazz == GameChar.SubClass.CLERIC ? 0 : i / 2;
                    }
                    if (i < 0) {
                        i = 0;
                    } else if (i > gameChar2.f93hp) {
                        i = gameChar2.f93hp;
                    }
                    gameChar2.f93hp -= i;
                    stringBuffer.append(this.mContext.getString(i > 0 ? C0380R.string.flog_desc_skill_damage : C0380R.string.flog_desc_skill_no_damage, gameChar2.name, Integer.valueOf(i)));
                    stringBuffer.append(this.mSentenceSeparator);
                    if (!gameChar2.isAlive()) {
                        arrayList.add(gameChar2);
                    } else if (i > 0) {
                        arrayList2.add(gameChar2);
                    }
                }
            }
        } else {
            stringBuffer.append(this.mContext.getString(skill.isMagic() ? C0380R.string.flog_desc_magic_fumble : C0380R.string.flog_desc_skill_fumble));
        }
        createAndAddSkillLog(gameChar, !gameChar.isPlayer(), null, list, skill, stringBuffer.toString());
        addCharsDeadLog(arrayList);
        for (GameChar gameChar3 : arrayList2) {
            if (gameChar3.getSpecifiedIllegalState(GameChar.IllegalStatus.SLEEP) != null) {
                Iterator<Enchant> it = gameChar3.illegalStatus.iterator();
                while (it.hasNext()) {
                    if (it.next().illegalStatus == GameChar.IllegalStatus.SLEEP) {
                        it.remove();
                    }
                }
                addLogForChar(gameChar3.isPlayer(), gameChar3, CommonLog.LogType.FIGHT_RESIST, this.mContext.getString(ID_FLOG_TITLE_WAKE_UP.get(gameChar3), gameChar3.name), null, gameChar3.isPlayer(), gameChar3);
            }
        }
    }

    private void processSkillMyStatus(GameChar gameChar, Skill skill) {
        processSkillStatusCommon(gameChar, gameChar, skill);
    }

    private void processSkillStatus(GameChar gameChar, GameChar gameChar2, Skill skill) {
        processSkillStatusCommon(gameChar, gameChar2, skill);
    }

    private void processSkillStatusAll(GameChar gameChar, Skill skill) {
        gameChar.f94mp -= skill.f108mp;
        StringBuffer stringBuffer = new StringBuffer();
        if (this.mThrower.attributeThrow(gameChar, skill.baseAttr, skill.clazz).fumble) {
            stringBuffer.append(this.mContext.getString(skill.isMagic() ? C0380R.string.flog_desc_magic_fumble : C0380R.string.flog_desc_skill_fumble));
        } else {
            Integer numValueOf = Integer.valueOf(skill.attrBase);
            stringBuffer.append(this.mAdv.getSkillUseAwareCustomized(this.mContext, gameChar, skill));
            stringBuffer.append(this.mSentenceSeparator);
            for (GameChar.Status status : skill.targetStatus) {
                if (gameChar.isPlayer()) {
                    this.mFightContext.addEnchantToParty(new Enchant(Enchant.Target.PARTY, status, numValueOf.intValue(), skill.f107id, skill.term));
                } else {
                    this.mFightContext.addEnchantToEnemyParty(new Enchant(Enchant.Target.ENEMY_PARTY, status, numValueOf.intValue(), skill.f107id, skill.term));
                }
                stringBuffer.append(this.mContext.getString(ID_FLOG_DESC_SKILL_STATUS_GROUP_ADD.get(gameChar), status.getString(this.mContext), numValueOf));
                stringBuffer.append(this.mSentenceSeparator);
            }
        }
        createAndAddSkillLog(gameChar, gameChar.isPlayer(), null, null, skill, stringBuffer.toString());
    }

    private void processSkillStatusCommon(GameChar gameChar, GameChar gameChar2, Skill skill) {
        gameChar.f94mp -= skill.f108mp;
        StringBuffer stringBuffer = new StringBuffer();
        if (this.mThrower.attributeThrow(gameChar, skill.baseAttr, skill.clazz).fumble) {
            stringBuffer.append(this.mContext.getString(skill.isMagic() ? C0380R.string.flog_desc_magic_fumble : C0380R.string.flog_desc_skill_fumble));
        } else {
            Integer numValueOf = Integer.valueOf(skill.attrBase);
            stringBuffer.append(this.mAdv.getSkillUseAwareCustomized(this.mContext, gameChar, skill));
            stringBuffer.append(this.mSentenceSeparator);
            for (GameChar.Status status : skill.targetStatus) {
                if (gameChar.isPlayer()) {
                    this.mFightContext.addEnchantToPlayer(gameChar2.index, new Enchant(Enchant.Target.PLAYER_CHAR, status, numValueOf.intValue(), skill.f107id, skill.term));
                } else {
                    this.mFightContext.addEnchantToEnemy(gameChar2.index, new Enchant(Enchant.Target.ENEMY, status, numValueOf.intValue(), skill.f107id, skill.term));
                }
                stringBuffer.append(this.mContext.getString(C0380R.string.flog_desc_skill_status_char_add, gameChar2.name, status.getString(this.mContext), numValueOf));
                stringBuffer.append(this.mSentenceSeparator);
            }
        }
        createAndAddSkillLog(gameChar, gameChar.isPlayer(), gameChar2, null, skill, stringBuffer.toString());
    }

    private void processSkillOther(GameChar gameChar, GameChar gameChar2, Skill skill) {
        int i = skill.f107id;
        if (i == 30160) {
            processSkillTranquility(gameChar, gameChar2, skill);
            return;
        }
        if (i == 30190) {
            processSkillDeath(gameChar, gameChar2, skill);
            return;
        }
        if (i == 40010 || i == 40030 || i == 40040) {
            processSkillSleep(gameChar, skill);
            return;
        }
        throw new IllegalArgumentException("Unsupported other skill:" + skill.name);
    }

    private void processSkillTranquility(GameChar gameChar, GameChar gameChar2, Skill skill) {
        gameChar.f94mp -= skill.f108mp;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.mAdv.getSkillUseAwareCustomized(this.mContext, gameChar, skill));
        stringBuffer.append(this.mSentenceSeparator);
        for (Enchant enchant : gameChar.isPlayer() ? this.mFightContext.enchantsPlayers[gameChar2.index] : this.mFightContext.enchantsEnemies[gameChar2.index]) {
            if (enchant.illegalStatus != null) {
                stringBuffer.append(this.mContext.getString(C0380R.string.flog_desc_skill_tranquility, gameChar2.name, enchant.illegalStatus.getAsString(this.mContext)));
                stringBuffer.append(this.mSentenceSeparator);
            }
        }
        createAndAddSkillLog(gameChar, gameChar.isPlayer(), gameChar2, null, skill, stringBuffer.toString());
    }

    private void processSkillDeath(GameChar gameChar, GameChar gameChar2, Skill skill) {
        gameChar.f94mp -= skill.f108mp;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.mAdv.getSkillUseAwareCustomized(this.mContext, gameChar, skill));
        stringBuffer.append(this.mSentenceSeparator);
        if (gameChar2.isAlive()) {
            boolean zIsResistFor = gameChar2.isResistFor(AttrType.DEATH);
            if (!zIsResistFor) {
                Thrower.ThrowResult throwResultAttributeFixedLevelThrow = this.mThrower.attributeFixedLevelThrow(gameChar, skill.attrBase, GameChar.Attribute.INT);
                Thrower.ThrowResult throwResultGenericThrow = this.mThrower.genericThrow(gameChar2, GameChar.Attribute.INT);
                if (skill.isMagic()) {
                    throwResultAttributeFixedLevelThrow.value += gameChar.getStatus(GameChar.Status.MAGIC_BONUS);
                    throwResultGenericThrow.value += gameChar2.getStatus(GameChar.Status.ANTI_MAGIC_BONUS);
                }
                throwResultGenericThrow.value += 4;
                zIsResistFor = throwResultGenericThrow.critical || (throwResultGenericThrow.value >= throwResultAttributeFixedLevelThrow.value && !throwResultAttributeFixedLevelThrow.critical) || throwResultAttributeFixedLevelThrow.fumble;
            }
            if (zIsResistFor) {
                stringBuffer.append(this.mContext.getString(C0380R.string.flog_desc_skill_resist_spell_success, gameChar2.name));
            } else {
                gameChar2.f93hp = 0;
                stringBuffer.append(this.mContext.getString(C0380R.string.flog_desc_skill_resist_spell_fail, gameChar2.name));
            }
            createAndAddSkillLog(gameChar, !gameChar.isPlayer(), gameChar2, null, skill, stringBuffer.toString());
            if (gameChar2.isAlive()) {
                return;
            }
            addCharDeadLog(gameChar2);
        }
    }

    private void processSkillSleep(GameChar gameChar, Skill skill) {
        gameChar.f94mp -= skill.f108mp;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.mAdv.getSkillUseAwareCustomized(this.mContext, gameChar, skill));
        stringBuffer.append(this.mSentenceSeparator);
        List<? extends GameChar> list = gameChar.isPlayer() ? this.mEnemies : this.mGame.characters;
        Thrower.ThrowResult throwResultAttributeFixedLevelThrow = this.mThrower.attributeFixedLevelThrow(gameChar, skill.attrBase, GameChar.Attribute.INT);
        if (skill.isMagic()) {
            throwResultAttributeFixedLevelThrow.value += gameChar.getStatus(GameChar.Status.MAGIC_BONUS);
        }
        for (GameChar gameChar2 : list) {
            if (gameChar2.isAlive() && gameChar2.getSpecifiedIllegalState(GameChar.IllegalStatus.SLEEP) == null) {
                boolean zIsResistFor = gameChar2.isResistFor(AttrType.SLEEP);
                if (!zIsResistFor) {
                    Thrower.ThrowResult throwResultGenericThrow = this.mThrower.genericThrow(gameChar2, GameChar.Attribute.INT);
                    if (skill.isMagic()) {
                        throwResultGenericThrow.value += gameChar2.getStatus(GameChar.Status.ANTI_MAGIC_BONUS);
                    }
                    zIsResistFor = throwResultGenericThrow.critical || (throwResultGenericThrow.value >= throwResultAttributeFixedLevelThrow.value && !throwResultAttributeFixedLevelThrow.critical) || throwResultAttributeFixedLevelThrow.fumble;
                }
                if (zIsResistFor) {
                    stringBuffer.append(this.mContext.getString(C0380R.string.flog_desc_skill_resist_spell_success, gameChar2.name));
                } else {
                    gameChar2.illegalStatus.add(new Enchant(gameChar.isPlayer() ? Enchant.Target.ENEMY : Enchant.Target.PLAYER_CHAR, GameChar.IllegalStatus.SLEEP, throwResultAttributeFixedLevelThrow.value, skill, -1));
                    stringBuffer.append(this.mContext.getString(C0380R.string.flog_desc_skill_resist_spell_fail, gameChar2.name));
                }
                stringBuffer.append(this.mSentenceSeparator);
            }
        }
        createAndAddSkillLog(gameChar, !gameChar.isPlayer(), null, list, skill, stringBuffer.toString());
    }

    private void processSkillAddAttack(GameChar gameChar, FightActionEvaluation fightActionEvaluation) {
        Skill skill = SkillRepository.getSkill(this.mContext, fightActionEvaluation.skillId);
        addLogForChar(gameChar.isPlayer(), gameChar, CommonLog.LogType.FIGHT_SKILL_ADD_ATTACK, this.mContext.getString(ID_FLOG_TITLE_ATTACK_SKILL.get(gameChar), gameChar.name, this.mAdv.getSkillNameAwareCustomized(gameChar, skill)), this.mAdv.getSkillUseAwareCustomized(this.mContext, gameChar, skill), gameChar.isPlayer(), gameChar);
        gameChar.f94mp -= skill.f108mp;
        if (skill.isMultiAttack && fightActionEvaluation.baseActions.size() >= 2) {
            processPhysicalAttack(gameChar, skill, fightActionEvaluation.baseActions.get(0).targetChar, fightActionEvaluation.baseActions.get(1).targetChar);
            return;
        }
        if (skill.isDoubleAttack) {
            FightActionEvaluation fightActionEvaluation2 = (FightActionEvaluation) fightActionEvaluation.baseActions.get(0);
            for (int i = 0; i < 2; i++) {
                if (fightActionEvaluation2.targetChar.isAlive()) {
                    processPhysicalAttack(gameChar, skill, fightActionEvaluation2.targetChar);
                }
            }
            return;
        }
        processPhysicalAttack(gameChar, skill, fightActionEvaluation.baseActions.get(0).targetChar);
    }

    private void processPhysicalAttack(GameChar gameChar, Skill skill, GameChar... gameCharArr) {
        CommonLog.LogType logType;
        CommonLog.LogType logType2;
        int i;
        int i2;
        CommonLog.LogType logType3;
        AttackResult[] attackResultArr;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        AttackResult[] attackResultArrProcessAttack = processAttack(gameChar, skill, gameCharArr);
        StringBuilder sb = new StringBuilder();
        int length = attackResultArrProcessAttack.length;
        int i10 = 0;
        boolean z = false;
        boolean z2 = false;
        while (i10 < length) {
            AttackResult attackResult = attackResultArrProcessAttack[i10];
            GameChar gameChar2 = attackResult.target;
            boolean z3 = attackResult.count <= 1;
            if (z3) {
                attackResultArr = attackResultArrProcessAttack;
            } else {
                attackResultArr = attackResultArrProcessAttack;
                sb.append(this.mContext.getString(ID_FLOG_DESC_ATTACK_MULTI.get(gameChar), gameChar2.name, Integer.valueOf(attackResult.count)));
                sb.append(this.mSentenceSeparator);
            }
            int i11 = 0;
            while (i11 < attackResult.count) {
                int i12 = attackResult.damage[i11];
                gameChar2.f93hp -= i12;
                gameChar2.f93hp = Math.max(0, gameChar2.f93hp);
                switch (attackResult.type[i11]) {
                    case MISS:
                        i3 = length;
                        if (z3) {
                            i4 = ID_FLOG_DESC_ATTACK_FAIL_DODGE.get(gameChar);
                        } else {
                            i4 = ID_FLOG_DESC_ATTACK_MULTI_FAIL_DODGE.get(gameChar);
                        }
                        sb.append(this.mContext.getString(i4, gameChar2.name));
                        break;
                    case BLOCK_ARMOR:
                        i3 = length;
                        if (z3) {
                            i5 = ID_FLOG_DESC_ATTACK_FAIL_ARMOR.get(gameChar);
                        } else {
                            i5 = ID_FLOG_DESC_ATTACK_MULTI_FAIL_ARMOR.get(gameChar);
                        }
                        sb.append(this.mContext.getString(i5, gameChar2.name));
                        break;
                    case BLOCK_SHIELD:
                        i3 = length;
                        if (z3) {
                            i6 = ID_FLOG_DESC_ATTACK_FAIL_SHILED.get(gameChar);
                        } else {
                            i6 = ID_FLOG_DESC_ATTACK_MULTI_FAIL_SHILED.get(gameChar);
                        }
                        sb.append(this.mContext.getString(i6, gameChar2.name));
                        break;
                    case CRITICAL:
                        i3 = length;
                        if (i12 > 0) {
                            if (z3) {
                                i7 = ID_FLOG_DESC_ATTACK_HIT_CRITICAL.get(gameChar);
                            } else {
                                i7 = ID_FLOG_DESC_ATTACK_MULTI_HIT_CRITICAL.get(gameChar);
                            }
                        } else if (z3) {
                            i7 = ID_FLOG_DESC_ATTACK_HIT_NO_DAMAGE.get(gameChar);
                        } else {
                            i7 = ID_FLOG_DESC_ATTACK_MULTI_HIT_NO_DAMAGE.get(gameChar);
                        }
                        sb.append(this.mContext.getString(i7, gameChar2.name, Integer.valueOf(i12)));
                        z = true;
                        z2 = true;
                        break;
                    case NORMAL_HIT:
                        i3 = length;
                        if (i12 > 0) {
                            i8 = (z3 ? ID_FLOG_DESC_ATTACK_HIT : ID_FLOG_DESC_ATTACK_MULTI_HIT).get(gameChar);
                        } else if (z3) {
                            i8 = ID_FLOG_DESC_ATTACK_HIT_NO_DAMAGE.get(gameChar);
                        } else {
                            i8 = ID_FLOG_DESC_ATTACK_MULTI_HIT_NO_DAMAGE.get(gameChar);
                        }
                        sb.append(this.mContext.getString(i8, gameChar2.name, Integer.valueOf(i12)));
                        z2 = true;
                        break;
                    case SHIELD_HIT:
                        if (i12 > 0) {
                            if (z3) {
                                i9 = ID_FLOG_DESC_ATTACK_HIT_SHIELD.get(gameChar);
                            } else {
                                i9 = ID_FLOG_DESC_ATTACK_MULTI_HIT_SHIELD.get(gameChar);
                            }
                        } else if (z3) {
                            i9 = ID_FLOG_DESC_ATTACK_HIT_NO_DAMAGE.get(gameChar);
                        } else {
                            i9 = ID_FLOG_DESC_ATTACK_MULTI_HIT_NO_DAMAGE.get(gameChar);
                        }
                        i3 = length;
                        sb.append(this.mContext.getString(i9, gameChar2.name, Integer.valueOf(i12)));
                        z2 = true;
                        break;
                    default:
                        i3 = length;
                        break;
                }
                sb.append(this.mSentenceSeparator);
                i11++;
                length = i3;
            }
            i10++;
            attackResultArrProcessAttack = attackResultArr;
        }
        if (gameChar.isPlayer()) {
            if (z) {
                logType3 = CommonLog.LogType.ATTACK_CRITICAL;
            } else {
                logType3 = z2 ? CommonLog.LogType.ATTACK : CommonLog.LogType.ATTACK_MISS;
            }
            logType2 = logType3;
        } else {
            if (z) {
                logType = CommonLog.LogType.DEFENSE_CRITICAL;
            } else {
                logType = z2 ? CommonLog.LogType.DEFENSE : CommonLog.LogType.DEFENSE_MISS;
            }
            logType2 = logType;
        }
        Context context = this.mContext;
        if (z) {
            i = ID_FLOG_TITLE_ATTACK_CRITICAL.get(gameChar);
            i2 = 1;
        } else {
            i = ID_FLOG_TITLE_ATTACK.get(gameChar);
            i2 = 1;
        }
        Object[] objArr = new Object[i2];
        objArr[0] = gameChar.name;
        addLogForCharArray(gameChar.isPlayer(), gameChar, logType2, context.getString(i, objArr), sb.toString(), !gameChar.isPlayer(), gameCharArr);
        ArrayList arrayList = new ArrayList(gameCharArr.length);
        for (GameChar gameChar3 : gameCharArr) {
            if (!gameChar3.isAlive()) {
                arrayList.add(gameChar3);
            } else if (gameChar3.getSpecifiedIllegalState(GameChar.IllegalStatus.SLEEP) != null) {
                Iterator<Enchant> it = gameChar3.illegalStatus.iterator();
                while (it.hasNext()) {
                    if (it.next().illegalStatus == GameChar.IllegalStatus.SLEEP) {
                        it.remove();
                    }
                }
                addLogForChar(gameChar3.isPlayer(), gameChar3, CommonLog.LogType.FIGHT_RESIST, this.mContext.getString(ID_FLOG_TITLE_WAKE_UP.get(gameChar3), gameChar3.name), null, gameChar3.isPlayer(), gameChar3);
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        addCharsDeadLog(arrayList);
    }

    private AttackResult[] processAttack(GameChar gameChar, Skill skill, GameChar... gameCharArr) {
        int i;
        int i2;
        int i3;
        boolean zIsWeakFor;
        boolean zIsResistFor;
        boolean zIsImmuneFor;
        int damageRatioForMonster;
        AttackResult attackResult;
        int i4;
        GameChar[] gameCharArr2 = gameCharArr;
        if (skill == null || skill.targetStatus == null) {
            i = 0;
            i2 = 0;
        } else {
            int i5 = 0;
            int i6 = 0;
            for (GameChar.Status status : skill.targetStatus) {
                if (status == GameChar.Status.CRITICAL) {
                    i5 = skill.attrBase;
                } else if (status == GameChar.Status.HIT_BONUS) {
                    i6 = skill.attrBase;
                } else {
                    throw new IllegalStateException("Unsupported additional skill:" + skill);
                }
            }
            i = i5;
            i2 = i6;
        }
        int maxAttackTimes = gameChar.getMaxAttackTimes();
        if (maxAttackTimes > 1) {
            int iMin = maxAttackTimes;
            for (GameChar gameChar2 : gameCharArr2) {
                Thrower.ThrowResult throwResultAttributeThrow = this.mThrower.attributeThrow(gameChar, GameChar.Attribute.AGI, gameChar.fightingSubClass);
                Thrower.ThrowResult throwResultAttributeThrow2 = this.mThrower.attributeThrow(gameChar2, GameChar.Attribute.AGI, gameChar2.fightingSubClass);
                if (throwResultAttributeThrow.critical) {
                    i4 = maxAttackTimes;
                } else if (throwResultAttributeThrow2.critical) {
                    i4 = 1;
                } else {
                    i4 = throwResultAttributeThrow.value > throwResultAttributeThrow2.value ? ((throwResultAttributeThrow.value - throwResultAttributeThrow2.value) / 2) + 1 : 1;
                }
                iMin = Math.min(iMin, i4);
            }
            i3 = iMin;
        } else {
            i3 = 1;
        }
        AttrType attackAttrType = gameChar.getAttackAttrType();
        if (attackAttrType != null) {
            zIsWeakFor = gameCharArr2[0].isWeakFor(attackAttrType);
            zIsResistFor = gameCharArr2[0].isResistFor(attackAttrType);
            zIsImmuneFor = gameCharArr2[0].isImmuneFor(attackAttrType);
        } else {
            zIsWeakFor = false;
            zIsResistFor = false;
            zIsImmuneFor = false;
        }
        AttackResult[] attackResultArr = new AttackResult[gameCharArr2.length];
        int i7 = 0;
        while (i7 < gameCharArr2.length) {
            GameChar gameChar3 = gameCharArr2[i7];
            attackResultArr[i7] = new AttackResult(gameChar3);
            AttackResult attackResult2 = attackResultArr[i7];
            if (gameChar.isPlayer()) {
                Inventory weapon = ((PlayerChar) gameChar).getWeapon();
                damageRatioForMonster = weapon.getDamageRatioForMonster(weapon.getBaseItem(this.mContext), ((Enemy) gameChar3).monster);
            } else {
                damageRatioForMonster = 1;
            }
            boolean zIsAsleep = gameChar3.isAsleep();
            int i8 = 0;
            while (i8 < i3) {
                int i9 = i8;
                AttackResult attackResult3 = attackResult2;
                GameChar gameChar4 = gameChar3;
                int i10 = i7;
                AttackResult[] attackResultArr2 = attackResultArr;
                Thrower.ThrowResult throwResultAttributeThrow3 = this.mThrower.attributeThrow(gameChar, GameChar.Attribute.AGI, gameChar.fightingSubClass, gameChar.getStatus(GameChar.Status.CRITICAL) - i, gameChar.getStatus(GameChar.Status.FUMBLE));
                Thrower.ThrowResult throwResultAttributeThrow4 = this.mThrower.attributeThrow(gameChar4, GameChar.Attribute.AGI, gameChar4.fightingSubClass);
                throwResultAttributeThrow3.value += i2 + 3;
                throwResultAttributeThrow3.value += ((i3 / 1) - 1) * (-1);
                throwResultAttributeThrow3.value += gameChar.getStatus(GameChar.Status.HIT_BONUS) + gameChar.getStatus(GameChar.Status.WEAPON_MAGIC_BONUS);
                throwResultAttributeThrow4.value += gameChar4.getStatus(GameChar.Status.DODGE_BONUS);
                if (zIsAsleep) {
                    throwResultAttributeThrow3.value += 8;
                }
                boolean z = throwResultAttributeThrow3.value >= throwResultAttributeThrow4.value + (gameChar4.getStatus(GameChar.Status.SHIELD_DODGE) + gameChar4.getStatus(GameChar.Status.SHIELD_MAGIC_BONUS));
                boolean z2 = throwResultAttributeThrow3.value >= throwResultAttributeThrow4.value;
                if (throwResultAttributeThrow4.critical || (!throwResultAttributeThrow3.critical && !z2)) {
                    attackResult = attackResult3;
                    attackResult.addMiss();
                } else {
                    int iThrowDice = this.mThrower.throwDice(gameChar.attackDiceNum, gameChar.attackDiceFace) + gameChar.getStatus(GameChar.Status.WEAPON_MAGIC_BONUS);
                    int status2 = gameChar4.getStatus(GameChar.Status.DEFENSE) + gameChar4.getStatus(GameChar.Status.ARMOR_MAGIC_BONUS);
                    if (throwResultAttributeThrow3.critical && z) {
                        status2 = 0;
                    }
                    int status3 = (iThrowDice * damageRatioForMonster) + gameChar.getStatus(GameChar.Status.DAMAGE) + EngineUtil.getAttrBonus(this.mRandom, gameChar.str);
                    if (skill != null && (skill.targetStatus == null || skill.targetStatus.isEmpty())) {
                        status3 = (status3 * (skill.attrBase + 100)) / 100;
                    }
                    if (!z) {
                        status3 /= 2;
                    }
                    if (zIsAsleep) {
                        status3 += status3;
                    }
                    int i11 = status3 - status2;
                    if (i11 < 0) {
                        i11 = 0;
                    }
                    if (i11 == 0 && throwResultAttributeThrow3.critical) {
                        i11 = 1;
                    }
                    int i12 = gameChar.attackBase;
                    if (!z) {
                        i12 /= 2;
                    }
                    int i13 = i11 + i12;
                    if (i13 != 0) {
                        switch (gameChar4.getMagicResist()) {
                            case FULL:
                                if (!gameChar.hasEnchantedWeapon()) {
                                    i13 = 0;
                                    break;
                                }
                                break;
                            case HALF:
                                if (!gameChar.hasEnchantedWeapon()) {
                                    i13 -= i13 / 2;
                                    break;
                                }
                                break;
                            case IMMUNE:
                                if (attackAttrType == null) {
                                    i13 = 0;
                                    break;
                                } else {
                                    i13 = (i13 + 1) / 3;
                                    break;
                                }
                        }
                        if (zIsWeakFor) {
                            i13 *= 2;
                        } else if (zIsResistFor) {
                            i13 -= i13 / 4;
                        } else if (zIsImmuneFor) {
                            i13 -= i13 / 2;
                        }
                        AttackResult.Type type = throwResultAttributeThrow3.critical ? AttackResult.Type.CRITICAL : z ? AttackResult.Type.NORMAL_HIT : AttackResult.Type.SHIELD_HIT;
                        attackResult = attackResult3;
                        attackResult.add(type, i13);
                    } else if (z) {
                        attackResult3.addBlockArmor();
                        attackResult = attackResult3;
                    } else {
                        attackResult3.addBlockShield();
                        attackResult = attackResult3;
                    }
                }
                i8 = i9 + 1;
                attackResult2 = attackResult;
                gameChar3 = gameChar4;
                i7 = i10;
                attackResultArr = attackResultArr2;
            }
            i7++;
            gameCharArr2 = gameCharArr;
        }
        return attackResultArr;
    }

    private void processEnemy(Enemy enemy) {
        EnemyActionEngine enemyActionEngine = new EnemyActionEngine(this.mContext, this.mRandom, enemy);
        enemyActionEngine.setParties(this.mEnemies, this.mGame.characters).setFightContext(this.mFightContext);
        FightActionEvaluation fightActionEvaluationAction = enemyActionEngine.action();
        if (fightActionEvaluationAction == null) {
            processNone(enemy);
            return;
        }
        if (fightActionEvaluationAction.action == ActionEvaluation.Action.ATTACK) {
            processPhysicalAttack(enemy, null, fightActionEvaluationAction.targetChar);
            return;
        }
        if (fightActionEvaluationAction.baseActions != null) {
            processSkillAddAttack(enemy, fightActionEvaluationAction);
            return;
        }
        if (fightActionEvaluationAction.action == ActionEvaluation.Action.SKILL_MAGIC) {
            processSkill(enemy, fightActionEvaluationAction);
            return;
        }
        if (fightActionEvaluationAction.action == ActionEvaluation.Action.USE_ITEM) {
            processEnemyItem(enemy, fightActionEvaluationAction);
            return;
        }
        if (fightActionEvaluationAction.action == ActionEvaluation.Action.EQUIP_ITEM) {
            processEnemyEquip(enemy, fightActionEvaluationAction);
        } else if (fightActionEvaluationAction.action == ActionEvaluation.Action.RUNNING) {
            processEnemyRun(enemy, fightActionEvaluationAction);
        } else {
            processNone(enemy);
        }
    }

    private void processEnemyRun(Enemy enemy, FightActionEvaluation fightActionEvaluation) {
        if (EngineUtil.antiGroupThrow(this.mRandom, this.mThrower, GameChar.Attribute.AGI, enemy, getAliveEnemies().size(), this.mGame.getAliveChars(), 4, true)) {
            enemy.running = true;
            addLogForChar(false, enemy, CommonLog.LogType.FIGHT_RUN, this.mContext.getString(C0380R.string.flog_title_enemy_run, enemy.name), this.mContext.getString(C0380R.string.flog_desc_enemy_run, enemy.name), false, enemy);
        } else {
            addLogForChar(false, enemy, CommonLog.LogType.FIGHT_RUN_FAILED, this.mContext.getString(C0380R.string.flog_title_enemy_run_failed, enemy.name), this.mContext.getString(C0380R.string.flog_desc_enemy_run_failed, enemy.name), false, enemy);
        }
    }

    private void processEnemyItem(Enemy enemy, FightActionEvaluation fightActionEvaluation) {
        throw new IllegalStateException("Enemy doesn't have any item.");
    }

    private void processEnemyEquip(Enemy enemy, FightActionEvaluation fightActionEvaluation) {
        throw new IllegalStateException("Enemy can't equip any item.");
    }

    private void processMaxRoundExceeds() {
        addLogForField(true, null, CommonLog.LogType.FIGHT_LOSE, this.mContext.getString(C0380R.string.flog_title_exceed_max_round, this.mFightContext.monsters.get(0).name), this.mContext.getString(C0380R.string.flog_desc_exceed_max_round));
    }

    private FightResult processEnd() {
        if (areAllEnemiesDiedOrRunning()) {
            return processWin();
        }
        if (!this.mGame.areAllCharsDied()) {
            processRun();
            return FightResult.RUN;
        }
        processLose();
        return FightResult.LOSE;
    }

    private void processLose() {
        addLogForField(true, null, CommonLog.LogType.FIGHT_LOSE, this.mContext.getString(C0380R.string.flog_title_lose, this.mFightContext.monsters.get(0).name), this.mContext.getString(C0380R.string.flog_desc_lose));
        this.mGame.getOrCreateFlag(GameFlag.Key.asMonsterLose(this.mFightContext.monsters.get(0))).addOptionAsInt(1);
        processFlags(Decision.Timing.FIGHTING_LOSE_MONSTER);
    }

    private void processRun() {
        this.mGame.getOrCreateFlag(GameFlag.Key.asMonsterRun(this.mFightContext.monsters.get(0))).addOptionAsInt(1);
    }

    private FightResult processWin() {
        Thrower thrower = new Thrower(this.mRandom);
        int iThrowDice = 0;
        int i = 0;
        for (Enemy enemy : this.mEnemies) {
            if (!enemy.running) {
                i += enemy.monster.exp;
                iThrowDice += enemy.monster.goldBase + thrower.throwDice(enemy.monster.goldNum, enemy.monster.goldFace);
            }
        }
        this.mGame.gold += iThrowDice;
        Iterator<PlayerChar> it = this.mGame.characters.iterator();
        int i2 = 0;
        while (it.hasNext()) {
            if (it.next().isAlive()) {
                i2++;
            }
        }
        int i3 = ((i + i2) - 1) / i2;
        for (PlayerChar playerChar : this.mGame.characters) {
            if (playerChar.isAlive()) {
                playerChar.exp += i3;
                if (playerChar.exp > 999999999) {
                    playerChar.exp = 999999999;
                }
            }
        }
        addLogForField(true, null, CommonLog.LogType.FIGHT_WIN, this.mContext.getString(C0380R.string.flog_title_win, this.mFightContext.monsters.get(0).name), this.mContext.getString(C0380R.string.flog_desc_win_exp_gold, Integer.valueOf(i), Integer.valueOf(iThrowDice)));
        for (PlayerChar playerChar2 : this.mGame.characters) {
            if (playerChar2.level < GameChar.getLevel(playerChar2.exp)) {
                playerChar2.level++;
                if (playerChar2.isStatusBonusGot()) {
                    playerChar2.statusBonus++;
                }
                String string = this.mContext.getString(C0380R.string.flog_title_level_up, playerChar2.name);
                String string2 = this.mContext.getString(C0380R.string.flog_desc_level_up, Integer.valueOf(playerChar2.level));
                int i4 = playerChar2.maxHp;
                int i5 = playerChar2.maxMp;
                playerChar2.calcStatus(this.mContext, this.mGame);
                playerChar2.f93hp += playerChar2.maxHp - i4;
                playerChar2.f94mp += playerChar2.maxMp - i5;
                addLogForChar(true, playerChar2, CommonLog.LogType.LEVEL_UP, string, string2, true, playerChar2);
            }
        }
        Monster monster = this.mFightContext.monsters.get(0);
        this.mGame.getOrCreateFlag(GameFlag.Key.asMonsterWin(monster)).addOptionAsInt(1);
        ItemDrop itemDrop = ItemDrop.getItemDrop(this.mFightContext.isWandering ? monster.randomItemDrops : monster.placedItemDrops, this.mRandom, this.mAdv.extraLevel > 0 ? FlagEngine.enhanceItemDropFactor(this.mAdv.extraLevel) : 0.0d);
        if (itemDrop != null) {
            processItemDrop(monster, itemDrop);
        }
        processFlags(Decision.Timing.FIGHTING_WIN_MONSTER);
        return itemDrop == null ? FightResult.WIN : FightResult.WIN_GOT_ITEM;
    }

    private void processItemDrop(Monster monster, ItemDrop itemDrop) {
        StringBuilder sb = new StringBuilder();
        if (!this.mFightContext.isWandering) {
            if (this.mRandom.nextInt(1000) < this.mAdv.dungeon.treasureTrapFactor) {
                new TrapEngine(this.mContext, (TrapEngine.TrapType) EngineUtil.getElementRandom(TrapEngine.TrapType.values(), this.mRandom), this.mAdv, this.mThrower.genericThrow(this.mEnemies.get(0), GameChar.Attribute.INT).value, this.mGame.getAliveChars(), this.mRandom).processTreasureBox(sb);
            } else {
                new TrapEngine(this.mContext, null, this.mAdv, 0, this.mGame.getAliveChars(), this.mRandom).processTreasureBox(sb);
            }
        }
        Item item = ItemRepository.getItem(this.mContext, itemDrop.itemId);
        Inventory inventoryCreateTemporaryInventory = this.mAdv.createTemporaryInventory();
        inventoryCreateTemporaryInventory.itemId = itemDrop.itemId;
        int iEnhanceItemDrop = itemDrop.enchantMax;
        if (this.mAdv.extraLevel > 0) {
            iEnhanceItemDrop = FlagEngine.enhanceItemDrop(iEnhanceItemDrop, this.mAdv.extraLevel);
        }
        inventoryCreateTemporaryInventory.setRandomEnchant(item, iEnhanceItemDrop, this.mRandom);
        this.mGame.inventories.add(inventoryCreateTemporaryInventory);
        this.mGame.getOrCreateFlag(GameFlag.Key.asItemGot(item)).addOptionAsInt(1);
        String string = this.mContext.getString(this.mFightContext.isWandering ? C0380R.string.flog_title_treasure_wandering : C0380R.string.flog_title_treasure_placed, monster.name);
        Context context = this.mContext;
        sb.append(context.getString(C0380R.string.flog_desc_treasure, inventoryCreateTemporaryInventory.getName(context)));
        addLogForField(true, null, CommonLog.LogType.ITEM_DROP, string, sb.toString());
    }

    private FightingLog createFightingLog(boolean z, CommonLog.LogType logType, GameChar gameChar, String str, String str2, String str3, boolean z2, GameChar gameChar2, List<? extends GameChar> list, GameChar[] gameCharArr) {
        FightingLog fightingLog = new FightingLog();
        fightingLog.type = logType;
        fightingLog.title = str;
        fightingLog.desc1 = str2;
        fightingLog.desc2 = str3;
        fightingLog.playersAct = z;
        fightingLog.toPlayer = z2;
        int i = 0;
        if (this.mByAdvancedTactics) {
            fightingLog.title = this.mTitlePrefixByAdvancedTactics + str;
            this.mByAdvancedTactics = false;
        }
        if (gameChar != null) {
            if (gameChar instanceof PlayerChar) {
                fightingLog.playerChar = (PlayerChar) gameChar;
            } else {
                fightingLog.enemyIndex = ((Enemy) gameChar).index;
            }
        }
        if (gameChar2 != null) {
            fightingLog.targetIds = new int[1];
            fightingLog.targetIds[0] = gameChar2.isPlayer() ? ((PlayerChar) gameChar2).f106id : gameChar2.index;
        } else if (list != null && !list.isEmpty()) {
            fightingLog.targetIds = new int[list.size()];
            if (list.get(0).isPlayer()) {
                while (i < list.size()) {
                    fightingLog.targetIds[i] = ((PlayerChar) list.get(i)).f106id;
                    i++;
                }
            } else {
                while (i < list.size()) {
                    fightingLog.targetIds[i] = list.get(i).index;
                    i++;
                }
            }
        } else if (gameCharArr != null && gameCharArr.length > 0) {
            fightingLog.targetIds = new int[gameCharArr.length];
            if (gameCharArr[0].isPlayer()) {
                while (i < gameCharArr.length) {
                    fightingLog.targetIds[i] = ((PlayerChar) gameCharArr[i]).f106id;
                    i++;
                }
            } else {
                while (i < gameCharArr.length) {
                    fightingLog.targetIds[i] = gameCharArr[i].index;
                    i++;
                }
            }
        }
        return fightingLog;
    }

    private void addLog(FightingLog fightingLog) {
        if (!CommonLog.LogChar.equalsToPCs(this.mLastLogChars, this.mGame.characters)) {
            fightingLog.logChars = CommonLog.LogChar.fromPlayerChar(this.mGame.characters);
            this.mLastLogChars = fightingLog.logChars;
        }
        if (!LogEnemy.equalsToEnemies(this.mLastLogEnemies, this.mEnemies)) {
            fightingLog.logEnemies = LogEnemy.fromEnemies(this.mEnemies);
            this.mLastLogEnemies = fightingLog.logEnemies;
        }
        this.mLogs.add(fightingLog);
    }

    private void addCharsDeadLog(List<? extends GameChar> list) {
        if (list.isEmpty()) {
            return;
        }
        GameChar gameChar = list.get(0);
        if (list.size() == 1) {
            addCharDeadLog(list.get(0));
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(this.mContext.getString(C0380R.string.flog_title_died_comma));
            }
            sb.append(list.get(i).name);
        }
        addLogForChars(gameChar.isPlayer(), null, gameChar.isPlayer() ? CommonLog.LogType.PLAYER_DEAD : CommonLog.LogType.ENEMY_DEAD, this.mContext.getString(ID_FLOG_TITLE_DIED.get(gameChar), sb.toString()), null, gameChar.isPlayer(), list);
    }

    private void addCharDeadLog(GameChar gameChar) {
        addLogForChar(gameChar.isPlayer(), gameChar, gameChar.isPlayer() ? CommonLog.LogType.PLAYER_DEAD : CommonLog.LogType.ENEMY_DEAD, this.mContext.getString(ID_FLOG_TITLE_DIED.get(gameChar), gameChar.name), null, gameChar.isPlayer(), gameChar);
    }

    public static class AttackResult {
        public GameChar target;
        public int count = 0;
        public Type[] type = new Type[8];
        public int[] damage = new int[8];

        public enum Type {
            CRITICAL,
            NORMAL_HIT,
            SHIELD_HIT,
            BLOCK_ARMOR,
            BLOCK_SHIELD,
            MISS
        }

        public AttackResult(GameChar gameChar) {
            this.target = gameChar;
        }

        public void add(Type type, int i) {
            Type[] typeArr = this.type;
            int i2 = this.count;
            typeArr[i2] = type;
            this.damage[i2] = i;
            this.count = i2 + 1;
        }

        public void addMiss() {
            add(Type.MISS, 0);
        }

        public void addBlockArmor() {
            add(Type.BLOCK_ARMOR, 0);
        }

        public void addBlockShield() {
            add(Type.BLOCK_SHIELD, 0);
        }
    }

    List<AdvancedTactics.TacticsComposition> getAdvancedFightTacticsForCharIndex(int i) {
        return this.mAdv.advancedFightTacticsForChar[i];
    }

    boolean matchesAdvancedTactics(AdvancedTactics.TacticsComposition tacticsComposition, PlayerChar playerChar) {
        return tacticsComposition.matches(playerChar, this.mGame, this.mAdv, this.mFightContext);
    }
}
