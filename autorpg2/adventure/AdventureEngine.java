package com.shirobakama.autorpg2.adventure;

import android.content.Context;
import android.database.SQLException;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.shirobakama.autorpg2.adventure.ActionEvaluation;
import com.shirobakama.autorpg2.adventure.FightEngine;
import com.shirobakama.autorpg2.adventure.FlagEngine;
import com.shirobakama.autorpg2.adventure.Thrower;
import com.shirobakama.autorpg2.adventure.TrapEngine;
import com.shirobakama.autorpg2.entity.AdvancedTactics;
import com.shirobakama.autorpg2.entity.AdventureContext;
import com.shirobakama.autorpg2.entity.AdventureLog;
import com.shirobakama.autorpg2.entity.CommonLog;
import com.shirobakama.autorpg2.entity.Decision;
import com.shirobakama.autorpg2.entity.Dungeon;
import com.shirobakama.autorpg2.entity.DungeonEvent;
import com.shirobakama.autorpg2.entity.DungeonStat;
import com.shirobakama.autorpg2.entity.Enchant;
import com.shirobakama.autorpg2.entity.FightingLog;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.GameFlag;
import com.shirobakama.autorpg2.entity.Inventory;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.autorpg2.entity.ItemDrop;
import com.shirobakama.autorpg2.entity.LogFight;
import com.shirobakama.autorpg2.entity.LogManagement;
import com.shirobakama.autorpg2.entity.LogStatus;
import com.shirobakama.autorpg2.entity.Monster;
import com.shirobakama.autorpg2.entity.MonsterPop;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.entity.Skill;
import com.shirobakama.autorpg2.entity.Tactics;
import com.shirobakama.autorpg2.p001db.Persister;
import com.shirobakama.autorpg2.repo.DungeonRepository;
import com.shirobakama.autorpg2.repo.ItemRepository;
import com.shirobakama.autorpg2.repo.MonsterDb;
import com.shirobakama.autorpg2.repo.MonsterRepository;
import com.shirobakama.autorpg2.repo.SkillDb;
import com.shirobakama.autorpg2.repo.SkillRepository;
import com.shirobakama.autorpg2.util.NotificationReceiver;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class AdventureEngine {
    private static final int FACTOR_REST_IN_MONSTER_BLOCK = 800;
    private static final int MINIMUM_ACTION_VALUE = 500;
    private static final int MIN_PROGRESS_IN_MINUTES = 4;
    private static final long MS_IN_MINUTE = 60000;
    protected static final String TAG = "logq-advengn";
    private AdventureContext mAdv;
    private boolean mCancelled = false;
    private Context mContext;
    private GameContext mGame;
    private List<CommonLog.LogChar> mLastLogChars;
    private List<AdventureLog.LogInventory> mLastLogInventories;
    private LogManagement mLogManagement;
    private GameContext mOriginalGame;
    private Persister mPersister;
    private int mProgressCaptured;
    private int mProgressNotCaptured;
    private Random mRandom;
    private Thrower mThrower;

    private void leaveBlock() {
    }

    private void returningToBlock() {
    }

    protected static class ProcessResult {
        public FightArgument fightArgument;
        public boolean progressTimer;

        public ProcessResult(boolean z, FightArgument fightArgument) {
            this.progressTimer = z;
            this.fightArgument = fightArgument;
        }

        public static ProcessResult createNoProgress() {
            return new ProcessResult(false, null);
        }

        public static ProcessResult createProgress() {
            return new ProcessResult(true, null);
        }

        public boolean hasFightArgument() {
            return this.fightArgument != null;
        }
    }

    protected static class ExploringThrowResult {
        public boolean success;
        public PlayerChar successPc;

        protected ExploringThrowResult() {
        }
    }

    public AdventureEngine(Context context) {
        this.mContext = context;
    }

    public void execute(GameContext gameContext, int i, boolean z) throws SQLException {
        Tactics tacticsNormal;
        this.mPersister = new Persister(this.mContext);
        this.mOriginalGame = gameContext;
        this.mOriginalGame.targetFloor = i;
        this.mGame = this.mPersister.readGameContext();
        this.mGame.targetFloor = i;
        this.mAdv = new AdventureContext();
        this.mAdv.calendar = Calendar.getInstance();
        this.mAdv.calendar.set(13, 0);
        this.mAdv.calendar.set(14, 0);
        this.mAdv.dungeon = DungeonRepository.getDungeon(this.mContext, this.mGame.dungeonId).copy();
        AdventureContext adventureContext = this.mAdv;
        adventureContext.targetFloor = i;
        adventureContext.flags = new HashMap();
        AdventureContext adventureContext2 = this.mAdv;
        Context context = this.mContext;
        GameContext gameContext2 = this.mGame;
        adventureContext2.calcCharacterStatus(context, gameContext2, gameContext2.characters);
        this.mAdv.tacticsArrays[0] = (Tactics[]) this.mPersister.readEnabledTactics(0).toArray(new Tactics[0]);
        Arrays.sort(this.mAdv.tacticsArrays[0], Tactics.FLOOR_COMPARATOR);
        for (int i2 = 0; i2 < this.mGame.characters.size(); i2++) {
            PlayerChar playerChar = this.mGame.characters.get(i2);
            ArrayList<Tactics> enabledTactics = this.mPersister.readEnabledTactics(playerChar.f106id);
            if (enabledTactics != null && !enabledTactics.isEmpty()) {
                int i3 = i2 + 1;
                this.mAdv.tacticsArrays[i3] = (Tactics[]) enabledTactics.toArray(new Tactics[0]);
                Arrays.sort(this.mAdv.tacticsArrays[i3], Tactics.FLOOR_COMPARATOR);
            } else {
                this.mAdv.tacticsArrays[i2 + 1] = null;
            }
            this.mAdv.advancedFightTacticsForChar[i2] = AdvancedTactics.TacticsComposition.fromAdvancedTacticsList(this.mPersister.readAdvancedTactics(playerChar.f106id));
            AdvancedTactics.TacticsComposition.normalizeAdvancedTactics(this.mContext, playerChar, this.mAdv.advancedFightTacticsForChar[i2], this.mGame);
        }
        Tactics[] tacticsArr = this.mAdv.tacticsArrays[0];
        int length = tacticsArr.length;
        int i4 = 0;
        while (true) {
            if (i4 >= length) {
                tacticsNormal = null;
                break;
            }
            tacticsNormal = tacticsArr[i4];
            if (tacticsNormal.targetFloor == 0) {
                break;
            } else {
                i4++;
            }
        }
        if (tacticsNormal == null) {
            tacticsNormal = Tactics.normal();
        }
        this.mAdv.defaultTactics = tacticsNormal;
        for (int i5 = 0; i5 < this.mGame.characters.size(); i5++) {
            PlayerChar playerChar2 = this.mGame.characters.get(i5);
            if (playerChar2.getAvailableSkill(this.mContext, SkillDb.SKILL_ROGUE_HIDE) != null) {
                this.mAdv.hasHidingSkill = true;
            }
            this.mAdv.putSkillCustomizationsForPc(playerChar2.f106id, this.mPersister.readSkillCustomizationForPc(playerChar2.f106id));
        }
        GameContext gameContext3 = this.mGame;
        AdventureContext adventureContext3 = this.mAdv;
        gameContext3.adventureContext = adventureContext3;
        this.mOriginalGame.startTime = adventureContext3.calendar.getTime();
        GameContext gameContext4 = this.mOriginalGame;
        gameContext4.estimateTime = new Date(gameContext4.startTime.getTime() + (estimateTimeInMinutes(this.mOriginalGame) * MS_IN_MINUTE));
        GameContext gameContext5 = this.mOriginalGame;
        gameContext5.advCount = z ? 5 : gameContext5.advCount - 1;
        this.mOriginalGame.startRealtime = (SystemClock.elapsedRealtime() / MS_IN_MINUTE) * 60 * 1000;
        List<LogManagement> logManagements = this.mPersister.readLogManagements();
        int maxLogHistories = getMaxLogHistories();
        if (logManagements.size() >= maxLogHistories) {
            int size = (logManagements.size() - maxLogHistories) + 1;
            for (int i6 = 0; i6 < size; i6++) {
                this.mPersister.deleteLogs(logManagements.get(i6));
            }
        }
        this.mLogManagement = new LogManagement(this.mGame.characters);
        this.mLogManagement.dungeonId = this.mGame.dungeonId;
        LogManagement logManagement = this.mLogManagement;
        logManagement.targetFloor = i;
        logManagement.completed = false;
        this.mPersister.writeLogManagement(logManagement);
        this.mGame.restoreCharacterHpMp();
        this.mRandom = new Random();
        this.mThrower = new Thrower(this.mRandom);
        for (int i7 = 0; i7 < this.mGame.characters.size(); i7++) {
            this.mGame.characters.get(i7).index = i7;
        }
        this.mGame.setPlayerCharIndexToInventory();
        processStart();
        if (!this.mAdv.aborting) {
            processAdventure();
        }
        if (this.mCancelled) {
            return;
        }
        processEnd();
        Date time = this.mAdv.calendar.getTime();
        GameContext gameContext6 = this.mOriginalGame;
        gameContext6.returnTime = time;
        gameContext6.returnRealtime = gameContext6.startRealtime + (time.getTime() - this.mOriginalGame.startTime.getTime());
        this.mGame.getOrCreateFlag(GameFlag.Key.asType(GameFlag.FlagType.OTHER, "total_adventure_time")).addOptionAsInt(this.mAdv.turn);
        this.mGame.startTime = this.mOriginalGame.startTime;
        this.mGame.estimateTime = this.mOriginalGame.estimateTime;
        this.mGame.returnTime = this.mOriginalGame.returnTime;
        this.mGame.advCount = this.mOriginalGame.advCount;
        GameContext gameContext7 = this.mGame;
        gameContext7.startRealtime = 0L;
        gameContext7.returnRealtime = 0L;
        writeGameContextAtEnd();
        LogManagement logManagement2 = this.mLogManagement;
        logManagement2.completed = true;
        this.mPersister.writeOnAdventureCompleted(this.mOriginalGame, logManagement2);
        NotificationReceiver.notifyWhenReturning(this.mContext, this.mAdv.dungeon, this.mAdv.targetFloor, time);
        this.mGame.adventureContext = null;
    }

    private int getMaxLogHistories() {
        String string = PreferenceManager.getDefaultSharedPreferences(this.mContext).getString(this.mContext.getString(C0380R.string.pref_key_log_history_number), null);
        if (TextUtils.isEmpty(string)) {
            return 5;
        }
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException unused) {
            return 5;
        }
    }

    public void setProgressAmount(GameContext gameContext, AdventureContext adventureContext) {
        int i = 0;
        int iMin = 18;
        int i2 = 0;
        for (PlayerChar playerChar : gameContext.characters) {
            if (playerChar.isAlive()) {
                iMin = Math.min(iMin, playerChar.agi);
            } else {
                i2++;
            }
        }
        int fixed10AttrBonus = ((EngineUtil.getFixed10AttrBonus(iMin) * 2) / 5) + 20;
        if (i2 > 0) {
            fixed10AttrBonus -= fixed10AttrBonus / 3;
        }
        if (i2 > 1) {
            fixed10AttrBonus -= fixed10AttrBonus / 3;
        }
        TreeSet<PlayerChar> treeSet = new TreeSet(PlayerChar.LEVEL_DESC_COMPARATOR);
        LinkedList linkedList = new LinkedList();
        for (PlayerChar playerChar2 : gameContext.getAliveChars()) {
            if (playerChar2.getSubLevel(GameChar.SubClass.ROGUE) > 0) {
                treeSet.add(playerChar2);
            } else {
                linkedList.add(playerChar2);
            }
        }
        int i3 = 0;
        int fixed10AttrBonus2 = 0;
        int fixed10LevelBonus = 0;
        for (PlayerChar playerChar3 : treeSet) {
            i3++;
            int i4 = 3;
            if (i3 <= 3) {
                i4 = i3;
            }
            int subLevel = playerChar3.getSubLevel(GameChar.SubClass.ROGUE);
            fixed10AttrBonus2 += EngineUtil.getFixed10AttrBonus(playerChar3.intl) / i4;
            fixed10LevelBonus += EngineUtil.getFixed10LevelBonus(subLevel) / i4;
        }
        Iterator it = linkedList.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            PlayerChar playerChar4 = (PlayerChar) it.next();
            fixed10AttrBonus2 += EngineUtil.getFixed10AttrBonus(playerChar4.intl) / 4;
            fixed10LevelBonus += EngineUtil.getFixed10LevelBonus(playerChar4.level) / 4;
        }
        int i5 = ((fixed10LevelBonus + fixed10AttrBonus2) + 100) / this.mAdv.dungeon.difficulty;
        if (i2 > 0) {
            i5 -= i5 / 3;
        }
        if (i2 > 1) {
            i5 -= i5 / 3;
        }
        int i6 = i5 >= 4 ? i5 : 4;
        if (adventureContext != null) {
            for (Enchant enchant : adventureContext.enchants) {
                if (enchant.otherEffect == Enchant.OtherEffect.MOVEMENT) {
                    i += enchant.value;
                }
            }
            fixed10AttrBonus = (fixed10AttrBonus * (i + 100)) / 100;
        }
        if (i6 > fixed10AttrBonus) {
            i6 = fixed10AttrBonus;
        }
        this.mProgressCaptured = fixed10AttrBonus;
        this.mProgressNotCaptured = i6;
    }

    public static int estimateTimeInMinutesStatic(Context context, GameContext gameContext) {
        AdventureEngine adventureEngine = new AdventureEngine(context);
        adventureEngine.mAdv = new AdventureContext();
        Dungeon dungeon = DungeonRepository.getDungeon(context, gameContext.dungeonId);
        adventureEngine.mAdv.dungeon = FlagEngine.enhanceExtraDungeonInInitialize(context, gameContext, dungeon);
        adventureEngine.mAdv.calcCharacterStatus(context, gameContext, gameContext.characters);
        return adventureEngine.estimateTimeInMinutes(gameContext);
    }

    public int estimateTimeInMinutes(GameContext gameContext) {
        setProgressAmount(gameContext, null);
        int iCeil = 0;
        int iCeil2 = 0;
        for (DungeonStat dungeonStat : gameContext.dungeonContext.stats) {
            if (dungeonStat.floor > gameContext.targetFloor) {
                break;
            }
            int i = dungeonStat.captiveRate;
            iCeil += ((int) Math.ceil(i / this.mProgressCaptured)) + ((int) Math.ceil((100 - i) / this.mProgressNotCaptured));
            iCeil2 += (int) Math.ceil(100.0f / this.mProgressCaptured);
        }
        return iCeil + iCeil2;
    }

    private void writeGameContextAtEnd() {
        if (this.mGame.areAllCharsDied() || this.mAdv.fightResult == FightEngine.FightResult.LOSE) {
            rollback();
        }
        this.mAdv.setTemporaryInventoryIdToNothing(this.mGame.inventories);
        this.mPersister.writeAdventureResult(this.mGame, this.mOriginalGame.flags);
    }

    private void rollback() {
        int i = 0;
        while (i < this.mGame.inventories.size()) {
            Inventory inventory = this.mGame.inventories.get(i);
            if (!this.mAdv.isTemporaryInventory(inventory) || inventory.isEventItem(this.mContext)) {
                i++;
            } else {
                this.mGame.inventories.remove(i);
            }
        }
        this.mGame.characters = this.mOriginalGame.characters;
        for (Map.Entry<GameFlag.Key, GameFlag> entry : this.mGame.flags.entrySet()) {
            GameFlag value = entry.getValue();
            if (value.type == GameFlag.FlagType.ITEM) {
                GameFlag gameFlag = this.mOriginalGame.flags.get(entry.getKey());
                int optionAsInt = gameFlag == null ? 0 : gameFlag.getOptionAsInt();
                if (value.getOptionAsInt() != optionAsInt && !ItemRepository.getItemBySymbol(this.mContext, value.name).isEventItem()) {
                    value.setOptionAsInt(optionAsInt);
                }
            }
        }
    }

    private ProcessResult processStart() {
        writeLog(createAdventureLog(CommonLog.LogType.START, this.mContext.getString(C0380R.string.alog_title_start), this.mContext.getString(C0380R.string.alog_desc_start, this.mAdv.dungeon.name, Integer.valueOf(this.mAdv.targetFloor))));
        return processFlags(Decision.Timing.START_ADVENTURE);
    }

    private void recalcPlayerStatusIfNecessary(FlagEngine.EngineResult engineResult) {
        if (engineResult == null || !engineResult.needsRecalc) {
            return;
        }
        AdventureContext adventureContext = this.mAdv;
        Context context = this.mContext;
        GameContext gameContext = this.mGame;
        adventureContext.calcCharacterStatus(context, gameContext, gameContext.characters);
    }

    private ProcessResult processFlags(Decision.Timing timing) {
        LinkedList<AdventureLog> linkedList = new LinkedList();
        FlagEngine.EngineResult engineResultProcessInAdventure = FlagEngine.processInAdventure(this.mContext, this.mRandom, this.mGame, timing, linkedList);
        for (AdventureLog adventureLog : linkedList) {
            addInfoToAdventureLog(adventureLog);
            writeLog(adventureLog);
        }
        recalcPlayerStatusIfNecessary(engineResultProcessInAdventure);
        if (engineResultProcessInAdventure == null) {
            return null;
        }
        if (engineResultProcessInAdventure.fightArgument != null) {
            return new ProcessResult(false, engineResultProcessInAdventure.fightArgument);
        }
        return ProcessResult.createNoProgress();
    }

    private void writeLog(AdventureLog adventureLog) {
        if (CommonLog.LogChar.equals(this.mLastLogChars, adventureLog.logChars)) {
            adventureLog.logChars = null;
        } else {
            this.mLastLogChars = adventureLog.logChars;
        }
        if (AdventureLog.LogInventory.equals(this.mLastLogInventories, adventureLog.logInventories)) {
            adventureLog.logInventories = null;
        } else {
            this.mLastLogInventories = adventureLog.logInventories;
        }
        this.mPersister.writeAdventureLog(this.mLogManagement, adventureLog);
    }

    private AdventureLog addInfoToAdventureLog(AdventureLog adventureLog) {
        adventureLog.gold = this.mGame.gold;
        adventureLog.logTime = CommonLog.calcLogTime(this.mAdv.calendar.getTimeInMillis());
        adventureLog.setLogCharacters(this.mGame.characters);
        adventureLog.setLogInventories(this.mGame.inventories);
        return adventureLog;
    }

    private AdventureLog createAdventureLog() {
        AdventureLog adventureLog = new AdventureLog(this.mGame, this.mAdv.calendar);
        adventureLog.setLogCharacters(this.mGame.characters);
        adventureLog.setLogInventories(this.mGame.inventories);
        return adventureLog;
    }

    private AdventureLog createAdventureLog(CommonLog.LogType logType, String str, String str2) {
        return createAdventureLog(logType, null, str, str2, null);
    }

    private AdventureLog createAdventureLog(CommonLog.LogType logType, PlayerChar playerChar, String str, String str2) {
        return createAdventureLog(logType, playerChar, str, str2, null);
    }

    private AdventureLog createAdventureLog(CommonLog.LogType logType, PlayerChar playerChar, String str, String str2, String str3) {
        AdventureLog adventureLogCreateAdventureLog = createAdventureLog();
        adventureLogCreateAdventureLog.type = logType;
        adventureLogCreateAdventureLog.title = str;
        adventureLogCreateAdventureLog.desc1 = str2;
        adventureLogCreateAdventureLog.desc2 = str3;
        adventureLogCreateAdventureLog.playerChar = playerChar;
        return adventureLogCreateAdventureLog;
    }

    private void processAdventure() {
        AdventureContext adventureContext = this.mAdv;
        adventureContext.inBlockProgress = 0;
        setProgressAmount(this.mGame, adventureContext);
        AdventureContext adventureContext2 = this.mAdv;
        adventureContext2.floor = 0;
        adventureContext2.block = 0;
        adventureContext2.inBlockProgress = 0;
        adventureContext2.inBlockProgressAtStart = 0;
        adventureContext2.isConquredAtStart = false;
        adventureContext2.success = false;
        enterDungeon();
        this.mAdv.floor = 1;
        while (this.mAdv.floor <= this.mAdv.targetFloor && this.mAdv.isAhead()) {
            AdventureContext adventureContext3 = this.mAdv;
            adventureContext3.block = 0;
            adventureContext3.inBlockProgressAtStart = 0;
            adventureContext3.isConquredAtStart = false;
            enterFloor();
            if (!this.mAdv.isAhead()) {
                break;
            }
            this.mAdv.block = 1;
            while (this.mAdv.block <= this.mAdv.dungeon.block && this.mAdv.isAhead()) {
                this.mAdv.dungeonStat = this.mGame.dungeonContext.getStat(this.mAdv.dungeon, this.mAdv.floor, this.mAdv.block);
                AdventureContext adventureContext4 = this.mAdv;
                adventureContext4.isConquredAtStart = adventureContext4.dungeonStat.isConqured();
                this.mAdv.inBlockProgressAtStart = 0;
                enterBlock();
                if (!this.mAdv.isAhead()) {
                    break;
                }
                AdventureContext adventureContext5 = this.mAdv;
                adventureContext5.inBlockProgress = 0;
                adventureContext5.inBlockProgress = 0;
                while (true) {
                    if (this.mAdv.inBlockProgress >= 100 || !this.mAdv.isAhead()) {
                        break;
                    }
                    processAdventureMain();
                    if (this.mCancelled) {
                        this.mAdv.aborting = true;
                        break;
                    }
                }
                if (!this.mAdv.isAhead()) {
                    break;
                }
                leaveBlock();
                if (!this.mAdv.isAhead()) {
                    break;
                }
                this.mAdv.block++;
            }
            if (!this.mAdv.isAhead()) {
                break;
            }
            leaveFloor();
            if (!this.mAdv.isAhead()) {
                break;
            }
            this.mGame.getOrCreateFlag(GameFlag.Key.asClearFloor(this.mAdv.dungeon, this.mAdv.floor)).setValue(true).addOptionAsInt(1);
            this.mAdv.floor++;
        }
        if (this.mAdv.aborting) {
            return;
        }
        if (!this.mAdv.returning) {
            if (this.mAdv.floor > this.mAdv.dungeon.floor) {
                clearDungeon();
                if (!this.mAdv.aborting && !this.mAdv.returning) {
                    this.mGame.getOrCreateFlag(GameFlag.Key.asClearDungeon(this.mAdv.dungeon)).setValue(true).addOptionAsInt(1);
                }
            } else {
                leaveTargetFloor();
            }
            if (this.mAdv.aborting) {
                return;
            }
            AdventureContext adventureContext6 = this.mAdv;
            adventureContext6.success = true;
            adventureContext6.floor--;
            this.mAdv.block--;
        }
        AdventureContext adventureContext7 = this.mAdv;
        adventureContext7.returning = true;
        int i = adventureContext7.block;
        int i2 = this.mAdv.floor;
        AdventureContext adventureContext8 = this.mAdv;
        adventureContext8.inBlockProgressAtStart = adventureContext8.inBlockProgress;
        while (this.mAdv.floor >= 1 && !this.mAdv.aborting) {
            if (this.mAdv.floor != i2) {
                AdventureContext adventureContext9 = this.mAdv;
                adventureContext9.block = adventureContext9.dungeon.block;
                returningToFloor();
            }
            while (this.mAdv.block >= 1 && !this.mAdv.aborting) {
                if (this.mAdv.floor != i2 || this.mAdv.block != i) {
                    AdventureContext adventureContext10 = this.mAdv;
                    adventureContext10.inBlockProgress = 100;
                    adventureContext10.inBlockProgressAtStart = 100;
                    returningToBlock();
                }
                this.mAdv.dungeonStat = this.mGame.dungeonContext.getStat(this.mAdv.dungeon, this.mAdv.floor, this.mAdv.block);
                while (true) {
                    if (this.mAdv.inBlockProgress <= 0 || this.mAdv.aborting) {
                        break;
                    }
                    processAdventureMain();
                    if (this.mCancelled) {
                        this.mAdv.aborting = true;
                        break;
                    }
                }
                if (this.mAdv.aborting) {
                    break;
                }
                this.mAdv.block--;
            }
            if (this.mAdv.aborting) {
                break;
            }
            this.mAdv.floor--;
        }
        if (this.mGame.areAllCharsDied()) {
            return;
        }
        leaveDungeon();
    }

    private boolean processTreasure() {
        int i = 0;
        for (MonsterPop monsterPop : this.mAdv.dungeon.floorRandomMonster.get(this.mAdv.floor - 1)) {
            Monster monster = MonsterRepository.getMonster(this.mContext, monsterPop.monsterId);
            if (monster.randomItemDrops != null) {
                int i2 = i;
                for (ItemDrop itemDrop : monster.randomItemDrops) {
                    i2 += monsterPop.factor * itemDrop.factor;
                }
                i = i2;
            }
        }
        if (i == 0) {
            return false;
        }
        int iNextInt = this.mRandom.nextInt(i);
        int i3 = iNextInt;
        ItemDrop itemDrop2 = null;
        for (MonsterPop monsterPop2 : this.mAdv.dungeon.floorRandomMonster.get(this.mAdv.floor - 1)) {
            Monster monster2 = MonsterRepository.getMonster(this.mContext, monsterPop2.monsterId);
            if (monster2.randomItemDrops != null) {
                ItemDrop[] itemDropArr = monster2.randomItemDrops;
                int length = itemDropArr.length;
                int i4 = i3;
                int i5 = 0;
                while (true) {
                    if (i5 >= length) {
                        i3 = i4;
                        break;
                    }
                    ItemDrop itemDrop3 = itemDropArr[i5];
                    int i6 = monsterPop2.factor * itemDrop3.factor;
                    if (i4 < i6) {
                        i3 = i4;
                        itemDrop2 = itemDrop3;
                        break;
                    }
                    i4 -= i6;
                    i5++;
                }
            }
        }
        if (itemDrop2 != null) {
            Item item = ItemRepository.getItem(this.mContext, itemDrop2.itemId);
            if (TownFlagEngine.getChangedClass(item) != null) {
                return false;
            }
            StringBuilder sb = new StringBuilder();
            if (this.mRandom.nextInt(1000) < this.mAdv.dungeon.treasureTrapFactor) {
                new TrapEngine(this.mContext, (TrapEngine.TrapType) EngineUtil.getElementRandom(TrapEngine.TrapType.values(), this.mRandom), this.mAdv, this.mAdv.dungeon.difficulty, this.mGame.getAliveChars(), this.mRandom).processTreasureBox(sb);
            } else {
                new TrapEngine(this.mContext, null, this.mAdv, 0, this.mGame.getAliveChars(), this.mRandom).processTreasureBox(sb);
            }
            Inventory inventoryCreateTemporaryInventory = this.mAdv.createTemporaryInventory();
            inventoryCreateTemporaryInventory.itemId = itemDrop2.itemId;
            inventoryCreateTemporaryInventory.setRandomEnchant(item, itemDrop2.enchantMax, this.mRandom);
            this.mGame.inventories.add(inventoryCreateTemporaryInventory);
            this.mGame.getOrCreateFlag(GameFlag.Key.asItemGot(item)).addOptionAsInt(1);
            CommonLog.LogType logType = CommonLog.LogType.TREASURE;
            String string = this.mContext.getString(C0380R.string.alog_title_treasure);
            Context context = this.mContext;
            sb.append(context.getString(C0380R.string.alog_desc_treasure, inventoryCreateTemporaryInventory.getName(context)));
            writeLog(createAdventureLog(logType, string, sb.toString()));
        }
        return true;
    }

    private boolean processInventoryMax() {
        int i;
        if (this.mGame.inventories.size() <= this.mGame.getMaxInventoryCount()) {
            return false;
        }
        Tactics.FullInventoryTactics fullInventoryTactics = this.mAdv.getCurrentTacticsForParty().fullInventory;
        PlayerChar playerChar = null;
        Inventory inventory = null;
        int i2 = -10000;
        for (Inventory inventory2 : this.mGame.inventories) {
            if (inventory2.equippedCharId == 0 && !inventory2.isEventItem(this.mContext)) {
                int sellPrice = inventory2.getSellPrice(this.mContext);
                Item baseItem = inventory2.getBaseItem(this.mContext);
                if (fullInventoryTactics == Tactics.FullInventoryTactics.HOLD_ENCHANTED) {
                    if (inventory2.extraEffects != null && !inventory2.extraEffects.isEmpty()) {
                        sellPrice += (inventory2.extraEffects.size() * 2500) + 5000;
                    }
                } else if (baseItem.artifact) {
                    sellPrice += 10000;
                }
                if (fullInventoryTactics == Tactics.FullInventoryTactics.DROP_CONSUMABLE && baseItem.type == Item.Type.CONSUMABLE) {
                    sellPrice -= 10000;
                }
                if (inventory == null || i2 > sellPrice) {
                    inventory = inventory2;
                    i2 = sellPrice;
                }
            }
        }
        if (inventory == null) {
            return false;
        }
        Item baseItem2 = inventory.getBaseItem(this.mContext);
        if (baseItem2.type == Item.Type.CONSUMABLE) {
            int i3 = 0;
            for (PlayerChar playerChar2 : this.mGame.characters) {
                if (playerChar2.isAlive()) {
                    if (baseItem2.getHpRestoreEffect() != null) {
                        i = playerChar2.maxHp - playerChar2.f93hp;
                    } else {
                        i = baseItem2.getMpRestoreEffect() != null ? playerChar2.maxMp - playerChar2.f94mp : 0;
                    }
                    if (i > i3) {
                        playerChar = playerChar2;
                        i3 = i;
                    }
                }
            }
        }
        if (playerChar != null) {
            processItem(playerChar, inventory, true);
        } else {
            processDropItem(inventory);
        }
        return true;
    }

    private void processEveryTurn() {
        for (int i = 0; i < this.mGame.characters.size(); i++) {
            PlayerChar playerChar = this.mGame.characters.get(i);
            if (playerChar.isAlive() && playerChar.f94mp < playerChar.maxMp) {
                int i2 = this.mAdv.dungeon.difficulty - (this.mAdv.turn - this.mAdv.mpRestoreTurn[i]);
                Thrower.ThrowResult throwResultGenericThrow = this.mThrower.genericThrow(playerChar, GameChar.Attribute.INT);
                if (throwResultGenericThrow.value >= i2 || throwResultGenericThrow.critical) {
                    playerChar.f94mp += (playerChar.f94mp / 20) + 1;
                    if (playerChar.f94mp > playerChar.maxMp) {
                        playerChar.f94mp = playerChar.maxMp;
                    }
                    this.mAdv.mpRestoreTurn[i] = this.mAdv.turn;
                }
            }
        }
        if (this.mAdv.tickEnchants()) {
            setProgressAmount(this.mGame, this.mAdv);
        }
    }

    private boolean evalRest() {
        boolean z = this.mAdv.dungeonStat.blockType == DungeonStat.BlockType.MONSTER && !this.mAdv.dungeonStat.isConqured() && this.mAdv.dungeonStat.knowBlockType;
        if (z && !this.mAdv.hasHidingSkill) {
            return false;
        }
        boolean z2 = this.mAdv.dungeonStat.blockType == DungeonStat.BlockType.SAFE_ZONE && this.mAdv.dungeonStat.knowBlockType;
        for (PlayerChar playerChar : this.mGame.characters) {
            if (playerChar.isAlive()) {
                Tactics currentTacticsForChar = this.mAdv.getCurrentTacticsForChar(playerChar.index);
                if (currentTacticsForChar.rest == Tactics.TacticsValue.NONE) {
                    continue;
                } else {
                    int i = (((playerChar.maxHp - playerChar.f93hp) * 250) / playerChar.maxHp) + (((playerChar.maxMp - playerChar.f94mp) * 250) / playerChar.maxMp);
                    if (playerChar.f93hp < playerChar.maxHp / 4) {
                        i += 50;
                    }
                    if (playerChar.f94mp < playerChar.maxMp / 6) {
                        i += 25;
                    }
                    if (z2) {
                        i += i / 4;
                    } else if (z) {
                        i -= 20;
                    }
                    if (i >= 420 - (currentTacticsForChar.rest.getParameter() * 80)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean evalReturn(boolean z) {
        int i = 0;
        for (PlayerChar playerChar : this.mGame.characters) {
            Tactics currentTacticsForChar = this.mAdv.getCurrentTacticsForChar(playerChar.index);
            if (currentTacticsForChar.abort != Tactics.TacticsValue.NONE) {
                if (playerChar.isAlive()) {
                    int i2 = (((playerChar.maxHp - playerChar.f93hp) * 250) / playerChar.maxHp) + (((playerChar.maxMp - playerChar.f94mp) * MonsterDb.MONSTER_GHOUL) / playerChar.maxMp);
                    if (z) {
                        i2 -= 200;
                    }
                    if (i2 >= (currentTacticsForChar.abort.getParameter() * 60) + MonsterDb.MONSTER_BLACK_PUDDING) {
                        i++;
                    }
                } else {
                    if (currentTacticsForChar.abort != Tactics.TacticsValue.AGGRESSIVE) {
                        return true;
                    }
                    i++;
                }
            }
        }
        if (i == 0) {
            return false;
        }
        return this.mGame.characters.size() == 1 || i >= 2;
    }

    private boolean handleProgressResult(ProcessResult processResult) {
        handleResultTick(handleResultFighting(processResult));
        return isAdventureContinued();
    }

    private ProcessResult handleResultFighting(ProcessResult processResult) {
        if (processResult != null && processResult.hasFightArgument()) {
            this.mAdv.fightResult = processFight(processResult.fightArgument);
            processResult.progressTimer = processResult.progressTimer && !this.mGame.areAllCharsDied();
            setProgressAmount(this.mGame, this.mAdv);
        }
        return processResult;
    }

    private ProcessResult handleResultTick(ProcessResult processResult) {
        if (processResult != null && processResult.progressTimer) {
            LogStatus logStatus = new LogStatus(this.mAdv.calendar);
            logStatus.floor = this.mAdv.floor;
            logStatus.block = this.mAdv.block;
            logStatus.captiveRate = this.mAdv.inBlockProgressAtStart;
            if (processResult.hasFightArgument()) {
                logStatus.action = LogStatus.LogAction.FIGHTING;
            } else if (this.mAdv.returning) {
                logStatus.action = LogStatus.LogAction.RETURNING;
            } else if (this.mAdv.dungeonStat != null && this.mAdv.isConquredAtStart) {
                logStatus.action = LogStatus.LogAction.MOVING;
            } else {
                logStatus.action = LogStatus.LogAction.EXPLORING;
            }
            this.mPersister.writeLogStatus(this.mLogManagement, logStatus);
            this.mAdv.calendar.add(12, 1);
            this.mAdv.turn++;
            AdventureContext adventureContext = this.mAdv;
            adventureContext.inBlockProgressAtStart = adventureContext.inBlockProgress;
            processEveryTurn();
        }
        return processResult;
    }

    private boolean isAdventureContinued() {
        this.mAdv.aborting |= this.mGame.areAllCharsDied();
        return !this.mAdv.aborting;
    }

    private ProcessResult processPlayerAction() {
        ActionEvaluation actionEvaluationAction;
        ProcessResult processResultCreateNoProgress = null;
        for (PlayerChar playerChar : this.mGame.characters) {
            if (playerChar.isAlive() && (actionEvaluationAction = action(playerChar)) != null) {
                if (actionEvaluationAction.action == ActionEvaluation.Action.SKILL_MAGIC) {
                    processSkill(playerChar, actionEvaluationAction);
                    processResultCreateNoProgress = ProcessResult.createNoProgress();
                } else if (actionEvaluationAction.action == ActionEvaluation.Action.USE_ITEM) {
                    processItem(playerChar, actionEvaluationAction);
                    processResultCreateNoProgress = ProcessResult.createNoProgress();
                }
            }
        }
        return processResultCreateNoProgress;
    }

    private ActionEvaluation action(GameChar gameChar) {
        Tactics currentTacticsForChar = this.mAdv.getCurrentTacticsForChar(gameChar.index);
        TreeSet treeSet = new TreeSet();
        evaluateSkills(gameChar, currentTacticsForChar, treeSet);
        evaluateItems(gameChar, currentTacticsForChar, treeSet);
        if (treeSet.isEmpty()) {
            return null;
        }
        ActionEvaluation actionEvaluationFirst = treeSet.first();
        if (actionEvaluationFirst.value < 500) {
            return null;
        }
        return actionEvaluationFirst;
    }

    private void evaluateSkills(GameChar gameChar, Tactics tactics, SortedSet<ActionEvaluation> sortedSet) {
        for (Skill skill : gameChar.getAvailableSkills(this.mContext)) {
            if (skill.context != Skill.SkillContext.FIGHT && skill.canUse(gameChar)) {
                switch (skill.type) {
                    case ADD_ATTACK:
                    case DAMAGE:
                    case DAMAGE_ALL:
                    case STATUS:
                    case STATUS_ALL:
                    case MY_STATUS:
                        throw new IllegalStateException("This skill cannot be candidate in adventure:" + skill.f107id);
                    case CURE:
                        if (tactics.cureSkill != Tactics.TacticsValue.NONE) {
                            evaluateSkillCure(gameChar, tactics.cureSkill, sortedSet, skill);
                            break;
                        } else {
                            break;
                        }
                    case CURE_ALL:
                        if (tactics.cureSkill != Tactics.TacticsValue.NONE) {
                            evaluateSkillCureAll(gameChar, tactics.cureSkill, sortedSet, skill);
                            break;
                        } else {
                            break;
                        }
                    case OTHER:
                        if (tactics.statusSkill != Tactics.TacticsValue.NONE) {
                            evaluateSkillOther(gameChar, tactics.statusSkill, sortedSet, skill);
                            break;
                        } else {
                            break;
                        }
                }
            }
        }
    }

    private void evaluateSkillCure(GameChar gameChar, Tactics.TacticsValue tacticsValue, SortedSet<ActionEvaluation> sortedSet, Skill skill) {
        for (PlayerChar playerChar : this.mGame.characters) {
            if (playerChar.isAlive()) {
                boolean z = playerChar.f93hp < playerChar.maxHp / 4;
                boolean z2 = playerChar.f93hp < playerChar.maxHp / 2;
                int fixed10AttrBonus = ((skill.attrBase + (skill.diceFace * skill.diceNum)) * 10) + EngineUtil.getFixed10AttrBonus(gameChar.intl);
                int fixed10AttrBonus2 = ((skill.attrBase + skill.diceNum) * 10) + EngineUtil.getFixed10AttrBonus(gameChar.intl);
                int i = (fixed10AttrBonus + fixed10AttrBonus2) / 20;
                if (z || playerChar.maxHp - playerChar.f93hp >= fixed10AttrBonus2 / 10) {
                    if (z || z2 || playerChar.maxHp - playerChar.f93hp >= i) {
                        int iMin = (Math.min((playerChar.maxHp - playerChar.f93hp) * 10, fixed10AttrBonus) + fixed10AttrBonus2) * 2;
                        if (playerChar.maxHp - playerChar.f93hp < i) {
                            iMin /= 2;
                        }
                        if (z) {
                            iMin += 1000;
                        } else if (z2) {
                            iMin += tacticsValue.getParameter() * 200;
                        }
                        sortedSet.add(new ActionEvaluation(iMin - (skill.f108mp * 5), playerChar, skill));
                    }
                }
            }
        }
    }

    private void evaluateSkillCureAll(GameChar gameChar, Tactics.TacticsValue tacticsValue, SortedSet<ActionEvaluation> sortedSet, Skill skill) {
        TreeSet treeSet = new TreeSet();
        evaluateSkillCure(gameChar, tacticsValue, treeSet, skill);
        if (treeSet.isEmpty()) {
            return;
        }
        int i = 0;
        Iterator<ActionEvaluation> it = treeSet.iterator();
        int i2 = 1;
        while (it.hasNext()) {
            i += it.next().value / i2;
            i2++;
        }
        sortedSet.add(new ActionEvaluation(i, (GameChar) null, skill));
    }

    private void evaluateSkillOther(GameChar gameChar, Tactics.TacticsValue tacticsValue, SortedSet<ActionEvaluation> sortedSet, Skill skill) {
        int i = skill.f107id;
        if (i == 20010) {
            evaluateDetectMonster(gameChar, tacticsValue, sortedSet, skill);
        } else if (i == 30100) {
            evaluateCalm(gameChar, tacticsValue, sortedSet, skill);
        } else {
            if (i != 30110) {
                return;
            }
            evaluateHaste(gameChar, tacticsValue, sortedSet, skill);
        }
    }

    private void evaluateDetectMonster(GameChar gameChar, Tactics.TacticsValue tacticsValue, SortedSet<ActionEvaluation> sortedSet, Skill skill) {
        Iterator<Enchant> it = this.mAdv.enchantsPlayers[gameChar.index].iterator();
        while (it.hasNext()) {
            if (it.next().causeSkillId == skill.f107id) {
                return;
            }
        }
        evaluateSupportingSkill(gameChar, tacticsValue, sortedSet, skill);
    }

    private void evaluateHaste(GameChar gameChar, Tactics.TacticsValue tacticsValue, SortedSet<ActionEvaluation> sortedSet, Skill skill) {
        evaluateSupportingSkill(gameChar, tacticsValue, sortedSet, skill);
    }

    private void evaluateCalm(GameChar gameChar, Tactics.TacticsValue tacticsValue, SortedSet<ActionEvaluation> sortedSet, Skill skill) {
        evaluateSupportingSkill(gameChar, tacticsValue, sortedSet, skill);
    }

    private void evaluateSupportingSkill(GameChar gameChar, Tactics.TacticsValue tacticsValue, SortedSet<ActionEvaluation> sortedSet, Skill skill) {
        Iterator<Enchant> it = this.mAdv.enchants.iterator();
        while (it.hasNext()) {
            if (it.next().causeSkillId == skill.f107id) {
                return;
            }
        }
        if (this.mAdv.dungeonStat.isConqured() || skill.f107id != 30110) {
            if (tacticsValue != Tactics.TacticsValue.MODERATE || gameChar.f94mp >= gameChar.maxMp / 2) {
                if (tacticsValue != Tactics.TacticsValue.CONSERVATIVE || gameChar.f94mp >= (gameChar.maxMp * 3) / 4) {
                    sortedSet.add(new ActionEvaluation(500, (GameChar) null, skill));
                }
            }
        }
    }

    private void evaluateItems(GameChar gameChar, Tactics tactics, SortedSet<ActionEvaluation> sortedSet) {
        if (tactics.item == Tactics.TacticsValue.NONE) {
            return;
        }
        HashSet hashSet = new HashSet();
        for (Inventory inventory : this.mGame.inventories) {
            Integer numValueOf = Integer.valueOf(inventory.itemId);
            if (!hashSet.contains(numValueOf)) {
                hashSet.add(numValueOf);
                Item baseItem = inventory.getBaseItem(this.mContext);
                if (baseItem.type == Item.Type.CONSUMABLE) {
                    Item.Effect hpRestoreEffect = baseItem.getHpRestoreEffect();
                    if (hpRestoreEffect != null) {
                        evaluateRestoreHpItem(gameChar, tactics.item, sortedSet, inventory, baseItem, hpRestoreEffect);
                        return;
                    } else {
                        Item.Effect mpRestoreEffect = baseItem.getMpRestoreEffect();
                        if (mpRestoreEffect != null) {
                            evaluateRestoreMpItem(gameChar, tactics.item, sortedSet, inventory, baseItem, mpRestoreEffect);
                        }
                    }
                } else {
                    continue;
                }
            }
        }
    }

    private void evaluateRestoreHpItem(GameChar gameChar, Tactics.TacticsValue tacticsValue, Set<ActionEvaluation> set, Inventory inventory, Item item, Item.Effect effect) {
        int parameter;
        boolean z = gameChar.f93hp < gameChar.maxHp / 4;
        int i = gameChar.maxHp - gameChar.f93hp;
        int fixed10AttrBonus = EngineUtil.getFixed10AttrBonus(gameChar.getAttr(effect.attr));
        int i2 = ((item.attrBase + (item.diceFace * item.diceNum)) * 10) + fixed10AttrBonus;
        int i3 = ((item.attrBase + item.diceNum) * 10) + fixed10AttrBonus;
        int i4 = (i2 + i3) / 20;
        if (z || i >= i3 / 10) {
            int i5 = i * 10;
            int iMin = Math.min(i5, i2);
            int i6 = (i3 + iMin) * 2;
            if (i < i4) {
                i6 /= 2;
            } else if (i5 < iMin) {
                i6 -= i6 / 4;
            }
            if (z) {
                parameter = i6 + 1000;
            } else if (gameChar.f93hp < gameChar.maxHp / 2) {
                parameter = i6 + (tacticsValue.getParameter() * 200);
            } else {
                parameter = i6 - ((3 - tacticsValue.getParameter()) * 100);
            }
            int iLog = ((int) (Math.log((item.price + 40) / 20) * 30.0d)) - 30;
            if (iLog > 120) {
                iLog = MonsterDb.MONSTER_STIRGE;
            }
            set.add(new ActionEvaluation(parameter - iLog, gameChar, inventory));
        }
    }

    private void evaluateRestoreMpItem(GameChar gameChar, Tactics.TacticsValue tacticsValue, Set<ActionEvaluation> set, Inventory inventory, Item item, Item.Effect effect) {
        int parameter;
        if (gameChar.f94mp > gameChar.maxMp / 2) {
            return;
        }
        boolean z = gameChar.f94mp < gameChar.maxMp / 5;
        int i = gameChar.maxMp - gameChar.f94mp;
        int fixed10AttrBonus = EngineUtil.getFixed10AttrBonus(gameChar.getAttr(effect.attr));
        int i2 = ((item.attrBase + (item.diceFace * item.diceNum)) * 10) + fixed10AttrBonus;
        int i3 = ((item.attrBase + item.diceNum) * 10) + fixed10AttrBonus;
        int i4 = (i2 + i3) / 20;
        int i5 = i * 10;
        int iMin = Math.min(i5, i2);
        int i6 = (i3 + iMin) * 2;
        if (i < i4) {
            i6 /= 2;
        } else if (i5 < iMin) {
            i6 -= i6 / 4;
        }
        if (z) {
            parameter = i6 + 800;
        } else if (gameChar.f94mp < gameChar.maxMp / 3) {
            parameter = i6 + (tacticsValue.getParameter() * MonsterDb.MONSTER_GIANT_ANT);
        } else {
            parameter = i6 - ((3 - tacticsValue.getParameter()) * 80);
        }
        int iLog = ((int) (Math.log((item.price + 40) / 20) * 30.0d)) - 30;
        if (iLog > 100) {
            iLog = 100;
        }
        set.add(new ActionEvaluation(parameter - iLog, gameChar, inventory));
    }

    private void processSkill(PlayerChar playerChar, ActionEvaluation actionEvaluation) {
        Skill skill = SkillRepository.getSkill(this.mContext, actionEvaluation.skillId);
        switch (skill.type) {
            case ADD_ATTACK:
            case DAMAGE:
            case DAMAGE_ALL:
            case STATUS:
            case STATUS_ALL:
            case MY_STATUS:
                throw new IllegalStateException("Add attack skill is illegally called:" + skill);
            case CURE:
                processSkillCure(playerChar, actionEvaluation.targetChar, skill);
                return;
            case CURE_ALL:
                processSkillCureAll(playerChar, skill);
                return;
            case OTHER:
                processSkillOther(playerChar, actionEvaluation.targetChar, skill);
                return;
            default:
                return;
        }
    }

    private void processSkillCure(PlayerChar playerChar, GameChar gameChar, Skill skill) {
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(gameChar);
        processSkillCureCommon(playerChar, arrayList, skill);
    }

    private void processSkillCureAll(PlayerChar playerChar, Skill skill) {
        processSkillCureCommon(playerChar, this.mGame.characters, skill);
    }

    private void processSkillCureCommon(PlayerChar playerChar, List<? extends GameChar> list, Skill skill) {
        createAndAddSkillLog(playerChar, skill, EngineUtil.processSkillCure(playerChar, list, skill, this.mThrower, this.mRandom, this.mAdv, this.mContext));
    }

    private void createAndAddSkillLog(PlayerChar playerChar, Skill skill, String str) {
        writeLog(createAdventureLog(skill.isMagic() ? CommonLog.LogType.MAGIC : CommonLog.LogType.USE_SKILL, playerChar, this.mContext.getString(skill.isMagic() ? C0380R.string.alog_title_skill_magic : C0380R.string.alog_title_skill_normal, playerChar.name, this.mAdv.getSkillNameAwareCustomized(playerChar, skill)), str));
    }

    private void processSkillOther(GameChar gameChar, GameChar gameChar2, Skill skill) {
        int i = skill.f107id;
        if (i == 20010) {
            processDetectMonster(gameChar, skill);
            return;
        }
        if (i == 20020) {
            processHide(gameChar, skill);
        } else if (i == 30100) {
            processCalm(gameChar, skill);
        } else {
            if (i != 30110) {
                return;
            }
            processHaste(gameChar, skill);
        }
    }

    private void processDetectMonster(GameChar gameChar, Skill skill) {
        gameChar.f94mp -= skill.f108mp;
        this.mAdv.enchantsPlayers[gameChar.index].add(new Enchant(Enchant.Target.PLAYER_CHAR, Enchant.OtherEffect.DETECT, skill.attrBase, skill, skill.term));
        createAndAddSkillLog((PlayerChar) gameChar, skill, this.mAdv.getSkillUseAwareCustomized(this.mContext, gameChar, skill));
    }

    private void processHide(GameChar gameChar, Skill skill) {
        gameChar.f94mp -= skill.f108mp;
        this.mAdv.enchants.add(new Enchant(Enchant.Target.PARTY, Enchant.OtherEffect.HIDING, -skill.attrBase, skill, skill.term));
        createAndAddSkillLog((PlayerChar) gameChar, skill, this.mAdv.getSkillUseAwareCustomized(this.mContext, gameChar, skill));
    }

    private void processHaste(GameChar gameChar, Skill skill) {
        gameChar.f94mp -= skill.f108mp;
        this.mAdv.enchants.add(new Enchant(Enchant.Target.PARTY, Enchant.OtherEffect.MOVEMENT, skill.attrBase, skill, skill.term));
        createAndAddSkillLog((PlayerChar) gameChar, skill, this.mAdv.getSkillUseAwareCustomized(this.mContext, gameChar, skill));
        setProgressAmount(this.mGame, this.mAdv);
    }

    private void processCalm(GameChar gameChar, Skill skill) {
        gameChar.f94mp -= skill.f108mp;
        this.mAdv.enchants.add(new Enchant(Enchant.Target.PARTY, Enchant.OtherEffect.ENCOUNTER, -skill.attrBase, skill, skill.term));
        createAndAddSkillLog((PlayerChar) gameChar, skill, this.mAdv.getSkillUseAwareCustomized(this.mContext, gameChar, skill));
    }

    private void processItem(PlayerChar playerChar, ActionEvaluation actionEvaluation) {
        processItem(playerChar, this.mGame.getInventory(actionEvaluation.inventoryId), false);
    }

    private boolean processItem(PlayerChar playerChar, Inventory inventory, boolean z) {
        Item baseItem = inventory.getBaseItem(this.mContext);
        Item.Effect hpRestoreEffect = baseItem.getHpRestoreEffect();
        if (hpRestoreEffect != null) {
            processPlayerItemHpRestore(playerChar, inventory, hpRestoreEffect, z);
            return true;
        }
        Item.Effect mpRestoreEffect = baseItem.getMpRestoreEffect();
        if (mpRestoreEffect == null) {
            return false;
        }
        processPlayerItemMpRestore(playerChar, inventory, mpRestoreEffect, z);
        return true;
    }

    private void processDropItem(Inventory inventory) {
        this.mGame.inventories.remove(inventory);
        CommonLog.LogType logType = CommonLog.LogType.USE_ITEM;
        String string = this.mContext.getString(C0380R.string.alog_title_inventory_max_drop_item);
        Context context = this.mContext;
        writeLog(createAdventureLog(logType, string, context.getString(C0380R.string.alog_desc_inventory_max_drop_item, inventory.getName(context))));
    }

    private void processPlayerItemHpRestore(PlayerChar playerChar, Inventory inventory, Item.Effect effect, boolean z) {
        Item baseItem = inventory.getBaseItem(this.mContext);
        int iThrowDiceWithBonus = this.mThrower.throwDiceWithBonus(baseItem.diceNum, baseItem.diceFace, playerChar.getAttr(effect.attr)) + baseItem.attrBase;
        if (iThrowDiceWithBonus > playerChar.maxHp - playerChar.f93hp) {
            iThrowDiceWithBonus = playerChar.maxHp - playerChar.f93hp;
        }
        playerChar.f93hp += iThrowDiceWithBonus;
        this.mGame.inventories.remove(inventory);
        CommonLog.LogType logType = CommonLog.LogType.USE_ITEM;
        String string = this.mContext.getString(C0380R.string.flog_title_player_item, playerChar.name, inventory.getName(this.mContext));
        String string2 = this.mContext.getString(C0380R.string.flog_desc_player_item_hp_restore, Integer.valueOf(iThrowDiceWithBonus));
        if (z) {
            string2 = this.mContext.getString(C0380R.string.alog_desc_inventory_max_use_item) + this.mContext.getString(C0380R.string.res_sentence_separator) + string2;
        }
        writeLog(createAdventureLog(logType, playerChar, string, string2));
    }

    private void processPlayerItemMpRestore(PlayerChar playerChar, Inventory inventory, Item.Effect effect, boolean z) {
        Item baseItem = inventory.getBaseItem(this.mContext);
        int iThrowDiceWithBonus = this.mThrower.throwDiceWithBonus(baseItem.diceNum, baseItem.diceFace, playerChar.getAttr(effect.attr)) + baseItem.attrBase;
        if (iThrowDiceWithBonus > playerChar.maxMp - playerChar.f94mp) {
            iThrowDiceWithBonus = playerChar.maxMp - playerChar.f94mp;
        }
        playerChar.f94mp += iThrowDiceWithBonus;
        this.mGame.inventories.remove(inventory);
        CommonLog.LogType logType = CommonLog.LogType.USE_ITEM;
        String string = this.mContext.getString(C0380R.string.flog_title_player_item, playerChar.name, inventory.getName(this.mContext));
        String string2 = this.mContext.getString(C0380R.string.flog_desc_player_item_mp_restore, Integer.valueOf(iThrowDiceWithBonus));
        if (z) {
            string2 = this.mContext.getString(C0380R.string.alog_desc_inventory_max_use_item) + this.mContext.getString(C0380R.string.res_sentence_separator) + string2;
        }
        writeLog(createAdventureLog(logType, playerChar, string, string2));
    }

    private void enterDungeon() {
        ProcessResult processResultProcessFlags;
        do {
            processResultProcessFlags = processFlags(Decision.Timing.START_DUNGEON);
            if (processResultProcessFlags == null) {
                return;
            }
        } while (handleProgressResult(processResultProcessFlags));
    }

    private void clearDungeon() {
        this.mAdv.fightResult = null;
        while (true) {
            if (this.mAdv.fightResult == FightEngine.FightResult.RUN) {
                this.mAdv.returning = true;
                break;
            }
            ProcessResult processResultProcessFlags = processFlags(Decision.Timing.CLEAR_DUNGEON);
            if (processResultProcessFlags == null || !handleProgressResult(processResultProcessFlags)) {
                break;
            }
        }
        if (this.mAdv.aborting || this.mAdv.returning) {
            return;
        }
        writeLog(createAdventureLog(CommonLog.LogType.CLEAR_DUNGEON, this.mContext.getString(C0380R.string.alog_title_clear_dungeon, this.mAdv.dungeon.name), this.mContext.getString(C0380R.string.alog_desc_clear_dungeon_1)));
    }

    private void leaveDungeon() {
        ProcessResult processResultProcessFlags;
        do {
            processResultProcessFlags = processFlags(Decision.Timing.END_DUNGEON);
            if (processResultProcessFlags == null) {
                return;
            }
        } while (handleProgressResult(processResultProcessFlags));
    }

    private void processAdventureMain() {
        ProcessResult processResultProcessPlayerAction;
        ProcessResult processResultProcessEncounterNotCapturedBlock;
        ProcessResult processResultProcessWanderingMonster;
        int i;
        List<DungeonEvent> eventInRange;
        do {
            processResultProcessPlayerAction = processPlayerAction();
            if (processResultProcessPlayerAction == null) {
                break;
            }
        } while (handleProgressResult(processResultProcessPlayerAction));
        boolean zEvalRest = evalRest();
        if (!this.mAdv.returning) {
            if (evalReturn(zEvalRest)) {
                this.mAdv.returning = true;
                processDecideReturn(false);
                return;
            } else if (this.mAdv.getCurrentTacticsForParty().fullInventory == Tactics.FullInventoryTactics.RETURN && this.mGame.inventories.size() >= this.mGame.getMaxInventoryCount()) {
                this.mAdv.returning = true;
                processDecideReturn(true);
                return;
            }
        }
        boolean z = this.mAdv.dungeonStat.blockType != DungeonStat.BlockType.SAFE_ZONE;
        int iExploreNotCapturedBlock = this.mAdv.dungeonStat.captiveRate;
        int iProgressReturning = this.mAdv.inBlockProgress;
        boolean zIsConqured = this.mAdv.dungeonStat.isConqured();
        if (zEvalRest) {
            processResultProcessEncounterNotCapturedBlock = processRest();
        } else {
            processNotRest();
            if (this.mAdv.returning) {
                iProgressReturning = progressReturning();
                processResultProcessEncounterNotCapturedBlock = null;
            } else if (zIsConqured) {
                iProgressReturning = progressCapturedBlock();
                processResultProcessEncounterNotCapturedBlock = null;
            } else {
                z = this.mAdv.dungeonStat.blockType == DungeonStat.BlockType.NORMAL;
                iProgressReturning = progressNotCapturedBlock();
                iExploreNotCapturedBlock = exploreNotCapturedBlock();
                if (iProgressReturning < iExploreNotCapturedBlock) {
                    iProgressReturning = iExploreNotCapturedBlock;
                    processResultProcessEncounterNotCapturedBlock = null;
                } else {
                    processResultProcessEncounterNotCapturedBlock = null;
                }
            }
        }
        if (processResultProcessEncounterNotCapturedBlock == null) {
            processResultProcessEncounterNotCapturedBlock = processEncounterNotCapturedBlock(iExploreNotCapturedBlock);
        }
        if (iProgressReturning != this.mAdv.inBlockProgress) {
            if (iProgressReturning < this.mAdv.inBlockProgress) {
                eventInRange = this.mGame.dungeonContext.getEventInRange(this.mAdv.floor, this.mAdv.block, iProgressReturning, this.mAdv.inBlockProgress);
            } else {
                eventInRange = this.mGame.dungeonContext.getEventInRange(this.mAdv.floor, this.mAdv.block, this.mAdv.inBlockProgress, iProgressReturning);
            }
            if (!eventInRange.isEmpty()) {
                DungeonEvent dungeonEventProcessEvents = processEvents(eventInRange, iProgressReturning <= this.mAdv.dungeonStat.captiveRate);
                if (!isAdventureContinued()) {
                    if (dungeonEventProcessEvents != null) {
                        if (this.mAdv.dungeonStat.captiveRate < 100) {
                            this.mAdv.dungeonStat.captiveRate = dungeonEventProcessEvents.position;
                        }
                        this.mAdv.inBlockProgress = dungeonEventProcessEvents.position;
                        return;
                    }
                    return;
                }
                setProgressAmount(this.mGame, this.mAdv);
            }
        }
        int i2 = this.mAdv.dungeonStat.captiveRate;
        this.mAdv.dungeonStat.captiveRate = iExploreNotCapturedBlock;
        AdventureContext adventureContext = this.mAdv;
        adventureContext.inBlockProgress = iProgressReturning;
        if (processResultProcessEncounterNotCapturedBlock != null) {
            adventureContext.fightResult = null;
            handleProgressResult(processResultProcessEncounterNotCapturedBlock);
            if (this.mAdv.fightResult != null) {
                this.mAdv.dungeonStat.knowBlockType = true;
                if (this.mAdv.fightResult != FightEngine.FightResult.LOSE && this.mAdv.fightResult != FightEngine.FightResult.RUN && this.mAdv.dungeonStat.monsterNumber > 0) {
                    this.mAdv.dungeonStat.monsterNumber--;
                }
                if (this.mAdv.dungeonStat.monsterNumber > 0 && this.mAdv.dungeonStat.captiveRate > (i = 100 - ((this.mAdv.dungeonStat.monsterNumber * 100) / this.mAdv.dungeonStat.initialMonsterNumber))) {
                    this.mAdv.dungeonStat.captiveRate = Math.min(i2 + 1, i);
                    this.mAdv.dungeonStat.captiveRate = Math.min(this.mAdv.dungeonStat.captiveRate, 99);
                    AdventureContext adventureContext2 = this.mAdv;
                    adventureContext2.inBlockProgress = adventureContext2.dungeonStat.captiveRate;
                }
                if (this.mAdv.fightResult != FightEngine.FightResult.RUN || zIsConqured) {
                    return;
                }
                this.mAdv.returning = true;
                processDecideReturnPlacedMonster();
                return;
            }
            return;
        }
        if (!zIsConqured && !adventureContext.returning && this.mRandom.nextInt(1000) < this.mAdv.dungeon.treasureFactor) {
            z &= !processTreasure();
        }
        if (z && (processResultProcessWanderingMonster = processWanderingMonster()) != null) {
            handleProgressResult(processResultProcessWanderingMonster);
        } else {
            processInventoryMax();
            handleResultTick(ProcessResult.createProgress());
        }
    }

    private DungeonEvent processEvents(List<DungeonEvent> list, boolean z) {
        DungeonEvent dungeonEvent = null;
        for (DungeonEvent dungeonEvent2 : list) {
            switch (dungeonEvent2.type) {
                case SPRING_CURE:
                case SPRING_CURE_ALL:
                case SPRING_CURE_MP:
                case SPRING_CURE_MP_ALL:
                case SPRING_NONE:
                case SPRING_POISON:
                    processEventSpring(dungeonEvent2, z);
                    dungeonEvent = dungeonEvent2;
                    break;
                case TRAP_ALARM:
                case TRAP_BOW:
                case TRAP_POISON:
                    if (!z) {
                        processEventTrap(dungeonEvent2);
                        dungeonEvent = dungeonEvent2;
                        break;
                    }
                    break;
            }
            if (!isAdventureContinued()) {
                return dungeonEvent;
            }
        }
        return dungeonEvent;
    }

    private void processEventSpring(DungeonEvent dungeonEvent, boolean z) {
        SpringEngine springEngine = new SpringEngine(this.mContext, dungeonEvent, this.mAdv.dungeon.springSubType, z, this.mAdv.dungeon.difficulty, this.mGame.getAliveChars(), this.mRandom);
        StringBuilder sb = new StringBuilder();
        writeLog(createAdventureLog(CommonLog.LogType.SPRING, this.mContext.getString(springEngine.processEventSpring(sb)), sb.toString().trim()));
    }

    private void processEventTrap(DungeonEvent dungeonEvent) {
        TrapEngine.TrapType trapType;
        switch (dungeonEvent.type) {
            case SPRING_CURE:
            case SPRING_CURE_ALL:
            case SPRING_CURE_MP:
            case SPRING_CURE_MP_ALL:
            case SPRING_NONE:
            case SPRING_POISON:
            default:
                trapType = null;
                break;
            case TRAP_ALARM:
                trapType = TrapEngine.TrapType.ALARM;
                break;
            case TRAP_BOW:
                trapType = TrapEngine.TrapType.BOW;
                break;
            case TRAP_POISON:
                trapType = TrapEngine.TrapType.POISON;
                break;
        }
        Context context = this.mContext;
        AdventureContext adventureContext = this.mAdv;
        TrapEngine trapEngine = new TrapEngine(context, trapType, adventureContext, adventureContext.dungeon.difficulty, this.mGame.getAliveChars(), this.mRandom);
        StringBuilder sb = new StringBuilder();
        trapEngine.processEventTrap(sb);
        writeLog(createAdventureLog(CommonLog.LogType.TRAP, this.mContext.getString(C0380R.string.alog_title_event_trap), sb.toString()));
    }

    private int progressNotCapturedBlock() {
        int i = this.mAdv.inBlockProgress;
        if (i >= this.mAdv.dungeonStat.captiveRate) {
            return i;
        }
        int i2 = this.mAdv.inBlockProgress + this.mProgressCaptured;
        return i2 > this.mAdv.dungeonStat.captiveRate ? this.mAdv.dungeonStat.captiveRate : i2;
    }

    private int exploreNotCapturedBlock() {
        int i = this.mAdv.dungeonStat.captiveRate;
        if (this.mAdv.inBlockProgress < this.mAdv.dungeonStat.captiveRate) {
            return i;
        }
        int i2 = i + this.mProgressNotCaptured;
        if (i2 > 100) {
            return 100;
        }
        return i2;
    }

    private ProcessResult processEncounterNotCapturedBlock(int i) {
        if (this.mAdv.dungeonStat.monsterNumber > 0 && i > this.mAdv.dungeonStat.captiveRate) {
            int i2 = i - this.mAdv.dungeonStat.captiveRate;
            int i3 = 100 / this.mAdv.dungeonStat.initialMonsterNumber;
            boolean z = true;
            if (i < 100 && i2 <= i3 && i2 <= this.mRandom.nextInt(i3)) {
                z = false;
            }
            if (z) {
                Boolean boolValueOf = this.mAdv.dungeonStat.monsterNumber == this.mAdv.dungeonStat.initialMonsterNumber ? Boolean.valueOf(this.mAdv.dungeonStat.knowBlockType) : null;
                ProcessResult processResultEncounterPlacedMonster = encounterPlacedMonster();
                processResultEncounterPlacedMonster.fightArgument.isExpected = boolValueOf;
                return processResultEncounterPlacedMonster;
            }
        }
        return null;
    }

    private ProcessResult encounterPlacedMonster() {
        int randomMinMax;
        MonsterPop placedMonsterInfo = this.mAdv.dungeon.getPlacedMonsterInfo(this.mAdv.floor, this.mAdv.dungeonStat.monsterId);
        if (placedMonsterInfo != null) {
            randomMinMax = placedMonsterInfo.getPartyMemberNumber(this.mRandom);
        } else {
            Monster monster = MonsterRepository.getMonster(this.mContext, this.mAdv.dungeonStat.monsterId);
            randomMinMax = EngineUtil.getRandomMinMax(this.mRandom, monster.defaultPartyMin, monster.defaultPartyMax);
        }
        return new ProcessResult(true, new FightArgument(false, false, this.mAdv.dungeonStat.monsterId, randomMinMax));
    }

    private ExploringThrowResult throwExploring(DungeonStat.BlockType blockType) {
        Thrower.ThrowResult throwResultGenericThrow;
        ExploringThrowResult exploringThrowResult = new ExploringThrowResult();
        int i = this.mAdv.dungeon.difficulty;
        int i2 = 0;
        for (PlayerChar playerChar : this.mGame.getAliveChars()) {
            int i3 = 0;
            for (Enchant enchant : this.mAdv.enchantsPlayers[playerChar.index]) {
                if (enchant.otherEffect == Enchant.OtherEffect.DETECT) {
                    i3 += enchant.value;
                }
            }
            GameChar.SubClass subClass = playerChar.hasSubClass(GameChar.SubClass.ROGUE) ? GameChar.SubClass.ROGUE : null;
            if (blockType == DungeonStat.BlockType.SAFE_ZONE && playerChar.hasSubClass(GameChar.SubClass.CLERIC) && (subClass == null || playerChar.getSubLevel(GameChar.SubClass.ROGUE) < playerChar.getSubLevel(GameChar.SubClass.CLERIC))) {
                subClass = GameChar.SubClass.CLERIC;
            }
            if (subClass != null) {
                throwResultGenericThrow = this.mThrower.attributeThrow(playerChar, GameChar.Attribute.INT, subClass);
            } else {
                throwResultGenericThrow = this.mThrower.genericThrow(playerChar, GameChar.Attribute.INT);
                throwResultGenericThrow.value -= 4;
            }
            throwResultGenericThrow.value += i3;
            if (throwResultGenericThrow.value >= i || throwResultGenericThrow.critical) {
                if (exploringThrowResult.successPc == null || i2 < throwResultGenericThrow.value) {
                    exploringThrowResult.successPc = playerChar;
                    i2 = throwResultGenericThrow.value;
                }
                exploringThrowResult.success = true;
            }
        }
        return exploringThrowResult;
    }

    private int progressCapturedBlock() {
        int i = this.mAdv.inBlockProgress + this.mProgressCaptured;
        if (i > 100) {
            return 100;
        }
        return i;
    }

    private int progressReturning() {
        int i = this.mAdv.inBlockProgress - this.mProgressCaptured;
        if (i < 0) {
            return 0;
        }
        return i;
    }

    private void processDecideReturn(boolean z) {
        writeLog(createAdventureLog(CommonLog.LogType.RETURN, this.mContext.getString(C0380R.string.alog_title_return), this.mContext.getString(z ? C0380R.string.alog_desc_return_max_inventory_1 : C0380R.string.alog_desc_return_danger_1)));
    }

    private void processDecideReturnPlacedMonster() {
        writeLog(createAdventureLog(CommonLog.LogType.RETURN, this.mContext.getString(C0380R.string.alog_title_placed_monster_run_return), this.mContext.getString(C0380R.string.alog_desc_placed_monster_run_return_1)));
    }

    private ProcessResult processRest() {
        Skill availableSkill;
        boolean z = this.mAdv.hasHidingSkill;
        if (z) {
            Iterator<Enchant> it = this.mAdv.enchants.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Enchant next = it.next();
                if (next.causeSkillId == 20020 && next.term == -1) {
                    z = false;
                    break;
                }
            }
        }
        boolean z2 = z;
        for (int i = 0; i < this.mGame.characters.size(); i++) {
            PlayerChar playerChar = this.mGame.characters.get(i);
            if (playerChar.isAlive()) {
                if (z2 && this.mAdv.getCurrentTacticsForChar(i).statusSkill != Tactics.TacticsValue.NONE && (availableSkill = playerChar.getAvailableSkill(this.mContext, SkillDb.SKILL_ROGUE_HIDE)) != null && availableSkill.canUse(playerChar)) {
                    processSkillOther(playerChar, null, availableSkill);
                    z2 = false;
                }
                int i2 = this.mAdv.dungeon.difficulty;
                if (playerChar.f93hp < playerChar.maxHp) {
                    Thrower.ThrowResult throwResultGenericThrow = this.mThrower.genericThrow(playerChar, GameChar.Attribute.VIT);
                    int i3 = playerChar.maxHp / 10;
                    if (!throwResultGenericThrow.success(i2)) {
                        i3 = (i3 + 1) / 2;
                    }
                    playerChar.f93hp += i3;
                    if (playerChar.f93hp > playerChar.maxHp) {
                        playerChar.f93hp = playerChar.maxHp;
                    }
                }
                if (playerChar.f94mp < playerChar.maxMp) {
                    Thrower.ThrowResult throwResultGenericThrow2 = this.mThrower.genericThrow(playerChar, GameChar.Attribute.INT);
                    int i4 = playerChar.maxMp / 5;
                    if (!throwResultGenericThrow2.success(i2)) {
                        i4 = (i4 + 1) / 2;
                    }
                    playerChar.f94mp += i4;
                    if (playerChar.f94mp > playerChar.maxMp) {
                        playerChar.f94mp = playerChar.maxMp;
                    }
                }
            }
        }
        writeLog(createAdventureLog(CommonLog.LogType.REST, this.mContext.getString(C0380R.string.alog_title_rest), this.mContext.getString(C0380R.string.alog_desc_rest_1)));
        if (this.mAdv.dungeonStat.blockType == DungeonStat.BlockType.MONSTER && this.mAdv.dungeonStat.monsterNumber > 0) {
            int i5 = 800;
            for (Enchant enchant : this.mAdv.enchants) {
                if (enchant.otherEffect == Enchant.OtherEffect.HIDING && enchant.term == -1) {
                    i5 = (i5 * (enchant.value + 100)) / 100;
                }
            }
            if (this.mRandom.nextInt(1000) < i5) {
                ProcessResult processResultEncounterPlacedMonster = encounterPlacedMonster();
                if (!this.mAdv.dungeonStat.knowBlockType) {
                    processResultEncounterPlacedMonster.fightArgument.isExpected = Boolean.FALSE;
                }
                return processResultEncounterPlacedMonster;
            }
        }
        return null;
    }

    private void processNotRest() {
        if (this.mAdv.hasHidingSkill) {
            for (Enchant enchant : this.mAdv.enchants) {
                if (enchant.causeSkillId == 20020) {
                    enchant.term = 0;
                }
            }
        }
    }

    private void leaveFloor() {
        ProcessResult processResultProcessFlags;
        do {
            processResultProcessFlags = processFlags(Decision.Timing.CLEAR_FLOOR);
            if (processResultProcessFlags == null) {
                return;
            }
        } while (handleProgressResult(processResultProcessFlags));
    }

    private void leaveTargetFloor() {
        ProcessResult processResultProcessFlags;
        do {
            processResultProcessFlags = processFlags(Decision.Timing.CLEAR_FLOOR);
            if (processResultProcessFlags == null) {
                break;
            }
        } while (handleProgressResult(processResultProcessFlags));
        writeLog(createAdventureLog(CommonLog.LogType.COMPLETE, this.mContext.getString(C0380R.string.alog_title_leave_target_floor), this.mContext.getString(C0380R.string.alog_desc_leave_target_floor_1, this.mAdv.dungeon.name, Integer.valueOf(this.mAdv.targetFloor))));
    }

    /* JADX WARN: Removed duplicated region for block: B:112:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:86:0x022c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void enterBlock() {
        /*
            Method dump skipped, instructions count: 676
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.adventure.AdventureEngine.enterBlock():void");
    }

    private void enterFloor() {
        String string;
        ProcessResult processResultProcessFlags;
        if (this.mAdv.floor == 1) {
            string = this.mContext.getString(this.mAdv.dungeon.descStringId) + this.mContext.getString(C0380R.string.alog_desc_enter_floor_1, this.mAdv.dungeon.name);
        } else {
            string = this.mContext.getString(EngineUtil.getElementRandom(this.mAdv.dungeon.floorDescStringIds, this.mRandom));
        }
        writeLog(createAdventureLog(this.mAdv.floor == 1 ? CommonLog.LogType.ENTER_DUNGEON : CommonLog.LogType.ENTER_FLOOR, this.mContext.getString(C0380R.string.alog_title_enter_floor, Integer.valueOf(this.mAdv.floor)), string));
        do {
            processResultProcessFlags = processFlags(Decision.Timing.START_FLOOR);
            if (processResultProcessFlags == null) {
                break;
            }
        } while (handleProgressResult(processResultProcessFlags));
        FlagEngine.processExtraDungeonInEnterFloor(this.mContext, this.mRandom, this.mGame);
    }

    private void returningToFloor() {
        writeLog(createAdventureLog(CommonLog.LogType.ENTER_FLOOR, null, this.mContext.getString(C0380R.string.alog_title_return_floor, Integer.valueOf(this.mAdv.floor)), this.mContext.getString(C0380R.string.alog_desc_return_floor), this.mContext.getString(C0380R.string.alog_desc_return_current_floor, this.mAdv.dungeon.name, Integer.valueOf(this.mAdv.floor))));
    }

    private ProcessResult processWanderingMonster() {
        int i = this.mAdv.dungeon.enemyFactor;
        for (Enchant enchant : this.mAdv.enchants) {
            if (enchant.otherEffect == Enchant.OtherEffect.ENCOUNTER || (enchant.otherEffect == Enchant.OtherEffect.HIDING && enchant.term == -1)) {
                i = (i * (enchant.value + 100)) / 100;
            }
        }
        if (i > 800) {
            i = 800;
        }
        if (this.mRandom.nextInt(1000) >= i) {
            return null;
        }
        MonsterPop monsterPopSelectRandomMonster = this.mAdv.dungeon.selectRandomMonster(this.mRandom, this.mAdv.floor);
        return new ProcessResult(true, new FightArgument(true, false, monsterPopSelectRandomMonster.monsterId, monsterPopSelectRandomMonster.getPartyMemberNumber(this.mRandom)));
    }

    private FightEngine.FightResult processFight(FightArgument fightArgument) {
        CommonLog.LogType logType;
        Monster monster = MonsterRepository.getMonster(this.mContext, fightArgument.monsterId);
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < fightArgument.monsterCount; i++) {
            arrayList.add(monster);
        }
        Context context = this.mContext;
        GameContext gameContext = this.mGame;
        AdventureContext adventureContext = this.mAdv;
        FightEngine fightEngine = new FightEngine(context, gameContext, adventureContext, arrayList, adventureContext.calendar, this.mRandom, fightArgument.isWandering, fightArgument.isExpected, fightArgument.isEvent);
        FightEngine.FightResult fightResultExecute = fightEngine.execute();
        List<FightingLog> logs = fightEngine.getLogs();
        LogFight logFight = new LogFight();
        logFight.fightingLogs = logs;
        logFight.isWandering = fightArgument.isWandering;
        logFight.isEvent = fightArgument.isEvent;
        logFight.monster = (Monster) arrayList.get(0);
        AdventureContext adventureContext2 = this.mAdv;
        Context context2 = this.mContext;
        GameContext gameContext2 = this.mGame;
        adventureContext2.calcCharacterStatus(context2, gameContext2, gameContext2.characters);
        int i2 = fightArgument.isEvent ? C0380R.string.alog_title_fight_event : fightArgument.isWandering ? C0380R.string.alog_title_fight_normal_wander : C0380R.string.alog_title_fight_normal_placed;
        String string = this.mContext.getString(logFight.monster.type.getNumberStrId(), Integer.valueOf(fightArgument.monsterCount));
        switch (fightResultExecute) {
            case LOSE:
                logType = CommonLog.LogType.ENCOUNTER_LOSE;
                break;
            case RUN:
                logType = CommonLog.LogType.ENCOUNTER_RUN;
                break;
            case WIN:
                if (fightArgument.isWandering) {
                    logType = CommonLog.LogType.ENCOUNTER_WIN;
                    break;
                } else {
                    logType = CommonLog.LogType.ENCOUNTER_WIN_MONSTER_BLOCK;
                    break;
                }
            case WIN_GOT_ITEM:
                logType = CommonLog.LogType.ENCOUNTER_GOT_ITEM;
                break;
            default:
                logType = null;
                break;
        }
        AdventureLog adventureLogCreateAdventureLog = createAdventureLog(logType, this.mContext.getString(i2, ((Monster) arrayList.get(0)).name, string), null);
        adventureLogCreateAdventureLog.logFight = logFight;
        writeLog(adventureLogCreateAdventureLog);
        return fightResultExecute;
    }

    private void processEnd() {
        CommonLog.LogType logType;
        boolean zAreAllCharsDied = this.mGame.areAllCharsDied();
        int i = C0380R.string.alog_desc_dead_1;
        int i2 = C0380R.string.alog_title_dead;
        if (zAreAllCharsDied || this.mAdv.fightResult == FightEngine.FightResult.LOSE) {
            logType = CommonLog.LogType.DEAD;
        } else if (this.mAdv.success) {
            logType = CommonLog.LogType.END;
            i2 = C0380R.string.alog_title_complete;
            i = C0380R.string.alog_desc_complete_1;
        } else {
            logType = CommonLog.LogType.RETURNED;
            i2 = C0380R.string.alog_title_returned;
            i = C0380R.string.alog_desc_returned_1;
        }
        writeLog(createAdventureLog(logType, this.mContext.getString(i2, this.mAdv.dungeon.name, Integer.valueOf(this.mAdv.targetFloor)), this.mContext.getString(i)));
    }

    public void cancel() {
        this.mCancelled = true;
    }
}
