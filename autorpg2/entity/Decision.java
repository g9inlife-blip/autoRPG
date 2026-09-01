package com.shirobakama.autorpg2.entity;

import com.shirobakama.autorpg2.entity.GameFlag;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public final class Decision {

    public enum ConditionType {
        DUNGEON,
        FLOOR,
        TOWN,
        NOT_DUNGEON,
        NOT_FLOOR,
        NOT_TOWN,
        MONSTER,
        ITEM,
        RANDOM,
        FLAG_VALUE
    }

    public enum ResultType {
        DUNGEON_AVAILABLE,
        TOWN_AVAILABLE,
        GET_ITEM,
        LOST_ITEM,
        FLAG_ON,
        FLAG_OFF,
        FLAG_INCREMENT,
        FLAG_ON_INCREMENT,
        ADD_LOG,
        SHOW_MESSAGE,
        END_ADVENTURE,
        MOVE_TO_DUNGEON,
        MOVE_TO_FLOOR,
        MOVE_TO_BLOCK,
        MOVE_TO_TOWN,
        ADD_ENCHANT,
        ENCOUNTER,
        TREASURE_BOX,
        TRAP
    }

    public enum Timing {
        AVAILABLE_DUNGEONS,
        AVAILABLE_TOWNS,
        START_ADVENTURE,
        START_DUNGEON,
        START_FLOOR,
        START_BLOCK,
        START_BLOCK_ADDITIONAL_LOG,
        USE_ITEM_IN_BLOCK,
        CLEAR_FLOOR,
        CLEAR_DUNGEON,
        END_DUNGEON,
        ENTER_TOWN,
        HEARING,
        GET_ITEM,
        ADVENTURING,
        WIN_MONSTER,
        LOSE_MONSTER,
        FIGHTING_START_FIGHT,
        FIGHTING_WIN_MONSTER,
        FIGHTING_LOSE_MONSTER
    }

    public static class Result {
        public Enchant enchant;
        public int enchantIndex;
        public String flagName;
        public GameFlag.FlagType flagType;

        /* renamed from: id */
        public int f89id;
        public CommonLog log;
        public String message;
        public int otherKey;
        public int otherValue;
        public ResultType type;

        public String toString() {
            return "[" + this.type + "," + this.f89id + "," + this.flagType + "," + this.flagName + "," + this.message + "," + this.log + "," + this.otherKey + "," + this.otherValue + "," + this.enchant + "]";
        }

        public static Result forFlagOnIncrement(GameFlag.Key key) {
            Result result = new Result();
            result.type = ResultType.FLAG_ON_INCREMENT;
            result.flagType = key.type;
            result.flagName = key.name;
            return result;
        }

        public static Result forAddLog(CommonLog commonLog) {
            Result result = new Result();
            result.type = ResultType.ADD_LOG;
            result.log = commonLog;
            return result;
        }

        public static Result forFlagOn(GameFlag.Key key) {
            Result result = new Result();
            result.type = ResultType.FLAG_ON;
            result.flagType = key.type;
            result.flagName = key.name;
            return result;
        }

        public static Result forGetItem(Item item) {
            Result result = new Result();
            result.type = ResultType.GET_ITEM;
            result.f89id = item.f97id;
            return result;
        }

        public static Result forEncounter(int i, int i2) {
            Result result = new Result();
            result.type = ResultType.ENCOUNTER;
            result.f89id = i;
            result.otherValue = i2;
            return result;
        }
    }

    private Decision() {
    }
}
