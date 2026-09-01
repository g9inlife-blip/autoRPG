package com.shirobakama.autorpg2.p001db;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.SparseArray;
import com.shirobakama.autorpg2.entity.AdventureLog;
import com.shirobakama.autorpg2.entity.CommonLog;
import com.shirobakama.autorpg2.entity.Dungeon;
import com.shirobakama.autorpg2.entity.FightingLog;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.autorpg2.entity.LogEnemy;
import com.shirobakama.autorpg2.entity.LogFight;
import com.shirobakama.autorpg2.entity.LogManagement;
import com.shirobakama.autorpg2.entity.LogStatus;
import com.shirobakama.autorpg2.entity.Monster;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.repo.DungeonRepository;
import com.shirobakama.autorpg2.repo.ItemRepository;
import com.shirobakama.autorpg2.repo.MonsterRepository;
import com.shirobakama.autorpg2.util.Base64;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.logquest2.C0380R;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class LogPersister {
    private static final String DELIMITER = "////";
    private static final String HEADER_IDENTIFIER = "logquest2_log_file";
    protected static final String TAG = "log-persister";
    protected SparseArray<AdventureLog> mAdvLogForId;
    private List<AdventureLog> mAdventureLogs;
    protected Context mContext;
    protected SparseArray<FightingLog> mFightingLogForId;
    private String mFileName;
    private BufferedReader mIn;
    private LogManagement mLogManagement;
    protected SparseArray<LogStatus> mLogStatus;
    private PrintWriter mOut;
    protected SparseArray<PlayerChar> mPlayerChars;
    private boolean mTerminate;
    private static final Pattern PATTERN_TAB = Pattern.compile("\\t");
    private static final Pattern PATTERN_COMMA = Pattern.compile(",");
    private final int LOG_VERSION = 10000;
    private final int LOG_TOTAL_VERSION = 10105;

    private interface LineFetcher {
        void fetch(String[] strArr);
    }

    public class TerminatedException extends RuntimeException {
        public TerminatedException() {
        }
    }

    public LogPersister(Context context, LogManagement logManagement, SparseArray<PlayerChar> sparseArray) {
        this.mContext = context;
        this.mLogManagement = logManagement;
        this.mPlayerChars = sparseArray;
    }

    public LogPersister(Context context, String str) {
        this.mContext = context;
        this.mFileName = str;
    }

    public int readLogs() throws IOException {
        if (!BackupRestoreUtil.isExternalStorageReadable()) {
            return C0380R.string.msg_external_storage_not_readable;
        }
        File file = new File(this.mFileName);
        if (!file.exists() || !file.isFile()) {
            return C0380R.string.msg_logview_cannot_read_file;
        }
        this.mPlayerChars = new SparseArray<>();
        this.mAdvLogForId = new SparseArray<>();
        this.mFightingLogForId = new SparseArray<>();
        this.mLogStatus = new SparseArray<>();
        this.mIn = null;
        try {
            this.mIn = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
            int logsFromFile = readLogsFromFile();
            if (logsFromFile != 0) {
                BufferedReader bufferedReader = this.mIn;
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException unused) {
                    }
                }
                return logsFromFile;
            }
            BufferedReader bufferedReader2 = this.mIn;
            if (bufferedReader2 != null) {
                try {
                    bufferedReader2.close();
                } catch (IOException unused2) {
                }
            }
            List<CommonLog.LogChar> list = null;
            List<AdventureLog.LogInventory> list2 = null;
            for (AdventureLog adventureLog : this.mAdventureLogs) {
                if (adventureLog.logChars != null) {
                    list = adventureLog.logChars;
                } else {
                    adventureLog.logChars = list;
                }
                if (adventureLog.logInventories != null) {
                    list2 = adventureLog.logInventories;
                } else {
                    adventureLog.logInventories = list2;
                }
                if (adventureLog.logFight != null && adventureLog.logFight.fightingLogs != null) {
                    List<CommonLog.LogChar> list3 = null;
                    List<LogEnemy> list4 = null;
                    FightingLog fightingLog = null;
                    for (FightingLog fightingLog2 : adventureLog.logFight.fightingLogs) {
                        if (fightingLog2.logChars != null) {
                            list3 = fightingLog2.logChars;
                        } else {
                            fightingLog2.logChars = list3;
                        }
                        if (fightingLog2.logEnemies != null) {
                            list4 = fightingLog2.logEnemies;
                        } else {
                            fightingLog2.logEnemies = list4;
                        }
                        fightingLog2.logFight = adventureLog.logFight;
                        fightingLog2.previousLog = fightingLog;
                        fightingLog = fightingLog2;
                    }
                }
            }
            return 0;
        } catch (IOException unused3) {
            BufferedReader bufferedReader3 = this.mIn;
            if (bufferedReader3 != null) {
                try {
                    bufferedReader3.close();
                } catch (IOException unused4) {
                }
            }
            return C0380R.string.msg_logview_read_file_io_error;
        } catch (Throwable th) {
            BufferedReader bufferedReader4 = this.mIn;
            if (bufferedReader4 != null) {
                try {
                    bufferedReader4.close();
                } catch (IOException unused5) {
                }
            }
            throw th;
        }
    }

    private int readLogsFromFile() throws IOException {
        int logEnemyChar;
        if (!HEADER_IDENTIFIER.equals(this.mIn.readLine())) {
            return C0380R.string.msg_logview_not_log_file;
        }
        int intLine = readIntLine();
        int i = intLine - (intLine % 10000);
        if (i < 10000) {
            return C0380R.string.msg_logview_illegal_file;
        }
        if (i > 10000) {
            return C0380R.string.msg_logview_illegal_file_newer_version;
        }
        if (!validateDelimiter()) {
            return C0380R.string.msg_logview_illegal_file;
        }
        readPlayerChars();
        do {
            String line = this.mIn.readLine();
            if (line == null) {
                return 0;
            }
            if (line.equals(SimpleRpgOpenHelper.TABLE_LOG_MANAGEMENT)) {
                logEnemyChar = readLogManagement();
            } else if (line.equals(SimpleRpgOpenHelper.TABLE_ADVENTURE_LOG)) {
                logEnemyChar = readAdventureLog();
            } else if (line.equals(SimpleRpgOpenHelper.TABLE_LOG_FIGHT)) {
                logEnemyChar = readLogFight();
            } else if (line.equals(SimpleRpgOpenHelper.TABLE_FIGHTING_LOG)) {
                logEnemyChar = readFightingLog();
            } else if (line.equals(SimpleRpgOpenHelper.TABLE_LOG_CHAR)) {
                logEnemyChar = readLogChar();
            } else if (line.equals(SimpleRpgOpenHelper.TABLE_LOG_STATUS)) {
                logEnemyChar = readLogStatus();
            } else if (line.equals(SimpleRpgOpenHelper.TABLE_LOG_ITEM)) {
                logEnemyChar = readLogItem();
            } else {
                logEnemyChar = line.equals(SimpleRpgOpenHelper.TABLE_LOG_ENEMY_CHAR) ? readLogEnemyChar() : C0380R.string.msg_logview_illegal_file;
            }
        } while (logEnemyChar == 0);
        return logEnemyChar;
    }

    private void readPlayerChars() throws IOException {
        readTableData(new String[]{"_id", "presetBitmapId", "bitmap"}, new LineFetcher() { // from class: com.shirobakama.autorpg2.db.LogPersister.1
            @Override // com.shirobakama.autorpg2.db.LogPersister.LineFetcher
            public void fetch(String[] strArr) {
                PlayerChar playerChar = new PlayerChar();
                playerChar.f106id = LogPersister.this.parseInt(strArr[0]);
                if (playerChar.f106id == 0) {
                    return;
                }
                playerChar.presetBitmapId = LogPersister.this.parseInt(strArr[1]);
                String str = strArr[2];
                if (str != null) {
                    byte[] bArrDecode = null;
                    try {
                        bArrDecode = Base64.decode(str);
                    } catch (IOException unused) {
                    }
                    if (bArrDecode != null) {
                        playerChar.bitmap = BitmapFactory.decodeByteArray(bArrDecode, 0, bArrDecode.length);
                    }
                }
                LogPersister.this.mPlayerChars.put(playerChar.f106id, playerChar);
            }
        });
    }

    private int readLogManagement() throws IOException {
        final LogManagement logManagement = new LogManagement();
        readTableData(Persister.LOG_MAGEMENT_COLUMNS, new LineFetcher() { // from class: com.shirobakama.autorpg2.db.LogPersister.2
            @Override // com.shirobakama.autorpg2.db.LogPersister.LineFetcher
            public void fetch(String[] strArr) {
                logManagement.f103id = LogPersister.this.parseInt(strArr[0]);
                logManagement.pcId = new int[]{LogPersister.this.parseInt(strArr[1]), LogPersister.this.parseInt(strArr[2]), LogPersister.this.parseInt(strArr[3])};
                LogManagement logManagement2 = logManagement;
                logManagement2.pcName = new String[]{strArr[4], strArr[5], strArr[6]};
                logManagement2.dungeonId = LogPersister.this.parseInt(strArr[7]);
                logManagement.targetFloor = LogPersister.this.parseInt(strArr[8]);
                logManagement.completed = LogPersister.this.parseBoolean(strArr[9]);
            }
        });
        this.mLogManagement = logManagement;
        for (int i = 0; i < logManagement.pcId.length; i++) {
            if (logManagement.pcId[i] != 0) {
                PlayerChar playerChar = this.mPlayerChars.get(logManagement.pcId[i]);
                if (playerChar == null) {
                    playerChar = new PlayerChar();
                    playerChar.f106id = logManagement.pcId[i];
                    this.mPlayerChars.put(playerChar.f106id, playerChar);
                }
                playerChar.name = logManagement.pcName[i];
            }
        }
        return 0;
    }

    private void readTableData(String[] strArr, LineFetcher lineFetcher) throws IOException {
        String[] unknownCountStrings = readUnknownCountStrings();
        if (unknownCountStrings == null) {
            return;
        }
        int[] iArr = new int[unknownCountStrings.length];
        for (int i = 0; i < unknownCountStrings.length; i++) {
            iArr[i] = -1;
            int i2 = 0;
            while (true) {
                if (i2 >= strArr.length) {
                    break;
                }
                if (unknownCountStrings[i].equals(strArr[i2])) {
                    iArr[i] = i2;
                    break;
                }
                i2++;
            }
        }
        int length = unknownCountStrings.length;
        String[] strArr2 = new String[strArr.length];
        while (true) {
            String[] strings = readStrings(length);
            if (strings == null) {
                return;
            }
            int i3 = 0;
            while (i3 < strArr2.length) {
                int i4 = iArr[i3];
                if (i4 >= 0) {
                    strArr2[i4] = i3 < strings.length ? strings[i3] : null;
                }
                i3++;
            }
            lineFetcher.fetch(strArr2);
        }
    }

    private String[] readUnknownCountStrings() throws IOException {
        String line = this.mIn.readLine();
        if (line == null || line.equals(DELIMITER)) {
            return null;
        }
        return PATTERN_TAB.split(line);
    }

    private String[] readStrings(int i) throws IOException {
        String line = this.mIn.readLine();
        if (line == null || line.equals(DELIMITER)) {
            return null;
        }
        String[] strArr = new String[i];
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (true) {
            if (i2 >= line.length()) {
                break;
            }
            if (line.charAt(i2) == '\t') {
                int i5 = i3 + 1;
                strArr[i3] = line.substring(i4, i2);
                if (i5 >= i) {
                    i3 = i5;
                    break;
                }
                i4 = i2 + 1;
                i3 = i5;
            }
            i2++;
        }
        if (i3 < i && i4 < line.length()) {
            strArr[i3] = line.substring(i4);
        }
        return strArr;
    }

    protected boolean parseBoolean(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (str.equals("1")) {
            return true;
        }
        if (str.equals("0")) {
            return false;
        }
        return Boolean.parseBoolean(str);
    }

    protected int parseInt(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        return Integer.parseInt(str);
    }

    private int readAdventureLog() throws IOException {
        final ArrayList arrayList = new ArrayList();
        final CommonLog.LogType[] logTypeArrValues = CommonLog.LogType.values();
        readTableData(Persister.ADVENTURE_LOG_COLUMNS, new LineFetcher() { // from class: com.shirobakama.autorpg2.db.LogPersister.3
            /* JADX WARN: Removed duplicated region for block: B:8:0x005d  */
            @Override // com.shirobakama.autorpg2.db.LogPersister.LineFetcher
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public void fetch(java.lang.String[] r7) {
                /*
                    r6 = this;
                    com.shirobakama.autorpg2.entity.AdventureLog r0 = new com.shirobakama.autorpg2.entity.AdventureLog
                    r0.<init>()
                    com.shirobakama.autorpg2.db.LogPersister r1 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r2 = 0
                    r2 = r7[r2]
                    int r1 = r1.parseInt(r2)
                    r0.f85id = r1
                    com.shirobakama.autorpg2.db.LogPersister r1 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r2 = 2
                    r2 = r7[r2]
                    int r1 = r1.parseInt(r2)
                    r0.logTime = r1
                    com.shirobakama.autorpg2.db.LogPersister r1 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r2 = 3
                    r2 = r7[r2]
                    int r1 = r1.parseInt(r2)
                    com.shirobakama.autorpg2.db.LogPersister r2 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r3 = 4
                    r3 = r7[r3]
                    int r2 = r2.parseInt(r3)
                    com.shirobakama.autorpg2.db.LogPersister r3 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r4 = 5
                    r4 = r7[r4]
                    int r3 = r3.parseInt(r4)
                    r4 = 6
                    r4 = r7[r4]
                    r0.title = r4
                    r4 = 7
                    r4 = r7[r4]
                    r0.desc1 = r4
                    r4 = 8
                    r4 = r7[r4]
                    r0.desc2 = r4
                    com.shirobakama.autorpg2.db.LogPersister r4 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r5 = 9
                    r7 = r7[r5]
                    int r7 = r4.parseInt(r7)
                    r0.gold = r7
                    if (r1 < 0) goto L5d
                    com.shirobakama.autorpg2.entity.CommonLog$LogType[] r7 = r2
                    int r4 = r7.length
                    if (r1 < r4) goto L5a
                    goto L5d
                L5a:
                    r7 = r7[r1]
                    goto L5f
                L5d:
                    com.shirobakama.autorpg2.entity.CommonLog$LogType r7 = com.shirobakama.autorpg2.entity.CommonLog.LogType.UNDEFINED
                L5f:
                    r0.type = r7
                    r7 = 0
                    if (r2 != 0) goto L66
                    r1 = r7
                    goto L6c
                L66:
                    com.shirobakama.autorpg2.db.LogPersister r1 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    com.shirobakama.autorpg2.entity.Item r1 = r1.getItemAwareUnknown(r2)
                L6c:
                    r0.item = r1
                    if (r3 != 0) goto L71
                    goto L7b
                L71:
                    com.shirobakama.autorpg2.db.LogPersister r7 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    android.util.SparseArray<com.shirobakama.autorpg2.entity.PlayerChar> r7 = r7.mPlayerChars
                    java.lang.Object r7 = r7.get(r3)
                    com.shirobakama.autorpg2.entity.PlayerChar r7 = (com.shirobakama.autorpg2.entity.PlayerChar) r7
                L7b:
                    r0.playerChar = r7
                    java.util.List r7 = r3
                    r7.add(r0)
                    com.shirobakama.autorpg2.db.LogPersister r7 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    android.util.SparseArray<com.shirobakama.autorpg2.entity.AdventureLog> r7 = r7.mAdvLogForId
                    int r1 = r0.f85id
                    r7.put(r1, r0)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.p001db.LogPersister.C03363.fetch(java.lang.String[]):void");
            }
        });
        this.mAdventureLogs = arrayList;
        return 0;
    }

    protected Item getItemAwareUnknown(int i) {
        Item item = ItemRepository.getItem(this.mContext, i);
        if (item != null) {
            return item;
        }
        Item item2 = new Item(-1);
        item2.name = this.mContext.getString(C0380R.string.res_logview_unknown_item);
        return item2;
    }

    protected Monster getMonsterAwareUnknown(int i) {
        Monster monster = MonsterRepository.getMonster(this.mContext, i);
        if (monster != null) {
            return monster;
        }
        Monster monster2 = new Monster();
        monster2.f105id = -1;
        monster2.name = this.mContext.getString(C0380R.string.res_logview_unknown_monster);
        monster2.drawableId = C0380R.drawable.monster_002_slime;
        monster2.type = Monster.MonsterType.OTHER;
        monster2.descriptionId = 0;
        return monster2;
    }

    private int readLogFight() throws IOException {
        readTableData(Persister.LOG_FIGHT_COLUMNS, new LineFetcher() { // from class: com.shirobakama.autorpg2.db.LogPersister.4
            @Override // com.shirobakama.autorpg2.db.LogPersister.LineFetcher
            public void fetch(String[] strArr) {
                LogFight logFight = new LogFight();
                logFight.f102id = LogPersister.this.parseInt(strArr[0]);
                logFight.adventureLogId = LogPersister.this.parseInt(strArr[1]);
                logFight.isWandering = LogPersister.this.parseBoolean(strArr[2]);
                logFight.isEvent = LogPersister.this.parseBoolean(strArr[3]);
                int i = LogPersister.this.parseInt(strArr[4]);
                int i2 = LogPersister.this.parseInt(strArr[5]);
                int i3 = LogPersister.this.parseInt(strArr[6]);
                logFight.monster = i == 0 ? null : LogPersister.this.getMonsterAwareUnknown(i);
                logFight.monster2 = i2 == 0 ? null : LogPersister.this.getMonsterAwareUnknown(i2);
                logFight.monster3 = i3 != 0 ? LogPersister.this.getMonsterAwareUnknown(i3) : null;
                AdventureLog adventureLog = LogPersister.this.mAdvLogForId.get(logFight.adventureLogId);
                if (adventureLog != null) {
                    adventureLog.logFight = logFight;
                }
            }
        });
        return 0;
    }

    private int readFightingLog() throws IOException {
        final CommonLog.LogType[] logTypeArrValues = CommonLog.LogType.values();
        readTableData(Persister.FIGHTING_LOG_COLUMNS, new LineFetcher() { // from class: com.shirobakama.autorpg2.db.LogPersister.5
            /* JADX WARN: Removed duplicated region for block: B:8:0x0080  */
            @Override // com.shirobakama.autorpg2.db.LogPersister.LineFetcher
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public void fetch(java.lang.String[] r7) {
                /*
                    r6 = this;
                    com.shirobakama.autorpg2.entity.FightingLog r0 = new com.shirobakama.autorpg2.entity.FightingLog
                    r0.<init>()
                    com.shirobakama.autorpg2.db.LogPersister r1 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r2 = 0
                    r2 = r7[r2]
                    int r1 = r1.parseInt(r2)
                    r0.f85id = r1
                    com.shirobakama.autorpg2.db.LogPersister r1 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r2 = 1
                    r2 = r7[r2]
                    int r1 = r1.parseInt(r2)
                    com.shirobakama.autorpg2.db.LogPersister r2 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r3 = 2
                    r3 = r7[r3]
                    int r2 = r2.parseInt(r3)
                    com.shirobakama.autorpg2.db.LogPersister r3 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r4 = 3
                    r4 = r7[r4]
                    int r3 = r3.parseInt(r4)
                    r4 = 4
                    r4 = r7[r4]
                    r0.title = r4
                    r4 = 5
                    r4 = r7[r4]
                    r0.desc1 = r4
                    r4 = 6
                    r4 = r7[r4]
                    r0.desc2 = r4
                    com.shirobakama.autorpg2.db.LogPersister r4 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r5 = 7
                    r5 = r7[r5]
                    int r4 = r4.parseInt(r5)
                    r0.adventureLogId = r4
                    com.shirobakama.autorpg2.db.LogPersister r4 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r5 = 8
                    r5 = r7[r5]
                    boolean r4 = r4.parseBoolean(r5)
                    r0.playersAct = r4
                    com.shirobakama.autorpg2.db.LogPersister r4 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r5 = 9
                    r5 = r7[r5]
                    boolean r4 = r4.parseBoolean(r5)
                    r0.toPlayer = r4
                    com.shirobakama.autorpg2.db.LogPersister r4 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r5 = 10
                    r5 = r7[r5]
                    int r4 = r4.parseInt(r5)
                    r0.enemyIndex = r4
                    com.shirobakama.autorpg2.db.LogPersister r4 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r5 = 11
                    r7 = r7[r5]
                    int[] r7 = r4.parseStringArray(r7)
                    r0.targetIds = r7
                    if (r1 < 0) goto L80
                    com.shirobakama.autorpg2.entity.CommonLog$LogType[] r7 = r2
                    int r4 = r7.length
                    if (r1 < r4) goto L7d
                    goto L80
                L7d:
                    r7 = r7[r1]
                    goto L82
                L80:
                    com.shirobakama.autorpg2.entity.CommonLog$LogType r7 = com.shirobakama.autorpg2.entity.CommonLog.LogType.UNDEFINED
                L82:
                    r0.type = r7
                    r7 = 0
                    if (r2 != 0) goto L89
                    r1 = r7
                    goto L91
                L89:
                    com.shirobakama.autorpg2.db.LogPersister r1 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    android.content.Context r1 = r1.mContext
                    com.shirobakama.autorpg2.entity.Item r1 = com.shirobakama.autorpg2.repo.ItemRepository.getItem(r1, r2)
                L91:
                    r0.item = r1
                    if (r3 != 0) goto L96
                    goto La0
                L96:
                    com.shirobakama.autorpg2.db.LogPersister r7 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    android.util.SparseArray<com.shirobakama.autorpg2.entity.PlayerChar> r7 = r7.mPlayerChars
                    java.lang.Object r7 = r7.get(r3)
                    com.shirobakama.autorpg2.entity.PlayerChar r7 = (com.shirobakama.autorpg2.entity.PlayerChar) r7
                La0:
                    r0.playerChar = r7
                    com.shirobakama.autorpg2.db.LogPersister r7 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    android.util.SparseArray<com.shirobakama.autorpg2.entity.AdventureLog> r7 = r7.mAdvLogForId
                    int r1 = r0.adventureLogId
                    java.lang.Object r7 = r7.get(r1)
                    com.shirobakama.autorpg2.entity.AdventureLog r7 = (com.shirobakama.autorpg2.entity.AdventureLog) r7
                    if (r7 == 0) goto Lca
                    com.shirobakama.autorpg2.entity.LogFight r1 = r7.logFight
                    if (r1 == 0) goto Lca
                    com.shirobakama.autorpg2.entity.LogFight r1 = r7.logFight
                    java.util.List<com.shirobakama.autorpg2.entity.FightingLog> r1 = r1.fightingLogs
                    if (r1 != 0) goto Lc3
                    com.shirobakama.autorpg2.entity.LogFight r1 = r7.logFight
                    java.util.ArrayList r2 = new java.util.ArrayList
                    r2.<init>()
                    r1.fightingLogs = r2
                Lc3:
                    com.shirobakama.autorpg2.entity.LogFight r7 = r7.logFight
                    java.util.List<com.shirobakama.autorpg2.entity.FightingLog> r7 = r7.fightingLogs
                    r7.add(r0)
                Lca:
                    com.shirobakama.autorpg2.db.LogPersister r7 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    android.util.SparseArray<com.shirobakama.autorpg2.entity.FightingLog> r7 = r7.mFightingLogForId
                    int r1 = r0.f85id
                    r7.put(r1, r0)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.p001db.LogPersister.C03385.fetch(java.lang.String[]):void");
            }
        });
        return 0;
    }

    protected int[] parseStringArray(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String[] strArrSplit = PATTERN_COMMA.split(str);
        int[] iArr = new int[strArrSplit.length];
        for (int i = 0; i < strArrSplit.length; i++) {
            iArr[i] = Integer.parseInt(strArrSplit[i]);
        }
        return iArr;
    }

    private int readLogChar() throws IOException {
        readTableData(Persister.LOG_CHAR_COLUMNS, new LineFetcher() { // from class: com.shirobakama.autorpg2.db.LogPersister.6
            @Override // com.shirobakama.autorpg2.db.LogPersister.LineFetcher
            public void fetch(String[] strArr) {
                AdventureLog adventureLog;
                CommonLog.LogChar logChar = new CommonLog.LogChar();
                logChar.f87id = LogPersister.this.parseInt(strArr[0]);
                logChar.adventureLogId = LogPersister.this.parseInt(strArr[1]);
                logChar.fightingLogId = LogPersister.this.parseInt(strArr[2]);
                logChar.charId = LogPersister.this.parseInt(strArr[3]);
                logChar.f86hp = LogPersister.this.parseInt(strArr[4]);
                logChar.maxHp = LogPersister.this.parseInt(strArr[5]);
                logChar.f88mp = LogPersister.this.parseInt(strArr[6]);
                logChar.maxMp = LogPersister.this.parseInt(strArr[7]);
                logChar.exp = LogPersister.this.parseInt(strArr[8]);
                logChar.level = LogPersister.this.parseInt(strArr[9]);
                if (logChar.fightingLogId != 0) {
                    adventureLog = LogPersister.this.mFightingLogForId.get(logChar.fightingLogId);
                } else {
                    adventureLog = LogPersister.this.mAdvLogForId.get(logChar.adventureLogId);
                }
                if (adventureLog != null) {
                    if (adventureLog.logChars == null) {
                        adventureLog.logChars = new ArrayList();
                    }
                    adventureLog.logChars.add(logChar);
                }
            }
        });
        return 0;
    }

    private int readLogStatus() throws IOException {
        final LogStatus.LogAction[] logActionArrValues = LogStatus.LogAction.values();
        readTableData(Persister.LOG_STATUS_COLUMNS, new LineFetcher() { // from class: com.shirobakama.autorpg2.db.LogPersister.7
            /* JADX WARN: Removed duplicated region for block: B:8:0x0050  */
            @Override // com.shirobakama.autorpg2.db.LogPersister.LineFetcher
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public void fetch(java.lang.String[] r5) {
                /*
                    r4 = this;
                    com.shirobakama.autorpg2.entity.LogStatus r0 = new com.shirobakama.autorpg2.entity.LogStatus
                    r0.<init>()
                    com.shirobakama.autorpg2.db.LogPersister r1 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r2 = 0
                    r2 = r5[r2]
                    int r1 = r1.parseInt(r2)
                    r0.f104id = r1
                    com.shirobakama.autorpg2.db.LogPersister r1 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r2 = 2
                    r2 = r5[r2]
                    int r1 = r1.parseInt(r2)
                    r0.logTime = r1
                    com.shirobakama.autorpg2.db.LogPersister r1 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r2 = 3
                    r2 = r5[r2]
                    int r1 = r1.parseInt(r2)
                    com.shirobakama.autorpg2.db.LogPersister r2 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r3 = 4
                    r3 = r5[r3]
                    int r2 = r2.parseInt(r3)
                    r0.floor = r2
                    com.shirobakama.autorpg2.db.LogPersister r2 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r3 = 5
                    r3 = r5[r3]
                    int r2 = r2.parseInt(r3)
                    r0.block = r2
                    com.shirobakama.autorpg2.db.LogPersister r2 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    r3 = 6
                    r5 = r5[r3]
                    int r5 = r2.parseInt(r5)
                    r0.captiveRate = r5
                    if (r1 < 0) goto L50
                    com.shirobakama.autorpg2.entity.LogStatus$LogAction[] r5 = r2
                    int r2 = r5.length
                    if (r1 < r2) goto L4d
                    goto L50
                L4d:
                    r5 = r5[r1]
                    goto L52
                L50:
                    com.shirobakama.autorpg2.entity.LogStatus$LogAction r5 = com.shirobakama.autorpg2.entity.LogStatus.LogAction.MOVING
                L52:
                    r0.action = r5
                    com.shirobakama.autorpg2.db.LogPersister r5 = com.shirobakama.autorpg2.p001db.LogPersister.this
                    android.util.SparseArray<com.shirobakama.autorpg2.entity.LogStatus> r5 = r5.mLogStatus
                    int r1 = r0.logTime
                    r5.put(r1, r0)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.p001db.LogPersister.C03407.fetch(java.lang.String[]):void");
            }
        });
        return 0;
    }

    private int readLogItem() throws IOException {
        readTableData(Persister.LOG_ITEM_COLUMNS, new LineFetcher() { // from class: com.shirobakama.autorpg2.db.LogPersister.8
            @Override // com.shirobakama.autorpg2.db.LogPersister.LineFetcher
            public void fetch(String[] strArr) {
                AdventureLog.LogInventory logInventory = new AdventureLog.LogInventory();
                logInventory.f84id = LogPersister.this.parseInt(strArr[0]);
                logInventory.adventureLogId = LogPersister.this.parseInt(strArr[1]);
                logInventory.itemId = LogPersister.this.parseInt(strArr[2]);
                logInventory.equippedCharId = LogPersister.this.parseInt(strArr[3]);
                AdventureLog adventureLog = LogPersister.this.mAdvLogForId.get(logInventory.adventureLogId);
                if (adventureLog != null) {
                    if (adventureLog.logInventories == null) {
                        adventureLog.logInventories = new ArrayList();
                    }
                    adventureLog.logInventories.add(logInventory);
                }
            }
        });
        return 0;
    }

    private int readLogEnemyChar() throws IOException {
        readTableData(Persister.LOG_ENEMY_CHAR_COLUMNS, new LineFetcher() { // from class: com.shirobakama.autorpg2.db.LogPersister.9
            @Override // com.shirobakama.autorpg2.db.LogPersister.LineFetcher
            public void fetch(String[] strArr) {
                LogEnemy logEnemy = new LogEnemy();
                logEnemy.f100id = LogPersister.this.parseInt(strArr[0]);
                logEnemy.fightingLogId = LogPersister.this.parseInt(strArr[1]);
                logEnemy.enemyIndex = LogPersister.this.parseInt(strArr[2]);
                logEnemy.f99hp = LogPersister.this.parseInt(strArr[3]);
                logEnemy.f101mp = LogPersister.this.parseInt(strArr[4]);
                FightingLog fightingLog = LogPersister.this.mFightingLogForId.get(logEnemy.fightingLogId);
                if (fightingLog != null) {
                    if (fightingLog.logEnemies == null) {
                        fightingLog.logEnemies = new ArrayList();
                    }
                    fightingLog.logEnemies.add(logEnemy);
                }
            }
        });
        return 0;
    }

    private boolean validateDelimiter() throws IOException {
        return TextUtils.equals(this.mIn.readLine(), DELIMITER);
    }

    private int readIntLine() throws IOException {
        String line = this.mIn.readLine();
        if (line == null) {
            return 0;
        }
        return Integer.parseInt(line);
    }

    public int writeLogs(AdventureLog adventureLog) {
        if (!BackupRestoreUtil.isExternalStorageWritable()) {
            return C0380R.string.msg_external_storage_not_writable;
        }
        this.mTerminate = false;
        this.mFileName = getBackupFilePath(this.mContext, this.mLogManagement, CommonLog.getDateFromLogTime(adventureLog.logTime));
        File file = new File(this.mFileName);
        this.mOut = null;
        try {
            File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }
            this.mOut = new PrintWriter(new GZIPOutputStream(new FileOutputStream(file)));
            writeLogsToFile();
            PrintWriter printWriter = this.mOut;
            if (printWriter != null) {
                printWriter.close();
            }
            if (this.mTerminate && file.exists()) {
                file.delete();
            }
            return 0;
        } catch (IOException unused) {
            PrintWriter printWriter2 = this.mOut;
            if (printWriter2 != null) {
                printWriter2.close();
            }
            return C0380R.string.msg_logview_save_file_io_error;
        } catch (Throwable th) {
            PrintWriter printWriter3 = this.mOut;
            if (printWriter3 != null) {
                printWriter3.close();
            }
            throw th;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x024f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void writeLogsToFile() throws java.lang.Throwable {
        /*
            Method dump skipped, instructions count: 601
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.p001db.LogPersister.writeLogsToFile():void");
    }

    private void exportPlayerChars() throws IOException {
        writeData(new String[]{"_id", "presetBitmapId", "bitmap"});
        String[] strArr = new String[3];
        for (int i = 0; i < this.mPlayerChars.size(); i++) {
            PlayerChar playerCharValueAt = this.mPlayerChars.valueAt(i);
            if (playerCharValueAt == null) {
                strArr[0] = Integer.toString(0);
                strArr[1] = null;
                strArr[2] = null;
            } else {
                strArr[0] = Integer.toString(playerCharValueAt.f106id);
                strArr[1] = Integer.toString(playerCharValueAt.presetBitmapId);
                if (playerCharValueAt.presetBitmapId == -1) {
                    Bitmap bitmap = playerCharValueAt.getBitmap(this.mContext);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    strEncodeBytes = bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream) ? Base64.encodeBytes(byteArrayOutputStream.toByteArray()) : null;
                    try {
                        byteArrayOutputStream.close();
                    } catch (IOException unused) {
                    }
                }
                strArr[2] = strEncodeBytes;
            }
            writeData(strArr);
        }
        writeDelimiter();
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    private void exportDb(String str, String[] strArr, Cursor cursor) {
        this.mOut.println(str);
        writeData(strArr);
        if (this.mTerminate) {
            throw new TerminatedException();
        }
        String[] strArr2 = new String[strArr.length];
        while (cursor.moveToNext()) {
            for (int i = 0; i < strArr.length; i++) {
                strArr2[i] = cursor.getString(i);
            }
            writeData(strArr2);
            if (this.mTerminate) {
                throw new TerminatedException();
            }
        }
        writeDelimiter();
        cursor.close();
    }

    private String join(String[] strArr) {
        return join(null, strArr);
    }

    private String join(String str, String[] strArr) {
        StringBuilder sb = new StringBuilder();
        for (String str2 : strArr) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            if (str != null) {
                sb.append(str);
            }
            sb.append(str2);
        }
        return sb.toString();
    }

    private void writeData(String[] strArr) {
        for (int i = 0; i < strArr.length; i++) {
            if (i > 0) {
                this.mOut.print('\t');
            }
            String str = strArr[i];
            if (str != null) {
                for (int i2 = 0; i2 < str.length(); i2++) {
                    char cCharAt = str.charAt(i2);
                    if (cCharAt == '\t') {
                        this.mOut.print("\\t");
                    } else if (cCharAt == '\n') {
                        this.mOut.print("\\n");
                    } else {
                        this.mOut.print(cCharAt);
                    }
                }
            }
        }
        this.mOut.println();
    }

    private void writeDelimiter() {
        this.mOut.println(DELIMITER);
    }

    public String getBackupFilePath(Context context, LogManagement logManagement, Date date) {
        Dungeon dungeon = DungeonRepository.getDungeon(context, logManagement.dungeonId);
        int i = logManagement.targetFloor;
        return new File(DeviceUtil.getDataDirectory(context), context.getString(C0380R.string.res_log_export_file, dungeon.name, Integer.valueOf(i), DateFormat.format(context.getString(C0380R.string.res_log_export_file_timestamp_format), date)) + context.getString(C0380R.string.res_log_export_file_ext)).getAbsolutePath();
    }

    public LogManagement getLogManagement() {
        return this.mLogManagement;
    }

    public List<AdventureLog> getAdventureLogs() {
        return this.mAdventureLogs;
    }

    public SparseArray<PlayerChar> getPlayerChars() {
        return this.mPlayerChars;
    }

    public void setmPlayerChars(SparseArray<PlayerChar> sparseArray) {
        this.mPlayerChars = sparseArray;
    }

    public String getFileName() {
        return this.mFileName;
    }

    public void terminate() {
        this.mTerminate = true;
    }
}
