package it.alian.gun.mesmerize;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;

import java.io.File;

public enum MLocale {

    PREFIX("§3[Mesmerize] §f"),
    GENERAL_LOAD("§e在 {0} 秒内加载完毕。"),
    WARN_ERROR_OCCUR("§c发生了错误: {0}"),
    GENERAL_DISABLE("§e在 {0} 秒内卸载完毕。"),
    CANNOT_CONSOLE_USE("§c不能在控制台使用此命令。"),
    USAGE("§e使用 §a{0} §e查看帮助。"),
    PERMISSION_DENIED("§c你没有权限这么做！"),
    GENERAL_HOOK("§a成功启用和 {0} 的交互功能。"),
    WARN_DEPENDENCY_MISSING("§c缺少 {0} 前置，插件将不能运行！"),
    GENERAL_VERSION("§a运行于 Nukkit {0}，Mesmerize 版本 {1}"),
    COMMAND_SHOW_STATS("§b----- {0} 模拟攻击的结果如下 -----"),
    COMMAND_ERROR("§c指令错误或指令格式错误，或没有权限执行。"),
    ERROR_LOADING_CUSTOM_SCRIPT("§c加载自定义伤害计算式失败，将使用默认的伤害计算。"),
    GENERAL_CUSTOM_SCRIPT_LOAD("§a成功加载了自定义伤害计算式。"),
    GENERAL_CONFIG_LOAD("§a配置文件加载完成。"),
    WARN_CONFIG_LOAD("§c配置文件加载失败。"),
    WARN_CONFIG_SET("§c配置 {0} 设置失败，请手动在 config.yml 中设置！"),
    CONFIG_SET("§a配置项 {0} 由 {1} 更改为 {2}，使用 /mes save 保存。"),
    GENERAL_CONFIG_SAVE("§a配置文件已保存。"),
    CONFIG_LIST("§a配置项 {0} 包含以下 {1} 个配置项："),
    CONFIG_LIST_1("§e  名称 {0}  §b可以继续列出"),
    CONFIG_LIST_2("§e  名称 {0}  §b类型 {1}  §a值 {2}"),
    UPDATER_HEADER("§aMesmerize 有新的版本 {0} ，发布于 {1} ，更新内容："),
    UPDATER_BODY("§a    {0}"),
    UPDATER_FOOTER("§a前往 {0} 下载。"),
    COMMAND_HELP_LIST("§a===== Mesmerize 帮助列表 =====\n" +
            "§3/mes stats §f- §e模拟一次攻击后解析的属性，用于评估自身属性\n" +
            "§3/mes evaluate §f- §e评估手中物品的价值\n" +
            "§3/mes config load §f- §e从磁盘加载配置\n" +
            "§3/mes config save §f- §e保存现在内存中的配置\n" +
            "§3/mes config reload §f- §e加载并保存配置\n" +
            "§3/mes config set <配置项> <配置值> §f- §e设置一个配置的值\n" +
            "§3/mes config list <配置项> §f- §e列出一个配置项中所有子项的值，使用 §3/mes config list . §e列出默认的\n" +
            "§3/mes sell §f- §e以评估的价格卖出手中物品"),
    LORE_HELP("§a用法 /mes lore <方法> <参数>...\n" +
            "§3/mes lore add <文本> §f- §e添加一行 lore\n" +
            "§3/mes lore set <行数> <文本> §f- §e设置某行的 lore 为指定的文本\n" +
            "§3/mes lore delete <行数> §f- §e删除某行的 lore\n" +
            "§3/mes lore insert <行数> <文本> §f- §e在某行§c后§e插入文本");

    private String msg, default_;

    MLocale(String default_) {
        this.default_ = default_;
        this.msg = this.default_;
    }

    public String msg() {
        return msg;
    }

    public String msg(String... replace) {
        String temp = msg;
        for (int i = 0; i < replace.length; i++) {
            temp = temp.replace("{" + i + "}", replace[i]);
        }
        return temp;
    }

    public void broadcast(String... replace) {
        String temp = msg;
        for (int i = 0; i < replace.length; i++) {
            temp = temp.replace("{" + i + "}", replace[i]);
        }
        Server.getInstance().broadcastMessage(MLocale.prefix() + temp);
    }

    public void broadcast() {
        Server.getInstance().broadcastMessage(MLocale.prefix() + this.msg);
    }

    public void console(String... replace) {
        String temp = msg;
        for (int i = 0; i < replace.length; i++) {
            temp = temp.replace("{" + i + "}", replace[i]);
        }
        Server.getInstance().getConsoleSender().sendMessage(MLocale.prefix() + temp);
    }

    public void player(CommandSender p, String... replace) {
        String temp = msg;
        for (int i = 0; i < replace.length; i++) {
            temp = temp.replace("{" + i + "}", replace[i]);
        }
        p.sendMessage(MLocale.prefix() + temp);
    }

    public void player(CommandSender p) {
        p.sendMessage(MLocale.prefix() + this.msg);
    }

    public void console() {
        Server.getInstance().getConsoleSender().sendMessage(MLocale.prefix() + this.msg);
    }

    public static void init() {
        if (!new File(Mesmerize.instance.getDataFolder().getAbsolutePath() + "/locale.yml").exists()) {
            Mesmerize.instance.saveResource("locale.yml", true);
        }

        File file = new File(Mesmerize.instance.getDataFolder().getAbsolutePath() + "/locale.yml");
        Config config = new Config(file);
        for (MLocale l : MLocale.values()) {
            if (!config.exists(l.name())) {
                config.set(l.name(), l.default_);
            }
            l.msg = config.getString(l.name());
        }
        config.save(file);
    }

    public static String prefix() {
        return MLocale.PREFIX.msg();
    }
}
