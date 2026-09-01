package com.shirobakama.autorpg2.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Date;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class SntpClient {
    private static final int SNTP_PORT = 123;
    protected static final String TAG = "sntp-client";
    private static final int TIMEOUT_MS = 4000;
    private String mHostName;

    public SntpClient(String str) {
        this.mHostName = str;
    }

    public Date execute() throws Throwable {
        DatagramSocket datagramSocket;
        NTPMessage nTPMessage = new NTPMessage();
        nTPMessage.setMode(3);
        nTPMessage.setVn(3);
        byte[] data = nTPMessage.getData();
        byte[] bArrCreateBuffer = NTPMessage.createBuffer();
        try {
            datagramSocket = new DatagramSocket();
        } catch (IOException unused) {
            datagramSocket = null;
        } catch (Throwable th) {
            th = th;
            datagramSocket = null;
        }
        try {
            datagramSocket.setSoTimeout(TIMEOUT_MS);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(this.mHostName, SNTP_PORT);
            if (!inetSocketAddress.isUnresolved()) {
                DatagramPacket datagramPacket = new DatagramPacket(data, data.length, inetSocketAddress);
                DatagramPacket datagramPacket2 = new DatagramPacket(bArrCreateBuffer, bArrCreateBuffer.length);
                datagramSocket.send(datagramPacket);
                datagramSocket.receive(datagramPacket2);
                datagramSocket.close();
                NTPMessage nTPMessage2 = new NTPMessage(bArrCreateBuffer);
                if (nTPMessage2.getLi() == 3) {
                    return null;
                }
                return new Date(NTPMessage.ntpTsToJava(nTPMessage2.getReceiveTimestamp()));
            }
            datagramSocket.close();
            return null;
        } catch (IOException unused2) {
            if (datagramSocket != null) {
                datagramSocket.close();
            }
            return null;
        } catch (Throwable th2) {
            th = th2;
            if (datagramSocket != null) {
                datagramSocket.close();
            }
            throw th;
        }
    }
}
