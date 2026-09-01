package com.shirobakama.autorpg2.entity;

import com.shirobakama.autorpg2.entity.GameChar;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class Enchant {
    public GameChar.Attribute attribute;
    public int causeSkillId;
    public GameChar.IllegalStatus illegalStatus;
    public OtherEffect otherEffect;
    public GameChar.Status status;
    public Target target;
    public int term;
    public int value;

    public enum OtherEffect {
        ENCOUNTER,
        MOVEMENT,
        DETECT,
        HIDING
    }

    public enum Target {
        PLAYER_CHAR,
        PARTY,
        ENEMY,
        ENEMY_PARTY,
        FIELD
    }

    private Enchant(Target target, GameChar.Attribute attribute, GameChar.Status status, GameChar.IllegalStatus illegalStatus, int i, int i2, int i3) {
        this.target = target;
        this.attribute = attribute;
        this.status = status;
        this.illegalStatus = illegalStatus;
        this.value = i;
        this.causeSkillId = i2;
        this.term = i3;
    }

    public Enchant(Target target, GameChar.Attribute attribute, int i, int i2, int i3) {
        this(target, attribute, null, null, i, i2, i3);
    }

    public Enchant(Target target, GameChar.Status status, int i, int i2, int i3) {
        this(target, null, status, null, i, i2, i3);
    }

    public Enchant(Target target, GameChar.IllegalStatus illegalStatus, int i, Skill skill, int i2) {
        this(target, null, null, illegalStatus, i, skill == null ? 0 : skill.f107id, i2);
    }

    public Enchant(Target target, OtherEffect otherEffect, int i, Skill skill, int i2) {
        this(target, null, null, null, i, skill == null ? 0 : skill.f107id, i2);
        this.otherEffect = otherEffect;
    }

    public void apply(GameChar gameChar) {
        GameChar.Attribute attribute = this.attribute;
        if (attribute != null) {
            gameChar.setAttr(attribute, gameChar.getAttr(attribute) + this.value);
            return;
        }
        GameChar.Status status = this.status;
        if (status != null) {
            gameChar.setStatus(status, gameChar.getStatus(status) + this.value);
            if (this.status != GameChar.Status.WEAPON_MAGIC_BONUS || this.value <= 0) {
                return;
            }
            gameChar.setEnchantedWeapon(true);
        }
    }

    public String toString() {
        return "[Enchant:" + this.target + "," + this.attribute + "," + this.status + "," + this.illegalStatus + "," + this.otherEffect + "," + this.value + "," + this.causeSkillId + "," + this.term + "]";
    }
}
