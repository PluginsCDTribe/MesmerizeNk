package it.alian.gun.mesmerize.compat;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Equipment {

    private static Equipment instance;

    public static void init() {
        instance = new Impl_1_8();
    }

    public static List<Item> collect(Entity entity) {
        return instance.collectOf(entity);
    }

    abstract List<Item> collectOf(Entity player);

    private static class Impl_1_8 extends Equipment {

        @Override
        List<Item> collectOf(Entity player) {
            if (player instanceof Player) {
                List<Item> rev = new ArrayList<>(Arrays.asList(((Player) player).getInventory().getArmorContents()));
                rev.add(((Player) player).getInventory().getItemInHand());
                return rev;
            }
            return new ArrayList<>();
        }
    }

}
