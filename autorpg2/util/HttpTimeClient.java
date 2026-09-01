package com.shirobakama.autorpg2.util;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class HttpTimeClient {
    private static final Pattern EXTRACT_PATTERN = Pattern.compile("<BODY>\\s+(\\d+(\\.\\d+)?)\\s+</BODY>", 10);
    private static final String TAG = "http-time-client";
    private static final int TIMEOUT_MS = 7000;
    private final String mUrl;

    public HttpTimeClient(String str) {
        this.mUrl = str;
    }

    public Date execute() throws Throwable {
        HttpURLConnection httpURLConnection;
        Throwable th;
        try {
            httpURLConnection = (HttpURLConnection) new URL(this.mUrl).openConnection();
        } catch (IOException unused) {
            httpURLConnection = null;
        } catch (NumberFormatException unused2) {
            httpURLConnection = null;
        } catch (Throwable th2) {
            httpURLConnection = null;
            th = th2;
        }
        try {
            httpURLConnection.setConnectTimeout(TIMEOUT_MS);
            httpURLConnection.connect();
            String strStreamToString = streamToString(httpURLConnection.getInputStream());
            if (strStreamToString == null) {
                Log.e(TAG, "No response.");
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                return null;
            }
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode != 200) {
                Log.e(TAG, "Status:" + responseCode);
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                return null;
            }
            Matcher matcher = EXTRACT_PATTERN.matcher(strStreamToString);
            if (!matcher.find()) {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                return null;
            }
            Date date = new Date((long) (Double.valueOf(matcher.group(1)).doubleValue() * 1000.0d));
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            return date;
        } catch (IOException unused3) {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            return null;
        } catch (NumberFormatException unused4) {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            return null;
        } catch (Throwable th3) {
            th = th3;
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            throw th;
        }
    }

    private String streamToString(InputStream inputStream) throws IOException {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            while (true) {
                int i = bufferedReader.read();
                if (i != -1) {
                    sb.append((char) i);
                } else {
                    return sb.toString();
                }
            }
        } catch (UnsupportedEncodingException unused) {
            return null;
        }
    }
}
