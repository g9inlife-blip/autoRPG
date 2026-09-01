package com.shirobakama.autorpg2.repo;

import com.shirobakama.autorpg2.entity.Quest;
import java.util.HashMap;
import java.util.Map;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public final class QuestRepository {
    private static int questNumber = 1;
    private static Map<String, Quest> quests;

    private QuestRepository() {
    }

    private static void initialize() {
        quests = new HashMap();
        QuestDb.initialize();
    }

    static void addQuest(Quest quest) {
        int i = questNumber;
        questNumber = i + 1;
        quest.number = i;
        quests.put(quest.symbol, quest);
    }

    public static Quest getQuest(String str) {
        if (quests == null) {
            initialize();
        }
        return quests.get(str);
    }

    public static Map<String, Quest> getQuests() {
        if (quests == null) {
            initialize();
        }
        return quests;
    }

    public static void flush() {
        quests = null;
    }
}
