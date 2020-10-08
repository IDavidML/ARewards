package me.davidml16.arewards.objects.rewards;

import me.davidml16.arewards.objects.RewardType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemReward extends Reward {

    private List<Item> items;

    public ItemReward(String id, String name, List<Item> items, RewardType parentReward) {
        super(id, name, parentReward);
        this.items = items;
    }

    public List<Item> getItems() { return items; }

    public void setItems(List<Item> items) { this.items = items; }

    public Item getItem(String id) {
        for(Item item : getItems())
            if(item.getId().equalsIgnoreCase(id))
                return item;
        return null;
    }

    @Override
    public String toString() {
        return "ItemReward{" +
                "items=" + items +
                '}';
    }

}
