package com.shirobakama.autorpg2;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import com.shirobakama.logquest2.C0380R;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class TownChangeDummyProcessor implements ProgressProcessor {
    private static final long SLEEP_MS = 5000;
    protected TownActivity mActivity;
    private boolean mCancelled = false;
    private AsyncTask<Void, Void, Void> mDummyTask;

    @Override // com.shirobakama.autorpg2.ProgressProcessor
    public int getMessageWhenCancelled() {
        return 0;
    }

    public TownChangeDummyProcessor(TownActivity townActivity) {
        this.mActivity = townActivity;
    }

    public void run(final Handler.Callback callback) {
        this.mDummyTask = new AsyncTask<Void, Void, Void>() { // from class: com.shirobakama.autorpg2.TownChangeDummyProcessor.1
            @Override // android.os.AsyncTask
            protected void onPreExecute() {
                synchronized (TownChangeDummyProcessor.this.mActivity) {
                    TownChangeDummyProcessor.this.mActivity.dlgProgress = ProgressDialog.show(TownChangeDummyProcessor.this.mActivity, TownChangeDummyProcessor.this.mActivity.getString(C0380R.string.msg_dlg_title_changing_town), TownChangeDummyProcessor.this.mActivity.getString(C0380R.string.msg_dlg_changing_town), true);
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Void r3) {
                synchronized (TownChangeDummyProcessor.this.mActivity) {
                    if (TownChangeDummyProcessor.this.mActivity.dlgProgress != null) {
                        if (TownChangeDummyProcessor.this.mActivity.dlgProgress.isShowing()) {
                            TownChangeDummyProcessor.this.mActivity.dlgProgress.dismiss();
                        }
                        TownChangeDummyProcessor.this.mActivity.dlgProgress = null;
                    }
                }
                callback.handleMessage(null);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... voidArr) throws InterruptedException {
                TownChangeDummyProcessor.this.doMainProcess();
                return null;
            }
        };
        this.mDummyTask.execute(new Void[0]);
    }

    protected void doMainProcess() throws InterruptedException {
        for (int i = 0; i < 100 && !this.mCancelled; i++) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException unused) {
            }
        }
    }

    @Override // com.shirobakama.autorpg2.ProgressProcessor
    public void cancel() {
        AsyncTask<Void, Void, Void> asyncTask = this.mDummyTask;
        if (asyncTask != null) {
            this.mCancelled = true;
            asyncTask.cancel(false);
        }
    }
}
