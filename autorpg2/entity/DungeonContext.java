package com.shirobakama.autorpg2.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.shirobakama.autorpg2.adventure.EngineUtil;
import com.shirobakama.autorpg2.adventure.FlagEngine;
import com.shirobakama.autorpg2.adventure.TownFlagEngine;
import com.shirobakama.autorpg2.entity.DungeonStat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class DungeonContext implements Parcelable {
    public static final Parcelable.Creator<DungeonContext> CREATOR = new Parcelable.Creator<DungeonContext>() { // from class: com.shirobakama.autorpg2.entity.DungeonContext.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DungeonContext[] newArray(int i) {
            return new DungeonContext[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DungeonContext createFromParcel(Parcel parcel) {
            return new DungeonContext(parcel);
        }
    };
    public List<DungeonEvent> events;
    public List<DungeonStat> stats;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DungeonContext() {
        this.stats = new ArrayList(0);
        this.events = new ArrayList(0);
    }

    public DungeonContext(Parcel parcel) {
        this.stats = parcel.createTypedArrayList(DungeonStat.CREATOR);
        this.events = parcel.createTypedArrayList(DungeonEvent.CREATOR);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(this.stats);
        parcel.writeTypedList(this.events);
    }

    public DungeonStat getStat(Dungeon dungeon, int i, int i2) throws IllegalStateException {
        return this.stats.get((((i - 1) * dungeon.block) + i2) - 1);
    }

    public void clear() {
        this.stats.clear();
        this.events.clear();
    }

    public void initialize(Context context, GameContext gameContext, Dungeon dungeon, Random random) {
        DungeonStat dungeonStat;
        this.stats.clear();
        this.events.clear();
        Dungeon dungeonEnhanceExtraDungeonInInitialize = FlagEngine.enhanceExtraDungeonInInitialize(context, gameContext, dungeon);
        for (int i = 0; i < dungeonEnhanceExtraDungeonInInitialize.floor; i++) {
            DungeonStat[] dungeonStatArr = new DungeonStat[dungeonEnhanceExtraDungeonInInitialize.block];
            int i2 = 0;
            while (i2 < dungeonEnhanceExtraDungeonInInitialize.block) {
                DungeonStat dungeonStat2 = new DungeonStat();
                dungeonStat2.dungeonId = dungeonEnhanceExtraDungeonInInitialize.f90id;
                dungeonStat2.floor = i + 1;
                int i3 = i2 + 1;
                dungeonStat2.block = i3;
                dungeonStat2.blockState = DungeonStat.BlockState.NONE;
                dungeonStatArr[i2] = dungeonStat2;
                while (random.nextInt(1000) < dungeonEnhanceExtraDungeonInInitialize.springFactor) {
                    DungeonEvent dungeonEvent = new DungeonEvent();
                    dungeonEvent.floor = dungeonStat2.floor;
                    dungeonEvent.block = dungeonStat2.block;
                    dungeonEvent.position = random.nextInt(100);
                    dungeonEvent.type = DungeonEvent.SPRINGS[random.nextInt(DungeonEvent.SPRINGS.length)];
                    this.events.add(dungeonEvent);
                }
                while (random.nextInt(1000) < dungeonEnhanceExtraDungeonInInitialize.trapFactor) {
                    DungeonEvent dungeonEvent2 = new DungeonEvent();
                    dungeonEvent2.floor = dungeonStat2.floor;
                    dungeonEvent2.block = dungeonStat2.block;
                    dungeonEvent2.position = random.nextInt(100);
                    dungeonEvent2.type = DungeonEvent.TRAPS[random.nextInt(DungeonEvent.TRAPS.length)];
                    this.events.add(dungeonEvent2);
                }
                i2 = i3;
            }
            int randomMinMax = EngineUtil.getRandomMinMax(random, dungeonEnhanceExtraDungeonInInitialize.enemyBlockMin, dungeonEnhanceExtraDungeonInInitialize.enemyBlockMax);
            for (int i4 = 0; i4 < randomMinMax; i4++) {
                do {
                    dungeonStat = dungeonStatArr[random.nextInt(dungeonEnhanceExtraDungeonInInitialize.block)];
                } while (dungeonStat.blockType != null);
                dungeonStat.blockType = DungeonStat.BlockType.MONSTER;
                MonsterPop monsterPopSelectPlacedMonster = dungeonEnhanceExtraDungeonInInitialize.selectPlacedMonster(random, dungeonStat.floor);
                dungeonStat.monsterId = monsterPopSelectPlacedMonster.monsterId;
                dungeonStat.initialMonsterNumber = monsterPopSelectPlacedMonster.getGroupNumber(random);
                dungeonStat.monsterNumber = dungeonStat.initialMonsterNumber;
            }
            for (int i5 = 0; i5 < dungeonEnhanceExtraDungeonInInitialize.block; i5++) {
                DungeonStat dungeonStat3 = dungeonStatArr[i5];
                if (dungeonStat3.blockType == null) {
                    if (random.nextInt(1000) - dungeonEnhanceExtraDungeonInInitialize.safeFactor < 0) {
                        dungeonStat3.blockType = DungeonStat.BlockType.SAFE_ZONE;
                    } else {
                        dungeonStat3.blockType = DungeonStat.BlockType.NORMAL;
                    }
                }
                dungeonStat3.captiveRate = 0;
                this.stats.add(dungeonStat3);
            }
        }
        TownFlagEngine.initializeDungeonContext(random, context, gameContext, dungeonEnhanceExtraDungeonInInitialize, this);
    }

    public List<DungeonEvent> getEventInRange(int i, int i2, int i3, int i4) {
        ArrayList arrayList = new ArrayList(2);
        for (int i5 = 0; i5 < this.events.size(); i5++) {
            DungeonEvent dungeonEvent = this.events.get(i5);
            if (dungeonEvent.floor == i && dungeonEvent.block == i2 && dungeonEvent.position >= i3 && dungeonEvent.position < i4) {
                arrayList.add(dungeonEvent);
            } else if (dungeonEvent.floor > i || (dungeonEvent.floor == i && dungeonEvent.block > i2)) {
                break;
            }
        }
        return arrayList;
    }
}
