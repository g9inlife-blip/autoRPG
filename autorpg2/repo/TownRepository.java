package com.shirobakama.autorpg2.repo;

import android.content.Context;
import com.shirobakama.autorpg2.entity.Town;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public final class TownRepository {
    public static Map<Integer, Town> towns;

    private TownRepository() {
    }

    private static void initialize(Context context) {
        towns = new TreeMap();
        TownDb.initialize();
        for (Town town : towns.values()) {
            town.name = context.getString(town.nameStringId);
        }
    }

    static void addTown(Town town) {
        towns.put(Integer.valueOf(town.f111id), town);
    }

    public static Town getTown(Context context, int i) {
        if (towns == null) {
            initialize(context);
        }
        return towns.get(Integer.valueOf(i));
    }

    public static void flush() {
        towns = null;
    }
}
