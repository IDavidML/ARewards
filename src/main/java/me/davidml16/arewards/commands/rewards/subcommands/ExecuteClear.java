package me.davidml16.arewards.commands.rewards.subcommands;

import me.davidml16.arewards.Constants;
import me.davidml16.arewards.Main;
import me.davidml16.arewards.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

public class ExecuteClear {

    private Main main;
    public ExecuteClear(Main main) {
        this.main = main;
    }

    public boolean executeCommand(CommandSender sender, String label, String[] args) {
        if(sender instanceof Player) {
            if (!main.playerHasPermission((Player) sender, Constants.ADMIN_PERMISSION)) {
                sender.sendMessage(main.getLanguageHandler().getMessage("Commands.NoPerms"));
                return false;
            }
        }

        if (args.length == 1) {
            sender.sendMessage("");
            sender.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix() + " &cUsage: /" + label + " clear [player]"));
            sender.sendMessage("");
            return true;
        }

        String player = args[1];

        try {
            if(!main.getDatabaseHandler().hasName(player)) {
                sender.sendMessage(Utils.translate(
                        main.getLanguageHandler().getPrefix() + " &cThis player not exists in the database!"));
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if(Bukkit.getPlayer(player) == null) {
            try {
                UUID uuid = UUID.fromString(main.getDatabaseHandler().getPlayerUUID(player));
                main.getDatabaseHandler().removeRewardsCollected(uuid);

                String msg = main.getLanguageHandler().getMessage("Commands.Clear");
                msg = msg.replaceAll("%player%", player);
                sender.sendMessage(Utils.translate(msg));

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            Player target = Bukkit.getPlayer(player);

            main.getPlayerDataHandler().getData(target).getRewards().clear();

            String msg = main.getLanguageHandler().getMessage("Commands.Clear");
            msg = msg.replaceAll("%player%", player);
            sender.sendMessage(Utils.translate(msg));

            if (main.getRewardsGUI().getOpened().contains(target.getUniqueId())) main.getRewardsGUI().reloadPage(target);

            if(main.getHologramHandler().getImplementation() != null)
                main.getHologramHandler().getImplementation().reloadHolograms(target);

            try {
                main.getDatabaseHandler().removeRewardsCollected(target.getUniqueId());
            } catch (SQLException ignored) { }

        }

        return true;
    }

}
