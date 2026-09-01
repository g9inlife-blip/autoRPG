package com.shirobakama.autorpg2.entity;

import android.content.Context;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.logquest2.C0380R;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class Monster {
    public int antiMagic;
    public AttrType attackAttrType;
    public int attackBase;
    public int attackDiceFace;
    public int attackDiceNum;
    public int baseAgi;
    public int baseInt;
    public int baseStr;
    public int baseVit;
    public GameChar.CharClass clazz;
    public int critical;
    public int defaultPartyMax;
    public int defaultPartyMin;
    public int defense;
    public int descriptionId;
    public int dodgeBonus;
    public int drawableId;
    public int exp;
    public int fumble;
    public int goldBase;
    public int goldFace;
    public int goldNum;
    public int hitBonus;

    /* renamed from: id */
    public int f105id;
    public int imageResId;
    public boolean[] immuneTypes;
    public MonsterIntelligence intelligence;
    public int level;
    public boolean longRange;
    public int magicBonus;
    public int magicDamage;
    public int magicDefense;
    public GameChar.MagicResist magicResist = GameChar.MagicResist.NONE;
    public String name;
    public int nameStringId;
    public ItemDrop[] placedItemDrops;
    public ItemDrop[] randomItemDrops;
    public boolean[] resistTypes;
    public int shieldDodge;
    public int[] skillIds;
    public String symbol;
    public int thumbnailImageResId;
    public MonsterType type;
    public boolean[] weakTypes;

    public enum MonsterIntelligence {
        NONE,
        NORMAL,
        HIGH
    }

    public enum MonsterType {
        ANIMAL(C0380R.string.res_monster_type_animal),
        SLIME(C0380R.string.res_monster_type_slime),
        INSECT(C0380R.string.res_monster_type_insect),
        DEMI_HUMAN(C0380R.string.res_monster_type_demi_human),
        PLANT(C0380R.string.res_monster_type_plant),
        UNDEAD(C0380R.string.res_monster_type_undead),
        MAGICAL(C0380R.string.res_monster_type_magical),
        CREATED(C0380R.string.res_monster_type_created),
        OTHER(C0380R.string.res_monster_type_other),
        DEMON(C0380R.string.res_monster_type_demon),
        HUMAN(C0380R.string.res_monster_type_human),
        SEA_ANIMAL(C0380R.string.res_monster_type_sea_animal),
        DRAGON(C0380R.string.res_monster_type_dragon),
        LYCANSLOPE(C0380R.string.res_monster_type_lycanthrope),
        GIANT(C0380R.string.res_monster_type_giant);

        private String mString;
        private int mStringId;

        MonsterType(int i) {
            this.mStringId = i;
        }

        public String getString(Context context) {
            if (this.mString == null) {
                this.mString = context.getString(this.mStringId);
            }
            return this.mString;
        }

        public int getNumberStrId() {
            return (this == HUMAN || this == DEMI_HUMAN) ? C0380R.string.alog_title_fight_number_human : (this == ANIMAL || this == INSECT) ? C0380R.string.alog_title_fight_number_animal : C0380R.string.alog_title_fight_number_monster;
        }
    }

    public String getRemark(Context context) {
        String string;
        String string2;
        String string3;
        StringBuilder sb = new StringBuilder();
        AttrType attrType = this.attackAttrType;
        if (attrType != null) {
            sb.append(context.getString(C0380R.string.res_logview_flog_enemy_remark_attack_attr, attrType.getString(context)));
            sb.append('\n');
        }
        if (this.longRange) {
            sb.append(context.getString(C0380R.string.res_logview_flog_enemy_remark_long_range));
            sb.append('\n');
        }
        if (this.magicResist == GameChar.MagicResist.IMMUNE) {
            sb.append(context.getString(C0380R.string.res_logview_flog_enemy_remark_magic_resist_immune));
            sb.append('\n');
        } else if (this.magicResist == GameChar.MagicResist.FULL) {
            sb.append(context.getString(C0380R.string.res_logview_flog_enemy_remark_magic_resist_full));
            sb.append('\n');
        } else if (this.magicResist == GameChar.MagicResist.HALF) {
            sb.append(context.getString(C0380R.string.res_logview_flog_enemy_remark_magic_resist_half));
            sb.append('\n');
        }
        if (this.immuneTypes != null) {
            for (int i = 0; i < AttrType.VALUES.length; i++) {
                if (this.immuneTypes[i] && (string3 = AttrType.VALUES[i].getString(context)) != null) {
                    sb.append(context.getString(C0380R.string.res_logview_flog_enemy_remark_immune_attr, string3));
                    sb.append('\n');
                }
            }
        }
        if (this.resistTypes != null) {
            for (int i2 = 0; i2 < AttrType.VALUES.length; i2++) {
                if (this.resistTypes[i2] && (string2 = AttrType.VALUES[i2].getString(context)) != null) {
                    sb.append(context.getString(C0380R.string.res_logview_flog_enemy_remark_resist_attr, string2));
                    sb.append('\n');
                }
            }
        }
        if (this.weakTypes != null) {
            for (int i3 = 0; i3 < AttrType.VALUES.length; i3++) {
                if (this.weakTypes[i3] && (string = AttrType.VALUES[i3].getString(context)) != null) {
                    sb.append(context.getString(C0380R.string.res_logview_flog_enemy_remark_weak_attr, string));
                    sb.append('\n');
                }
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public boolean equals(Object obj) {
        return (obj instanceof Monster) && this.f105id == ((Monster) obj).f105id;
    }

    public int hashCode() {
        return this.f105id;
    }

    public String getDescription(Context context) {
        return context.getString(this.descriptionId);
    }
}
