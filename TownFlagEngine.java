package com.shirobakama.autorpg2.adventure;

import android.app.Activity;
import android.content.Context;
import android.database.SQLException;
import android.text.TextUtils;
import com.shirobakama.autorpg2.adventure.FlagEngine;
import com.shirobakama.autorpg2.entity.Dungeon;
import com.shirobakama.autorpg2.entity.DungeonContext;
import com.shirobakama.autorpg2.entity.DungeonStat;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.GameFlag;
import com.shirobakama.autorpg2.entity.Inventory;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.entity.Quest;
import com.shirobakama.autorpg2.p001db.InheritanceAnalyzer;
import com.shirobakama.autorpg2.p001db.Persister;
import com.shirobakama.autorpg2.repo.ItemDb;
import com.shirobakama.autorpg2.repo.ItemRepository;
import com.shirobakama.autorpg2.repo.MonsterDb;
import com.shirobakama.autorpg2.repo.QuestRepository;
import com.shirobakama.autorpg2.repo.SkillDb;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.logquest2.BuildConfig;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public abstract class TownFlagEngine {
    protected static final List<Enum<?>> SYNTHETIC_ENUMS;
    protected static final String TAG = "town-flag-engine";
    protected Random random = new Random();
    protected TownEngineState state = new TownEngineState();

    protected abstract String processAcceptQuestSub(Result result, FlagEngine.CurrentValues currentValues, Quest quest);

    protected abstract String processRefuseQuestSub(Result result, FlagEngine.CurrentValues currentValues, Quest quest);

    public static class Result {
        public List<GameFlag> flags;
        public int gold;
        public Item item;
        public Integer joinMember;
        public Item lostItem;
        public String message;
        public String questCandidateSymbol;

        public Result() {
        }

        public Result(String str) {
            this.message = str;
        }

        public Result addFlag(GameFlag gameFlag) {
            if (this.flags == null) {
                this.flags = new LinkedList();
            }
            this.flags.add(gameFlag);
            return this;
        }

        public Result setQuestCandidate(String str) {
            this.questCandidateSymbol = str;
            return this;
        }

        public Result addGold(int i) {
            this.gold = i;
            return this;
        }

        public Result setItem(Item item) {
            this.item = item;
            return this;
        }

        public Result setLostItem(Item item) {
            this.lostItem = item;
            return this;
        }
    }

    public static class TownEngineState {
        protected boolean isLast;
        protected int conversationSeq = 0;
        protected boolean isAdventuring = false;
        protected int townId = 0;
        protected boolean acceptQuest = false;
        protected boolean refuseQuest = false;
        public String questStates = null;
        public String flagStates = null;

        public TownEngineState copy() {
            TownEngineState townEngineState = new TownEngineState();
            townEngineState.conversationSeq = this.conversationSeq;
            townEngineState.isLast = this.isLast;
            townEngineState.isAdventuring = this.isAdventuring;
            townEngineState.townId = this.townId;
            townEngineState.acceptQuest = this.acceptQuest;
            townEngineState.refuseQuest = this.refuseQuest;
            townEngineState.questStates = this.questStates;
            townEngineState.flagStates = this.flagStates;
            return townEngineState;
        }
    }

    static {
        ArrayList arrayList = new ArrayList();
        for (GameChar.Attribute attribute : GameChar.ATTRIBUTES) {
            arrayList.add(attribute);
        }
        for (GameChar.Status status : GameChar.STATUS_ARRAY) {
            arrayList.add(status);
        }
        SYNTHETIC_ENUMS = arrayList;
    }

    public void resetState(Context context, GameContext gameContext) {
        TownEngineState townEngineState = this.state;
        townEngineState.conversationSeq = 0;
        townEngineState.isLast = false;
        townEngineState.acceptQuest = false;
        townEngineState.refuseQuest = false;
        townEngineState.isAdventuring = gameContext.isAdventuring(context);
        this.state.townId = gameContext.townId;
        TownEngineState townEngineState2 = this.state;
        townEngineState2.questStates = null;
        townEngineState2.flagStates = null;
    }

    protected boolean resetConversationIfQuestStatesAreChanged(FlagEngine.QuestState... questStateArr) {
        StringBuilder sb = new StringBuilder();
        for (FlagEngine.QuestState questState : questStateArr) {
            sb.append(questState.started);
            sb.append(',');
            sb.append(questState.cleared);
            sb.append(',');
        }
        String string = sb.toString();
        if (TextUtils.equals(string, this.state.questStates)) {
            return false;
        }
        TownEngineState townEngineState = this.state;
        townEngineState.questStates = string;
        townEngineState.conversationSeq = 0;
        return true;
    }

    protected boolean resetConversationIfFlagsAreChanged(FlagEngine.CurrentValues currentValues, GameFlag.Key... keyArr) {
        StringBuilder sb = new StringBuilder();
        for (GameFlag.Key key : keyArr) {
            GameFlag flag = currentValues.getFlag(key);
            if (flag == null) {
                sb.append("null,");
            } else {
                sb.append(flag.option);
                sb.append('.');
                sb.append(flag.value);
                sb.append(",");
            }
        }
        String string = sb.toString();
        if (TextUtils.equals(string, this.state.flagStates)) {
            return false;
        }
        TownEngineState townEngineState = this.state;
        townEngineState.flagStates = string;
        townEngineState.conversationSeq = 0;
        return true;
    }

    protected void checkConversationState(Context context, GameContext gameContext) {
        if (this.state.isAdventuring == gameContext.isAdventuring(context) && this.state.townId == gameContext.townId) {
            return;
        }
        resetState(context, gameContext);
    }

    protected int getStringIdSeq(int[] iArr) {
        if (this.state.conversationSeq >= iArr.length) {
            this.state.conversationSeq = 0;
        }
        int i = iArr[this.state.conversationSeq];
        this.state.conversationSeq++;
        return i;
    }

    protected Result messageResult(FlagEngine.CurrentValues currentValues, int[] iArr, Object... objArr) {
        Result result = new Result();
        int stringIdSeq = getStringIdSeq(iArr);
        TownEngineState townEngineState = this.state;
        townEngineState.isLast = townEngineState.conversationSeq == iArr.length;
        result.message = (objArr == null || objArr.length == 0) ? currentValues.context.getString(stringIdSeq) : currentValues.context.getString(stringIdSeq, objArr);
        return result;
    }

    protected Result messageResultOrQuest(FlagEngine.CurrentValues currentValues, int[] iArr, String str, Object... objArr) {
        Result result = new Result();
        if (str != null && this.state.conversationSeq == iArr.length) {
            result.message = null;
            result.questCandidateSymbol = str;
            TownEngineState townEngineState = this.state;
            townEngineState.isLast = false;
            townEngineState.conversationSeq++;
        } else {
            int stringIdSeq = getStringIdSeq(iArr);
            TownEngineState townEngineState2 = this.state;
            townEngineState2.isLast = townEngineState2.conversationSeq == iArr.length;
            result.message = objArr.length == 0 ? currentValues.context.getString(stringIdSeq) : currentValues.context.getString(stringIdSeq, objArr);
        }
        return result;
    }

    protected Result addClearQuest(FlagEngine.CurrentValues currentValues, Result result, String str) {
        this.state.acceptQuest = true;
        Quest quest = QuestRepository.getQuest(str);
        GameFlag gameFlag = new GameFlag(new GameFlag.Key(GameFlag.FlagType.QUEST_CLEAR, str));
        gameFlag.value = true;
        result.addFlag(gameFlag);
        String string = currentValues.context.getString(C0380R.string.msg_quest_cleared, currentValues.context.getString(quest.nameStringId));
        if (result.message != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(string);
            sb.append("\n");
            sb.append(result.message.startsWith("!") ? result.message.substring(1) : result.message);
            string = sb.toString();
        }
        result.message = string;
        return result;
    }

    protected Result addJoinMember(FlagEngine.CurrentValues currentValues, Result result, int i) {
        result.joinMember = Integer.valueOf(i);
        GameFlag gameFlag = new GameFlag(keyMemberJoined(i));
        gameFlag.value = true;
        result.addFlag(gameFlag);
        String string = currentValues.context.getString(C0380R.string.msg_new_member_joined, getMemberName(currentValues.context, i));
        if (result.message != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(string);
            sb.append("\n");
            sb.append(result.message.startsWith("!") ? result.message.substring(1) : result.message);
            string = sb.toString();
        }
        result.message = string;
        return result;
    }

    public Result processAcceptQuest(Context context, GameContext gameContext, String str) {
        this.state.acceptQuest = true;
        Quest quest = QuestRepository.getQuest(str);
        Result result = new Result();
        GameFlag orCreateFlag = gameContext.getOrCreateFlag(new GameFlag.Key(GameFlag.FlagType.QUEST_START, str));
        orCreateFlag.value = true;
        orCreateFlag.addOptionAsInt(1);
        result.addFlag(orCreateFlag);
        GameFlag flag = gameContext.getFlag(new GameFlag.Key(GameFlag.FlagType.QUEST_CLEAR, str));
        if (flag != null) {
            flag.value = false;
            result.addFlag(flag);
        }
        String strProcessAcceptQuestSub = processAcceptQuestSub(result, new FlagEngine.CurrentValues(this.random, context, gameContext), quest);
        String string = context.getString(C0380R.string.msg_quest_started, context.getString(quest.nameStringId));
        if (strProcessAcceptQuestSub != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(string);
            sb.append("\n");
            if (strProcessAcceptQuestSub.startsWith("!")) {
                strProcessAcceptQuestSub = strProcessAcceptQuestSub.substring(1);
            }
            sb.append(strProcessAcceptQuestSub);
            string = sb.toString();
        }
        result.message = string;
        return result;
    }

    public Result processRefuseQuest(Context context, GameContext gameContext, String str) {
        this.state.refuseQuest = true;
        FlagEngine.CurrentValues currentValues = new FlagEngine.CurrentValues(this.random, context, gameContext);
        Result result = new Result();
        String strProcessRefuseQuestSub = processRefuseQuestSub(result, currentValues, QuestRepository.getQuest(str));
        if (strProcessRefuseQuestSub != null) {
            result.message = strProcessRefuseQuestSub;
        }
        return result;
    }

    public static int getMaxFloor(Context context, GameContext gameContext, Dungeon dungeon) {
        FlagEngine.CurrentValues currentValues = new FlagEngine.CurrentValues(new Random(), context, gameContext);
        int i = dungeon.floor;
        if (dungeon.f90id == 2 && currentValues.isOff(new GameFlag.Key(GameFlag.FlagType.QUEST, QuestConst.CAVE_FLAG_USE_KEY))) {
            return 2;
        }
        return i;
    }

    public static void initializeDungeonContext(Random random, Context context, GameContext gameContext, Dungeon dungeon, DungeonContext dungeonContext) {
        if (dungeon.f90id == 1) {
            if (new FlagEngine.CurrentValues(random, context, gameContext).isOff(GameFlag.Key.asClearDungeon(dungeon))) {
                DungeonStat stat = dungeonContext.getStat(dungeon, dungeon.floor, dungeon.block);
                stat.blockType = DungeonStat.BlockType.MONSTER;
                stat.monsterId = MonsterDb.MONSTER_GOBLIN;
                stat.initialMonsterNumber = random.nextInt(2) + 4;
                stat.monsterNumber = stat.initialMonsterNumber;
                return;
            }
            return;
        }
        if (dungeon.f90id == 2) {
            if (new FlagEngine.CurrentValues(random, context, gameContext).isOff(GameFlag.Key.asClearDungeon(dungeon))) {
                DungeonStat stat2 = dungeonContext.getStat(dungeon, dungeon.floor, dungeon.block);
                stat2.blockType = DungeonStat.BlockType.MONSTER;
                stat2.monsterId = MonsterDb.MONSTER_HOBGOBLIN;
                stat2.initialMonsterNumber = random.nextInt(2) + 4;
                stat2.monsterNumber = stat2.initialMonsterNumber;
                return;
            }
            return;
        }
        if (dungeon.f90id == 3) {
            if (new FlagEngine.CurrentValues(random, context, gameContext).isOff(GameFlag.Key.asClearDungeon(dungeon))) {
                DungeonStat stat3 = dungeonContext.getStat(dungeon, dungeon.floor, dungeon.block);
                stat3.blockType = DungeonStat.BlockType.MONSTER;
                stat3.monsterId = 200;
                stat3.initialMonsterNumber = random.nextInt(6) + 4;
                stat3.monsterNumber = stat3.initialMonsterNumber;
                return;
            }
            return;
        }
        if (dungeon.f90id == 8) {
            DungeonStat stat4 = dungeonContext.getStat(dungeon, 1, dungeon.block);
            stat4.blockType = DungeonStat.BlockType.MONSTER;
            stat4.monsterId = MonsterDb.MONSTER_SWORDSMAN;
            stat4.initialMonsterNumber = random.nextInt(3) + 3;
            stat4.monsterNumber = stat4.initialMonsterNumber;
            DungeonStat stat5 = dungeonContext.getStat(dungeon, 2, dungeon.block);
            stat5.blockType = DungeonStat.BlockType.MONSTER;
            stat5.monsterId = MonsterDb.MONSTER_CLERIC;
            stat5.initialMonsterNumber = random.nextInt(3) + 3;
            stat5.monsterNumber = stat5.initialMonsterNumber;
            DungeonStat stat6 = dungeonContext.getStat(dungeon, 3, dungeon.block);
            stat6.blockType = DungeonStat.BlockType.MONSTER;
            stat6.monsterId = MonsterDb.MONSTER_MAGE;
            stat6.initialMonsterNumber = random.nextInt(3) + 3;
            stat6.monsterNumber = stat6.initialMonsterNumber;
            DungeonStat stat7 = dungeonContext.getStat(dungeon, 4, dungeon.block);
            stat7.blockType = DungeonStat.BlockType.MONSTER;
            stat7.monsterId = MonsterDb.MONSTER_GUARDIAN;
            stat7.initialMonsterNumber = random.nextInt(3) + 3;
            stat7.monsterNumber = stat7.initialMonsterNumber;
            return;
        }
        if (dungeon.f90id == 14) {
            if (new FlagEngine.CurrentValues(random, context, gameContext).isOff(GameFlag.Key.asClearDungeon(dungeon))) {
                DungeonStat stat8 = dungeonContext.getStat(dungeon, dungeon.floor, dungeon.block);
                stat8.blockType = DungeonStat.BlockType.MONSTER;
                stat8.monsterId = 1090;
                stat8.initialMonsterNumber = random.nextInt(2) + 4;
                stat8.monsterNumber = stat8.initialMonsterNumber;
                return;
            }
            return;
        }
        if (dungeon.f90id == 16) {
            FlagEngine.CurrentValues currentValues = new FlagEngine.CurrentValues(random, context, gameContext);
            if (currentValues.isOff(GameFlag.Key.asClearDungeon(dungeon)) || !currentValues.isMonsterWon(1520)) {
                DungeonStat stat9 = dungeonContext.getStat(dungeon, 10, 1);
                stat9.blockType = DungeonStat.BlockType.MONSTER;
                stat9.monsterId = 1520;
                stat9.initialMonsterNumber = 3;
                stat9.monsterNumber = stat9.initialMonsterNumber;
                return;
            }
            return;
        }
        if (dungeon.f90id == 18) {
            FlagEngine.CurrentValues currentValues2 = new FlagEngine.CurrentValues(random, context, gameContext);
            if (currentValues2.isOff(GameFlag.Key.asClearDungeon(dungeon)) || !currentValues2.isMonsterWon(1540)) {
                DungeonStat stat10 = dungeonContext.getStat(dungeon, dungeon.floor, dungeon.block);
                stat10.blockType = DungeonStat.BlockType.MONSTER;
                stat10.monsterId = 1540;
                stat10.initialMonsterNumber = 1;
                stat10.monsterNumber = stat10.initialMonsterNumber;
                return;
            }
            return;
        }
        if (dungeon.f90id == 19) {
            FlagEngine.CurrentValues currentValues3 = new FlagEngine.CurrentValues(random, context, gameContext);
            if (currentValues3.isOff(GameFlag.Key.asClearDungeon(dungeon)) || !currentValues3.isMonsterWon(1530)) {
                DungeonStat stat11 = dungeonContext.getStat(dungeon, 9, dungeon.block);
                stat11.blockType = DungeonStat.BlockType.MONSTER;
                stat11.monsterId = 1530;
                stat11.initialMonsterNumber = 1;
                stat11.monsterNumber = stat11.initialMonsterNumber;
            }
        }
    }

    public static List<Result> inherit(Context context, GameContext gameContext, InheritanceAnalyzer inheritanceAnalyzer) {
        LinkedList linkedList = new LinkedList();
        GameFlag orCreateFlag = gameContext.getOrCreateFlag(GameFlag.Key.asType(GameFlag.FlagType.OTHER, QuestConst.FLAG_INHERITED));
        orCreateFlag.setValue(true);
        linkedList.add(new Result(context.getString(C0380R.string.cnv_quest_inherited_previous_version)).addGold(MonsterDb.MONSTER_ROPER).setItem(ItemRepository.getItem(context, 30)).addFlag(orCreateFlag));
        linkedList.add(new Result().setItem(ItemRepository.getItem(context, 40)));
        if (inheritanceAnalyzer.level >= 10) {
            linkedList.add(new Result().setItem(ItemRepository.getItem(context, ItemDb.ITEM_PLATE_ARMOR)));
        }
        if (inheritanceAnalyzer.level >= 20) {
            linkedList.add(new Result().setItem(ItemRepository.getItem(context, 1220)));
        }
        if (inheritanceAnalyzer.level >= 30) {
            linkedList.add(new Result().setItem(ItemRepository.getItem(context, ItemDb.ITEM_LINK_SHIELD)));
        }
        if (inheritanceAnalyzer.level >= 40) {
            linkedList.add(new Result().setItem(ItemRepository.getItem(context, ItemDb.ITEM_RING_OF_DEFENSE)));
        }
        ArrayList arrayList = new ArrayList(4);
        arrayList.add(Integer.valueOf(ItemDb.ITEM_SECRET_SCROLL));
        arrayList.add(Integer.valueOf(ItemDb.ITEM_ASTRO_BOOK));
        arrayList.add(1540);
        arrayList.add(Integer.valueOf(ItemDb.ITEM_MEDAL_OF_LORDS));
        Collections.shuffle(arrayList);
        Iterator it = arrayList.iterator();
        if (inheritanceAnalyzer.gameCleared) {
            linkedList.add(new Result().setItem(ItemRepository.getItem(context, ((Integer) it.next()).intValue())));
        }
        if (inheritanceAnalyzer.mazeCleared) {
            while (it.hasNext()) {
                linkedList.add(new Result().setItem(ItemRepository.getItem(context, ((Integer) it.next()).intValue())));
            }
        }
        if (inheritanceAnalyzer.expCounterStopped) {
            linkedList.add(new Result().addGold(2000).setItem(ItemRepository.getItem(context, 1380)));
        }
        return linkedList;
    }

    public static Result useClassChangeItem(Context context, PlayerChar playerChar, Inventory inventory) {
        Result result = new Result();
        Item baseItem = inventory.getBaseItem(context);
        GameChar.CharClass changedClass = getChangedClass(baseItem);
        if (changedClass != null) {
            playerChar.clazz = changedClass;
            playerChar.statusBonus -= playerChar.level / 8;
            playerChar.exp = 0;
            playerChar.level = GameChar.getLevel(playerChar.exp);
            playerChar.getAvailableSkillIds().clear();
            result.setLostItem(baseItem);
            String string = BuildConfig.FLAVOR;
            switch (changedClass) {
                case FIGHTER:
                    string = context.getString(C0380R.string.msg_use_item_class_change_fighter);
                    break;
                case PRIEST:
                    string = context.getString(C0380R.string.msg_use_item_class_change_priest);
                    break;
                case THIEF:
                    string = context.getString(C0380R.string.msg_use_item_class_change_thief);
                    break;
                case MAGICIAN:
                    string = context.getString(C0380R.string.msg_use_item_class_change_magician);
                    break;
                case BISHOP:
                    string = context.getString(C0380R.string.msg_use_item_class_change_bishop);
                    break;
                case LORD:
                    string = context.getString(C0380R.string.msg_use_item_class_change_lord);
                    break;
                case NINJA:
                    string = context.getString(C0380R.string.msg_use_item_class_change_ninja);
                    break;
                case SAMURAI:
                    string = context.getString(C0380R.string.msg_use_item_class_change_samurai);
                    break;
            }
            result.message = context.getString(C0380R.string.msg_use_item_class_change, playerChar.name, baseItem.name, string, changedClass.getString(context));
        } else {
            result.message = context.getString(C0380R.string.msg_use_item_no_effect);
        }
        return result;
    }

    public static GameChar.CharClass getChangedClass(Item item) {
        int i = item.f97id;
        if (i == 1540) {
            return GameChar.CharClass.NINJA;
        }
        if (i == 5080) {
            return GameChar.CharClass.LORD;
        }
        if (i == 5090) {
            return GameChar.CharClass.SAMURAI;
        }
        if (i == 5100) {
            return GameChar.CharClass.BISHOP;
        }
        if (i == 5110) {
            return GameChar.CharClass.FIGHTER;
        }
        if (i == 5120) {
            return GameChar.CharClass.THIEF;
        }
        if (i == 5130) {
            return GameChar.CharClass.PRIEST;
        }
        if (i != 5140) {
            return null;
        }
        return GameChar.CharClass.MAGICIAN;
    }

    public static PlayerChar joinNewMember(Activity activity, Integer num, GameContext gameContext) {
        if (num == null) {
            return null;
        }
        int iIntValue = num.intValue();
        PlayerChar playerChar = new PlayerChar();
        playerChar.presetBitmapId = playerChar.getPresetBitmapIdForNpc(iIntValue);
        switch (iIntValue) {
            case 1:
                playerChar.name = activity.getString(C0380R.string.res_member_priest_name);
                playerChar.level = 5;
                playerChar.exp = GameChar.getExp(playerChar.level);
                playerChar.baseStr = 10;
                playerChar.baseInt = 14;
                playerChar.baseAgi = 11;
                playerChar.baseVit = 10;
                playerChar.statusBonus = 3;
                playerChar.clazz = GameChar.CharClass.PRIEST;
                playerChar.race = PlayerChar.Race.HUMAN;
                playerChar.addSkillId(SkillDb.SKILL_CLERIC_CURE_WOUNDS);
                break;
            case 2:
                playerChar.name = activity.getString(C0380R.string.res_member_halfling_name);
                playerChar.level = 10;
                playerChar.exp = GameChar.getExp(playerChar.level);
                playerChar.baseStr = 11;
                playerChar.baseInt = 12;
                playerChar.baseAgi = 14;
                playerChar.baseVit = 11;
                playerChar.statusBonus = 1;
                playerChar.clazz = GameChar.CharClass.THIEF;
                playerChar.race = PlayerChar.Race.HALFLING;
                playerChar.addSkillId(SkillDb.SKILL_ROGUE_DETECT_TRAP);
                playerChar.addSkillId(SkillDb.SKILL_ROGUE_DETECT_MONSTER);
                break;
            case 3:
                playerChar.name = activity.getString(C0380R.string.res_member_dwarf_name);
                playerChar.level = 20;
                playerChar.exp = GameChar.getExp(playerChar.level);
                playerChar.baseStr = 15;
                playerChar.baseInt = 11;
                playerChar.baseAgi = 11;
                playerChar.baseVit = 12;
                playerChar.statusBonus = 1;
                playerChar.clazz = GameChar.CharClass.FIGHTER;
                playerChar.race = PlayerChar.Race.DWARF;
                playerChar.addSkillId(SkillDb.SKILL_WARRIOR_POWER);
                playerChar.addSkillId(SkillDb.SKILL_WARRIOR_CONCENTRATION);
                break;
            case 4:
                playerChar.name = activity.getString(C0380R.string.res_member_elf_name);
                playerChar.level = 30;
                playerChar.exp = GameChar.getExp(playerChar.level);
                playerChar.baseStr = 11;
                playerChar.baseInt = 15;
                playerChar.baseAgi = 12;
                playerChar.baseVit = 11;
                playerChar.statusBonus = 2;
                playerChar.clazz = GameChar.CharClass.MAGICIAN;
                playerChar.race = PlayerChar.Race.ELF;
                playerChar.addSkillId(SkillDb.SKILL_SORCERER_MAGIC_MISSILE);
                playerChar.addSkillId(SkillDb.SKILL_SORCERER_SLEEP);
                playerChar.addSkillId(SkillDb.SKILL_SORCERER_HEALING);
                break;
            default:
                return null;
        }
        playerChar.calcStatus((Context) activity, gameContext);
        playerChar.restoreHpMp();
        try {
            new Persister(activity).writePlayer(playerChar);
            return playerChar;
        } catch (SQLException e) {
            DeviceUtil.handleSqliteException(activity, e);
            return null;
        }
    }

    protected String getMemberName(Context context, int i) {
        switch (i) {
            case 1:
                return context.getString(C0380R.string.res_member_priest_name);
            case 2:
                return context.getString(C0380R.string.res_member_halfling_name);
            case 3:
                return context.getString(C0380R.string.res_member_dwarf_name);
            case 4:
                return context.getString(C0380R.string.res_member_elf_name);
            default:
                return null;
        }
    }

    protected GameFlag.Key keyMemberJoined(int i) {
        return GameFlag.Key.asType(GameFlag.FlagType.OTHER, QuestConst.FLAG_PREFIX_JOIN_MEMBER + i);
    }

    /* JADX WARN: Removed duplicated region for block: B:130:0x0172  */
    /* JADX WARN: Removed duplicated region for block: B:134:0x017c  */
    /* JADX WARN: Removed duplicated region for block: B:137:0x0180  */
    /* JADX WARN: Removed duplicated region for block: B:139:0x0183  */
    /* JADX WARN: Removed duplicated region for block: B:158:0x01d1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static com.shirobakama.autorpg2.entity.Inventory synthesizeItem(android.content.Context r16, com.shirobakama.autorpg2.entity.Inventory r17, com.shirobakama.autorpg2.entity.Inventory r18, boolean r19) {
        /*
            Method dump skipped, instructions count: 522
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.adventure.TownFlagEngine.synthesizeItem(android.content.Context, com.shirobakama.autorpg2.entity.Inventory, com.shirobakama.autorpg2.entity.Inventory, boolean):com.shirobakama.autorpg2.entity.Inventory");
    }

    private static boolean isMagicBonus(Enum<?> r1) {
        return r1 == GameChar.Status.WEAPON_MAGIC_BONUS || r1 == GameChar.Status.SHIELD_MAGIC_BONUS || r1 == GameChar.Status.ARMOR_MAGIC_BONUS;
    }

    protected Result processJewelReading(FlagEngine.CurrentValues currentValues) {
        GameFlag.Key keyAsQuest = GameFlag.Key.asQuest(QuestConst.FLAG_EXTRA_DUNGEON_ITEM_MSG_READ);
        if (currentValues.isOn(keyAsQuest)) {
            return null;
        }
        Item item = ItemRepository.getItem(currentValues.context, ItemDb.ITEM_MONSTERS_JEWEL);
        if (!currentValues.isOn(GameFlag.Key.asHasItem(item))) {
            return null;
        }
        Result resultMessageResult = messageResult(currentValues, new int[]{C0380R.string.msg_quest_extra_from_lord_guardian_1, C0380R.string.msg_quest_extra_from_lord_guardian_2, C0380R.string.msg_quest_extra_from_lord_guardian_3, C0380R.string.msg_quest_extra_from_lord_guardian_4, C0380R.string.msg_quest_extra_from_lord_guardian_5}, this.state.conversationSeq == 0 ? new Object[]{item.name} : new Object[0]);
        if (this.state.isLast) {
            resultMessageResult.addFlag(new GameFlag(keyAsQuest).setValue(true));
        }
        return resultMessageResult;
    }
}
