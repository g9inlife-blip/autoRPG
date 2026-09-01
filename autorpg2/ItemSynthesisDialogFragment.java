package com.shirobakama.autorpg2;

import android.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.shirobakama.autorpg2.adventure.TownFlagEngine;
import com.shirobakama.autorpg2.entity.Inventory;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class ItemSynthesisDialogFragment extends AlertDialogFragment implements AdapterView.OnItemSelectedListener {
    private Inventory mCatalystInventory;
    private CheckBox mChkSynthesisUseCatalyst;
    private Spinner mSpnSynthesisItem;
    private Spinner mSpnSynthesisTarget;
    private List<Inventory> mSyntheticInventories;
    private TextView mTvSynthesisResult;

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    @SuppressLint({"InflateParams"})
    protected View getAlertDialogView() {
        View viewInflate = ((TownActivity) getActivity()).getCurrentLayoutInflater().inflate(C0380R.layout.item_synthesis_dialog, (ViewGroup) null);
        this.mSpnSynthesisTarget = (Spinner) viewInflate.findViewById(C0380R.id.spnSynthesisTarget);
        this.mSpnSynthesisItem = (Spinner) viewInflate.findViewById(C0380R.id.spnSynthesisItem);
        this.mChkSynthesisUseCatalyst = (CheckBox) viewInflate.findViewById(C0380R.id.chkSynthesisUseCatalyst);
        this.mTvSynthesisResult = (TextView) viewInflate.findViewById(C0380R.id.tvSynthesisResult);
        TownActivity townActivity = (TownActivity) getActivity();
        this.mSyntheticInventories = new ArrayList();
        this.mCatalystInventory = null;
        for (Inventory inventory : townActivity.game.inventories) {
            Item baseItem = inventory.getBaseItem(townActivity);
            if (!baseItem.artifact) {
                if (baseItem.equipable) {
                    this.mSyntheticInventories.add(inventory);
                } else if (this.mCatalystInventory == null && baseItem.f97id == 5160) {
                    this.mCatalystInventory = inventory;
                }
            }
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(townActivity, R.layout.simple_spinner_item, convertInventoriesToNameArray(this.mSyntheticInventories, townActivity));
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        this.mSpnSynthesisTarget.setAdapter((SpinnerAdapter) arrayAdapter);
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(townActivity, R.layout.simple_spinner_item, convertInventoriesToNameArray(this.mSyntheticInventories, townActivity));
        arrayAdapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        this.mSpnSynthesisItem.setAdapter((SpinnerAdapter) arrayAdapter2);
        this.mChkSynthesisUseCatalyst.setChecked(false);
        this.mChkSynthesisUseCatalyst.setEnabled(this.mCatalystInventory != null);
        this.mSpnSynthesisTarget.setOnItemSelectedListener(this);
        this.mSpnSynthesisItem.setOnItemSelectedListener(this);
        this.mChkSynthesisUseCatalyst.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.shirobakama.autorpg2.ItemSynthesisDialogFragment.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                ItemSynthesisDialogFragment.this.onItemSelected(null, compoundButton, 0, 0L);
            }
        });
        return viewInflate;
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        Inventory targetInventory = getTargetInventory();
        Inventory syntheticInventory = getSyntheticInventory();
        if (targetInventory != null && syntheticInventory != null && targetInventory.f98id == syntheticInventory.f98id) {
            this.mTvSynthesisResult.setText(C0380R.string.lbl_synthesis_cannot_synthesize_same_item);
        } else {
            Inventory inventorySynthesizeItem = TownFlagEngine.synthesizeItem(getActivity(), targetInventory, syntheticInventory, this.mChkSynthesisUseCatalyst.isChecked());
            this.mTvSynthesisResult.setText(inventorySynthesizeItem == null ? getString(C0380R.string.lbl_synthesis_cannot_synthesize) : inventorySynthesizeItem.getName(getActivity()));
        }
    }

    private Inventory getSyntheticInventory() {
        if (this.mSyntheticInventories.isEmpty()) {
            return null;
        }
        return this.mSyntheticInventories.get(this.mSpnSynthesisItem.getSelectedItemPosition());
    }

    private Inventory getTargetInventory() {
        if (this.mSyntheticInventories.isEmpty()) {
            return null;
        }
        return this.mSyntheticInventories.get(this.mSpnSynthesisTarget.getSelectedItemPosition());
    }

    private String[] convertInventoriesToNameArray(List<Inventory> list, Context context) {
        String[] strArr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            strArr[i] = list.get(i).getName(context);
        }
        return strArr;
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    protected void onClick(DialogInterface dialogInterface, int i) {
        if (i != -1) {
            return;
        }
        ((TownActivity) getActivity()).confirmSynthesis(getTargetInventory(), getSyntheticInventory(), this.mChkSynthesisUseCatalyst.isChecked());
    }
}
