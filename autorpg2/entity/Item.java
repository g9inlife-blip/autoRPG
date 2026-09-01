package com.shirobakama.autorpg2.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.shirobakama.autorpg2.adventure.TownFlagEngine;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.Monster;
import com.shirobakama.autorpg2.util.TypeUtil;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class Item {
    public static final int RARITY_COMMON = 1;
    public static final int RARITY_RARE = 3;
    public static final int RARITY_UNCOMMON = 2;
    public int attrBase;
    public boolean[] classEquipable;
    public int diceFace;
    public int diceNum;
    public int drawableId;

    /* renamed from: id */
    public int f97id;
    public String name;
    public int nameStringId;
    public transient int number;
    public int price;
    public String symbol;
    public Type type;
    public WeaponType weaponType;
    public int descriptionStringId = -1;
    public boolean artifact = false;
    public boolean equipable = false;
    public boolean twoHanded = false;
    public boolean longRange = false;
    public boolean useableInTown = false;
    public List<Effect> effects = new ArrayList(0);
    private transient Effect mHpRestoreEffect = null;
    private transient Effect mMpRestoreEffect = null;

    public enum Type {
        CONSUMABLE,
        WEAPON,
        ARMOR,
        SHIELD,
        RING,
        OTHER
    }

    public enum WeaponType {
        SWORD(C0380R.string.res_weapon_type_sword),
        SPEAR(C0380R.string.res_weapon_type_spear),
        MACE(C0380R.string.res_weapon_type_mace),
        MISSILE(C0380R.string.res_weapon_type_missile);

        private int mStrId;
        private String mString;

        WeaponType(int i) {
            this.mStrId = i;
        }

        public String getString(Context context) {
            if (this.mString == null) {
                this.mString = context.getString(this.mStrId);
            }
            return this.mString;
        }
    }

    public Item(int i) {
        this.f97id = i;
    }

    public boolean equals(Object obj) {
        return (obj instanceof Item) && this.f97id == ((Item) obj).f97id;
    }

    public int hashCode() {
        return this.f97id;
    }

    public String toString() {
        return "[Item:" + this.f97id + "," + this.name + "," + this.effects + "]";
    }

    public static class Effect implements Parcelable, Comparable<Effect> {
        public static final Parcelable.Creator<Effect> CREATOR = new Parcelable.Creator<Effect>() { // from class: com.shirobakama.autorpg2.entity.Item.Effect.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Effect[] newArray(int i) {
                return new Effect[i];
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Effect createFromParcel(Parcel parcel) {
                return new Effect(parcel);
            }
        };
        public static final Comparator<Effect> VALUE_COMPARATOR = new Comparator<Effect>() { // from class: com.shirobakama.autorpg2.entity.Item.Effect.2
            @Override // java.util.Comparator
            public int compare(Effect effect, Effect effect2) {
                int i = effect.value - effect2.value;
                boolean z = effect.status == GameChar.Status.ARMOR_MAGIC_BONUS || effect.status == GameChar.Status.SHIELD_MAGIC_BONUS || effect.status == GameChar.Status.WEAPON_MAGIC_BONUS;
                boolean z2 = effect2.status == GameChar.Status.ARMOR_MAGIC_BONUS || effect2.status == GameChar.Status.SHIELD_MAGIC_BONUS || effect2.status == GameChar.Status.WEAPON_MAGIC_BONUS;
                if (z) {
                    if (z2) {
                        return i != 0 ? -i : effect.status.compareTo(effect2.status);
                    }
                    return -1;
                }
                if (z2) {
                    return 1;
                }
                if (i != 0) {
                    return -i;
                }
                if (effect.attrType != null) {
                    if (effect2.attrType == null) {
                        return -1;
                    }
                    return effect.attrType.compareTo(effect2.attrType);
                }
                if (effect2.attrType != null) {
                    return 1;
                }
                if (effect.attr != null) {
                    if (effect2.attr == null) {
                        return -1;
                    }
                    return effect.attr.compareTo(effect2.attr);
                }
                if (effect2.attr != null) {
                    return 1;
                }
                if (effect.status != null) {
                    if (effect2.status == null) {
                        return -1;
                    }
                    return effect.status.compareTo(effect2.status);
                }
                if (effect2.status != null) {
                    return 1;
                }
                return effect.monsterId - effect2.monsterId;
            }
        };
        public GameChar.Attribute attr;
        public AttrType attrType;
        public int monsterId;
        public Monster.MonsterType monsterType;
        public GameChar.Status status;
        public Type type;
        public int value;

        public enum Type {
            ATTRIBUTE,
            STATUS,
            ATTR_TYPE,
            RESTORE_HP,
            RESTORE_MP,
            KILLER
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public Effect(Parcel parcel) {
            int i = parcel.readInt();
            int i2 = parcel.readInt();
            int i3 = parcel.readInt();
            int i4 = parcel.readInt();
            this.monsterId = parcel.readInt();
            int i5 = parcel.readInt();
            this.value = parcel.readInt();
            this.type = i < 0 ? null : Type.values()[i];
            this.attr = i2 < 0 ? null : GameChar.ATTRIBUTES[i2];
            this.status = i3 < 0 ? null : GameChar.STATUS_ARRAY[i3];
            this.attrType = i4 < 0 ? null : AttrType.values()[i4];
            this.monsterType = i5 >= 0 ? Monster.MonsterType.values()[i5] : null;
        }

        public Effect(GameChar.Status status, int i) {
            this.type = Type.STATUS;
            this.value = i;
            this.status = status;
        }

        public Effect(GameChar.Attribute attribute, int i) {
            this.type = Type.ATTRIBUTE;
            this.value = i;
            this.attr = attribute;
        }

        public Effect(Type type, GameChar.Attribute attribute, int i) {
            this.type = type;
            this.value = i;
            this.attr = attribute;
        }

        public Effect(AttrType attrType) {
            this.type = Type.ATTR_TYPE;
            this.attrType = attrType;
        }

        public Effect(Monster.MonsterType monsterType, int i) {
            this.type = Type.KILLER;
            this.monsterType = monsterType;
            this.value = i;
        }

        public Effect(int i, int i2) {
            this.type = Type.KILLER;
            this.monsterId = i;
            this.value = i2;
        }

        Effect() {
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            Type type = this.type;
            parcel.writeInt(type == null ? -1 : type.ordinal());
            GameChar.Attribute attribute = this.attr;
            parcel.writeInt(attribute == null ? -1 : attribute.ordinal());
            GameChar.Status status = this.status;
            parcel.writeInt(status == null ? -1 : status.ordinal());
            AttrType attrType = this.attrType;
            parcel.writeInt(attrType == null ? -1 : attrType.ordinal());
            parcel.writeInt(this.monsterId);
            Monster.MonsterType monsterType = this.monsterType;
            parcel.writeInt(monsterType != null ? monsterType.ordinal() : -1);
            parcel.writeInt(this.value);
        }

        public String toString() {
            return "[Ef:" + this.type + "," + this.attr + "," + this.status + "," + this.attrType + "," + this.monsterId + "," + this.monsterType + "," + this.value + "]";
        }

        @Override // java.lang.Comparable
        public int compareTo(Effect effect) {
            int iCompareEnum = TypeUtil.compareEnum(this.type, effect.type);
            if (iCompareEnum == 0) {
                iCompareEnum = TypeUtil.compareEnum(this.attr, effect.attr);
            }
            if (iCompareEnum == 0) {
                iCompareEnum = TypeUtil.compareEnum(this.status, effect.status);
            }
            if (iCompareEnum == 0) {
                iCompareEnum = TypeUtil.compareEnum(this.attrType, effect.attrType);
            }
            if (iCompareEnum == 0) {
                iCompareEnum = this.monsterId - effect.monsterId;
            }
            if (iCompareEnum == 0) {
                iCompareEnum = TypeUtil.compareEnum(this.monsterType, effect.monsterType);
            }
            return iCompareEnum == 0 ? this.value - effect.value : iCompareEnum;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Effect)) {
                return false;
            }
            Effect effect = (Effect) obj;
            return this.type == effect.type && this.attr == effect.attr && this.attrType == effect.attrType && this.status == effect.status && this.monsterId == effect.monsterId && this.monsterType == effect.monsterType && this.value == effect.value;
        }

        public int hashCode() {
            int iOrdinal;
            Type type = this.type;
            if (type == null) {
                iOrdinal = 0;
            } else {
                int iOrdinal2 = type.ordinal() * 31;
                GameChar.Attribute attribute = this.attr;
                iOrdinal = iOrdinal2 + (attribute == null ? 0 : attribute.ordinal());
            }
            int i = iOrdinal * 31;
            AttrType attrType = this.attrType;
            int iOrdinal3 = (i + (attrType == null ? 0 : attrType.ordinal())) * 31;
            GameChar.Status status = this.status;
            int iOrdinal4 = (iOrdinal3 + (status == null ? 0 : status.ordinal())) * 31;
            Monster.MonsterType monsterType = this.monsterType;
            return ((((iOrdinal4 + (monsterType != null ? monsterType.ordinal() : 0)) * 31) + this.monsterId) * 31) + this.value;
        }
    }

    public void addEffect(Effect effect) {
        this.effects.add(effect);
        if (effect.type == Effect.Type.RESTORE_HP) {
            this.mHpRestoreEffect = effect;
        } else if (effect.type == Effect.Type.RESTORE_MP) {
            this.mMpRestoreEffect = effect;
        }
    }

    public int getMinValue() {
        return this.diceNum + this.attrBase;
    }

    public int getMaxValue() {
        return (this.diceFace * this.diceNum) + this.attrBase;
    }

    public int getBaseRequiredStrX2() {
        int i;
        switch (this.type) {
            case CONSUMABLE:
            case OTHER:
            case RING:
            default:
                i = 0;
                break;
            case ARMOR:
                i = this.attrBase * 6;
                break;
            case SHIELD:
                i = this.attrBase * 8;
                break;
            case WEAPON:
                int i2 = this.diceNum;
                int i3 = this.attrBase;
                i = (i2 + i3 + (this.diceFace * i2) + i3 + i3) * 2;
                if (this.weaponType == WeaponType.MISSILE) {
                    i += 2;
                }
                if (this.twoHanded) {
                    i -= 2;
                    break;
                }
                break;
        }
        if (i < 24) {
            return i;
        }
        int iPow = (int) (Math.pow(i - 24, 0.8d) + 24.0d);
        if (iPow > 36) {
            return 36;
        }
        return iPow;
    }

    public boolean isEventItem() {
        return this.type == Type.OTHER && TownFlagEngine.getChangedClass(this) == null;
    }

    public Effect getHpRestoreEffect() {
        return this.mHpRestoreEffect;
    }

    public Effect getMpRestoreEffect() {
        return this.mMpRestoreEffect;
    }
}
