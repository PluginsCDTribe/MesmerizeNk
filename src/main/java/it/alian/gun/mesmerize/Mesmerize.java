package it.alian.gun.mesmerize;

import cn.nukkit.plugin.PluginBase;
import it.alian.gun.mesmerize.compat.Equipment;
import it.alian.gun.mesmerize.compat.SplashParticle;
import it.alian.gun.mesmerize.listeners.BattleListener;
import it.alian.gun.mesmerize.listeners.ItemListener;
import it.alian.gun.mesmerize.listeners.MiscListener;
import it.alian.gun.mesmerize.lore.LoreCalculator;
import it.alian.gun.mesmerize.lore.LoreParser;
import it.alian.gun.mesmerize.util.Updater;

import java.text.DecimalFormat;

public final class Mesmerize extends PluginBase {

    public static Mesmerize instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();
        MLocale.init();
        MLocale.GENERAL_VERSION.console(getServer().getVersion(), getDescription().getVersion());
        MConfig.init();
        MTasks.init();
        MCommand.init();
        LoreCalculator.init();
        LoreParser.init();
        BattleListener.init();
        ItemListener.init();
        Equipment.init();
        SplashParticle.init();
        MiscListener.init();
        Updater.start();
        MLocale.GENERAL_LOAD.console(new DecimalFormat("0.00")
                .format(((double) System.currentTimeMillis() - (double) time) / 1000D));
    }

    @Override
    public void onDisable() {
        MTasks.unload();
    }
}
