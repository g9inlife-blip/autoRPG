package com.shirobakama.autorpg2.adventure;

import android.content.Context;
import com.shirobakama.autorpg2.entity.DungeonEvent;
import com.shirobakama.autorpg2.entity.GameChar;
import com.shirobakama.autorpg2.entity.PlayerChar;
import com.shirobakama.logquest2.C0380R;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class SpringEngine {
    private static final StrIds STR_IDS_CUP_BOARD;
    private static final StrIds STR_IDS_RACK;
    private static final StrIds STR_IDS_SPRING = new StrIds();
    private List<PlayerChar> mCharacters;
    private Context mContext;
    private int mDifficulty;
    private DungeonEvent mEvent;
    private DungeonEvent.EventSubType mEventSubType;
    private boolean mKnowing;
    private Random mRandom;
    private String mSentenceSeparator;
    private StrIds mStrIds;

    static class StrIds {
        public int cureHp;
        public int cureHpAll;
        public int cureMp;
        public int cureMpAll;
        public int know;
        public int knowDrink;
        public int knowNoDrink;
        public int none;
        public int notKnow;
        public int notKnowIdentifyBad;
        public int notKnowIdentifyGood;
        public int notKnowNotIdentify;
        public int poison;
        public int title;

        StrIds() {
        }
    }

    static {
        StrIds strIds = STR_IDS_SPRING;
        strIds.title = C0380R.string.alog_title_event_spring;
        strIds.notKnow = C0380R.string.alog_desc_event_spring_not_know;
        strIds.notKnowNotIdentify = C0380R.string.alog_desc_event_spring_not_know_not_identify;
        strIds.notKnowIdentifyBad = C0380R.string.alog_desc_event_spring_not_know_identify_bad;
        strIds.notKnowIdentifyGood = C0380R.string.alog_desc_event_spring_not_know_identify_good;
        strIds.poison = C0380R.string.alog_desc_event_spring_poison;
        strIds.cureHp = C0380R.string.alog_desc_event_spring_cure_hp;
        strIds.cureHpAll = C0380R.string.alog_desc_event_spring_cure_hp_all;
        strIds.cureMp = C0380R.string.alog_desc_event_spring_cure_mp;
        strIds.cureMpAll = C0380R.string.alog_desc_event_spring_cure_mp_all;
        strIds.none = C0380R.string.alog_desc_event_spring_none;
        strIds.know = C0380R.string.alog_desc_event_spring_know;
        strIds.knowNoDrink = C0380R.string.alog_desc_event_spring_know_no_drink;
        strIds.knowDrink = C0380R.string.alog_desc_event_spring_know_drink;
        STR_IDS_CUP_BOARD = new StrIds();
        StrIds strIds2 = STR_IDS_CUP_BOARD;
        strIds2.title = C0380R.string.alog_title_event_cup_board;
        strIds2.notKnow = C0380R.string.alog_desc_event_cup_board_not_know;
        strIds2.notKnowNotIdentify = C0380R.string.alog_desc_event_cup_board_not_know_not_identify;
        strIds2.notKnowIdentifyBad = C0380R.string.alog_desc_event_cup_board_not_know_identify_bad;
        strIds2.notKnowIdentifyGood = C0380R.string.alog_desc_event_cup_board_not_know_identify_good;
        strIds2.poison = C0380R.string.alog_desc_event_cup_board_poison;
        strIds2.cureHp = C0380R.string.alog_desc_event_cup_board_cure_hp;
        strIds2.cureHpAll = C0380R.string.alog_desc_event_cup_board_cure_hp_all;
        strIds2.cureMp = C0380R.string.alog_desc_event_cup_board_cure_mp;
        strIds2.cureMpAll = C0380R.string.alog_desc_event_cup_board_cure_mp_all;
        strIds2.none = C0380R.string.alog_desc_event_cup_board_none;
        strIds2.know = C0380R.string.alog_desc_event_cup_board_know;
        strIds2.knowNoDrink = C0380R.string.alog_desc_event_cup_board_know_no_drink;
        strIds2.knowDrink = C0380R.string.alog_desc_event_cup_board_know_drink;
        STR_IDS_RACK = new StrIds();
        StrIds strIds3 = STR_IDS_RACK;
        strIds3.title = C0380R.string.alog_title_event_rack;
        strIds3.notKnow = C0380R.string.alog_desc_event_rack_not_know;
        strIds3.notKnowNotIdentify = C0380R.string.alog_desc_event_rack_not_know_not_identify;
        strIds3.notKnowIdentifyBad = C0380R.string.alog_desc_event_rack_not_know_identify_bad;
        strIds3.notKnowIdentifyGood = C0380R.string.alog_desc_event_rack_not_know_identify_good;
        strIds3.poison = C0380R.string.alog_desc_event_rack_poison;
        strIds3.cureHp = C0380R.string.alog_desc_event_rack_cure_hp;
        strIds3.cureHpAll = C0380R.string.alog_desc_event_rack_cure_hp_all;
        strIds3.cureMp = C0380R.string.alog_desc_event_rack_cure_mp;
        strIds3.cureMpAll = C0380R.string.alog_desc_event_rack_cure_mp_all;
        strIds3.none = C0380R.string.alog_desc_event_rack_none;
        strIds3.know = C0380R.string.alog_desc_event_rack_know;
        strIds3.knowNoDrink = C0380R.string.alog_desc_event_rack_know_no_drink;
        strIds3.knowDrink = C0380R.string.alog_desc_event_rack_know_drink;
    }

    public SpringEngine(Context context, DungeonEvent dungeonEvent, DungeonEvent.EventSubType eventSubType, boolean z, int i, List<PlayerChar> list, Random random) {
        this.mContext = context;
        this.mEvent = dungeonEvent;
        this.mEventSubType = eventSubType;
        this.mKnowing = z;
        this.mDifficulty = i;
        this.mCharacters = list;
        this.mRandom = random;
        Collections.shuffle(this.mCharacters, this.mRandom);
        this.mSentenceSeparator = context.getString(C0380R.string.res_sentence_separator);
        switch (this.mEventSubType) {
            case CUP_BOARD:
                this.mStrIds = STR_IDS_CUP_BOARD;
                break;
            case RACK:
                this.mStrIds = STR_IDS_RACK;
                break;
            case SPRING:
                this.mStrIds = STR_IDS_SPRING;
                break;
        }
    }

    public int processEventSpring(StringBuilder sb) {
        PlayerChar next;
        PlayerChar playerChar = null;
        int i = 0;
        if (!this.mKnowing) {
            sb.append(this.mContext.getString(this.mStrIds.notKnow));
            sb.append(this.mSentenceSeparator);
            Iterator<PlayerChar> it = this.mCharacters.iterator();
            while (true) {
                if (!it.hasNext()) {
                    next = null;
                    break;
                }
                next = it.next();
                if (next.hasSubClass(GameChar.SubClass.SORCERER) || next.hasSubClass(GameChar.SubClass.CLERIC)) {
                    if (new Thrower(this.mRandom).attributeThrow(next, GameChar.Attribute.INT, next.getSubLevel(GameChar.SubClass.SORCERER) >= next.getSubLevel(GameChar.SubClass.CLERIC) ? GameChar.SubClass.SORCERER : GameChar.SubClass.CLERIC).success(this.mDifficulty)) {
                        break;
                    }
                }
            }
            if (next == null) {
                List<PlayerChar> list = this.mCharacters;
                PlayerChar playerChar2 = list.get(this.mRandom.nextInt(list.size()));
                sb.append(this.mContext.getString(this.mStrIds.notKnowNotIdentify, playerChar2.name));
                playerChar = playerChar2;
            } else if (this.mEvent.type == DungeonEvent.EventType.SPRING_POISON) {
                sb.append(this.mContext.getString(this.mStrIds.notKnowIdentifyBad, next.name));
            } else {
                sb.append(this.mContext.getString(this.mStrIds.notKnowIdentifyGood, next.name));
            }
            sb.append(this.mSentenceSeparator);
        } else {
            sb.append(this.mContext.getString(this.mStrIds.know));
            sb.append(this.mSentenceSeparator);
            if (this.mEvent.type != DungeonEvent.EventType.SPRING_POISON) {
                sb.append(this.mContext.getString(this.mStrIds.knowDrink));
                sb.append(this.mSentenceSeparator);
            }
        }
        Thrower thrower = new Thrower(this.mRandom);
        switch (this.mEvent.type) {
            case SPRING_CURE:
                i = this.mStrIds.cureHp;
                for (PlayerChar playerChar3 : this.mCharacters) {
                    playerChar3.f93hp += thrower.throwDice(1, 12) + (this.mDifficulty / 5);
                    if (playerChar3.f93hp > playerChar3.maxHp) {
                        playerChar3.f93hp = playerChar3.maxHp;
                    }
                }
                break;
            case SPRING_CURE_ALL:
                i = this.mStrIds.cureHpAll;
                for (PlayerChar playerChar4 : this.mCharacters) {
                    playerChar4.f93hp = playerChar4.maxHp;
                }
                break;
            case SPRING_CURE_MP:
                i = this.mStrIds.cureMp;
                for (PlayerChar playerChar5 : this.mCharacters) {
                    playerChar5.f94mp += thrower.throwDice(1, 12) + (this.mDifficulty / 5);
                    if (playerChar5.f94mp > playerChar5.maxMp) {
                        playerChar5.f94mp = playerChar5.maxMp;
                    }
                }
                break;
            case SPRING_CURE_MP_ALL:
                i = this.mStrIds.cureMpAll;
                for (PlayerChar playerChar6 : this.mCharacters) {
                    playerChar6.f94mp = playerChar6.maxMp;
                }
                break;
            case SPRING_NONE:
                i = this.mStrIds.none;
                break;
            case SPRING_POISON:
                if (playerChar != null) {
                    i = this.mStrIds.poison;
                    playerChar.f93hp = Math.max(playerChar.f93hp - (thrower.throwDice(1, 12) + (this.mDifficulty / 5)), 1);
                    break;
                } else {
                    i = this.mStrIds.knowNoDrink;
                    break;
                }
        }
        if (i != 0) {
            sb.append(this.mContext.getString(i));
            sb.append(this.mSentenceSeparator);
        }
        return this.mStrIds.title;
    }
}
