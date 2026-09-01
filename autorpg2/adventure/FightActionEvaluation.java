package com.shirobakama.autorpg2.adventure;

import com.shirobakama.autorpg2.adventure.ActionEvaluation;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.Inventory;
import com.shirobakama.autorpg2.entity.Skill;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class FightActionEvaluation extends ActionEvaluation {
    private boolean mByAdvancedTactics;

    private FightActionEvaluation(ActionEvaluation.Action action, int i, GameChar gameChar, int i2, int i3) {
        super(action, i, gameChar, i2, i3);
        this.mByAdvancedTactics = false;
    }

    public FightActionEvaluation(int i, GameChar gameChar) {
        this(ActionEvaluation.Action.ATTACK, i, gameChar, 0, 0);
    }

    public FightActionEvaluation(int i, GameChar gameChar, Skill skill) {
        this(ActionEvaluation.Action.SKILL_MAGIC, i, gameChar, skill.f107id, 0);
    }

    public FightActionEvaluation(ActionEvaluation.Action action, int i, GameChar gameChar, Inventory inventory) {
        this(action, i, gameChar, 0, inventory.f98id);
    }

    public FightActionEvaluation(ActionEvaluation.Action action, int i) {
        this(action, i, null, 0, 0);
    }

    public void setByAdvancedTactics() {
        this.mByAdvancedTactics = true;
    }

    public boolean byAdvancedTactics() {
        return this.mByAdvancedTactics;
    }
}
