package com.shirobakama.autorpg2.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class LabelValueItem implements Parcelable {
    public static final Parcelable.Creator<LabelValueItem> CREATOR = new Parcelable.Creator<LabelValueItem>() { // from class: com.shirobakama.autorpg2.view.LabelValueItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LabelValueItem[] newArray(int i) {
            return new LabelValueItem[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LabelValueItem createFromParcel(Parcel parcel) {
            return new LabelValueItem(parcel);
        }
    };
    public CharSequence label;
    public int value;

    public interface ItemCreator<T> {
        LabelValueItem create(T t);
    }

    public static List<LabelValueItem> castParcelableListToLabelValueItemList(ArrayList<Parcelable> arrayList) {
        return arrayList;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public LabelValueItem() {
    }

    public LabelValueItem(int i, CharSequence charSequence) {
        this.value = i;
        this.label = charSequence;
    }

    public LabelValueItem(Parcel parcel) {
        this.value = parcel.readInt();
        this.label = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.value);
        TextUtils.writeToParcel(this.label, parcel, i);
    }

    public String toString() {
        return this.label.toString();
    }

    public static <T> ArrayList<LabelValueItem> createList(List<T> list, ItemCreator<T> itemCreator) {
        ArrayList<LabelValueItem> arrayList = new ArrayList<>(list.size());
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(itemCreator.create(it.next()));
        }
        return arrayList;
    }

    public static CharSequence[] toLabelCharSequences(List<LabelValueItem> list) {
        CharSequence[] charSequenceArr = new CharSequence[list.size()];
        for (int i = 0; i < list.size(); i++) {
            charSequenceArr[i] = list.get(i).label;
        }
        return charSequenceArr;
    }
}
