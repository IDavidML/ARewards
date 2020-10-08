package me.davidml16.arewards.objects.rewards;

import me.davidml16.arewards.objects.RewardType;
import org.bukkit.inventory.ItemStack;

public class Reward {

    private String id;
    private String name;
    private RewardType parentReward;

    public Reward(String id, String name, RewardType parentReward) {
        this.id = id;
        this.name = name;
        this.parentReward = parentReward;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public RewardType getParentReward() { return parentReward; }

    public void setParentReward(RewardType parentReward) { this.parentReward = parentReward; }

    @Override
    public String toString() {
        return "Reward{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
