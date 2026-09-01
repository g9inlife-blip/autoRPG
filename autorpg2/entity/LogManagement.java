package com.shirobakama.autorpg2.entity;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class LogManagement implements Parcelable {
    public static final Parcelable.Creator<LogManagement> CREATOR = new Parcelable.Creator<LogManagement>() { // from class: com.shirobakama.autorpg2.entity.LogManagement.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LogManagement[] newArray(int i) {
            return new LogManagement[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LogManagement createFromParcel(Parcel parcel) {
            return new LogManagement(parcel);
        }
    };
    public boolean completed;
    public int dungeonId;

    /* renamed from: id */
    public int f103id;
    public int[] pcId;
    public String[] pcName;
    public int targetFloor;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public LogManagement() {
        this.f103id = 0;
        this.pcId = new int[3];
        this.pcName = new String[3];
    }

    public LogManagement(List<PlayerChar> list) {
        this.f103id = 0;
        this.pcId = new int[3];
        this.pcName = new String[3];
        for (int i = 0; i < this.pcId.length; i++) {
            if (i < list.size()) {
                this.pcId[i] = list.get(i).f106id;
                this.pcName[i] = list.get(i).name;
            } else {
                this.pcId[i] = 0;
                this.pcName[i] = null;
            }
        }
    }

    public LogManagement(Parcel parcel) {
        this.f103id = 0;
        this.pcId = new int[3];
        this.pcName = new String[3];
        this.f103id = parcel.readInt();
        parcel.readIntArray(this.pcId);
        parcel.readStringArray(this.pcName);
        this.dungeonId = parcel.readInt();
        this.targetFloor = parcel.readInt();
        this.completed = parcel.readInt() != 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.f103id);
        parcel.writeIntArray(this.pcId);
        parcel.writeStringArray(this.pcName);
        parcel.writeInt(this.dungeonId);
        parcel.writeInt(this.targetFloor);
        parcel.writeInt(this.completed ? 1 : 0);
    }
}
