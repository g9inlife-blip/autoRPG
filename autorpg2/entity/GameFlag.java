package com.shirobakama.autorpg2.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.shirobakama.autorpg2.util.TypeUtil;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class GameFlag implements Parcelable {
    public static final Parcelable.Creator<GameFlag> CREATOR = new Parcelable.Creator<GameFlag>() { // from class: com.shirobakama.autorpg2.entity.GameFlag.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GameFlag[] newArray(int i) {
            return new GameFlag[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GameFlag createFromParcel(Parcel parcel) {
            return new GameFlag(parcel);
        }
    };

    /* renamed from: id */
    public int f96id;
    public String name;
    public String option;
    public FlagType type;
    public boolean value;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public enum FlagType {
        DUNGEON,
        DUNGEON_FLOOR,
        TOWN,
        MONSTER,
        MONSTER_WIN,
        MONSTER_LOSE,
        MONSTER_RUN,
        ITEM,
        QUEST_START,
        QUEST_CLEAR,
        QUEST_TERM,
        QUEST,
        OTHER,
        HAS_ITEM,
        STOCK_ITEM,
        ADVENTURING;

        private static FlagType[] values = values();

        public static FlagType byOrdinal(int i) {
            return values[i];
        }

        public static FlagType byName(String str) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            return valueOf(str);
        }
    }

    public static class Key {
        public String name;
        public FlagType type;

        public Key() {
        }

        public Key(FlagType flagType, String str) {
            this.type = flagType;
            this.name = str;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Key)) {
                return false;
            }
            Key key = (Key) obj;
            return this.type == key.type && this.name.equals(key.name);
        }

        public int hashCode() {
            return (this.name.hashCode() * 31) + this.type.ordinal();
        }

        public static Key asClearDungeon(Dungeon dungeon) {
            return new Key(FlagType.DUNGEON, dungeon.symbol);
        }

        public static Key asMonsterEnconter(Monster monster) {
            return new Key(FlagType.MONSTER, monster.symbol);
        }

        public static Key asMonsterWin(Monster monster) {
            return new Key(FlagType.MONSTER_WIN, monster.symbol);
        }

        public static Key asMonsterLose(Monster monster) {
            return new Key(FlagType.MONSTER_LOSE, monster.symbol);
        }

        public static Key asMonsterRun(Monster monster) {
            return new Key(FlagType.MONSTER_RUN, monster.symbol);
        }

        public static Key asClearFloor(Dungeon dungeon, int i) {
            return new Key(FlagType.DUNGEON_FLOOR, dungeon.symbol + "," + Integer.toString(i));
        }

        public static Key asItemGot(Item item) {
            return new Key(FlagType.ITEM, item.symbol);
        }

        public static Key asHasItem(Item item) {
            return new Key(FlagType.HAS_ITEM, Integer.toString(item.f97id));
        }

        public static Key asStockItem(Item item) {
            return new Key(FlagType.STOCK_ITEM, Integer.toString(item.f97id));
        }

        public static Key asQuest(String str) {
            return new Key(FlagType.QUEST, str);
        }

        public static Key asType(FlagType flagType, String str) {
            return new Key(flagType, str);
        }
    }

    public GameFlag() {
        this.f96id = 0;
    }

    public GameFlag(Key key) {
        this.f96id = 0;
        this.type = key.type;
        this.name = key.name;
    }

    public GameFlag(FlagType flagType, String str, boolean z) {
        this.f96id = 0;
        this.type = flagType;
        this.name = str;
        this.value = z;
    }

    public GameFlag(Parcel parcel) {
        this.f96id = 0;
        this.f96id = parcel.readInt();
        this.type = FlagType.byOrdinal(parcel.readInt());
        this.name = parcel.readString();
        this.value = parcel.readInt() != 0;
        this.option = parcel.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.f96id);
        parcel.writeInt(this.type.ordinal());
        parcel.writeString(this.name);
        parcel.writeInt(this.value ? 1 : 0);
        parcel.writeString(this.option);
    }

    public Key key() {
        return new Key(this.type, this.name);
    }

    public int getOptionAsInt() {
        if (TextUtils.isEmpty(this.option)) {
            return 0;
        }
        try {
            return Integer.parseInt(this.option);
        } catch (NumberFormatException unused) {
            return 0;
        }
    }

    public GameFlag setValue(boolean z) {
        this.value = z;
        return this;
    }

    public GameFlag setOptionAsInt(int i) {
        this.option = Integer.toString(i);
        return this;
    }

    public GameFlag addOptionAsInt(int i) {
        setOptionAsInt(getOptionAsInt() + i);
        return this;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof GameFlag)) {
            return false;
        }
        GameFlag gameFlag = (GameFlag) obj;
        return this.f96id == gameFlag.f96id && this.type == gameFlag.type && TextUtils.equals(this.name, gameFlag.name) && this.value == gameFlag.value && TextUtils.equals(this.option, gameFlag.option);
    }

    public int hashCode() {
        return TypeUtil.hash(this.f96id, TypeUtil.hash(this.type), TypeUtil.hash(this.name), TypeUtil.hash(this.value), TypeUtil.hash(this.option));
    }
}
