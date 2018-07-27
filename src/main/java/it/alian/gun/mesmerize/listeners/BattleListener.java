package it.alian.gun.mesmerize.listeners;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import it.alian.gun.mesmerize.MConfig;
import it.alian.gun.mesmerize.MTasks;
import it.alian.gun.mesmerize.Mesmerize;
import it.alian.gun.mesmerize.lore.LoreCalculator;
import it.alian.gun.mesmerize.lore.LoreInfo;
import it.alian.gun.mesmerize.lore.LoreParser;

public class BattleListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        long nano = System.nanoTime();
        performAttack(event);
        if (MConfig.debug)
            System.out.println(event.getEventName() + " processed in " + (System.nanoTime() - nano) * 1E-6 + " ms.");
    }

    private static void performAttack(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof EntityLiving) {
            if (event.getEntity().hasMetadata("NPC"))
                return;
            EntityLiving entity = (EntityLiving) event.getEntity();
            EntityLiving source = null;
            boolean bow = false;
            if (event.getDamager() instanceof EntityLiving)
                source = ((EntityLiving) event.getDamager());
            if (event.getDamager() instanceof EntityProjectile
                    && ((EntityProjectile) event.getDamager()).shootingEntity instanceof EntityLiving) {
                source = (EntityLiving) ((EntityProjectile) event.getDamager()).shootingEntity;
                bow = true;
            }
            if (source == null)
                return;
            // 攻击
            LoreInfo[] info = new LoreInfo[]{LoreParser.getByEntityId(source.getId()),
                    LoreParser.getByEntityId(entity.getId())};
            // 攻击范围
            if (MConfig.Performance.enableAttackRange && (!bow) &&
                    (info[0].getAttackRange() + MConfig.General.baseAttackRange) * (info[0].getAttackRange() + MConfig.General.baseAttackRange)
                            < source.getLocation().distanceSquared(entity.getLocation())) {
                event.setCancelled(true);
                return;
            }
            // 命中及闪避
            if (Math.random() > (MConfig.General.baseAccuracy + info[0].getAccuracy() - MConfig.General.baseDodge - info[1].getDodge())) {
                event.setCancelled(true);
                if (MConfig.CombatMessage.showOnMiss) {
                    EntityLiving finalSource = source;
                    if (finalSource instanceof Player)
                        MTasks.execute(() -> ((Player) finalSource).sendMessage(String.format(MConfig.CombatMessage.onMiss, entity.getName())));
                }
                if (MConfig.CombatMessage.showOnDodge) {
                    EntityLiving finalSource = source;
                    if (finalSource instanceof Player)
                        MTasks.execute(() -> ((Player) finalSource).sendMessage(String.format(MConfig.CombatMessage.onDodge, finalSource.getName())));
                }
                return;
            }
            // 会心一击
            // 设置伤害
            if (Math.random() < info[0].getSuddenDeath()) {
                event.setDamage(entity.getHealth());
                if (MConfig.CombatMessage.showOnSuddenDeath) {
                    EntityLiving finalSource = source;
                    if (finalSource instanceof Player)
                        MTasks.execute(() -> ((Player) finalSource).sendMessage(String.format(MConfig.CombatMessage.onSuddenDeath, entity.getName()
                                , event.getDamage())));
                }
                return;
            } else {
                event.setDamage((float) LoreCalculator.finalDamage(event.getDamage(), info[0], info[1], source, entity, bow));
            }
            if (MConfig.CombatMessage.showOnDamage) {
                EntityLiving finalSource = source;
                if (finalSource instanceof Player)
                    MTasks.execute(() -> ((Player) finalSource).sendMessage(String.format(MConfig.CombatMessage.onDamage, entity.getName()
                            , event.getDamage())));
            }
            // 反弹
            {
                double health = source.getHealth(), prev = health;
                health = health - LoreCalculator.finalReflect(event.getDamage(), info[1]);
                if (health < 0) health = 0;
                source.setHealth((float) health);
                if (MConfig.CombatMessage.showOnReflect && (prev - health) > 1E-6) {
                    EntityLiving finalSource = source;
                    double finalHealth = health;
                    if (entity instanceof Player)
                        MTasks.execute(() -> ((Player) entity).sendMessage(String.format(MConfig.CombatMessage.onReflect, prev - finalHealth,
                                finalSource.getName())));
                }
            }
            // 吸血
            {
                double health = source.getHealth(), prev = health;
                health += info[0].getLifeSteal() * event.getDamage();
                if (health > source.getMaxHealth())
                    health = source.getMaxHealth();
                source.setHealth((float) health);
                if (MConfig.CombatMessage.showOnLifeSteal && (health - prev) > 1E-6) {
                    EntityLiving finalSource = source;
                    double finalHealth = health;
                    if (finalSource instanceof Player)
                        MTasks.execute(() -> ((Player) finalSource).sendMessage(String.format(MConfig.CombatMessage.onLifeSteal, entity.getName(),
                                finalHealth - prev)));
                }
            }
        }
    }

    public static void init() {
        Server.getInstance().getPluginManager().registerEvents(new BattleListener(), Mesmerize.instance);
    }

}
