package com.shirobakama.autorpg2;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.shirobakama.autorpg2.AlertDialogFragment;
import com.shirobakama.autorpg2.CharacterDetailDialogFragment;
import com.shirobakama.autorpg2.ConfirmationDialogFragment;
import com.shirobakama.autorpg2.OptionMenuDialogFragment;
import com.shirobakama.autorpg2.SkillListDialogFragment;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.entity.Skill;
import com.shirobakama.autorpg2.p001db.Persister;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.autorpg2.view.CharViewHolder;
import com.shirobakama.autorpg2.view.HelpDialogFragment;
import com.shirobakama.autorpg2.view.LabelValueItem;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class TavernActivity extends FragmentActivity implements View.OnClickListener, CharacterDetailDialogFragment.CharacterDetailCallback, SkillListDialogFragment.SkillListCallback, ConfirmationDialogFragment.OnConfirmationListener, OptionMenuDialogFragment.Callback {
    private static final int MENU_CHAR_SKILL = 104;
    private static final int MENU_CHAR_STATUS = 103;
    private static final int REQUEST_CHARACTER_MAKING = 1;
    private static final String STATE_CHECKED = "checked";
    private static final int TAG_DELETE = 0;
    private static final int TAG_NOT_MAX = 1;
    private CharacterAdapter mCharacterAdapter;
    protected GameContext mGame;
    protected LayoutInflater mInflater;
    private ListView mLvCharacters;
    private boolean mMovingToAnotherActivity;
    protected Persister mPersister;
    protected List<PlayerChar> mPlayerChars;

    @Override // com.shirobakama.autorpg2.CharacterDetailDialogFragment.CharacterDetailCallback
    public GameContext getGameContext() {
        return null;
    }

    @Override // com.shirobakama.autorpg2.ConfirmationDialogFragment.OnConfirmationListener
    public void onConfirmationCancel(int i, int i2, int i3, Bundle bundle) {
    }

    @Override // com.shirobakama.autorpg2.ConfirmationDialogFragment.OnConfirmationListener
    public void onConfirmationNeutral(int i, int i2, int i3, Bundle bundle) {
    }

    @Override // com.shirobakama.autorpg2.SkillListDialogFragment.SkillListCallback
    public void onSkillSelected(String str, int i, Skill skill, Parcelable parcelable) {
    }

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        DeviceUtil.setLocaleIfNeeded(this, null);
        setContentView(C0380R.layout.tavern);
        DeviceUtil.handleOrientation(this, null);
        this.mInflater = (LayoutInflater) getSystemService("layout_inflater");
        ((TextView) findViewById(C0380R.id.tvTavern)).setText(getString(C0380R.string.lbl_tavern_text, new Object[]{10}));
        this.mPersister = new Persister(getApplicationContext());
        this.mPlayerChars = this.mPersister.readAllPlayerChars();
        ArrayList arrayList = new ArrayList();
        if (bundle != null) {
            this.mGame = (GameContext) bundle.getParcelable("game");
            this.mGame.calcCharacterStatus(this);
            for (boolean z : bundle.getBooleanArray(STATE_CHECKED)) {
                arrayList.add(Boolean.valueOf(z));
            }
        } else {
            this.mGame = GameContext.game;
            GameContext.game = null;
            if (this.mGame == null) {
                this.mGame = AutoRpgMainActivity.readOrInitializeGameContext(this, new Persister(this));
            }
            for (int i = 0; i < this.mPlayerChars.size(); i++) {
                arrayList.add(Boolean.valueOf(this.mGame.characters.contains(this.mPlayerChars.get(i))));
            }
        }
        Button button = (Button) findViewById(C0380R.id.btnSet);
        button.setEnabled(!this.mGame.isAdventuring(this));
        button.setOnClickListener(this);
        ((Button) findViewById(C0380R.id.btnMake)).setOnClickListener(this);
        ((Button) findViewById(C0380R.id.btnDelete)).setOnClickListener(this);
        ((Button) findViewById(C0380R.id.btnHelp)).setOnClickListener(this);
        ((Button) findViewById(C0380R.id.btnClose)).setOnClickListener(this);
        this.mLvCharacters = (ListView) findViewById(C0380R.id.lvCharacters);
        this.mCharacterAdapter = new CharacterAdapter(this, 0, this.mPlayerChars, arrayList);
        this.mLvCharacters.setAdapter((ListAdapter) this.mCharacterAdapter);
        this.mLvCharacters.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.shirobakama.autorpg2.TavernActivity.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i2, long j) {
                TavernActivity.this.selectChatacter(i2);
            }
        });
        setResult(0);
    }

    protected void selectChatacter(int i) {
        ArrayList<LabelValueItem> arrayList = new ArrayList<>();
        arrayList.add(new LabelValueItem(MENU_CHAR_STATUS, getString(C0380R.string.lbl_inn_char_menu_status)));
        arrayList.add(new LabelValueItem(MENU_CHAR_SKILL, getString(C0380R.string.lbl_inn_char_menu_skill)));
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setChoiceMode(0);
        decorator.setCancelable(true);
        decorator.setLabelValueItems(arrayList);
        decorator.args().putInt("char_index", i);
        decorator.decorate(new OptionMenuDialogFragment()).show(getSupportFragmentManager());
    }

    @Override // com.shirobakama.autorpg2.OptionMenuDialogFragment.Callback
    public void selectMenu(Bundle bundle, int i, List<LabelValueItem> list) {
        switch (list.get(i).value) {
            case MENU_CHAR_STATUS /* 103 */:
                showCharacterDetail(bundle);
                break;
            case MENU_CHAR_SKILL /* 104 */:
                showSkill(bundle);
                break;
        }
    }

    private void showSkill(Bundle bundle) {
        int i = bundle.getInt("char_index");
        PlayerChar playerChar = this.mPlayerChars.get(i);
        playerChar.calcStatus((Context) this, this.mGame);
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setCancelable(true).setPositiveText(0);
        decorator.args().putInt("char_index", i);
        decorator.args().putBoolean("selectable", false);
        decorator.args().putIntegerArrayList("skill_ids", playerChar.getSkillIds());
        decorator.decorate(new SkillListDialogFragment()).show(getSupportFragmentManager());
    }

    private void showCharacterDetail(Bundle bundle) {
        int i = bundle.getInt("char_index");
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setCancelable(true).setPositiveText(0);
        decorator.args().putInt("char_index", i);
        decorator.decorate(new CharacterDetailDialogFragment()).show(getSupportFragmentManager());
    }

    @Override // com.shirobakama.autorpg2.CharacterDetailDialogFragment.CharacterDetailCallback
    public List<PlayerChar> getPlayerChars() {
        return this.mPlayerChars;
    }

    @Override // com.shirobakama.autorpg2.CharacterDetailDialogFragment.CharacterDetailCallback
    public LayoutInflater getCurrentLayoutInflater() {
        return this.mInflater;
    }

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("game", this.mGame);
        List<Boolean> checked = this.mCharacterAdapter.getChecked();
        boolean[] zArr = new boolean[checked.size()];
        for (int i = 0; i < zArr.length; i++) {
            Boolean bool = checked.get(i);
            zArr[i] = bool != null && bool.booleanValue();
        }
        bundle.putBooleanArray(STATE_CHECKED, zArr);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        this.mMovingToAnotherActivity = false;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case C0380R.id.btnClose /* 2131165245 */:
                finish();
                break;
            case C0380R.id.btnDelete /* 2131165248 */:
                confirmDelete();
                break;
            case C0380R.id.btnHelp /* 2131165258 */:
                showHelpDialog();
                break;
            case C0380R.id.btnMake /* 2131165268 */:
                makeCharacater();
                break;
            case C0380R.id.btnSet /* 2131165279 */:
                returnToInn();
                break;
        }
    }

    private void showHelpDialog() {
        HelpDialogFragment.show(this, getString(C0380R.string.msg_help_tavern));
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        if (isFinishing() && GameContext.game == null) {
            GameContext.game = this.mGame;
        }
    }

    private boolean canMoveToAnotherActivity() {
        if (this.mMovingToAnotherActivity) {
            return false;
        }
        this.mMovingToAnotherActivity = true;
        return true;
    }

    private void confirmDelete() {
        List<PlayerChar> selectedCharacters = getSelectedCharacters();
        if (this.mGame.isAdventuring(this)) {
            Iterator<PlayerChar> it = selectedCharacters.iterator();
            while (it.hasNext()) {
                if (this.mGame.characters.contains(it.next())) {
                    Toast.makeText(this, C0380R.string.msg_cannot_select_active_character, 1).show();
                    return;
                }
            }
        }
        if (selectedCharacters.isEmpty() || selectedCharacters.size() > 1) {
            Toast.makeText(this, C0380R.string.msg_select_one_character, 1).show();
        } else {
            PlayerChar playerChar = selectedCharacters.get(0);
            ConfirmationDialogFragment.show(this, getString(C0380R.string.msg_dlg_title_confirm_character_delete), getString(C0380R.string.msg_dlg_confirm_character_delete, new Object[]{playerChar.name}), 0, playerChar.f106id);
        }
    }

    private void deleteCharacter(int i) {
        PlayerChar next;
        Iterator<PlayerChar> it = this.mPlayerChars.iterator();
        while (true) {
            if (!it.hasNext()) {
                next = null;
                break;
            } else {
                next = it.next();
                if (next.f106id == i) {
                    break;
                }
            }
        }
        if (next != null) {
            try {
                this.mPersister.deletePlayerChar(next);
            } catch (SQLException e) {
                DeviceUtil.handleSqliteException(this, e);
            }
            this.mGame.characters.remove(next);
            refresh();
            this.mCharacterAdapter.resetChecked();
            Toast.makeText(this, getString(C0380R.string.msg_character_deleted, new Object[]{next.name}), 1).show();
        }
    }

    private void makeCharacater() {
        if (canMoveToAnotherActivity()) {
            if (this.mPlayerChars.size() >= 10) {
                Toast.makeText(this, C0380R.string.msg_character_number_max_exceeds, 1).show();
                return;
            }
            GameContext.game = this.mGame;
            Intent intent = new Intent(getApplicationContext(), (Class<?>) CharacterMakingActivity.class);
            DeviceUtil.setRequestOrientationForIntent(this, intent);
            startActivityForResult(intent, 1);
        }
    }

    private void returnToInn() {
        List<PlayerChar> selectedCharacters = getSelectedCharacters();
        if (selectedCharacters.size() < 1 || selectedCharacters.size() > 3) {
            Toast.makeText(this, getString(C0380R.string.msg_illegal_character_number, new Object[]{3}), 1).show();
        } else {
            setActiveCharacters(selectedCharacters);
        }
    }

    private void setActiveCharacters(List<PlayerChar> list) {
        for (PlayerChar playerChar : list) {
            if (!this.mGame.characters.contains(playerChar)) {
                playerChar.weaponId = 0;
                playerChar.armorId = 0;
                playerChar.shieldId = 0;
                playerChar.ringId = 0;
                this.mPersister.writeEquipmentChanging(playerChar);
            }
        }
        try {
            this.mPersister.writeActiveCharacters(list);
        } catch (SQLException e) {
            DeviceUtil.handleSqliteException(this, e);
        }
        GameContext gameContext = this.mGame;
        gameContext.characters = list;
        GameContext.game = gameContext;
        setResult(-1);
        finish();
    }

    private List<PlayerChar> getSelectedCharacters() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.mPlayerChars.size(); i++) {
            if (this.mCharacterAdapter.getChecked(i)) {
                arrayList.add(this.mPlayerChars.get(i));
            }
        }
        return arrayList;
    }

    private void refresh() {
        List<PlayerChar> allPlayerChars = this.mPersister.readAllPlayerChars();
        this.mPlayerChars.clear();
        this.mPlayerChars.addAll(allPlayerChars);
        this.mCharacterAdapter.notifyDataSetChanged();
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1 && i2 == -1) {
            this.mGame = GameContext.game;
            GameContext.game = null;
            refresh();
        }
    }

    @Override // com.shirobakama.autorpg2.ConfirmationDialogFragment.OnConfirmationListener
    public void onConfirmationOk(int i, int i2, int i3, Bundle bundle) {
        switch (i2) {
            case 0:
                deleteCharacter(i3);
                break;
            case 1:
                setActiveCharacters(getSelectedCharacters());
                break;
        }
    }

    public static class CharacterAdapter extends ArrayAdapter<PlayerChar> {
        private LayoutInflater mAdapterInflater;
        private List<Boolean> mChecked;

        public CharacterAdapter(Context context, int i, List<PlayerChar> list, List<Boolean> list2) {
            super(context, i, list);
            this.mAdapterInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            this.mChecked = list2;
        }

        public List<Boolean> getChecked() {
            return this.mChecked;
        }

        public boolean getChecked(int i) {
            Boolean bool;
            return i < this.mChecked.size() && (bool = this.mChecked.get(i)) != null && bool.booleanValue();
        }

        public void resetChecked() {
            this.mChecked.clear();
        }

        public void setChecked(int i, boolean z) {
            while (i >= this.mChecked.size()) {
                this.mChecked.add(null);
            }
            this.mChecked.set(i, Boolean.valueOf(z));
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(final int i, View view, ViewGroup viewGroup) {
            CharViewHolder charViewHolder;
            if (view == null) {
                view = this.mAdapterInflater.inflate(C0380R.layout.inn_character, viewGroup, false);
                charViewHolder = new CharViewHolder(view, i);
                charViewHolder.llayEquipment.setVisibility(8);
                view.setTag(charViewHolder);
            } else {
                charViewHolder = (CharViewHolder) view.getTag();
            }
            charViewHolder.show(getContext(), null, getItem(i));
            charViewHolder.chkSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.shirobakama.autorpg2.TavernActivity.CharacterAdapter.1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    CharacterAdapter.this.setChecked(i, z);
                }
            });
            charViewHolder.chkSelected.setChecked(getChecked(i));
            return view;
        }
    }
}
