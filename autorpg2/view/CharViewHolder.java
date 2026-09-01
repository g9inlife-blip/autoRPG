package com.shirobakama.autorpg2.view;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.Inventory;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.logquest2.C0380R;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class CharViewHolder {
    public CheckBox chkSelected;
    public int index;
    public ImageView ivArmor;
    public ImageView ivCharacater;
    public ImageView ivRing;
    public ImageView ivShield;
    public ImageView ivWeapon;
    public LinearLayout llayEquipment;
    public TextView tvArmor;
    public TextView tvClass;
    public TextView tvExp;
    public TextView tvHp;
    public TextView tvLevel;
    public TextView tvMp;
    public TextView tvName;
    public TextView tvRace;
    public TextView tvRing;
    public TextView tvShield;
    public TextView tvWeapon;
    public View vwContainer;

    public CharViewHolder(View view, int i) {
        this.vwContainer = view;
        this.ivCharacater = (ImageView) view.findViewById(C0380R.id.ivCharacter);
        this.tvName = (TextView) view.findViewById(C0380R.id.tvName);
        this.tvRace = (TextView) view.findViewById(C0380R.id.tvRace);
        this.tvClass = (TextView) view.findViewById(C0380R.id.tvClass);
        this.tvLevel = (TextView) view.findViewById(C0380R.id.tvLevel);
        this.tvExp = (TextView) view.findViewById(C0380R.id.tvExp);
        this.tvHp = (TextView) view.findViewById(C0380R.id.tvHp);
        this.tvMp = (TextView) view.findViewById(C0380R.id.tvMp);
        this.llayEquipment = (LinearLayout) view.findViewById(C0380R.id.llayEquipment);
        this.ivWeapon = (ImageView) view.findViewById(C0380R.id.ivWeapon);
        this.ivArmor = (ImageView) view.findViewById(C0380R.id.ivArmor);
        this.ivShield = (ImageView) view.findViewById(C0380R.id.ivShield);
        this.ivRing = (ImageView) view.findViewById(C0380R.id.ivRing);
        this.chkSelected = (CheckBox) view.findViewById(C0380R.id.chkSelected);
        this.tvWeapon = (TextView) view.findViewById(C0380R.id.btnWeapon);
        if (this.tvWeapon != null) {
            this.tvArmor = (TextView) view.findViewById(C0380R.id.btnArmor);
            this.tvShield = (TextView) view.findViewById(C0380R.id.btnShield);
            this.tvRing = (TextView) view.findViewById(C0380R.id.btnRing);
        } else {
            this.tvWeapon = (TextView) view.findViewById(C0380R.id.tvWeaponName);
            this.tvArmor = (TextView) view.findViewById(C0380R.id.tvArmorName);
            this.tvShield = (TextView) view.findViewById(C0380R.id.tvShieldName);
            this.tvRing = (TextView) view.findViewById(C0380R.id.tvRingName);
        }
        this.index = i;
    }

    public void show(Context context, GameContext gameContext, PlayerChar playerChar) {
        ImageView imageView = this.ivCharacater;
        if (imageView != null) {
            imageView.setImageBitmap(playerChar.getBitmap(context));
        }
        TextView textView = this.tvName;
        if (textView != null) {
            textView.setText(playerChar.name);
        }
        TextView textView2 = this.tvClass;
        if (textView2 != null) {
            textView2.setText(playerChar.clazz.getString(context));
        }
        TextView textView3 = this.tvRace;
        if (textView3 != null) {
            textView3.setText(playerChar.race.getName(context));
        }
        TextView textView4 = this.tvLevel;
        if (textView4 != null) {
            textView4.setText(Integer.toString(playerChar.level));
        }
        TextView textView5 = this.tvExp;
        if (textView5 != null) {
            textView5.setText(Integer.toString(playerChar.exp));
        }
        TextView textView6 = this.tvWeapon;
        if (textView6 != null && gameContext != null) {
            setEquipmentViews(context, this.ivWeapon, textView6, gameContext.getInventory(playerChar.weaponId));
            setEquipmentViews(context, this.ivArmor, this.tvArmor, gameContext.getInventory(playerChar.armorId));
            setEquipmentViews(context, this.ivShield, this.tvShield, gameContext.getInventory(playerChar.shieldId));
            setEquipmentViews(context, this.ivRing, this.tvRing, gameContext.getInventory(playerChar.ringId));
        }
        showHpMp(playerChar);
    }

    private void setEquipmentViews(Context context, ImageView imageView, TextView textView, Inventory inventory) {
        if (inventory == null) {
            imageView.setImageDrawable(context.getResources().getDrawable(C0380R.drawable.item_default));
            imageView.setVisibility(4);
            textView.setVisibility(4);
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(inventory.getBaseItem(context).drawableId));
            textView.setText(inventory.getName(context));
            imageView.setVisibility(0);
            textView.setVisibility(0);
        }
    }

    public void showHpMp(PlayerChar playerChar) {
        TextView textView = this.tvHp;
        if (textView != null) {
            textView.setText(Integer.toString(playerChar.f93hp));
        }
        TextView textView2 = this.tvMp;
        if (textView2 != null) {
            textView2.setText(Integer.toString(playerChar.f94mp));
        }
    }
}
