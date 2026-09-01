package com.shirobakama.autorpg2.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.preference.DialogPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.shirobakama.logquest2.C0380R;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class DirectorySelectDialogPreference extends DialogPreference {
    private static final FileFilter DIR_FILTER = new FileFilter() { // from class: com.shirobakama.autorpg2.util.DirectorySelectDialogPreference.1
        @Override // java.io.FileFilter
        public boolean accept(File file) {
            return file.isDirectory();
        }
    };
    private FileArrayAdapter mAdapter;
    private List<File> mFiles;
    protected LinkedList<File> mHistory;
    private ListView mLvDirectories;
    private TextView mTvCurrentDirectory;

    public DirectorySelectDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setPositiveButtonText(C0380R.string.lbl_output_directory_here);
    }

    public DirectorySelectDialogPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setPositiveButtonText(C0380R.string.lbl_output_directory_here);
    }

    @Override // android.preference.DialogPreference
    @SuppressLint({"InflateParams"})
    protected View onCreateDialogView() {
        File defaultDataDirectory = null;
        View viewInflate = LayoutInflater.from(getContext()).inflate(C0380R.layout.directory_select_dialog, (ViewGroup) null);
        this.mTvCurrentDirectory = (TextView) viewInflate.findViewById(C0380R.id.tvCurrentDirectory);
        this.mLvDirectories = (ListView) viewInflate.findViewById(C0380R.id.lvDirectories);
        this.mFiles = new ArrayList();
        this.mAdapter = new FileArrayAdapter(getContext(), C0380R.layout.directory_dialog_item, this.mFiles);
        this.mHistory = new LinkedList<>();
        this.mLvDirectories.setAdapter((ListAdapter) this.mAdapter);
        this.mLvDirectories.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.shirobakama.autorpg2.util.DirectorySelectDialogPreference.2
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                File parentFile = (File) adapterView.getItemAtPosition(i);
                File last = DirectorySelectDialogPreference.this.mHistory.getLast();
                if (last.getParentFile() != null && i == 0) {
                    parentFile = last.getParentFile();
                } else if (!parentFile.isDirectory()) {
                    return;
                }
                DirectorySelectDialogPreference.this.mHistory.add(parentFile);
                DirectorySelectDialogPreference.this.refresh(true);
            }
        });
        this.mLvDirectories.setOnKeyListener(new View.OnKeyListener() { // from class: com.shirobakama.autorpg2.util.DirectorySelectDialogPreference.3
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (1 != keyEvent.getAction() || 4 != i) {
                    return false;
                }
                DirectorySelectDialogPreference.this.mHistory.removeLast();
                if (DirectorySelectDialogPreference.this.mHistory.isEmpty()) {
                    return false;
                }
                DirectorySelectDialogPreference.this.refresh(true);
                return true;
            }
        });
        String persistedString = getPersistedString(null);
        if (!TextUtils.isEmpty(persistedString)) {
            File file = new File(persistedString);
            if (file.exists()) {
                defaultDataDirectory = !file.isDirectory() ? file.getParentFile() : file;
            }
        }
        if (defaultDataDirectory == null) {
            defaultDataDirectory = DeviceUtil.getDefaultDataDirectory(getContext());
        }
        this.mHistory.clear();
        this.mHistory.add(defaultDataDirectory);
        refresh(false);
        return viewInflate;
    }

    @Override // android.preference.DialogPreference
    protected void onDialogClosed(boolean z) {
        if (z && !this.mHistory.isEmpty()) {
            persistString(this.mHistory.getLast().getAbsolutePath());
            setSummary(getSummary());
        }
        super.onDialogClosed(z);
    }

    protected void refresh(boolean z) {
        File last = this.mHistory.getLast();
        this.mTvCurrentDirectory.setText(getContext().getString(C0380R.string.lbl_output_directory_current, last.getAbsolutePath()));
        this.mAdapter.clear();
        if (last.getParentFile() != null) {
            this.mAdapter.add(new File(last, ".."));
        }
        File[] fileArrListFiles = last.listFiles(DIR_FILTER);
        if (fileArrListFiles != null) {
            Arrays.sort(fileArrListFiles, FileArrayAdapter.FILE_NAME_COMPARATOR);
            for (File file : fileArrListFiles) {
                this.mAdapter.add(file);
            }
        }
        if (z) {
            this.mAdapter.notifyDataSetChanged();
        }
    }

    @Override // android.preference.Preference
    public CharSequence getSummary() {
        String persistedString = getPersistedString(null);
        String absolutePath = DeviceUtil.getDefaultDataDirectory(getContext()).getAbsolutePath();
        if (TextUtils.isEmpty(persistedString)) {
            persistedString = absolutePath;
        }
        if (persistedString.equals(absolutePath)) {
            persistedString = getContext().getString(C0380R.string.res_output_directory_default, persistedString);
        }
        return getContext().getString(C0380R.string.pref_smry_output_directory, persistedString);
    }
}
