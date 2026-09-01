package com.shirobakama.autorpg2.util;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class NTPMessage {
    private static final long MSB0_BASE_TIME = 2085978496000L;
    private static final long MSB1_BASE_TIME = -2208988800000L;
    private static final int SNTP_PACKET_SIZE = 68;
    private int keyIdentifier;

    /* renamed from: li */
    private int f113li;
    private long messageDigest1;
    private long messageDigest2;
    private int mode;
    private long originateTimestamp;
    private int pollInterval;
    private int precision;
    private long receiveTimestamp;
    private int referenceIdentifier;
    private long referenceTimestamp;
    private int rootDelay;
    private int rootDispersion;
    private int stratum;
    private long transmitTimestamp;

    /* renamed from: vn */
    private int f114vn;

    public static long ntpTsToJava(long j) {
        long j2 = (j >> 32) & 4294967295L;
        double d = j & 4294967295L;
        Double.isNaN(d);
        long j3 = (long) (((d * 1000.0d) / 4.294967296E9d) + 0.5d);
        return (2147483648L & j2) == 0 ? (j2 * 1000) + MSB0_BASE_TIME + j3 : (j2 * 1000) + MSB1_BASE_TIME + j3;
    }

    public static long javaTsToNtp(long j) {
        long j2 = MSB0_BASE_TIME;
        boolean z = j < MSB0_BASE_TIME;
        if (z) {
            j2 = MSB1_BASE_TIME;
        }
        long j3 = j - j2;
        long j4 = j3 / 1000;
        long j5 = ((j3 % 1000) * 4294967296L) / 1000;
        if (z) {
            j4 |= 2147483648L;
        }
        return j5 | (j4 << 32);
    }

    public NTPMessage() {
    }

    public NTPMessage(byte[] bArr) {
        int intData = getIntData(bArr, 0);
        this.f113li = (intData >> 30) & 3;
        this.f114vn = (intData >> 27) & 7;
        this.mode = (intData >> 24) & 7;
        this.stratum = (intData >> 16) & 255;
        this.pollInterval = (intData >> 8) & 255;
        this.precision = intData & 255;
        this.rootDelay = getIntData(bArr, 4);
        this.rootDispersion = getIntData(bArr, 8);
        this.referenceIdentifier = getIntData(bArr, 12);
        this.referenceTimestamp = getLongData(bArr, 16);
        this.originateTimestamp = getLongData(bArr, 24);
        this.receiveTimestamp = getLongData(bArr, 32);
        this.transmitTimestamp = getLongData(bArr, 40);
        this.keyIdentifier = getIntData(bArr, 48);
        this.messageDigest1 = getLongData(bArr, 52);
        this.messageDigest2 = getLongData(bArr, 60);
    }

    private long getLongData(byte[] bArr, int i) {
        return (getIntData(bArr, i + 4) & 4294967295L) | (getIntData(bArr, i) << 32);
    }

    private int getIntData(byte[] bArr, int i) {
        int i2 = i + 1;
        int i3 = i2 + 1;
        int i4 = (((bArr[i] & 255) << 8) | (bArr[i2] & 255)) << 8;
        int i5 = i3 + 1;
        return (bArr[i5] & 255) | ((i4 | (bArr[i3] & 255)) << 8);
    }

    public int getLi() {
        return this.f113li;
    }

    public void setLi(int i) {
        this.f113li = i;
    }

    public int getVn() {
        return this.f114vn;
    }

    public void setVn(int i) {
        this.f114vn = i;
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int i) {
        this.mode = i;
    }

    public int getStratum() {
        return this.stratum;
    }

    public void setStratum(int i) {
        this.stratum = i;
    }

    public int getPollInterval() {
        return this.pollInterval;
    }

    public void setPollInterval(int i) {
        this.pollInterval = i;
    }

    public int getPrecision() {
        return this.precision;
    }

    public void setPrecision(int i) {
        this.precision = i;
    }

    public int getRootDelay() {
        return this.rootDelay;
    }

    public void setRootDelay(int i) {
        this.rootDelay = i;
    }

    public int getRootDispersion() {
        return this.rootDispersion;
    }

    public void setRootDispersion(int i) {
        this.rootDispersion = i;
    }

    public int getReferenceIdentifier() {
        return this.referenceIdentifier;
    }

    public void setReferenceIdentifier(int i) {
        this.referenceIdentifier = i;
    }

    public long getReferenceTimestamp() {
        return this.referenceTimestamp;
    }

    public void setReferenceTimestamp(long j) {
        this.referenceTimestamp = j;
    }

    public long getOriginateTimestamp() {
        return this.originateTimestamp;
    }

    public void setOriginateTimestamp(long j) {
        this.originateTimestamp = j;
    }

    public long getReceiveTimestamp() {
        return this.receiveTimestamp;
    }

    public void setReceiveTimestamp(long j) {
        this.receiveTimestamp = j;
    }

    public long getTransmitTimestamp() {
        return this.transmitTimestamp;
    }

    public void setTransmitTimestamp(long j) {
        this.transmitTimestamp = j;
    }

    public int getKeyIdentifier() {
        return this.keyIdentifier;
    }

    public void setKeyIdentifier(int i) {
        this.keyIdentifier = i;
    }

    public long getMessageDigest1() {
        return this.messageDigest1;
    }

    public void setMessageDigest1(long j) {
        this.messageDigest1 = j;
    }

    public long getMessageDigest2() {
        return this.messageDigest2;
    }

    public void setMessageDigest2(long j) {
        this.messageDigest2 = j;
    }

    public static byte[] createBuffer() {
        return new byte[SNTP_PACKET_SIZE];
    }

    public byte[] getData() {
        byte[] bArr = new byte[SNTP_PACKET_SIZE];
        addData(bArr, addData(bArr, addData(bArr, addData(bArr, addData(bArr, addData(bArr, addData(bArr, addData(bArr, addData(bArr, addData(bArr, addData(bArr, 0, (this.f113li << 30) | (this.f114vn << 27) | (this.mode << 24) | (this.stratum << 16) | (this.pollInterval << 8) | this.precision), this.rootDelay), this.rootDispersion), this.referenceIdentifier), this.referenceTimestamp), this.originateTimestamp), this.receiveTimestamp), this.transmitTimestamp), this.keyIdentifier), this.messageDigest1), this.messageDigest2);
        return bArr;
    }

    private int addData(byte[] bArr, int i, long j) {
        return addData(bArr, addData(bArr, i, (int) ((j >> 32) & 4294967295L)), (int) (j & 4294967295L));
    }

    private int addData(byte[] bArr, int i, int i2) {
        int i3 = i + 1;
        bArr[i] = (byte) ((i2 >> 24) & 255);
        int i4 = i3 + 1;
        bArr[i3] = (byte) ((i2 >> 16) & 255);
        int i5 = i4 + 1;
        bArr[i4] = (byte) ((i2 >> 8) & 255);
        int i6 = i5 + 1;
        bArr[i5] = (byte) (i2 & 255);
        return i6;
    }
}
