package me.davidml16.arewards.handlers;

import com.cryptomorin.xseries.XItemStack;
import me.davidml16.arewards.Main;
import me.davidml16.arewards.objects.rewards.*;
import me.davidml16.arewards.objects.*;
import me.davidml16.arewards.utils.Sounds;
import me.davidml16.arewards.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RewardHandler {

	private Main main;
	public RewardHandler(Main main) {
		this.main = main;
	}

	public void loadRewards() {
		for(RewardType rewardType : main.getRewardTypeHandler().getTypes().values()) {
			loadReward(rewardType);
		}
	}

	public void loadReward(RewardType rewardType) {
		FileConfiguration config = main.getRewardTypeHandler().getConfig(rewardType.getId());

		List<Reward> rewards = new ArrayList<>();

		if (config.contains("type.rewards")) {
			if (config.getConfigurationSection("type.rewards") != null) {
				int iterator = 0;
				for (String rewardid : config.getConfigurationSection("type.rewards").getKeys(false)) {
					if(validRewardData(config, rewardid)) {

						String name = config.getString("type.rewards." + rewardid + ".name");

						if(config.contains("type.rewards." + rewardid + ".command")) {

							List<String> commands = new ArrayList<>();
							if (config.get("type.rewards." + rewardid + ".command") instanceof ArrayList)
								commands.addAll(config.getStringList("type.rewards." + rewardid + ".command"));
							else
								commands.add(config.getString("type.rewards." + rewardid + ".command"));

							rewards.add(new CommandReward("reward_" + iterator, name, commands, rewardType));

						} else if(config.contains("type.rewards." + rewardid + ".item")) {

							List<Item> items = new ArrayList<>();
							int iterator2 = 0;
							if (config.contains("type.rewards." + rewardid + ".item")) {
								if (config.getConfigurationSection("type.rewards." + rewardid + ".item") != null) {
									for (String itemid : config.getConfigurationSection("type.rewards." + rewardid + ".item").getKeys(false)) {
										items.add(new Item("item_" + iterator2, XItemStack.deserialize(Utils.getConfigurationSection(config, "type.rewards." + rewardid + ".item." + itemid))));
										iterator2++;
									}
								}
							}

							rewards.add(new ItemReward("reward_" + iterator, name, items, rewardType));

						}

						iterator++;

					}
				}
			}
		}

		rewardType.setRewards(rewards);
		rewardType.save();

	}

	private boolean validRewardData(FileConfiguration config, String rewardID) {
		return config.contains("type.rewards." + rewardID + ".name");
	}

	public void giveReward(Player player, RewardType rewardType) {
		for(Reward reward : rewardType.getRewards()) {
			if (reward instanceof CommandReward) {
				for (String command : ((CommandReward) reward).getCommands()) {
					Bukkit.getServer().dispatchCommand(main.getServer().getConsoleSender(), command.replaceAll("%player%", player.getName()));
				}
			} else if(reward instanceof ItemReward) {
				for (Item item : ((ItemReward) reward).getItems()) {
					if(player == null)
						player.getLocation().getWorld().dropItemNaturally(player.getLocation().clone().add(0.5, 1, 0.5), item.getItemStack().clone());
					else
					if(player.getInventory().firstEmpty() >= 0)
						player.getInventory().addItem(item.getItemStack());
					else
						player.getLocation().getWorld().dropItemNaturally(player.getLocation(), item.getItemStack().clone());
				}

				if(player != null)
					Sounds.playSound(player, player.getLocation(), Sounds.MySound.ITEM_PICKUP, 0.5F, (float) ThreadLocalRandom.current().nextDouble(1, 3));
			}
		}
	}

}
