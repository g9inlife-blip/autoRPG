package com.shirobakama.autorpg2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.shirobakama.autorpg2.AlertDialogFragment;
import com.shirobakama.autorpg2.adventure.FlagEngine;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.GameFlag;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.autorpg2.entity.ItemObject;
import com.shirobakama.autorpg2.entity.Monster;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.autorpg2.entity.Quest;
import com.shirobakama.autorpg2.entity.ShopItem;
import com.shirobakama.autorpg2.repo.ItemRepository;
import com.shirobakama.autorpg2.repo.MonsterDb;
import com.shirobakama.autorpg2.repo.MonsterRepository;
import com.shirobakama.autorpg2.repo.QuestRepository;
import com.shirobakama.autorpg2.util.DeviceUtil;
import com.shirobakama.logquest2.C0380R;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class CatalogActivity extends FragmentActivity implements View.OnClickListener {
    private static final int CATALOG_MODE_ITEM = 1;
    private static final int CATALOG_MODE_MONSTER = 2;
    private static final int CATALOG_MODE_QUEST = 3;
    private static final String EXTRA_CATALOG_MODE = "catalog.mode";
    private CatalogAdapter mCatalogAdapter;
    private List<CatalogEntry> mCatalogEntries;
    protected GameContext mGame;
    protected boolean mItemCatalog;
    private ListView mLvCatalog;
    protected boolean mMonsterCatalog;
    protected boolean mQuestCatalog;
    protected SparseArray<Quest> mQuestsForNumber;
    protected boolean mShowExtraClass;
    private TextView mTvTotalAdventureTime;

    public static class CatalogEntry {
        public String count;
        public String description;
        public int iconDrawableId;

        /* renamed from: id */
        public int f55id;
        public String name;
        public String number;
    }

    static void setForItemCatalog(Intent intent) {
        intent.putExtra(EXTRA_CATALOG_MODE, 1);
    }

    static void setForMonsterCatalog(Intent intent) {
        intent.putExtra(EXTRA_CATALOG_MODE, 2);
    }

    static void setForQuestCatalog(Intent intent) {
        intent.putExtra(EXTRA_CATALOG_MODE, 3);
    }

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        DeviceUtil.setLocaleIfNeeded(this, null);
        setContentView(C0380R.layout.catalog);
        DeviceUtil.handleOrientation(this, null);
        int intExtra = getIntent().getIntExtra(EXTRA_CATALOG_MODE, 1);
        this.mItemCatalog = intExtra == 1;
        this.mMonsterCatalog = intExtra == 2;
        this.mQuestCatalog = intExtra == 3;
        TextView textView = (TextView) findViewById(C0380R.id.tvCatalog);
        this.mLvCatalog = (ListView) findViewById(C0380R.id.lvCatalog);
        this.mTvTotalAdventureTime = (TextView) findViewById(C0380R.id.tvTotalAdventureTime);
        if (bundle != null) {
            this.mGame = (GameContext) bundle.getParcelable("game");
        } else {
            this.mGame = GameContext.game;
            GameContext.game = null;
        }
        this.mShowExtraClass = false;
        Iterator<PlayerChar> it = this.mGame.characters.iterator();
        while (it.hasNext()) {
            this.mShowExtraClass = (!it.next().clazz.isStandard()) | this.mShowExtraClass;
        }
        createEntries();
        this.mCatalogAdapter = new CatalogAdapter(this, this.mCatalogEntries);
        this.mLvCatalog.setAdapter((ListAdapter) this.mCatalogAdapter);
        this.mLvCatalog.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.shirobakama.autorpg2.CatalogActivity.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                CatalogEntry catalogEntry = (CatalogEntry) adapterView.getItemAtPosition(i);
                if (CatalogActivity.this.mItemCatalog) {
                    AlertDialogFragment.Decorator decorator = new AlertDialogFragment.Decorator();
                    decorator.setCancelable(true).setPositiveText(0);
                    ItemDetailDialogFragment.setArguments(decorator.args(), catalogEntry.f55id, CatalogActivity.this.mShowExtraClass, true, false, false, CatalogActivity.this.mGame);
                    decorator.decorate(new ItemDetailDialogFragment()).show(CatalogActivity.this.getSupportFragmentManager());
                    return;
                }
                if (CatalogActivity.this.mMonsterCatalog) {
                    AlertDialogFragment.Decorator decorator2 = new AlertDialogFragment.Decorator();
                    decorator2.setTitle(C0380R.string.lbl_monster_catalog_dialog_title).setCancelable(true);
                    decorator2.setPositiveText(0);
                    decorator2.args().putInt("monster_id", catalogEntry.f55id);
                    decorator2.decorate(new MonsterDetailDialogFragment()).show(CatalogActivity.this.getSupportFragmentManager());
                    return;
                }
                if (CatalogActivity.this.mQuestCatalog) {
                    AlertDialogFragment.Decorator decorator3 = new AlertDialogFragment.Decorator();
                    decorator3.setTitle(CatalogActivity.this.mQuestsForNumber.get(catalogEntry.f55id).nameStringId).setCancelable(true);
                    decorator3.setMessage(CatalogActivity.this.mQuestsForNumber.get(catalogEntry.f55id).descStringId);
                    decorator3.setPositiveText(0);
                    decorator3.decorate(new AlertDialogFragment.SimpleDialogFragment()).show(CatalogActivity.this.getSupportFragmentManager());
                }
            }
        });
        textView.setText(getString(this.mItemCatalog ? C0380R.string.lbl_item_catalog_title : this.mMonsterCatalog ? C0380R.string.lbl_monster_catalog_title : C0380R.string.lbl_quest_catalog_title, new Object[]{Integer.valueOf(this.mCatalogEntries.size())}));
        ((Button) findViewById(C0380R.id.btnClose)).setOnClickListener(this);
        if (this.mQuestCatalog) {
            GameFlag flag = this.mGame.getFlag(GameFlag.Key.asType(GameFlag.FlagType.OTHER, "total_adventure_time"));
            int optionAsInt = flag == null ? 0 : flag.getOptionAsInt();
            this.mTvTotalAdventureTime.setText(getString(C0380R.string.msg_dlg_about_total_time, new Object[]{Integer.valueOf(optionAsInt / 60), Integer.valueOf(optionAsInt % 60)}));
            this.mTvTotalAdventureTime.setVisibility(0);
        } else {
            this.mTvTotalAdventureTime.setVisibility(8);
        }
        setResult(0);
    }

    @Override // android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("game", this.mGame);
    }

    public static class ItemCatalogEntry extends CatalogEntry {
        private ItemObject mItemObject = new ShopItem();

        public ItemCatalogEntry(Context context, int i, int i2, int i3) {
            ItemObject itemObject = this.mItemObject;
            itemObject.f98id = i;
            itemObject.itemId = i;
            this.f55id = i;
            this.number = context.getString(C0380R.string.lbl_catalog_number, Integer.valueOf(i2));
            this.iconDrawableId = this.mItemObject.getBaseItem(context).drawableId;
            this.description = this.mItemObject.getDescription(context, true);
            this.name = this.mItemObject.getName(context);
            this.count = context.getString(C0380R.string.lbl_item_catalog_count, Integer.valueOf(i3));
        }
    }

    public static class MonsterCatalogEntry extends CatalogEntry {
        public int encount;
        private Monster mMonster;
        public int win;

        public MonsterCatalogEntry(Context context, Monster monster, int i) {
            this.mMonster = monster;
            this.f55id = monster.f105id;
            this.number = context.getString(C0380R.string.lbl_catalog_number, Integer.valueOf(i));
            this.iconDrawableId = monster.thumbnailImageResId;
            this.description = context.getString(C0380R.string.lbl_monster_catalog_level, Integer.valueOf(monster.level));
            this.name = this.mMonster.name;
        }

        public void setCount(Context context) {
            this.count = context.getString(C0380R.string.lbl_monster_catalog_count, Integer.valueOf(this.encount), Integer.valueOf(this.win));
        }
    }

    public static class QuestCatalogEntry extends CatalogEntry {
        public static QuestCatalogEntry create(Context context, Quest quest, FlagEngine.QuestState questState) {
            int i;
            if (questState.started) {
                i = questState.cleared ? C0380R.string.lbl_quest_state_cleared : C0380R.string.lbl_quest_state_running;
            } else {
                i = questState.cleared ? C0380R.string.lbl_quest_state_not_accept_cleared : 0;
            }
            if (i == 0) {
                return null;
            }
            QuestCatalogEntry questCatalogEntry = new QuestCatalogEntry();
            questCatalogEntry.f55id = quest.number;
            questCatalogEntry.number = context.getString(C0380R.string.lbl_catalog_number, Integer.valueOf(quest.number));
            questCatalogEntry.name = context.getString(quest.nameStringId);
            questCatalogEntry.count = context.getString(i);
            return questCatalogEntry;
        }
    }

    private void createEntries() {
        int optionAsInt;
        int optionAsInt2;
        Item itemBySymbol;
        this.mCatalogEntries = new ArrayList();
        if (this.mItemCatalog) {
            for (GameFlag gameFlag : this.mGame.flags.values()) {
                if (gameFlag.type == GameFlag.FlagType.ITEM && (itemBySymbol = ItemRepository.getItemBySymbol(this, gameFlag.name)) != null) {
                    this.mCatalogEntries.add(new ItemCatalogEntry(this, itemBySymbol.f97id, itemBySymbol.number, gameFlag.getOptionAsInt()));
                }
            }
        } else if (this.mMonsterCatalog) {
            MonsterRepository.getMonster(this, 20);
            SparseIntArray sparseIntArray = new SparseIntArray();
            Iterator<Map.Entry<Integer, Monster>> it = MonsterRepository.monsters.entrySet().iterator();
            int i = 1;
            while (it.hasNext()) {
                int iIntValue = it.next().getKey().intValue();
                if (iIntValue != 1560 && iIntValue != 1580) {
                    sparseIntArray.put(iIntValue, i);
                    i++;
                }
            }
            sparseIntArray.put(1560, sparseIntArray.get(1570));
            sparseIntArray.put(MonsterDb.MONSTER_DARK_LOAD_AMULET_GHOST, sparseIntArray.get(MonsterDb.MONSTER_DARK_LOAD_NORMAL_GHOST));
            MonsterCatalogEntry[] monsterCatalogEntryArr = new MonsterCatalogEntry[i];
            Iterator<GameFlag> it2 = this.mGame.flags.values().iterator();
            while (true) {
                optionAsInt = 0;
                if (!it2.hasNext()) {
                    break;
                }
                GameFlag next = it2.next();
                if (next.type == GameFlag.FlagType.MONSTER || next.type == GameFlag.FlagType.MONSTER_WIN) {
                    Monster monsterBySymbol = MonsterRepository.getMonsterBySymbol(this, next.name);
                    if (monsterBySymbol != null) {
                        int i2 = sparseIntArray.get(monsterBySymbol.f105id) - 1;
                        MonsterCatalogEntry monsterCatalogEntry = monsterCatalogEntryArr[i2];
                        if (monsterCatalogEntry == null) {
                            monsterCatalogEntry = new MonsterCatalogEntry(this, monsterBySymbol, i2 + 1);
                            monsterCatalogEntryArr[i2] = monsterCatalogEntry;
                        }
                        if (next.type == GameFlag.FlagType.MONSTER) {
                            optionAsInt2 = next.getOptionAsInt();
                        } else {
                            optionAsInt = next.getOptionAsInt();
                            optionAsInt2 = 0;
                        }
                        monsterCatalogEntry.encount += optionAsInt2;
                        monsterCatalogEntry.win += optionAsInt;
                    }
                }
            }
            int length = monsterCatalogEntryArr.length;
            while (optionAsInt < length) {
                MonsterCatalogEntry monsterCatalogEntry2 = monsterCatalogEntryArr[optionAsInt];
                if (monsterCatalogEntry2 != null) {
                    monsterCatalogEntry2.setCount(this);
                    this.mCatalogEntries.add(monsterCatalogEntry2);
                }
                optionAsInt++;
            }
        } else {
            Collection<Quest> collectionValues = QuestRepository.getQuests().values();
            this.mQuestsForNumber = new SparseArray<>();
            for (Quest quest : collectionValues) {
                QuestCatalogEntry questCatalogEntryCreate = QuestCatalogEntry.create(this, quest, FlagEngine.getQuestState(this.mGame, quest.symbol));
                if (questCatalogEntryCreate != null) {
                    this.mCatalogEntries.add(questCatalogEntryCreate);
                }
                this.mQuestsForNumber.put(quest.number, quest);
            }
        }
        Collections.sort(this.mCatalogEntries, new Comparator<CatalogEntry>() { // from class: com.shirobakama.autorpg2.CatalogActivity.2
            @Override // java.util.Comparator
            public int compare(CatalogEntry catalogEntry, CatalogEntry catalogEntry2) {
                return catalogEntry.f55id - catalogEntry2.f55id;
            }
        });
    }

    public static class CatalogAdapter extends ArrayAdapter<CatalogEntry> {
        private LayoutInflater mInflater;

        public static class ViewHolder {
            public ImageView ivIcon;
            public TextView tvCount;
            public TextView tvDescription;
            public TextView tvName;
            public TextView tvNumber;
        }

        public CatalogAdapter(Context context, List<CatalogEntry> list) {
            super(context, 0, list);
            this.mInflater = LayoutInflater.from(context);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = this.mInflater.inflate(C0380R.layout.list_item_catalog_entry, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.tvNumber = (TextView) view.findViewById(C0380R.id.tvNumber);
                viewHolder.tvName = (TextView) view.findViewById(C0380R.id.tvName);
                viewHolder.tvDescription = (TextView) view.findViewById(C0380R.id.tvDescription);
                viewHolder.tvCount = (TextView) view.findViewById(C0380R.id.tvCount);
                viewHolder.ivIcon = (ImageView) view.findViewById(C0380R.id.ivIcon);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            CatalogEntry item = getItem(i);
            viewHolder.tvNumber.setText(item.number);
            viewHolder.tvName.setText(item.name);
            viewHolder.tvCount.setText(item.count);
            if (item.description != null) {
                viewHolder.tvDescription.setText(item.description);
                viewHolder.tvDescription.setVisibility(0);
            } else {
                viewHolder.tvDescription.setVisibility(8);
            }
            if (item.iconDrawableId != 0) {
                viewHolder.ivIcon.setImageDrawable(getContext().getResources().getDrawable(item.iconDrawableId));
                viewHolder.ivIcon.setVisibility(0);
            } else {
                viewHolder.ivIcon.setVisibility(8);
            }
            return view;
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() != 2131165245) {
            return;
        }
        finish();
    }

    public static class MonsterDetailDialogFragment extends AlertDialogFragment {
        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        protected void onClick(DialogInterface dialogInterface, int i) {
        }

        @Override // com.shirobakama.autorpg2.AlertDialogFragment
        @SuppressLint({"InflateParams"})
        protected View getAlertDialogView() {
            View viewInflate = LayoutInflater.from(getActivity()).inflate(C0380R.layout.monster_catalog_dialog, (ViewGroup) null);
            Monster monster = MonsterRepository.getMonster(getActivity(), getArguments().getInt("monster_id"));
            LogViewActivity.setMonsterInfoToDetail(getActivity(), viewInflate, 0, monster);
            ((TextView) viewInflate.findViewById(C0380R.id.tvMonsterDescription)).setText(monster.getDescription(getActivity()));
            return viewInflate;
        }
    }
}
