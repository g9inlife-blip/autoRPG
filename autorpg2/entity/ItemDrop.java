package com.shirobakama.autorpg2.entity;

import java.util.Random;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class ItemDrop {
    public static final int FACTOR = 1000;
    public int enchantMax;
    public int factor;
    public int itemId;

    public ItemDrop(int i, int i2, int i3) {
        this.factor = i;
        this.itemId = i2;
        this.enchantMax = i3;
    }

    public static ItemDrop getItemDrop(ItemDrop[] itemDropArr, Random random, double d) {
        if (itemDropArr == null || itemDropArr.length == 0) {
            return null;
        }
        int iNextInt = random.nextInt(1000);
        for (ItemDrop itemDrop : itemDropArr) {
            double d2 = itemDrop.factor;
            Double.isNaN(d2);
            double d3 = d2 * (1.0d + d);
            double d4 = iNextInt;
            if (d4 < d3) {
                return itemDrop;
            }
            Double.isNaN(d4);
            iNextInt = (int) (d4 - d3);
        }
        return null;
    }
}
