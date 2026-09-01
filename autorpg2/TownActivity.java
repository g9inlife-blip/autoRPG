package com.shirobakama.autorpg2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.SQLException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.shirobakama.autorpg2.AlertDialogFragment;
import com.shirobakama.autorpg2.CharacterDetailDialogFragment;
import com.shirobakama.autorpg2.CharacterSelectDialogFragment;
import com.shirobakama.autorpg2.ConfirmationDialogFragment;
import com.shirobakama.autorpg2.OptionMenuDialogFragment;
import com.shirobakama.autorpg2.SkillListDialogFragment;
import com.shirobakama.autorpg2.SkillSlotDialogFragment;
import com.shirobakama.autorpg2.adventure.QuestConst;
import com.shirobakama.autorpg2.adventure.TownFlagEngine;
import com.shirobakama.autorpg2.entity.Dungeon;
import com.shirobakama.autorpg2.entity.DungeonStat;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.GameFlag;
import com.shirobakama.autorpg2.entity.Inventory;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.autorpg2.entity.ItemObject;
import com.shirobakama.autorpg2.entity.LogManagement;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.entity.Quest;
import com.shirobakama.autorpg2.entity.ShopItem;
import com.shirobakama.autorpg2.entity.Skill;
import com.shirobakama.autorpg2.entity.SkillCustomization;
import com.shirobakama.autorpg2.entity.Stock;
import com.shirobakama.autorpg2.p001db.BackupRestoreUtil;
import com.shirobakama.autorpg2.p001db.InheritanceAnalyzer;
import com.shirobakama.autorpg2.p001db.Persister;
import com.shirobakama.autorpg2.repo.DungeonRepository;
import com.shirobakama.autorpg2.repo.ItemDb;
import com.shirobakama.autorpg2.repo.QuestDb;
import com.shirobakama.autorpg2.repo.QuestRepository;
import com.shirobakama.autorpg2.repo.SkillRepository;
import com.shirobakama.autorpg2.util.AboutDialogFragment;
import com.shirobakama.autorpg2.util.AdManager;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.autorpg2.util.FileSelectDialogFragment;
import com.shirobakama.autorpg2.util.NotificationReceiver;
import com.shirobakama.autorpg2.util.TypeUtil;
import com.shirobakama.autorpg2.view.HelpDialogFragment;
import com.shirobakama.autorpg2.view.ItemAdapter;
import com.shirobakama.autorpg2.view.LabelValueItem;
import com.shirobakama.autorpg2.view.MessageAdapter;
import com.shirobakama.logquest2.BuildConfig;
import com.shirobakama.logquest2.C0380R;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class TownActivity extends FragmentActivity implements View.OnClickListener, ConfirmationDialogFragment.OnConfirmationListener, CharacterSelectDialogFragment.OnCharacterSelectListener, CharacterDetailDialogFragment.CharacterDetailCallback, SkillListDialogFragment.SkillListCallback, SkillSlotDialogFragment.SkilSlotCallback, OptionMenuDialogFragment.Callback {
    private static final int MAX_MESSAGE = 100;
    private static final int MENU_HELP = 10004;
    private static final int MENU_HELP_EDGE_OF_TOWN = 10301;
    private static final int MENU_HELP_INN_AND_CHARACTER = 10302;
    private static final int MENU_HELP_SHOP = 10303;
    private static final int MENU_HELP_STOCK = 10304;
    private static final int MENU_OLDER_LOG = 10001;
    private static final int MENU_OTHER = 10100;
    private static final int MENU_OTHER_ABOUT = 10205;
    private static final int MENU_OTHER_BACKUP = 10201;
    private static final int MENU_OTHER_INHERIT = 10203;
    private static final int MENU_OTHER_RESTORE = 10202;
    private static final int MENU_OTHER_STORY = 10204;
    private static final int MENU_PREFERENCES = 10005;
    private static final int MENU_PRIVACY_POLICY = 10006;
    private static final int MENU_QUEST = 10003;
    private static final int MENU_STORY_EPILOG = 13002;
    private static final int MENU_STORY_INTRODUCTION = 13001;
    private static final int MENU_USE_ITEM_DETAIL = 12200;
    private static final int MENU_USE_ITEM_USE = 12100;
    private static final int MENU_VIEW_LOG_FILE = 10002;
    static final int NUMBER_OF_OPERATIONS = 4;
    static final int OPERATION_EDGE_OF_TOWN = 0;
    static final int OPERATION_INN = 1;
    static final int OPERATION_SHOP = 2;
    static final int OPERATION_STOCK = 3;
    static final int REQUEST_LOG_VIEW = 2;
    static final int REQUEST_PERMISSION = 20;
    static final int REQUEST_PREFS = 10;
    static final int REQUEST_TACTICS_MAKING = 3;
    static final int REQUEST_TACTICS_MAKING_FOR_CHARACTER = 4;
    static final int REQUEST_TAVERN = 1;
    static final String SKILL_SELECT_TAG_ADD_SLOT = "add-slot";
    static final String SKILL_SELECT_TAG_CUSTOMIZE = "customize";
    static final String SKILL_SELECT_TAG_LEARN_SKILL = "learn-skill";
    private static final String STATE_CURRENT_OPERATION = "current.operation";
    private static final String STATE_LOG_MANAGEMENT = "log.management";
    static final int STORY_EPILOG = 2;
    static final int STORY_INTRODUCTION = 1;
    protected static final String TAG = "town-activity";
    ProgressDialog dlgProgress;
    EdgeOfTownOperation edgeOfTown;
    GameContext game;
    InnOperation innOperation;
    LayoutInflater layoutInflater;
    LogManagement logManagement;
    private AdManager mAdManager;
    private int mCurrentOperation;
    private FrameLayout[] mFlayOperations;
    private ListView mLvMessages;
    private List<String> mMessages;
    private boolean mMovingToAnotherActivity;
    private TownOperation[] mOperations;
    private ViewPager mVpMain;
    private View mVwEdgeOfTown;
    private View mVwInn;
    private View mVwShop;
    private View mVwStock;
    ShopOperation shopOperation;
    StockOperation stockOperation;
    ProgressProcessor progressProcessor = null;
    ItemAdapter.InventoryEquipmentHandler inventoryEquipmentHandler = new ItemAdapter.InventoryEquipmentHandler() { // from class: com.shirobakama.autorpg2.TownActivity.1
        @Override // com.shirobakama.autorpg2.view.ItemAdapter.InventoryEquipmentHandler
        public String getEquippedCharInfo(Inventory inventory) {
            PlayerChar equippedChar = TownActivity.this.game.getEquippedChar(inventory);
            if (equippedChar == null) {
                return null;
            }
            return equippedChar.name;
        }
    };
    ItemAdapter.ItemEquippableHandler itemEquippableHandler = new ItemAdapter.ItemEquippableHandler() { // from class: com.shirobakama.autorpg2.TownActivity.2
        @Override // com.shirobakama.autorpg2.view.ItemAdapter.ItemEquippableHandler
        public String getEquippableInfo(ItemObject itemObject) {
            StringBuilder sb = new StringBuilder();
            for (PlayerChar playerChar : TownActivity.this.game.characters) {
                TownActivity townActivity = TownActivity.this;
                if (itemObject.canEquip(townActivity, playerChar, townActivity.game.inventories)) {
                    sb.append(playerChar.getEquippedCharInfo(TownActivity.this));
                    sb.append(TownActivity.this.getString(C0380R.string.res_sentence_separator));
                }
            }
            if (sb.length() == 0) {
                return null;
            }
            sb.insert(0, TownActivity.this.getString(C0380R.string.lbl_item_desc_equippable_label));
            return sb.toString().trim();
        }
    };

    private enum CharSelect {
        EQUIP_INVENTORY
    }

    enum Confirmation {
        BUY_ITEM,
        SELL_ITEM,
        BACKUP,
        RESTORE,
        INHERIT,
        SYNTHESIZE,
        PERMISSION
    }

    public interface TownOperation {
        TownFlagEngine.Result acceptQuest(String str);

        void activate(boolean z);

        ArrayList<LabelValueItem> getMenuItems();

        boolean onActivityResult(int i, int i2, Intent intent);

        void onChangeTown();

        void onCreate(View view);

        void onReturningAdventure();

        TownFlagEngine.Result refuseQuest(String str);

        void selectMenu(Bundle bundle, int i, List<LabelValueItem> list);
    }

    @Override // com.shirobakama.autorpg2.ConfirmationDialogFragment.OnConfirmationListener
    public void onConfirmationCancel(int i, int i2, int i3, Bundle bundle) {
    }

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) throws Resources.NotFoundException {
        super.onCreate(bundle);
        DeviceUtil.setLocaleIfNeeded(this, null);
        setContentView(C0380R.layout.town);
        DeviceUtil.handleOrientation(this, null);
        this.mAdManager = new AdManager(this);
        this.mAdManager.handleAdOnCreate(C0380R.id.llayAdBanner);
        this.layoutInflater = (LayoutInflater) getSystemService("layout_inflater");
        this.edgeOfTown = new EdgeOfTownOperation(this);
        this.innOperation = new InnOperation(this);
        this.shopOperation = new ShopOperation(this);
        this.stockOperation = new StockOperation(this);
        this.mOperations = new TownOperation[]{this.edgeOfTown, this.innOperation, this.shopOperation, this.stockOperation};
        Button button = (Button) findViewById(C0380R.id.btnEdgeOfTown);
        Button button2 = (Button) findViewById(C0380R.id.btnInn);
        Button button3 = (Button) findViewById(C0380R.id.btnShop);
        Button button4 = (Button) findViewById(C0380R.id.btnStock);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        if (!getResources().getBoolean(C0380R.bool.town_tab_icon_left)) {
            Button[] buttonArr = {button, button2, button3, button4};
            for (Button button5 : buttonArr) {
                button5.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, button5.getCompoundDrawables()[0], (Drawable) null, (Drawable) null);
            }
        }
        this.mFlayOperations = new FrameLayout[]{(FrameLayout) findViewById(C0380R.id.flayBtnEdgeOfTown), (FrameLayout) findViewById(C0380R.id.flayBtnInn), (FrameLayout) findViewById(C0380R.id.flayBtnShop), (FrameLayout) findViewById(C0380R.id.flayBtnStock)};
        this.mLvMessages = (ListView) findViewById(C0380R.id.lvMessages);
        this.mMessages = new ArrayList();
        this.mLvMessages.setAdapter((ListAdapter) new MessageAdapter(this, this.mMessages));
        this.mLvMessages.setItemsCanFocus(false);
        this.mLvMessages.setFocusable(false);
        View viewInflate = this.layoutInflater.inflate(C0380R.layout.list_item_message, (ViewGroup) this.mLvMessages, false);
        viewInflate.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, viewInflate.getMeasuredHeight() + (this.mLvMessages.getDividerHeight() * 2) + getResources().getDimensionPixelSize(C0380R.dimen.message_height_total_padding));
        int dimensionPixelSize = getResources().getDimensionPixelSize(C0380R.dimen.message_horizontal_padding);
        layoutParams.setMargins(dimensionPixelSize, 0, dimensionPixelSize, 0);
        this.mLvMessages.setLayoutParams(layoutParams);
        this.mVpMain = (ViewPager) findViewById(C0380R.id.vpMain);
        this.mVwEdgeOfTown = this.layoutInflater.inflate(C0380R.layout.edge_of_town, (ViewGroup) this.mVpMain, false);
        this.mVwInn = this.layoutInflater.inflate(C0380R.layout.inn, (ViewGroup) this.mVpMain, false);
        this.mVwShop = this.layoutInflater.inflate(C0380R.layout.shop, (ViewGroup) this.mVpMain, false);
        this.mVwStock = this.layoutInflater.inflate(C0380R.layout.stock, (ViewGroup) this.mVpMain, false);
        View[] viewArr = {this.mVwEdgeOfTown, this.mVwInn, this.mVwShop, this.mVwStock};
        this.mVpMain.setAdapter(new TownPagerAdapter(this, viewArr));
        this.mVpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: com.shirobakama.autorpg2.TownActivity.3
            @Override // android.support.v4.view.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int i) {
            }

            @Override // android.support.v4.view.ViewPager.OnPageChangeListener
            public void onPageScrolled(int i, float f, int i2) {
            }

            @Override // android.support.v4.view.ViewPager.OnPageChangeListener
            public void onPageSelected(int i) throws Resources.NotFoundException {
                TownActivity.this.onPageSelected(i);
            }
        });
        if (bundle != null) {
            this.game = (GameContext) bundle.getParcelable("game");
            this.mCurrentOperation = bundle.getInt(STATE_CURRENT_OPERATION);
            this.logManagement = (LogManagement) bundle.getParcelable(STATE_LOG_MANAGEMENT);
        } else {
            this.game = GameContext.game;
            GameContext.game = null;
            if (this.game == null) {
                this.game = AutoRpgMainActivity.readOrInitializeGameContext(this, new Persister(this));
            }
            this.mCurrentOperation = 0;
            readLogManagement();
        }
        this.game.calcCharacterStatus(this);
        for (int i = 0; i < this.game.characters.size(); i++) {
            this.game.characters.get(i).index = i;
        }
        for (int i2 = 0; i2 < 4; i2++) {
            this.mOperations[i2].onCreate(viewArr[i2]);
        }
    }

    private void readLogManagement() {
        this.logManagement = new Persister(this).readLatestLogManagement();
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        AdManager adManager = this.mAdManager;
        if (adManager != null) {
            adManager.handleAdOnDestory();
        }
        super.onDestroy();
        ProgressProcessor progressProcessor = this.progressProcessor;
        if (progressProcessor != null) {
            progressProcessor.cancel();
            if (this.progressProcessor.getMessageWhenCancelled() != 0) {
                Toast.makeText(this, this.progressProcessor.getMessageWhenCancelled(), 1).show();
            }
            this.progressProcessor = null;
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        AdManager adManager = this.mAdManager;
        if (adManager != null) {
            adManager.handleAdOnPause();
        }
        super.onPause();
        synchronized (this) {
            if (this.dlgProgress != null && this.dlgProgress.isShowing()) {
                this.dlgProgress.dismiss();
            }
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onResume() throws Resources.NotFoundException {
        super.onResume();
        this.mMovingToAnotherActivity = false;
        AdManager adManager = this.mAdManager;
        if (adManager != null) {
            adManager.handleAdOnResume();
        }
        synchronized (this) {
            if (this.dlgProgress != null) {
                this.dlgProgress.show();
            }
        }
        if (this.progressProcessor == null) {
            onPageSelected(this.mCurrentOperation);
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("game", this.game);
        bundle.putInt(STATE_CURRENT_OPERATION, this.mCurrentOperation);
        bundle.putParcelable(STATE_LOG_MANAGEMENT, this.logManagement);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (i == 82) {
            action();
            return true;
        }
        return super.onKeyUp(i, keyEvent);
    }

    boolean canMoveToAnotherActivity() {
        if (this.mMovingToAnotherActivity) {
            return false;
        }
        this.mMovingToAnotherActivity = true;
        return true;
    }

    String makeItemDialogTitle(ItemObject itemObject, int i) {
        return getString(C0380R.string.msg_dlg_title_sell_buy_item, new Object[]{itemObject.getName(this), Integer.valueOf(i)});
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == 2131165256) {
            changeCurrentOperation(0);
            return;
        }
        if (view.getId() == 2131165261) {
            changeCurrentOperation(1);
        } else if (view.getId() == 2131165281) {
            changeCurrentOperation(2);
        } else if (view.getId() == 2131165286) {
            changeCurrentOperation(3);
        }
    }

    void action() {
        ArrayList<LabelValueItem> arrayList = new ArrayList<>();
        ArrayList<LabelValueItem> menuItems = this.mOperations[this.mCurrentOperation].getMenuItems();
        if (menuItems != null) {
            arrayList.addAll(menuItems);
        }
        arrayList.add(new LabelValueItem(MENU_OLDER_LOG, getString(C0380R.string.lbl_town_menu_older_log)));
        arrayList.add(new LabelValueItem(MENU_VIEW_LOG_FILE, getString(C0380R.string.lbl_town_menu_view_log_file)));
        arrayList.add(new LabelValueItem(MENU_QUEST, getString(C0380R.string.lbl_town_menu_quest)));
        arrayList.add(new LabelValueItem(MENU_HELP, getString(C0380R.string.lbl_town_menu_help)));
        arrayList.add(new LabelValueItem(MENU_PREFERENCES, getString(C0380R.string.lbl_town_menu_preferences)));
        arrayList.add(new LabelValueItem(MENU_PRIVACY_POLICY, getString(C0380R.string.lbl_town_menu_privacy_policy)));
        arrayList.add(new LabelValueItem(10100, getString(C0380R.string.lbl_town_menu_other)));
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setChoiceMode(0);
        decorator.setCancelable(true);
        decorator.setLabelValueItems(arrayList);
        decorator.decorate(new OptionMenuDialogFragment()).show(getSupportFragmentManager());
    }

    void onPageSelected(int i) throws Resources.NotFoundException {
        int color;
        this.mCurrentOperation = i;
        this.mOperations[this.mCurrentOperation].activate(true);
        for (int i2 = 0; i2 < this.mFlayOperations.length; i2++) {
            if (i2 == this.mCurrentOperation) {
                color = getResources().getColor(C0380R.color.tab_btn_bg_selected);
            } else {
                color = getResources().getColor(C0380R.color.tab_btn_bg_translucent);
            }
            this.mFlayOperations[i2].setBackgroundColor(color);
        }
        this.game.calcCharacterStatus(this);
        Iterator<PlayerChar> it = this.game.characters.iterator();
        while (it.hasNext()) {
            it.next().restoreHpMp();
        }
    }

    private void changeCurrentOperation(int i) {
        this.mVpMain.setCurrentItem(i);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        addMessage(BuildConfig.FLAVOR);
        if (!this.innOperation.onActivityResult(i, i2, intent) && i == 10) {
            checkPreferenceChanged();
        }
    }

    private void checkPreferenceChanged() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (defaultSharedPreferences.getBoolean(getString(C0380R.string.pref_key_notification), false)) {
            if (this.game.isAdventuring(this)) {
                NotificationReceiver.notifyWhenReturning(this, DungeonRepository.getDungeon(this, this.game.dungeonId), this.game.targetFloor, this.game.returnTime);
            }
            if (defaultSharedPreferences.getBoolean(getString(C0380R.string.pref_key_notification_remove), false)) {
                NotificationReceiver.removeCurrentNotification(this);
            }
        } else {
            NotificationReceiver.unregisterNotificationAlarm(this);
        }
        PlayerChar.resetEquippdCharInfoType();
    }

    void persistGameChar(PlayerChar playerChar) {
        try {
            new Persister(this).writePlayer(playerChar);
        } catch (SQLException e) {
            DeviceUtil.handleSqliteException(this, e);
        }
    }

    String createInventoryLabel() {
        return getString(C0380R.string.lbl_inventory, new Object[]{Integer.valueOf(this.game.inventories.size()), Integer.valueOf(this.game.getMaxInventoryCount()), Integer.valueOf(this.game.gold)});
    }

    void onChangeTown() {
        this.shopOperation.onChangeTown();
        this.innOperation.onChangeTown();
    }

    void onReturningAdventure() {
        readLogManagement();
        this.game.calcCharacterStatus(this);
        for (int i = 0; i < this.game.characters.size(); i++) {
            this.game.characters.get(i).index = i;
        }
        this.innOperation.onReturningAdventure();
    }

    void selectViewingLog() {
        List<LogManagement> logManagements = new Persister(this).readLogManagements();
        if (logManagements.isEmpty()) {
            addMessage(getString(C0380R.string.msg_logview_no_log));
            return;
        }
        Collections.reverse(logManagements);
        ArrayList<LabelValueItem> arrayListCreateList = LabelValueItem.createList(logManagements, new LabelValueItem.ItemCreator<LogManagement>() { // from class: com.shirobakama.autorpg2.TownActivity.4
            @Override // com.shirobakama.autorpg2.view.LabelValueItem.ItemCreator
            public LabelValueItem create(LogManagement logManagement) {
                return new LabelValueItem(logManagement.f103id, TownActivity.this.getString(C0380R.string.lbl_current_destination, new Object[]{DungeonRepository.getDungeon(TownActivity.this, logManagement.dungeonId).name, Integer.valueOf(logManagement.targetFloor)}));
            }
        });
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setChoiceMode(1);
        decorator.setTitle(C0380R.string.msg_dlg_select_log).setCancelable(true);
        decorator.setPositiveText(0).setNegativeText(0);
        decorator.setLabelValueItems(arrayListCreateList);
        decorator.decorate(new SelectLogDialogFlagment()).show(getSupportFragmentManager());
    }

    public static class SelectLogDialogFlagment extends AlertDialogFragment {
        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        protected void onClick(DialogInterface dialogInterface, int i) {
            if (i != -1) {
                return;
            }
            ((TownActivity) getActivity()).selectLog(this.whichItem, this.items);
        }
    }

    protected void selectLog(int i, List<LabelValueItem> list) {
        if (i < 0 || i >= list.size()) {
            return;
        }
        moveToLogView(list.get(i).value);
    }

    void moveToLogView(int i) {
        if (canMoveToAnotherActivity()) {
            GameContext.game = this.game.copy();
            Intent intent = new Intent(getApplicationContext(), (Class<?>) LogViewActivity.class);
            DeviceUtil.setRequestOrientationForIntent(this, intent);
            LogViewActivity.setLogManagementIdToIntent(intent, i);
            startActivityForResult(intent, 2);
        }
    }

    protected void sellItem(int i, boolean z, boolean z2) {
        ArrayList<ItemObject> arrayList = new ArrayList();
        ItemAdapter.ItemListItemManager currentListItemManager = getCurrentListItemManager();
        if (z) {
            int inventoryIndex = this.game.getInventoryIndex(i);
            if (inventoryIndex < 0) {
                Toast.makeText(this, C0380R.string.msg_internal_error, 0).show();
                return;
            }
            Inventory inventory = this.game.inventories.get(inventoryIndex);
            if (z2) {
                int i2 = 0;
                while (i2 < this.game.inventories.size()) {
                    Inventory inventory2 = this.game.inventories.get(i2);
                    if (inventory2.isSame(inventory) && this.inventoryEquipmentHandler.getEquippedCharInfo(inventory2) == null) {
                        arrayList.add(this.game.inventories.remove(i2));
                    } else {
                        i2++;
                    }
                }
            } else {
                arrayList.add(inventory);
                this.game.inventories.remove(inventoryIndex);
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                unequipInventory((Inventory) ((ItemObject) it.next()));
            }
        } else {
            int stockIndex = this.game.getStockIndex(i);
            if (stockIndex < 0) {
                Toast.makeText(this, C0380R.string.msg_internal_error, 0).show();
                return;
            }
            Stock stock = this.game.stocks.get(stockIndex);
            stock.countNum--;
            if (stock.countNum <= 0) {
                this.game.stocks.remove(stockIndex);
            }
            arrayList.add(stock);
        }
        Iterator it2 = arrayList.iterator();
        int sellPrice = 0;
        while (it2.hasNext()) {
            sellPrice += ((ItemObject) it2.next()).getSellPrice(this);
        }
        if (this.game.gold + sellPrice > 2147483647L) {
            this.game.gold = Integer.MAX_VALUE;
        } else {
            this.game.gold += sellPrice;
        }
        Persister persister = new Persister(this);
        try {
            for (ItemObject itemObject : arrayList) {
                if (z) {
                    persister.writeInventoryRemoving((Inventory) itemObject, this.game);
                } else {
                    persister.writeStockRemoving((Stock) itemObject, this.game);
                }
            }
            currentListItemManager.setInventoryLabel(createInventoryLabel());
            currentListItemManager.notifyAdapterDataSetChanged();
            if (arrayList.size() == 1) {
                addMessage(getString(C0380R.string.msg_item_selled, new Object[]{((ItemObject) arrayList.get(0)).getName(this), Integer.valueOf(this.game.gold)}));
            } else {
                addMessage(getString(C0380R.string.msg_item_selled_all, new Object[]{((ItemObject) arrayList.get(0)).getName(this), Integer.valueOf(arrayList.size()), Integer.valueOf(this.game.gold)}));
            }
        } catch (SQLException e) {
            DeviceUtil.handleSqliteException(this, e);
        }
    }

    void moveToStock(ItemAdapter.ItemListItemManager itemListItemManager, ItemAdapter.ItemListItem itemListItem) {
        Inventory inventory = (Inventory) itemListItem.itemObject;
        if (this.game.getEquippedChar(inventory) != null) {
            unequipInventory(inventory);
        }
        int iIndexOf = this.game.inventories.indexOf(inventory);
        if (iIndexOf < 0) {
            Toast.makeText(this, C0380R.string.msg_internal_error, 0).show();
            return;
        }
        this.game.inventories.remove(iIndexOf);
        Stock stock = null;
        Iterator<Stock> it = this.game.stocks.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Stock next = it.next();
            if (next.isSame(inventory)) {
                stock = next;
                break;
            }
        }
        if (stock == null) {
            stock = new Stock(inventory);
        }
        stock.countNum++;
        if (stock.countNum == 1) {
            this.game.stocks.add(stock);
        }
        try {
            new Persister(this).writeInventoryAndStock(inventory, true, stock);
            if (itemListItemManager.getInventoryLabel() != null) {
                itemListItemManager.setInventoryLabel(createInventoryLabel());
            }
            itemListItemManager.notifyAdapterDataSetChanged();
        } catch (SQLException e) {
            DeviceUtil.handleSqliteException(this, e);
        }
    }

    void moveToInventory(ItemAdapter.ItemListItemManager itemListItemManager, ItemAdapter.ItemListItem itemListItem) {
        if (this.game.inventories.size() >= this.game.getMaxInventoryCount()) {
            addMessage(getString(C0380R.string.msg_no_room_in_inventory));
            return;
        }
        int iIndexOf = this.game.stocks.indexOf(itemListItem.itemObject);
        Stock stock = this.game.stocks.get(iIndexOf);
        stock.countNum--;
        if (stock.countNum == 0) {
            this.game.stocks.remove(iIndexOf);
        }
        Inventory inventory = new Inventory(stock);
        this.game.inventories.add(inventory);
        try {
            new Persister(this).writeInventoryAndStock(inventory, false, stock);
            itemListItemManager.setInventoryLabel(createInventoryLabel());
            itemListItemManager.notifyAdapterDataSetChanged();
        } catch (SQLException e) {
            DeviceUtil.handleSqliteException(this, e);
        }
    }

    void changeEquipmentInventory(ItemAdapter.ItemListItem itemListItem) {
        changeEquipmentInventory((Inventory) itemListItem.itemObject);
    }

    void changeEquipmentInventory(Inventory inventory) {
        ArrayList arrayList = new ArrayList(3);
        for (PlayerChar playerChar : this.game.characters) {
            playerChar.calcStatus((Context) this, this.game);
            if (inventory.canEquip(this, playerChar, this.game.inventories)) {
                arrayList.add(playerChar);
            }
        }
        if (arrayList.isEmpty()) {
            addMessage(getString(C0380R.string.msg_cannot_equip_all_char));
        } else {
            CharacterSelectDialogFragment.show(this, arrayList, CharSelect.EQUIP_INVENTORY.ordinal(), inventory.f98id, C0380R.string.msg_unequip);
        }
    }

    protected void unequipInventory(Inventory inventory) {
        PlayerChar equippedChar = this.game.getEquippedChar(inventory);
        if (equippedChar == null) {
            return;
        }
        if (equippedChar.weaponId == inventory.f98id) {
            equippedChar.weaponId = 0;
        } else if (equippedChar.armorId == inventory.f98id) {
            equippedChar.armorId = 0;
        } else if (equippedChar.shieldId == inventory.f98id) {
            equippedChar.shieldId = 0;
        } else if (equippedChar.ringId == inventory.f98id) {
            equippedChar.ringId = 0;
        }
        equippedChar.calcStatus((Context) this, this.game);
        try {
            new Persister(this).writeEquipmentChanging(equippedChar);
        } catch (SQLException e) {
            DeviceUtil.handleSqliteException(this, e);
        }
    }

    void changeEquipment(int i, int i2) {
        Inventory inventory;
        PlayerChar playerChar = i2 >= 0 ? this.game.characters.get(this.game.getPlayerCharIndex(i2)) : null;
        Inventory inventory2 = this.game.getInventory(i);
        if (inventory2 == null) {
            Toast.makeText(this, C0380R.string.msg_internal_error, 0).show();
            return;
        }
        unequipInventory(inventory2);
        if (playerChar != null) {
            Item baseItem = inventory2.getBaseItem(this);
            switch (baseItem.type) {
                case WEAPON:
                    playerChar.weaponId = inventory2.f98id;
                    break;
                case ARMOR:
                    playerChar.armorId = inventory2.f98id;
                    break;
                case SHIELD:
                    playerChar.shieldId = inventory2.f98id;
                    break;
                case RING:
                    playerChar.ringId = inventory2.f98id;
                    break;
                default:
                    Toast.makeText(this, C0380R.string.msg_internal_error, 0).show();
                    return;
            }
            if (baseItem.twoHanded) {
                if (playerChar.weaponId == inventory2.f98id) {
                    playerChar.shieldId = 0;
                } else if (playerChar.shieldId == inventory2.f98id) {
                    playerChar.weaponId = 0;
                }
            } else if (playerChar.shieldId == inventory2.f98id && (inventory = this.game.getInventory(playerChar.weaponId)) != null && inventory.getBaseItem(this).twoHanded) {
                playerChar.weaponId = 0;
            }
        }
        if (playerChar != null) {
            playerChar.calcStatus((Context) this, this.game);
        }
        Persister persister = new Persister(this);
        if (playerChar != null) {
            try {
                persister.writeEquipmentChanging(playerChar);
            } catch (SQLException e) {
                DeviceUtil.handleSqliteException(this, e);
                return;
            }
        }
        getCurrentListItemManager().notifyAdapterDataSetChanged();
    }

    private ItemAdapter.ItemListItemManager getCurrentListItemManager() {
        switch (this.mCurrentOperation) {
            case 0:
            case 1:
                return this.innOperation.getListItemManager();
            case 2:
                return this.shopOperation.getListItemManager();
            case 3:
                return this.stockOperation.getListItemManager();
            default:
                return null;
        }
    }

    TownOperation getOperation(int i) {
        return this.mOperations[i];
    }

    void addMessage(String str) {
        if (this.mMessages.size() >= 100) {
            for (int i = 0; i < 10; i++) {
                this.mMessages.remove(0);
            }
        }
        this.mMessages.add(str);
        ((ArrayAdapter) this.mLvMessages.getAdapter()).notifyDataSetChanged();
        this.mLvMessages.setSelection(this.mMessages.size() - 1);
    }

    public void confirmSellItem(ItemAdapter.ItemListItem itemListItem, boolean z) {
        String strMakeItemDialogTitle = makeItemDialogTitle(itemListItem.itemObject, itemListItem.itemObject.getSellPrice(this));
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(strMakeItemDialogTitle);
        decorator.setMessage(C0380R.string.msg_dlg_confirm_sell_item);
        decorator.args().putBoolean("is_inventory", z);
        if (z) {
            decorator.setNeutralText(C0380R.string.lbl_dlg_confirm_sell_item_maximum);
        }
        ConfirmationDialogFragment.show(decorator, this, Confirmation.SELL_ITEM.ordinal(), itemListItem.itemObject.f98id);
    }

    @Override // com.shirobakama.autorpg2.ConfirmationDialogFragment.OnConfirmationListener
    public void onConfirmationOk(int i, int i2, int i3, Bundle bundle) throws Throwable {
        switch (Confirmation.values()[i2]) {
            case BUY_ITEM:
                this.shopOperation.buyItem(i3, false);
                break;
            case SELL_ITEM:
                sellItem(i3, bundle.getBoolean("is_inventory"), false);
                break;
            case BACKUP:
                executeBackup();
                break;
            case RESTORE:
                executeRestore();
                break;
            case INHERIT:
                executeInherit();
                break;
            case SYNTHESIZE:
                executeSynthesize(bundle.getInt("target_id"), bundle.getInt("synthetic_inv_id"), bundle.getBoolean("use_catalyst"));
                break;
            case PERMISSION:
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"}, 20);
                break;
        }
    }

    @Override // com.shirobakama.autorpg2.ConfirmationDialogFragment.OnConfirmationListener
    public void onConfirmationNeutral(int i, int i2, int i3, Bundle bundle) {
        switch (Confirmation.values()[i2]) {
            case BUY_ITEM:
                this.shopOperation.buyItem(i3, true);
                break;
            case SELL_ITEM:
                sellItem(i3, bundle.getBoolean("is_inventory"), true);
                break;
        }
    }

    /* renamed from: com.shirobakama.autorpg2.TownActivity$5 */
    static /* synthetic */ class C03225 {
        static final /* synthetic */ int[] $SwitchMap$com$shirobakama$autorpg2$TownActivity$CharSelect = new int[CharSelect.values().length];

        static {
            try {
                $SwitchMap$com$shirobakama$autorpg2$TownActivity$CharSelect[CharSelect.EQUIP_INVENTORY.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            $SwitchMap$com$shirobakama$autorpg2$TownActivity$Confirmation = new int[Confirmation.values().length];
            try {
                $SwitchMap$com$shirobakama$autorpg2$TownActivity$Confirmation[Confirmation.BUY_ITEM.ordinal()] = 1;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$shirobakama$autorpg2$TownActivity$Confirmation[Confirmation.SELL_ITEM.ordinal()] = 2;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$shirobakama$autorpg2$TownActivity$Confirmation[Confirmation.BACKUP.ordinal()] = 3;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$shirobakama$autorpg2$TownActivity$Confirmation[Confirmation.RESTORE.ordinal()] = 4;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$shirobakama$autorpg2$TownActivity$Confirmation[Confirmation.INHERIT.ordinal()] = 5;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$shirobakama$autorpg2$TownActivity$Confirmation[Confirmation.SYNTHESIZE.ordinal()] = 6;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$shirobakama$autorpg2$TownActivity$Confirmation[Confirmation.PERMISSION.ordinal()] = 7;
            } catch (NoSuchFieldError unused8) {
            }
            $SwitchMap$com$shirobakama$autorpg2$entity$Item$Type = new int[Item.Type.values().length];
            try {
                $SwitchMap$com$shirobakama$autorpg2$entity$Item$Type[Item.Type.WEAPON.ordinal()] = 1;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$shirobakama$autorpg2$entity$Item$Type[Item.Type.ARMOR.ordinal()] = 2;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$shirobakama$autorpg2$entity$Item$Type[Item.Type.SHIELD.ordinal()] = 3;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$shirobakama$autorpg2$entity$Item$Type[Item.Type.RING.ordinal()] = 4;
            } catch (NoSuchFieldError unused12) {
            }
        }
    }

    @Override // com.shirobakama.autorpg2.CharacterSelectDialogFragment.OnCharacterSelectListener
    public void onCharacterSelect(int i, int i2, int i3, int i4) {
        if (C03225.$SwitchMap$com$shirobakama$autorpg2$TownActivity$CharSelect[CharSelect.values()[i3].ordinal()] != 1) {
            return;
        }
        if (i == 0) {
            changeEquipment(i4, i2);
        } else if (i == -1) {
            changeEquipment(i4, -1);
        }
    }

    String getCurrentDungeonStatus(Dungeon dungeon) {
        int i;
        if (dungeon == null) {
            return BuildConfig.FLAVOR;
        }
        if (TypeUtil.isNullOrEmpty(this.game.dungeonContext.stats)) {
            return getString(C0380R.string.msg_dungeon_status_none);
        }
        if (this.game.dungeonContext.stats.get(0).dungeonId != this.game.dungeonId) {
            return getString(C0380R.string.msg_dungeon_status_none);
        }
        Iterator<DungeonStat> it = this.game.dungeonContext.stats.iterator();
        while (true) {
            if (!it.hasNext()) {
                i = -1;
                break;
            }
            DungeonStat next = it.next();
            if (!next.isConqured()) {
                i = next.floor - 1;
                break;
            }
        }
        if (i == -1) {
            return getString(C0380R.string.msg_dungeon_status_explored);
        }
        return i == 0 ? getString(C0380R.string.msg_dungeon_status_none) : getString(C0380R.string.msg_dungeon_status_exploring, new Object[]{Integer.valueOf(i)});
    }

    @Override // com.shirobakama.autorpg2.CharacterDetailDialogFragment.CharacterDetailCallback
    public List<PlayerChar> getPlayerChars() {
        return this.game.characters;
    }

    @Override // com.shirobakama.autorpg2.CharacterDetailDialogFragment.CharacterDetailCallback
    public GameContext getGameContext() {
        return this.game;
    }

    @Override // com.shirobakama.autorpg2.CharacterDetailDialogFragment.CharacterDetailCallback
    public LayoutInflater getCurrentLayoutInflater() {
        return this.layoutInflater;
    }

    @Override // com.shirobakama.autorpg2.SkillListDialogFragment.SkillListCallback
    public void onSkillSelected(String str, int i, Skill skill, Parcelable parcelable) {
        if (SKILL_SELECT_TAG_LEARN_SKILL.equals(str)) {
            PlayerChar playerChar = this.game.characters.get(i);
            playerChar.learnNewSkill(skill.f107id);
            persistGameChar(playerChar);
            addMessage(getString(C0380R.string.msg_skill_up_completed, new Object[]{skill.name}));
            return;
        }
        if (SKILL_SELECT_TAG_ADD_SLOT.equals(str)) {
            SkillSlotDialogFragment.State state = (SkillSlotDialogFragment.State) parcelable;
            state.availableSkillIds.add(Integer.valueOf(skill.f107id));
            showSkillSlot(state);
        } else if (SKILL_SELECT_TAG_CUSTOMIZE.equals(str)) {
            openSkillCustomization(i, skill);
        }
    }

    void showSkillSlot(SkillSlotDialogFragment.State state) {
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(getString(C0380R.string.msg_dlg_title_skill_slot, new Object[]{Integer.valueOf(state.maxSkillSlots)}));
        decorator.setCancelable(true).setPositiveText(0);
        decorator.args().putParcelable("state", state);
        if (state.canModify) {
            decorator.setNeutralText(C0380R.string.lbl_dlg_btn_up_skill);
        }
        decorator.decorate(new SkillSlotDialogFragment()).show(getSupportFragmentManager());
    }

    @Override // com.shirobakama.autorpg2.SkillSlotDialogFragment.SkilSlotCallback
    public void selectSkillForSlot(SkillSlotDialogFragment.State state) {
        ArrayList<Integer> arrayList = new ArrayList<>(this.game.characters.get(state.charIndex).getSkillIds());
        arrayList.removeAll(state.availableSkillIds);
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setCancelable(true).setPositiveText(0);
        decorator.args().putInt("char_index", state.charIndex);
        decorator.args().putBoolean("selectable", true);
        decorator.args().putString("tag", SKILL_SELECT_TAG_ADD_SLOT);
        decorator.args().putIntegerArrayList("skill_ids", arrayList);
        decorator.args().putParcelable("extra", state);
        decorator.decorate(new SkillListDialogFragment()).show(getSupportFragmentManager());
    }

    @Override // com.shirobakama.autorpg2.SkillSlotDialogFragment.SkilSlotCallback
    public void completeSkillSlot(SkillSlotDialogFragment.State state) {
        PlayerChar playerChar = this.game.characters.get(state.charIndex);
        playerChar.setAvailableSkillIds(state.availableSkillIds);
        persistGameChar(playerChar);
    }

    @Override // com.shirobakama.autorpg2.OptionMenuDialogFragment.Callback
    public void selectMenu(Bundle bundle, int i, List<LabelValueItem> list) {
        LabelValueItem labelValueItem = list.get(i);
        if (labelValueItem.value >= MENU_USE_ITEM_USE && labelValueItem.value < MENU_USE_ITEM_DETAIL) {
            confirmUseItem(labelValueItem.value - MENU_USE_ITEM_USE, bundle);
            return;
        }
        int i2 = labelValueItem.value;
        if (i2 == 10100) {
            showOtherMenu();
            return;
        }
        if (i2 != MENU_USE_ITEM_DETAIL) {
            switch (i2) {
                case MENU_OLDER_LOG /* 10001 */:
                    selectViewingLog();
                    break;
                case MENU_VIEW_LOG_FILE /* 10002 */:
                    selectLogFile();
                    break;
                case MENU_QUEST /* 10003 */:
                    showQuestDetails();
                    break;
                case MENU_HELP /* 10004 */:
                    showHelpSelectionMenu();
                    break;
                case MENU_PREFERENCES /* 10005 */:
                    showPreferences();
                    break;
                case MENU_PRIVACY_POLICY /* 10006 */:
                    showPrivacyPolicy();
                    break;
                default:
                    switch (i2) {
                        case MENU_OTHER_BACKUP /* 10201 */:
                            confirmBackup();
                            break;
                        case MENU_OTHER_RESTORE /* 10202 */:
                            confirmRestore();
                            break;
                        case MENU_OTHER_INHERIT /* 10203 */:
                            confirmInherit();
                            break;
                        case MENU_OTHER_STORY /* 10204 */:
                            selectStory();
                            break;
                        case MENU_OTHER_ABOUT /* 10205 */:
                            showAboutDialog();
                            break;
                        default:
                            switch (i2) {
                                case MENU_HELP_EDGE_OF_TOWN /* 10301 */:
                                case MENU_HELP_INN_AND_CHARACTER /* 10302 */:
                                case MENU_HELP_SHOP /* 10303 */:
                                case MENU_HELP_STOCK /* 10304 */:
                                    showHelpDialog(labelValueItem.value);
                                    break;
                                default:
                                    switch (i2) {
                                        case MENU_STORY_INTRODUCTION /* 13001 */:
                                            showStory(1);
                                            break;
                                        case MENU_STORY_EPILOG /* 13002 */:
                                            showStory(2);
                                            break;
                                        default:
                                            this.mOperations[this.mCurrentOperation].selectMenu(bundle, i, list);
                                            break;
                                    }
                            }
                    }
            }
            return;
        }
        showInventoryDetail(bundle);
    }

    void upAttribute(int i, List<LabelValueItem> list, int i2) {
        if (i < 0 || i >= list.size()) {
            return;
        }
        GameChar.Attribute attribute = GameChar.Attribute.values()[list.get(i).value];
        PlayerChar playerChar = this.game.characters.get(i2);
        playerChar.statusBonus--;
        playerChar.setBaseAttr(attribute, playerChar.getBaseAttr(attribute) + 1);
        persistGameChar(playerChar);
        playerChar.calcStatus((Context) this, this.game);
        addMessage(getString(C0380R.string.msg_attr_up_completed));
    }

    private void confirmInherit() {
        if (this.game.isAdventuring(this)) {
            return;
        }
        ConfirmationDialogFragment.show(this, getString(C0380R.string.msg_dlg_title_confirm_inherit), getString(C0380R.string.msg_dlg_confirm_inherit, new Object[]{InheritanceAnalyzer.getPreviousBackupFilePath(this)}), Confirmation.INHERIT.ordinal(), 0);
    }

    private void confirmBackup() {
        if (!this.game.isAdventuring(this) && checkPermissions(false)) {
            ConfirmationDialogFragment.show(this, getString(C0380R.string.msg_dlg_title_confirm_backup), getString(C0380R.string.msg_dlg_confirm_backup, new Object[]{BackupRestoreUtil.getBackupFilePath(this)}), Confirmation.BACKUP.ordinal(), 0);
        }
    }

    private boolean checkPermissions(boolean z) {
        String str = z ? "android.permission.READ_EXTERNAL_STORAGE" : "android.permission.WRITE_EXTERNAL_STORAGE";
        int i = !z ? 1 : 0;
        if (ContextCompat.checkSelfPermission(this, str) == 0) {
            return true;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, str)) {
            Log.d(TAG, "onResume: Showing permission request.");
            ConfirmationDialogFragment.show(this, getString(C0380R.string.msg_dlg_request_storage_permission_title), getString(C0380R.string.msg_request_storage_permission), Confirmation.PERMISSION.ordinal(), i);
            return false;
        }
        Log.d(TAG, "onResume: First time request.");
        ConfirmationDialogFragment.show(this, getString(C0380R.string.msg_dlg_request_storage_permission_title), getString(C0380R.string.msg_request_storage_permission), Confirmation.PERMISSION.ordinal(), i);
        return false;
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (i == 20) {
            if (iArr.length > 0 && iArr[0] == 0) {
                Toast.makeText(this, C0380R.string.msg_retry_again, 1).show();
            } else {
                Toast.makeText(this, C0380R.string.msg_no_storage_permission, 1).show();
            }
        }
    }

    private void executeBackup() throws Throwable {
        String strCreateBackupFile = BackupRestoreUtil.createBackupFile(this);
        if (strCreateBackupFile != null) {
            Toast.makeText(this, strCreateBackupFile, 1).show();
        } else {
            Toast.makeText(this, C0380R.string.msg_backup_completed, 0).show();
        }
    }

    private void confirmRestore() {
        if (!this.game.isAdventuring(this) && checkPermissions(true)) {
            ConfirmationDialogFragment.show(this, getString(C0380R.string.msg_dlg_title_confirm_restore), getString(C0380R.string.msg_dlg_confirm_restore, new Object[]{BackupRestoreUtil.getBackupFilePath(this)}), Confirmation.RESTORE.ordinal(), 0);
        }
    }

    private void executeRestore() throws Throwable {
        String strRestoreBackupFile = BackupRestoreUtil.restoreBackupFile(this);
        if (strRestoreBackupFile != null) {
            Toast.makeText(this, strRestoreBackupFile, 1).show();
            return;
        }
        Toast.makeText(this, C0380R.string.msg_restore_completed, 0).show();
        startActivity(new Intent(getApplicationContext(), (Class<?>) AutoRpgMainActivity.class));
        finish();
    }

    private void executeInherit() {
        GameFlag flag = this.game.getFlag(GameFlag.Key.asType(GameFlag.FlagType.OTHER, QuestConst.FLAG_INHERITED));
        if (flag == null || !flag.value) {
            InheritanceAnalyzer inheritanceAnalyzer = new InheritanceAnalyzer();
            if (!inheritanceAnalyzer.analyze(this)) {
                Toast.makeText(this, inheritanceAnalyzer.errorMessage, 1).show();
                return;
            }
            Iterator<TownFlagEngine.Result> it = TownFlagEngine.inherit(this, this.game, inheritanceAnalyzer).iterator();
            boolean zProcessEngineResult = false;
            while (it.hasNext()) {
                zProcessEngineResult |= processEngineResult(it.next());
            }
            Toast.makeText(this, C0380R.string.msg_previous_inherited, 0).show();
            if (zProcessEngineResult) {
                this.mOperations[this.mCurrentOperation].activate(false);
            }
        }
    }

    private void showOtherMenu() {
        ArrayList<LabelValueItem> arrayList = new ArrayList<>();
        if (!this.game.isAdventuring(this)) {
            arrayList.add(new LabelValueItem(MENU_OTHER_BACKUP, getString(C0380R.string.lbl_town_menu_backup)));
            arrayList.add(new LabelValueItem(MENU_OTHER_RESTORE, getString(C0380R.string.lbl_town_menu_restore)));
            GameFlag flag = this.game.getFlag(GameFlag.Key.asType(GameFlag.FlagType.OTHER, QuestConst.FLAG_INHERITED));
            if (flag == null || !flag.value) {
                arrayList.add(new LabelValueItem(MENU_OTHER_INHERIT, getString(C0380R.string.lbl_town_menu_inherit)));
            }
        }
        arrayList.add(new LabelValueItem(MENU_OTHER_STORY, getString(C0380R.string.lbl_town_menu_story)));
        arrayList.add(new LabelValueItem(MENU_OTHER_ABOUT, getString(C0380R.string.lbl_town_menu_about)));
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setChoiceMode(0);
        decorator.setLabelValueItems(arrayList);
        decorator.decorate(new OptionMenuDialogFragment()).show(getSupportFragmentManager());
    }

    private void selectStory() {
        GameFlag flag = this.game.getFlag(new GameFlag.Key(GameFlag.FlagType.QUEST_CLEAR, QuestDb.QUEST_ATTACK_DARK_LORD));
        if (flag == null || !flag.value) {
            showStory(1);
            return;
        }
        ArrayList<LabelValueItem> arrayList = new ArrayList<>();
        arrayList.add(new LabelValueItem(MENU_STORY_INTRODUCTION, getString(C0380R.string.lbl_town_menu_story_introduction)));
        arrayList.add(new LabelValueItem(MENU_STORY_EPILOG, getString(C0380R.string.lbl_town_menu_story_epilog)));
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setChoiceMode(0);
        decorator.setLabelValueItems(arrayList);
        decorator.decorate(new OptionMenuDialogFragment()).show(getSupportFragmentManager());
    }

    void showStory(int i) {
        Intent intent = new Intent(getApplicationContext(), (Class<?>) StoryActivity.class);
        intent.putExtra(StoryActivity.EXTRA_STORY_TYPE, i);
        DeviceUtil.setRequestOrientationForIntent(this, intent);
        startActivity(intent);
    }

    private void showQuestDetails() {
        Intent intent = new Intent(getApplicationContext(), (Class<?>) CatalogActivity.class);
        CatalogActivity.setForQuestCatalog(intent);
        GameContext.game = this.game.copy();
        DeviceUtil.setRequestOrientationForIntent(this, intent);
        startActivity(intent);
    }

    private void showHelpSelectionMenu() {
        ArrayList<LabelValueItem> arrayList = new ArrayList<>();
        arrayList.add(new LabelValueItem(MENU_HELP_EDGE_OF_TOWN, getString(C0380R.string.lbl_help_edge_of_town)));
        arrayList.add(new LabelValueItem(MENU_HELP_INN_AND_CHARACTER, getString(C0380R.string.lbl_help_inn_and_character)));
        arrayList.add(new LabelValueItem(MENU_HELP_SHOP, getString(C0380R.string.lbl_help_shop)));
        arrayList.add(new LabelValueItem(MENU_HELP_STOCK, getString(C0380R.string.lbl_help_stock)));
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setChoiceMode(0);
        decorator.setLabelValueItems(arrayList);
        decorator.decorate(new OptionMenuDialogFragment()).show(getSupportFragmentManager());
    }

    private void showHelpDialog(int i) {
        String string;
        switch (i) {
            case MENU_HELP_EDGE_OF_TOWN /* 10301 */:
                string = getString(C0380R.string.msg_help_edge_of_town);
                break;
            case MENU_HELP_INN_AND_CHARACTER /* 10302 */:
                string = getString(C0380R.string.msg_help_inn) + getString(C0380R.string.msg_help_subclass_status);
                break;
            case MENU_HELP_SHOP /* 10303 */:
                string = getString(C0380R.string.msg_help_shop);
                break;
            case MENU_HELP_STOCK /* 10304 */:
                string = getString(C0380R.string.msg_help_stock);
                break;
            default:
                string = null;
                break;
        }
        HelpDialogFragment.show(this, string);
    }

    private void showAboutDialog() {
        GameFlag flag = this.game.getFlag(GameFlag.Key.asType(GameFlag.FlagType.OTHER, "total_adventure_time"));
        int optionAsInt = flag == null ? 0 : flag.getOptionAsInt();
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(C0380R.string.lbl_town_menu_about);
        decorator.setCancelable(true).setPositiveText(0);
        decorator.args().putInt("total_adventure_time", optionAsInt);
        decorator.decorate(new AboutDialogFragment()).show(getSupportFragmentManager());
    }

    private void showPreferences() {
        Intent intent = new Intent(this, (Class<?>) AutoRpgPrefsActivity.class);
        AutoRpgPrefsActivity.setIsAdventuring(intent, this.game.isAdventuring(this));
        DeviceUtil.setRequestOrientationForIntent(this, intent);
        startActivityForResult(intent, 10);
    }

    private void showPrivacyPolicy() {
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getString(C0380R.string.res_url_privacy_policy))));
    }

    void confirmQuest(String str) {
        if (str == null) {
            return;
        }
        Quest quest = QuestRepository.getQuest(str);
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(C0380R.string.msg_dlg_title_confirm_quest);
        decorator.setMessage(getString(quest.descStringId) + getString(C0380R.string.msg_dlg_confirm_quest));
        decorator.setPositiveText(C0380R.string.msg_dlg_ok_confirm_quest).setNegativeText(0);
        decorator.args().putString("quest_symbol", str);
        decorator.decorate(new QuestConfirmDialogFragment()).show(getSupportFragmentManager());
    }

    public static class QuestConfirmDialogFragment extends AlertDialogFragment {
        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        protected void onClick(DialogInterface dialogInterface, int i) {
            String string = getArguments().getString("quest_symbol");
            switch (i) {
                case -2:
                    ((TownActivity) getActivity()).refuseQuest(string);
                    break;
                case -1:
                    ((TownActivity) getActivity()).startQuest(string);
                    break;
            }
        }
    }

    protected void refuseQuest(String str) {
        processEngineResult(this.mOperations[this.mCurrentOperation].refuseQuest(str));
    }

    protected void startQuest(String str) {
        processEngineResult(this.mOperations[this.mCurrentOperation].acceptQuest(str));
    }

    boolean processEngineResult(TownFlagEngine.Result result) {
        Inventory inventory;
        if (result.message != null) {
            addMessage(result.message);
        }
        Persister persister = new Persister(this);
        try {
            LinkedList linkedList = new LinkedList();
            if (result.flags != null) {
                for (GameFlag gameFlag : result.flags) {
                    GameFlag orCreateFlag = this.game.getOrCreateFlag(gameFlag.key());
                    orCreateFlag.value = gameFlag.value;
                    orCreateFlag.option = gameFlag.option;
                    linkedList.add(orCreateFlag);
                }
            }
            if (result.gold != 0) {
                this.game.gold += result.gold;
            }
            Inventory inventory2 = null;
            if (result.item != null) {
                inventory = new Inventory();
                inventory.itemId = result.item.f97id;
                this.game.inventories.add(inventory);
                GameFlag orCreateFlag2 = this.game.getOrCreateFlag(GameFlag.Key.asItemGot(result.item));
                orCreateFlag2.addOptionAsInt(1);
                linkedList.add(orCreateFlag2);
            } else {
                inventory = null;
            }
            if (result.lostItem != null) {
                Iterator<Inventory> it = this.game.inventories.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Inventory next = it.next();
                    if (next.itemId == result.lostItem.f97id) {
                        inventory2 = next;
                        break;
                    }
                }
                if (inventory2 != null) {
                    this.game.inventories.remove(inventory2);
                }
            }
            if (result.gold != 0 || inventory != null) {
                persister.writeInventoryAdding(inventory, this.game);
            }
            if (inventory2 != null) {
                persister.writeInventoryRemoving(inventory2, this.game);
            }
            if (!linkedList.isEmpty()) {
                persister.writeFlags(linkedList);
            }
            if (result.joinMember != null) {
                TownFlagEngine.joinNewMember(this, result.joinMember, this.game);
            }
            return (result.gold == 0 && inventory == null && inventory2 == null) ? false : true;
        } catch (SQLException e) {
            DeviceUtil.handleSqliteException(this, e);
            return false;
        }
    }

    void showItemDetailOrUseItem(ItemAdapter.ItemListItem itemListItem) {
        ItemObject itemObject = itemListItem.itemObject;
        if (!((itemObject instanceof Inventory) && itemObject.getBaseItem(this).useableInTown && !this.game.isAdventuring(this))) {
            showItemDetail(itemObject);
            return;
        }
        ArrayList<LabelValueItem> arrayList = new ArrayList<>();
        for (int i = 0; i < this.game.characters.size(); i++) {
            arrayList.add(new LabelValueItem(i + MENU_USE_ITEM_USE, getString(C0380R.string.lbl_town_inventory_menu_use_char, new Object[]{this.game.characters.get(i).name})));
        }
        arrayList.add(new LabelValueItem(MENU_USE_ITEM_DETAIL, getString(C0380R.string.lbl_inn_inventory_menu_detail)));
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setChoiceMode(0);
        decorator.setCancelable(true);
        decorator.setLabelValueItems(arrayList);
        decorator.args().putInt("inventory_id", itemObject.f98id);
        decorator.decorate(new OptionMenuDialogFragment()).show(getSupportFragmentManager());
    }

    private void confirmUseItem(int i, Bundle bundle) {
        if (i < 0 || i >= this.game.characters.size()) {
            return;
        }
        PlayerChar playerChar = this.game.characters.get(i);
        int i2 = bundle.getInt("inventory_id");
        Inventory inventory = this.game.getInventory(i2);
        if (inventory == null) {
            return;
        }
        GameChar.CharClass changedClass = TownFlagEngine.getChangedClass(inventory.getBaseItem(this));
        if (changedClass == null) {
            useItem(i, i2);
            return;
        }
        String string = getString(C0380R.string.msg_dlg_confirm_use_item_class_change, new Object[]{playerChar.name, inventory.getName(this), changedClass.getString(this)});
        if (playerChar.getLernableSkillCount(this) > 0) {
            string = string + getString(C0380R.string.msg_learnable_skill_available);
        }
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(C0380R.string.msg_dlg_title_confirm_use_item);
        decorator.setMessage(string);
        decorator.setNegativeText(0).setPositiveText(0);
        decorator.args().putInt("char_index", i);
        decorator.args().putInt("inventory_id", i2);
        decorator.decorate(new UseItemConfirmDialogFragment()).show(getSupportFragmentManager());
    }

    public static class UseItemConfirmDialogFragment extends AlertDialogFragment {
        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        protected void onClick(DialogInterface dialogInterface, int i) {
            int i2 = getArguments().getInt("char_index");
            int i3 = getArguments().getInt("inventory_id");
            if (i != -1) {
                return;
            }
            ((TownActivity) getActivity()).useItem(i2, i3);
        }
    }

    private void showInventoryDetail(Bundle bundle) {
        Inventory inventory = this.game.getInventory(bundle.getInt("inventory_id"));
        if (inventory != null) {
            showItemDetail(inventory);
        }
    }

    void useItem(int i, int i2) {
        Inventory inventory;
        if (i < 0 || i >= this.game.characters.size()) {
            return;
        }
        PlayerChar playerChar = this.game.characters.get(i);
        Inventory inventory2 = this.game.getInventory(i2);
        if (inventory2 == null) {
            return;
        }
        Item baseItem = inventory2.getBaseItem(this);
        if (!baseItem.useableInTown) {
            addMessage(getString(C0380R.string.msg_use_item_no_effect));
            return;
        }
        if (baseItem.f97id == 5150) {
            synthesizeItem(i);
            return;
        }
        processEngineResult(TownFlagEngine.useClassChangeItem(this, playerChar, inventory2));
        for (int i3 : new int[]{playerChar.armorId, playerChar.weaponId, playerChar.shieldId, playerChar.ringId}) {
            if (i3 != 0 && (inventory = this.game.getInventory(i3)) != null) {
                unequipInventory(inventory);
            }
        }
        playerChar.calcStatus((Context) this, this.game);
        playerChar.restoreHpMp();
        persistGameChar(playerChar);
        this.mOperations[this.mCurrentOperation].activate(false);
    }

    void showItemDetail(ItemObject itemObject) {
        Iterator<PlayerChar> it = this.game.characters.iterator();
        boolean z = false;
        while (it.hasNext()) {
            z |= !it.next().clazz.isStandard();
        }
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setCancelable(true).setPositiveText(0);
        ItemDetailDialogFragment.setArguments(decorator.args(), itemObject.f98id, z, false, itemObject instanceof ShopItem, itemObject instanceof Stock, this.game);
        if (!this.game.isAdventuring(this) && (itemObject instanceof Inventory)) {
            decorator.setNeutralText(C0380R.string.lbl_dlg_btn_item_detail_change_name);
        }
        decorator.decorate(new ItemDetailDialogFragment()).show(getSupportFragmentManager());
    }

    void inputItemName(int i) {
        if (this.game.getInventory(i) == null) {
            return;
        }
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(C0380R.string.lbl_dlg_title_item_detail_change_name);
        decorator.setCancelable(true).setPositiveText(0).setNegativeText(0).setNeutralText(C0380R.string.lbl_dlg_btn_item_restore_name);
        decorator.args().putInt("item_obj_id", i);
        decorator.decorate(new ItemChangeNameDialogFragment()).show(getSupportFragmentManager());
    }

    public static class ItemChangeNameDialogFragment extends AlertDialogFragment {
        private EditText mEtItemName;

        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        @SuppressLint({"InflateParams"})
        protected View getAlertDialogView() {
            View viewInflate = ((TownActivity) getActivity()).getCurrentLayoutInflater().inflate(C0380R.layout.item_change_name_dialog, (ViewGroup) null);
            this.mEtItemName = (EditText) viewInflate.findViewById(C0380R.id.etItemName);
            return viewInflate;
        }

        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        protected void onClick(DialogInterface dialogInterface, int i) {
            int i2 = getArguments().getInt("item_obj_id");
            if (i == -3) {
                ((TownActivity) getActivity()).changeItemName(i2, null);
            } else {
                if (i != -1) {
                    return;
                }
                String string = this.mEtItemName.getText().toString();
                if (TextUtils.isEmpty(string)) {
                    return;
                }
                ((TownActivity) getActivity()).changeItemName(i2, string);
            }
        }
    }

    public void changeItemName(int i, String str) {
        Inventory inventory = this.game.getInventory(i);
        if (inventory == null) {
            return;
        }
        String name = inventory.getName(this);
        if (TextUtils.isEmpty(str)) {
            str = inventory.getOriginalName(this);
            inventory.name = null;
        } else {
            inventory.name = str;
        }
        if (name.equals(str)) {
            return;
        }
        try {
            new Persister(this).writeInventoryAdding(inventory, this.game);
        } catch (SQLException e) {
            DeviceUtil.handleSqliteException(this, e);
        }
        addMessage(getString(C0380R.string.msg_item_name_changed, new Object[]{name, str}));
        this.mOperations[this.mCurrentOperation].activate(false);
    }

    void selectLogFile() {
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(C0380R.string.msg_dlg_select_log_file).setCancelable(true);
        decorator.setNegativeText(0);
        decorator.args().putStringArray("file_extentions", new String[]{getString(C0380R.string.res_log_export_file_ext)});
        decorator.decorate(new FileSelectDialogFragment()).show(getSupportFragmentManager());
    }

    public void onLogFileSelect(File file) {
        Toast.makeText(this, C0380R.string.msg_logview_log_file_read, 0).show();
        GameContext.game = this.game.copy();
        Intent intent = new Intent(getApplicationContext(), (Class<?>) LogViewActivity.class);
        DeviceUtil.setRequestOrientationForIntent(this, intent);
        LogViewActivity.setLogFileToIntent(intent, file);
        startActivityForResult(intent, 2);
    }

    void showSkill(int i) {
        PlayerChar playerChar = this.game.characters.get(i);
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setCancelable(true).setPositiveText(0);
        decorator.args().putInt("char_index", i);
        decorator.args().putIntegerArrayList("skill_ids", playerChar.getSkillIds());
        decorator.args().putBoolean("clickable", true ^ this.game.isAdventuring(this));
        decorator.args().putString("tag", SKILL_SELECT_TAG_CUSTOMIZE);
        decorator.decorate(new SkillListDialogFragment()).show(getSupportFragmentManager());
    }

    private void openSkillCustomization(int i, Skill skill) {
        if (skill == null) {
            return;
        }
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(C0380R.string.msg_dlg_title_skill_customize);
        decorator.setCancelable(true).setPositiveText(0).setNegativeText(0).setNeutralText(C0380R.string.lbl_dlg_btn_skill_customize_restore);
        decorator.args().putInt("char_index", i);
        decorator.args().putInt("skill_id", skill.f107id);
        decorator.decorate(new SkillCustomizationDialogFragment()).show(getSupportFragmentManager());
    }

    public static class SkillCustomizationDialogFragment extends AlertDialogFragment {
        private EditText mEtSkillDesc;
        private EditText mEtSkillName;

        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        @SuppressLint({"InflateParams"})
        protected View getAlertDialogView() {
            View viewInflate = ((TownActivity) getActivity()).getCurrentLayoutInflater().inflate(C0380R.layout.skill_customization_dialog, (ViewGroup) null);
            this.mEtSkillName = (EditText) viewInflate.findViewById(C0380R.id.etSkillName);
            this.mEtSkillDesc = (EditText) viewInflate.findViewById(C0380R.id.etSkillDesc);
            PlayerChar playerChar = ((TownActivity) getActivity()).game.characters.get(getArguments().getInt("char_index"));
            Skill skill = SkillRepository.getSkill(getActivity(), getArguments().getInt("skill_id"));
            ArrayList arrayList = new ArrayList(1);
            arrayList.add(skill);
            new Persister(getActivity()).readSkillCustomizationForSkills(playerChar.f106id, arrayList);
            if (skill.skillCustomization != null) {
                this.mEtSkillName.setText(skill.skillCustomization.skillName);
                this.mEtSkillDesc.setText(skill.skillCustomization.skillDesc);
            }
            ((TextView) viewInflate.findViewById(C0380R.id.tvSkillOriginalName)).setText(skill.name);
            return viewInflate;
        }

        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        protected void onClick(DialogInterface dialogInterface, int i) {
            int i2 = getArguments().getInt("char_index");
            int i3 = getArguments().getInt("skill_id");
            if (i == -3) {
                ((TownActivity) getActivity()).customizeSkill(i2, i3, null, null);
            } else {
                if (i != -1) {
                    return;
                }
                String string = this.mEtSkillName.getText().toString();
                String strTrim = this.mEtSkillDesc.getText().toString().trim();
                ((TownActivity) getActivity()).customizeSkill(i2, i3, string.trim(), strTrim);
            }
        }
    }

    protected void customizeSkill(int i, int i2, String str, String str2) {
        PlayerChar playerChar = this.game.characters.get(i);
        Skill skill = SkillRepository.getSkill(this, i2);
        Persister persister = new Persister(this);
        if (TextUtils.isEmpty(str) && TextUtils.isEmpty(str2)) {
            persister.deleteSkillCustomization(playerChar.f106id, i2);
            Toast.makeText(this, getString(C0380R.string.msg_skill_customize_restored, new Object[]{skill.name}), 0).show();
        } else {
            SkillCustomization skillCustomization = persister.readSkillCustomization(playerChar.f106id, i2);
            if (skillCustomization == null) {
                skillCustomization = new SkillCustomization();
                skillCustomization.charId = playerChar.f106id;
                skillCustomization.skillId = i2;
            }
            skillCustomization.skillName = str;
            skillCustomization.skillDesc = str2;
            if (skillCustomization.skillName == null || TextUtils.isEmpty(skillCustomization.skillName.trim())) {
                skillCustomization.skillName = skill.name;
            }
            skillCustomization.skillDesc = str2 == null ? BuildConfig.FLAVOR : str2.replace("\n", BuildConfig.FLAVOR).replace("\r", BuildConfig.FLAVOR).trim();
            if (TextUtils.isEmpty(skillCustomization.skillDesc)) {
                skillCustomization.skillDesc = getString(skill.useStringId);
            }
            persister.writeSkillCustomization(skillCustomization);
            Toast.makeText(this, getString(C0380R.string.msg_skill_customized, new Object[]{skill.name}), 0).show();
        }
        showSkill(i);
    }

    private void synthesizeItem(int i) {
        Iterator<Inventory> it = this.game.inventories.iterator();
        int i2 = 0;
        while (it.hasNext()) {
            Item baseItem = it.next().getBaseItem(this);
            if (!baseItem.artifact && baseItem.equipable) {
                i2++;
            }
        }
        if (i2 <= 1) {
            addMessage(getString(C0380R.string.msg_synthesis_no_target_or_synthetic_item));
            return;
        }
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(C0380R.string.msg_dlg_title_synthesis);
        decorator.setCancelable(true).setPositiveText(C0380R.string.lbl_dlg_btn_synthesize).setNegativeText(0);
        decorator.args().putInt("char_index", i);
        decorator.decorate(new ItemSynthesisDialogFragment()).show(getSupportFragmentManager());
    }

    public void confirmSynthesis(Inventory inventory, Inventory inventory2, boolean z) {
        if (inventory == null || inventory2 == null) {
            return;
        }
        if (inventory.f98id == inventory2.f98id) {
            addMessage(getString(C0380R.string.msg_synthesis_cannot_synthesize_same_item));
            return;
        }
        Inventory inventorySynthesizeItem = TownFlagEngine.synthesizeItem(this, inventory, inventory2, z);
        if (inventorySynthesizeItem == null) {
            addMessage(getString(C0380R.string.msg_synthesis_cannot_synthesize_different_type));
            return;
        }
        String string = getString(C0380R.string.msg_dlg_confirm_synthesize, new Object[]{inventory.getName(this), inventory2.getName(this), inventorySynthesizeItem.getName(this)});
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(C0380R.string.msg_dlg_title_confirm_synthesize);
        decorator.setMessage(string);
        decorator.args().putInt("target_id", inventory.f98id);
        decorator.args().putInt("synthetic_inv_id", inventory2.f98id);
        decorator.args().putBoolean("use_catalyst", z);
        ConfirmationDialogFragment.show(decorator, this, Confirmation.SYNTHESIZE.ordinal(), 0);
    }

    private void executeSynthesize(int i, int i2, boolean z) {
        Inventory inventorySynthesizeItem;
        Inventory inventory = this.game.getInventory(i);
        Inventory inventory2 = this.game.getInventory(i2);
        Inventory inventory3 = z ? this.game.inventories.get(this.game.getInventoryIndexForItemId(ItemDb.ITEM_SYNTHETIC_CATALYST)) : null;
        if (inventory == null || inventory2 == null) {
            return;
        }
        if ((z && inventory3 == null) || (inventorySynthesizeItem = TownFlagEngine.synthesizeItem(this, inventory, inventory2, z)) == null) {
            return;
        }
        unequipInventory(inventory);
        unequipInventory(inventory2);
        this.game.inventories.remove(inventory);
        this.game.inventories.remove(inventory2);
        if (inventory3 != null) {
            this.game.inventories.remove(inventory3);
        }
        this.game.inventories.add(inventorySynthesizeItem);
        Persister persister = new Persister(this);
        try {
            persister.writeInventoryRemoving(inventory, this.game);
            persister.writeInventoryRemoving(inventory2, this.game);
            if (inventory3 != null) {
                persister.writeInventoryRemoving(inventory3, this.game);
            }
            persister.writeInventoryAdding(inventorySynthesizeItem, this.game);
            ItemAdapter.ItemListItemManager currentListItemManager = getCurrentListItemManager();
            currentListItemManager.setInventoryLabel(createInventoryLabel());
            currentListItemManager.notifyAdapterDataSetChanged();
            addMessage(getString(C0380R.string.msg_synthesis_synthesize_completed));
        } catch (SQLException e) {
            DeviceUtil.handleSqliteException(this, e);
        }
    }
}
