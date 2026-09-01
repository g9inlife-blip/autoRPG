package com.shirobakama.autorpg2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.shirobakama.autorpg2.adventure.QuestConst;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.GameFlag;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.autorpg2.entity.ItemObject;
import com.shirobakama.autorpg2.entity.ShopItem;
import com.shirobakama.autorpg2.repo.MonsterRepository;
import com.shirobakama.autorpg2.util.FormatUtil;
import com.shirobakama.logquest2.C0380R;
import java.util.Iterator;
import java.util.List;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class ItemDetailDialogFragment extends AlertDialogFragment {
    private boolean mJewelRead;
    private ItemObject mSelectedItem;
    private String mSeparator;
    private boolean mShowExtraClass;

    public static void setArguments(Bundle bundle, int i, boolean z, boolean z2, boolean z3, boolean z4, GameContext gameContext) {
        bundle.putInt("item_obj_id", i);
        bundle.putBoolean("show_extra_class", z);
        bundle.putBoolean("catalog", z2);
        bundle.putBoolean("shop_item", z3);
        bundle.putBoolean("stock_item", z4);
        GameFlag flag = gameContext.getFlag(GameFlag.Key.asQuest(QuestConst.FLAG_EXTRA_DUNGEON_ITEM_MSG_READ));
        bundle.putBoolean("jewel_read", flag != null && flag.value);
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment, android.support.v4.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        List list;
        this.mSeparator = getString(C0380R.string.item_object_name_separator);
        this.mShowExtraClass = getArguments().getBoolean("show_extra_class");
        int i = getArguments().getInt("item_obj_id");
        this.mJewelRead = getArguments().getBoolean("jewel_read", false);
        this.mSelectedItem = null;
        if (getArguments().getBoolean("catalog")) {
            this.mSelectedItem = new ShopItem();
            ItemObject itemObject = this.mSelectedItem;
            itemObject.f98id = i;
            itemObject.itemId = i;
        } else {
            TownActivity townActivity = (TownActivity) getActivity();
            if (getArguments().getBoolean("shop_item")) {
                list = townActivity.shopOperation.shopItems;
            } else if (getArguments().getBoolean("stock_item")) {
                list = townActivity.game.stocks;
            } else {
                list = townActivity.game.inventories;
            }
            Iterator it = list.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                ItemObject itemObject2 = (ItemObject) it.next();
                if (itemObject2.f98id == i) {
                    this.mSelectedItem = itemObject2;
                    break;
                }
            }
        }
        if (this.mSelectedItem == null) {
            Toast.makeText(getActivity(), C0380R.string.msg_internal_error, 0).show();
        }
        return super.onCreateDialog(bundle);
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    protected void prepareBuilderOnCreateDialog(AlertDialog.Builder builder) {
        int i;
        switch (this.mSelectedItem.getBaseItem(getActivity()).type) {
            case CONSUMABLE:
            case OTHER:
                i = C0380R.string.msg_dlg_title_item;
                break;
            case RING:
                i = C0380R.string.msg_dlg_title_ring;
                break;
            case ARMOR:
                i = C0380R.string.msg_dlg_title_armor;
                break;
            case SHIELD:
                i = C0380R.string.msg_dlg_title_shield;
                break;
            case WEAPON:
                i = C0380R.string.msg_dlg_title_weapon;
                break;
            default:
                i = 0;
                break;
        }
        builder.setTitle(i);
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    @SuppressLint({"InflateParams"})
    protected View getAlertDialogView() {
        if (this.mSelectedItem == null) {
            return new View(getActivity());
        }
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(getActivity());
        switch (this.mSelectedItem.getBaseItem(getActivity()).type) {
            case CONSUMABLE:
            case OTHER:
            case RING:
                View viewInflate = layoutInflaterFrom.inflate(C0380R.layout.detailed_item_dialog, (ViewGroup) null);
                ItemObject itemObject = this.mSelectedItem;
                showOtherItemView(viewInflate, itemObject, itemObject.getBaseItem(getActivity()));
                return viewInflate;
            case ARMOR:
            case SHIELD:
                View viewInflate2 = layoutInflaterFrom.inflate(C0380R.layout.detailed_def_item_dialog, (ViewGroup) null);
                ItemObject itemObject2 = this.mSelectedItem;
                showDefenceItemView(viewInflate2, itemObject2, itemObject2.getBaseItem(getActivity()));
                return viewInflate2;
            case WEAPON:
                View viewInflate3 = layoutInflaterFrom.inflate(C0380R.layout.detailed_weapon_dialog, (ViewGroup) null);
                ItemObject itemObject3 = this.mSelectedItem;
                showWeaponView(viewInflate3, itemObject3, itemObject3.getBaseItem(getActivity()));
                return viewInflate3;
            default:
                return null;
        }
    }

    private void showWeaponView(View view, ItemObject itemObject, Item item) {
        ItemObject.AttrStatAdjustments attrStatusAdjustments = itemObject.getAttrStatusAdjustments(getActivity());
        int i = attrStatusAdjustments.get(GameChar.Status.WEAPON_MAGIC_BONUS);
        ((TextView) view.findViewById(C0380R.id.tvWeaponName)).setText(itemObject.getName(getActivity()));
        ((TextView) view.findViewById(C0380R.id.tvWeaponSort)).setText(attrStatusAdjustments.isEnchanted() ? C0380R.string.res_weapon_sort_enchanted : C0380R.string.res_weapon_sort_normal);
        setRequiredStr((TextView) view.findViewById(C0380R.id.tvWeaponRequiredStr), itemObject, item);
        setEquipableClass((TextView) view.findViewById(C0380R.id.tvWeaponEquipableClass), itemObject, item);
        ((TextView) view.findViewById(C0380R.id.tvWeaponType)).setText(item.weaponType.getString(getActivity()));
        ((TextView) view.findViewById(C0380R.id.tvWeaponNormalDamage)).setText(getString(C0380R.string.lbl_from_to, Integer.valueOf(Math.max(item.diceNum + i, 0)), Integer.valueOf(Math.max((item.diceFace * item.diceNum) + i, 0))));
        ((TextView) view.findViewById(C0380R.id.tvWeaponThroughDamage)).setText(Integer.toString(item.attrBase));
        ((TextView) view.findViewById(C0380R.id.tvWeaponHitAdjust)).setText(Integer.toString(attrStatusAdjustments.get(GameChar.Status.HIT_BONUS) + i));
        ((TextView) view.findViewById(C0380R.id.tvWeaponCriticalAdjust)).setText(FormatUtil.getAdjustDesc(attrStatusAdjustments.get(GameChar.Status.CRITICAL)));
        ((TextView) view.findViewById(C0380R.id.tvWeaponFumbleAdjust)).setText(FormatUtil.getAdjustDesc(attrStatusAdjustments.get(GameChar.Status.FUMBLE)));
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        getItemAdjustDescription(attrStatusAdjustments, item, sb, sb2);
        ((TextView) view.findViewById(C0380R.id.tvWeaponOtherAdjust)).setText(sb.toString());
        if (sb2.length() > 0) {
            ((TextView) view.findViewById(C0380R.id.tvWeaponSpecial)).setText(sb2.toString());
        } else {
            view.findViewById(C0380R.id.trWeaponSpecial).setVisibility(8);
        }
        ((TextView) view.findViewById(C0380R.id.tvWeaponDetail)).setText(getString(item.descriptionStringId));
    }

    private void setEquipableClass(TextView textView, ItemObject itemObject, Item item) {
        StringBuilder sb = new StringBuilder();
        for (GameChar.CharClass charClass : GameChar.CharClass.values()) {
            if (this.mShowExtraClass || charClass.isStandard()) {
                boolean z = item.classEquipable[charClass.ordinal()];
                if (charClass == GameChar.CharClass.THIEF || charClass == GameChar.CharClass.NINJA) {
                    z &= ((itemObject.getRequiredStrX2(item) + 1) * 2) / 3 <= 18;
                }
                if (z) {
                    sb.append(charClass.getString(textView.getContext()).subSequence(0, 1));
                }
            }
        }
        textView.setText(sb.toString());
    }

    private void setRequiredStr(TextView textView, ItemObject itemObject, Item item) {
        String string;
        int requiredStrX2 = (itemObject.getRequiredStrX2(item) + 1) / 2;
        int requiredStrX22 = ((itemObject.getRequiredStrX2(item) + 1) * 2) / 3;
        if (requiredStrX22 > 18) {
            string = textView.getContext().getString(C0380R.string.res_required_str_no_rogue, Integer.valueOf(requiredStrX2));
        } else {
            string = textView.getContext().getString(C0380R.string.res_required_str, Integer.valueOf(requiredStrX2), Integer.valueOf(requiredStrX22));
        }
        textView.setText(string);
    }

    private void addAdjustDescription(StringBuilder sb, String str, int i) {
        if (sb.length() > 0) {
            sb.append("\n");
        }
        sb.append(str);
        if (this.mSeparator.length() > 0 && !str.endsWith(" ")) {
            sb.append(this.mSeparator);
        }
        if (i > 0) {
            sb.append('+');
        }
        sb.append(i);
    }

    private void getItemAdjustDescription(ItemObject.AttrStatAdjustments attrStatAdjustments, Item item, StringBuilder sb, StringBuilder sb2) {
        String string;
        for (GameChar.Attribute attribute : GameChar.ATTRIBUTES) {
            int i = attrStatAdjustments.get(attribute);
            if (i != 0) {
                addAdjustDescription(sb, attribute.getString(getActivity()), i);
            }
        }
        for (GameChar.Status status : GameChar.STATUS_ARRAY) {
            int i2 = attrStatAdjustments.get(status);
            if (i2 != 0 && (item.type != Item.Type.WEAPON || (status != GameChar.Status.HIT_BONUS && status != GameChar.Status.CRITICAL && status != GameChar.Status.FUMBLE))) {
                addAdjustDescription(sb, status.getString(getActivity()), i2);
            }
        }
        if (attrStatAdjustments.getAttrType() != null && sb2 != null && sb2.length() > 0) {
            sb2.append("\n");
            sb2.append(getString(C0380R.string.lbl_item_desc_attr_type, attrStatAdjustments.getAttrType().getString(getActivity())));
        }
        for (Item.Effect effect : item.effects) {
            if (effect.type == Item.Effect.Type.KILLER && sb2 != null) {
                if (sb2.length() > 0) {
                    sb2.append("\n");
                }
                if (effect.monsterType != null) {
                    string = effect.monsterType.getString(getActivity());
                } else {
                    string = MonsterRepository.getMonster(getActivity(), effect.monsterId).name;
                }
                sb2.append(getString(C0380R.string.res_weapon_special_desc, string, Integer.valueOf(effect.value)));
            }
        }
    }

    private void showDefenceItemView(View view, ItemObject itemObject, Item item) {
        ItemObject.AttrStatAdjustments attrStatusAdjustments = itemObject.getAttrStatusAdjustments(getActivity());
        ((TextView) view.findViewById(C0380R.id.tvDefItemName)).setText(itemObject.getName(getActivity()));
        setRequiredStr((TextView) view.findViewById(C0380R.id.tvDefItemRequiredStr), itemObject, item);
        setEquipableClass((TextView) view.findViewById(C0380R.id.tvDefItemEquipableClass), itemObject, item);
        ((TextView) view.findViewById(C0380R.id.tvDefItemValue)).setText(Integer.toString(Math.max(item.attrBase + attrStatusAdjustments.get(item.type == Item.Type.ARMOR ? GameChar.Status.ARMOR_MAGIC_BONUS : GameChar.Status.SHIELD_MAGIC_BONUS), 0)));
        if (item.type == Item.Type.ARMOR) {
            ((TextView) view.findViewById(C0380R.id.tvDefItemValueLabel)).setText(C0380R.string.lbl_defense_power);
        } else {
            ((TextView) view.findViewById(C0380R.id.tvDefItemValueLabel)).setText(C0380R.string.lbl_shield_power);
        }
        StringBuilder sb = new StringBuilder();
        getItemAdjustDescription(attrStatusAdjustments, item, sb, sb);
        ((TextView) view.findViewById(C0380R.id.tvDefItemOtherAdjust)).setText(sb.toString());
        ((TextView) view.findViewById(C0380R.id.tvDefItemDetail)).setText(getString(item.descriptionStringId));
    }

    private void showOtherItemView(View view, ItemObject itemObject, Item item) {
        ItemObject.AttrStatAdjustments attrStatusAdjustments = itemObject.getAttrStatusAdjustments(getActivity());
        String name = itemObject.getName(getActivity());
        ((TextView) view.findViewById(C0380R.id.tvItemName)).setText(name);
        int iMax = Math.max(item.diceNum, 0);
        int iMax2 = Math.max(item.diceFace * item.diceNum, 0);
        if (iMax != 0 || iMax2 != 0) {
            ((TextView) view.findViewById(C0380R.id.tvItemValue)).setText(getString(C0380R.string.lbl_from_to, Integer.valueOf(iMax), Integer.valueOf(iMax2)));
        } else {
            view.findViewById(C0380R.id.trItemValue).setVisibility(8);
        }
        StringBuilder sb = new StringBuilder();
        getItemAdjustDescription(attrStatusAdjustments, item, sb, null);
        ((TextView) view.findViewById(C0380R.id.tvItemOtherAdjust)).setText(sb.toString());
        if (item.f97id != 5170 || !this.mJewelRead) {
            ((TextView) view.findViewById(C0380R.id.tvItemOtherDetail)).setText(item.descriptionStringId);
            return;
        }
        ((TextView) view.findViewById(C0380R.id.tvItemOtherDetail)).setText(getString(item.descriptionStringId) + getString(C0380R.string.msg_quest_extra_from_lord_guardian_in_item_detail_1, name) + getString(C0380R.string.msg_quest_extra_from_lord_guardian_in_item_detail_2));
    }

    @Override // com.shirobakama.autorpg2.AlertDialogFragment
    protected void onClick(DialogInterface dialogInterface, int i) {
        if (i == -3) {
            ((TownActivity) getActivity()).inputItemName(getArguments().getInt("item_obj_id"));
        }
    }
}
