package me.davidml16.arewards.handlers;

import com.cryptomorin.xseries.XItemStack;
import me.davidml16.arewards.Constants;
import me.davidml16.arewards.Main;
import me.davidml16.arewards.objects.RewardType;
import me.davidml16.arewards.utils.SkullCreator;
import me.davidml16.arewards.utils.TimeAPI.TimeAPI;
import me.davidml16.arewards.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RewardTypeHandler {

    private HashMap<String, RewardType> types;
    private HashMap<String, File> typesFiles;
    private HashMap<String, YamlConfiguration> typesConfigs;

    private Main main;

    public RewardTypeHandler(Main main) {
        this.main = main;
        this.types = new HashMap<String, RewardType>();
        this.typesFiles = new HashMap<String, File>();
        this.typesConfigs = new HashMap<String, YamlConfiguration>();
    }

    public HashMap<String, RewardType> getTypes() {
        return types;
    }

    public HashMap<String, File> getTypesFiles() {
        return typesFiles;
    }

    public HashMap<String, YamlConfiguration> getTypesConfigs() {
        return typesConfigs;
    }

    public RewardType getTypeBydId(String id) {
        for (RewardType type : types.values()) {
            if (type.getId().equalsIgnoreCase(id))
                return type;
        }
        return null;
    }

    public boolean createType(String id) {
        File file = new File(main.getDataFolder(), "types/" + id + ".yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
                typesFiles.put(id, file);
                typesConfigs.put(id, YamlConfiguration.loadConfiguration(file));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean removeType(String id) {
        if(typesFiles.containsKey(id) && typesConfigs.containsKey(id)) {
            typesFiles.get(id).delete();
            typesFiles.remove(id);
            typesConfigs.remove(id);
            return true;
        }
        return false;
    }

    public void saveConfig(String id) {
        try {
            File file = typesFiles.get(id);
            if(file.exists()) {
                typesConfigs.get(id).save(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig(String id) {
        return typesConfigs.get(id);
    }

    public void loadTypes() {

        typesConfigs.clear();
        typesFiles.clear();
        types.clear();

        File directory = new File(main.getDataFolder(), "types");
        if(!directory.exists()) {
            directory.mkdir();
        }

        File[] allFiles = new File(main.getDataFolder(), "types").listFiles();
        for (File file : allFiles) {
            String id = file.getName().toLowerCase().replace(".yml", "");

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            typesFiles.put(id, file);
            typesConfigs.put(id, config);

            if(!Character.isDigit(id.charAt(0))) {
                if (validTypeData(config)) {

                    if (!config.contains("type.cooldown")) {
                        config.set("type.cooldown", "24h");
                    }

                    if (!config.contains("type.slot")) {
                        config.set("type.slot", -1);
                    }

                    if (!config.contains("type.require_permission")) {
                        config.set("type.require_permission.enabled", false);
                        config.set("type.require_permission.permission", "arewards.rewards.example");
                        config.set("type.require_permission.message", Arrays.asList(
                                "&cYou need at least Vip to be able to claim this reward!",
                                "&bPurchase ranks at &6store.example.net"
                        ));
                    }

                    if (!config.contains("type.require_vote")) {
                        config.set("type.require_vote.enabled", false);
                    }

                    if (!config.contains("type.require_vote.website_service")) {
                        config.set("type.require_vote.website_service", "example.com");
                    }

                    if (!config.contains("type.one_time")) {
                        config.set("type.one_time.enabled", false);
                    }

                    if (!config.contains("type.description")) {
                        List<String> lore = Arrays.asList(
                                "&7Claim a example reward"
                        );
                        config.set("type.description", lore);
                    }

                    if (!config.contains("type.lore.available")) {
                        List<String> loreAvailable = Arrays.asList(
                                "%description%",
                                "",
                                "&5Rewards:",
                                "%rewards%",
                                "",
                                "&6Click to claim reward!"
                        );
                        config.set("type.lore.available", loreAvailable);
                    }

                    if (!config.contains("type.lore.cooldown")) {
                        List<String> cooldown = Arrays.asList(
                                "%description%",
                                "",
                                "&5Rewards:",
                                "%rewards%",
                                "",
                                "&6%cooldown%"
                        );
                        config.set("type.lore.cooldown", cooldown);
                    }

                    if (!config.contains("type.lore.permission")) {
                        List<String> permission = Arrays.asList(
                                "%description%",
                                "",
                                "&5Rewards:",
                                "%rewards%",
                                "",
                                "&cYou need at least &7Example &cto be able to claim this reward!",
                                "&bPurchase ranks at &6store.example.net"
                        );
                        config.set("type.lore.permission", permission);
                    }

                    if (!config.contains("type.lore.claimed")) {
                        List<String> permission = Arrays.asList(
                                "%description%",
                                "",
                                "&5Rewards:",
                                "%rewards%",
                                "",
                                "&8&oThis reward can only be claimed once.",
                                "",
                                "&cYou have already claimed this reward!"
                        );
                        config.set("type.lore.claimed", permission);
                    }

                    if (!config.contains("type.icon.available.item")) {
                        config.set("type.icon.available.item", "base64:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNlZjlhYTE0ZTg4NDc3M2VhYzEzNGE0ZWU4OTcyMDYzZjQ2NmRlNjc4MzYzY2Y3YjFhMjFhODViNyJ9fX0=");
                    }

                    if (!config.contains("type.icon.cooldown.item")) {
                        config.set("type.icon.cooldown.item", "base64:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzZkMWZhYmRmM2UzNDI2NzFiZDlmOTVmNjg3ZmUyNjNmNDM5ZGRjMmYxYzllYThmZjE1YjEzZjFlN2U0OGI5In19fQ==");
                    }

                    if (!config.contains("type.rewards")) {
                        config.set("type.rewards", new ArrayList<>());
                    }

                    saveConfig(id);

                    String name = config.getString("type.name");

                    int slot = config.getInt("type.slot");

                    RewardType rewardType = new RewardType(main, id, name, slot);
                    types.put(id, rewardType);

                    if (!(config.get("type.icon.available.item") instanceof MemorySection)) {
                        String[] icon = ((String) config.get("type.icon.available.item")).split(":");
                        switch (icon[0].toLowerCase()) {
                            case "base64":
                                rewardType.setAvailableIcon(SkullCreator.itemFromBase64(icon[1]));
                                break;
                            case "uuid":
                                rewardType.setAvailableIcon(SkullCreator.itemFromUuid(UUID.fromString(icon[1])));
                                break;
                            case "name":
                                rewardType.setAvailableIcon(SkullCreator.itemFromName(icon[1]));
                                break;
                        }
                    } else {
                        rewardType.setAvailableIcon(XItemStack.deserialize(Utils.getConfigurationSection(config, "type.icon.available.item")));
                    }

                    if (!(config.get("type.icon.cooldown.item") instanceof MemorySection)) {
                        String[] icon = ((String) config.get("type.icon.cooldown.item")).split(":");
                        switch (icon[0].toLowerCase()) {
                            case "base64":
                                rewardType.setCooldownIcon(SkullCreator.itemFromBase64(icon[1]));
                                break;
                            case "uuid":
                                rewardType.setCooldownIcon(SkullCreator.itemFromUuid(UUID.fromString(icon[1])));
                                break;
                            case "name":
                                rewardType.setCooldownIcon(SkullCreator.itemFromName(icon[1]));
                                break;
                        }
                    } else {
                        rewardType.setCooldownIcon(XItemStack.deserialize(Utils.getConfigurationSection(config, "type.icon.cooldown.item")));
                    }

                    List<String> loreAvailable = new ArrayList<>();
                    for(String line : config.getStringList("type.lore.available")) {
                        if(line.contains("%description%")) {
                            loreAvailable.addAll(config.getStringList("type.description"));
                        } else {
                            loreAvailable.add(line);
                        }
                    }
                    rewardType.setLoreAvailable(loreAvailable);

                    List<String> loreCooldown = new ArrayList<>();
                    for(String line : config.getStringList("type.lore.cooldown")) {
                        if(line.contains("%description%")) {
                            loreCooldown.addAll(config.getStringList("type.description"));
                        } else {
                            loreCooldown.add(line);
                        }
                    }
                    rewardType.setLoreCooldown(loreCooldown);

                    List<String> lorePermission = new ArrayList<>();
                    for(String line : config.getStringList("type.lore.permission")) {
                        if(line.contains("%description%")) {
                            lorePermission.addAll(config.getStringList("type.description"));
                        } else {
                            lorePermission.add(line);
                        }
                    }
                    rewardType.setLorePermission(lorePermission);

                    List<String> claimedPermission = new ArrayList<>();
                    for(String line : config.getStringList("type.lore.claimed")) {
                        if(line.contains("%description%")) {
                            claimedPermission.addAll(config.getStringList("type.description"));
                        } else {
                            claimedPermission.add(line);
                        }
                    }
                    rewardType.setLoreClaimed(claimedPermission);

                    rewardType.setRequirePermission(config.getBoolean("type.require_permission.enabled"));

                    rewardType.setPermission(config.getString("type.require_permission.permission"));

                    rewardType.setNoPermissionMessage(config.getStringList("type.require_permission.message"));

                    rewardType.setNeedVote(config.getBoolean("type.require_vote.enabled"));
                    rewardType.setVoteService(config.getString("type.require_vote.website_service"));

                    rewardType.setOneTime(config.getBoolean("type.one_time.enabled"));

                    rewardType.setDescription(config.getStringList("type.description"));

                    long convertedTime;
                    if(Objects.requireNonNull(config.getString("type.cooldown")).equalsIgnoreCase(""))
                        convertedTime = 0;
                    else
                        convertedTime = new TimeAPI(config.getString("type.cooldown")).getMilliseconds();
                    rewardType.setCooldown(convertedTime);

                    rewardType.setCooldownString(config.getString("type.cooldown"));

                }
            }
        }



    }

    private boolean validTypeData(FileConfiguration config) {
        return config.contains("type.name");
    }

    public boolean typeExist(String id) {
        return typesFiles.containsKey(id);
    }

    public void printLog() {

        Main.log.sendMessage(Utils.translate(""));
        Main.log.sendMessage(Utils.translate("  &eLoading reward types:"));

        int longestWord = longestID();

        for(RewardType rewardType : types.values()) {

            String log = "    &a'" + rewardType.getId() + "' " + getStringSpaces(rewardType.getId(), longestWord) + "&7► ";

            int rewards = rewardType.getRewards().size();
            log += (rewards > 0 ? "&a" : "&c") + rewards + " rewards";

            Main.log.sendMessage(Utils.translate(log));

        }

        if(types.size() == 0) Main.log.sendMessage(Utils.translate("    &cNo reward types has been loaded!"));
        Main.log.sendMessage(Utils.translate(" "));

    }

    public int longestID() {
        int longest = 0;

        for(String rewardID : types.keySet()) {
            if(rewardID.length() > longest) longest = rewardID.length();
        }

        return longest;
    }

    public String getStringSpaces(String word, int longest) {
        String str = "";

        if(word.length() >= longest) return str;

        str += StringUtils.repeat(" ", longest - word.length());

        return str;
    }

    public boolean haveRewardPermission(Player player, RewardType rewardType) {
        return player.hasPermission(rewardType.getPermission());
    }

}