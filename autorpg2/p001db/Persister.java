package com.shirobakama.autorpg2.p001db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.util.SparseArray;
import com.google.android.gms.measurement.api.AppMeasurementSdk;
import com.shirobakama.autorpg2.TacticsMakingActivity;
import com.shirobakama.autorpg2.entity.AdvancedTactics;
import com.shirobakama.autorpg2.entity.AdventureLog;
import com.shirobakama.autorpg2.entity.CommonLog;
import com.shirobakama.autorpg2.entity.DungeonContext;
import com.shirobakama.autorpg2.entity.DungeonEvent;
import com.shirobakama.autorpg2.entity.DungeonStat;
import com.shirobakama.autorpg2.entity.FightingLog;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.GameFlag;
import com.shirobakama.autorpg2.entity.Inventory;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.autorpg2.entity.LogEnemy;
import com.shirobakama.autorpg2.entity.LogFight;
import com.shirobakama.autorpg2.entity.LogManagement;
import com.shirobakama.autorpg2.entity.LogStatus;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.entity.Skill;
import com.shirobakama.autorpg2.entity.SkillCustomization;
import com.shirobakama.autorpg2.entity.Stock;
import com.shirobakama.autorpg2.entity.Tactics;
import com.shirobakama.autorpg2.p001db.BackupRestoreUtil;
import com.shirobakama.autorpg2.repo.ItemRepository;
import com.shirobakama.autorpg2.repo.MonsterRepository;
import com.shirobakama.autorpg2.util.TypeUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class Persister {
    public static final int ID_NOTHING = 0;
    protected static final String TAG = "persister";
    private SimpleRpgOpenHelper mHelper;
    private static final String[] PLAYER_CHAR_COLUMNS = {"_id", AppMeasurementSdk.ConditionalUserProperty.NAME, "level", "race", "clazz", "exp", "statusBonus", "baseStr", "baseInt", "baseAgi", "baseVit", "presetBitmapId", "weaponId", "armorId", "shieldId", "ringId", "hp", "mp"};
    private static final String[] PLAYER_CHAR_BITMAP_COLUMNS = {"_id", "bitmap"};
    private static final String[] ACTIVE_CHAR_COLUMNS = {"_id", "charId", "charOrder"};
    private static final String[] CHAR_SKILL_COLUMNS = {"_id", "charId", "skillId"};
    private static final String[] SKILL_SLOT_COLUMNS = {"_id", "charId", "skillId"};
    private static final String[] GAME_CONTEXT_COLUMNS = {"_id", "gold", "townId", "dungeonId", "targetFloor", "startTime", "estimateTime", "returnTime", "advCount", "startRealtime", "returnRealtime"};
    private static final String[] DUNGEON_STAT_COLUMNS = {"_id", "dungeonId", TacticsMakingActivity.DIALOG_ARGS_FLOOR, TacticsMakingActivity.DIALOG_ARGS_BLOCK, "blockType", "blockState", "monsterId1", "monsterId2", "monsterId3", "monsterNumber", "initialMonsterNumber", "captiveRate"};
    private static final String[] DUNGEON_EVENT_COLUMNS = {"_id", "dungeonId", TacticsMakingActivity.DIALOG_ARGS_FLOOR, TacticsMakingActivity.DIALOG_ARGS_BLOCK, "position", "type"};
    private static final String[] GAME_FLAG_COLUMNS = {"_id", "type", AppMeasurementSdk.ConditionalUserProperty.NAME, AppMeasurementSdk.ConditionalUserProperty.VALUE, "option"};
    private static final String[] INVENTORY_COLUMNS = {"_id", "itemId", AppMeasurementSdk.ConditionalUserProperty.NAME, "enchants"};
    private static final String[] STOCK_COLUMNS = {"_id", "itemId", AppMeasurementSdk.ConditionalUserProperty.NAME, "enchants", "countNum"};
    static final String[] LOG_MAGEMENT_COLUMNS = {"_id", "pcId1", "pcId2", "pcId3", "pcName1", "pcName2", "pcName3", "dungeonId", "targetFloor", "completed"};
    static final String[] ADVENTURE_LOG_COLUMNS = {"_id", "lmId", "logTime", "type", "itemId", "charId", "title", "desc1", "desc2", "gold"};
    static final String[] LOG_FIGHT_COLUMNS = {"_id", "adventureLogId", "wandering", NotificationCompat.CATEGORY_EVENT, "monsterId1", "monsterId2", "monsterId3"};
    static final String[] FIGHTING_LOG_COLUMNS = {"_id", "type", "itemId", "charId", "title", "desc1", "desc2", "adventureLogId", "playersAct", "toPlayer", "enemyIndex", "targetIds"};
    static final String[] LOG_STATUS_COLUMNS = {"_id", "lmId", "logTime", "action", TacticsMakingActivity.DIALOG_ARGS_FLOOR, TacticsMakingActivity.DIALOG_ARGS_BLOCK, "captiveRate"};
    static final String[] LOG_CHAR_COLUMNS = {"_id", "adventureLogId", "fightingLogId", "charId", "hp", "maxHp", "mp", "maxMp", "exp", "level"};
    static final String[] LOG_ENEMY_CHAR_COLUMNS = {"_id", "fightingLogId", "enemyIndex", "hp", "mp"};
    static final String[] LOG_ITEM_COLUMNS = {"_id", "adventureLogId", "itemId", "equippedCharId"};
    private static final String[] TACTICS_COLUMNS = {"_id", "charId", "enabled", "targetFloor", "abort", "running", "attackSkill", "statusSkill", "cureSkill", "damageSkill", "item", "rest", "fullInventory", "useItemFloor", "useItemBlock", "useItemId"};
    private static final String[] ADVANCED_TACTICS_COLUMNS = {"_id", "charId", "fighting", "condition", "conditionSub", "target", "action", "actionSub", "targetId", "targetCharId", "conditionValue", "conditionCharId", "conditionNot"};
    private static final String[] SKILL_COLUMN = {"skillId"};
    private static final String[] SKILL_CUSTOMIZATION_COLUMNS = {"_id", "charId", "skillId", "skillName", "skillDesc"};
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    public void forwardLogs(Context context, GameContext gameContext, LogManagement logManagement, int i) {
    }

    public static final class PlayerUpdateFailedException extends RuntimeException {
        public PlayerUpdateFailedException(String str) {
            super(str);
        }
    }

    public Persister(Context context) {
        this.mHelper = SimpleRpgOpenHelper.getInstance(context);
    }

    public GameContext readGameContext() {
        SQLiteDatabase readableDatabase = this.mHelper.getReadableDatabase();
        GameContext gameContextOnly = readGameContextOnly(readableDatabase, SimpleRpgOpenHelper.TABLE_GAME_CONTEXT);
        if (gameContextOnly != null) {
            gameContextOnly.flags = readGameFlags(readableDatabase, false);
            gameContextOnly.characters = readActivePlayerChars(readableDatabase);
            gameContextOnly.inventories = readInventories(readableDatabase, SimpleRpgOpenHelper.TABLE_INVENTORY);
            gameContextOnly.stocks = readStocks(readableDatabase);
            gameContextOnly.dungeonContext = new DungeonContext();
            gameContextOnly.dungeonContext.stats = readDungeonStats(readableDatabase);
            gameContextOnly.dungeonContext.events = readDungeonEvents(readableDatabase);
        }
        readableDatabase.close();
        return gameContextOnly;
    }

    private GameContext readGameContextOnly(SQLiteDatabase sQLiteDatabase, String str) {
        GameContext gameContext;
        Cursor cursorQuery = sQLiteDatabase.query(str, GAME_CONTEXT_COLUMNS, null, null, null, null, null);
        if (cursorQuery.moveToFirst()) {
            gameContext = new GameContext();
            gameContext.f95id = cursorQuery.getInt(cursorQuery.getColumnIndex("_id"));
            gameContext.gold = cursorQuery.getInt(cursorQuery.getColumnIndex("gold"));
            gameContext.townId = cursorQuery.getInt(cursorQuery.getColumnIndex("townId"));
            gameContext.dungeonId = cursorQuery.getInt(cursorQuery.getColumnIndex("dungeonId"));
            gameContext.targetFloor = cursorQuery.getInt(cursorQuery.getColumnIndex("targetFloor"));
            gameContext.startTime = new Date(cursorQuery.getLong(cursorQuery.getColumnIndex("startTime")));
            gameContext.estimateTime = new Date(cursorQuery.getLong(cursorQuery.getColumnIndex("estimateTime")));
            gameContext.returnTime = new Date(cursorQuery.getLong(cursorQuery.getColumnIndex("returnTime")));
            gameContext.advCount = cursorQuery.getInt(cursorQuery.getColumnIndex("advCount"));
            gameContext.startRealtime = cursorQuery.getLong(cursorQuery.getColumnIndex("startRealtime"));
            gameContext.returnRealtime = cursorQuery.getLong(cursorQuery.getColumnIndex("returnRealtime"));
        } else {
            gameContext = null;
        }
        cursorQuery.close();
        return gameContext;
    }

    private List<Inventory> readInventories(SQLiteDatabase sQLiteDatabase, String str) {
        ArrayList arrayList = new ArrayList();
        Cursor cursorQuery = sQLiteDatabase.query(str, INVENTORY_COLUMNS, null, null, null, null, null);
        while (cursorQuery.moveToNext()) {
            Inventory inventory = new Inventory();
            inventory.f98id = cursorQuery.getInt(cursorQuery.getColumnIndex("_id"));
            inventory.itemId = cursorQuery.getInt(cursorQuery.getColumnIndex("itemId"));
            inventory.name = cursorQuery.getString(cursorQuery.getColumnIndex(AppMeasurementSdk.ConditionalUserProperty.NAME));
            inventory.setEnchantsFromPersister(cursorQuery.getString(cursorQuery.getColumnIndex("enchants")));
            arrayList.add(inventory);
        }
        cursorQuery.close();
        return arrayList;
    }

    private List<Stock> readStocks(SQLiteDatabase sQLiteDatabase) {
        ArrayList arrayList = new ArrayList();
        Cursor cursorQuery = sQLiteDatabase.query(SimpleRpgOpenHelper.TABLE_STOCK, STOCK_COLUMNS, null, null, null, null, null);
        while (cursorQuery.moveToNext()) {
            Stock stock = new Stock();
            stock.f98id = cursorQuery.getInt(cursorQuery.getColumnIndex("_id"));
            stock.itemId = cursorQuery.getInt(cursorQuery.getColumnIndex("itemId"));
            stock.name = cursorQuery.getString(cursorQuery.getColumnIndex(AppMeasurementSdk.ConditionalUserProperty.NAME));
            stock.setEnchantsFromPersister(cursorQuery.getString(cursorQuery.getColumnIndex("enchants")));
            stock.countNum = cursorQuery.getInt(cursorQuery.getColumnIndex("countNum"));
            arrayList.add(stock);
        }
        cursorQuery.close();
        return arrayList;
    }

    private List<DungeonStat> readDungeonStats(SQLiteDatabase sQLiteDatabase) {
        ArrayList arrayList = new ArrayList();
        DungeonStat.BlockType[] blockTypeArrValues = DungeonStat.BlockType.values();
        DungeonStat.BlockState[] blockStateArrValues = DungeonStat.BlockState.values();
        Cursor cursorQuery = sQLiteDatabase.query(SimpleRpgOpenHelper.TABLE_DUNGEON_STAT, DUNGEON_STAT_COLUMNS, null, null, null, null, null);
        while (cursorQuery.moveToNext()) {
            DungeonStat dungeonStat = new DungeonStat();
            dungeonStat.f92id = cursorQuery.getInt(cursorQuery.getColumnIndex("_id"));
            dungeonStat.dungeonId = cursorQuery.getInt(cursorQuery.getColumnIndex("dungeonId"));
            dungeonStat.floor = cursorQuery.getInt(cursorQuery.getColumnIndex(TacticsMakingActivity.DIALOG_ARGS_FLOOR));
            dungeonStat.block = cursorQuery.getInt(cursorQuery.getColumnIndex(TacticsMakingActivity.DIALOG_ARGS_BLOCK));
            dungeonStat.blockType = blockTypeArrValues[cursorQuery.getInt(cursorQuery.getColumnIndex("blockType"))];
            dungeonStat.blockState = blockStateArrValues[cursorQuery.getInt(cursorQuery.getColumnIndex("blockState"))];
            dungeonStat.monsterId = cursorQuery.getInt(cursorQuery.getColumnIndex("monsterId1"));
            dungeonStat.monsterId2 = cursorQuery.getInt(cursorQuery.getColumnIndex("monsterId2"));
            dungeonStat.monsterId3 = cursorQuery.getInt(cursorQuery.getColumnIndex("monsterId3"));
            dungeonStat.monsterNumber = cursorQuery.getInt(cursorQuery.getColumnIndex("monsterNumber"));
            dungeonStat.initialMonsterNumber = cursorQuery.getInt(cursorQuery.getColumnIndex("initialMonsterNumber"));
            dungeonStat.captiveRate = cursorQuery.getInt(cursorQuery.getColumnIndex("captiveRate"));
            arrayList.add(dungeonStat);
        }
        cursorQuery.close();
        return arrayList;
    }

    private List<DungeonEvent> readDungeonEvents(SQLiteDatabase sQLiteDatabase) {
        ArrayList arrayList = new ArrayList();
        DungeonEvent.EventType[] eventTypeArrValues = DungeonEvent.EventType.values();
        Cursor cursorQuery = sQLiteDatabase.query(SimpleRpgOpenHelper.TABLE_DUNGEON_EVENT, DUNGEON_EVENT_COLUMNS, null, null, null, null, null);
        while (cursorQuery.moveToNext()) {
            DungeonEvent dungeonEvent = new DungeonEvent();
            dungeonEvent.f91id = cursorQuery.getInt(cursorQuery.getColumnIndex("_id"));
            dungeonEvent.dungeonId = cursorQuery.getInt(cursorQuery.getColumnIndex("dungeonId"));
            dungeonEvent.floor = cursorQuery.getInt(cursorQuery.getColumnIndex(TacticsMakingActivity.DIALOG_ARGS_FLOOR));
            dungeonEvent.block = cursorQuery.getInt(cursorQuery.getColumnIndex(TacticsMakingActivity.DIALOG_ARGS_BLOCK));
            dungeonEvent.position = cursorQuery.getInt(cursorQuery.getColumnIndex("position"));
            dungeonEvent.type = eventTypeArrValues[cursorQuery.getInt(cursorQuery.getColumnIndex("type"))];
            arrayList.add(dungeonEvent);
        }
        cursorQuery.close();
        return arrayList;
    }

    private Map<GameFlag.Key, GameFlag> readGameFlags(SQLiteDatabase sQLiteDatabase, boolean z) {
        String str = z ? SimpleRpgOpenHelper.TABLE_GAME_FLAG_RESULT : SimpleRpgOpenHelper.TABLE_GAME_FLAG;
        HashMap map = new HashMap();
        Cursor cursorQuery = sQLiteDatabase.query(str, GAME_FLAG_COLUMNS, null, null, null, null, null);
        while (cursorQuery.moveToNext()) {
            GameFlag gameFlag = new GameFlag();
            gameFlag.f96id = cursorQuery.getInt(cursorQuery.getColumnIndex("_id"));
            gameFlag.type = GameFlag.FlagType.byName(cursorQuery.getString(cursorQuery.getColumnIndex("type")));
            gameFlag.name = cursorQuery.getString(cursorQuery.getColumnIndex(AppMeasurementSdk.ConditionalUserProperty.NAME));
            gameFlag.value = cursorQuery.getInt(cursorQuery.getColumnIndex(AppMeasurementSdk.ConditionalUserProperty.VALUE)) != 0;
            gameFlag.option = cursorQuery.getString(cursorQuery.getColumnIndex("option"));
            map.put(gameFlag.key(), gameFlag);
        }
        cursorQuery.close();
        return map;
    }

    private List<Integer> readSkills(SQLiteDatabase sQLiteDatabase, String str, int i) {
        ArrayList arrayList = new ArrayList();
        Cursor cursorQuery = sQLiteDatabase.query(str, SKILL_COLUMN, "charId = ?", new String[]{Integer.toString(i)}, null, null, "skillId asc");
        while (cursorQuery.moveToNext()) {
            arrayList.add(Integer.valueOf(cursorQuery.getInt(0)));
        }
        cursorQuery.close();
        return arrayList;
    }

    private List<Integer> readSkillSlots(SQLiteDatabase sQLiteDatabase, int i) {
        ArrayList arrayList = new ArrayList(3);
        Cursor cursorQuery = sQLiteDatabase.query(SimpleRpgOpenHelper.TABLE_SKILL_SLOT, SKILL_COLUMN, "charId = ?", new String[]{Integer.toString(i)}, null, null, "skillId asc");
        while (cursorQuery.moveToNext()) {
            arrayList.add(Integer.valueOf(cursorQuery.getInt(0)));
        }
        cursorQuery.close();
        return arrayList;
    }

    public void deletePlayerChar(PlayerChar playerChar) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            String[] strArr = {Integer.toString(playerChar.f106id)};
            if (writableDatabase.delete(SimpleRpgOpenHelper.TABLE_PLAYER_CHAR, "_id = ?", strArr) != 1) {
                throw new IllegalStateException("Delete failed:" + playerChar.f106id);
            }
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_ACTIVE_CHAR, "charId = ?", strArr);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_CHAR_SKILL, "charId = ?", strArr);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_SKILL_SLOT, "charId = ?", strArr);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_SKILL_CUSTOMIZATION, "charId = ?", strArr);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_TACTICS, "charId = ?", strArr);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_ADVANCED_TACTICS, "charId = ?", strArr);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    public void writeActiveCharacters(Collection<PlayerChar> collection) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_ACTIVE_CHAR, null, null);
            ContentValues contentValues = new ContentValues();
            int i = 0;
            Iterator<PlayerChar> it = collection.iterator();
            while (it.hasNext()) {
                contentValues.put("charId", Integer.valueOf(it.next().f106id));
                contentValues.put("charOrder", Integer.valueOf(i));
                writableDatabase.insertOrThrow(SimpleRpgOpenHelper.TABLE_ACTIVE_CHAR, null, contentValues);
                i++;
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    public List<PlayerChar> readAllPlayerChars() {
        SQLiteDatabase readableDatabase = this.mHelper.getReadableDatabase();
        List<PlayerChar> allPlayerChars = readAllPlayerChars(readableDatabase, SimpleRpgOpenHelper.TABLE_PLAYER_CHAR);
        readableDatabase.close();
        return allPlayerChars;
    }

    private List<PlayerChar> readActivePlayerChars(SQLiteDatabase sQLiteDatabase) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("SELECT ");
        for (int i = 0; i < PLAYER_CHAR_COLUMNS.length; i++) {
            if (i > 0) {
                stringBuffer.append(',');
            }
            stringBuffer.append(SimpleRpgOpenHelper.TABLE_PLAYER_CHAR);
            stringBuffer.append('.');
            stringBuffer.append(PLAYER_CHAR_COLUMNS[i]);
        }
        stringBuffer.append(" FROM player_char,active_char");
        stringBuffer.append(" WHERE player_char._Id = active_char.charId");
        stringBuffer.append(" ORDER BY active_char.charOrder asc");
        return cursorToPlayerChars(sQLiteDatabase, sQLiteDatabase.rawQuery(stringBuffer.toString(), null));
    }

    private List<PlayerChar> readAllPlayerChars(SQLiteDatabase sQLiteDatabase, String str) {
        return cursorToPlayerChars(sQLiteDatabase, sQLiteDatabase.query(str, PLAYER_CHAR_COLUMNS, null, null, null, null, null));
    }

    private List<PlayerChar> cursorToPlayerChars(SQLiteDatabase sQLiteDatabase, Cursor cursor) {
        ArrayList<PlayerChar> arrayList = new ArrayList();
        while (cursor.moveToNext()) {
            PlayerChar playerChar = new PlayerChar();
            playerChar.f106id = cursor.getInt(cursor.getColumnIndex("_id"));
            playerChar.name = cursor.getString(cursor.getColumnIndex(AppMeasurementSdk.ConditionalUserProperty.NAME));
            playerChar.level = cursor.getInt(cursor.getColumnIndex("level"));
            playerChar.race = PlayerChar.Race.values()[cursor.getInt(cursor.getColumnIndex("race"))];
            playerChar.clazz = GameChar.CharClass.values()[cursor.getInt(cursor.getColumnIndex("clazz"))];
            playerChar.exp = cursor.getInt(cursor.getColumnIndex("exp"));
            playerChar.statusBonus = cursor.getInt(cursor.getColumnIndex("statusBonus"));
            playerChar.baseStr = cursor.getInt(cursor.getColumnIndex("baseStr"));
            playerChar.baseInt = cursor.getInt(cursor.getColumnIndex("baseInt"));
            playerChar.baseAgi = cursor.getInt(cursor.getColumnIndex("baseAgi"));
            playerChar.baseVit = cursor.getInt(cursor.getColumnIndex("baseVit"));
            playerChar.presetBitmapId = cursor.getInt(cursor.getColumnIndex("presetBitmapId"));
            playerChar.weaponId = cursor.getInt(cursor.getColumnIndex("weaponId"));
            playerChar.armorId = cursor.getInt(cursor.getColumnIndex("armorId"));
            playerChar.shieldId = cursor.getInt(cursor.getColumnIndex("shieldId"));
            playerChar.ringId = cursor.getInt(cursor.getColumnIndex("ringId"));
            playerChar.f93hp = cursor.getInt(cursor.getColumnIndex("hp"));
            playerChar.f94mp = cursor.getInt(cursor.getColumnIndex("mp"));
            arrayList.add(playerChar);
        }
        cursor.close();
        for (PlayerChar playerChar2 : arrayList) {
            Iterator<Integer> it = readSkills(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_CHAR_SKILL, playerChar2.f106id).iterator();
            while (it.hasNext()) {
                playerChar2.addSkillId(it.next().intValue());
            }
            Iterator<Integer> it2 = readSkillSlots(sQLiteDatabase, playerChar2.f106id).iterator();
            while (it2.hasNext()) {
                playerChar2.addAvailableSkillId(it2.next().intValue());
            }
        }
        return arrayList;
    }

    public byte[] readCharacterBitmap(int i) {
        SQLiteDatabase readableDatabase = this.mHelper.getReadableDatabase();
        Cursor cursorQuery = readableDatabase.query(SimpleRpgOpenHelper.TABLE_PLAYER_CHAR_BITMAP, PLAYER_CHAR_BITMAP_COLUMNS, "_id = ?", new String[]{Integer.toString(i)}, null, null, null);
        byte[] blob = cursorQuery.moveToNext() ? cursorQuery.getBlob(cursorQuery.getColumnIndex("bitmap")) : null;
        cursorQuery.close();
        readableDatabase.close();
        return blob;
    }

    public void writeOnAdventureCompleted(GameContext gameContext, LogManagement logManagement) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writeGameContextTable(writableDatabase, false, gameContext);
            if (logManagement != null) {
                writeLogManagementInTx(writableDatabase, logManagement);
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    public void writeGameDungeonContext(GameContext gameContext) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writeGameContextTable(writableDatabase, false, gameContext);
            writeDungeonContext(writableDatabase, false, gameContext.dungeonContext);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    public void writeFlags(Collection<GameFlag> collection) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            Iterator<GameFlag> it = collection.iterator();
            while (it.hasNext()) {
                writeFlag(writableDatabase, false, it.next());
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    private void writeGameContextTable(SQLiteDatabase sQLiteDatabase, boolean z, GameContext gameContext) throws SQLException {
        String str = z ? SimpleRpgOpenHelper.TABLE_GAME_CONTEXT_RESULT : SimpleRpgOpenHelper.TABLE_GAME_CONTEXT;
        ContentValues contentValues = new ContentValues();
        contentValues.put("gold", Integer.valueOf(gameContext.gold));
        contentValues.put("townId", Integer.valueOf(gameContext.townId));
        contentValues.put("targetFloor", Integer.valueOf(gameContext.targetFloor));
        contentValues.put("dungeonId", Integer.valueOf(gameContext.dungeonId));
        contentValues.put("startTime", Long.valueOf(gameContext.startTime.getTime()));
        contentValues.put("estimateTime", Long.valueOf(gameContext.estimateTime.getTime()));
        contentValues.put("returnTime", Long.valueOf(gameContext.returnTime.getTime()));
        contentValues.put("advCount", Integer.valueOf(gameContext.advCount));
        contentValues.put("startRealtime", Long.valueOf(gameContext.startRealtime));
        contentValues.put("returnRealtime", Long.valueOf(gameContext.returnRealtime));
        if (z) {
            contentValues.put("_id", Integer.valueOf(gameContext.f95id));
            sQLiteDatabase.insertOrThrow(str, null, contentValues);
            return;
        }
        if (gameContext.f95id == 0) {
            long jInsertOrThrow = sQLiteDatabase.insertOrThrow(str, null, contentValues);
            if (jInsertOrThrow >= 0) {
                gameContext.f95id = (int) jInsertOrThrow;
                return;
            }
            return;
        }
        if (sQLiteDatabase.update(str, contentValues, "_id = ?", new String[]{Integer.toString(gameContext.f95id)}) == 1) {
            return;
        }
        throw new IllegalStateException("Update failed:" + gameContext.f95id);
    }

    public void writePlayer(PlayerChar playerChar) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writePlayerTable(writableDatabase, false, playerChar);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    private void writePlayerTable(SQLiteDatabase sQLiteDatabase, boolean z, PlayerChar playerChar) throws SQLException {
        String str = z ? SimpleRpgOpenHelper.TABLE_PLAYER_CHAR_RESULT : SimpleRpgOpenHelper.TABLE_PLAYER_CHAR;
        String str2 = z ? SimpleRpgOpenHelper.TABLE_CHAR_SKILL_RESULT : SimpleRpgOpenHelper.TABLE_CHAR_SKILL;
        ContentValues contentValues = new ContentValues();
        contentValues.put(AppMeasurementSdk.ConditionalUserProperty.NAME, playerChar.name);
        contentValues.put("level", Integer.valueOf(playerChar.level));
        contentValues.put("race", Integer.valueOf(playerChar.race.ordinal()));
        contentValues.put("clazz", Integer.valueOf(playerChar.clazz.ordinal()));
        contentValues.put("exp", Integer.valueOf(playerChar.exp));
        contentValues.put("statusBonus", Integer.valueOf(playerChar.statusBonus));
        contentValues.put("baseStr", Integer.valueOf(playerChar.baseStr));
        contentValues.put("baseInt", Integer.valueOf(playerChar.baseInt));
        contentValues.put("baseAgi", Integer.valueOf(playerChar.baseAgi));
        contentValues.put("baseVit", Integer.valueOf(playerChar.baseVit));
        contentValues.put("presetBitmapId", Integer.valueOf(playerChar.presetBitmapId));
        contentValues.put("weaponId", Integer.valueOf(playerChar.weaponId));
        contentValues.put("armorId", Integer.valueOf(playerChar.armorId));
        contentValues.put("shieldId", Integer.valueOf(playerChar.shieldId));
        contentValues.put("ringId", Integer.valueOf(playerChar.ringId));
        contentValues.put("hp", Integer.valueOf(playerChar.f93hp));
        contentValues.put("mp", Integer.valueOf(playerChar.f94mp));
        if (z) {
            contentValues.put("_id", Integer.valueOf(playerChar.f106id));
            sQLiteDatabase.insertOrThrow(str, null, contentValues);
        } else if (playerChar.f106id == 0) {
            long jInsertOrThrow = sQLiteDatabase.insertOrThrow(str, null, contentValues);
            if (jInsertOrThrow >= 0) {
                playerChar.f106id = (int) jInsertOrThrow;
            }
        } else if (sQLiteDatabase.update(str, contentValues, "_id = ?", new String[]{Integer.toString(playerChar.f106id)}) != 1) {
            throw new PlayerUpdateFailedException("Update failed:" + playerChar.f106id);
        }
        List<Integer> skills = readSkills(sQLiteDatabase, str2, playerChar.f106id);
        ArrayList arrayList = new ArrayList(skills);
        ArrayList arrayList2 = new ArrayList(playerChar.getSkillIds());
        arrayList.removeAll(playerChar.getSkillIds());
        arrayList2.removeAll(skills);
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            deleteSkill(sQLiteDatabase, str2, playerChar.f106id, ((Integer) it.next()).intValue());
        }
        Iterator it2 = arrayList2.iterator();
        while (it2.hasNext()) {
            writeSkill(sQLiteDatabase, str2, playerChar.f106id, ((Integer) it2.next()).intValue());
        }
        writeSkillSlots(sQLiteDatabase, playerChar.f106id, playerChar.getAvailableSkillIds());
    }

    private void deleteSkill(SQLiteDatabase sQLiteDatabase, String str, int i, int i2) {
        if (sQLiteDatabase.delete(str, "charId = ? AND skillId = ?", new String[]{Integer.toString(i), Integer.toString(i2)}) == 1) {
            return;
        }
        throw new IllegalStateException("Delete failed:" + i + "," + i2);
    }

    private void writeSkill(SQLiteDatabase sQLiteDatabase, String str, int i, int i2) throws SQLException {
        ContentValues contentValues = new ContentValues();
        contentValues.put("charId", Integer.valueOf(i));
        contentValues.put("skillId", Integer.valueOf(i2));
        sQLiteDatabase.insertOrThrow(str, null, contentValues);
    }

    private void writeSkillSlots(SQLiteDatabase sQLiteDatabase, int i, List<Integer> list) throws SQLException {
        sQLiteDatabase.delete(SimpleRpgOpenHelper.TABLE_SKILL_SLOT, "charId = ?", new String[]{Integer.toString(i)});
        ContentValues contentValues = new ContentValues();
        contentValues.put("charId", Integer.valueOf(i));
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            contentValues.put("skillId", it.next());
            sQLiteDatabase.insertOrThrow(SimpleRpgOpenHelper.TABLE_SKILL_SLOT, null, contentValues);
        }
    }

    public void writeCharacterBitmap(int i, byte[] bArr) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_PLAYER_CHAR_BITMAP, "_id = ?", new String[]{Integer.toString(i)});
            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", Integer.valueOf(i));
            contentValues.put("bitmap", bArr);
            writableDatabase.insertOrThrow(SimpleRpgOpenHelper.TABLE_PLAYER_CHAR_BITMAP, null, contentValues);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    public void writeInventory(Inventory inventory) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writeInventoryTable(writableDatabase, false, inventory);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    private void writeInventoryTable(SQLiteDatabase sQLiteDatabase, boolean z, Inventory inventory) throws SQLException {
        String str = z ? SimpleRpgOpenHelper.TABLE_INVENTORY_RESULT : SimpleRpgOpenHelper.TABLE_INVENTORY;
        ContentValues contentValues = new ContentValues();
        contentValues.put("itemId", Integer.valueOf(inventory.itemId));
        contentValues.put(AppMeasurementSdk.ConditionalUserProperty.NAME, inventory.name);
        contentValues.put("enchants", inventory.getEnchantsForPersister());
        if (z) {
            contentValues.put("_id", Integer.valueOf(inventory.f98id));
            sQLiteDatabase.insertOrThrow(str, null, contentValues);
            return;
        }
        if (inventory.f98id == 0) {
            long jInsertOrThrow = sQLiteDatabase.insertOrThrow(str, null, contentValues);
            if (jInsertOrThrow >= 0) {
                inventory.f98id = (int) jInsertOrThrow;
                return;
            }
            return;
        }
        if (sQLiteDatabase.update(str, contentValues, "_id = ?", new String[]{Integer.toString(inventory.f98id)}) == 1) {
            return;
        }
        throw new IllegalStateException("Update failed:" + inventory.f98id);
    }

    private void writeFlag(SQLiteDatabase sQLiteDatabase, boolean z, GameFlag gameFlag) throws SQLException {
        if (gameFlag.type == GameFlag.FlagType.ADVENTURING) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", gameFlag.type.name());
        contentValues.put(AppMeasurementSdk.ConditionalUserProperty.NAME, gameFlag.name);
        contentValues.put(AppMeasurementSdk.ConditionalUserProperty.VALUE, Integer.valueOf(gameFlag.value ? 1 : 0));
        contentValues.put("option", gameFlag.option);
        if (z) {
            contentValues.put("_id", Integer.valueOf(gameFlag.f96id));
            sQLiteDatabase.insertOrThrow(SimpleRpgOpenHelper.TABLE_GAME_FLAG_RESULT, null, contentValues);
            return;
        }
        if (gameFlag.f96id == 0) {
            long jInsertOrThrow = sQLiteDatabase.insertOrThrow(SimpleRpgOpenHelper.TABLE_GAME_FLAG, null, contentValues);
            if (jInsertOrThrow >= 0) {
                gameFlag.f96id = (int) jInsertOrThrow;
                return;
            }
            return;
        }
        if (sQLiteDatabase.update(SimpleRpgOpenHelper.TABLE_GAME_FLAG, contentValues, "_id = ?", new String[]{Integer.toString(gameFlag.f96id)}) == 1) {
            return;
        }
        throw new IllegalStateException("Update failed:" + gameFlag.f96id);
    }

    private void deleteInventory(SQLiteDatabase sQLiteDatabase, Inventory inventory) {
        if (sQLiteDatabase.delete(SimpleRpgOpenHelper.TABLE_INVENTORY, "_id = ?", new String[]{Integer.toString(inventory.f98id)}) == 1) {
            return;
        }
        throw new IllegalStateException("Delete failed:" + inventory.f98id);
    }

    private void deleteStock(SQLiteDatabase sQLiteDatabase, Stock stock) {
        if (sQLiteDatabase.delete(SimpleRpgOpenHelper.TABLE_STOCK, "_id = ?", new String[]{Integer.toString(stock.f98id)}) == 1) {
            return;
        }
        throw new IllegalStateException("Delete failed:" + stock.f98id);
    }

    private void writeStock(SQLiteDatabase sQLiteDatabase, Stock stock) throws SQLException {
        ContentValues contentValues = new ContentValues();
        contentValues.put("itemId", Integer.valueOf(stock.itemId));
        contentValues.put(AppMeasurementSdk.ConditionalUserProperty.NAME, stock.name);
        contentValues.put("enchants", stock.getEnchantsForPersister());
        contentValues.put("countNum", Integer.valueOf(stock.countNum));
        if (stock.f98id == 0) {
            long jInsertOrThrow = sQLiteDatabase.insertOrThrow(SimpleRpgOpenHelper.TABLE_STOCK, null, contentValues);
            if (jInsertOrThrow >= 0) {
                stock.f98id = (int) jInsertOrThrow;
                return;
            }
            return;
        }
        if (sQLiteDatabase.update(SimpleRpgOpenHelper.TABLE_STOCK, contentValues, "_id = ?", new String[]{Integer.toString(stock.f98id)}) == 1) {
            return;
        }
        throw new IllegalStateException("Update failed:" + stock.f98id);
    }

    public void writeInventoryAdding(Inventory inventory, GameContext gameContext) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        if (inventory != null) {
            try {
                writeInventoryTable(writableDatabase, false, inventory);
            } catch (Throwable th) {
                writableDatabase.endTransaction();
                writableDatabase.close();
                throw th;
            }
        }
        if (gameContext != null) {
            writeGameContextTable(writableDatabase, false, gameContext);
        }
        writableDatabase.setTransactionSuccessful();
        writableDatabase.endTransaction();
        writableDatabase.close();
    }

    public void writeStockRemoving(Stock stock, GameContext gameContext) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            if (stock.countNum <= 0) {
                deleteStock(writableDatabase, stock);
            } else {
                writeStock(writableDatabase, stock);
            }
            writeGameContextTable(writableDatabase, false, gameContext);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    public void writeInventoryRemoving(Inventory inventory, GameContext gameContext) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            deleteInventory(writableDatabase, inventory);
            writeGameContextTable(writableDatabase, false, gameContext);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    public void writeInventoryAndStock(Inventory inventory, boolean z, Stock stock) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            if (z) {
                deleteInventory(writableDatabase, inventory);
            } else {
                writeInventoryTable(writableDatabase, false, inventory);
            }
            if (stock.countNum == 0) {
                deleteStock(writableDatabase, stock);
            } else {
                writeStock(writableDatabase, stock);
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    public void writeEquipmentChanging(PlayerChar playerChar) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("weaponId", Integer.valueOf(playerChar.weaponId));
            contentValues.put("armorId", Integer.valueOf(playerChar.armorId));
            contentValues.put("shieldId", Integer.valueOf(playerChar.shieldId));
            contentValues.put("ringId", Integer.valueOf(playerChar.ringId));
            if (writableDatabase.update(SimpleRpgOpenHelper.TABLE_PLAYER_CHAR, contentValues, "_id = ?", new String[]{Integer.toString(playerChar.f106id)}) != 1) {
                throw new IllegalStateException("Update failed:" + playerChar.f106id);
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    public int deleteDungeonContext() {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            int iDelete = writableDatabase.delete(SimpleRpgOpenHelper.TABLE_DUNGEON_STAT, null, null) + writableDatabase.delete(SimpleRpgOpenHelper.TABLE_DUNGEON_EVENT, null, null);
            writableDatabase.setTransactionSuccessful();
            return iDelete;
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    private int writeDungeonContext(SQLiteDatabase sQLiteDatabase, boolean z, DungeonContext dungeonContext) throws SQLException {
        int iDelete;
        String str = z ? SimpleRpgOpenHelper.TABLE_DUNGEON_STAT_RESULT : SimpleRpgOpenHelper.TABLE_DUNGEON_STAT;
        if (dungeonContext.stats == null || dungeonContext.stats.isEmpty()) {
            iDelete = sQLiteDatabase.delete(str, null, null);
        } else {
            iDelete = 0;
            for (DungeonStat dungeonStat : dungeonContext.stats) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("dungeonId", Integer.valueOf(dungeonStat.dungeonId));
                contentValues.put(TacticsMakingActivity.DIALOG_ARGS_FLOOR, Integer.valueOf(dungeonStat.floor));
                contentValues.put(TacticsMakingActivity.DIALOG_ARGS_BLOCK, Integer.valueOf(dungeonStat.block));
                contentValues.put("blockType", Integer.valueOf(dungeonStat.blockType.ordinal()));
                contentValues.put("blockState", Integer.valueOf(dungeonStat.blockState.ordinal()));
                contentValues.put("monsterId1", Integer.valueOf(dungeonStat.monsterId));
                contentValues.put("monsterId2", Integer.valueOf(dungeonStat.monsterId2));
                contentValues.put("monsterId3", Integer.valueOf(dungeonStat.monsterId3));
                contentValues.put("monsterNumber", Integer.valueOf(dungeonStat.monsterNumber));
                contentValues.put("initialMonsterNumber", Integer.valueOf(dungeonStat.initialMonsterNumber));
                contentValues.put("captiveRate", Integer.valueOf(dungeonStat.captiveRate));
                if (z) {
                    contentValues.put("_id", Integer.valueOf(dungeonStat.f92id));
                    sQLiteDatabase.insertOrThrow(str, null, contentValues);
                } else if (dungeonStat.f92id == 0) {
                    long jInsertOrThrow = sQLiteDatabase.insertOrThrow(str, null, contentValues);
                    if (jInsertOrThrow >= 0) {
                        dungeonStat.f92id = (int) jInsertOrThrow;
                    }
                    iDelete++;
                } else {
                    if (sQLiteDatabase.update(str, contentValues, "_id = ?", new String[]{Integer.toString(dungeonStat.f92id)}) != 1) {
                        throw new IllegalStateException("Update failed:" + dungeonStat.f92id);
                    }
                    iDelete++;
                }
            }
        }
        if (dungeonContext.events == null || dungeonContext.events.isEmpty()) {
            return iDelete + sQLiteDatabase.delete(SimpleRpgOpenHelper.TABLE_DUNGEON_EVENT, null, null);
        }
        for (DungeonEvent dungeonEvent : dungeonContext.events) {
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put("dungeonId", Integer.valueOf(dungeonEvent.dungeonId));
            contentValues2.put(TacticsMakingActivity.DIALOG_ARGS_FLOOR, Integer.valueOf(dungeonEvent.floor));
            contentValues2.put(TacticsMakingActivity.DIALOG_ARGS_BLOCK, Integer.valueOf(dungeonEvent.block));
            contentValues2.put("position", Integer.valueOf(dungeonEvent.position));
            contentValues2.put("type", Integer.valueOf(dungeonEvent.type.ordinal()));
            if (dungeonEvent.f91id == 0) {
                long jInsertOrThrow2 = sQLiteDatabase.insertOrThrow(SimpleRpgOpenHelper.TABLE_DUNGEON_EVENT, null, contentValues2);
                if (jInsertOrThrow2 >= 0) {
                    dungeonEvent.f91id = (int) jInsertOrThrow2;
                }
                iDelete++;
            } else {
                if (sQLiteDatabase.update(SimpleRpgOpenHelper.TABLE_DUNGEON_EVENT, contentValues2, "_id = ?", new String[]{Integer.toString(dungeonEvent.f91id)}) != 1) {
                    throw new IllegalStateException("Update failed:" + dungeonEvent.f91id);
                }
                iDelete++;
            }
        }
        return iDelete;
    }

    public void writeLogManagement(LogManagement logManagement) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writeLogManagementInTx(writableDatabase, logManagement);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    private void writeLogManagementInTx(SQLiteDatabase sQLiteDatabase, LogManagement logManagement) throws SQLException {
        ContentValues contentValues = new ContentValues();
        contentValues.put("pcId1", Integer.valueOf(logManagement.pcId[0]));
        contentValues.put("pcId2", Integer.valueOf(logManagement.pcId[1]));
        contentValues.put("pcId3", Integer.valueOf(logManagement.pcId[2]));
        contentValues.put("pcName1", logManagement.pcName[0]);
        contentValues.put("pcName2", logManagement.pcName[1]);
        contentValues.put("pcName3", logManagement.pcName[2]);
        contentValues.put("dungeonId", Integer.valueOf(logManagement.dungeonId));
        contentValues.put("targetFloor", Integer.valueOf(logManagement.targetFloor));
        contentValues.put("completed", logManagement.completed ? 1 : 0);
        if (logManagement.f103id == 0) {
            long jInsertOrThrow = sQLiteDatabase.insertOrThrow(SimpleRpgOpenHelper.TABLE_LOG_MANAGEMENT, null, contentValues);
            if (jInsertOrThrow >= 0) {
                logManagement.f103id = (int) jInsertOrThrow;
                return;
            }
            return;
        }
        int iUpdate = sQLiteDatabase.update(SimpleRpgOpenHelper.TABLE_LOG_MANAGEMENT, contentValues, "_id = ?", new String[]{Integer.toString(logManagement.f103id)});
        if (iUpdate == 1) {
            return;
        }
        throw new IllegalStateException("Update failed:" + logManagement.f103id + ",rows:" + iUpdate);
    }

    public LogManagement readOldestLogManagement() {
        List<LogManagement> logManagements = readLogManagements(true, 1);
        if (logManagements.isEmpty()) {
            return null;
        }
        return logManagements.get(0);
    }

    public LogManagement readLatestLogManagement() {
        List<LogManagement> logManagements = readLogManagements(false, 1);
        if (logManagements.isEmpty()) {
            return null;
        }
        return logManagements.get(0);
    }

    public List<LogManagement> readLogManagements() {
        return readLogManagements(true, -1);
    }

    private List<LogManagement> readLogManagements(boolean z, int i) {
        SQLiteDatabase readableDatabase = this.mHelper.getReadableDatabase();
        Cursor cursorQuery = readableDatabase.query(SimpleRpgOpenHelper.TABLE_LOG_MANAGEMENT, LOG_MAGEMENT_COLUMNS, null, null, null, null, z ? "_id asc" : "_id desc", i <= 0 ? null : Integer.toString(i));
        ArrayList arrayList = new ArrayList();
        while (cursorQuery.moveToNext()) {
            arrayList.add(readLogManagementFromCursor(cursorQuery));
        }
        cursorQuery.close();
        readableDatabase.close();
        return arrayList;
    }

    public LogManagement readLogManagement(int i) {
        SQLiteDatabase readableDatabase = this.mHelper.getReadableDatabase();
        Cursor cursorQuery = readableDatabase.query(SimpleRpgOpenHelper.TABLE_LOG_MANAGEMENT, LOG_MAGEMENT_COLUMNS, "_id = ?", new String[]{Integer.toString(i)}, null, null, null);
        LogManagement logManagementFromCursor = cursorQuery.moveToNext() ? readLogManagementFromCursor(cursorQuery) : null;
        cursorQuery.close();
        readableDatabase.close();
        return logManagementFromCursor;
    }

    private LogManagement readLogManagementFromCursor(Cursor cursor) {
        LogManagement logManagement = new LogManagement();
        logManagement.f103id = cursor.getInt(cursor.getColumnIndex("_id"));
        logManagement.pcId[0] = cursor.getInt(cursor.getColumnIndex("pcId1"));
        logManagement.pcId[1] = cursor.getInt(cursor.getColumnIndex("pcId2"));
        logManagement.pcId[2] = cursor.getInt(cursor.getColumnIndex("pcId3"));
        logManagement.pcName[0] = cursor.getString(cursor.getColumnIndex("pcName1"));
        logManagement.pcName[1] = cursor.getString(cursor.getColumnIndex("pcName2"));
        logManagement.pcName[2] = cursor.getString(cursor.getColumnIndex("pcName3"));
        logManagement.dungeonId = cursor.getInt(cursor.getColumnIndex("dungeonId"));
        logManagement.targetFloor = cursor.getInt(cursor.getColumnIndex("targetFloor"));
        logManagement.completed = cursor.getInt(cursor.getColumnIndex("completed")) != 0;
        return logManagement;
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    public void writeAdventureLog(LogManagement logManagement, AdventureLog adventureLog) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("lmId", Integer.valueOf(logManagement.f103id));
            contentValues.put("logTime", Integer.valueOf(adventureLog.logTime));
            contentValues.put("type", Integer.valueOf(adventureLog.type.ordinal()));
            contentValues.put("itemId", adventureLog.item == null ? 0 : Integer.valueOf(adventureLog.item.f97id));
            contentValues.put("charId", adventureLog.playerChar == null ? 0 : Integer.valueOf(adventureLog.playerChar.f106id));
            contentValues.put("title", adventureLog.title);
            contentValues.put("desc1", adventureLog.desc1);
            contentValues.put("desc2", adventureLog.desc2);
            contentValues.put("gold", Integer.valueOf(adventureLog.gold));
            if (adventureLog.f85id == 0) {
                long jInsertOrThrow = writableDatabase.insertOrThrow(SimpleRpgOpenHelper.TABLE_ADVENTURE_LOG, null, contentValues);
                if (jInsertOrThrow >= 0) {
                    adventureLog.f85id = (int) jInsertOrThrow;
                }
            } else if (writableDatabase.update(SimpleRpgOpenHelper.TABLE_ADVENTURE_LOG, contentValues, "_id = ?", new String[]{Integer.toString(adventureLog.f85id)}) != 1) {
                throw new IllegalStateException("Update log failed:" + adventureLog.f85id);
            }
            if (adventureLog.logChars != null && !adventureLog.logChars.isEmpty()) {
                writeLogChars(writableDatabase, adventureLog.f85id, 0, adventureLog.logChars);
            }
            if (adventureLog.logInventories != null && !adventureLog.logInventories.isEmpty()) {
                for (AdventureLog.LogInventory logInventory : adventureLog.logInventories) {
                    logInventory.adventureLogId = adventureLog.f85id;
                    ContentValues contentValues2 = new ContentValues();
                    contentValues2.put("adventureLogId", Integer.valueOf(logInventory.adventureLogId));
                    contentValues2.put("itemId", Integer.valueOf(logInventory.itemId));
                    contentValues2.put("equippedCharId", Integer.valueOf(logInventory.equippedCharId));
                    if (logInventory.f84id == 0) {
                        long jInsertOrThrow2 = writableDatabase.insertOrThrow(SimpleRpgOpenHelper.TABLE_LOG_ITEM, null, contentValues2);
                        if (jInsertOrThrow2 >= 0) {
                            logInventory.f84id = (int) jInsertOrThrow2;
                        }
                    } else if (writableDatabase.update(SimpleRpgOpenHelper.TABLE_LOG_ITEM, contentValues2, "_id = ?", new String[]{Integer.toString(logInventory.f84id)}) != 1) {
                        throw new IllegalStateException("Update inv failed:" + logInventory.f84id);
                    }
                }
            }
            if (adventureLog.logFight != null) {
                writeLogFight(writableDatabase, adventureLog.f85id, adventureLog.logFight);
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    public void writeLogStatus(LogManagement logManagement, LogStatus logStatus) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("lmId", Integer.valueOf(logManagement.f103id));
            contentValues.put("logTime", Integer.valueOf(logStatus.logTime));
            contentValues.put("action", Integer.valueOf(logStatus.action.ordinal()));
            contentValues.put(TacticsMakingActivity.DIALOG_ARGS_FLOOR, Integer.valueOf(logStatus.floor));
            contentValues.put(TacticsMakingActivity.DIALOG_ARGS_BLOCK, Integer.valueOf(logStatus.block));
            contentValues.put("captiveRate", Integer.valueOf(logStatus.captiveRate));
            if (logStatus.f104id == 0) {
                long jInsertOrThrow = writableDatabase.insertOrThrow(SimpleRpgOpenHelper.TABLE_LOG_STATUS, null, contentValues);
                if (jInsertOrThrow >= 0) {
                    logStatus.f104id = (int) jInsertOrThrow;
                }
            } else if (writableDatabase.update(SimpleRpgOpenHelper.TABLE_LOG_STATUS, contentValues, "_id = ?", new String[]{Integer.toString(logStatus.f104id)}) != 1) {
                throw new IllegalStateException("Update failed:" + logStatus.f104id);
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    private void writeLogChars(SQLiteDatabase sQLiteDatabase, int i, int i2, List<CommonLog.LogChar> list) throws SQLException {
        for (CommonLog.LogChar logChar : list) {
            logChar.adventureLogId = i;
            logChar.fightingLogId = i2;
            ContentValues contentValues = new ContentValues();
            contentValues.put("adventureLogId", Integer.valueOf(logChar.adventureLogId));
            contentValues.put("fightingLogId", Integer.valueOf(logChar.fightingLogId));
            contentValues.put("charId", Integer.valueOf(logChar.charId));
            contentValues.put("exp", Integer.valueOf(logChar.exp));
            contentValues.put("level", Integer.valueOf(logChar.level));
            contentValues.put("hp", Integer.valueOf(logChar.f86hp));
            contentValues.put("maxHp", Integer.valueOf(logChar.maxHp));
            contentValues.put("mp", Integer.valueOf(logChar.f88mp));
            contentValues.put("maxMp", Integer.valueOf(logChar.maxMp));
            if (logChar.f87id == 0) {
                long jInsertOrThrow = sQLiteDatabase.insertOrThrow(SimpleRpgOpenHelper.TABLE_LOG_CHAR, null, contentValues);
                if (jInsertOrThrow >= 0) {
                    logChar.f87id = (int) jInsertOrThrow;
                }
            } else if (sQLiteDatabase.update(SimpleRpgOpenHelper.TABLE_LOG_CHAR, contentValues, "_id = ?", new String[]{Integer.toString(logChar.f87id)}) != 1) {
                throw new IllegalStateException("Update failed:" + logChar.f87id);
            }
        }
    }

    private void writeLogFight(SQLiteDatabase sQLiteDatabase, int i, LogFight logFight) throws SQLException {
        logFight.adventureLogId = i;
        ContentValues contentValues = new ContentValues();
        contentValues.put("adventureLogId", Integer.valueOf(logFight.adventureLogId));
        contentValues.put("wandering", Integer.valueOf(logFight.isWandering ? 1 : 0));
        contentValues.put(NotificationCompat.CATEGORY_EVENT, Integer.valueOf(logFight.isEvent ? 1 : 0));
        contentValues.put("monsterId1", logFight.monster == null ? 0 : Integer.valueOf(logFight.monster.f105id));
        contentValues.put("monsterId2", logFight.monster2 == null ? 0 : Integer.valueOf(logFight.monster2.f105id));
        contentValues.put("monsterId3", logFight.monster3 == null ? 0 : Integer.valueOf(logFight.monster3.f105id));
        if (logFight.f102id == 0) {
            long jInsertOrThrow = sQLiteDatabase.insertOrThrow(SimpleRpgOpenHelper.TABLE_LOG_FIGHT, null, contentValues);
            if (jInsertOrThrow >= 0) {
                logFight.f102id = (int) jInsertOrThrow;
            }
        } else if (sQLiteDatabase.update(SimpleRpgOpenHelper.TABLE_LOG_FIGHT, contentValues, "_id = ?", new String[]{Integer.toString(logFight.f102id)}) != 1) {
            throw new IllegalStateException("Update failed:" + logFight.f102id);
        }
        writeFightingLogs(sQLiteDatabase, i, logFight.fightingLogs);
    }

    private void writeFightingLogs(SQLiteDatabase sQLiteDatabase, int i, List<FightingLog> list) throws SQLException {
        for (FightingLog fightingLog : list) {
            fightingLog.adventureLogId = i;
            ContentValues contentValues = new ContentValues();
            contentValues.put("type", Integer.valueOf(fightingLog.type.ordinal()));
            contentValues.put("itemId", fightingLog.item == null ? 0 : Integer.valueOf(fightingLog.item.f97id));
            contentValues.put("charId", fightingLog.playerChar == null ? 0 : Integer.valueOf(fightingLog.playerChar.f106id));
            contentValues.put("title", fightingLog.title);
            contentValues.put("desc1", fightingLog.desc1);
            contentValues.put("desc2", fightingLog.desc2);
            contentValues.put("adventureLogId", Integer.valueOf(fightingLog.adventureLogId));
            contentValues.put("playersAct", Integer.valueOf(fightingLog.playersAct ? 1 : 0));
            contentValues.put("toPlayer", Integer.valueOf(fightingLog.toPlayer ? 1 : 0));
            contentValues.put("enemyIndex", Integer.valueOf(fightingLog.enemyIndex));
            contentValues.put("targetIds", intArraysToString(fightingLog.targetIds));
            if (fightingLog.f85id == 0) {
                long jInsertOrThrow = sQLiteDatabase.insertOrThrow(SimpleRpgOpenHelper.TABLE_FIGHTING_LOG, null, contentValues);
                if (jInsertOrThrow >= 0) {
                    fightingLog.f85id = (int) jInsertOrThrow;
                }
            } else if (sQLiteDatabase.update(SimpleRpgOpenHelper.TABLE_FIGHTING_LOG, contentValues, "_id = ?", new String[]{Integer.toString(fightingLog.f85id)}) != 1) {
                throw new IllegalStateException("Update failed:" + fightingLog.f85id);
            }
            if (fightingLog.logChars != null && !fightingLog.logChars.isEmpty()) {
                writeLogChars(sQLiteDatabase, fightingLog.adventureLogId, fightingLog.f85id, fightingLog.logChars);
            }
            if (fightingLog.logEnemies != null && !fightingLog.logEnemies.isEmpty()) {
                writeLogEnemies(sQLiteDatabase, fightingLog.f85id, fightingLog.logEnemies);
            }
        }
    }

    private String intArraysToString(int[] iArr) {
        if (iArr == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i : iArr) {
            sb.append(i);
            sb.append(',');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    private int[] stringToIntArray(String str) {
        if (str == null) {
            return null;
        }
        String[] strArrSplit = COMMA_PATTERN.split(str);
        int[] iArr = new int[strArrSplit.length];
        for (int i = 0; i < strArrSplit.length; i++) {
            iArr[i] = Integer.parseInt(strArrSplit[i]);
        }
        return iArr;
    }

    private void writeLogEnemies(SQLiteDatabase sQLiteDatabase, int i, List<LogEnemy> list) throws SQLException {
        for (LogEnemy logEnemy : list) {
            logEnemy.fightingLogId = i;
            ContentValues contentValues = new ContentValues();
            contentValues.put("fightingLogId", Integer.valueOf(logEnemy.fightingLogId));
            contentValues.put("enemyIndex", Integer.valueOf(logEnemy.enemyIndex));
            contentValues.put("hp", Integer.valueOf(logEnemy.f99hp));
            contentValues.put("mp", Integer.valueOf(logEnemy.f101mp));
            if (logEnemy.f100id == 0) {
                long jInsertOrThrow = sQLiteDatabase.insertOrThrow(SimpleRpgOpenHelper.TABLE_LOG_ENEMY_CHAR, null, contentValues);
                if (jInsertOrThrow >= 0) {
                    logEnemy.f100id = (int) jInsertOrThrow;
                }
            } else if (sQLiteDatabase.update(SimpleRpgOpenHelper.TABLE_LOG_ENEMY_CHAR, contentValues, "_id = ?", new String[]{Integer.toString(logEnemy.f100id)}) != 1) {
                throw new IllegalStateException("Update failed:" + logEnemy.f100id);
            }
        }
    }

    public List<AdventureLog> readAdventureLog(Context context, LogManagement logManagement, Date date, Date date2, SparseArray<PlayerChar> sparseArray) {
        String[] strArr;
        int iCalcLogTime = CommonLog.calcLogTime(date2.getTime());
        int iCalcLogTime2 = date == null ? 0 : CommonLog.calcLogTime(date.getTime());
        if (iCalcLogTime2 == iCalcLogTime) {
            return new ArrayList(0);
        }
        SQLiteDatabase readableDatabase = this.mHelper.getReadableDatabase();
        CommonLog.LogType[] logTypeArrValues = CommonLog.LogType.values();
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("lmId = ? AND ");
        sb.append(date == null ? "logTime <= ?" : "logTime > ? AND logTime <= ?");
        String string = sb.toString();
        if (date == null) {
            strArr = new String[]{Integer.toString(logManagement.f103id), Integer.toString(iCalcLogTime)};
        } else {
            strArr = new String[]{Integer.toString(logManagement.f103id), Integer.toString(iCalcLogTime2), Integer.toString(iCalcLogTime)};
        }
        Cursor cursorQuery = readableDatabase.query(SimpleRpgOpenHelper.TABLE_ADVENTURE_LOG, ADVENTURE_LOG_COLUMNS, string, strArr, null, null, "_id asc");
        while (cursorQuery.moveToNext()) {
            AdventureLog adventureLog = new AdventureLog();
            adventureLog.f85id = cursorQuery.getInt(cursorQuery.getColumnIndex("_id"));
            adventureLog.logTime = cursorQuery.getInt(cursorQuery.getColumnIndex("logTime"));
            adventureLog.type = logTypeArrValues[cursorQuery.getInt(cursorQuery.getColumnIndex("type"))];
            int i = cursorQuery.getInt(cursorQuery.getColumnIndex("itemId"));
            int i2 = cursorQuery.getInt(cursorQuery.getColumnIndex("charId"));
            adventureLog.title = cursorQuery.getString(cursorQuery.getColumnIndex("title"));
            adventureLog.desc1 = cursorQuery.getString(cursorQuery.getColumnIndex("desc1"));
            adventureLog.desc2 = cursorQuery.getString(cursorQuery.getColumnIndex("desc2"));
            adventureLog.gold = cursorQuery.getInt(cursorQuery.getColumnIndex("gold"));
            Item item = null;
            adventureLog.playerChar = i2 == 0 ? null : sparseArray.get(i2);
            if (i != 0) {
                item = ItemRepository.getItem(context, i);
            }
            adventureLog.item = item;
            arrayList.add(adventureLog);
        }
        cursorQuery.close();
        readAdventureLogChars(readableDatabase, logManagement, arrayList);
        readLogItems(readableDatabase, logManagement, arrayList);
        readLogFights(context, readableDatabase, logManagement, arrayList);
        readableDatabase.close();
        return arrayList;
    }

    public boolean existsNewerLog(Date date) {
        SQLiteDatabase readableDatabase = this.mHelper.getReadableDatabase();
        Cursor cursorRawQuery = readableDatabase.rawQuery("SELECT COUNT(*) FROM adventure_log WHERE logTime > ?", new String[]{Integer.toString(CommonLog.calcLogTime(date.getTime()))});
        int i = cursorRawQuery.moveToNext() ? cursorRawQuery.getInt(0) : 0;
        cursorRawQuery.close();
        readableDatabase.close();
        return i > 0;
    }

    public LogStatus readLogStatus(LogManagement logManagement, Date date) {
        LogStatus logStatus;
        SQLiteDatabase readableDatabase = this.mHelper.getReadableDatabase();
        Cursor cursorQuery = readableDatabase.query(SimpleRpgOpenHelper.TABLE_LOG_STATUS, LOG_STATUS_COLUMNS, "lmId = ? AND logTime = ?", new String[]{Integer.toString(logManagement.f103id), Integer.toString(CommonLog.calcLogTime(date.getTime()))}, null, null, null);
        if (cursorQuery.moveToNext()) {
            logStatus = new LogStatus();
            logStatus.f104id = cursorQuery.getInt(cursorQuery.getColumnIndex("_id"));
            logStatus.logTime = cursorQuery.getInt(cursorQuery.getColumnIndex("logTime"));
            logStatus.action = LogStatus.LogAction.values()[cursorQuery.getInt(cursorQuery.getColumnIndex("action"))];
            logStatus.floor = cursorQuery.getInt(cursorQuery.getColumnIndex(TacticsMakingActivity.DIALOG_ARGS_FLOOR));
            logStatus.block = cursorQuery.getInt(cursorQuery.getColumnIndex(TacticsMakingActivity.DIALOG_ARGS_BLOCK));
            logStatus.captiveRate = cursorQuery.getInt(cursorQuery.getColumnIndex("captiveRate"));
        } else {
            logStatus = null;
        }
        cursorQuery.close();
        readableDatabase.close();
        return logStatus;
    }

    private void readAdventureLogChars(SQLiteDatabase sQLiteDatabase, LogManagement logManagement, List<AdventureLog> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("SELECT ");
        for (int i = 0; i < LOG_CHAR_COLUMNS.length; i++) {
            if (i > 0) {
                stringBuffer.append(',');
            }
            stringBuffer.append(SimpleRpgOpenHelper.TABLE_LOG_CHAR);
            stringBuffer.append('.');
            stringBuffer.append(LOG_CHAR_COLUMNS[i]);
        }
        stringBuffer.append(" FROM log_char,adventure_log");
        stringBuffer.append(" WHERE adventure_log.lmId = ?");
        stringBuffer.append(" AND log_char.adventureLogId = adventure_log._id");
        stringBuffer.append(" AND adventure_log.logTime >= ?");
        stringBuffer.append(" AND adventure_log.logTime <= ?");
        stringBuffer.append(" AND log_char.fightingLogId = ?");
        stringBuffer.append(" ORDER BY log_char._id asc");
        Cursor cursorRawQuery = sQLiteDatabase.rawQuery(stringBuffer.toString(), new String[]{Integer.toString(logManagement.f103id), Integer.toString(list.get(0).logTime), Integer.toString(list.get(list.size() - 1).logTime), Integer.toString(0)});
        getLogCharsFromCursor(list, cursorRawQuery);
        cursorRawQuery.close();
    }

    private void getLogCharsFromCursor(List<? extends CommonLog> list, Cursor cursor) {
        Iterator<? extends CommonLog> it = list.iterator();
        CommonLog next = it.next();
        while (cursor.moveToNext()) {
            CommonLog.LogChar logChar = new CommonLog.LogChar();
            logChar.f87id = cursor.getInt(cursor.getColumnIndex("_id"));
            logChar.adventureLogId = cursor.getInt(cursor.getColumnIndex("adventureLogId"));
            logChar.fightingLogId = cursor.getInt(cursor.getColumnIndex("fightingLogId"));
            logChar.charId = cursor.getInt(cursor.getColumnIndex("charId"));
            logChar.exp = cursor.getInt(cursor.getColumnIndex("exp"));
            logChar.level = cursor.getInt(cursor.getColumnIndex("level"));
            logChar.f86hp = cursor.getInt(cursor.getColumnIndex("hp"));
            logChar.maxHp = cursor.getInt(cursor.getColumnIndex("maxHp"));
            logChar.f88mp = cursor.getInt(cursor.getColumnIndex("mp"));
            logChar.maxMp = cursor.getInt(cursor.getColumnIndex("maxMp"));
            while (true) {
                if ((logChar.fightingLogId == 0 && logChar.adventureLogId == next.f85id) || (logChar.fightingLogId != 0 && logChar.fightingLogId == next.f85id)) {
                    break;
                }
                next = null;
                if (!it.hasNext()) {
                    break;
                } else {
                    next = it.next();
                }
            }
            if (next == null) {
                return;
            }
            if (next.logChars == null) {
                next.logChars = new ArrayList(3);
            }
            next.logChars.add(logChar);
        }
    }

    private void readLogItems(SQLiteDatabase sQLiteDatabase, LogManagement logManagement, List<AdventureLog> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("SELECT ");
        for (int i = 0; i < LOG_ITEM_COLUMNS.length; i++) {
            if (i > 0) {
                stringBuffer.append(',');
            }
            stringBuffer.append(SimpleRpgOpenHelper.TABLE_LOG_ITEM);
            stringBuffer.append('.');
            stringBuffer.append(LOG_ITEM_COLUMNS[i]);
        }
        stringBuffer.append(" FROM log_item,adventure_log");
        stringBuffer.append(" WHERE adventure_log.lmId = ?");
        stringBuffer.append(" AND log_item.adventureLogId = adventure_log._id");
        stringBuffer.append(" AND adventure_log.logTime >= ?");
        stringBuffer.append(" AND adventure_log.logTime <= ?");
        stringBuffer.append(" ORDER BY log_item._id asc");
        Cursor cursorRawQuery = sQLiteDatabase.rawQuery(stringBuffer.toString(), new String[]{Integer.toString(logManagement.f103id), Integer.toString(list.get(0).logTime), Integer.toString(list.get(list.size() - 1).logTime)});
        Iterator<AdventureLog> it = list.iterator();
        AdventureLog next = it.next();
        while (cursorRawQuery.moveToNext()) {
            AdventureLog.LogInventory logInventory = new AdventureLog.LogInventory();
            logInventory.f84id = cursorRawQuery.getInt(cursorRawQuery.getColumnIndex("_id"));
            logInventory.adventureLogId = cursorRawQuery.getInt(cursorRawQuery.getColumnIndex("adventureLogId"));
            logInventory.itemId = cursorRawQuery.getInt(cursorRawQuery.getColumnIndex("itemId"));
            logInventory.equippedCharId = cursorRawQuery.getInt(cursorRawQuery.getColumnIndex("equippedCharId"));
            while (next.f85id != logInventory.adventureLogId) {
                next = null;
                if (!it.hasNext()) {
                    break;
                } else {
                    next = it.next();
                }
            }
            if (next == null) {
                break;
            }
            if (next.logInventories == null) {
                next.logInventories = new ArrayList();
            }
            next.logInventories.add(logInventory);
        }
        cursorRawQuery.close();
    }

    private void readLogFights(Context context, SQLiteDatabase sQLiteDatabase, LogManagement logManagement, List<AdventureLog> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("SELECT ");
        for (int i = 0; i < LOG_FIGHT_COLUMNS.length; i++) {
            if (i > 0) {
                stringBuffer.append(',');
            }
            stringBuffer.append(SimpleRpgOpenHelper.TABLE_LOG_FIGHT);
            stringBuffer.append('.');
            stringBuffer.append(LOG_FIGHT_COLUMNS[i]);
        }
        stringBuffer.append(" FROM log_fight,adventure_log");
        stringBuffer.append(" WHERE adventure_log.lmId = ?");
        stringBuffer.append(" AND log_fight.adventureLogId = adventure_log._id");
        stringBuffer.append(" AND adventure_log.logTime >= ?");
        stringBuffer.append(" AND adventure_log.logTime <= ?");
        stringBuffer.append(" ORDER BY log_fight._id asc");
        Cursor cursorRawQuery = sQLiteDatabase.rawQuery(stringBuffer.toString(), new String[]{Integer.toString(logManagement.f103id), Integer.toString(list.get(0).logTime), Integer.toString(list.get(list.size() - 1).logTime)});
        Iterator<AdventureLog> it = list.iterator();
        AdventureLog next = it.next();
        while (cursorRawQuery.moveToNext()) {
            LogFight logFightFromCursor = readLogFightFromCursor(context, cursorRawQuery);
            while (next.f85id != logFightFromCursor.adventureLogId) {
                next = null;
                if (!it.hasNext()) {
                    break;
                } else {
                    next = it.next();
                }
            }
            if (next == null) {
                break;
            } else {
                next.logFight = logFightFromCursor;
            }
        }
        cursorRawQuery.close();
    }

    private LogFight readLogFightFromCursor(Context context, Cursor cursor) {
        LogFight logFight = new LogFight();
        logFight.f102id = cursor.getInt(cursor.getColumnIndex("_id"));
        logFight.adventureLogId = cursor.getInt(cursor.getColumnIndex("adventureLogId"));
        logFight.isWandering = cursor.getInt(cursor.getColumnIndex("wandering")) != 0;
        logFight.isEvent = cursor.getInt(cursor.getColumnIndex(NotificationCompat.CATEGORY_EVENT)) != 0;
        int i = cursor.getInt(cursor.getColumnIndex("monsterId1"));
        int i2 = cursor.getInt(cursor.getColumnIndex("monsterId2"));
        int i3 = cursor.getInt(cursor.getColumnIndex("monsterId3"));
        logFight.monster = i == 0 ? null : MonsterRepository.getMonster(context, i);
        logFight.monster2 = i2 == 0 ? null : MonsterRepository.getMonster(context, i2);
        logFight.monster3 = i3 != 0 ? MonsterRepository.getMonster(context, i3) : null;
        return logFight;
    }

    public LogFight readLogFight(Context context, int i, SparseArray<PlayerChar> sparseArray) {
        SQLiteDatabase readableDatabase = this.mHelper.getReadableDatabase();
        Cursor cursorQuery = readableDatabase.query(SimpleRpgOpenHelper.TABLE_LOG_FIGHT, LOG_FIGHT_COLUMNS, "adventureLogId = ?", new String[]{Integer.toString(i)}, null, null, null);
        LogFight logFightFromCursor = cursorQuery.moveToFirst() ? readLogFightFromCursor(context, cursorQuery) : null;
        cursorQuery.close();
        if (logFightFromCursor != null) {
            CommonLog.LogType[] logTypeArrValues = CommonLog.LogType.values();
            ArrayList arrayList = new ArrayList();
            logFightFromCursor.fightingLogs = arrayList;
            Cursor cursorQuery2 = readableDatabase.query(SimpleRpgOpenHelper.TABLE_FIGHTING_LOG, FIGHTING_LOG_COLUMNS, "adventureLogId = ?", new String[]{Integer.toString(i)}, null, null, "_id asc");
            while (cursorQuery2.moveToNext()) {
                FightingLog fightingLog = new FightingLog();
                fightingLog.f85id = cursorQuery2.getInt(cursorQuery2.getColumnIndex("_id"));
                fightingLog.type = logTypeArrValues[cursorQuery2.getInt(cursorQuery2.getColumnIndex("type"))];
                int i2 = cursorQuery2.getInt(cursorQuery2.getColumnIndex("itemId"));
                int i3 = cursorQuery2.getInt(cursorQuery2.getColumnIndex("charId"));
                fightingLog.title = cursorQuery2.getString(cursorQuery2.getColumnIndex("title"));
                fightingLog.desc1 = cursorQuery2.getString(cursorQuery2.getColumnIndex("desc1"));
                fightingLog.desc2 = cursorQuery2.getString(cursorQuery2.getColumnIndex("desc2"));
                fightingLog.adventureLogId = cursorQuery2.getInt(cursorQuery2.getColumnIndex("adventureLogId"));
                fightingLog.playersAct = cursorQuery2.getInt(cursorQuery2.getColumnIndex("playersAct")) != 0;
                fightingLog.toPlayer = cursorQuery2.getInt(cursorQuery2.getColumnIndex("toPlayer")) != 0;
                fightingLog.enemyIndex = cursorQuery2.getInt(cursorQuery2.getColumnIndex("enemyIndex"));
                fightingLog.targetIds = stringToIntArray(cursorQuery2.getString(cursorQuery2.getColumnIndex("targetIds")));
                fightingLog.playerChar = i3 == 0 ? null : sparseArray.get(i3);
                fightingLog.item = i2 == 0 ? null : ItemRepository.getItem(context, i2);
                fightingLog.logFight = logFightFromCursor;
                arrayList.add(fightingLog);
            }
            cursorQuery2.close();
            if (!arrayList.isEmpty()) {
                Cursor cursorQuery3 = readableDatabase.query(SimpleRpgOpenHelper.TABLE_LOG_CHAR, LOG_CHAR_COLUMNS, "adventureLogId = ? AND fightingLogId != ?", new String[]{Integer.toString(i), Integer.toString(0)}, null, null, "_id asc");
                getLogCharsFromCursor(arrayList, cursorQuery3);
                cursorQuery3.close();
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("SELECT ");
                for (int i4 = 0; i4 < LOG_ENEMY_CHAR_COLUMNS.length; i4++) {
                    if (i4 > 0) {
                        stringBuffer.append(',');
                    }
                    stringBuffer.append("lec.");
                    stringBuffer.append(LOG_ENEMY_CHAR_COLUMNS[i4]);
                }
                stringBuffer.append(" FROM log_enemy_char lec,fighting_log fl");
                stringBuffer.append(" WHERE lec.fightingLogId = fl._id AND fl.adventureLogId = ?");
                stringBuffer.append(" ORDER BY lec._id asc");
                String[] strArr = {Integer.toString(i)};
                Iterator it = arrayList.iterator();
                FightingLog fightingLog2 = (FightingLog) it.next();
                Cursor cursorRawQuery = readableDatabase.rawQuery(stringBuffer.toString(), strArr);
                while (cursorRawQuery.moveToNext()) {
                    LogEnemy logEnemy = new LogEnemy();
                    logEnemy.f100id = cursorRawQuery.getInt(cursorRawQuery.getColumnIndex("_id"));
                    logEnemy.fightingLogId = cursorRawQuery.getInt(cursorRawQuery.getColumnIndex("fightingLogId"));
                    logEnemy.enemyIndex = cursorRawQuery.getInt(cursorRawQuery.getColumnIndex("enemyIndex"));
                    logEnemy.f99hp = cursorRawQuery.getInt(cursorRawQuery.getColumnIndex("hp"));
                    logEnemy.f101mp = cursorRawQuery.getInt(cursorRawQuery.getColumnIndex("mp"));
                    while (fightingLog2.f85id != logEnemy.fightingLogId && it.hasNext()) {
                        fightingLog2 = (FightingLog) it.next();
                    }
                    if (fightingLog2.f85id != logEnemy.fightingLogId) {
                        break;
                    }
                    if (fightingLog2.logEnemies == null) {
                        fightingLog2.logEnemies = new ArrayList();
                    }
                    fightingLog2.logEnemies.add(logEnemy);
                }
                cursorRawQuery.close();
            }
        }
        readableDatabase.close();
        return logFightFromCursor;
    }

    public void removeIllegalLogs(LogManagement logManagement) {
        deleteLogs(logManagement);
    }

    public void deleteLogs(LogManagement logManagement) {
        String[] strArr = {Integer.toString(logManagement.f103id)};
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.execSQL("DELETE FROM log_char WHERE adventureLogId IN (SELECT _id FROM adventure_log WHERE lmId = ?)", strArr);
            writableDatabase.execSQL("DELETE FROM log_enemy_char WHERE fightingLogId IN (SELECT fl._id FROM fighting_log fl, adventure_log al WHERE fl.adventureLogId = al._id AND al.lmId = ?)", strArr);
            writableDatabase.execSQL("DELETE FROM fighting_log WHERE adventureLogId IN (SELECT _id FROM adventure_log WHERE lmId = ?)", strArr);
            writableDatabase.execSQL("DELETE FROM log_fight WHERE adventureLogId IN (SELECT _id FROM adventure_log WHERE lmId = ?)", strArr);
            writableDatabase.execSQL("DELETE FROM log_item WHERE adventureLogId IN (SELECT _id FROM adventure_log WHERE lmId = ?)", strArr);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_ADVENTURE_LOG, "lmId = ?", strArr);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_LOG_STATUS, "lmId = ?", strArr);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_LOG_MANAGEMENT, "_id = ?", strArr);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    public boolean isResultExists() {
        SQLiteDatabase readableDatabase = this.mHelper.getReadableDatabase();
        Cursor cursorRawQuery = readableDatabase.rawQuery("SELECT COUNT(*) FROM game_context_result", null);
        int i = cursorRawQuery.moveToNext() ? cursorRawQuery.getInt(0) : 0;
        cursorRawQuery.close();
        readableDatabase.close();
        return i > 0;
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    public void writeAdventureResult(GameContext gameContext, Map<GameFlag.Key, GameFlag> map) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_GAME_CONTEXT_RESULT, null, null);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_PLAYER_CHAR_RESULT, null, null);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_CHAR_SKILL_RESULT, null, null);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_INVENTORY_RESULT, null, null);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_GAME_FLAG_RESULT, null, null);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_DUNGEON_STAT_RESULT, null, null);
            writeGameContextTable(writableDatabase, true, gameContext);
            Iterator<PlayerChar> it = gameContext.characters.iterator();
            while (it.hasNext()) {
                writePlayerTable(writableDatabase, true, it.next());
            }
            writeDungeonContext(writableDatabase, true, gameContext.dungeonContext);
            Iterator<Inventory> it2 = gameContext.inventories.iterator();
            while (it2.hasNext()) {
                writeInventoryTable(writableDatabase, true, it2.next());
            }
            for (Map.Entry<GameFlag.Key, GameFlag> entry : gameContext.flags.entrySet()) {
                GameFlag value = entry.getValue();
                GameFlag gameFlag = map.get(entry.getKey());
                if (gameFlag == null || !gameFlag.equals(value)) {
                    writeFlag(writableDatabase, true, value);
                }
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    public void copyResultsToCurrent() {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writeGameContextTable(writableDatabase, false, readGameContextOnly(writableDatabase, SimpleRpgOpenHelper.TABLE_GAME_CONTEXT_RESULT));
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_GAME_CONTEXT_RESULT, null, null);
            List<PlayerChar> allPlayerChars = readAllPlayerChars(writableDatabase, SimpleRpgOpenHelper.TABLE_PLAYER_CHAR_RESULT);
            Iterator<PlayerChar> it = allPlayerChars.iterator();
            while (it.hasNext()) {
                writePlayerTable(writableDatabase, false, it.next());
            }
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_PLAYER_CHAR_RESULT, null, null);
            for (PlayerChar playerChar : allPlayerChars) {
                List<Integer> skills = readSkills(writableDatabase, SimpleRpgOpenHelper.TABLE_CHAR_SKILL_RESULT, playerChar.f106id);
                HashSet hashSet = new HashSet(readSkills(writableDatabase, SimpleRpgOpenHelper.TABLE_CHAR_SKILL, playerChar.f106id));
                for (Integer num : skills) {
                    if (!hashSet.contains(num)) {
                        writeSkill(writableDatabase, SimpleRpgOpenHelper.TABLE_CHAR_SKILL, playerChar.f106id, num.intValue());
                    }
                }
            }
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_CHAR_SKILL_RESULT, null, null);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_DUNGEON_STAT, null, null);
            String strJoinExceptId = joinExceptId(DUNGEON_STAT_COLUMNS);
            writableDatabase.execSQL("INSERT INTO dungeon_stat (" + strJoinExceptId + ") SELECT " + strJoinExceptId + " FROM " + SimpleRpgOpenHelper.TABLE_DUNGEON_STAT_RESULT);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_DUNGEON_STAT_RESULT, null, null);
            Iterator<GameFlag> it2 = readGameFlags(writableDatabase, true).values().iterator();
            while (it2.hasNext()) {
                writeFlag(writableDatabase, false, it2.next());
            }
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_GAME_FLAG_RESULT, null, null);
            String strJoinExceptId2 = joinExceptId(INVENTORY_COLUMNS);
            writableDatabase.execSQL("DELETE FROM inventory WHERE _id NOT IN (SELECT _id FROM inventory_result)");
            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE ");
            sb.append(SimpleRpgOpenHelper.TABLE_INVENTORY);
            sb.append(" SET ");
            for (int i = 1; i < INVENTORY_COLUMNS.length; i++) {
                if (i > 1) {
                    sb.append(',');
                }
                sb.append(INVENTORY_COLUMNS[i]);
                sb.append(" = ");
                sb.append("(SELECT ");
                sb.append(SimpleRpgOpenHelper.TABLE_INVENTORY_RESULT);
                sb.append(".");
                sb.append(INVENTORY_COLUMNS[i]);
                sb.append(" FROM ");
                sb.append(SimpleRpgOpenHelper.TABLE_INVENTORY_RESULT);
                sb.append(" WHERE ");
                sb.append(SimpleRpgOpenHelper.TABLE_INVENTORY);
                sb.append("._id = ");
                sb.append(SimpleRpgOpenHelper.TABLE_INVENTORY_RESULT);
                sb.append("._id)");
            }
            writableDatabase.execSQL(sb.toString());
            writableDatabase.execSQL("INSERT INTO inventory (" + strJoinExceptId2 + ") SELECT " + strJoinExceptId2 + " FROM " + SimpleRpgOpenHelper.TABLE_INVENTORY_RESULT + " WHERE _id = 0");
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_INVENTORY_RESULT, null, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    private String joinExceptId(String[] strArr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < strArr.length; i++) {
            if (i > 1) {
                sb.append(',');
            }
            sb.append(strArr[i]);
        }
        return sb.toString();
    }

    public ArrayList<Tactics> readTactics(int i) {
        SQLiteDatabase readableDatabase = this.mHelper.getReadableDatabase();
        Cursor cursorQuery = readableDatabase.query(SimpleRpgOpenHelper.TABLE_TACTICS, TACTICS_COLUMNS, "charId = ?", new String[]{Integer.toString(i)}, null, null, null);
        ArrayList<Tactics> tacticsFromCursor = readTacticsFromCursor(cursorQuery);
        cursorQuery.close();
        readableDatabase.close();
        return tacticsFromCursor;
    }

    public ArrayList<Tactics> readEnabledTactics(int i) {
        SQLiteDatabase readableDatabase = this.mHelper.getReadableDatabase();
        Cursor cursorQuery = readableDatabase.query(SimpleRpgOpenHelper.TABLE_TACTICS, TACTICS_COLUMNS, "charId = ? AND enabled <> 0", new String[]{Integer.toString(i)}, null, null, null);
        ArrayList<Tactics> tacticsFromCursor = readTacticsFromCursor(cursorQuery);
        cursorQuery.close();
        readableDatabase.close();
        return tacticsFromCursor;
    }

    private ArrayList<Tactics> readTacticsFromCursor(Cursor cursor) {
        ArrayList<Tactics> arrayList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Tactics.TacticsValue[] tacticsValueArrValues = Tactics.TacticsValue.values();
            Tactics tactics = new Tactics();
            tactics.f110id = cursor.getInt(cursor.getColumnIndex("_id"));
            tactics.charId = cursor.getInt(cursor.getColumnIndex("charId"));
            tactics.enabled = cursor.getInt(cursor.getColumnIndex("enabled")) != 0;
            tactics.targetFloor = cursor.getInt(cursor.getColumnIndex("targetFloor"));
            tactics.abort = tacticsValueArrValues[cursor.getInt(cursor.getColumnIndex("abort"))];
            tactics.running = tacticsValueArrValues[cursor.getInt(cursor.getColumnIndex("running"))];
            tactics.attackSkill = tacticsValueArrValues[cursor.getInt(cursor.getColumnIndex("attackSkill"))];
            tactics.statusSkill = tacticsValueArrValues[cursor.getInt(cursor.getColumnIndex("statusSkill"))];
            tactics.cureSkill = tacticsValueArrValues[cursor.getInt(cursor.getColumnIndex("cureSkill"))];
            tactics.damageSkill = tacticsValueArrValues[cursor.getInt(cursor.getColumnIndex("damageSkill"))];
            tactics.item = tacticsValueArrValues[cursor.getInt(cursor.getColumnIndex("item"))];
            tactics.rest = tacticsValueArrValues[cursor.getInt(cursor.getColumnIndex("rest"))];
            tactics.fullInventory = Tactics.FullInventoryTactics.values()[cursor.getInt(cursor.getColumnIndex("fullInventory"))];
            tactics.useItemFloor = cursor.getInt(cursor.getColumnIndex("useItemFloor"));
            tactics.useItemBlock = cursor.getInt(cursor.getColumnIndex("useItemBlock"));
            tactics.useItemId = cursor.getInt(cursor.getColumnIndex("useItemId"));
            arrayList.add(tactics);
        }
        return arrayList;
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    public void writeTactics(int i, List<Tactics> list) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_TACTICS, "charId = ?", new String[]{Integer.toString(i)});
            ContentValues contentValues = new ContentValues();
            for (Tactics tactics : list) {
                contentValues.put("charId", Integer.valueOf(tactics.charId));
                contentValues.put("enabled", Integer.valueOf(tactics.enabled ? 1 : 0));
                contentValues.put("targetFloor", Integer.valueOf(tactics.targetFloor));
                contentValues.put("abort", Integer.valueOf(tactics.abort.ordinal()));
                contentValues.put("running", Integer.valueOf(tactics.running.ordinal()));
                contentValues.put("attackSkill", Integer.valueOf(tactics.attackSkill.ordinal()));
                contentValues.put("statusSkill", Integer.valueOf(tactics.statusSkill.ordinal()));
                contentValues.put("cureSkill", Integer.valueOf(tactics.cureSkill.ordinal()));
                contentValues.put("damageSkill", Integer.valueOf(tactics.damageSkill.ordinal()));
                contentValues.put("item", Integer.valueOf(tactics.item.ordinal()));
                contentValues.put("rest", Integer.valueOf(tactics.rest.ordinal()));
                contentValues.put("fullInventory", Integer.valueOf(tactics.fullInventory.ordinal()));
                contentValues.put("useItemFloor", Integer.valueOf(tactics.useItemFloor));
                contentValues.put("useItemBlock", Integer.valueOf(tactics.useItemBlock));
                contentValues.put("useItemId", Integer.valueOf(tactics.useItemId));
                long jInsertOrThrow = writableDatabase.insertOrThrow(SimpleRpgOpenHelper.TABLE_TACTICS, null, contentValues);
                if (jInsertOrThrow >= 0) {
                    tactics.f110id = (int) jInsertOrThrow;
                }
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    public List<AdvancedTactics> readAdvancedTactics(int i) {
        SQLiteDatabase readableDatabase = this.mHelper.getReadableDatabase();
        Cursor cursorQuery = readableDatabase.query(SimpleRpgOpenHelper.TABLE_ADVANCED_TACTICS, ADVANCED_TACTICS_COLUMNS, "charId = ?", new String[]{Integer.toString(i)}, null, null, "_id");
        ArrayList arrayList = new ArrayList();
        while (cursorQuery.moveToNext()) {
            AdvancedTactics advancedTactics = new AdvancedTactics();
            advancedTactics.f73id = cursorQuery.getInt(cursorQuery.getColumnIndex("_id"));
            advancedTactics.fighting = cursorQuery.getInt(cursorQuery.getColumnIndex("fighting")) != 0;
            advancedTactics.charId = cursorQuery.getInt(cursorQuery.getColumnIndex("charId"));
            advancedTactics.condition = (AdvancedTactics.Condition) TypeUtil.getEnumOrNull(AdvancedTactics.Condition.class, cursorQuery.getString(cursorQuery.getColumnIndex("condition")));
            advancedTactics.conditionSub = (AdvancedTactics.ConditionSub) TypeUtil.getEnumOrNull(AdvancedTactics.ConditionSub.class, cursorQuery.getString(cursorQuery.getColumnIndex("conditionSub")));
            advancedTactics.target = (AdvancedTactics.Target) TypeUtil.getEnumOrNull(AdvancedTactics.Target.class, cursorQuery.getString(cursorQuery.getColumnIndex("target")));
            advancedTactics.action = (AdvancedTactics.TacticsAction) TypeUtil.getEnumOrNull(AdvancedTactics.TacticsAction.class, cursorQuery.getString(cursorQuery.getColumnIndex("action")));
            advancedTactics.actionSub = (AdvancedTactics.TacticsActionSub) TypeUtil.getEnumOrNull(AdvancedTactics.TacticsActionSub.class, cursorQuery.getString(cursorQuery.getColumnIndex("actionSub")));
            advancedTactics.targetId = cursorQuery.getInt(cursorQuery.getColumnIndex("targetId"));
            advancedTactics.targetCharId = cursorQuery.getInt(cursorQuery.getColumnIndex("targetCharId"));
            advancedTactics.conditionValue = cursorQuery.getInt(cursorQuery.getColumnIndex("conditionValue"));
            advancedTactics.conditionCharId = cursorQuery.getInt(cursorQuery.getColumnIndex("conditionCharId"));
            advancedTactics.conditionNot = cursorQuery.getInt(cursorQuery.getColumnIndex("conditionNot")) != 0;
            arrayList.add(advancedTactics);
        }
        cursorQuery.close();
        readableDatabase.close();
        return arrayList;
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    public void writeAdvancedTactics(int i, List<AdvancedTactics> list) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_ADVANCED_TACTICS, "charId = ?", new String[]{Integer.toString(i)});
            ContentValues contentValues = new ContentValues();
            for (AdvancedTactics advancedTactics : list) {
                contentValues.put("charId", Integer.valueOf(advancedTactics.charId));
                contentValues.put("fighting", Integer.valueOf(advancedTactics.fighting ? 1 : 0));
                contentValues.put("condition", TypeUtil.getNameOrNull(advancedTactics.condition));
                contentValues.put("conditionSub", TypeUtil.getNameOrNull(advancedTactics.conditionSub));
                contentValues.put("target", TypeUtil.getNameOrNull(advancedTactics.target));
                contentValues.put("action", TypeUtil.getNameOrNull(advancedTactics.action));
                contentValues.put("actionSub", TypeUtil.getNameOrNull(advancedTactics.actionSub));
                contentValues.put("targetId", Integer.valueOf(advancedTactics.targetId));
                contentValues.put("targetCharId", Integer.valueOf(advancedTactics.targetCharId));
                contentValues.put("conditionValue", Integer.valueOf(advancedTactics.conditionValue));
                contentValues.put("conditionCharId", Integer.valueOf(advancedTactics.conditionCharId));
                contentValues.put("conditionNot", Integer.valueOf(advancedTactics.conditionNot ? 1 : 0));
                long jInsertOrThrow = writableDatabase.insertOrThrow(SimpleRpgOpenHelper.TABLE_ADVANCED_TACTICS, null, contentValues);
                if (jInsertOrThrow >= 0) {
                    advancedTactics.f73id = (int) jInsertOrThrow;
                }
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    SQLiteDatabase getReadableDatabase() {
        return this.mHelper.getReadableDatabase();
    }

    SQLiteDatabase getWritableDatabase() {
        return this.mHelper.getWritableDatabase();
    }

    BackupRestoreUtil.BackupData backupData(SQLiteDatabase sQLiteDatabase) {
        BackupRestoreUtil.BackupData backupData = new BackupRestoreUtil.BackupData();
        backupData.normalTables = new ArrayList();
        backupData.blobTables = new ArrayList();
        backupData.normalTables.add(readRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_GAME_CONTEXT, GAME_CONTEXT_COLUMNS));
        backupData.normalTables.add(readRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_PLAYER_CHAR, PLAYER_CHAR_COLUMNS));
        backupData.normalTables.add(readRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_ACTIVE_CHAR, ACTIVE_CHAR_COLUMNS));
        backupData.normalTables.add(readRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_CHAR_SKILL, CHAR_SKILL_COLUMNS));
        backupData.normalTables.add(readRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_SKILL_SLOT, SKILL_SLOT_COLUMNS));
        backupData.normalTables.add(readRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_GAME_FLAG, GAME_FLAG_COLUMNS));
        backupData.normalTables.add(readRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_INVENTORY, INVENTORY_COLUMNS));
        backupData.normalTables.add(readRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_STOCK, STOCK_COLUMNS));
        backupData.normalTables.add(readRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_TACTICS, TACTICS_COLUMNS));
        backupData.normalTables.add(readRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_ADVANCED_TACTICS, ADVANCED_TACTICS_COLUMNS));
        backupData.normalTables.add(readRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_SKILL_CUSTOMIZATION, SKILL_CUSTOMIZATION_COLUMNS));
        backupData.blobTables.add(readRawTableWithBlob(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_PLAYER_CHAR_BITMAP, PLAYER_CHAR_BITMAP_COLUMNS, new String[]{"bitmap"}));
        return backupData;
    }

    private List<Object[]> readRawTableWithBlob(SQLiteDatabase sQLiteDatabase, String str, String[] strArr, String[] strArr2) {
        HashSet hashSet = new HashSet(strArr2.length);
        for (String str2 : strArr2) {
            hashSet.add(str2);
        }
        ArrayList arrayList = new ArrayList();
        Cursor cursorQuery = sQLiteDatabase.query(str, strArr, null, null, null, null, null);
        while (cursorQuery.moveToNext()) {
            Object[] objArr = new Object[strArr.length];
            arrayList.add(objArr);
            for (int i = 0; i < strArr.length; i++) {
                if (hashSet.contains(strArr[i])) {
                    objArr[i] = cursorQuery.getBlob(i);
                } else {
                    objArr[i] = cursorQuery.getString(i);
                }
            }
        }
        cursorQuery.close();
        return arrayList;
    }

    private List<String[]> readRawTable(SQLiteDatabase sQLiteDatabase, String str, String[] strArr) {
        ArrayList arrayList = new ArrayList();
        Cursor cursorQuery = sQLiteDatabase.query(str, strArr, null, null, null, null, null);
        while (cursorQuery.moveToNext()) {
            String[] strArr2 = new String[strArr.length];
            arrayList.add(strArr2);
            for (int i = 0; i < strArr.length; i++) {
                strArr2[i] = cursorQuery.getString(i);
            }
        }
        cursorQuery.close();
        return arrayList;
    }

    void restoreData(SQLiteDatabase sQLiteDatabase, BackupRestoreUtil.BackupData backupData) throws SQLException {
        Iterator<List<String[]>> it = backupData.normalTables.iterator();
        writeRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_GAME_CONTEXT, GAME_CONTEXT_COLUMNS, it.next());
        writeRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_PLAYER_CHAR, PLAYER_CHAR_COLUMNS, it.next());
        writeRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_ACTIVE_CHAR, ACTIVE_CHAR_COLUMNS, it.next());
        writeRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_CHAR_SKILL, CHAR_SKILL_COLUMNS, it.next());
        writeRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_SKILL_SLOT, SKILL_SLOT_COLUMNS, it.next());
        writeRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_GAME_FLAG, GAME_FLAG_COLUMNS, it.next());
        writeRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_INVENTORY, INVENTORY_COLUMNS, it.next());
        writeRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_STOCK, STOCK_COLUMNS, it.next());
        writeRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_TACTICS, TACTICS_COLUMNS, it.next());
        writeRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_ADVANCED_TACTICS, ADVANCED_TACTICS_COLUMNS, it.next());
        writeRawTable(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_SKILL_CUSTOMIZATION, SKILL_CUSTOMIZATION_COLUMNS, it.next());
        writeRawTableWithBlob(sQLiteDatabase, SimpleRpgOpenHelper.TABLE_PLAYER_CHAR_BITMAP, PLAYER_CHAR_BITMAP_COLUMNS, new String[]{"bitmap"}, backupData.blobTables.iterator().next());
    }

    private void writeRawTable(SQLiteDatabase sQLiteDatabase, String str, String[] strArr, List<String[]> list) throws SQLException {
        for (String[] strArr2 : list) {
            ContentValues contentValues = new ContentValues();
            for (int i = 0; i < strArr.length; i++) {
                contentValues.put(strArr[i], strArr2[i]);
            }
            sQLiteDatabase.insertOrThrow(str, null, contentValues);
        }
    }

    private void writeRawTableWithBlob(SQLiteDatabase sQLiteDatabase, String str, String[] strArr, String[] strArr2, List<Object[]> list) throws SQLException {
        HashSet hashSet = new HashSet(strArr2.length);
        for (String str2 : strArr2) {
            hashSet.add(str2);
        }
        for (Object[] objArr : list) {
            ContentValues contentValues = new ContentValues();
            for (int i = 0; i < strArr.length; i++) {
                String str3 = strArr[i];
                if (hashSet.contains(str3)) {
                    contentValues.put(str3, (byte[]) objArr[i]);
                } else {
                    contentValues.put(str3, (String) objArr[i]);
                }
            }
            sQLiteDatabase.insertOrThrow(str, null, contentValues);
        }
    }

    public void deleteSkillCustomization(int i, int i2) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_SKILL_CUSTOMIZATION, "charId = ? AND skillId = ?", new String[]{Integer.toString(i), Integer.toString(i2)});
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    public void writeSkillCustomization(SkillCustomization skillCustomization) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("charId", Integer.valueOf(skillCustomization.charId));
            contentValues.put("skillId", Integer.valueOf(skillCustomization.skillId));
            contentValues.put("skillName", skillCustomization.skillName);
            contentValues.put("skillDesc", skillCustomization.skillDesc);
            if (skillCustomization.f109id == 0) {
                long jInsertOrThrow = writableDatabase.insertOrThrow(SimpleRpgOpenHelper.TABLE_SKILL_CUSTOMIZATION, null, contentValues);
                if (jInsertOrThrow >= 0) {
                    skillCustomization.f109id = (int) jInsertOrThrow;
                }
            } else if (writableDatabase.update(SimpleRpgOpenHelper.TABLE_SKILL_CUSTOMIZATION, contentValues, "_id = ?", new String[]{Integer.toString(skillCustomization.f109id)}) != 1) {
                throw new IllegalStateException("Update failed:" + skillCustomization.f109id);
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    public SkillCustomization readSkillCustomization(int i, int i2) {
        SQLiteDatabase readableDatabase = this.mHelper.getReadableDatabase();
        Cursor cursorQuery = readableDatabase.query(SimpleRpgOpenHelper.TABLE_SKILL_CUSTOMIZATION, SKILL_CUSTOMIZATION_COLUMNS, "charId = ? AND skillId = ?", new String[]{Integer.toString(i), Integer.toString(i2)}, null, null, null);
        SkillCustomization skillCustomizationFromCursor = cursorQuery.moveToFirst() ? readSkillCustomizationFromCursor(cursorQuery) : null;
        cursorQuery.close();
        readableDatabase.close();
        return skillCustomizationFromCursor;
    }

    private SkillCustomization readSkillCustomizationFromCursor(Cursor cursor) {
        SkillCustomization skillCustomization = new SkillCustomization();
        skillCustomization.f109id = cursor.getInt(cursor.getColumnIndex("_id"));
        skillCustomization.charId = cursor.getInt(cursor.getColumnIndex("charId"));
        skillCustomization.skillId = cursor.getInt(cursor.getColumnIndex("skillId"));
        skillCustomization.skillName = cursor.getString(cursor.getColumnIndex("skillName"));
        skillCustomization.skillDesc = cursor.getString(cursor.getColumnIndex("skillDesc"));
        return skillCustomization;
    }

    public void readSkillCustomizationForSkills(int i, List<Skill> list) {
        SparseArray<SkillCustomization> skillCustomizationForPc = readSkillCustomizationForPc(i);
        for (Skill skill : list) {
            if (skill != null) {
                skill.skillCustomization = skillCustomizationForPc.get(skill.f107id);
            }
        }
    }

    public SparseArray<SkillCustomization> readSkillCustomizationForPc(int i) {
        SQLiteDatabase readableDatabase = this.mHelper.getReadableDatabase();
        SparseArray<SkillCustomization> sparseArray = new SparseArray<>();
        Cursor cursorQuery = readableDatabase.query(SimpleRpgOpenHelper.TABLE_SKILL_CUSTOMIZATION, SKILL_CUSTOMIZATION_COLUMNS, "charId = ?", new String[]{Integer.toString(i)}, null, null, null);
        while (cursorQuery.moveToNext()) {
            SkillCustomization skillCustomizationFromCursor = readSkillCustomizationFromCursor(cursorQuery);
            sparseArray.put(skillCustomizationFromCursor.skillId, skillCustomizationFromCursor);
        }
        cursorQuery.close();
        readableDatabase.close();
        return sparseArray;
    }

    public void recoverDataError() {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_GAME_CONTEXT_RESULT, null, null);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_PLAYER_CHAR_RESULT, null, null);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_CHAR_SKILL_RESULT, null, null);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_DUNGEON_STAT_RESULT, null, null);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_GAME_FLAG_RESULT, null, null);
            writableDatabase.delete(SimpleRpgOpenHelper.TABLE_INVENTORY_RESULT, null, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }

    public void rewindLogs(GameContext gameContext, LogManagement logManagement, Date date, long j) {
        SQLiteDatabase writableDatabase = this.mHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            int iCalcLogTime = CommonLog.calcLogTime(date.getTime());
            Cursor cursorRawQuery = writableDatabase.rawQuery("SELECT MIN(logTime) AS logTime FROM adventure_log WHERE lmId = ?", new String[]{Integer.toString(logManagement.f103id)});
            int i = cursorRawQuery.moveToNext() ? cursorRawQuery.getInt(0) : iCalcLogTime;
            cursorRawQuery.close();
            int i2 = iCalcLogTime - i;
            writableDatabase.execSQL("UPDATE adventure_log SET logTime = logTime + " + i2 + " WHERE lmId = " + logManagement.f103id);
            writableDatabase.execSQL("UPDATE log_status SET logTime = logTime + " + i2 + " WHERE lmId = " + logManagement.f103id);
            long j2 = ((long) (i2 * 60)) * 1000;
            writableDatabase.execSQL("UPDATE game_context_result SET startTime = startTime + " + j2 + ", estimateTime = estimateTime + " + j2 + ", returnTime = returnTime + " + j2);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
            writableDatabase.close();
        }
    }
}
