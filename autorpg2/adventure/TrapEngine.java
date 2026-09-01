package com.shirobakama.autorpg2.adventure;

import android.content.Context;
import android.support.annotation.StringRes;
import com.shirobakama.autorpg2.adventure.Thrower;
import com.shirobakama.autorpg2.entity.AdventureContext;
import com.shirobakama.autorpg2.entity.Enchant;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.entity.Skill;
import com.shirobakama.autorpg2.entity.Tactics;
import com.shirobakama.autorpg2.repo.SkillDb;
import com.shirobakama.logquest2.C0380R;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class TrapEngine {
    private AdventureContext mAdv;
    private List<PlayerChar> mCharacters;
    private Context mContext;
    private int mDifficulty;
    private Random mRandom;
    private TrapType mTrapType;

    public enum TrapType {
        ALARM,
        BOW,
        POISON;

        public int foundTrapStrId(boolean z) {
            switch (this) {
                case ALARM:
                    return z ? C0380R.string.alog_desc_trap_alarm_identify : C0380R.string.alog_desc_event_trap_alarm_identify;
                case BOW:
                    return z ? C0380R.string.alog_desc_trap_bow_identify : C0380R.string.alog_desc_event_trap_bow_identify;
                case POISON:
                    return z ? C0380R.string.alog_desc_trap_poison_identify : C0380R.string.alog_desc_event_trap_poison_identify;
                default:
                    return 0;
            }
        }

        public int invokeStrId(boolean z) {
            switch (this) {
                case ALARM:
                    return z ? C0380R.string.alog_desc_trap_alarm_invoke : C0380R.string.alog_desc_event_trap_alarm_invoke;
                case BOW:
                    return z ? C0380R.string.alog_desc_trap_bow_invoke : C0380R.string.alog_desc_event_trap_bow_invoke;
                case POISON:
                    return z ? C0380R.string.alog_desc_trap_poison_invoke : C0380R.string.alog_desc_event_trap_poison_invoke;
                default:
                    return 0;
            }
        }

        public int dodgeStrId(boolean z) {
            if (C03331.f71x427ff709[ordinal()] != 2) {
                return 0;
            }
            return z ? C0380R.string.alog_desc_trap_bow_dodge : C0380R.string.alog_desc_event_trap_bow_dodge;
        }

        public int hitStrId(boolean z) {
            switch (this) {
                case ALARM:
                default:
                    return 0;
                case BOW:
                    return z ? C0380R.string.alog_desc_trap_bow_hit : C0380R.string.alog_desc_event_trap_bow_hit;
                case POISON:
                    return z ? C0380R.string.alog_desc_trap_poison_hit : C0380R.string.alog_desc_event_trap_poison_hit;
            }
        }

        @StringRes
        public int foundButInvokeStrId() {
            switch (this) {
                case ALARM:
                    return C0380R.string.alog_desc_trap_alarm_remove_failed;
                case BOW:
                    return C0380R.string.alog_desc_trap_bow_remove_failed;
                case POISON:
                    return C0380R.string.alog_desc_trap_poison_remove_failed;
                default:
                    return 0;
            }
        }

        @StringRes
        public int resistStrId(boolean z) {
            switch (this) {
                case ALARM:
                case BOW:
                default:
                    return 0;
                case POISON:
                    return z ? C0380R.string.alog_desc_trap_poison_resist : C0380R.string.alog_desc_event_trap_poison_resist;
            }
        }
    }

    public TrapEngine(Context context, TrapType trapType, AdventureContext adventureContext, int i, List<PlayerChar> list, Random random) {
        this.mContext = context;
        this.mTrapType = trapType;
        this.mAdv = adventureContext;
        this.mDifficulty = i;
        this.mCharacters = list;
        this.mRandom = random;
        Collections.shuffle(this.mCharacters, this.mRandom);
    }

    public void processTreasureBox(StringBuilder sb) {
        PlayerChar next;
        PlayerChar playerChar;
        Thrower thrower = new Thrower(this.mRandom);
        Iterator<PlayerChar> it = this.mCharacters.iterator();
        PlayerChar playerChar2 = null;
        Skill availableSkill = null;
        while (true) {
            if (!it.hasNext()) {
                next = null;
                break;
            }
            next = it.next();
            if (this.mAdv.getCurrentTacticsForChar(next.index).statusSkill != Tactics.TacticsValue.NONE) {
                availableSkill = next.getAvailableSkill(this.mContext, SkillDb.SKILL_ROGUE_DETECT_TRAP);
                if (availableSkill == null) {
                    availableSkill = next.getAvailableSkill(this.mContext, SkillDb.SKILL_CLERIC_DETECT_TRAP);
                }
                if (availableSkill != null && availableSkill.canUse(next)) {
                    break;
                }
            }
        }
        if (next == null || availableSkill == null) {
            playerChar = null;
        } else {
            sb.append(this.mContext.getString(availableSkill.isMagic() ? C0380R.string.alog_desc_skill_magic : C0380R.string.alog_desc_skill_normal, next.name, this.mAdv.getSkillNameAwareCustomized(next, availableSkill)));
            sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
            sb.append(this.mAdv.getSkillUseAwareCustomized(this.mContext, next, availableSkill));
            sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
            int i = availableSkill.attrBase;
            next.f94mp -= availableSkill.f108mp;
            Thrower.ThrowResult throwResultAttributeThrow = thrower.attributeThrow(next, GameChar.Attribute.INT, availableSkill.clazz);
            int i2 = this.mDifficulty;
            if (i2 <= 0 || !throwResultAttributeThrow.success(i2 - i)) {
                sb.append(this.mContext.getString(C0380R.string.alog_desc_trap_not_found));
                sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
                playerChar = null;
            } else {
                playerChar = next;
            }
        }
        if (this.mDifficulty <= 0) {
            if (next == null) {
                addSearchAndNotFound(sb);
            }
            sb.append(this.mContext.getString(C0380R.string.alog_desc_treasure_open));
            sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
            return;
        }
        if (next == null) {
            Iterator<PlayerChar> it2 = this.mCharacters.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                PlayerChar next2 = it2.next();
                boolean zHasSubClass = next2.hasSubClass(GameChar.SubClass.ROGUE);
                if (zHasSubClass && playerChar2 == null) {
                    playerChar2 = next2;
                }
                if (thrower.attributeThrow(next2, GameChar.Attribute.INT, GameChar.SubClass.ROGUE).success(this.mDifficulty + (zHasSubClass ? 0 : 8))) {
                    sb.append(this.mContext.getString(C0380R.string.alog_desc_trap_search, next2.name));
                    sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
                    if (zHasSubClass) {
                        playerChar2 = next2;
                        playerChar = playerChar2;
                    } else {
                        playerChar = next2;
                    }
                }
            }
        } else {
            Iterator<PlayerChar> it3 = this.mCharacters.iterator();
            while (true) {
                if (!it3.hasNext()) {
                    break;
                }
                PlayerChar next3 = it3.next();
                if (next3.hasSubClass(GameChar.SubClass.ROGUE)) {
                    playerChar2 = next3;
                    break;
                }
            }
        }
        if (playerChar == null) {
            addSearchAndNotFound(sb);
        }
        processTrapCommon(sb, true, playerChar, playerChar2);
    }

    void addSearchAndNotFound(StringBuilder sb) {
        PlayerChar playerChar = this.mCharacters.get(0);
        Iterator<PlayerChar> it = this.mCharacters.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            PlayerChar next = it.next();
            if (next.hasSubClass(GameChar.SubClass.ROGUE)) {
                playerChar = next;
                break;
            }
        }
        sb.append(this.mContext.getString(C0380R.string.alog_desc_trap_search, playerChar.name));
        sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
        sb.append(this.mContext.getString(C0380R.string.alog_desc_trap_not_found));
        sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
    }

    public void processEventTrap(StringBuilder sb) {
        Thrower thrower = new Thrower(this.mRandom);
        Iterator<PlayerChar> it = this.mCharacters.iterator();
        PlayerChar playerChar = null;
        PlayerChar playerChar2 = null;
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            PlayerChar next = it.next();
            if (next.hasSubClass(GameChar.SubClass.ROGUE)) {
                if (playerChar2 == null) {
                    playerChar2 = next;
                }
                if (thrower.attributeThrow(next, GameChar.Attribute.INT, GameChar.SubClass.ROGUE).success(this.mDifficulty)) {
                    playerChar = next;
                    playerChar2 = playerChar;
                    break;
                }
            }
        }
        processTrapCommon(sb, false, playerChar, playerChar2);
    }

    private void processTrapCommon(StringBuilder sb, boolean z, PlayerChar playerChar, PlayerChar playerChar2) {
        int i;
        PlayerChar next;
        Thrower thrower = new Thrower(this.mRandom);
        boolean z2 = playerChar != null;
        PlayerChar playerChar3 = null;
        if (playerChar != null) {
            sb.append(this.mContext.getString(this.mTrapType.foundTrapStrId(z), playerChar.name));
            sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
            if (!z) {
                return;
            }
            if (playerChar2 == null) {
                Iterator<PlayerChar> it = this.mCharacters.iterator();
                while (true) {
                    if (it.hasNext()) {
                        next = it.next();
                        if (next.getAvailableSkill(this.mContext, SkillDb.SKILL_ROGUE_REMOVE_TRAP) != null) {
                            break;
                        }
                    } else {
                        next = playerChar2;
                        break;
                    }
                }
                if (next == null) {
                    next = this.mCharacters.get(0);
                }
                playerChar3 = next;
                i = 8;
            } else {
                playerChar3 = playerChar2;
                i = 0;
            }
            Skill availableSkill = playerChar3.getAvailableSkill(this.mContext, SkillDb.SKILL_ROGUE_REMOVE_TRAP);
            if (availableSkill != null && availableSkill.canUse(playerChar3)) {
                sb.append(this.mContext.getString(availableSkill.isMagic() ? C0380R.string.alog_desc_skill_magic : C0380R.string.alog_desc_skill_normal, playerChar3.name, this.mAdv.getSkillNameAwareCustomized(playerChar3, availableSkill)));
                sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
                sb.append(this.mAdv.getSkillUseAwareCustomized(this.mContext, playerChar3, availableSkill));
                sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
                i -= availableSkill.attrBase;
                playerChar3.f94mp -= availableSkill.f108mp;
            }
            if (thrower.attributeThrow(playerChar3, GameChar.Attribute.AGI, GameChar.SubClass.ROGUE).success(this.mDifficulty + i)) {
                sb.append(this.mContext.getString(C0380R.string.alog_desc_trap_removed, playerChar3.name));
                sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
                sb.append(this.mContext.getString(C0380R.string.alog_desc_treasure_open));
                sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
                return;
            }
            sb.append(this.mContext.getString(C0380R.string.alog_desc_trap_remove_failed, playerChar3.name));
            sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
        }
        if (playerChar3 == null) {
            playerChar3 = this.mCharacters.get(0);
        }
        if (!z2 && z) {
            sb.append(this.mContext.getString(C0380R.string.alog_desc_treasure_open));
            sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
        }
        switch (this.mTrapType) {
            case ALARM:
                sb.append(this.mContext.getString(z2 ? this.mTrapType.foundButInvokeStrId() : this.mTrapType.invokeStrId(z), playerChar3.name));
                sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
                this.mAdv.enchants.add(new Enchant(Enchant.Target.PARTY, Enchant.OtherEffect.ENCOUNTER, 100, (Skill) null, this.mRandom.nextInt(this.mDifficulty / 2) + 5));
                break;
            case BOW:
                sb.append(this.mContext.getString(z2 ? this.mTrapType.foundButInvokeStrId() : this.mTrapType.invokeStrId(z)));
                sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
                if (thrower.attributeThrow(playerChar3, GameChar.Attribute.AGI, playerChar3.fightingSubClass).success(this.mDifficulty + 4)) {
                    sb.append(this.mContext.getString(this.mTrapType.dodgeStrId(z), playerChar3.name));
                    sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
                    break;
                } else {
                    sb.append(this.mContext.getString(this.mTrapType.hitStrId(z), playerChar3.name));
                    sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
                    playerChar3.f93hp = Math.max(playerChar3.f93hp - Math.max(thrower.throwDice(((this.mDifficulty - 7) / 6) + 1, 12) - playerChar3.getStatus(GameChar.Status.DEFENSE), 1), 0);
                    break;
                }
            case POISON:
                sb.append(this.mContext.getString(z2 ? this.mTrapType.foundButInvokeStrId() : this.mTrapType.invokeStrId(z)));
                sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
                for (PlayerChar playerChar4 : this.mCharacters) {
                    if (thrower.genericThrow(playerChar4, GameChar.Attribute.VIT).success(this.mDifficulty + 4)) {
                        sb.append(this.mContext.getString(this.mTrapType.resistStrId(z), playerChar4.name));
                        sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
                    } else {
                        sb.append(this.mContext.getString(this.mTrapType.hitStrId(z), playerChar4.name));
                        sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
                        playerChar4.f93hp = Math.max(playerChar4.f93hp - Math.max(thrower.throwDice(((this.mDifficulty - 6) / 5) + 1, 6), 0), 0);
                    }
                }
                break;
        }
        if (z2 && z) {
            sb.append(this.mContext.getString(C0380R.string.alog_desc_treasure_open));
            sb.append(this.mContext.getString(C0380R.string.res_sentence_separator));
        }
    }
}
