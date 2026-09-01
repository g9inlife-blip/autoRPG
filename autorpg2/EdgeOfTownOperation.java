package com.shirobakama.autorpg2;

import android.R;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.shirobakama.autorpg2.AlertDialogFragment;
import com.shirobakama.autorpg2.TownActivity;
import com.shirobakama.autorpg2.adventure.AdventureEngine;
import com.shirobakama.autorpg2.adventure.FlagEngine;
import com.shirobakama.autorpg2.adventure.TownEOTFlagEngine;
import com.shirobakama.autorpg2.adventure.TownFlagEngine;
import com.shirobakama.autorpg2.entity.Dungeon;
import com.shirobakama.autorpg2.entity.DungeonContext;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.GameFlag;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.entity.Stock;
import com.shirobakama.autorpg2.entity.Town;
import com.shirobakama.autorpg2.p001db.Persister;
import com.shirobakama.autorpg2.repo.DungeonRepository;
import com.shirobakama.autorpg2.repo.QuestDb;
import com.shirobakama.autorpg2.repo.TownRepository;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.autorpg2.util.FormatUtil;
import com.shirobakama.autorpg2.view.LabelValueItem;
import com.shirobakama.logquest2.BuildConfig;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class EdgeOfTownOperation implements View.OnClickListener, TownActivity.TownOperation {
    protected static final String TAG = "edge-of-town";
    protected TownActivity mActivity;
    private Button mBtnEOTQuest;
    private Button mBtnEOTSelectTown;
    private Button mBtnSelectDungeon;
    private View mLlayEdgeOfTownInTown;
    private View mRlayEdgeOfTownAdventuring;
    private Spinner mSpnFloor;
    private int mTargetFloor;
    private TextView mTvAdventureDuration;
    private TextView mTvCurrentDestination;
    private TextView mTvCurrentDungeon;
    private TextView mTvCurrentTown;
    private TextView mTvCurrestStatus;
    private TextView mTvLastLogDestination;
    private TextView mTvReturnTime;
    private TextView mTvStartTime;
    private TownEOTFlagEngine mFlagEngine = new TownEOTFlagEngine();
    private boolean mFirst = true;

    protected void cancelSelectTown() {
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public ArrayList<LabelValueItem> getMenuItems() {
        return null;
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public void selectMenu(Bundle bundle, int i, List<LabelValueItem> list) {
    }

    public EdgeOfTownOperation(TownActivity townActivity) {
        this.mActivity = townActivity;
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public void onCreate(View view) {
        view.findViewById(C0380R.id.btnDungeonInfo).setOnClickListener(this);
        this.mBtnSelectDungeon = (Button) view.findViewById(C0380R.id.btnSelectDungeon);
        this.mBtnSelectDungeon.setOnClickListener(this);
        view.findViewById(C0380R.id.btnGoToAdventure).setOnClickListener(this);
        view.findViewById(C0380R.id.btnDisplayLastLog).setOnClickListener(this);
        view.findViewById(C0380R.id.btnEOTOther).setOnClickListener(this);
        view.findViewById(C0380R.id.btnEOTTactics).setOnClickListener(this);
        this.mBtnEOTSelectTown = (Button) view.findViewById(C0380R.id.btnEOTSelectTown);
        this.mBtnEOTSelectTown.setOnClickListener(this);
        this.mBtnEOTQuest = (Button) view.findViewById(C0380R.id.btnEOTQuest);
        this.mBtnEOTQuest.setOnClickListener(this);
        this.mLlayEdgeOfTownInTown = view.findViewById(C0380R.id.llayEdgeOfTownInTown);
        this.mRlayEdgeOfTownAdventuring = view.findViewById(C0380R.id.rlayEdgeOfTownAdventuring);
        this.mTvCurrentTown = (TextView) view.findViewById(C0380R.id.tvCurrentTown);
        this.mTvLastLogDestination = (TextView) view.findViewById(C0380R.id.tvLastLogDestination);
        this.mTvCurrentDungeon = (TextView) view.findViewById(C0380R.id.tvCurrentDungeon);
        this.mTvCurrestStatus = (TextView) view.findViewById(C0380R.id.tvCurrentStatus);
        this.mTvCurrentDestination = (TextView) view.findViewById(C0380R.id.tvCurrentDestination);
        this.mTvStartTime = (TextView) view.findViewById(C0380R.id.tvStartTime);
        this.mTvReturnTime = (TextView) view.findViewById(C0380R.id.tvReturnTime);
        this.mTvAdventureDuration = (TextView) view.findViewById(C0380R.id.tvAdventureDuration);
        this.mSpnFloor = (Spinner) view.findViewById(C0380R.id.spnFloor);
        this.mSpnFloor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.shirobakama.autorpg2.EdgeOfTownOperation.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view2, int i, long j) {
                EdgeOfTownOperation.this.selectFloor((LabelValueItem) adapterView.getAdapter().getItem(i));
            }
        });
        setFloorSpinner();
        this.mBtnEOTQuest.setVisibility(8);
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public void activate(boolean z) {
        refreshCurrentStatus();
        if (this.mFirst) {
            if (this.mActivity.game.characters.isEmpty()) {
                TownActivity townActivity = this.mActivity;
                townActivity.addMessage(townActivity.getString(C0380R.string.msg_tutorial_no_character_warning));
            } else {
                TownActivity townActivity2 = this.mActivity;
                townActivity2.addMessage(townActivity2.getString(C0380R.string.msg_you_are_in_town, new Object[]{TownRepository.getTown(townActivity2, townActivity2.game.townId).name}));
            }
            this.mFirst = false;
        }
        setBtnEOTQuestVisibilityByEngine();
        setButtonsEnabledByAdventuring();
    }

    private void setBtnEOTQuestVisibilityByEngine() {
        TownEOTFlagEngine townEOTFlagEngine = this.mFlagEngine;
        TownActivity townActivity = this.mActivity;
        TownFlagEngine.Result resultPeekEOT = townEOTFlagEngine.peekEOT(townActivity, townActivity.game);
        this.mBtnEOTQuest.setVisibility(resultPeekEOT != null && ((resultPeekEOT.questCandidateSymbol != null || resultPeekEOT.message != null) && !this.mActivity.game.isAdventuring(this.mActivity)) ? 0 : 8);
    }

    private void setButtonsEnabledByAdventuring() {
        boolean z = !this.mActivity.game.isAdventuring(this.mActivity);
        this.mBtnEOTSelectTown.setEnabled(z);
        this.mBtnSelectDungeon.setEnabled(z);
    }

    private void showCurrentDungeon() {
        Dungeon dungeon;
        if (this.mActivity.game.dungeonId != 0) {
            TownActivity townActivity = this.mActivity;
            dungeon = DungeonRepository.getDungeon(townActivity, townActivity.game.dungeonId);
        } else {
            dungeon = null;
        }
        TownActivity townActivity2 = this.mActivity;
        Object[] objArr = new Object[1];
        objArr[0] = dungeon == null ? townActivity2.getString(C0380R.string.lbl_current_dungeon_none) : dungeon.name;
        this.mTvCurrentDungeon.setText(townActivity2.getString(C0380R.string.res_blacket_for_string, objArr));
        this.mTvCurrestStatus.setText(this.mActivity.getCurrentDungeonStatus(dungeon));
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public TownFlagEngine.Result acceptQuest(String str) {
        TownEOTFlagEngine townEOTFlagEngine = this.mFlagEngine;
        TownActivity townActivity = this.mActivity;
        TownFlagEngine.Result resultProcessAcceptQuest = townEOTFlagEngine.processAcceptQuest(townActivity, townActivity.game, str);
        setBtnEOTQuestVisibilityByEngine();
        return resultProcessAcceptQuest;
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public TownFlagEngine.Result refuseQuest(String str) {
        TownEOTFlagEngine townEOTFlagEngine = this.mFlagEngine;
        TownActivity townActivity = this.mActivity;
        TownFlagEngine.Result resultProcessRefuseQuest = townEOTFlagEngine.processRefuseQuest(townActivity, townActivity.game, str);
        setBtnEOTQuestVisibilityByEngine();
        return resultProcessRefuseQuest;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == 2131165257) {
            confirmGoToAdventure();
            return;
        }
        if (view.getId() == 2131165251) {
            showDungeonInfo();
            return;
        }
        if (view.getId() == 2131165278) {
            prepareSelectDungeon();
            return;
        }
        if (view.getId() == 2131165254) {
            prepareSelectTown();
            return;
        }
        if (view.getId() == 2131165250) {
            this.mActivity.moveToLogView(-1);
            return;
        }
        if (view.getId() == 2131165255) {
            selectTacticsTarget();
        } else if (view.getId() == 2131165252) {
            this.mActivity.action();
        } else if (view.getId() == 2131165253) {
            doQuest();
        }
    }

    protected void refreshCurrentStatus() {
        if (this.mActivity.game.isAdventuring(this.mActivity)) {
            this.mLlayEdgeOfTownInTown.setVisibility(8);
            this.mRlayEdgeOfTownAdventuring.setVisibility(0);
            setBtnEOTQuestVisibilityByEngine();
            setButtonsEnabledByAdventuring();
            TownActivity townActivity = this.mActivity;
            Dungeon dungeon = DungeonRepository.getDungeon(townActivity, townActivity.game.dungeonId);
            TownActivity townActivity2 = this.mActivity;
            Object[] objArr = new Object[2];
            objArr[0] = dungeon == null ? BuildConfig.FLAVOR : dungeon.name;
            objArr[1] = Integer.valueOf(this.mActivity.game.targetFloor);
            this.mTvCurrentDestination.setText(townActivity2.getString(C0380R.string.lbl_current_destination, objArr));
            TextView textView = this.mTvLastLogDestination;
            TownActivity townActivity3 = this.mActivity;
            Object[] objArr2 = new Object[2];
            objArr2[0] = dungeon == null ? BuildConfig.FLAVOR : dungeon.name;
            objArr2[1] = Integer.valueOf(this.mActivity.game.targetFloor);
            textView.setText(townActivity3.getString(C0380R.string.lbl_last_log_destination, objArr2));
            this.mTvStartTime.setText(FormatUtil.formatTimeToHhmm(this.mActivity.game.startTime));
            this.mTvReturnTime.setText(FormatUtil.formatTimeToHhmm(this.mActivity.game.estimateTime));
        } else {
            Persister persister = new Persister(this.mActivity);
            if (persister.isResultExists()) {
                try {
                    copyResultsToCurrent(persister);
                    TownActivity townActivity4 = this.mActivity;
                    townActivity4.addMessage(townActivity4.getString(C0380R.string.msg_adventure_end));
                    this.mActivity.onReturningAdventure();
                    onReturningAdventure();
                } catch (Persister.PlayerUpdateFailedException e) {
                    AutoRpgMainActivity.recordRecoverableExceptionOccurred(this.mActivity, e);
                    throw e;
                }
            }
            if (this.mActivity.logManagement == null) {
                this.mTvLastLogDestination.setText(BuildConfig.FLAVOR);
            } else {
                TownActivity townActivity5 = this.mActivity;
                this.mTvLastLogDestination.setText(this.mActivity.getString(C0380R.string.lbl_last_log_destination, new Object[]{DungeonRepository.getDungeon(townActivity5, townActivity5.logManagement.dungeonId).name, Integer.valueOf(this.mActivity.logManagement.targetFloor)}));
            }
            this.mLlayEdgeOfTownInTown.setVisibility(0);
            this.mRlayEdgeOfTownAdventuring.setVisibility(8);
        }
        showCurrentDungeon();
        TextView textView2 = this.mTvCurrentTown;
        TownActivity townActivity6 = this.mActivity;
        textView2.setText(townActivity6.getString(C0380R.string.lbl_current_town, new Object[]{TownRepository.getTown(townActivity6, townActivity6.game.townId).name}));
    }

    private void copyResultsToCurrent(Persister persister) {
        persister.copyResultsToCurrent();
        this.mActivity.game = persister.readGameContext();
        TownActivity townActivity = this.mActivity;
        Stock.sortByTypeAndName(townActivity, townActivity.game.stocks);
    }

    private void showDungeonInfo() {
        TownActivity townActivity = this.mActivity;
        Dungeon dungeon = DungeonRepository.getDungeon(townActivity, townActivity.game.dungeonId);
        if (dungeon == null) {
            return;
        }
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(dungeon.name).setCancelable(true);
        decorator.setPositiveText(0);
        decorator.args().putInt("dungeon_id", this.mActivity.game.dungeonId);
        decorator.decorate(new DungeonInfoDialogFragment()).show(this.mActivity.getSupportFragmentManager());
    }

    public static class DungeonInfoDialogFragment extends AlertDialogFragment {
        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        protected void onClick(DialogInterface dialogInterface, int i) {
        }

        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        @SuppressLint({"InflateParams"})
        protected View getAlertDialogView() {
            TownActivity townActivity = (TownActivity) getActivity();
            View viewInflate = townActivity.layoutInflater.inflate(C0380R.layout.dungeon_info_dialog, (ViewGroup) null);
            Dungeon dungeon = DungeonRepository.getDungeon(townActivity, getArguments().getInt("dungeon_id"));
            ((TextView) viewInflate.findViewById(C0380R.id.tvDungeonFloor)).setText(townActivity.getString(C0380R.string.msg_dungeon_floor, new Object[]{Integer.valueOf(dungeon.floor)}));
            ((TextView) viewInflate.findViewById(C0380R.id.tvDungeonBlock)).setText(townActivity.getString(C0380R.string.msg_dungeon_block, new Object[]{Integer.valueOf(dungeon.block)}));
            ((TextView) viewInflate.findViewById(C0380R.id.tvDungeonStatus)).setText(townActivity.getCurrentDungeonStatus(dungeon));
            return viewInflate;
        }
    }

    private void prepareSelectDungeon() {
        final String string = this.mActivity.getString(C0380R.string.res_dungeon_difficulty_star);
        TownActivity townActivity = this.mActivity;
        ArrayList<LabelValueItem> arrayListCreateList = LabelValueItem.createList(FlagEngine.getAvailableDungeons(townActivity, townActivity.game), new LabelValueItem.ItemCreator<Dungeon>() { // from class: com.shirobakama.autorpg2.EdgeOfTownOperation.2
            @Override // com.shirobakama.autorpg2.view.LabelValueItem.ItemCreator
            public LabelValueItem create(Dungeon dungeon) {
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < dungeon.difficultyStars; i++) {
                    stringBuffer.append(string);
                }
                return new LabelValueItem(dungeon.f90id, Html.fromHtml(dungeon.name + "<br/><small><font color=\"#808080\">" + EdgeOfTownOperation.this.mActivity.getString(C0380R.string.lbl_dungeon_difficulty_star, new Object[]{stringBuffer.toString(), Integer.valueOf(dungeon.difficultyStars)}) + "</font></small>"));
            }
        });
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setChoiceMode(1);
        decorator.setTitle(C0380R.string.msg_dlg_select_dungeon).setCancelable(true);
        decorator.setPositiveText(0).setNegativeText(0);
        decorator.setLabelValueItems(arrayListCreateList);
        decorator.decorate(new SelectDungeonDialogFrament()).show(this.mActivity.getSupportFragmentManager());
    }

    public static class SelectDungeonDialogFrament extends AlertDialogFragment {
        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        protected void onClick(DialogInterface dialogInterface, int i) {
            if (i != -1) {
                return;
            }
            ((TownActivity) getActivity()).edgeOfTown.selectDungeon(this.whichItem, this.items);
        }
    }

    protected void selectDungeon(int i, List<LabelValueItem> list) {
        if (i < 0 || i >= list.size()) {
            return;
        }
        int i2 = list.get(i).value;
        if (isDungeonReady() && this.mActivity.game.dungeonId == i2) {
            return;
        }
        if (this.mActivity.game.dungeonContext == null || this.mActivity.game.dungeonContext.stats == null || this.mActivity.game.dungeonContext.stats.isEmpty()) {
            changeToNewDungeon(i2);
        } else {
            confirmToNewDungeonOrTown(i2, 0);
        }
    }

    private void confirmToNewDungeonOrTown(int i, int i2) {
        AlertDialogFragment.Decorator decoratorConfirmationDecorator = AlertDialogFragment.Decorator.confirmationDecorator(i != 0 ? C0380R.string.msg_dlg_title_confirm_change_dungeon : C0380R.string.msg_dlg_title_confirm_change_town, C0380R.string.msg_dlg_confirm_change_dungeon_or_town);
        decoratorConfirmationDecorator.args().putInt("new_dungeon_id", i);
        decoratorConfirmationDecorator.args().putInt("new_town_id", i2);
        decoratorConfirmationDecorator.decorate(new DungeonStatusResetConfirmDialogFragment()).show(this.mActivity.getSupportFragmentManager());
    }

    public static class DungeonStatusResetConfirmDialogFragment extends AlertDialogFragment {
        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        protected void onClick(DialogInterface dialogInterface, int i) {
            if (i != -1) {
                return;
            }
            int i2 = getArguments().getInt("new_dungeon_id", 0);
            int i3 = getArguments().getInt("new_town_id", 0);
            if (i2 != 0) {
                ((TownActivity) getActivity()).edgeOfTown.changeToNewDungeon(i2);
            } else {
                ((TownActivity) getActivity()).edgeOfTown.changeToNewTown(i3);
            }
        }
    }

    protected void changeToNewDungeon(int i) {
        if (i == 0) {
            return;
        }
        this.mActivity.game.dungeonId = i;
        DungeonContext dungeonContext = this.mActivity.game.dungeonContext;
        TownActivity townActivity = this.mActivity;
        dungeonContext.initialize(townActivity, townActivity.game, DungeonRepository.getDungeon(this.mActivity, i), new Random());
        Persister persister = new Persister(this.mActivity);
        try {
            persister.deleteDungeonContext();
            persister.writeGameDungeonContext(this.mActivity.game);
        } catch (SQLException e) {
            DeviceUtil.handleSqliteException(this.mActivity, e);
        }
        showCurrentDungeon();
        setFloorSpinner();
        TownActivity townActivity2 = this.mActivity;
        townActivity2.addMessage(townActivity2.getString(C0380R.string.msg_dungeon_selected));
    }

    private void setFloorSpinner() {
        int selectedItemPosition = this.mSpnFloor.getSelectedItemPosition();
        ArrayList arrayList = new ArrayList();
        TownActivity townActivity = this.mActivity;
        Dungeon dungeon = DungeonRepository.getDungeon(townActivity, townActivity.game.dungeonId);
        if (dungeon == null) {
            this.mSpnFloor.setEnabled(false);
            this.mTvAdventureDuration.setText(BuildConfig.FLAVOR);
        } else {
            this.mSpnFloor.setEnabled(true);
            TownActivity townActivity2 = this.mActivity;
            int maxFloor = TownFlagEngine.getMaxFloor(townActivity2, townActivity2.game, dungeon);
            int i = 0;
            while (i < maxFloor) {
                i++;
                arrayList.add(new LabelValueItem(i, this.mActivity.getString(C0380R.string.msg_dungeon_floor, new Object[]{Integer.valueOf(i)})));
            }
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this.mActivity, R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        this.mSpnFloor.setAdapter((SpinnerAdapter) arrayAdapter);
        if (this.mActivity.logManagement == null || this.mActivity.logManagement.dungeonId != this.mActivity.game.dungeonId) {
            return;
        }
        if (selectedItemPosition != -1 && selectedItemPosition < arrayList.size()) {
            this.mSpnFloor.setSelection(selectedItemPosition);
            return;
        }
        int count = this.mActivity.logManagement.targetFloor - 1;
        if (count >= this.mSpnFloor.getCount()) {
            count = this.mSpnFloor.getCount() - 1;
        }
        this.mSpnFloor.setSelection(count, false);
    }

    private boolean isDungeonReady() {
        return (this.mActivity.game.dungeonId == 0 || this.mActivity.game.dungeonContext.stats == null || this.mActivity.game.dungeonContext.stats.isEmpty()) ? false : true;
    }

    protected void selectFloor(LabelValueItem labelValueItem) {
        this.mTargetFloor = labelValueItem.value;
        if (!isDungeonReady() || this.mActivity.game.characters.isEmpty()) {
            this.mTvAdventureDuration.setText((CharSequence) null);
            return;
        }
        this.mActivity.game.targetFloor = this.mTargetFloor;
        showAdventureDuration();
    }

    private void showAdventureDuration() {
        String string;
        if (this.mActivity.game.dungeonId == 0) {
            return;
        }
        TownActivity townActivity = this.mActivity;
        int iEstimateTimeInMinutesStatic = AdventureEngine.estimateTimeInMinutesStatic(townActivity, townActivity.game);
        int i = iEstimateTimeInMinutesStatic / 60;
        int i2 = iEstimateTimeInMinutesStatic % 60;
        if (i == 0) {
            string = this.mActivity.getString(C0380R.string.lbl_adventure_duration_min, new Object[]{Integer.valueOf(i2)});
        } else {
            string = this.mActivity.getString(C0380R.string.lbl_adventure_duration_hour_min, new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        }
        this.mTvAdventureDuration.setText(string);
    }

    private void prepareSelectTown() {
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(C0380R.string.msg_dlg_select_town).setCancelable(true);
        decorator.setPositiveText(0).setNegativeText(0);
        decorator.decorate(new TownSelectionDialogFragment()).show(this.mActivity.getSupportFragmentManager());
    }

    private void prepareSelectTownOld() {
        TownActivity townActivity = this.mActivity;
        ArrayList<LabelValueItem> arrayListCreateList = LabelValueItem.createList(FlagEngine.getAvailableTowns(townActivity, townActivity.game), new LabelValueItem.ItemCreator<Town>() { // from class: com.shirobakama.autorpg2.EdgeOfTownOperation.3
            @Override // com.shirobakama.autorpg2.view.LabelValueItem.ItemCreator
            public LabelValueItem create(Town town) {
                return new LabelValueItem(town.f111id, town.name);
            }
        });
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setChoiceMode(1);
        decorator.setTitle(C0380R.string.msg_dlg_select_town).setCancelable(true);
        decorator.setPositiveText(0).setNegativeText(0);
        decorator.setLabelValueItems(arrayListCreateList);
        decorator.decorate(new SelectTownDialogFragment()).show(this.mActivity.getSupportFragmentManager());
    }

    public static class SelectTownDialogFragment extends AlertDialogFragment {
        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        protected void onClick(DialogInterface dialogInterface, int i) {
            switch (i) {
                case -2:
                    ((TownActivity) getActivity()).edgeOfTown.cancelSelectTown();
                    break;
                case -1:
                    ((TownActivity) getActivity()).edgeOfTown.selectTown(this.whichItem, this.items);
                    break;
            }
        }
    }

    protected void selectTown(int i) {
        if (this.mActivity.game.dungeonContext == null || this.mActivity.game.dungeonContext.stats == null || this.mActivity.game.dungeonContext.stats.isEmpty()) {
            changeToNewTown(i);
        } else {
            confirmToNewDungeonOrTown(0, i);
        }
    }

    protected void selectTown(int i, List<LabelValueItem> list) {
        if (i < 0 || i >= list.size()) {
            return;
        }
        int i2 = list.get(i).value;
        if (this.mActivity.game.dungeonContext == null || this.mActivity.game.dungeonContext.stats == null || this.mActivity.game.dungeonContext.stats.isEmpty()) {
            changeToNewTown(i2);
        } else {
            confirmToNewDungeonOrTown(0, i2);
        }
    }

    protected void changeToNewTown(int i) {
        if (i == 0) {
            return;
        }
        this.mActivity.game.townId = i;
        this.mActivity.game.dungeonId = 0;
        this.mActivity.game.dungeonContext.clear();
        try {
            new Persister(this.mActivity).writeGameDungeonContext(this.mActivity.game);
        } catch (SQLException e) {
            DeviceUtil.handleSqliteException(this.mActivity, e);
        }
        TownChangeDummyProcessor townChangeDummyProcessor = new TownChangeDummyProcessor(this.mActivity);
        this.mActivity.progressProcessor = townChangeDummyProcessor;
        townChangeDummyProcessor.run(new Handler.Callback() { // from class: com.shirobakama.autorpg2.EdgeOfTownOperation.4
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message message) {
                EdgeOfTownOperation.this.mActivity.progressProcessor = null;
                EdgeOfTownOperation.this.mActivity.onChangeTown();
                EdgeOfTownOperation.this.onChangeTown();
                EdgeOfTownOperation.this.refreshCurrentStatus();
                return true;
            }
        });
    }

    private void confirmGoToAdventure() {
        if (isDungeonReady()) {
            if (this.mActivity.game.characters.isEmpty()) {
                TownActivity townActivity = this.mActivity;
                townActivity.addMessage(townActivity.getString(C0380R.string.msg_less_characters));
            } else if (this.mActivity.game.inventories.size() > this.mActivity.game.getMaxInventoryCount()) {
                TownActivity townActivity2 = this.mActivity;
                townActivity2.addMessage(townActivity2.getString(C0380R.string.msg_exceed_equipment));
            } else {
                AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
                decorator.setCancelable(true).setTitle(C0380R.string.msg_dlg_title_confirm_go_to_adventure);
                decorator.setPositiveText(0).setNegativeText(0);
                decorator.decorate(new GoToAdventureConfirmDialogFragment()).show(this.mActivity.getSupportFragmentManager());
            }
        }
    }

    protected void goToAdventure(Date date, long j, boolean z) {
        if (date != null) {
            if (Math.abs(System.currentTimeMillis() - (date.getTime() + (SystemClock.elapsedRealtime() - j))) > GameContext.MAX_DIALOG_TOLERANCE_TS) {
                TownActivity townActivity = this.mActivity;
                townActivity.addMessage(townActivity.getString(C0380R.string.msg_illegal_modification_detected));
                return;
            }
        } else if (this.mActivity.game.advCount <= 0) {
            return;
        }
        AdventureProcessor adventureProcessor = new AdventureProcessor(this.mActivity.game, this.mActivity, this.mTargetFloor, z);
        this.mActivity.progressProcessor = adventureProcessor;
        adventureProcessor.run(new Handler.Callback() { // from class: com.shirobakama.autorpg2.EdgeOfTownOperation.5
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message message) {
                EdgeOfTownOperation.this.mActivity.addMessage(EdgeOfTownOperation.this.mActivity.getString(C0380R.string.msg_adventure_start));
                EdgeOfTownOperation.this.refreshCurrentStatus();
                EdgeOfTownOperation.this.mActivity.progressProcessor = null;
                return true;
            }
        });
    }

    private void selectTacticsTarget() {
        ArrayList arrayList = new ArrayList(this.mActivity.game.characters.size() + 1);
        arrayList.add(this.mActivity.getString(C0380R.string.lbl_tactics_target_party));
        Iterator<PlayerChar> it = this.mActivity.game.characters.iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().name);
        }
        ArrayList<LabelValueItem> arrayListCreateList = LabelValueItem.createList(arrayList, new LabelValueItem.ItemCreator<String>() { // from class: com.shirobakama.autorpg2.EdgeOfTownOperation.6
            @Override // com.shirobakama.autorpg2.view.LabelValueItem.ItemCreator
            public LabelValueItem create(String str) {
                return new LabelValueItem(0, str);
            }
        });
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(C0380R.string.msg_dlg_title_tactics_target).setCancelable(true);
        decorator.setNegativeText(0);
        decorator.setLabelValueItems(arrayListCreateList);
        decorator.decorate(new TacticsTargetDiaglogFragment()).show(this.mActivity.getSupportFragmentManager());
    }

    public static class TacticsTargetDiaglogFragment extends AlertDialogFragment {
        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        protected void onClick(DialogInterface dialogInterface, int i) {
            if (i != 0) {
                return;
            }
            TownActivity townActivity = (TownActivity) getActivity();
            townActivity.edgeOfTown.moveToMakingTactics(this.whichItem == 0 ? 0 : townActivity.game.characters.get(this.whichItem - 1).f106id);
        }
    }

    protected void moveToMakingTactics(int i) {
        if (this.mActivity.canMoveToAnotherActivity()) {
            GameContext.game = this.mActivity.game.copy();
            Intent intent = new Intent(this.mActivity.getApplicationContext(), (Class<?>) TacticsMakingActivity.class);
            TacticsMakingActivity.setCharacterId(intent, i);
            DeviceUtil.setRequestOrientationForIntent(this.mActivity, intent);
            this.mActivity.startActivityForResult(intent, 3);
        }
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (i == 2) {
            refreshCurrentStatus();
            return true;
        }
        if (i != 3) {
            return false;
        }
        refreshCurrentStatus();
        return true;
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public void onChangeTown() {
        TownEOTFlagEngine townEOTFlagEngine = this.mFlagEngine;
        TownActivity townActivity = this.mActivity;
        townEOTFlagEngine.resetState(townActivity, townActivity.game);
        TownActivity townActivity2 = this.mActivity;
        townActivity2.addMessage(townActivity2.getString(C0380R.string.msg_you_are_in_town, new Object[]{TownRepository.getTown(townActivity2, townActivity2.game.townId).name}));
        setFloorSpinner();
        setBtnEOTQuestVisibilityByEngine();
        setButtonsEnabledByAdventuring();
    }

    @Override // com.shirobakama.autorpg2.TownActivity.TownOperation
    public void onReturningAdventure() {
        showAdventureDuration();
        setFloorSpinner();
        TownEOTFlagEngine townEOTFlagEngine = this.mFlagEngine;
        TownActivity townActivity = this.mActivity;
        townEOTFlagEngine.resetState(townActivity, townActivity.game);
        setBtnEOTQuestVisibilityByEngine();
        setButtonsEnabledByAdventuring();
    }

    private void doQuest() {
        GameFlag flag;
        GameFlag.Key key = new GameFlag.Key(GameFlag.FlagType.QUEST_CLEAR, QuestDb.QUEST_ATTACK_DARK_LORD);
        GameFlag flag2 = this.mActivity.game.getFlag(key);
        boolean z = flag2 != null && flag2.value;
        TownEOTFlagEngine townEOTFlagEngine = this.mFlagEngine;
        TownActivity townActivity = this.mActivity;
        TownFlagEngine.Result resultNextEOT = townEOTFlagEngine.nextEOT(townActivity, townActivity.game);
        if (resultNextEOT == null) {
            setBtnEOTQuestVisibilityByEngine();
            return;
        }
        if (resultNextEOT.questCandidateSymbol != null) {
            this.mActivity.confirmQuest(resultNextEOT.questCandidateSymbol);
            return;
        }
        if (this.mActivity.processEngineResult(resultNextEOT)) {
            activate(false);
        } else {
            setBtnEOTQuestVisibilityByEngine();
        }
        if (z || (flag = this.mActivity.game.getFlag(key)) == null || !flag.value) {
            return;
        }
        this.mActivity.showStory(2);
    }
}
