package com.shirobakama.autorpg2.adventure;

import android.support.annotation.StringRes;
import com.shirobakama.autorpg2.entity.GameChar;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class IdHolder {
    private int idForEnemy;
    private int idForPlayer;

    public IdHolder(int i, int i2) {
        this.idForPlayer = i;
        this.idForEnemy = i2;
    }

    @StringRes
    public int get(GameChar gameChar) {
        return gameChar.isPlayer() ? this.idForPlayer : this.idForEnemy;
    }
}
