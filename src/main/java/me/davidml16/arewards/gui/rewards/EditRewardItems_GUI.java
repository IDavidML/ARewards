package me.davidml16.arewards.gui.rewards;

import me.davidml16.arewards.Main;
import me.davidml16.arewards.objects.RewardType;
import me.davidml16.arewards.objects.rewards.*;
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

import java.util.*;

public class EditRewardItems_GUI implements Listener {

    private HashMap<UUID, GUISession> opened;
    private List<Integer> borders;

    private Main main;

    public EditRewardItems_GUI(Main main) {
        this.main = main;
        this.opened = new HashMap<UUID, GUISession>();
        this.borders = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35);
        this.main.getServer().getPluginManager().registerEvents(this, this.main);
    }

    public HashMap<UUID, GUISession> getOpened() {
        return opened;
    }

    public void reloadGUI(Reward reward) {
        for(UUID uuid : opened.keySet()) {
            if(opened.get(uuid).getReward().getId().equalsIgnoreCase(reward.getId())) {
                Player p = Bukkit.getPlayer(uuid);
                openPage(p, reward, opened.get(uuid).getPage());
            }
        }
    }

    private void openPage(Player p, Reward reward, int page) {
        List<Item> items = ((ItemReward) reward).getItems();

        if(page > 0 && items.size() < (page * 14) + 1) {
            openPage(p, reward, page - 1);
            return;
        }

        Inventory gui = Bukkit.createInventory(null, 36, "%reward% | Items".replaceAll("%reward%", reward.getId()));

        ItemStack edge = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("").toItemStack();
        ItemStack back = new ItemBuilder(XMaterial.ARROW.parseItem()).setName(Utils.translate("&aBack to config")).toItemStack();

        for (Integer i : borders)
            gui.setItem(i, edge);

        for (int i = 10; i <= 16; i++)
            gui.setItem(i, null);
        for (int i = 19; i <= 25; i++)
            gui.setItem(i, null);

        if (page > 0) {
            gui.setItem(27, new ItemBuilder(XMaterial.ENDER_PEARL.parseItem()).setName(Utils.translate("&aPrevious page")).toItemStack());
        } else {
            gui.setItem(27, new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("").toItemStack());
        }

        if (items.size() > (page + 1) * 14) {
            gui.setItem(35, new ItemBuilder(XMaterial.ENDER_PEARL.parseItem()).setName(Utils.translate("&aNext page")).toItemStack());
        } else {
            gui.setItem(35, new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("").toItemStack());
        }

        gui.setItem(31, back);

        if (items.size() > 14) items = items.subList(page * 14, ((page * 14) + 14) > items.size() ? items.size() : (page * 14) + 14);

        if(items.size() > 0) {
            for (Item item : items) {
                gui.addItem(new ItemBuilder(item.getItemStack().clone())
                        .setName(Utils.translate("&a" + item.getId()))
                        .setLore(
                                "",
                                Utils.translate("&eLeft-Click » &aRemove item ")
                        ).toItemStack());
            }
        } else {
            for(int i = 10; i <= 25; i++)
                if(gui.getItem(i) == null)
                    gui.setItem(i, new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE.parseItem()).setName(Utils.translate("&cAny items added")).setLore(
                            "",
                            Utils.translate(" &7You dont have any "),
                            Utils.translate(" &7items added. "),
                            "",
                            Utils.translate(" &7Click items of your. "),
                            Utils.translate(" &7inventory to add it. "),
                            ""
                    ).toItemStack());
        }

        if (!opened.containsKey(p.getUniqueId())) {
            p.openInventory(gui);
        } else {
            p.getOpenInventory().getTopInventory().setContents(gui.getContents());
        }

        Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 10, 2);
        Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> opened.put(p.getUniqueId(), new GUISession(reward, page)), 1L);
    }

    public void open(Player p, Reward reward) {
        p.updateInventory();
        openPage(p, reward, 0);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() == Material.AIR) return;

        if (opened.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            int slot = e.getRawSlot();

            Reward reward = opened.get(p.getUniqueId()).getReward();
            RewardType rewardType = reward.getParentReward();

            if (slot == 27 && e.getCurrentItem().getType() == XMaterial.ENDER_PEARL.parseMaterial()) {
                openPage(p, reward, opened.get(p.getUniqueId()).getPage() - 1);
            } else if (slot == 35 && e.getCurrentItem().getType() == XMaterial.ENDER_PEARL.parseMaterial()) {
                openPage(p, reward, opened.get(p.getUniqueId()).getPage() + 1);
            } else if (slot == 31) {
                main.getEditRewardsGUI().open(p, rewardType.getId());
            } else if ((slot >= 10 && slot <= 16) || (slot >= 19 && slot <= 25)) {

                if (((ItemReward) reward).getItems().size() == 0) return;

                String itemID = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                Item item = ((ItemReward) reward).getItem(itemID);

                if(e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {

                    ((ItemReward) reward).getItems().remove(item);

                    p.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix()
                            + " &aYou removed item &e" + item.getId() + " &afrom items of reward &e" + reward.getId()));
                    reloadGUI(reward);

                }

            } else if (slot >= 45 && slot <= 80) {

                ItemStack itemInHand = e.getCurrentItem();

                List<Item> items = ((ItemReward) reward).getItems();

                int lastIndex = items.size() > 0 ? Integer.parseInt(items.get(items.size() - 1).getId().replaceAll("item_", "")) : 0;
                Item newItem = new Item("item_" + (lastIndex + 1), itemInHand);

                ((ItemReward) reward).getItems().add(newItem);
                p.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix()
                        + " &aYou added item &e" + newItem.getId() + " &ato items of reward &e" + reward.getId()));
                reloadGUI(reward);

            }
        }
    }

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (opened.containsKey(p.getUniqueId())) {
            opened.get(p.getUniqueId()).getReward().getParentReward().save();
            opened.remove(p.getUniqueId());
        }
    }

    class GUISession {

        private Reward reward;
        private int page;

        public GUISession(Reward reward, int page) {
            this.reward = reward;
            this.page = page;
        }

        public Reward getReward() {
            return reward;
        }

        public void setReward(Reward reward) {
            this.reward = reward;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

    }

}
