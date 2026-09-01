package com.shirobakama.autorpg2.entity;

import com.shirobakama.autorpg2.entity.DungeonEvent;
import java.util.List;
import java.util.Random;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class Dungeon {
    public static final int MAX_BLOCKS = 12;
    public static final int MAX_FLOORS = 20;
    public static final int TYPE_FACTOR = 1000;
    public int block;
    public int descStringId;
    public int difficulty;
    public int difficultyStars;
    public int enemyBlockMax;
    public int enemyBlockMin;
    public int enemyFactor;
    public int floor;
    public int[] floorDescStringIds;
    public List<MonsterPop[]> floorPlacedMonster;
    public List<MonsterPop[]> floorRandomMonster;

    /* renamed from: id */
    public int f90id;
    public String name;
    public int nameStringId;
    public int safeFactor;
    public int springFactor;
    public DungeonEvent.EventSubType springSubType;
    public String symbol;
    public int trapFactor;
    public int treasureFactor;
    public int treasureTrapFactor;

    public MonsterPop selectPlacedMonster(Random random, int i) {
        return selectMonster(random, this.floorPlacedMonster, i);
    }

    public MonsterPop selectRandomMonster(Random random, int i) {
        return selectMonster(random, this.floorRandomMonster, i);
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    private MonsterPop selectMonster(Random random, List<MonsterPop[]> list, int i) {
        MonsterPop[] monsterPopArr = list.get(i - 1);
        int iNextInt = random.nextInt(1000);
        for (int i2 = 0; i2 < monsterPopArr.length; i2++) {
            iNextInt -= monsterPopArr[i2].factor;
            if (iNextInt < 0) {
                return monsterPopArr[i2];
            }
        }
        throw new IllegalStateException("Illegal monster data for dungeon:" + this.f90id + ",floor:" + i);
    }

    public MonsterPop getPlacedMonsterInfo(int i, int i2) {
        for (MonsterPop monsterPop : this.floorPlacedMonster.get(i - 1)) {
            if (monsterPop.monsterId == i2) {
                return monsterPop;
            }
        }
        return null;
    }

    public Dungeon copy() {
        Dungeon dungeon = new Dungeon();
        dungeon.f90id = this.f90id;
        dungeon.symbol = this.symbol;
        dungeon.nameStringId = this.nameStringId;
        dungeon.name = this.name;
        dungeon.difficultyStars = this.difficultyStars;
        dungeon.floor = this.floor;
        dungeon.block = this.block;
        dungeon.difficulty = this.difficulty;
        dungeon.enemyBlockMin = this.enemyBlockMin;
        dungeon.enemyBlockMax = this.enemyBlockMax;
        dungeon.enemyFactor = this.enemyFactor;
        dungeon.treasureFactor = this.treasureFactor;
        dungeon.safeFactor = this.safeFactor;
        dungeon.springFactor = this.springFactor;
        dungeon.springSubType = this.springSubType;
        dungeon.trapFactor = this.trapFactor;
        dungeon.treasureTrapFactor = this.treasureTrapFactor;
        dungeon.descStringId = this.descStringId;
        dungeon.floorDescStringIds = this.floorDescStringIds;
        dungeon.floorPlacedMonster = this.floorPlacedMonster;
        dungeon.floorRandomMonster = this.floorRandomMonster;
        return dungeon;
    }
}
