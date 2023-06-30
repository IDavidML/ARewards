package me.davidml16.arewards.holograms;

import me.davidml16.arewards.objects.RewardChest;
import org.bukkit.entity.Player;

public interface HologramImplementation {

    void loadHolograms();

    void loadHolograms(Player p);

    void loadHolograms(RewardChest box);

    void removeHolograms();

    void removeHolograms(Player p);

    void removeHolograms(RewardChest box);

    void loadHolograms(Player p, RewardChest box);

    void reloadHolograms();

    void reloadHolograms(Player p);

    void reloadHologram(RewardChest box);

    void reloadHologram(Player p, RewardChest box);

    void clearLines(RewardChest box);

    void clearHolograms(RewardChest box);

    void moveHologram(RewardChest box);

}
