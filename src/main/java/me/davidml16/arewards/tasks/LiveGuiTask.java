package me.davidml16.arewards.tasks;

import me.davidml16.arewards.Main;
import me.davidml16.arewards.objects.GUILayout;
import me.davidml16.arewards.objects.Profile;
import me.davidml16.arewards.objects.RewardCollected;
import me.davidml16.arewards.objects.RewardType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LiveGuiTask {

	private int id;

	private GUILayout layout;
	private int size;

	private Main main;
	public LiveGuiTask(Main main) {
		this.main = main;
	}

	class Task implements Runnable {
		@Override
		public void run() {

			if(main.getPlayerCount() == 0) return;

			for(UUID uuid : main.getRewardsGUI().getOpened()) {

				Player player = Bukkit.getPlayer(uuid);
				Profile profile = main.getPlayerDataHandler().getData(player);

				if(profile.getRewards().size() == 0) continue;

				Map<Integer, ItemStack> items = new HashMap<>();

				List<RewardCollected> rewardCollecteds = new ArrayList<>(profile.getRewards());

				for(RewardType rewardType : main.getRewardTypeHandler().getTypes().values()) {

					if(rewardType.getSlot() >= 0 && rewardType.getSlot() < size)

						items.put(rewardType.getSlot(), main.getRewardsGUI().getRewardItem(player, layout, rewardType, rewardCollecteds));

				}

				Bukkit.getScheduler().runTask(main, () -> {

					Player target = Bukkit.getPlayer(uuid);

					if(target == null) return;

					if(!main.getRewardsGUI().getOpened().contains(uuid)) return;

					for(int i : items.keySet())
						target.getOpenInventory().getTopInventory().setItem(i, items.get(i));

				});

			}

		}
	}
	
	public int getId() { return id; }

	@SuppressWarnings("deprecation")
	public void start() {
		layout = main.getLayoutHandler().getLayout("rewards");

		int size;
		if(layout.getInteger("Size") < 9 && layout.getInteger("Size") > 54) size = 54;
		else size = layout.getInteger("Size");
		this.size = size;

		id = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(main, new Task(), 0, 20);
	}
	
	public void stop() {
		Bukkit.getServer().getScheduler().cancelTask(id);
	}
	
}
