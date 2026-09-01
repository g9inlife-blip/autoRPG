package com.shirobakama.autorpg2.entity;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Comparator;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class Tactics implements Parcelable {
    public TacticsValue abort;
    public TacticsValue attackSkill;
    public int charId;
    public TacticsValue cureSkill;
    public TacticsValue damageSkill;
    public boolean enabled;
    public FullInventoryTactics fullInventory;

    /* renamed from: id */
    public int f110id;
    public TacticsValue item;
    public TacticsValue rest;
    public TacticsValue running;
    public TacticsValue statusSkill;
    public int targetFloor;
    public int useItemBlock;
    public int useItemFloor;
    public int useItemId;
    public static Comparator<Tactics> FLOOR_COMPARATOR = new Comparator<Tactics>() { // from class: com.shirobakama.autorpg2.entity.Tactics.1
        @Override // java.util.Comparator
        public int compare(Tactics tactics, Tactics tactics2) {
            int i = tactics.targetFloor - tactics2.targetFloor;
            return i == 0 ? tactics2.f110id - tactics.f110id : i;
        }
    };
    public static final Parcelable.Creator<Tactics> CREATOR = new Parcelable.Creator<Tactics>() { // from class: com.shirobakama.autorpg2.entity.Tactics.2
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Tactics[] newArray(int i) {
            return new Tactics[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Tactics createFromParcel(Parcel parcel) {
            return new Tactics(parcel);
        }
    };

    public enum FullInventoryTactics {
        DROP_CHEAPEST,
        RETURN,
        DROP_CONSUMABLE,
        HOLD_ENCHANTED
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public enum TacticsValue {
        NONE(0),
        CONSERVATIVE(1),
        MODERATE(2),
        AGGRESSIVE(3);

        private int mParameter;

        TacticsValue(int i) {
            this.mParameter = i;
        }

        public int getParameter() {
            return this.mParameter;
        }

        public TacticsValue composite(TacticsValue tacticsValue) {
            return ordinal() <= tacticsValue.ordinal() ? this : tacticsValue;
        }

        public TacticsValue decrementIfLesser(TacticsValue tacticsValue) {
            if (ordinal() <= tacticsValue.ordinal()) {
                return this;
            }
            if (this == AGGRESSIVE) {
                return MODERATE;
            }
            if (this == MODERATE) {
                return CONSERVATIVE;
            }
            return NONE;
        }
    }

    public Tactics() {
        this.f110id = 0;
        this.charId = 0;
        this.enabled = false;
        this.targetFloor = 0;
        this.useItemFloor = 0;
        this.useItemBlock = 0;
        this.useItemId = 0;
    }

    public Tactics(Parcel parcel) {
        this.f110id = 0;
        this.charId = 0;
        this.enabled = false;
        this.targetFloor = 0;
        this.useItemFloor = 0;
        this.useItemBlock = 0;
        this.useItemId = 0;
        this.f110id = parcel.readInt();
        this.charId = parcel.readInt();
        this.enabled = parcel.readInt() != 0;
        this.targetFloor = parcel.readInt();
        this.abort = TacticsValue.values()[parcel.readInt()];
        this.running = TacticsValue.values()[parcel.readInt()];
        this.attackSkill = TacticsValue.values()[parcel.readInt()];
        this.statusSkill = TacticsValue.values()[parcel.readInt()];
        this.cureSkill = TacticsValue.values()[parcel.readInt()];
        this.damageSkill = TacticsValue.values()[parcel.readInt()];
        this.item = TacticsValue.values()[parcel.readInt()];
        this.rest = TacticsValue.values()[parcel.readInt()];
        this.fullInventory = FullInventoryTactics.values()[parcel.readInt()];
        this.useItemFloor = parcel.readInt();
        this.useItemBlock = parcel.readInt();
        this.useItemId = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.f110id);
        parcel.writeInt(this.charId);
        parcel.writeInt(this.enabled ? 1 : 0);
        parcel.writeInt(this.targetFloor);
        parcel.writeInt(this.abort.ordinal());
        parcel.writeInt(this.running.ordinal());
        parcel.writeInt(this.attackSkill.ordinal());
        parcel.writeInt(this.statusSkill.ordinal());
        parcel.writeInt(this.cureSkill.ordinal());
        parcel.writeInt(this.damageSkill.ordinal());
        parcel.writeInt(this.item.ordinal());
        parcel.writeInt(this.rest.ordinal());
        parcel.writeInt(this.fullInventory.ordinal());
        parcel.writeInt(this.useItemFloor);
        parcel.writeInt(this.useItemBlock);
        parcel.writeInt(this.useItemId);
    }

    public static Tactics normal() {
        Tactics tactics = new Tactics();
        tactics.abort = TacticsValue.MODERATE;
        tactics.running = TacticsValue.MODERATE;
        tactics.attackSkill = TacticsValue.MODERATE;
        tactics.statusSkill = TacticsValue.MODERATE;
        tactics.cureSkill = TacticsValue.MODERATE;
        tactics.damageSkill = TacticsValue.MODERATE;
        tactics.item = TacticsValue.MODERATE;
        tactics.rest = TacticsValue.MODERATE;
        tactics.fullInventory = FullInventoryTactics.DROP_CHEAPEST;
        return tactics;
    }

    public String toString() {
        return "[Tactics: a:" + this.abort + ",r:" + this.running + ",as:" + this.attackSkill + ",ss:" + this.statusSkill + ",cs:" + this.cureSkill + ",ds:" + this.damageSkill + ",i:" + this.item + ",r:" + this.rest + ",fi:" + this.fullInventory + ",iu:" + this.useItemFloor + "-" + this.useItemBlock + "-" + this.useItemId + "]";
    }
}
