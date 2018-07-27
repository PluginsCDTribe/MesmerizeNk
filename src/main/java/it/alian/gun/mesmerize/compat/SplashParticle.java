package it.alian.gun.mesmerize.compat;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.Vector3;
import it.alian.gun.mesmerize.MConfig;
import it.alian.gun.mesmerize.Mesmerize;
import it.alian.gun.mesmerize.util.Collections;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class SplashParticle implements Listener {

    private static SplashParticle impl;

    private static List<Particle> effects;

    public abstract void generateParticles(Entity entity);

    public static void init() {
        effects = Arrays.stream(MConfig.Misc.splashParticles).map(s -> {
            try {
                Class<?> clazz = Class.forName("cn.nukkit.level.particle." + s + "Particle");
                Constructor<?> constructor = clazz.getConstructor(Vector3.class);
                Vector3 vec = new Vector3(0.5f, 0.5f, 0.5f);
                Object o = constructor.newInstance(vec);
                if (o instanceof Particle) return ((Particle) o);
                else return null;
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        if (MConfig.Misc.enableSplashParticles) {
            impl = new PlayEffectImpl();
            Server.getInstance().getPluginManager().registerEvents(impl, Mesmerize.instance);
        }
    }

    private static class PlayEffectImpl extends SplashParticle {

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            EntityDamageEvent cause = event.getEntity().getLastDamageCause();
            if (cause instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) cause).getDamager() instanceof Player)
                generateParticles(event.getEntity());
        }

        @Override
        public void generateParticles(Entity entity) {
            if (entity instanceof EntityLiving && entity.getLastDamageCause() != null && entity.getLastDamageCause() instanceof EntityDamageByEntityEvent
                    && ((EntityDamageByEntityEvent) entity.getLastDamageCause()).getDamager() instanceof Player) {
                Particle particle = Collections.random(effects);
                particle.x = entity.x;
                particle.y = entity.y + 1.0;
                particle.z = entity.z;
                entity.getLevel().addParticle(particle);
            }
        }
    }


}
