package com.shirobakama.autorpg2.adventure;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class FightArgument {
    public boolean isEvent;
    public Boolean isExpected;
    public boolean isWandering;
    public int monsterCount;
    public int monsterId;

    public FightArgument(boolean z, boolean z2, int i, int i2) {
        this.isWandering = z;
        this.isEvent = z2;
        this.monsterId = i;
        this.monsterCount = i2;
    }

    public String toString() {
        return "[" + this.monsterId + "x" + this.monsterCount + ",W:" + this.isWandering + ",Ev:" + this.isEvent + ",Ex:" + this.isExpected + "]";
    }
}
