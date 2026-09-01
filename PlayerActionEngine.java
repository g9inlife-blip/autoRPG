package com.shirobakama.autorpg2.adventure;

import android.content.Context;
import com.shirobakama.autorpg2.adventure.ActionEvaluation;
import com.shirobakama.autorpg2.entity.AdvancedTactics;
import com.shirobakama.autorpg2.entity.Enchant;
import com.shirobakama.autorpg2.entity.Enemy;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.Inventory;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.entity.Skill;
import com.shirobakama.autorpg2.entity.Tactics;
import com.shirobakama.autorpg2.repo.SkillRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class PlayerActionEngine extends CharacterActionEngine {
    private static final int ADVANCED_TACTICS_EVAL_VALUE = 10000;
    protected static final String TAG = "player-ae";
    private FightEngine mFightEngine;
    private transient Boolean[] mHasDamageSkill;
    private List<Inventory> mInventories;
    private PlayerChar mPlayerChar;

    @Override // com.shirobakama.autorpg2.adventure.CharacterActionEngine
    public void preprocess() {
    }

    public PlayerActionEngine(Context context, FightEngine fightEngine, Random random, Tactics tactics, PlayerChar playerChar, List<Inventory> list) {
        super(context, random, tactics, playerChar);
        this.mHasDamageSkill = new Boolean[3];
        this.mFightEngine = fightEngine;
        this.mPlayerChar = playerChar;
        this.mInventories = list;
    }

    @Override // com.shirobakama.autorpg2.adventure.CharacterActionEngine
    protected boolean evaluatePhysicalAttack(Set<FightActionEvaluation> set) {
        int i;
        Inventory weapon = this.mPlayerChar.getWeapon();
        Item baseItem = weapon.getBaseItem(this.mContext);
        if (!this.mGameChar.isForward() && !baseItem.longRange) {
            return false;
        }
        Iterator<? extends GameChar> it = this.mCounterChars.iterator();
        boolean z = false;
        while (it.hasNext()) {
            Enemy enemy = (Enemy) it.next();
            if (enemy.isAlive()) {
                int damageRatioForMonster = weapon.getDamageRatioForMonster(baseItem, enemy.monster);
                int iMax = Math.max(((((baseItem.getMaxValue() + this.mPlayerChar.getStatus(GameChar.Status.WEAPON_MAGIC_BONUS)) - enemy.getStatus(GameChar.Status.DEFENSE)) - enemy.getStatus(GameChar.Status.ARMOR_MAGIC_BONUS)) * 10) + EngineUtil.getFixed10AttrBonus(this.mPlayerChar.str), baseItem.attrBase * 10) * damageRatioForMonster;
                int iMax2 = damageRatioForMonster * Math.max(((((baseItem.getMinValue() + this.mPlayerChar.getStatus(GameChar.Status.WEAPON_MAGIC_BONUS)) - enemy.getStatus(GameChar.Status.DEFENSE)) - enemy.getStatus(GameChar.Status.ARMOR_MAGIC_BONUS)) * 10) + EngineUtil.getFixed10AttrBonus(this.mPlayerChar.str), baseItem.attrBase * 10);
                if (!this.mPlayerChar.hasEnchantedWeapon()) {
                    if (enemy.getMagicResist() == GameChar.MagicResist.FULL) {
                        iMax2 = 0;
                        iMax = 0;
                    } else if (enemy.getMagicResist() == GameChar.MagicResist.HALF) {
                        iMax /= 2;
                        iMax2 /= 2;
                    }
                } else if (enemy.getMagicResist() == GameChar.MagicResist.IMMUNE) {
                    if (this.mPlayerChar.getAttackAttrType() == null) {
                        iMax2 = 0;
                        iMax = 0;
                    } else {
                        iMax /= 3;
                        iMax2 /= 3;
                    }
                }
                if (iMax != 0) {
                    int iMin = Math.min(((this.mPlayerChar.agi + this.mPlayerChar.level) - enemy.agi) - enemy.level, 12);
                    int estimateAttackTimes10 = getEstimateAttackTimes10(enemy);
                    int i2 = (iMax2 * estimateAttackTimes10) / 10;
                    int i3 = ((((((iMax * estimateAttackTimes10) / 10) + i2) / 2) * 8) / enemy.maxHp) + ((iMin - 10) * 2);
                    if (enemy.isAsleep() && (i3 = i3 + (i3 / 2)) > 100) {
                        i3 = 100;
                    }
                    if (enemy.f93hp <= i2 / 10) {
                        i = (i3 / 2) + i3;
                        if (i > 100) {
                            i = 100;
                        }
                    } else {
                        i = i3;
                    }
                    set.add(addFickleness(new FightActionEvaluation(i + (((enemy.maxHp - enemy.f93hp) * 10) / enemy.maxHp) + ((enemy.power * 3) / this.mPlayerChar.power), enemy)));
                    z = true;
                }
            }
        }
        return z;
    }

    @Override // com.shirobakama.autorpg2.adventure.CharacterActionEngine
    protected void evaluateItems(Set<FightActionEvaluation> set) {
        if (this.mBaseTactics.item == Tactics.TacticsValue.NONE) {
            return;
        }
        HashSet hashSet = new HashSet();
        for (Inventory inventory : this.mInventories) {
            Integer numValueOf = Integer.valueOf(inventory.itemId);
            if (!hashSet.contains(numValueOf)) {
                hashSet.add(numValueOf);
                Item baseItem = inventory.getBaseItem(this.mContext);
                if (baseItem.type == Item.Type.CONSUMABLE) {
                    Item.Effect hpRestoreEffect = baseItem.getHpRestoreEffect();
                    if (hpRestoreEffect != null) {
                        evaluateRestoreHpItem(set, inventory, baseItem, hpRestoreEffect);
                        return;
                    } else {
                        Item.Effect mpRestoreEffect = baseItem.getMpRestoreEffect();
                        if (mpRestoreEffect != null) {
                            evaluateRestoreMpItem(set, inventory, baseItem, mpRestoreEffect);
                        }
                    }
                } else {
                    continue;
                }
            }
        }
    }

    private void evaluateRestoreHpItem(Set<FightActionEvaluation> set, Inventory inventory, Item item, Item.Effect effect) {
        int parameter;
        boolean z = this.mPlayerChar.f93hp < this.mPlayerChar.maxHp / 4;
        int i = this.mPlayerChar.maxHp - this.mPlayerChar.f93hp;
        int fixed10AttrBonus = EngineUtil.getFixed10AttrBonus(this.mPlayerChar.getAttr(effect.attr));
        int i2 = ((item.attrBase + (item.diceFace * item.diceNum)) * 10) + fixed10AttrBonus;
        int i3 = ((item.attrBase + item.diceNum) * 10) + fixed10AttrBonus;
        int i4 = (i2 + i3) / 20;
        if (z || i >= i3 / 10) {
            int i5 = i * 10;
            int iMin = Math.min(i5, i2);
            int i6 = (((i3 + iMin) / 2) * 10) / this.mPlayerChar.maxHp;
            if (i < i4) {
                i6 /= 4;
            } else if (i5 < iMin) {
                i6 -= i6 / 4;
            }
            if (z) {
                parameter = 200;
            } else if (this.mPlayerChar.f93hp < this.mPlayerChar.maxHp / 2) {
                parameter = (this.mBaseTactics.item.getParameter() * 5) + i6;
            } else {
                parameter = i6 - ((4 - this.mBaseTactics.item.getParameter()) * 10);
            }
            int parameter2 = parameter + ((this.mBaseTactics.item.getParameter() * 8) - 16);
            int iLog = ((int) (Math.log((item.price + 40) / 20) * 4.0d)) - 5;
            if (iLog > 20) {
                iLog = 20;
            }
            set.add(addFickleness(new FightActionEvaluation(ActionEvaluation.Action.USE_ITEM, parameter2 - iLog, this.mPlayerChar, inventory)));
        }
    }

    private void evaluateRestoreMpItem(Set<FightActionEvaluation> set, Inventory inventory, Item item, Item.Effect effect) {
        if (this.mPlayerChar.f94mp > this.mPlayerChar.maxMp / 2) {
            return;
        }
        boolean z = this.mPlayerChar.f94mp < this.mPlayerChar.maxMp / 5;
        int fixed10AttrBonus = EngineUtil.getFixed10AttrBonus(this.mPlayerChar.getAttr(effect.attr));
        int i = ((item.attrBase + (item.diceFace * item.diceNum)) * 10) + fixed10AttrBonus;
        int i2 = ((item.attrBase + item.diceNum) * 10) + fixed10AttrBonus;
        int i3 = (i + i2) / 20;
        int i4 = this.mPlayerChar.maxMp - this.mPlayerChar.f94mp;
        int i5 = i4 * 10;
        int iMin = Math.min(i5, i);
        int parameter = (((i2 + iMin) / 2) * 10) / this.mPlayerChar.maxHp;
        if (i4 < i3) {
            parameter /= 3;
        } else if (i5 < iMin) {
            parameter /= 2;
        }
        if (z) {
            parameter = 100;
        } else if (this.mPlayerChar.f94mp < this.mPlayerChar.maxMp / 3) {
            parameter += this.mBaseTactics.item.getParameter() * 5;
        }
        int parameter2 = parameter + ((this.mBaseTactics.item.getParameter() * 8) - 16);
        int iLog = ((int) (Math.log((item.price + 40) / 20) * 4.0d)) - 5;
        if (iLog > 20) {
            iLog = 20;
        }
        set.add(addFickleness(new FightActionEvaluation(ActionEvaluation.Action.USE_ITEM, parameter2 - iLog, this.mPlayerChar, inventory)));
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x008a  */
    @Override // com.shirobakama.autorpg2.adventure.CharacterActionEngine
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void evaluateRunning(java.util.Set<com.shirobakama.autorpg2.adventure.FightActionEvaluation> r9) {
        /*
            Method dump skipped, instructions count: 274
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.adventure.PlayerActionEngine.evaluateRunning(java.util.Set):void");
    }

    @Override // com.shirobakama.autorpg2.adventure.CharacterActionEngine
    protected boolean evaluateAdvancedTactics(Set<FightActionEvaluation> set, boolean z) {
        List<Enchant> list;
        boolean z2 = false;
        for (AdvancedTactics.TacticsComposition tacticsComposition : this.mFightEngine.getAdvancedFightTacticsForCharIndex(this.mPlayerChar.index)) {
            AdvancedTactics firstTactics = tacticsComposition.getFirstTactics();
            PlayerChar playerChar = null;
            Skill skill = firstTactics.action == AdvancedTactics.TacticsAction.USE_SKILL ? SkillRepository.getSkill(this.mContext, firstTactics.targetId) : null;
            if (z || (firstTactics.action != AdvancedTactics.TacticsAction.ATTACK && (firstTactics.action != AdvancedTactics.TacticsAction.USE_SKILL || skill == null || skill.type != Skill.SkillType.ADD_ATTACK))) {
                if (this.mFightEngine.matchesAdvancedTactics(tacticsComposition, this.mPlayerChar)) {
                    switch (firstTactics.action) {
                        case ATTACK:
                            if (firstTactics.target == AdvancedTactics.Target.ENEMY_ANY || firstTactics.target == AdvancedTactics.Target.ENEMY_HP_HIGHEST || firstTactics.target == AdvancedTactics.Target.ENEMY_HP_LOWEST || firstTactics.target == AdvancedTactics.Target.ENEMY_ASLEEP) {
                                set.add(new FightActionEvaluation(ADVANCED_TACTICS_EVAL_VALUE, selectEnemy(firstTactics.target)));
                                z2 = true;
                                break;
                            }
                            break;
                        case EQUIP_ITEM:
                            Inventory inventorySelectItemForEquip = selectItemForEquip(firstTactics);
                            if (inventorySelectItemForEquip != null && inventorySelectItemForEquip.equippedCharId != this.mPlayerChar.f106id) {
                                set.add(new FightActionEvaluation(ActionEvaluation.Action.EQUIP_ITEM, ADVANCED_TACTICS_EVAL_VALUE, this.mPlayerChar, inventorySelectItemForEquip));
                                z2 = true;
                                break;
                            }
                            break;
                        case NONE:
                            set.add(new FightActionEvaluation(ActionEvaluation.Action.NONE, ADVANCED_TACTICS_EVAL_VALUE));
                            z2 = true;
                            break;
                        case RUNNING:
                            set.add(new FightActionEvaluation(ActionEvaluation.Action.RUNNING, ADVANCED_TACTICS_EVAL_VALUE));
                            z2 = true;
                            break;
                        case USE_ITEM:
                            Inventory inventorySelectItem = selectItem(firstTactics);
                            if (inventorySelectItem != null) {
                                set.add(new FightActionEvaluation(ActionEvaluation.Action.USE_ITEM, ADVANCED_TACTICS_EVAL_VALUE, this.mPlayerChar, inventorySelectItem));
                                z2 = true;
                                break;
                            }
                            break;
                        case USE_SKILL:
                            if (skill != null && skill.canUse(this.mPlayerChar) && skill.context != Skill.SkillContext.ADVENTURE) {
                                int i = skill.isMultiAttack ? 2 : 1;
                                switch (skill.type) {
                                    case ADD_ATTACK:
                                        Inventory weapon = this.mPlayerChar.getWeapon();
                                        if (weapon != null && (skill.weaponType == null || weapon.getBaseItem(this.mContext).weaponType == skill.weaponType)) {
                                            ArrayList arrayList = new ArrayList(i);
                                            Enemy[] enemyArrSelectEnemies = selectEnemies(firstTactics.target, i);
                                            if (enemyArrSelectEnemies.length >= i) {
                                                for (int i2 = 0; i2 < enemyArrSelectEnemies.length; i2++) {
                                                    arrayList.add(new FightActionEvaluation(9999 - i2, enemyArrSelectEnemies[i2]));
                                                }
                                                FightActionEvaluation fightActionEvaluation = new FightActionEvaluation(ADVANCED_TACTICS_EVAL_VALUE, null, skill);
                                                fightActionEvaluation.baseActions = arrayList;
                                                set.add(fightActionEvaluation);
                                                z2 = true;
                                                break;
                                            }
                                        }
                                        break;
                                    case CURE:
                                        PlayerChar playerCharSelectCharacter = selectCharacter(firstTactics);
                                        if (playerCharSelectCharacter != null && playerCharSelectCharacter.f93hp < playerCharSelectCharacter.maxHp) {
                                            set.add(new FightActionEvaluation(ADVANCED_TACTICS_EVAL_VALUE, playerCharSelectCharacter, skill));
                                            z2 = true;
                                            break;
                                        }
                                        break;
                                    case CURE_ALL:
                                        set.add(new FightActionEvaluation(ADVANCED_TACTICS_EVAL_VALUE, null, skill));
                                        z2 = true;
                                        break;
                                    case DAMAGE:
                                        Enemy enemySelectEnemy = selectEnemy(firstTactics.target);
                                        if (canUseDamageSkill(skill, enemySelectEnemy)) {
                                            set.add(new FightActionEvaluation(ADVANCED_TACTICS_EVAL_VALUE, enemySelectEnemy, skill));
                                            z2 = true;
                                            break;
                                        }
                                        break;
                                    case DAMAGE_ALL:
                                        if (canUseDamageSkill(skill, (Enemy) this.mCounterChars.get(0))) {
                                            set.add(new FightActionEvaluation(ADVANCED_TACTICS_EVAL_VALUE, null, skill));
                                            z2 = true;
                                            break;
                                        }
                                        break;
                                    case MY_STATUS:
                                    case STATUS_ALL:
                                        if (skill.type == Skill.SkillType.STATUS_ALL) {
                                            list = this.mFightContext.enchantsParty;
                                        } else {
                                            playerChar = this.mPlayerChar;
                                            list = this.mFightContext.enchantsPlayers[playerChar.index];
                                        }
                                        if (!existsSameEffectForStatusSkill(list, skill)) {
                                            set.add(new FightActionEvaluation(ADVANCED_TACTICS_EVAL_VALUE, playerChar, skill));
                                            z2 = true;
                                            break;
                                        }
                                        break;
                                    case STATUS:
                                        PlayerChar playerCharSelectCharacterForStatusSkill = selectCharacterForStatusSkill(firstTactics, skill);
                                        if (playerCharSelectCharacterForStatusSkill != null) {
                                            set.add(new FightActionEvaluation(ADVANCED_TACTICS_EVAL_VALUE, playerCharSelectCharacterForStatusSkill, skill));
                                            z2 = true;
                                            break;
                                        }
                                        break;
                                    case OTHER:
                                        if (skill.f107id == 30160) {
                                            PlayerChar playerCharSelectCharacter2 = selectCharacter(firstTactics);
                                            if (playerCharSelectCharacter2 != null) {
                                                set.add(new FightActionEvaluation(ADVANCED_TACTICS_EVAL_VALUE, playerCharSelectCharacter2, skill));
                                                z2 = true;
                                                break;
                                            }
                                        } else if (skill.f107id == 30190) {
                                            set.add(new FightActionEvaluation(ADVANCED_TACTICS_EVAL_VALUE, selectEnemy(firstTactics.target), skill));
                                            z2 = true;
                                            break;
                                        } else if ((skill.f107id == 40010 || skill.f107id == 40030 || skill.f107id == 40040) && !areAllEnemiesSleeping()) {
                                            set.add(new FightActionEvaluation(ADVANCED_TACTICS_EVAL_VALUE, null, skill));
                                            z2 = true;
                                            break;
                                        }
                                        break;
                                }
                            }
                            break;
                    }
                    if (z2) {
                        return z2;
                    }
                } else {
                    continue;
                }
            }
        }
        return z2;
    }

    private boolean areAllEnemiesSleeping() {
        for (GameChar gameChar : this.mCounterChars) {
            if (gameChar.isAlive() && !gameChar.isAsleep()) {
                return false;
            }
        }
        return true;
    }

    private PlayerChar selectCharacterForStatusSkill(AdvancedTactics advancedTactics, Skill skill) {
        AdvancedTactics.Target target = advancedTactics.target;
        if (target != AdvancedTactics.Target.FRONT && target != AdvancedTactics.Target.PARTY_ANY) {
            PlayerChar playerCharSelectCharacter = selectCharacter(advancedTactics);
            if (existsSameEffectForStatusSkill(this.mFightContext.enchantsPlayers[playerCharSelectCharacter.index], skill)) {
                return null;
            }
            return playerCharSelectCharacter;
        }
        int iMin = Math.min(target == AdvancedTactics.Target.FRONT ? 2 : 3, this.mOurChars.size());
        ArrayList arrayList = new ArrayList(iMin);
        for (int i = 0; i < iMin; i++) {
            PlayerChar playerChar = (PlayerChar) this.mOurChars.get(i);
            if (playerChar.isAlive() && !existsSameEffectForStatusSkill(this.mFightContext.enchantsPlayers[playerChar.index], skill)) {
                arrayList.add(playerChar);
            }
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        return (PlayerChar) arrayList.get(this.mRandom.nextInt(arrayList.size()));
    }

    private Inventory selectItem(AdvancedTactics advancedTactics) {
        switch (advancedTactics.actionSub) {
            case HP_RESTORE:
                for (Inventory inventory : this.mInventories) {
                    if (inventory.getBaseItem(this.mContext).getHpRestoreEffect() != null) {
                        return inventory;
                    }
                }
                return null;
            case MP_RESTORE:
                for (Inventory inventory2 : this.mInventories) {
                    if (inventory2.getBaseItem(this.mContext).getMpRestoreEffect() != null) {
                        return inventory2;
                    }
                }
                return null;
            case SPECIFIC:
                for (Inventory inventory3 : this.mInventories) {
                    if (inventory3.itemId == advancedTactics.targetId) {
                        return inventory3;
                    }
                }
                return null;
            default:
                return null;
        }
    }

    private Inventory selectItemForEquip(AdvancedTactics advancedTactics) {
        Inventory inventory = null;
        for (Inventory inventory2 : this.mInventories) {
            if (inventory2.itemId == advancedTactics.targetId) {
                if (inventory2.equippedCharId == this.mPlayerChar.f106id) {
                    return inventory2;
                }
                if (inventory == null && inventory2.equippedCharId == 0 && inventory2.canEquip(this.mContext, this.mPlayerChar, this.mInventories)) {
                    inventory = inventory2;
                }
            }
        }
        return inventory;
    }

    private static class CharSelector {
        private SortedMap<Integer, GameChar> chars = new TreeMap();

        public void putChar(int i, GameChar gameChar) {
            if (gameChar.isAlive()) {
                this.chars.put(Integer.valueOf(i), gameChar);
            }
        }

        public GameChar getChar() {
            if (this.chars.isEmpty()) {
                return null;
            }
            SortedMap<Integer, GameChar> sortedMap = this.chars;
            return sortedMap.get(sortedMap.firstKey());
        }

        public Collection<GameChar> getChars() {
            return this.chars.values();
        }
    }

    private PlayerChar selectCharacter(AdvancedTactics advancedTactics) {
        int i;
        AdvancedTactics.Target target = advancedTactics.target;
        switch (target) {
            case BACK:
                if (this.mOurChars.size() < 3) {
                    return null;
                }
                PlayerChar playerChar = (PlayerChar) this.mOurChars.get(2);
                if (playerChar.isAlive()) {
                    return playerChar;
                }
                return null;
            case FRONT:
            case FRONT_HP_HIGHEST:
            case FRONT_HP_LOWEST:
                ArrayList arrayList = new ArrayList();
                arrayList.add(this.mOurChars.get(0));
                if (this.mOurChars.size() > 1) {
                    arrayList.add(this.mOurChars.get(1));
                }
                if (target == AdvancedTactics.Target.FRONT) {
                    return (PlayerChar) selectRandom(arrayList);
                }
                CharSelector charSelector = new CharSelector();
                for (GameChar gameChar : arrayList) {
                    int i2 = (gameChar.f93hp * 10) + gameChar.index;
                    if (target == AdvancedTactics.Target.FRONT_HP_HIGHEST) {
                        i2 = -i2;
                    }
                    charSelector.putChar(i2, gameChar);
                }
                return (PlayerChar) charSelector.getChar();
            case HP_HIGHEST:
            case HP_LOWEST:
            case MP_HIGHEST:
            case MP_LOWEST:
                CharSelector charSelector2 = new CharSelector();
                for (GameChar gameChar2 : this.mOurChars) {
                    if (target == AdvancedTactics.Target.HP_HIGHEST || target == AdvancedTactics.Target.HP_LOWEST) {
                        i = gameChar2.f93hp * 10;
                    } else {
                        i = gameChar2.f94mp * 10;
                    }
                    int i3 = i + gameChar2.index;
                    if (target == AdvancedTactics.Target.HP_HIGHEST || target == AdvancedTactics.Target.MP_HIGHEST) {
                        i3 = -i3;
                    }
                    charSelector2.putChar(i3, gameChar2);
                }
                return (PlayerChar) charSelector2.getChar();
            case PARTY_ANY:
                return (PlayerChar) selectRandom(this.mOurChars);
            case SPECIFIC_CHAR:
                Iterator<? extends GameChar> it = this.mOurChars.iterator();
                while (it.hasNext()) {
                    PlayerChar playerChar2 = (PlayerChar) it.next();
                    if (playerChar2.f106id == advancedTactics.targetCharId) {
                        return playerChar2;
                    }
                }
                return (PlayerChar) selectRandom(this.mOurChars);
            case SELF:
                return this.mPlayerChar;
            default:
                return null;
        }
    }

    private GameChar selectRandom(List<? extends GameChar> list) {
        ArrayList arrayList = new ArrayList(list.size());
        for (GameChar gameChar : list) {
            if (gameChar.isAlive()) {
                arrayList.add(gameChar);
            }
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        return (GameChar) arrayList.get(this.mRandom.nextInt(arrayList.size()));
    }

    private Enemy selectEnemy(AdvancedTactics.Target target) {
        switch (target) {
            case ENEMY_ANY:
                return (Enemy) selectRandom(this.mCounterChars);
            case ENEMY_HP_HIGHEST:
            case ENEMY_HP_LOWEST:
                CharSelector charSelector = new CharSelector();
                for (GameChar gameChar : this.mCounterChars) {
                    int i = (gameChar.f93hp * 10) + gameChar.index;
                    if (target == AdvancedTactics.Target.ENEMY_HP_HIGHEST) {
                        i = -i;
                    }
                    charSelector.putChar(i, gameChar);
                }
                return (Enemy) charSelector.getChar();
            case ENEMY_ASLEEP:
                ArrayList arrayList = new ArrayList(this.mCounterChars.size());
                for (GameChar gameChar2 : this.mCounterChars) {
                    if (gameChar2.isAlive() && gameChar2.isAsleep()) {
                        arrayList.add((Enemy) gameChar2);
                    }
                }
                Collections.shuffle(arrayList);
                if (arrayList.isEmpty()) {
                    return null;
                }
                return (Enemy) arrayList.get(0);
            default:
                return null;
        }
    }

    private Enemy[] selectEnemies(AdvancedTactics.Target target, int i) {
        int i2 = 0;
        switch (target) {
            case ENEMY_ANY:
                ArrayList arrayList = new ArrayList(this.mCounterChars.size());
                for (GameChar gameChar : this.mCounterChars) {
                    if (gameChar.isAlive()) {
                        arrayList.add((Enemy) gameChar);
                    }
                }
                Collections.shuffle(arrayList);
                return (Enemy[]) arrayList.subList(0, Math.min(i, arrayList.size())).toArray(new Enemy[0]);
            case ENEMY_HP_HIGHEST:
            case ENEMY_HP_LOWEST:
                CharSelector charSelector = new CharSelector();
                for (GameChar gameChar2 : this.mCounterChars) {
                    int i3 = (gameChar2.f93hp * 10) + gameChar2.index;
                    if (target == AdvancedTactics.Target.ENEMY_HP_HIGHEST) {
                        i3 = -i3;
                    }
                    charSelector.putChar(i3, gameChar2);
                }
                Collection<GameChar> chars = charSelector.getChars();
                int iMin = Math.min(i, chars.size());
                Enemy[] enemyArr = new Enemy[iMin];
                Iterator<GameChar> it = chars.iterator();
                while (it.hasNext()) {
                    int i4 = i2 + 1;
                    enemyArr[i2] = (Enemy) it.next();
                    if (i4 >= iMin) {
                        return enemyArr;
                    }
                    i2 = i4;
                }
                return enemyArr;
            case ENEMY_ASLEEP:
                ArrayList arrayList2 = new ArrayList(this.mCounterChars.size());
                for (GameChar gameChar3 : this.mCounterChars) {
                    if (gameChar3.isAlive() && gameChar3.isAsleep()) {
                        arrayList2.add((Enemy) gameChar3);
                    }
                }
                Collections.shuffle(arrayList2);
                return (Enemy[]) arrayList2.subList(0, Math.min(i, arrayList2.size())).toArray(new Enemy[0]);
            default:
                return null;
        }
    }
}
