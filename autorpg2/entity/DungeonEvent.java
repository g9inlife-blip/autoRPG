package com.shirobakama.autorpg2.entity;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class DungeonEvent implements Parcelable {
    public int block;
    public int dungeonId;
    public int floor;

    /* renamed from: id */
    public int f91id;
    public int position;
    public EventType type;
    public static final EventType[] SPRINGS = {EventType.SPRING_NONE, EventType.SPRING_CURE, EventType.SPRING_CURE_ALL, EventType.SPRING_POISON, EventType.SPRING_CURE_MP, EventType.SPRING_CURE_MP_ALL};
    public static final EventType[] TRAPS = {EventType.TRAP_BOW, EventType.TRAP_POISON, EventType.TRAP_ALARM};
    public static final Parcelable.Creator<DungeonEvent> CREATOR = new Parcelable.Creator<DungeonEvent>() { // from class: com.shirobakama.autorpg2.entity.DungeonEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DungeonEvent[] newArray(int i) {
            return new DungeonEvent[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DungeonEvent createFromParcel(Parcel parcel) {
            return new DungeonEvent(parcel);
        }
    };

    public enum EventSubType {
        SPRING,
        CUP_BOARD,
        RACK
    }

    public enum EventType {
        SPRING_NONE,
        SPRING_CURE,
        SPRING_CURE_ALL,
        SPRING_POISON,
        SPRING_CURE_MP,
        SPRING_CURE_MP_ALL,
        TRAP_BOW,
        TRAP_POISON,
        TRAP_ALARM
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DungeonEvent() {
        this.f91id = 0;
        this.dungeonId = 0;
    }

    public DungeonEvent(Parcel parcel) {
        this.f91id = 0;
        this.dungeonId = 0;
        this.f91id = parcel.readInt();
        this.dungeonId = parcel.readInt();
        this.floor = parcel.readInt();
        this.block = parcel.readInt();
        this.position = parcel.readInt();
        this.type = EventType.values()[parcel.readInt()];
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.f91id);
        parcel.writeInt(this.dungeonId);
        parcel.writeInt(this.floor);
        parcel.writeInt(this.block);
        parcel.writeInt(this.position);
        parcel.writeInt(this.type.ordinal());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DungeonEvent)) {
            return false;
        }
        DungeonEvent dungeonEvent = (DungeonEvent) obj;
        return this.dungeonId == dungeonEvent.dungeonId && this.floor == dungeonEvent.floor && this.block == dungeonEvent.block && this.position == dungeonEvent.position && this.type == dungeonEvent.type;
    }

    public int hashCode() {
        return (((((((this.dungeonId * 31) + this.floor) * 31) + this.block) * 31) + this.position) * 31) + this.type.ordinal();
    }

    public String toString() {
        return "[" + this.f91id + "," + this.dungeonId + "," + this.floor + "," + this.block + "," + this.position + "," + this.type + "]";
    }
}
