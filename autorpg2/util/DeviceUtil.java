package com.shirobakama.autorpg2.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteFullException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;
import com.shirobakama.autorpg2.repo.DungeonRepository;
import com.shirobakama.autorpg2.repo.ItemRepository;
import com.shirobakama.autorpg2.repo.MonsterRepository;
import com.shirobakama.autorpg2.repo.QuestRepository;
import com.shirobakama.autorpg2.repo.SkillRepository;
import com.shirobakama.autorpg2.repo.TownRepository;
import com.shirobakama.logquest2.C0380R;
import java.io.File;
import java.util.Locale;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public final class DeviceUtil {
    private static final String EXTRA_REQUESTED_ORIENTATION = "DeviceUtil_requested_orientation";
    protected static final String TAG = "device-util";

    public static boolean isTestDevice(Context context) {
        return false;
    }

    private DeviceUtil() {
    }

    public static void handleSqliteException(Activity activity, RuntimeException runtimeException) {
        if ((runtimeException instanceof SQLiteFullException) || (runtimeException instanceof SQLiteDiskIOException)) {
            Toast.makeText(activity, C0380R.string.msg_db_write_error, 1).show();
            activity.finish();
            return;
        }
        throw runtimeException;
    }

    public static PackageInfo getPackageInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    public static File getDefaultDataDirectory(Context context) {
        return new File(Environment.getExternalStorageDirectory(), context.getString(C0380R.string.res_backup_dir));
    }

    public static File getDataDirectory(Context context) {
        String string = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(C0380R.string.pref_key_output_directory), null);
        if (TextUtils.isEmpty(string)) {
            return getDefaultDataDirectory(context);
        }
        return new File(string);
    }

    public static boolean isNetworkConnected(Context context) {
        NetworkInfo activeNetworkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService("connectivity");
        if (connectivityManager == null || (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) == null) {
            return false;
        }
        return activeNetworkInfo.isConnected();
    }

    public static boolean handleOrientation(Activity activity, SharedPreferences sharedPreferences) {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }
        if (!sharedPreferences.getBoolean(activity.getString(C0380R.string.pref_key_disable_rotation), false)) {
            return false;
        }
        Intent intent = activity.getIntent();
        int currentOrientation = getCurrentOrientation(activity);
        int requestedOrientation = activity.getRequestedOrientation();
        if (hasPreviousOrientation(intent)) {
            currentOrientation = getPreviousOrientation(intent);
        }
        if (requestedOrientation == currentOrientation) {
            return false;
        }
        activity.setRequestedOrientation(currentOrientation);
        return true;
    }

    private static int getPreviousOrientation(Intent intent) {
        if (intent == null) {
            return 0;
        }
        return intent.getIntExtra(EXTRA_REQUESTED_ORIENTATION, 0);
    }

    private static boolean hasPreviousOrientation(Intent intent) {
        if (intent == null) {
            return false;
        }
        return intent.hasExtra(EXTRA_REQUESTED_ORIENTATION);
    }

    public static void setRequestOrientationForIntent(Activity activity, Intent intent) {
        int requestedOrientation = activity.getRequestedOrientation();
        if (requestedOrientation == 0 || requestedOrientation == 1 || requestedOrientation == 8 || requestedOrientation == 9) {
            intent.putExtra(EXTRA_REQUESTED_ORIENTATION, requestedOrientation);
        }
    }

    private static int getCurrentOrientation(Activity activity) {
        if (Build.VERSION.SDK_INT < 8) {
            return getScreenOrientationEclair(activity);
        }
        if (Build.VERSION.SDK_INT < 9) {
            return getScreenOrientationFroyo(activity);
        }
        return getScreenOrientation(activity);
    }

    @TargetApi(7)
    private static int getScreenOrientationEclair(Activity activity) {
        int orientation = activity.getWindowManager().getDefaultDisplay().getOrientation();
        return (orientation != 0 && orientation == 1) ? 0 : 1;
    }

    @TargetApi(8)
    private static int getScreenOrientationFroyo(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int i = displayMetrics.widthPixels;
        int i2 = displayMetrics.heightPixels;
        if (((rotation == 0 || rotation == 2) && i2 > i) || ((rotation == 1 || rotation == 3) && i > i2)) {
            switch (rotation) {
            }
            return 0;
        }
        switch (rotation) {
        }
        return 0;
    }

    @TargetApi(9)
    private static int getScreenOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int i = displayMetrics.widthPixels;
        int i2 = displayMetrics.heightPixels;
        if (((rotation == 0 || rotation == 2) && i2 > i) || ((rotation == 1 || rotation == 3) && i > i2)) {
            switch (rotation) {
            }
            return 0;
        }
        switch (rotation) {
        }
        return 0;
    }

    public static void setLocaleIfNeeded(Activity activity, String str) {
        if (str == null) {
            str = PreferenceManager.getDefaultSharedPreferences(activity).getString(activity.getString(C0380R.string.pref_key_language), null);
        }
        Context baseContext = activity.getBaseContext();
        Configuration configuration = baseContext.getResources().getConfiguration();
        Locale locale = Resources.getSystem().getConfiguration().locale;
        Locale locale2 = configuration.locale;
        if (!TextUtils.isEmpty(str)) {
            String[] strArrSplit = str.split("-");
            if (strArrSplit.length == 1) {
                locale = new Locale(strArrSplit[0]);
            } else if (strArrSplit.length == 2) {
                locale = new Locale(strArrSplit[0], strArrSplit[1]);
            }
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if (locale2 == null) {
            locale2 = Locale.getDefault();
        }
        if (locale.getLanguage().equals(locale2.getLanguage()) && locale.getCountry().equals(locale2.getCountry())) {
            return;
        }
        configuration.locale = locale;
        Locale.setDefault(locale);
        baseContext.getResources().updateConfiguration(configuration, baseContext.getResources().getDisplayMetrics());
        ItemRepository.flush();
        DungeonRepository.flush();
        MonsterRepository.flush();
        SkillRepository.flush();
        TownRepository.flush();
        QuestRepository.flush();
    }
}
