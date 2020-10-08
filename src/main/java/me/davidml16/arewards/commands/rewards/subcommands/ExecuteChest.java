package me.davidml16.arewards.commands.rewards.subcommands;

import me.davidml16.arewards.Constants;
import me.davidml16.arewards.Main;
import me.davidml16.arewards.utils.Utils;
import me.davidml16.arewards.utils.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ExecuteChest {

    private Main main;
    public ExecuteChest(Main main) {
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
            sender.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix() + " &cUsage: /" + label + " chest [create/remove/edit]"));
            sender.sendMessage("");
            return false;
        }

        if (args[1].equalsIgnoreCase("create")) {
            Block block = ((Player)sender).getTargetBlock(null, 5);

            if(block.getType().isBlock() && block.getType() != Material.AIR) {
                if (!main.getRewardChestHandler().getChests().containsKey(block.getLocation())) {

                    Sounds.playSound(((Player)sender), ((Player)sender).getLocation(), Sounds.MySound.ANVIL_USE, 10, 3);

                    sender.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix()
                            + " &aSuccesfully " + label + " new reward chest on" +
                            " &aX: &e" + block.getLocation().getBlockX() +
                            ", &aY: &e" + block.getLocation().getBlockY() +
                            ", &aZ: &e" + block.getLocation().getBlockZ()));

                    main.getRewardChestHandler().createChest(block.getLocation(), 0.8125);
                    return true;
                } else {
                    sender.sendMessage(Utils.translate(
                            main.getLanguageHandler().getPrefix() + " &cThis " + label + " chest location already exists!"));
                    return false;
                }
            } else {
                sender.sendMessage(Utils.translate(
                        main.getLanguageHandler().getPrefix() + " &cA " + label + " chest needs to be a block!"));
                return false;
            }
        }

        if (args[1].equalsIgnoreCase("remove")) {
            Block block = ((Player) sender).getTargetBlock(null, 5);

            if(block.getType().isBlock() && block.getType() != Material.AIR) {
                if (main.getRewardChestHandler().getChests().containsKey(block.getLocation())) {
                    main.getRewardChestHandler().removeChest(block.getLocation());

                    Sounds.playSound(((Player)sender), ((Player)sender).getLocation(), Sounds.MySound.ANVIL_USE, 10, 3);

                    sender.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix()
                            + " &aSuccesfully removed " + label + " chest of" +
                            " &aX: &e" + block.getLocation().getBlockX() +
                            ", &aY: &e" + block.getLocation().getBlockY() +
                            ", &aZ: &e" + block.getLocation().getBlockZ()));
                    return true;
                } else {
                    sender.sendMessage(Utils.translate(
                            main.getLanguageHandler().getPrefix() + " &cThis " + label + " chest location no exists!"));
                    return false;
                }
            } else {
                sender.sendMessage(Utils.translate(
                        main.getLanguageHandler().getPrefix() + " &cA " + label + " chest needs to be a block!"));
                return false;
            }
        }

        if (args[1].equalsIgnoreCase("edit")) {
            Block block = ((Player) sender).getTargetBlock(null, 5);

            if(block.getType().isBlock() && block.getType() != Material.AIR) {
                if (main.getRewardChestHandler().getChests().containsKey(block.getLocation())) {
                    main.getEditChestGUI().open(((Player) sender), main.getRewardChestHandler().getChestByLocation(block.getLocation()));
                    return true;
                } else {
                    sender.sendMessage(Utils.translate(
                            main.getLanguageHandler().getPrefix() + " &cThis " + label + " chest location no exists!"));
                    return false;
                }
            } else {
                sender.sendMessage(Utils.translate(
                        main.getLanguageHandler().getPrefix() + " &cA " + label + " chest needs to be a block!"));
                return false;
            }
        }

        return true;
    }

}
