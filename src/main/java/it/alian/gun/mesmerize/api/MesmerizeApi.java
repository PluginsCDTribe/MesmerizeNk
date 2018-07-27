package it.alian.gun.mesmerize.api;

import cn.nukkit.item.Item;
import it.alian.gun.mesmerize.lore.ItemInfo;
import it.alian.gun.mesmerize.lore.LoreInfo;
import it.alian.gun.mesmerize.lore.LoreParser;

public class MesmerizeApi {

    /**
     * 获取玩家的属性信息，不包括手中的物品的特有信息如灵魂绑定等
     *
     * @param id 玩家的 Entity ID
     * @return 屬性信息
     */
    public static LoreInfo getPlayerLoreInfo(int id) {
        return LoreParser.getByEntityId(id);
    }

    /**
     * 获取单个物品的属性信息
     *
     * @param itemStack 物品实例
     * @return 属性信息
     */
    public static LoreInfo getLoreInfo(Item itemStack) {
        return LoreParser.parseSingleItem(itemStack);
    }

    /**
     * 获取物品的独有信息
     *
     * @param itemStack 物品实例
     * @return 物品信息
     */
    public static ItemInfo getItemInfo(Item itemStack) {
        return LoreParser.parseItem(itemStack);
    }

}
