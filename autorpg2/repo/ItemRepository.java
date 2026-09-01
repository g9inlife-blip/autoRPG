package com.shirobakama.autorpg2.repo;

import android.content.Context;
import android.util.SparseArray;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.autorpg2.entity.ShopItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public final class ItemRepository {
    private static final int SHOP_ITEM_ID_BASE = 1000000;
    public static final int SHOP_ITEM_ID_EXTRA_BASE = 2000000;
    private static SparseArray<Item> allItems;
    private static Map<String, Integer> itemIdsForSymbol;
    private static SparseArray<List<ShopItem>> shopItems = new SparseArray<>();

    private ItemRepository() {
    }

    private static void initialize(Context context) {
        if (allItems == null) {
            allItems = new SparseArray<>();
            SparseArray<List<ShopItem>> sparseArray = shopItems;
            if (sparseArray != null) {
                sparseArray.clear();
            }
            ItemDb.initialize(context);
            int i = 1;
            getItem(context, 1540).useableInTown = true;
            getItem(context, ItemDb.ITEM_MEDAL_OF_LORDS).useableInTown = true;
            getItem(context, ItemDb.ITEM_ASTRO_BOOK).useableInTown = true;
            getItem(context, ItemDb.ITEM_SECRET_SCROLL).useableInTown = true;
            getItem(context, ItemDb.ITEM_DWARVEN_ANVIL).useableInTown = true;
            getItem(context, ItemDb.ITEM_FIGHTER_SYMBOL).useableInTown = true;
            getItem(context, ItemDb.ITEM_MAGIC_BOOK).useableInTown = true;
            getItem(context, ItemDb.ITEM_THIEVES_GUILD).useableInTown = true;
            getItem(context, ItemDb.ITEM_HOLY_SYMBOL).useableInTown = true;
            for (int i2 = 0; i2 < allItems.size(); i2++) {
                int iKeyAt = allItems.keyAt(i2);
                if (iKeyAt != 1010) {
                    allItems.get(iKeyAt).number = i;
                    i++;
                }
            }
        }
    }

    static Item create(Item.Type type, int i, String str, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z) {
        Item item = new Item(i);
        item.type = type;
        item.symbol = str;
        item.nameStringId = i2;
        item.drawableId = i3;
        item.descriptionStringId = i4;
        item.price = i5;
        item.attrBase = i8;
        item.equipable = false;
        item.diceFace = i6;
        item.diceNum = i7;
        item.attrBase = i8;
        item.artifact = z;
        return item;
    }

    static Item asEquippable(Item item, boolean[] zArr) {
        item.equipable = true;
        item.classEquipable = zArr;
        return item;
    }

    static Item asWeapon(Item item, Item.WeaponType weaponType, int i, boolean z, int i2, boolean z2) {
        item.twoHanded = z;
        item.longRange = z2;
        item.weaponType = weaponType;
        if (i != 0) {
            item.addEffect(new Item.Effect(GameChar.Status.WEAPON_MAGIC_BONUS, i));
        }
        if (i2 != 0) {
            item.addEffect(new Item.Effect(GameChar.Status.SHIELD_DODGE, i2));
        }
        return item;
    }

    static Item asArmor(Item item, int i) {
        if (i != 0) {
            item.addEffect(new Item.Effect(GameChar.Status.ARMOR_MAGIC_BONUS, i));
        }
        return item;
    }

    static Item asShield(Item item, int i) {
        if (i != 0) {
            item.addEffect(new Item.Effect(GameChar.Status.SHIELD_MAGIC_BONUS, i));
        }
        return item;
    }

    static void addItem(Item item) {
        allItems.put(item.f97id, item);
    }

    static void addShopItemToTown(Context context, int i, int i2, int i3) {
        List<ShopItem> arrayList = shopItems.get(i2);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            shopItems.put(i2, arrayList);
        }
        ShopItem shopItem = new ShopItem();
        shopItem.f98id = arrayList.size() + SHOP_ITEM_ID_BASE + 1;
        shopItem.itemId = i;
        if (i3 != 0) {
            Item item = getItem(context, i);
            shopItem.extraEffects.add(new Item.Effect(item.type == Item.Type.WEAPON ? GameChar.Status.WEAPON_MAGIC_BONUS : item.type == Item.Type.ARMOR ? GameChar.Status.ARMOR_MAGIC_BONUS : GameChar.Status.SHIELD_MAGIC_BONUS, i3));
        }
        arrayList.add(shopItem);
    }

    public static List<ShopItem> getShopItemsForTown(Context context, int i) {
        if (allItems == null) {
            initialize(context);
        }
        return shopItems.get(i);
    }

    public static Item getItem(Context context, int i) {
        if (allItems == null) {
            initialize(context);
        }
        Item item = allItems.get(i);
        if (item == null) {
            return null;
        }
        if (item.name == null) {
            item.name = context.getString(item.nameStringId);
        }
        return item;
    }

    public static ShopItem getShopItem(Context context, int i, int i2) {
        for (ShopItem shopItem : getShopItemsForTown(context, i)) {
            if (shopItem.f98id == i2) {
                return shopItem;
            }
        }
        return null;
    }

    public static Item getItemBySymbol(Context context, String str) {
        if (itemIdsForSymbol == null) {
            if (allItems == null) {
                initialize(context);
            }
            itemIdsForSymbol = new HashMap();
            for (int i = 0; i < allItems.size(); i++) {
                Item itemValueAt = allItems.valueAt(i);
                itemIdsForSymbol.put(itemValueAt.symbol, Integer.valueOf(itemValueAt.f97id));
            }
        }
        return getItem(context, itemIdsForSymbol.get(str).intValue());
    }

    public static void flush() {
        allItems = null;
    }
}
