package com.shirobakama.autorpg2.entity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class FightContext {
    public List<Enchant>[] enchantsEnemies;
    public List<Enemy> enemies;
    public boolean isEvent;
    public Boolean isExpected;
    public boolean isWandering;
    private Random mRandom;
    public List<Monster> monsters;
    public int round;
    public List<Enchant> enchantsField = new LinkedList();
    public List<Enchant> enchantsParty = new LinkedList();
    public List<Enchant> enchantsEnemyParty = new LinkedList();
    public List<Enchant>[] enchantsPlayers = new List[3];
    public boolean playerSurprise = false;
    public boolean enemySurprise = false;
    public boolean playerRunning = false;

    public FightContext(int i, Random random) {
        int i2 = 0;
        int i3 = 0;
        while (true) {
            List<Enchant>[] listArr = this.enchantsPlayers;
            if (i3 >= listArr.length) {
                break;
            }
            listArr[i3] = new LinkedList();
            i3++;
        }
        this.enchantsEnemies = new List[i];
        while (true) {
            List<Enchant>[] listArr2 = this.enchantsEnemies;
            if (i2 < listArr2.length) {
                listArr2[i2] = new LinkedList();
                i2++;
            } else {
                this.mRandom = random;
                return;
            }
        }
    }

    public Random getRandom() {
        return this.mRandom;
    }

    public void addEnchantToField(Enchant enchant) {
        this.enchantsField.add(enchant);
    }

    public void addEnchantToParty(Enchant enchant) {
        this.enchantsParty.add(enchant);
    }

    public void addEnchantToEnemyParty(Enchant enchant) {
        this.enchantsEnemyParty.add(enchant);
    }

    public void addEnchantToPlayer(int i, Enchant enchant) {
        this.enchantsPlayers[i].add(enchant);
    }

    public void addEnchantToEnemy(int i, Enchant enchant) {
        this.enchantsEnemies[i].add(enchant);
    }

    public void applyEnchantToPlayer(PlayerChar playerChar, int i) {
        Iterator<Enchant> it = this.enchantsField.iterator();
        while (it.hasNext()) {
            it.next().apply(playerChar);
        }
        Iterator<Enchant> it2 = this.enchantsParty.iterator();
        while (it2.hasNext()) {
            it2.next().apply(playerChar);
        }
        Iterator<Enchant> it3 = this.enchantsPlayers[i].iterator();
        while (it3.hasNext()) {
            it3.next().apply(playerChar);
        }
    }

    public void applyEnchantToEnemy(Enemy enemy, int i) {
        Iterator<Enchant> it = this.enchantsField.iterator();
        while (it.hasNext()) {
            it.next().apply(enemy);
        }
        Iterator<Enchant> it2 = this.enchantsParty.iterator();
        while (it2.hasNext()) {
            it2.next().apply(enemy);
        }
        Iterator<Enchant> it3 = this.enchantsEnemies[i].iterator();
        while (it3.hasNext()) {
            it3.next().apply(enemy);
        }
    }
}
