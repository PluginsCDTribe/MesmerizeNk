package it.alian.gun.mesmerize.compat;


import cn.nukkit.Player;
import cn.nukkit.Server;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class Compat {

    public static List<Player> getOnlinePlayers() {
        return ImmutableList.copyOf(Server.getInstance().getOnlinePlayers().values());
    }

}
