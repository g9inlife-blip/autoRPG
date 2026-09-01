package com.shirobakama.autorpg2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.shirobakama.autorpg2.adventure.AdventureEngine;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.logquest2.C0380R;
import java.util.Date;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class AdventureProcessor implements ProgressProcessor {
    protected static final String TAG = "adv-proc";
    protected TownActivity mActivity;
    private AdventureEngine mAdventureEngine;
    private AsyncTask<Void, Void, SQLException> mAdventureTask;
    private GameContext mGame;
    private int mTargetFloor;
    private boolean mTimeChecked;

    @Override // com.shirobakama.autorpg2.ProgressProcessor
    public int getMessageWhenCancelled() {
        return C0380R.string.msg_adventure_cancelled;
    }

    public AdventureProcessor(GameContext gameContext, TownActivity townActivity, int i, boolean z) {
        this.mGame = gameContext;
        this.mActivity = townActivity;
        this.mTargetFloor = i;
        this.mTimeChecked = z;
    }

    public void run(final Handler.Callback callback) {
        if (this.mGame.returnTime != null && this.mGame.returnTime.compareTo(new Date()) >= 0) {
            Toast.makeText(this.mActivity, C0380R.string.msg_illegal_timestamp, 1).show();
        } else {
            this.mAdventureTask = new AsyncTask<Void, Void, SQLException>() { // from class: com.shirobakama.autorpg2.AdventureProcessor.1
                @Override // android.os.AsyncTask
                protected void onPreExecute() {
                    synchronized (AdventureProcessor.this.mActivity) {
                        AdventureProcessor.this.mActivity.dlgProgress = ProgressDialog.show(AdventureProcessor.this.mActivity, AdventureProcessor.this.mActivity.getString(C0380R.string.msg_dlg_title_exploring), AdventureProcessor.this.mActivity.getString(C0380R.string.msg_dlg_exploring), true);
                    }
                    AdventureProcessor.this.notifyAdventuring();
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(SQLException sQLException) {
                    synchronized (AdventureProcessor.this.mActivity) {
                        if (AdventureProcessor.this.mActivity.dlgProgress != null) {
                            if (AdventureProcessor.this.mActivity.dlgProgress.isShowing()) {
                                AdventureProcessor.this.mActivity.dlgProgress.dismiss();
                            }
                            AdventureProcessor.this.mActivity.dlgProgress = null;
                        }
                    }
                    AdventureProcessor.removeAdventuringNotification(AdventureProcessor.this.mActivity);
                    if (sQLException != null) {
                        DeviceUtil.handleSqliteException(AdventureProcessor.this.mActivity, sQLException);
                    }
                    callback.handleMessage(null);
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public SQLException doInBackground(Void... voidArr) throws SQLException {
                    AdventureProcessor.this.doMainProcess();
                    return null;
                }
            };
            this.mAdventureTask.execute(new Void[0]);
        }
    }

    protected void notifyAdventuring() {
        String string = this.mActivity.getString(C0380R.string.msg_notification_title_exploring);
        String string2 = this.mActivity.getString(C0380R.string.msg_notification_content_exploring);
        NotificationManager notificationManager = (NotificationManager) this.mActivity.getSystemService("notification");
        PendingIntent activity = PendingIntent.getActivity(this.mActivity, 0, new Intent(this.mActivity, (Class<?>) TownActivity.class), 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.mActivity);
        builder.setAutoCancel(false);
        builder.setSmallIcon(C0380R.drawable.ic_status_lq2);
        builder.setTicker(string);
        builder.setContentTitle(string);
        builder.setContentText(string2);
        builder.setOngoing(true);
        builder.setContentIntent(activity);
        builder.setWhen(System.currentTimeMillis());
        notificationManager.notify(2, builder.build());
    }

    public static void removeAdventuringNotification(Context context) {
        ((NotificationManager) context.getSystemService("notification")).cancel(2);
    }

    protected void doMainProcess() throws SQLException {
        this.mAdventureEngine = new AdventureEngine(this.mActivity.getApplicationContext());
        this.mAdventureEngine.execute(this.mGame, this.mTargetFloor, this.mTimeChecked);
        this.mAdventureEngine = null;
    }

    @Override // com.shirobakama.autorpg2.ProgressProcessor
    public void cancel() {
        AsyncTask<Void, Void, SQLException> asyncTask = this.mAdventureTask;
        if (asyncTask == null || this.mAdventureEngine == null) {
            return;
        }
        asyncTask.cancel(false);
        this.mAdventureEngine.cancel();
        removeAdventuringNotification(this.mActivity);
    }
}
