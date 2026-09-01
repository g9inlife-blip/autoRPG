package com.shirobakama.autorpg2.entity;

import android.content.Context;
import android.util.SparseArray;
import com.shirobakama.autorpg2.adventure.FightEngine;
import com.shirobakama.autorpg2.entity.AdvancedTactics;
import com.shirobakama.autorpg2.entity.GameFlag;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class AdventureContext {
    private static final int MAX_TEMP_INVENTORY_ID = -1;
    protected static final String TAG = "adv-ctx";
    public boolean aborting;
    public Calendar calendar;
    public Tactics defaultTactics;
    public Dungeon dungeon;
    public DungeonStat dungeonStat;
    public FightEngine.FightResult fightResult;
    public Map<GameFlag.Key, GameFlag> flags;
    public boolean hasHidingSkill;
    public int inBlockProgress;
    public int inBlockProgressAtStart;
    public boolean isConquredAtStart;
    public boolean returning;
    public int targetFloor;
    public int turn = 0;
    public int floor = 0;
    public int block = 0;
    public int extraLevel = 0;
    public List<Enchant> enchants = new LinkedList();
    public List<Enchant>[] enchantsPlayers = new List[3];
    public boolean success = false;
    public Tactics[][] tacticsArrays = new Tactics[4][];
    public List<AdvancedTactics.TacticsComposition>[] advancedFightTacticsForChar = new List[3];
    public int[] mpRestoreTurn = new int[3];
    private int tempInventoryId = -1;
    private SparseArray<SparseArray<SkillCustomization>> mSkillCustomizationsForPc = new SparseArray<>();

    public AdventureContext() {
        int i = 0;
        while (true) {
            List<Enchant>[] listArr = this.enchantsPlayers;
            if (i >= listArr.length) {
                return;
            }
            listArr[i] = new LinkedList();
            i++;
        }
    }

    public Tactics getCurrentTacticsForChar(int i) {
        int i2 = i + 1;
        Tactics[][] tacticsArr = this.tacticsArrays;
        if (tacticsArr[i2] == null) {
            return getCurrentTacticsForParty();
        }
        Tactics tacticsForFloor = getTacticsForFloor(tacticsArr[i2]);
        return tacticsForFloor == null ? getCurrentTacticsForParty() : tacticsForFloor;
    }

    private Tactics getTacticsForFloor(Tactics[] tacticsArr) {
        Tactics tactics = null;
        for (Tactics tactics2 : tacticsArr) {
            if (tactics2.enabled && (tactics2.targetFloor == 0 || this.floor <= tactics2.targetFloor)) {
                tactics = tactics2;
            }
        }
        return tactics;
    }

    public Tactics getCurrentTacticsForParty() {
        Tactics tacticsForFloor = getTacticsForFloor(this.tacticsArrays[0]);
        return tacticsForFloor == null ? this.defaultTactics : tacticsForFloor;
    }

    public boolean isAhead() {
        return (this.aborting || this.returning) ? false : true;
    }

    public void addEnchant(Enchant enchant) {
        this.enchants.add(enchant);
    }

    public void addEnchantToPlayer(int i, Enchant enchant) {
        this.enchantsPlayers[i].add(enchant);
    }

    public void applyEnchantToPlayer(PlayerChar playerChar, int i) {
        Iterator<Enchant> it = this.enchants.iterator();
        while (it.hasNext()) {
            it.next().apply(playerChar);
        }
        Iterator<Enchant> it2 = this.enchantsPlayers[i].iterator();
        while (it2.hasNext()) {
            it2.next().apply(playerChar);
        }
    }

    public boolean tickEnchants() {
        boolean zTickEnchants = tickEnchants(this.enchants);
        for (List<Enchant> list : this.enchantsPlayers) {
            zTickEnchants |= tickEnchants(list);
        }
        return zTickEnchants;
    }

    static boolean tickEnchants(List<Enchant> list) {
        Iterator<Enchant> it = list.iterator();
        boolean z = false;
        while (it.hasNext()) {
            Enchant next = it.next();
            if (next.term >= 0) {
                next.term--;
                if (next.term <= 0) {
                    it.remove();
                    z = true;
                }
            }
        }
        return z;
    }

    public void calcCharacterStatus(Context context, GameContext gameContext, List<PlayerChar> list) {
        for (int i = 0; i < list.size(); i++) {
            PlayerChar playerChar = list.get(i);
            playerChar.calcStatus(context, gameContext);
            playerChar.illegalStatus.clear();
            applyEnchantToPlayer(playerChar, i);
        }
    }

    public Inventory createTemporaryInventory() {
        Inventory inventory = new Inventory();
        int i = this.tempInventoryId;
        this.tempInventoryId = i - 1;
        inventory.f98id = i;
        return inventory;
    }

    public boolean isTemporaryInventory(Inventory inventory) {
        return inventory.f98id <= -1;
    }

    public void setTemporaryInventoryIdToNothing(Collection<Inventory> collection) {
        for (Inventory inventory : collection) {
            if (isTemporaryInventory(inventory)) {
                inventory.f98id = 0;
            }
        }
    }

    public void putSkillCustomizationsForPc(int i, SparseArray<SkillCustomization> sparseArray) {
        this.mSkillCustomizationsForPc.put(i, sparseArray);
    }

    private SkillCustomization getSkillCustomization(GameChar gameChar, Skill skill) {
        SparseArray<SkillCustomization> sparseArray;
        if (!gameChar.isPlayer() || (sparseArray = this.mSkillCustomizationsForPc.get(((PlayerChar) gameChar).f106id)) == null) {
            return null;
        }
        return sparseArray.get(skill.f107id);
    }

    public String getSkillNameAwareCustomized(GameChar gameChar, Skill skill) {
        SkillCustomization skillCustomization = getSkillCustomization(gameChar, skill);
        if (skillCustomization != null) {
            return skillCustomization.skillName;
        }
        return skill.name;
    }

    public String getSkillUseAwareCustomized(Context context, GameChar gameChar, Skill skill) {
        SkillCustomization skillCustomization = getSkillCustomization(gameChar, skill);
        if (skillCustomization != null) {
            return skillCustomization.skillDesc;
        }
        return context.getString(skill.useStringId);
    }
}
