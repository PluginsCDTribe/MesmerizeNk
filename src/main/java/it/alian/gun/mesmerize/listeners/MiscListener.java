package it.alian.gun.mesmerize.listeners;

import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerQuitEvent;
import it.alian.gun.mesmerize.Mesmerize;
import it.alian.gun.mesmerize.lore.LoreParser;

public class MiscListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        long id = event.getPlayer().getId();
        LoreParser.remove(id);
    }

    public static void init() {
        Server.getInstance().getPluginManager().registerEvents(new MiscListener(), Mesmerize.instance);
    }
}
