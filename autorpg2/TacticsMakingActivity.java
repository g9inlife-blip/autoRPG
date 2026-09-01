package com.shirobakama.autorpg2;

import android.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.Inventory;
import com.shirobakama.autorpg2.entity.Tactics;
import com.shirobakama.autorpg2.p001db.Persister;
import com.shirobakama.autorpg2.repo.ItemRepository;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.autorpg2.view.HelpDialogFragment;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class TacticsMakingActivity extends FragmentActivity implements View.OnClickListener {
    public static final String DIALOG_ARGS_BLOCK = "block";
    public static final String DIALOG_ARGS_CURRENT_ITEM_ID = "current_item_id";
    public static final String DIALOG_ARGS_FLOOR = "floor";
    public static final String DIALOG_ARGS_ITEM_IDS = "item_ids";
    private static final String EXTRA_TARGET_CHAR_ID = "target.char.id";
    public static final int NUMBER_OF_PATTERNS_FOR_CHAR = 3;
    private static final int REQUEST_ADVANCED_TACTICS = 1;
    private static final String STATE_TACTICS = "tactics";
    protected static final String TAG = "tactics-making";
    String[] floorStringsForSpinner;
    private Tactics mDefaultTactics;
    private GameContext mGame;
    private int[] mItemIds;
    private boolean mMovingToAnotherActivity;
    private int mNumberOfPatterns;
    private List<PatternViews> mPatterns;
    private ArrayList<Tactics> mTacticsList;
    private int mTargetCharId;
    private TextView mTvUseItem;

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
    }

    static class PatternViews {
        public Button btnActivate;
        public CheckBox chkEnabled;
        public Spinner spnAbort;
        public Spinner spnAttackSkill;
        public Spinner spnCureSkill;
        public Spinner spnDamageSkill;
        public Spinner spnFloor;
        public Spinner spnFullInventory;
        public Spinner spnItem;
        public Spinner spnRest;
        public Spinner spnRunning;
        public Spinner spnStatusSkill;
        public TableLayout tlayContainer;
        public TableRow trFloor;
        public TableRow trFullInventory;

        PatternViews() {
        }

        public void setEnabled(boolean z) {
            this.spnFloor.setEnabled(z);
            this.spnAbort.setEnabled(z);
            this.spnRunning.setEnabled(z);
            this.spnAttackSkill.setEnabled(z);
            this.spnStatusSkill.setEnabled(z);
            this.spnCureSkill.setEnabled(z);
            this.spnDamageSkill.setEnabled(z);
            this.spnItem.setEnabled(z);
            this.spnRest.setEnabled(z);
            this.spnFullInventory.setEnabled(z);
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        DeviceUtil.setLocaleIfNeeded(this, null);
        setContentView(C0380R.layout.tactics_making);
        DeviceUtil.handleOrientation(this, null);
        this.mTargetCharId = getIntent().getIntExtra(EXTRA_TARGET_CHAR_ID, 0);
        this.mNumberOfPatterns = isForCharacter() ? 3 : 1;
        this.floorStringsForSpinner = new String[20];
        int i = 0;
        while (true) {
            String[] strArr = this.floorStringsForSpinner;
            if (i >= strArr.length) {
                break;
            }
            int i2 = i + 1;
            strArr[i] = getString(C0380R.string.lbl_tactics_use_item_floor_value, new Object[]{Integer.valueOf(i2)});
            i = i2;
        }
        findViewById(C0380R.id.btnUseItem).setOnClickListener(this);
        findViewById(C0380R.id.btnOK).setOnClickListener(this);
        findViewById(C0380R.id.btnCancel).setOnClickListener(this);
        findViewById(C0380R.id.btnHelp).setOnClickListener(this);
        findViewById(C0380R.id.btnAdvancedTactics).setOnClickListener(this);
        this.mTvUseItem = (TextView) findViewById(C0380R.id.tvUseItem);
        FrameLayout frameLayout = (FrameLayout) findViewById(C0380R.id.flayPatterns);
        this.mPatterns = new ArrayList(this.mNumberOfPatterns);
        for (final int i3 = 0; i3 < this.mNumberOfPatterns; i3++) {
            PatternViews patternViews = new PatternViews();
            switch (i3) {
                case 0:
                    patternViews.btnActivate = (Button) findViewById(C0380R.id.btnPattern0);
                    break;
                case 1:
                    patternViews.btnActivate = (Button) findViewById(C0380R.id.btnPattern1);
                    break;
                case 2:
                    patternViews.btnActivate = (Button) findViewById(C0380R.id.btnPattern2);
                    break;
            }
            patternViews.btnActivate.setOnClickListener(new View.OnClickListener() { // from class: com.shirobakama.autorpg2.TacticsMakingActivity.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    TacticsMakingActivity.this.onActivatePattern(i3);
                }
            });
            patternViews.tlayContainer = (TableLayout) getLayoutInflater().inflate(C0380R.layout.tactics_pattern, (ViewGroup) frameLayout, false);
            patternViews.trFloor = (TableRow) patternViews.tlayContainer.findViewById(C0380R.id.trFloor);
            patternViews.chkEnabled = (CheckBox) patternViews.tlayContainer.findViewById(C0380R.id.chkEnabled);
            patternViews.chkEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.shirobakama.autorpg2.TacticsMakingActivity.2
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    TacticsMakingActivity.this.onEnabledChanged(i3, z);
                }
            });
            patternViews.spnFloor = (Spinner) patternViews.tlayContainer.findViewById(C0380R.id.spnFloor);
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, this.floorStringsForSpinner);
            arrayAdapter.setDropDownViewResource(R.layout.simple_list_item_1);
            patternViews.spnFloor.setAdapter((SpinnerAdapter) arrayAdapter);
            patternViews.spnAbort = (Spinner) patternViews.tlayContainer.findViewById(C0380R.id.spnAbort);
            ArrayAdapter<CharSequence> arrayAdapterCreateFromResource = ArrayAdapter.createFromResource(this, C0380R.array.tactics_abort, R.layout.simple_spinner_item);
            arrayAdapterCreateFromResource.setDropDownViewResource(R.layout.simple_list_item_1);
            patternViews.spnAbort.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource);
            patternViews.spnRunning = (Spinner) patternViews.tlayContainer.findViewById(C0380R.id.spnRunning);
            ArrayAdapter<CharSequence> arrayAdapterCreateFromResource2 = ArrayAdapter.createFromResource(this, C0380R.array.tactics_running, R.layout.simple_spinner_item);
            arrayAdapterCreateFromResource2.setDropDownViewResource(R.layout.simple_list_item_1);
            patternViews.spnRunning.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource2);
            patternViews.spnAttackSkill = (Spinner) patternViews.tlayContainer.findViewById(C0380R.id.spnAttackSkill);
            ArrayAdapter<CharSequence> arrayAdapterCreateFromResource3 = ArrayAdapter.createFromResource(this, C0380R.array.tactics_skill, R.layout.simple_spinner_item);
            arrayAdapterCreateFromResource3.setDropDownViewResource(R.layout.simple_list_item_1);
            patternViews.spnAttackSkill.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource3);
            patternViews.spnStatusSkill = (Spinner) patternViews.tlayContainer.findViewById(C0380R.id.spnStatusSkill);
            ArrayAdapter<CharSequence> arrayAdapterCreateFromResource4 = ArrayAdapter.createFromResource(this, C0380R.array.tactics_skill, R.layout.simple_spinner_item);
            arrayAdapterCreateFromResource4.setDropDownViewResource(R.layout.simple_list_item_1);
            patternViews.spnStatusSkill.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource4);
            patternViews.spnCureSkill = (Spinner) patternViews.tlayContainer.findViewById(C0380R.id.spnCureSkill);
            ArrayAdapter<CharSequence> arrayAdapterCreateFromResource5 = ArrayAdapter.createFromResource(this, C0380R.array.tactics_skill, R.layout.simple_spinner_item);
            arrayAdapterCreateFromResource5.setDropDownViewResource(R.layout.simple_list_item_1);
            patternViews.spnCureSkill.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource5);
            patternViews.spnDamageSkill = (Spinner) patternViews.tlayContainer.findViewById(C0380R.id.spnDamageSkill);
            ArrayAdapter<CharSequence> arrayAdapterCreateFromResource6 = ArrayAdapter.createFromResource(this, C0380R.array.tactics_skill, R.layout.simple_spinner_item);
            arrayAdapterCreateFromResource6.setDropDownViewResource(R.layout.simple_list_item_1);
            patternViews.spnDamageSkill.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource6);
            patternViews.spnItem = (Spinner) patternViews.tlayContainer.findViewById(C0380R.id.spnItem);
            ArrayAdapter<CharSequence> arrayAdapterCreateFromResource7 = ArrayAdapter.createFromResource(this, C0380R.array.tactics_item, R.layout.simple_spinner_item);
            arrayAdapterCreateFromResource7.setDropDownViewResource(R.layout.simple_list_item_1);
            patternViews.spnItem.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource7);
            patternViews.spnRest = (Spinner) patternViews.tlayContainer.findViewById(C0380R.id.spnRest);
            ArrayAdapter<CharSequence> arrayAdapterCreateFromResource8 = ArrayAdapter.createFromResource(this, C0380R.array.tactics_rest, R.layout.simple_spinner_item);
            arrayAdapterCreateFromResource8.setDropDownViewResource(R.layout.simple_list_item_1);
            patternViews.spnRest.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource8);
            patternViews.spnFullInventory = (Spinner) patternViews.tlayContainer.findViewById(C0380R.id.spnFullInventory);
            ArrayAdapter<CharSequence> arrayAdapterCreateFromResource9 = ArrayAdapter.createFromResource(this, C0380R.array.tactics_full_inventory, R.layout.simple_spinner_item);
            arrayAdapterCreateFromResource9.setDropDownViewResource(R.layout.simple_list_item_1);
            patternViews.spnFullInventory.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource9);
            patternViews.trFullInventory = (TableRow) patternViews.tlayContainer.findViewById(C0380R.id.trFullInventory);
            frameLayout.addView(patternViews.tlayContainer);
            this.mPatterns.add(patternViews);
        }
        if (bundle != null) {
            this.mGame = (GameContext) bundle.getParcelable("game");
            this.mTacticsList = bundle.getParcelableArrayList("tactics");
            this.mGame.calcCharacterStatus(this);
        } else {
            this.mGame = GameContext.game;
            GameContext.game = null;
            this.mTacticsList = new Persister(this).readTactics(this.mTargetCharId);
            while (this.mTacticsList.size() < this.mNumberOfPatterns) {
                this.mTacticsList.add(Tactics.normal());
            }
        }
        if (!isForCharacter()) {
            this.mTacticsList.get(0).enabled = true;
        }
        this.mTacticsList.get(0).targetFloor = 0;
        this.mDefaultTactics = this.mTacticsList.get(0);
        TreeSet treeSet = new TreeSet();
        Iterator<Inventory> it = this.mGame.inventories.iterator();
        while (it.hasNext()) {
            treeSet.add(Integer.valueOf(it.next().itemId));
        }
        this.mItemIds = new int[treeSet.size()];
        Iterator it2 = treeSet.iterator();
        int i4 = 0;
        while (it2.hasNext()) {
            this.mItemIds[i4] = ((Integer) it2.next()).intValue();
            i4++;
        }
        LinearLayout linearLayout = (LinearLayout) findViewById(C0380R.id.llayUseItem);
        TextView textView = (TextView) findViewById(C0380R.id.tvDefaultPattern);
        TextView textView2 = (TextView) findViewById(C0380R.id.tvCharacterName);
        if (isForCharacter()) {
            textView2.setText(this.mGame.characters.get(this.mGame.getPlayerCharIndex(this.mTargetCharId)).name);
            linearLayout.setVisibility(8);
            textView.setVisibility(8);
        } else {
            findViewById(C0380R.id.llaySelectPattern).setVisibility(8);
            textView2.setVisibility(8);
            linearLayout.setVisibility(0);
            textView.setVisibility(0);
        }
        for (int i5 = 0; i5 < this.mNumberOfPatterns; i5++) {
            PatternViews patternViews2 = this.mPatterns.get(i5);
            patternViews2.chkEnabled.setText(getString(C0380R.string.lbl_tactics_use_this_pattern, new Object[]{Integer.valueOf(i5)}));
            Tactics tactics = this.mTacticsList.get(i5);
            patternViews2.chkEnabled.setChecked(tactics.enabled);
            patternViews2.spnFloor.setSelection(Math.max(tactics.targetFloor - 1, 0));
            patternViews2.spnAbort.setSelection(tactics.abort.ordinal());
            patternViews2.spnRunning.setSelection(tactics.running.ordinal());
            patternViews2.spnAttackSkill.setSelection(tactics.attackSkill.ordinal());
            patternViews2.spnStatusSkill.setSelection(tactics.statusSkill.ordinal());
            patternViews2.spnDamageSkill.setSelection(tactics.damageSkill.ordinal());
            patternViews2.spnCureSkill.setSelection(tactics.cureSkill.ordinal());
            patternViews2.spnItem.setSelection(tactics.item.ordinal());
            patternViews2.spnRest.setSelection(tactics.rest.ordinal());
            patternViews2.spnFullInventory.setSelection(tactics.fullInventory.ordinal());
            patternViews2.setEnabled(tactics.enabled);
            if (i5 == 0) {
                if (!isForCharacter()) {
                    patternViews2.chkEnabled.setVisibility(8);
                } else {
                    patternViews2.chkEnabled.setText(C0380R.string.lbl_tactics_use_default_pattern);
                }
                patternViews2.trFloor.setVisibility(8);
            }
            if (isForCharacter()) {
                patternViews2.trFullInventory.setVisibility(8);
            }
        }
        onActivatePattern(0);
        refreshUseItem();
        setResult(0);
    }

    static void setCharacterId(Intent intent, int i) {
        intent.putExtra(EXTRA_TARGET_CHAR_ID, i);
    }

    private boolean isForCharacter() {
        return this.mTargetCharId != 0;
    }

    protected void onActivatePattern(int i) {
        int i2 = 0;
        while (i2 < this.mNumberOfPatterns) {
            this.mPatterns.get(i2).tlayContainer.setVisibility(i2 == i ? 0 : 8);
            i2++;
        }
    }

    protected void onEnabledChanged(int i, boolean z) {
        this.mPatterns.get(i).setEnabled(z);
    }

    private void refreshUseItem() {
        if (this.mDefaultTactics.useItemId == 0) {
            this.mTvUseItem.setText(C0380R.string.lbl_tactics_use_item_none);
        } else {
            this.mTvUseItem.setText(getString(C0380R.string.lbl_tactics_use_item_use, new Object[]{Integer.valueOf(this.mDefaultTactics.useItemFloor), Integer.valueOf(this.mDefaultTactics.useItemBlock), ItemRepository.getItem(this, this.mDefaultTactics.useItemId).name}));
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        this.mMovingToAnotherActivity = false;
    }

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("game", this.mGame);
        bundle.putParcelableArrayList("tactics", this.mTacticsList);
    }

    private boolean canMoveToAnotherActivity() {
        if (this.mMovingToAnotherActivity) {
            return false;
        }
        this.mMovingToAnotherActivity = true;
        return true;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == 2131165237 && isForCharacter()) {
            moveToAdvancedTactics();
            return;
        }
        if (view.getId() == 2131165294) {
            changeUseItem();
            return;
        }
        if (view.getId() == 2131165271) {
            finishMaking();
        } else if (view.getId() == 2131165244) {
            finish();
        } else if (view.getId() == 2131165258) {
            showHelpDialog();
        }
    }

    private void showHelpDialog() {
        HelpDialogFragment.show(this, getString(C0380R.string.msg_help_tactics_making));
    }

    private void changeUseItem() {
        int[] iArr = this.mItemIds;
        if (iArr.length == 0) {
            Toast.makeText(this, C0380R.string.msg_no_item_for_tactics, 0).show();
        } else {
            TacticsUseItemDialogFragment.newInstance(iArr, this.mDefaultTactics.useItemFloor, this.mDefaultTactics.useItemBlock, this.mDefaultTactics.useItemId).show(getSupportFragmentManager(), "use_item");
        }
    }

    public void setUseItemCondition(int i, int i2, int i3) {
        Tactics tactics = this.mDefaultTactics;
        tactics.useItemFloor = i;
        tactics.useItemBlock = i2;
        tactics.useItemId = i3;
        refreshUseItem();
    }

    private void moveToAdvancedTactics() {
        if (canMoveToAnotherActivity()) {
            GameContext.game = this.mGame.copy();
            Intent intent = new Intent(getApplicationContext(), (Class<?>) AdvancedTacticsMakingActivity.class);
            AdvancedTacticsMakingActivity.setCharacterId(intent, this.mTargetCharId);
            DeviceUtil.setRequestOrientationForIntent(this, intent);
            startActivityForResult(intent, 1);
        }
    }

    private void finishMaking() {
        Tactics.TacticsValue[] tacticsValueArrValues = Tactics.TacticsValue.values();
        for (int i = 0; i < this.mNumberOfPatterns; i++) {
            Tactics tactics = this.mTacticsList.get(i);
            PatternViews patternViews = this.mPatterns.get(i);
            tactics.charId = this.mTargetCharId;
            tactics.enabled = patternViews.chkEnabled.isChecked();
            tactics.targetFloor = patternViews.spnFloor.getSelectedItemPosition() + 1;
            tactics.abort = tacticsValueArrValues[patternViews.spnAbort.getSelectedItemPosition()];
            tactics.running = tacticsValueArrValues[patternViews.spnRunning.getSelectedItemPosition()];
            tactics.attackSkill = tacticsValueArrValues[patternViews.spnAttackSkill.getSelectedItemPosition()];
            tactics.statusSkill = tacticsValueArrValues[patternViews.spnStatusSkill.getSelectedItemPosition()];
            tactics.cureSkill = tacticsValueArrValues[patternViews.spnCureSkill.getSelectedItemPosition()];
            tactics.damageSkill = tacticsValueArrValues[patternViews.spnDamageSkill.getSelectedItemPosition()];
            tactics.item = tacticsValueArrValues[patternViews.spnItem.getSelectedItemPosition()];
            tactics.rest = tacticsValueArrValues[patternViews.spnRest.getSelectedItemPosition()];
            tactics.fullInventory = Tactics.FullInventoryTactics.values()[patternViews.spnFullInventory.getSelectedItemPosition()];
        }
        this.mTacticsList.get(0).targetFloor = 0;
        if (!isForCharacter()) {
            this.mTacticsList.get(0).enabled = true;
        }
        new Persister(this).writeTactics(this.mTargetCharId, this.mTacticsList);
        Toast.makeText(this, C0380R.string.msg_tactics_made, 0).show();
        setResult(-1);
        finish();
    }

    public static class TacticsUseItemDialogFragment extends DialogFragment {
        private int[] mItemIds;
        private Spinner mSpnBlock;
        private Spinner mSpnFloor;
        private Spinner mSpnItem;

        public static TacticsUseItemDialogFragment newInstance(int[] iArr, int i, int i2, int i3) {
            TacticsUseItemDialogFragment tacticsUseItemDialogFragment = new TacticsUseItemDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putIntArray(TacticsMakingActivity.DIALOG_ARGS_ITEM_IDS, iArr);
            bundle.putInt(TacticsMakingActivity.DIALOG_ARGS_FLOOR, i);
            bundle.putInt(TacticsMakingActivity.DIALOG_ARGS_BLOCK, i2);
            bundle.putInt(TacticsMakingActivity.DIALOG_ARGS_CURRENT_ITEM_ID, i3);
            tacticsUseItemDialogFragment.setArguments(bundle);
            return tacticsUseItemDialogFragment;
        }

        @Override // android.support.v4.app.DialogFragment
        @SuppressLint({"InflateParams"})
        public Dialog onCreateDialog(Bundle bundle) {
            Bundle arguments = getArguments();
            this.mItemIds = arguments.getIntArray(TacticsMakingActivity.DIALOG_ARGS_ITEM_IDS);
            int i = arguments.getInt(TacticsMakingActivity.DIALOG_ARGS_CURRENT_ITEM_ID);
            int i2 = arguments.getInt(TacticsMakingActivity.DIALOG_ARGS_FLOOR);
            int i3 = arguments.getInt(TacticsMakingActivity.DIALOG_ARGS_BLOCK);
            String[] strArr = new String[12];
            String[] strArr2 = new String[this.mItemIds.length + 1];
            int i4 = 0;
            int i5 = 0;
            while (i5 < strArr.length) {
                int i6 = i5 + 1;
                strArr[i5] = getActivity().getString(C0380R.string.lbl_tactics_use_item_block_value, new Object[]{Integer.valueOf(i6)});
                i5 = i6;
            }
            strArr2[0] = getActivity().getString(C0380R.string.lbl_tactics_use_item_target_value_none);
            int i7 = 0;
            while (i7 < this.mItemIds.length) {
                int i8 = i7 + 1;
                strArr2[i8] = ItemRepository.getItem(getActivity().getApplicationContext(), this.mItemIds[i7]).name;
                i7 = i8;
            }
            View viewInflate = getActivity().getLayoutInflater().inflate(C0380R.layout.tactics_use_item_dialog, (ViewGroup) null);
            this.mSpnFloor = (Spinner) viewInflate.findViewById(C0380R.id.spnFloor);
            this.mSpnBlock = (Spinner) viewInflate.findViewById(C0380R.id.spnBlock);
            this.mSpnItem = (Spinner) viewInflate.findViewById(C0380R.id.spnItem);
            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, ((TacticsMakingActivity) getActivity()).floorStringsForSpinner);
            arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            this.mSpnFloor.setAdapter((SpinnerAdapter) arrayAdapter);
            ArrayAdapter arrayAdapter2 = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, strArr);
            arrayAdapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            this.mSpnBlock.setAdapter((SpinnerAdapter) arrayAdapter2);
            ArrayAdapter arrayAdapter3 = new ArrayAdapter(getActivity(), R.layout.simple_spinner_item, strArr2);
            arrayAdapter3.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            this.mSpnItem.setAdapter((SpinnerAdapter) arrayAdapter3);
            if (i2 < 1 || i2 > 20) {
                i2 = 1;
            }
            if (i3 < 1 || i3 > 20) {
                i3 = 1;
            }
            int i9 = 0;
            while (true) {
                int[] iArr = this.mItemIds;
                if (i9 >= iArr.length) {
                    break;
                }
                if (iArr[i9] == i) {
                    i4 = i9 + 1;
                    break;
                }
                i9++;
            }
            this.mSpnFloor.setSelection(i2 - 1);
            this.mSpnBlock.setSelection(i3 - 1);
            this.mSpnItem.setSelection(i4);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(viewInflate);
            builder.setTitle(C0380R.string.msg_dlg_title_tactics_use_item);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: com.shirobakama.autorpg2.TacticsMakingActivity.TacticsUseItemDialogFragment.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i10) {
                    TacticsUseItemDialogFragment.this.selectCondition();
                }
            });
            builder.setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null);
            return builder.create();
        }

        protected void selectCondition() {
            int selectedItemPosition = this.mSpnItem.getSelectedItemPosition();
            ((TacticsMakingActivity) getActivity()).setUseItemCondition(this.mSpnFloor.getSelectedItemPosition() + 1, this.mSpnBlock.getSelectedItemPosition() + 1, selectedItemPosition == 0 ? 0 : this.mItemIds[selectedItemPosition - 1]);
        }
    }
}
