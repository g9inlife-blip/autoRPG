package com.shirobakama.autorpg2.entity;

import android.content.Context;
import android.os.Parcel;
import com.shirobakama.autorpg2.entity.Tactics;
import com.shirobakama.autorpg2.repo.SkillRepository;
import com.shirobakama.autorpg2.util.TypeUtil;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public abstract class GameChar {
    public static final int ATTRIBUTE_MAX = 18;
    public static final int ATTRIBUTE_MIN = 3;
    protected static final int DEFAULT_CRITICAL_DICE_NORMAL = 12;
    protected static final int DEFAULT_CRITICAL_DICE_THIEF = 11;
    protected static final int DEFAULT_FUMBLE_DICE = 2;
    public static final int LEVEL_MULTI_ATTACK = 7;
    public static final int MAX_ATTACK_TIMES = 8;
    public static final int MAX_LEVEL = 100;
    private static final double POWER_NUM_BY_LEVEL = 3.0d;
    public int agi;
    public int attackBase;
    public int attackDiceFace;
    public int attackDiceNum;
    public CharClass clazz;
    public transient SubClass fightingSubClass;

    /* renamed from: hp */
    public int f93hp;
    public transient List<Enchant> illegalStatus;
    public int imageResId;
    public transient int index;
    public int intl;
    public int level;
    private ArrayList<Integer> mAvailableSkillIds;
    private transient List<Skill> mAvailableSkills;
    protected transient int[] mSubLevels;
    public int maxHp;
    public int maxMp;

    /* renamed from: mp */
    public int f94mp;
    public String name;
    public transient int power;
    protected transient int[] status;
    public int str;
    public int vit;
    protected static final int[][] SUBCLASS_EXP_RATIO = {new int[]{5, 0, 0, 0}, new int[]{0, 5, 0, 0}, new int[]{1, 0, 4, 0}, new int[]{1, 0, 0, 4}, new int[]{4, 0, 1, 0}, new int[]{1, 0, 2, 2}, new int[]{4, 0, 0, 1}, new int[]{0, 4, 0, 1}};
    public static final Attribute[] ATTRIBUTES = Attribute.values();
    public static final Status[] STATUS_ARRAY = Status.values();
    public static final IllegalStatus[] ILLEGAL_STATUS_ARRAY = IllegalStatus.values();
    public static final int NUMBER_OF_ATTRIBUTE = ATTRIBUTES.length;

    public enum MagicResist {
        NONE,
        FULL,
        HALF,
        IMMUNE
    }

    public abstract GameChar calcStatus(Context context, GameContext gameContext);

    public abstract GameChar calcStatusInFight(Context context, GameContext gameContext, FightContext fightContext, int i);

    protected abstract void calcSubLevels();

    public abstract AttrType getAttackAttrType();

    public abstract Tactics.TacticsValue getFickleness();

    public abstract MagicResist getMagicResist();

    public abstract boolean hasEnchantedWeapon();

    public abstract boolean isForward();

    public abstract boolean isImmuneFor(AttrType attrType);

    public abstract boolean isPlayer();

    public abstract boolean isResistFor(AttrType attrType);

    public abstract boolean isWeakFor(AttrType attrType);

    public abstract void setEnchantedWeapon(boolean z);

    public enum CharClass {
        FIGHTER(true, C0380R.string.res_class_fighter),
        THIEF(true, C0380R.string.res_class_thief),
        PRIEST(true, C0380R.string.res_class_priest),
        MAGICIAN(true, C0380R.string.res_class_magician),
        LORD(false, C0380R.string.res_class_lord),
        BISHOP(false, C0380R.string.res_class_bishop),
        SAMURAI(false, C0380R.string.res_class_samurai),
        NINJA(false, C0380R.string.res_class_ninja);

        private static final int[] RATIO_FOR_EXP_PERCENTAGE = {0, 10, 18, 30, 60, 100};
        private int mHpFactor;
        private int mMpFactor;
        private boolean mStandard;
        private int mStrId;
        private String mString;

        CharClass(boolean z, int i) {
            this.mStandard = z;
            this.mStrId = i;
            for (int i2 = 0; i2 < 4; i2++) {
                int i3 = GameChar.SUBCLASS_EXP_RATIO[ordinal()][i2];
                SubClass subClass = SubClass.values()[i2];
                this.mHpFactor += subClass.getHpFactor() * i3;
                this.mMpFactor += subClass.getMpFactor() * i3;
            }
        }

        public int getMpFactor() {
            return this.mMpFactor;
        }

        public int getHpFactor() {
            return this.mHpFactor;
        }

        public boolean hasSubClass(SubClass subClass) {
            return GameChar.SUBCLASS_EXP_RATIO[ordinal()][subClass.ordinal()] > 0;
        }

        public String getString(Context context) {
            if (this.mString == null) {
                this.mString = context.getString(this.mStrId);
            }
            return this.mString;
        }

        public int[] getExpRatio() {
            return GameChar.SUBCLASS_EXP_RATIO[ordinal()];
        }

        public int[] calcSubLevels(int i, int i2, int[] iArr) {
            if (i2 < 0) {
                i2 = GameChar.getExp(i);
            }
            int[] expRatio = getExpRatio();
            int[] iArr2 = new int[expRatio.length];
            for (int i3 = 0; i3 < expRatio.length; i3++) {
                int i4 = RATIO_FOR_EXP_PERCENTAGE[expRatio[i3]];
                if (i4 > 0) {
                    iArr2[i3] = (int) ((i2 * i4) / 100);
                    if (iArr != null) {
                        iArr[i3] = GameChar.getLevel(iArr2[i3]);
                        if (expRatio[i3] < 3) {
                            iArr[i3] = iArr[i3] - 1;
                        }
                    }
                }
            }
            return iArr2;
        }

        public int[] getSubLevels(int i, int i2) {
            int[] iArr = new int[GameChar.SUBCLASS_EXP_RATIO[0].length];
            calcSubLevels(i, i2, iArr);
            return iArr;
        }

        public boolean isStandard() {
            return this.mStandard;
        }
    }

    public enum SubClass {
        WARRIOR(C0380R.string.res_subclass_warrior, 4, 1),
        ROGUE(C0380R.string.res_subclass_rogue, 3, 2),
        CLERIC(C0380R.string.res_subclass_cleric, 2, 3),
        SORCERER(C0380R.string.res_subclass_sorcerer, 1, 4);

        public static SubClass[] VALUES = values();
        private int mHpFactor;
        private int mMpFactor;
        private String mStr;
        private int mStrId;

        SubClass(int i, int i2, int i3) {
            this.mStrId = i;
            this.mHpFactor = i2;
            this.mMpFactor = i3;
        }

        public String getString(Context context) {
            if (this.mStr == null) {
                this.mStr = context.getString(this.mStrId);
            }
            return this.mStr;
        }

        public int getMpFactor() {
            return this.mMpFactor;
        }

        public int getHpFactor() {
            return this.mHpFactor;
        }
    }

    public enum Attribute {
        STR(C0380R.string.lbl_str),
        INT(C0380R.string.lbl_int),
        AGI(C0380R.string.lbl_agi),
        VIT(C0380R.string.lbl_vit);

        private int mStrId;
        private String mString;

        Attribute(int i) {
            this.mStrId = i;
        }

        public String getString(Context context) {
            if (this.mString == null) {
                this.mString = context.getString(this.mStrId);
            }
            return this.mString;
        }
    }

    public enum Status {
        CRITICAL(C0380R.string.res_status_critical),
        FUMBLE(C0380R.string.res_status_fumble),
        DEFENSE(C0380R.string.res_status_defense),
        SHIELD_DODGE(C0380R.string.res_status_shield_dodge),
        DAMAGE(C0380R.string.res_status_damage),
        HIT_BONUS(C0380R.string.res_status_hit_bonus),
        DODGE_BONUS(C0380R.string.res_status_dodge_bonus),
        ANTI_MAGIC_BONUS(C0380R.string.res_status_anti_magic_bonus),
        MAGIC_DEFENSE(C0380R.string.res_status_magic_defense),
        MAGIC_BONUS(C0380R.string.res_status_magic_bonus),
        MAGIC_DAMAGE_BONUS(C0380R.string.res_status_magic_damage_bonus),
        WEAPON_MAGIC_BONUS(C0380R.string.res_status_weapon_magic_bonus),
        ARMOR_MAGIC_BONUS(C0380R.string.res_status_armor_magic_bonus),
        SHIELD_MAGIC_BONUS(C0380R.string.res_status_shield_magic_bonus),
        LIGHT_WEIGHT(C0380R.string.res_status_light_weight);

        private int mStrId;
        private String mString;

        Status(int i) {
            this.mStrId = i;
        }

        public String getString(Context context) {
            if (this.mString == null) {
                this.mString = context.getString(this.mStrId);
            }
            return this.mString;
        }
    }

    public enum IllegalStatus {
        POISON(C0380R.string.res_illegal_status_poison),
        SLEEP(C0380R.string.res_illegal_status_sleep);

        private String string;
        private int stringId;

        IllegalStatus(int i) {
            this.stringId = i;
        }

        public String getAsString(Context context) {
            if (this.string == null) {
                this.string = context.getString(this.stringId);
            }
            return this.string;
        }
    }

    public GameChar() {
        this.mAvailableSkillIds = new ArrayList<>(0);
        this.status = new int[STATUS_ARRAY.length];
        this.illegalStatus = new ArrayList(0);
    }

    public GameChar(Parcel parcel) {
        this.mAvailableSkillIds = new ArrayList<>(0);
        this.status = new int[STATUS_ARRAY.length];
        this.illegalStatus = new ArrayList(0);
        this.name = parcel.readString();
        int i = parcel.readInt();
        this.clazz = i < 0 ? null : CharClass.values()[i];
        this.imageResId = parcel.readInt();
        this.f93hp = parcel.readInt();
        this.maxHp = parcel.readInt();
        this.f94mp = parcel.readInt();
        this.maxMp = parcel.readInt();
        this.str = parcel.readInt();
        this.intl = parcel.readInt();
        this.agi = parcel.readInt();
        this.vit = parcel.readInt();
        this.attackDiceFace = parcel.readInt();
        this.attackDiceNum = parcel.readInt();
        this.attackBase = parcel.readInt();
        this.level = parcel.readInt();
        int i2 = parcel.readInt();
        this.mAvailableSkillIds = new ArrayList<>(i2);
        for (int i3 = 0; i3 < i2; i3++) {
            this.mAvailableSkillIds.add(Integer.valueOf(parcel.readInt()));
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        CharClass charClass = this.clazz;
        parcel.writeInt(charClass == null ? -1 : charClass.ordinal());
        parcel.writeInt(this.imageResId);
        parcel.writeInt(this.f93hp);
        parcel.writeInt(this.maxHp);
        parcel.writeInt(this.f94mp);
        parcel.writeInt(this.maxMp);
        parcel.writeInt(this.str);
        parcel.writeInt(this.intl);
        parcel.writeInt(this.agi);
        parcel.writeInt(this.vit);
        parcel.writeInt(this.attackDiceFace);
        parcel.writeInt(this.attackDiceNum);
        parcel.writeInt(this.attackBase);
        parcel.writeInt(this.level);
        parcel.writeInt(this.mAvailableSkillIds.size());
        Iterator<Integer> it = this.mAvailableSkillIds.iterator();
        while (it.hasNext()) {
            parcel.writeInt(it.next().intValue());
        }
    }

    public int calcMaxHp(int i) {
        return ((i - 1) / 3) + 8 + (((this.clazz.getHpFactor() + (i * 2)) * this.level) / 11);
    }

    public int calcMaxMp(int i) {
        return ((i - 1) / 3) + 8 + (((this.clazz.getMpFactor() + (i * 2)) * this.level) / 11);
    }

    public int getAttr(Attribute attribute) {
        switch (attribute) {
            case STR:
                return this.str;
            case INT:
                return this.intl;
            case AGI:
                return this.agi;
            case VIT:
                return this.vit;
            default:
                return 0;
        }
    }

    public void setAttr(Attribute attribute, int i) {
        switch (attribute) {
            case STR:
                this.str = i;
                break;
            case INT:
                this.intl = i;
                break;
            case AGI:
                this.agi = i;
                break;
            case VIT:
                this.vit = i;
                break;
        }
    }

    public int getStatus(Status status) {
        return this.status[status.ordinal()];
    }

    protected void setStatus(Status status, int i) {
        this.status[status.ordinal()] = i;
    }

    protected void clearStatus() {
        int i = 0;
        while (true) {
            int[] iArr = this.status;
            if (i >= iArr.length) {
                return;
            }
            iArr[i] = 0;
            i++;
        }
    }

    public boolean isAlive() {
        return this.f93hp > 0;
    }

    public boolean isAsleep() {
        return getSpecifiedIllegalState(IllegalStatus.SLEEP) != null;
    }

    public Enchant getSpecifiedIllegalState(IllegalStatus illegalStatus) {
        if (this.illegalStatus.isEmpty()) {
            return null;
        }
        for (Enchant enchant : this.illegalStatus) {
            if (enchant.illegalStatus == illegalStatus) {
                return enchant;
            }
        }
        return null;
    }

    public static int getLevel(int i) {
        double d = i;
        Double.isNaN(d);
        int iPow = ((int) Math.pow(d / 10.0d, 0.3333333333333333d)) + 1;
        if (iPow > 100) {
            return 100;
        }
        return iPow;
    }

    public static int getExp(int i) {
        return (int) (Math.pow(i - 1, POWER_NUM_BY_LEVEL) * 10.0d);
    }

    public int getSubLevel(SubClass subClass) {
        return this.mSubLevels[subClass.ordinal()];
    }

    public boolean hasSubClass(SubClass subClass) {
        return this.clazz.hasSubClass(subClass);
    }

    public String toString() {
        return "[GC:" + this.name + ",H" + this.f93hp + "/" + this.maxHp + ",M" + this.f94mp + "/" + this.maxMp + ",S" + this.str + ",I" + this.intl + ",A" + this.agi + ",V" + this.vit + ",L" + this.level + ",ADF" + this.attackDiceFace + ",ADN" + this.attackDiceNum + ",AB" + this.attackBase + ",St" + TypeUtil.toString(this.status) + ",AS" + this.mAvailableSkills + ",SL" + TypeUtil.toString(this.mSubLevels) + ",FC" + this.fightingSubClass + ",IS" + this.illegalStatus + ",P" + this.power + ",Idx" + this.index + "]";
    }

    public int getMaxAttackTimes() {
        if (this.fightingSubClass == SubClass.WARRIOR) {
            return Math.min((getSubLevel(SubClass.WARRIOR) / 7) + 1, 8);
        }
        if (hasSubClass(SubClass.ROGUE)) {
            return Math.min((getSubLevel(SubClass.ROGUE) / 7) + 1, 8);
        }
        return 1;
    }

    public ArrayList<Integer> getAvailableSkillIds() {
        return this.mAvailableSkillIds;
    }

    public void setAvailableSkillIds(ArrayList<Integer> arrayList) {
        this.mAvailableSkillIds = arrayList;
    }

    public List<Skill> getAvailableSkills(Context context) {
        if (this.mAvailableSkills == null) {
            this.mAvailableSkills = new ArrayList(this.mAvailableSkillIds.size());
            Iterator<Integer> it = this.mAvailableSkillIds.iterator();
            while (it.hasNext()) {
                this.mAvailableSkills.add(SkillRepository.getSkill(context, it.next().intValue()));
            }
        }
        return this.mAvailableSkills;
    }

    public void addAvailableSkillId(int i) {
        this.mAvailableSkillIds.add(Integer.valueOf(i));
        this.mAvailableSkills = null;
    }

    public Skill getAvailableSkill(Context context, int i) {
        for (Skill skill : getAvailableSkills(context)) {
            if (skill.f107id == i) {
                return skill;
            }
        }
        return null;
    }
}
