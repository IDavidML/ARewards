package me.davidml16.arewards.holograms.implementations;

import me.davidml16.arewards.Main;
import me.davidml16.arewards.holograms.HologramHandler;
import me.davidml16.arewards.holograms.HologramImplementation;
import me.davidml16.arewards.objects.RewardChest;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class HolographicDisplaysImpl implements HologramImplementation, Listener {

    private Main main;
    private HologramHandler hologramHandler;

    private static final double LINE_HEIGHT = 0.36;

    private HashMap<RewardChest, HashMap<UUID, Hologram>> holograms;

    public HolographicDisplaysImpl(Main main, HologramHandler hologramHandler) {
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
        HolographicDisplaysAPI api = HolographicDisplaysAPI.get(main);
        for (Hologram hologram : api.getHolograms()) {
            hologram.delete();
        }
    }

    public void reloadHolograms() {
        hologramHandler.setActualColor(hologramHandler.getColorAnimation().nextColor());
        for(Player p : Bukkit.getOnlinePlayers()) {
            reloadHolograms(p);
        }
    }

    public void loadHolograms(Player p, RewardChest box) {

        HolographicDisplaysAPI api = HolographicDisplaysAPI.get(main);
        Hologram hologram = api.createHologram(box.getLocation().clone().add(0.5, 1.025 + (box.getBlockHeight() + 0.1875), 0.5));
        VisibilitySettings visibilitySettings = hologram.getVisibilitySettings();

        visibilitySettings.setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        visibilitySettings.setIndividualVisibility(p, VisibilitySettings.Visibility.VISIBLE);

        List<String> lines = hologramHandler.getLines(p);
        int max = Math.max(main.getLanguageHandler().getMessageList("Holograms.RewardsAvailable").size(), main.getLanguageHandler().getMessageList("Holograms.NoRewardsAvailable").size());

        for (String line : lines) {
            hologram.getLines().appendText(line);
        }

        hologram.setPosition(box.getLocation().clone().add(0.5, (max * LINE_HEIGHT) + (box.getBlockHeight() + 0.1875), 0.5));

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
        for (Hologram hologram : holograms.get(box).values()) {
            hologram.getLines().clear();
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            reloadHologram(p, box);
        }
    }

    public void reloadHologram(Player p, RewardChest box) {

        if (holograms.get(box) == null || !holograms.get(box).containsKey(p.getUniqueId()))
            return;

        Hologram hologram = holograms.get(box).get(p.getUniqueId());

        if (!Objects.equals(box.getLocation().getWorld(), p.getLocation().getWorld())) return;

        if(box.getLocation().distanceSquared(p.getLocation()) > 75) return;

        List<String> lines = hologramHandler.getLines(p);

        VisibilitySettings visibilitySettings = hologram.getVisibilitySettings();
        if (!visibilitySettings.isVisibleTo(p))
            visibilitySettings.setIndividualVisibility(p, VisibilitySettings.Visibility.VISIBLE);

        int max = Math.max(main.getLanguageHandler().getMessageList("Holograms.RewardsAvailable").size(), main.getLanguageHandler().getMessageList("Holograms.NoRewardsAvailable").size());
        if (hologram.getLines().size() != lines.size())
            hologram.setPosition(box.getLocation().clone().add(0.5, (max * LINE_HEIGHT) + (box.getBlockHeight() + 0.1875), 0.5));

        if (hologram.getLines().size() > lines.size()) {
            for (int i = lines.size(); i < hologram.getLines().size(); i++)
                hologram.getLines().remove(i);

        } else if (hologram.getLines().size() < lines.size()) {
            for (int i = hologram.getLines().size(); i < lines.size(); i++)
                hologram.getLines().appendText(lines.get(i));
        }

        for (int i = 0; i < lines.size(); i++) {
            TextHologramLine line = (TextHologramLine) hologram.getLines().get(i);
            if (!(line.getText().equalsIgnoreCase(lines.get(i))))
                line.setText(lines.get(i));
        }

    }

    @Override
    public void clearLines(RewardChest box) {
        for (Hologram hologram : holograms.get(box).values()) {
            hologram.getLines().clear();
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
            hologram.setPosition(box.getLocation().clone().add(0.5, (max * LINE_HEIGHT) + (box.getBlockHeight() + 0.1875), 0.5));
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
            holo.delete();
        }

    }

}
