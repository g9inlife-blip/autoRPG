package com.shirobakama.autorpg2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.shirobakama.autorpg2.adventure.AdventureEngine;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.repo.DungeonRepository;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.autorpg2.util.FormatUtil;
import com.shirobakama.autorpg2.util.HttpTimeClient;
import com.shirobakama.autorpg2.util.SntpClient;
import com.shirobakama.logquest2.C0380R;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class GoToAdventureConfirmDialogFragment extends AlertDialogFragment {
    protected static final int MSG_DATE_RETRIEVED = 1;
    private static final String STATE_CONFIRMED = "state-confirmed";
    private static final String STATE_DEVICE_DATE = "state-device-date";
    private static final String STATE_ELAPSED_TIME = "state-elapsed-time";
    private static final String STATE_NTP_TRIED = "state-ntp-tried";
    protected static final String TAG = "goto-confirm";
    private Date mDeviceDateOnReceive;
    private long mElapsedTime;
    protected Handler mHandler;
    private boolean mOfflineTimeChecking;
    private Button mPositiveButton;
    private TimeRetrieveRunnable mTimeRetrieveRunnable;
    private TextView mTvTimeChecking;
    private boolean mNtpTried = false;
    private boolean mConfirmed = false;

    private static class TimeRetrieveRunnable implements Runnable {
        private Activity mActivity;
        private boolean mDestroyed;
        private Handler mHandler;

        public TimeRetrieveRunnable(Activity activity, Handler handler) {
            this.mActivity = activity;
            this.mHandler = handler;
        }

        @Override // java.lang.Runnable
        public void run() throws Throwable {
            Date dateExecute = null;
            for (String str : this.mActivity.getResources().getStringArray(C0380R.array.ntp_hosts)) {
                dateExecute = new SntpClient(str).execute();
                if (dateExecute != null || this.mDestroyed) {
                    break;
                }
            }
            if (dateExecute == null && !this.mDestroyed) {
                dateExecute = new HttpTimeClient(this.mActivity.getString(C0380R.string.res_http_time_url)).execute();
            }
            if (this.mDestroyed) {
                return;
            }
            this.mHandler.sendMessage(Message.obtain(this.mHandler, 1, dateExecute));
        }

        public void destroy() {
            this.mDestroyed = true;
        }
    }

    @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mOfflineTimeChecking = AutoRpgPrefsActivity.isOfflineTimeChecking(getActivity());
        this.mHandler = new Handler(new Handler.Callback() { // from class: com.shirobakama.autorpg2.GoToAdventureConfirmDialogFragment.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message message) {
                if (message.what != 1) {
                    return false;
                }
                GoToAdventureConfirmDialogFragment.this.dateRetrieved((Date) message.obj);
                return true;
            }
        });
        if (bundle != null) {
            this.mNtpTried = bundle.getBoolean(STATE_NTP_TRIED);
            this.mConfirmed = bundle.getBoolean(STATE_CONFIRMED);
            long j = bundle.getLong(STATE_DEVICE_DATE);
            this.mDeviceDateOnReceive = j < 0 ? null : new Date(j);
            this.mElapsedTime = bundle.getLong(STATE_ELAPSED_TIME);
        }
    }

    @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(STATE_NTP_TRIED, this.mNtpTried);
        bundle.putBoolean(STATE_CONFIRMED, this.mConfirmed);
        Date date = this.mDeviceDateOnReceive;
        bundle.putLong(STATE_DEVICE_DATE, date == null ? -1L : date.getTime());
        bundle.putLong(STATE_ELAPSED_TIME, this.mElapsedTime);
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment, android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        this.mPositiveButton = ((AlertDialog) getDialog()).getButton(-1);
        if (this.mOfflineTimeChecking) {
            boolean z = ((TownActivity) getActivity()).game.returnRealtime < SystemClock.elapsedRealtime();
            this.mPositiveButton.setEnabled(z);
            if (z) {
                this.mConfirmed = true;
                this.mDeviceDateOnReceive = new Date();
                this.mElapsedTime = SystemClock.elapsedRealtime();
                this.mTvTimeChecking.setText(C0380R.string.msg_dlg_confirm_go_to_adventure_time_offline_checking_ok);
                return;
            }
            ((TownActivity) getActivity()).addMessage(getString(C0380R.string.msg_device_time_is_illegal));
            dismiss();
            return;
        }
        if (this.mNtpTried) {
            this.mPositiveButton.setEnabled(true);
            return;
        }
        this.mPositiveButton.setEnabled(false);
        if (!DeviceUtil.isNetworkConnected(getActivity())) {
            dateRetrieved(null);
        } else {
            this.mTimeRetrieveRunnable = new TimeRetrieveRunnable(getActivity(), this.mHandler);
            new Thread(this.mTimeRetrieveRunnable).start();
        }
    }

    @Override // android.support.v4.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        TimeRetrieveRunnable timeRetrieveRunnable = this.mTimeRetrieveRunnable;
        if (timeRetrieveRunnable != null) {
            timeRetrieveRunnable.destroy();
        }
        this.mNtpTried = false;
        this.mConfirmed = false;
    }

    protected void dateRetrieved(Date date) {
        this.mNtpTried = true;
        if (date == null) {
            GameContext gameContext = ((TownActivity) getActivity()).game;
            if (gameContext.advCount > 0) {
                this.mTvTimeChecking.setText(getString(C0380R.string.msg_dlg_confirm_go_to_adventure_time_warning, Integer.valueOf(gameContext.advCount)));
                this.mTvTimeChecking.setGravity(3);
                this.mConfirmed = false;
            } else {
                ((TownActivity) getActivity()).addMessage(getString(C0380R.string.msg_time_checking_failed));
                dismiss();
                return;
            }
        } else if (Math.abs(date.getTime() - System.currentTimeMillis()) > GameContext.MAX_TIME_RANGE_TS) {
            ((TownActivity) getActivity()).addMessage(getString(C0380R.string.msg_device_time_is_illegal));
            dismiss();
            return;
        } else {
            this.mTvTimeChecking.setText(C0380R.string.msg_dlg_confirm_go_to_adventure_time_checking_ok);
            if (getActivity() != null) {
                this.mTvTimeChecking.setTextColor(getActivity().getResources().getColor(C0380R.color.message_weak));
            }
            this.mConfirmed = true;
        }
        this.mDeviceDateOnReceive = new Date();
        this.mElapsedTime = SystemClock.elapsedRealtime();
        Button button = this.mPositiveButton;
        if (button != null) {
            button.setEnabled(true);
        }
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    @SuppressLint({"InflateParams"})
    protected View getAlertDialogView() {
        TownActivity townActivity = (TownActivity) getActivity();
        View viewInflate = townActivity.layoutInflater.inflate(C0380R.layout.confirm_adventure_dialog, (ViewGroup) null);
        int iEstimateTimeInMinutesStatic = AdventureEngine.estimateTimeInMinutesStatic(townActivity, townActivity.game);
        Calendar calendar = Calendar.getInstance();
        calendar.add(12, iEstimateTimeInMinutesStatic);
        boolean z = true;
        String string = getString(C0380R.string.msg_dlg_confirm_destination, DungeonRepository.getDungeon(townActivity, townActivity.game.dungeonId).name, Integer.valueOf(townActivity.game.targetFloor));
        ((TextView) viewInflate.findViewById(C0380R.id.tvReturnTime)).setText(FormatUtil.formatTimeToHhmm(calendar.getTime()));
        ((TextView) viewInflate.findViewById(C0380R.id.tvDestination)).setText(string);
        this.mTvTimeChecking = (TextView) viewInflate.findViewById(C0380R.id.tvTimeChecking);
        this.mTvTimeChecking.setText(C0380R.string.msg_dlg_confirm_go_to_adventure_time_checking);
        StringBuilder sb = new StringBuilder();
        Iterator<PlayerChar> it = townActivity.game.characters.iterator();
        while (true) {
            if (!it.hasNext()) {
                z = false;
                break;
            }
            if (it.next().getAvailableSkillIds().isEmpty()) {
                break;
            }
        }
        if (z) {
            sb.append(getString(C0380R.string.msg_dlg_confirm_go_to_adventure_warn_skill_empty));
        }
        TextView textView = (TextView) viewInflate.findViewById(C0380R.id.tvAdventureWarning);
        if (sb.length() == 0) {
            textView.setVisibility(8);
        } else {
            textView.setVisibility(0);
            textView.setText(sb.toString());
        }
        return viewInflate;
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    protected void onClick(DialogInterface dialogInterface, int i) {
        if (i != -1) {
            return;
        }
        ((TownActivity) getActivity()).edgeOfTown.goToAdventure(this.mDeviceDateOnReceive, this.mElapsedTime, this.mConfirmed);
    }
}
