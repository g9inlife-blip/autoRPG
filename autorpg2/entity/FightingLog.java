package com.shirobakama.autorpg2.entity;

import com.shirobakama.autorpg2.entity.CommonLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class FightingLog extends CommonLog {
    public int adventureLogId;
    public int enemyIndex = -1;
    public List<LogEnemy> logEnemies;
    public LogFight logFight;
    public boolean playersAct;
    public transient FightingLog previousLog;
    public int[] targetIds;
    public boolean toPlayer;

    public void setLogEnemies(List<Enemy> list) {
        this.logEnemies = new ArrayList(list.size());
        Iterator<Enemy> it = list.iterator();
        while (it.hasNext()) {
            this.logEnemies.add(new LogEnemy(it.next()));
        }
    }

    public static void copyLogCharsForView(List<FightingLog> list) {
        if (list.isEmpty()) {
            return;
        }
        List<CommonLog.LogChar> list2 = null;
        List<LogEnemy> list3 = null;
        for (FightingLog fightingLog : list) {
            if (fightingLog.logChars != null) {
                list2 = fightingLog.logChars;
            } else {
                fightingLog.logChars = list2;
            }
            List<LogEnemy> list4 = fightingLog.logEnemies;
            if (list4 != null) {
                list3 = list4;
            } else {
                fightingLog.logEnemies = list3;
            }
        }
    }
}
