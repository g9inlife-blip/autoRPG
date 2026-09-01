package com.shirobakama.autorpg2.util;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import com.shirobakama.autorpg2.AutoRpgMainActivity;
import com.shirobakama.autorpg2.entity.Dungeon;
import com.shirobakama.logquest2.C0380R;
import java.util.Date;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class NotificationReceiver extends BroadcastReceiver {
    private static final String EXTRA_CONTENT = "content";
    private static final String EXTRA_LED = "led";
    private static final String EXTRA_SOUND = "sound";
    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_VIBRATION = "vibration";
    public static final int NOFITICATION_ID_PROCESSING_ADVENTURE = 2;
    public static final int NOFITICATION_ID_RETURN_ADVENTURE = 1;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String stringExtra = intent.getStringExtra(EXTRA_TITLE);
        String stringExtra2 = intent.getStringExtra(EXTRA_CONTENT);
        boolean booleanExtra = intent.getBooleanExtra(EXTRA_VIBRATION, false);
        boolean booleanExtra2 = intent.getBooleanExtra(EXTRA_SOUND, false);
        boolean booleanExtra3 = intent.getBooleanExtra(EXTRA_LED, false);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        int i = booleanExtra2 ? 1 : 0;
        if (booleanExtra) {
            i |= 2;
        }
        if (booleanExtra3) {
            i |= 4;
        }
        PendingIntent activity = PendingIntent.getActivity(context, 0, new Intent(context, (Class<?>) AutoRpgMainActivity.class), 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true);
        builder.setSmallIcon(C0380R.drawable.ic_status_lq2);
        builder.setTicker(stringExtra);
        builder.setContentTitle(stringExtra);
        builder.setContentText(stringExtra2);
        builder.setContentIntent(activity);
        builder.setWhen(System.currentTimeMillis());
        if (i != 0) {
            builder.setDefaults(i);
        }
        notificationManager.notify(1, builder.build());
    }

    public static void registerNotificationAlarm(Context context, String str, String str2, Date date, boolean z, boolean z2, boolean z3) {
        Intent intent = new Intent(context.getApplicationContext(), (Class<?>) NotificationReceiver.class);
        intent.putExtra(EXTRA_TITLE, str);
        intent.putExtra(EXTRA_CONTENT, str2);
        intent.putExtra(EXTRA_VIBRATION, z);
        intent.putExtra(EXTRA_SOUND, z2);
        intent.putExtra(EXTRA_LED, z3);
        ((AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM)).set(0, date.getTime(), PendingIntent.getBroadcast(context, 0, intent, 134217728));
    }

    public static void unregisterNotificationAlarm(Context context) {
        ((AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM)).cancel(PendingIntent.getBroadcast(context, 0, new Intent(context.getApplicationContext(), (Class<?>) NotificationReceiver.class), 134217728));
    }

    public static void removeCurrentNotification(Context context) {
        ((NotificationManager) context.getSystemService("notification")).cancel(1);
    }

    public static void notifyWhenReturning(Context context, Dungeon dungeon, int i, Date date) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (defaultSharedPreferences.getBoolean(context.getString(C0380R.string.pref_key_notification), false)) {
            boolean z = defaultSharedPreferences.getBoolean(context.getString(C0380R.string.pref_key_notification_vib), false);
            boolean z2 = defaultSharedPreferences.getBoolean(context.getString(C0380R.string.pref_key_notification_sound), false);
            boolean z3 = defaultSharedPreferences.getBoolean(context.getString(C0380R.string.pref_key_notification_led), false);
            String string = context.getString(C0380R.string.msg_notification_title);
            String string2 = context.getString(C0380R.string.msg_notification_text, dungeon.name, Integer.valueOf(i));
            if (date.getTime() > System.currentTimeMillis()) {
                registerNotificationAlarm(context, string, string2, date, z, z2, z3);
            }
        }
    }
}
