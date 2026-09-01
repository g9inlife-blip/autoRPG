package com.shirobakama.autorpg2.adventure;

import android.content.Context;
import com.shirobakama.autorpg2.adventure.FlagEngine;
import com.shirobakama.autorpg2.adventure.TownFlagEngine;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.GameFlag;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.autorpg2.entity.Quest;
import com.shirobakama.autorpg2.entity.ShopItem;
import com.shirobakama.autorpg2.repo.ItemDb;
import com.shirobakama.autorpg2.repo.ItemRepository;
import com.shirobakama.autorpg2.repo.QuestDb;
import com.shirobakama.logquest2.C0380R;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class TownShopFlagEngine extends TownFlagEngine {
    private static final int SHOP_ITEM_ID_MONSTERS_JEWEL = 2000001;

    @Override // com.shirobakama.autorpg2.adventure.TownFlagEngine
    protected String processAcceptQuestSub(TownFlagEngine.Result result, FlagEngine.CurrentValues currentValues, Quest quest) {
        return null;
    }

    @Override // com.shirobakama.autorpg2.adventure.TownFlagEngine
    protected String processRefuseQuestSub(TownFlagEngine.Result result, FlagEngine.CurrentValues currentValues, Quest quest) {
        return null;
    }

    public TownFlagEngine.Result peekShopHearing(Context context, GameContext gameContext) {
        TownFlagEngine.TownEngineState townEngineStateCopy = this.state.copy();
        TownFlagEngine.Result resultProcessShopHearing = processShopHearing(context, gameContext);
        this.state = townEngineStateCopy;
        return resultProcessShopHearing;
    }

    public TownFlagEngine.Result nextShopHearing(Context context, GameContext gameContext) {
        return processShopHearing(context, gameContext);
    }

    private TownFlagEngine.Result processShopHearing(Context context, GameContext gameContext) {
        checkConversationState(context, gameContext);
        if (gameContext.townId != 4) {
            return null;
        }
        return processShopMerchant(new FlagEngine.CurrentValues(this.random, context, gameContext));
    }

    private TownFlagEngine.Result processShopMerchant(FlagEngine.CurrentValues currentValues) {
        this.state.acceptQuest = false;
        this.state.refuseQuest = false;
        if (!FlagEngine.getQuestState(currentValues, QuestDb.QUEST_ATTACK_DARK_LORD).cleared || currentValues.isOn(GameFlag.Key.asQuest(QuestConst.FLAG_EXTRA_DUNGEON_ITEM_MSG_READ))) {
            return null;
        }
        Item item = ItemRepository.getItem(currentValues.context, ItemDb.ITEM_MONSTERS_JEWEL);
        if (!currentValues.isOn(GameFlag.Key.asHasItem(item)) && !currentValues.isOn(GameFlag.Key.asStockItem(item))) {
            return messageResult(currentValues, new int[]{C0380R.string.cnv_merchant_trading_post_cleared_1, C0380R.string.cnv_merchant_trading_post_cleared_2, C0380R.string.cnv_merchant_trading_post_cleared_3}, new Object[0]);
        }
        TownFlagEngine.Result resultProcessJewelReading = processJewelReading(currentValues);
        if (resultProcessJewelReading != null) {
            return resultProcessJewelReading;
        }
        return null;
    }

    public void modifyShopItemsByEngine(Context context, GameContext gameContext, List<ShopItem> list) {
        if (gameContext.townId != 4) {
            return;
        }
        FlagEngine.CurrentValues currentValues = new FlagEngine.CurrentValues(this.random, context, gameContext);
        if (FlagEngine.getQuestState(currentValues, QuestDb.QUEST_ATTACK_DARK_LORD).cleared) {
            Item item = ItemRepository.getItem(currentValues.context, ItemDb.ITEM_MONSTERS_JEWEL);
            boolean z = false;
            boolean z2 = currentValues.isOn(GameFlag.Key.asHasItem(item)) || currentValues.isOn(GameFlag.Key.asStockItem(item));
            boolean zIsOn = currentValues.isOn(GameFlag.Key.asQuest(QuestConst.FLAG_EXTRA_DUNGEON_ITEM_MSG_READ));
            if (!z2 && !zIsOn) {
                z = true;
            }
            ShopItem shopItemCreateShopItemMonstersJewel = createShopItemMonstersJewel();
            if (z) {
                if (list.contains(shopItemCreateShopItemMonstersJewel)) {
                    return;
                }
                list.add(shopItemCreateShopItemMonstersJewel);
                return;
            }
            list.remove(shopItemCreateShopItemMonstersJewel);
        }
    }

    private ShopItem createShopItemMonstersJewel() {
        ShopItem shopItem = new ShopItem();
        shopItem.f98id = SHOP_ITEM_ID_MONSTERS_JEWEL;
        shopItem.itemId = ItemDb.ITEM_MONSTERS_JEWEL;
        return shopItem;
    }

    public ShopItem getExtraShopItem(int i) {
        if (i == SHOP_ITEM_ID_MONSTERS_JEWEL) {
            return createShopItemMonstersJewel();
        }
        return null;
    }
}
