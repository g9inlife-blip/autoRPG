package com.shirobakama.autorpg2.p001db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.logquest2.C0380R;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public final class BackupRestoreUtil {
    private static final byte[] FILE_ID = {16, 85, 21, -98, 0, 1};
    private static final String TABLE_DB_VERSION = "db_version";
    protected static final String TAG = "backup-restore";
    private static final String TEMP_DB_NAME = "backup_temp.db";

    private BackupRestoreUtil() {
    }

    static class BackupData {
        List<List<Object[]>> blobTables;
        List<List<String[]>> normalTables;

        BackupData() {
        }
    }

    public static String createBackupFile(Context context) throws Throwable {
        SQLiteDatabase readableDatabase;
        SQLiteDatabase sQLiteDatabaseOpenOrCreateDatabase;
        FileOutputStream fileOutputStream;
        if (!isExternalStorageWritable()) {
            return context.getString(C0380R.string.msg_external_storage_not_writable);
        }
        Persister persister = new Persister(context);
        SQLiteDatabase sQLiteDatabase = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            try {
                readableDatabase = persister.getReadableDatabase();
            } catch (Throwable th) {
                th = th;
            }
            try {
                BackupData backupData = persister.backupData(readableDatabase);
                if (readableDatabase != null) {
                    readableDatabase.close();
                }
                File databasePath = context.getDatabasePath(TEMP_DB_NAME);
                databasePath.delete();
                try {
                    sQLiteDatabaseOpenOrCreateDatabase = context.openOrCreateDatabase(TEMP_DB_NAME, 0, null);
                } catch (Throwable th2) {
                    th = th2;
                    sQLiteDatabaseOpenOrCreateDatabase = null;
                }
                try {
                    sQLiteDatabaseOpenOrCreateDatabase.beginTransaction();
                    SimpleRpgOpenHelper.createDb(sQLiteDatabaseOpenOrCreateDatabase);
                    saveDbVersion(sQLiteDatabaseOpenOrCreateDatabase);
                    persister.restoreData(sQLiteDatabaseOpenOrCreateDatabase, backupData);
                    sQLiteDatabaseOpenOrCreateDatabase.setTransactionSuccessful();
                    if (sQLiteDatabaseOpenOrCreateDatabase != null) {
                        sQLiteDatabaseOpenOrCreateDatabase.endTransaction();
                        sQLiteDatabaseOpenOrCreateDatabase.close();
                    }
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                    try {
                        BufferedInputStream bufferedInputStream2 = new BufferedInputStream(new FileInputStream(databasePath));
                        while (true) {
                            try {
                                int i = bufferedInputStream2.read();
                                if (i == -1) {
                                    break;
                                }
                                gZIPOutputStream.write(i);
                            } catch (Throwable th3) {
                                th = th3;
                                bufferedInputStream = bufferedInputStream2;
                                if (bufferedInputStream != null) {
                                    bufferedInputStream.close();
                                }
                                databasePath.delete();
                                throw th;
                            }
                        }
                        bufferedInputStream2.close();
                        databasePath.delete();
                        gZIPOutputStream.close();
                        byte[] bArrEncrypt = encrypt(context, byteArrayOutputStream.toByteArray());
                        String strPrepareDirectory = prepareDirectory(context);
                        if (strPrepareDirectory != null) {
                            return strPrepareDirectory;
                        }
                        try {
                            fileOutputStream = new FileOutputStream(new File(getBackupFilePath(context)));
                        } catch (Throwable th4) {
                            th = th4;
                            fileOutputStream = null;
                        }
                        try {
                            fileOutputStream.write(FILE_ID);
                            fileOutputStream.write(bArrEncrypt);
                            fileOutputStream.close();
                            return null;
                        } catch (Throwable th5) {
                            th = th5;
                            if (fileOutputStream != null) {
                                fileOutputStream.close();
                            }
                            throw th;
                        }
                    } catch (Throwable th6) {
                        th = th6;
                    }
                } catch (Throwable th7) {
                    th = th7;
                    if (sQLiteDatabaseOpenOrCreateDatabase != null) {
                        sQLiteDatabaseOpenOrCreateDatabase.endTransaction();
                        sQLiteDatabaseOpenOrCreateDatabase.close();
                    }
                    throw th;
                }
            } catch (Throwable th8) {
                th = th8;
                sQLiteDatabase = readableDatabase;
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.close();
                }
                throw th;
            }
        } catch (SQLiteException unused) {
            return context.getString(C0380R.string.msg_internal_error);
        } catch (IOException unused2) {
            return context.getString(C0380R.string.msg_create_backup_file_error);
        } catch (IllegalStateException unused3) {
            return context.getString(C0380R.string.msg_internal_error);
        }
    }

    private static String prepareDirectory(Context context) {
        File dataDirectory = DeviceUtil.getDataDirectory(context);
        if (dataDirectory.exists() || dataDirectory.mkdirs()) {
            return null;
        }
        return context.getString(C0380R.string.msg_create_backup_file_error);
    }

    /* JADX WARN: Removed duplicated region for block: B:167:0x015e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:173:0x0157 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:192:? A[Catch: IllegalStateException -> 0x017c, InvalidKeyException -> 0x0181, SQLiteException -> 0x0189, SYNTHETIC, TRY_ENTER, TRY_LEAVE, TryCatch #22 {SQLiteException -> 0x0189, IllegalStateException -> 0x017c, InvalidKeyException -> 0x0181, blocks: (B:11:0x0028, B:25:0x0060, B:28:0x0067, B:30:0x006c, B:33:0x0075, B:36:0x007d, B:38:0x0082, B:45:0x00ad, B:46:0x00b0, B:47:0x00b3, B:53:0x00ca, B:54:0x00cd, B:60:0x00e0, B:61:0x00e3, B:65:0x00ed, B:66:0x00f0, B:68:0x00fb, B:73:0x0112, B:80:0x011f, B:81:0x0125, B:87:0x012c, B:88:0x012f, B:89:0x0132, B:112:0x0157, B:116:0x015e, B:117:0x0161, B:102:0x0147, B:106:0x014e, B:129:0x0178, B:130:0x017b, B:126:0x0172), top: B:183:0x0028 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.String restoreBackupFile(android.content.Context r10) throws java.lang.Throwable {
        /*
            Method dump skipped, instructions count: 398
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.p001db.BackupRestoreUtil.restoreBackupFile(android.content.Context):java.lang.String");
    }

    @SuppressLint({"TrulyRandom"})
    private static byte[] encrypt(Context context, byte[] bArr) throws IllegalStateException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(getKey(context), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(1, secretKeySpec);
            return cipher.doFinal(bArr);
        } catch (InvalidKeyException e) {
            throw new IllegalStateException(e);
        } catch (NoSuchAlgorithmException e2) {
            throw new IllegalStateException(e2);
        } catch (BadPaddingException e3) {
            throw new IllegalStateException(e3);
        } catch (IllegalBlockSizeException e4) {
            throw new IllegalStateException(e4);
        } catch (NoSuchPaddingException e5) {
            throw new IllegalStateException(e5);
        }
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    private static byte[] getKey(Context context) throws PackageManager.NameNotFoundException {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 64);
            byte[] bytes = null;
            for (int i = 0; i < packageInfo.signatures.length && (bytes = packageInfo.signatures[i].toByteArray()) == null; i++) {
            }
            if (bytes == null) {
                bytes = context.getString(C0380R.string.logical_app_name).getBytes();
            }
            byte[] bArr = new byte[32];
            int iMax = Math.max(bArr.length, bytes.length);
            for (int i2 = 0; i2 < iMax; i2++) {
                int length = i2 % bArr.length;
                bArr[length] = (byte) (bArr[length] ^ bytes[i2 % bytes.length]);
            }
            return bArr;
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private static byte[] decrypt(Context context, byte[] bArr) throws IllegalStateException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(getKey(context), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(2, secretKeySpec);
            return cipher.doFinal(bArr);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        } catch (BadPaddingException e2) {
            throw new IllegalStateException(e2);
        } catch (IllegalBlockSizeException e3) {
            throw new IllegalStateException(e3);
        } catch (NoSuchPaddingException e4) {
            throw new IllegalStateException(e4);
        }
    }

    private static void saveDbVersion(SQLiteDatabase sQLiteDatabase) throws SQLException {
        sQLiteDatabase.execSQL("create table db_version (dbVersion integer)");
        ContentValues contentValues = new ContentValues();
        contentValues.put("dbVersion", (Integer) 105);
        sQLiteDatabase.insertOrThrow(TABLE_DB_VERSION, null, contentValues);
    }

    private static int loadDbVersion(SQLiteDatabase sQLiteDatabase) {
        Cursor cursorQuery = sQLiteDatabase.query(TABLE_DB_VERSION, new String[]{"dbVersion"}, null, null, null, null, null);
        int i = cursorQuery.moveToNext() ? cursorQuery.getInt(0) : -1;
        cursorQuery.close();
        return i;
    }

    static boolean isExternalStorageReadable() {
        String externalStorageState = Environment.getExternalStorageState();
        return "mounted".equals(externalStorageState) || "mounted_ro".equals(externalStorageState);
    }

    static boolean isExternalStorageWritable() {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    public static String getBackupFilePath(Context context) {
        return new File(DeviceUtil.getDataDirectory(context), context.getString(C0380R.string.res_backup_file)).getAbsolutePath();
    }
}
