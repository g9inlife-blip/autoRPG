package com.shirobakama.autorpg2;

import android.R;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.shirobakama.autorpg2.adventure.FlagEngine;
import com.shirobakama.autorpg2.entity.Dungeon;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.Town;
import com.shirobakama.autorpg2.repo.TownRepository;
import com.shirobakama.logquest2.C0380R;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class TownSelectionDialogFragment extends AlertDialogFragment implements CompoundButton.OnCheckedChangeListener {
    private static final int NUMBER_OF_TOWNS = 14;
    private static final String TAG = "town-select-dlg";
    private static final int[][] TOWN_VIEW_POSITIONS;
    private static final int VIEW_HEIGHT = 6;
    private static final int VIEW_WIDTH = 3;
    private SparseArray<FlagEngine.TownKnowledge> mKnownTowns;
    private List<RadioButton> mRadioButtons;
    private int mSelectedTownId = 0;
    private int[][] mTownIdsOnView;

    static {
        int[][] iArr = new int[14][];
        iArr[1] = new int[]{1, 0};
        iArr[2] = new int[]{1, 1};
        iArr[3] = new int[]{1, 2};
        iArr[11] = new int[]{2, 2};
        iArr[10] = new int[]{0, 2};
        iArr[6] = new int[]{0, 3};
        iArr[4] = new int[]{1, 3};
        iArr[5] = new int[]{2, 3};
        iArr[8] = new int[]{0, 4};
        iArr[7] = new int[]{1, 4};
        iArr[12] = new int[]{2, 4};
        iArr[9] = new int[]{0, 5};
        iArr[13] = new int[]{1, 5};
        TOWN_VIEW_POSITIONS = iArr;
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment, android.support.v4.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        TownActivity townActivity = (TownActivity) getActivity();
        GameContext gameContext = townActivity.game;
        this.mKnownTowns = new SparseArray<>();
        setKnownTowns(gameContext, this.mKnownTowns, 1);
        FlagEngine.setSecretTownsKnowledge(townActivity, gameContext, this.mKnownTowns);
        this.mKnownTowns.put(gameContext.townId, FlagEngine.TownKnowledge.CURRENT);
        Iterator<Town> it = FlagEngine.getAvailableTowns(townActivity, gameContext).iterator();
        while (it.hasNext()) {
            this.mKnownTowns.put(it.next().f111id, FlagEngine.TownKnowledge.CAN_MOVE);
        }
        this.mTownIdsOnView = (int[][]) Array.newInstance((Class<?>) int.class, 3, 6);
        assignTown(this.mTownIdsOnView, this.mKnownTowns, 1);
        return super.onCreateDialog(bundle);
    }

    private void setKnownTowns(GameContext gameContext, SparseArray<FlagEngine.TownKnowledge> sparseArray, int i) {
        if (sparseArray.get(i) != null) {
            return;
        }
        sparseArray.put(i, FlagEngine.TownKnowledge.KNOWN);
        Iterator<Town> it = FlagEngine.getAvailableTowns(getActivity(), gameContext, i).iterator();
        while (it.hasNext()) {
            setKnownTowns(gameContext, sparseArray, it.next().f111id);
        }
    }

    private void assignTown(int[][] iArr, SparseArray<FlagEngine.TownKnowledge> sparseArray, int i) {
        FlagEngine.TownKnowledge townKnowledge = sparseArray.get(i);
        if (townKnowledge == FlagEngine.TownKnowledge.SECRET) {
            return;
        }
        int[][] iArr2 = TOWN_VIEW_POSITIONS;
        int i2 = iArr2[i][0];
        int i3 = iArr2[i][1];
        if (iArr[i2][i3] != 0) {
            return;
        }
        iArr[i2][i3] = i;
        if (townKnowledge == null || townKnowledge == FlagEngine.TownKnowledge.HEARD_BUT_CANT_GO) {
            return;
        }
        for (int i4 : FlagEngine.getRelatedTownIds(i)) {
            assignTown(iArr, sparseArray, i4);
        }
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    @SuppressLint({"InflateParams"})
    protected View getAlertDialogView() {
        View view;
        GameContext gameContext;
        LinearLayout linearLayout;
        View view2;
        View view3;
        TextView textView;
        String str;
        FragmentActivity activity = getActivity();
        GameContext gameContext2 = ((TownActivity) activity).game;
        this.mRadioButtons = new ArrayList();
        LinearLayout linearLayout2 = new LinearLayout(activity);
        linearLayout2.setBackgroundColor(getResources().getColor(R.color.background_light));
        linearLayout2.setGravity(17);
        ScrollView scrollView = new ScrollView(activity);
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(activity);
        TableLayout tableLayout = new TableLayout(activity);
        tableLayout.setPadding(4, 4, 4, 4);
        horizontalScrollView.addView(tableLayout);
        scrollView.addView(horizontalScrollView);
        linearLayout2.addView(scrollView);
        int i = 0;
        LinearLayout linearLayout3 = linearLayout2;
        while (i < 6) {
            TableRow tableRow = new TableRow(activity);
            int[] iArr = new int[3];
            LinearLayout[] linearLayoutArr = new LinearLayout[3];
            int i2 = 0;
            boolean z = false;
            int i3 = 0;
            LinearLayout linearLayout4 = linearLayout3;
            for (int i4 = 3; i2 < i4; i4 = 3) {
                int i5 = this.mTownIdsOnView[i2][i];
                if (i5 != 0) {
                    FlagEngine.TownKnowledge townKnowledge = this.mKnownTowns.get(i5);
                    LinearLayout linearLayout5 = new LinearLayout(activity);
                    linearLayout5.setLayoutParams(new TableRow.LayoutParams(-2, -2));
                    linearLayout5.setOrientation(1);
                    if (townKnowledge == FlagEngine.TownKnowledge.CURRENT) {
                        linearLayout5.setBackgroundResource(C0380R.drawable.town_map_town_highlighted);
                    } else {
                        linearLayout5.setBackgroundResource(C0380R.drawable.town_map_town);
                    }
                    if (townKnowledge == FlagEngine.TownKnowledge.CAN_MOVE) {
                        RadioButton radioButton = new RadioButton(activity);
                        radioButton.setTag(Integer.valueOf(i5));
                        radioButton.setOnCheckedChangeListener(this);
                        this.mRadioButtons.add(radioButton);
                        textView = radioButton;
                    } else {
                        textView = new TextView(activity);
                    }
                    textView.setTextAppearance(activity, R.style.TextAppearance.Medium);
                    linearLayout5.addView(textView);
                    if (townKnowledge == null || townKnowledge == FlagEngine.TownKnowledge.HEARD_BUT_CANT_GO || townKnowledge == FlagEngine.TownKnowledge.SECRET) {
                        gameContext = gameContext2;
                        linearLayout = linearLayout4;
                        str = "?      ";
                    } else {
                        String str2 = TownRepository.getTown(activity, i5).name;
                        List<Dungeon> availableDungeons = FlagEngine.getAvailableDungeons(activity, gameContext2, i5);
                        LinearLayout linearLayout6 = linearLayout4;
                        for (Dungeon dungeon : availableDungeons) {
                            GameContext gameContext3 = gameContext2;
                            String str3 = str2;
                            TextView textView2 = new TextView(activity);
                            textView2.setTextAppearance(activity, R.style.TextAppearance.Small);
                            textView2.setText(dungeon.name);
                            linearLayout5.addView(textView2);
                            str2 = str3;
                            gameContext2 = gameContext3;
                            linearLayout6 = linearLayout6;
                        }
                        gameContext = gameContext2;
                        linearLayout = linearLayout6;
                        String str4 = str2;
                        iArr[i2] = (availableDungeons.size() * 10) + (townKnowledge == FlagEngine.TownKnowledge.CAN_MOVE ? 1 : 0);
                        if (i3 < iArr[i2]) {
                            i3 = iArr[i2];
                            str = str4;
                        } else {
                            str = str4;
                        }
                    }
                    textView.setText(str);
                    linearLayoutArr[i2] = linearLayout5;
                    z = true;
                    view2 = linearLayout5;
                } else {
                    gameContext = gameContext2;
                    linearLayout = linearLayout4;
                    view2 = null;
                }
                if (view2 == null) {
                    view2 = new View(activity);
                }
                tableRow.addView(view2);
                if (i2 < 2) {
                    int[][] iArr2 = this.mTownIdsOnView;
                    if (FlagEngine.existTownRelation(iArr2[i2][i], iArr2[i2 + 1][i])) {
                        TextView textView3 = new TextView(activity);
                        textView3.setLayoutParams(new TableRow.LayoutParams(-2, -1));
                        textView3.setGravity(17);
                        textView3.setPadding(0, 0, 0, 0);
                        textView3.setText("―");
                        view3 = textView3;
                    } else {
                        view3 = new View(activity);
                    }
                    tableRow.addView(view3);
                }
                i2++;
                gameContext2 = gameContext;
                linearLayout4 = linearLayout;
            }
            GameContext gameContext4 = gameContext2;
            LinearLayout linearLayout7 = linearLayout4;
            if (!z) {
                return linearLayout7;
            }
            for (int i6 = 0; i6 < 3; i6++) {
                LinearLayout linearLayout8 = linearLayoutArr[i6];
                if (linearLayout8 != null && iArr[i6] < i3) {
                    linearLayout8.getLayoutParams().height = -1;
                }
            }
            tableLayout.addView(tableRow);
            if (i < 5) {
                TableRow tableRow2 = new TableRow(activity);
                for (int i7 = 0; i7 < 3; i7++) {
                    int[][] iArr3 = this.mTownIdsOnView;
                    if (FlagEngine.existTownRelation(iArr3[i7][i], iArr3[i7][i + 1])) {
                        TextView textView4 = new TextView(activity);
                        textView4.setLayoutParams(new TableRow.LayoutParams(-1, -2));
                        textView4.setGravity(17);
                        textView4.setPadding(0, 0, 0, 0);
                        textView4.setText("|");
                        view = textView4;
                    } else {
                        view = new View(activity);
                    }
                    tableRow2.addView(view);
                    if (i7 < 2) {
                        tableRow2.addView(new View(activity));
                    }
                }
                tableLayout.addView(tableRow2);
            }
            i++;
            gameContext2 = gameContext4;
            linearLayout3 = linearLayout7;
        }
        return linearLayout3;
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    protected void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1 && this.mSelectedTownId != 0) {
            ((TownActivity) getActivity()).edgeOfTown.selectTown(this.mSelectedTownId);
        }
        if (i == -2) {
            ((TownActivity) getActivity()).edgeOfTown.cancelSelectTown();
        }
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        Integer num;
        if (z && (num = (Integer) compoundButton.getTag()) != null) {
            this.mSelectedTownId = num.intValue();
            for (RadioButton radioButton : this.mRadioButtons) {
                if (radioButton != compoundButton) {
                    radioButton.setChecked(false);
                }
            }
        }
    }
}
