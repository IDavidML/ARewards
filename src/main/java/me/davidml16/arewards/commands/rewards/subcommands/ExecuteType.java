package me.davidml16.arewards.commands.rewards.subcommands;

import me.davidml16.arewards.Constants;
import me.davidml16.arewards.Main;
import me.davidml16.arewards.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.*;

public class ExecuteType {

    private List<String> templates;

    private Main main;
    public ExecuteType(Main main) {
        this.main = main;
        this.templates = main.getTemplates();
    }

    public boolean executeCommand(CommandSender sender, String label, String[] args) {
        if(sender instanceof Player) {
            if (!main.playerHasPermission((Player) sender, Constants.ADMIN_PERMISSION)) {
                sender.sendMessage(main.getLanguageHandler().getMessage("Commands.NoPerms"));
                return false;
            }
        }

        if(args.length == 1) {
            sender.sendMessage("");
            sender.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix() + " &cUsage: /" + label + " type create [id] [name]"));
            sender.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix() + " &cUsage: /" + label + " type remove [id]"));
            sender.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix() + " &cUsage: /" + label + " type template [name]"));
            sender.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix() + " &cUsage: /" + label + " type list"));
            sender.sendMessage("");
            return false;
        }

        if(args[1].equalsIgnoreCase("list")) {
            if(sender instanceof Player)
                //main.getTypeListGUI().open((Player) sender);
            return true;
        } else if(args[1].equalsIgnoreCase("create")) {

             if (args.length <= 3) {
                sender.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix() + " &cUsage: /" + label + " type create [id] [name]"));
                return false;
            }

            String id = args[2].toLowerCase();

            StringBuilder name = new StringBuilder();
            for(int i = 3; i < args.length; i++) {
                name.append(args[i]).append(args.length - 1 > i ? " " : "");
            }

            if (main.getRewardTypeHandler().typeExist(id)) {
                sender.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix() + " &cThis " + label + " type already exists!"));
                return true;
            }

            if (!Character.isDigit(id.charAt(0))) {
                if (main.getRewardTypeHandler().createType(id)) {
                    FileConfiguration config = main.getRewardTypeHandler().getConfig(id);
                    config.set("type.name", name.toString());

                    main.getRewardTypeHandler().saveConfig(id);

                    main.getPluginHandler().reloadAll();

                    sender.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix()
                            + " &aSuccesfully created " + label + " type &e" + id + " &awith the name &e" + name));
                    return true;
                }
            } else {
                sender.sendMessage(Utils.translate(
                        main.getLanguageHandler().getPrefix() + " &cThe " + label + " type cannot start with a number, use for example 'normal'."));
                return false;
            }
        } else if(args[1].equalsIgnoreCase("remove")) {
            if (args.length < 3) {
                sender.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix() + " &cUsage: /" + label + " type remove [id]"));
                return false;
            }

            String id = args[2].toLowerCase();

            if (!main.getRewardTypeHandler().typeExist(id)) {
                sender.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix() + " &cThis " + label + " type no exists!"));
                return true;
            }

            if (!Character.isDigit(id.charAt(0))) {
                if (main.getRewardTypeHandler().removeType(id)) {
                    main.getSetupGUI().getGuis().remove(id);
                    main.getEditRewardsGUI().getGuis().remove(id);
                    main.getRewardTypeHandler().getTypes().remove(id);

                    try {
                        main.getDatabaseHandler().removeRewardsCollected(id);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                        main.getPlayerDataHandler().getData(p).getRewards().removeIf(rewardCollected -> {
                            return rewardCollected.getRewardID().equalsIgnoreCase(id);
                        });
                        if(main.getRewardsGUI().getOpened().contains(p.getUniqueId())) p.closeInventory();
                        if(main.getSetupGUI().getOpened().containsKey(p.getUniqueId())) p.closeInventory();
                        if(main.getEditRewardsGUI().getOpened().containsKey(p.getUniqueId())) p.closeInventory();
                        if(main.getEditRewardItemsGUI().getOpened().containsKey(p.getUniqueId())) p.closeInventory();
                    }

                    sender.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix() + " &aSuccesfully removed " + label + " type &e" + id + "&a!"));
                    return true;
                }
            } else {
                sender.sendMessage(Utils.translate(
                        main.getLanguageHandler().getPrefix() + " &cThe " + label + " type cannot start with a number, use for example 'normal'."));
                return false;
            }
        } else if(args[1].equalsIgnoreCase("template")) {
            if (args.length < 3) {
                sender.sendMessage("");
                sender.sendMessage(Utils.translate(" &cUsage: /" + label + " type template [name]"));
                sender.sendMessage("");
                sender.sendMessage(Utils.translate(" &aAvailable templates: &e" + templates));
                sender.sendMessage("");
                return false;
            }

            String template = args[2].toLowerCase();

            if(!template.equalsIgnoreCase("*")) {

                if (!templates.contains(template.toLowerCase())) {
                    sender.sendMessage("");
                    sender.sendMessage(Utils.translate(" &cThis template not exists"));
                    sender.sendMessage("");
                    sender.sendMessage(Utils.translate(" &aAvailable templates: &e" + templates));
                    sender.sendMessage("");
                    return false;
                }

                sender.sendMessage("");
                donwloadTemplate(sender, template);

            } else {

                sender.sendMessage("");
                for(String temp : templates) donwloadTemplate(sender, temp);

            }

            sender.sendMessage("");
            sender.sendMessage(Utils.translate(" &aPlease use /" + label + " reload, to load it"));
            sender.sendMessage("");

        }
        return true;
    }

    public void donwloadTemplate(CommandSender sender, String template) {
        File file = new File(main.getDataFolder() + "/types/" + template + ".yml");

        if(file.exists())
            file.delete();

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        Map<String, Object> msgDefaults = new LinkedHashMap<String, Object>();

        InputStreamReader input = new InputStreamReader(main.getResource("type_templates/" + template + ".yml"));
        FileConfiguration data = YamlConfiguration.loadConfiguration(input);

        for(String key : data.getKeys(true)) {
            if(!(data.get(key) instanceof MemorySection)) {
                msgDefaults.put(key, data.get(key));
            }
        }

        for (String key : msgDefaults.keySet()) {
            if (!cfg.isSet(key)) {
                cfg.set(key, msgDefaults.get(key));
            }
        }

        for(String key : cfg.getKeys(true)) {
            if(!(cfg.get(key) instanceof MemorySection)) {
                if (!data.isSet(key)) {
                    cfg.set(key, null);
                }
            }
        }

        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sender.sendMessage(Utils.translate(" &aTemplate '&6" + template + "&a' has been downloaded successfully"));

    }

}
