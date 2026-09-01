package com.shirobakama.autorpg2.util;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.shirobakama.autorpg2.AlertDialogFragment;
import com.shirobakama.logquest2.BuildConfig;
import com.shirobakama.logquest2.C0380R;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class AboutDialogFragment extends AlertDialogFragment {
    public static final String ARGS_KEY_TOTAL_ADVENTURE_TIME = "total_adventure_time";

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    protected void onClick(DialogInterface dialogInterface, int i) {
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    @SuppressLint({"InflateParams"})
    protected View getAlertDialogView() {
        FragmentActivity activity = getActivity();
        PackageInfo packageInfo = DeviceUtil.getPackageInfo(activity);
        String str = packageInfo == null ? BuildConfig.FLAVOR : packageInfo.versionName;
        int i = getArguments().getInt("total_adventure_time", 0);
        View viewInflate = LayoutInflater.from(activity).inflate(C0380R.layout.about_dialog, (ViewGroup) null);
        ((TextView) viewInflate.findViewById(C0380R.id.tvAppName)).setText(getString(C0380R.string.msg_dlg_about_app_name, str));
        ((TextView) viewInflate.findViewById(C0380R.id.tvAppDescription)).setText(C0380R.string.msg_dlg_about_app_description);
        ((TextView) viewInflate.findViewById(C0380R.id.tvTotalAdventureTime)).setText(getString(C0380R.string.msg_dlg_about_total_time, Integer.valueOf(i / 60), Integer.valueOf(i % 60)));
        return viewInflate;
    }
}
