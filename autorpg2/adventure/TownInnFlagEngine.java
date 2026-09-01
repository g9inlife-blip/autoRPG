package com.shirobakama.autorpg2.adventure;

import android.content.Context;
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
public class TownInnFlagEngine extends TownFlagEngine {
    private static final int[] MERCHANT_INN_PATTERN_1_NORMAL = {C0380R.string.cnv_merchant_inn_normal_1, C0380R.string.cnv_merchant_inn_normal_2, C0380R.string.cnv_merchant_inn_normal_3};
    private static final int[] MERCHANT_INN_PATTERN_2_INTRIGUE = {C0380R.string.cnv_merchant_inn_intrigue_accepted_1, C0380R.string.cnv_merchant_inn_intrigue_accepted_2, C0380R.string.cnv_merchant_inn_intrigue_accepted_3};

    public TownFlagEngine.Result peekInnHearing(Context context, GameContext gameContext) {
        TownFlagEngine.TownEngineState townEngineStateCopy = this.state.copy();
        TownFlagEngine.Result resultProcessInnHearing = processInnHearing(context, gameContext);
        this.state = townEngineStateCopy;
        return resultProcessInnHearing;
    }

    public TownFlagEngine.Result nextInnHearing(Context context, GameContext gameContext) {
        return processInnHearing(context, gameContext);
    }

    private TownFlagEngine.Result processInnHearing(Context context, GameContext gameContext) {
        checkConversationState(context, gameContext);
        FlagEngine.CurrentValues currentValues = new FlagEngine.CurrentValues(this.random, context, gameContext);
        switch (gameContext.townId) {
            case 1:
                return processInnFirst(currentValues);
            case 2:
                return processInnCountry(currentValues);
            case 3:
                return processInnLake(currentValues);
            case 4:
                return processInnMerchant(currentValues);
            case 5:
                return processInnMiner(currentValues);
            case 6:
                return processInnPort(currentValues);
            case 7:
                return processInnMagcity(currentValues);
            case 8:
                return processInnFortress(currentValues);
            case 9:
                return processInnRuin(currentValues);
            case 10:
                return processInnEast(currentValues);
            case 11:
                return processInnShire(currentValues);
            case 12:
                return processInnDwarf(currentValues);
            case 13:
                return processInnElf(currentValues);
            default:
                return null;
        }
    }

