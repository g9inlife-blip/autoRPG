package com.shirobakama.autorpg2.entity;

import com.shirobakama.autorpg2.adventure.EngineUtil;
import java.util.Random;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class MonsterPop {
    public static final int FACTOR = 1000;
    public int factor;
    public int groupMax;
    public int groupMin;
    public int monsterId;
    public int partyMax;
    public int partyMin;

    public MonsterPop(int i, int i2, int i3, int i4) {
        this(i, i2, 0, 0, i3, i4);
    }

    public MonsterPop(int i, int i2, int i3, int i4, int i5, int i6) {
        this.factor = i2;
        this.monsterId = i;
        this.groupMin = i3;
        this.groupMax = i4;
        this.partyMin = i5;
        this.partyMax = i6;
    }

    public int getPartyMemberNumber(Random random) {
        return EngineUtil.getRandomMinMax(random, this.partyMin, this.partyMax);
    }

    public int getGroupNumber(Random random) {
        return EngineUtil.getRandomMinMax(random, this.groupMin, this.groupMax);
    }
}
