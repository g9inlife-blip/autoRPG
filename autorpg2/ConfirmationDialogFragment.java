package com.shirobakama.autorpg2;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.measurement.api.AppMeasurementSdk;
import com.shirobakama.autorpg2.AlertDialogFragment;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class ConfirmationDialogFragment extends AlertDialogFragment {

    public interface OnConfirmationListener {
        void onConfirmationCancel(int i, int i2, int i3, Bundle bundle);

        void onConfirmationNeutral(int i, int i2, int i3, Bundle bundle);

        void onConfirmationOk(int i, int i2, int i3, Bundle bundle);
    }

    public static ConfirmationDialogFragment show(FragmentActivity fragmentActivity, String str, String str2, int i, int i2) {
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(str);
        decorator.setMessage(str2);
        return show(decorator, fragmentActivity, i, i2);
    }

    public static ConfirmationDialogFragment show(FragmentActivity fragmentActivity, int i, int i2, int i3, int i4) {
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(i);
        decorator.setMessage(i2);
        return show(decorator, fragmentActivity, i3, i4);
    }

    public static ConfirmationDialogFragment show(AlertDialogFragment.Decorator decorator, FragmentActivity fragmentActivity, int i, int i2) {
        decorator.setCancelable(true);
        decorator.setPositiveText(0).setNegativeText(0);
        decorator.args().putInt("tag", i);
        decorator.args().putInt(AppMeasurementSdk.ConditionalUserProperty.VALUE, i2);
        AlertDialogFragment alertDialogFragmentDecorate = decorator.decorate(new ConfirmationDialogFragment());
        alertDialogFragmentDecorate.show(fragmentActivity.getSupportFragmentManager());
        return (ConfirmationDialogFragment) alertDialogFragmentDecorate;
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    protected void onClick(DialogInterface dialogInterface, int i) {
        switch (i) {
            case -3:
                ((OnConfirmationListener) getActivity()).onConfirmationNeutral(this.whichItem, getArguments().getInt("tag"), getArguments().getInt(AppMeasurementSdk.ConditionalUserProperty.VALUE), getArguments());
                break;
            case -2:
                ((OnConfirmationListener) getActivity()).onConfirmationCancel(this.whichItem, getArguments().getInt("tag"), getArguments().getInt(AppMeasurementSdk.ConditionalUserProperty.VALUE), getArguments());
                break;
            case -1:
                ((OnConfirmationListener) getActivity()).onConfirmationOk(this.whichItem, getArguments().getInt("tag"), getArguments().getInt(AppMeasurementSdk.ConditionalUserProperty.VALUE), getArguments());
                break;
        }
    }
}
