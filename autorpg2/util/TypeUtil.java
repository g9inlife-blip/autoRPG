package com.shirobakama.autorpg2.util;

import android.os.Parcel;
import android.support.v4.view.InputDeviceCompat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public final class TypeUtil {
    public static int hash(boolean z) {
        if (z) {
            return 131;
        }
        return InputDeviceCompat.SOURCE_KEYBOARD;
    }

    private TypeUtil() {
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static int hash(int... iArr) {
        if (iArr == null) {
            return 0;
        }
        int i = 0;
        for (int i2 : iArr) {
            i = (i * 31) + i2;
        }
        return i;
    }

    public static int hash(String str) {
        if (str == null) {
            return 0;
        }
        return str.hashCode();
    }

    public static int hash(Enum<?> r0) {
        if (r0 == null) {
            return 0;
        }
        return r0.hashCode();
    }

    public static <T extends Enum<T>> int compareEnum(Enum<T> r1, Enum<T> r2) {
        return (r1 == null ? -1 : r1.ordinal()) - (r2 != null ? r2.ordinal() : -1);
    }

    public static void writeIntegerList(Parcel parcel, List<Integer> list) {
        if (list == null) {
            parcel.writeInt(-1);
            return;
        }
        parcel.writeInt(list.size());
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            parcel.writeInt(it.next().intValue());
        }
    }

    public static ArrayList<Integer> readIntegerList(Parcel parcel) {
        int i = parcel.readInt();
        if (i < 0) {
            return null;
        }
        ArrayList<Integer> arrayList = new ArrayList<>(i);
        for (int i2 = 0; i2 < i; i2++) {
            arrayList.add(Integer.valueOf(parcel.readInt()));
        }
        return arrayList;
    }

    public static String toString(int[] iArr) {
        StringBuilder sb = new StringBuilder();
        sb.append("[IA:");
        if (iArr == null) {
            sb.append("null");
        } else {
            for (int i : iArr) {
                sb.append(i);
                sb.append(',');
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static <T extends Enum<T>> String getNameOrNull(T t) {
        if (t == null) {
            return null;
        }
        return t.name();
    }

    public static <T extends Enum<T>> T getEnumOrNull(Class<T> cls, String str) {
        if (str == null) {
            return null;
        }
        try {
            return (T) Enum.valueOf(cls, str);
        } catch (IllegalArgumentException unused) {
            return null;
        }
    }

    public static <T extends Enum<T>> T getEnumOrNullByOrdinal(T[] tArr, int i) {
        if (i < 0 || i >= tArr.length) {
            return null;
        }
        return tArr[i];
    }

    public static int getEnumIndex(Enum<?>[] enumArr, Enum<?> r3, int i) {
        for (int i2 = 0; i2 < enumArr.length; i2++) {
            if (enumArr[i2] == r3) {
                return i2;
            }
        }
        return i;
    }

    public static int getIntIndex(int[] iArr, int i, int i2) {
        for (int i3 = 0; i3 < iArr.length; i3++) {
            if (iArr[i3] == i) {
                return i3;
            }
        }
        return i2;
    }
}
