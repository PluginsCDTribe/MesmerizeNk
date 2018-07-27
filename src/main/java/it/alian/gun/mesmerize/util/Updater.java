package it.alian.gun.mesmerize.util;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import com.google.gson.Gson;
import it.alian.gun.mesmerize.MConfig;
import it.alian.gun.mesmerize.MLocale;
import it.alian.gun.mesmerize.MTasks;
import it.alian.gun.mesmerize.Mesmerize;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater implements Listener {

    private static final String UPDATER_API = "https://raw.githubusercontent.com/PluginsCDTribe/MesmerizeNk/master/version.json";

    private static UpdatePacket packet;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("mesmerize.updater") && packet != null)
            sendMessages(event.getPlayer());
    }

    private static void sendMessages(CommandSender sender) {
        if (MConfig.checkUpdate && !packet.version.equals(Mesmerize.instance.getDescription().getVersion())) {
            MLocale.UPDATER_HEADER.player(sender, packet.version, packet.releaseDate);
            for (String s : packet.description) {
                MLocale.UPDATER_BODY.player(sender, s);
            }
            MLocale.UPDATER_FOOTER.player(sender, Mesmerize.instance.getDescription().getWebsite());
        }
    }

    public static void start() {
        MTasks.executeTimer(() -> {
            try {
                URL url = new URL(UPDATER_API);
                HttpURLConnection connection = ((HttpURLConnection) url.openConnection());
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.connect();
                UpdatePacket[] packets = new Gson().fromJson(new InputStreamReader(connection.getInputStream(), "utf-8"), UpdatePacket[].class);
                if (packets.length > 0) {
                    packet = packets[0];
                    sendMessages(Server.getInstance().getConsoleSender());
                }
            } catch (IOException ignored) {
            }
        }, 1000 * 60 * 60);
        Server.getInstance().getPluginManager().registerEvents(new Updater(), Mesmerize.instance);
    }

    private static class UpdatePacket {
        String version, releaseDate;
        String[] description;
    }
}
