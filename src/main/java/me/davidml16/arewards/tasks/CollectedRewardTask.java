package me.davidml16.arewards.tasks;

import me.davidml16.arewards.Main;
import me.davidml16.arewards.objects.Profile;
import me.davidml16.arewards.objects.RewardCollected;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class CollectedRewardTask {

	private int id;

	private Main main;
	public CollectedRewardTask(Main main) {
		this.main = main;
	}

	class Task implements Runnable {
		@Override
		public void run() {
			if(main.getPlayerCount() > 0) {

				for(Profile profile : main.getPlayerDataHandler().getPlayersData().values()) {

					try {

						Iterator<RewardCollected> it = profile.getRewards().iterator();
						int i = 0;
						while(it.hasNext()) {
							RewardCollected rewardCollected = it.next();
							if(rewardCollected.getExpire() <= System.currentTimeMillis()) {
								try {
									main.getDatabaseHandler().removeRewardCollected(profile.getUuid(), rewardCollected.getRewardID());
								} catch (SQLException e) {
									e.printStackTrace();
								}
								it.remove();
								i++;
							}
						}

						if(i > 0) {
							if(main.getHologramHandler().getImplementation() != null) {
								Bukkit.getScheduler().runTask(main, () -> {
									Player player = Bukkit.getPlayer(profile.getUuid());
									if (player != null)
										main.getHologramHandler().getImplementation().reloadHolograms(player);
								});
							}
						}

					} catch (NullPointerException ignore) {}

				}

			}
		}
	}
	
	public int getId() { return id; }

	@SuppressWarnings("deprecation")
	public void start() {
		id = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(main, new Task(), 10, 10);
	}
	
	public void stop() {
		Bukkit.getServer().getScheduler().cancelTask(id);
	}
	
}
