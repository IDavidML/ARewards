package me.davidml16.arewards.commands.rewards.subcommands;

import me.davidml16.arewards.Constants;
import me.davidml16.arewards.Main;
import me.davidml16.arewards.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExecuteSetup {

    private Main main;
    public ExecuteSetup(Main main) {
        this.main = main;
    }

    public boolean executeCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate("&cThe commands only can be use by players!"));
            return true;
        }

        if (!main.playerHasPermission((Player) sender, Constants.ADMIN_PERMISSION)) {
            sender.sendMessage(main.getLanguageHandler().getMessage("Commands.NoPerms"));
            return false;
        }

        if (args.length == 1) {
            sender.sendMessage("");
            sender.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix() + " &cUsage: /" + label + " setup [typeID]"));
            sender.sendMessage("");
            return true;
        }

        String id = args[1].toLowerCase();
        if (!main.getRewardTypeHandler().getTypes().containsKey(id)) {
            sender.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix() + " &cThis " + label + " type doesn't exists!"));
            return true;
        }

        main.getSetupGUI().open((Player) sender, id);

        return true;
    }

}
