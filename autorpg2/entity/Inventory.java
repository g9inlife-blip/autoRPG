package com.shirobakama.autorpg2.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.autorpg2.entity.ItemObject;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class Inventory extends ItemObject implements Parcelable {
    public static final Parcelable.Creator<Inventory> CREATOR = new Parcelable.Creator<Inventory>() { // from class: com.shirobakama.autorpg2.entity.Inventory.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Inventory[] newArray(int i) {
            return new Inventory[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Inventory createFromParcel(Parcel parcel) {
            return new Inventory(parcel);
        }
    };
    public transient ItemObject.AttrStatAdjustments adjustments;
    public transient int equippedCharId;

    public enum InventoryType {
        WEAPON,
        ARMOR,
        SHIELD,
        RING
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Inventory() {
    }

    public Inventory(Parcel parcel) {
        super(parcel);
    }

    public Inventory(ItemObject itemObject) {
        super(itemObject);
    }

    @Override // com.shirobakama.autorpg2.entity.ItemObject, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
    }

    public int getDamageRatioForMonster(Item item, Monster monster) {
        if (item.effects.isEmpty()) {
            return 1;
        }
        for (Item.Effect effect : item.effects) {
            if (effect.type == Item.Effect.Type.KILLER && (effect.monsterId == monster.f105id || effect.monsterType == monster.type)) {
                return effect.value;
            }
        }
        return 1;
    }

    public boolean isEventItem(Context context) {
        return getBaseItem(context).isEventItem();
    }
}
