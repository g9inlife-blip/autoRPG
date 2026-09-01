package com.shirobakama.autorpg2.adventure;

import android.content.Context;
import com.shirobakama.autorpg2.adventure.ActionEvaluation;
import com.shirobakama.autorpg2.entity.AttrType;
import com.shirobakama.autorpg2.entity.Enchant;
import com.shirobakama.autorpg2.entity.Enemy;
import com.shirobakama.autorpg2.entity.FightContext;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.Inventory;
import com.shirobakama.autorpg2.entity.Monster;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.entity.Skill;
import com.shirobakama.autorpg2.entity.Tactics;
import com.shirobakama.autorpg2.repo.SkillRepository;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public abstract class CharacterActionEngine {
    static final int RUNNING_EVALUATE_VALUE = 100;
    protected static final String TAG0 = "charact-engn";
    protected Tactics mBaseTactics;
    protected Context mContext;
    protected List<? extends GameChar> mCounterChars;
    protected int mCounterPower;
    protected Tactics mCurrentSkillTactics;
    protected FightContext mFightContext;
    protected GameChar mGameChar;
    protected List<? extends GameChar> mOurChars;
    protected int mOurPower;
    protected Random mRandom;
    protected List<Skill> mSkills;

    protected abstract boolean evaluateAdvancedTactics(Set<FightActionEvaluation> set, boolean z);

    protected abstract void evaluateItems(Set<FightActionEvaluation> set);

    protected abstract boolean evaluatePhysicalAttack(Set<FightActionEvaluation> set);

    protected abstract void evaluateRunning(Set<FightActionEvaluation> set);

    protected abstract void preprocess();

    protected CharacterActionEngine(Context context, Random random, Tactics tactics, GameChar gameChar) {
        this.mContext = context;
        this.mRandom = random;
        this.mGameChar = gameChar;
        this.mSkills = this.mGameChar.getAvailableSkills(context);
        this.mBaseTactics = tactics;
    }

    public CharacterActionEngine setParties(List<? extends GameChar> list, List<? extends GameChar> list2) {
        this.mOurChars = list;
        this.mCounterChars = list2;
        return this;
    }

    public CharacterActionEngine setFightContext(FightContext fightContext) {
        this.mFightContext = fightContext;
        return this;
    }

    public int evaluatePower(GameChar gameChar) {
        gameChar.power = (gameChar.level * 4) + ((gameChar.str + gameChar.intl + gameChar.agi + gameChar.vit) * 2) + gameChar.f94mp + gameChar.f93hp + (gameChar.getAvailableSkillIds().size() * 2);
        return gameChar.power;
    }

    public int evaluatePower(List<? extends GameChar> list) {
        int iEvaluatePower = 0;
        for (GameChar gameChar : list) {
            if (gameChar.isAlive()) {
                iEvaluatePower = ((iEvaluatePower * 8) + (evaluatePower(gameChar) * 10)) / 10;
            }
        }
        return iEvaluatePower;
    }

    public FightActionEvaluation action() {
        preprocess();
        if (cannotActionByIllegalStatus()) {
            return null;
        }
        this.mCounterPower = evaluatePower(this.mCounterChars);
        this.mOurPower = evaluatePower(this.mOurChars);
        SortedSet<FightActionEvaluation> treeSet = new TreeSet<>();
        boolean zEvaluatePhysicalAttack = evaluatePhysicalAttack(treeSet);
        if (evaluateAdvancedTactics(treeSet, zEvaluatePhysicalAttack)) {
            if (!treeSet.isEmpty()) {
                treeSet.first().setByAdvancedTactics();
            }
        } else {
            evaluateSkills(treeSet, zEvaluatePhysicalAttack);
            evaluateItems(treeSet);
            evaluateRunning(treeSet);
        }
        if (treeSet.isEmpty()) {
            return null;
        }
        FightActionEvaluation fightActionEvaluationFirst = treeSet.first();
        if (fightActionEvaluationFirst.action != ActionEvaluation.Action.RUNNING || fightActionEvaluationFirst.value >= 100) {
            return fightActionEvaluationFirst;
        }
        Iterator<FightActionEvaluation> it = treeSet.iterator();
        it.next();
        return it.hasNext() ? it.next() : null;
    }

    protected FightActionEvaluation addFickleness(FightActionEvaluation fightActionEvaluation) {
        fightActionEvaluation.value += this.mRandom.nextInt(this.mGameChar.getFickleness().getParameter() * 10);
        return fightActionEvaluation;
    }

    private boolean cannotActionByIllegalStatus() {
        return this.mGameChar.getSpecifiedIllegalState(GameChar.IllegalStatus.SLEEP) != null;
    }

    protected int getEstimateAttackTimes10(GameChar gameChar) {
        int maxAttackTimes = this.mGameChar.getMaxAttackTimes();
        if (maxAttackTimes <= 1) {
            return 10;
        }
        return Math.max(10, Math.min((((((EngineUtil.getFixed10AttrBonus(this.mGameChar.agi) + EngineUtil.getFixed10LevelBonus(this.mGameChar.level)) + ((this.mGameChar.getStatus(GameChar.Status.HIT_BONUS) + this.mGameChar.getStatus(GameChar.Status.WEAPON_MAGIC_BONUS)) * 10)) - ((EngineUtil.getFixed10AttrBonus(gameChar.agi) + EngineUtil.getFixed10LevelBonus(gameChar.level)) + ((gameChar.getStatus(GameChar.Status.HIT_BONUS) + gameChar.getStatus(GameChar.Status.WEAPON_MAGIC_BONUS)) * 10))) + 100) * maxAttackTimes) / 30, maxAttackTimes * 10));
    }

    /* JADX WARN: Removed duplicated region for block: B:55:0x005a A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x005e A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:57:0x006a A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:58:0x0076 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0082 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:60:0x008e A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:61:0x009a A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:62:0x00a7 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:63:0x00b4 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:78:0x001f A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void evaluateSkills(java.util.SortedSet<com.shirobakama.autorpg2.adventure.FightActionEvaluation> r5, boolean r6) {
        /*
            r4 = this;
            com.shirobakama.autorpg2.entity.GameChar r0 = r4.mGameChar
            boolean r0 = r0.isPlayer()
            if (r0 != 0) goto L11
            java.util.Random r0 = r4.mRandom
            boolean r0 = r0.nextBoolean()
            if (r0 == 0) goto L11
            return
        L11:
            com.shirobakama.autorpg2.entity.Tactics r0 = r4.mBaseTactics
            com.shirobakama.autorpg2.entity.Tactics r0 = r4.getCurrentSkillTactics(r0)
            r4.mCurrentSkillTactics = r0
            java.util.List<com.shirobakama.autorpg2.entity.Skill> r0 = r4.mSkills
            java.util.Iterator r0 = r0.iterator()
        L1f:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto Lc3
            java.lang.Object r1 = r0.next()
            com.shirobakama.autorpg2.entity.Skill r1 = (com.shirobakama.autorpg2.entity.Skill) r1
            com.shirobakama.autorpg2.entity.Skill$SkillContext r2 = r1.context
            com.shirobakama.autorpg2.entity.Skill$SkillContext r3 = com.shirobakama.autorpg2.entity.Skill.SkillContext.ADVENTURE
            if (r2 != r3) goto L32
            goto L1f
        L32:
            com.shirobakama.autorpg2.entity.GameChar r2 = r4.mGameChar
            boolean r2 = r2.isPlayer()
            if (r2 == 0) goto L43
            com.shirobakama.autorpg2.entity.GameChar r2 = r4.mGameChar
            boolean r2 = r1.canUse(r2)
            if (r2 != 0) goto L4c
            goto L1f
        L43:
            com.shirobakama.autorpg2.entity.GameChar r2 = r4.mGameChar
            int r2 = r2.f94mp
            int r3 = r1.f108mp
            if (r2 >= r3) goto L4c
            goto L1f
        L4c:
            int[] r2 = com.shirobakama.autorpg2.adventure.CharacterActionEngine.C03251.$SwitchMap$com$shirobakama$autorpg2$entity$Skill$SkillType
            com.shirobakama.autorpg2.entity.Skill$SkillType r3 = r1.type
            int r3 = r3.ordinal()
            r2 = r2[r3]
            switch(r2) {
                case 1: goto Lb4;
                case 2: goto La7;
                case 3: goto L9a;
                case 4: goto L8e;
                case 5: goto L82;
                case 6: goto L76;
                case 7: goto L6a;
                case 8: goto L5e;
                case 9: goto L5a;
                default: goto L59;
            }
        L59:
            goto L1f
        L5a:
            r4.evaluateSkillOther(r5, r1)
            goto L1f
        L5e:
            com.shirobakama.autorpg2.entity.Tactics r2 = r4.mCurrentSkillTactics
            com.shirobakama.autorpg2.entity.Tactics$TacticsValue r2 = r2.statusSkill
            com.shirobakama.autorpg2.entity.Tactics$TacticsValue r3 = com.shirobakama.autorpg2.entity.Tactics.TacticsValue.NONE
            if (r2 == r3) goto L1f
            r4.evaluateSkillMyStatus(r5, r1)
            goto L1f
        L6a:
            com.shirobakama.autorpg2.entity.Tactics r2 = r4.mCurrentSkillTactics
            com.shirobakama.autorpg2.entity.Tactics$TacticsValue r2 = r2.damageSkill
            com.shirobakama.autorpg2.entity.Tactics$TacticsValue r3 = com.shirobakama.autorpg2.entity.Tactics.TacticsValue.NONE
            if (r2 == r3) goto L1f
            r4.evaluateSkillDamageAll(r5, r1)
            goto L1f
        L76:
            com.shirobakama.autorpg2.entity.Tactics r2 = r4.mCurrentSkillTactics
            com.shirobakama.autorpg2.entity.Tactics$TacticsValue r2 = r2.damageSkill
            com.shirobakama.autorpg2.entity.Tactics$TacticsValue r3 = com.shirobakama.autorpg2.entity.Tactics.TacticsValue.NONE
            if (r2 == r3) goto L1f
            r4.evaluateSkillDamage(r5, r1)
            goto L1f
        L82:
            com.shirobakama.autorpg2.entity.Tactics r2 = r4.mCurrentSkillTactics
            com.shirobakama.autorpg2.entity.Tactics$TacticsValue r2 = r2.cureSkill
            com.shirobakama.autorpg2.entity.Tactics$TacticsValue r3 = com.shirobakama.autorpg2.entity.Tactics.TacticsValue.NONE
            if (r2 == r3) goto L1f
            r4.evaluateSkillCureAll(r5, r1)
            goto L1f
        L8e:
            com.shirobakama.autorpg2.entity.Tactics r2 = r4.mCurrentSkillTactics
            com.shirobakama.autorpg2.entity.Tactics$TacticsValue r2 = r2.cureSkill
            com.shirobakama.autorpg2.entity.Tactics$TacticsValue r3 = com.shirobakama.autorpg2.entity.Tactics.TacticsValue.NONE
            if (r2 == r3) goto L1f
            r4.evaluateSkillCure(r5, r1)
            goto L1f
        L9a:
            com.shirobakama.autorpg2.entity.Tactics r2 = r4.mCurrentSkillTactics
            com.shirobakama.autorpg2.entity.Tactics$TacticsValue r2 = r2.statusSkill
            com.shirobakama.autorpg2.entity.Tactics$TacticsValue r3 = com.shirobakama.autorpg2.entity.Tactics.TacticsValue.NONE
            if (r2 == r3) goto L1f
            r4.evaluateSkillStatusAll(r5, r1)
            goto L1f
        La7:
            com.shirobakama.autorpg2.entity.Tactics r2 = r4.mCurrentSkillTactics
            com.shirobakama.autorpg2.entity.Tactics$TacticsValue r2 = r2.statusSkill
            com.shirobakama.autorpg2.entity.Tactics$TacticsValue r3 = com.shirobakama.autorpg2.entity.Tactics.TacticsValue.NONE
            if (r2 == r3) goto L1f
            r4.evaluateSkillStatus(r5, r1)
            goto L1f
        Lb4:
            if (r6 == 0) goto L1f
            com.shirobakama.autorpg2.entity.Tactics r2 = r4.mCurrentSkillTactics
            com.shirobakama.autorpg2.entity.Tactics$TacticsValue r2 = r2.attackSkill
            com.shirobakama.autorpg2.entity.Tactics$TacticsValue r3 = com.shirobakama.autorpg2.entity.Tactics.TacticsValue.NONE
            if (r2 == r3) goto L1f
            r4.evaluateSkillAddAttack(r5, r1)
            goto L1f
        Lc3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.adventure.CharacterActionEngine.evaluateSkills(java.util.SortedSet, boolean):void");
    }

    private void evaluateSkillStatus(Set<FightActionEvaluation> set, Skill skill) {
        for (int i = 0; i < this.mOurChars.size(); i++) {
            GameChar gameChar = this.mOurChars.get(i);
            if (gameChar.isAlive()) {
                evaluateSkillStatusToChar(set, skill, gameChar);
            }
        }
    }

    private void evaluateSkillMyStatus(Set<FightActionEvaluation> set, Skill skill) {
        evaluateSkillStatusToChar(set, skill, this.mGameChar);
    }

    protected boolean existsSameEffectForStatusSkill(List<Enchant> list, Skill skill) {
        for (Enchant enchant : list) {
            if (enchant.causeSkillId != 0) {
                if (enchant.causeSkillId == skill.f107id) {
                    return true;
                }
                if (SkillRepository.getSkill(this.mContext, enchant.causeSkillId).group == skill.group && skill.group != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private void evaluateSkillStatusToChar(Set<FightActionEvaluation> set, Skill skill, GameChar gameChar) {
        int parameter;
        if (this.mGameChar.isPlayer() || this.mRandom.nextInt(3) <= 0) {
            List<Enchant> list = this.mGameChar.isPlayer() ? this.mFightContext.enchantsPlayers[gameChar.index] : this.mFightContext.enchantsEnemies[gameChar.index];
            if (skill.group == 0 || !existsSameEffectForStatusSkill(list, skill)) {
                if (skill.isUsefulForSupporter || gameChar.isForward()) {
                    int size = (((skill.targetStatus.size() * skill.attrBase) * 3) - (list.size() * 18)) + ((this.mCounterPower * 22) / this.mOurPower) + (skill.term < 0 ? 10 : skill.term) + (-(skill.f108mp * 2)) + ((this.mGameChar.f94mp * 6) / this.mGameChar.maxMp);
                    if (skill.isEnchantWeapon && !gameChar.hasEnchantedWeapon()) {
                        GameChar.MagicResist magicResist = this.mCounterChars.get(0).getMagicResist();
                        if (magicResist == GameChar.MagicResist.HALF) {
                            size += 20;
                        } else if (magicResist == GameChar.MagicResist.FULL) {
                            size += 80;
                        }
                    }
                    int i = size - (((gameChar.maxHp - gameChar.f93hp) * 10) / gameChar.maxHp);
                    if (this.mCurrentSkillTactics.statusSkill == Tactics.TacticsValue.CONSERVATIVE) {
                        parameter = i - (20 - ((this.mGameChar.f94mp * 20) / this.mGameChar.maxMp));
                        if (parameter < 10) {
                            return;
                        }
                    } else {
                        parameter = i + ((this.mCurrentSkillTactics.statusSkill.getParameter() * 14) - 14);
                    }
                    set.add(addFickleness(new FightActionEvaluation(parameter, gameChar, skill)));
                }
            }
        }
    }

    private void evaluateSkillStatusAll(Set<FightActionEvaluation> set, Skill skill) {
        int parameter;
        if (this.mGameChar.isPlayer() || !this.mRandom.nextBoolean()) {
            List<Enchant> list = this.mGameChar.isPlayer() ? this.mFightContext.enchantsParty : this.mFightContext.enchantsEnemyParty;
            if (skill.group == 0 || !existsSameEffectForStatusSkill(list, skill)) {
                int size = (((skill.targetStatus.size() * skill.attrBase) * 8) - (list.size() * 12)) + ((this.mCounterPower * 22) / this.mOurPower) + (skill.term < 0 ? 10 : skill.term) + (-(skill.f108mp * 2));
                if (this.mCurrentSkillTactics.statusSkill == Tactics.TacticsValue.CONSERVATIVE) {
                    parameter = size - (20 - ((this.mGameChar.f94mp * 20) / this.mGameChar.maxMp));
                    if (parameter < 10) {
                        return;
                    }
                } else {
                    parameter = size + ((this.mGameChar.f94mp * 8) / this.mGameChar.maxMp) + ((this.mCurrentSkillTactics.statusSkill.getParameter() * 15) - 15);
                }
                set.add(addFickleness(new FightActionEvaluation(parameter, null, skill)));
            }
        }
    }

    private void evaluateSkillAddAttack(SortedSet<FightActionEvaluation> sortedSet, Skill skill) {
        Inventory weapon;
        if (!this.mGameChar.isPlayer()) {
            if (this.mRandom.nextBoolean()) {
                return;
            }
        } else if (this.mCurrentSkillTactics.attackSkill == Tactics.TacticsValue.CONSERVATIVE && this.mRandom.nextBoolean()) {
            return;
        }
        if (skill.weaponType == null || !this.mGameChar.isPlayer() || ((weapon = ((PlayerChar) this.mGameChar).getWeapon()) != null && weapon.getBaseItem(this.mContext).weaponType == skill.weaponType)) {
            int parameter = ((this.mCurrentSkillTactics.attackSkill.getParameter() * 4) - 7) + 10 + ((this.mGameChar.f94mp * 6) / this.mGameChar.maxMp);
            int parameter2 = this.mGameChar.getFickleness().getParameter();
            if (this.mCounterPower * (parameter + (this.mRandom.nextInt(parameter2 * 4) - parameter2)) < this.mOurPower * 10) {
                return;
            }
            int iMax = 0;
            ArrayList arrayList = new ArrayList(this.mCounterChars.size());
            for (FightActionEvaluation fightActionEvaluation : sortedSet) {
                if (fightActionEvaluation.action == ActionEvaluation.Action.ATTACK) {
                    arrayList.add(fightActionEvaluation);
                    iMax = Math.max(iMax, fightActionEvaluation.value);
                }
            }
            if (arrayList.isEmpty()) {
                return;
            }
            if (!skill.isMultiAttack || arrayList.size() > 1) {
                FightActionEvaluation fightActionEvaluation2 = new FightActionEvaluation(iMax + this.mRandom.nextInt(parameter2 + skill.f108mp) + 1, null, skill);
                fightActionEvaluation2.baseActions = arrayList;
                sortedSet.add(fightActionEvaluation2);
            }
        }
    }

    protected boolean canUseDamageSkill(Skill skill, Enemy enemy) {
        return !skill.isForUndead || enemy.monster.type == Monster.MonsterType.UNDEAD;
    }

    private void evaluateSkillDamage(Set<FightActionEvaluation> set, Skill skill) {
        Boolean boolValueOf;
        int parameter;
        Boolean boolValueOf2 = null;
        if (this.mGameChar.isPlayer()) {
            GameChar gameChar = this.mCounterChars.get(0);
            if (!canUseDamageSkill(skill, (Enemy) gameChar)) {
                return;
            }
            if (skill.attrType == null) {
                boolValueOf = null;
            } else {
                if (gameChar.isImmuneFor(skill.attrType)) {
                    return;
                }
                boolValueOf2 = Boolean.valueOf(gameChar.isResistFor(skill.attrType));
                boolValueOf = Boolean.valueOf(gameChar.isWeakFor(skill.attrType));
            }
        } else if (this.mGameChar.clazz != GameChar.CharClass.MAGICIAN && this.mRandom.nextBoolean()) {
            return;
        } else {
            boolValueOf = null;
        }
        int status = skill.isMagic() ? this.mGameChar.getStatus(GameChar.Status.MAGIC_DAMAGE_BONUS) : 0;
        GameChar.Attribute attribute = skill.baseAttr;
        int attr = this.mGameChar.getAttr(attribute) + this.mGameChar.getStatus(GameChar.Status.MAGIC_BONUS) + this.mGameChar.level;
        int i = ((((this.mGameChar.f94mp * 16) / this.mGameChar.maxMp) - 12) + 0) - ((skill.f108mp - 3) * 2);
        if (this.mCurrentSkillTactics.damageSkill == Tactics.TacticsValue.CONSERVATIVE) {
            parameter = i - (30 - ((this.mGameChar.f94mp * 30) / this.mGameChar.maxMp));
        } else {
            parameter = i + ((this.mCurrentSkillTactics.damageSkill.getParameter() * 7) - 9);
        }
        int fixed10AttrBonus = ((skill.attrBase + (skill.diceFace * skill.diceNum) + status) * 10) + EngineUtil.getFixed10AttrBonus(this.mGameChar.getAttr(attribute));
        int fixed10AttrBonus2 = ((skill.attrBase + skill.diceNum + status) * 10) + EngineUtil.getFixed10AttrBonus(this.mGameChar.getAttr(attribute));
        for (GameChar gameChar2 : this.mCounterChars) {
            if (gameChar2.isAlive() && (this.mGameChar.isPlayer() || skill.attrType == null || !gameChar2.isImmuneFor(skill.attrType))) {
                int status2 = (gameChar2.getStatus(GameChar.Status.ARMOR_MAGIC_BONUS) + (skill.isMagic() ? gameChar2.getStatus(GameChar.Status.MAGIC_DEFENSE) : 0)) * 10;
                int i2 = fixed10AttrBonus - status2;
                int i3 = fixed10AttrBonus2 - status2;
                if (skill.attrType != null) {
                    boolean zBooleanValue = boolValueOf2 != null ? boolValueOf2.booleanValue() : gameChar2.isResistFor(skill.attrType);
                    boolean zBooleanValue2 = boolValueOf != null ? boolValueOf.booleanValue() : gameChar2.isWeakFor(skill.attrType);
                    if (zBooleanValue) {
                        i2 /= 2;
                        i3 /= 2;
                    } else if (zBooleanValue2) {
                        i2 *= 2;
                        i3 *= 2;
                    }
                }
                if (i2 > 0) {
                    int iMin = Math.min((((i2 + i3) / 2) * 8) / gameChar2.maxHp, 80) + (Math.max(Math.min(((attr - gameChar2.getAttr(attribute)) - gameChar2.getStatus(GameChar.Status.ANTI_MAGIC_BONUS)) - gameChar2.level, 12), -5) - 5);
                    if (gameChar2.isAsleep() && (iMin = iMin + (iMin / 4)) > 100) {
                        iMin = 100;
                    }
                    int i4 = ((gameChar2.f93hp > (i2 / 10) / 10 || gameChar2.f93hp > (i3 / 10) / 10) ? iMin : 100) + (((gameChar2.maxHp - gameChar2.f93hp) * 5) / gameChar2.maxHp) + ((gameChar2.power * 8) / this.mGameChar.power);
                    if (skill.isMagic()) {
                        if (gameChar2.getMagicResist() == GameChar.MagicResist.FULL || gameChar2.getMagicResist() == GameChar.MagicResist.IMMUNE) {
                            i4 += 10;
                        } else if (gameChar2.getMagicResist() == GameChar.MagicResist.HALF) {
                            i4 += 5;
                        }
                    }
                    int i5 = i4 + parameter;
                    if (this.mCurrentSkillTactics.damageSkill == Tactics.TacticsValue.CONSERVATIVE && i5 < 15) {
                        return;
                    } else {
                        set.add(addFickleness(new FightActionEvaluation(i5, gameChar2, skill)));
                    }
                }
            }
        }
    }

    private Tactics getCurrentSkillTactics(Tactics tactics) {
        Tactics.TacticsValue tacticsValue;
        Tactics tactics2 = new Tactics();
        int iNextInt = this.mRandom.nextInt(this.mGameChar.getFickleness().getParameter() + 1) + 6;
        int i = this.mCounterPower * (((this.mGameChar.f94mp * 6) / this.mGameChar.maxMp) + 11);
        int i2 = this.mOurPower;
        if (i > (iNextInt + 2) * i2) {
            tacticsValue = Tactics.TacticsValue.AGGRESSIVE;
        } else if (i > i2 * iNextInt) {
            tacticsValue = Tactics.TacticsValue.MODERATE;
        } else {
            tacticsValue = Tactics.TacticsValue.CONSERVATIVE;
        }
        Tactics.TacticsValue tacticsValue2 = tacticsValue != Tactics.TacticsValue.CONSERVATIVE ? tacticsValue : Tactics.TacticsValue.NONE;
        tactics2.attackSkill = tactics.attackSkill.composite(tacticsValue2);
        tactics2.statusSkill = tactics.statusSkill.composite(tacticsValue2);
        tactics2.cureSkill = tactics.cureSkill.decrementIfLesser(tacticsValue);
        if (this.mGameChar.clazz == GameChar.CharClass.MAGICIAN || this.mGameChar.clazz == GameChar.CharClass.PRIEST || this.mGameChar.clazz == GameChar.CharClass.BISHOP) {
            tactics2.damageSkill = tactics.damageSkill;
        } else {
            tactics2.damageSkill = tactics.damageSkill.decrementIfLesser(tacticsValue);
        }
        return tactics2;
    }

    private void evaluateSkillDamageAll(Set<FightActionEvaluation> set, Skill skill) {
        if (this.mGameChar.isPlayer() || this.mGameChar.clazz == GameChar.CharClass.MAGICIAN || !this.mRandom.nextBoolean()) {
            TreeSet treeSet = new TreeSet();
            evaluateSkillDamage(treeSet, skill);
            if (treeSet.isEmpty()) {
                return;
            }
            int i = 0;
            int i2 = 1;
            Iterator<FightActionEvaluation> it = treeSet.iterator();
            while (it.hasNext()) {
                i += it.next().value / i2;
                i2 += 2;
            }
            set.add(addFickleness(new FightActionEvaluation(i, null, skill)));
        }
    }

    private void evaluateSkillCure(Set<FightActionEvaluation> set, Skill skill) {
        if (this.mGameChar.isPlayer() || !this.mRandom.nextBoolean()) {
            int parameter = ((((this.mCurrentSkillTactics.cureSkill.getParameter() * 2) + 0) + ((this.mGameChar.f94mp * 4) / this.mGameChar.maxMp)) - ((skill.f108mp - 1) * 2)) + ((this.mCounterChars.size() * 3) - 3);
            int fixed10AttrBonus = ((skill.attrBase + (skill.diceFace * skill.diceNum)) * 10) + EngineUtil.getFixed10AttrBonus(this.mGameChar.intl);
            int fixed10AttrBonus2 = ((skill.attrBase + skill.diceNum) * 10) + EngineUtil.getFixed10AttrBonus(this.mGameChar.intl);
            for (GameChar gameChar : this.mOurChars) {
                if (gameChar.isAlive()) {
                    boolean z = gameChar.f93hp <= gameChar.maxHp / 4;
                    boolean z2 = gameChar.f93hp <= gameChar.maxHp / 2;
                    int i = gameChar.maxHp - gameChar.f93hp;
                    if (z || i >= fixed10AttrBonus2 / 10) {
                        int i2 = i * 10;
                        int iMin = Math.min(i2, fixed10AttrBonus) + fixed10AttrBonus2;
                        int i3 = iMin / 20;
                        if (z || z2 || i >= i3) {
                            int iMin2 = (Math.min(iMin / 2, i2) * 10) / gameChar.maxHp;
                            if (z) {
                                iMin2 *= 10;
                            } else if (z2) {
                                int parameter2 = this.mCurrentSkillTactics.cureSkill.getParameter();
                                iMin2 += (parameter2 * parameter2 * 6) + iMin2;
                            }
                            set.add(addFickleness(new FightActionEvaluation(iMin2 + parameter, gameChar, skill)));
                        }
                    }
                }
            }
        }
    }

    private void evaluateSkillCureAll(Set<FightActionEvaluation> set, Skill skill) {
        TreeSet treeSet = new TreeSet();
        evaluateSkillCure(treeSet, skill);
        if (treeSet.isEmpty()) {
            return;
        }
        int i = 0;
        int i2 = 1;
        Iterator<FightActionEvaluation> it = treeSet.iterator();
        while (it.hasNext()) {
            i += it.next().value / i2;
            i2 += 3;
        }
        set.add(new FightActionEvaluation(i, null, skill));
    }

    private void evaluateSkillOther(Set<FightActionEvaluation> set, Skill skill) {
        int i = skill.f107id;
        if (i == 30160) {
            if (this.mCurrentSkillTactics.cureSkill != Tactics.TacticsValue.NONE) {
                evaluateTranquility(set, skill);
            }
        } else if (i == 30190) {
            if (this.mCurrentSkillTactics.damageSkill != Tactics.TacticsValue.NONE) {
                evaluateDeath(set, skill);
            }
        } else if ((i == 40010 || i == 40030 || i == 40040) && this.mCurrentSkillTactics.statusSkill != Tactics.TacticsValue.NONE) {
            evaluateSleep(set, skill);
        }
    }

    private void evaluateTranquility(Set<FightActionEvaluation> set, Skill skill) {
        for (GameChar gameChar : this.mOurChars) {
            if (gameChar.isAlive()) {
                int parameter = 0;
                for (Enchant enchant : gameChar.illegalStatus) {
                    if (enchant.illegalStatus == GameChar.IllegalStatus.SLEEP) {
                        parameter += 100;
                    } else if (enchant.illegalStatus == GameChar.IllegalStatus.POISON) {
                        parameter += 50;
                    }
                }
                if (parameter != 0) {
                    if (gameChar.f93hp < gameChar.maxHp / 4) {
                        parameter += 20;
                    } else if (gameChar.f93hp < gameChar.maxHp / 2) {
                        parameter += this.mCurrentSkillTactics.cureSkill.getParameter() * 3;
                    }
                    set.add(addFickleness(new FightActionEvaluation(parameter + ((this.mCurrentSkillTactics.cureSkill.getParameter() * 3) - 3) + ((this.mGameChar.f94mp * 4) / this.mGameChar.maxMp), gameChar, skill)));
                }
            }
        }
    }

    private void evaluateSleep(Set<FightActionEvaluation> set, Skill skill) {
        int i;
        TreeSet treeSet = new TreeSet();
        Iterator<? extends GameChar> it = this.mCounterChars.iterator();
        while (true) {
            i = 0;
            if (!it.hasNext()) {
                break;
            }
            GameChar next = it.next();
            if (next.isAlive() && !next.isResistFor(AttrType.SLEEP)) {
                int parameter = ((this.mCurrentSkillTactics.statusSkill.getParameter() * 5) - 5) + 0;
                for (Enchant enchant : next.illegalStatus) {
                    if (enchant.illegalStatus == GameChar.IllegalStatus.SLEEP) {
                        i = 1;
                    } else if (enchant.illegalStatus == GameChar.IllegalStatus.POISON) {
                        parameter -= 10;
                    }
                }
                if (i == 0) {
                    treeSet.add(addFickleness(new FightActionEvaluation(((((parameter + (((((this.mGameChar.getAttr(skill.baseAttr) + skill.attrBase) - next.getAttr(skill.baseAttr)) - next.level) * 3) - 10)) + ((next.f93hp * 5) / next.maxHp)) + ((next.power * 8) / this.mGameChar.power)) + ((this.mGameChar.f94mp * 6) / this.mGameChar.maxMp)) - (skill.f108mp * 2), next, skill)));
                }
            }
        }
        if (treeSet.isEmpty()) {
            return;
        }
        Iterator it2 = treeSet.iterator();
        int i2 = 1;
        while (it2.hasNext()) {
            i += ((FightActionEvaluation) it2.next()).value / i2;
            i2++;
        }
        set.add(addFickleness(new FightActionEvaluation(i, null, skill)));
    }

    private void evaluateDeath(Set<FightActionEvaluation> set, Skill skill) {
        int parameter;
        int attr = this.mGameChar.getAttr(skill.baseAttr) + this.mGameChar.getStatus(GameChar.Status.MAGIC_BONUS) + skill.attrBase;
        int i = ((((this.mGameChar.f94mp * 16) / this.mGameChar.maxMp) - 12) + 40) - ((skill.f108mp - 2) * 1);
        if (this.mCurrentSkillTactics.damageSkill == Tactics.TacticsValue.CONSERVATIVE) {
            parameter = i - (30 - ((this.mGameChar.f94mp * 30) / this.mGameChar.maxMp));
        } else {
            parameter = i + ((this.mCurrentSkillTactics.damageSkill.getParameter() * 7) - 9);
        }
        for (GameChar gameChar : this.mCounterChars) {
            if (gameChar.isAlive() && !gameChar.isResistFor(AttrType.DEATH)) {
                int i2 = 0;
                for (Enchant enchant : gameChar.illegalStatus) {
                    if (enchant.illegalStatus == GameChar.IllegalStatus.SLEEP) {
                        i2 -= 10;
                    } else if (enchant.illegalStatus == GameChar.IllegalStatus.POISON) {
                        i2 -= 5;
                    }
                }
                int iMax = i2 + (Math.max(((attr - gameChar.getAttr(r0)) - gameChar.getStatus(GameChar.Status.ANTI_MAGIC_BONUS)) - gameChar.level, -5) - 5) + (5 - (((gameChar.maxHp - gameChar.f93hp) * 5) / gameChar.maxHp)) + ((gameChar.power * 8) / this.mGameChar.power);
                if (skill.isMagic()) {
                    if (gameChar.getMagicResist() == GameChar.MagicResist.FULL || gameChar.getMagicResist() == GameChar.MagicResist.IMMUNE) {
                        iMax += 10;
                    } else if (gameChar.getMagicResist() == GameChar.MagicResist.HALF) {
                        iMax += 5;
                    }
                }
                int i3 = iMax + parameter;
                if (this.mCurrentSkillTactics.damageSkill == Tactics.TacticsValue.CONSERVATIVE && i3 < 15) {
                    return;
                } else {
                    set.add(addFickleness(new FightActionEvaluation(i3, gameChar, skill)));
                }
            }
        }
    }
}
