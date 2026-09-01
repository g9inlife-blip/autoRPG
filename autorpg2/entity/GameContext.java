package com.shirobakama.autorpg2.entity;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.SparseIntArray;
import com.shirobakama.autorpg2.AutoRpgPrefsActivity;
import com.shirobakama.autorpg2.entity.GameFlag;
import com.shirobakama.autorpg2.entity.PlayerChar;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class GameContext implements Parcelable {
    public static final Parcelable.Creator<GameContext> CREATOR = new Parcelable.Creator<GameContext>() { // from class: com.shirobakama.autorpg2.entity.GameContext.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GameContext[] newArray(int i) {
            return new GameContext[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GameContext createFromParcel(Parcel parcel) {
            return new GameContext(parcel);
        }
    };
    public static final int DEFAULT_MAX_LOG_HISTORIES = 5;
    public static final int DUNGEON_STATUS_NONE = 0;
    private static final int INITIAL_GOLD = 100;
    public static final int MAX_ACTIVE_CHARACTERS = 3;
    public static final int MAX_ADV_COUNT_WO_TIME_CHECKING = 5;
    public static final int MAX_CHARACTERS = 10;
    public static final long MAX_DIALOG_TOLERANCE_TS = 10000;
    public static final int MAX_ENEMIES_PER_GROUP = 3;
    public static final long MAX_TIME_RANGE_TS = 7800000;
    protected static final String TAG = "game-ctx";
    public static GameContext game;
    public int advCount;
    public transient AdventureContext adventureContext;
    public List<PlayerChar> characters;
    public DungeonContext dungeonContext;
    public int dungeonId;
    public Date estimateTime;
    public transient FightContext fightContext;
    public Map<GameFlag.Key, GameFlag> flags;
    public int gold;

    /* renamed from: id */
    public int f95id;
    public List<Inventory> inventories;
    public long returnRealtime;
    public Date returnTime;
    public long startRealtime;
    public Date startTime;
    public List<Stock> stocks;
    public int targetFloor;
    public int townId;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public GameContext() {
        this.characters = new ArrayList(3);
        this.inventories = new ArrayList();
        this.stocks = new ArrayList();
        this.dungeonContext = new DungeonContext();
        this.flags = new HashMap();
        this.f95id = 0;
        this.townId = 0;
        this.dungeonId = 0;
    }

    public GameContext(Parcel parcel) {
        this.characters = new ArrayList(3);
        this.inventories = new ArrayList();
        this.stocks = new ArrayList();
        this.dungeonContext = new DungeonContext();
        this.flags = new HashMap();
        this.f95id = 0;
        this.townId = 0;
        this.dungeonId = 0;
        this.characters = parcel.createTypedArrayList(PlayerChar.CREATOR);
        this.inventories = parcel.createTypedArrayList(Inventory.CREATOR);
        this.stocks = parcel.createTypedArrayList(Stock.CREATOR);
        this.f95id = parcel.readInt();
        this.gold = parcel.readInt();
        this.townId = parcel.readInt();
        this.dungeonId = parcel.readInt();
        this.targetFloor = parcel.readInt();
        this.startTime = readDate(parcel);
        this.estimateTime = readDate(parcel);
        this.returnTime = readDate(parcel);
        this.advCount = parcel.readInt();
        this.startRealtime = parcel.readLong();
        this.returnRealtime = parcel.readLong();
        GameFlag[] gameFlagArr = (GameFlag[]) parcel.createTypedArray(GameFlag.CREATOR);
        this.dungeonContext = (DungeonContext) parcel.readParcelable(getClass().getClassLoader());
        for (GameFlag gameFlag : gameFlagArr) {
            this.flags.put(gameFlag.key(), gameFlag);
        }
    }

    private Date readDate(Parcel parcel) {
        long j = parcel.readLong();
        return j == 0 ? new Date() : new Date(j);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        GameFlag[] gameFlagArr = (GameFlag[]) this.flags.values().toArray(new GameFlag[this.flags.size()]);
        parcel.writeTypedList(this.characters);
        parcel.writeTypedList(this.inventories);
        parcel.writeTypedList(this.stocks);
        parcel.writeInt(this.f95id);
        parcel.writeInt(this.gold);
        parcel.writeInt(this.townId);
        parcel.writeInt(this.dungeonId);
        parcel.writeInt(this.targetFloor);
        parcel.writeLong(this.startTime.getTime());
        parcel.writeLong(this.estimateTime.getTime());
        parcel.writeLong(this.returnTime.getTime());
        parcel.writeInt(this.advCount);
        parcel.writeLong(this.startRealtime);
        parcel.writeLong(this.returnRealtime);
        parcel.writeTypedArray(gameFlagArr, i);
        parcel.writeParcelable(this.dungeonContext, 0);
    }

    public static GameContext initialize() {
        GameContext gameContext = new GameContext();
        gameContext.gold = 100;
        gameContext.characters = new ArrayList();
        gameContext.townId = 1;
        gameContext.dungeonId = 0;
        gameContext.dungeonContext = new DungeonContext();
        gameContext.startTime = new Date();
        gameContext.estimateTime = new Date();
        gameContext.returnTime = new Date();
        gameContext.advCount = 5;
        gameContext.startRealtime = 0L;
        gameContext.returnRealtime = 0L;
        return gameContext;
    }

    public PlayerChar getEquippedChar(Inventory inventory) {
        if (inventory.f98id == 0) {
            return null;
        }
        for (PlayerChar playerChar : this.characters) {
            if (playerChar.weaponId == inventory.f98id || playerChar.armorId == inventory.f98id || playerChar.shieldId == inventory.f98id || playerChar.ringId == inventory.f98id) {
                return playerChar;
            }
        }
        return null;
    }

    public void setPlayerCharIndexToInventory() {
        SparseIntArray sparseIntArray = new SparseIntArray();
        for (PlayerChar playerChar : this.characters) {
            sparseIntArray.put(playerChar.weaponId, playerChar.f106id);
            sparseIntArray.put(playerChar.armorId, playerChar.f106id);
            sparseIntArray.put(playerChar.shieldId, playerChar.f106id);
            sparseIntArray.put(playerChar.ringId, playerChar.f106id);
        }
        sparseIntArray.put(0, 0);
        for (Inventory inventory : this.inventories) {
            inventory.equippedCharId = sparseIntArray.get(inventory.f98id);
        }
    }

    public void restoreCharacterHpMp() {
        Iterator<PlayerChar> it = this.characters.iterator();
        while (it.hasNext()) {
            it.next().restoreHpMp();
        }
    }

    public int getPlayerCharIndex(int i) {
        for (int i2 = 0; i2 < this.characters.size(); i2++) {
            if (this.characters.get(i2).f106id == i) {
                return i2;
            }
        }
        return -1;
    }

    public Inventory getInventory(int i) {
        if (i == 0) {
            return null;
        }
        for (Inventory inventory : this.inventories) {
            if (inventory.f98id == i) {
                return inventory;
            }
        }
        return null;
    }

    public int getInventoryIndexForItemId(int i) {
        for (int i2 = 0; i2 < this.inventories.size(); i2++) {
            if (this.inventories.get(i2).itemId == i) {
                return i2;
            }
        }
        return -1;
    }

    public int getInventoryIndex(int i) {
        for (int i2 = 0; i2 < this.inventories.size(); i2++) {
            if (this.inventories.get(i2).f98id == i) {
                return i2;
            }
        }
        return -1;
    }

    public int getStockIndex(int i) {
        for (int i2 = 0; i2 < this.stocks.size(); i2++) {
            if (this.stocks.get(i2).f98id == i) {
                return i2;
            }
        }
        return -1;
    }

    public boolean areAllCharsDied() {
        Iterator<PlayerChar> it = this.characters.iterator();
        while (it.hasNext()) {
            if (it.next().isAlive()) {
                return false;
            }
        }
        return true;
    }

    public List<PlayerChar> getAliveChars() {
        ArrayList arrayList = new ArrayList(3);
        for (PlayerChar playerChar : this.characters) {
            if (playerChar.isAlive()) {
                arrayList.add(playerChar);
            }
        }
        return arrayList;
    }

    public int getAliveCharCount() {
        Iterator<PlayerChar> it = this.characters.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (it.next().isAlive()) {
                i++;
            }
        }
        return i;
    }

    public PlayerChar getMostPowerfulChar() {
        int i;
        int i2 = 0;
        PlayerChar playerChar = null;
        for (PlayerChar playerChar2 : this.characters) {
            if (playerChar2.isAlive() && (i = playerChar2.str) > i2) {
                playerChar = playerChar2;
                i2 = i;
            }
        }
        return playerChar;
    }

    public PlayerChar getMostIntelliChar() {
        int i;
        int i2 = 0;
        PlayerChar playerChar = null;
        for (PlayerChar playerChar2 : this.characters) {
            if (playerChar2.isAlive() && (i = playerChar2.intl) > i2) {
                playerChar = playerChar2;
                i2 = i;
            }
        }
        return playerChar;
    }

    public boolean isRaceActiveMember(PlayerChar.Race race) {
        for (PlayerChar playerChar : this.characters) {
            if (playerChar.isAlive() && playerChar.race == race) {
                return true;
            }
        }
        return false;
    }

    public boolean isAdventuring(Context context) {
        boolean z = this.returnTime.getTime() > System.currentTimeMillis();
        return !AutoRpgPrefsActivity.isOfflineTimeChecking(context) ? z : z || this.returnRealtime > SystemClock.elapsedRealtime();
    }

    public GameFlag getOrCreateFlag(GameFlag.Key key) {
        GameFlag gameFlag;
        if (key.type == GameFlag.FlagType.ADVENTURING) {
            gameFlag = this.adventureContext.flags.get(key);
        } else {
            gameFlag = this.flags.get(key);
        }
        if (gameFlag != null) {
            return gameFlag;
        }
        GameFlag gameFlag2 = new GameFlag(key);
        gameFlag2.value = false;
        putFlag(gameFlag2);
        return gameFlag2;
    }

    public GameFlag getFlag(GameFlag.Key key) {
        if (key.type == GameFlag.FlagType.ADVENTURING) {
            return this.adventureContext.flags.get(key);
        }
        return this.flags.get(key);
    }

    public void putFlag(GameFlag gameFlag) {
        if (gameFlag.type == GameFlag.FlagType.ADVENTURING) {
            this.adventureContext.flags.put(gameFlag.key(), gameFlag);
        } else {
            this.flags.put(gameFlag.key(), gameFlag);
        }
    }

    public int getMaxInventoryCount() {
        Iterator<PlayerChar> it = this.characters.iterator();
        int i = 0;
        while (it.hasNext()) {
            i += it.next().str;
        }
        return Math.max(i / 2, this.characters.size() * 4);
    }

    public void calcCharacterStatus(Context context) {
        Iterator<PlayerChar> it = this.characters.iterator();
        while (it.hasNext()) {
            it.next().calcStatus(context, this);
        }
    }

    public GameContext copy() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("serialize", this);
        return (GameContext) bundle.getParcelable("serialize");
    }
}
