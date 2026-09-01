package com.shirobakama.autorpg2.entity;

import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public abstract class CommonLog {
    public String desc1;
    public String desc2;

    /* renamed from: id */
    public int f85id;
    public Item item;
    public List<LogChar> logChars;
    public PlayerChar playerChar;
    public String title;
    public LogType type;

    public enum LogType {
        UNDEFINED(C0380R.drawable.alog_icon_undefined),
        USE_ITEM(C0380R.drawable.alog_icon_use_item),
        MAGIC(C0380R.drawable.alog_icon_magic),
        QUEST_CLEAR(C0380R.drawable.alog_icon_quest_clear),
        USE_SKILL(C0380R.drawable.flog_icon_use_skill),
        START(C0380R.drawable.alog_icon_start),
        ENTER_DUNGEON(C0380R.drawable.alog_icon_enter_dungeon),
        ENTER_FLOOR(C0380R.drawable.alog_icon_enter_floor),
        ENTER_BLOCK_NORMAL_OR_UNIDENTIFIED(C0380R.drawable.alog_icon_enter_block_normal),
        ENTER_BLOCK_MONSTER(C0380R.drawable.alog_icon_enter_block_monster, C0380R.drawable.list_item_background_log_danger),
        ENTER_BLOCK_SAFE(C0380R.drawable.alog_icon_enter_block_safe_zone, C0380R.drawable.list_item_background_log_safe),
        RETURN(C0380R.drawable.alog_icon_return, C0380R.drawable.list_item_background_log_danger),
        ENCOUNTER_WIN(C0380R.drawable.alog_icon_encounter_win),
        ENCOUNTER_GOT_ITEM(C0380R.drawable.alog_icon_encounter_got_item),
        ENCOUNTER_RUN(C0380R.drawable.alog_icon_encounter_run, C0380R.drawable.list_item_background_log_warn),
        ENCOUNTER_LOSE(C0380R.drawable.alog_icon_encounter_lose, C0380R.drawable.list_item_background_log_danger),
        ENCOUNTER_EVENT(C0380R.drawable.alog_icon_encounter_event, C0380R.drawable.list_item_background_log_info),
        REST(C0380R.drawable.alog_icon_rest, C0380R.drawable.list_item_background_log_safe),
        TREASURE(C0380R.drawable.alog_icon_treasure, C0380R.drawable.list_item_background_log_info),
        TREASURE_ABANDON(C0380R.drawable.alog_icon_treasure_abandon, C0380R.drawable.list_item_background_log_info),
        SPRING(C0380R.drawable.alog_icon_spring, C0380R.drawable.list_item_background_log_info),
        TRAP(C0380R.drawable.alog_icon_trap, C0380R.drawable.list_item_background_log_danger),
        ADVENTURE_EVENT(C0380R.drawable.alog_icon_event, C0380R.drawable.list_item_background_log_info),
        CLEAR_DUNGEON(C0380R.drawable.alog_icon_clear_dungeon, C0380R.drawable.list_item_background_log_safe),
        COMPLETE(C0380R.drawable.alog_icon_complete, C0380R.drawable.list_item_background_log_safe),
        END(C0380R.drawable.alog_icon_returned, C0380R.drawable.list_item_background_log_safe),
        RETURNED(C0380R.drawable.alog_icon_returned, C0380R.drawable.list_item_background_log_warn),
        DEAD(C0380R.drawable.alog_icon_dead, C0380R.drawable.list_item_background_log_danger),
        FIGHT_ENCOUNTER(C0380R.drawable.flog_icon_encounter),
        ATTACK(C0380R.drawable.flog_icon_attack),
        ATTACK_CRITICAL(C0380R.drawable.flog_icon_attack_critical),
        ATTACK_MISS(C0380R.drawable.flog_icon_attack_miss),
        DEFENSE(C0380R.drawable.flog_icon_defense),
        DEFENSE_CRITICAL(C0380R.drawable.flog_icon_defense_critical),
        DEFENSE_MISS(C0380R.drawable.flog_icon_defense_miss),
        LOSE_ENCHANT(C0380R.drawable.flog_icon_run),
        FIGHT_WIN(C0380R.drawable.flog_icon_fight_win, C0380R.drawable.list_item_background_log_safe),
        FIGHT_LOSE(C0380R.drawable.flog_icon_fight_lose, C0380R.drawable.list_item_background_log_danger),
        FIGHT_RUN(C0380R.drawable.flog_icon_run),
        FIGHT_RUN_FAILED(C0380R.drawable.flog_icon_run_failed),
        FIGHT_ENEMY_RUN(C0380R.drawable.flog_icon_run),
        FIGHT_ENEMY_RUN_FAILED(C0380R.drawable.flog_icon_run_failed),
        LEVEL_UP(C0380R.drawable.flog_icon_level_up),
        PLAYER_DEAD(C0380R.drawable.flog_icon_fight_lose, C0380R.drawable.list_item_background_log_danger),
        ENEMY_DEAD(C0380R.drawable.flog_icon_attack_critical, C0380R.drawable.list_item_background_log_safe),
        ITEM_DROP(C0380R.drawable.alog_icon_encounter_got_item),
        SURPRISE(C0380R.drawable.flog_icon_surprise, C0380R.drawable.list_item_background_log_info),
        FIGHT_EVENT(C0380R.drawable.flog_icon_event, C0380R.drawable.list_item_background_log_info),
        FIGHT_RESIST(C0380R.drawable.flog_icon_resist),
        FIGHT_NONE(C0380R.drawable.flog_icon_defense_miss),
        FIGHT_SKILL_ADD_ATTACK(C0380R.drawable.flog_icon_add_attack),
        FIGHT_ENEMY_BREATH(C0380R.drawable.flog_icon_breath),
        FIGHT_USE_SKILL(C0380R.drawable.flog_icon_use_skill),
        ENCOUNTER_WIN_MONSTER_BLOCK(C0380R.drawable.alog_icon_encounter_win_monster_block);

        public int backgroundDrawableId;
        public int drawableId;

        LogType(int i) {
            this.backgroundDrawableId = 0;
            this.drawableId = i;
        }

        LogType(int i, int i2) {
            this.backgroundDrawableId = 0;
            this.drawableId = i;
            this.backgroundDrawableId = i2;
        }
    }

    public static class LogChar {
        public int adventureLogId;
        public int charId;
        public int exp;
        public int fightingLogId;

        /* renamed from: hp */
        public int f86hp;

        /* renamed from: id */
        public int f87id;
        public int level;
        public int maxHp;
        public int maxMp;

        /* renamed from: mp */
        public int f88mp;

        public LogChar(PlayerChar playerChar) {
            this.charId = playerChar.f106id;
            this.f86hp = playerChar.f93hp;
            this.maxHp = playerChar.maxHp;
            this.f88mp = playerChar.f94mp;
            this.maxMp = playerChar.maxMp;
            this.exp = playerChar.exp;
            this.level = playerChar.level;
        }

        public LogChar() {
        }

        public static boolean equals(List<LogChar> list, List<LogChar> list2) {
            if (list == null && list2 == null) {
                return true;
            }
            if (list == null || list2 == null || list.size() != list2.size()) {
                return false;
            }
            for (int i = 0; i < list.size(); i++) {
                LogChar logChar = list.get(i);
                LogChar logChar2 = list2.get(i);
                if (logChar.charId != logChar2.charId || logChar.exp != logChar2.exp || logChar.f86hp != logChar2.f86hp || logChar.maxHp != logChar2.maxHp || logChar.f88mp != logChar2.f88mp || logChar.maxMp != logChar2.maxMp || logChar.level != logChar2.level) {
                    return false;
                }
            }
            return true;
        }

        public static boolean equalsToPCs(List<LogChar> list, List<PlayerChar> list2) {
            if (list == null || list.isEmpty() || list.size() != list2.size()) {
                return false;
            }
            for (int i = 0; i < list.size(); i++) {
                LogChar logChar = list.get(i);
                PlayerChar playerChar = list2.get(i);
                if (logChar.charId != playerChar.f106id || logChar.exp != playerChar.exp || logChar.f86hp != playerChar.f93hp || logChar.maxHp != playerChar.maxHp || logChar.f88mp != playerChar.f94mp || logChar.maxMp != playerChar.maxMp || logChar.level != playerChar.level) {
                    return false;
                }
            }
            return true;
        }

        public static List<LogChar> fromPlayerChar(List<PlayerChar> list) {
            ArrayList arrayList = new ArrayList(list.size());
            Iterator<PlayerChar> it = list.iterator();
            while (it.hasNext()) {
                arrayList.add(new LogChar(it.next()));
            }
            return arrayList;
        }
    }

    public static int calcLogTime(long j) {
        return (int) (j / 60000);
    }

    public static Date getDateFromLogTime(int i) {
        return new Date(i * 60000);
    }

    public void setLogCharacters(List<PlayerChar> list) {
        this.logChars = new ArrayList(list.size());
        Iterator<PlayerChar> it = list.iterator();
        while (it.hasNext()) {
            this.logChars.add(new LogChar(it.next()));
        }
    }

    public void setLogCharacters(GameChar[] gameCharArr) {
        this.logChars = new ArrayList(gameCharArr.length);
        for (GameChar gameChar : gameCharArr) {
            this.logChars.add(new LogChar((PlayerChar) gameChar));
        }
    }

    public String toString() {
        return "[CommonLog:" + this.f85id + "," + this.type + "," + this.item + "," + this.playerChar + "," + this.title + "," + this.desc1 + "," + this.desc2 + "]";
    }

    public static void copyLogCharsForView(List<? extends CommonLog> list, List<? extends CommonLog> list2) {
        if (list2.isEmpty()) {
            return;
        }
        if (list != null && !list.isEmpty() && list2.get(0).logChars == null) {
            list2.get(0).logChars = list.get(list.size() - 1).logChars;
        }
        List<LogChar> list3 = null;
        for (CommonLog commonLog : list2) {
            List<LogChar> list4 = commonLog.logChars;
            if (list4 != null) {
                list3 = list4;
            } else {
                commonLog.logChars = list3;
            }
        }
    }
}
