package com.shirobakama.autorpg2;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.shirobakama.autorpg2.AlertDialogFragment;
import com.shirobakama.autorpg2.TownActivity;
import com.shirobakama.autorpg2.adventure.TownFlagEngine;
import com.shirobakama.autorpg2.adventure.TownShopFlagEngine;
import com.shirobakama.autorpg2.entity.GameFlag;
import com.shirobakama.autorpg2.entity.Inventory;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.autorpg2.entity.ItemObject;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.entity.ShopItem;
import com.shirobakama.autorpg2.p001db.Persister;
import com.shirobakama.autorpg2.repo.ItemRepository;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.autorpg2.view.ItemAdapter;
import com.shirobakama.autorpg2.view.LabelValueItem;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class ShopOperation implements View.OnClickListener, TownActivity.TownOperation {
    protected TownActivity mActivity;
    private Button mBtnShopQuest;
    private ItemAdapter.ItemListItemManager mListItemManager;
    protected ListView mLvItems;
    List<ShopItem> shopItems = null;
    private TownShopFlagEngine mFlagEngine = new TownShopFlagEngine();

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public TownFlagEngine.Result acceptQuest(String str) {
        return null;
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public ArrayList<LabelValueItem> getMenuItems() {
        return null;
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public boolean onActivityResult(int i, int i2, Intent intent) {
        return false;
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public void onReturningAdventure() {
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public TownFlagEngine.Result refuseQuest(String str) {
        return null;
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public void selectMenu(Bundle bundle, int i, List<LabelValueItem> list) {
    }

    public ShopOperation(TownActivity townActivity) {
        this.mActivity = townActivity;
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public void onCreate(View view) {
        this.shopItems = new ArrayList();
        onChangeTown();
        this.mLvItems = (ListView) view.findViewById(C0380R.id.lvItems);
        this.mLvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.shirobakama.autorpg2.ShopOperation.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view2, int i, long j) {
                ShopOperation.this.clickItem((ItemAdapter.ItemListItem) adapterView.getItemAtPosition(i), view2, i);
            }
        });
        view.findViewById(C0380R.id.btnShopOther).setOnClickListener(this);
        this.mBtnShopQuest = (Button) view.findViewById(C0380R.id.btnShopQuest);
        this.mBtnShopQuest.setOnClickListener(this);
        this.mBtnShopQuest.setVisibility(8);
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public void activate(boolean z) {
        ItemAdapter itemAdapter;
        boolean z2;
        setBtnShopQuestVisibilityByEngine();
        modifyShopItemsByEngine();
        if (this.mListItemManager == null) {
            this.mListItemManager = new ItemAdapter.ItemListItemManager(this.mActivity.inventoryEquipmentHandler, this.mActivity.itemEquippableHandler);
            this.mListItemManager.setInventoryLabel(this.mActivity.createInventoryLabel());
            this.mListItemManager.setInventories(this.mActivity.game.inventories);
            this.mListItemManager.setShopItemLabel(this.mActivity.getString(C0380R.string.lbl_shop_items));
            this.mListItemManager.setShopItems(this.shopItems);
            itemAdapter = new ItemAdapter(this.mActivity, this.mListItemManager);
            this.mLvItems.setAdapter((ListAdapter) itemAdapter);
            z2 = false;
        } else {
            itemAdapter = (ItemAdapter) this.mLvItems.getAdapter();
            this.mListItemManager.setInventoryLabel(this.mActivity.createInventoryLabel());
            this.mListItemManager.setInventories(this.mActivity.game.inventories);
            z2 = true;
        }
        boolean z3 = !this.mActivity.game.isAdventuring(this.mActivity);
        itemAdapter.setButtonEnabled(0, z3);
        itemAdapter.setButtonEnabled(1, z3);
        itemAdapter.setButtonEnabled(2, z3);
        if (z2) {
            itemAdapter.notifyDataSetChanged();
        }
    }

    private void modifyShopItemsByEngine() {
        TownShopFlagEngine townShopFlagEngine = this.mFlagEngine;
        TownActivity townActivity = this.mActivity;
        townShopFlagEngine.modifyShopItemsByEngine(townActivity, townActivity.game, this.shopItems);
        TownShopFlagEngine townShopFlagEngine2 = this.mFlagEngine;
        TownActivity townActivity2 = this.mActivity;
        townShopFlagEngine2.resetState(townActivity2, townActivity2.game);
    }

    private void setBtnShopQuestVisibilityByEngine() {
        TownShopFlagEngine townShopFlagEngine = this.mFlagEngine;
        TownActivity townActivity = this.mActivity;
        TownFlagEngine.Result resultPeekShopHearing = townShopFlagEngine.peekShopHearing(townActivity, townActivity.game);
        this.mBtnShopQuest.setVisibility((resultPeekShopHearing == null || resultPeekShopHearing.message == null) ? 8 : 0);
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public void onChangeTown() {
        this.shopItems.clear();
        this.shopItems.addAll(ItemRepository.getShopItemsForTown(this.mActivity.getApplicationContext(), this.mActivity.game.townId));
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == 2131165283) {
            doHearing();
        } else if (view.getId() == 2131165282) {
            this.mActivity.action();
        }
    }

    private void doHearing() {
        TownShopFlagEngine townShopFlagEngine = this.mFlagEngine;
        TownActivity townActivity = this.mActivity;
        TownFlagEngine.Result resultNextShopHearing = townShopFlagEngine.nextShopHearing(townActivity, townActivity.game);
        if (resultNextShopHearing == null) {
            return;
        }
        if (this.mActivity.processEngineResult(resultNextShopHearing)) {
            activate(false);
        } else {
            setBtnShopQuestVisibilityByEngine();
        }
    }

    protected void clickItem(ItemAdapter.ItemListItem itemListItem, View view, int i) {
        switch (itemListItem.type) {
            case INVENTORY:
                if (view.getId() == 2131165241) {
                    this.mActivity.changeEquipmentInventory(itemListItem);
                    break;
                } else if (view.getId() == 2131165242) {
                    this.mActivity.moveToStock(this.mListItemManager, itemListItem);
                    break;
                } else if (view.getId() == 2131165243) {
                    this.mActivity.confirmSellItem(itemListItem, true);
                    break;
                } else {
                    this.mActivity.showItemDetailOrUseItem(itemListItem);
                    break;
                }
            case SHOP:
                if (view.getId() == 2131165241) {
                    confirmBuyItem(itemListItem);
                    break;
                } else {
                    this.mActivity.showItemDetailOrUseItem(itemListItem);
                    break;
                }
        }
    }

    private void confirmBuyItem(ItemAdapter.ItemListItem itemListItem) {
        if (itemListItem.itemObject.getBuyPrice(this.mActivity) > this.mActivity.game.gold) {
            TownActivity townActivity = this.mActivity;
            townActivity.addMessage(townActivity.getString(C0380R.string.msg_no_money));
            return;
        }
        if (this.mActivity.game.inventories.size() >= this.mActivity.game.getMaxInventoryCount()) {
            TownActivity townActivity2 = this.mActivity;
            townActivity2.addMessage(townActivity2.getString(C0380R.string.msg_no_room_in_inventory));
            return;
        }
        boolean zCanEquip = false;
        Item baseItem = itemListItem.itemObject.getBaseItem(this.mActivity);
        if (baseItem.equipable) {
            for (PlayerChar playerChar : this.mActivity.game.characters) {
                ItemObject itemObject = itemListItem.itemObject;
                TownActivity townActivity3 = this.mActivity;
                zCanEquip |= itemObject.canEquip(townActivity3, playerChar, townActivity3.game.inventories);
                if (zCanEquip) {
                    break;
                }
            }
        } else {
            zCanEquip = true;
        }
        int i = zCanEquip ? C0380R.string.msg_dlg_confirm_buy_item : C0380R.string.msg_dlg_confirm_buy_item_not_equippable;
        String strMakeItemDialogTitle = this.mActivity.makeItemDialogTitle(itemListItem.itemObject, itemListItem.itemObject.getBuyPrice(this.mActivity));
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(strMakeItemDialogTitle);
        decorator.setMessage(i);
        if (baseItem.type == Item.Type.CONSUMABLE || baseItem.f97id == 5160) {
            decorator.setNeutralText(C0380R.string.lbl_dlg_confirm_sell_buy_maximum);
        }
        ConfirmationDialogFragment.show(decorator, this.mActivity, TownActivity.Confirmation.BUY_ITEM.ordinal(), itemListItem.itemObject.f98id);
    }

    protected void buyItem(int i, boolean z) {
        int maxInventoryCount = this.mActivity.game.getMaxInventoryCount() - this.mActivity.game.inventories.size();
        if (maxInventoryCount <= 0) {
            return;
        }
        ShopItem extraShopItem = this.mFlagEngine.getExtraShopItem(i);
        if (extraShopItem == null) {
            TownActivity townActivity = this.mActivity;
            extraShopItem = ItemRepository.getShopItem(townActivity, townActivity.game.townId, i);
        }
        if (extraShopItem == null) {
            Toast.makeText(this.mActivity, C0380R.string.msg_internal_error, 0).show();
            return;
        }
        int buyPrice = extraShopItem.getBuyPrice(this.mActivity);
        int iMax = z ? Math.max(1, Math.min(this.mActivity.game.gold / buyPrice, maxInventoryCount)) : 1;
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < iMax; i2++) {
            Inventory inventory = new Inventory(extraShopItem);
            arrayList.add(inventory);
            this.mActivity.game.inventories.add(inventory);
            this.mActivity.game.gold -= buyPrice;
        }
        GameFlag orCreateFlag = this.mActivity.game.getOrCreateFlag(GameFlag.Key.asItemGot(extraShopItem.getBaseItem(this.mActivity)));
        orCreateFlag.addOptionAsInt(iMax);
        Persister persister = new Persister(this.mActivity);
        try {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                persister.writeInventoryAdding((Inventory) it.next(), this.mActivity.game);
            }
            LinkedList linkedList = new LinkedList();
            linkedList.add(orCreateFlag);
            persister.writeFlags(linkedList);
            this.mListItemManager.setInventoryLabel(this.mActivity.createInventoryLabel());
            this.mListItemManager.notifyAdapterDataSetChanged();
            modifyShopItemsByEngine();
            if (arrayList.size() == 1) {
                TownActivity townActivity2 = this.mActivity;
                townActivity2.addMessage(townActivity2.getString(C0380R.string.msg_item_bought, new Object[]{((Inventory) arrayList.get(0)).getName(this.mActivity), Integer.valueOf(this.mActivity.game.gold)}));
            } else {
                TownActivity townActivity3 = this.mActivity;
                townActivity3.addMessage(townActivity3.getString(C0380R.string.msg_item_bought_all, new Object[]{((Inventory) arrayList.get(0)).getName(this.mActivity), Integer.valueOf(arrayList.size()), Integer.valueOf(this.mActivity.game.gold)}));
            }
        } catch (SQLException e) {
            DeviceUtil.handleSqliteException(this.mActivity, e);
        }
    }

    ItemAdapter.ItemListItemManager getListItemManager() {
        return this.mListItemManager;
    }
}
