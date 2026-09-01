package com.shirobakama.autorpg2;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import com.shirobakama.autorpg2.view.LabelValueItem;
import java.util.ArrayList;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public abstract class AlertDialogFragment extends DialogFragment {
    private static final String ALERT_DIALOG_FIXED_TAG = "alert-dialog";
    public static final int WHICH_BUTTON_ITEM_SELECTED = 0;
    private static boolean dialogShowing = false;
    private static Object lock = new Object();
    protected List<LabelValueItem> items;
    protected int whichItem = -1;

    public static class SimpleDialogFragment extends AlertDialogFragment {
        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        protected void onClick(DialogInterface dialogInterface, int i) {
        }
    }

    protected View getAlertDialogView() {
        return null;
    }

    protected abstract void onClick(DialogInterface dialogInterface, int i);

    protected void prepareAfterBuilding(AlertDialog alertDialog) {
    }

    protected void prepareBuilderOnCreateDialog(AlertDialog.Builder builder) {
    }

    public static class Decorator {
        private Bundle mArgs = new Bundle();

        public static Decorator confirmationDecorator(int i) {
            return new Decorator().setMessage(i).setPositiveText(0).setNegativeText(0);
        }

        public static Decorator confirmationDecorator(int i, int i2) {
            return new Decorator().setTitle(i).setMessage(i2).setPositiveText(0).setNegativeText(0);
        }

        public AlertDialogFragment decorate(AlertDialogFragment alertDialogFragment) {
            alertDialogFragment.setArguments(this.mArgs);
            return alertDialogFragment;
        }

        public Decorator setTitle(int i) {
            this.mArgs.putInt("title_id", i);
            return this;
        }

        public Decorator setTitle(String str) {
            this.mArgs.putString("title", str);
            return this;
        }

        public Decorator setMessage(String str) {
            this.mArgs.putString("message", str);
            return this;
        }

        public Decorator setMessage(int i) {
            this.mArgs.putInt("message_id", i);
            return this;
        }

        public Decorator setLabelValueItems(ArrayList<LabelValueItem> arrayList) {
            this.mArgs.putParcelableArrayList("items", arrayList);
            return this;
        }

        public Decorator setPositiveText(int i) {
            this.mArgs.putInt("positive_text", i);
            return this;
        }

        public Decorator setNegativeText(int i) {
            this.mArgs.putInt("negative_text", i);
            return this;
        }

        public Decorator setNeutralText(int i) {
            this.mArgs.putInt("neutral_text", i);
            return this;
        }

        public Decorator setCancelable(boolean z) {
            this.mArgs.putBoolean("cancelable", z);
            return this;
        }

        public Bundle args() {
            return this.mArgs;
        }

        public Decorator setChoiceMode(int i) {
            this.mArgs.putInt("choice_mode", i);
            return this;
        }
    }

    @Override // android.support.v4.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.shirobakama.autorpg2.AlertDialogFragment.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (AlertDialogFragment.this.getActivity() != null) {
                    AlertDialogFragment.this.onClick(dialogInterface, i);
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle arguments = getArguments();
        if (arguments.containsKey("cancelable")) {
            setCancelable(arguments.getBoolean("cancelable"));
        }
        if (arguments.containsKey("title")) {
            builder.setTitle(arguments.getString("title"));
        } else if (arguments.containsKey("title_id")) {
            builder.setTitle(arguments.getInt("title_id"));
        }
        if (arguments.containsKey("message")) {
            builder.setMessage(arguments.getString("message"));
        } else if (arguments.containsKey("message_id")) {
            builder.setMessage(arguments.getInt("message_id"));
        }
        if (arguments.containsKey("positive_text")) {
            int i = arguments.getInt("positive_text");
            if (i == 0) {
                i = R.string.ok;
            }
            builder.setPositiveButton(i, onClickListener);
        }
        if (arguments.containsKey("negative_text")) {
            int i2 = arguments.getInt("negative_text");
            if (i2 == 0) {
                i2 = R.string.cancel;
            }
            builder.setNegativeButton(i2, onClickListener);
        }
        if (arguments.containsKey("neutral_text")) {
            builder.setNeutralButton(arguments.getInt("neutral_text"), onClickListener);
        }
        if (arguments.containsKey("items")) {
            this.items = LabelValueItem.castParcelableListToLabelValueItemList(arguments.getParcelableArrayList("items"));
            CharSequence[] labelCharSequences = LabelValueItem.toLabelCharSequences(this.items);
            int i3 = arguments.containsKey("choice_mode") ? arguments.getInt("choice_mode") : 0;
            DialogInterface.OnClickListener onClickListener2 = new DialogInterface.OnClickListener() { // from class: com.shirobakama.autorpg2.AlertDialogFragment.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i4) {
                    if (AlertDialogFragment.this.getActivity() != null) {
                        AlertDialogFragment alertDialogFragment = AlertDialogFragment.this;
                        alertDialogFragment.whichItem = i4;
                        alertDialogFragment.onClick(dialogInterface, 0);
                    }
                }
            };
            if (i3 == 0) {
                builder.setItems(labelCharSequences, onClickListener2);
            } else if (i3 == 1) {
                builder.setSingleChoiceItems(labelCharSequences, -1, onClickListener2);
            }
        }
        View alertDialogView = getAlertDialogView();
        if (alertDialogView != null) {
            builder.setView(alertDialogView);
        }
        prepareBuilderOnCreateDialog(builder);
        AlertDialog alertDialogCreate = builder.create();
        prepareAfterBuilding(alertDialogCreate);
        return alertDialogCreate;
    }

    public void show(FragmentManager fragmentManager) {
        synchronized (lock) {
            if (dialogShowing) {
                return;
            }
            dialogShowing = true;
            dismissPreviousDialog(fragmentManager, ALERT_DIALOG_FIXED_TAG);
            super.show(fragmentManager, ALERT_DIALOG_FIXED_TAG);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        synchronized (lock) {
            dialogShowing = false;
        }
    }

    private void dismissPreviousDialog(FragmentManager fragmentManager, String str) {
        Dialog dialog;
        AlertDialogFragment alertDialogFragment = (AlertDialogFragment) fragmentManager.findFragmentByTag(str);
        if (alertDialogFragment == null || (dialog = alertDialogFragment.getDialog()) == null || !dialog.isShowing()) {
            return;
        }
        alertDialogFragment.dismiss();
    }
}
