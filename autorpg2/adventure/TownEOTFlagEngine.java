package com.shirobakama.autorpg2.adventure;

import android.content.Context;
import com.shirobakama.autorpg2.AutoRpgMainActivity;
import com.shirobakama.autorpg2.adventure.FlagEngine;
import com.shirobakama.autorpg2.adventure.TownFlagEngine;
import com.shirobakama.autorpg2.entity.GameContext;
import com.shirobakama.autorpg2.entity.GameFlag;
import com.shirobakama.autorpg2.entity.Item;
import com.shirobakama.autorpg2.entity.Quest;
import com.shirobakama.autorpg2.repo.ItemDb;
import com.shirobakama.autorpg2.repo.ItemRepository;
import com.shirobakama.autorpg2.repo.QuestDb;
import com.shirobakama.logquest2.C0380R;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public class TownEOTFlagEngine extends TownFlagEngine {
    public TownFlagEngine.Result peekEOT(Context context, GameContext gameContext) {
        TownFlagEngine.TownEngineState townEngineStateCopy = this.state.copy();
        TownFlagEngine.Result resultProcessEdgeOfTown = processEdgeOfTown(context, gameContext);
        this.state = townEngineStateCopy;
        return resultProcessEdgeOfTown;
    }

    public TownFlagEngine.Result nextEOT(Context context, GameContext gameContext) {
        return processEdgeOfTown(context, gameContext);
    }

    public TownFlagEngine.Result processEdgeOfTown(Context context, GameContext gameContext) {
        FlagEngine.CurrentValues currentValues = new FlagEngine.CurrentValues(this.random, context, gameContext);
        int i = gameContext.townId;
        if (i == 2) {
            return processEOTCountry(currentValues);
        }
        if (i != 4) {
            return null;
        }
        return processEOTMerchant(currentValues);
    }

    private TownFlagEngine.Result processEOTCountry(FlagEngine.CurrentValues currentValues) {
        FlagEngine.QuestState questState = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_MIDNIGHT_FLOWER);
        if (!FlagEngine.getQuestState(currentValues, QuestDb.QUEST_GIRL_RESCUE).cleared || questState.cleared) {
            return null;
        }
        resetConversationIfQuestStatesAreChanged(questState);
        if (!questState.started) {
            return messageResultOrQuest(currentValues, new int[]{C0380R.string.cnv_country_eot_flower_normal_1, C0380R.string.cnv_country_eot_flower_normal_2, C0380R.string.cnv_country_eot_flower_normal_3, C0380R.string.cnv_country_eot_flower_normal_4, C0380R.string.cnv_country_eot_flower_normal_5}, QuestDb.QUEST_MIDNIGHT_FLOWER, new Object[0]);
        }
        Item item = ItemRepository.getItem(currentValues.context, ItemDb.ITEM_PURPLE_FLOWER);
        if (currentValues.isOn(GameFlag.Key.asHasItem(item))) {
            TownFlagEngine.Result resultMessageResult = messageResult(currentValues, new int[]{C0380R.string.cnv_country_eot_flower_cleared_1}, new Object[0]);
            return this.state.isLast ? addClearQuest(currentValues, resultMessageResult, QuestDb.QUEST_MIDNIGHT_FLOWER).setLostItem(item) : resultMessageResult;
        }
        return messageResult(currentValues, new int[]{C0380R.string.cnv_country_eot_flower_running_1}, new Object[0]);
    }

    private TownFlagEngine.Result processEOTMerchant(FlagEngine.CurrentValues currentValues) {
        FlagEngine.QuestState questState = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_PROOF_OF_ASSASSINATION);
        FlagEngine.QuestState questState2 = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_AT_TEMPLE);
        FlagEngine.QuestState questState3 = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_INTRIGUE_OF_WIZARD);
        FlagEngine.QuestState questState4 = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_ATTACK_WIZARD);
        FlagEngine.QuestState questState5 = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_ATTACK_DARK_LORD);
        if (!questState.cleared) {
            return null;
        }
        if (questState2.cleared && questState5.cleared) {
            TownFlagEngine.Result resultProcessJewelReading = processJewelReading(currentValues);
            if (resultProcessJewelReading != null) {
                return resultProcessJewelReading;
            }
            return null;
        }
        resetConversationIfQuestStatesAreChanged(questState, questState2, questState3, questState4, questState5);
        GameFlag.Key keyAsQuest = GameFlag.Key.asQuest(QuestConst.TEMPLE_FLAG_REPOSED);
        GameFlag.Key keyAsQuest2 = GameFlag.Key.asQuest(QuestConst.TOWER_FLAG_DEFEAT_WIZARD);
        GameFlag.Key keyAsQuest3 = GameFlag.Key.asQuest(QuestConst.DARKLOAD_ACCEPT_TS);
        GameFlag.Key keyAsQuest4 = GameFlag.Key.asQuest(QuestConst.PALACE_FLAG_DEFEAT_LORD);
        GameFlag.Key keyAsStockItem = GameFlag.Key.asStockItem(ItemRepository.getItem(currentValues.context, 1280));
        resetConversationIfFlagsAreChanged(currentValues, keyAsQuest, keyAsQuest2, keyAsQuest3, keyAsQuest4, keyAsStockItem);
        if (!questState2.cleared) {
            if (!questState2.started) {
                return messageResultOrQuest(currentValues, new int[]{C0380R.string.cnv_merchant_eot_temple_normal_1, C0380R.string.cnv_merchant_eot_temple_normal_2, C0380R.string.cnv_merchant_eot_temple_normal_3, C0380R.string.cnv_merchant_eot_temple_normal_4}, QuestDb.QUEST_AT_TEMPLE, this.state.conversationSeq == 2 ? new Object[]{800} : new Object[0]);
            }
            if (this.state.acceptQuest) {
                TownFlagEngine.Result resultMessageResult = messageResult(currentValues, new int[]{C0380R.string.cnv_merchant_eot_temple_accept_1, C0380R.string.cnv_merchant_eot_temple_accept_2}, new Object[0]);
                this.state.acceptQuest = true ^ this.state.isLast;
                return resultMessageResult;
            }
            if (currentValues.isOff(keyAsQuest)) {
                return messageResult(currentValues, new int[]{C0380R.string.cnv_merchant_eot_temple_running_1}, new Object[0]);
            }
            TownFlagEngine.Result resultMessageResult2 = messageResult(currentValues, new int[]{C0380R.string.cnv_merchant_eot_temple_cleared_1, C0380R.string.cnv_merchant_eot_temple_cleared_2}, this.state.conversationSeq == 1 ? new Object[]{800} : new Object[0]);
            return !this.state.isLast ? resultMessageResult2 : addClearQuest(currentValues, resultMessageResult2, QuestDb.QUEST_AT_TEMPLE).addGold(800);
        }
        if (!questState3.cleared) {
            return null;
        }
        if (!questState4.cleared) {
            if (!questState4.started) {
                this.state.acceptQuest = false;
                return messageResultOrQuest(currentValues, new int[]{C0380R.string.cnv_merchant_eot_wizard_normal_1, C0380R.string.cnv_merchant_eot_wizard_normal_2, C0380R.string.cnv_merchant_eot_wizard_normal_3, C0380R.string.cnv_merchant_eot_wizard_normal_4, C0380R.string.cnv_merchant_eot_wizard_normal_5, C0380R.string.cnv_merchant_eot_wizard_normal_6, C0380R.string.cnv_merchant_eot_wizard_normal_7}, QuestDb.QUEST_ATTACK_WIZARD, new Object[0]);
            }
            if (this.state.acceptQuest) {
                TownFlagEngine.Result resultMessageResult3 = messageResult(currentValues, new int[]{C0380R.string.cnv_merchant_eot_wizard_accept_1, C0380R.string.cnv_merchant_eot_wizard_accept_2}, new Object[0]);
                this.state.acceptQuest = true ^ this.state.isLast;
                return resultMessageResult3;
            }
            if (currentValues.isOff(keyAsQuest2)) {
                return messageResult(currentValues, new int[]{C0380R.string.cnv_merchant_eot_wizard_running_1}, new Object[0]);
            }
            return addClearQuest(currentValues, new TownFlagEngine.Result(null), QuestDb.QUEST_ATTACK_WIZARD);
        }
        if (questState5.cleared) {
            return null;
        }
        if (!questState5.started) {
            this.state.acceptQuest = false;
            return messageResultOrQuest(currentValues, new int[]{C0380R.string.cnv_merchant_eot_wizard_cleared_1, C0380R.string.cnv_merchant_eot_wizard_cleared_2, C0380R.string.cnv_merchant_eot_wizard_cleared_3, C0380R.string.cnv_merchant_eot_wizard_cleared_4, C0380R.string.cnv_merchant_eot_wizard_cleared_5, C0380R.string.cnv_merchant_eot_wizard_cleared_6}, QuestDb.QUEST_ATTACK_DARK_LORD, new Object[0]);
        }
        if (this.state.acceptQuest) {
            TownFlagEngine.Result resultMessageResult4 = messageResult(currentValues, new int[]{C0380R.string.cnv_merchant_eot_lord_accept_1}, new Object[0]);
            this.state.acceptQuest = true ^ this.state.isLast;
            this.state.conversationSeq = 0;
            return resultMessageResult4;
        }
        if (currentValues.isOff(keyAsQuest4)) {
            boolean z = System.currentTimeMillis() > ((((long) currentValues.getFlag(keyAsQuest3).getOptionAsInt()) * 60) * 1000) + AutoRpgMainActivity.FORCE_RETURN_PERIOD_MS;
            if (currentValues.isOn(keyAsStockItem) || !z) {
                return messageResult(currentValues, new int[]{C0380R.string.cnv_merchant_eot_lord_running_1_1, C0380R.string.cnv_merchant_eot_lord_running_1_2, C0380R.string.cnv_merchant_eot_lord_running_1_3}, new Object[0]);
            }
            Item item = ItemRepository.getItem(currentValues.context, 1280);
            int[] iArr = {C0380R.string.cnv_merchant_eot_lord_running_2_1, C0380R.string.cnv_merchant_eot_lord_running_2_2, C0380R.string.cnv_merchant_eot_lord_running_2_3, C0380R.string.cnv_merchant_eot_lord_running_2_4, C0380R.string.cnv_merchant_eot_lord_running_2_5, C0380R.string.cnv_merchant_eot_lord_running_2_6, C0380R.string.cnv_merchant_eot_lord_running_2_7};
            TownFlagEngine.Result resultMessageResult5 = messageResult(currentValues, iArr, this.state.conversationSeq == iArr.length - 1 ? new Object[]{item.name} : new Object[0]);
            if (this.state.isLast) {
                resultMessageResult5.item = item;
                resultMessageResult5.addFlag(new GameFlag(keyAsQuest3).setOptionAsInt(((int) ((System.currentTimeMillis() / 1000) / 60)) + 14400));
            }
            return resultMessageResult5;
        }
        Item item2 = ItemRepository.getItem(currentValues.context, 5070);
        TownFlagEngine.Result resultMessageResult6 = messageResult(currentValues, new int[]{C0380R.string.cnv_merchant_eot_lord_cleared_1, C0380R.string.cnv_merchant_eot_lord_cleared_2, C0380R.string.cnv_merchant_eot_lord_cleared_3, C0380R.string.cnv_merchant_eot_lord_cleared_4, C0380R.string.cnv_merchant_eot_lord_cleared_5, C0380R.string.cnv_merchant_eot_lord_cleared_6, C0380R.string.cnv_merchant_eot_lord_cleared_7}, this.state.conversationSeq == 4 ? new Object[]{item2.name} : new Object[0]);
        if (this.state.isLast) {
            resultMessageResult6.item = item2;
            addClearQuest(currentValues, resultMessageResult6, QuestDb.QUEST_ATTACK_DARK_LORD);
        }
        return resultMessageResult6;
    }

    @Override // com.shirobakama.autorpg2.adventure.TownFlagEngine
    protected String processAcceptQuestSub(TownFlagEngine.Result result, FlagEngine.CurrentValues currentValues, Quest quest) {
        if (quest.symbol.equals(QuestDb.QUEST_MIDNIGHT_FLOWER)) {
            return currentValues.context.getString(C0380R.string.cnv_country_eot_flower_accept_1);
        }
        if (quest.symbol.equals(QuestDb.QUEST_ATTACK_DARK_LORD)) {
            GameFlag.Key keyAsQuest = GameFlag.Key.asQuest(QuestConst.DARKLOAD_ACCEPT_TS);
            result.addFlag(currentValues.game.getOrCreateFlag(keyAsQuest).setOptionAsInt((int) ((System.currentTimeMillis() / 1000) / 60)));
        }
        return null;
    }

    @Override // com.shirobakama.autorpg2.adventure.TownFlagEngine
    protected String processRefuseQuestSub(TownFlagEngine.Result result, FlagEngine.CurrentValues currentValues, Quest quest) {
        if (quest.symbol.equals(QuestDb.QUEST_ATTACK_WIZARD)) {
            return currentValues.context.getString(C0380R.string.cnv_merchant_eot_wizard_not_accept_1);
        }
        if (quest.symbol.equals(QuestDb.QUEST_ATTACK_DARK_LORD)) {
            return currentValues.context.getString(C0380R.string.cnv_merchant_eot_lord_not_accept_1);
        }
        return null;
    }
}
