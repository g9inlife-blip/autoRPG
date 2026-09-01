package com.shirobakama.autorpg2.entity;

import android.content.Context;
import com.shirobakama.logquest2.C0380R;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public enum AttrType {
    FIRE(C0380R.string.res_attr_fire),
    WATER(C0380R.string.res_attr_water),
    WIND(C0380R.string.res_attr_wind),
    SLEEP(C0380R.string.res_attr_sleep),
    DEATH(0);

    private String str;
    private int strId;
    public static final AttrType[] VALUES = values();
    public static final int ENCHANTABLE_ATTRS = WIND.ordinal() + 1;

    AttrType(int i) {
        this.strId = i;
    }

    public String getString(Context context) {
        int i = this.strId;
        if (i == 0) {
            return null;
        }
        if (this.str == null) {
            this.str = context.getString(i);
        }
        return this.str;
    }
}
