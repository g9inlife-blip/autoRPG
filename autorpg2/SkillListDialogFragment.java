package com.shirobakama.autorpg2;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import com.shirobakama.autorpg2.CharacterDetailDialogFragment;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.entity.Skill;
import com.shirobakama.autorpg2.p001db.Persister;
import com.shirobakama.autorpg2.repo.SkillRepository;
import com.shirobakama.autorpg2.view.SkillAdapter;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class SkillListDialogFragment extends AlertDialogFragment {
    protected static final String TAG = "skill-dialog";
    private boolean mClicked = false;
    private ListView mLvSkills;
    private ViewGroup mVwChart;

    public interface SkillListCallback {
        void onSkillSelected(String str, int i, Skill skill, Parcelable parcelable);
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    protected View getAlertDialogView() throws Resources.NotFoundException {
        this.mLvSkills = new ListView(getActivity());
        ArrayList arrayList = new ArrayList();
        Iterator<Integer> it = getArguments().getIntegerArrayList("skill_ids").iterator();
        while (it.hasNext()) {
            arrayList.add(SkillRepository.getSkill(getActivity(), it.next().intValue()));
        }
        if (arrayList.isEmpty()) {
            arrayList.add(null);
        }
        PlayerChar playerChar = getActivity() instanceof CharacterDetailDialogFragment.CharacterDetailCallback ? ((CharacterDetailDialogFragment.CharacterDetailCallback) getActivity()).getPlayerChars().get(getArguments().getInt("char_index")) : null;
        if (playerChar != null) {
            new Persister(getActivity()).readSkillCustomizationForSkills(playerChar.f106id, arrayList);
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(getActivity()).inflate(C0380R.layout.skill_chart, (ViewGroup) this.mLvSkills, false);
            this.mVwChart = (TableLayout) viewGroup.findViewById(C0380R.id.llayChartMain);
            HashSet hashSet = new HashSet(playerChar.getSkillIds());
            for (GameChar.SubClass subClass : GameChar.SubClass.VALUES) {
                if (playerChar.hasSubClass(subClass)) {
                    addSkillChartView(this.mVwChart, subClass, hashSet);
                }
            }
            this.mVwChart.setVisibility(8);
            this.mLvSkills.addFooterView(viewGroup);
            viewGroup.findViewById(C0380R.id.btnToggleChart).setOnClickListener(new View.OnClickListener() { // from class: com.shirobakama.autorpg2.SkillListDialogFragment.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SkillListDialogFragment.this.toggleChart(view);
                }
            });
        }
        boolean z = getArguments().getBoolean("selectable");
        this.mLvSkills.setAdapter((ListAdapter) new SkillAdapter(getActivity(), playerChar, arrayList, z, false));
        if (z) {
            this.mLvSkills.setChoiceMode(1);
        }
        if (getArguments().getBoolean("clickable")) {
            this.mLvSkills.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: com.shirobakama.autorpg2.SkillListDialogFragment.2
                @Override // android.widget.AdapterView.OnItemLongClickListener
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                    Skill skill = (Skill) adapterView.getItemAtPosition(i);
                    int i2 = SkillListDialogFragment.this.getArguments().getInt("char_index");
                    ((SkillListCallback) SkillListDialogFragment.this.getActivity()).onSkillSelected(SkillListDialogFragment.this.getArguments().getString("tag"), i2, skill, SkillListDialogFragment.this.getArguments().getParcelable("extra"));
                    return true;
                }
            });
        }
        return this.mLvSkills;
    }

    protected void toggleChart(View view) {
        this.mVwChart.setVisibility(this.mVwChart.getVisibility() == 0 ? 8 : 0);
        this.mLvSkills.setSelection(r2.getCount() - 1);
    }

    private static class SkillTreeNode {
        public List<SkillTreeNode> children = new ArrayList(3);
        public SkillTreeNode elder;
        public Skill skill;

        /* renamed from: x */
        public int f59x;

        /* renamed from: y */
        public int f60y;
        public SkillTreeNode younger;

        public SkillTreeNode(Skill skill) {
            this.skill = skill;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:56:0x0148  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void addSkillChartView(android.view.ViewGroup r17, com.shirobakama.autorpg2.entity.GameChar.SubClass r18, java.util.Set<java.lang.Integer> r19) throws android.content.res.Resources.NotFoundException {
        /*
            Method dump skipped, instructions count: 546
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.SkillListDialogFragment.addSkillChartView(android.view.ViewGroup, com.shirobakama.autorpg2.entity.GameChar$SubClass, java.util.Set):void");
    }

    private void addRequiredAttr(StringBuilder sb, GameChar.Attribute attribute, int i) {
        sb.append(attribute.getString(getActivity()));
        sb.append(i);
    }

    private int numberNode(SkillTreeNode skillTreeNode, int i, int i2) {
        skillTreeNode.f59x = i;
        skillTreeNode.f60y = i2;
        if (skillTreeNode.children.isEmpty()) {
            return 1;
        }
        int iNumberNode = 0;
        Iterator<SkillTreeNode> it = skillTreeNode.children.iterator();
        while (it.hasNext()) {
            iNumberNode += numberNode(it.next(), i + 1, i2 + iNumberNode);
        }
        return iNumberNode;
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    protected void onClick(DialogInterface dialogInterface, int i) {
        int checkedItemPosition;
        Skill skill;
        if (this.mClicked) {
            return;
        }
        this.mClicked = true;
        int i2 = getArguments().getInt("char_index");
        String string = getArguments().getString("tag");
        SkillListCallback skillListCallback = (SkillListCallback) getActivity();
        if (i == -1 && getArguments().getBoolean("selectable") && (checkedItemPosition = this.mLvSkills.getCheckedItemPosition()) >= 0 && checkedItemPosition < this.mLvSkills.getCount() && (skill = (Skill) this.mLvSkills.getItemAtPosition(checkedItemPosition)) != null) {
            skillListCallback.onSkillSelected(string, i2, skill, getArguments().getParcelable("extra"));
        }
    }
}
