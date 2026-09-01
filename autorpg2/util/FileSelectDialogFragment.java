package com.shirobakama.autorpg2.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.shirobakama.autorpg2.AlertDialogFragment;
import com.shirobakama.autorpg2.TownActivity;
import com.shirobakama.logquest2.C0380R;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class FileSelectDialogFragment extends AlertDialogFragment {
    private FileArrayAdapter mAdapter;
    private FileFilter mDirOrFileNameFilter;
    protected List<String> mDisplayFileExts;
    private List<File> mFiles;
    protected LinkedList<File> mHistory;
    private ListView mLvFiles;

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    protected void onClick(DialogInterface dialogInterface, int i) {
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment, android.support.v4.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String[] stringArray = getArguments().getStringArray("file_extentions");
        if (stringArray == null) {
            this.mDisplayFileExts = Collections.emptyList();
        } else {
            this.mDisplayFileExts = Arrays.asList(stringArray);
        }
        this.mDirOrFileNameFilter = new FileFilter() { // from class: com.shirobakama.autorpg2.util.FileSelectDialogFragment.1
            @Override // java.io.FileFilter
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                }
                int iLastIndexOf = file.getName().lastIndexOf(46);
                if (iLastIndexOf < 0) {
                    return false;
                }
                return FileSelectDialogFragment.this.mDisplayFileExts.contains(file.getName().substring(iLastIndexOf).toLowerCase(Locale.US));
            }
        };
        return super.onCreateDialog(bundle);
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    @SuppressLint({"InflateParams"})
    protected View getAlertDialogView() {
        View viewInflate = LayoutInflater.from(getActivity()).inflate(C0380R.layout.directory_select_dialog, (ViewGroup) null);
        viewInflate.findViewById(C0380R.id.tvCurrentDirectory).setVisibility(8);
        this.mLvFiles = (ListView) viewInflate.findViewById(C0380R.id.lvDirectories);
        this.mFiles = new ArrayList();
        this.mAdapter = new FileArrayAdapter(getActivity(), C0380R.layout.directory_dialog_item, this.mFiles);
        this.mHistory = new LinkedList<>();
        this.mLvFiles.setAdapter((ListAdapter) this.mAdapter);
        this.mLvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.shirobakama.autorpg2.util.FileSelectDialogFragment.2
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                File parentFile = (File) adapterView.getItemAtPosition(i);
                File last = FileSelectDialogFragment.this.mHistory.getLast();
                if (last.getParentFile() != null && i == 0) {
                    parentFile = last.getParentFile();
                } else if (!parentFile.isDirectory()) {
                    FileSelectDialogFragment.this.onFileSelect(parentFile);
                    return;
                }
                FileSelectDialogFragment.this.mHistory.add(parentFile);
                FileSelectDialogFragment.this.refresh(true);
            }
        });
        this.mLvFiles.setOnKeyListener(new View.OnKeyListener() { // from class: com.shirobakama.autorpg2.util.FileSelectDialogFragment.3
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (1 != keyEvent.getAction() || 4 != i) {
                    return false;
                }
                FileSelectDialogFragment.this.mHistory.removeLast();
                if (FileSelectDialogFragment.this.mHistory.isEmpty()) {
                    return false;
                }
                FileSelectDialogFragment.this.refresh(true);
                return true;
            }
        });
        File defaultDataDirectory = DeviceUtil.getDefaultDataDirectory(getActivity());
        this.mHistory.clear();
        this.mHistory.add(defaultDataDirectory);
        refresh(false);
        return viewInflate;
    }

    protected void refresh(boolean z) {
        File last = this.mHistory.getLast();
        this.mAdapter.clear();
        if (last.getParentFile() != null) {
            this.mAdapter.add(new File(last, ".."));
        }
        File[] fileArrListFiles = last.listFiles(this.mDirOrFileNameFilter);
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

    protected void onFileSelect(File file) {
        ((TownActivity) getActivity()).onLogFileSelect(file);
        dismiss();
    }
}
