package com.shirobakama.autorpg2.util;

import android.app.Activity;
import android.widget.LinearLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class AdManager {
    private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-3566375224375692/1171641493";
    protected static final String TAG = "ad-manager";
    private Activity mActivity;
    private AdView mAdView;

    public AdManager(Activity activity) {
        this.mActivity = activity;
    }

    public void handleAdOnCreate(int i) {
        LinearLayout linearLayout = (LinearLayout) this.mActivity.findViewById(i);
        float f = linearLayout.getLayoutParams().height;
        if (f >= 0.0f && f / this.mActivity.getResources().getDisplayMetrics().density < 48.0f) {
            this.mAdView = null;
            return;
        }
        if (!DeviceUtil.isNetworkConnected(this.mActivity)) {
            this.mAdView = null;
            return;
        }
        this.mAdView = new AdView(this.mActivity);
        this.mAdView.setAdUnitId(ADMOB_AD_UNIT_ID);
        this.mAdView.setAdSize(AdSize.BANNER);
        linearLayout.addView(this.mAdView);
        this.mAdView.loadAd(new AdRequest.Builder().addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB").addTestDevice("9183687D8FA7C8EAE7DC05DB2F970BB3").addTestDevice("ACCF688BAE8B0F9EF7F55EBDE75DC87F").build());
    }

    public void handleAdOnDestory() {
        AdView adView = this.mAdView;
        if (adView != null) {
            adView.removeAllViews();
            this.mAdView.destroy();
        }
    }

    public void handleAdOnPause() {
        AdView adView = this.mAdView;
        if (adView != null) {
            adView.pause();
        }
    }

    public void handleAdOnResume() {
        AdView adView = this.mAdView;
        if (adView != null) {
            adView.resume();
        }
    }
}
