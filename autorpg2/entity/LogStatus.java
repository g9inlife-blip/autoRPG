package com.shirobakama.autorpg2.entity;

import java.util.Calendar;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class LogStatus {
    public LogAction action;
    public int block;
    public int captiveRate;
    public int floor;

    /* renamed from: id */
    public int f104id;
    public int logTime;

    public enum LogAction {
        EXPLORING,
        MOVING,
        RETURNING,
        FIGHTING
    }

    public LogStatus() {
    }

    public LogStatus(Calendar calendar) {
        this.logTime = CommonLog.calcLogTime(calendar.getTimeInMillis());
    }
}
