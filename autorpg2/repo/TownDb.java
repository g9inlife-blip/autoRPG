package com.shirobakama.autorpg2.repo;

import com.shirobakama.autorpg2.entity.Town;
import com.shirobakama.logquest2.C0380R;

/* loaded from: D:\Android_hobby\작업중\com.shirobakama.logquest2-1.3.13\classes.dex */
public final class TownDb {
    public static final int TOWN_COUNTRY = 2;
    public static final int TOWN_DWARF = 12;
    public static final int TOWN_EAST = 10;
    public static final int TOWN_ELF = 13;
    public static final int TOWN_FIRST = 1;
    public static final int TOWN_FORTRESS = 8;
    public static final int TOWN_LAKE = 3;
    public static final int TOWN_MAGCITY = 7;
    public static final int TOWN_MERCHANT = 4;
    public static final int TOWN_MINER = 5;
    public static final int TOWN_PORT = 6;
    public static final int TOWN_RUIN = 9;
    public static final int TOWN_SHIRE = 11;

    static void initialize() {
        Town town = new Town();
        town.f111id = 1;
        town.symbol = "first";
        town.nameStringId = C0380R.string.res_town_first;
        TownRepository.addTown(town);
        Town town2 = new Town();
        town2.f111id = 2;
        town2.symbol = "country";
        town2.nameStringId = C0380R.string.res_town_country;
        TownRepository.addTown(town2);
        Town town3 = new Town();
        town3.f111id = 3;
        town3.symbol = "lake";
        town3.nameStringId = C0380R.string.res_town_lake;
        TownRepository.addTown(town3);
        Town town4 = new Town();
        town4.f111id = 4;
        town4.symbol = "merchant";
        town4.nameStringId = C0380R.string.res_town_merchant;
        TownRepository.addTown(town4);
        Town town5 = new Town();
        town5.f111id = 5;
        town5.symbol = "miner";
        town5.nameStringId = C0380R.string.res_town_miner;
        TownRepository.addTown(town5);
        Town town6 = new Town();
        town6.f111id = 6;
        town6.symbol = "port";
        town6.nameStringId = C0380R.string.res_town_port;
        TownRepository.addTown(town6);
        Town town7 = new Town();
        town7.f111id = 7;
        town7.symbol = "magcity";
        town7.nameStringId = C0380R.string.res_town_magcity;
        TownRepository.addTown(town7);
        Town town8 = new Town();
        town8.f111id = 8;
        town8.symbol = "fortress";
        town8.nameStringId = C0380R.string.res_town_fortress;
        TownRepository.addTown(town8);
        Town town9 = new Town();
        town9.f111id = 9;
        town9.symbol = "ruin";
        town9.nameStringId = C0380R.string.res_town_ruin;
        TownRepository.addTown(town9);
        Town town10 = new Town();
        town10.f111id = 10;
        town10.symbol = "east";
        town10.nameStringId = C0380R.string.res_town_east;
        TownRepository.addTown(town10);
        Town town11 = new Town();
        town11.f111id = 11;
        town11.symbol = "shire";
        town11.nameStringId = C0380R.string.res_town_shire;
        TownRepository.addTown(town11);
        Town town12 = new Town();
        town12.f111id = 12;
        town12.symbol = "dwarf";
        town12.nameStringId = C0380R.string.res_town_dwarf;
        TownRepository.addTown(town12);
        Town town13 = new Town();
        town13.f111id = 13;
        town13.symbol = "elf";
        town13.nameStringId = C0380R.string.res_town_elf;
        TownRepository.addTown(town13);
    }
}
