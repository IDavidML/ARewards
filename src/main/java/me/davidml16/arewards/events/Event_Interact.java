package me.davidml16.arewards.events;

import me.davidml16.arewards.Main;
import me.davidml16.arewards.objects.RewardChest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class Event_Interact implements Listener {

    private Main main;
    public Event_Interact(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action action = e.getAction();

        if(action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK) {
            if(main.getRewardChestHandler().getChests().containsKey(e.getClickedBlock().getLocation())) {
                e.setCancelled(true);

                if (!Bukkit.getVersion().contains("1.8")) {
                    if (e.getHand() == EquipmentSlot.OFF_HAND) return;
                }

                RewardChest box = main.getRewardChestHandler().getChestByLocation(e.getClickedBlock().getLocation());

                main.getPlayerDataHandler().getData(p).setBoxOpened(box);

                main.getRewardsGUI().open(p);
            }
        }
    }

}
