package com.shirobakama.autorpg2.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.entity.Skill;
import com.shirobakama.logquest2.BuildConfig;
import com.shirobakama.logquest2.C0380R;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class SkillAdapter extends ArrayAdapter<Skill> {
    private boolean mCheckable;
    private PlayerChar mPlayerChar;
    private boolean mRemovable;

    public static class ViewHolder {
        public Button btnSkillSlotRemove;
        public TextView tvCannotUse;
        public TextView tvDescription;
        public TextView tvMp;
        public TextView tvName;
        public TextView tvSubClass;
    }

    public SkillAdapter(Context context, PlayerChar playerChar, List<Skill> list, boolean z, boolean z2) {
        super(context, 0, list);
        this.mPlayerChar = playerChar;
        this.mCheckable = z;
        this.mRemovable = z2;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = new ListItemSkillView(getContext());
            viewHolder = new ViewHolder();
            viewHolder.tvSubClass = (TextView) view.findViewById(C0380R.id.tvSubClass);
            viewHolder.tvName = (TextView) view.findViewById(C0380R.id.tvName);
            viewHolder.tvCannotUse = (TextView) view.findViewById(C0380R.id.tvCannotUse);
            viewHolder.tvMp = (TextView) view.findViewById(C0380R.id.tvMp);
            viewHolder.tvDescription = (TextView) view.findViewById(C0380R.id.tvDescription);
            viewHolder.btnSkillSlotRemove = (Button) view.findViewById(C0380R.id.btnSkillSlotRemove);
            view.setTag(viewHolder);
            if (!this.mCheckable) {
                view.findViewById(C0380R.id.radCheck).setVisibility(8);
            }
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Skill item = getItem(i);
        if (this.mRemovable && item != null) {
            viewHolder.btnSkillSlotRemove.setVisibility(0);
            viewHolder.btnSkillSlotRemove.setOnClickListener(new View.OnClickListener() { // from class: com.shirobakama.autorpg2.view.SkillAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    ((ListView) viewGroup).performItemClick(view2, i, 0L);
                }
            });
        } else {
            viewHolder.btnSkillSlotRemove.setVisibility(8);
        }
        if (item != null) {
            viewHolder.tvSubClass.setText(item.clazz.getString(getContext()));
            viewHolder.tvName.setText(item.skillCustomization == null ? item.name : item.skillCustomization.skillName);
            TextView textView = viewHolder.tvCannotUse;
            PlayerChar playerChar = this.mPlayerChar;
            textView.setText((playerChar == null || item.canUse(playerChar)) ? BuildConfig.FLAVOR : getContext().getString(C0380R.string.lbl_skill_cannot_use));
            viewHolder.tvMp.setText(getContext().getString(C0380R.string.res_skill_mp_description, Integer.valueOf(item.f108mp)));
            viewHolder.tvDescription.setText(item.getDescription(getContext()));
        } else {
            viewHolder.tvSubClass.setText(BuildConfig.FLAVOR);
            viewHolder.tvName.setText(C0380R.string.lbl_skill_none);
            viewHolder.tvCannotUse.setText(BuildConfig.FLAVOR);
            viewHolder.tvMp.setText(BuildConfig.FLAVOR);
            viewHolder.tvDescription.setText(BuildConfig.FLAVOR);
        }
        return view;
    }
}
