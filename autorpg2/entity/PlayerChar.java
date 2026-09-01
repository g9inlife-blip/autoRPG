package com.shirobakama.autorpg2.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import com.shirobakama.autorpg2.adventure.EngineUtil;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.ItemObject;
import com.shirobakama.autorpg2.entity.Tactics;
import com.shirobakama.autorpg2.p001db.Persister;
import com.shirobakama.autorpg2.repo.MonsterDb;
import com.shirobakama.autorpg2.repo.SkillRepository;
import com.shirobakama.autorpg2.util.TypeUtil;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class PlayerChar extends GameChar implements Parcelable {
    public static final int AGI_PENALTY_INVENTORY_MAX_EXCEEDS = 4;
    private static final int BITMAP_ID_NPC_MAX = 10004;
    private static final int BITMAP_ID_NPC_MIN = 10001;
    public static final int BITMAP_SIZE_ON_DENSITY_HIGH = 96;
    public static final Parcelable.Creator<PlayerChar> CREATOR;
    public static final int LEVEL_PER_STATUS_UP = 8;
    private static final int MAX_ATTRIBUTE_SUM;
    public static final int MAX_EXP = 999999999;
    public static final int MAX_NUMBER_OF_SKILL_SLOTS = 6;
    public static final int MIN_NUMBER_OF_SKILL_SLOTS = 3;
    protected static final String TAG = "player-char";
    private static Boolean showCharByIndex;
    public int armorId;
    public int baseAgi;
    public int baseInt;
    public int baseStr;
    public int baseVit;
    public transient Bitmap bitmap;
    public int exp;

    /* renamed from: id */
    public int f106id;
    public transient boolean isForward;
    private transient Inventory mArmor;
    private transient AttrType mAttackAttrType;
    private transient boolean mHasEnchantedWeapon;
    private transient boolean[] mImmuneTypes;
    private transient boolean[] mResistTypes;
    private transient Inventory mRing;
    private transient Inventory mShield;
    private ArrayList<Integer> mSkillIds;
    private transient List<Skill> mSkills;
    private transient Inventory mWeapon;
    public int presetBitmapId;
    public Race race;
    public int ringId;
    public int shieldId;
    public int statusBonus;
    public int weaponId;
    private static final int[][] RACE_MINIMUM_ATTRIBUTE = {new int[]{10, 10, 10, 10}, new int[]{13, 7, 8, 12}, new int[]{8, 13, 12, 7}, new int[]{7, 10, 13, 10}};
    public static final Comparator<PlayerChar> LEVEL_DESC_COMPARATOR = new Comparator<PlayerChar>() { // from class: com.shirobakama.autorpg2.entity.PlayerChar.1
        @Override // java.util.Comparator
        public int compare(PlayerChar playerChar, PlayerChar playerChar2) {
            return playerChar2.level - playerChar.level;
        }
    };
    public static final int[] BITMAP_RESOURCE_IDS = {C0380R.drawable.char001_actor101, C0380R.drawable.char002_actor105, C0380R.drawable.char003_actor112, C0380R.drawable.char004_actor114, C0380R.drawable.char005_actor115, C0380R.drawable.char006_actor118, C0380R.drawable.char008_actor123, C0380R.drawable.char009_actor124, C0380R.drawable.char010_actor127, C0380R.drawable.char011_actor128, C0380R.drawable.char012_actor129, C0380R.drawable.char013_actor13, C0380R.drawable.char014_actor133, C0380R.drawable.char015_actor135, C0380R.drawable.char016_actor136, C0380R.drawable.char017_actor16, C0380R.drawable.char018_actor17, C0380R.drawable.char019_actor18, C0380R.drawable.char020_actor19, C0380R.drawable.char021_actor21, C0380R.drawable.char022_actor22, C0380R.drawable.char023_actor23, C0380R.drawable.char024_actor25, C0380R.drawable.char025_actor26, C0380R.drawable.char026_actor30, C0380R.drawable.char027_actor31, C0380R.drawable.char028_actor32, C0380R.drawable.char029_actor35, C0380R.drawable.char030_actor36, C0380R.drawable.char031_actor38, C0380R.drawable.char032_actor39, C0380R.drawable.char033_actor40, C0380R.drawable.char034_actor41, C0380R.drawable.char035_actor42, C0380R.drawable.char036_actor43, C0380R.drawable.char037_actor44, C0380R.drawable.char038_actor45, C0380R.drawable.char039_actor47, C0380R.drawable.char040_actor49, C0380R.drawable.char041_actor50, C0380R.drawable.char042_actor53, C0380R.drawable.char043_actor54, C0380R.drawable.char044_actor55, C0380R.drawable.char045_actor56, C0380R.drawable.char046_actor57, C0380R.drawable.char047_actor62, C0380R.drawable.char048_actor63, C0380R.drawable.char049_actor65, C0380R.drawable.char050_actor66, C0380R.drawable.char051_actor67, C0380R.drawable.char052_actor69, C0380R.drawable.char053_actor7, C0380R.drawable.char054_actor71, C0380R.drawable.char055_actor71_b, C0380R.drawable.char056_actor72, C0380R.drawable.char057_actor73, C0380R.drawable.char058_actor75, C0380R.drawable.char059_actor77, C0380R.drawable.char060_actor79, C0380R.drawable.char061_actor80, C0380R.drawable.char062_actor81, C0380R.drawable.char063_actor82, C0380R.drawable.char065_actor85, C0380R.drawable.char066_actor86, C0380R.drawable.char067_actor9, C0380R.drawable.char068_actor91, C0380R.drawable.char069_actor92, C0380R.drawable.char070_actor93, C0380R.drawable.char071_actor94, C0380R.drawable.char072_actor139, C0380R.drawable.char101_actor10, C0380R.drawable.char102_actor100, C0380R.drawable.char103_actor102, C0380R.drawable.char104_actor103, C0380R.drawable.char106_actor106, C0380R.drawable.char107_actor107, C0380R.drawable.char108_actor108, C0380R.drawable.char109_actor109, C0380R.drawable.char110_actor11, C0380R.drawable.char111_actor110, C0380R.drawable.char112_actor111, C0380R.drawable.char113_actor113, C0380R.drawable.char114_actor117, C0380R.drawable.char115_actor120, C0380R.drawable.char116_actor121, C0380R.drawable.char117_actor122, C0380R.drawable.char118_actor125, C0380R.drawable.char119_actor126, C0380R.drawable.char120_actor130, C0380R.drawable.char121_actor131, C0380R.drawable.char122_actor132a, C0380R.drawable.char123_actor132b, C0380R.drawable.char124_actor134, C0380R.drawable.char125_actor14, C0380R.drawable.char126_actor14_color_01, C0380R.drawable.char127_actor15, C0380R.drawable.char128_actor20, C0380R.drawable.char129_actor27, C0380R.drawable.char130_actor28, C0380R.drawable.char131_actor29, C0380R.drawable.char132_actor33, C0380R.drawable.char133_actor34, C0380R.drawable.char134_actor37, C0380R.drawable.char135_actor4, C0380R.drawable.char136_actor46, C0380R.drawable.char137_actor5, C0380R.drawable.char138_actor58, C0380R.drawable.char139_actor59, C0380R.drawable.char140_actor6, C0380R.drawable.char142_actor61, C0380R.drawable.char143_actor64, C0380R.drawable.char144_actor68, C0380R.drawable.char145_actor70, C0380R.drawable.char146_actor74, C0380R.drawable.char147_actor76, C0380R.drawable.char148_actor78, C0380R.drawable.char149_actor8, C0380R.drawable.char150_actor83, C0380R.drawable.char151_actor87, C0380R.drawable.char152_actor87_verb, C0380R.drawable.char153_actor88, C0380R.drawable.char154_actor90, C0380R.drawable.char155_actor95, C0380R.drawable.char156_actor96, C0380R.drawable.char157_actor97, C0380R.drawable.char158_actor98, C0380R.drawable.char159_actor99, C0380R.drawable.char160_actor137, C0380R.drawable.char161_actor138, C0380R.drawable.char162_actor140, C0380R.drawable.char163_actor142, C0380R.drawable.char164_actor143, C0380R.drawable.char165_actor144, C0380R.drawable.char201_actor89, C0380R.drawable.char202_actor52, C0380R.drawable.char203_actor51, C0380R.drawable.char204_actor48};
    public static final int[] BITMAP_NPC_RESOURCE_IDS = {C0380R.drawable.npc_priest_actor60, C0380R.drawable.npc_halfling_actor84, C0380R.drawable.npc_dwarf_actor119, C0380R.drawable.npc_elf_actor104};
    private static final Inventory WEAPON_BEAR_HAND = new Inventory();

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getPresetBitmapIdForNpc(int i) {
        return i + 10000;
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public boolean isPlayer() {
        return true;
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public boolean isWeakFor(AttrType attrType) {
        return false;
    }

    public enum Race {
        HUMAN(Tactics.TacticsValue.MODERATE, C0380R.string.lbl_human),
        DWARF(Tactics.TacticsValue.MODERATE, C0380R.string.lbl_dwarf),
        ELF(Tactics.TacticsValue.CONSERVATIVE, C0380R.string.lbl_elf),
        HALFLING(Tactics.TacticsValue.AGGRESSIVE, C0380R.string.lbl_halfling);

        private Tactics.TacticsValue mFickle;
        private int mStrId;

        Race(Tactics.TacticsValue tacticsValue, int i) {
            this.mFickle = tacticsValue;
            this.mStrId = i;
        }

        public Tactics.TacticsValue getFickle() {
            return this.mFickle;
        }

        public CharSequence getName(Context context) {
            return context.getString(this.mStrId);
        }
    }

    static {
        Inventory inventory = WEAPON_BEAR_HAND;
        inventory.itemId = 1010;
        inventory.adjustments = new ItemObject.AttrStatAdjustments();
        showCharByIndex = null;
        CREATOR = new Parcelable.Creator<PlayerChar>() { // from class: com.shirobakama.autorpg2.entity.PlayerChar.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public PlayerChar[] newArray(int i) {
                return new PlayerChar[i];
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public PlayerChar createFromParcel(Parcel parcel) {
                return new PlayerChar(parcel);
            }
        };
        MAX_ATTRIBUTE_SUM = ATTRIBUTES.length * 15;
    }

    public PlayerChar() {
        this.f106id = 0;
        this.presetBitmapId = -1;
        this.mSkillIds = new ArrayList<>(0);
        this.mResistTypes = new boolean[AttrType.VALUES.length];
        this.mImmuneTypes = new boolean[AttrType.VALUES.length];
    }

    public PlayerChar(Parcel parcel) {
        super(parcel);
        this.f106id = 0;
        this.presetBitmapId = -1;
        this.mSkillIds = new ArrayList<>(0);
        this.mResistTypes = new boolean[AttrType.VALUES.length];
        this.mImmuneTypes = new boolean[AttrType.VALUES.length];
        this.f106id = parcel.readInt();
        int i = parcel.readInt();
        this.race = i < 0 ? null : Race.values()[i];
        this.exp = parcel.readInt();
        this.statusBonus = parcel.readInt();
        this.baseStr = parcel.readInt();
        this.baseInt = parcel.readInt();
        this.baseAgi = parcel.readInt();
        this.baseVit = parcel.readInt();
        this.presetBitmapId = parcel.readInt();
        this.weaponId = parcel.readInt();
        this.armorId = parcel.readInt();
        this.shieldId = parcel.readInt();
        this.ringId = parcel.readInt();
        this.mSkillIds = TypeUtil.readIntegerList(parcel);
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.f106id);
        Race race = this.race;
        parcel.writeInt(race == null ? -1 : race.ordinal());
        parcel.writeInt(this.exp);
        parcel.writeInt(this.statusBonus);
        parcel.writeInt(this.baseStr);
        parcel.writeInt(this.baseInt);
        parcel.writeInt(this.baseAgi);
        parcel.writeInt(this.baseVit);
        parcel.writeInt(this.presetBitmapId);
        parcel.writeInt(this.weaponId);
        parcel.writeInt(this.armorId);
        parcel.writeInt(this.shieldId);
        parcel.writeInt(this.ringId);
        TypeUtil.writeIntegerList(parcel, this.mSkillIds);
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public Tactics.TacticsValue getFickleness() {
        return this.race.getFickle();
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public GameChar.MagicResist getMagicResist() {
        return GameChar.MagicResist.NONE;
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public AttrType getAttackAttrType() {
        return this.mAttackAttrType;
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public boolean isResistFor(AttrType attrType) {
        return this.mResistTypes[attrType.ordinal()];
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public boolean isImmuneFor(AttrType attrType) {
        return this.mImmuneTypes[attrType.ordinal()];
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public boolean hasEnchantedWeapon() {
        return this.mHasEnchantedWeapon;
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public void setEnchantedWeapon(boolean z) {
        this.mHasEnchantedWeapon = z | this.mHasEnchantedWeapon;
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public boolean isForward() {
        return this.isForward;
    }

    private void prepareInventory(GameContext gameContext) {
        Inventory inventory = this.mWeapon;
        if (inventory == null || inventory.f98id != this.weaponId) {
            this.mWeapon = gameContext.getInventory(this.weaponId);
        }
        Inventory inventory2 = this.mArmor;
        if (inventory2 == null || inventory2.f98id != this.armorId) {
            this.mArmor = gameContext.getInventory(this.armorId);
        }
        Inventory inventory3 = this.mShield;
        if (inventory3 == null || inventory3.f98id != this.shieldId) {
            this.mShield = gameContext.getInventory(this.shieldId);
        }
        Inventory inventory4 = this.mRing;
        if (inventory4 == null || inventory4.f98id != this.ringId) {
            this.mRing = gameContext.getInventory(this.ringId);
        }
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public PlayerChar calcStatusInFight(Context context, GameContext gameContext, FightContext fightContext, int i) {
        calcStatus(context, gameContext);
        fightContext.applyEnchantToPlayer(this, i);
        return this;
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public PlayerChar calcStatus(Context context, GameContext gameContext) {
        clearStatus();
        calcSubLevels();
        this.maxHp = calcMaxHp(this.baseVit);
        this.maxMp = calcMaxMp(this.baseInt);
        this.str = this.baseStr;
        this.intl = this.baseInt;
        this.agi = this.baseAgi;
        this.vit = this.baseVit;
        setStatus(GameChar.Status.CRITICAL, this.fightingSubClass == GameChar.SubClass.ROGUE ? 11 : 12);
        setStatus(GameChar.Status.FUMBLE, 2);
        if (gameContext != null) {
            prepareInventory(gameContext);
        } else {
            this.mWeapon = null;
            this.mArmor = null;
            this.mShield = null;
            this.mRing = null;
        }
        ItemObject.AttrStatAdjustments attrStatAdjustments = new ItemObject.AttrStatAdjustments();
        Inventory inventory = this.mWeapon;
        if (inventory == null) {
            inventory = WEAPON_BEAR_HAND;
        } else {
            inventory.adjustments = inventory.getAttrStatusAdjustments(context);
        }
        attrStatAdjustments.sum(inventory.adjustments);
        this.mHasEnchantedWeapon = inventory.adjustments.isEnchanted();
        this.mAttackAttrType = inventory.adjustments.getAttrType();
        Item baseItem = inventory.getBaseItem(context);
        this.attackBase = baseItem.attrBase;
        this.attackDiceFace = baseItem.diceFace;
        this.attackDiceNum = baseItem.diceNum;
        Inventory inventory2 = this.mArmor;
        if (inventory2 != null) {
            inventory2.adjustments = inventory2.getAttrStatusAdjustments(context);
            attrStatAdjustments.add(GameChar.Status.DEFENSE, this.mArmor.getBaseItem(context).attrBase);
            attrStatAdjustments.sum(this.mArmor.adjustments);
        }
        Inventory inventory3 = this.mShield;
        if (inventory3 != null) {
            inventory3.adjustments = inventory3.getAttrStatusAdjustments(context);
            attrStatAdjustments.add(GameChar.Status.SHIELD_DODGE, this.mShield.getBaseItem(context).attrBase);
            attrStatAdjustments.sum(this.mShield.adjustments);
        }
        for (int i = 0; i < AttrType.ENCHANTABLE_ATTRS; i++) {
            Inventory inventory4 = this.mArmor;
            boolean z = true;
            boolean z2 = inventory4 != null && inventory4.adjustments.getAttrType() == AttrType.VALUES[i];
            Inventory inventory5 = this.mShield;
            boolean z3 = inventory5 != null && inventory5.adjustments.getAttrType() == AttrType.VALUES[i];
            this.mResistTypes[i] = z2 || z3;
            boolean[] zArr = this.mImmuneTypes;
            if (!z2 || !z3) {
                z = false;
            }
            zArr[i] = z;
        }
        Inventory inventory6 = this.mRing;
        if (inventory6 != null) {
            inventory6.adjustments = inventory6.getAttrStatusAdjustments(context);
            attrStatAdjustments.sum(this.mRing.adjustments);
        }
        this.str += attrStatAdjustments.get(GameChar.Attribute.STR);
        this.intl += attrStatAdjustments.get(GameChar.Attribute.INT);
        this.agi += attrStatAdjustments.get(GameChar.Attribute.AGI);
        this.vit += attrStatAdjustments.get(GameChar.Attribute.VIT);
        if (!inventory.canEquipSingle(context, this)) {
            this.agi -= 4;
        }
        Inventory inventory7 = this.mArmor;
        if (inventory7 != null && !inventory7.canEquipSingle(context, this)) {
            this.agi -= 4;
        }
        Inventory inventory8 = this.mShield;
        if (inventory8 != null && !inventory8.canEquipSingle(context, this)) {
            this.agi -= 4;
        }
        for (GameChar.Status status : GameChar.STATUS_ARRAY) {
            if (status == GameChar.Status.CRITICAL) {
                int[] iArr = this.status;
                int iOrdinal = status.ordinal();
                iArr[iOrdinal] = iArr[iOrdinal] - attrStatAdjustments.get(status);
            } else {
                int[] iArr2 = this.status;
                int iOrdinal2 = status.ordinal();
                iArr2[iOrdinal2] = iArr2[iOrdinal2] + attrStatAdjustments.get(status);
            }
        }
        this.str = Math.min(this.str, 18);
        this.intl = Math.min(this.intl, 18);
        this.agi = Math.min(this.agi, 18);
        this.vit = Math.min(this.vit, 18);
        return this;
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    protected void calcSubLevels() {
        this.mSubLevels = this.clazz.getSubLevels(this.level, this.exp);
        this.fightingSubClass = getSubLevel(GameChar.SubClass.WARRIOR) >= getSubLevel(GameChar.SubClass.ROGUE) ? GameChar.SubClass.WARRIOR : GameChar.SubClass.ROGUE;
    }

    public String getAttackPowerAsString() {
        int fixed10AttrBonus = (EngineUtil.getFixed10AttrBonus(this.str) / 10) + getStatus(GameChar.Status.DAMAGE) + getStatus(GameChar.Status.WEAPON_MAGIC_BONUS);
        return (this.attackDiceNum + this.attackBase + fixed10AttrBonus) + "〜" + ((this.attackDiceNum * this.attackDiceFace) + this.attackBase + fixed10AttrBonus);
    }

    public String getDefensePowerAsString() {
        return Integer.toString(getStatus(GameChar.Status.DEFENSE) + getStatus(GameChar.Status.ARMOR_MAGIC_BONUS));
    }

    public String getShieldDodgeAsString() {
        return Integer.toString(getStatus(GameChar.Status.SHIELD_DODGE) + getStatus(GameChar.Status.SHIELD_MAGIC_BONUS));
    }

    public void restoreHpMp() {
        this.f93hp = this.maxHp;
        this.f94mp = this.maxMp;
    }

    public void setDefaultStatus() {
        int[] iArr = RACE_MINIMUM_ATTRIBUTE[this.race.ordinal()];
        this.baseStr = iArr[GameChar.Attribute.STR.ordinal()];
        this.baseInt = iArr[GameChar.Attribute.INT.ordinal()];
        this.baseAgi = iArr[GameChar.Attribute.AGI.ordinal()];
        this.baseVit = iArr[GameChar.Attribute.VIT.ordinal()];
    }

    public static int getMinimumAttr(Race race, GameChar.Attribute attribute) {
        return RACE_MINIMUM_ATTRIBUTE[race.ordinal()][attribute.ordinal()];
    }

    public int getBaseAttr(GameChar.Attribute attribute) {
        switch (attribute) {
            case STR:
                return this.baseStr;
            case INT:
                return this.baseInt;
            case AGI:
                return this.baseAgi;
            case VIT:
                return this.baseVit;
            default:
                return 0;
        }
    }

    public void setBaseAttr(GameChar.Attribute attribute, int i) {
        switch (attribute) {
            case STR:
                this.baseStr = i;
                break;
            case INT:
                this.baseInt = i;
                break;
            case AGI:
                this.baseAgi = i;
                break;
            case VIT:
                this.baseVit = i;
                break;
        }
    }

    public ArrayList<Integer> getLearnableSkills(Context context) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        calcSubLevels();
        List<Skill> skills = getSkills(context);
        for (GameChar.SubClass subClass : GameChar.SubClass.VALUES) {
            addLearnableSkills(context, arrayList, skills, subClass, getSubLevel(subClass));
        }
        return arrayList;
    }

    private void addLearnableSkills(Context context, ArrayList<Integer> arrayList, List<Skill> list, GameChar.SubClass subClass, int i) {
        if (i <= 0) {
            return;
        }
        HashSet hashSet = new HashSet();
        for (Skill skill : list) {
            if (skill.clazz == subClass) {
                hashSet.add(Integer.valueOf(skill.f107id));
            }
        }
        if (hashSet.size() >= getNumberOfSkillsForSubLevel(i)) {
            return;
        }
        for (Skill skill2 : SkillRepository.getSkillForSubClass(context, subClass)) {
            if (!hashSet.contains(Integer.valueOf(skill2.f107id)) && (skill2.preSkillId == 0 || hashSet.contains(Integer.valueOf(skill2.preSkillId)))) {
                arrayList.add(Integer.valueOf(skill2.f107id));
            }
        }
    }

    public int getLernableSkillCount(Context context) {
        int numberOfSkillsForSubLevel;
        int[] iArr = new int[GameChar.SubClass.VALUES.length];
        Iterator<Skill> it = getSkills(context).iterator();
        while (it.hasNext()) {
            int iOrdinal = it.next().clazz.ordinal();
            iArr[iOrdinal] = iArr[iOrdinal] + 1;
        }
        int i = 0;
        for (GameChar.SubClass subClass : GameChar.SubClass.VALUES) {
            if (iArr[subClass.ordinal()] < SkillRepository.getSkillForSubClass(context, subClass).size() && (numberOfSkillsForSubLevel = getNumberOfSkillsForSubLevel(getSubLevel(subClass)) - iArr[subClass.ordinal()]) > 0) {
                i += numberOfSkillsForSubLevel;
            }
        }
        return i;
    }

    private int getNumberOfSkillsForSubLevel(int i) {
        return (i + 3) / 4;
    }

    public int getNumberOfSkillSlots() {
        return Math.min((this.level / 20) + 3, 6);
    }

    public boolean isStatusBonusGot() {
        if (this.level % 8 != 0) {
            return false;
        }
        int baseAttr = 0;
        for (GameChar.Attribute attribute : GameChar.ATTRIBUTES) {
            baseAttr += getBaseAttr(attribute);
        }
        return baseAttr < MAX_ATTRIBUTE_SUM;
    }

    public Inventory getWeapon() {
        Inventory inventory = this.mWeapon;
        return inventory != null ? inventory : WEAPON_BEAR_HAND;
    }

    public Bitmap getBitmap(Context context) {
        Bitmap bitmap = this.bitmap;
        if (bitmap != null) {
            return bitmap;
        }
        int i = this.presetBitmapId;
        if (i >= 0) {
            if (i < BITMAP_RESOURCE_IDS.length) {
                this.bitmap = BitmapFactory.decodeResource(context.getResources(), BITMAP_RESOURCE_IDS[this.presetBitmapId]);
                return this.bitmap;
            }
            if (i >= BITMAP_ID_NPC_MIN && i <= BITMAP_ID_NPC_MAX) {
                this.bitmap = BitmapFactory.decodeResource(context.getResources(), BITMAP_NPC_RESOURCE_IDS[this.presetBitmapId - BITMAP_ID_NPC_MIN]);
                return this.bitmap;
            }
        }
        byte[] characterBitmap = new Persister(context).readCharacterBitmap(this.f106id);
        if (characterBitmap == null) {
            return BitmapFactory.decodeResource(context.getResources(), C0380R.drawable.char000_default);
        }
        this.bitmap = BitmapFactory.decodeByteArray(characterBitmap, 0, characterBitmap.length);
        this.bitmap.setDensity(getCharBitmapDensity(this.bitmap.getWidth()));
        return this.bitmap;
    }

    public static int getDesiredBitmapWidth(int i) {
        return Math.min(256, Math.max((i * 96) / MonsterDb.MONSTER_BANSHEE, 96));
    }

    public static int getCharBitmapDensity(int i) {
        if (i <= 48) {
            return MonsterDb.MONSTER_STIRGE;
        }
        double d = i;
        if (d <= 64.0d) {
            return MonsterDb.MONSTER_GIANT_ANT;
        }
        if (d <= 77.8d) {
            return 213;
        }
        if (i <= 96) {
            return MonsterDb.MONSTER_BANSHEE;
        }
        if (d <= 144.0d) {
            return 320;
        }
        if (i <= 192) {
            return MonsterDb.MONSTER_GHOST;
        }
        Double.isNaN(d);
        return (int) (d * 2.5d);
    }

    public boolean equals(Object obj) {
        return (obj instanceof PlayerChar) && this.f106id == ((PlayerChar) obj).f106id;
    }

    public int hashCode() {
        return this.f106id;
    }

    public String getEquippedCharInfo(Context context) {
        if (showCharByIndex == null) {
            showCharByIndex = Boolean.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(C0380R.string.pref_key_equip_char_info_index), false));
        }
        Boolean bool = showCharByIndex;
        return (bool == null || !bool.booleanValue()) ? this.name.substring(0, 1) : Integer.toString(this.index + 1);
    }

    public static void resetEquippdCharInfoType() {
        showCharByIndex = null;
    }

    public void learnNewSkill(int i) {
        addSkillId(i);
        sortSkills();
    }

    public void addSkillId(int i) {
        this.mSkillIds.add(Integer.valueOf(i));
        this.mSkills = null;
    }

    public ArrayList<Integer> getSkillIds() {
        return this.mSkillIds;
    }

    protected void sortSkills() {
        Collections.sort(this.mSkillIds);
    }

    public List<Skill> getSkills(Context context) {
        if (this.mSkills == null) {
            this.mSkills = new ArrayList(this.mSkillIds.size());
            Iterator<Integer> it = this.mSkillIds.iterator();
            while (it.hasNext()) {
                this.mSkills.add(SkillRepository.getSkill(context, it.next().intValue()));
            }
        }
        return this.mSkills;
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public String toString() {
        return "[PC:" + super.toString() + ",id" + this.f106id + ",R" + this.race + ",X" + this.exp + ",SB" + this.statusBonus + ",BS" + this.baseStr + ",BI" + this.baseInt + ",BA" + this.baseAgi + ",BV" + this.baseVit + ",BID" + this.presetBitmapId + ",Sk" + this.mSkills + ",W" + this.mWeapon + ",A" + this.mArmor + ",S" + this.mShield + ",R" + this.mRing + ",EW" + this.mHasEnchantedWeapon + ",AAT:" + this.mAttackAttrType + ",Fw" + this.isForward + "]";
    }
}
