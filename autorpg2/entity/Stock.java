package com.shirobakama.autorpg2.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class Stock extends ItemObject implements Parcelable {
    public static final Parcelable.Creator<Stock> CREATOR = new Parcelable.Creator<Stock>() { // from class: com.shirobakama.autorpg2.entity.Stock.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Stock[] newArray(int i) {
            return new Stock[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Stock createFromParcel(Parcel parcel) {
            return new Stock(parcel);
        }
    };
    public int countNum;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private static class TypeNameComparator implements Comparator<Stock> {
        private Context context;

        public TypeNameComparator(Context context) {
            this.context = context;
        }

        @Override // java.util.Comparator
        public int compare(Stock stock, Stock stock2) {
            Item baseItem = stock.getBaseItem(this.context);
            Item baseItem2 = stock2.getBaseItem(this.context);
            int iOrdinal = baseItem.type.ordinal() - baseItem2.type.ordinal();
            if (iOrdinal != 0) {
                return iOrdinal;
            }
            String str = stock.name;
            String str2 = stock2.name;
            if (str == null) {
                str = baseItem.name;
            }
            if (str2 == null) {
                str2 = baseItem2.name;
            }
            return str.compareTo(str2);
        }
    }

    private static class TypeIdComparator implements Comparator<Stock> {
        private Context context;

        public TypeIdComparator(Context context) {
            this.context = context;
        }

        @Override // java.util.Comparator
        public int compare(Stock stock, Stock stock2) {
            Item baseItem = stock.getBaseItem(this.context);
            Item baseItem2 = stock2.getBaseItem(this.context);
            int iOrdinal = baseItem.type.ordinal() - baseItem2.type.ordinal();
            if (iOrdinal != 0) {
                return iOrdinal;
            }
            int i = baseItem.f97id - baseItem2.f97id;
            return i == 0 ? stock.f98id - stock2.f98id : i;
        }
    }

    public Stock() {
        this.countNum = 0;
    }

    public Stock(Parcel parcel) {
        super(parcel);
        this.countNum = 0;
        this.countNum = parcel.readInt();
    }

    public Stock(ItemObject itemObject) {
        super(itemObject);
        this.countNum = 0;
        this.countNum = 0;
    }

    @Override // com.shirobakama.autorpg2.entity.ItemObject, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.countNum);
    }

    public static List<Stock> sortByTypeAndName(Context context, List<Stock> list) {
        Collections.sort(list, new TypeNameComparator(context));
        return list;
    }

    public static List<Stock> sortByTypeAndId(Context context, List<Stock> list) {
        Collections.sort(list, new TypeIdComparator(context));
        return list;
    }
}
