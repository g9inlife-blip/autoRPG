package com.shirobakama.autorpg2;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class TownPagerAdapter extends PagerAdapter {
    private TownActivity mTownActivity;
    private View[] mViews;

    @Override // android.support.v4.view.PagerAdapter
    public int getCount() {
        return 4;
    }

    @Override // android.support.v4.view.PagerAdapter
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public TownPagerAdapter(TownActivity townActivity, View[] viewArr) {
        this.mTownActivity = townActivity;
        this.mViews = viewArr;
    }

    @Override // android.support.v4.view.PagerAdapter
    public Object instantiateItem(ViewGroup viewGroup, int i) {
        View view = this.mViews[i];
        viewGroup.addView(view);
        return view;
    }

    @Override // android.support.v4.view.PagerAdapter
    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        viewGroup.removeView((View) obj);
    }
}
