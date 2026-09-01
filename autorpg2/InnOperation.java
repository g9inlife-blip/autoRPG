package com.shirobakama.autorpg2;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.shirobakama.autorpg2.AlertDialogFragment;
import com.shirobakama.autorpg2.SkillSlotDialogFragment;
import com.shirobakama.autorpg2.TownActivity;
import com.shirobakama.autorpg2.adventure.TownFlagEngine;
import com.shirobakama.autorpg2.adventure.TownInnFlagEngine;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.Inventory;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.p001db.Persister;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.autorpg2.view.CharViewHolder;
import com.shirobakama.autorpg2.view.ItemAdapter;
import com.shirobakama.autorpg2.view.LabelValueItem;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class InnOperation implements View.OnClickListener, TownActivity.TownOperation {
    private static final int MENU_CHAR_MOVE_DOWNER = 102;
    private static final int MENU_CHAR_MOVE_UPPER = 101;
    private static final int MENU_CHAR_SKILL = 105;
    private static final int MENU_CHAR_SKILL_ADD = 106;
    private static final int MENU_CHAR_SKILL_SLOT = 107;
    private static final int MENU_CHAR_STATUS = 103;
    private static final int MENU_CHAR_STATUS_UP = 104;
    private static final int MENU_CHAR_TACTICS = 108;
    private static final int MENU_INVENTORY_DETAIL = 203;
    private static final int MENU_INVENTORY_EQUIP = 201;
    private static final int MENU_INVENTORY_UNEQUIP = 202;
    protected static final String TAG = "inn-operation";
    private static boolean mFirst = true;
    protected TownActivity mActivity;
    private Button mBtnInnHearing;
    private Button mBtnInnQuest;
    private Button mBtnMakeChar;
    private TownInnFlagEngine mFlagEngine = new TownInnFlagEngine();
    private ItemAdapter.ItemListItemManager mListItemManager;
    private ListView mLvInventories;

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public ArrayList<LabelValueItem> getMenuItems() {
        return null;
    }

    public InnOperation(TownActivity townActivity) {
        this.mActivity = townActivity;
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public void onCreate(View view) {
        this.mLvInventories = (ListView) view.findViewById(C0380R.id.lvInventories);
        this.mLvInventories.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.shirobakama.autorpg2.InnOperation.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view2, int i, long j) {
                if (i >= adapterView.getCount()) {
                    Toast.makeText(InnOperation.this.mActivity, "internal error. position exceeds: " + i, 1).show();
                    return;
                }
                InnOperation.this.clickItem((ItemAdapter.ItemListItem) adapterView.getItemAtPosition(i), view2, i);
            }
        });
        this.mBtnMakeChar = (Button) view.findViewById(C0380R.id.btnMakeCharacter);
        this.mBtnMakeChar.setOnClickListener(this);
        view.findViewById(C0380R.id.btnInnOther).setOnClickListener(this);
        view.findViewById(C0380R.id.btnInnTavern).setOnClickListener(this);
        this.mBtnInnHearing = (Button) view.findViewById(C0380R.id.btnInnHearing);
        this.mBtnInnHearing.setOnClickListener(this);
        this.mBtnInnQuest = (Button) view.findViewById(C0380R.id.btnInnQuest);
        this.mBtnInnQuest.setOnClickListener(this);
        this.mBtnInnQuest.setVisibility(8);
        this.mBtnMakeChar.setVisibility(8);
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public void activate(boolean z) {
        ItemAdapter itemAdapter;
        boolean z2;
        boolean z3;
        handleMakeCharButton();
        if (z && !this.mActivity.game.characters.isEmpty()) {
            if (mFirst) {
                Iterator<PlayerChar> it = this.mActivity.game.characters.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (!it.next().getSkillIds().isEmpty()) {
                            z3 = false;
                            break;
                        }
                    } else {
                        z3 = true;
                        break;
                    }
                }
                strTrim = z3 ? this.mActivity.getString(C0380R.string.msg_tutorial_no_skill) : null;
                mFirst = false;
            }
            if (strTrim == null && !this.mActivity.game.isAdventuring(this.mActivity)) {
                StringBuilder sb = new StringBuilder();
                for (PlayerChar playerChar : this.mActivity.game.characters) {
                    boolean z4 = playerChar.statusBonus > 0;
                    boolean z5 = playerChar.getLernableSkillCount(this.mActivity) > 0;
                    if (z4 && z5) {
                        sb.append(this.mActivity.getString(C0380R.string.msg_info_status_and_skill_uppable, new Object[]{playerChar.name}));
                        sb.append(this.mActivity.getString(C0380R.string.res_sentence_separator));
                    } else if (z4) {
                        sb.append(this.mActivity.getString(C0380R.string.msg_info_status_uppable, new Object[]{playerChar.name}));
                        sb.append(this.mActivity.getString(C0380R.string.res_sentence_separator));
                    } else if (z5) {
                        sb.append(this.mActivity.getString(C0380R.string.msg_info_skill_uppable, new Object[]{playerChar.name}));
                        sb.append(this.mActivity.getString(C0380R.string.res_sentence_separator));
                    }
                }
                if (sb.length() > 0) {
                    strTrim = sb.toString().trim();
                }
            }
            if (strTrim != null) {
                this.mActivity.addMessage(strTrim);
            }
        }
        setBtnInnQuestVisibilityByEngine();
        this.mBtnInnHearing.setVisibility((this.mActivity.game.characters.isEmpty() || this.mActivity.game.isAdventuring(this.mActivity)) ? 8 : 0);
        if (this.mListItemManager == null) {
            this.mListItemManager = new ItemAdapter.ItemListItemManager(this.mActivity.inventoryEquipmentHandler, this.mActivity.itemEquippableHandler);
            this.mListItemManager.setCharacterViewHandler(new ItemAdapter.CharacterViewHandler() { // from class: com.shirobakama.autorpg2.InnOperation.2
                @Override // com.shirobakama.autorpg2.view.ItemAdapter.CharacterViewHandler
                public void refresh(View view) {
                    LinearLayout linearLayout = (LinearLayout) view;
                    int i = 0;
                    for (PlayerChar playerChar2 : InnOperation.this.mActivity.game.characters) {
                        CharViewHolder charViewHolder = (CharViewHolder) linearLayout.getChildAt(i).getTag();
                        charViewHolder.show(InnOperation.this.mActivity, InnOperation.this.mActivity.game, playerChar2);
                        charViewHolder.vwContainer.setVisibility(0);
                        i++;
                    }
                    while (i < 3) {
                        ((CharViewHolder) linearLayout.getChildAt(i).getTag()).vwContainer.setVisibility(8);
                        i++;
                    }
                }
            });
            this.mListItemManager.setInventoryLabel(this.mActivity.createInventoryLabel());
            this.mListItemManager.setInventories(this.mActivity.game.inventories);
            itemAdapter = new ItemAdapter(this.mActivity, this.mListItemManager);
            this.mLvInventories.setAdapter((ListAdapter) itemAdapter);
            z2 = false;
        } else {
            itemAdapter = (ItemAdapter) this.mLvInventories.getAdapter();
            this.mListItemManager.setInventoryLabel(this.mActivity.createInventoryLabel());
            this.mListItemManager.setInventories(this.mActivity.game.inventories);
            z2 = true;
        }
        boolean z6 = !this.mActivity.game.isAdventuring(this.mActivity);
        itemAdapter.setButtonEnabled(0, z6);
        itemAdapter.setButtonEnabled(1, z6);
        itemAdapter.setButtonEnabled(2, z6);
        if (z2) {
            itemAdapter.notifyDataSetChanged();
        }
    }

    private void setBtnInnQuestVisibilityByEngine() {
        TownInnFlagEngine townInnFlagEngine = this.mFlagEngine;
        TownActivity townActivity = this.mActivity;
        this.mBtnInnQuest.setVisibility(townInnFlagEngine.peekInnHearing(townActivity, townActivity.game).questCandidateSymbol != null ? 0 : 8);
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public TownFlagEngine.Result acceptQuest(String str) {
        TownInnFlagEngine townInnFlagEngine = this.mFlagEngine;
        TownActivity townActivity = this.mActivity;
        TownFlagEngine.Result resultProcessAcceptQuest = townInnFlagEngine.processAcceptQuest(townActivity, townActivity.game, str);
        setBtnInnQuestVisibilityByEngine();
        return resultProcessAcceptQuest;
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public TownFlagEngine.Result refuseQuest(String str) {
        TownInnFlagEngine townInnFlagEngine = this.mFlagEngine;
        TownActivity townActivity = this.mActivity;
        TownFlagEngine.Result resultProcessRefuseQuest = townInnFlagEngine.processRefuseQuest(townActivity, townActivity.game, str);
        setBtnInnQuestVisibilityByEngine();
        return resultProcessRefuseQuest;
    }

    protected void clickItem(ItemAdapter.ItemListItem itemListItem, View view, int i) {
        switch (itemListItem.type) {
            case CHARACTERS:
                int i2 = ((CharViewHolder) view.getTag()).index;
                if (view instanceof TextView) {
                    Inventory.InventoryType inventoryType = null;
                    int id = view.getId();
                    if (id == 2131165240) {
                        inventoryType = Inventory.InventoryType.ARMOR;
                    } else if (id == 2131165276) {
                        inventoryType = Inventory.InventoryType.RING;
                    } else if (id == 2131165280) {
                        inventoryType = Inventory.InventoryType.SHIELD;
                    } else if (id == 2131165297) {
                        inventoryType = Inventory.InventoryType.WEAPON;
                    }
                    if (inventoryType != null) {
                        showCharacterEquipmentMenu(i2, inventoryType);
                        break;
                    }
                } else {
                    selectCharacter(i2);
                    break;
                }
                break;
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
        }
    }

    private void showCharacterEquipmentMenu(int i, Inventory.InventoryType inventoryType) {
        int i2;
        PlayerChar playerChar = this.mActivity.game.characters.get(i);
        switch (inventoryType) {
            case ARMOR:
                i2 = playerChar.armorId;
                break;
            case RING:
                i2 = playerChar.ringId;
                break;
            case SHIELD:
                i2 = playerChar.shieldId;
                break;
            case WEAPON:
                i2 = playerChar.weaponId;
                break;
            default:
                i2 = 0;
                break;
        }
        if (i2 == 0) {
            return;
        }
        ArrayList<LabelValueItem> arrayList = new ArrayList<>();
        if (!this.mActivity.game.isAdventuring(this.mActivity)) {
            arrayList.add(new LabelValueItem(MENU_INVENTORY_EQUIP, this.mActivity.getString(C0380R.string.lbl_inn_inventory_menu_equip)));
            arrayList.add(new LabelValueItem(MENU_INVENTORY_UNEQUIP, this.mActivity.getString(C0380R.string.msg_unequip)));
        }
        arrayList.add(new LabelValueItem(MENU_INVENTORY_DETAIL, this.mActivity.getString(C0380R.string.lbl_inn_inventory_menu_detail)));
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setChoiceMode(0);
        decorator.setCancelable(true);
        decorator.setLabelValueItems(arrayList);
        decorator.args().putInt("char_index", i);
        decorator.args().putInt("inventory_id", i2);
        decorator.decorate(new OptionMenuDialogFragment()).show(this.mActivity.getSupportFragmentManager());
    }

    protected void selectCharacter(int i) {
        ArrayList<LabelValueItem> arrayList = new ArrayList<>();
        arrayList.add(new LabelValueItem(MENU_CHAR_STATUS, this.mActivity.getString(C0380R.string.lbl_inn_char_menu_status)));
        if (!this.mActivity.game.isAdventuring(this.mActivity)) {
            PlayerChar playerChar = this.mActivity.game.characters.get(i);
            if (playerChar.statusBonus > 0) {
                arrayList.add(new LabelValueItem(MENU_CHAR_STATUS_UP, this.mActivity.getString(C0380R.string.lbl_inn_char_menu_status_up)));
            }
            arrayList.add(new LabelValueItem(MENU_CHAR_SKILL, this.mActivity.getString(C0380R.string.lbl_inn_char_menu_skill)));
            if (playerChar.getLernableSkillCount(this.mActivity) > 0) {
                arrayList.add(new LabelValueItem(MENU_CHAR_SKILL_ADD, this.mActivity.getString(C0380R.string.lbl_inn_char_menu_skill_learn)));
            }
        } else {
            arrayList.add(new LabelValueItem(MENU_CHAR_SKILL, this.mActivity.getString(C0380R.string.lbl_inn_char_menu_skill)));
        }
        arrayList.add(new LabelValueItem(MENU_CHAR_SKILL_SLOT, this.mActivity.getString(C0380R.string.lbl_inn_char_menu_skill_slot)));
        arrayList.add(new LabelValueItem(MENU_CHAR_TACTICS, this.mActivity.getString(C0380R.string.lbl_inn_char_menu_tactics)));
        if (!this.mActivity.game.isAdventuring(this.mActivity)) {
            if (i > 0) {
                arrayList.add(new LabelValueItem(MENU_CHAR_MOVE_UPPER, this.mActivity.getString(C0380R.string.lbl_inn_char_menu_move_upper)));
            }
            if (i < this.mActivity.game.characters.size() - 1) {
                arrayList.add(new LabelValueItem(MENU_CHAR_MOVE_DOWNER, this.mActivity.getString(C0380R.string.lbl_inn_char_menu_move_downer)));
            }
        }
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setChoiceMode(0);
        decorator.setCancelable(true);
        decorator.setLabelValueItems(arrayList);
        decorator.args().putInt("char_index", i);
        decorator.decorate(new OptionMenuDialogFragment()).show(this.mActivity.getSupportFragmentManager());
    }

    protected void handleMakeCharButton() {
        if (this.mActivity.game.characters.size() < 3) {
            this.mBtnMakeChar.setVisibility(0);
        } else {
            this.mBtnMakeChar.setVisibility(8);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == 2131165269) {
            moveToTavern();
            return;
        }
        if (view.getId() == 2131165262) {
            doHearing();
            return;
        }
        if (view.getId() == 2131165265) {
            moveToTavern();
            return;
        }
        if (view.getId() == 2131165264) {
            TownActivity townActivity = this.mActivity;
            townActivity.confirmQuest(this.mFlagEngine.nextInnHearing(townActivity, townActivity.game).questCandidateSymbol);
        } else if (view.getId() == 2131165263) {
            this.mActivity.action();
        }
    }

    private void moveToTavern() {
        if (this.mActivity.canMoveToAnotherActivity()) {
            GameContext.game = this.mActivity.game.copy();
            Intent intent = new Intent(this.mActivity.getApplicationContext(), (Class<?>) TavernActivity.class);
            DeviceUtil.setRequestOrientationForIntent(this.mActivity, intent);
            this.mActivity.startActivityForResult(intent, 1);
        }
    }

    private void doHearing() {
        TownInnFlagEngine townInnFlagEngine = this.mFlagEngine;
        TownActivity townActivity = this.mActivity;
        TownFlagEngine.Result resultNextInnHearing = townInnFlagEngine.nextInnHearing(townActivity, townActivity.game);
        if (resultNextInnHearing.message == null) {
            TownInnFlagEngine townInnFlagEngine2 = this.mFlagEngine;
            TownActivity townActivity2 = this.mActivity;
            resultNextInnHearing = townInnFlagEngine2.nextInnHearing(townActivity2, townActivity2.game);
        }
        if (this.mActivity.processEngineResult(resultNextInnHearing)) {
            activate(false);
        } else {
            setBtnInnQuestVisibilityByEngine();
        }
    }

    ItemAdapter.ItemListItemManager getListItemManager() {
        return this.mListItemManager;
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public void selectMenu(Bundle bundle, int i, List<LabelValueItem> list) {
        int i2 = list.get(i).value;
        switch (i2) {
            case MENU_CHAR_MOVE_UPPER /* 101 */:
                moveCharacter(bundle, -1);
                break;
            case MENU_CHAR_MOVE_DOWNER /* 102 */:
                moveCharacter(bundle, 1);
                break;
            case MENU_CHAR_STATUS /* 103 */:
                showCharacterDetail(bundle);
                break;
            case MENU_CHAR_STATUS_UP /* 104 */:
                showAttributeUpDialog(bundle);
                break;
            case MENU_CHAR_SKILL /* 105 */:
                showSkill(bundle);
                break;
            case MENU_CHAR_SKILL_ADD /* 106 */:
                showSkillAdding(bundle);
                break;
            case MENU_CHAR_SKILL_SLOT /* 107 */:
                showSkillSlot(bundle);
                break;
            case MENU_CHAR_TACTICS /* 108 */:
                editTactics(bundle);
                break;
            default:
                switch (i2) {
                    case MENU_INVENTORY_EQUIP /* 201 */:
                        changeInventoryEquipment(bundle);
                        break;
                    case MENU_INVENTORY_UNEQUIP /* 202 */:
                        unequipInventory(bundle);
                        break;
                    case MENU_INVENTORY_DETAIL /* 203 */:
                        showInventoryDetail(bundle);
                        break;
                }
        }
    }

    private void showInventoryDetail(Bundle bundle) {
        Inventory inventory = this.mActivity.game.getInventory(bundle.getInt("inventory_id"));
        if (inventory != null) {
            this.mActivity.showItemDetail(inventory);
        }
    }

    private void changeInventoryEquipment(Bundle bundle) {
        Inventory inventory = this.mActivity.game.getInventory(bundle.getInt("inventory_id"));
        if (inventory != null) {
            this.mActivity.changeEquipmentInventory(inventory);
        }
    }

    private void unequipInventory(Bundle bundle) {
        this.mActivity.changeEquipment(bundle.getInt("inventory_id"), -1);
    }

    private void moveCharacter(Bundle bundle, int i) {
        int i2 = bundle.getInt("char_index");
        int i3 = i + i2;
        if (i3 < 0 || i3 >= this.mActivity.game.characters.size()) {
            return;
        }
        PlayerChar playerChar = this.mActivity.game.characters.get(i2);
        PlayerChar playerChar2 = this.mActivity.game.characters.get(i3);
        this.mActivity.game.characters.set(i3, playerChar);
        this.mActivity.game.characters.set(i2, playerChar2);
        try {
            new Persister(this.mActivity).writeActiveCharacters(this.mActivity.game.characters);
            activate(false);
        } catch (SQLException e) {
            DeviceUtil.handleSqliteException(this.mActivity, e);
        }
    }

    private void showCharacterDetail(Bundle bundle) {
        int i = bundle.getInt("char_index");
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setCancelable(true).setPositiveText(0).setTitle(this.mActivity.game.characters.get(i).name);
        decorator.args().putInt("char_index", i);
        decorator.decorate(new CharacterDetailDialogFragment()).show(this.mActivity.getSupportFragmentManager());
    }

    private void showSkill(Bundle bundle) {
        this.mActivity.showSkill(bundle.getInt("char_index"));
    }

    private void showSkillAdding(Bundle bundle) {
        int i = bundle.getInt("char_index");
        PlayerChar playerChar = this.mActivity.game.characters.get(i);
        if (this.mActivity.game.isAdventuring(this.mActivity) || playerChar.getLernableSkillCount(this.mActivity) <= 0) {
            return;
        }
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(C0380R.string.msg_dlg_skill_up);
        decorator.setCancelable(true).setPositiveText(0).setNegativeText(0);
        decorator.args().putInt("char_index", i);
        decorator.args().putBoolean("selectable", true);
        decorator.args().putString("tag", "learn-skill");
        decorator.args().putIntegerArrayList("skill_ids", playerChar.getLearnableSkills(this.mActivity));
        decorator.decorate(new SkillListDialogFragment()).show(this.mActivity.getSupportFragmentManager());
    }

    private void showSkillSlot(Bundle bundle) {
        int i = bundle.getInt("char_index");
        PlayerChar playerChar = this.mActivity.game.characters.get(i);
        if (playerChar.getSkillIds().isEmpty()) {
            TownActivity townActivity = this.mActivity;
            townActivity.addMessage(townActivity.getString(C0380R.string.msg_cant_open_slot_no_skill));
        } else {
            this.mActivity.showSkillSlot(new SkillSlotDialogFragment.State(i, playerChar.getAvailableSkillIds(), playerChar.getSkillIds(), playerChar.getNumberOfSkillSlots(), !this.mActivity.game.isAdventuring(this.mActivity)));
        }
    }

    private void editTactics(Bundle bundle) {
        if (this.mActivity.canMoveToAnotherActivity()) {
            int i = bundle.getInt("char_index");
            GameContext.game = this.mActivity.game.copy();
            Intent intent = new Intent(this.mActivity.getApplicationContext(), (Class<?>) TacticsMakingActivity.class);
            TacticsMakingActivity.setCharacterId(intent, this.mActivity.game.characters.get(i).f106id);
            DeviceUtil.setRequestOrientationForIntent(this.mActivity, intent);
            this.mActivity.startActivityForResult(intent, 4);
        }
    }

    public void showAttributeUpDialog(Bundle bundle) {
        int i = bundle.getInt("char_index");
        PlayerChar playerChar = this.mActivity.game.characters.get(i);
        ArrayList<LabelValueItem> arrayList = new ArrayList<>();
        for (GameChar.Attribute attribute : GameChar.Attribute.values()) {
            if (playerChar.getBaseAttr(attribute) < 18) {
                int iOrdinal = attribute.ordinal();
                TownActivity townActivity = this.mActivity;
                arrayList.add(new LabelValueItem(iOrdinal, townActivity.getString(C0380R.string.lbl_status_up_description, new Object[]{attribute.getString(townActivity), Integer.valueOf(playerChar.getBaseAttr(attribute))})));
            }
        }
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(C0380R.string.msg_dlg_attr_up);
        decorator.setCancelable(true).setPositiveText(0).setNegativeText(0);
        decorator.setLabelValueItems(arrayList).setChoiceMode(1);
        decorator.args().putInt("char_index", i);
        decorator.decorate(new SelectUpAttributeDialogFrament()).show(this.mActivity.getSupportFragmentManager());
    }

    public static class SelectUpAttributeDialogFrament extends AlertDialogFragment {
        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        protected void onClick(DialogInterface dialogInterface, int i) {
            switch (i) {
                case -1:
                    ((TownActivity) getActivity()).upAttribute(this.whichItem, this.items, getArguments().getInt("char_index"));
                    break;
            }
        }
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (i != 1) {
            return i == 4;
        }
        this.mActivity.game = GameContext.game;
        GameContext.game = null;
        if (!this.mActivity.game.characters.isEmpty()) {
            try {
                new Persister(this.mActivity).writeActiveCharacters(this.mActivity.game.characters);
            } catch (SQLException e) {
                DeviceUtil.handleSqliteException(this.mActivity, e);
            }
            for (int i3 = 0; i3 < this.mActivity.game.characters.size(); i3++) {
                this.mActivity.game.characters.get(i3).index = i3;
            }
        }
        this.mActivity.game.calcCharacterStatus(this.mActivity);
        handleMakeCharButton();
        return true;
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public void onChangeTown() {
        TownInnFlagEngine townInnFlagEngine = this.mFlagEngine;
        TownActivity townActivity = this.mActivity;
        townInnFlagEngine.resetState(townActivity, townActivity.game);
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public void onReturningAdventure() {
        TownInnFlagEngine townInnFlagEngine = this.mFlagEngine;
        TownActivity townActivity = this.mActivity;
        townInnFlagEngine.resetState(townActivity, townActivity.game);
    }
}
