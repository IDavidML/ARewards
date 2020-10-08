package me.davidml16.arewards;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.davidml16.arewards.objects.Profile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Objects;

public class PlaceholderHook extends PlaceholderExpansion {

    private Main main;
    public PlaceholderHook(Main main) {
        this.main = main;
    }

    @Override
    public boolean canRegister() {
        return Bukkit.getPluginManager().getPlugin("ARewards") != null;
    }

    @Override
    public boolean register() {
        if (!canRegister()) {
            return false;
        }

        return super.register();
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public String getIdentifier() {
        return "arewards";
    }

    @Override
    public String getAuthor() {
        return "DavidML16";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (player == null) return "";

        Profile profile = main.getPlayerDataHandler().getData(player.getPlayer());

        String[] identifiers = identifier.split("_");
        if (identifiers[0].equalsIgnoreCase("available"))
            if(profile != null)
                return ""+profile.getAvailableRewards();
            else
                return "N/A";

        return null;
    }
}