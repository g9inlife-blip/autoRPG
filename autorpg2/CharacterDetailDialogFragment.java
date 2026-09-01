package com.shirobakama.autorpg2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.view.DetailedCharViewHolder;
import com.shirobakama.logquest2.C0380R;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class CharacterDetailDialogFragment extends AlertDialogFragment {

    public interface CharacterDetailCallback {
        LayoutInflater getCurrentLayoutInflater();

        GameContext getGameContext();

        List<PlayerChar> getPlayerChars();
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    protected void onClick(DialogInterface dialogInterface, int i) {
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    @SuppressLint({"InflateParams"})
    protected View getAlertDialogView() {
        CharacterDetailCallback characterDetailCallback = (CharacterDetailCallback) getActivity();
        View viewInflate = characterDetailCallback.getCurrentLayoutInflater().inflate(C0380R.layout.detailed_status_dialog, (ViewGroup) null);
        int i = getArguments().getInt("char_index");
        PlayerChar playerChar = characterDetailCallback.getPlayerChars().get(i);
        playerChar.calcStatus((Context) getActivity(), characterDetailCallback.getGameContext());
        final DetailedCharViewHolder detailedCharViewHolder = new DetailedCharViewHolder(viewInflate, i);
        detailedCharViewHolder.show(getActivity(), characterDetailCallback.getGameContext(), playerChar);
        detailedCharViewHolder.llayDetail.setVisibility(8);
        detailedCharViewHolder.btnDetailedStatus.setOnClickListener(new View.OnClickListener() { // from class: com.shirobakama.autorpg2.CharacterDetailDialogFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                detailedCharViewHolder.llayDetail.setVisibility(detailedCharViewHolder.llayDetail.getVisibility() == 8 ? 0 : 8);
            }
        });
        return viewInflate;
    }
}
