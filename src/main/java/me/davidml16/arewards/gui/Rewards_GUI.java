package me.davidml16.arewards.gui;

import me.davidml16.arewards.Main;
import me.davidml16.arewards.objects.GUILayout;
import me.davidml16.arewards.objects.RewardCollected;
import me.davidml16.arewards.objects.RewardType;
import me.davidml16.arewards.objects.Profile;
import me.davidml16.arewards.objects.rewards.Reward;
import me.davidml16.arewards.utils.*;
import me.davidml16.arewards.utils.TimeAPI.TimeUtils;
import me.davidml16.arewards.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.*;

public class Rewards_GUI implements Listener {

    private List<UUID> opened;
    private Main main;

    public Rewards_GUI(Main main) {
        this.main = main;
        this.opened = new ArrayList<>();
        this.main.getServer().getPluginManager().registerEvents(this, this.main);
    }

    public List<UUID> getOpened() {
        return opened;
    }

    public void reloadPage(Player p) {
        open(p);
    }

    public void open(Player p) {

        GUILayout guiLayout = main.getLayoutHandler().getLayout("rewards");

        int size;
        if(guiLayout.getInteger("Size") < 9 && guiLayout.getInteger("Size") > 54)
            size = 54;
        else
            size = guiLayout.getInteger("Size");

        Inventory gui = Bukkit.createInventory(null, size, guiLayout.getMessage("Title"));

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {

            Profile profile = main.getPlayerDataHandler().getData(p.getUniqueId());
            List<RewardCollected> rewardCollecteds = new ArrayList<>(profile.getRewards());

            ItemStack close = new ItemBuilder(XMaterial.matchXMaterial(guiLayout.getMessage("Items.Close.Material")).get().parseItem())
                    .setName(guiLayout.getMessage("Items.Close.Name"))
                    .setLore(guiLayout.getMessageList("Items.Close.Lore"))
                    .toItemStack();
            close = NBTEditor.set(close, "close", "action");
            gui.setItem((size - 10) + guiLayout.getSlot("Close"), close);

            for(RewardType rewardType : main.getRewardTypeHandler().getTypes().values()) {

                if(rewardType.getSlot() >= 0 && rewardType.getSlot() < size)

                    gui.setItem(rewardType.getSlot(), getRewardItem(p, guiLayout, rewardType, rewardCollecteds));

            }

            Bukkit.getScheduler().runTask(main, () -> {
                p.openInventory(gui);
                opened.add(p.getUniqueId());
            });

        });
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() == Material.AIR) return;

        if (opened.contains(p.getUniqueId())) {
            e.setCancelled(true);

            String action = NBTEditor.getString(e.getCurrentItem(), "action");

            if(e.getClick() == ClickType.DOUBLE_CLICK) return;

            if(action.equalsIgnoreCase("close")) {
                p.closeInventory();
                return;
            }

            String rewardID = NBTEditor.getString(e.getCurrentItem(), "rewardID");
            RewardType rewardType = main.getRewardTypeHandler().getTypeBydId(rewardID);

            switch (Objects.requireNonNull(action)) {
                case "claim":

                    RewardCollected rewardCollected = new RewardCollected(p.getUniqueId(), rewardID);
                    try {
                        main.getDatabaseHandler().addRewardCollected(rewardCollected.getUuid(), rewardCollected.getRewardID(), rewardCollected.getExpire());
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    main.getPlayerDataHandler().getData(p).getRewards().add(rewardCollected);

                    main.getRewardHandler().giveReward(p, main.getRewardTypeHandler().getTypeBydId(rewardID));

                    sendClaimedMessage(p, rewardType);

                    main.getHologramHandler().reloadHolograms(p);

                    open(p);

                    break;
                case "cooldown":

                    Bukkit.getScheduler().runTaskAsynchronously(main, () -> {

                        Optional<RewardCollected> rewards = main.getPlayerDataHandler().getData(p).getRewards()
                                .stream().filter(rc -> rc.getRewardID().equalsIgnoreCase(rewardType.getId())).findFirst();

                        if (rewards.isPresent()) {

                            RewardCollected rewardCollected1 = rewards.get();

                            String cooldownMessage = main.getLanguageHandler().getMessage("Rewards.Cooldown");
                            cooldownMessage = cooldownMessage.replaceAll("%cooldown%",
                                    TimeUtils.millisToLongDHMS(rewardCollected1.getExpire() - System.currentTimeMillis()));

                            String finalCooldownMessage = cooldownMessage;
                            Bukkit.getScheduler().runTask(main, () -> {
                                p.sendMessage(Utils.translate(finalCooldownMessage));
                            });

                        }

                    });

                    break;
                case "no_permission":

                    for(String line : rewardType.getNoPermissionMessage()) {
                        p.sendMessage(Utils.translate(line));
                    }

                    break;
            }

            p.updateInventory();
        }
    }

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        opened.remove(p.getUniqueId());
    }

    public ItemStack getRewardItem(Player player, GUILayout guiLayout, RewardType rewardType, List<RewardCollected> rewardCollecteds) {

        Optional<RewardCollected> rewardCollected = rewardCollecteds.stream().filter(rc -> rc.getRewardID().equalsIgnoreCase(rewardType.getId())).findFirst();

        List<String> lore = new ArrayList<>();

        List<String> rewardsLore = new ArrayList<>();
        for(Reward reward : rewardType.getRewards()) {
            rewardsLore.add(Utils.translate(guiLayout.getMessage("Items.Reward.DisplayLine").replaceAll("%reward_name%", reward.getName())));
        }
        if(rewardType.getRewards().size() == 0)
            rewardsLore.add(Utils.translate("&7- &cN/A"));

        if(rewardType.isRequirePermission()) {

            if(!main.getRewardTypeHandler().haveRewardPermission(player, rewardType)) {

                ItemStack itemStack = rewardType.getAvailableIcon();

                for (String line : rewardType.getLorePermission()) {

                    if(!line.contains("%rewards%"))
                        lore.add(Utils.translate(line));
                    else
                        lore.addAll(rewardsLore);

                }

                itemStack = new ItemBuilder(itemStack)
                        .setName(Utils.translate(rewardType.getName()))
                        .setLore(lore)
                        .toItemStack();

                itemStack = NBTEditor.set(itemStack, "no_permission", "action");
                itemStack = NBTEditor.set(itemStack, rewardType.getId(), "rewardID");

                return itemStack;

            }

        }

        if(!rewardCollected.isPresent()) {

            ItemStack itemStack = rewardType.getAvailableIcon();

            for (String line : rewardType.getLoreAvailable()) {

                if(!line.contains("%rewards%"))
                    lore.add(Utils.translate(line));
                else
                    lore.addAll(rewardsLore);

            }

            itemStack = new ItemBuilder(itemStack)
                    .setName(Utils.translate(rewardType.getName()))
                    .setLore(lore)
                    .toItemStack();

            itemStack = NBTEditor.set(itemStack, "claim", "action");
            itemStack = NBTEditor.set(itemStack, rewardType.getId(), "rewardID");

            return itemStack;

        } else {

            ItemStack itemStack = rewardType.getCooldownIcon();

            for (String line : rewardType.getLoreCooldown()) {

                if(!line.contains("%rewards%"))
                    lore.add(Utils.translate(line
                            .replaceAll("%cooldown%", TimeUtils.millisToLongDHMS(rewardCollected.get().getExpire() - System.currentTimeMillis()))));
                else
                    lore.addAll(rewardsLore);


            }

            itemStack = new ItemBuilder(itemStack)
                    .setName(Utils.translate(rewardType.getName()))
                    .setLore(lore)
                    .toItemStack();

            itemStack = NBTEditor.set(itemStack, "cooldown", "action");
            itemStack = NBTEditor.set(itemStack, rewardType.getId(), "rewardID");

            return itemStack;

        }

    }

    private void sendClaimedMessage(Player player, RewardType rewardType) {

        List<String> rewardsLore = new ArrayList<>();
        for(Reward reward : rewardType.getRewards()) {
            rewardsLore.add(Utils.translate(main.getLanguageHandler().getMessage("Rewards.Reward").replaceAll("%reward_name%", reward.getName())));
        }

        List<String> lore = new ArrayList<>();
        for (String line : main.getLanguageHandler().getMessageList("Rewards.Claimed")) {
            if(!line.contains("%rewards%"))
                lore.add(Utils.translate(line.replaceAll("%center%", "")));
            else
                lore.addAll(rewardsLore);
        }

        for (String line : lore)
            if(!line.contains("%center%"))
                player.sendMessage(Utils.translate(line));
            else
                player.sendMessage(Utils.translate(MessageUtils.centeredMessage(line)));

    }

}
