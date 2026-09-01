package com.shirobakama.autorpg2.util;

import android.graphics.Bitmap;
import com.shirobakama.autorpg2.entity.CommonLog;
import com.shirobakama.autorpg2.repo.MonsterDb;
import java.util.Calendar;
import java.util.Date;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public final class FormatUtil {
    private static Calendar calendar = Calendar.getInstance();

    private FormatUtil() {
    }

    public static String formatLogTimeToHhmm(int i) {
        return formatTimeToHhmm(CommonLog.getDateFromLogTime(i));
    }

    public static String formatTimeToHhmm(Date date) {
        calendar.setTime(date);
        int i = calendar.get(11);
        int i2 = calendar.get(12);
        StringBuilder sb = new StringBuilder();
        if (i < 10) {
            sb.append('0');
        }
        sb.append(i);
        sb.append(':');
        if (i2 < 10) {
            sb.append('0');
        }
        sb.append(i2);
        return sb.toString();
    }

    public static Bitmap resizeBitmapWithDensity(Bitmap bitmap, int i) {
        int density = bitmap.getDensity();
        int width = (density * ((bitmap.getWidth() * MonsterDb.MONSTER_GIANT_ANT) / density)) / i;
        Bitmap bitmapCopy = bitmap.copy(bitmap.getConfig(), false);
        bitmapCopy.setDensity(width);
        return bitmapCopy;
    }

    public static String getAdjustDesc(int i) {
        if (i <= 0) {
            return Integer.toString(i);
        }
        return "+" + Integer.toString(i);
    }
}
