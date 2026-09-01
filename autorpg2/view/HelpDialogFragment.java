package com.shirobakama.autorpg2.view;

import android.R;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import com.shirobakama.autorpg2.AlertDialogFragment;
import com.shirobakama.logquest2.C0380R;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class HelpDialogFragment extends AlertDialogFragment {
    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    protected void onClick(DialogInterface dialogInterface, int i) {
    }

    public static void show(FragmentActivity fragmentActivity, String str) {
        AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
        decorator.setCancelable(true).setPositiveText(0).setTitle(C0380R.string.msg_dlg_title_help);
        decorator.args().putString("help_message", str);
        decorator.decorate(new HelpDialogFragment()).show(fragmentActivity.getSupportFragmentManager());
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    protected View getAlertDialogView() {
        FragmentActivity activity = getActivity();
        String string = getArguments().getString("help_message");
        ScrollView scrollView = new ScrollView(activity);
        TextView textView = new TextView(activity);
        textView.setSingleLine(false);
        textView.setPadding(4, 4, 4, 4);
        textView.setTextSize(2, 14.0f);
        textView.setText(Html.fromHtml(MessageAdapter.decorateMessage(activity, string)));
        textView.setBackgroundColor(activity.getResources().getColor(R.color.background_light));
        scrollView.addView(textView);
        return scrollView;
    }
}
