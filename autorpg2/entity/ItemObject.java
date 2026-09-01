package com.shirobakama.autorpg2.entity;

import android.content.Context;
import android.os.Parcel;
import android.text.TextUtils;
import androidx.exifinterface.media.ExifInterface;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.autorpg2.repo.ItemRepository;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public abstract class ItemObject {
    private static final long MAX_BUY_PRICE = 10000000;
    public List<Item.Effect> extraEffects;

    /* renamed from: id */
    public int f98id;
    public int itemId;
    private transient Item mItem;
    private transient Rarity mRarity;
    public String name;
    private static Pattern PATTERN_COMMA_SEPARATE = Pattern.compile(",");
    private static final AttrType[] ATTR_TYPE_FOR_ENCHANTS = {AttrType.FIRE, AttrType.WATER, AttrType.WIND};
    private static final Enum<?>[] ATTR_STAT_FOR_WEAPON_ENCHANTS = {GameChar.Attribute.AGI, GameChar.Attribute.INT, GameChar.Attribute.STR, GameChar.Attribute.VIT, GameChar.Status.CRITICAL, GameChar.Status.HIT_BONUS, GameChar.Status.MAGIC_BONUS, GameChar.Status.MAGIC_DAMAGE_BONUS, GameChar.Status.LIGHT_WEIGHT};
    private static final Enum<?>[] ATTR_STAT_FOR_ARMOR_ENCHANTS = {GameChar.Attribute.AGI, GameChar.Attribute.INT, GameChar.Attribute.STR, GameChar.Attribute.VIT, GameChar.Status.DODGE_BONUS, GameChar.Status.ANTI_MAGIC_BONUS, GameChar.Status.MAGIC_DEFENSE, GameChar.Status.LIGHT_WEIGHT};
    private static final Enum<?>[] ATTR_STAT_FOR_SHIELD_ENCHANTS = {GameChar.Attribute.AGI, GameChar.Attribute.INT, GameChar.Attribute.STR, GameChar.Attribute.VIT, GameChar.Status.ANTI_MAGIC_BONUS, GameChar.Status.MAGIC_DEFENSE, GameChar.Status.LIGHT_WEIGHT};

    private boolean canEquipRing(PlayerChar playerChar) {
        return true;
    }

    public static class AttrStatAdjustments {
        private AttrType mAttrType;
        private int[] mAttr = new int[GameChar.ATTRIBUTES.length];
        private int[] mStatus = new int[GameChar.STATUS_ARRAY.length];
        private boolean mIsEnchanted = false;

        public void sum(AttrStatAdjustments attrStatAdjustments) {
            int i = 0;
            int i2 = 0;
            while (true) {
                int[] iArr = this.mAttr;
                if (i2 >= iArr.length) {
                    break;
                }
                iArr[i2] = iArr[i2] + attrStatAdjustments.mAttr[i2];
                i2++;
            }
            while (true) {
                int[] iArr2 = this.mStatus;
                if (i < iArr2.length) {
                    iArr2[i] = iArr2[i] + attrStatAdjustments.mStatus[i];
                    i++;
                } else {
                    this.mAttrType = null;
                    this.mIsEnchanted = attrStatAdjustments.mIsEnchanted | this.mIsEnchanted;
                    return;
                }
            }
        }

        public void add(Enum<?> r4, int i) {
            if (r4 instanceof GameChar.Attribute) {
                int[] iArr = this.mAttr;
                int iOrdinal = r4.ordinal();
                iArr[iOrdinal] = iArr[iOrdinal] + i;
                return;
            }
            if (r4 instanceof GameChar.Status) {
                int[] iArr2 = this.mStatus;
                int iOrdinal2 = r4.ordinal();
                iArr2[iOrdinal2] = iArr2[iOrdinal2] + i;
                if (((GameChar.Status) r4) != GameChar.Status.WEAPON_MAGIC_BONUS || i <= 0) {
                    return;
                }
                this.mIsEnchanted = true;
                return;
            }
            if (r4 instanceof AttrType) {
                this.mAttrType = (AttrType) r4;
                return;
            }
            throw new IllegalArgumentException("Unsupported type of enum:" + r4);
        }

        public int get(Enum<?> r4) {
            if (r4 instanceof GameChar.Attribute) {
                return this.mAttr[r4.ordinal()];
            }
            if (r4 instanceof GameChar.Status) {
                return this.mStatus[r4.ordinal()];
            }
            if (r4 instanceof AttrType) {
                return this.mAttrType.ordinal();
            }
            throw new IllegalArgumentException("Unsupported type of enum:" + r4);
        }

        public void setAttrType(AttrType attrType) {
            this.mAttrType = attrType;
        }

        public AttrType getAttrType() {
            return this.mAttrType;
        }

        public void add(List<Item.Effect> list) {
            for (Item.Effect effect : list) {
                if (effect.type == Item.Effect.Type.ATTRIBUTE) {
                    add(effect.attr, effect.value);
                } else if (effect.type == Item.Effect.Type.STATUS) {
                    add(effect.status, effect.value);
                } else if (effect.type == Item.Effect.Type.ATTR_TYPE) {
                    setAttrType(effect.attrType);
                } else if (effect.type == Item.Effect.Type.KILLER) {
                    this.mIsEnchanted = true;
                }
            }
        }

        public boolean isEnchanted() {
            return this.mIsEnchanted;
        }
    }

    public enum Rarity {
        COMMON(C0380R.color.item_name_common),
        UNCOMMON(C0380R.color.item_name_uncommon),
        RARE(C0380R.color.item_name_rare),
        VERY_RARE(C0380R.color.item_name_very_rare);

        private int mColor;
        private int mColorId;
        private boolean mInitialized;

        Rarity(int i) {
            this.mColorId = i;
        }

        public int getColor(Context context) {
            if (!this.mInitialized) {
                this.mColor = context.getResources().getColor(this.mColorId);
                this.mInitialized = true;
            }
            return this.mColor;
        }
    }

    public ItemObject() {
        this.f98id = 0;
        this.extraEffects = new ArrayList(0);
    }

    public ItemObject(Parcel parcel) {
        this.f98id = 0;
        this.extraEffects = new ArrayList(0);
        this.f98id = parcel.readInt();
        this.itemId = parcel.readInt();
        this.name = parcel.readString();
        this.extraEffects = parcel.createTypedArrayList(Item.Effect.CREATOR);
    }

    public ItemObject(ItemObject itemObject) {
        this.f98id = 0;
        this.extraEffects = new ArrayList(0);
        this.name = itemObject.name;
        this.itemId = itemObject.itemId;
        this.extraEffects = copyEffects(itemObject.extraEffects);
    }

    private List<Item.Effect> copyEffects(List<Item.Effect> list) {
        ArrayList arrayList = new ArrayList();
        for (Item.Effect effect : list) {
            Item.Effect effect2 = new Item.Effect();
            effect2.type = effect.type;
            effect2.attrType = effect.attrType;
            effect2.attr = effect.attr;
            effect2.status = effect.status;
            effect2.monsterId = effect.monsterId;
            effect2.monsterType = effect.monsterType;
            effect2.value = effect.value;
            arrayList.add(effect2);
        }
        return arrayList;
    }

    public Item getBaseItem(Context context) {
        if (this.mItem == null) {
            this.mItem = ItemRepository.getItem(context, this.itemId);
        }
        return this.mItem;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.f98id);
        parcel.writeInt(this.itemId);
        parcel.writeString(this.name);
        parcel.writeTypedList(this.extraEffects);
    }

    public void setEnchantsFromPersister(String str) throws NumberFormatException {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.extraEffects = new ArrayList();
        String[] strArrSplit = PATTERN_COMMA_SEPARATE.split(str, 0);
        for (int i = 0; i < strArrSplit.length - 2; i += 3) {
            Item.Effect effect = new Item.Effect();
            String str2 = strArrSplit[i];
            String str3 = strArrSplit[i + 1];
            int i2 = Integer.parseInt(strArrSplit[i + 2]);
            if (str2.equals(ExifInterface.GPS_MEASUREMENT_IN_PROGRESS)) {
                effect.type = Item.Effect.Type.ATTRIBUTE;
                effect.attr = GameChar.Attribute.valueOf(str3);
            } else if (str2.equals(ExifInterface.LATITUDE_SOUTH)) {
                effect.type = Item.Effect.Type.STATUS;
                effect.status = GameChar.Status.valueOf(str3);
            } else if (str2.equals("T")) {
                effect.type = Item.Effect.Type.ATTR_TYPE;
                effect.attrType = AttrType.valueOf(str3);
            }
            effect.value = i2;
            this.extraEffects.add(effect);
        }
    }

    public String getEnchantsForPersister() {
        List<Item.Effect> list = this.extraEffects;
        if (list == null || list.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Item.Effect effect : this.extraEffects) {
            switch (effect.type) {
                case ATTRIBUTE:
                    sb.append("A,");
                    sb.append(effect.attr.name());
                    sb.append(',');
                    sb.append(effect.value);
                    sb.append(',');
                    break;
                case ATTR_TYPE:
                    sb.append("T,");
                    sb.append(effect.attrType.name());
                    sb.append(',');
                    sb.append(effect.value);
                    sb.append(',');
                    break;
                case STATUS:
                    sb.append("S,");
                    sb.append(effect.status.name());
                    sb.append(',');
                    sb.append(effect.value);
                    sb.append(',');
                    break;
                case KILLER:
                case RESTORE_HP:
                case RESTORE_MP:
                    break;
                default:
                    sb.append(',');
                    sb.append(effect.value);
                    sb.append(',');
                    break;
            }
        }
        return sb.toString();
    }

    public int getSellPrice(Context context) {
        return getBuyPrice(context) / 2;
    }

    public int getBuyPrice(Context context) {
        int iPow;
        int i = getBaseItem(context).price;
        List<Item.Effect> list = this.extraEffects;
        if (list == null || list.isEmpty()) {
            return i;
        }
        int i2 = 0;
        List<Item.Effect> list2 = this.extraEffects;
        if (list2 != null) {
            int i3 = 2;
            int i4 = 0;
            for (Item.Effect effect : list2) {
                if (effect.attr == null && effect.status == null) {
                    i3 *= 2;
                } else if (effect.status == GameChar.Status.WEAPON_MAGIC_BONUS || effect.status == GameChar.Status.ARMOR_MAGIC_BONUS || effect.status == GameChar.Status.SHIELD_MAGIC_BONUS) {
                    i4 = effect.value;
                } else {
                    double d = i3;
                    double dPow = Math.pow(1.5d, effect.value);
                    Double.isNaN(d);
                    i3 = (int) (d * dPow);
                }
            }
            iPow = i3;
            i2 = i4;
        } else {
            iPow = 2;
        }
        if (i2 > 0) {
            iPow *= ((int) Math.pow(2.0d, i2)) + 2;
        }
        long j = i;
        return (int) Math.min((2 * j) + (j * iPow), MAX_BUY_PRICE);
    }

    public String getName(Context context) {
        if (!TextUtils.isEmpty(this.name)) {
            return this.name;
        }
        return getOriginalName(context);
    }

    public String getOriginalName(Context context) {
        int i;
        List<Item.Effect> list = this.extraEffects;
        if (list == null || list.isEmpty()) {
            return getBaseItem(context).name;
        }
        String string = context.getString(C0380R.string.item_object_name_separator);
        StringBuilder sb = new StringBuilder(getBaseItem(context).name);
        StringBuilder sb2 = new StringBuilder();
        int i2 = 0;
        for (Item.Effect effect : this.extraEffects) {
            if (effect.type == Item.Effect.Type.ATTR_TYPE) {
                switch (effect.attrType) {
                    case FIRE:
                        i = C0380R.string.lbl_item_name_attr_type_fire;
                        break;
                    case WATER:
                        i = C0380R.string.lbl_item_name_attr_type_water;
                        break;
                    case WIND:
                        i = C0380R.string.lbl_item_name_attr_type_wind;
                        break;
                    default:
                        i = 0;
                        break;
                }
                if (i != 0) {
                    sb.insert(0, context.getString(i));
                }
            } else if (effect.type == Item.Effect.Type.ATTRIBUTE) {
                sb2.append(effect.attr.name().charAt(0));
                sb2.append(effect.value);
            } else if (effect.status == GameChar.Status.WEAPON_MAGIC_BONUS || effect.status == GameChar.Status.ARMOR_MAGIC_BONUS || effect.status == GameChar.Status.SHIELD_MAGIC_BONUS) {
                i2 = effect.value;
            } else {
                if (effect.status == GameChar.Status.ANTI_MAGIC_BONUS) {
                    sb2.append("R");
                } else if (effect.status == GameChar.Status.MAGIC_DAMAGE_BONUS) {
                    sb2.append("MA");
                } else if (effect.status == GameChar.Status.MAGIC_DEFENSE) {
                    sb2.append("MD");
                } else {
                    sb2.append(effect.status.name().charAt(0));
                }
                sb2.append(effect.value);
            }
        }
        if (i2 > 0) {
            sb.append(string);
            sb.append('+');
            sb.append(i2);
        }
        if (sb2.length() > 0) {
            sb.append(string);
            sb.append('(');
            sb.append(sb2.toString());
            sb.append(')');
        }
        return sb.toString();
    }

    public boolean equals(Object obj) {
        return (obj instanceof ItemObject) && this.f98id == ((ItemObject) obj).f98id;
    }

    public int hashCode() {
        return this.f98id;
    }

    public String toString() {
        return "[ItemObject:" + this.f98id + "," + this.itemId + "," + this.mItem + "," + this.extraEffects + "]";
    }

    public boolean isSame(ItemObject itemObject) {
        List<Item.Effect> list;
        if (this.itemId != itemObject.itemId || !TextUtils.equals(this.name, itemObject.name)) {
            return false;
        }
        List<Item.Effect> list2 = this.extraEffects;
        if ((list2 == null || list2.isEmpty()) && ((list = itemObject.extraEffects) == null || list.isEmpty())) {
            return true;
        }
        return this.extraEffects.equals(itemObject.extraEffects);
    }

    public String getDescription(Context context, boolean z) {
        getBaseItem(context);
        String string = context.getString(C0380R.string.item_object_name_separator);
        StringBuilder sb = new StringBuilder();
        if (z) {
            sb.append(context.getString(this.mItem.descriptionStringId));
            sb.append(string);
        }
        AttrStatAdjustments attrStatusAdjustments = getAttrStatusAdjustments(context);
        boolean z2 = true;
        switch (this.mItem.type) {
            case CONSUMABLE:
            case OTHER:
            case RING:
            default:
                z2 = false;
                break;
            case ARMOR:
                sb.append(context.getString(C0380R.string.lbl_item_desc_armor_power, Integer.valueOf(this.mItem.attrBase + attrStatusAdjustments.get(GameChar.Status.ARMOR_MAGIC_BONUS))));
                break;
            case SHIELD:
                sb.append(context.getString(C0380R.string.lbl_item_desc_shield_power, Integer.valueOf(this.mItem.attrBase + attrStatusAdjustments.get(GameChar.Status.SHIELD_MAGIC_BONUS))));
                break;
            case WEAPON:
                if (this.mItem.twoHanded) {
                    sb.append(context.getString(C0380R.string.lbl_item_desc_two_handed));
                    sb.append(context.getString(C0380R.string.lbl_item_desc_comma));
                }
                int i = attrStatusAdjustments.get(GameChar.Status.WEAPON_MAGIC_BONUS);
                sb.append(context.getString(C0380R.string.lbl_item_desc_weapon_power, Integer.valueOf(this.mItem.getMinValue() + i), Integer.valueOf(this.mItem.getMaxValue() + i)));
                break;
        }
        if (sb.length() > 0 && z2) {
            sb.append(context.getString(C0380R.string.lbl_item_desc_period));
        }
        return sb.toString();
    }

    public AttrStatAdjustments getAttrStatusAdjustments(Context context) {
        AttrStatAdjustments attrStatAdjustments = new AttrStatAdjustments();
        attrStatAdjustments.add(getBaseItem(context).effects);
        attrStatAdjustments.add(this.extraEffects);
        return attrStatAdjustments;
    }

    /* JADX WARN: Removed duplicated region for block: B:50:0x0099  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x00bc  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x00c8  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0100  */
    /* JADX WARN: Removed duplicated region for block: B:78:0x0106  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void setRandomEnchant(com.shirobakama.autorpg2.entity.Item r19, int r20, java.util.Random r21) {
        /*
            Method dump skipped, instructions count: 288
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.entity.ItemObject.setRandomEnchant(com.shirobakama.autorpg2.entity.Item, int, java.util.Random):void");
    }

    public static boolean isRareStatus(Enum<?> r1) {
        return r1 == GameChar.Status.HIT_BONUS || r1 == GameChar.Status.MAGIC_DAMAGE_BONUS || r1 == GameChar.Status.WEAPON_MAGIC_BONUS || r1 == GameChar.Status.SHIELD_MAGIC_BONUS || r1 == GameChar.Status.ARMOR_MAGIC_BONUS;
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x007b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean canEquip(android.content.Context r11, com.shirobakama.autorpg2.entity.PlayerChar r12, java.util.List<com.shirobakama.autorpg2.entity.Inventory> r13) {
        /*
            r10 = this;
            com.shirobakama.autorpg2.entity.Item r0 = r10.getBaseItem(r11)
            int[] r1 = com.shirobakama.autorpg2.entity.ItemObject.C03551.$SwitchMap$com$shirobakama$autorpg2$entity$Item$Type
            com.shirobakama.autorpg2.entity.Item$Type r2 = r0.type
            int r2 = r2.ordinal()
            r1 = r1[r2]
            r2 = 0
            r3 = 1
            switch(r1) {
                case 1: goto L30;
                case 2: goto L30;
                case 3: goto L2b;
                case 4: goto L27;
                case 5: goto L23;
                case 6: goto L17;
                default: goto L13;
            }
        L13:
            r1 = 0
            r4 = 0
            r5 = 0
            goto L31
        L17:
            boolean r1 = r0.twoHanded
            if (r1 == 0) goto L1f
            r1 = 1
            r4 = 0
            r5 = 1
            goto L31
        L1f:
            r1 = 1
            r4 = 0
            r5 = 0
            goto L31
        L23:
            r1 = 0
            r4 = 0
            r5 = 1
            goto L31
        L27:
            r1 = 0
            r4 = 1
            r5 = 0
            goto L31
        L2b:
            boolean r11 = r10.canEquipRing(r12)
            return r11
        L30:
            return r2
        L31:
            int r6 = r12.baseStr
            java.util.Iterator r13 = r13.iterator()
        L37:
            boolean r7 = r13.hasNext()
            if (r7 == 0) goto L8c
            java.lang.Object r7 = r13.next()
            com.shirobakama.autorpg2.entity.Inventory r7 = (com.shirobakama.autorpg2.entity.Inventory) r7
            int r8 = r7.f98id
            int r9 = r12.weaponId
            if (r8 != r9) goto L5c
            if (r5 == 0) goto L54
            com.shirobakama.autorpg2.entity.Item r8 = r7.getBaseItem(r11)
            boolean r8 = r8.twoHanded
            if (r8 == 0) goto L54
            r1 = 1
        L54:
            if (r1 != 0) goto L59
            r8 = r1
            r1 = 1
            goto L7d
        L59:
            r8 = r1
            r1 = 0
            goto L7d
        L5c:
            int r8 = r7.f98id
            int r9 = r12.armorId
            if (r8 != r9) goto L67
            if (r4 != 0) goto L7b
            r8 = r1
            r1 = 1
            goto L7d
        L67:
            int r8 = r7.f98id
            int r9 = r12.shieldId
            if (r8 != r9) goto L72
            if (r5 != 0) goto L7b
            r8 = r1
            r1 = 1
            goto L7d
        L72:
            int r8 = r7.f98id
            int r9 = r12.ringId
            if (r8 != r9) goto L7b
            r8 = r1
            r1 = 1
            goto L7d
        L7b:
            r8 = r1
            r1 = 0
        L7d:
            if (r1 == 0) goto L8a
            com.shirobakama.autorpg2.entity.ItemObject$AttrStatAdjustments r1 = r7.getAttrStatusAdjustments(r11)
            com.shirobakama.autorpg2.entity.GameChar$Attribute r7 = com.shirobakama.autorpg2.entity.GameChar.Attribute.STR
            int r1 = r1.get(r7)
            int r6 = r6 + r1
        L8a:
            r1 = r8
            goto L37
        L8c:
            com.shirobakama.autorpg2.entity.ItemObject$AttrStatAdjustments r11 = r10.getAttrStatusAdjustments(r11)
            com.shirobakama.autorpg2.entity.GameChar$Attribute r13 = com.shirobakama.autorpg2.entity.GameChar.Attribute.STR
            int r11 = r11.get(r13)
            int r6 = r6 + r11
            r11 = 18
            int r11 = java.lang.Math.min(r6, r11)
            boolean r11 = r10.canEquip(r12, r11, r0)
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.entity.ItemObject.canEquip(android.content.Context, com.shirobakama.autorpg2.entity.PlayerChar, java.util.List):boolean");
    }

    public boolean canEquipSingle(Context context, PlayerChar playerChar) {
        return canEquip(playerChar, playerChar.str, getBaseItem(context));
    }

    private boolean canEquip(PlayerChar playerChar, int i, Item item) {
        if (item.classEquipable == null || item.classEquipable.length <= playerChar.clazz.ordinal()) {
            return false;
        }
        int i2 = i * 4;
        boolean z = item.classEquipable[playerChar.clazz.ordinal()];
        if (playerChar.fightingSubClass == GameChar.SubClass.ROGUE) {
            i2 = i * 3;
        }
        return z && i2 >= getRequiredStrX2(item) * 2;
    }

    public int getRequiredStrX2(Item item) {
        int baseRequiredStrX2 = item.getBaseRequiredStrX2();
        ArrayList<Item.Effect> arrayList = new ArrayList(item.effects);
        arrayList.addAll(this.extraEffects);
        for (Item.Effect effect : arrayList) {
            if (effect.type == Item.Effect.Type.STATUS && effect.status == GameChar.Status.LIGHT_WEIGHT) {
                baseRequiredStrX2 -= effect.value * 2;
            }
        }
        if (baseRequiredStrX2 < 3) {
            return 3;
        }
        return baseRequiredStrX2;
    }

    public Rarity getRarity(Context context) {
        Rarity rarity = this.mRarity;
        if (rarity != null) {
            return rarity;
        }
        if (getBaseItem(context).artifact) {
            this.mRarity = Rarity.VERY_RARE;
        } else {
            List<Item.Effect> list = this.extraEffects;
            if (list == null || list.isEmpty()) {
                this.mRarity = Rarity.COMMON;
            } else {
                AttrStatAdjustments attrStatusAdjustments = getAttrStatusAdjustments(context);
                if (attrStatusAdjustments.isEnchanted() || attrStatusAdjustments.get(GameChar.Status.ARMOR_MAGIC_BONUS) > 0 || attrStatusAdjustments.get(GameChar.Status.SHIELD_MAGIC_BONUS) > 0) {
                    this.mRarity = Rarity.RARE;
                } else {
                    this.mRarity = Rarity.UNCOMMON;
                }
            }
        }
        return this.mRarity;
    }
}
