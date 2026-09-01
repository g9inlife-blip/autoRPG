package com.shirobakama.autorpg2;

import android.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.logquest2.C0380R;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class AutoRpgPrefsActivity extends PreferenceActivity {
    private static final String EXTRA_IS_ADVENTURING = "is.adventuring";
    private ApiAware mApiAware;
    private boolean mIsAdventuring;

    private interface ApiAware {
        void addResource();
    }

    @Override // android.preference.PreferenceActivity, android.app.Activity
    protected void onCreate(Bundle bundle) throws NoSuchMethodException, SecurityException {
        super.onCreate(bundle);
        DeviceUtil.handleOrientation(this, null);
        this.mIsAdventuring = getIntent().getBooleanExtra(EXTRA_IS_ADVENTURING, true);
        try {
            getClass().getMethod("getFragmentManager", new Class[0]);
            this.mApiAware = new Api11AndGreater(this);
        } catch (NoSuchMethodException unused) {
            this.mApiAware = new ApiLessThan11(this);
        }
        this.mApiAware.addResource();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setEnabledOfflineChecking(PreferenceScreen preferenceScreen) {
        if (this.mIsAdventuring) {
            ((CheckBoxPreference) preferenceScreen.findPreference(getString(C0380R.string.pref_key_offline_time_checking))).setEnabled(false);
        }
    }

    @TargetApi(11)
    private static class Api11AndGreater implements ApiAware {
        private static final String FRAGMENT_TAG_PREFS = "prefs";
        private Activity mActivity;

        public Api11AndGreater(Activity activity) {
            this.mActivity = activity;
        }

        @Override // com.shirobakama.autorpg2.AutoRpgPrefsActivity.ApiAware
        public void addResource() {
            this.mActivity.getFragmentManager().beginTransaction().replace(R.id.content, new MyPrefsFragment(), FRAGMENT_TAG_PREFS).commit();
        }
    }

    private static class ApiLessThan11 implements ApiAware {
        protected AutoRpgPrefsActivity mActivity;

        public ApiLessThan11(AutoRpgPrefsActivity autoRpgPrefsActivity) {
            this.mActivity = autoRpgPrefsActivity;
        }

        @Override // com.shirobakama.autorpg2.AutoRpgPrefsActivity.ApiAware
        public void addResource() {
            this.mActivity.addPreferencesFromResource(C0380R.xml.preference);
            AutoRpgPrefsActivity autoRpgPrefsActivity = this.mActivity;
            autoRpgPrefsActivity.setEnabledOfflineChecking(autoRpgPrefsActivity.getPreferenceScreen());
        }
    }

    @TargetApi(11)
    public static class MyPrefsFragment extends PreferenceFragment {
        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            addPreferencesFromResource(C0380R.xml.preference);
            ((AutoRpgPrefsActivity) getActivity()).setEnabledOfflineChecking(getPreferenceScreen());
        }
    }

    public static void setIsAdventuring(Intent intent, boolean z) {
        intent.putExtra(EXTRA_IS_ADVENTURING, z);
    }

    public static boolean isOfflineTimeChecking(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(C0380R.string.pref_key_offline_time_checking), false);
    }
}
