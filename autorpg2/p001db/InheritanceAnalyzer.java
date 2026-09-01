package com.shirobakama.autorpg2.p001db;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.SparseArray;
import com.shirobakama.autorpg2.util.Base64;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.logquest2.C0380R;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class InheritanceAnalyzer {
    private static final int DUNGEON_MAZE = 16;
    private static final int DUNGEON_PALACE = 15;
    public static final int EXP_MAX = 999999999;
    private static int SERIALIZER_MAJOR_VERSION = 1;
    private static int SERIALIZER_MINOR_VERSION = 3;
    private static final int TYPE_CLEAR_DUNGEON_BASE = 1000;
    public String errorMessage;
    public int exp;
    public boolean expCounterStopped;
    public boolean gameCleared;
    public int gold;
    public int level;
    public boolean mazeCleared;

    protected static class OldGameFlag {

        /* renamed from: id */
        public int f72id;
        public String option;
        public int type;
        public boolean value;

        protected OldGameFlag() {
        }
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    public boolean analyze(Context context) throws Throwable {
        File file = new File(getPreviousBackupFilePath(context));
        if (!file.exists()) {
            this.errorMessage = context.getString(C0380R.string.msg_no_backup_file);
            return false;
        }
        BufferedReader bufferedReader = null;
        try {
            try {
                BufferedReader bufferedReader2 = new BufferedReader(new FileReader(file));
                try {
                    bufferedReader2.readLine();
                    bufferedReader2.readLine();
                    String line = bufferedReader2.readLine();
                    try {
                        bufferedReader2.close();
                    } catch (IOException unused) {
                    }
                    if (line == null) {
                        this.errorMessage = context.getString(C0380R.string.msg_invalid_prevous_app_data);
                        return false;
                    }
                    try {
                        byte[] bArrDecode = Base64.decode(line, 0);
                        if (!(bArrDecode[2] == SERIALIZER_MAJOR_VERSION && bArrDecode[3] <= SERIALIZER_MINOR_VERSION)) {
                            this.errorMessage = context.getString(C0380R.string.msg_invalid_prevous_app_data);
                            return false;
                        }
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArrDecode);
                        try {
                            byteArrayInputStream.read();
                            byteArrayInputStream.read();
                            byteArrayInputStream.read();
                            byteArrayInputStream.read();
                            readString(byteArrayInputStream);
                            readByte(byteArrayInputStream);
                            readByte(byteArrayInputStream);
                            this.exp = readInt(byteArrayInputStream);
                            this.level = readByte(byteArrayInputStream);
                            this.gold = readInt(byteArrayInputStream);
                            readByte(byteArrayInputStream);
                            readByte(byteArrayInputStream);
                            readByte(byteArrayInputStream);
                            readByte(byteArrayInputStream);
                            readByte(byteArrayInputStream);
                            readByte(byteArrayInputStream);
                            readByte(byteArrayInputStream);
                            int i = readByte(byteArrayInputStream);
                            for (int i2 = 0; i2 < i; i2++) {
                                readItem(byteArrayInputStream);
                            }
                            int i3 = readInt(byteArrayInputStream);
                            for (int i4 = 0; i4 < i3; i4++) {
                                readItem(byteArrayInputStream);
                            }
                            readTactics(byteArrayInputStream);
                            readTactics(byteArrayInputStream);
                            int i5 = readShort(byteArrayInputStream);
                            SparseArray<OldGameFlag> sparseArray = new SparseArray<>(i5);
                            for (int i6 = 0; i6 < i5; i6++) {
                                OldGameFlag flag = readFlag(byteArrayInputStream);
                                sparseArray.put(flag.type, flag);
                            }
                            byteArrayInputStream.close();
                            this.gameCleared = isCleared(sparseArray, 15);
                            this.mazeCleared = isCleared(sparseArray, 16);
                            this.expCounterStopped = this.exp == 999999999;
                            return true;
                        } catch (IOException unused2) {
                            this.errorMessage = context.getString(C0380R.string.msg_internal_error);
                            return false;
                        }
                    } catch (IOException unused3) {
                        this.errorMessage = context.getString(C0380R.string.msg_invalid_prevous_app_data);
                        return false;
                    }
                } catch (IOException unused4) {
                    bufferedReader = bufferedReader2;
                    this.errorMessage = context.getString(C0380R.string.msg_backup_file_io_error);
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException unused5) {
                        }
                    }
                    return false;
                } catch (Throwable th) {
                    th = th;
                    bufferedReader = bufferedReader2;
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException unused6) {
                        }
                    }
                    throw th;
                }
            } catch (IOException unused7) {
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private OldGameFlag readFlag(ByteArrayInputStream byteArrayInputStream) throws IOException {
        OldGameFlag oldGameFlag = new OldGameFlag();
        oldGameFlag.type = readInt(byteArrayInputStream);
        oldGameFlag.value = readByte(byteArrayInputStream) != 0;
        oldGameFlag.option = readString(byteArrayInputStream);
        return oldGameFlag;
    }

    protected int readByte(ByteArrayInputStream byteArrayInputStream) {
        return byteArrayInputStream.read();
    }

    protected int readShort(ByteArrayInputStream byteArrayInputStream) {
        int i = byteArrayInputStream.read();
        return ((byteArrayInputStream.read() << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (i & 255);
    }

    protected int readInt(ByteArrayInputStream byteArrayInputStream) throws IOException {
        byte[] bArr = new byte[4];
        byteArrayInputStream.read(bArr);
        return ((((((bArr[3] & 255) << 8) | (bArr[2] & 255)) << 8) | (bArr[1] & 255)) << 8) | (bArr[0] & 255);
    }

    protected String readString(ByteArrayInputStream byteArrayInputStream) throws IOException {
        int i = readShort(byteArrayInputStream);
        if (i == 0) {
            return null;
        }
        byte[] bArr = new byte[i];
        byteArrayInputStream.read(bArr);
        return new String(bArr);
    }

    protected int readItem(ByteArrayInputStream byteArrayInputStream) {
        int i = readShort(byteArrayInputStream) & 32767;
        if (i == 32767) {
            return 0;
        }
        return i;
    }

    protected void readTactics(ByteArrayInputStream byteArrayInputStream) {
        int i = readByte(byteArrayInputStream);
        for (int i2 = 0; i2 < i; i2++) {
            readByte(byteArrayInputStream);
            readItem(byteArrayInputStream);
            readByte(byteArrayInputStream);
            readByte(byteArrayInputStream);
            readItem(byteArrayInputStream);
            readByte(byteArrayInputStream);
        }
    }

    private boolean isCleared(SparseArray<OldGameFlag> sparseArray, int i) {
        OldGameFlag oldGameFlag = sparseArray.get(i + 1000);
        return oldGameFlag != null && oldGameFlag.value;
    }

    public String toString() {
        return "[" + this.exp + "," + this.level + "," + this.gold + "," + this.mazeCleared + "," + this.gameCleared + "]";
    }

    public static String getPreviousBackupFilePath(Context context) {
        return new File(DeviceUtil.getDataDirectory(context), context.getString(C0380R.string.res_previous_app_backup_file)).getAbsolutePath();
    }
}
