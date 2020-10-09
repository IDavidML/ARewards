package me.davidml16.arewards.handlers;

import me.davidml16.arewards.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PluginHandler {

    private final Main main;
    public PluginHandler(Main main) {
        this.main = main;
    }

    public void reloadAll() {

        for(Player p : Bukkit.getServer().getOnlinePlayers()) {
            main.getGuiHandler().closeIfOpened(p);
        }

        main.reloadConfig();

        main.getLanguageHandler().loadLanguage("en");
        main.getLanguageHandler().setLanguage(main.getConfig().getString("Language").toLowerCase());

        main.setLiveGuiUpdates(main.getConfig().getBoolean("LiveGuiUpdates"));

        main.getHologramTask().stop();
        main.getCollectedRewardTask().stop();
        main.getLiveGuiTask().stop();

        main.getHologramHandler().removeHolograms();
        main.getLanguageHandler().pushMessages();
        main.getRewardChestHandler().loadChests();
        main.getRewardTypeHandler().loadTypes();
        main.getRewardHandler().loadRewards();
        main.getRewardTypeHandler().printLog();
        main.getSetupGUI().reloadAllGUI();
        main.getEditRewardsGUI().reloadAllGUI();
        main.getEditSettingsGUI().reloadAllGUI();
        main.getHologramHandler().getColorAnimation().setColors(main.getConfig().getStringList("Holograms.ColorAnimation"));
        main.getHologramHandler().loadHolograms();

        main.getHologramTask().start();
        main.getCollectedRewardTask().start();
        if(main.isLiveGuiUpdates())
            main.getLiveGuiTask().start();
    }

}