    private TownFlagEngine.Result processInnFirst(FlagEngine.CurrentValues currentValues) {
        int[] iArr;
        int[] iArr2;
        this.state.acceptQuest = false;
        this.state.refuseQuest = false;
        FlagEngine.QuestState questState = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_FIRST_FOREST_MONSTER);
        FlagEngine.QuestState questState2 = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_CAVE_GOBLINS);
        resetConversationIfQuestStatesAreChanged(questState, questState2);
        if (!questState.cleared) {
            if (!questState.started) {
                return messageResultOrQuest(currentValues, new int[]{C0380R.string.cnv_first_inn_forest_monster_normal_1, C0380R.string.cnv_first_inn_forest_monster_normal_2, C0380R.string.cnv_first_inn_forest_monster_normal_3}, QuestDb.QUEST_FIRST_FOREST_MONSTER, new Object[0]);
            }
            return messageResult(currentValues, new int[]{C0380R.string.cnv_first_inn_forest_monster_running_1, C0380R.string.cnv_first_inn_forest_monster_running_2}, new Object[0]);
        }
        if (!questState2.cleared) {
            if (!questState2.started) {
                if (questState.started) {
                    iArr2 = new int[]{C0380R.string.cnv_first_inn_forest_monster_cleared_1, C0380R.string.cnv_first_inn_forest_monster_cleared_2, C0380R.string.cnv_first_inn_forest_monster_cleared_3};
                } else {
                    iArr2 = new int[]{C0380R.string.cnv_first_inn_forest_monster_not_accept_cleared_1, C0380R.string.cnv_first_inn_forest_monster_not_accept_cleared_2, C0380R.string.cnv_first_inn_forest_monster_not_accept_cleared_3};
                }
                return messageResultOrQuest(currentValues, iArr2, QuestDb.QUEST_CAVE_GOBLINS, new Object[0]);
            }
            return messageResult(currentValues, new int[]{C0380R.string.cnv_first_inn_cave_goblins_running_1, C0380R.string.cnv_first_inn_cave_goblins_running_2}, new Object[0]);
        }
        if (questState2.started) {
            iArr = new int[]{C0380R.string.cnv_first_inn_cave_goblins_cleared_1, C0380R.string.cnv_first_inn_cave_goblins_cleared_2};
        } else {
            iArr = new int[]{C0380R.string.cnv_first_inn_cave_goblins_not_accept_cleared_1, C0380R.string.cnv_first_inn_cave_goblins_not_accept_cleared_2};
        }
        if (currentValues.isOff(keyMemberJoined(1))) {
            int[] iArr3 = {C0380R.string.cnv_first_inn_all_cleared_join_member_1, C0380R.string.cnv_first_inn_all_cleared_join_member_2, C0380R.string.cnv_first_inn_all_cleared_join_member_3, C0380R.string.cnv_first_inn_all_cleared_join_member_4, C0380R.string.cnv_first_inn_all_cleared_join_member_5};
            TownFlagEngine.Result resultMessageResult = messageResult(currentValues, iArr3, this.state.conversationSeq < iArr3.length + (-4) ? null : new Object[]{getMemberName(currentValues.context, 1)});
            return this.state.isLast ? addJoinMember(currentValues, resultMessageResult, 1).addGold(100) : resultMessageResult;
        }
        return messageResult(currentValues, iArr, new Object[0]);
    }

    private TownFlagEngine.Result processInnCountry(FlagEngine.CurrentValues currentValues) {
        int[] iArr;
        this.state.acceptQuest = false;
        this.state.refuseQuest = false;
        FlagEngine.QuestState questState = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_CAVE_GOBLINS);
        FlagEngine.QuestState questState2 = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_GIRL_RESCUE);
        resetConversationIfQuestStatesAreChanged(questState, questState2);
        if (!questState2.cleared) {
            if (!questState2.started) {
                if (!questState.cleared) {
                    return messageResult(currentValues, new int[]{C0380R.string.cnv_country_inn_rescue_normal_1, C0380R.string.cnv_country_inn_rescue_normal_2}, new Object[0]);
                }
                return messageResultOrQuest(currentValues, new int[]{C0380R.string.cnv_country_inn_rescue_normal_1, C0380R.string.cnv_country_inn_rescue_normal_2, C0380R.string.cnv_country_inn_rescue_normal_3}, QuestDb.QUEST_GIRL_RESCUE, new Object[0]);
            }
            return messageResult(currentValues, new int[]{C0380R.string.cnv_country_inn_rescue_running_1, C0380R.string.cnv_country_inn_rescue_running_2}, new Object[0]);
        }
        if (questState2.started) {
            iArr = new int[]{C0380R.string.cnv_country_inn_rescue_cleared_1, C0380R.string.cnv_country_inn_rescue_cleared_2};
        } else {
            iArr = new int[]{C0380R.string.cnv_country_inn_rescue_not_accept_cleared_1, C0380R.string.cnv_country_inn_rescue_not_accept_cleared_2};
        }
        return messageResult(currentValues, iArr, new Object[0]);
    }

    private TownFlagEngine.Result processInnLake(FlagEngine.CurrentValues currentValues) {
        int[] iArr;
        int[] iArr2;
        this.state.acceptQuest = false;
        this.state.refuseQuest = false;
        FlagEngine.QuestState questState = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_GIRL_RESCUE);
        FlagEngine.QuestState questState2 = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_GATE_OF_LAKE);
        FlagEngine.QuestState questState3 = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_DANGER_PATH);
        resetConversationIfQuestStatesAreChanged(questState, questState2, questState3);
        if (!questState2.cleared) {
            if (!questState2.started) {
                if (!questState.cleared) {
                    return messageResult(currentValues, new int[]{C0380R.string.cnv_lake_inn_gate_normal_1, C0380R.string.cnv_lake_inn_gate_normal_2, C0380R.string.cnv_lake_inn_gate_normal_3}, new Object[0]);
                }
                return messageResultOrQuest(currentValues, new int[]{C0380R.string.cnv_lake_inn_gate_normal_1, C0380R.string.cnv_lake_inn_gate_normal_2, C0380R.string.cnv_lake_inn_gate_normal_3, C0380R.string.cnv_lake_inn_gate_normal_4}, QuestDb.QUEST_GATE_OF_LAKE, new Object[0]);
            }
            return messageResult(currentValues, new int[]{C0380R.string.cnv_lake_inn_gate_running_1, C0380R.string.cnv_lake_inn_gate_running_2}, new Object[0]);
        }
        if (currentValues.isOff(keyMemberJoined(2))) {
            TownFlagEngine.Result resultMessageResult = messageResult(currentValues, new int[]{C0380R.string.cnv_lake_inn_gate_cleared_join_member_1, C0380R.string.cnv_lake_inn_gate_cleared_join_member_2, C0380R.string.cnv_lake_inn_gate_cleared_join_member_3, C0380R.string.cnv_lake_inn_gate_cleared_join_member_4, C0380R.string.cnv_lake_inn_gate_cleared_join_member_5}, this.state.conversationSeq < 1 ? null : new Object[]{getMemberName(currentValues.context, 2)});
            return this.state.isLast ? addJoinMember(currentValues, resultMessageResult, 2).addGold(200) : resultMessageResult;
        }
        if (!questState3.cleared) {
            if (!questState3.started) {
                if (questState2.started) {
                    iArr2 = new int[]{C0380R.string.cnv_lake_inn_gate_cleared_1, C0380R.string.cnv_lake_inn_gate_cleared_2};
                } else {
                    iArr2 = new int[]{C0380R.string.cnv_lake_inn_gate_not_accept_cleared_1, C0380R.string.cnv_lake_inn_gate_not_accept_cleared_2};
                }
                return messageResultOrQuest(currentValues, iArr2, QuestDb.QUEST_DANGER_PATH, new Object[0]);
            }
            return messageResult(currentValues, new int[]{C0380R.string.cnv_lake_inn_path_running_1, C0380R.string.cnv_lake_inn_path_running_2}, new Object[0]);
        }
        if (questState3.started) {
            iArr = new int[]{C0380R.string.cnv_lake_inn_path_cleared_1, C0380R.string.cnv_lake_inn_path_cleared_2};
        } else {
            iArr = new int[]{C0380R.string.cnv_lake_inn_path_not_accept_cleared_1, C0380R.string.cnv_lake_inn_path_not_accept_cleared_2};
        }
        return messageResult(currentValues, iArr, new Object[0]);
    }

    private TownFlagEngine.Result processInnShire(FlagEngine.CurrentValues currentValues) {
        int[] iArr;
        this.state.acceptQuest = false;
        this.state.refuseQuest = false;
        FlagEngine.QuestState questState = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_WILDERNESS_MONSTER);
        resetConversationIfQuestStatesAreChanged(questState);
        if (!questState.cleared) {
            if (!questState.started) {
                return messageResultOrQuest(currentValues, new int[]{C0380R.string.cnv_shire_inn_wilderness_normal_1, C0380R.string.cnv_shire_inn_wilderness_normal_2, C0380R.string.cnv_shire_inn_wilderness_normal_3}, QuestDb.QUEST_WILDERNESS_MONSTER, new Object[0]);
            }
            return messageResult(currentValues, new int[]{C0380R.string.cnv_shire_inn_wilderness_running_1, C0380R.string.cnv_shire_inn_wilderness_running_2}, new Object[0]);
        }
        if (questState.started) {
            iArr = new int[]{C0380R.string.cnv_shire_inn_wilderness_cleared_1, C0380R.string.cnv_shire_inn_wilderness_cleared_2};
        } else {
            iArr = new int[]{C0380R.string.cnv_shire_inn_wilderness_not_accept_cleared_1, C0380R.string.cnv_shire_inn_wilderness_not_accept_cleared_2};
        }
        return messageResult(currentValues, iArr, new Object[0]);
    }

    private TownFlagEngine.Result processInnMerchant(FlagEngine.CurrentValues currentValues) {
        this.state.acceptQuest = false;
        this.state.refuseQuest = false;
        FlagEngine.QuestState questState = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_PROOF_OF_ASSASSINATION);
        FlagEngine.QuestState questState2 = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_AT_TEMPLE);
        FlagEngine.QuestState questState3 = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_INTRIGUE_OF_WIZARD);
        FlagEngine.QuestState questState4 = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_ATTACK_WIZARD);
        FlagEngine.QuestState questState5 = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_ATTACK_DARK_LORD);
        resetConversationIfQuestStatesAreChanged(questState, questState2, questState3, questState4, questState5);
        GameFlag.Key keyAsQuest = GameFlag.Key.asQuest(QuestConst.MERCHANT_FLAG_INN_PROOF_OF_ASSASIN_CNV);
        Item item = ItemRepository.getItem(currentValues.context, ItemDb.ITEM_PAPER_FOLDER);
        GameFlag.Key keyAsHasItem = GameFlag.Key.asHasItem(item);
        GameFlag.Key keyAsQuest2 = GameFlag.Key.asQuest(QuestConst.MERCHANT_FLAG_INN_INTRIGUE_CNV);
        GameFlag.Key keyAsQuest3 = GameFlag.Key.asQuest(QuestConst.CATACOMBE_FLAG_HELP_ELF);
        resetConversationIfFlagsAreChanged(currentValues, keyAsQuest, keyAsHasItem, keyAsQuest2, keyAsQuest3, GameFlag.Key.asHasItem(ItemRepository.getItem(currentValues.context, ItemDb.ITEM_MONSTERS_JEWEL)));
        if (!questState.cleared) {
            if (!questState.started) {
                if (currentValues.isOff(keyAsQuest)) {
                    TownFlagEngine.Result resultMessageResult = messageResult(currentValues, MERCHANT_INN_PATTERN_1_NORMAL, new Object[0]);
                    if (this.state.isLast) {
                        resultMessageResult.addFlag(new GameFlag(keyAsQuest).setValue(true));
                    }
                    return resultMessageResult;
                }
                TownFlagEngine.Result resultMessageResultOrQuest = messageResultOrQuest(currentValues, new int[]{C0380R.string.cnv_merchant_inn_proof_normal_1, C0380R.string.cnv_merchant_inn_proof_normal_2, C0380R.string.cnv_merchant_inn_proof_normal_3, C0380R.string.cnv_merchant_inn_proof_normal_4, C0380R.string.cnv_merchant_inn_proof_normal_5}, QuestDb.QUEST_PROOF_OF_ASSASSINATION, new Object[0]);
                if (resultMessageResultOrQuest.message == null) {
                    resultMessageResultOrQuest.addFlag(new GameFlag(keyAsQuest).setValue(false));
                }
                return resultMessageResultOrQuest;
            }
            if (currentValues.isOff(keyAsHasItem)) {
                return messageResult(currentValues, MERCHANT_INN_PATTERN_1_NORMAL, new Object[0]);
            }
            int[] iArr = {C0380R.string.cnv_merchant_inn_proof_cleared_1, C0380R.string.cnv_merchant_inn_proof_cleared_2};
            TownFlagEngine.Result resultMessageResult2 = messageResult(currentValues, iArr, this.state.conversationSeq >= iArr.length - 1 ? new Object[]{500} : null);
            return this.state.isLast ? addClearQuest(currentValues, resultMessageResult2, QuestDb.QUEST_PROOF_OF_ASSASSINATION).addGold(500).setLostItem(item) : resultMessageResult2;
        }
        if (!questState2.cleared) {
            return messageResult(currentValues, MERCHANT_INN_PATTERN_1_NORMAL, new Object[0]);
        }
        if (!questState3.started) {
            if (currentValues.isOff(keyAsQuest2)) {
                TownFlagEngine.Result resultMessageResult3 = messageResult(currentValues, MERCHANT_INN_PATTERN_1_NORMAL, new Object[0]);
                if (this.state.isLast) {
                    resultMessageResult3.addFlag(new GameFlag(keyAsQuest2).setValue(true));
                }
                return resultMessageResult3;
            }
            return messageResultOrQuest(currentValues, new int[]{C0380R.string.cnv_merchant_inn_intrigue_normal_1, C0380R.string.cnv_merchant_inn_intrigue_normal_2, C0380R.string.cnv_merchant_inn_intrigue_normal_3, C0380R.string.cnv_merchant_inn_intrigue_normal_4, C0380R.string.cnv_merchant_inn_intrigue_normal_5, C0380R.string.cnv_merchant_inn_intrigue_normal_6}, QuestDb.QUEST_INTRIGUE_OF_WIZARD, new Object[0]);
        }
        if (!questState3.cleared) {
            if (currentValues.isOff(keyAsQuest3)) {
                return messageResult(currentValues, MERCHANT_INN_PATTERN_2_INTRIGUE, new Object[0]);
            }
            int[] iArr2 = {C0380R.string.cnv_merchant_inn_intrigue_cleared_1, C0380R.string.cnv_merchant_inn_intrigue_cleared_2};
            TownFlagEngine.Result resultMessageResult4 = messageResult(currentValues, iArr2, this.state.conversationSeq >= iArr2.length - 1 ? new Object[]{1500} : null);
            return this.state.isLast ? addClearQuest(currentValues, resultMessageResult4, QuestDb.QUEST_INTRIGUE_OF_WIZARD).addGold(1500) : resultMessageResult4;
        }
        if (!questState4.cleared) {
            return messageResult(currentValues, MERCHANT_INN_PATTERN_2_INTRIGUE, new Object[0]);
        }
        if (!questState5.cleared) {
            return messageResult(currentValues, new int[]{C0380R.string.cnv_merchant_inn_wizard_defeated_1, C0380R.string.cnv_merchant_inn_wizard_defeated_2, C0380R.string.cnv_merchant_inn_wizard_defeated_3}, new Object[0]);
        }
        TownFlagEngine.Result resultProcessJewelReading = processJewelReading(currentValues);
        return resultProcessJewelReading != null ? resultProcessJewelReading : messageResult(currentValues, new int[]{C0380R.string.cnv_merchant_inn_lord_defeated_1, C0380R.string.cnv_merchant_inn_lord_defeated_2}, new Object[0]);
    }

    private TownFlagEngine.Result processInnPort(FlagEngine.CurrentValues currentValues) {
        int[] iArr;
        this.state.acceptQuest = false;
        this.state.refuseQuest = false;
        FlagEngine.QuestState questState = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_SEA_GHOST);
        resetConversationIfQuestStatesAreChanged(questState);
        if (!questState.cleared) {
            if (!questState.started) {
                return messageResultOrQuest(currentValues, new int[]{C0380R.string.cnv_port_inn_ghost_normal_1, C0380R.string.cnv_port_inn_ghost_normal_2, C0380R.string.cnv_port_inn_ghost_normal_3, C0380R.string.cnv_port_inn_ghost_normal_4}, questState.symbol, new Object[0]);
            }
            return messageResult(currentValues, new int[]{C0380R.string.cnv_port_inn_ghost_running_1, C0380R.string.cnv_port_inn_ghost_running_2}, new Object[0]);
        }
        if (questState.started) {
            iArr = new int[]{C0380R.string.cnv_port_inn_ghost_cleared_1, C0380R.string.cnv_port_inn_ghost_cleared_2};
        } else {
            iArr = new int[]{C0380R.string.cnv_port_inn_ghost_not_accept_cleared_1, C0380R.string.cnv_port_inn_ghost_not_accept_cleared_2};
        }
        return messageResult(currentValues, iArr, new Object[0]);
    }

    private TownFlagEngine.Result processInnMiner(FlagEngine.CurrentValues currentValues) {
        if (currentValues.isOn(GameFlag.Key.asQuest(QuestConst.MINE_FLAG_HELP_DWARF)) && currentValues.isOff(keyMemberJoined(3))) {
            int[] iArr = {C0380R.string.cnv_miner_inn_mine_cleared_join_member_1, C0380R.string.cnv_miner_inn_mine_cleared_join_member_2, C0380R.string.cnv_miner_inn_mine_cleared_join_member_3, C0380R.string.cnv_miner_inn_mine_cleared_join_member_4, C0380R.string.cnv_miner_inn_mine_cleared_join_member_5, C0380R.string.cnv_miner_inn_mine_cleared_join_member_6};
            TownFlagEngine.Result resultMessageResult = messageResult(currentValues, iArr, this.state.conversationSeq < iArr.length - 4 ? null : new Object[]{getMemberName(currentValues.context, 3)});
            return this.state.isLast ? addJoinMember(currentValues, resultMessageResult, 3).addGold(400) : resultMessageResult;
        }
        return messageResult(currentValues, new int[]{C0380R.string.cnv_miner_inn_normal_1, C0380R.string.cnv_miner_inn_normal_2, C0380R.string.cnv_miner_inn_normal_3, C0380R.string.cnv_miner_inn_normal_4}, new Object[0]);
    }

    private TownFlagEngine.Result processInnMagcity(FlagEngine.CurrentValues currentValues) {
        this.state.acceptQuest = false;
        this.state.refuseQuest = false;
        FlagEngine.QuestState questState = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_INTRIGUE_OF_WIZARD);
        resetConversationIfQuestStatesAreChanged(questState);
        if (currentValues.isOn(GameFlag.Key.asQuest(QuestConst.CATACOMBE_FLAG_HELP_ELF)) && currentValues.isOff(keyMemberJoined(4))) {
            int[] iArr = {C0380R.string.cnv_magcity_inn_catacomb_cleared_join_member_1, C0380R.string.cnv_magcity_inn_catacomb_cleared_join_member_2, C0380R.string.cnv_magcity_inn_catacomb_cleared_join_member_3, C0380R.string.cnv_magcity_inn_catacomb_cleared_join_member_4, C0380R.string.cnv_magcity_inn_catacomb_cleared_join_member_5, C0380R.string.cnv_magcity_inn_catacomb_cleared_join_member_6, C0380R.string.cnv_magcity_inn_catacomb_cleared_join_member_7};
            TownFlagEngine.Result resultMessageResult = messageResult(currentValues, iArr, this.state.conversationSeq < iArr.length + (-6) ? null : new Object[]{getMemberName(currentValues.context, 4)});
            return this.state.isLast ? addJoinMember(currentValues, resultMessageResult, 4).addGold(800) : resultMessageResult;
        }
        if (!questState.started || questState.cleared) {
            return messageResult(currentValues, new int[]{C0380R.string.cnv_magcity_inn_normal_1, C0380R.string.cnv_magcity_inn_normal_2, C0380R.string.cnv_magcity_inn_normal_3}, new Object[0]);
        }
        return messageResult(currentValues, new int[]{C0380R.string.cnv_magcity_inn_intrigue_accepted_1, C0380R.string.cnv_magcity_inn_intrigue_accepted_2, C0380R.string.cnv_magcity_inn_intrigue_accepted_3, C0380R.string.cnv_magcity_inn_intrigue_accepted_4}, new Object[0]);
    }

    private TownFlagEngine.Result processInnDwarf(FlagEngine.CurrentValues currentValues) {
        int[] iArr;
        this.state.acceptQuest = false;
        this.state.refuseQuest = false;
        FlagEngine.QuestState questState = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_DRAGON_OF_VOLCANO);
        resetConversationIfQuestStatesAreChanged(questState);
        if (!questState.cleared) {
            if (!questState.started) {
                return messageResultOrQuest(currentValues, new int[]{C0380R.string.cnv_dwarf_inn_normal_1, C0380R.string.cnv_dwarf_inn_normal_2, C0380R.string.cnv_dwarf_inn_normal_3, C0380R.string.cnv_dwarf_inn_normal_4}, questState.symbol, new Object[0]);
            }
            return messageResult(currentValues, new int[]{C0380R.string.cnv_dwarf_inn_running_1}, new Object[0]);
        }
        if (questState.started) {
            iArr = new int[]{C0380R.string.cnv_dwarf_inn_cleared_1, C0380R.string.cnv_dwarf_inn_cleared_2};
        } else {
            iArr = new int[]{C0380R.string.cnv_dwarf_inn_not_accept_cleared_1, C0380R.string.cnv_dwarf_inn_not_accept_cleared_2};
        }
        return messageResult(currentValues, iArr, new Object[0]);
    }

    private TownFlagEngine.Result processInnElf(FlagEngine.CurrentValues currentValues) {
        int[] iArr;
        this.state.acceptQuest = false;
        this.state.refuseQuest = false;
        FlagEngine.QuestState questState = FlagEngine.getQuestState(currentValues, QuestDb.QUEST_FOREST_RAIDER);
        resetConversationIfQuestStatesAreChanged(questState);
        if (!questState.cleared) {
            if (!questState.started) {
                return messageResultOrQuest(currentValues, new int[]{C0380R.string.cnv_elf_inn_fog_normal_1, C0380R.string.cnv_elf_inn_fog_normal_2, C0380R.string.cnv_elf_inn_fog_normal_3, C0380R.string.cnv_elf_inn_fog_normal_4}, questState.symbol, new Object[0]);
            }
            return messageResult(currentValues, new int[]{C0380R.string.cnv_elf_inn_fog_running_1, C0380R.string.cnv_elf_inn_fog_running_2}, new Object[0]);
        }
        if (questState.started) {
            iArr = new int[]{C0380R.string.cnv_elf_inn_fog_cleared_1, C0380R.string.cnv_elf_inn_fog_cleared_2};
        } else {
            iArr = new int[]{C0380R.string.cnv_elf_inn_fog_not_accept_cleared_1, C0380R.string.cnv_elf_inn_fog_not_accept_cleared_2};
        }
        return messageResult(currentValues, iArr, new Object[0]);
    }

    private TownFlagEngine.Result processInnFortress(FlagEngine.CurrentValues currentValues) {
        return messageResult(currentValues, new int[]{C0380R.string.cnv_fortress_inn_normal_1, C0380R.string.cnv_fortress_inn_normal_2, C0380R.string.cnv_fortress_inn_normal_3, C0380R.string.cnv_fortress_inn_normal_4, C0380R.string.cnv_fortress_inn_normal_5}, new Object[0]);
    }

    private TownFlagEngine.Result processInnRuin(FlagEngine.CurrentValues currentValues) {
        return messageResult(currentValues, new int[]{C0380R.string.cnv_ruin_inn_normal_1, C0380R.string.cnv_ruin_inn_normal_2}, new Object[0]);
    }

    private TownFlagEngine.Result processInnEast(FlagEngine.CurrentValues currentValues) {
        return messageResult(currentValues, new int[]{C0380R.string.cnv_east_inn_normal_1, C0380R.string.cnv_east_inn_normal_2}, new Object[0]);
    }

    @Override // com.shirobakama.autorpg2.adventure.TownFlagEngine
    protected String processAcceptQuestSub(TownFlagEngine.Result result, FlagEngine.CurrentValues currentValues, Quest quest) {
        if (quest.symbol.equals(QuestDb.QUEST_FIRST_FOREST_MONSTER)) {
            return currentValues.context.getString(C0380R.string.cnv_first_inn_forest_monster_accept_1);
        }
        if (quest.symbol.equals(QuestDb.QUEST_CAVE_GOBLINS)) {
            return currentValues.context.getString(C0380R.string.cnv_first_inn_cave_goblins_accept_1);
        }
        if (quest.symbol.equals(QuestDb.QUEST_GIRL_RESCUE)) {
            return currentValues.context.getString(C0380R.string.cnv_country_inn_rescue_accept_1);
        }
        if (quest.symbol.equals(QuestDb.QUEST_GATE_OF_LAKE)) {
            return currentValues.context.getString(C0380R.string.cnv_lake_inn_gate_accept_1);
        }
        if (quest.symbol.equals(QuestDb.QUEST_DANGER_PATH)) {
            return currentValues.context.getString(C0380R.string.cnv_lake_inn_path_accept_1);
        }
        if (quest.symbol.equals(QuestDb.QUEST_WILDERNESS_MONSTER)) {
            return currentValues.context.getString(C0380R.string.cnv_shire_inn_wilderness_accept_1);
        }
        if (quest.symbol.equals(QuestDb.QUEST_PROOF_OF_ASSASSINATION)) {
            String string = currentValues.context.getString(C0380R.string.cnv_merchant_inn_proof_accept_1);
            result.addFlag(currentValues.game.getOrCreateFlag(GameFlag.Key.asQuest(QuestConst.MERCHANT_FLAG_INN_INTRIGUE_CNV)).setValue(true));
            return string;
        }
        if (quest.symbol.equals(QuestDb.QUEST_SEA_GHOST)) {
            return currentValues.context.getString(C0380R.string.cnv_port_inn_ghost_accept_1);
        }
        if (quest.symbol.equals(QuestDb.QUEST_INTRIGUE_OF_WIZARD)) {
            return currentValues.context.getString(C0380R.string.cnv_merchant_inn_intrigue_accept_1);
        }
        if (quest.symbol.equals(QuestDb.QUEST_DANGER_PATH)) {
            return currentValues.context.getString(C0380R.string.cnv_dwarf_inn_accept_1);
        }
        if (quest.symbol.equals(QuestDb.QUEST_FOREST_RAIDER)) {
            return currentValues.context.getString(C0380R.string.cnv_elf_inn_fog_accept_1);
        }
        return null;
    }

    @Override // com.shirobakama.autorpg2.adventure.TownFlagEngine
    protected String processRefuseQuestSub(TownFlagEngine.Result result, FlagEngine.CurrentValues currentValues, Quest quest) {
        if (quest.symbol.equals(QuestDb.QUEST_PROOF_OF_ASSASSINATION)) {
            String string = currentValues.context.getString(C0380R.string.cnv_merchant_inn_proof_not_accept_1);
            result.addFlag(currentValues.game.getOrCreateFlag(GameFlag.Key.asQuest(QuestConst.MERCHANT_FLAG_INN_PROOF_OF_ASSASIN_CNV)).setValue(false));
            return string;
        }
        if (!quest.symbol.equals(QuestDb.QUEST_INTRIGUE_OF_WIZARD)) {
            return null;
        }
        String string2 = currentValues.context.getString(C0380R.string.cnv_merchant_inn_intrigue_not_accept_1);
        result.addFlag(currentValues.game.getOrCreateFlag(GameFlag.Key.asQuest(QuestConst.MERCHANT_FLAG_INN_INTRIGUE_CNV)).setValue(false));
        return string2;
    }
}
