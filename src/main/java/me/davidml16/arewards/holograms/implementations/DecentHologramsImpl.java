package me.davidml16.arewards.holograms.implementations;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import me.davidml16.arewards.Main;
import me.davidml16.arewards.holograms.HologramHandler;
import me.davidml16.arewards.holograms.HologramImplementation;
import me.davidml16.arewards.objects.RewardChest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DecentHologramsImpl implements HologramImplementation, Listener {

    private Main main;
    private HologramHandler hologramHandler;

    private static final double LINE_HEIGHT = 0.36;

    private HashMap<RewardChest, HashMap<UUID, Hologram>> holograms;

    public DecentHologramsImpl(Main main, HologramHandler hologramHandler) {
        this.main = main;
        this.hologramHandler = hologramHandler;
        this.holograms = new HashMap<>();
    }

    public void loadHolograms() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            loadHolograms(p);
        }
    }

    public void loadHolograms(Player p) {
        for(RewardChest box : main.getRewardChestHandler().getChests().values()) {
            loadHolograms(p, box);
        }
    }

    public void loadHolograms(RewardChest box) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            loadHolograms(p, box);
        }
    }

    public void removeHolograms() {
        for (Hologram hologram : Hologram.getCachedHolograms()) {
            hologram.hideAll();
            hologram.destroy();
        }
    }

    public void reloadHolograms() {
        hologramHandler.setActualColor(hologramHandler.getColorAnimation().nextColor());
        for(Player p : Bukkit.getOnlinePlayers()) {
            reloadHolograms(p);
        }
    }

    public void loadHolograms(Player p, RewardChest box) {

        Hologram hologram = DHAPI.createHologram(UUID.randomUUID().toString(), box.getLocation().clone().add(0.5, 1.025 + (box.getBlockHeight() + 0.1875), 0.5));
        hologram.setDefaultVisibleState(false);
        hologram.setShowPlayer(p);

        int max = Math.max(main.getLanguageHandler().getMessageList("Holograms.RewardsAvailable").size(), main.getLanguageHandler().getMessageList("Holograms.NoRewardsAvailable").size());

        DHAPI.setHologramLines(hologram, hologramHandler.getLines(p));
        DHAPI.moveHologram(hologram, box.getLocation().clone().add(0.5, (max * LINE_HEIGHT) + (box.getBlockHeight() + 0.1875), 0.5));

        if(holograms.get(box) == null)
            holograms.put(box, new HashMap<>());

        holograms.get(box).put(p.getUniqueId(), hologram);

    }

    public void reloadHolograms(Player p) {
        for(RewardChest box : main.getRewardChestHandler().getChests().values()) {
            reloadHologram(p, box);
        }
    }

    public void reloadHologram(RewardChest box) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            reloadHologram(p, box);
        }
    }

    public void reloadHologram(Player p, RewardChest box) {
        if (!Objects.equals(box.getLocation().getWorld(), p.getLocation().getWorld())) return;

        if(box.getLocation().distanceSquared(p.getLocation()) > 75) return;

        if (holograms.get(box) == null || !holograms.get(box).containsKey(p.getUniqueId()))
            return;

        Hologram hologram = holograms.get(box).get(p.getUniqueId());
        List<String> lines = hologramHandler.getLines(p);

        if (holograms.get(box).containsKey(p.getUniqueId())) {
            int max = Math.max(main.getLanguageHandler().getMessageList("Holograms.RewardsAvailable").size(), main.getLanguageHandler().getMessageList("Holograms.NoRewardsAvailable").size());
            DHAPI.setHologramLines(hologram, lines);
            DHAPI.moveHologram(hologram, box.getLocation().clone().add(0.5, (max * LINE_HEIGHT) + (box.getBlockHeight() + 0.1875), 0.5));
        }
    }

    @Override
    public void clearLines(RewardChest box) {
        for (Hologram hologram : holograms.get(box).values()) {
            HologramPage page = hologram.getPage(0);
            while(page.size() > 0) {
                page.removeLine(page.size() - 1);
            }
            hologram.realignLines();
            hologram.updateAll();
            hologram.save();
        }
    }

    @Override
    public void clearHolograms(RewardChest box) {
        holograms.get(box).clear();
    }

    public void moveHologram(RewardChest box) {

        int max = Math.max(main.getLanguageHandler().getMessageList("Holograms.RewardsAvailable").size(), main.getLanguageHandler().getMessageList("Holograms.NoRewardsAvailable").size());

        if(holograms.get(box) == null)
            return;

        for (Hologram hologram : holograms.get(box).values()) {
            DHAPI.moveHologram(hologram, box.getLocation().clone().add(0.5, (max * LINE_HEIGHT) + (box.getBlockHeight() + 0.1875), 0.5));
        }

    }

    public void removeHolograms(Player p) {

        for(RewardChest box : main.getRewardChestHandler().getChests().values()) {

            if(holograms.get(box) == null)
                continue;

            if(holograms.get(box).containsKey(p.getUniqueId())) {

                if(holograms.get(box).get(p.getUniqueId()) == null)
                    continue;

                holograms.get(box).get(p.getUniqueId()).delete();

                holograms.get(box).remove(p.getUniqueId());

            }

        }

    }

    @Override
    public void removeHolograms(RewardChest box) {

        if(holograms.get(box) == null)
            return;

        for(Hologram holo : holograms.get(box).values()) {
            holo.destroy();
        }

    }

}