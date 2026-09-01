package com.shirobakama.autorpg2.adventure;

import android.content.Context;
import com.shirobakama.autorpg2.adventure.Thrower;
import com.shirobakama.autorpg2.entity.AdventureContext;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.Skill;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public final class EngineUtil {
    private static final int AVERAGE_10_ATTR_BONUS = getFixed10AttrBonus(11);

    public static int getFixed10AttrBonus(int i) {
        return (i - 2) * 4;
    }

    public static int getFixed10LevelBonus(int i) {
        return (i - 1) * 2;
    }

    private EngineUtil() {
    }

    public static int getRandomMinMax(Random random, int i, int i2) {
        return i == i2 ? i : i + random.nextInt((i2 - i) + 1);
    }

    public static int getAttrBonus(Random random, int i) {
        int fixed10AttrBonus = getFixed10AttrBonus(i);
        int i2 = fixed10AttrBonus / 10;
        if (random.nextInt(10) < fixed10AttrBonus - (i2 * 10)) {
            i2++;
        }
        if (i2 < 0) {
            return 0;
        }
        return i2;
    }

    public static int getFixed10AttrBonusAroundZero(int i) {
        return getFixed10AttrBonus(i) - AVERAGE_10_ATTR_BONUS;
    }

    public static int getLevelBonus(Random random, int i) {
        int fixed10LevelBonus = getFixed10LevelBonus(i);
        int i2 = fixed10LevelBonus / 10;
        return ((float) random.nextInt(10)) < ((float) (fixed10LevelBonus - (i2 * 10))) ? i2 + 1 : i2;
    }

    public static String processSkillCure(GameChar gameChar, List<? extends GameChar> list, Skill skill, Thrower thrower, Random random, AdventureContext adventureContext, Context context) {
        int attrBonus;
        gameChar.f94mp -= skill.f108mp;
        StringBuffer stringBuffer = new StringBuffer();
        int attr = gameChar.getAttr(skill.baseAttr);
        Thrower.ThrowResult throwResultAttributeThrow = thrower.attributeThrow(gameChar, skill.baseAttr, skill.clazz);
        if (throwResultAttributeThrow.fumble) {
            stringBuffer.append(context.getString(skill.isMagic() ? C0380R.string.flog_desc_magic_fumble : C0380R.string.flog_desc_skill_fumble));
        } else {
            stringBuffer.append(adventureContext.getSkillUseAwareCustomized(context, gameChar, skill));
            if (throwResultAttributeThrow.critical) {
                stringBuffer.append(context.getString(skill.isMagic() ? C0380R.string.flog_desc_magic_critical : C0380R.string.flog_desc_skill_critical));
                attrBonus = getAttrBonus(random, attr) + skill.getMaxValue();
            } else {
                attrBonus = 0;
            }
            int status = skill.diceNum * gameChar.getStatus(GameChar.Status.MAGIC_DAMAGE_BONUS);
            for (GameChar gameChar2 : list) {
                if (gameChar2.isAlive()) {
                    int iThrowDiceWithBonus = (attrBonus == 0 ? thrower.throwDiceWithBonus(skill.diceNum, skill.diceFace, attr) + skill.attrBase : attrBonus) + status;
                    if (iThrowDiceWithBonus > gameChar2.maxHp - gameChar2.f93hp) {
                        iThrowDiceWithBonus = gameChar2.maxHp - gameChar2.f93hp;
                    }
                    gameChar2.f93hp += iThrowDiceWithBonus;
                    if (iThrowDiceWithBonus > 0) {
                        stringBuffer.append(context.getString(C0380R.string.flog_desc_skill_hp_cure, gameChar2.name, Integer.valueOf(iThrowDiceWithBonus)));
                        stringBuffer.append(context.getString(C0380R.string.res_sentence_separator));
                    }
                }
            }
        }
        return stringBuffer.toString();
    }

    public static boolean antiGroupThrow(Random random, Thrower thrower, GameChar.Attribute attribute, GameChar gameChar, int i, List<? extends GameChar> list, int i2, boolean z) {
        Thrower.ThrowResult throwResultGenericThrow;
        int size = (((list.size() * 10) / i) + 5) / 10;
        if (size <= 0) {
            size = 1;
        }
        for (int i3 = 0; i3 < size; i3++) {
            GameChar gameChar2 = list.get(random.nextInt(list.size()));
            Thrower.ThrowResult throwResultAttributeThrow = z ? thrower.attributeThrow(gameChar, attribute, gameChar.fightingSubClass) : thrower.genericThrow(gameChar, attribute);
            if (z) {
                throwResultGenericThrow = thrower.attributeThrow(gameChar2, attribute, gameChar2.fightingSubClass);
            } else {
                throwResultGenericThrow = thrower.genericThrow(gameChar2, attribute);
            }
            if (!(throwResultAttributeThrow.critical || throwResultGenericThrow.fumble || !(throwResultGenericThrow.critical || throwResultAttributeThrow.fumble || throwResultAttributeThrow.value + i2 < throwResultGenericThrow.value))) {
                return false;
            }
        }
        return true;
    }

    public static int groupThrow(Random random, Thrower thrower, GameChar.Attribute attribute, List<? extends GameChar> list, List<? extends GameChar> list2, int i, boolean z) {
        List aliveChars = getAliveChars(list);
        List aliveChars2 = getAliveChars(list2);
        Iterator it = aliveChars.iterator();
        int i2 = 0;
        while (it.hasNext()) {
            if (antiGroupThrow(random, thrower, attribute, (GameChar) it.next(), aliveChars.size(), aliveChars2, i, z)) {
                i2++;
            }
        }
        return i2;
    }

    private static <T extends GameChar> List<T> getAliveChars(List<T> list) {
        ArrayList arrayList = new ArrayList(list.size());
        for (T t : list) {
            if (t.isAlive()) {
                arrayList.add(t);
            }
        }
        return arrayList;
    }

    public static int getElementRandom(int[] iArr, Random random) {
        return iArr[random.nextInt(iArr.length)];
    }

    public static <T> T getMemberRandom(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }

    public static <T> T getElementRandom(T[] tArr, Random random) {
        return tArr[random.nextInt(tArr.length)];
    }
}
