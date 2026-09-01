package com.shirobakama.autorpg2.adventure;

import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.Inventory;
import com.shirobakama.autorpg2.entity.Skill;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class ActionEvaluation implements Comparable<ActionEvaluation> {
    public Action action;
    public List<? extends ActionEvaluation> baseActions;
    public int inventoryId;
    public int skillId;
    public GameChar targetChar;
    public int value;

    public enum Action {
        ATTACK,
        SKILL_MAGIC,
        USE_ITEM,
        EQUIP_ITEM,
        RUNNING,
        NONE
    }

    protected ActionEvaluation(Action action, int i, GameChar gameChar, int i2, int i3) {
        this.action = action;
        this.value = i;
        this.targetChar = gameChar;
        this.skillId = i2;
        this.inventoryId = i3;
    }

    public ActionEvaluation(int i, GameChar gameChar, Skill skill) {
        this(Action.SKILL_MAGIC, i, gameChar, skill.f107id, 0);
    }

    public ActionEvaluation(int i, GameChar gameChar, Inventory inventory) {
        this(Action.USE_ITEM, i, gameChar, 0, inventory.f98id);
    }

    public ActionEvaluation(Action action, int i) {
        this(action, i, null, 0, 0);
    }

    @Override // java.lang.Comparable
    public int compareTo(ActionEvaluation actionEvaluation) {
        return actionEvaluation.value - this.value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(this.action);
        sb.append(",");
        sb.append(this.value);
        sb.append(",");
        GameChar gameChar = this.targetChar;
        sb.append(gameChar == null ? "null" : gameChar.name);
        sb.append(",");
        sb.append(this.skillId);
        sb.append(",");
        sb.append(this.inventoryId);
        sb.append(",");
        sb.append(this.baseActions);
        sb.append("]");
        return sb.toString();
    }
}
