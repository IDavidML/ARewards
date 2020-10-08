package me.davidml16.arewards.objects;

import me.davidml16.arewards.Main;
import me.davidml16.arewards.objects.rewards.Reward;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Profile {

	private Main main;

	private UUID uuid;

	private RewardChest boxOpened;

	private List<RewardCollected> rewards;

	public Profile(Main main, UUID uuid) {
		this.main = main;
		this.uuid = uuid;
		this.boxOpened = null;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public RewardChest getBoxOpened() { return boxOpened; }

	public void setBoxOpened(RewardChest boxOpened) { this.boxOpened = boxOpened; }

	public List<RewardCollected> getRewards() { return rewards; }

	public void setRewards(List<RewardCollected> rewards) { this.rewards = rewards; }

	public int getAvailableRewards() {
		int available = 0;

		for(RewardType rewardType : main.getRewardTypeHandler().getTypes().values()) {
			Optional<RewardCollected> rewardCollected = rewards.stream().filter(rc -> rc.getRewardID().equalsIgnoreCase(rewardType.getId())).findFirst();
			if(rewardType.isRequirePermission()) {
				if(!rewardCollected.isPresent())
					if(main.getRewardTypeHandler().haveRewardPermission(Bukkit.getPlayer(uuid), rewardType))
						available++;
			} else {
				if(!rewardCollected.isPresent())
					available++;
			}
		}

		return available;
	}

	@Override
	public String toString() {
		return "Profile{" +
				"main=" + main +
				", uuid=" + uuid +
				", boxOpened=" + boxOpened +
				'}';
	}

}
