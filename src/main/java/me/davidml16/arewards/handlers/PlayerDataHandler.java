package me.davidml16.arewards.handlers;

import me.davidml16.arewards.Main;
import me.davidml16.arewards.objects.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerDataHandler {

	public HashMap<UUID, Profile> data = new HashMap<UUID, Profile>();

	private Main main;
	public PlayerDataHandler(Main main) {
		this.main = main;
	}

	public HashMap<UUID, Profile> getPlayersData() {
		return data;
	}

	public Profile getData(Player p) {
		if (data.containsKey(p.getUniqueId()))
			return data.get(p.getUniqueId());
		return null;
	}

	public Profile getData(UUID uuid) {
		if (data.containsKey(uuid))
			return data.get(uuid);
		return null;
	}

	public boolean playerExists(Player p) {
		return data.containsKey(p.getUniqueId());
	}

	public void loadPlayerData(Player p) {
		Profile profile = new Profile(main, p.getUniqueId());

		try {
			if(!main.getDatabaseHandler().hasName(p.getName()))
				main.getDatabaseHandler().createPlayerData(p);
			else
				main.getDatabaseHandler().updatePlayerName(p);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		try {
			main.getDatabaseHandler().removeExpiredRewards(p.getUniqueId());
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		main.getDatabaseHandler().getRewardCollected(p.getUniqueId()).thenAccept(rewards -> {
			profile.setRewards(rewards);
			Bukkit.getScheduler().runTaskLater(main, () -> main.getHologramHandler().reloadHolograms(p), 2L);
		});

		data.put(p.getUniqueId(), profile);
	}

	public void loadAllPlayerData() {
		data.clear();
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			loadPlayerData(p);
		}
	}

}
