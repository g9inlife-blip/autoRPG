package com.shirobakama.autorpg2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.logquest2.C0380R;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class StoryActivity extends Activity implements View.OnClickListener {
    public static final String EXTRA_STORY_TYPE = "story_type";
    private int mSkipTapCount = 0;
    private int mStoryType;
    private int mStrId;
    private TextView mTvStory;

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        int i;
        super.onCreate(bundle);
        setContentView(C0380R.layout.story);
        DeviceUtil.handleOrientation(this, null);
        findViewById(C0380R.id.btnSkip).setOnClickListener(this);
        this.mTvStory = (TextView) findViewById(C0380R.id.tvStory);
        this.mStoryType = getIntent().getIntExtra(EXTRA_STORY_TYPE, 1);
        int i2 = this.mStoryType;
        if (i2 == 1) {
            this.mStrId = C0380R.string.msg_story_introduction;
            i = C0380R.drawable.bg_introduction;
        } else if (i2 == 2) {
            this.mStrId = C0380R.string.msg_story_epilog;
            i = C0380R.drawable.bg_epilog;
        } else {
            finish();
            return;
        }
        ((ImageView) findViewById(C0380R.id.ivStoryBg)).setImageDrawable(getResources().getDrawable(i));
        View viewFindViewById = findViewById(C0380R.id.flayStoryTexts);
        viewFindViewById.setVisibility(0);
        findViewById(C0380R.id.rlayTitleLogo).setVisibility(8);
        setResult(0);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(1500L);
        viewFindViewById.startAnimation(alphaAnimation);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        this.mTvStory.setText(getString(this.mStrId).substring(1));
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == 2131165285) {
            this.mSkipTapCount++;
            if (this.mStoryType == 1 && this.mSkipTapCount == 1) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                alphaAnimation.setDuration(1500L);
                alphaAnimation.setFillAfter(true);
                AlphaAnimation alphaAnimation2 = new AlphaAnimation(0.0f, 1.0f);
                alphaAnimation2.setStartOffset(1500L);
                alphaAnimation2.setDuration(1500L);
                findViewById(C0380R.id.flayStoryTexts).startAnimation(alphaAnimation);
                findViewById(C0380R.id.rlayTitleLogo).startAnimation(alphaAnimation2);
                findViewById(C0380R.id.rlayTitleLogo).setVisibility(0);
                return;
            }
            setResult(-1);
            finish();
        }
    }
}
