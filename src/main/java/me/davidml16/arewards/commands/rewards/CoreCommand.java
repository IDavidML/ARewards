package me.davidml16.arewards.commands.rewards;

import me.davidml16.arewards.Constants;
import me.davidml16.arewards.Main;
import me.davidml16.arewards.commands.rewards.subcommands.*;
import me.davidml16.arewards.objects.Profile;
import me.davidml16.arewards.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CoreCommand extends Command {

    private final Main main = Main.get();

    private final ExecuteChest executeChest = new ExecuteChest(main);
    private final ExecuteType executeType = new ExecuteType(main);
    private final ExecuteReload executeReload = new ExecuteReload(main);
    private final ExecuteSetup executeSetup = new ExecuteSetup(main);
    private final ExecuteClear executeClear = new ExecuteClear(main);
    private final ExecuteMenu executeMenu = new ExecuteMenu(main);

    public CoreCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {

        if(args.length == 0) {
            if(sender instanceof Player) {

                Profile profile = main.getPlayerDataHandler().getData((Player) sender);
                int available = profile.getAvailableRewards();

                sender.sendMessage(main.getLanguageHandler().getMessage("Commands.Balance")
                        .replaceAll("%rewards_available%", ""+profile.getAvailableRewards())
                        .replaceAll("%rewards_string%", available != 1 ? main.getLanguageHandler().getMessage("Strings.Rewards") : main.getLanguageHandler().getMessage("Strings.Reward")));
                return true;
            } else {
                return sendCommandHelp(sender, label);
            }
        }

        if(sender instanceof Player) {
            if(main.getGuiHandler().haveConversation((Player) sender)) return true;
        }

        switch (args[0]) {
            case "menu":
                return executeMenu.executeCommand(sender, label, args);
            case "help":
                return sendCommandHelp(sender, label);
            case "chest":
                return executeChest.executeCommand(sender, label, args);
            case "type":
                return executeType.executeCommand(sender, label, args);
            case "setup":
                return executeSetup.executeCommand(sender, label, args);
            case "reload":
                return executeReload.executeCommand(sender, label, args);
            case "clear":
                return executeClear.executeCommand(sender, label, args);
        }

        sender.sendMessage("");
        sender.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix() + " &cInvalid argument, use /" + label + " help to see available commands"));
        sender.sendMessage("");
        return false;
    }

    private boolean sendCommandHelp(CommandSender sender, String label) {
        if(sender instanceof Player) {

            if (main.playerHasPermission((Player) sender, Constants.OPEN_MENU_PERMISSION) || main.playerHasPermission((Player) sender, Constants.ADMIN_PERMISSION)) {
                sender.sendMessage("");
                sender.sendMessage(Utils.translate("&7 - &a/" + label + " menu"));
                sender.sendMessage("");
            }

            if (main.playerHasPermission((Player) sender, Constants.ADMIN_PERMISSION)) {
                sender.sendMessage(Utils.translate("&7 - &a/" + label + " clear [player]"));
                sender.sendMessage("");
                sender.sendMessage(Utils.translate("&7 - &a/" + label + " chest [create/remove/edit]"));
                sender.sendMessage("");
                sender.sendMessage(Utils.translate("&7 - &a/" + label + " type"));
                sender.sendMessage(Utils.translate("&7 - &a/" + label + " setup [typeID]"));
                sender.sendMessage("");
                sender.sendMessage(Utils.translate("&7 - &a/" + label + " reload"));
                sender.sendMessage("");
            }

        } else {
            sender.sendMessage("");
            sender.sendMessage(Utils.translate("&7 - &a/" + label + " menu"));
            sender.sendMessage("");
            sender.sendMessage(Utils.translate("&7 - &a/" + label + " clear [player]"));
            sender.sendMessage("");
            sender.sendMessage(Utils.translate("&7 - &a/" + label + " chest [create/remove/edit]"));
            sender.sendMessage("");
            sender.sendMessage(Utils.translate("&7 - &a/" + label + " type"));
            sender.sendMessage(Utils.translate("&7 - &a/" + label + " setup [typeID]"));
            sender.sendMessage("");
            sender.sendMessage(Utils.translate("&7 - &a/" + label + " reload"));
            sender.sendMessage("");
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        Player p = (Player) sender;

        List<String> list = new ArrayList<String>();
        List<String> auto = new ArrayList<String>();

        if (args.length == 1) {
            if (main.playerHasPermission(p, Constants.OPEN_MENU_PERMISSION) || main.playerHasPermission(p, Constants.ADMIN_PERMISSION)) {
                list.add("menu");
                list.add("help");
            }

            if (main.playerHasPermission(p, Constants.ADMIN_PERMISSION)) {
                list.add("clear");
                list.add("chest");
                list.add("type");
                list.add("setup");
                list.add("reload");
            }
        }

        if (args[0].equalsIgnoreCase("clear")) {
            if (args.length == 2) {
                if (main.playerHasPermission(p, Constants.ADMIN_PERMISSION)) {
                    for (Player target : main.getServer().getOnlinePlayers()) {
                        list.add(target.getName());
                    }
                }
            }
        } else if (args[0].equalsIgnoreCase("setup")) {
            if(args.length == 2) {
                if (main.playerHasPermission(p, Constants.ADMIN_PERMISSION)) {
                    for (File file : Objects.requireNonNull(new File(main.getDataFolder(), "types").listFiles())) {
                        list.add(file.getName().replace(".yml", ""));
                    }
                }
            }
        } else if (args[0].equalsIgnoreCase("chest")) {
            if(args.length == 2) {
                if (main.playerHasPermission(p, Constants.ADMIN_PERMISSION)) {
                    list.add("create");
                    list.add("remove");
                    list.add("edit");
                }
            }
        } else if (args[0].equalsIgnoreCase("type")) {
            if (main.playerHasPermission(p, Constants.ADMIN_PERMISSION)) {
                if(args.length == 2) {
                    list.add("create");
                    list.add("remove");
                    list.add("template");
                    list.add("list");
                } else if(args.length == 3) {
                    if(args[1].equalsIgnoreCase("remove")) {
                        for (String type : main.getRewardTypeHandler().getTypes().keySet()) {
                            list.add(type.toLowerCase());
                        }
                    } else if(args[1].equalsIgnoreCase("template")) {
                        list.addAll(main.getTemplates());
                        list.add("*");
                    }
                }
            }
        }

        for (String s : list) {
            if (s.startsWith(args[args.length - 1])) {
                auto.add(s);
            }
        }

        return auto.isEmpty() ? list : auto;
    }

}