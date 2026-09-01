package com.shirobakama.autorpg2.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.shirobakama.autorpg2.adventure.EngineUtil;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.util.FormatUtil;
import com.shirobakama.logquest2.BuildConfig;
import com.shirobakama.logquest2.C0380R;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class DetailedCharViewHolder extends CharViewHolder {
    public Button btnDetailedStatus;
    public LinearLayout llayDetail;
    public TableLayout tlayDetailedStatusInventories;
    public TableRow trwSkillBonus;
    public TableRow trwStatusBonus;
    public TextView tvAgi;
    public TextView tvAntiBreathAdjust;
    public TextView tvAntiMagicAdjust;
    public TextView tvAttackPower;
    public TextView tvBreathDefense;
    public TextView tvClericAdjust;
    public TextView tvClericExp;
    public TextView tvClericLevel;
    public TextView tvClericRatio;
    public TextView tvCurrentAgi;
    public TextView tvCurrentInt;
    public TextView tvCurrentStr;
    public TextView tvCurrentVit;
    public TextView tvDamageBonus;
    public TextView tvDefensePower;
    public TextView tvDodgeAdjust;
    public TextView tvHitAdjust;
    public TextView tvInt;
    public TextView tvMagicDamageBonus;
    public TextView tvMagicDefense;
    public TextView tvMaxAttackTimes;
    public TextView tvRogueExp;
    public TextView tvRogueLevel;
    public TextView tvRogueRatio;
    public TextView tvShieldDodge;
    public TextView tvSkillBonus;
    public TextView tvSorcererAdjust;
    public TextView tvSorcererExp;
    public TextView tvSorcererLevel;
    public TextView tvSorcererRatio;
    public TextView tvStatusBonus;
    public TextView tvStr;
    public TextView tvVit;
    public TextView tvWarriorExp;
    public TextView tvWarriorLevel;
    public TextView tvWarriorRatio;

    public DetailedCharViewHolder(View view, int i) {
        super(view, i);
        this.tlayDetailedStatusInventories = (TableLayout) view.findViewById(C0380R.id.tlayDetailedStatusInventories);
        this.tvWarriorRatio = (TextView) view.findViewById(C0380R.id.tvWarriorRatio);
        this.tvWarriorLevel = (TextView) view.findViewById(C0380R.id.tvWarriorLevel);
        this.tvRogueRatio = (TextView) view.findViewById(C0380R.id.tvRogueRatio);
        this.tvRogueLevel = (TextView) view.findViewById(C0380R.id.tvRogueLevel);
        this.tvClericRatio = (TextView) view.findViewById(C0380R.id.tvClericRatio);
        this.tvClericLevel = (TextView) view.findViewById(C0380R.id.tvClericLevel);
        this.tvSorcererRatio = (TextView) view.findViewById(C0380R.id.tvSorcererRatio);
        this.tvSorcererLevel = (TextView) view.findViewById(C0380R.id.tvSorcererLevel);
        this.tvAttackPower = (TextView) view.findViewById(C0380R.id.tvAttackPower);
        this.tvDefensePower = (TextView) view.findViewById(C0380R.id.tvDefensePower);
        this.tvShieldDodge = (TextView) view.findViewById(C0380R.id.tvShieldDodge);
        this.tvStr = (TextView) view.findViewById(C0380R.id.tvStr);
        this.tvCurrentStr = (TextView) view.findViewById(C0380R.id.tvCurrentStr);
        this.tvInt = (TextView) view.findViewById(C0380R.id.tvInt);
        this.tvCurrentInt = (TextView) view.findViewById(C0380R.id.tvCurrentInt);
        this.tvAgi = (TextView) view.findViewById(C0380R.id.tvAgi);
        this.tvCurrentAgi = (TextView) view.findViewById(C0380R.id.tvCurrentAgi);
        this.tvVit = (TextView) view.findViewById(C0380R.id.tvVit);
        this.tvCurrentVit = (TextView) view.findViewById(C0380R.id.tvCurrentVit);
        this.tvStatusBonus = (TextView) view.findViewById(C0380R.id.tvStatusBonus);
        this.tvSkillBonus = (TextView) view.findViewById(C0380R.id.tvSkillBonus);
        this.trwStatusBonus = (TableRow) view.findViewById(C0380R.id.trwStatusBonus);
        this.trwSkillBonus = (TableRow) view.findViewById(C0380R.id.trwSkillBonus);
        this.btnDetailedStatus = (Button) view.findViewById(C0380R.id.btnDetailedStatus);
        this.llayDetail = (LinearLayout) view.findViewById(C0380R.id.llayDetail);
        this.tvMaxAttackTimes = (TextView) view.findViewById(C0380R.id.tvMaxAttackTimes);
        this.tvDamageBonus = (TextView) view.findViewById(C0380R.id.tvDamageBonus);
        this.tvHitAdjust = (TextView) view.findViewById(C0380R.id.tvHitAdjust);
        this.tvDodgeAdjust = (TextView) view.findViewById(C0380R.id.tvDodgeAdjust);
        this.tvSorcererAdjust = (TextView) view.findViewById(C0380R.id.tvSorcererAdjust);
        this.tvClericAdjust = (TextView) view.findViewById(C0380R.id.tvClericAdjust);
        this.tvMagicDamageBonus = (TextView) view.findViewById(C0380R.id.tvMagicDamageBonus);
        this.tvAntiMagicAdjust = (TextView) view.findViewById(C0380R.id.tvAntiMagicAdjust);
        this.tvAntiBreathAdjust = (TextView) view.findViewById(C0380R.id.tvAntiBreathAdjust);
        this.tvMagicDefense = (TextView) view.findViewById(C0380R.id.tvMagicDefense);
        this.tvBreathDefense = (TextView) view.findViewById(C0380R.id.tvBreathDefense);
        this.tvWarriorExp = (TextView) view.findViewById(C0380R.id.tvWarriorExp);
        this.tvRogueExp = (TextView) view.findViewById(C0380R.id.tvRogueExp);
        this.tvClericExp = (TextView) view.findViewById(C0380R.id.tvClericExp);
        this.tvSorcererExp = (TextView) view.findViewById(C0380R.id.tvSorcererExp);
    }

    @Override // com.shirobakama.autorpg2.view.CharViewHolder
    public void show(Context context, GameContext gameContext, PlayerChar playerChar) {
        super.show(context, gameContext, playerChar);
        if (gameContext != null) {
            this.tlayDetailedStatusInventories.setVisibility(0);
        } else {
            this.tlayDetailedStatusInventories.setVisibility(8);
        }
        int[] expRatio = playerChar.clazz.getExpRatio();
        this.tvWarriorRatio.setText(getSubclassRatio(context, expRatio[GameChar.SubClass.WARRIOR.ordinal()]));
        this.tvRogueRatio.setText(getSubclassRatio(context, expRatio[GameChar.SubClass.ROGUE.ordinal()]));
        this.tvClericRatio.setText(getSubclassRatio(context, expRatio[GameChar.SubClass.CLERIC.ordinal()]));
        this.tvSorcererRatio.setText(getSubclassRatio(context, expRatio[GameChar.SubClass.SORCERER.ordinal()]));
        this.tvWarriorLevel.setText(getSubclassLevel(expRatio[GameChar.SubClass.WARRIOR.ordinal()], playerChar.getSubLevel(GameChar.SubClass.WARRIOR)));
        this.tvRogueLevel.setText(getSubclassLevel(expRatio[GameChar.SubClass.ROGUE.ordinal()], playerChar.getSubLevel(GameChar.SubClass.ROGUE)));
        this.tvClericLevel.setText(getSubclassLevel(expRatio[GameChar.SubClass.CLERIC.ordinal()], playerChar.getSubLevel(GameChar.SubClass.CLERIC)));
        this.tvSorcererLevel.setText(getSubclassLevel(expRatio[GameChar.SubClass.SORCERER.ordinal()], playerChar.getSubLevel(GameChar.SubClass.SORCERER)));
        this.tvAttackPower.setText(playerChar.getAttackPowerAsString());
        this.tvDefensePower.setText(playerChar.getDefensePowerAsString());
        this.tvShieldDodge.setText(playerChar.getShieldDodgeAsString());
        this.tvStr.setText(Integer.toString(playerChar.baseStr));
        this.tvCurrentStr.setText(Integer.toString(playerChar.str));
        this.tvInt.setText(Integer.toString(playerChar.baseInt));
        this.tvCurrentInt.setText(Integer.toString(playerChar.intl));
        this.tvAgi.setText(Integer.toString(playerChar.baseAgi));
        this.tvCurrentAgi.setText(Integer.toString(playerChar.agi));
        this.tvVit.setText(Integer.toString(playerChar.baseVit));
        this.tvCurrentVit.setText(Integer.toString(playerChar.vit));
        this.tvStatusBonus.setText(Integer.toString(playerChar.statusBonus >= 0 ? playerChar.statusBonus : 0));
        this.tvSkillBonus.setText(Integer.toString(playerChar.getLernableSkillCount(context)));
        int fixed10AttrBonus = (EngineUtil.getFixed10AttrBonus(playerChar.str) / 10) + playerChar.getStatus(GameChar.Status.DAMAGE);
        int fixed10AttrBonus2 = ((EngineUtil.getFixed10AttrBonus(playerChar.agi) + EngineUtil.getFixed10LevelBonus(playerChar.getSubLevel(playerChar.fightingSubClass))) / 10) + playerChar.getStatus(GameChar.Status.HIT_BONUS) + playerChar.getStatus(GameChar.Status.WEAPON_MAGIC_BONUS);
        int fixed10AttrBonus3 = ((EngineUtil.getFixed10AttrBonus(playerChar.agi) + EngineUtil.getFixed10LevelBonus(playerChar.getSubLevel(playerChar.fightingSubClass))) / 10) + playerChar.getStatus(GameChar.Status.DODGE_BONUS);
        int fixed10AttrBonus4 = ((EngineUtil.getFixed10AttrBonus(playerChar.intl) + EngineUtil.getFixed10LevelBonus(playerChar.getSubLevel(GameChar.SubClass.SORCERER))) / 10) + playerChar.getStatus(GameChar.Status.MAGIC_BONUS);
        int fixed10AttrBonus5 = ((EngineUtil.getFixed10AttrBonus(playerChar.intl) + EngineUtil.getFixed10LevelBonus(playerChar.getSubLevel(GameChar.SubClass.CLERIC))) / 10) + playerChar.getStatus(GameChar.Status.MAGIC_BONUS);
        int fixed10AttrBonus6 = (EngineUtil.getFixed10AttrBonus(playerChar.intl) / 10) + playerChar.getStatus(GameChar.Status.MAGIC_DAMAGE_BONUS);
        int fixed10AttrBonus7 = ((EngineUtil.getFixed10AttrBonus(playerChar.intl) + EngineUtil.getFixed10LevelBonus(playerChar.level)) / 10) + playerChar.getStatus(GameChar.Status.ARMOR_MAGIC_BONUS) + playerChar.getStatus(GameChar.Status.ANTI_MAGIC_BONUS);
        int fixed10AttrBonus8 = ((EngineUtil.getFixed10AttrBonus(playerChar.vit) + EngineUtil.getFixed10LevelBonus(playerChar.level)) / 10) + playerChar.getStatus(GameChar.Status.ARMOR_MAGIC_BONUS);
        int status = playerChar.getStatus(GameChar.Status.ARMOR_MAGIC_BONUS) + playerChar.getStatus(GameChar.Status.MAGIC_DEFENSE);
        int status2 = playerChar.getStatus(GameChar.Status.ARMOR_MAGIC_BONUS);
        this.tvMaxAttackTimes.setText(Integer.toString(playerChar.getMaxAttackTimes()));
        this.tvDamageBonus.setText(FormatUtil.getAdjustDesc(fixed10AttrBonus));
        this.tvHitAdjust.setText(FormatUtil.getAdjustDesc(fixed10AttrBonus2));
        this.tvDodgeAdjust.setText(FormatUtil.getAdjustDesc(fixed10AttrBonus3));
        this.tvSorcererAdjust.setText(FormatUtil.getAdjustDesc(fixed10AttrBonus4));
        this.tvClericAdjust.setText(FormatUtil.getAdjustDesc(fixed10AttrBonus5));
        this.tvMagicDamageBonus.setText(FormatUtil.getAdjustDesc(fixed10AttrBonus6));
        this.tvAntiMagicAdjust.setText(FormatUtil.getAdjustDesc(fixed10AttrBonus7));
        this.tvAntiBreathAdjust.setText(FormatUtil.getAdjustDesc(fixed10AttrBonus8));
        this.tvMagicDefense.setText(Integer.toString(status));
        this.tvBreathDefense.setText(Integer.toString(status2));
        int[] iArrCalcSubLevels = playerChar.clazz.calcSubLevels(playerChar.level, playerChar.exp, null);
        this.tvWarriorExp.setText(Integer.toString(iArrCalcSubLevels[GameChar.SubClass.WARRIOR.ordinal()]));
        this.tvRogueExp.setText(Integer.toString(iArrCalcSubLevels[GameChar.SubClass.ROGUE.ordinal()]));
        this.tvClericExp.setText(Integer.toString(iArrCalcSubLevels[GameChar.SubClass.CLERIC.ordinal()]));
        this.tvSorcererExp.setText(Integer.toString(iArrCalcSubLevels[GameChar.SubClass.SORCERER.ordinal()]));
    }

    private String getSubclassLevel(int i, int i2) {
        return i <= 0 ? BuildConfig.FLAVOR : Integer.toString(i2);
    }

    private CharSequence getSubclassRatio(Context context, int i) {
        String string = context.getString(C0380R.string.lbl_subclass_ratio);
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < i; i2++) {
            sb.append(string);
        }
        return sb.toString();
    }
}
