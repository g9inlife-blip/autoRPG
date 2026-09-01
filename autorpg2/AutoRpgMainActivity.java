package com.shirobakama.autorpg2;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.LogManagement;
import com.shirobakama.autorpg2.entity.Stock;
import com.shirobakama.autorpg2.p001db.Persister;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.autorpg2.util.NotificationReceiver;
import com.shirobakama.logquest2.BuildConfig;
import com.shirobakama.logquest2.C0380R;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class AutoRpgMainActivity extends Activity {
    public static final long FORCE_RETURN_PERIOD_MS = 86400000;
    private static final String PREFS_NAME = "ex_prefs";
    private static final String PREF_KEY_TYPE = "type";
    private static final int REQUEST_STORY = 1;
    static final String STATE_GAME = "game";
    protected static final String TAG = "arpg-main";
    private GameContext mGame;

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            Class.forName("android.os.AsyncTask");
        } catch (Throwable unused) {
        }
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        DeviceUtil.setLocaleIfNeeded(this, defaultSharedPreferences.getString(getString(C0380R.string.pref_key_language), BuildConfig.FLAVOR));
        setResult(0);
        Persister persister = new Persister(this);
        this.mGame = readOrInitializeGameContext(this, persister);
        if (defaultSharedPreferences.getBoolean(getString(C0380R.string.pref_key_notification), false) && defaultSharedPreferences.getBoolean(getString(C0380R.string.pref_key_notification_remove), false)) {
            NotificationReceiver.removeCurrentNotification(this);
        }
        AdventureProcessor.removeAdventuringNotification(this);
        List<LogManagement> logManagements = persister.readLogManagements();
        for (LogManagement logManagement : logManagements) {
            if (logManagement != null && !logManagement.completed) {
                persister.removeIllegalLogs(logManagement);
            }
        }
        checkRebootingInOfflineTimeChecking(persister, logManagements);
        if (showExceptionRecoverDialog()) {
            return;
        }
        if (defaultSharedPreferences.getBoolean(getString(C0380R.string.pref_key_booted), false)) {
            moveToTown();
        } else {
            defaultSharedPreferences.edit().putBoolean(getString(C0380R.string.pref_key_booted), true).apply();
            showStory();
        }
    }

    private void checkRebootingInOfflineTimeChecking(Persister persister, List<LogManagement> list) {
        if (list == null || list.isEmpty() || !AutoRpgPrefsActivity.isOfflineTimeChecking(this)) {
            return;
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        if (this.mGame.returnRealtime <= jElapsedRealtime) {
            return;
        }
        long j = this.mGame.returnRealtime - this.mGame.startRealtime;
        if (j > FORCE_RETURN_PERIOD_MS) {
            j = 86400000;
        }
        if (j < jElapsedRealtime) {
            GameContext gameContext = this.mGame;
            gameContext.startRealtime = 0L;
            gameContext.returnRealtime = j;
            persister.writeOnAdventureCompleted(gameContext, null);
            return;
        }
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(new Date().getTime() - jElapsedRealtime);
        calendar.setTime(date);
        calendar.set(13, 0);
        calendar.set(14, 0);
        calendar.add(12, 1);
        Date time = calendar.getTime();
        long time2 = time.getTime() - date.getTime();
        long time3 = time.getTime() - this.mGame.startTime.getTime();
        if (time3 <= 0) {
            return;
        }
        Date date2 = new Date(this.mGame.returnTime.getTime() + time3);
        Date date3 = new Date(this.mGame.estimateTime.getTime() + time3);
        GameContext gameContext2 = this.mGame;
        gameContext2.startTime = time;
        gameContext2.returnTime = date2;
        gameContext2.estimateTime = date3;
        gameContext2.startRealtime = 60000 - time2;
        gameContext2.returnRealtime = gameContext2.startRealtime + j;
        persister.writeOnAdventureCompleted(this.mGame, null);
        persister.rewindLogs(this.mGame, list.get(list.size() - 1), time, j);
    }

    static GameContext readOrInitializeGameContext(Activity activity, Persister persister) {
        GameContext gameContext = persister.readGameContext();
        if (gameContext != null) {
            Stock.sortByTypeAndName(activity, gameContext.stocks);
            if (gameContext.dungeonContext == null || gameContext.dungeonContext.stats == null || gameContext.dungeonContext.stats.isEmpty()) {
                gameContext.dungeonId = 0;
            }
        } else {
            gameContext = GameContext.initialize();
            try {
                persister.writeGameDungeonContext(gameContext);
            } catch (SQLException e) {
                DeviceUtil.handleSqliteException(activity, e);
                return null;
            }
        }
        return gameContext;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveToTown() {
        GameContext.game = this.mGame;
        this.mGame = null;
        startActivity(new Intent(getApplicationContext(), (Class<?>) TownActivity.class));
        finish();
    }

    private void showStory() {
        Intent intent = new Intent(getApplicationContext(), (Class<?>) StoryActivity.class);
        intent.putExtra(StoryActivity.EXTRA_STORY_TYPE, 1);
        startActivityForResult(intent, 1);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1) {
            moveToTown();
        }
    }

    public static void recordRecoverableExceptionOccurred(Activity activity, Persister.PlayerUpdateFailedException playerUpdateFailedException) {
        activity.getApplicationContext().getSharedPreferences(PREFS_NAME, 0).edit().putString(PREF_KEY_TYPE, playerUpdateFailedException.getClass().getSimpleName()).commit();
    }

    public boolean showExceptionRecoverDialog() {
        if (getApplicationContext().getSharedPreferences(PREFS_NAME, 0).getString(PREF_KEY_TYPE, null) == null) {
            return false;
        }
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.shirobakama.autorpg2.AutoRpgMainActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                new Persister(AutoRpgMainActivity.this).recoverDataError();
                AutoRpgMainActivity.this.getApplicationContext().getSharedPreferences(AutoRpgMainActivity.PREFS_NAME, 0).edit().remove(AutoRpgMainActivity.PREF_KEY_TYPE).commit();
                Toast.makeText(AutoRpgMainActivity.this, C0380R.string.msg_error_recovered, 0).show();
                AutoRpgMainActivity.this.finish();
            }
        };
        new AlertDialog.Builder(this).setTitle(C0380R.string.msg_dlg_title_confirm_error_recovery).setMessage(C0380R.string.msg_dlg_confirm_error_recovery).setPositiveButton(R.string.ok, onClickListener).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.shirobakama.autorpg2.AutoRpgMainActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                AutoRpgMainActivity.this.getApplicationContext().getSharedPreferences(AutoRpgMainActivity.PREFS_NAME, 0).edit().remove(AutoRpgMainActivity.PREF_KEY_TYPE).commit();
                AutoRpgMainActivity.this.moveToTown();
            }
        }).create().show();
        return true;
    }
}
