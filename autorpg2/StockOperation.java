package com.shirobakama.autorpg2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.shirobakama.autorpg2.TownActivity;
import com.shirobakama.autorpg2.adventure.TownFlagEngine;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.Stock;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.autorpg2.view.ItemAdapter;
import com.shirobakama.autorpg2.view.LabelValueItem;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class StockOperation implements View.OnClickListener, TownActivity.TownOperation {
    private static ArrayList<LabelValueItem> menuItems;
    private TownActivity mActivity;
    private boolean mLastSortIdTypeAndName = true;
    private ItemAdapter.ItemListItemManager mListItemManager;
    private ListView mLvStocks;

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public TownFlagEngine.Result acceptQuest(String str) {
        return null;
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public boolean onActivityResult(int i, int i2, Intent intent) {
        return false;
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public void onChangeTown() {
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

    public StockOperation(TownActivity townActivity) {
        this.mActivity = townActivity;
        if (menuItems == null) {
            menuItems = new ArrayList<>();
        }
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public void onCreate(View view) {
        this.mLvStocks = (ListView) view.findViewById(C0380R.id.lvStocks);
        this.mLvStocks.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.shirobakama.autorpg2.StockOperation.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view2, int i, long j) {
                StockOperation.this.clickItem((ItemAdapter.ItemListItem) adapterView.getItemAtPosition(i), view2, i);
            }
        });
        view.findViewById(C0380R.id.btnStockOther).setOnClickListener(this);
        view.findViewById(C0380R.id.btnStockItemCatalog).setOnClickListener(this);
        view.findViewById(C0380R.id.btnStockMonsterCatalog).setOnClickListener(this);
        view.findViewById(C0380R.id.btnStockSort).setOnClickListener(this);
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public void activate(boolean z) {
        ItemAdapter itemAdapter;
        boolean z2;
        if (this.mListItemManager == null) {
            this.mListItemManager = new ItemAdapter.ItemListItemManager(this.mActivity.inventoryEquipmentHandler, this.mActivity.itemEquippableHandler);
            this.mListItemManager.setInventoryLabel(this.mActivity.createInventoryLabel());
            this.mListItemManager.setInventories(this.mActivity.game.inventories);
            this.mListItemManager.setStockLabel(this.mActivity.getString(C0380R.string.lbl_stock_items));
            this.mListItemManager.setStocks(this.mActivity.game.stocks);
            itemAdapter = new ItemAdapter(this.mActivity, this.mListItemManager);
            this.mLvStocks.setAdapter((ListAdapter) itemAdapter);
            z2 = false;
        } else {
            itemAdapter = (ItemAdapter) this.mLvStocks.getAdapter();
            this.mListItemManager.setInventoryLabel(this.mActivity.createInventoryLabel());
            this.mListItemManager.setInventories(this.mActivity.game.inventories);
            this.mListItemManager.setStocks(this.mActivity.game.stocks);
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
            case STOCK:
                if (view.getId() == 2131165241) {
                    this.mActivity.moveToInventory(this.mListItemManager, itemListItem);
                    break;
                } else if (view.getId() == 2131165242) {
                    this.mActivity.confirmSellItem(itemListItem, false);
                    break;
                } else {
                    this.mActivity.showItemDetailOrUseItem(itemListItem);
                    break;
                }
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == 2131165287) {
            showItemCatalog();
            return;
        }
        if (view.getId() == 2131165288) {
            showMonsterCatalog();
        } else if (view.getId() == 2131165289) {
            this.mActivity.action();
        } else if (view.getId() == 2131165290) {
            sortStocks();
        }
    }

    ItemAdapter.ItemListItemManager getListItemManager() {
        return this.mListItemManager;
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public ArrayList<LabelValueItem> getMenuItems() {
        return menuItems;
    }

    private void showItemCatalog() {
        if (this.mActivity.canMoveToAnotherActivity()) {
            Intent intent = new Intent(this.mActivity.getApplicationContext(), (Class<?>) CatalogActivity.class);
            CatalogActivity.setForItemCatalog(intent);
            GameContext.game = this.mActivity.game.copy();
            DeviceUtil.setRequestOrientationForIntent(this.mActivity, intent);
            this.mActivity.startActivity(intent);
        }
    }

    private void showMonsterCatalog() {
        if (this.mActivity.canMoveToAnotherActivity()) {
            Intent intent = new Intent(this.mActivity.getApplicationContext(), (Class<?>) CatalogActivity.class);
            CatalogActivity.setForMonsterCatalog(intent);
            GameContext.game = this.mActivity.game.copy();
            DeviceUtil.setRequestOrientationForIntent(this.mActivity, intent);
            this.mActivity.startActivity(intent);
        }
    }

    private void sortStocks() {
        if (this.mLastSortIdTypeAndName) {
            TownActivity townActivity = this.mActivity;
            Stock.sortByTypeAndId(townActivity, townActivity.game.stocks);
        } else {
            TownActivity townActivity2 = this.mActivity;
            Stock.sortByTypeAndName(townActivity2, townActivity2.game.stocks);
        }
        this.mLastSortIdTypeAndName = !this.mLastSortIdTypeAndName;
        this.mListItemManager.notifyAdapterDataSetChanged();
    }
}
