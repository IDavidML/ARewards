package me.davidml16.arewards.handlers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import me.davidml16.arewards.Main;
import me.davidml16.arewards.objects.ColorAnimation;
import me.davidml16.arewards.objects.Profile;
import me.davidml16.arewards.objects.RewardChest;
import me.davidml16.arewards.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HologramHandler {

    private Main main;

    private ColorAnimation colorAnimation;
    private String actualColor;

    private static final double LINE_HEIGHT = 0.36;

    public HologramHandler(Main main) {
        this.main = main;
        this.colorAnimation = new ColorAnimation();
        this.actualColor = "&c";
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
        for (Hologram hologram : HologramsAPI.getHolograms(main)) {
            hologram.delete();
        }
    }

    public void reloadHolograms() {
        this.actualColor = colorAnimation.nextColor();
        for(Player p : Bukkit.getOnlinePlayers()) {
            reloadHolograms(p);
        }
    }

    public String getColor() {
        return this.actualColor;
    }
    public ColorAnimation getColorAnimation() { return colorAnimation; }

    public void loadHolograms(Player p, RewardChest box) {
        Hologram hologram = HologramsAPI.createHologram(main, box.getLocation().clone().add(0.5, 1.025 + (box.getBlockHeight() + 0.1875), 0.5));
        VisibilityManager visibilityManager = hologram.getVisibilityManager();

        visibilityManager.showTo(p);
        visibilityManager.setVisibleByDefault(false);

        List<String> lines = getLines(p);
        int max = Math.max(main.getLanguageHandler().getMessageList("Holograms.RewardsAvailable").size(), main.getLanguageHandler().getMessageList("Holograms.NoRewardsAvailable").size());

        for (String line : lines) {
            hologram.appendTextLine(line);
        }

        hologram.teleport(box.getLocation().clone().add(0.5, (max * LINE_HEIGHT) + (box.getBlockHeight() + 0.1875), 0.5));


        box.getHolograms().put(p.getUniqueId(), hologram);
    }

    public void reloadHolograms(Player p) {
        for(RewardChest box : main.getRewardChestHandler().getChests().values()) {
            reloadHologram(p, box);
        }
    }

    public void reloadHologram(RewardChest box) {
        for (Hologram hologram : box.getHolograms().values()) {
            hologram.clearLines();
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            reloadHologram(p, box);
        }
    }

    public void moveHologram(RewardChest box) {
        int max = Math.max(main.getLanguageHandler().getMessageList("Holograms.RewardsAvailable").size(), main.getLanguageHandler().getMessageList("Holograms.NoRewardsAvailable").size());
        for (Hologram hologram : box.getHolograms().values()) {
            hologram.teleport(box.getLocation().clone().add(0.5, (max * LINE_HEIGHT) + (box.getBlockHeight() + 0.1875), 0.5));
        }
    }

    public void reloadHologram(Player p, RewardChest box) {
        if (!Objects.equals(box.getLocation().getWorld(), p.getLocation().getWorld())) return;
        if (box.getLocation().distance(p.getLocation()) > 75) return;

        if (box.getHolograms().containsKey(p.getUniqueId())) {
            List<String> lines = getLines(p);
            Hologram hologram = box.getHolograms().get(p.getUniqueId());

            int max = Math.max(main.getLanguageHandler().getMessageList("Holograms.RewardsAvailable").size(), main.getLanguageHandler().getMessageList("Holograms.NoRewardsAvailable").size());
            if (hologram.size() != lines.size())
                hologram.teleport(box.getLocation().clone().add(0.5, (max * LINE_HEIGHT) + (box.getBlockHeight() + 0.1875), 0.5));

            if (hologram.size() > lines.size()) {
                for (int i = lines.size(); i < hologram.size(); i++)
                    hologram.getLine(i).removeLine();

            } else if (hologram.size() < lines.size()) {
                for (int i = hologram.size(); i < lines.size(); i++)
                    hologram.appendTextLine(lines.get(i));
            }

            for (int i = 0; i < lines.size(); i++) {
                if (!((TextLine) hologram.getLine(i)).getText().equalsIgnoreCase(lines.get(i)))
                    ((TextLine) hologram.getLine(i)).setText(lines.get(i));
            }
        }

    }

    public void removeHolograms(Player p) {
        for(RewardChest box : main.getRewardChestHandler().getChests().values()) {
            if(box.getHolograms().containsKey(p.getUniqueId())) {
                box.getHolograms().get(p.getUniqueId()).delete();
                box.getHolograms().remove(p.getUniqueId());
            }
        }
    }

    public List<String> getLines(Player p) {
        List<String> lines = new ArrayList<String>();

        int available = 0;
        Profile profile = main.getPlayerDataHandler().getData(p);

        if(profile != null)
            if(profile.getRewards() != null)
                available = profile.getAvailableRewards();

        String rewardsString = available != 1 ? main.getLanguageHandler().getMessage("Strings.Rewards") : main.getLanguageHandler().getMessage("Strings.Reward");

        if(available > 0) {
            for(String line : main.getLanguageHandler().getMessageList("Holograms.RewardsAvailable")) {
                lines.add(Utils.translate(line
                        .replaceAll("%blink%", getColor())
                        .replaceAll("%rewards_available%", String.valueOf(available))
                        .replaceAll("%rewards_string%", rewardsString)
                ));
            }
        } else {
            for(String line : main.getLanguageHandler().getMessageList("Holograms.NoRewardsAvailable")) {
                lines.add(Utils.translate(line
                        .replaceAll("%blink%", getColor())
                        .replaceAll("%rewards_available%", String.valueOf(available))
                        .replaceAll("%rewards_string%", rewardsString)
                ));
            }
        }
        return lines;
    }

}