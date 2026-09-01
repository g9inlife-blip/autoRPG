package com.shirobakama.autorpg2.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.text.ClipboardManager;
import android.widget.Toast;
import com.shirobakama.logquest2.C0380R;
import java.lang.Thread;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String PREFS_NAME = "ExTracePrefs";
    private static final String PREF_TRACE = "trace";
    private static Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    private Context context;

    public MyUncaughtExceptionHandler(Context context) {
        this.context = context;
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable th) {
        StringBuilder sb = new StringBuilder();
        Throwable cause = th;
        do {
            sb.append(cause.getClass().toString());
            sb.append(": ");
            sb.append(cause.getMessage());
            sb.append('\n');
            for (StackTraceElement stackTraceElement : cause.getStackTrace()) {
                sb.append(stackTraceElement.getClassName());
                sb.append("#");
                sb.append(stackTraceElement.getMethodName());
                sb.append(":");
                sb.append(stackTraceElement.getLineNumber());
                sb.append('\n');
            }
            cause = cause.getCause();
            if (cause != null) {
                sb.append("Caused by: ");
            }
        } while (cause != null);
        SharedPreferences.Editor editorEdit = this.context.getSharedPreferences(PREFS_NAME, 0).edit();
        editorEdit.putString(PREF_TRACE, sb.toString());
        editorEdit.commit();
        defaultHandler.uncaughtException(thread, th);
    }

    public static void sendBugReport(final Activity activity) {
        SharedPreferences sharedPreferences = activity.getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        String string = sharedPreferences.getString(PREF_TRACE, null);
        if (string == null) {
            return;
        }
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        editorEdit.remove(PREF_TRACE);
        editorEdit.apply();
        PackageInfo packageInfo = DeviceUtil.getPackageInfo(activity);
        StringBuilder sb = new StringBuilder();
        sb.append("dev:");
        sb.append(Build.DEVICE);
        sb.append(",");
        sb.append("mod:");
        sb.append(Build.MODEL);
        sb.append(",");
        sb.append("sdk:");
        sb.append(Build.VERSION.SDK_INT);
        sb.append(",");
        sb.append("ver:");
        sb.append(packageInfo == null ? "null" : packageInfo.versionName);
        sb.append(",");
        sb.append("trace:");
        sb.append("\n");
        sb.append(string);
        final String string2 = sb.toString();
        final String string3 = activity.getString(C0380R.string.msg_bug_report_mail_title);
        new AlertDialog.Builder(activity).setTitle(C0380R.string.msg_dlg_title_bug_report).setMessage(C0380R.string.msg_dlg_bug_report_dialog).setPositiveButton(C0380R.string.lbl_dlg_btn_bug_report_mail, new DialogInterface.OnClickListener() { // from class: com.shirobakama.autorpg2.util.MyUncaughtExceptionHandler.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("message/rfc822");
                    intent.putExtra("android.intent.extra.EMAIL", new String[]{activity.getString(C0380R.string.res_bug_report_mail_to)});
                    intent.putExtra("android.intent.extra.SUBJECT", string3);
                    intent.putExtra("android.intent.extra.TEXT", string2);
                    activity.startActivity(intent);
                } catch (ActivityNotFoundException unused) {
                    MyUncaughtExceptionHandler.copyToClipboard(activity, string2);
                    Toast.makeText(activity, C0380R.string.msg_no_mailer_clipboard_copied, 1).show();
                }
            }
        }).setNeutralButton(C0380R.string.lbl_dlg_btn_bug_report_clipboard, new DialogInterface.OnClickListener() { // from class: com.shirobakama.autorpg2.util.MyUncaughtExceptionHandler.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MyUncaughtExceptionHandler.copyToClipboard(activity, string2);
                Toast.makeText(activity, C0380R.string.msg_clipboard_copied, 0).show();
            }
        }).setNegativeButton(C0380R.string.lbl_dlg_btn_bug_report_no, (DialogInterface.OnClickListener) null).show();
    }

    protected static void copyToClipboard(Context context, String str) {
        ((ClipboardManager) context.getSystemService("clipboard")).setText(str);
    }
}
