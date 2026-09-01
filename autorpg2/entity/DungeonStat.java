package com.shirobakama.autorpg2.entity;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class DungeonStat implements Parcelable {
    public static final Parcelable.Creator<DungeonStat> CREATOR = new Parcelable.Creator<DungeonStat>() { // from class: com.shirobakama.autorpg2.entity.DungeonStat.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DungeonStat[] newArray(int i) {
            return new DungeonStat[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DungeonStat createFromParcel(Parcel parcel) {
            return new DungeonStat(parcel);
        }
    };
    public static final int INITIAL_CAPTIVE_RATE = 0;
    public static final int MAX_CAPTIVE_RATE = 100;
    public int block;
    public BlockState blockState;
    public BlockType blockType;
    public int captiveRate;
    public int dungeonId;
    public int floor;

    /* renamed from: id */
    public int f92id;
    public int initialMonsterNumber;
    public transient boolean knowBlockType;
    public int monsterId;
    public int monsterId2;
    public int monsterId3;
    public int monsterNumber;

    public enum BlockState {
        NONE,
        CONQUERING,
        CONQUERED
    }

    public enum BlockType {
        NORMAL,
        MONSTER,
        SAFE_ZONE
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DungeonStat() {
        this.f92id = 0;
        this.dungeonId = 0;
    }

    public DungeonStat(Parcel parcel) {
        this.f92id = 0;
        this.dungeonId = 0;
        this.f92id = parcel.readInt();
        this.dungeonId = parcel.readInt();
        this.floor = parcel.readInt();
        this.block = parcel.readInt();
        this.blockType = BlockType.values()[parcel.readInt()];
        this.blockState = BlockState.values()[parcel.readInt()];
        this.monsterId = parcel.readInt();
        this.monsterId2 = parcel.readInt();
        this.monsterId3 = parcel.readInt();
        this.monsterNumber = parcel.readInt();
        this.initialMonsterNumber = parcel.readInt();
        this.captiveRate = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.f92id);
        parcel.writeInt(this.dungeonId);
        parcel.writeInt(this.floor);
        parcel.writeInt(this.block);
        parcel.writeInt(this.blockType.ordinal());
        parcel.writeInt(this.blockState.ordinal());
        parcel.writeInt(this.monsterId);
        parcel.writeInt(this.monsterId2);
        parcel.writeInt(this.monsterId3);
        parcel.writeInt(this.monsterNumber);
        parcel.writeInt(this.initialMonsterNumber);
        parcel.writeInt(this.captiveRate);
    }

    public boolean isConqured() {
        return this.captiveRate >= 100;
    }

    public boolean isConquringOrConqured() {
        return this.captiveRate > 0;
    }

    public String toString() {
        return "[" + this.f92id + ",F:" + this.floor + ",B:" + this.block + "@" + this.dungeonId + ",Ty:" + this.blockType + ",St:" + this.blockState + ",Mo:" + this.monsterId + "-" + this.monsterId2 + "-" + this.monsterId3 + "-" + this.monsterNumber + "/" + this.initialMonsterNumber + ",Cap:" + this.captiveRate + "]";
    }
}
