package com.shirobakama.autorpg2.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.shirobakama.autorpg2.util.TypeUtil;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class AdvancedTactics implements Parcelable {
    protected static final String TAG = "adv-tactics";
    public TacticsAction action;
    public TacticsActionSub actionSub;
    public int charId;
    public Condition condition;
    public int conditionCharId;
    public boolean conditionNot;
    public ConditionSub conditionSub;
    public int conditionValue;
    public boolean fighting;

    /* renamed from: id */
    public int f73id;
    public Target target;
    public int targetCharId;
    public int targetId;
    public static final int[] LEVEL_DIFFS = {0, 3, 5, 10, 20};
    public static final int[] ENEMY_NUMBERS = {1, 2, 3, 4, 5};
    public static final Parcelable.Creator<AdvancedTactics> CREATOR = new Parcelable.Creator<AdvancedTactics>() { // from class: com.shirobakama.autorpg2.entity.AdvancedTactics.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AdvancedTactics[] newArray(int i) {
            return new AdvancedTactics[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AdvancedTactics createFromParcel(Parcel parcel) {
            return new AdvancedTactics(parcel);
        }
    };

    public interface StrIdHolder {
        int getStrId();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public enum Condition implements StrIdHolder {
        OWN(C0380R.string.lbl_adv_tactics_cond_own),
        SPECIFIC_CHARACTER(C0380R.string.lbl_adv_tactics_cond_char_specific),
        ANY_CHARACTER(C0380R.string.lbl_adv_tactics_cond_char_any),
        TWO_CHARACTERS(C0380R.string.lbl_adv_tactics_cond_char_two),
        ALL_CHARACTERS(C0380R.string.lbl_adv_tactics_cond_char_all),
        ANY_ENEMY(C0380R.string.lbl_adv_tactics_cond_enemy_any),
        ALL_ENEMY(C0380R.string.lbl_adv_tactics_cond_enemy_all),
        ENEMY_SPECIFIC(C0380R.string.lbl_adv_tactics_cond_enemy_specific),
        ENEMY_TYPE(C0380R.string.lbl_adv_tactics_cond_enemy_type),
        ENEMY_ATTR(C0380R.string.lbl_adv_tactics_cond_enemy_attr),
        NUMBER_OF_ENEMY(C0380R.string.lbl_adv_tactics_cond_number_of_enemy),
        LEVEL_DIFFERENCE(C0380R.string.lbl_adv_tactics_cond_level_diff),
        FLOOR(C0380R.string.lbl_adv_tactics_cond_floor),
        PROBABILITY(C0380R.string.lbl_adv_tactics_cond_probability);

        private int mStrId;

        Condition(int i) {
            this.mStrId = i;
        }

        @Override // com.shirobakama.autorpg2.entity.AdvancedTactics.StrIdHolder
        public int getStrId() {
            return this.mStrId;
        }
    }

    public enum ConditionSub implements StrIdHolder {
        HP(C0380R.string.lbl_adv_tactics_cond_sub_hp),
        MP(C0380R.string.lbl_adv_tactics_cond_sub_mp),
        ASLEEP(C0380R.string.lbl_adv_tactics_cond_sub_asleep),
        UPPER_FLOOR(C0380R.string.lbl_adv_tactics_cond_sub_upper_floor),
        LOWER_FLOOR(C0380R.string.lbl_adv_tactics_cond_sub_lower_floor),
        SPECIFIC_FLOOR(C0380R.string.lbl_adv_tactics_cond_sub_specific_floor);

        private int mStrId;

        ConditionSub(int i) {
            this.mStrId = i;
        }

        @Override // com.shirobakama.autorpg2.entity.AdvancedTactics.StrIdHolder
        public int getStrId() {
            return this.mStrId;
        }
    }

    public enum Target implements StrIdHolder {
        NONE(C0380R.string.lbl_adv_tactics_target_none),
        SELF(C0380R.string.lbl_adv_tactics_target_self),
        SPECIFIC_CHAR(C0380R.string.lbl_adv_tactics_target_specific),
        PARTY_ANY(C0380R.string.lbl_adv_tactics_target_party_any),
        HP_LOWEST(C0380R.string.lbl_adv_tactics_target_hp_lowest),
        MP_LOWEST(C0380R.string.lbl_adv_tactics_target_mp_lowest),
        HP_HIGHEST(C0380R.string.lbl_adv_tactics_target_hp_highest),
        MP_HIGHEST(C0380R.string.lbl_adv_tactics_target_mp_highest),
        FRONT(C0380R.string.lbl_adv_tactics_target_front),
        BACK(C0380R.string.lbl_adv_tactics_target_back),
        FRONT_HP_LOWEST(C0380R.string.lbl_adv_tactics_target_front_hp_lowest),
        FRONT_HP_HIGHEST(C0380R.string.lbl_adv_tactics_target_front_hp_highest),
        ENEMY_ANY(C0380R.string.lbl_adv_tactics_target_enemy_any),
        ENEMY_HP_HIGHEST(C0380R.string.lbl_adv_tactics_target_enemy_hp_highest),
        ENEMY_HP_LOWEST(C0380R.string.lbl_adv_tactics_target_enemy_hp_lowest),
        ENEMY_ASLEEP(C0380R.string.lbl_adv_tactics_target_enemy_asleep);

        public static final Target[] VALUES = values();
        private int mStrId;

        Target(int i) {
            this.mStrId = i;
        }

        @Override // com.shirobakama.autorpg2.entity.AdvancedTactics.StrIdHolder
        public int getStrId() {
            return this.mStrId;
        }
    }

    public enum TacticsAction implements StrIdHolder {
        CONDITION_AND(0),
        CONDITION_OR(0),
        NONE(C0380R.string.lbl_adv_tactics_action_none),
        ATTACK(C0380R.string.lbl_adv_tactics_action_attack),
        USE_SKILL(C0380R.string.lbl_adv_tactics_action_use_skill),
        USE_ITEM(C0380R.string.lbl_adv_tactics_action_use_item),
        EQUIP_ITEM(C0380R.string.lbl_adv_tactics_action_equip),
        RUNNING(C0380R.string.lbl_adv_tactics_action_running);

        private int mStrId;
        public static final TacticsAction[] SELECTABLE_ACTIONS = {NONE, ATTACK, USE_SKILL, USE_ITEM, EQUIP_ITEM, RUNNING};

        TacticsAction(int i) {
            this.mStrId = i;
        }

        @Override // com.shirobakama.autorpg2.entity.AdvancedTactics.StrIdHolder
        public int getStrId() {
            return this.mStrId;
        }
    }

    public enum TacticsActionSub implements StrIdHolder {
        SPECIFIC(0),
        HP_RESTORE(C0380R.string.lbl_adv_tactics_action_use_item_hp),
        MP_RESTORE(C0380R.string.lbl_adv_tactics_action_use_item_mp);

        private int mStrId;

        TacticsActionSub(int i) {
            this.mStrId = i;
        }

        @Override // com.shirobakama.autorpg2.entity.AdvancedTactics.StrIdHolder
        public int getStrId() {
            return this.mStrId;
        }
    }

    public enum ConditionValueHpMp implements StrIdHolder {
        LESS_THREE_OF_FOUR(C0380R.string.lbl_adv_tactics_cond_val_le_3_of_4),
        LESS_ONE_OF_TWO(C0380R.string.lbl_adv_tactics_cond_val_le_1_of_2),
        LESS_ONE_OF_FOUR(C0380R.string.lbl_adv_tactics_cond_val_le_1_of_4),
        GREAT_THREE_OF_FOUR(C0380R.string.lbl_adv_tactics_cond_val_ge_3_of_4),
        GREAT_ONE_OF_TWO(C0380R.string.lbl_adv_tactics_cond_val_ge_1_of_2),
        GREAT_ONE_OF_FOUR(C0380R.string.lbl_adv_tactics_cond_val_ge_1_of_4);

        private int mStrId;

        ConditionValueHpMp(int i) {
            this.mStrId = i;
        }

        @Override // com.shirobakama.autorpg2.entity.AdvancedTactics.StrIdHolder
        public int getStrId() {
            return this.mStrId;
        }
    }

    public enum ConditionValueProbability implements StrIdHolder {
        PERCENTAGE_75(C0380R.string.lbl_adv_tactics_cond_val_percentage_75),
        PERCENTAGE_50(C0380R.string.lbl_adv_tactics_cond_val_percentage_50),
        PERCENTAGE_25(C0380R.string.lbl_adv_tactics_cond_val_percentage_25);

        private int mStrId;

        ConditionValueProbability(int i) {
            this.mStrId = i;
        }

        @Override // com.shirobakama.autorpg2.entity.AdvancedTactics.StrIdHolder
        public int getStrId() {
            return this.mStrId;
        }
    }

    public enum ConditionValueEnemyAttr implements StrIdHolder {
        FIRE_RESIST(C0380R.string.lbl_adv_tactics_cond_val_fire_resist),
        WATER_RESIST(C0380R.string.lbl_adv_tactics_cond_val_water_resist),
        WIND_RESIST(C0380R.string.lbl_adv_tactics_cond_val_wind_resist),
        FIRE_IMMUNE(C0380R.string.lbl_adv_tactics_cond_val_fire_immune),
        WATER_IMMUNE(C0380R.string.lbl_adv_tactics_cond_val_water_immune),
        WIND_IMMUNE(C0380R.string.lbl_adv_tactics_cond_val_wind_immune),
        FIRE_WEAK(C0380R.string.lbl_adv_tactics_cond_val_fire_weak),
        WATER_WEAK(C0380R.string.lbl_adv_tactics_cond_val_water_weak),
        WIND_WEAK(C0380R.string.lbl_adv_tactics_cond_val_wind_weak),
        FIRE_ATTACK(C0380R.string.lbl_adv_tactics_cond_val_fire_attack),
        WATER_ATTACK(C0380R.string.lbl_adv_tactics_cond_val_water_attack),
        WIND_ATTACK(C0380R.string.lbl_adv_tactics_cond_val_wind_attack),
        SLEEP_RESIST(C0380R.string.lbl_adv_tactics_cond_val_sleep_resist),
        WEAPON_RESIST_HALF(C0380R.string.lbl_adv_tactics_cond_val_weapon_resist_half),
        WEAPON_RESIST_FULL(C0380R.string.lbl_adv_tactics_cond_val_weapon_resist_full),
        LONG_RANGE(C0380R.string.lbl_adv_tactics_cond_val_long_range);

        private int mStrId;

        ConditionValueEnemyAttr(int i) {
            this.mStrId = i;
        }

        @Override // com.shirobakama.autorpg2.entity.AdvancedTactics.StrIdHolder
        public int getStrId() {
            return this.mStrId;
        }
    }

    public AdvancedTactics() {
        this.f73id = 0;
        this.charId = 0;
        this.targetId = 0;
        this.targetCharId = 0;
    }

    public AdvancedTactics(Parcel parcel) {
        this.f73id = 0;
        this.charId = 0;
        this.targetId = 0;
        this.targetCharId = 0;
        this.f73id = parcel.readInt();
        this.fighting = parcel.readInt() != 0;
        this.charId = parcel.readInt();
        this.condition = (Condition) TypeUtil.getEnumOrNull(Condition.class, parcel.readString());
        this.conditionSub = (ConditionSub) TypeUtil.getEnumOrNull(ConditionSub.class, parcel.readString());
        this.target = (Target) TypeUtil.getEnumOrNull(Target.class, parcel.readString());
        this.action = (TacticsAction) TypeUtil.getEnumOrNull(TacticsAction.class, parcel.readString());
        this.actionSub = (TacticsActionSub) TypeUtil.getEnumOrNull(TacticsActionSub.class, parcel.readString());
        this.targetId = parcel.readInt();
        this.targetCharId = parcel.readInt();
        this.conditionValue = parcel.readInt();
        this.conditionCharId = parcel.readInt();
        this.conditionNot = parcel.readInt() != 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.f73id);
        parcel.writeInt(this.fighting ? 1 : 0);
        parcel.writeInt(this.charId);
        parcel.writeString(TypeUtil.getNameOrNull(this.condition));
        parcel.writeString(TypeUtil.getNameOrNull(this.conditionSub));
        parcel.writeString(TypeUtil.getNameOrNull(this.target));
        parcel.writeString(TypeUtil.getNameOrNull(this.action));
        parcel.writeString(TypeUtil.getNameOrNull(this.actionSub));
        parcel.writeInt(this.targetId);
        parcel.writeInt(this.targetCharId);
        parcel.writeInt(this.conditionValue);
        parcel.writeInt(this.conditionCharId);
        parcel.writeInt(this.conditionNot ? 1 : 0);
    }

    public String toString() {
        return "[Tactics: " + this.f73id + "," + this.charId + ",cond:" + this.condition + ",cS:" + this.conditionSub + ",tgt:" + this.target + ",act:" + this.action + ",aS:" + this.actionSub + ",tgtId:" + this.targetId + ",tgtChr:" + this.targetCharId + ",cV:" + this.conditionValue + ",cCI:" + this.conditionCharId + ",cN:" + this.conditionNot + "]";
    }

    public AdvancedTactics copy() {
        Parcel parcelObtain = Parcel.obtain();
        writeToParcel(parcelObtain, 0);
        parcelObtain.setDataPosition(0);
        AdvancedTactics advancedTactics = new AdvancedTactics(parcelObtain);
        parcelObtain.recycle();
        return advancedTactics;
    }

    public static class TacticsComposition {
        private String description;
        public List<AdvancedTactics> tactics = new ArrayList(2);

        public TacticsComposition copy() {
            TacticsComposition tacticsComposition = new TacticsComposition();
            Iterator<AdvancedTactics> it = this.tactics.iterator();
            while (it.hasNext()) {
                tacticsComposition.add(it.next().copy());
            }
            return tacticsComposition;
        }

        public static List<TacticsComposition> fromAdvancedTacticsList(List<AdvancedTactics> list) {
            ArrayList arrayList = new ArrayList();
            for (AdvancedTactics advancedTactics : list) {
                int size = arrayList.size();
                if (size >= 1 && (advancedTactics.action == TacticsAction.CONDITION_AND || advancedTactics.action == TacticsAction.CONDITION_OR)) {
                    ((TacticsComposition) arrayList.get(size - 1)).add(advancedTactics);
                } else {
                    TacticsComposition tacticsComposition = new TacticsComposition();
                    tacticsComposition.add(advancedTactics);
                    arrayList.add(tacticsComposition);
                }
            }
            return arrayList;
        }

        public void add(AdvancedTactics advancedTactics) {
            this.tactics.add(advancedTactics);
        }

        public boolean matches(PlayerChar playerChar, GameContext gameContext, AdventureContext adventureContext, FightContext fightContext) {
            boolean z = true;
            for (int i = 0; i < this.tactics.size(); i++) {
                AdvancedTactics advancedTactics = this.tactics.get(i);
                if (advancedTactics.action == TacticsAction.CONDITION_OR) {
                    z = true;
                }
                if (z) {
                    if (advancedTactics.matches(playerChar, gameContext, adventureContext, fightContext)) {
                        int i2 = i + 1;
                        AdvancedTactics advancedTactics2 = i2 < this.tactics.size() ? this.tactics.get(i2) : null;
                        if (advancedTactics2 == null || advancedTactics2.action != TacticsAction.CONDITION_AND) {
                            return true;
                        }
                    } else {
                        z = false;
                    }
                }
            }
            return false;
        }

        /* JADX WARN: Removed duplicated region for block: B:26:0x007b  */
        /* JADX WARN: Removed duplicated region for block: B:59:0x011b A[FALL_THROUGH] */
        /* JADX WARN: Removed duplicated region for block: B:61:0x011e  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public java.lang.String updateDescription(android.content.Context r12, com.shirobakama.autorpg2.entity.GameContext r13, android.util.SparseArray<com.shirobakama.autorpg2.entity.Skill> r14) {
            /*
                Method dump skipped, instructions count: 1156
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.entity.AdvancedTactics.TacticsComposition.updateDescription(android.content.Context, com.shirobakama.autorpg2.entity.GameContext, android.util.SparseArray):java.lang.String");
        }

        private String getCharacterNameForCharId(Context context, GameContext gameContext, int i) {
            if (gameContext != null) {
                int playerCharIndex = gameContext.getPlayerCharIndex(i);
                if (playerCharIndex >= 0) {
                    return gameContext.characters.get(playerCharIndex).name;
                }
                return context.getString(C0380R.string.msg_adv_tactics_cond_specific_char_removed);
            }
            return "Id:" + i;
        }

        public String toString() {
            return this.description;
        }

        public static void normalizeAdvancedTactics(Context context, PlayerChar playerChar, List<TacticsComposition> list, GameContext gameContext) {
            int size = gameContext.characters.size();
            int[] iArr = new int[size];
            for (int i = 0; i < size; i++) {
                iArr[i] = gameContext.characters.get(i).f106id;
            }
            Iterator<TacticsComposition> it = list.iterator();
            while (it.hasNext()) {
                TacticsComposition next = it.next();
                AdvancedTactics advancedTactics = next.tactics.get(0);
                boolean z = (advancedTactics.action == TacticsAction.USE_SKILL && playerChar.getAvailableSkill(context, advancedTactics.targetId) == null) ? false : true;
                if (z && advancedTactics.targetCharId != 0 && !findCharId(iArr, advancedTactics.targetCharId)) {
                    z = false;
                }
                if (z) {
                    Iterator<AdvancedTactics> it2 = next.tactics.iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        }
                        AdvancedTactics next2 = it2.next();
                        if (next2.condition == Condition.SPECIFIC_CHARACTER && !findCharId(iArr, next2.conditionCharId)) {
                            z = false;
                            break;
                        }
                    }
                }
                if (!z) {
                    it.remove();
                }
            }
        }

        private static boolean findCharId(int[] iArr, int i) {
            for (int i2 : iArr) {
                if (i2 == i) {
                    return true;
                }
            }
            return false;
        }

        public AdvancedTactics getFirstTactics() {
            if (TypeUtil.isNullOrEmpty(this.tactics)) {
                return null;
            }
            return this.tactics.get(0);
        }

        public static boolean matchBackStub(List<TacticsComposition> list, PlayerChar playerChar, GameContext gameContext, AdventureContext adventureContext, FightContext fightContext) {
            for (TacticsComposition tacticsComposition : list) {
                AdvancedTactics firstTactics = tacticsComposition.getFirstTactics();
                if (firstTactics != null && firstTactics.action == TacticsAction.USE_SKILL && firstTactics.targetId == 20040 && tacticsComposition.matches(playerChar, gameContext, adventureContext, fightContext)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void setDefault() {
        this.fighting = true;
        this.action = TacticsAction.NONE;
        this.condition = Condition.OWN;
        this.conditionSub = ConditionSub.HP;
        this.conditionValue = 0;
        this.target = Target.NONE;
        this.conditionNot = false;
    }

    private boolean charMatches(GameChar gameChar) {
        int i;
        int i2;
        if (!gameChar.isAlive()) {
            return false;
        }
        if (this.conditionSub == ConditionSub.HP) {
            i = gameChar.maxHp;
            i2 = gameChar.f93hp;
        } else if (this.conditionSub == ConditionSub.MP) {
            i = gameChar.maxMp;
            i2 = gameChar.f94mp;
        } else {
            if (this.conditionSub == ConditionSub.ASLEEP) {
                return gameChar.isAsleep();
            }
            return false;
        }
        ConditionValueHpMp conditionValueHpMp = (ConditionValueHpMp) TypeUtil.getEnumOrNullByOrdinal(ConditionValueHpMp.values(), this.conditionValue);
        if (conditionValueHpMp == null) {
            return false;
        }
        switch (conditionValueHpMp) {
            case GREAT_ONE_OF_FOUR:
                if (i2 >= i / 4) {
                }
                break;
            case GREAT_ONE_OF_TWO:
                if (i2 >= i / 2) {
                }
                break;
            case GREAT_THREE_OF_FOUR:
                if (i2 >= (i * 3) / 4) {
                }
                break;
            case LESS_ONE_OF_FOUR:
                if (i2 <= i / 4) {
                }
                break;
            case LESS_ONE_OF_TWO:
                if (i2 <= i / 2) {
                }
                break;
            case LESS_THREE_OF_FOUR:
                if (i2 <= (i * 3) / 4) {
                }
                break;
        }
        return false;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:128:0x01ff  */
    /* JADX WARN: Removed duplicated region for block: B:158:0x0283  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean matches(com.shirobakama.autorpg2.entity.PlayerChar r5, com.shirobakama.autorpg2.entity.GameContext r6, com.shirobakama.autorpg2.entity.AdventureContext r7, com.shirobakama.autorpg2.entity.FightContext r8) {
        /*
            Method dump skipped, instructions count: 778
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.entity.AdvancedTactics.matches(com.shirobakama.autorpg2.entity.PlayerChar, com.shirobakama.autorpg2.entity.GameContext, com.shirobakama.autorpg2.entity.AdventureContext, com.shirobakama.autorpg2.entity.FightContext):boolean");
    }
}
