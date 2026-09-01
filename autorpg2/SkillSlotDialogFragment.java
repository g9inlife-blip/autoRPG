package com.shirobakama.autorpg2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.shirobakama.autorpg2.CharacterDetailDialogFragment;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.entity.Skill;
import com.shirobakama.autorpg2.p001db.Persister;
import com.shirobakama.autorpg2.repo.SkillRepository;
import com.shirobakama.autorpg2.util.TypeUtil;
import com.shirobakama.autorpg2.view.SkillAdapter;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class SkillSlotDialogFragment extends AlertDialogFragment {
    private SkillAdapter mAdapter;
    protected List<Skill> mAvailableSkills;
    private ListView mLvSkills;
    protected State mState;

    public interface SkilSlotCallback {
        void completeSkillSlot(State state);

        void selectSkillForSlot(State state);
    }

    public static class State implements Parcelable {
        public static final Parcelable.Creator<State> CREATOR = new Parcelable.Creator<State>() { // from class: com.shirobakama.autorpg2.SkillSlotDialogFragment.State.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public State[] newArray(int i) {
                return new State[i];
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public State createFromParcel(Parcel parcel) {
                return new State(parcel);
            }
        };
        public ArrayList<Integer> availableSkillIds;
        public boolean canModify;
        public int charIndex;
        public int maxSkillSlots;
        public List<Integer> skillIds;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public State(int i, ArrayList<Integer> arrayList, List<Integer> list, int i2, boolean z) {
            this.charIndex = i;
            this.availableSkillIds = arrayList;
            this.skillIds = list;
            this.maxSkillSlots = i2;
            this.canModify = z;
        }

        public State(Parcel parcel) {
            this.availableSkillIds = TypeUtil.readIntegerList(parcel);
            this.skillIds = TypeUtil.readIntegerList(parcel);
            this.charIndex = parcel.readInt();
            this.maxSkillSlots = parcel.readInt();
            this.canModify = parcel.readInt() != 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            TypeUtil.writeIntegerList(parcel, this.availableSkillIds);
            TypeUtil.writeIntegerList(parcel, this.skillIds);
            parcel.writeInt(this.charIndex);
            parcel.writeInt(this.maxSkillSlots);
            parcel.writeInt(this.canModify ? 1 : 0);
        }
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    protected View getAlertDialogView() {
        this.mLvSkills = new ListView(getActivity());
        this.mState = (State) getArguments().getParcelable("state");
        this.mAvailableSkills = new ArrayList();
        Iterator<Integer> it = this.mState.availableSkillIds.iterator();
        while (it.hasNext()) {
            this.mAvailableSkills.add(SkillRepository.getSkill(getActivity(), it.next().intValue()));
        }
        if (this.mAvailableSkills.isEmpty()) {
            this.mAvailableSkills.add(null);
        }
        PlayerChar playerChar = getActivity() instanceof CharacterDetailDialogFragment.CharacterDetailCallback ? ((CharacterDetailDialogFragment.CharacterDetailCallback) getActivity()).getPlayerChars().get(this.mState.charIndex) : null;
        if (playerChar != null) {
            new Persister(getActivity()).readSkillCustomizationForSkills(playerChar.f106id, this.mAvailableSkills);
        }
        this.mAdapter = new SkillAdapter(getActivity(), playerChar, this.mAvailableSkills, false, this.mState.canModify);
        this.mLvSkills.setAdapter((ListAdapter) this.mAdapter);
        if (this.mState.canModify) {
            this.mLvSkills.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.shirobakama.autorpg2.SkillSlotDialogFragment.1
                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    if (view.getId() != 2131165284 || i >= SkillSlotDialogFragment.this.mAvailableSkills.size()) {
                        return;
                    }
                    SkillSlotDialogFragment skillSlotDialogFragment = SkillSlotDialogFragment.this;
                    skillSlotDialogFragment.removeSkillSlot(skillSlotDialogFragment.mAvailableSkills.get(i));
                }
            });
        }
        return this.mLvSkills;
    }

    @Override // android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
    public void onStart() {
        Button button;
        super.onStart();
        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog == null || (button = alertDialog.getButton(-3)) == null) {
            return;
        }
        button.setOnClickListener(new View.OnClickListener() { // from class: com.shirobakama.autorpg2.SkillSlotDialogFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (SkillSlotDialogFragment.this.mState.availableSkillIds.size() >= SkillSlotDialogFragment.this.mState.maxSkillSlots) {
                    Toast.makeText(SkillSlotDialogFragment.this.getActivity(), C0380R.string.msg_skill_slot_no_room, 0).show();
                    return;
                }
                ArrayList arrayList = new ArrayList(SkillSlotDialogFragment.this.mState.skillIds);
                arrayList.removeAll(SkillSlotDialogFragment.this.mState.availableSkillIds);
                if (arrayList.isEmpty()) {
                    Toast.makeText(SkillSlotDialogFragment.this.getActivity(), C0380R.string.msg_no_available_skill, 0).show();
                } else {
                    ((SkilSlotCallback) SkillSlotDialogFragment.this.getActivity()).selectSkillForSlot(SkillSlotDialogFragment.this.mState);
                    SkillSlotDialogFragment.this.dismiss();
                }
            }
        });
    }

    protected void removeSkillSlot(Skill skill) {
        if (skill == null) {
            return;
        }
        this.mAvailableSkills.remove(skill);
        if (this.mAvailableSkills.isEmpty()) {
            this.mAvailableSkills.add(null);
        }
        this.mState.availableSkillIds.remove(Integer.valueOf(skill.f107id));
        this.mAdapter.notifyDataSetChanged();
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    protected void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            ((SkilSlotCallback) getActivity()).completeSkillSlot(this.mState);
        }
    }
}
