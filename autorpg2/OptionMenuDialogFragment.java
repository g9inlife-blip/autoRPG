package com.shirobakama.autorpg2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import com.shirobakama.autorpg2.view.LabelValueItem;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class OptionMenuDialogFragment extends AlertDialogFragment {

    public interface Callback {
        void selectMenu(Bundle bundle, int i, List<LabelValueItem> list);
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment, android.support.v4.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Dialog dialogOnCreateDialog = super.onCreateDialog(bundle);
        dialogOnCreateDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.shirobakama.autorpg2.OptionMenuDialogFragment.1
            @Override // android.content.DialogInterface.OnKeyListener
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i != 82 || keyEvent.getAction() != 1) {
                    return false;
                }
                dialogInterface.dismiss();
                return true;
            }
        });
        return dialogOnCreateDialog;
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    protected void onClick(DialogInterface dialogInterface, int i) {
        if (i == 0) {
            ((Callback) getActivity()).selectMenu(getArguments(), this.whichItem, this.items);
        }
    }
}
