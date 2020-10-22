package me.davidml16.arewards.handlers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import me.davidml16.arewards.Main;
import me.davidml16.arewards.objects.RewardChest;
import me.davidml16.arewards.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class RewardChestHandler {

    private HashMap<Location, RewardChest> chests;
    private File file;
    private YamlConfiguration config;

    private Main main;

    public RewardChestHandler(Main main) {
        this.main = main;
        this.chests = new HashMap<Location, RewardChest>();
    }

    public HashMap<Location, RewardChest> getChests() {
        return chests;
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public RewardChest getChestByLocation(Location loc) {
        return chests.get(loc);
    }

    public void createChest(Location loc, double blockHeight) {
        RewardChest box = new RewardChest(loc.getWorld().getName(), loc, blockHeight);
        chests.put(loc, box);
        main.getHologramHandler().loadHolograms(box);

        config.set("locations", new ArrayList<>());

        int i = 1;
        for(RewardChest bx : chests.values()) {
            config.set("locations." + i + ".world", bx.getLocation().getWorld().getName());
            config.set("locations." + i + ".x", bx.getLocation().getBlockX());
            config.set("locations." + i + ".y", bx.getLocation().getBlockY());
            config.set("locations." + i + ".z", bx.getLocation().getBlockZ());
            config.set("locations." + i + ".blockHeight", bx.getBlockHeight());
            i++;
        }

        saveConfig();
    }

    public void removeChest(Location loc) {
        if(chests.containsKey(loc)) {

            RewardChest chest = getChestByLocation(loc);
            for(Hologram holo : chest.getHolograms().values()) {
                holo.delete();
            }
            chest.getHolograms().clear();

            chests.remove(loc);

            config.set("locations", new ArrayList<>());

            int i = 1;
            for(RewardChest bx : chests.values()) {
                config.set("locations." + i + ".world", bx.getLocation().getWorld().getName());
                config.set("locations." + i + ".x", bx.getLocation().getBlockX());
                config.set("locations." + i + ".y", bx.getLocation().getBlockY());
                config.set("locations." + i + ".z", bx.getLocation().getBlockZ());
                config.set("locations." + i + ".blockHeight", bx.getBlockHeight());
                i++;
            }

            saveConfig();
        }
    }

    public void saveChests() {
        config.set("locations", new ArrayList<>());

        int i = 1;
        for(RewardChest bx : chests.values()) {
            config.set("locations." + i + ".world", bx.getLocation().getWorld().getName());
            config.set("locations." + i + ".x", bx.getLocation().getBlockX());
            config.set("locations." + i + ".y", bx.getLocation().getBlockY());
            config.set("locations." + i + ".z", bx.getLocation().getBlockZ());
            config.set("locations." + i + ".blockHeight", bx.getBlockHeight());
            i++;
        }

        saveConfig();
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadChests() {

        chests.clear();

        File file = new File(main.getDataFolder(), "locations.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if(!file.exists()) {
            try {
                file.createNewFile();
                config.set("locations", new ArrayList<>());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.file = file;
        this.config = config;

        if(!config.contains("locations")) {
            config.set("locations", new ArrayList<>());
        }

        saveConfig();

        Main.log.sendMessage(Utils.translate(""));
        Main.log.sendMessage(Utils.translate("  &eLoading chests:"));

        if(config.contains("locations")) {
            if(config.getConfigurationSection("locations") != null) {
                for (int i = 1; i <= config.getConfigurationSection("locations").getKeys(false).size(); i++) {
                    String world = config.getString("locations." + i + ".world");
                    int x = config.getInt("locations." + i + ".x");
                    int y = config.getInt("locations." + i + ".y");
                    int z = config.getInt("locations." + i + ".z");

                    if(Bukkit.getServer().getWorld(world) == null) continue;

                    double blockHeight = 0.875;
                    if(config.contains("locations." + i + ".blockHeight"))
                        blockHeight = config.getDouble("locations." + i + ".blockHeight");

                    Location loc = new Location(Bukkit.getWorld(world), x, y, z);
                    chests.put(loc, new RewardChest(world, loc, blockHeight));
                }
            }

            config.set("locations", new ArrayList<>());
            int i = 1;
            for(RewardChest bx : chests.values()) {
                if(bx.getLocation().getWorld() == null) continue;
                config.set("locations." + i + ".world", bx.getWorldName());
                config.set("locations." + i + ".x", bx.getLocation().getBlockX());
                config.set("locations." + i + ".y", bx.getLocation().getBlockY());
                config.set("locations." + i + ".z", bx.getLocation().getBlockZ());
                config.set("locations." + i + ".blockHeight", bx.getBlockHeight());
                i++;
            }
            saveConfig();
        }

        if(chests.size() == 0)
            Main.log.sendMessage(Utils.translate("    &cNo Reward Chests has been loaded!"));
        else
            Main.log.sendMessage(Utils.translate("    &b" + chests.size() + " &aReward Chests loaded!"));

    }

}
