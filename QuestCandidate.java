package com.shirobakama.autorpg2.adventure;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class QuestCandidate implements Parcelable {
    public static final Parcelable.Creator<QuestCandidate> CREATOR = new Parcelable.Creator<QuestCandidate>() { // from class: com.shirobakama.autorpg2.adventure.QuestCandidate.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public QuestCandidate[] newArray(int i) {
            return new QuestCandidate[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public QuestCandidate createFromParcel(Parcel parcel) {
            return new QuestCandidate(parcel);
        }
    };
    public String questSymbol;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public QuestCandidate() {
    }

    public QuestCandidate(Parcel parcel) {
        this.questSymbol = parcel.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.questSymbol);
    }
}
