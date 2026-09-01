package com.shirobakama.autorpg2;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.measurement.api.AppMeasurementSdk;
import com.shirobakama.autorpg2.AlertDialogFragment;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.view.LabelValueItem;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class CharacterSelectDialogFragment extends AlertDialogFragment {

    public interface OnCharacterSelectListener {
        void onCharacterSelect(int i, int i2, int i3, int i4);
    }

    public static CharacterSelectDialogFragment show(FragmentActivity fragmentActivity, List<PlayerChar> list, int i, int i2, int i3) {
        ArrayList<LabelValueItem> arrayListCreateList = LabelValueItem.createList(list, new LabelValueItem.ItemCreator<PlayerChar>() { // from class: com.shirobakama.autorpg2.CharacterSelectDialogFragment.1
            @Override // com.shirobakama.autorpg2.view.LabelValueItem.ItemCreator
            public LabelValueItem create(PlayerChar playerChar) {
                return new LabelValueItem(playerChar.f106id, playerChar.name);
            }
        });
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(C0380R.string.msg_dlg_title_select_character).setCancelable(true);
        if (i3 != -1) {
            decorator.setPositiveText(i3);
        }
        decorator.setNegativeText(0);
        decorator.setLabelValueItems(arrayListCreateList);
        decorator.args().putInt("dialog_type", i);
        decorator.args().putInt(AppMeasurementSdk.ConditionalUserProperty.VALUE, i2);
        AlertDialogFragment alertDialogFragmentDecorate = decorator.decorate(new CharacterSelectDialogFragment());
        alertDialogFragmentDecorate.show(fragmentActivity.getSupportFragmentManager());
        return (CharacterSelectDialogFragment) alertDialogFragmentDecorate;
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    protected void onClick(DialogInterface dialogInterface, int i) {
        int i2;
        switch (i) {
            case -1:
                i2 = 0;
                break;
            case 0:
                i2 = this.items.get(this.whichItem).value;
                break;
            default:
                return;
        }
        ((OnCharacterSelectListener) getActivity()).onCharacterSelect(i, i2, getArguments().getInt("dialog_type"), getArguments().getInt(AppMeasurementSdk.ConditionalUserProperty.VALUE));
    }
}
