package me.davidml16.arewards.gui.rewards;

import com.cryptomorin.xseries.XMaterial;
import me.davidml16.arewards.Main;
import me.davidml16.arewards.conversation.rewards.CommandRewardMenu;
import me.davidml16.arewards.conversation.rewards.EditCommandRewardMenu;
import me.davidml16.arewards.conversation.rewards.EditItemRewardMenu;
import me.davidml16.arewards.conversation.rewards.ItemRewardMenu;
import me.davidml16.arewards.objects.rewards.ItemReward;
import me.davidml16.arewards.objects.rewards.Reward;
import me.davidml16.arewards.objects.RewardType;
import me.davidml16.arewards.objects.Pair;
import me.davidml16.arewards.objects.rewards.CommandReward;
import me.davidml16.arewards.utils.Utils;
import me.davidml16.arewards.utils.ItemBuilder;
import me.davidml16.arewards.utils.Sounds;
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

public class EditRewards_GUI implements Listener {

    private HashMap<UUID, Pair> opened;
    private HashMap<String, Inventory> guis;
    private List<Integer> borders;

    private Main main;

    public EditRewards_GUI(Main main) {
        this.main = main;
        this.opened = new HashMap<UUID, Pair>();
        this.guis = new HashMap<String, Inventory>();
        this.borders = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35);
        this.main.getServer().getPluginManager().registerEvents(this, this.main);
    }

    public HashMap<UUID, Pair> getOpened() {
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

        Inventory gui = Bukkit.createInventory(null, 36, "%reward_type% | Rewards".replaceAll("%reward_type%", id));

        ItemStack edge = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("").toItemStack();
        ItemStack newReward = new ItemBuilder(XMaterial.SUNFLOWER.parseItem()).setName(Utils.translate("&aCreate new reward"))
                .setLore(
                    "",
                    Utils.translate("&eLeft-Click » &aCommand reward "),
                    Utils.translate("&eRight-Click » &aItem reward "))
                .toItemStack();
        ItemStack back = new ItemBuilder(XMaterial.ARROW.parseItem()).setName(Utils.translate("&aBack to config")).toItemStack();

        gui.setItem(30, newReward);
        gui.setItem(32, back);

        for (Integer i : borders) {
            if(gui.getItem(i) == null)
                gui.setItem(i, edge);
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
            if(opened.get(uuid).getRewardType().equals(id)) {
                Player p = Bukkit.getPlayer(uuid);
                openPage(p, id, opened.get(uuid).getPage());
            }
        }
    }

    private void openPage(Player p, String id, int page) {
        List<Reward> rewards = main.getRewardTypeHandler().getTypeBydId(id).getRewards();

        if(page > 0 && rewards.size() < (page * 14) + 1) {
            openPage(p, id, page - 1);
            return;
        }

        Inventory gui = Bukkit.createInventory(null, 36, "%reward_type% | Rewards".replaceAll("%reward_type%", id));

        if(!guis.containsKey(id)) loadGUI(id);

        gui.setContents(guis.get(id).getContents());

        for (int i = 10; i <= 16; i++)
            gui.setItem(i, null);
        for (int i = 19; i <= 25; i++)
            gui.setItem(i, null);

        if (page > 0) {
            gui.setItem(27, new ItemBuilder(XMaterial.ENDER_PEARL.parseItem()).setName(Utils.translate("&aPrevious page")).toItemStack());
        } else {
            gui.setItem(27, new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("").toItemStack());
        }

        if (rewards.size() > (page + 1) * 14) {
            gui.setItem(35, new ItemBuilder(XMaterial.ENDER_PEARL.parseItem()).setName(Utils.translate("&aNext page")).toItemStack());
        } else {
            gui.setItem(35, new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("").toItemStack());
        }

        if (rewards.size() > 14) rewards = rewards.subList(page * 14, ((page * 14) + 14) > rewards.size() ? rewards.size() : (page * 14) + 14);

        if(rewards.size() > 0) {
            for (Reward reward : rewards) {
                if (reward instanceof CommandReward) {
                    gui.addItem(new ItemBuilder(XMaterial.CHEST.parseItem())
                            .setName(Utils.translate("&a" + reward.getId()))
                            .setLore(
                                    "",
                                    Utils.translate(" &7Name: &6" + reward.getName() + " "),
                                    Utils.translate(" &7Commands: &6" + ((CommandReward) reward).getCommands().size() + " "),
                                    "",
                                    Utils.translate("&eLeft-Click » &aRemove reward "),
                                    Utils.translate("&eRight-Click » &aEdit reward ")
                            ).hideAttributes().toItemStack());
                } else if (reward instanceof ItemReward) {
                    gui.addItem(new ItemBuilder(XMaterial.CHEST.parseItem())
                            .setName(Utils.translate("&a" + reward.getId()))
                            .setLore(
                                    "",
                                    Utils.translate(" &7Name: &6" + reward.getName() + " "),
                                    Utils.translate(" &7Items: &6" + ((ItemReward) reward).getItems().size() + " "),
                                    "",
                                    Utils.translate("&eLeft-Click » &aRemove reward "),
                                    Utils.translate("&eMiddle-Click » &aEdit items "),
                                    Utils.translate("&eRight-Click » &aEdit reward ")
                            ).hideAttributes().toItemStack());
                }
            }
        } else {
            for(int i = 10; i <= 25; i++)
                if(gui.getItem(i) == null)
                    gui.setItem(i, new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE.parseItem()).setName(Utils.translate("&cAny rewards created")).setLore(
                            "",
                            Utils.translate(" &7You dont have any "),
                            Utils.translate(" &7reward created. "),
                            ""
                    ).toItemStack());
        }

        if (!opened.containsKey(p.getUniqueId())) {
            p.openInventory(gui);
        } else {
            p.getOpenInventory().getTopInventory().setContents(gui.getContents());
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> opened.put(p.getUniqueId(), new Pair(id, page)), 1L);
    }

    public void open(Player p, String id) {
        p.updateInventory();
        openPage(p, id, 0);

        Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 10, 2);
        Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> opened.put(p.getUniqueId(), new Pair(id, 0)), 1L);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null) return;

        if (opened.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            int slot = e.getRawSlot();
            String id = opened.get(p.getUniqueId()).getRewardType();
            RewardType rewardType = main.getRewardTypeHandler().getTypeBydId(opened.get(p.getUniqueId()).getRewardType());
            if (slot == 27 && e.getCurrentItem().getType() == XMaterial.ENDER_PEARL.parseMaterial()) {
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 10, 2);
                openPage(p, id, opened.get(p.getUniqueId()).getPage() - 1);
            } else if (slot == 35 && e.getCurrentItem().getType() == XMaterial.ENDER_PEARL.parseMaterial()) {
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 10, 2);
                openPage(p, id, opened.get(p.getUniqueId()).getPage() + 1);
            } else if (slot == 30) {
                p.closeInventory();
                if(e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT)
                    new CommandRewardMenu(main).getConversation(p, rewardType).begin();
                else if(e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT)
                    new ItemRewardMenu(main).getConversation(p, rewardType).begin();
                Sounds.playSound(p, p.getLocation(), Sounds.MySound.ANVIL_USE, 100, 3);
            } else if (slot == 32) {
                main.getSetupGUI().open(p, rewardType.getId());
            } else if ((slot >= 10 && slot <= 16) || (slot >= 19 && slot <= 25)) {
                if (e.getCurrentItem().getType() == Material.AIR) return;

                if (rewardType.getRewards().size() == 0) return;

                String rewardID = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                Reward reward = rewardType.getReward(rewardID);

                if(e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {

                    rewardType.getRewards().remove(reward);

                    p.sendMessage(Utils.translate(main.getLanguageHandler().getPrefix()
                            + " &aYou removed reward &e" + rewardID + " &afrom reward type &e" + rewardType.getId()));
                    reloadGUI(rewardType.getId());
                    Sounds.playSound(p, p.getLocation(), Sounds.MySound.CLICK, 10, 2);

                } else if(e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {

                    p.closeInventory();

                    if(reward instanceof CommandReward)
                        new EditCommandRewardMenu(main).getConversation(p, rewardType, reward).begin();
                    else if(reward instanceof ItemReward)
                        new EditItemRewardMenu(main).getConversation(p, rewardType, reward).begin();

                    Sounds.playSound(p, p.getLocation(), Sounds.MySound.ANVIL_USE, 50, 3);

                } else if(e.getClick() == ClickType.MIDDLE) {

                    if(reward instanceof ItemReward)
                        main.getEditRewardItemsGUI().open(p, reward);

                }

            }
        }
    }

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (opened.containsKey(p.getUniqueId())) {
            main.getRewardTypeHandler().getTypeBydId(opened.get(p.getUniqueId()).getRewardType()).save();
            opened.remove(p.getUniqueId());
        }
    }

}
