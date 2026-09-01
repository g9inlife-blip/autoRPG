package com.shirobakama.autorpg2.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class AdventureLog extends CommonLog {
    public int gold;
    public LogFight logFight;
    public List<LogInventory> logInventories;
    public int logTime;

    public AdventureLog() {
    }

    public AdventureLog(GameContext gameContext, Calendar calendar) {
        this.gold = gameContext.gold;
        if (calendar != null) {
            this.logTime = calcLogTime(calendar.getTimeInMillis());
        }
    }

    public static class LogInventory {
        public int adventureLogId;
        public int equippedCharId;

        /* renamed from: id */
        public int f84id;
        public int itemId;

        public LogInventory(Inventory inventory) {
            this.equippedCharId = 0;
            this.itemId = inventory.itemId;
            this.equippedCharId = inventory.equippedCharId;
        }

        public LogInventory() {
            this.equippedCharId = 0;
        }

        public int hashCode() {
            return this.itemId;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof LogInventory)) {
                return false;
            }
            LogInventory logInventory = (LogInventory) obj;
            return this.itemId == logInventory.itemId && this.equippedCharId == logInventory.equippedCharId;
        }

        public static boolean equals(List<LogInventory> list, List<LogInventory> list2) {
            if (list == null || list2 == null || list.size() != list2.size()) {
                return false;
            }
            for (int i = 0; i < list.size(); i++) {
                if (!list.get(i).equals(list2.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    public void setLogInventories(List<Inventory> list) {
        this.logInventories = new ArrayList();
        Iterator<Inventory> it = list.iterator();
        while (it.hasNext()) {
            this.logInventories.add(new LogInventory(it.next()));
        }
    }

    public static void copyLogInventoriesForView(List<AdventureLog> list, List<AdventureLog> list2) {
        if (list2.isEmpty()) {
            return;
        }
        if (!list.isEmpty() && list2.get(0).logInventories == null) {
            list2.get(0).logInventories = list.get(list.size() - 1).logInventories;
        }
        List<LogInventory> list3 = null;
        for (AdventureLog adventureLog : list2) {
            List<LogInventory> list4 = adventureLog.logInventories;
            if (list4 != null) {
                list3 = list4;
            } else {
                adventureLog.logInventories = list3;
            }
        }
    }
}
