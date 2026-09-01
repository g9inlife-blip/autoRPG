package com.shirobakama.autorpg2.entity;

import android.content.Context;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.Tactics;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class Enemy extends GameChar {
    public Monster monster;
    public boolean running = false;

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public boolean hasEnchantedWeapon() {
        return false;
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public boolean isForward() {
        return true;
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public boolean isPlayer() {
        return false;
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public void setEnchantedWeapon(boolean z) {
    }

    public Enemy() {
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public boolean isAlive() {
        return !this.running && super.isAlive();
    }

    public Enemy(int i, Monster monster, String str) {
        this.index = i;
        this.monster = monster;
        setPropertiesFromMonster();
        this.name = this.monster.name + str;
        this.f93hp = this.maxHp;
        this.f94mp = this.maxMp;
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public Tactics.TacticsValue getFickleness() {
        return Tactics.TacticsValue.AGGRESSIVE;
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public GameChar.MagicResist getMagicResist() {
        return this.monster.magicResist;
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public boolean isResistFor(AttrType attrType) {
        if (this.monster.resistTypes == null) {
            return false;
        }
        return this.monster.resistTypes[attrType.ordinal()];
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public boolean isWeakFor(AttrType attrType) {
        if (this.monster.weakTypes == null) {
            return false;
        }
        return this.monster.weakTypes[attrType.ordinal()];
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public boolean isImmuneFor(AttrType attrType) {
        if (this.monster.immuneTypes == null) {
            return false;
        }
        return this.monster.immuneTypes[attrType.ordinal()];
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public AttrType getAttackAttrType() {
        return this.monster.attackAttrType;
    }

    private void setPropertiesFromMonster() {
        this.clazz = this.monster.clazz;
        this.level = this.monster.level;
        this.str = this.monster.baseStr;
        this.intl = this.monster.baseInt;
        this.agi = this.monster.baseAgi;
        this.vit = this.monster.baseVit;
        this.attackDiceFace = this.monster.attackDiceFace;
        this.attackDiceNum = this.monster.attackDiceNum;
        this.attackBase = this.monster.attackBase;
        if (this.monster.skillIds != null) {
            for (int i : this.monster.skillIds) {
                addAvailableSkillId(i);
            }
        }
        this.imageResId = this.monster.imageResId;
        this.maxHp = calcMaxHp(this.monster.baseVit);
        this.maxMp = calcMaxMp(this.monster.baseInt);
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public Enemy calcStatusInFight(Context context, GameContext gameContext, FightContext fightContext, int i) {
        calcStatus(context, gameContext);
        fightContext.applyEnchantToEnemy(this, i);
        return this;
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public GameChar calcStatus(Context context, GameContext gameContext) {
        clearStatus();
        calcSubLevels();
        setStatus(GameChar.Status.DEFENSE, this.monster.defense);
        setStatus(GameChar.Status.CRITICAL, this.monster.critical);
        setStatus(GameChar.Status.FUMBLE, this.monster.fumble);
        setStatus(GameChar.Status.HIT_BONUS, this.monster.hitBonus);
        setStatus(GameChar.Status.DODGE_BONUS, this.monster.dodgeBonus);
        setStatus(GameChar.Status.SHIELD_DODGE, this.monster.shieldDodge);
        setStatus(GameChar.Status.MAGIC_BONUS, this.monster.magicBonus);
        setStatus(GameChar.Status.MAGIC_DAMAGE_BONUS, this.monster.magicDamage);
        setStatus(GameChar.Status.ANTI_MAGIC_BONUS, this.monster.antiMagic);
        setStatus(GameChar.Status.MAGIC_DEFENSE, this.monster.magicDefense);
        return null;
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    protected void calcSubLevels() {
        this.mSubLevels = this.clazz.getSubLevels(this.level, -1);
        this.fightingSubClass = getSubLevel(GameChar.SubClass.WARRIOR) >= getSubLevel(GameChar.SubClass.ROGUE) ? GameChar.SubClass.WARRIOR : GameChar.SubClass.ROGUE;
    }

    @Override // com.shirobakama.autorpg2.entity.GameChar
    public String toString() {
        return "[EN:[" + super.toString() + "]," + this.monster + "," + this.running + "]";
    }

    public static String createSuffix(int i) {
        return Character.toString((char) (i + 65));
    }
}
