package it.alian.gun.mesmerize.compat;

import cn.nukkit.item.Item;

public class AttackDamage {

    public static double getAttackDamage(Item itemStack) {
        return itemStack.getAttackDamage();
    }

}
