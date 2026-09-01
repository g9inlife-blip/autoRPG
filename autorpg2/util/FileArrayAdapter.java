package com.shirobakama.autorpg2.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.shirobakama.logquest2.C0380R;
import java.io.File;
import java.util.Comparator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
class FileArrayAdapter extends ArrayAdapter<File> {
    static final Comparator<File> FILE_NAME_COMPARATOR = new Comparator<File>() { // from class: com.shirobakama.autorpg2.util.FileArrayAdapter.1
        @Override // java.util.Comparator
        public int compare(File file, File file2) {
            return file.getName().compareTo(file2.getName());
        }
    };
    private int resourceId;

    public FileArrayAdapter(Context context, int i, List<File> list) {
        super(context, i, list);
        this.resourceId = i;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        File item = getItem(i);
        if (view == null) {
            view = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(this.resourceId, (ViewGroup) null);
        }
        TextView textView = (TextView) view.findViewById(C0380R.id.tvDirectoryName);
        if (item.getName().equalsIgnoreCase("..")) {
            textView.setText("../");
        } else {
            String name = item.getName();
            if (item.isDirectory()) {
                name = name + "/";
            }
            textView.setText(name);
        }
        return view;
    }
}
