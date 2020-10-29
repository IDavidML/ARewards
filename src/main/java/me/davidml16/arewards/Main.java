package me.davidml16.arewards;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.davidml16.arewards.database.DatabaseHandler;
import me.davidml16.arewards.database.types.Database;
import me.davidml16.arewards.events.Event_Interact;
import me.davidml16.arewards.events.Event_JoinQuit;
import me.davidml16.arewards.events.Event_Vote;
import me.davidml16.arewards.gui.*;
import me.davidml16.arewards.gui.rewards.EditRewardItems_GUI;
import me.davidml16.arewards.gui.rewards.EditRewards_GUI;
import me.davidml16.arewards.handlers.*;
import me.davidml16.arewards.handlers.PluginHandler;
import me.davidml16.arewards.tasks.CollectedRewardTask;
import me.davidml16.arewards.tasks.HologramTask;
import me.davidml16.arewards.tasks.LiveGuiTask;
import me.davidml16.arewards.utils.Utils;
import me.davidml16.arewards.utils.ConfigUpdater;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class Main extends JavaPlugin {

    private static Main main;
    public static ConsoleCommandSender log;
    private MetricsLite metrics;

    private ProtocolManager protocolManager;

    private HologramTask hologramTask;
    private CollectedRewardTask collectedRewardTask;
    private LiveGuiTask liveGuiTask;

    private LanguageHandler languageHandler;
    private DatabaseHandler databaseHandler;
    private PlayerDataHandler playerDataHandler;
    private RewardHandler rewardHandler;
    private RewardTypeHandler rewardTypeHandler;
    private RewardChestHandler rewardChestHandler;
    private HologramHandler hologramHandler;
    private GUIHandler guiHandler;
    private LayoutHandler layoutHandler;
    private TransactionHandler transactionHandler;

    private PluginHandler pluginHandler;

    private Rewards_GUI rewardsGUI;
    private EditChest_GUI editChestGUI;
    private Setup_GUI setupGUI;
    private EditRewards_GUI editRewardsGUI;
    private EditRewardItems_GUI editRewardItemsGUI;
    private EditSettings_GUI editSettingsGUI;

    private int playerCount;

    private Map<String, Boolean> settings;

    private CommandMap commandMap;

    private List<String> templates = Arrays.asList("daily", "iron", "lapiz", "gold", "diamond", "emerald", "obsidian", "vote", "onetime");

    @Override
    public void onEnable() {
        main = this;
        log = Bukkit.getConsoleSender();
        metrics = new MetricsLite(this, 9028);

        settings = new HashMap<>();

        saveDefaultConfig();
        try {
            ConfigUpdater.update(this, "config.yml", new File(main.getDataFolder(), "config.yml"), Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();

        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays") || !Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            getLogger().severe("*** HolographicDisplays / ProtocolLib is not installed or not enabled. ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            setEnabled(false);
            return;
        }

        protocolManager = ProtocolLibrary.getProtocolManager();

        pluginHandler = new PluginHandler(this);

        languageHandler = new LanguageHandler(this, getConfig().getString("Language").toLowerCase());
        languageHandler.pushMessages();

        databaseHandler = new DatabaseHandler(this);
        databaseHandler.openConnection();
        databaseHandler.getDatabase().loadTables();

        transactionHandler = new TransactionHandler(this);

        rewardChestHandler = new RewardChestHandler(this);
        rewardChestHandler.loadChests();

        rewardTypeHandler = new RewardTypeHandler(this);
        rewardTypeHandler.loadTypes();

        rewardHandler = new RewardHandler(this);
        rewardHandler.loadRewards();

        rewardTypeHandler.printLog();

        playerDataHandler = new PlayerDataHandler(this);

        hologramHandler = new HologramHandler(this);
        hologramHandler.getColorAnimation().setColors(getConfig().getStringList("Holograms.ColorAnimation"));
        hologramHandler.loadHolograms();

        playerDataHandler.loadAllPlayerData();

        hologramTask = new HologramTask(this);
        hologramTask.start();

        collectedRewardTask = new CollectedRewardTask(this);
        collectedRewardTask.start();

        layoutHandler = new LayoutHandler(this);

        rewardsGUI = new Rewards_GUI(this);

        settings.put("LiveGuiUpdates", getConfig().getBoolean("LiveGuiUpdates"));
        liveGuiTask = new LiveGuiTask(this);
        if(isLiveGuiUpdates())
            liveGuiTask.start();

        setupGUI = new Setup_GUI(this);
        setupGUI.loadGUI();

        editRewardsGUI = new EditRewards_GUI(this);
        editRewardsGUI.loadGUI();

        editSettingsGUI = new EditSettings_GUI(this);
        editSettingsGUI.loadGUI();

        editRewardItemsGUI = new EditRewardItems_GUI(this);

        editChestGUI = new EditChest_GUI(this);

        guiHandler = new GUIHandler(this);

        registerCommands();
        registerEvents();

        playerCount = getServer().getOnlinePlayers().size();

        PluginDescriptionFile pdf = getDescription();
        log.sendMessage(Utils.translate("  &eARewards Enabled!"));
        log.sendMessage(Utils.translate("    &aVersion: &b" + pdf.getVersion()));
        log.sendMessage(Utils.translate("    &aAuthor: &b" + pdf.getAuthors().get(0)));
        log.sendMessage("");

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderHook(this).register();
            settings.put("placeholderapi", true);
        } else {
            settings.put("placeholderapi", false);
        }

    }

    @Override
    public void onDisable() {
        PluginDescriptionFile pdf = getDescription();
        log.sendMessage("");
        log.sendMessage(Utils.translate("  &eARewards Disabled!"));
        log.sendMessage(Utils.translate("    &aVersion: &b" + pdf.getVersion()));
        log.sendMessage(Utils.translate("    &aAuthor: &b" + pdf.getAuthors().get(0)));
        log.sendMessage("");

        if(hologramHandler != null) hologramHandler.removeHolograms();

        for (Hologram hologram : HologramsAPI.getHolograms(this)) {
            hologram.delete();
        }

        if(databaseHandler != null) databaseHandler.getDatabase().close();
    }

    public static Main get() { return main; }

    public ProtocolManager getProtocolManager() { return protocolManager; }

    public MetricsLite getMetrics() {
        return metrics;
    }

    public List<String> getTemplates() { return templates; }

    public LanguageHandler getLanguageHandler() {
        return languageHandler;
    }

    public Database getDatabaseHandler() {
        return databaseHandler.getDatabase();
    }

    public TransactionHandler getTransactionHandler() { return transactionHandler; }

    public DatabaseHandler getDatabase() { return databaseHandler; }

    public PlayerDataHandler getPlayerDataHandler() { return playerDataHandler; }

    public RewardTypeHandler getRewardTypeHandler() { return rewardTypeHandler; }

    public RewardHandler getRewardHandler() { return rewardHandler; }

    public RewardChestHandler getRewardChestHandler() { return rewardChestHandler; }

    public HologramHandler getHologramHandler() { return hologramHandler; }

    public GUIHandler getGuiHandler() { return guiHandler; }

    public LayoutHandler getLayoutHandler() { return layoutHandler; }

    public Rewards_GUI getRewardsGUI() { return rewardsGUI; }

    public Setup_GUI getSetupGUI() { return setupGUI; }

    public EditChest_GUI getEditChestGUI() { return editChestGUI; }

    public EditSettings_GUI getEditSettingsGUI() { return editSettingsGUI; }

    public EditRewards_GUI getEditRewardsGUI() { return editRewardsGUI; }

    public EditRewardItems_GUI getEditRewardItemsGUI() { return editRewardItemsGUI; }

    public PluginHandler getPluginHandler() { return pluginHandler; }

    public HologramTask getHologramTask() { return hologramTask; }

    public CollectedRewardTask getCollectedRewardTask() { return collectedRewardTask; }

    public LiveGuiTask getLiveGuiTask() { return liveGuiTask; }

    public int getPlayerCount() { return playerCount; }

    public void setPlayerCount(int playerCount) { this.playerCount = playerCount; }

    public boolean playerHasPermission(Player p, String permission) {
        return p.hasPermission(permission) || p.isOp();
    }

    public boolean isLiveGuiUpdates() { return settings.get("LiveGuiUpdates"); }

    public void setLiveGuiUpdates(boolean value) { settings.put("LiveGuiUpdates", value); }

    public CommandMap getCommandMap() {
        return commandMap;
    }

    public boolean hasPlaceholderAPI() { return settings.get("placeholderapi"); }

    private void registerCommands() {
        Field bukkitCommandMap = null;
        try {
            bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            commandMap.register("arewards", new me.davidml16.arewards.commands.rewards.CoreCommand(Objects.requireNonNull(getConfig().getString("Commands.Rewards")).toLowerCase()));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new Event_Interact(this), this);
        Bukkit.getPluginManager().registerEvents(new Event_JoinQuit(this), this);

        if (Bukkit.getPluginManager().isPluginEnabled("Votifier"))
            Bukkit.getPluginManager().registerEvents(new Event_Vote(this), this);

    }

}
