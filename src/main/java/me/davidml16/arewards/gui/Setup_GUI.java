package me.davidml16.arewards.gui;

import me.davidml16.arewards.Constants;
import me.davidml16.arewards.Main;
import me.davidml16.arewards.conversation.RenameMenu;
import me.davidml16.arewards.conversation.TypeIconMenu;
import me.davidml16.arewards.objects.RewardType;
import me.davidml16.arewards.utils.*;
import me.davidml16.arewards.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Setup_GUI implements Listener {

    private HashMap<UUID, String> opened;
    private HashMap<String, Inventory> guis;

    private Main main;

    public Setup_GUI(Main main) {
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
        if(guis.containsKey(id)) return;

        Inventory gui = Bukkit.createInventory(null, 36, "%reward_type% | Configuration".replaceAll("%reward_type%", id));
        ItemStack edge = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("").toItemStack();

        FileConfiguration config = main.getRewardTypeHandler().getConfig(id);

        RewardType type = main.getRewardTypeHandler().getTypeBydId(id);
        List<String> lore = new ArrayList<>();
        for(String line : type.getLoreAvailable()) {
            lore.add(Utils.translate(line));
        }

        gui.setItem(10, new ItemBuilder(type.getAvailableIcon()).setName(Utils.translate("&aAvailable reward icon"))
                .setLore(
                        "",
                        Utils.translate(" &7You can change the "),
                        Utils.translate(" &7icon clicking this item "),
                        Utils.translate(" &7and opening icon setup "),
                        "",
                        Utils.translate("&eClick change available reward skull! ")
                )
                .toItemStack());

        gui.setItem(11, new ItemBuilder(type.getCooldownIcon()).setName(Utils.translate("&aCooldown reward icon"))
                .setLore(
                        "",
                        Utils.translate(" &7You can change the "),
                        Utils.translate(" &7icon clicking this item "),
                        Utils.translate(" &7and opening icon setup "),
                        "",
                        Utils.translate("&eClick change cooldown reward skull! ")
                )
                .toItemStack());

        gui.setItem(12, new ItemBuilder(XMaterial.ANVIL.parseItem()).setName(Utils.translate("&aReward type name"))
                .setLore(
                        "",
                        Utils.translate(" &7Click on the anvil "),
                        Utils.translate(" &7to start rename menu "),
                        "",
                        Utils.translate("&eClick to rename reward! ")
                )
                .toItemStack());

        gui.setItem(14, new ItemBuilder(XMaterial.GOLD_NUGGET.parseItem())
                .setName(Utils.translate("&aRewards"))
                .setLore(
                        "",
                        Utils.translate(" &7Open rewards gui and "),
                        Utils.translate(" &7click on new reward "),
                        Utils.translate(" &7to begin reward setup. "),
                        "",
                        Utils.translate(" &7Click the rewards item "),
                        Utils.translate(" &7in the GUI to remove it. "),
                        "",
                        Utils.translate("&eClick to config rewards! ")
                ).toItemStack());

        gui.setItem(16, new ItemBuilder(XMaterial.CHEST.parseItem()).setName(Utils.translate("&aSettings"))
                .setLore(
                        "",
                        Utils.translate(" &7Click on the chest "),
                        Utils.translate(" &7to change some settings "),
                        "",
                        Utils.translate(" &7- Slot "),
                        Utils.translate(" &7- Cooldown "),
                        Utils.translate(" &7- Required permission "),
                        Utils.translate(" &7- Vote settings "),
                        "",
                        Utils.translate("&eClick to open settings menu! ")
                )
                .toItemStack());
        
        gui.setItem(31, new ItemBuilder(XMaterial.BARRIER.parseItem())
                .setName(Utils.translate("&cReload configuration "))
                .setLore(
                        "",
                        Utils.translate(" &7Reload configuration to "),
                        Utils.translate(" &7update last changes made. "),
                        "",
                        Utils.translate("&eClick reload rewards! ")
                )
                .toItemStack());

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
        Inventory gui = guis.get(id);

        RewardType type = main.getRewardTypeHandler().getTypeBydId(id);

        gui.setItem(10, new ItemBuilder(type.getAvailableIcon()).setName(Utils.translate("&aAvailable reward icon"))
                .setLore(
                        "",
                        Utils.translate(" &7You can change the "),
                        Utils.translate(" &7icon clicking this item "),
                        Utils.translate(" &7and opening icon setup "),
                        "",
                        Utils.translate("&eClick change available reward skull! ")
                )
                .toItemStack());

        gui.setItem(11, new ItemBuilder(type.getCooldownIcon()).setName(Utils.translate("&aCooldown reward icon"))
                .setLore(
                        "",
                        Utils.translate(" &7You can change the "),
                        Utils.translate(" &7icon clicking this item "),
                        Utils.translate(" &7and opening icon setup "),
                        "",
                        Utils.translate("&eClick change cooldown reward skull! ")
                )
                .toItemStack());



        for(HumanEntity pl : gui.getViewers()) {
            pl.getOpenInventory().getTopInventory().setContents(gui.getContents());
        }
    }

    public void open(Player p, String id) {
        p.updateInventory();

        if(!guis.containsKey(id)) loadGUI(id);

        p.openInventory(guis.get(id));

        Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);
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
            String id = opened.get(p.getUniqueId());

            RewardType type = main.getRewardTypeHandler().getTypeBydId(id);

            if (slot == 10) {
                p.closeInventory();
                new TypeIconMenu(main).getConversation(p, type, "available").begin();
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.ANVIL_USE, 50, 3);
            } else if (slot == 11) {
                p.closeInventory();
                new TypeIconMenu(main).getConversation(p, type, "cooldown").begin();
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.ANVIL_USE, 50, 3);
            } else if (slot == 12) {
                p.closeInventory();
                new RenameMenu(main).getConversation(p, type).begin();
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.ANVIL_USE, 50, 3);
            } else if (slot == 14) {
                main.getEditRewardsGUI().open(p, id);
            } else if (slot == 16) {
                main.getEditSettingsGUI().open(p, id);
            } else if (slot == 31) {
                main.getPluginHandler().reloadAll();
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.ANVIL_USE, 50, 3);
                p.sendMessage(main.getLanguageHandler().getMessage("Commands.Reload"));
            }
        }

    }

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        opened.remove(p.getUniqueId());
    }

}