package me.davidml16.arewards.gui;

import me.davidml16.arewards.Constants;
import me.davidml16.arewards.Main;
import me.davidml16.arewards.conversation.CooldownMenu;
import me.davidml16.arewards.conversation.RenameMenu;
import me.davidml16.arewards.conversation.SlotMenu;
import me.davidml16.arewards.conversation.VoteServiceMenu;
import me.davidml16.arewards.conversation.rewards.CommandRewardMenu;
import me.davidml16.arewards.conversation.rewards.EditCommandRewardMenu;
import me.davidml16.arewards.conversation.rewards.EditItemRewardMenu;
import me.davidml16.arewards.conversation.rewards.ItemRewardMenu;
import me.davidml16.arewards.objects.Pair;
import me.davidml16.arewards.objects.RewardType;
import me.davidml16.arewards.objects.rewards.CommandReward;
import me.davidml16.arewards.objects.rewards.ItemReward;
import me.davidml16.arewards.objects.rewards.Reward;
import me.davidml16.arewards.utils.ItemBuilder;
import me.davidml16.arewards.utils.Sounds;
import me.davidml16.arewards.utils.Utils;
import me.davidml16.arewards.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class EditSettings_GUI implements Listener {

    private HashMap<UUID, String> opened;
    private HashMap<String, Inventory> guis;

    private Main main;

    public EditSettings_GUI(Main main) {
        this.main = main;
        this.opened = new HashMap<UUID, String>();
        this.guis = new HashMap<String, Inventory>();
        this.main.getServer().getPluginManager().registerEvents(this, this.main);
    }

    public HashMap<UUID, String> getOpened() {
        return opened;
    }

    public HashMap<String, Inventory> getGuis() {
        return guis;
    }

    public void loadGUI() {
        for (File file : Objects.requireNonNull(new File(main.getDataFolder(), "types").listFiles())) {
            loadGUI(file.getName().toLowerCase().replace(".yml", ""));
        }
    }

    public void loadGUI(String id) {
        if (guis.containsKey(id)) return;

        Inventory gui = Bukkit.createInventory(null, 36, "%reward_type% | Settings".replaceAll("%reward_type%", id));

        ItemStack back = new ItemBuilder(XMaterial.BOOK.parseItem()).setName(Utils.translate("&aBack to setup menu")).toItemStack();

        ItemStack edge = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("").toItemStack();

        RewardType type = main.getRewardTypeHandler().getTypeBydId(id);

        gui.setItem(10, new ItemBuilder(XMaterial.COMPASS.parseItem()).setName(Utils.translate("&aSlot"))
                .setLore(
                        "",
                        Utils.translate(" &7Change the slot of this "),
                        Utils.translate(" &7reward in the menu "),
                        "",
                        Utils.translate(" &7Slot: &6" + type.getSlot() + " "),
                        "",
                        Utils.translate("&eClick to edit! ")
                )
                .toItemStack());

        if(type.isRequirePermission()) {
            gui.setItem(12, new ItemBuilder(XMaterial.LIME_DYE.parseItem()).setName(Utils.translate("&aRequired permission"))
                    .setLore(
                            "",
                            Utils.translate(" &7Toggle if you need permission "),
                            Utils.translate(" &7to claim this reward "),
                            "",
                            Utils.translate(" &7Permission: &6" + type.getPermission() + " "),
                            "",
                            Utils.translate("&eClick to disable! ")
                    )
                    .toItemStack());
        } else {
            gui.setItem(12, new ItemBuilder(XMaterial.GRAY_DYE.parseItem()).setName(Utils.translate("&cRequired permission"))
                    .setLore(
                            "",
                            Utils.translate(" &7Toggle if you need permission "),
                            Utils.translate(" &7to claim this reward "),
                            "",
                            Utils.translate(" &7Permission: &6" + type.getPermission() + " "),
                            "",
                            Utils.translate("&eClick to enable! ")
                    )
                    .toItemStack());
        }

        if(type.isNeedVote()) {

            gui.setItem(16, new ItemBuilder(XMaterial.LIME_DYE.parseItem()).setName(Utils.translate("&aRequired vote"))
                    .setLore(
                            "",
                            Utils.translate(" &7Toggle if you need vote "),
                            Utils.translate(" &7to claim this reward "),
                            "",
                            Utils.translate("&eClick to disable! ")
                    )
                    .toItemStack());

            gui.setItem(25, new ItemBuilder(XMaterial.MAGMA_CREAM.parseItem()).setName(Utils.translate("&aVote website service"))
                    .setLore(
                            "",
                            Utils.translate(" &7Change the vote website "),
                            Utils.translate(" &7service from you perform the vote, "),
                            Utils.translate(" &7for example: &6minecraftservers.org. "),
                            "",
                            Utils.translate(" &7Vote service: &6" + type.getVoteService() + " "),
                            "",
                            Utils.translate("&eClick to edit! ")
                    )
                    .toItemStack());

        } else {

            gui.setItem(16, new ItemBuilder(XMaterial.GRAY_DYE.parseItem()).setName(Utils.translate("&cRequired vote"))
                    .setLore(
                            "",
                            Utils.translate(" &7Toggle if you need vote "),
                            Utils.translate(" &7to claim this reward "),
                            "",
                            Utils.translate("&eClick to enable! ")
                    )
                    .toItemStack());

            gui.setItem(25, edge);

        }

        if(type.isOneTime()) {
            gui.setItem(14, new ItemBuilder(XMaterial.LIME_DYE.parseItem()).setName(Utils.translate("&aOne time claim"))
                    .setLore(
                            "",
                            Utils.translate(" &7Toggle if this reward can "),
                            Utils.translate(" &7only be claimed once. "),
                            "",
                            Utils.translate("&eClick to disable! ")
                    )
                    .toItemStack());
        } else {
            gui.setItem(14, new ItemBuilder(XMaterial.GRAY_DYE.parseItem()).setName(Utils.translate("&cOne time claim"))
                    .setLore(
                            "",
                            Utils.translate(" &7Toggle if this reward can "),
                            Utils.translate(" &7only be claimed once. "),
                            "",
                            Utils.translate("&eClick to enable! ")
                    )
                    .toItemStack());
        }

        gui.setItem(19, new ItemBuilder(XMaterial.CLOCK.parseItem()).setName(Utils.translate("&aCooldown"))
                .setLore(
                        "",
                        Utils.translate(" &7Change the cooldown "),
                        Utils.translate(" &7to claim again this reward "),
                        "",
                        Utils.translate(" &7Cooldown: &6" + type.getCooldownString() + " "),
                        "",
                        Utils.translate("&eClick to edit! ")
                )
                .toItemStack());

        gui.setItem(31, back);

        for (int i = 0; i < 36; i++) {
            if(gui.getItem(i) == null) {
                gui.setItem(i, edge);
            }
        }

        guis.put(id, gui);
    }

    public void reloadAllGUI() {
        for(String id : main.getRewardTypeHandler().getTypes().keySet()) {
            loadGUI(id);
            reloadGUI(id);
        }
    }

    public void reloadGUI(String id) {
        for(UUID uuid : opened.keySet()) {
            if(opened.get(uuid).equals(id)) {
                Player p = Bukkit.getPlayer(uuid);
                openPage(p, id);
            }
        }
    }

    private void openPage(Player p, String id) {

        Inventory gui = Bukkit.createInventory(null, 36, "%reward_type% | Settings".replaceAll("%reward_type%", id));

        if(!guis.containsKey(id)) loadGUI(id);

        gui.setContents(guis.get(id).getContents());

        ItemStack edge = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("").toItemStack();

        RewardType type = main.getRewardTypeHandler().getTypeBydId(id);

        gui.setItem(10, new ItemBuilder(XMaterial.COMPASS.parseItem()).setName(Utils.translate("&aSlot"))
                .setLore(
                        "",
                        Utils.translate(" &7Change the slot of this "),
                        Utils.translate(" &7reward in the menu "),
                        "",
                        Utils.translate(" &7Slot: &6" + type.getSlot() + " "),
                        "",
                        Utils.translate("&eClick to edit! ")
                )
                .toItemStack());

        if(type.isRequirePermission()) {
            gui.setItem(12, new ItemBuilder(XMaterial.LIME_DYE.parseItem()).setName(Utils.translate("&aRequired permission"))
                    .setLore(
                            "",
                            Utils.translate(" &7Toggle if you need permission "),
                            Utils.translate(" &7to claim this reward "),
                            "",
                            Utils.translate(" &7Permission: &6" + type.getPermission() + " "),
                            "",
                            Utils.translate("&eClick to disable! ")
                    )
                    .toItemStack());
        } else {
            gui.setItem(12, new ItemBuilder(XMaterial.GRAY_DYE.parseItem()).setName(Utils.translate("&cRequired permission"))
                    .setLore(
                            "",
                            Utils.translate(" &7Toggle if you need permission "),
                            Utils.translate(" &7to claim this reward "),
                            "",
                            Utils.translate(" &7Permission: &6" + type.getPermission() + " "),
                            "",
                            Utils.translate("&eClick to enable! ")
                    )
                    .toItemStack());
        }

        if(type.isNeedVote()) {

            gui.setItem(16, new ItemBuilder(XMaterial.LIME_DYE.parseItem()).setName(Utils.translate("&aRequired vote"))
                    .setLore(
                            "",
                            Utils.translate(" &7Toggle if you need vote "),
                            Utils.translate(" &7to claim this reward "),
                            "",
                            Utils.translate("&eClick to disable! ")
                    )
                    .toItemStack());

            gui.setItem(25, new ItemBuilder(XMaterial.MAGMA_CREAM.parseItem()).setName(Utils.translate("&aVote website service"))
                    .setLore(
                            "",
                            Utils.translate(" &7Change the vote website "),
                            Utils.translate(" &7service from you perform the vote, "),
                            Utils.translate(" &7for example: &6minecraftservers.org. "),
                            "",
                            Utils.translate(" &7Vote service: &6" + type.getVoteService() + " "),
                            "",
                            Utils.translate("&eClick to edit! ")
                    )
                    .toItemStack());

        } else {

            gui.setItem(16, new ItemBuilder(XMaterial.GRAY_DYE.parseItem()).setName(Utils.translate("&cRequired vote"))
                    .setLore(
                            "",
                            Utils.translate(" &7Toggle if you need vote "),
                            Utils.translate(" &7to claim this reward "),
                            "",
                            Utils.translate("&eClick to enable! ")
                    )
                    .toItemStack());

            gui.setItem(25, edge);

        }

        if(type.isOneTime()) {
            gui.setItem(14, new ItemBuilder(XMaterial.LIME_DYE.parseItem()).setName(Utils.translate("&aOne time claim"))
                    .setLore(
                            "",
                            Utils.translate(" &7Toggle if this reward can "),
                            Utils.translate(" &7only be claimed once. "),
                            "",
                            Utils.translate("&eClick to disable! ")
                    )
                    .toItemStack());
        } else {
            gui.setItem(14, new ItemBuilder(XMaterial.GRAY_DYE.parseItem()).setName(Utils.translate("&cOne time claim"))
                    .setLore(
                            "",
                            Utils.translate(" &7Toggle if this reward can "),
                            Utils.translate(" &7only be claimed once. "),
                            "",
                            Utils.translate("&eClick to enable! ")
                    )
                    .toItemStack());
        }

        gui.setItem(19, new ItemBuilder(XMaterial.CLOCK.parseItem()).setName(Utils.translate("&aCooldown"))
                .setLore(
                        "",
                        Utils.translate(" &7Change the cooldown "),
                        Utils.translate(" &7to claim again this reward "),
                        "",
                        Utils.translate(" &7Cooldown: &6" + type.getCooldownString() + " "),
                        "",
                        Utils.translate("&eClick to edit! ")
                )
                .toItemStack());

        if (!opened.containsKey(p.getUniqueId())) {
            p.openInventory(gui);
        } else {
            p.getOpenInventory().getTopInventory().setContents(gui.getContents());
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> opened.put(p.getUniqueId(), id), 1L);
    }

    public void open(Player p, String id) {
        p.updateInventory();
        openPage(p, id);

        Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 10, 2);
        Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> opened.put(p.getUniqueId(), id), 1L);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null) return;

        if (opened.containsKey(p.getUniqueId())) {

            e.setCancelled(true);

            int slot = e.getRawSlot();

            RewardType rewardType = main.getRewardTypeHandler().getTypeBydId(opened.get(p.getUniqueId()));

            if (slot == 10) {
                p.closeInventory();
                new SlotMenu(main).getConversation(p, rewardType).begin();
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.ANVIL_USE, 50, 3);
            } else if (slot == 19) {
                p.closeInventory();
                new CooldownMenu(main).getConversation(p, rewardType).begin();
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.ANVIL_USE, 50, 3);
            } else if (slot == 12) {
                rewardType.setRequirePermission(!rewardType.isRequirePermission());
                rewardType.save();
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);
                reloadGUI(rewardType.getId());
            } else if (slot == 16) {
                rewardType.setNeedVote(!rewardType.isNeedVote());
                rewardType.save();
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);
                reloadGUI(rewardType.getId());
            } else if (slot == 14) {
                rewardType.setOneTime(!rewardType.isOneTime());
                rewardType.save();
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);
                reloadGUI(rewardType.getId());
            } else if (slot == 25 && rewardType.isNeedVote()) {
                p.closeInventory();
                new VoteServiceMenu(main).getConversation(p, rewardType).begin();
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.ANVIL_USE, 50, 3);
            } else if (slot == 31) {
                main.getSetupGUI().open(p, rewardType.getId());
            }

        }
    }

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        opened.remove(p.getUniqueId());
    }

}
