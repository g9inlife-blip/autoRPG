package com.shirobakama.autorpg2.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.shirobakama.autorpg2.entity.CommonLog;
import com.shirobakama.autorpg2.entity.FightingLog;
import com.shirobakama.autorpg2.entity.LogEnemy;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.util.FormatUtil;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class LogAdapter<T extends CommonLog> extends ArrayAdapter<T> {
    protected static final String TAG = "logq-logadpt";
    private static int charPaddingPixels;
    private static int paddingPixels;
    private static String resExp;
    private static String resHp;
    private static String resLv;
    private static String resMp;
    private static String sentenceSeparator;
    private SparseArray<Bitmap> mCharIcons;
    private SparseArray<String> mCharNames;
    private LayoutInflater mInflater;
    private boolean mIsAdventure;
    private boolean mIsDetail;
    private int mItemLayoutId;

    public static class ViewHolder {
        public ImageView ivCharacterThumbnail;
        public ImageView ivIcon;
        public TextView tvDescription1;
        public TextView[] tvStatusX = new TextView[3];
        public TextView tvTime;
        public TextView tvTitle;
    }

    public LogAdapter(boolean z, Activity activity, List<T> list, List<PlayerChar> list2, boolean z2) {
        super(activity, 0, list);
        this.mCharNames = new SparseArray<>();
        this.mCharIcons = new SparseArray<>();
        this.mIsAdventure = z;
        this.mIsDetail = z2;
        this.mItemLayoutId = this.mIsAdventure ? C0380R.layout.log_view_list_item : C0380R.layout.log_view_list_item_fight;
        for (PlayerChar playerChar : list2) {
            this.mCharNames.put(playerChar.f106id, playerChar.name);
            this.mCharIcons.put(playerChar.f106id, FormatUtil.resizeBitmapWithDensity(playerChar.getBitmap(activity), 32));
        }
        if (!this.mIsAdventure) {
            for (int i = 1; i < list.size(); i++) {
                ((FightingLog) list.get(i)).previousLog = (FightingLog) list.get(i - 1);
            }
        }
        this.mInflater = (LayoutInflater) activity.getSystemService("layout_inflater");
        if (resHp == null) {
            sentenceSeparator = activity.getString(C0380R.string.res_sentence_separator);
            resHp = activity.getString(C0380R.string.lbl_logview_hp) + sentenceSeparator;
            resMp = activity.getString(C0380R.string.lbl_logview_mp) + sentenceSeparator;
            resExp = activity.getString(C0380R.string.lbl_logview_exp) + sentenceSeparator;
            resLv = activity.getString(C0380R.string.lbl_logview_level) + sentenceSeparator;
            paddingPixels = activity.getResources().getDimensionPixelSize(C0380R.dimen.log_icon_padding);
            charPaddingPixels = activity.getResources().getDimensionPixelSize(C0380R.dimen.log_char_image_padding);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x00df  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x01f0  */
    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public android.view.View getView(int r11, android.view.View r12, android.view.ViewGroup r13) {
        /*
            Method dump skipped, instructions count: 852
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.shirobakama.autorpg2.view.LogAdapter.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }

    private List<LogEnemy> findAliveEnemies(List<LogEnemy> list) {
        ArrayList arrayList = new ArrayList(list.size());
        for (LogEnemy logEnemy : list) {
            if (logEnemy.f99hp > 0) {
                arrayList.add(logEnemy);
            }
        }
        return arrayList;
    }

    private List<CommonLog.LogChar> findLogCharsForIds(List<CommonLog.LogChar> list, int[] iArr) {
        ArrayList arrayList = new ArrayList(iArr.length);
        for (int i : iArr) {
            Iterator<CommonLog.LogChar> it = list.iterator();
            while (true) {
                if (it.hasNext()) {
                    CommonLog.LogChar next = it.next();
                    if (next.charId == i) {
                        arrayList.add(next);
                        break;
                    }
                }
            }
        }
        return arrayList;
    }
}
