package com.shirobakama.autorpg2;

import android.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.shirobakama.autorpg2.AlertDialogFragment;
import com.shirobakama.autorpg2.entity.AdventureLog;
import com.shirobakama.autorpg2.entity.CommonLog;
import com.shirobakama.autorpg2.entity.Dungeon;
import com.shirobakama.autorpg2.entity.FightingLog;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.Inventory;
import com.shirobakama.autorpg2.entity.LogFight;
import com.shirobakama.autorpg2.entity.LogManagement;
import com.shirobakama.autorpg2.entity.LogStatus;
import com.shirobakama.autorpg2.entity.Monster;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.p001db.LogPersister;
import com.shirobakama.autorpg2.p001db.Persister;
import com.shirobakama.autorpg2.repo.DungeonRepository;
import com.shirobakama.autorpg2.repo.ItemRepository;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.autorpg2.util.FormatUtil;
import com.shirobakama.autorpg2.view.ItemAdapter;
import com.shirobakama.autorpg2.view.LogAdapter;
import com.shirobakama.logquest2.BuildConfig;
import com.shirobakama.logquest2.C0380R;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class LogViewActivity extends FragmentActivity implements View.OnClickListener {
    private static final String EXTRA_LOG_FILE_NAME = "log_file_name";
    private static final String EXTRA_LOG_MANAGEMENT_ID = "log_management_id";
    protected static int MSG_SAVE_COMPLETED = 1;
    protected static int MSG_SAVE_FAILED = 2;
    protected static final String TAG = "logq-logviewact";
    protected GameContext mGame;
    protected LayoutInflater mInflater;
    private LogManagement mLogManagement;
    protected LogSavingRunnable mLogSavingRunnable;
    private Thread mLogSavingThread;
    private LogStatus mLogStatus;
    private ListView mLvAdventureLog;
    protected boolean mNormalLog;
    private boolean mOlderLog;
    protected Persister mPersister;
    protected List<PlayerChar> mPlayerChars;
    protected SparseArray<PlayerChar> mPlayerCharsForId;
    protected RelativeLayout mRlayLoadingPanel;
    protected int mSelectedLogPosition;
    private TextView mTvDestination;
    private TextView mTvLogStatus;
    private TextView mTvLogStatusTime;
    private boolean mCompleted = false;
    protected List<AdventureLog> mAdventureLogs = new ArrayList();
    private int mLastForwardMinutes = 1;

    private void forwardLog() {
    }

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) throws IOException {
        super.onCreate(bundle);
        DeviceUtil.setLocaleIfNeeded(this, null);
        setContentView(C0380R.layout.log_view);
        DeviceUtil.handleOrientation(this, null);
        this.mInflater = (LayoutInflater) getSystemService("layout_inflater");
        this.mTvDestination = (TextView) findViewById(C0380R.id.tvDestination);
        this.mTvLogStatus = (TextView) findViewById(C0380R.id.tvLogStatus);
        this.mTvLogStatusTime = (TextView) findViewById(C0380R.id.tvLogStatusTime);
        if (bundle != null) {
            this.mGame = (GameContext) bundle.getParcelable("game");
            this.mGame.calcCharacterStatus(this);
        } else {
            this.mGame = GameContext.game;
            GameContext.game = null;
        }
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_LOG_FILE_NAME)) {
            this.mNormalLog = false;
            int logsFromFile = readLogsFromFile(intent.getStringExtra(EXTRA_LOG_FILE_NAME));
            if (logsFromFile != 0) {
                Toast.makeText(this, logsFromFile, 0).show();
                finish();
                return;
            }
        } else {
            this.mNormalLog = true;
            this.mPersister = new Persister(getApplicationContext());
            int intExtra = getIntent().getIntExtra(EXTRA_LOG_MANAGEMENT_ID, -1);
            if (intExtra >= 0) {
                this.mLogManagement = this.mPersister.readLogManagement(intExtra);
                this.mOlderLog = true;
            } else {
                this.mLogManagement = this.mPersister.readLatestLogManagement();
                this.mOlderLog = false;
            }
            LogManagement logManagement = this.mLogManagement;
            if (logManagement == null || !logManagement.completed) {
                Toast.makeText(this, C0380R.string.msg_logview_no_log, 0).show();
                finish();
                return;
            }
            List<PlayerChar> allPlayerChars = this.mPersister.readAllPlayerChars();
            SparseArray sparseArray = new SparseArray();
            for (PlayerChar playerChar : allPlayerChars) {
                sparseArray.put(playerChar.f106id, playerChar);
            }
            this.mPlayerCharsForId = new SparseArray<>();
            this.mPlayerChars = new ArrayList(3);
            int i = 0;
            for (int i2 : this.mLogManagement.pcId) {
                if (i2 != 0) {
                    PlayerChar playerChar2 = (PlayerChar) sparseArray.get(i2);
                    if (playerChar2 == null) {
                        playerChar2 = new PlayerChar();
                        playerChar2.bitmap = BitmapFactory.decodeResource(getResources(), C0380R.drawable.char000_default);
                        playerChar2.name = this.mLogManagement.pcName[i];
                    } else {
                        playerChar2.getBitmap(this);
                    }
                    playerChar2.index = i;
                    this.mPlayerCharsForId.put(i2, playerChar2);
                    this.mPlayerChars.add(playerChar2);
                    i++;
                }
            }
        }
        Dungeon dungeon = DungeonRepository.getDungeon(this, this.mLogManagement.dungeonId);
        if (dungeon == null) {
            dungeon = new Dungeon();
            dungeon.f90id = -1;
            dungeon.name = getString(C0380R.string.res_logview_unknown_dungeon);
        }
        this.mTvDestination.setText(getResources().getString(C0380R.string.lbl_logview_destination, dungeon.name, Integer.valueOf(this.mLogManagement.targetFloor)));
        Button button = (Button) findViewById(C0380R.id.btnReload);
        if (!this.mNormalLog || this.mOlderLog) {
            button.setVisibility(8);
        } else {
            button.setVisibility(0);
            button.setOnClickListener(this);
        }
        ((Button) findViewById(C0380R.id.btnClose)).setOnClickListener(this);
        this.mTvDestination.setOnClickListener(this);
        this.mRlayLoadingPanel = (RelativeLayout) findViewById(C0380R.id.rlayLoadingPanel);
        this.mRlayLoadingPanel.setOnClickListener(this);
        this.mRlayLoadingPanel.setVisibility(8);
        this.mLvAdventureLog = (ListView) findViewById(C0380R.id.lvAdventureLog);
        this.mLvAdventureLog.setAdapter((ListAdapter) new LogAdapter(true, this, this.mAdventureLogs, this.mPlayerChars, false));
        this.mLvAdventureLog.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.shirobakama.autorpg2.LogViewActivity.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i3, long j) {
                LogViewActivity.this.showDetailLog(i3);
            }
        });
        this.mLvAdventureLog.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: com.shirobakama.autorpg2.LogViewActivity.2
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i3, long j) {
                AdventureLog adventureLog = LogViewActivity.this.mAdventureLogs.get(i3);
                int size = (adventureLog == null || adventureLog.logInventories == null) ? 0 : adventureLog.logInventories.size();
                StringBuilder sb = new StringBuilder();
                sb.append(adventureLog == null ? BuildConfig.FLAVOR : FormatUtil.formatLogTimeToHhmm(adventureLog.logTime));
                sb.append(" ");
                sb.append(LogViewActivity.this.getString(C0380R.string.lbl_logview_inventory, new Object[]{Integer.valueOf(size)}));
                String string = sb.toString();
                AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
                decorator.setCancelable(true).setPositiveText(0);
                decorator.args().putInt("selected_log_position", i3);
                decorator.setTitle(string);
                decorator.decorate(new LogItemDialogFragment()).show(LogViewActivity.this.getSupportFragmentManager());
                return true;
            }
        });
        if (this.mNormalLog) {
            this.mCompleted = readLogs();
        }
        Button button2 = (Button) findViewById(C0380R.id.btnSave);
        if (this.mNormalLog && this.mCompleted) {
            button2.setOnClickListener(this);
            button2.setVisibility(0);
        } else {
            button2.setVisibility(8);
        }
        setResult(0);
    }

    private int readLogsFromFile(String str) throws IOException {
        LogPersister logPersister = new LogPersister(this, str);
        int logs = logPersister.readLogs();
        if (logs != 0) {
            return logs;
        }
        this.mLogManagement = logPersister.getLogManagement();
        this.mAdventureLogs = logPersister.getAdventureLogs();
        this.mPlayerCharsForId = logPersister.getPlayerChars();
        this.mPlayerChars = new ArrayList();
        for (int i = 0; i < this.mPlayerCharsForId.size(); i++) {
            PlayerChar playerCharValueAt = this.mPlayerCharsForId.valueAt(i);
            this.mPlayerChars.add(playerCharValueAt);
            playerCharValueAt.index = i;
        }
        return 0;
    }

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("game", this.mGame);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.mNormalLog) {
            readLogs();
        }
        if (this.mAdventureLogs.isEmpty()) {
            Toast.makeText(this, C0380R.string.msg_logview_no_log, 0).show();
            finish();
        } else if (this.mNormalLog) {
            refreshLogs();
        } else {
            setLogStatusAsViewing();
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onPause() throws InterruptedException {
        super.onPause();
        LogSavingRunnable logSavingRunnable = this.mLogSavingRunnable;
        if (logSavingRunnable == null || logSavingRunnable.isDestroyed()) {
            return;
        }
        this.mLogSavingRunnable.destroy();
        this.mLogSavingRunnable = null;
        Toast.makeText(this, C0380R.string.msg_logview_log_file_saving_cancelled, 0).show();
        try {
            this.mLogSavingThread.join();
        } catch (InterruptedException unused) {
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == 2131165245) {
            finish();
            return;
        }
        if (id == 2131165275) {
            readLogs();
            refreshLogs();
        } else if (id == 2131165277) {
            saveLogs();
        } else if (id == 2131165555 && DeviceUtil.isTestDevice(this)) {
            forwardLog();
        }
    }

    protected void showDetailLog(int i) {
        AdventureLog adventureLog = this.mAdventureLogs.get(i);
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setCancelable(true).setPositiveText(0);
        decorator.args().putInt("selected_log_position", i);
        decorator.setTitle(adventureLog == null ? BuildConfig.FLAVOR : FormatUtil.formatLogTimeToHhmm(adventureLog.logTime));
        if (i > 0) {
            decorator.setNegativeText(C0380R.string.lbl_btn_log_detail_previous);
        }
        if (i < this.mAdventureLogs.size() - 1) {
            decorator.setNeutralText(C0380R.string.lbl_btn_log_detail_next);
        }
        decorator.decorate(new LogDetailDialogFragment()).show(getSupportFragmentManager());
    }

    public static class LogDetailDialogFragment extends AlertDialogFragment {
        @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
        }

        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        @SuppressLint({"InflateParams"})
        protected View getAlertDialogView() {
            LogFight logFight;
            LogViewActivity logViewActivity = (LogViewActivity) getActivity();
            View viewInflate = logViewActivity.mInflater.inflate(C0380R.layout.log_dialog_detail, (ViewGroup) null);
            RelativeLayout relativeLayout = (RelativeLayout) viewInflate.findViewById(C0380R.id.rlayMonsterDetail);
            List<AdventureLog> list = logViewActivity.mAdventureLogs;
            if (list == null || list.isEmpty()) {
                Toast.makeText(logViewActivity, "internal error: No logs.", 1).show();
            } else {
                int i = getArguments().getInt("selected_log_position");
                if (i < 0 || i >= list.size()) {
                    i = 0;
                }
                AdventureLog adventureLog = list.get(i);
                if (logViewActivity.mNormalLog) {
                    logFight = logViewActivity.mPersister.readLogFight(logViewActivity, adventureLog.f85id, logViewActivity.mPlayerCharsForId);
                } else {
                    logFight = adventureLog.logFight;
                }
                ListView listView = (ListView) viewInflate.findViewById(C0380R.id.lvDetailLog);
                if (logFight == null) {
                    ArrayList arrayList = new ArrayList(1);
                    arrayList.add(list.get(i));
                    listView.setAdapter((ListAdapter) new LogAdapter(true, logViewActivity, arrayList, logViewActivity.mPlayerChars, true));
                    relativeLayout.setVisibility(8);
                } else {
                    logFight.adventureLog = adventureLog;
                    FightingLog.copyLogCharsForView(logFight.fightingLogs);
                    listView.setAdapter((ListAdapter) new LogAdapter(false, logViewActivity, logFight.fightingLogs, logViewActivity.mPlayerChars, true));
                    LogViewActivity.setMonsterInfoToDetail(getActivity(), relativeLayout, C0380R.string.msg_logview_flog_enemy_header, logFight.monster);
                }
            }
            return viewInflate;
        }

        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        protected void onClick(DialogInterface dialogInterface, int i) {
            int i2 = getArguments().getInt("selected_log_position");
            switch (i) {
                case -3:
                    ((LogViewActivity) getActivity()).showDetailLog(i2 + 1);
                    break;
                case -2:
                    ((LogViewActivity) getActivity()).showDetailLog(i2 - 1);
                    break;
            }
        }
    }

    static void setMonsterInfoToDetail(Context context, View view, int i, Monster monster) {
        ImageView imageView = (ImageView) view.findViewById(C0380R.id.ivMonster);
        TextView textView = (TextView) view.findViewById(C0380R.id.tvEnemyName);
        TextView textView2 = (TextView) view.findViewById(C0380R.id.tvEnemyType);
        TextView textView3 = (TextView) view.findViewById(C0380R.id.tvEnemyRemark);
        if (monster != null) {
            imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), monster.drawableId));
            textView.setText(i == 0 ? monster.name : context.getString(i, monster.name));
            textView2.setText(monster.type.getString(context));
            textView3.setText(monster.getRemark(context));
        }
    }

    public static class LogItemDialogFragment extends AlertDialogFragment {
        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        protected void onClick(DialogInterface dialogInterface, int i) {
        }

        @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
        }

        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        protected View getAlertDialogView() {
            final LogViewActivity logViewActivity = (LogViewActivity) getActivity();
            ListView listView = new ListView(logViewActivity);
            listView.setBackgroundResource(R.drawable.screen_background_light);
            List<AdventureLog> list = logViewActivity.mAdventureLogs;
            if (list == null || list.isEmpty()) {
                Toast.makeText(logViewActivity, "internal error: No logs.", 1).show();
            } else {
                int i = getArguments().getInt("selected_log_position");
                if (i < 0 || i >= list.size()) {
                    i = 0;
                }
                AdventureLog adventureLog = list.get(i);
                final List<AdventureLog.LogInventory> listEmptyList = adventureLog.logInventories;
                if (adventureLog.logInventories == null) {
                    Toast.makeText(logViewActivity, "internal error: No inventories.", 1).show();
                    listEmptyList = Collections.emptyList();
                }
                ArrayList arrayList = new ArrayList(listEmptyList.size());
                for (int i2 = 0; i2 < listEmptyList.size(); i2++) {
                    AdventureLog.LogInventory logInventory = listEmptyList.get(i2);
                    Inventory inventory = new Inventory();
                    inventory.f98id = i2;
                    inventory.itemId = logInventory.itemId;
                    arrayList.add(inventory);
                    if (ItemRepository.getItem(getActivity(), inventory.itemId) == null) {
                        inventory.itemId = 1020;
                    }
                }
                ItemAdapter.ItemListItemManager itemListItemManager = new ItemAdapter.ItemListItemManager(new ItemAdapter.InventoryEquipmentHandler() { // from class: com.shirobakama.autorpg2.LogViewActivity.LogItemDialogFragment.1
                    @Override // com.shirobakama.autorpg2.view.ItemAdapter.InventoryEquipmentHandler
                    public String getEquippedCharInfo(Inventory inventory2) {
                        AdventureLog.LogInventory logInventory2 = (AdventureLog.LogInventory) listEmptyList.get(inventory2.f98id);
                        if (logInventory2.equippedCharId == 0) {
                            return null;
                        }
                        return logViewActivity.mPlayerCharsForId.get(logInventory2.equippedCharId).getEquippedCharInfo(logViewActivity);
                    }
                }, null);
                itemListItemManager.setLogInventoryMode();
                itemListItemManager.setInventories(arrayList);
                listView.setAdapter((ListAdapter) new ItemAdapter(logViewActivity, itemListItemManager));
            }
            return listView;
        }
    }

    private void refreshLogs() {
        String string;
        ((LogAdapter) this.mLvAdventureLog.getAdapter()).notifyDataSetChanged();
        if (!this.mOlderLog) {
            this.mLvAdventureLog.setSelection(this.mAdventureLogs.size() - 1);
        }
        LogFight logFight = null;
        if (this.mLogStatus == null) {
            string = getString(C0380R.string.msg_logview_stat_complete);
            this.mTvLogStatusTime.setText((CharSequence) null);
        } else {
            switch (this.mLogStatus.action) {
                case EXPLORING:
                    string = getString(C0380R.string.msg_logview_stat_exploring, new Object[]{Integer.valueOf(this.mLogStatus.floor), Integer.valueOf(this.mLogStatus.block), Integer.valueOf(this.mLogStatus.captiveRate)});
                    break;
                case FIGHTING:
                    Iterator<AdventureLog> it = this.mAdventureLogs.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            AdventureLog next = it.next();
                            if (next.logTime <= this.mLogStatus.logTime) {
                                if (next.logTime == this.mLogStatus.logTime && next.logFight != null) {
                                    logFight = next.logFight;
                                }
                            }
                        }
                    }
                    Object[] objArr = new Object[1];
                    objArr[0] = logFight == null ? getString(C0380R.string.msg_internal_error) : logFight.monster.name;
                    string = getString(C0380R.string.msg_logview_stat_fighting, objArr);
                    break;
                case MOVING:
                    string = getString(C0380R.string.msg_logview_stat_explored, new Object[]{Integer.valueOf(this.mLogStatus.floor), Integer.valueOf(this.mLogStatus.block), Integer.valueOf(this.mLogStatus.captiveRate)});
                    break;
                case RETURNING:
                    string = getString(C0380R.string.msg_logview_stat_returning, new Object[]{Integer.valueOf(this.mLogStatus.floor), Integer.valueOf(this.mLogStatus.block), Integer.valueOf(this.mLogStatus.captiveRate)});
                    break;
                default:
                    string = null;
                    break;
            }
            this.mTvLogStatusTime.setText(FormatUtil.formatLogTimeToHhmm(this.mLogStatus.logTime));
        }
        this.mTvLogStatus.setText(string);
    }

    private void setLogStatusAsViewing() {
        String string = getString(C0380R.string.msg_logview_stat_view_log_file, new Object[]{!this.mAdventureLogs.isEmpty() ? DateFormat.format(getString(C0380R.string.msg_logview_stat_view_log_file_timestamp_format), CommonLog.getDateFromLogTime(this.mAdventureLogs.get(0).logTime)) : null});
        this.mTvLogStatusTime.setText((CharSequence) null);
        this.mTvLogStatus.setText(string);
    }

    private boolean readLogs() {
        Date dateFromLogTime;
        if (this.mAdventureLogs == null) {
            this.mAdventureLogs = new ArrayList();
        }
        Date date = new Date();
        if (this.mAdventureLogs.isEmpty()) {
            dateFromLogTime = null;
        } else {
            dateFromLogTime = CommonLog.getDateFromLogTime(this.mAdventureLogs.get(r1.size() - 1).logTime);
        }
        List<AdventureLog> adventureLog = this.mPersister.readAdventureLog(this, this.mLogManagement, dateFromLogTime, date, this.mPlayerCharsForId);
        AdventureLog.copyLogInventoriesForView(this.mAdventureLogs, adventureLog);
        CommonLog.copyLogCharsForView(this.mAdventureLogs, adventureLog);
        this.mAdventureLogs.addAll(adventureLog);
        this.mLogStatus = this.mPersister.readLogStatus(this.mLogManagement, date);
        return !this.mPersister.existsNewerLog(date);
    }

    public static void setLogManagementIdToIntent(Intent intent, int i) {
        intent.putExtra(EXTRA_LOG_MANAGEMENT_ID, i);
    }

    public static void setLogFileToIntent(Intent intent, File file) {
        intent.putExtra(EXTRA_LOG_FILE_NAME, file.getAbsolutePath());
    }

    private static class LogSavingRunnable implements Runnable {
        private Activity mActivity;
        private boolean mDestroyed;
        private AdventureLog mFirstLog;
        private Handler mHandler;
        private LogManagement mLogMgmt;
        private LogPersister mLogPersister;
        private SparseArray<PlayerChar> mPcs;

        public LogSavingRunnable(Activity activity, Handler handler, LogManagement logManagement, SparseArray<PlayerChar> sparseArray, AdventureLog adventureLog) {
            this.mActivity = activity;
            this.mHandler = handler;
            this.mLogMgmt = logManagement;
            this.mPcs = sparseArray;
            this.mFirstLog = adventureLog;
        }

        @Override // java.lang.Runnable
        public void run() {
            Message messageObtain;
            this.mLogPersister = new LogPersister(this.mActivity, this.mLogMgmt, this.mPcs);
            int iWriteLogs = this.mLogPersister.writeLogs(this.mFirstLog);
            if (this.mDestroyed) {
                return;
            }
            if (iWriteLogs == 0) {
                messageObtain = Message.obtain(this.mHandler, LogViewActivity.MSG_SAVE_COMPLETED, this.mLogPersister.getFileName());
            } else {
                messageObtain = Message.obtain(this.mHandler, LogViewActivity.MSG_SAVE_FAILED, Integer.valueOf(iWriteLogs));
            }
            this.mHandler.sendMessage(messageObtain);
        }

        public void destroy() {
            this.mDestroyed = true;
            this.mLogPersister.terminate();
        }

        public boolean isDestroyed() {
            return this.mDestroyed;
        }
    }

    private void saveLogs() {
        Handler handler = new Handler(new Handler.Callback() { // from class: com.shirobakama.autorpg2.LogViewActivity.3
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message message) {
                if (message.what != LogViewActivity.MSG_SAVE_COMPLETED && message.what != LogViewActivity.MSG_SAVE_FAILED) {
                    return false;
                }
                if (message.what == LogViewActivity.MSG_SAVE_FAILED) {
                    Toast.makeText(LogViewActivity.this, ((Integer) message.obj).intValue(), 0).show();
                }
                if (message.what == LogViewActivity.MSG_SAVE_COMPLETED) {
                    LogViewActivity logViewActivity = LogViewActivity.this;
                    Toast.makeText(logViewActivity, logViewActivity.getString(C0380R.string.msg_logview_export_completed, new Object[]{(String) message.obj}), 1).show();
                }
                LogViewActivity logViewActivity2 = LogViewActivity.this;
                logViewActivity2.mLogSavingRunnable = null;
                logViewActivity2.mRlayLoadingPanel.setVisibility(8);
                return true;
            }
        });
        this.mRlayLoadingPanel.setVisibility(0);
        this.mLogSavingRunnable = new LogSavingRunnable(this, handler, this.mLogManagement, this.mPlayerCharsForId, this.mAdventureLogs.get(0));
        this.mLogSavingThread = new Thread(this.mLogSavingRunnable);
        this.mLogSavingThread.start();
    }
}
