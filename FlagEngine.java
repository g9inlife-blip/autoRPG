package com.shirobakama.autorpg2.adventure;

import android.content.Context;
import android.util.SparseArray;
import com.shirobakama.autorpg2.entity.AdventureContext;
import com.shirobakama.autorpg2.entity.AdventureLog;
import com.shirobakama.autorpg2.entity.CommonLog;
import com.shirobakama.autorpg2.entity.Decision;
import com.shirobakama.autorpg2.entity.Dungeon;
import com.shirobakama.autorpg2.entity.Enchant;
import com.shirobakama.autorpg2.entity.Enemy;
import com.shirobakama.autorpg2.entity.FightingLog;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.GameFlag;
import com.shirobakama.autorpg2.entity.Inventory;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.autorpg2.entity.Monster;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.entity.Quest;
import com.shirobakama.autorpg2.entity.Skill;
import com.shirobakama.autorpg2.entity.Stock;
import com.shirobakama.autorpg2.entity.Tactics;
import com.shirobakama.autorpg2.entity.Town;
import com.shirobakama.autorpg2.repo.DungeonRepository;
import com.shirobakama.autorpg2.repo.ItemDb;
import com.shirobakama.autorpg2.repo.ItemRepository;
import com.shirobakama.autorpg2.repo.MonsterDb;
import com.shirobakama.autorpg2.repo.MonsterRepository;
import com.shirobakama.autorpg2.repo.QuestDb;
import com.shirobakama.autorpg2.repo.QuestRepository;
import com.shirobakama.autorpg2.repo.SkillRepository;
import com.shirobakama.autorpg2.repo.TownRepository;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class FlagEngine {
    private static final int EXTRA_DUNGEON_DIFFICULTY_ADD_PER_JEWEL = 4;
    private static final int MAX_EXTRA_DUNGEON_ITEM_COUNT = 3;
    protected static final String TAG = "flag-engine";
    private static final GameFlag.Key KEY_BOSS_ENCOUNTER = new GameFlag.Key(GameFlag.FlagType.ADVENTURING, "encounter_boss");
    private static final GameFlag.Key KEY_EXTRA_DUNGEON_LEVEL = new GameFlag.Key(GameFlag.FlagType.ADVENTURING, "extra_dungeon_level");
    private static final GameFlag.Key KEY_EXTRA_DUNGEON_CONQURED = new GameFlag.Key(GameFlag.FlagType.ADVENTURING, "extra_dungeon_conqured");

    public static class EngineResult {
        public FightArgument fightArgument;
        public boolean needsRecalc;
    }

    public enum TownKnowledge {
        SECRET,
        HEARD_BUT_CANT_GO,
        KNOWN,
        CURRENT,
        CAN_MOVE
    }

    private static List<Decision.Result> processAdventuring(CurrentValues currentValues) {
        return null;
    }

    private static List<Decision.Result> processClearFloor(CurrentValues currentValues) {
        return null;
    }

    private static List<Decision.Result> processEndDungeon(CurrentValues currentValues) {
        return null;
    }

    private static List<Decision.Result> processFightingLoseMonster(CurrentValues currentValues) {
        return null;
    }

    private static List<Decision.Result> processFightingWinMonster(CurrentValues currentValues) {
        return null;
    }

    private static List<Decision.Result> processGetItem(CurrentValues currentValues) {
        return null;
    }

    private static List<Decision.Result> processLoseMonster(CurrentValues currentValues) {
        return null;
    }

    private static List<Decision.Result> processStartAdventure(CurrentValues currentValues) {
        return null;
    }

    private static List<Decision.Result> processStartDungeon(CurrentValues currentValues) {
        return null;
    }

    private static List<Decision.Result> processWinMonster(CurrentValues currentValues) {
        return null;
    }

    protected static Map<GameFlag.Key, GameFlag> getInventoryFlags(GameContext gameContext, Context context) {
        HashMap map = new HashMap();
        for (Inventory inventory : gameContext.inventories) {
            GameFlag gameFlag = new GameFlag();
            gameFlag.type = GameFlag.FlagType.HAS_ITEM;
            gameFlag.name = Integer.toString(inventory.getBaseItem(context).f97id);
            gameFlag.value = true;
            map.put(gameFlag.key(), gameFlag);
        }
        return map;
    }

    protected static Map<GameFlag.Key, GameFlag> getStockFlags(GameContext gameContext, Context context) {
        HashMap map = new HashMap();
        for (Stock stock : gameContext.stocks) {
            GameFlag gameFlag = new GameFlag();
            gameFlag.type = GameFlag.FlagType.STOCK_ITEM;
            gameFlag.name = Integer.toString(stock.getBaseItem(context).f97id);
            gameFlag.value = true;
            map.put(gameFlag.key(), gameFlag);
        }
        return map;
    }

    public static class CurrentValues {
        public int block;
        public Context context;
        public int floor;
        public GameContext game;
        private Dungeon mDungeon;
        private Map<GameFlag.Key, GameFlag> mHoldingItemFlags;
        private Map<GameFlag.Key, GameFlag> mStockFlags;
        public Random random;

        public CurrentValues(Random random, Context context, GameContext gameContext) {
            this.random = random;
            this.game = gameContext;
            this.context = context;
        }

        public CurrentValues(Random random, Context context, GameContext gameContext, int i, int i2) {
            this(random, context, gameContext);
            this.floor = i;
            this.block = i2;
        }

        public Dungeon getDungeon() {
            if (this.game.dungeonId == 0) {
                return null;
            }
            if (this.mDungeon == null) {
                this.mDungeon = DungeonRepository.getDungeon(this.context, this.game.dungeonId);
            }
            return this.mDungeon;
        }

        public GameFlag getFlag(GameFlag.Key key) {
            if (key.type == GameFlag.FlagType.HAS_ITEM || key.type == GameFlag.FlagType.STOCK_ITEM) {
                if (this.mHoldingItemFlags == null) {
                    this.mHoldingItemFlags = FlagEngine.getInventoryFlags(this.game, this.context);
                }
                if (key.type == GameFlag.FlagType.HAS_ITEM) {
                    return this.mHoldingItemFlags.get(key);
                }
                if (this.mStockFlags == null) {
                    this.mStockFlags = FlagEngine.getStockFlags(this.game, this.context);
                }
                GameFlag gameFlag = this.mStockFlags.get(key);
                if (gameFlag != null) {
                    return gameFlag;
                }
                return this.mHoldingItemFlags.get(new GameFlag.Key(GameFlag.FlagType.HAS_ITEM, key.name));
            }
            return this.game.getFlag(key);
        }

        public boolean isOn(GameFlag.Key key) {
            GameFlag flag = getFlag(key);
            return flag != null && flag.value;
        }

        public boolean isOff(GameFlag.Key key) {
            return !isOn(key);
        }

        public boolean isMonsterWon(int i) {
            GameFlag flag;
            Monster monster = MonsterRepository.getMonster(this.context, i);
            return (monster == null || (flag = getFlag(GameFlag.Key.asMonsterWin(monster))) == null || flag.getOptionAsInt() <= 0) ? false : true;
        }
    }

    public static class QuestState {
        public boolean cleared;
        public boolean started;
        public String symbol;
        public boolean terminated;

        public QuestState(String str, boolean z, boolean z2, boolean z3) {
            this.symbol = str;
            this.started = z;
            this.cleared = z2;
            this.terminated = z3;
        }
    }

    public static QuestState getQuestState(CurrentValues currentValues, String str) {
        GameFlag flag = currentValues.getFlag(new GameFlag.Key(GameFlag.FlagType.QUEST_START, str));
        GameFlag flag2 = currentValues.getFlag(new GameFlag.Key(GameFlag.FlagType.QUEST_CLEAR, str));
        GameFlag flag3 = currentValues.getFlag(new GameFlag.Key(GameFlag.FlagType.QUEST_TERM, str));
        return new QuestState(str, flag != null && flag.value, flag2 != null && flag2.value, flag3 != null && flag3.value);
    }

    public static QuestState getQuestState(GameContext gameContext, String str) {
        GameFlag flag = gameContext.getFlag(new GameFlag.Key(GameFlag.FlagType.QUEST_START, str));
        GameFlag flag2 = gameContext.getFlag(new GameFlag.Key(GameFlag.FlagType.QUEST_CLEAR, str));
        GameFlag flag3 = gameContext.getFlag(new GameFlag.Key(GameFlag.FlagType.QUEST_TERM, str));
        return new QuestState(str, flag != null && flag.value, flag2 != null && flag2.value, flag3 != null && flag3.value);
    }

    public static List<Town> getAvailableTowns(Context context, GameContext gameContext) {
        return getAvailableTowns(context, gameContext, gameContext.townId);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static List<Town> getAvailableTowns(Context context, GameContext gameContext, int i) {
        CurrentValues currentValues = new CurrentValues(new Random(), context, gameContext);
        ArrayList arrayList = new ArrayList();
        switch (i) {
            case 1:
                arrayList.add(TownRepository.getTown(currentValues.context, 2));
                return arrayList;
            case 2:
                arrayList.add(TownRepository.getTown(currentValues.context, 1));
                arrayList.add(TownRepository.getTown(currentValues.context, 3));
                return arrayList;
            case 3:
                arrayList.add(TownRepository.getTown(currentValues.context, 2));
                if (currentValues.isOn(GameFlag.Key.asClearDungeon(DungeonRepository.getDungeon(context, 6)))) {
                    arrayList.add(TownRepository.getTown(currentValues.context, 4));
                }
                if (currentValues.isOn(GameFlag.Key.asQuest(QuestConst.LAKECAVE_FLAG_HELP_SMALLMAN)) && gameContext.isRaceActiveMember(PlayerChar.Race.HALFLING)) {
                    arrayList.add(TownRepository.getTown(currentValues.context, 11));
                }
                return arrayList;
            case 4:
                arrayList.add(TownRepository.getTown(currentValues.context, 3));
                arrayList.add(TownRepository.getTown(currentValues.context, 6));
                arrayList.add(TownRepository.getTown(currentValues.context, 7));
                arrayList.add(TownRepository.getTown(currentValues.context, 5));
                return arrayList;
            case 5:
                arrayList.add(TownRepository.getTown(currentValues.context, 4));
                if (currentValues.isOn(GameFlag.Key.asQuest(QuestConst.MINE_FLAG_HELP_DWARF)) && gameContext.isRaceActiveMember(PlayerChar.Race.DWARF)) {
                    arrayList.add(TownRepository.getTown(currentValues.context, 12));
                }
                return arrayList;
            case 6:
                arrayList.add(TownRepository.getTown(currentValues.context, 4));
                if (currentValues.isOn(GameFlag.Key.asQuest(QuestConst.SHIP_FLAG_REPOSED))) {
                    arrayList.add(TownRepository.getTown(currentValues.context, 10));
                }
                if (getQuestState(currentValues, QuestDb.QUEST_ATTACK_WIZARD).cleared) {
                    arrayList.add(TownRepository.getTown(currentValues.context, 8));
                }
                return arrayList;
            case 7:
                arrayList.add(TownRepository.getTown(currentValues.context, 4));
                if (currentValues.isOn(GameFlag.Key.asQuest(QuestConst.CATACOMBE_FLAG_HELP_ELF)) && gameContext.isRaceActiveMember(PlayerChar.Race.ELF)) {
                    arrayList.add(TownRepository.getTown(currentValues.context, 13));
                }
                return arrayList;
            case 8:
                arrayList.add(TownRepository.getTown(currentValues.context, 6));
                if (currentValues.isOn(GameFlag.Key.asClearDungeon(DungeonRepository.getDungeon(context, 18)))) {
                    arrayList.add(TownRepository.getTown(currentValues.context, 9));
                }
                return arrayList;
            case 9:
                arrayList.add(TownRepository.getTown(currentValues.context, 8));
                return arrayList;
            case 10:
                arrayList.add(TownRepository.getTown(currentValues.context, 6));
                return arrayList;
            case 11:
                arrayList.add(TownRepository.getTown(currentValues.context, 3));
                return arrayList;
            case 12:
                arrayList.add(TownRepository.getTown(currentValues.context, 5));
                return arrayList;
            case 13:
                arrayList.add(TownRepository.getTown(currentValues.context, 7));
                return arrayList;
            default:
                return arrayList;
        }
    }

    public static void setSecretTownsKnowledge(Context context, GameContext gameContext, SparseArray<TownKnowledge> sparseArray) {
        CurrentValues currentValues = new CurrentValues(new Random(), context, gameContext);
        if (sparseArray.indexOfKey(11) < 0) {
            boolean zIsOn = currentValues.isOn(GameFlag.Key.asType(GameFlag.FlagType.QUEST_START, QuestDb.QUEST_WILDERNESS_MONSTER));
            boolean z = gameContext.isRaceActiveMember(PlayerChar.Race.HALFLING) || zIsOn;
            if (currentValues.isOn(GameFlag.Key.asQuest(QuestConst.LAKECAVE_FLAG_HELP_SMALLMAN)) && !z) {
                sparseArray.put(11, TownKnowledge.HEARD_BUT_CANT_GO);
            } else if (zIsOn) {
                sparseArray.put(11, TownKnowledge.KNOWN);
            } else {
                sparseArray.put(11, TownKnowledge.SECRET);
            }
        }
        if (sparseArray.indexOfKey(12) < 0) {
            boolean zIsOn2 = currentValues.isOn(GameFlag.Key.asType(GameFlag.FlagType.QUEST_START, QuestDb.QUEST_DRAGON_OF_VOLCANO));
            boolean z2 = gameContext.isRaceActiveMember(PlayerChar.Race.DWARF) || zIsOn2;
            if (currentValues.isOn(GameFlag.Key.asQuest(QuestConst.MINE_FLAG_HELP_DWARF)) && !z2) {
                sparseArray.put(12, TownKnowledge.HEARD_BUT_CANT_GO);
            } else if (zIsOn2) {
                sparseArray.put(12, TownKnowledge.KNOWN);
            } else {
                sparseArray.put(12, TownKnowledge.SECRET);
            }
        }
        if (sparseArray.indexOfKey(13) < 0) {
            boolean zIsOn3 = currentValues.isOn(GameFlag.Key.asType(GameFlag.FlagType.QUEST_START, QuestDb.QUEST_FOREST_RAIDER));
            boolean z3 = gameContext.isRaceActiveMember(PlayerChar.Race.ELF) || zIsOn3;
            if (currentValues.isOn(GameFlag.Key.asQuest(QuestConst.CATACOMBE_FLAG_HELP_ELF)) && !z3) {
                sparseArray.put(13, TownKnowledge.HEARD_BUT_CANT_GO);
            } else if (zIsOn3) {
                sparseArray.put(13, TownKnowledge.KNOWN);
            } else {
                sparseArray.put(13, TownKnowledge.SECRET);
            }
        }
    }

    public static int[] getRelatedTownIds(int i) {
        switch (i) {
            case 1:
                return new int[]{2};
            case 2:
                return new int[]{1, 3};
            case 3:
                return new int[]{2, 4, 11};
            case 4:
                return new int[]{3, 6, 7, 5};
            case 5:
                return new int[]{4, 12};
            case 6:
                return new int[]{4, 8, 10};
            case 7:
                return new int[]{4, 13};
            case 8:
                return new int[]{6, 9};
            case 9:
                return new int[]{8};
            case 10:
                return new int[]{6};
            case 11:
                return new int[]{3};
            case 12:
                return new int[]{5};
            case 13:
                return new int[]{7};
            default:
                return new int[0];
        }
    }

    public static boolean existTownRelation(int i, int i2) {
        if (i == 0 || i2 == 0) {
            return false;
        }
        for (int i3 : getRelatedTownIds(i)) {
            if (i3 == i2) {
                return true;
            }
        }
        return false;
    }

    public static List<Dungeon> getAvailableDungeons(Context context, GameContext gameContext) {
        return getAvailableDungeons(context, gameContext, gameContext.townId);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static List<Dungeon> getAvailableDungeons(Context context, GameContext gameContext, int i) {
        CurrentValues currentValues = new CurrentValues(new Random(), context, gameContext);
        ArrayList arrayList = new ArrayList();
        switch (i) {
            case 1:
                arrayList.add(DungeonRepository.getDungeon(currentValues.context, 1));
                arrayList.add(DungeonRepository.getDungeon(currentValues.context, 2));
                return arrayList;
            case 2:
                arrayList.add(DungeonRepository.getDungeon(currentValues.context, 3));
                QuestState questState = getQuestState(currentValues, QuestDb.QUEST_MIDNIGHT_FLOWER);
                if (questState.started || questState.cleared) {
                    arrayList.add(DungeonRepository.getDungeon(currentValues.context, 4));
                }
                return arrayList;
            case 3:
                arrayList.add(DungeonRepository.getDungeon(currentValues.context, 5));
                arrayList.add(DungeonRepository.getDungeon(currentValues.context, 6));
                return arrayList;
            case 4:
                QuestState questState2 = getQuestState(currentValues, QuestDb.QUEST_PROOF_OF_ASSASSINATION);
                if (questState2.started || questState2.cleared) {
                    arrayList.add(DungeonRepository.getDungeon(currentValues.context, 8));
                }
                QuestState questState3 = getQuestState(currentValues, QuestDb.QUEST_AT_TEMPLE);
                if (questState3.started || questState3.cleared) {
                    arrayList.add(DungeonRepository.getDungeon(currentValues.context, 13));
                }
                return arrayList;
            case 5:
                arrayList.add(DungeonRepository.getDungeon(currentValues.context, 9));
                return arrayList;
            case 6:
                arrayList.add(DungeonRepository.getDungeon(currentValues.context, 11));
                return arrayList;
            case 7:
                QuestState questState4 = getQuestState(currentValues, QuestDb.QUEST_INTRIGUE_OF_WIZARD);
                if (questState4.started || questState4.cleared) {
                    arrayList.add(DungeonRepository.getDungeon(currentValues.context, 14));
                }
                QuestState questState5 = getQuestState(currentValues, QuestDb.QUEST_ATTACK_WIZARD);
                if (questState5.started || questState5.cleared) {
                    arrayList.add(DungeonRepository.getDungeon(currentValues.context, 16));
                }
                return arrayList;
            case 8:
                arrayList.add(DungeonRepository.getDungeon(currentValues.context, 17).copy());
                arrayList.add(DungeonRepository.getDungeon(currentValues.context, 18).copy());
                if (currentValues.isOn(GameFlag.Key.asQuest(QuestConst.FLAG_EXTRA_DUNGEON_ITEM_MSG_READ))) {
                    Dungeon dungeonCopy = DungeonRepository.getDungeon(currentValues.context, 20).copy();
                    dungeonCopy.difficultyStars += getExtraDungeonItemCount(context, gameContext, false);
                    arrayList.add(dungeonCopy);
                }
                return arrayList;
            case 9:
                arrayList.add(DungeonRepository.getDungeon(currentValues.context, 19));
                return arrayList;
            case 10:
                arrayList.add(DungeonRepository.getDungeon(currentValues.context, 12));
                return arrayList;
            case 11:
                arrayList.add(DungeonRepository.getDungeon(currentValues.context, 7));
                return arrayList;
            case 12:
                arrayList.add(DungeonRepository.getDungeon(currentValues.context, 10));
                return arrayList;
            case 13:
                arrayList.add(DungeonRepository.getDungeon(currentValues.context, 15));
                return arrayList;
            default:
                return arrayList;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static EngineResult processInAdventure(Context context, Random random, GameContext gameContext, Decision.Timing timing, List<AdventureLog> list) {
        List<Decision.Result> listProcessStartAdventure;
        CurrentValues currentValues = new CurrentValues(random, context, gameContext, gameContext.adventureContext.floor, gameContext.adventureContext.block);
        switch (timing) {
            case AVAILABLE_DUNGEONS:
            case AVAILABLE_TOWNS:
            case ENTER_TOWN:
            case HEARING:
            case FIGHTING_START_FIGHT:
            case FIGHTING_LOSE_MONSTER:
            case FIGHTING_WIN_MONSTER:
                throw new IllegalStateException(timing.toString());
            case START_ADVENTURE:
                listProcessStartAdventure = processStartAdventure(currentValues);
                break;
            case START_DUNGEON:
                listProcessStartAdventure = processStartDungeon(currentValues);
                break;
            case ADVENTURING:
                listProcessStartAdventure = processAdventuring(currentValues);
                break;
            case CLEAR_DUNGEON:
                listProcessStartAdventure = processClearDungeon(currentValues);
                break;
            case CLEAR_FLOOR:
                listProcessStartAdventure = processClearFloor(currentValues);
                break;
            case GET_ITEM:
                listProcessStartAdventure = processGetItem(currentValues);
                break;
            case START_FLOOR:
                listProcessStartAdventure = processStartFloor(currentValues);
                break;
            case START_BLOCK:
                listProcessStartAdventure = processStartBlock(currentValues);
                break;
            case START_BLOCK_ADDITIONAL_LOG:
                listProcessStartAdventure = processStartBlockAdditionalLog(currentValues);
                break;
            case USE_ITEM_IN_BLOCK:
                listProcessStartAdventure = processUseItemInBlock(currentValues);
                break;
            case END_DUNGEON:
                listProcessStartAdventure = processEndDungeon(currentValues);
                break;
            case LOSE_MONSTER:
                listProcessStartAdventure = processLoseMonster(currentValues);
                break;
            case WIN_MONSTER:
                listProcessStartAdventure = processWinMonster(currentValues);
                break;
            default:
                listProcessStartAdventure = null;
                break;
        }
        if (listProcessStartAdventure == null || listProcessStartAdventure.isEmpty()) {
            return null;
        }
        EngineResult engineResult = new EngineResult();
        for (Decision.Result result : listProcessStartAdventure) {
            switch (result.type) {
                case DUNGEON_AVAILABLE:
                case SHOW_MESSAGE:
                case TOWN_AVAILABLE:
                    throw new IllegalStateException(result.toString());
                case END_ADVENTURE:
                    gameContext.adventureContext.aborting = true;
                    break;
                case ADD_LOG:
                    list.add(copyResultLogToLog(new AdventureLog(gameContext, null), result));
                    break;
                case ADD_ENCHANT:
                    switch (result.enchant.target) {
                        case FIELD:
                            gameContext.adventureContext.addEnchant(result.enchant);
                            break;
                        case PLAYER_CHAR:
                            gameContext.adventureContext.addEnchantToPlayer(result.enchantIndex, result.enchant);
                            break;
                    }
                    engineResult.needsRecalc = true;
                    break;
                case FLAG_ON:
                case FLAG_OFF:
                case FLAG_INCREMENT:
                case FLAG_ON_INCREMENT:
                    handleFlagResult(gameContext, result);
                    break;
                case GET_ITEM:
                    list.add(handleGetLostItem(context, gameContext, result, new AdventureLog(gameContext, null)));
                    break;
                case LOST_ITEM:
                    list.add(handleGetLostItem(context, gameContext, result, new AdventureLog(gameContext, null)));
                    engineResult.needsRecalc = true;
                    break;
                case MOVE_TO_BLOCK:
                    int i = result.otherValue;
                    gameContext.adventureContext.block = i;
                    gameContext.adventureContext.inBlockProgress = 0;
                    AdventureLog adventureLog = new AdventureLog(gameContext, null);
                    adventureLog.type = CommonLog.LogType.UNDEFINED;
                    adventureLog.title = "move to block:" + i;
                    list.add(adventureLog);
                    break;
                case MOVE_TO_FLOOR:
                    int i2 = result.otherValue;
                    gameContext.adventureContext.floor = i2;
                    gameContext.adventureContext.block = 0;
                    gameContext.adventureContext.inBlockProgress = 0;
                    AdventureLog adventureLog2 = new AdventureLog(gameContext, null);
                    adventureLog2.type = CommonLog.LogType.UNDEFINED;
                    adventureLog2.title = "move to floor:" + i2;
                    list.add(adventureLog2);
                    break;
                case MOVE_TO_DUNGEON:
                    gameContext.adventureContext.dungeon = DungeonRepository.getDungeon(context, result.f89id);
                    gameContext.adventureContext.floor = 0;
                    gameContext.adventureContext.block = 0;
                    gameContext.adventureContext.inBlockProgress = 0;
                    AdventureLog adventureLog3 = new AdventureLog(gameContext, null);
                    adventureLog3.type = CommonLog.LogType.UNDEFINED;
                    adventureLog3.title = "move to " + gameContext.adventureContext.dungeon.name;
                    list.add(adventureLog3);
                    break;
                case MOVE_TO_TOWN:
                    int i3 = result.f89id;
                    gameContext.townId = i3;
                    gameContext.adventureContext.aborting = true;
                    Town town = TownRepository.getTown(context, i3);
                    AdventureLog adventureLog4 = new AdventureLog(gameContext, null);
                    adventureLog4.type = CommonLog.LogType.UNDEFINED;
                    adventureLog4.title = "move to " + town.name;
                    list.add(adventureLog4);
                    break;
                case ENCOUNTER:
                    if (engineResult.fightArgument != null) {
                        throw new IllegalStateException("Duplicate fighting:" + result);
                    }
                    engineResult.fightArgument = new FightArgument(false, true, result.f89id, result.otherValue);
                    break;
                case TRAP:
                    throw new UnsupportedOperationException(result.toString());
                case TREASURE_BOX:
                    throw new UnsupportedOperationException(result.toString());
            }
        }
        return engineResult;
    }

    private static List<Decision.Result> processClearDungeon(CurrentValues currentValues) {
        if (currentValues.game.dungeonId == 1) {
            List<Decision.Result> listProcessQuestClear = processQuestClear(currentValues, getQuestState(currentValues, QuestDb.QUEST_FIRST_FOREST_MONSTER));
            Item item = ItemRepository.getItem(currentValues.context, ItemDb.ITEM_RUSTED_KEY);
            if (currentValues.isOff(GameFlag.Key.asStockItem(item))) {
                Decision.Result resultForGetItem = Decision.Result.forGetItem(item);
                resultForGetItem.log = new FightingLog();
                resultForGetItem.log.type = CommonLog.LogType.TREASURE;
                resultForGetItem.log.title = currentValues.context.getString(C0380R.string.alog_title_get_item, item.name);
                resultForGetItem.log.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_forest_monster_find_key, item.name);
                listProcessQuestClear.add(resultForGetItem);
            }
            if (listProcessQuestClear.isEmpty()) {
                return null;
            }
            return listProcessQuestClear;
        }
        if (currentValues.game.dungeonId == 2) {
            return processQuestClear(currentValues, getQuestState(currentValues, QuestDb.QUEST_CAVE_GOBLINS));
        }
        if (currentValues.game.dungeonId == 3) {
            QuestState questState = getQuestState(currentValues, QuestDb.QUEST_GIRL_RESCUE);
            LinkedList linkedList = new LinkedList();
            if (!questState.cleared) {
                AdventureLog adventureLog = new AdventureLog();
                adventureLog.type = CommonLog.LogType.ADVENTURE_EVENT;
                adventureLog.title = currentValues.context.getString(C0380R.string.alog_title_quest_rescue_help_1);
                adventureLog.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_rescue_help_1);
                linkedList.add(Decision.Result.forAddLog(adventureLog));
                PlayerChar mostPowerfulChar = currentValues.game.getMostPowerfulChar();
                AdventureLog adventureLog2 = new AdventureLog();
                adventureLog2.type = CommonLog.LogType.ADVENTURE_EVENT;
                adventureLog2.title = currentValues.context.getString(C0380R.string.alog_title_quest_rescue_help_2);
                adventureLog2.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_rescue_help_2, mostPowerfulChar.name);
                linkedList.add(Decision.Result.forAddLog(adventureLog2));
            }
            linkedList.addAll(processQuestClear(currentValues, questState));
            Item item2 = ItemRepository.getItem(currentValues.context, ItemDb.ITEM_SILVER_HANDLE);
            if (currentValues.isOff(GameFlag.Key.asStockItem(item2))) {
                Decision.Result resultForGetItem2 = Decision.Result.forGetItem(item2);
                resultForGetItem2.log = new AdventureLog();
                resultForGetItem2.log.type = CommonLog.LogType.TREASURE;
                resultForGetItem2.log.title = currentValues.context.getString(C0380R.string.alog_title_get_item, item2.name);
                resultForGetItem2.log.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_base_find_handle, item2.name);
                linkedList.add(resultForGetItem2);
            }
            return linkedList;
        }
        if (currentValues.game.dungeonId == 4) {
            QuestState questState2 = getQuestState(currentValues, QuestDb.QUEST_MIDNIGHT_FLOWER);
            LinkedList linkedList2 = new LinkedList();
            if (questState2.started && !questState2.cleared) {
                GameFlag.Key key = new GameFlag.Key(GameFlag.FlagType.ADVENTURING, QuestConst.MIDNIGHT_FLOWER_GOT_PROCESSED);
                if (currentValues.isOff(key)) {
                    linkedList2.add(Decision.Result.forFlagOn(key));
                    Item item3 = ItemRepository.getItem(currentValues.context, ItemDb.ITEM_PURPLE_FLOWER);
                    if (currentValues.isOff(GameFlag.Key.asStockItem(item3))) {
                        int i = currentValues.game.adventureContext.calendar.get(11);
                        if (i >= 20 || i <= 4) {
                            Decision.Result resultForGetItem3 = Decision.Result.forGetItem(item3);
                            resultForGetItem3.log = new AdventureLog();
                            resultForGetItem3.log.type = CommonLog.LogType.TREASURE;
                            resultForGetItem3.log.title = currentValues.context.getString(C0380R.string.alog_title_get_item, item3.name);
                            resultForGetItem3.log.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_flower_find_flower, item3.name);
                            linkedList2.add(resultForGetItem3);
                        } else {
                            AdventureLog adventureLog3 = new AdventureLog();
                            adventureLog3.type = CommonLog.LogType.ADVENTURE_EVENT;
                            adventureLog3.title = currentValues.context.getString(C0380R.string.alog_title_quest_flower_dont_find_flower);
                            adventureLog3.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_flower_dont_find_flower);
                            linkedList2.add(Decision.Result.forAddLog(adventureLog3));
                        }
                    }
                }
            }
            return linkedList2;
        }
        if (currentValues.game.dungeonId == 5) {
            return null;
        }
        if (currentValues.game.dungeonId == 6) {
            return processQuestClear(currentValues, getQuestState(currentValues, QuestDb.QUEST_DANGER_PATH));
        }
        if (currentValues.game.dungeonId == 7) {
            LinkedList linkedList3 = new LinkedList();
            linkedList3.addAll(processQuestClear(currentValues, getQuestState(currentValues, QuestDb.QUEST_WILDERNESS_MONSTER)));
            Item item4 = ItemRepository.getItem(currentValues.context, 1300);
            if (currentValues.isOff(GameFlag.Key.asStockItem(item4))) {
                Decision.Result resultForGetItem4 = Decision.Result.forGetItem(item4);
                resultForGetItem4.log = new AdventureLog();
                resultForGetItem4.log.type = CommonLog.LogType.TREASURE;
                resultForGetItem4.log.title = currentValues.context.getString(C0380R.string.alog_title_get_item, item4.name);
                resultForGetItem4.log.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_wilderness_find_item, item4.name);
                linkedList3.add(resultForGetItem4);
            }
            return linkedList3;
        }
        if (currentValues.game.dungeonId == 8) {
            QuestState questState3 = getQuestState(currentValues, QuestDb.QUEST_PROOF_OF_ASSASSINATION);
            LinkedList linkedList4 = new LinkedList();
            if (questState3.started && !questState3.cleared) {
                Item item5 = ItemRepository.getItem(currentValues.context, ItemDb.ITEM_PAPER_FOLDER);
                if (currentValues.isOff(GameFlag.Key.asStockItem(item5))) {
                    PlayerChar mostIntelliChar = currentValues.game.getMostIntelliChar();
                    Decision.Result resultForGetItem5 = Decision.Result.forGetItem(item5);
                    resultForGetItem5.log = new AdventureLog();
                    resultForGetItem5.log.type = CommonLog.LogType.TREASURE;
                    resultForGetItem5.log.title = currentValues.context.getString(C0380R.string.alog_title_get_item, item5.name);
                    resultForGetItem5.log.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_proof_find_folder, mostIntelliChar.name, item5.name);
                    linkedList4.add(resultForGetItem5);
                }
            }
            return linkedList4;
        }
        if (currentValues.game.dungeonId == 9) {
            LinkedList linkedList5 = new LinkedList();
            Item item6 = ItemRepository.getItem(currentValues.context, ItemDb.ITEM_SOUL_AMULET);
            if (currentValues.isOff(GameFlag.Key.asStockItem(item6))) {
                Decision.Result resultForGetItem6 = Decision.Result.forGetItem(item6);
                resultForGetItem6.log = new AdventureLog();
                resultForGetItem6.log.type = CommonLog.LogType.TREASURE;
                resultForGetItem6.log.title = currentValues.context.getString(C0380R.string.alog_title_get_item, item6.name);
                resultForGetItem6.log.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_ghost_find_amulet, item6.name);
                linkedList5.add(resultForGetItem6);
            }
            return linkedList5;
        }
        if (currentValues.game.dungeonId == 10) {
            LinkedList linkedList6 = new LinkedList();
            if (currentValues.isOff(GameFlag.Key.asClearDungeon(currentValues.getDungeon())) && currentValues.isOff(KEY_BOSS_ENCOUNTER)) {
                linkedList6.add(Decision.Result.forEncounter(1390, 1));
                linkedList6.add(Decision.Result.forFlagOn(KEY_BOSS_ENCOUNTER));
                return linkedList6;
            }
            QuestState questState4 = getQuestState(currentValues, QuestDb.QUEST_DRAGON_OF_VOLCANO);
            if (!questState4.cleared) {
                linkedList6.addAll(processQuestClear(currentValues, questState4));
            }
            Item item7 = ItemRepository.getItem(currentValues.context, ItemDb.ITEM_DWARVEN_ANVIL);
            if (currentValues.isOff(GameFlag.Key.asStockItem(item7))) {
                Decision.Result resultForGetItem7 = Decision.Result.forGetItem(item7);
                resultForGetItem7.log = new AdventureLog();
                resultForGetItem7.log.type = CommonLog.LogType.TREASURE;
                resultForGetItem7.log.title = currentValues.context.getString(C0380R.string.alog_title_get_item, item7.name);
                resultForGetItem7.log.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_volcano_find_item, item7.name);
                linkedList6.add(resultForGetItem7);
            }
            return linkedList6;
        }
        if (currentValues.game.dungeonId == 11) {
            return null;
        }
        if (currentValues.game.dungeonId == 12) {
            LinkedList linkedList7 = new LinkedList();
            if (currentValues.isOff(GameFlag.Key.asClearDungeon(currentValues.getDungeon())) && currentValues.isOff(KEY_BOSS_ENCOUNTER)) {
                linkedList7.add(Decision.Result.forEncounter(1510, 1));
                linkedList7.add(Decision.Result.forFlagOn(KEY_BOSS_ENCOUNTER));
                return linkedList7;
            }
            Item item8 = ItemRepository.getItem(currentValues.context, 1290);
            if (currentValues.isOff(GameFlag.Key.asStockItem(item8))) {
                Decision.Result resultForGetItem8 = Decision.Result.forGetItem(item8);
                resultForGetItem8.log = new AdventureLog();
                resultForGetItem8.log.type = CommonLog.LogType.TREASURE;
                resultForGetItem8.log.title = currentValues.context.getString(C0380R.string.alog_title_get_item, item8.name);
                resultForGetItem8.log.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_holy_get_sword, item8.name);
                linkedList7.add(resultForGetItem8);
            }
            return linkedList7;
        }
        if (currentValues.game.dungeonId == 13) {
            QuestState questState5 = getQuestState(currentValues, QuestDb.QUEST_AT_TEMPLE);
            LinkedList linkedList8 = new LinkedList();
            if (questState5.started && !questState5.cleared) {
                GameFlag.Key keyAsQuest = GameFlag.Key.asQuest(QuestConst.TEMPLE_FLAG_REPOSED);
                if (currentValues.isOff(keyAsQuest)) {
                    linkedList8.add(Decision.Result.forFlagOn(keyAsQuest));
                    AdventureLog adventureLog4 = new AdventureLog();
                    adventureLog4.type = CommonLog.LogType.ADVENTURE_EVENT;
                    adventureLog4.title = currentValues.context.getString(C0380R.string.alog_title_quest_temple_repose);
                    adventureLog4.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_temple_repose);
                    linkedList8.add(Decision.Result.forAddLog(adventureLog4));
                }
            }
            return linkedList8;
        }
        if (currentValues.game.dungeonId == 14) {
            QuestState questState6 = getQuestState(currentValues, QuestDb.QUEST_INTRIGUE_OF_WIZARD);
            LinkedList linkedList9 = new LinkedList();
            if (questState6.started && !questState6.cleared) {
                GameFlag.Key keyAsQuest2 = GameFlag.Key.asQuest(QuestConst.CATACOMBE_FLAG_HELP_ELF);
                if (currentValues.isOff(keyAsQuest2)) {
                    linkedList9.add(Decision.Result.forFlagOn(keyAsQuest2));
                    PlayerChar mostPowerfulChar2 = currentValues.game.getMostPowerfulChar();
                    boolean zIsRaceActiveMember = currentValues.game.isRaceActiveMember(PlayerChar.Race.ELF);
                    AdventureLog adventureLog5 = new AdventureLog();
                    adventureLog5.type = CommonLog.LogType.ADVENTURE_EVENT;
                    adventureLog5.title = currentValues.context.getString(C0380R.string.alog_title_quest_intrigue_help_1);
                    adventureLog5.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_intrigue_help_1, mostPowerfulChar2.name);
                    linkedList9.add(Decision.Result.forAddLog(adventureLog5));
                    AdventureLog adventureLog6 = new AdventureLog();
                    adventureLog6.type = CommonLog.LogType.ADVENTURE_EVENT;
                    adventureLog6.title = currentValues.context.getString(C0380R.string.alog_title_quest_intrigue_help_2);
                    adventureLog6.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_intrigue_help_2);
                    linkedList9.add(Decision.Result.forAddLog(adventureLog6));
                    AdventureLog adventureLog7 = new AdventureLog();
                    adventureLog7.type = CommonLog.LogType.ADVENTURE_EVENT;
                    adventureLog7.title = currentValues.context.getString(C0380R.string.alog_title_quest_intrigue_help_3);
                    adventureLog7.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_intrigue_help_3);
                    linkedList9.add(Decision.Result.forAddLog(adventureLog7));
                    AdventureLog adventureLog8 = new AdventureLog();
                    adventureLog8.type = CommonLog.LogType.ADVENTURE_EVENT;
                    adventureLog8.title = currentValues.context.getString(C0380R.string.alog_title_quest_intrigue_help_4);
                    adventureLog8.desc1 = currentValues.context.getString(zIsRaceActiveMember ? C0380R.string.alog_desc_quest_intrigue_help_4_1 : C0380R.string.alog_desc_quest_intrigue_help_4_2);
                    linkedList9.add(Decision.Result.forAddLog(adventureLog8));
                }
            }
            return linkedList9;
        }
        if (currentValues.game.dungeonId == 15) {
            LinkedList linkedList10 = new LinkedList();
            if (currentValues.isOff(GameFlag.Key.asClearDungeon(currentValues.getDungeon())) && currentValues.isOff(KEY_BOSS_ENCOUNTER)) {
                linkedList10.add(Decision.Result.forEncounter(1500, 1));
                linkedList10.add(Decision.Result.forFlagOn(KEY_BOSS_ENCOUNTER));
                return linkedList10;
            }
            QuestState questState7 = getQuestState(currentValues, QuestDb.QUEST_FOREST_RAIDER);
            if (!questState7.cleared) {
                Monster monster = MonsterRepository.getMonster(currentValues.context, 1500);
                AdventureLog adventureLog9 = new AdventureLog();
                adventureLog9.type = CommonLog.LogType.ADVENTURE_EVENT;
                adventureLog9.title = currentValues.context.getString(C0380R.string.alog_title_quest_fog_defeat_1, monster.name);
                adventureLog9.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_fog_defeat_1);
                linkedList10.add(Decision.Result.forAddLog(adventureLog9));
                linkedList10.addAll(processQuestClear(currentValues, questState7));
            }
            Item item9 = ItemRepository.getItem(currentValues.context, 1530);
            if (currentValues.isOff(GameFlag.Key.asStockItem(item9))) {
                AdventureLog adventureLog10 = new AdventureLog();
                adventureLog10.type = CommonLog.LogType.ADVENTURE_EVENT;
                adventureLog10.title = currentValues.context.getString(C0380R.string.alog_title_quest_fog_find_item_1);
                adventureLog10.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_fog_find_item_1);
                linkedList10.add(Decision.Result.forAddLog(adventureLog10));
                Decision.Result resultForGetItem9 = Decision.Result.forGetItem(item9);
                resultForGetItem9.log = new AdventureLog();
                resultForGetItem9.log.type = CommonLog.LogType.TREASURE;
                resultForGetItem9.log.title = currentValues.context.getString(C0380R.string.alog_title_get_item, item9.name);
                resultForGetItem9.log.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_fog_find_item_2, item9.name);
                linkedList10.add(resultForGetItem9);
            }
            return linkedList10;
        }
        if (currentValues.game.dungeonId == 16) {
            LinkedList linkedList11 = new LinkedList();
            if (currentValues.isOff(GameFlag.Key.asClearDungeon(currentValues.getDungeon())) && currentValues.isOff(KEY_BOSS_ENCOUNTER)) {
                linkedList11.add(Decision.Result.forEncounter(1550, 1));
                linkedList11.add(Decision.Result.forFlagOn(KEY_BOSS_ENCOUNTER));
                return linkedList11;
            }
            GameFlag.Key keyAsQuest3 = GameFlag.Key.asQuest(QuestConst.TOWER_FLAG_DEFEAT_WIZARD);
            if (!currentValues.isOff(keyAsQuest3)) {
                return linkedList11;
            }
            linkedList11.add(Decision.Result.forFlagOn(keyAsQuest3));
            AdventureLog adventureLog11 = new AdventureLog();
            adventureLog11.type = CommonLog.LogType.ADVENTURE_EVENT;
            adventureLog11.title = currentValues.context.getString(C0380R.string.alog_title_quest_wizard_defeat_1);
            adventureLog11.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_wizard_defeat_1);
            linkedList11.add(Decision.Result.forAddLog(adventureLog11));
            AdventureLog adventureLog12 = new AdventureLog();
            adventureLog12.type = CommonLog.LogType.ADVENTURE_EVENT;
            adventureLog12.title = currentValues.context.getString(C0380R.string.alog_title_quest_wizard_defeat_2);
            adventureLog12.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_wizard_defeat_2);
            linkedList11.add(Decision.Result.forAddLog(adventureLog12));
            AdventureLog adventureLog13 = new AdventureLog();
            adventureLog13.type = CommonLog.LogType.ADVENTURE_EVENT;
            adventureLog13.title = currentValues.context.getString(C0380R.string.alog_title_quest_wizard_defeat_3);
            adventureLog13.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_wizard_defeat_3);
            linkedList11.add(Decision.Result.forAddLog(adventureLog13));
            AdventureLog adventureLog14 = new AdventureLog();
            adventureLog14.type = CommonLog.LogType.ADVENTURE_EVENT;
            adventureLog14.title = currentValues.context.getString(C0380R.string.alog_title_quest_wizard_defeat_4);
            adventureLog14.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_wizard_defeat_4);
            linkedList11.add(Decision.Result.forAddLog(adventureLog14));
            return linkedList11;
        }
        if (currentValues.game.dungeonId == 17) {
            LinkedList linkedList12 = new LinkedList();
            Item item10 = ItemRepository.getItem(currentValues.context, ItemDb.ITEM_DEMON_AMULET);
            if (currentValues.isOff(GameFlag.Key.asStockItem(item10))) {
                AdventureLog adventureLog15 = new AdventureLog();
                adventureLog15.type = CommonLog.LogType.ADVENTURE_EVENT;
                adventureLog15.title = currentValues.context.getString(C0380R.string.alog_title_quest_lord_find_amulet_1);
                adventureLog15.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_lord_find_amulet_1);
                linkedList12.add(Decision.Result.forAddLog(adventureLog15));
                Decision.Result resultForGetItem10 = Decision.Result.forGetItem(item10);
                resultForGetItem10.log = new AdventureLog();
                resultForGetItem10.log.type = CommonLog.LogType.TREASURE;
                resultForGetItem10.log.title = currentValues.context.getString(C0380R.string.alog_title_get_item, item10.name);
                resultForGetItem10.log.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_lord_find_amulet_2, item10.name);
                linkedList12.add(resultForGetItem10);
            }
            return linkedList12;
        }
        if (currentValues.game.dungeonId == 18) {
            return null;
        }
        if (currentValues.game.dungeonId == 19) {
            LinkedList linkedList13 = new LinkedList();
            boolean zIsOn = currentValues.isOn(GameFlag.Key.asHasItem(ItemRepository.getItem(currentValues.context, ItemDb.ITEM_DEMON_AMULET)));
            int i2 = zIsOn ? 1560 : 1570;
            if (currentValues.isOff(GameFlag.Key.asClearDungeon(currentValues.getDungeon()))) {
                if (currentValues.isOff(KEY_BOSS_ENCOUNTER)) {
                    linkedList13.add(Decision.Result.forEncounter(i2, 1));
                    linkedList13.add(Decision.Result.forFlagOn(KEY_BOSS_ENCOUNTER));
                    return linkedList13;
                }
            } else if (currentValues.isOff(KEY_BOSS_ENCOUNTER)) {
                linkedList13.add(Decision.Result.forEncounter(zIsOn ? MonsterDb.MONSTER_DARK_LOAD_AMULET_GHOST : MonsterDb.MONSTER_DARK_LOAD_NORMAL_GHOST, 1));
                linkedList13.add(Decision.Result.forFlagOn(KEY_BOSS_ENCOUNTER));
                return linkedList13;
            }
            GameFlag.Key keyAsQuest4 = GameFlag.Key.asQuest(QuestConst.PALACE_FLAG_DEFEAT_LORD);
            if (!currentValues.isOff(keyAsQuest4)) {
                return linkedList13;
            }
            linkedList13.add(Decision.Result.forFlagOn(keyAsQuest4));
            Monster monster2 = MonsterRepository.getMonster(currentValues.context, i2);
            AdventureLog adventureLog16 = new AdventureLog();
            adventureLog16.type = CommonLog.LogType.ADVENTURE_EVENT;
            adventureLog16.title = currentValues.context.getString(C0380R.string.alog_title_quest_lord_defeat_1, monster2.name);
            adventureLog16.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_lord_defeat_1);
            linkedList13.add(Decision.Result.forAddLog(adventureLog16));
            return linkedList13;
        }
        if (currentValues.game.dungeonId != 20) {
            return null;
        }
        LinkedList linkedList14 = new LinkedList();
        if (currentValues.isOff(KEY_BOSS_ENCOUNTER)) {
            linkedList14.add(Decision.Result.forFlagOn(KEY_BOSS_ENCOUNTER));
            if (currentValues.isOff(KEY_EXTRA_DUNGEON_CONQURED)) {
                Item item11 = ItemRepository.getItem(currentValues.context, ItemDb.ITEM_MONSTERS_JEWEL);
                if (currentValues.isOn(KEY_EXTRA_DUNGEON_LEVEL)) {
                    int optionAsInt = currentValues.getFlag(KEY_EXTRA_DUNGEON_LEVEL).getOptionAsInt();
                    if (getExtraDungeonItemCount(currentValues.context, currentValues.game, true) < 3) {
                        Decision.Result resultForGetItem11 = Decision.Result.forGetItem(item11);
                        resultForGetItem11.log = new AdventureLog();
                        resultForGetItem11.log.type = CommonLog.LogType.TREASURE;
                        resultForGetItem11.log.title = currentValues.context.getString(C0380R.string.alog_title_get_item, item11.name);
                        resultForGetItem11.log.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_extra_find_jewels, item11.name);
                        linkedList14.add(resultForGetItem11);
                    }
                    linkedList14.add(Decision.Result.forFlagOn(GameFlag.Key.asQuest(QuestConst.FLAG_EXTRA_DUNGEON_CLEARED_WITH_ITEM_PREFIX + optionAsInt)));
                }
            }
            AdventureLog adventureLog17 = new AdventureLog();
            adventureLog17.type = CommonLog.LogType.ADVENTURE_EVENT;
            adventureLog17.title = currentValues.context.getString(C0380R.string.alog_title_quest_extra_clear_dungeon_1);
            adventureLog17.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_extra_clear_dungeon_1);
            linkedList14.add(Decision.Result.forAddLog(adventureLog17));
        }
        return linkedList14;
    }

    private static List<Decision.Result> processQuestClear(CurrentValues currentValues, QuestState questState) {
        LinkedList linkedList = new LinkedList();
        if (questState.cleared) {
            return linkedList;
        }
        linkedList.add(Decision.Result.forFlagOnIncrement(GameFlag.Key.asType(GameFlag.FlagType.QUEST_CLEAR, questState.symbol)));
        if (questState.started) {
            Quest quest = QuestRepository.getQuest(questState.symbol);
            AdventureLog adventureLog = new AdventureLog();
            adventureLog.type = CommonLog.LogType.QUEST_CLEAR;
            adventureLog.title = currentValues.context.getString(C0380R.string.flog_title_quest_clear);
            adventureLog.desc1 = currentValues.context.getString(C0380R.string.flog_desc_quest_clear, currentValues.context.getString(quest.nameStringId));
            linkedList.add(Decision.Result.forAddLog(adventureLog));
        }
        return linkedList;
    }

    private static List<Decision.Result> processStartFloor(CurrentValues currentValues) {
        if (currentValues.game.dungeonId == 20 && currentValues.floor == 1) {
            Item item = ItemRepository.getItem(currentValues.context, ItemDb.ITEM_MONSTERS_JEWEL);
            if (currentValues.isOn(GameFlag.Key.asHasItem(item))) {
                int extraDungeonItemCount = getExtraDungeonItemCount(currentValues.context, currentValues.game, false);
                if (extraDungeonItemCount <= 0 || currentValues.isOn(KEY_EXTRA_DUNGEON_LEVEL)) {
                    return null;
                }
                int i = extraDungeonItemCount <= 3 ? extraDungeonItemCount : 3;
                LinkedList linkedList = new LinkedList();
                AdventureLog adventureLog = new AdventureLog();
                adventureLog.type = CommonLog.LogType.ADVENTURE_EVENT;
                adventureLog.title = currentValues.context.getString(C0380R.string.alog_title_quest_extra_have_jewels, item.name);
                adventureLog.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_extra_have_jewels, item.name);
                linkedList.add(Decision.Result.forAddLog(adventureLog));
                for (int i2 = 0; i2 < i; i2++) {
                    linkedList.add(Decision.Result.forFlagOnIncrement(KEY_EXTRA_DUNGEON_LEVEL));
                }
                Dungeon dungeon = currentValues.game.adventureContext.dungeon;
                if (currentValues.game.dungeonContext.getStat(dungeon, dungeon.floor, dungeon.block).isConqured()) {
                    linkedList.add(Decision.Result.forFlagOn(KEY_EXTRA_DUNGEON_CONQURED));
                }
                return linkedList;
            }
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x010c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static java.util.List<com.shirobakama.autorpg2.entity.Decision.Result> processStartBlock(com.shirobakama.autorpg2.adventure.FlagEngine.CurrentValues r11) {
        /*
            Method dump skipped, instructions count: 528
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.adventure.FlagEngine.processStartBlock(com.shirobakama.autorpg2.adventure.FlagEngine$CurrentValues):java.util.List");
    }

    private static List<Decision.Result> processStartBlockAdditionalLog(CurrentValues currentValues) {
        if (currentValues.game.dungeonId == 2) {
            if (currentValues.floor != 2 || currentValues.block != currentValues.getDungeon().block) {
                return null;
            }
            GameFlag.Key keyAsQuest = GameFlag.Key.asQuest(QuestConst.CAVE_FLAG_USE_KEY);
            LinkedList linkedList = new LinkedList();
            AdventureLog adventureLog = new AdventureLog();
            if (currentValues.isOff(keyAsQuest)) {
                adventureLog.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_cave_goblins_key_locked);
            } else {
                adventureLog.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_cave_goblins_key_unlocked);
            }
            linkedList.add(Decision.Result.forAddLog(adventureLog));
            return linkedList;
        }
        if (currentValues.game.dungeonId == 5) {
            if (currentValues.floor != currentValues.getDungeon().floor || currentValues.block != currentValues.getDungeon().block) {
                return null;
            }
            GameFlag.Key keyAsQuest2 = GameFlag.Key.asQuest(QuestConst.LAKECAVE_FLAG_CLOSE_GATE);
            LinkedList linkedList2 = new LinkedList();
            AdventureLog adventureLog2 = new AdventureLog();
            if (currentValues.isOff(keyAsQuest2)) {
                adventureLog2.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_gate_opened);
            } else {
                adventureLog2.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_gate_closed);
            }
            linkedList2.add(Decision.Result.forAddLog(adventureLog2));
            return linkedList2;
        }
        if (currentValues.game.dungeonId != 11 || currentValues.floor != currentValues.getDungeon().floor || currentValues.block != currentValues.getDungeon().block) {
            return null;
        }
        GameFlag.Key keyAsQuest3 = GameFlag.Key.asQuest(QuestConst.SHIP_FLAG_REPOSED);
        LinkedList linkedList3 = new LinkedList();
        AdventureLog adventureLog3 = new AdventureLog();
        if (currentValues.isOff(keyAsQuest3)) {
            adventureLog3.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_ghost_not_reposed);
        } else {
            adventureLog3.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_ghost_reposed);
        }
        linkedList3.add(Decision.Result.forAddLog(adventureLog3));
        return linkedList3;
    }

    private static List<Decision.Result> processUseItemInBlock(CurrentValues currentValues) {
        Item item;
        Tactics tactics = currentValues.game.adventureContext.defaultTactics;
        if (tactics.useItemId == 0 || (item = ItemRepository.getItem(currentValues.context, tactics.useItemId)) == null) {
            return null;
        }
        PlayerChar playerChar = currentValues.game.getAliveChars().get(0);
        if (currentValues.game.dungeonId == 2) {
            if (item.f97id == 5010 && tactics.useItemFloor == 2 && tactics.useItemBlock == currentValues.getDungeon().block) {
                GameFlag.Key keyAsQuest = GameFlag.Key.asQuest(QuestConst.CAVE_FLAG_USE_KEY);
                if (currentValues.isOff(keyAsQuest)) {
                    LinkedList linkedList = new LinkedList();
                    linkedList.add(Decision.Result.forFlagOn(keyAsQuest));
                    AdventureLog adventureLog = new AdventureLog();
                    adventureLog.type = CommonLog.LogType.ADVENTURE_EVENT;
                    adventureLog.title = currentValues.context.getString(C0380R.string.alog_title_player_item, playerChar.name, item.name);
                    adventureLog.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_cave_goblins_unlock_key, item.name);
                    linkedList.add(Decision.Result.forAddLog(adventureLog));
                    return linkedList;
                }
            }
            return null;
        }
        if (currentValues.game.dungeonId == 5) {
            if (item.f97id == 5030 && tactics.useItemFloor == currentValues.getDungeon().floor && tactics.useItemBlock == currentValues.getDungeon().block) {
                GameFlag.Key keyAsQuest2 = GameFlag.Key.asQuest(QuestConst.LAKECAVE_FLAG_CLOSE_GATE);
                if (currentValues.isOff(keyAsQuest2)) {
                    LinkedList linkedList2 = new LinkedList();
                    linkedList2.add(Decision.Result.forFlagOn(keyAsQuest2));
                    AdventureLog adventureLog2 = new AdventureLog();
                    adventureLog2.type = CommonLog.LogType.ADVENTURE_EVENT;
                    adventureLog2.title = currentValues.context.getString(C0380R.string.alog_title_player_item, playerChar.name, item.name);
                    adventureLog2.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_gate_move_by_handle, playerChar.name, item.name);
                    linkedList2.add(Decision.Result.forAddLog(adventureLog2));
                    linkedList2.addAll(processQuestClear(currentValues, getQuestState(currentValues, QuestDb.QUEST_GATE_OF_LAKE)));
                    return linkedList2;
                }
            }
            return null;
        }
        if (currentValues.game.dungeonId == 11 && item.f97id == 5050 && tactics.useItemFloor == currentValues.getDungeon().floor && tactics.useItemBlock == currentValues.getDungeon().block) {
            GameFlag.Key keyAsQuest3 = GameFlag.Key.asQuest(QuestConst.SHIP_FLAG_REPOSED);
            if (currentValues.isOff(keyAsQuest3)) {
                LinkedList linkedList3 = new LinkedList();
                linkedList3.add(Decision.Result.forFlagOn(keyAsQuest3));
                AdventureLog adventureLog3 = new AdventureLog();
                adventureLog3.type = CommonLog.LogType.ADVENTURE_EVENT;
                adventureLog3.title = currentValues.context.getString(C0380R.string.alog_title_player_item, playerChar.name, item.name);
                adventureLog3.desc1 = currentValues.context.getString(C0380R.string.alog_desc_quest_ghost_use_amulet, item.name);
                linkedList3.add(Decision.Result.forAddLog(adventureLog3));
                linkedList3.addAll(processQuestClear(currentValues, getQuestState(currentValues, QuestDb.QUEST_SEA_GHOST)));
                return linkedList3;
            }
        }
        return null;
    }

    private static <LT extends CommonLog> LT handleGetLostItem(Context context, GameContext gameContext, Decision.Result result, LT lt) {
        int inventoryIndexForItemId;
        Inventory inventory;
        Item item = ItemRepository.getItem(context, result.f89id);
        if (result.log != null) {
            copyResultLogToLog(lt, result);
        }
        if (result.type == Decision.ResultType.GET_ITEM) {
            if (gameContext.adventureContext != null) {
                inventory = gameContext.adventureContext.createTemporaryInventory();
            } else {
                inventory = new Inventory();
            }
            inventory.itemId = result.f89id;
            gameContext.inventories.add(inventory);
            gameContext.getOrCreateFlag(GameFlag.Key.asItemGot(item)).addOptionAsInt(1);
            if (result.log == null) {
                lt.type = CommonLog.LogType.TREASURE;
                lt.title = context.getString(C0380R.string.alog_title_get_item, inventory.getName(context));
            }
        } else if (result.type == Decision.ResultType.LOST_ITEM && (inventoryIndexForItemId = gameContext.getInventoryIndexForItemId(result.f89id)) >= 0) {
            Inventory inventory2 = gameContext.inventories.get(inventoryIndexForItemId);
            gameContext.inventories.remove(inventoryIndexForItemId);
            if (result.log == null) {
                lt.type = CommonLog.LogType.UNDEFINED;
                lt.title = context.getString(C0380R.string.alog_title_lost_item, inventory2.getName(context));
            }
        }
        return lt;
    }

    private static void handleFlagResult(GameContext gameContext, Decision.Result result) {
        GameFlag.Key key = new GameFlag.Key(result.flagType, result.flagName);
        if (result.type == Decision.ResultType.FLAG_ON || result.type == Decision.ResultType.FLAG_ON_INCREMENT) {
            gameContext.getOrCreateFlag(key).value = true;
        }
        if (result.type == Decision.ResultType.FLAG_OFF) {
            GameFlag orCreateFlag = gameContext.getOrCreateFlag(key);
            orCreateFlag.value = false;
            orCreateFlag.setOptionAsInt(0);
        }
        if (result.type == Decision.ResultType.FLAG_INCREMENT || result.type == Decision.ResultType.FLAG_ON_INCREMENT) {
            gameContext.getOrCreateFlag(key).addOptionAsInt(1);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static EngineResult processInFighting(Context context, Random random, GameContext gameContext, Decision.Timing timing, List<FightingLog> list) {
        List<Decision.Result> listProcessFightingStart;
        CurrentValues currentValues = new CurrentValues(random, context, gameContext, gameContext.adventureContext.floor, gameContext.adventureContext.block);
        switch (timing) {
            case AVAILABLE_DUNGEONS:
            case AVAILABLE_TOWNS:
            case ENTER_TOWN:
            case HEARING:
            case START_ADVENTURE:
            case START_DUNGEON:
            case ADVENTURING:
            case CLEAR_DUNGEON:
            case CLEAR_FLOOR:
            case GET_ITEM:
            case START_FLOOR:
            case START_BLOCK:
            case START_BLOCK_ADDITIONAL_LOG:
            case USE_ITEM_IN_BLOCK:
            case END_DUNGEON:
            case LOSE_MONSTER:
            case WIN_MONSTER:
                throw new IllegalStateException(timing.toString());
            case FIGHTING_START_FIGHT:
                listProcessFightingStart = processFightingStart(currentValues);
                break;
            case FIGHTING_LOSE_MONSTER:
                listProcessFightingStart = processFightingLoseMonster(currentValues);
                break;
            case FIGHTING_WIN_MONSTER:
                listProcessFightingStart = processFightingWinMonster(currentValues);
                break;
            default:
                listProcessFightingStart = null;
                break;
        }
        if (listProcessFightingStart == null || listProcessFightingStart.isEmpty()) {
            return null;
        }
        EngineResult engineResult = new EngineResult();
        for (Decision.Result result : listProcessFightingStart) {
            switch (result.type) {
                case DUNGEON_AVAILABLE:
                case SHOW_MESSAGE:
                case TOWN_AVAILABLE:
                case END_ADVENTURE:
                case MOVE_TO_BLOCK:
                case MOVE_TO_FLOOR:
                case MOVE_TO_DUNGEON:
                case MOVE_TO_TOWN:
                case ENCOUNTER:
                case TRAP:
                case TREASURE_BOX:
                    throw new IllegalStateException(result.toString());
                case ADD_LOG:
                    list.add(copyResultLogToLog(new FightingLog(), result));
                    break;
                case ADD_ENCHANT:
                    switch (result.enchant.target) {
                        case FIELD:
                            gameContext.fightContext.addEnchantToField(result.enchant);
                            break;
                        case PLAYER_CHAR:
                            gameContext.fightContext.addEnchantToPlayer(result.enchantIndex, result.enchant);
                            break;
                        case ENEMY:
                            gameContext.fightContext.addEnchantToEnemy(result.enchantIndex, result.enchant);
                            break;
                        case ENEMY_PARTY:
                            gameContext.fightContext.addEnchantToEnemyParty(result.enchant);
                            break;
                        case PARTY:
                            gameContext.fightContext.addEnchantToParty(result.enchant);
                            break;
                    }
                    engineResult.needsRecalc = true;
                    break;
                case FLAG_ON:
                case FLAG_OFF:
                case FLAG_INCREMENT:
                case FLAG_ON_INCREMENT:
                    handleFlagResult(gameContext, result);
                    break;
                case GET_ITEM:
                    list.add(handleGetLostItem(context, gameContext, result, new FightingLog()));
                    break;
                case LOST_ITEM:
                    list.add(handleGetLostItem(context, gameContext, result, new FightingLog()));
                    engineResult.needsRecalc = true;
                    break;
            }
        }
        return engineResult;
    }

    private static List<Decision.Result> processFightingStart(CurrentValues currentValues) {
        int i;
        boolean z = true;
        if (currentValues.game.fightContext.isEvent) {
            Monster monster = currentValues.game.fightContext.monsters.get(0);
            if (currentValues.game.dungeonId == 16 && currentValues.isOn(KEY_BOSS_ENCOUNTER)) {
                LinkedList linkedList = new LinkedList();
                GameFlag flag = currentValues.getFlag(GameFlag.Key.asMonsterEnconter(monster));
                int i2 = (flag == null || flag.getOptionAsInt() <= 0) ? C0380R.string.flog_desc_quest_wizard_encounter_1 : C0380R.string.flog_desc_quest_wizard_encounter_2;
                FightingLog fightingLog = new FightingLog();
                fightingLog.type = CommonLog.LogType.FIGHT_EVENT;
                fightingLog.title = currentValues.context.getString(C0380R.string.flog_title_quest_wizard_encounter);
                fightingLog.desc1 = currentValues.context.getString(i2);
                linkedList.add(Decision.Result.forAddLog(fightingLog));
                return linkedList;
            }
            if (currentValues.game.dungeonId != 19 || !currentValues.isOn(KEY_BOSS_ENCOUNTER)) {
                return null;
            }
            if (monster.f105id == 1580 || monster.f105id == 1590) {
                LinkedList linkedList2 = new LinkedList();
                FightingLog fightingLog2 = new FightingLog();
                fightingLog2.type = CommonLog.LogType.FIGHT_EVENT;
                fightingLog2.title = currentValues.context.getString(C0380R.string.flog_title_quest_lord_ghost_encounter, monster.name);
                fightingLog2.desc1 = currentValues.context.getString(C0380R.string.flog_desc_quest_lord_ghost_encounter);
                linkedList2.add(Decision.Result.forAddLog(fightingLog2));
                return linkedList2;
            }
            LinkedList linkedList3 = new LinkedList();
            GameFlag flag2 = currentValues.getFlag(GameFlag.Key.asMonsterEnconter(MonsterRepository.getMonster(currentValues.context, 1570)));
            GameFlag flag3 = currentValues.getFlag(GameFlag.Key.asMonsterEnconter(MonsterRepository.getMonster(currentValues.context, 1560)));
            boolean z2 = flag2 == null || flag2.getOptionAsInt() <= 0;
            boolean z3 = flag3 == null || flag3.getOptionAsInt() <= 0;
            if (z2 && z3) {
                FightingLog fightingLog3 = new FightingLog();
                fightingLog3.type = CommonLog.LogType.FIGHT_EVENT;
                fightingLog3.title = currentValues.context.getString(C0380R.string.flog_title_quest_lord_encounter_1);
                fightingLog3.desc1 = currentValues.context.getString(C0380R.string.flog_desc_quest_lord_encounter_1);
                linkedList3.add(Decision.Result.forAddLog(fightingLog3));
                boolean zIsOn = currentValues.isOn(GameFlag.Key.asHasItem(ItemRepository.getItem(currentValues.context, ItemDb.ITEM_DEMON_AMULET)));
                FightingLog fightingLog4 = new FightingLog();
                fightingLog4.type = CommonLog.LogType.FIGHT_EVENT;
                fightingLog4.title = currentValues.context.getString(C0380R.string.flog_title_quest_lord_encounter_2, monster.name);
                fightingLog4.desc1 = currentValues.context.getString(zIsOn ? C0380R.string.flog_desc_quest_lord_encounter_2_2 : C0380R.string.flog_desc_quest_lord_encounter_2_1);
                linkedList3.add(Decision.Result.forAddLog(fightingLog4));
            } else {
                int i3 = z3 ? C0380R.string.flog_desc_quest_lord_encounter_3_2 : C0380R.string.flog_desc_quest_lord_encounter_3_1;
                FightingLog fightingLog5 = new FightingLog();
                fightingLog5.type = CommonLog.LogType.FIGHT_EVENT;
                fightingLog5.title = currentValues.context.getString(C0380R.string.flog_title_quest_lord_encounter_3, monster.name);
                fightingLog5.desc1 = currentValues.context.getString(i3);
                linkedList3.add(Decision.Result.forAddLog(fightingLog5));
            }
            return linkedList3;
        }
        if (currentValues.game.dungeonId == 8) {
            if (currentValues.game.fightContext.isWandering || currentValues.block != currentValues.getDungeon().block || currentValues.game.adventureContext.dungeonStat.initialMonsterNumber != currentValues.game.adventureContext.dungeonStat.monsterNumber) {
                return null;
            }
            LinkedList linkedList4 = new LinkedList();
            FightingLog fightingLog6 = new FightingLog();
            fightingLog6.type = CommonLog.LogType.FIGHT_EVENT;
            fightingLog6.title = currentValues.context.getString(C0380R.string.flog_title_quest_proof_floor_guardian);
            fightingLog6.desc1 = currentValues.context.getString(currentValues.random.nextBoolean() ? C0380R.string.flog_desc_quest_proof_floor_guardian_1 : C0380R.string.flog_desc_quest_proof_floor_guardian_2);
            linkedList4.add(Decision.Result.forAddLog(fightingLog6));
            return linkedList4;
        }
        if (currentValues.game.dungeonId == 16) {
            if (currentValues.game.fightContext.isWandering || currentValues.game.fightContext.monsters.get(0).f105id != 1520 || currentValues.game.adventureContext.dungeonStat.initialMonsterNumber != currentValues.game.adventureContext.dungeonStat.monsterNumber) {
                return null;
            }
            boolean z4 = false;
            for (PlayerChar playerChar : currentValues.game.characters) {
                z4 = playerChar.isAlive() && playerChar.getSubLevel(GameChar.SubClass.SORCERER) > 0;
                if (z4) {
                    break;
                }
            }
            LinkedList linkedList5 = new LinkedList();
            FightingLog fightingLog7 = new FightingLog();
            fightingLog7.type = CommonLog.LogType.FIGHT_EVENT;
            fightingLog7.title = currentValues.context.getString(C0380R.string.flog_title_quest_wizard_guardian);
            fightingLog7.desc1 = currentValues.context.getString(z4 ? C0380R.string.flog_desc_quest_wizard_guardian_mag : C0380R.string.flog_desc_quest_wizard_guardian_no_mag);
            linkedList5.add(Decision.Result.forAddLog(fightingLog7));
            return linkedList5;
        }
        if (currentValues.game.dungeonId == 19) {
            Monster monster2 = currentValues.game.fightContext.monsters.get(0);
            if (currentValues.game.fightContext.isWandering || monster2.f105id != 1530 || currentValues.game.adventureContext.dungeonStat.initialMonsterNumber != currentValues.game.adventureContext.dungeonStat.monsterNumber) {
                return null;
            }
            GameFlag flag4 = currentValues.getFlag(GameFlag.Key.asMonsterEnconter(monster2));
            if (flag4 != null && flag4.getOptionAsInt() > 0) {
                z = false;
            }
            LinkedList linkedList6 = new LinkedList();
            FightingLog fightingLog8 = new FightingLog();
            fightingLog8.type = CommonLog.LogType.FIGHT_EVENT;
            fightingLog8.title = currentValues.context.getString(C0380R.string.flog_title_quest_lord_guardian);
            fightingLog8.desc1 = currentValues.context.getString(z ? C0380R.string.flog_desc_quest_lord_guardian_1 : C0380R.string.flog_desc_quest_lord_guardian_2);
            linkedList6.add(Decision.Result.forAddLog(fightingLog8));
            Decision.Result result = new Decision.Result();
            result.type = Decision.ResultType.ADD_ENCHANT;
            result.enchant = new Enchant(Enchant.Target.ENEMY, GameChar.Status.ANTI_MAGIC_BONUS, 12, 0, -1);
            result.enchantIndex = 0;
            linkedList6.add(result);
            return linkedList6;
        }
        if (currentValues.game.dungeonId != 20 || (i = currentValues.game.adventureContext.extraLevel) <= 0) {
            return null;
        }
        for (Enemy enemy : currentValues.game.fightContext.enemies) {
            enemy.level += enemy.level / 5;
            if (i == 2) {
                enemy.level += enemy.level / 5;
            } else if (i == 3) {
                enemy.level += 15;
                if (enemy.level > 100) {
                    enemy.level = 100;
                }
            }
        }
        int[] iArr = currentValues.game.fightContext.monsters.get(0).skillIds;
        if (i < 2 || iArr == null) {
            return null;
        }
        int[] iArr2 = new int[iArr.length];
        for (int i4 = 0; i4 < iArr.length; i4++) {
            Skill upperSkill = SkillRepository.getUpperSkill(currentValues.context, iArr[i4]);
            if (upperSkill != null) {
                iArr2[i4] = upperSkill.f107id;
            } else {
                iArr2[i4] = iArr[i4];
            }
        }
        for (Enemy enemy2 : currentValues.game.fightContext.enemies) {
            enemy2.setAvailableSkillIds(new ArrayList<>(iArr2.length));
            for (int i5 : iArr2) {
                enemy2.addAvailableSkillId(i5);
            }
        }
        return null;
    }

    private static <LT extends CommonLog> LT copyResultLogToLog(LT lt, Decision.Result result) {
        lt.type = result.log.type;
        lt.title = result.log.title;
        lt.desc1 = result.log.desc1;
        lt.desc2 = result.log.desc2;
        return lt;
    }

    public static void processExtraDungeonInEnterFloor(Context context, Random random, GameContext gameContext) {
        GameFlag flag;
        AdventureContext adventureContext = gameContext.adventureContext;
        if (adventureContext.dungeon.f90id == 20 && adventureContext.floor == 1 && (flag = new CurrentValues(random, context, gameContext, adventureContext.floor, adventureContext.block).getFlag(KEY_EXTRA_DUNGEON_LEVEL)) != null) {
            int optionAsInt = flag.getOptionAsInt();
            adventureContext.dungeon.difficulty += optionAsInt * 4;
            Dungeon dungeon = adventureContext.dungeon;
            double d = adventureContext.dungeon.treasureTrapFactor;
            double d2 = optionAsInt;
            Double.isNaN(d2);
            Double.isNaN(d);
            dungeon.treasureTrapFactor = (int) (d * ((d2 * 0.2d) + 1.0d));
            adventureContext.extraLevel = optionAsInt;
        }
    }

    public static Dungeon enhanceExtraDungeonInInitialize(Context context, GameContext gameContext, Dungeon dungeon) {
        int extraDungeonItemCount;
        if (dungeon.f90id != 20 || (extraDungeonItemCount = getExtraDungeonItemCount(context, gameContext, false)) == 0) {
            return dungeon;
        }
        Dungeon dungeonCopy = dungeon.copy();
        dungeonCopy.difficulty += extraDungeonItemCount * 4;
        double d = dungeonCopy.trapFactor;
        double d2 = extraDungeonItemCount;
        Double.isNaN(d2);
        Double.isNaN(d);
        dungeonCopy.trapFactor = (int) (d * (d2 + 1.0d));
        double d3 = dungeonCopy.treasureFactor;
        Double.isNaN(d2);
        Double.isNaN(d3);
        dungeonCopy.treasureFactor = (int) (d3 * ((d2 * 0.5d) + 1.0d));
        return dungeonCopy;
    }

    private static int getExtraDungeonItemCount(Context context, GameContext gameContext, boolean z) {
        Item item = ItemRepository.getItem(context, ItemDb.ITEM_MONSTERS_JEWEL);
        Iterator<Inventory> it = gameContext.inventories.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (it.next().itemId == item.f97id) {
                i++;
            }
        }
        if (z) {
            for (Stock stock : gameContext.stocks) {
                if (stock.itemId == item.f97id) {
                    i += stock.countNum;
                }
            }
        }
        return i;
    }

    public static int enhanceItemDrop(int i, int i2) {
        if (i2 <= 0) {
            return i;
        }
        double d = i;
        double d2 = i2;
        Double.isNaN(d2);
        Double.isNaN(d);
        return (int) (d * ((d2 * 0.3d) + 1.0d));
    }

    public static double enhanceItemDropFactor(int i) {
        if (i <= 0) {
            return 0.0d;
        }
        double d = i;
        Double.isNaN(d);
        return d * 0.25d;
    }
}
