package com.shirobakama.autorpg2.repo;

import android.content.Context;
import com.shirobakama.autorpg2.entity.Dungeon;
import com.shirobakama.autorpg2.entity.MonsterPop;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public final class DungeonRepository {
    public static Map<Integer, Dungeon> dungeons;

    private DungeonRepository() {
    }

    private static void initialize(Context context) {
        dungeons = new TreeMap();
        DungeonDb.initialize(context);
        for (Dungeon dungeon : dungeons.values()) {
            dungeon.name = context.getString(dungeon.nameStringId);
        }
    }

    static void addDungeon(Dungeon dungeon) {
        dungeons.put(Integer.valueOf(dungeon.f90id), dungeon);
    }

    public static Map<Integer, Dungeon> getDungeons(Context context) {
        if (dungeons == null) {
            initialize(context);
        }
        return dungeons;
    }

    public static Dungeon getDungeon(Context context, int i) {
        if (dungeons == null) {
            initialize(context);
        }
        return dungeons.get(Integer.valueOf(i));
    }

    static void addPlacedMonster(int i, int i2, MonsterPop[] monsterPopArr) {
        Dungeon dungeon = dungeons.get(Integer.valueOf(i));
        if (dungeon.floorPlacedMonster == null) {
            dungeon.floorPlacedMonster = new ArrayList(dungeon.floor);
        }
        if (dungeon.floorPlacedMonster.size() != i2 - 1) {
            throw new IllegalStateException("Illegal placed monster data:" + i + "," + i2);
        }
        dungeon.floorPlacedMonster.add(monsterPopArr);
    }

    static void addRandomMonster(int i, int i2, MonsterPop[] monsterPopArr) {
        Dungeon dungeon = dungeons.get(Integer.valueOf(i));
        if (dungeon.floorRandomMonster == null) {
            dungeon.floorRandomMonster = new ArrayList(dungeon.floor);
        }
        if (dungeon.floorRandomMonster.size() != i2 - 1) {
            throw new IllegalStateException("Illegal random monster data:" + i + "," + i2);
        }
        dungeon.floorRandomMonster.add(monsterPopArr);
    }

    public static void flush() {
        dungeons = null;
    }
}
