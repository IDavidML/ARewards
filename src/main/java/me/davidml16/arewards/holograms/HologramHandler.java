package me.davidml16.arewards.holograms;

import me.davidml16.arewards.Main;
import me.davidml16.arewards.holograms.implementations.DecentHologramsImpl;
import me.davidml16.arewards.holograms.implementations.HolographicDisplaysImpl;
import me.davidml16.arewards.objects.ColorAnimation;
import me.davidml16.arewards.objects.Profile;
import me.davidml16.arewards.utils.Utils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HologramHandler {

    private Main main;

    private HologramImplementation implementation;

    private ColorAnimation colorAnimation;
    private String actualColor;

    private int visibilityDistance = 75;

    public HologramHandler(Main main) {

        this.main = main;
        this.colorAnimation = new ColorAnimation();
        this.actualColor = "&c";

        if(main.getServer().getPluginManager().isPluginEnabled("HolographicDisplays"))
            implementation = new HolographicDisplaysImpl(main, this);
        else if(main.getServer().getPluginManager().isPluginEnabled("DecentHolograms"))
            implementation = new DecentHologramsImpl(main, this);

    }

    public HologramImplementation getImplementation() {
        return implementation;
    }

    public ColorAnimation getColorAnimation() {
        return colorAnimation;
    }

    public void setColorAnimation(ColorAnimation colorAnimation) {
        this.colorAnimation = colorAnimation;
    }

    public String getActualColor() {
        return actualColor;
    }

    public void setActualColor(String actualColor) {
        this.actualColor = actualColor;
    }

    public int getVisibilityDistance() {
        return visibilityDistance;
    }

    public void setVisibilityDistance(int visibilityDistance) {
        this.visibilityDistance = visibilityDistance;
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
                        .replaceAll("%blink%", actualColor)
                        .replaceAll("%rewards_available%", String.valueOf(available))
                        .replaceAll("%rewards_string%", rewardsString)
                ));
            }
        } else {
            for(String line : main.getLanguageHandler().getMessageList("Holograms.NoRewardsAvailable")) {
                lines.add(Utils.translate(line
                        .replaceAll("%blink%", actualColor)
                        .replaceAll("%rewards_available%", String.valueOf(available))
                        .replaceAll("%rewards_string%", rewardsString)
                ));
            }
        }
        return lines;
    }

}
