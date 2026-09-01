package com.shirobakama.autorpg2.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import com.shirobakama.logquest2.C0380R;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class ListItemSkillView extends FrameLayout implements Checkable {
    private RadioButton mRad;

    public ListItemSkillView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initialize(context);
    }

    public ListItemSkillView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context);
    }

    public ListItemSkillView(Context context) {
        super(context);
        initialize(context);
    }

    private void initialize(Context context) {
        addView(inflate(context, C0380R.layout.list_item_skill, null));
        this.mRad = (RadioButton) findViewById(C0380R.id.radCheck);
    }

    @Override // android.widget.Checkable
    public void setChecked(boolean z) {
        this.mRad.setChecked(z);
    }

    @Override // android.widget.Checkable
    public boolean isChecked() {
        return this.mRad.isChecked();
    }

    @Override // android.widget.Checkable
    public void toggle() {
        this.mRad.setChecked(!r0.isChecked());
    }
}
