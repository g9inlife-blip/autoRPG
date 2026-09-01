package com.shirobakama.autorpg2.adventure;

import com.shirobakama.autorpg2.entity.GameChar;
import java.util.Random;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class Thrower {
    private Random mRandom;

    public Thrower(Random random) {
        this.mRandom = random;
    }

    public int throwDice(int i, int i2) {
        int iNextInt = 0;
        for (int i3 = 0; i3 < i; i3++) {
            iNextInt += this.mRandom.nextInt(i2) + 1;
        }
        return iNextInt;
    }

    public int throwDiceWithBonus(int i, int i2, int i3) {
        int iNextInt = 0;
        for (int i4 = 0; i4 < i; i4++) {
            iNextInt += this.mRandom.nextInt(i2) + 1;
        }
        return iNextInt + EngineUtil.getAttrBonus(this.mRandom, i3);
    }

    public ThrowResult attributeThrow(GameChar gameChar, GameChar.Attribute attribute, GameChar.SubClass subClass) {
        return attributeThrow(gameChar.getAttr(attribute), gameChar.getSubLevel(subClass), 12, 2);
    }

    public ThrowResult attributeFixedLevelThrow(GameChar gameChar, int i, GameChar.Attribute attribute) {
        return attributeThrow(gameChar.getAttr(attribute), i, 12, 2);
    }

    public ThrowResult genericThrow(GameChar gameChar, GameChar.Attribute attribute) {
        return attributeThrow(gameChar.getAttr(attribute), gameChar.level, 12, 2);
    }

    public ThrowResult attributeThrow(GameChar gameChar, GameChar.Attribute attribute, GameChar.SubClass subClass, int i, int i2) {
        return attributeThrow(gameChar.getAttr(attribute), gameChar.getSubLevel(subClass), i, i2);
    }

    private ThrowResult attributeThrow(int i, int i2, int i3, int i4) {
        int iThrowDice = throwDice(2, 6);
        return new ThrowResult(EngineUtil.getAttrBonus(this.mRandom, i) + iThrowDice + EngineUtil.getLevelBonus(this.mRandom, i2), iThrowDice >= i3, iThrowDice <= i4);
    }

    public static class ThrowResult {
        public boolean critical;
        public boolean fumble;
        public int value;

        public ThrowResult() {
        }

        public ThrowResult(int i, boolean z, boolean z2) {
            this.value = i;
            this.critical = z;
            this.fumble = z2;
        }

        public boolean success(int i) {
            return this.critical || this.value >= i;
        }

        public String toString() {
            return "[" + this.value + "," + this.critical + "," + this.fumble + "]";
        }
    }
}
