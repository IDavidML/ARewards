package me.davidml16.arewards.objects;

import me.davidml16.arewards.Main;
import me.davidml16.arewards.objects.rewards.*;
import me.davidml16.arewards.utils.XSeries.XItemStack;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RewardType {

    private Main main;

    private String id;
    private String name;

    private int slot;

    private String cooldownString;
    private Long cooldown;

    private ItemStack availableIcon;
    private List<String> loreAvailable;

    private ItemStack cooldownIcon;
    private List<String> loreCooldown;

    private boolean requirePermission;
    private String permission;
    private List<String> lorePermission;
    private List<String> noPermissionMessage;

    private List<String> loreClaimed;

    private List<String> description;
    private List<Reward> rewards;

    private boolean needVote;
    private boolean oneTime;

    public RewardType(Main main, String id, String name, int slot) {
        this.main = main;
        this.id = id;
        this.name = name;
        this.rewards = new ArrayList<>();
        this.cooldown = 0L;
        this.slot = slot;
        this.needVote = false;
        this.oneTime = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSlot() { return slot; }

    public void setSlot(int slot) { this.slot = slot; }

    public List<Reward> getRewards() { return rewards; }

    public Reward getReward(String id) {
        for(Reward reward : rewards)
            if(reward.getId().equalsIgnoreCase(id))
                return reward;
        return  null;
    }

    public void setRewards(List<Reward> rewards) { this.rewards = rewards; }

    public ItemStack getAvailableIcon() { return availableIcon; }

    public void setAvailableIcon(ItemStack availableIcon) { this.availableIcon = availableIcon; }

    public ItemStack getCooldownIcon() { return cooldownIcon; }

    public void setCooldownIcon(ItemStack cooldownIcon) { this.cooldownIcon = cooldownIcon; }

    public List<String> getLoreAvailable() {
        return loreAvailable;
    }

    public void setLoreAvailable(List<String> loreAvailable) {
        this.loreAvailable = loreAvailable;
    }

    public List<String> getLoreCooldown() { return loreCooldown; }

    public void setLoreCooldown(List<String> loreCooldown) { this.loreCooldown = loreCooldown; }

    public boolean isRequirePermission() { return requirePermission; }

    public void setRequirePermission(boolean requirePermission) { this.requirePermission = requirePermission; }

    public List<String> getLorePermission() { return lorePermission; }

    public void setLorePermission(List<String> lorePermission) { this.lorePermission = lorePermission; }

    public List<String> getNoPermissionMessage() { return noPermissionMessage; }

    public void setNoPermissionMessage(List<String> noPermissionMessage) { this.noPermissionMessage = noPermissionMessage; }

    public String getPermission() { return permission; }

    public void setPermission(String permission) { this.permission = permission; }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public Long getCooldown() { return cooldown; }

    public void setCooldown(Long cooldown) { this.cooldown = cooldown; }

    public String getCooldownString() { return cooldownString; }

    public void setCooldownString(String cooldownString) { this.cooldownString = cooldownString; }

    public boolean isNeedVote() { return needVote; }

    public void setNeedVote(boolean needVote) { this.needVote = needVote; }

    public boolean isOneTime() { return oneTime; }

    public void setOneTime(boolean oneTime) { this.oneTime = oneTime; }

    public List<String> getLoreClaimed() { return loreClaimed; }

    public void setLoreClaimed(List<String> loreClaimed) { this.loreClaimed = loreClaimed; }

    @Override
    public String toString() {
        return "RewardType{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", cooldown=" + cooldown +
                ", slot=" + slot +
                ", loreAvailable=" + loreAvailable +
                ", loreCooldown=" + loreCooldown +
                ", description=" + description +
                ", rewards=" + rewards +
                '}';
    }

    public void save() {
        FileConfiguration config = main.getRewardTypeHandler().getConfig(id);

        config.set("type.slot", getSlot());

        config.set("type.cooldown", getCooldownString());

        config.set("type.require_vote.enabled", isNeedVote());

        config.set("type.one_time.enabled", isOneTime());

        config.set("type.rewards", new ArrayList<>());
        if (config.contains("type.rewards")) {
            List<Reward> rewards = getRewards();
            for (int i = 0; i < rewards.size(); i++) {
                Reward reward = rewards.get(i);
                config.set("type.rewards.reward_" + i + ".name", reward.getName());

                if(reward instanceof CommandReward)
                    config.set("type.rewards.reward_" + i + ".command", ((CommandReward) reward).getCommands());
                else if(reward instanceof ItemReward) {
                    List<Item> items = ((ItemReward) reward).getItems();
                    config.set("type.rewards.reward_" + i + ".item", new ArrayList<>());
                    for (int j = 0; j < items.size(); j++) {
                        XItemStack.serializeItem(items.get(j).getItemStack(), config, "type.rewards.reward_" + i + ".item.item_" + j);
                    }
                }

            }
        }

        main.getRewardTypeHandler().saveConfig(id);
    }

}
