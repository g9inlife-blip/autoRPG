package com.shirobakama.autorpg2.repo;

import android.content.Context;
import com.shirobakama.autorpg2.entity.AttrType;
import com.shirobakama.autorpg2.entity.Monster;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public final class MonsterRepository {
    private static Map<String, Monster> monsterForSymbol;
    public static Map<Integer, Monster> monsters;

    private MonsterRepository() {
    }

    public static void initialize(Context context) {
        if (monsters == null) {
            monsters = new TreeMap();
            MonsterDb.initialize(context);
        }
        for (Monster monster : monsters.values()) {
            monster.name = context.getResources().getString(monster.nameStringId);
            if (monster.type == Monster.MonsterType.MAGICAL) {
                setResistType(monster.f105id, AttrType.SLEEP);
            } else if (monster.type == Monster.MonsterType.SLIME) {
                setResistType(monster.f105id, AttrType.SLEEP);
            } else if (monster.type == Monster.MonsterType.UNDEAD) {
                setResistType(monster.f105id, AttrType.SLEEP);
                setResistType(monster.f105id, AttrType.DEATH);
            }
        }
    }

    static void addMonster(Monster monster) {
        monsters.put(Integer.valueOf(monster.f105id), monster);
    }

    static void setResistType(int i, AttrType attrType) {
        Monster monster = monsters.get(Integer.valueOf(i));
        if (monster.resistTypes == null) {
            monster.resistTypes = new boolean[AttrType.VALUES.length];
        }
        monster.resistTypes[attrType.ordinal()] = true;
    }

    static void setWeakType(int i, AttrType attrType) {
        Monster monster = monsters.get(Integer.valueOf(i));
        if (monster.weakTypes == null) {
            monster.weakTypes = new boolean[AttrType.VALUES.length];
        }
        monster.weakTypes[attrType.ordinal()] = true;
    }

    static void setImmuneType(int i, AttrType attrType) {
        Monster monster = monsters.get(Integer.valueOf(i));
        if (monster.immuneTypes == null) {
            monster.immuneTypes = new boolean[AttrType.VALUES.length];
        }
        monster.immuneTypes[attrType.ordinal()] = true;
    }

    public static Monster getMonster(Context context, int i) {
        if (monsters == null) {
            initialize(context);
        }
        return monsters.get(Integer.valueOf(i));
    }

    public static Monster getMonsterBySymbol(Context context, String str) {
        if (monsterForSymbol == null) {
            if (monsters == null) {
                initialize(context);
            }
            monsterForSymbol = new HashMap();
            for (Monster monster : monsters.values()) {
                monsterForSymbol.put(monster.symbol, monster);
            }
        }
        return monsterForSymbol.get(str);
    }

    public static void flush() {
        monsters = null;
    }
}
