package me.davidml16.arewards.handlers;

import me.davidml16.arewards.Main;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GUIHandler {

    private Set<UUID> conversations;

    private Main main;

    public GUIHandler(Main main) {
        this.main = main;
        this.conversations = new HashSet<>();
    }

    public boolean isOpened(Player player) {
        if(main.getRewardsGUI().getOpened().contains(player.getUniqueId())) return true;
        if(main.getSetupGUI().getOpened().containsKey(player.getUniqueId())) return true;
        if(main.getEditRewardsGUI().getOpened().containsKey(player.getUniqueId())) return true;
        if(main.getEditChestGUI().getOpened().containsKey(player.getUniqueId())) return true;
        return main.getEditRewardItemsGUI().getOpened().containsKey(player.getUniqueId());
    }

    public void closeIfOpened(Player player) {
        if(isOpened(player))
            player.closeInventory();
    }

    public void removeOpened(Player player) {
        main.getRewardsGUI().getOpened().remove(player.getUniqueId());
        main.getSetupGUI().getOpened().remove(player.getUniqueId());
        main.getRewardsGUI().getOpened().remove(player.getUniqueId());
        main.getEditChestGUI().getOpened().remove(player.getUniqueId());
        main.getEditRewardItemsGUI().getOpened().remove(player.getUniqueId());
    }

    public void addConversation(Player player) {
        conversations.add(player.getUniqueId());
    }

    public boolean haveConversation(Player player) {
        return conversations.contains(player.getUniqueId());
    }

    public void removeConversation(Player player) {
        conversations.remove(player.getUniqueId());
    }

}
