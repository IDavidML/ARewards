package me.davidml16.arewards.gui;

import me.davidml16.arewards.Constants;
import me.davidml16.arewards.Main;
import me.davidml16.arewards.objects.GUILayout;
import me.davidml16.arewards.objects.RewardChest;
import me.davidml16.arewards.utils.Utils;
import me.davidml16.arewards.utils.ItemBuilder;
import me.davidml16.arewards.utils.SkullCreator;
import me.davidml16.arewards.utils.Sounds;
import me.davidml16.arewards.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class EditChest_GUI implements Listener {

    private HashMap<UUID, RewardChest> opened;

    private Main main;

    public EditChest_GUI(Main main) {
        this.main = main;
        this.opened = new HashMap<UUID, RewardChest>();
        this.main.getServer().getPluginManager().registerEvents(this, this.main);
    }

    public HashMap<UUID, RewardChest> getOpened() {
        return opened;
    }

    public void reloadGUI(RewardChest box) {
        Inventory gui = Bukkit.createInventory(null, 36, "Reward chest editor");
        ItemStack edge = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("").toItemStack();

        ItemStack upArrow = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk5YWFmMjQ1NmE2MTIyZGU4ZjZiNjI2ODNmMmJjMmVlZDlhYmI4MWZkNWJlYTFiNGMyM2E1ODE1NmI2NjkifX19");
        ItemStack downArrow = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzkxMmQ0NWIxYzc4Y2MyMjQ1MjcyM2VlNjZiYTJkMTU3NzdjYzI4ODU2OGQ2YzFiNjJhNTQ1YjI5YzcxODcifX19");
        ItemStack remove = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGVkNjc5OTE0OTc4OGI5ZTkwMTY4MTFkM2EzZDBlZDFmNTUyNTMwZDY3Zjk4Njk0NTAzMmQ2ZTQzOWZhODk5ZCJ9fX0=");

        gui.setItem(11, new ItemBuilder(upArrow).setName(Utils.translate("&aIncrease height")).setLore(
                "",
                Utils.translate(" &7Actual height: &6" + String.format("%.3f", box.getBlockHeight())),
                Utils.translate(" &7New height: &6" + String.format("%.3f", (box.getBlockHeight() + 0.015))),
                "",
                Utils.translate("&eClick to increase height")
        ).toItemStack());
        gui.setItem(12, new ItemBuilder(XMaterial.ANVIL.parseMaterial()).setName(Utils.translate("&aReset height to default")).setLore(
                "",
                Utils.translate(" &7New height: &6" + String.format("%.3f", Constants.BLOCK_HEIGHT_DEFAULT)),
                "",
                Utils.translate("&eClick » &aReset height")
        ).toItemStack());
        gui.setItem(13, new ItemBuilder(downArrow).setName(Utils.translate("&aDecrease height")).setLore(
                "",
                Utils.translate(" &7Actual height: &6" + String.format("%.3f", box.getBlockHeight())),
                Utils.translate(" &7New height: &6" + String.format("%.3f", (box.getBlockHeight() - 0.015))),
                "",
                Utils.translate("&eClick to decrease height")
        ).toItemStack());
        gui.setItem(15, new ItemBuilder(remove).setName(Utils.translate("&cRemove reward chest")).setLore(
                "",
                Utils.translate("&eClick to remove this chest")
        ).toItemStack());

        for (int i = 0; i < 36; i++) {
            if(gui.getItem(i) == null) {
                gui.setItem(i, edge);
            }
        }

        GUILayout guiLayout = main.getLayoutHandler().getLayout("rewards");
        ItemStack back = new ItemBuilder(XMaterial.matchXMaterial(guiLayout.getMessage("Items.Close.Material")).get().parseItem())
                .setName(guiLayout.getMessage("Items.Close.Name"))
                .setLore(guiLayout.getMessageList("Items.Close.Lore"))
                .toItemStack();
        gui.setItem(31, back);

        for(UUID uuid : opened.keySet()) {
            if(opened.get(uuid) == box) {
                Objects.requireNonNull(Bukkit.getPlayer(uuid)).getOpenInventory().getTopInventory().setContents(gui.getContents());
            }
        }
    }

    public void open(Player p, RewardChest box) {
        p.updateInventory();

        Inventory gui = Bukkit.createInventory(null, 36, "Reward chest editor");
        ItemStack edge = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("").toItemStack();

        ItemStack upArrow = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk5YWFmMjQ1NmE2MTIyZGU4ZjZiNjI2ODNmMmJjMmVlZDlhYmI4MWZkNWJlYTFiNGMyM2E1ODE1NmI2NjkifX19");
        ItemStack downArrow = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzkxMmQ0NWIxYzc4Y2MyMjQ1MjcyM2VlNjZiYTJkMTU3NzdjYzI4ODU2OGQ2YzFiNjJhNTQ1YjI5YzcxODcifX19");
        ItemStack remove = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGVkNjc5OTE0OTc4OGI5ZTkwMTY4MTFkM2EzZDBlZDFmNTUyNTMwZDY3Zjk4Njk0NTAzMmQ2ZTQzOWZhODk5ZCJ9fX0=");

        gui.setItem(11, new ItemBuilder(upArrow).setName(Utils.translate("&aIncrease height")).setLore(
                "",
                Utils.translate(" &7Actual height: &6" + String.format("%.3f", box.getBlockHeight())),
                Utils.translate(" &7New height: &6" + String.format("%.3f", (box.getBlockHeight() + 0.015))),
                "",
                Utils.translate("&eClick to increase height")
        ).toItemStack());
        gui.setItem(12, new ItemBuilder(XMaterial.ANVIL.parseMaterial()).setName(Utils.translate("&aReset height to default")).setLore(
                "",
                Utils.translate(" &7New height: &6" + String.format("%.3f", Constants.BLOCK_HEIGHT_DEFAULT)),
                "",
                Utils.translate("&eClick » &aReset height")
        ).toItemStack());
        gui.setItem(13, new ItemBuilder(downArrow).setName(Utils.translate("&aDecrease height")).setLore(
                "",
                Utils.translate(" &7Actual height: &6" + String.format("%.3f", box.getBlockHeight())),
                Utils.translate(" &7New height: &6" + String.format("%.3f", (box.getBlockHeight() - 0.015))),
                "",
                Utils.translate("&eClick to decrease height")
        ).toItemStack());
        gui.setItem(15, new ItemBuilder(remove).setName(Utils.translate("&cRemove reward chest")).setLore(
                "",
                Utils.translate("&eClick to remove this chest")
        ).toItemStack());

        for (int i = 0; i < 36; i++) {
            if(gui.getItem(i) == null) {
                gui.setItem(i, edge);
            }
        }

        ItemStack back = new ItemBuilder(XMaterial.BOOK.parseItem())
                .setName(Utils.translate("&9Close"))
                .setLore(Utils.translate("&7Click to close"), Utils.translate("&7this menu."))
                .toItemStack();
        gui.setItem(31, back);

        p.openInventory(gui);

        Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);
        Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> opened.put(p.getUniqueId(), box), 1L);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null) return;

        if (opened.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            int slot = e.getRawSlot();
            RewardChest chest = opened.get(p.getUniqueId());

            if(slot == 11) {
                chest.setBlockHeight(chest.getBlockHeight() + 0.015);
                main.getHologramHandler().moveHologram(chest);
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);
                reloadGUI(chest);
            } else if(slot == 12) {
                if(e.getClick() == ClickType.LEFT || e.getClick() == ClickType.RIGHT) {
                    chest.setBlockHeight(Constants.BLOCK_HEIGHT_DEFAULT);
                    Sounds.playSound(p, p.getLocation(), Sounds.MySound.CHICKEN_EGG_POP, 100, 3);
                    reloadGUI(chest);

                    main.getHologramHandler().moveHologram(chest);
                    Bukkit.getScheduler().runTaskLater(main, () -> main.getHologramHandler().moveHologram(chest), 5L);
                }
            } else if(slot == 13) {
                chest.setBlockHeight(chest.getBlockHeight() - 0.015);
                main.getHologramHandler().moveHologram(chest);
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 100, 3);
                reloadGUI(chest);
            } else if(slot == 15) {
                if (main.getRewardChestHandler().getChests().containsKey(chest.getLocation())) {
                    main.getRewardChestHandler().removeChest(chest.getLocation());

                    Sounds.playSound(p, p.getLocation(), Sounds.MySound.ANVIL_USE, 10, 3);

                    p.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix()
                            + " &aSuccesfully removed reward chest of" +
                            " &aX: &e" + chest.getLocation().getBlockX() +
                            ", &aY: &e" + chest.getLocation().getBlockY() +
                            ", &aZ: &e" + chest.getLocation().getBlockZ()));

                    for(Player player : Bukkit.getOnlinePlayers()) {
                        if(opened.get(player.getUniqueId()).getLocation().equals(chest.getLocation())) {
                            p.closeInventory();
                        }
                    }
                } else {
                    p.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix() + " &cThis reward chest location no exists!"));
                }
            } else if (slot == 31) {
                p.closeInventory();
            }
        }
    }

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if(opened.containsKey(p.getUniqueId())) {
            opened.remove(p.getUniqueId());
            main.getRewardChestHandler().saveChests();
        }
    }

}