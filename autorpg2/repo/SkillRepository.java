package com.shirobakama.autorpg2.repo;

import android.annotation.SuppressLint;
import android.content.Context;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.Skill;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public final class SkillRepository {
    protected static final String TAG = "logq-townrepo";
    private static Map<Integer, Skill> skills;
    private static List<Skill>[] skillsForSubClass = new List[GameChar.SubClass.VALUES.length];
    private static Map<String, Skill> skillsForSymbol = null;

    private SkillRepository() {
    }

    @SuppressLint({"UseSparseArrays"})
    public static void initialize(Context context) {
        if (skills != null) {
            return;
        }
        skills = new HashMap();
        SkillDb.initialize();
        for (int i = 0; i < GameChar.SubClass.VALUES.length; i++) {
            skillsForSubClass[i] = new ArrayList();
        }
        for (Skill skill : skills.values()) {
            skill.name = context.getString(skill.nameStringId);
            if (skill.clazz != null) {
                skillsForSubClass[skill.clazz.ordinal()].add(skill);
            }
        }
        for (int i2 = 0; i2 < GameChar.SubClass.VALUES.length; i2++) {
            Collections.sort(skillsForSubClass[i2]);
        }
    }

    static void addSkill(Skill skill) {
        skills.put(Integer.valueOf(skill.f107id), skill);
    }

    public static Skill getSkill(Context context, int i) {
        if (skills == null) {
            initialize(context);
        }
        return skills.get(Integer.valueOf(i));
    }

    static void addStatusToSkill(int i, GameChar.Status status) {
        Skill skill = skills.get(Integer.valueOf(i));
        if (skill.targetStatus == null) {
            skill.targetStatus = new LinkedList();
        }
        if (status == GameChar.Status.ARMOR_MAGIC_BONUS || status == GameChar.Status.ANTI_MAGIC_BONUS) {
            skill.isUsefulForSupporter = true;
        }
        skill.targetStatus.add(status);
    }

    public static List<Skill> getSkillForSubClass(Context context, GameChar.SubClass subClass) {
        if (skills == null) {
            initialize(context);
        }
        return skillsForSubClass[subClass.ordinal()];
    }

    public static Skill getUpperSkill(Context context, int i) {
        Skill skill = getSkill(context, i);
        if (skillsForSymbol == null) {
            skillsForSymbol = new HashMap();
            for (Skill skill2 : skills.values()) {
                skillsForSymbol.put(skill2.symbol, skill2);
            }
        }
        char cCharAt = skill.symbol.charAt(skill.symbol.length() - 1);
        return skillsForSymbol.get((cCharAt >= '1' && cCharAt <= '9') ? skill.symbol.substring(0, skill.symbol.length() - 1) + ((cCharAt - '1') + 1 + 1) : skill.symbol + "_2");
    }

    public static void flush() {
        skills = null;
    }
}
