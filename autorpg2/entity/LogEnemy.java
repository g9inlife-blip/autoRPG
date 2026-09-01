package com.shirobakama.autorpg2.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class LogEnemy {
    public int enemyIndex;
    public int fightingLogId;

    /* renamed from: hp */
    public int f99hp;

    /* renamed from: id */
    public int f100id;

    /* renamed from: mp */
    public int f101mp;

    public LogEnemy(Enemy enemy) {
        this.enemyIndex = enemy.index;
        this.f99hp = enemy.running ? 0 : enemy.f93hp;
        this.f101mp = enemy.f94mp;
    }

    public LogEnemy() {
    }

    public static boolean equals(List<LogEnemy> list, List<LogEnemy> list2) {
        if (list == null && list2 == null) {
            return true;
        }
        if (list == null || list2 == null || list.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            LogEnemy logEnemy = list.get(i);
            LogEnemy logEnemy2 = list2.get(i);
            if (logEnemy.enemyIndex != logEnemy2.enemyIndex || logEnemy.f99hp != logEnemy2.f99hp || logEnemy.f101mp != logEnemy2.f101mp) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalsToEnemies(List<LogEnemy> list, List<Enemy> list2) {
        if (list == null || list.isEmpty() || list.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            LogEnemy logEnemy = list.get(i);
            Enemy enemy = list2.get(i);
            if (logEnemy.enemyIndex != enemy.index || logEnemy.f99hp != enemy.f93hp || logEnemy.f101mp != enemy.f94mp) {
                return false;
            }
        }
        return true;
    }

    public static List<LogEnemy> fromEnemies(List<Enemy> list) {
        ArrayList arrayList = new ArrayList(list.size());
        Iterator<Enemy> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(new LogEnemy(it.next()));
        }
        return arrayList;
    }
}
