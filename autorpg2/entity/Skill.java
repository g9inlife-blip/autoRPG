package com.shirobakama.autorpg2.entity;

import android.content.Context;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.logquest2.C0380R;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class Skill implements Comparable<Skill> {
    public static final int DEATH_RESIST_DICE_BONUS = 4;
    public int attrBase;
    public AttrType attrType;
    public GameChar.Attribute baseAttr;
    public GameChar.SubClass clazz;
    public SkillContext context;
    public int descriptionStringId;
    public int diceFace;
    public int diceNum;
    public int group;

    /* renamed from: id */
    public int f107id;
    public boolean isDoubleAttack;
    public boolean isEnchantWeapon;
    public boolean isForUndead;
    public boolean isMultiAttack;

    /* renamed from: mp */
    public int f108mp;
    public String name;
    public int nameStringId;
    public GameChar.Attribute requiredAttr1;
    public GameChar.Attribute requiredAttr2;
    public int requiredAttrValue1;
    public int requiredAttrValue2;
    public transient SkillCustomization skillCustomization;
    public String symbol;
    public List<GameChar.Status> targetStatus;
    public int term;
    public SkillType type;
    public int useStringId;
    public Item.WeaponType weaponType;
    public int preSkillId = 0;
    public boolean isUsefulForSupporter = false;
    public boolean hideInChart = true;

    public enum SkillContext {
        ADVENTURE,
        FIGHT,
        BOTH
    }

    public enum SkillType {
        MY_STATUS,
        STATUS,
        STATUS_ALL,
        CURE,
        CURE_ALL,
        DAMAGE,
        DAMAGE_ALL,
        ADD_ATTACK,
        OTHER
    }

    @Override // java.lang.Comparable
    public int compareTo(Skill skill) {
        return this.f107id - skill.f107id;
    }

    public boolean equals(Object obj) {
        return (obj instanceof Skill) && compareTo((Skill) obj) == 0;
    }

    public int hashCode() {
        return this.f107id;
    }

    public String getDescription(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(this.descriptionStringId));
        sb.append('\n');
        int length = sb.length();
        switch (this.type) {
            case ADD_ATTACK:
                List<GameChar.Status> list = this.targetStatus;
                if (list == null || list.isEmpty()) {
                    int i = this.attrBase;
                    if (i > 0) {
                        sb.append(context.getString(C0380R.string.res_skill_desc_add_attack_damage, Integer.valueOf(i)));
                        break;
                    }
                } else {
                    sb.append(context.getString(C0380R.string.res_skill_desc_add_attack_status));
                    addStatusDesc(context, sb);
                    break;
                }
                break;
            case CURE:
                sb.append(context.getString(C0380R.string.res_skill_desc_cure));
                sb.append(context.getString(C0380R.string.res_skill_desc_from_to, Integer.valueOf(getMinValue()), Integer.valueOf(getMaxValue())));
                break;
            case CURE_ALL:
                sb.append(context.getString(C0380R.string.res_skill_desc_all_cure));
                sb.append(context.getString(C0380R.string.res_skill_desc_from_to, Integer.valueOf(getMinValue()), Integer.valueOf(getMaxValue())));
                break;
            case DAMAGE:
                sb.append(context.getString(C0380R.string.res_skill_desc_damage));
                sb.append(context.getString(C0380R.string.res_skill_desc_from_to, Integer.valueOf(getMinValue()), Integer.valueOf(getMaxValue())));
                break;
            case DAMAGE_ALL:
                sb.append(context.getString(C0380R.string.res_skill_desc_all_damage));
                sb.append(context.getString(C0380R.string.res_skill_desc_from_to, Integer.valueOf(getMinValue()), Integer.valueOf(getMaxValue())));
                break;
            case MY_STATUS:
                sb.append(context.getString(C0380R.string.res_skill_desc_my_status));
                addStatusDesc(context, sb);
                break;
            case STATUS:
                sb.append(context.getString(C0380R.string.res_skill_desc_add_status));
                addStatusDesc(context, sb);
                break;
            case STATUS_ALL:
                sb.append(context.getString(C0380R.string.res_skill_desc_all_status));
                addStatusDesc(context, sb);
                break;
        }
        if (this.term > 0) {
            if (this.context == SkillContext.FIGHT) {
                sb.append(context.getString(C0380R.string.res_skill_desc_term_fight, Integer.valueOf(this.term)));
            } else if (this.context == SkillContext.ADVENTURE) {
                sb.append(context.getString(C0380R.string.res_skill_desc_term_adventure, Integer.valueOf(this.term)));
            }
        }
        if (sb.length() > length) {
            sb.append('\n');
        }
        switch (this.context) {
            case ADVENTURE:
                sb.append(context.getString(C0380R.string.res_skill_desc_adventure));
                sb.append('\n');
                break;
            case FIGHT:
                sb.append(context.getString(C0380R.string.res_skill_desc_fight));
                sb.append('\n');
                break;
        }
        if (this.requiredAttr1 != null) {
            sb.append(context.getString(C0380R.string.res_skill_desc_required_attr));
            addRequiredAttr(context, sb, this.requiredAttr1, this.requiredAttrValue1);
            if (this.requiredAttr2 != null) {
                sb.append(context.getString(C0380R.string.lbl_item_desc_comma));
                addRequiredAttr(context, sb, this.requiredAttr2, this.requiredAttrValue2);
            }
            sb.append(context.getString(C0380R.string.lbl_item_desc_period));
        }
        return sb.toString();
    }

    private void addRequiredAttr(Context context, StringBuilder sb, GameChar.Attribute attribute, int i) {
        sb.append(attribute.getString(context));
        sb.append(i);
    }

    private void addStatusDesc(Context context, StringBuilder sb) {
        boolean z = true;
        for (GameChar.Status status : this.targetStatus) {
            if (!z) {
                sb.append(context.getString(C0380R.string.lbl_item_desc_comma));
            }
            sb.append(context.getString(this.attrBase >= 0 ? C0380R.string.res_skill_desc_attr_plus : C0380R.string.res_skill_desc_attr_minus, status.getString(context), Integer.valueOf(this.attrBase)));
            z = false;
        }
        sb.append(context.getString(C0380R.string.lbl_item_desc_period));
    }

    public int getMinValue() {
        return this.diceNum + this.attrBase;
    }

    public int getMaxValue() {
        return (this.diceFace * this.diceNum) + this.attrBase;
    }

    public boolean isMagic() {
        return this.clazz == GameChar.SubClass.CLERIC || this.clazz == GameChar.SubClass.SORCERER;
    }

    public boolean isMonsters() {
        return this.clazz == null;
    }

    public boolean canUse(GameChar gameChar) {
        if (gameChar.f94mp < this.f108mp) {
            return false;
        }
        GameChar.Attribute attribute = this.requiredAttr1;
        if (attribute != null && gameChar.getAttr(attribute) < this.requiredAttrValue1) {
            return false;
        }
        GameChar.Attribute attribute2 = this.requiredAttr2;
        return attribute2 == null || gameChar.getAttr(attribute2) >= this.requiredAttrValue2;
    }

    public String toString() {
        return "skill:" + this.symbol;
    }
}
