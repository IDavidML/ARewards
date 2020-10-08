package me.davidml16.arewards.commands.rewards.subcommands;

import me.davidml16.arewards.Constants;
import me.davidml16.arewards.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExecuteReload {

    private Main main;
    public ExecuteReload(Main main) {
        this.main = main;
    }

    public boolean executeCommand(CommandSender sender, String label, String[] args) {
        if ((sender instanceof Player)) {
            if (!main.playerHasPermission((Player) sender, Constants.ADMIN_PERMISSION)) {
                sender.sendMessage(main.getLanguageHandler().getMessage("Commands.NoPerms"));
                return false;
            }
        }

        main.getPluginHandler().reloadAll();
        sender.sendMessage(main.getLanguageHandler().getMessage("Commands.Reload"));

        return true;
    }

}
