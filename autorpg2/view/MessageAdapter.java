package com.shirobakama.autorpg2.view;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.shirobakama.logquest2.C0380R;
import java.util.List;
import java.util.regex.Pattern;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class MessageAdapter extends ArrayAdapter<String> {
    protected static final String TAG = "msg-adpt";
    private static String colorSpeech;
    private static String colorWeak;
    private LayoutInflater mInflater;
    private static final Pattern PATTERN_STRONG = Pattern.compile("\\*\\*([^\\*]+)\\*\\*");
    private static final Pattern PATTERN_WEAK = Pattern.compile("##([^#]+)##");
    private static final Pattern PATTERN_SPEECH = Pattern.compile("\\$\\$([^\\$]+)\\$\\$");
    private static final Pattern PATTERN_HEADER = Pattern.compile("__([^_]+)__");
    private static final Pattern PATTERN_NEW_LINE = Pattern.compile("\\n");
    private static String colorStrong = null;

    public MessageAdapter(Context context, List<String> list) {
        super(context, 0, list);
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.mInflater.inflate(C0380R.layout.list_item_message, viewGroup, false);
        }
        TextView textView = (TextView) view;
        String item = getItem(i);
        if (item.startsWith("!")) {
            textView.setText(Html.fromHtml(decorateMessage(getContext(), item)));
        } else {
            textView.setText(item);
        }
        return view;
    }

    public static String decorateMessage(Context context, String str) {
        if (colorStrong == null) {
            colorStrong = Integer.toHexString(context.getResources().getColor(C0380R.color.message_strong)).substring(2);
            colorWeak = Integer.toHexString(context.getResources().getColor(C0380R.color.message_weak)).substring(2);
            colorSpeech = Integer.toHexString(context.getResources().getColor(C0380R.color.message_speech)).substring(2);
        }
        return PATTERN_NEW_LINE.matcher(PATTERN_HEADER.matcher(PATTERN_SPEECH.matcher(PATTERN_WEAK.matcher(PATTERN_STRONG.matcher(str.substring(1)).replaceAll("<font color=\"#" + colorStrong + "\">$1</font>")).replaceAll("<font color=\"#" + colorWeak + "\">$1</font>")).replaceAll("<font color=\"#" + colorSpeech + "\">$1</font>")).replaceAll("<h4>$1</h4>")).replaceAll("<br>");
    }
}
