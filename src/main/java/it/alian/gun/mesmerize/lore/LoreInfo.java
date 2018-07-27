package it.alian.gun.mesmerize.lore;

import it.alian.gun.mesmerize.MConfig;

import java.lang.reflect.Field;

public class LoreInfo {

    double damage, playerDamage, entityDamage, bowDamage, realDamage;
    boolean isCritical, isReflect = true, isLifeSteal = true;
    double criticalChance, lifeSteal, lifeStealChance, reflectChance;
    double criticalDamage;
    double defense, playerDefense, entityDefense, bowDefense;
    double reflect, meleeReflect, rangeReflect;
    double health, regeneration, attackExpModifier, otherExpModifier, moveSpeed, attackSpeed, flySpeed;
    double attackRange, suddenDeath;
    double accuracy, dodge;

    public static LoreInfo merge(LoreInfo info, LoreInfo info2) {
        try {
            for (Field field : LoreInfo.class.getDeclaredFields()) {
                if ((field.getType() == double.class || field.getType() == Double.class)) {
                    field.setAccessible(true);
                    if (((MConfig.Stats) MConfig.Prefixes.class.getDeclaredField(field.getName()).get(null)).isSumUp()) {
                        field.set(info, (double) field.get(info) + (double) field.get(info2));
                    } else {
                        field.set(info, Math.max((double) field.get(info), (double) field.get(info2)));
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return info;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getDodge() {
        return dodge;
    }

    public double getCriticalChance() {
        return criticalChance;
    }

    public double getLifeStealChance() {
        return lifeStealChance;
    }

    public double getReflectChance() {
        return reflectChance;
    }

    public double getAttackRange() {
        return attackRange;
    }

    public double getSuddenDeath() {
        return suddenDeath;
    }

    public boolean isLifeSteal() {
        return isLifeSteal;
    }

    public static LoreInfo empty() {
        return new LoreInfo();
    }

    public double getLifeSteal() {
        return lifeSteal;
    }

    public boolean isReflect() {
        return isReflect;
    }

    public double getReflect() {
        return reflect;
    }

    public double getMeleeReflect() {
        return meleeReflect;
    }

    public double getRangeReflect() {
        return rangeReflect;
    }

    public double getCriticalDamage() {
        return criticalDamage;
    }

    public double getDamage() {
        return damage;
    }

    public double getPlayerDamage() {
        return playerDamage;
    }

    public double getEntityDamage() {
        return entityDamage;
    }

    public double getBowDamage() {
        return bowDamage;
    }

    public double getRealDamage() {
        return realDamage;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public double getDefense() {
        return defense;
    }

    public double getPlayerDefense() {
        return playerDefense;
    }

    public double getEntityDefense() {
        return entityDefense;
    }

    public double getBowDefense() {
        return bowDefense;
    }

    public double getHealth() {
        return health;
    }

    public double getRegeneration() {
        return regeneration;
    }

    public double getAttackExpModifier() {
        return attackExpModifier;
    }

    public double getOtherExpModifier() {
        return otherExpModifier;
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public double getFlySpeed() {
        return flySpeed;
    }
}
