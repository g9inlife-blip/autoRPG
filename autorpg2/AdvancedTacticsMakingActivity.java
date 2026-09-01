package com.shirobakama.autorpg2;

import android.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.shirobakama.autorpg2.AlertDialogFragment;
import com.shirobakama.autorpg2.ConfirmationDialogFragment;
import com.shirobakama.autorpg2.entity.AdvancedTactics;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.GameFlag;
import com.shirobakama.autorpg2.entity.Inventory;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.autorpg2.entity.Monster;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.entity.Skill;
import com.shirobakama.autorpg2.p001db.Persister;
import com.shirobakama.autorpg2.repo.ItemRepository;
import com.shirobakama.autorpg2.repo.MonsterDb;
import com.shirobakama.autorpg2.repo.MonsterRepository;
import com.shirobakama.autorpg2.repo.SkillRepository;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.autorpg2.util.TypeUtil;
import com.shirobakama.autorpg2.view.HelpDialogFragment;
import com.shirobakama.autorpg2.view.LabelValueItem;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class AdvancedTacticsMakingActivity extends FragmentActivity implements View.OnClickListener, ConfirmationDialogFragment.OnConfirmationListener {
    private static final String EXTRA_TARGET_CHAR_ID = "target.char.id";
    private static final int OPERATION_DELETE = 2;
    private static final int OPERATION_EDIT = 1;
    private static final int OPERATION_INSERT = 3;
    private static final int OPERATION_MOVE_DOWN = 5;
    private static final int OPERATION_MOVE_UP = 4;
    private static final String STATE_ADVANCED_TACTICS = "advanced_tactics";
    protected static final String TAG = "adv-tactics-making";
    private Button mBtnAddTactics;
    protected boolean mDialogShowing = false;
    protected List<Item> mEquippableItems;
    protected GameContext mGame;
    private ListView mLvTactics;
    private int mMaxTacticsCount;
    protected List<Monster> mMonsters;
    protected List<Skill> mSkills;
    protected SparseArray<Skill> mSkillsForId;
    protected List<AdvancedTactics.TacticsComposition> mTacticsCompositions;
    private ArrayList<LabelValueItem> mTacticsOperationItems;
    private int mTargetCharId;
    private TextView mTvTacticsCount;
    protected List<Item> mUseableItems;

    @Override // com.shirobakama.autorpg2.ConfirmationDialogFragment.OnConfirmationListener
    public void onConfirmationCancel(int i, int i2, int i3, Bundle bundle) {
    }

    @Override // com.shirobakama.autorpg2.ConfirmationDialogFragment.OnConfirmationListener
    public void onConfirmationNeutral(int i, int i2, int i3, Bundle bundle) {
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
    }

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        List<AdvancedTactics> advancedTactics;
        super.onCreate(bundle);
        DeviceUtil.setLocaleIfNeeded(this, null);
        setContentView(C0380R.layout.advanced_tactics_making);
        DeviceUtil.handleOrientation(this, null);
        this.mTargetCharId = getIntent().getIntExtra(EXTRA_TARGET_CHAR_ID, 0);
        this.mBtnAddTactics = (Button) findViewById(C0380R.id.btnAddTactics);
        this.mBtnAddTactics.setOnClickListener(this);
        findViewById(C0380R.id.btnOK).setOnClickListener(this);
        findViewById(C0380R.id.btnCancel).setOnClickListener(this);
        findViewById(C0380R.id.btnHelp).setOnClickListener(this);
        this.mTacticsOperationItems = new ArrayList<>();
        this.mTacticsOperationItems.add(new LabelValueItem(1, getString(C0380R.string.lbl_adv_tactics_edit)));
        this.mTacticsOperationItems.add(new LabelValueItem(2, getString(C0380R.string.lbl_adv_tactics_delete)));
        this.mTacticsOperationItems.add(new LabelValueItem(3, getString(C0380R.string.lbl_adv_tactics_insert)));
        this.mTacticsOperationItems.add(new LabelValueItem(4, getString(C0380R.string.lbl_inn_char_menu_move_upper)));
        this.mTacticsOperationItems.add(new LabelValueItem(5, getString(C0380R.string.lbl_inn_char_menu_move_downer)));
        if (bundle != null) {
            this.mGame = (GameContext) bundle.getParcelable("game");
            this.mGame.calcCharacterStatus(this);
        } else {
            this.mGame = GameContext.game;
            GameContext.game = null;
        }
        if (this.mTargetCharId == 0) {
            this.mTargetCharId = this.mGame.characters.get(0).f106id;
        }
        if (bundle != null) {
            advancedTactics = bundle.getParcelableArrayList("advanced_tactics");
        } else {
            advancedTactics = new Persister(this).readAdvancedTactics(this.mTargetCharId);
        }
        this.mTacticsCompositions = AdvancedTactics.TacticsComposition.fromAdvancedTacticsList(advancedTactics);
        TreeMap treeMap = new TreeMap();
        for (GameFlag gameFlag : this.mGame.flags.values()) {
            if (gameFlag.type == GameFlag.FlagType.MONSTER || gameFlag.type == GameFlag.FlagType.MONSTER_WIN) {
                Monster monsterBySymbol = MonsterRepository.getMonsterBySymbol(this, gameFlag.name);
                if (monsterBySymbol != null) {
                    if (monsterBySymbol.f105id == 1560) {
                        monsterBySymbol = MonsterRepository.getMonster(this, 1570);
                    } else if (monsterBySymbol.f105id == 1580) {
                        monsterBySymbol = MonsterRepository.getMonster(this, MonsterDb.MONSTER_DARK_LOAD_NORMAL_GHOST);
                    }
                    treeMap.put(Integer.valueOf(monsterBySymbol.f105id), monsterBySymbol);
                }
            }
        }
        this.mMonsters = new ArrayList(treeMap.values());
        TreeMap treeMap2 = new TreeMap();
        for (Inventory inventory : this.mGame.inventories) {
            treeMap2.put(Integer.valueOf(inventory.itemId), inventory.getBaseItem(this));
        }
        for (AdvancedTactics advancedTactics2 : advancedTactics) {
            if (advancedTactics2.action == AdvancedTactics.TacticsAction.EQUIP_ITEM || advancedTactics2.action == AdvancedTactics.TacticsAction.USE_ITEM) {
                Item item = ItemRepository.getItem(this, advancedTactics2.targetId);
                if (item != null) {
                    treeMap2.put(Integer.valueOf(advancedTactics2.targetId), item);
                }
            }
        }
        this.mUseableItems = new ArrayList();
        this.mEquippableItems = new ArrayList();
        for (Item item2 : treeMap2.values()) {
            if (item2.getHpRestoreEffect() != null || item2.getMpRestoreEffect() != null) {
                this.mUseableItems.add(item2);
            }
            if (item2.equipable) {
                this.mEquippableItems.add(item2);
            }
        }
        PlayerChar playerChar = this.mGame.characters.get(this.mGame.getPlayerCharIndex(this.mTargetCharId));
        ArrayList<Integer> skillIds = playerChar.getSkillIds();
        this.mSkills = new ArrayList();
        this.mSkillsForId = new SparseArray<>();
        Iterator<Integer> it = skillIds.iterator();
        while (it.hasNext()) {
            Skill skill = SkillRepository.getSkill(this, it.next().intValue());
            if (skill.context != Skill.SkillContext.ADVENTURE) {
                this.mSkills.add(skill);
                this.mSkillsForId.append(skill.f107id, skill);
            }
        }
        new Persister(this).readSkillCustomizationForSkills(playerChar.f106id, this.mSkills);
        Iterator<AdvancedTactics.TacticsComposition> it2 = this.mTacticsCompositions.iterator();
        while (it2.hasNext()) {
            it2.next().updateDescription(this, this.mGame, this.mSkillsForId);
        }
        this.mMaxTacticsCount = playerChar.intl;
        ((TextView) findViewById(C0380R.id.tvCharacterName)).setText(playerChar.name);
        this.mTvTacticsCount = (TextView) findViewById(C0380R.id.tvTacticsCount);
        showTacticsCount();
        this.mLvTactics = (ListView) findViewById(C0380R.id.lvAdvancedTactics);
        this.mLvTactics.setAdapter((ListAdapter) new TacticsListAdapter(this, this.mTacticsCompositions));
        this.mLvTactics.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.shirobakama.autorpg2.AdvancedTacticsMakingActivity.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (i < 0 || i >= AdvancedTacticsMakingActivity.this.mTacticsCompositions.size()) {
                    return;
                }
                AdvancedTacticsMakingActivity.this.selectTacticsOperation(i);
            }
        });
        setResult(0);
        this.mDialogShowing = false;
    }

    private boolean canShowDialog() {
        if (this.mDialogShowing) {
            return false;
        }
        this.mDialogShowing = true;
        return true;
    }

    public static class TacticsListAdapter extends ArrayAdapter<AdvancedTactics.TacticsComposition> {
        private LayoutInflater mInflater;

        public static class ViewHolder {
            public TextView tvDescription;
            public TextView tvNumber;
        }

        public TacticsListAdapter(Context context, List<AdvancedTactics.TacticsComposition> list) {
            super(context, 0, list);
            this.mInflater = LayoutInflater.from(context);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = this.mInflater.inflate(C0380R.layout.list_item_advanced_tactics, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.tvNumber = (TextView) view.findViewById(C0380R.id.tvNumber);
                viewHolder.tvDescription = (TextView) view.findViewById(C0380R.id.tvDescription);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            AdvancedTactics.TacticsComposition item = getItem(i);
            viewHolder.tvNumber.setText(getContext().getString(C0380R.string.lbl_adv_tactics_number, Integer.valueOf(i + 1)));
            viewHolder.tvDescription.setText(item.toString());
            return view;
        }
    }

    private void showTacticsCount() {
        this.mTvTacticsCount.setText(getString(C0380R.string.lbl_adv_tactics_total_number, new Object[]{Integer.valueOf(this.mMaxTacticsCount), Integer.valueOf(this.mTacticsCompositions.size())}));
        this.mBtnAddTactics.setEnabled(this.mTacticsCompositions.size() < this.mMaxTacticsCount);
    }

    protected void selectTacticsOperation(int i) {
        ArrayList<LabelValueItem> arrayList = new ArrayList<>();
        Iterator<LabelValueItem> it = this.mTacticsOperationItems.iterator();
        while (it.hasNext()) {
            LabelValueItem next = it.next();
            if (next.value == 4) {
                if (i > 0) {
                    arrayList.add(next);
                }
            } else if (next.value == 5) {
                if (i < this.mTacticsCompositions.size() - 1) {
                    arrayList.add(next);
                }
            } else {
                arrayList.add(next);
            }
        }
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setChoiceMode(0);
        decorator.setCancelable(true);
        decorator.setLabelValueItems(arrayList);
        decorator.args().putInt("position", i);
        decorator.decorate(new TacticsOperationSelectDialogFragment()).show(getSupportFragmentManager());
    }

    public static class TacticsOperationSelectDialogFragment extends AlertDialogFragment {
        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        protected void onClick(DialogInterface dialogInterface, int i) {
            if (i == 0) {
                int i2 = getArguments().getInt("position");
                AdvancedTacticsMakingActivity advancedTacticsMakingActivity = (AdvancedTacticsMakingActivity) getActivity();
                LabelValueItem labelValueItem = this.items.get(this.whichItem);
                if (labelValueItem.value == 1) {
                    advancedTacticsMakingActivity.showTactics(i2);
                    return;
                }
                if (labelValueItem.value == 2) {
                    advancedTacticsMakingActivity.confirmDeleteTactics(i2);
                    return;
                }
                if (labelValueItem.value == 3) {
                    advancedTacticsMakingActivity.insertTactics(i2);
                } else if (labelValueItem.value == 4) {
                    advancedTacticsMakingActivity.moveTactics(i2, true);
                } else if (labelValueItem.value == 5) {
                    advancedTacticsMakingActivity.moveTactics(i2, false);
                }
            }
        }
    }

    protected void showTactics(int i) {
        if (canShowDialog()) {
            TacticsEditDialogFragment.newInstance(i).show(getSupportFragmentManager(), "edit_tactics");
        }
    }

    public void insertTactics(int i) {
        AdvancedTactics advancedTactics = new AdvancedTactics();
        advancedTactics.setDefault();
        AdvancedTactics.TacticsComposition tacticsComposition = new AdvancedTactics.TacticsComposition();
        tacticsComposition.add(advancedTactics);
        tacticsComposition.updateDescription(this, this.mGame, this.mSkillsForId);
        this.mTacticsCompositions.add(i, tacticsComposition);
        showTacticsCount();
        ((ArrayAdapter) this.mLvTactics.getAdapter()).notifyDataSetChanged();
    }

    protected void moveTactics(int i, boolean z) {
        if (i == 0 && z) {
            return;
        }
        if (i < this.mTacticsCompositions.size() - 1 || z) {
            this.mTacticsCompositions.add(z ? i - 1 : i + 1, this.mTacticsCompositions.remove(i));
            ((ArrayAdapter) this.mLvTactics.getAdapter()).notifyDataSetChanged();
        }
    }

    protected void confirmDeleteTactics(int i) {
        String string = getString(C0380R.string.msg_adv_tactics_dlg_msg_delete, new Object[]{this.mTacticsCompositions.get(i).toString()});
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setTitle(C0380R.string.msg_adv_tactics_dlg_title_delete);
        decorator.setMessage(string);
        ConfirmationDialogFragment.show(decorator, this, 0, i);
    }

    @Override // com.shirobakama.autorpg2.ConfirmationDialogFragment.OnConfirmationListener
    public void onConfirmationOk(int i, int i2, int i3, Bundle bundle) {
        deleteTactics(i3);
    }

    protected void deleteTactics(int i) {
        if (i < 0 || i >= this.mTacticsCompositions.size()) {
            return;
        }
        this.mTacticsCompositions.remove(i);
        editCompleted();
    }

    static void setCharacterId(Intent intent, int i) {
        intent.putExtra(EXTRA_TARGET_CHAR_ID, i);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
    }

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("game", this.mGame);
        ArrayList<? extends Parcelable> arrayList = new ArrayList<>();
        Iterator<AdvancedTactics.TacticsComposition> it = this.mTacticsCompositions.iterator();
        while (it.hasNext()) {
            arrayList.addAll(it.next().tactics);
        }
        bundle.putParcelableArrayList("advanced_tactics", arrayList);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == 2131165236) {
            addTactics();
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

    private void addTactics() {
        if (this.mTacticsCompositions.size() >= this.mMaxTacticsCount) {
            return;
        }
        showTactics(-1);
    }

    protected void editCompleted() {
        showTacticsCount();
        ((ArrayAdapter) this.mLvTactics.getAdapter()).notifyDataSetChanged();
    }

    private void showHelpDialog() {
        HelpDialogFragment.show(this, getString(C0380R.string.msg_help_adv_tactics_making));
    }

    private void finishMaking() {
        ArrayList arrayList = new ArrayList();
        Iterator<AdvancedTactics.TacticsComposition> it = this.mTacticsCompositions.iterator();
        while (it.hasNext()) {
            for (AdvancedTactics advancedTactics : it.next().tactics) {
                advancedTactics.fighting = true;
                advancedTactics.charId = this.mTargetCharId;
                arrayList.add(advancedTactics);
            }
        }
        new Persister(this).writeAdvancedTactics(this.mTargetCharId, arrayList);
        Toast.makeText(this, C0380R.string.msg_adv_tactics_made, 0).show();
        setResult(-1);
        finish();
    }

    public static class TacticsEditDialogFragment extends DialogFragment {
        private static final String DIALOG_ARGS_TACTICS_POSITION = "tactics_position";
        private List<ConditionsViewHolder> mConditions;
        private LinearLayout mLlayConditions;
        private Spinner mSpnAction;
        private Spinner mSpnActionSubEquipItem;
        private Spinner mSpnActionSubSkill;
        private Spinner mSpnActionSubUseItem;
        private Spinner mSpnTarget;
        private Spinner mSpnTargetChar;
        private AdvancedTactics.TacticsComposition mTacticsComposition;
        private boolean mTargetCharVisible = false;
        private TableRow mTrTarget;
        private TableRow mTrTargetChar;

        public static TacticsEditDialogFragment newInstance(int i) {
            TacticsEditDialogFragment tacticsEditDialogFragment = new TacticsEditDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(DIALOG_ARGS_TACTICS_POSITION, i);
            tacticsEditDialogFragment.setArguments(bundle);
            return tacticsEditDialogFragment;
        }

        protected static class ConditionsViewHolder {
            public Button btnConditionRemove;
            public CheckBox chkConditionNot;
            public LinearLayout llayContainer;
            protected TacticsEditDialogFragment mFragment;
            public RadioButton radConditionAnd;
            public RadioButton radConditionOr;
            public RadioGroup radgConditionAndOr;
            public Spinner spnConditionChar;
            public Spinner spnConditionMain;
            public Spinner spnConditionSubFloorKind;
            public Spinner spnConditionSubHpMp;
            public Spinner spnConditionSubHpMpAsleep;
            public Spinner spnConditionValueFloor;
            public Spinner spnConditionValueHpMp;
            public Spinner spnConditionValueLevelDiff;
            public Spinner spnConditionValueMonster;
            public Spinner spnConditionValueMonsterAttr;
            public Spinner spnConditionValueMonsterType;
            public Spinner spnConditionValueNumberOfEnemy;
            public Spinner spnConditionValueProbability;

            public ConditionsViewHolder(TacticsEditDialogFragment tacticsEditDialogFragment) {
                this.mFragment = tacticsEditDialogFragment;
            }

            public void initialize(final AdvancedTacticsMakingActivity advancedTacticsMakingActivity, final LinearLayout linearLayout, int i, final AdvancedTactics advancedTactics) {
                this.llayContainer = linearLayout;
                this.radgConditionAndOr = (RadioGroup) linearLayout.findViewById(C0380R.id.radgConditionAndOr);
                this.radConditionAnd = (RadioButton) linearLayout.findViewById(C0380R.id.radConditionAnd);
                this.radConditionOr = (RadioButton) linearLayout.findViewById(C0380R.id.radConditionOr);
                this.spnConditionMain = (Spinner) linearLayout.findViewById(C0380R.id.spnConditionMain);
                this.spnConditionChar = (Spinner) linearLayout.findViewById(C0380R.id.spnConditionChar);
                this.spnConditionSubHpMp = (Spinner) linearLayout.findViewById(C0380R.id.spnConditionSubHpMp);
                this.spnConditionSubHpMpAsleep = (Spinner) linearLayout.findViewById(C0380R.id.spnConditionSubHpMpAsleep);
                this.spnConditionSubFloorKind = (Spinner) linearLayout.findViewById(C0380R.id.spnConditionSubFloorKind);
                this.spnConditionValueHpMp = (Spinner) linearLayout.findViewById(C0380R.id.spnConditionValueHpMp);
                this.spnConditionValueLevelDiff = (Spinner) linearLayout.findViewById(C0380R.id.spnConditionValueLevelDiff);
                this.spnConditionValueNumberOfEnemy = (Spinner) linearLayout.findViewById(C0380R.id.spnConditionValueNumberOfEnemy);
                this.spnConditionValueMonster = (Spinner) linearLayout.findViewById(C0380R.id.spnConditionValueMonster);
                this.spnConditionValueMonsterType = (Spinner) linearLayout.findViewById(C0380R.id.spnConditionValueMonsterType);
                this.spnConditionValueMonsterAttr = (Spinner) linearLayout.findViewById(C0380R.id.spnConditionValueMonsterAttr);
                this.spnConditionValueFloor = (Spinner) linearLayout.findViewById(C0380R.id.spnConditionValueFloor);
                this.spnConditionValueProbability = (Spinner) linearLayout.findViewById(C0380R.id.spnConditionValueProbability);
                this.chkConditionNot = (CheckBox) linearLayout.findViewById(C0380R.id.chkConditionNot);
                this.btnConditionRemove = (Button) linearLayout.findViewById(C0380R.id.btnConditionRemove);
                this.spnConditionMain.setAdapter((SpinnerAdapter) new ArrayAdapter(advancedTacticsMakingActivity, R.layout.simple_list_item_1, TacticsEditDialogFragment.getStringsFromEnums(advancedTacticsMakingActivity, AdvancedTactics.Condition.values())));
                this.spnConditionMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.shirobakama.autorpg2.AdvancedTacticsMakingActivity.TacticsEditDialogFragment.ConditionsViewHolder.1
                    @Override // android.widget.AdapterView.OnItemSelectedListener
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }

                    @Override // android.widget.AdapterView.OnItemSelectedListener
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i2, long j) {
                        advancedTactics.condition = AdvancedTactics.Condition.values()[i2];
                        ConditionsViewHolder.this.setVisibility(advancedTacticsMakingActivity, advancedTactics);
                    }
                });
                List<PlayerChar> list = advancedTacticsMakingActivity.mGame.characters;
                String[] strArr = new String[list.size()];
                for (int i2 = 0; i2 < list.size(); i2++) {
                    strArr[i2] = list.get(i2).name;
                }
                this.spnConditionChar.setAdapter((SpinnerAdapter) new ArrayAdapter(advancedTacticsMakingActivity, R.layout.simple_list_item_1, strArr));
                this.spnConditionSubFloorKind.setAdapter((SpinnerAdapter) new ArrayAdapter(advancedTacticsMakingActivity, R.layout.simple_list_item_1, new String[]{advancedTacticsMakingActivity.getString(AdvancedTactics.ConditionSub.UPPER_FLOOR.getStrId()), advancedTacticsMakingActivity.getString(AdvancedTactics.ConditionSub.LOWER_FLOOR.getStrId()), advancedTacticsMakingActivity.getString(AdvancedTactics.ConditionSub.SPECIFIC_FLOOR.getStrId())}));
                this.spnConditionSubHpMp.setAdapter((SpinnerAdapter) new ArrayAdapter(advancedTacticsMakingActivity, R.layout.simple_list_item_1, new String[]{advancedTacticsMakingActivity.getString(AdvancedTactics.ConditionSub.HP.getStrId()), advancedTacticsMakingActivity.getString(AdvancedTactics.ConditionSub.MP.getStrId())}));
                this.spnConditionSubHpMpAsleep.setAdapter((SpinnerAdapter) new ArrayAdapter(advancedTacticsMakingActivity, R.layout.simple_list_item_1, new String[]{advancedTacticsMakingActivity.getString(AdvancedTactics.ConditionSub.HP.getStrId()), advancedTacticsMakingActivity.getString(AdvancedTactics.ConditionSub.MP.getStrId()), advancedTacticsMakingActivity.getString(AdvancedTactics.ConditionSub.ASLEEP.getStrId())}));
                this.spnConditionSubHpMpAsleep.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.shirobakama.autorpg2.AdvancedTacticsMakingActivity.TacticsEditDialogFragment.ConditionsViewHolder.2
                    @Override // android.widget.AdapterView.OnItemSelectedListener
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }

                    @Override // android.widget.AdapterView.OnItemSelectedListener
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i3, long j) {
                        advancedTactics.conditionSub = i3 == 0 ? AdvancedTactics.ConditionSub.HP : i3 == 1 ? AdvancedTactics.ConditionSub.MP : AdvancedTactics.ConditionSub.ASLEEP;
                        ConditionsViewHolder.this.setVisibility(advancedTacticsMakingActivity, advancedTactics);
                    }
                });
                this.spnConditionValueHpMp.setAdapter((SpinnerAdapter) new ArrayAdapter(advancedTacticsMakingActivity, R.layout.simple_list_item_1, TacticsEditDialogFragment.getStringsFromEnums(advancedTacticsMakingActivity, AdvancedTactics.ConditionValueHpMp.values())));
                String[] strArr2 = new String[AdvancedTactics.LEVEL_DIFFS.length];
                for (int i3 = 0; i3 < AdvancedTactics.LEVEL_DIFFS.length; i3++) {
                    int i4 = AdvancedTactics.LEVEL_DIFFS[i3];
                    if (i4 == 0) {
                        strArr2[i3] = advancedTacticsMakingActivity.getString(C0380R.string.lbl_adv_tactics_cond_level_diff_val_greater);
                    } else {
                        strArr2[i3] = advancedTacticsMakingActivity.getString(C0380R.string.lbl_adv_tactics_cond_level_diff_val, new Object[]{Integer.valueOf(i4)});
                    }
                }
                this.spnConditionValueLevelDiff.setAdapter((SpinnerAdapter) new ArrayAdapter(advancedTacticsMakingActivity, R.layout.simple_list_item_1, strArr2));
                String[] strArr3 = new String[AdvancedTactics.ENEMY_NUMBERS.length];
                for (int i5 = 0; i5 < AdvancedTactics.ENEMY_NUMBERS.length; i5++) {
                    strArr3[i5] = advancedTacticsMakingActivity.getString(C0380R.string.lbl_adv_tactics_cond_num_of_enemy_val, new Object[]{Integer.valueOf(AdvancedTactics.ENEMY_NUMBERS[i5])});
                }
                this.spnConditionValueNumberOfEnemy.setAdapter((SpinnerAdapter) new ArrayAdapter(advancedTacticsMakingActivity, R.layout.simple_list_item_1, strArr3));
                Monster.MonsterType[] monsterTypeArrValues = Monster.MonsterType.values();
                String[] strArr4 = new String[monsterTypeArrValues.length];
                for (int i6 = 0; i6 < monsterTypeArrValues.length; i6++) {
                    strArr4[i6] = monsterTypeArrValues[i6].getString(advancedTacticsMakingActivity);
                }
                this.spnConditionValueMonsterType.setAdapter((SpinnerAdapter) new ArrayAdapter(advancedTacticsMakingActivity, R.layout.simple_list_item_1, strArr4));
                String[] strArr5 = new String[advancedTacticsMakingActivity.mMonsters.size()];
                for (int i7 = 0; i7 < advancedTacticsMakingActivity.mMonsters.size(); i7++) {
                    strArr5[i7] = advancedTacticsMakingActivity.mMonsters.get(i7).name;
                }
                this.spnConditionValueMonster.setAdapter((SpinnerAdapter) new ArrayAdapter(advancedTacticsMakingActivity, R.layout.simple_list_item_1, strArr5));
                AdvancedTactics.ConditionValueEnemyAttr[] conditionValueEnemyAttrArrValues = AdvancedTactics.ConditionValueEnemyAttr.values();
                String[] strArr6 = new String[conditionValueEnemyAttrArrValues.length];
                for (int i8 = 0; i8 < conditionValueEnemyAttrArrValues.length; i8++) {
                    strArr6[i8] = advancedTacticsMakingActivity.getString(conditionValueEnemyAttrArrValues[i8].getStrId());
                }
                this.spnConditionValueMonsterAttr.setAdapter((SpinnerAdapter) new ArrayAdapter(advancedTacticsMakingActivity, R.layout.simple_list_item_1, strArr6));
                String[] strArr7 = new String[20];
                int i9 = 0;
                while (i9 < strArr7.length) {
                    int i10 = i9 + 1;
                    strArr7[i9] = advancedTacticsMakingActivity.getString(C0380R.string.lbl_tactics_use_item_floor_value, new Object[]{Integer.valueOf(i10)});
                    i9 = i10;
                }
                this.spnConditionValueFloor.setAdapter((SpinnerAdapter) new ArrayAdapter(advancedTacticsMakingActivity, R.layout.simple_list_item_1, strArr7));
                AdvancedTactics.ConditionValueProbability[] conditionValueProbabilityArrValues = AdvancedTactics.ConditionValueProbability.values();
                String[] strArr8 = new String[conditionValueProbabilityArrValues.length];
                for (int i11 = 0; i11 < conditionValueProbabilityArrValues.length; i11++) {
                    strArr8[i11] = advancedTacticsMakingActivity.getString(conditionValueProbabilityArrValues[i11].getStrId());
                }
                this.spnConditionValueProbability.setAdapter((SpinnerAdapter) new ArrayAdapter(advancedTacticsMakingActivity, R.layout.simple_list_item_1, strArr8));
                this.btnConditionRemove.setOnClickListener(new View.OnClickListener() { // from class: com.shirobakama.autorpg2.AdvancedTacticsMakingActivity.TacticsEditDialogFragment.ConditionsViewHolder.3
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        int iIndexOfChild = ((LinearLayout) linearLayout.getParent()).indexOfChild(linearLayout);
                        if (iIndexOfChild >= 0) {
                            ConditionsViewHolder.this.mFragment.removeCondition(iIndexOfChild);
                        }
                    }
                });
            }

            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            /* JADX WARN: Removed duplicated region for block: B:38:0x00ed  */
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public void setSpinner(com.shirobakama.autorpg2.AdvancedTacticsMakingActivity r7, com.shirobakama.autorpg2.entity.AdvancedTactics r8) {
                /*
                    Method dump skipped, instructions count: 338
                    To view this dump change 'Code comments level' option to 'DEBUG'
                */
                throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.AdvancedTacticsMakingActivity.TacticsEditDialogFragment.ConditionsViewHolder.setSpinner(com.shirobakama.autorpg2.AdvancedTacticsMakingActivity, com.shirobakama.autorpg2.entity.AdvancedTactics):void");
            }

            public void setVisibility(AdvancedTacticsMakingActivity advancedTacticsMakingActivity, AdvancedTactics advancedTactics) {
                boolean z;
                boolean z2;
                boolean z3;
                boolean z4;
                boolean z5;
                boolean z6;
                boolean z7;
                boolean z8;
                boolean z9;
                boolean z10;
                boolean z11;
                boolean z12 = true;
                switch (advancedTactics.condition) {
                    case ANY_ENEMY:
                    case ALL_ENEMY:
                        if (advancedTactics.conditionSub != AdvancedTactics.ConditionSub.HP && advancedTactics.conditionSub != AdvancedTactics.ConditionSub.MP) {
                            z = false;
                            z2 = true;
                            z12 = false;
                            z3 = false;
                            z4 = false;
                            z5 = false;
                            z6 = false;
                            z7 = false;
                            z8 = false;
                            z9 = false;
                            z10 = false;
                            z11 = false;
                            break;
                        } else {
                            z = false;
                            z2 = true;
                            z12 = false;
                            z3 = false;
                            z4 = false;
                            z5 = true;
                            z6 = false;
                            z7 = false;
                            z8 = false;
                            z9 = false;
                            z10 = false;
                            z11 = false;
                            break;
                        }
                        break;
                    case SPECIFIC_CHARACTER:
                        z = true;
                        z2 = false;
                        z3 = false;
                        z4 = false;
                        z5 = true;
                        z6 = false;
                        z7 = false;
                        z8 = false;
                        z9 = false;
                        z10 = false;
                        z11 = false;
                        break;
                    case ALL_CHARACTERS:
                    case ANY_CHARACTER:
                    case OWN:
                    case TWO_CHARACTERS:
                        z = false;
                        z2 = false;
                        z3 = false;
                        z4 = false;
                        z5 = true;
                        z6 = false;
                        z7 = false;
                        z8 = false;
                        z9 = false;
                        z10 = false;
                        z11 = false;
                        break;
                    case LEVEL_DIFFERENCE:
                        z = false;
                        z2 = false;
                        z12 = false;
                        z3 = false;
                        z4 = true;
                        z5 = false;
                        z6 = false;
                        z7 = false;
                        z8 = false;
                        z9 = false;
                        z10 = false;
                        z11 = false;
                        break;
                    case NUMBER_OF_ENEMY:
                        z = false;
                        z2 = false;
                        z12 = false;
                        z3 = false;
                        z4 = false;
                        z5 = false;
                        z6 = true;
                        z7 = false;
                        z8 = false;
                        z9 = false;
                        z10 = false;
                        z11 = false;
                        break;
                    case ENEMY_TYPE:
                        z = false;
                        z2 = false;
                        z12 = false;
                        z3 = false;
                        z4 = false;
                        z5 = false;
                        z6 = false;
                        z7 = true;
                        z8 = false;
                        z9 = false;
                        z10 = false;
                        z11 = false;
                        break;
                    case ENEMY_SPECIFIC:
                        z = false;
                        z2 = false;
                        z12 = false;
                        z3 = false;
                        z4 = false;
                        z5 = false;
                        z6 = false;
                        z7 = false;
                        z8 = true;
                        z9 = false;
                        z10 = false;
                        z11 = false;
                        break;
                    case ENEMY_ATTR:
                        z = false;
                        z2 = false;
                        z12 = false;
                        z3 = false;
                        z4 = false;
                        z5 = false;
                        z6 = false;
                        z7 = false;
                        z8 = false;
                        z9 = true;
                        z10 = false;
                        z11 = false;
                        break;
                    case FLOOR:
                        z = false;
                        z2 = false;
                        z12 = false;
                        z3 = true;
                        z4 = false;
                        z5 = false;
                        z6 = false;
                        z7 = false;
                        z8 = false;
                        z9 = false;
                        z10 = true;
                        z11 = false;
                        break;
                    case PROBABILITY:
                        z = false;
                        z2 = false;
                        z12 = false;
                        z3 = false;
                        z4 = false;
                        z5 = false;
                        z6 = false;
                        z7 = false;
                        z8 = false;
                        z9 = false;
                        z10 = false;
                        z11 = true;
                        break;
                    default:
                        z = false;
                        z2 = false;
                        z12 = false;
                        z3 = false;
                        z4 = false;
                        z5 = false;
                        z6 = false;
                        z7 = false;
                        z8 = false;
                        z9 = false;
                        z10 = false;
                        z11 = false;
                        break;
                }
                this.spnConditionChar.setVisibility(z ? 0 : 8);
                this.spnConditionSubHpMp.setVisibility(z12 ? 0 : 8);
                this.spnConditionSubHpMpAsleep.setVisibility(z2 ? 0 : 8);
                this.spnConditionSubFloorKind.setVisibility(z3 ? 0 : 8);
                this.spnConditionValueLevelDiff.setVisibility(z4 ? 0 : 8);
                this.spnConditionValueHpMp.setVisibility(z5 ? 0 : 8);
                this.spnConditionValueNumberOfEnemy.setVisibility(z6 ? 0 : 8);
                this.spnConditionValueMonsterType.setVisibility(z7 ? 0 : 8);
                this.spnConditionValueMonster.setVisibility(z8 ? 0 : 8);
                this.spnConditionValueMonsterAttr.setVisibility(z9 ? 0 : 8);
                this.spnConditionValueFloor.setVisibility(z10 ? 0 : 8);
                this.spnConditionValueProbability.setVisibility(z11 ? 0 : 8);
            }

            public void setFirst(boolean z) {
                if (z) {
                    this.radgConditionAndOr.setVisibility(8);
                    this.btnConditionRemove.setVisibility(4);
                } else {
                    this.radgConditionAndOr.setVisibility(0);
                    this.btnConditionRemove.setVisibility(0);
                }
            }
        }

        @Override // android.support.v4.app.DialogFragment
        @SuppressLint({"InflateParams"})
        public Dialog onCreateDialog(Bundle bundle) {
            List<AdvancedTactics.TacticsComposition> list = ((AdvancedTacticsMakingActivity) getActivity()).mTacticsCompositions;
            int i = getArguments().getInt(DIALOG_ARGS_TACTICS_POSITION);
            if (i >= 0 && i < list.size()) {
                this.mTacticsComposition = list.get(i).copy();
            } else {
                AdvancedTactics advancedTactics = new AdvancedTactics();
                advancedTactics.setDefault();
                this.mTacticsComposition = new AdvancedTactics.TacticsComposition();
                this.mTacticsComposition.add(advancedTactics);
            }
            View viewInflate = getActivity().getLayoutInflater().inflate(C0380R.layout.advanced_tactics_detail_dialog, (ViewGroup) null);
            this.mLlayConditions = (LinearLayout) viewInflate.findViewById(C0380R.id.llayConditions);
            initializeConditions();
            this.mSpnTarget = (Spinner) viewInflate.findViewById(C0380R.id.spnTarget);
            this.mTrTarget = (TableRow) viewInflate.findViewById(C0380R.id.trTarget);
            this.mSpnTargetChar = (Spinner) viewInflate.findViewById(C0380R.id.spnTargetChar);
            this.mTrTargetChar = (TableRow) viewInflate.findViewById(C0380R.id.trTargetChar);
            this.mSpnAction = (Spinner) viewInflate.findViewById(C0380R.id.spnAction);
            this.mSpnActionSubUseItem = (Spinner) viewInflate.findViewById(C0380R.id.spnActionSubUseItem);
            this.mSpnActionSubEquipItem = (Spinner) viewInflate.findViewById(C0380R.id.spnActionSubEquipItem);
            this.mSpnActionSubSkill = (Spinner) viewInflate.findViewById(C0380R.id.spnActionSubSkill);
            initializeSpinners();
            viewInflate.findViewById(C0380R.id.btnConditionAdd).setOnClickListener(new View.OnClickListener() { // from class: com.shirobakama.autorpg2.AdvancedTacticsMakingActivity.TacticsEditDialogFragment.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    TacticsEditDialogFragment.this.addCondition();
                }
            });
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(viewInflate);
            builder.setTitle(C0380R.string.msg_adv_tactics_dlg_title_edit);
            builder.setPositiveButton(R.string.ok, (DialogInterface.OnClickListener) null);
            builder.setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null);
            return builder.create();
        }

        @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
        public void onStart() {
            Button button;
            super.onStart();
            AlertDialog alertDialog = (AlertDialog) getDialog();
            if (alertDialog == null || (button = alertDialog.getButton(-1)) == null) {
                return;
            }
            button.setOnClickListener(new View.OnClickListener() { // from class: com.shirobakama.autorpg2.AdvancedTacticsMakingActivity.TacticsEditDialogFragment.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    String strComplete = TacticsEditDialogFragment.this.complete();
                    if (strComplete != null) {
                        Toast.makeText(TacticsEditDialogFragment.this.getActivity(), strComplete, 1).show();
                    } else {
                        TacticsEditDialogFragment.this.dismiss();
                    }
                }
            });
        }

        @Override // android.support.v4.app.Fragment
        public void onPause() {
            ((AdvancedTacticsMakingActivity) getActivity()).mDialogShowing = false;
            super.onPause();
        }

        protected static String[] getStringsFromEnums(Context context, AdvancedTactics.StrIdHolder[] strIdHolderArr) {
            String[] strArr = new String[strIdHolderArr.length];
            for (int i = 0; i < strIdHolderArr.length; i++) {
                strArr[i] = context.getString(strIdHolderArr[i].getStrId());
            }
            return strArr;
        }

        private void initializeConditions() {
            this.mConditions = new ArrayList();
            int i = 0;
            while (i < this.mTacticsComposition.tactics.size()) {
                addConditionToView(this.mTacticsComposition.tactics.get(i)).setFirst(i == 0);
                i++;
            }
        }

        private ConditionsViewHolder addConditionToView(AdvancedTactics advancedTactics) {
            int size = this.mConditions.size();
            AdvancedTacticsMakingActivity advancedTacticsMakingActivity = (AdvancedTacticsMakingActivity) getActivity();
            ConditionsViewHolder conditionsViewHolder = new ConditionsViewHolder(this);
            conditionsViewHolder.initialize(advancedTacticsMakingActivity, (LinearLayout) advancedTacticsMakingActivity.getLayoutInflater().inflate(C0380R.layout.advanced_tactics_condition, (ViewGroup) this.mLlayConditions, false), size, advancedTactics);
            this.mConditions.add(conditionsViewHolder);
            this.mLlayConditions.addView(conditionsViewHolder.llayContainer);
            conditionsViewHolder.setSpinner(advancedTacticsMakingActivity, advancedTactics);
            conditionsViewHolder.setVisibility(advancedTacticsMakingActivity, advancedTactics);
            return conditionsViewHolder;
        }

        protected void addCondition() {
            AdvancedTactics advancedTactics = new AdvancedTactics();
            advancedTactics.setDefault();
            this.mTacticsComposition.add(advancedTactics);
            int size = this.mConditions.size();
            if (size > 0) {
                this.mConditions.get(size - 1).setFirst(size == 1);
            }
            addConditionToView(advancedTactics).setFirst(size + 1 <= 1);
        }

        protected void removeCondition(int i) {
            if (this.mTacticsComposition.tactics.size() <= 1) {
                return;
            }
            this.mTacticsComposition.tactics.remove(i);
            this.mConditions.remove(i);
            this.mLlayConditions.removeViewAt(i);
            if (i == 0) {
                this.mConditions.get(0).setFirst(true);
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        private void initializeSpinners() {
            int i;
            int i2;
            int i3;
            this.mSpnTarget.setAdapter((SpinnerAdapter) new ArrayAdapter(getActivity(), R.layout.simple_list_item_1, getStringsFromEnums(getActivity(), AdvancedTactics.Target.VALUES)));
            this.mSpnTarget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.shirobakama.autorpg2.AdvancedTacticsMakingActivity.TacticsEditDialogFragment.3
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> adapterView, View view, int i4, long j) {
                    if (i4 < 0 || i4 >= AdvancedTactics.Target.VALUES.length) {
                        return;
                    }
                    TacticsEditDialogFragment.this.setTargetSpinnerVisibility(AdvancedTactics.Target.VALUES[i4]);
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> adapterView) {
                    TacticsEditDialogFragment.this.setTargetSpinnerVisibility(AdvancedTactics.Target.NONE);
                }
            });
            this.mSpnAction.setAdapter((SpinnerAdapter) new ArrayAdapter(getActivity(), R.layout.simple_list_item_1, getStringsFromEnums(getActivity(), AdvancedTactics.TacticsAction.SELECTABLE_ACTIONS)));
            this.mSpnAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.shirobakama.autorpg2.AdvancedTacticsMakingActivity.TacticsEditDialogFragment.4
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> adapterView, View view, int i4, long j) {
                    if (i4 < 0 || i4 >= AdvancedTactics.TacticsAction.SELECTABLE_ACTIONS.length) {
                        return;
                    }
                    TacticsEditDialogFragment.this.setSpinnerVisibility(AdvancedTactics.TacticsAction.SELECTABLE_ACTIONS[i4]);
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> adapterView) {
                    TacticsEditDialogFragment.this.setSpinnerVisibility(AdvancedTactics.TacticsAction.ATTACK);
                }
            });
            AdvancedTacticsMakingActivity advancedTacticsMakingActivity = (AdvancedTacticsMakingActivity) getActivity();
            String[] strArr = new String[advancedTacticsMakingActivity.mUseableItems.size() + 2];
            int i4 = 0;
            strArr[0] = advancedTacticsMakingActivity.getString(C0380R.string.msg_adv_tactics_use_item_hp_restore);
            strArr[1] = advancedTacticsMakingActivity.getString(C0380R.string.msg_adv_tactics_use_item_mp_restore);
            for (int i5 = 0; i5 < advancedTacticsMakingActivity.mUseableItems.size(); i5++) {
                strArr[i5 + 2] = advancedTacticsMakingActivity.mUseableItems.get(i5).name;
            }
            this.mSpnActionSubUseItem.setAdapter((SpinnerAdapter) new ArrayAdapter(getActivity(), R.layout.simple_list_item_1, strArr));
            String[] strArr2 = new String[advancedTacticsMakingActivity.mEquippableItems.size()];
            for (int i6 = 0; i6 < advancedTacticsMakingActivity.mEquippableItems.size(); i6++) {
                strArr2[i6] = advancedTacticsMakingActivity.mEquippableItems.get(i6).name;
            }
            this.mSpnActionSubEquipItem.setAdapter((SpinnerAdapter) new ArrayAdapter(getActivity(), R.layout.simple_list_item_1, strArr2));
            String[] strArr3 = new String[advancedTacticsMakingActivity.mSkills.size()];
            for (int i7 = 0; i7 < strArr3.length; i7++) {
                Skill skill = advancedTacticsMakingActivity.mSkills.get(i7);
                if (skill.skillCustomization == null) {
                    strArr3[i7] = skill.name;
                } else {
                    strArr3[i7] = skill.skillCustomization.skillName;
                }
            }
            this.mSpnActionSubSkill.setAdapter((SpinnerAdapter) new ArrayAdapter(getActivity(), R.layout.simple_list_item_1, strArr3));
            List<PlayerChar> list = advancedTacticsMakingActivity.mGame.characters;
            String[] strArr4 = new String[list.size()];
            for (int i8 = 0; i8 < list.size(); i8++) {
                strArr4[i8] = list.get(i8).name;
            }
            this.mSpnTargetChar.setAdapter((SpinnerAdapter) new ArrayAdapter(advancedTacticsMakingActivity, R.layout.simple_list_item_1, strArr4));
            AdvancedTactics advancedTactics = this.mTacticsComposition.tactics.get(0);
            this.mSpnAction.setSelection(TypeUtil.getEnumIndex(AdvancedTactics.TacticsAction.SELECTABLE_ACTIONS, advancedTactics.action, 0));
            this.mSpnTarget.setSelection(advancedTactics.target.ordinal());
            int playerCharIndex = advancedTacticsMakingActivity.mGame.getPlayerCharIndex(advancedTactics.conditionCharId);
            if (playerCharIndex < 0) {
                playerCharIndex = 0;
            }
            this.mSpnTargetChar.setSelection(playerCharIndex);
            switch (advancedTactics.action) {
                case ATTACK:
                case CONDITION_AND:
                case CONDITION_OR:
                case NONE:
                case RUNNING:
                default:
                    i = 0;
                    i2 = 0;
                    break;
                case USE_ITEM:
                    if (advancedTactics.actionSub == AdvancedTactics.TacticsActionSub.HP_RESTORE) {
                        i = 0;
                        i2 = 0;
                        break;
                    } else if (advancedTactics.actionSub == AdvancedTactics.TacticsActionSub.MP_RESTORE) {
                        i = 0;
                        i2 = 0;
                        i4 = 1;
                        break;
                    } else {
                        int i9 = 0;
                        while (true) {
                            if (i9 >= advancedTacticsMakingActivity.mUseableItems.size()) {
                                i3 = 0;
                            } else if (advancedTacticsMakingActivity.mUseableItems.get(i9).f97id == advancedTactics.targetId) {
                                i3 = i9 + 2;
                            } else {
                                i9++;
                            }
                        }
                        i4 = i3;
                        i = 0;
                        i2 = 0;
                        break;
                    }
                case EQUIP_ITEM:
                    i2 = 0;
                    while (true) {
                        if (i2 >= advancedTacticsMakingActivity.mEquippableItems.size()) {
                            i2 = 0;
                        } else if (advancedTacticsMakingActivity.mEquippableItems.get(i2).f97id != advancedTactics.targetId) {
                            i2++;
                        }
                    }
                    i = 0;
                    break;
                case USE_SKILL:
                    for (int i10 = 0; i10 < advancedTacticsMakingActivity.mSkills.size(); i10++) {
                        if (advancedTacticsMakingActivity.mSkills.get(i10).f107id == advancedTactics.targetId) {
                            i = i10;
                            i2 = 0;
                            break;
                        }
                    }
                    i = 0;
                    i2 = 0;
                    break;
            }
            this.mSpnActionSubUseItem.setSelection(i4);
            this.mSpnActionSubEquipItem.setSelection(i2);
            this.mSpnActionSubSkill.setSelection(i);
            setTargetSpinnerVisibility(advancedTactics.target);
            setSpinnerVisibility(advancedTactics.action);
        }

        protected void setSpinnerVisibility(AdvancedTactics.TacticsAction tacticsAction) {
            boolean z;
            boolean z2;
            boolean z3;
            boolean z4 = true;
            switch (tacticsAction) {
                case ATTACK:
                    z = false;
                    z4 = false;
                    z2 = false;
                    z3 = true;
                    break;
                case CONDITION_AND:
                case CONDITION_OR:
                case NONE:
                case RUNNING:
                default:
                    z = false;
                    z4 = false;
                    z2 = false;
                    z3 = false;
                    break;
                case USE_ITEM:
                    z = false;
                    z2 = false;
                    z3 = false;
                    break;
                case EQUIP_ITEM:
                    z = true;
                    z4 = false;
                    z2 = false;
                    z3 = false;
                    break;
                case USE_SKILL:
                    z = false;
                    z4 = false;
                    z2 = true;
                    z3 = true;
                    break;
            }
            this.mSpnActionSubUseItem.setVisibility(z4 ? 0 : 8);
            this.mSpnActionSubEquipItem.setVisibility(z ? 0 : 8);
            this.mSpnActionSubSkill.setVisibility(z2 ? 0 : 8);
            this.mTrTarget.setVisibility(z3 ? 0 : 8);
            this.mTrTargetChar.setVisibility((z3 && this.mTargetCharVisible) ? 0 : 8);
        }

        protected void setTargetSpinnerVisibility(AdvancedTactics.Target target) {
            this.mTargetCharVisible = target == AdvancedTactics.Target.SPECIFIC_CHAR;
            this.mTrTargetChar.setVisibility(this.mTargetCharVisible ? 0 : 8);
        }

        private boolean isPartyCharOnly(Skill skill) {
            return skill.type == Skill.SkillType.CURE || skill.type == Skill.SkillType.STATUS || skill.f107id == 30160;
        }

        /* JADX WARN: Removed duplicated region for block: B:24:0x00c4  */
        /* JADX WARN: Removed duplicated region for block: B:25:0x00cb  */
        /* JADX WARN: Removed duplicated region for block: B:28:0x00d7  */
        /* JADX WARN: Removed duplicated region for block: B:29:0x00dc  */
        /* JADX WARN: Removed duplicated region for block: B:34:0x00f1  */
        /* JADX WARN: Removed duplicated region for block: B:87:0x0239  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        protected java.lang.String complete() {
            /*
                Method dump skipped, instructions count: 812
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.AdvancedTacticsMakingActivity.TacticsEditDialogFragment.complete():java.lang.String");
        }
    }
}
