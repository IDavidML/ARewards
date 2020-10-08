package me.davidml16.arewards.objects.rewards;

import me.davidml16.arewards.objects.RewardType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandReward extends Reward {

    private List<String> commands;

    public CommandReward(String id, String name, List<String> commands, RewardType parentReward) {
        super(id, name, parentReward);
        this.commands = commands;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    @Override
    public String toString() {
        return "CommandReward{" +
                super.toString() +
                ", commands=" + commands +
                '}';
    }
}
