package com.shirobakama.autorpg2.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.shirobakama.autorpg2.entity.Inventory;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.autorpg2.entity.ItemObject;
import com.shirobakama.autorpg2.entity.ShopItem;
import com.shirobakama.autorpg2.entity.Stock;
import com.shirobakama.logquest2.BuildConfig;
import com.shirobakama.logquest2.C0380R;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class ItemAdapter extends ArrayAdapter<ItemListItem> {
    protected static final String TAG = "item-adpt";
    private static final int VIEW_TYPE_CHARACTERS = 0;
    private static final int VIEW_TYPE_ITEM = 2;
    private static final int VIEW_TYPE_SEPARATOR = 1;
    private boolean[] mButtonEnabled;
    private LayoutInflater mInflater;
    private ItemListItemManager mItemListItemManager;

    public interface CharacterViewHandler {
        void refresh(View view);
    }

    public interface InventoryEquipmentHandler {
        String getEquippedCharInfo(Inventory inventory);
    }

    public interface ItemEquippableHandler {
        String getEquippableInfo(ItemObject itemObject);
    }

    public enum ListItemType {
        CHARACTERS,
        SEPARATOR,
        INVENTORY,
        STOCK,
        SHOP,
        LOG_INVENTORY
    }

    public static class ViewHolder {
        public Button btnButton1;
        public Button btnButton2;
        public Button btnButton3;
        public ImageView ivIcon;
        public TextView tvCount;
        public TextView tvDescription;
        public TextView tvEquipment;
        public TextView tvName;
        public TextView tvSeparator;
        public LinearLayout vwCharacter;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        return 3;
    }

    public static class ItemListItem {
        public ItemObject itemObject;
        public String label;
        public ListItemType type;

        public ItemListItem(ItemObject itemObject, ListItemType listItemType) {
            this.itemObject = itemObject;
            this.type = listItemType;
        }

        public ItemListItem(String str) {
            this.label = str;
            this.type = ListItemType.SEPARATOR;
        }

        public ItemListItem() {
            this.type = ListItemType.CHARACTERS;
        }

        public int hashCode() {
            int iOrdinal = this.type.ordinal() * 31;
            String str = this.label;
            int iHashCode = iOrdinal + (str == null ? 0 : str.hashCode());
            ItemObject itemObject = this.itemObject;
            return iHashCode + (itemObject != null ? itemObject.hashCode() : 0);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof ItemListItem)) {
                return false;
            }
            ItemListItem itemListItem = (ItemListItem) obj;
            ListItemType listItemType = this.type;
            if (listItemType != itemListItem.type) {
                return false;
            }
            if (listItemType == ListItemType.SEPARATOR) {
                return TextUtils.equals(this.label, itemListItem.label);
            }
            if (this.type == ListItemType.CHARACTERS) {
                return true;
            }
            if (this.itemObject != null || itemListItem.itemObject == null) {
                return (this.itemObject == null || itemListItem.itemObject != null) && this.itemObject.f98id == itemListItem.itemObject.f98id;
            }
            return false;
        }

        public String toString() {
            return "[ILI:" + this.itemObject + "," + this.type + "," + this.label + "]";
        }
    }

    public static class ItemListItemManager {
        private ItemAdapter mAdapter;
        protected CharacterViewHandler mCharacterViewHandler;
        protected InventoryEquipmentHandler mInventoryEquipmentHandler;
        protected ItemEquippableHandler mItemEquippableHandler;
        private ItemList mItemList = new ItemList();

        public ItemListItemManager(InventoryEquipmentHandler inventoryEquipmentHandler, ItemEquippableHandler itemEquippableHandler) {
            this.mItemList.setInventoryEquipmentHandler(inventoryEquipmentHandler);
            this.mInventoryEquipmentHandler = inventoryEquipmentHandler;
            this.mItemEquippableHandler = itemEquippableHandler;
        }

        public void setCharacterViewHandler(CharacterViewHandler characterViewHandler) {
            this.mCharacterViewHandler = characterViewHandler;
            this.mItemList.hasCharacterView = true;
        }

        public void setLogInventoryMode() {
            this.mItemList.isLogInventory = true;
        }

        public void setAdapter(ItemAdapter itemAdapter) {
            this.mAdapter = itemAdapter;
        }

        public void notifyAdapterDataSetChanged() {
            this.mAdapter.notifyDataSetChanged();
        }

        public List<ItemListItem> getItems() {
            return this.mItemList;
        }

        public void setInventoryLabel(String str) {
            this.mItemList.inventoryLabel = str;
        }

        public String getInventoryLabel() {
            return this.mItemList.inventoryLabel;
        }

        public void setInventories(List<Inventory> list) {
            this.mItemList.inventories = list;
        }

        public void setShopItemLabel(String str) {
            this.mItemList.shopItemLabel = str;
        }

        public void setShopItems(List<ShopItem> list) {
            this.mItemList.shopItems = list;
        }

        public void setStockLabel(String str) {
            this.mItemList.stockLabel = str;
        }

        public void setStocks(List<Stock> list) {
            this.mItemList.stocks = list;
        }
    }

    private static class ItemList extends AbstractList<ItemListItem> {
        private static final ItemListItem CHARACTER_ITEM_LIST_ITEM = new ItemListItem();
        public boolean hasCharacterView;
        public List<Inventory> inventories;
        public String inventoryLabel;
        public boolean isLogInventory;
        private InventoryEquipmentHandler mInventoryEquipmentHandler;
        public String shopItemLabel;
        public List<ShopItem> shopItems;
        public String stockLabel;
        public List<Stock> stocks;

        public ItemList() {
        }

        public ItemList(List<ItemListItem> list) {
        }

        public void setInventoryEquipmentHandler(InventoryEquipmentHandler inventoryEquipmentHandler) {
            this.mInventoryEquipmentHandler = inventoryEquipmentHandler;
        }

        /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
        @Override // java.util.AbstractList, java.util.List
        public ItemListItem get(int i) {
            int size;
            if (!this.hasCharacterView) {
                size = i;
            } else {
                if (i == 0) {
                    return CHARACTER_ITEM_LIST_ITEM;
                }
                size = i - 1;
            }
            String str = this.inventoryLabel;
            if (str != null) {
                if (size == 0) {
                    return new ItemListItem(str);
                }
                size--;
            }
            List<Inventory> list = this.inventories;
            if (list != null) {
                if (!this.isLogInventory && this.mInventoryEquipmentHandler != null) {
                    for (Inventory inventory : list) {
                        if (this.mInventoryEquipmentHandler.getEquippedCharInfo(inventory) == null) {
                            if (size == 0) {
                                return new ItemListItem(inventory, ListItemType.INVENTORY);
                            }
                            size--;
                        }
                    }
                } else {
                    if (size < this.inventories.size()) {
                        return new ItemListItem(this.inventories.get(size), ListItemType.LOG_INVENTORY);
                    }
                    size -= this.inventories.size();
                }
            }
            String str2 = this.shopItemLabel;
            if (str2 != null) {
                if (size == 0) {
                    return new ItemListItem(str2);
                }
                size--;
            }
            List<ShopItem> list2 = this.shopItems;
            if (list2 != null) {
                if (size < list2.size()) {
                    return new ItemListItem(this.shopItems.get(size), ListItemType.SHOP);
                }
                size -= this.shopItems.size();
            }
            String str3 = this.stockLabel;
            if (str3 != null) {
                if (size == 0) {
                    return new ItemListItem(str3);
                }
                size--;
            }
            List<Stock> list3 = this.stocks;
            if (list3 != null) {
                if (size < list3.size()) {
                    return new ItemListItem(this.stocks.get(size), ListItemType.STOCK);
                }
                this.stocks.size();
            }
            throw new IndexOutOfBoundsException("location:" + i);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            int size = this.hasCharacterView ? 1 : 0;
            if (this.inventoryLabel != null) {
                size++;
            }
            List<Inventory> list = this.inventories;
            if (list != null) {
                if (!this.isLogInventory && this.mInventoryEquipmentHandler != null) {
                    Iterator<Inventory> it = list.iterator();
                    while (it.hasNext()) {
                        if (this.mInventoryEquipmentHandler.getEquippedCharInfo(it.next()) == null) {
                            size++;
                        }
                    }
                } else {
                    size += this.inventories.size();
                }
            }
            if (this.shopItemLabel != null) {
                size++;
            }
            List<ShopItem> list2 = this.shopItems;
            if (list2 != null) {
                size += list2.size();
            }
            if (this.stockLabel != null) {
                size++;
            }
            List<Stock> list3 = this.stocks;
            return list3 != null ? size + list3.size() : size;
        }
    }

    public ItemAdapter(Context context, ItemListItemManager itemListItemManager) {
        super(context, 0, itemListItemManager.getItems());
        this.mButtonEnabled = new boolean[3];
        this.mItemListItemManager = itemListItemManager;
        this.mItemListItemManager.setAdapter(this);
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        boolean[] zArr = this.mButtonEnabled;
        zArr[0] = true;
        zArr[1] = true;
        zArr[2] = true;
    }

    public void setButtonEnabled(int i, boolean z) {
        this.mButtonEnabled[i] = z;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int i) {
        switch (getItem(i).type) {
            case CHARACTERS:
                return 0;
            case SEPARATOR:
                return 1;
            default:
                return 2;
        }
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        View viewCreateCharacterview;
        ViewHolder viewHolder;
        String equippableInfo;
        int itemViewType = getItemViewType(i);
        if (view == null) {
            viewHolder = new ViewHolder();
            switch (itemViewType) {
                case 0:
                    viewCreateCharacterview = createCharacterview(i, viewGroup, getContext());
                    viewHolder.vwCharacter = (LinearLayout) viewCreateCharacterview;
                    break;
                case 1:
                    viewCreateCharacterview = this.mInflater.inflate(C0380R.layout.list_item_separator, viewGroup, false);
                    viewHolder.tvSeparator = (TextView) viewCreateCharacterview.findViewById(C0380R.id.tvSeparator);
                    break;
                default:
                    View viewInflate = this.mInflater.inflate(C0380R.layout.list_item_item, viewGroup, false);
                    viewHolder.ivIcon = (ImageView) viewInflate.findViewById(C0380R.id.ivIcon);
                    viewHolder.tvEquipment = (TextView) viewInflate.findViewById(C0380R.id.tvEquipment);
                    viewHolder.tvName = (TextView) viewInflate.findViewById(C0380R.id.tvName);
                    viewHolder.tvDescription = (TextView) viewInflate.findViewById(C0380R.id.tvDescription);
                    viewHolder.tvCount = (TextView) viewInflate.findViewById(C0380R.id.tvCount);
                    viewHolder.btnButton1 = (Button) viewInflate.findViewById(C0380R.id.btnButton1);
                    viewHolder.btnButton2 = (Button) viewInflate.findViewById(C0380R.id.btnButton2);
                    viewHolder.btnButton3 = (Button) viewInflate.findViewById(C0380R.id.btnButton3);
                    viewHolder.btnButton1.setFocusable(false);
                    viewHolder.btnButton2.setFocusable(false);
                    viewHolder.btnButton3.setFocusable(false);
                    viewHolder.btnButton1.setFocusableInTouchMode(false);
                    viewHolder.btnButton2.setFocusableInTouchMode(false);
                    viewHolder.btnButton3.setFocusableInTouchMode(false);
                    viewHolder.btnButton1.setEnabled(this.mButtonEnabled[0]);
                    viewHolder.btnButton2.setEnabled(this.mButtonEnabled[1]);
                    viewHolder.btnButton3.setEnabled(this.mButtonEnabled[2]);
                    viewCreateCharacterview = viewInflate;
                    break;
            }
            viewCreateCharacterview.setTag(viewHolder);
        } else {
            ViewHolder viewHolder2 = (ViewHolder) view.getTag();
            if (itemViewType == 2) {
                viewHolder2.btnButton1.setEnabled(this.mButtonEnabled[0]);
                viewHolder2.btnButton2.setEnabled(this.mButtonEnabled[1]);
                viewHolder2.btnButton3.setEnabled(this.mButtonEnabled[2]);
            }
            viewCreateCharacterview = view;
            viewHolder = viewHolder2;
        }
        if (itemViewType == 0) {
            if (this.mItemListItemManager.mCharacterViewHandler != null) {
                this.mItemListItemManager.mCharacterViewHandler.refresh(viewHolder.vwCharacter);
            }
        } else if (itemViewType == 1) {
            viewHolder.tvSeparator.setText(getItem(i).label);
        } else {
            ItemListItem item = getItem(i);
            int i2 = C03774.f115xed53cd53[item.type.ordinal()];
            if (i2 != 1) {
                switch (i2) {
                    case 3:
                        viewHolder.tvCount.setVisibility(8);
                        viewHolder.tvEquipment.setVisibility(8);
                        viewHolder.btnButton1.setText(C0380R.string.lbl_btn_equip);
                        viewHolder.btnButton2.setText(C0380R.string.lbl_btn_stock);
                        viewHolder.btnButton3.setText(C0380R.string.lbl_btn_sell);
                        viewHolder.btnButton1.setVisibility(0);
                        viewHolder.btnButton2.setVisibility(0);
                        viewHolder.btnButton3.setVisibility(0);
                        break;
                    case 4:
                        viewHolder.tvCount.setVisibility(8);
                        viewHolder.tvEquipment.setVisibility(8);
                        viewHolder.btnButton1.setText(C0380R.string.lbl_btn_buy);
                        viewHolder.btnButton1.setVisibility(0);
                        viewHolder.btnButton2.setVisibility(8);
                        viewHolder.btnButton3.setVisibility(8);
                        break;
                    case 5:
                        viewHolder.tvCount.setVisibility(0);
                        viewHolder.tvEquipment.setVisibility(8);
                        viewHolder.btnButton1.setText(C0380R.string.lbl_btn_inventory);
                        viewHolder.btnButton2.setText(C0380R.string.lbl_btn_sell);
                        viewHolder.btnButton1.setVisibility(0);
                        viewHolder.btnButton2.setVisibility(0);
                        viewHolder.btnButton3.setVisibility(8);
                        break;
                    case 6:
                        viewHolder.tvCount.setVisibility(8);
                        viewHolder.tvEquipment.setVisibility(0);
                        viewHolder.tvDescription.setVisibility(8);
                        viewHolder.btnButton1.setVisibility(8);
                        viewHolder.btnButton2.setVisibility(8);
                        viewHolder.btnButton3.setVisibility(8);
                        break;
                }
            }
            View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.shirobakama.autorpg2.view.ItemAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    ((ListView) viewGroup).performItemClick(view2, i, 0L);
                }
            };
            viewHolder.btnButton1.setOnClickListener(onClickListener);
            viewHolder.btnButton2.setOnClickListener(onClickListener);
            viewHolder.btnButton3.setOnClickListener(onClickListener);
            Item baseItem = item.itemObject.getBaseItem(getContext());
            viewHolder.ivIcon.setImageDrawable(getContext().getResources().getDrawable(baseItem.drawableId));
            viewHolder.tvName.setText(item.itemObject.getName(getContext()));
            StringBuilder sb = null;
            viewHolder.tvName.setTextColor(item.itemObject.getRarity(null).getColor(getContext()));
            int i3 = C03774.f115xed53cd53[item.type.ordinal()];
            if (i3 != 1) {
                switch (i3) {
                    case 3:
                        viewHolder.btnButton1.setEnabled(this.mButtonEnabled[0] && baseItem.equipable);
                        sb = new StringBuilder();
                        sb.append(item.itemObject.getDescription(getContext(), false));
                        break;
                    case 4:
                        int buyPrice = item.itemObject.getBuyPrice(getContext());
                        sb = new StringBuilder();
                        sb.append(item.itemObject.getDescription(getContext(), true));
                        sb.append(buyPrice);
                        sb.append(getContext().getString(C0380R.string.res_sentence_separator));
                        sb.append("GP");
                        sb.append(getContext().getString(C0380R.string.lbl_item_desc_period));
                        break;
                    case 5:
                        viewHolder.tvCount.setText(getContext().getString(C0380R.string.lbl_item_count, Integer.valueOf(((Stock) item.itemObject).countNum)));
                        sb = new StringBuilder();
                        sb.append(item.itemObject.getDescription(getContext(), false));
                        break;
                    case 6:
                        String equippedCharInfo = this.mItemListItemManager.mInventoryEquipmentHandler.getEquippedCharInfo((Inventory) item.itemObject);
                        if (equippedCharInfo == null) {
                            viewHolder.tvEquipment.setText(BuildConfig.FLAVOR);
                            break;
                        } else {
                            viewHolder.tvEquipment.setText(equippedCharInfo);
                            break;
                        }
                }
            }
            if (sb != null) {
                if (this.mItemListItemManager.mItemEquippableHandler != null && (equippableInfo = this.mItemListItemManager.mItemEquippableHandler.getEquippableInfo(item.itemObject)) != null) {
                    sb.append(equippableInfo);
                }
                viewHolder.tvDescription.setText(sb.toString());
            }
        }
        return viewCreateCharacterview;
    }

    private View createCharacterview(final int i, final ViewGroup viewGroup, Context context) {
        ViewGroup.LayoutParams layoutParams = new AbsListView.LayoutParams(-1, -2);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(1);
        ViewGroup.LayoutParams layoutParams2 = new ViewGroup.LayoutParams(-1, -2);
        for (int i2 = 0; i2 < 3; i2++) {
            ViewGroup viewGroup2 = (ViewGroup) this.mInflater.inflate(C0380R.layout.inn_character, (ViewGroup) linearLayout, false);
            viewGroup2.setLayoutParams(layoutParams2);
            CharViewHolder charViewHolder = new CharViewHolder(viewGroup2, i2);
            charViewHolder.chkSelected.setVisibility(8);
            viewGroup2.setOnClickListener(new View.OnClickListener() { // from class: com.shirobakama.autorpg2.view.ItemAdapter.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ((ListView) viewGroup).performItemClick(view, i, 0L);
                }
            });
            viewGroup2.setTag(charViewHolder);
            View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.shirobakama.autorpg2.view.ItemAdapter.3
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ((ListView) viewGroup).performItemClick(view, i, 0L);
                }
            };
            charViewHolder.tvWeapon.setTag(charViewHolder);
            charViewHolder.tvArmor.setTag(charViewHolder);
            charViewHolder.tvShield.setTag(charViewHolder);
            charViewHolder.tvRing.setTag(charViewHolder);
            charViewHolder.tvWeapon.setOnClickListener(onClickListener);
            charViewHolder.tvArmor.setOnClickListener(onClickListener);
            charViewHolder.tvShield.setOnClickListener(onClickListener);
            charViewHolder.tvRing.setOnClickListener(onClickListener);
            linearLayout.addView(viewGroup2);
        }
        return linearLayout;
    }
}
