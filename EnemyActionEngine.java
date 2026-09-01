package com.shirobakama.autorpg2.adventure;

import android.content.Context;
import com.shirobakama.autorpg2.adventure.ActionEvaluation;
import com.shirobakama.autorpg2.entity.Enemy;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.Monster;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.entity.Tactics;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class EnemyActionEngine extends CharacterActionEngine {
    protected static final String TAG = "enemy-act";
    private Enemy mEnemy;
    private Monster.MonsterIntelligence mIntelligence;
    private PlayerChar[] mPcs;

    @Override // com.shirobakama.autorpg2.adventure.CharacterActionEngine
    protected boolean evaluateAdvancedTactics(Set<FightActionEvaluation> set, boolean z) {
        return false;
    }

    @Override // com.shirobakama.autorpg2.adventure.CharacterActionEngine
    protected void evaluateItems(Set<FightActionEvaluation> set) {
    }

    public EnemyActionEngine(Context context, Random random, Enemy enemy) {
        super(context, random, Tactics.normal(), enemy);
        this.mPcs = new PlayerChar[3];
        this.mEnemy = enemy;
        this.mIntelligence = enemy.monster.intelligence;
    }

    @Override // com.shirobakama.autorpg2.adventure.CharacterActionEngine
    public void preprocess() {
        int i = 0;
        while (i < this.mCounterChars.size()) {
            this.mPcs[i] = (PlayerChar) this.mCounterChars.get(i);
            i++;
        }
        while (i < 3) {
            this.mPcs[i] = null;
            i++;
        }
    }

    @Override // com.shirobakama.autorpg2.adventure.CharacterActionEngine
    protected boolean evaluatePhysicalAttack(Set<FightActionEvaluation> set) {
        switch (this.mIntelligence) {
            case NONE:
                return evaluatePhysicalAttackRandom(set);
            case NORMAL:
                return evaluatePhysicalAttackNormal(set);
            case HIGH:
                return evaluatePhysicalAttackHigh(set);
            default:
                return false;
        }
    }

    private boolean canBackAttack() {
        if (this.mCounterChars.size() <= 2) {
            return false;
        }
        boolean zIsAlive = this.mCounterChars.get(0).isAlive();
        boolean zIsAlive2 = this.mCounterChars.get(1).isAlive();
        int size = this.mOurChars.size() / 2;
        return this.mGameChar.index < size ? !zIsAlive : this.mGameChar.index >= this.mOurChars.size() - size ? !zIsAlive2 : (zIsAlive || zIsAlive2) ? false : true;
    }

    /* JADX WARN: Removed duplicated region for block: B:63:0x00b0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean evaluatePhysicalAttackRandom(java.util.Set<com.shirobakama.autorpg2.adventure.FightActionEvaluation> r9) {
        /*
            Method dump skipped, instructions count: 205
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.adventure.EnemyActionEngine.evaluatePhysicalAttackRandom(java.util.Set):boolean");
    }

    private boolean evaluatePhysicalAttackNormal(Set<FightActionEvaluation> set) {
        return evaluatePhysicalAttackRandom(set);
    }

    private boolean evaluatePhysicalAttackHigh(Set<FightActionEvaluation> set) {
        boolean z = this.mEnemy.monster.longRange || canBackAttack();
        boolean z2 = false;
        boolean z3 = true;
        for (int i = 0; i < this.mCounterChars.size(); i++) {
            if (z || i != 2 || !z3) {
                PlayerChar playerChar = this.mPcs[i];
                if (playerChar.isAlive()) {
                    int iEvaluatePhysicalAttackForPc = evaluatePhysicalAttackForPc(playerChar);
                    if (iEvaluatePhysicalAttackForPc >= 0) {
                        set.add(addFickleness(new FightActionEvaluation(iEvaluatePhysicalAttackForPc, this.mPcs[i])));
                        z2 = true;
                    }
                } else {
                    z3 = false;
                }
            }
        }
        return z2;
    }

    private int evaluatePhysicalAttackForPc(PlayerChar playerChar) {
        int iMax = Math.max(((((((this.mEnemy.attackDiceFace * this.mEnemy.attackDiceNum) + this.mEnemy.attackBase) + this.mEnemy.getStatus(GameChar.Status.WEAPON_MAGIC_BONUS)) - playerChar.getStatus(GameChar.Status.DEFENSE)) - playerChar.getStatus(GameChar.Status.ARMOR_MAGIC_BONUS)) * 10) + EngineUtil.getFixed10AttrBonus(this.mEnemy.str), this.mEnemy.attackBase * 10);
        int iMax2 = Math.max((((((this.mEnemy.attackDiceNum + this.mEnemy.attackBase) + this.mEnemy.getStatus(GameChar.Status.WEAPON_MAGIC_BONUS)) - playerChar.getStatus(GameChar.Status.DEFENSE)) - playerChar.getStatus(GameChar.Status.ARMOR_MAGIC_BONUS)) * 10) + EngineUtil.getFixed10AttrBonus(this.mEnemy.str), this.mEnemy.attackBase * 10);
        if (iMax == 0) {
            return -1;
        }
        int i = ((this.mEnemy.agi + this.mEnemy.level) - playerChar.agi) - playerChar.level;
        int estimateAttackTimes10 = getEstimateAttackTimes10(playerChar);
        int i2 = (iMax2 * estimateAttackTimes10) / 10;
        int i3 = ((((((iMax * estimateAttackTimes10) / 10) + i2) / 2) * 8) / playerChar.maxHp) + ((i + 0) * 2);
        if (playerChar.isAsleep() && (i3 = i3 + (i3 / 2)) > 100) {
            i3 = 100;
        }
        if (playerChar.f93hp <= i2 / 10) {
            i3 = 100;
        }
        int i4 = i3 + (((playerChar.maxHp - playerChar.f93hp) * 10) / playerChar.maxHp) + ((playerChar.power * 5) / this.mGameChar.power);
        if (i4 < 1) {
            return 1;
        }
        return i4;
    }

    @Override // com.shirobakama.autorpg2.adventure.CharacterActionEngine
    protected void evaluateRunning(Set<FightActionEvaluation> set) {
        if (this.mFightContext.isEvent || this.mBaseTactics.running == Tactics.TacticsValue.NONE || this.mEnemy.monster.intelligence == Monster.MonsterIntelligence.NONE || this.mEnemy.f93hp > this.mEnemy.maxHp / 2) {
            return;
        }
        int parameter = (((this.mCounterPower * ((this.mBaseTactics.running.getParameter() + 1) - this.mRandom.nextInt(this.mBaseTactics.running.getParameter()))) * 50) / (this.mOurPower * 5)) - 50;
        if (parameter < 100) {
            return;
        }
        int i = 0;
        Iterator<? extends GameChar> it = this.mOurChars.iterator();
        while (it.hasNext()) {
            if (it.next().isAlive()) {
                i++;
            }
        }
        if (i >= this.mOurChars.size() / 2) {
            return;
        }
        set.add(addFickleness(new FightActionEvaluation(ActionEvaluation.Action.RUNNING, parameter)));
    }
}
