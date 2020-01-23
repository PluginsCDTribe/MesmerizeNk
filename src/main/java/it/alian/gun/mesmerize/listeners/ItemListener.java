package it.alian.gun.mesmerize.listeners;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.inventory.InventoryHolder;
import it.alian.gun.mesmerize.MConfig;
import it.alian.gun.mesmerize.Mesmerize;
import it.alian.gun.mesmerize.compat.Compat;
import it.alian.gun.mesmerize.lore.ItemInfo;
import it.alian.gun.mesmerize.lore.LoreInfo;
import it.alian.gun.mesmerize.lore.LoreParser;
import it.alian.gun.mesmerize.util.Math;

public class ItemListener implements Listener {

    @EventHandler
    public void onItemChange(PlayerItemHeldEvent event) {
        long nano = System.nanoTime();
        if (LoreParser.check(event.getItem(), event.getPlayer())) {
            event.setCancelled(true);
        }
        if (MConfig.debug)
            System.out.println(event.getEventName() + " processed in " + (System.nanoTime() - nano) * 1E-6 + " ms.");
    }

    /*
    @EventHandler
    public void onItemUse(PlayerItemDamageEvent event) {
        long nano = System.nanoTime();
        ItemInfo info = LoreParser.parseItem(event.getItem());
        if (Math.random() < info.getUnbreakable()) event.setDamage(0);
        if (event.getItem().getDurability() == (event.getItem().getType().getMaxDurability() - 1) && !MConfig.General.breakOnDurabilityOff) {
            ItemStack itemStack = event.getItem().clone();
            event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), itemStack);
            event.setCancelled(true);
            event.getPlayer().getInventory().remove(itemStack);
            event.getPlayer().sendMessage(String.format(MConfig.Message.onDurabilityItemDrop, itemStack.getItemMeta().getDisplayName()));
        } else if (event.getItem().getType().getMaxDurability() != 0) {
            double prev = ((double) event.getItem().getDurability()) / ((double) event.getItem().getType().getMaxDurability());
            double now = ((double) (event.getItem().getDurability() + event.getDamage())) / ((double) event.getItem().getType().getMaxDurability());
            for (double v : MConfig.General.durabilityWarnThreshold) {
                if (v > prev && v <= now) {
                    event.getPlayer().sendMessage(String.format(MConfig.Message.onDurabilityWarn,
                            event.getItem().hasItemMeta() ? event.getItem().getItemMeta().hasDisplayName() ?
                                    event.getItem().getItemMeta().getDisplayName() : event.getItem().getType().name() :
                                    event.getItem().getType().name(), ((1D - prev) * 100D)));
                }
            }
        }
        if (MConfig.debug)
            System.out.println(event.getEventName() + " processed in " + (System.nanoTime() - nano) * 1E-6 + " ms.");
    }
    */

    @EventHandler
    public void onPickup(InventoryPickupItemEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Player)
            if (LoreParser.check(event.getItem().getItem(), ((Player) holder))) {
                event.setCancelled(true);
            }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        /*
        if (LoreParser.check(event.getCurrentItem(), event.getWhoClicked())) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(MConfig.Message.omSoulboundCheck);
        }
        */
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (MConfig.General.enableHealthControl)
            for (Player player : Compat.getOnlinePlayers()) {
                if (player.isAlive() && player.isValid()) {
                    double health = Math.min(LoreParser.getByEntityId(player.getId()).getRegeneration() + player.getHealth(),
                            player.getMaxHealth());
                    player.setHealth((float) health);
                }
            }
    }

    public static void init() {
        Server.getInstance().getPluginManager().registerEvents(new ItemListener(), Mesmerize.instance);
        Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(Mesmerize.instance, () ->
                Server.getInstance().getPluginManager().callEvent(new TickEvent()), 10, (int) MConfig.General.regenInterval);
        Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(Mesmerize.instance, () -> {
            for (Player livingEntity : Compat.getOnlinePlayers()) {
                LoreInfo info = LoreParser.getByEntityId(livingEntity.getId());
                if (MConfig.General.enableHealthControl) {
                    livingEntity.setMaxHealth((int) Math.constraint(MConfig.General.maximumHealth,
                            MConfig.General.minimalHealth, MConfig.General.baseHealth + info.getHealth()));
                }
                livingEntity.setMovementSpeed((float) Math.constraint(0.9999,
                        MConfig.General.minimalMovespeed, MConfig.General.baseMovespeed + info.getMoveSpeed()));
            }
        }, 5, (int) MConfig.Performance.loreUpdateInterval);
    }

    public static class TickEvent extends Event {

        private static final HandlerList handlers = new HandlerList();

        public static HandlerList getHandlers() {
            return handlers;
        }

    }

}
