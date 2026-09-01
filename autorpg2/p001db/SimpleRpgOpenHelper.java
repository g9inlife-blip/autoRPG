package com.shirobakama.autorpg2.p001db;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
class SimpleRpgOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "autorpg2.db";
    static final int DATABASE_VERSION = 105;
    private static final String ID_COLUMN = " (_id integer primary key autoincrement, ";
    private static final String RESULT_ID_COLUMN = " (_id integer, ";
    public static final String TABLE_ACTIVE_CHAR = "active_char";
    public static final String TABLE_ADVANCED_TACTICS = "advanced_tactics";
    public static final String TABLE_ADVENTURE_LOG = "adventure_log";
    public static final String TABLE_CHAR_SKILL = "char_skill";
    public static final String TABLE_CHAR_SKILL_RESULT = "char_skill_result";
    public static final String TABLE_DUNGEON_EVENT = "dungeon_event";
    public static final String TABLE_DUNGEON_STAT = "dungeon_stat";
    public static final String TABLE_DUNGEON_STAT_RESULT = "dungeon_stat_result";
    public static final String TABLE_FIGHTING_LOG = "fighting_log";
    public static final String TABLE_GAME_CONTEXT = "game_context";
    public static final String TABLE_GAME_CONTEXT_RESULT = "game_context_result";
    public static final String TABLE_GAME_FLAG = "game_flag";
    public static final String TABLE_GAME_FLAG_RESULT = "game_flag_result";
    public static final String TABLE_INVENTORY = "inventory";
    public static final String TABLE_INVENTORY_RESULT = "inventory_result";
    public static final String TABLE_LOG_CHAR = "log_char";
    public static final String TABLE_LOG_ENEMY_CHAR = "log_enemy_char";
    public static final String TABLE_LOG_FIGHT = "log_fight";
    public static final String TABLE_LOG_ITEM = "log_item";
    public static final String TABLE_LOG_MANAGEMENT = "log_management";
    public static final String TABLE_LOG_STATUS = "log_status";
    public static final String TABLE_PLAYER_CHAR = "player_char";
    public static final String TABLE_PLAYER_CHAR_BITMAP = "player_char_bitmap";
    public static final String TABLE_PLAYER_CHAR_RESULT = "player_char_result";
    public static final String TABLE_SKILL_CUSTOMIZATION = "skill_customization";
    public static final String TABLE_SKILL_SLOT = "skill_slot";
    public static final String TABLE_STOCK = "stock";
    public static final String TABLE_TACTICS = "tactics";
    protected static final String TAG = "srpg-open-helper";
    private static SimpleRpgOpenHelper mInstance;

    private SimpleRpgOpenHelper(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, DATABASE_VERSION);
    }

    static synchronized SimpleRpgOpenHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SimpleRpgOpenHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) throws SQLException {
        createDb(sQLiteDatabase);
    }

    public static void createDb(SQLiteDatabase sQLiteDatabase) throws SQLException {
        sQLiteDatabase.execSQL("create table player_char (_id integer primary key autoincrement,  name text, level integer, race integer, clazz integer, exp integer, statusBonus integer, baseStr integer, baseInt integer, baseAgi integer, baseVit integer, presetBitmapId integer, weaponId integer, armorId integer, shieldId integer, ringId integer, hp integer, mp integer)");
        sQLiteDatabase.execSQL("create table player_char_result (_id integer,  name text, level integer, race integer, clazz integer, exp integer, statusBonus integer, baseStr integer, baseInt integer, baseAgi integer, baseVit integer, presetBitmapId integer, weaponId integer, armorId integer, shieldId integer, ringId integer, hp integer, mp integer)");
        sQLiteDatabase.execSQL("create table player_char_bitmap (_id integer,  bitmap blob)");
        sQLiteDatabase.execSQL("create table char_skill (_id integer primary key autoincrement,  charId integer, skillId integer)");
        sQLiteDatabase.execSQL("create table char_skill_result (_id integer primary key autoincrement,  charId integer, skillId integer)");
        sQLiteDatabase.execSQL("create table skill_slot (_id integer,  charId integer, skillId integer)");
        sQLiteDatabase.execSQL("create table game_context (_id integer primary key autoincrement, gold integer, townId integer, dungeonId integer, targetFloor integer, startTime integer, estimateTime integer, returnTime integer, advCount integer, startRealtime integer, returnRealtime integer)");
        sQLiteDatabase.execSQL("create table game_context_result (_id integer, gold integer, townId integer, dungeonId integer, targetFloor integer, startTime integer, estimateTime integer, returnTime integer, advCount integer, startRealtime integer, returnRealtime integer)");
        sQLiteDatabase.execSQL("create table active_char (_id integer primary key autoincrement,  charId integer, charOrder integer)");
        sQLiteDatabase.execSQL("create table dungeon_stat (_id integer primary key autoincrement, dungeonId integer, floor integer, block integer, blockType integer, blockState integer, monsterId1 integer, monsterId2 integer, monsterId3 integer, monsterNumber integer, initialMonsterNumber integer, captiveRate integer)");
        sQLiteDatabase.execSQL("create table dungeon_stat_result (_id integer, dungeonId integer, floor integer, block integer, blockType integer, blockState integer, monsterId1 integer, monsterId2 integer, monsterId3 integer, monsterNumber integer, initialMonsterNumber integer, captiveRate integer)");
        sQLiteDatabase.execSQL("create table dungeon_event (_id integer primary key autoincrement, dungeonId integer, floor integer, block integer, position integer, type integer)");
        sQLiteDatabase.execSQL("create table game_flag (_id integer primary key autoincrement, type text, name text, value integer, option text)");
        sQLiteDatabase.execSQL("create table game_flag_result (_id integer, type text, name text, value integer, option text)");
        sQLiteDatabase.execSQL("create table inventory (_id integer primary key autoincrement, itemId integer, name text, enchants text)");
        sQLiteDatabase.execSQL("create table inventory_result (_id integer, itemId integer, name text, enchants text)");
        sQLiteDatabase.execSQL("create table stock (_id integer primary key autoincrement, itemId integer, name text, enchants text, countNum integer)");
        sQLiteDatabase.execSQL("create table log_management (_id integer primary key autoincrement, pcId1 integer, pcId2 integer, pcId3 integer, pcName1 text, pcName2 text, pcName3 text, dungeonId integer, targetFloor integer, completed integer)");
        sQLiteDatabase.execSQL("create table adventure_log (_id integer primary key autoincrement, type integer, itemId integer, charId integer, title text, desc1 text, desc2 text, gold integer, logTime integer, lmId integer)");
        sQLiteDatabase.execSQL("create table log_fight (_id integer primary key autoincrement, adventureLogId integer, wandering integer, event integer, monsterId1 integer, monsterId2 integer, monsterId3 integer)");
        sQLiteDatabase.execSQL("create table fighting_log (_id integer primary key autoincrement, type integer, itemId integer, charId integer, title text, desc1 text, desc2 text, adventureLogId integer, playersAct integer, toPlayer integer, enemyIndex integer, targetIds text)");
        sQLiteDatabase.execSQL("create table log_status (_id integer primary key autoincrement, lmId integer, logTime integer, action integer, floor integer, block integer, captiveRate integer)");
        sQLiteDatabase.execSQL("create table log_char (_id integer primary key autoincrement, adventureLogId integer, fightingLogId integer, charId integer, hp integer, maxHp integer, mp integer, maxMp integer, exp integer, level integer)");
        sQLiteDatabase.execSQL("create table log_enemy_char (_id integer primary key autoincrement, fightingLogId integer, enemyIndex integer, hp integer, mp integer)");
        sQLiteDatabase.execSQL("create table log_item (_id integer primary key autoincrement, adventureLogId integer, itemId integer, equippedCharId integer)");
        sQLiteDatabase.execSQL("create table tactics (_id integer primary key autoincrement, charId integer, enabled integer, targetFloor integer, abort integer, running integer, attackSkill integer, statusSkill integer, cureSkill integer, damageSkill integer, item integer, rest integer, fullInventory integer, useItemFloor integer, useItemBlock interger, useItemId interger)");
        sQLiteDatabase.execSQL("create table advanced_tactics (_id integer primary key autoincrement, charId integer, fighting integer, condition text, conditionSub text,  conditionCharId integer, conditionValue integer, conditionNot integer, action text, actionSub text, target text, targetId integer, targetCharId integer)");
        sQLiteDatabase.execSQL("create table skill_customization (_id integer primary key autoincrement, charId integer, skillId integer, skillName text, skillDesc text)");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) throws SQLException {
        upgradeDb(sQLiteDatabase, i);
    }

    private static void dropDb(SQLiteDatabase sQLiteDatabase) throws SQLException {
        sQLiteDatabase.execSQL("drop table if exists player_char");
        sQLiteDatabase.execSQL("drop table if exists player_char_bitmap");
        sQLiteDatabase.execSQL("drop table if exists char_skill");
        sQLiteDatabase.execSQL("drop table if exists skill_slot");
        sQLiteDatabase.execSQL("drop table if exists game_context");
        sQLiteDatabase.execSQL("drop table if exists active_char");
        sQLiteDatabase.execSQL("drop table if exists dungeon_stat");
        sQLiteDatabase.execSQL("drop table if exists dungeon_event");
        sQLiteDatabase.execSQL("drop table if exists game_flag");
        sQLiteDatabase.execSQL("drop table if exists inventory");
        sQLiteDatabase.execSQL("drop table if exists stock");
        sQLiteDatabase.execSQL("drop table if exists log_management");
        sQLiteDatabase.execSQL("drop table if exists adventure_log");
        sQLiteDatabase.execSQL("drop table if exists fighting_log");
        sQLiteDatabase.execSQL("drop table if exists log_status");
        sQLiteDatabase.execSQL("drop table if exists log_char");
        sQLiteDatabase.execSQL("drop table if exists log_item");
        sQLiteDatabase.execSQL("drop table if exists log_fight");
        sQLiteDatabase.execSQL("drop table if exists log_enemy_char");
        sQLiteDatabase.execSQL("drop table if exists tactics");
        sQLiteDatabase.execSQL("drop table if exists advanced_tactics");
        sQLiteDatabase.execSQL("drop table if exists inventory_result");
        sQLiteDatabase.execSQL("drop table if exists player_char_result");
        sQLiteDatabase.execSQL("drop table if exists game_context_result");
        sQLiteDatabase.execSQL("drop table if exists char_skill_result");
        sQLiteDatabase.execSQL("drop table if exists game_flag_result");
        sQLiteDatabase.execSQL("drop table if exists dungeon_stat_result");
        sQLiteDatabase.execSQL("drop table if exists skill_customization");
    }

    public static void trancateDb(SQLiteDatabase sQLiteDatabase) throws SQLException {
        sQLiteDatabase.execSQL("delete from player_char");
        sQLiteDatabase.execSQL("delete from player_char_bitmap");
        sQLiteDatabase.execSQL("delete from char_skill");
        sQLiteDatabase.execSQL("delete from skill_slot");
        sQLiteDatabase.execSQL("delete from game_context");
        sQLiteDatabase.execSQL("delete from active_char");
        sQLiteDatabase.execSQL("delete from dungeon_stat");
        sQLiteDatabase.execSQL("delete from dungeon_event");
        sQLiteDatabase.execSQL("delete from game_flag");
        sQLiteDatabase.execSQL("delete from inventory");
        sQLiteDatabase.execSQL("delete from stock");
        sQLiteDatabase.execSQL("delete from log_management");
        sQLiteDatabase.execSQL("delete from adventure_log");
        sQLiteDatabase.execSQL("delete from fighting_log");
        sQLiteDatabase.execSQL("delete from log_status");
        sQLiteDatabase.execSQL("delete from log_char");
        sQLiteDatabase.execSQL("delete from log_item");
        sQLiteDatabase.execSQL("delete from log_fight");
        sQLiteDatabase.execSQL("delete from log_enemy_char");
        sQLiteDatabase.execSQL("delete from tactics");
        sQLiteDatabase.execSQL("delete from advanced_tactics");
        sQLiteDatabase.execSQL("delete from inventory_result");
        sQLiteDatabase.execSQL("delete from player_char_result");
        sQLiteDatabase.execSQL("delete from game_context_result");
        sQLiteDatabase.execSQL("delete from char_skill_result");
        sQLiteDatabase.execSQL("delete from game_flag_result");
        sQLiteDatabase.execSQL("delete from dungeon_stat_result");
        sQLiteDatabase.execSQL("delete from skill_customization");
    }

    public static boolean upgradeDb(SQLiteDatabase sQLiteDatabase, int i) throws SQLException {
        if (i < 100) {
            dropDb(sQLiteDatabase);
            createDb(sQLiteDatabase);
            return false;
        }
        if (i <= 100) {
            sQLiteDatabase.execSQL("create table skill_customization (_id integer primary key autoincrement, charId integer, skillId integer, skillName text, skillDesc text)");
        }
        if (i <= 101) {
            sQLiteDatabase.execSQL("create table advanced_tactics (_id integer primary key autoincrement, charId integer, fighting integer, condition text, conditionSub text,  conditionCharId integer, conditionValue integer, action text, actionSub text, target text, targetId integer)");
        }
        if (i <= 102) {
            sQLiteDatabase.execSQL("alter table advanced_tactics add column conditionNot integer");
            ContentValues contentValues = new ContentValues(1);
            contentValues.put("conditionNot", (Integer) 0);
            sQLiteDatabase.update(TABLE_ADVANCED_TACTICS, contentValues, null, null);
        }
        if (i <= 103) {
            sQLiteDatabase.execSQL("alter table advanced_tactics add column targetCharId integer");
            ContentValues contentValues2 = new ContentValues(1);
            contentValues2.put("targetCharId", (Integer) 0);
            sQLiteDatabase.update(TABLE_ADVANCED_TACTICS, contentValues2, null, null);
        }
        if (i <= 104) {
            sQLiteDatabase.execSQL("alter table game_context add column startRealtime integer");
            sQLiteDatabase.execSQL("alter table game_context_result add column startRealtime integer");
            sQLiteDatabase.execSQL("alter table game_context add column returnRealtime integer");
            sQLiteDatabase.execSQL("alter table game_context_result add column returnRealtime integer");
        }
        return true;
    }
}
