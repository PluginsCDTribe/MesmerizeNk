package it.alian.gun.mesmerize;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.item.Item;
import com.google.common.collect.Lists;
import it.alian.gun.mesmerize.lore.LoreInfo;
import it.alian.gun.mesmerize.lore.LoreParser;
import it.alian.gun.mesmerize.util.ChatColor;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class MCommand extends VanillaCommand {

    private static final DecimalFormat format = new DecimalFormat(MConfig.Misc.customDecimalFormat);

    public MCommand() {
        super("mesmerize", "", null, new String[]{"mes"});
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        // Show stats
        if (args.length >= 1 && args[0].equalsIgnoreCase("lore") && sender instanceof Player && sender.hasPermission("mesmerize.lore")) {
            if (handleLore(((Player) sender), args)) return true;
            else {
                loreHelp(((Player) sender));
                return true;
            }
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("stats") && sender instanceof Player &&
                sender.hasPermission("mesmerize.showstats")) {
            MLocale.COMMAND_SHOW_STATS.player(sender, sender.getName());
            MTasks.execute(() -> {
                LoreInfo info = new LoreParser.ParseEntityTask((Player) sender).call();
                for (Field field : info.getClass().getDeclaredFields()) {
                    try {
                        field.setAccessible(true);
                        String value = format.format(field.get(info));
                        MConfig.Stats stats = (MConfig.Stats) MConfig.Prefixes.class.getDeclaredField(field.getName()).get(null);
                        sender.sendMessage(stats.getColor() + stats.getName() + ": " + value);
                    } catch (Exception ignored) {
                    }
                }
            });
            return true;
        }
        // Evaluate item
        if (args.length == 1 && args[0].equalsIgnoreCase("evaluate") && sender instanceof Player &&
                sender.hasPermission("mesmerize.evaluate")) {
            MTasks.execute(MCommand.evaluateOrSell(false, (Player) sender));
            return true;
        }
        // Sell item
        if (args.length == 1 && args[0].equalsIgnoreCase("sell") && sender instanceof Player &&
                sender.hasPermission("mesmerize.sell")) {
            MTasks.execute(MCommand.evaluateOrSell(true, (Player) sender));
            return true;
        }
        // Config save load
        if (args.length >= 2 && args[0].equalsIgnoreCase("config") && sender.hasPermission("mesmerize.config")) {
            MTasks.execute(() -> {
                if (args[1].equalsIgnoreCase("reload")) {
                    MConfig.load();
                    MConfig.save();
                    MLocale.GENERAL_CONFIG_LOAD.player(sender);
                    return;
                }
                if (args[1].equalsIgnoreCase("load")) {
                    MConfig.load();
                    MLocale.GENERAL_CONFIG_LOAD.player(sender);
                    return;
                }
                if (args[1].equalsIgnoreCase("save")) {
                    MConfig.save();
                    MLocale.GENERAL_CONFIG_SAVE.player(sender);
                    return;
                }
                if (args.length == 4 && args[1].equalsIgnoreCase("set")) {
                    String[] path = args[2].split("\\.");
                    Object rev = new MConfig();
                    Class clazz = MConfig.class;
                    try {
                        for (int i = 0; i < path.length - 1; i++) {
                            Field field = rev.getClass().getDeclaredField(path[i]);
                            field.setAccessible(true);
                            rev = field.get(rev);
                            clazz = rev.getClass();
                        }
                        Field field = clazz.getDeclaredField(path[path.length - 1]);
                        Object prev = field.get(rev);
                        if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
                            field.set(rev, Integer.parseInt(args[3]));
                        } else if (field.getType().equals(long.class) || field.getType().equals(Long.class)) {
                            field.set(rev, Long.parseLong(args[3]));
                        } else if (field.getType().equals(double.class) || field.getType().equals(Double.class)) {
                            field.set(rev, Double.parseDouble(args[3]));
                        } else if (field.getType().equals(float.class) || field.getType().equals(Float.class)) {
                            field.set(rev, Float.parseFloat(args[3]));
                        } else if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
                            field.set(rev, Boolean.parseBoolean(args[3]));
                        } else
                            field.set(rev, args[3]);
                        MLocale.CONFIG_SET.player(sender, args[2], String.valueOf(prev), args[3]);
                    } catch (ReflectiveOperationException e) {
                        MLocale.WARN_CONFIG_SET.player(sender, args[2]);
                    }
                    return;
                }
                if (args.length == 3 && args[1].equalsIgnoreCase("list")) {
                    String[] path = args[2].split("\\.");
                    Object rev = new MConfig();
                    Class clazz = MConfig.class;
                    try {
                        for (String aPath : path) {
                            Field field = rev.getClass().getDeclaredField(aPath);
                            field.setAccessible(true);
                            rev = field.get(rev);
                            clazz = rev.getClass();
                        }
                        Field[] fields = clazz.getDeclaredFields();
                        MLocale.CONFIG_LIST.player(sender, args[2], String.valueOf(fields.length));
                        Yaml yaml = new Yaml();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            if (!Modifier.isTransient(field.getModifiers())) {
                                if (field.getType().getName().contains(".") && field.getType().getName().contains("$"))
                                    MLocale.CONFIG_LIST_1.player(sender, field.getName());
                                else
                                    MLocale.CONFIG_LIST_2.player(sender, field.getName(),
                                            field.getType().getName().substring(field.getType().getName().lastIndexOf('.') + 1),
                                            yaml.dump(field.get(rev)).replace("\n", "").replace("\r", ""));
                            }
                        }
                    } catch (ReflectiveOperationException e) {
                        MLocale.WARN_CONFIG_SET.player(sender, args[2]);
                    }
                    return;
                }
                help(sender);
            });
            return true;
        }
        help(sender);
        return true;
    }

    private static void help(CommandSender sender) {
        MLocale.COMMAND_ERROR.player(sender);
        for (String s : MLocale.COMMAND_HELP_LIST.msg().split("\n")) {
            sender.sendMessage(s);
        }
    }

    private static boolean handleLore(Player player, String[] args) {
        if (args.length < 2) {
            return false;
        } else {
            Item item = player.getInventory().getItemInHand();
            if (item == null) return false;
            List<String> lore = item.getLore() == null ? Lists.newArrayList() : Lists.newArrayList(Arrays.asList(item.getLore()));
            switch (args[1].toLowerCase()) {
                case "add":
                    if (args.length < 3) {
                        return false;
                    } else {
                        StringJoiner joiner = new StringJoiner(" ");
                        for (int i = 2; i < args.length; i++) {
                            joiner.add(args[i]);
                        }
                        lore.add(ChatColor.translateAlternateColorCodes('&', joiner.toString()));
                        item.setLore(lore.toArray(new String[0]));
                        player.getInventory().setItemInHand(item);
                        return true;
                    }
                case "delete":
                    if (args.length != 3) {
                        return false;
                    } else {
                        try {
                            int i = Integer.parseInt(args[2]);
                            lore.remove(i + 1);
                            item.setLore(lore.toArray(new String[0]));
                            player.getInventory().setItemInHand(item);
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    }
                case "set":
                    if (args.length < 4) {
                        return false;
                    } else {
                        try {
                            int index = Integer.parseInt(args[2]);
                            StringJoiner joiner = new StringJoiner(" ");
                            for (int i = 3; i < args.length; i++) {
                                joiner.add(args[i]);
                            }
                            if (lore.size() < index)
                                for (int i = 0; i < index - lore.size(); i++) {
                                    lore.add("");
                                }
                            lore.set(index + 1, ChatColor.translateAlternateColorCodes('&', joiner.toString()));
                            item.setLore(lore.toArray(new String[0]));
                            player.getInventory().setItemInHand(item);
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    }
                case "insert":
                    if (args.length < 4) {
                        return false;
                    } else {
                        try {
                            int index = Integer.parseInt(args[2]);
                            StringJoiner joiner = new StringJoiner(" ");
                            for (int i = 3; i < args.length; i++) {
                                joiner.add(args[i]);
                            }
                            lore.add(index, ChatColor.translateAlternateColorCodes('&', joiner.toString()));
                            item.setLore(lore.toArray(new String[0]));
                            player.getInventory().setItemInHand(item);
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    }
                default:
                    return false;
            }
        }
    }

    private static void loreHelp(Player player) {
        for (String s : MLocale.LORE_HELP.msg().split("\n")) {
            player.sendMessage(s);
        }
    }

    public static void init() {
        MCommand instance = new MCommand();
        Server.getInstance().getCommandMap().register("mes", instance);
    }

    private static Runnable evaluateOrSell(boolean sell, Player sender) {
        return () -> {
            LoreInfo info = LoreParser.parseSingleItem(sender.getInventory().getItemInHand());
            double price = 0;
            for (Field field : info.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    double value = (double) field.get(info);
                    MConfig.Stats stats = (MConfig.Stats) MConfig.Prefixes.class.getDeclaredField(field.getName()).get(null);
                    price += stats.getValuePerPercentage() * value * 100D;
                } catch (Throwable ignored) {
                }
            }
            price *= sender.getInventory().getItemInHand().getCount();
            /*
            if (!sell)
            */
            sender.sendMessage(String.format(MConfig.Message.onPriceEvaluate, price));
            /*
            else if (MesmerizeVault.give(sender, price)) {
                sender.getEquipment().setItemInHand(new ItemStack(Material.AIR));
                sender.sendMessage(String.format(MConfig.Message.onItemSell, price));
            }
            */
        };
    }

}
