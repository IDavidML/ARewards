package me.davidml16.arewards.objects;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

public class RewardChest {

    private String worldName;
    private Location location;

    private double blockHeight;
    private HashMap<UUID, Hologram> holograms;

    public RewardChest(String worldName, Location location, double blockHeight) {
        this.worldName = worldName;
        this.location = location;
        this.holograms = new HashMap<>();
        this.blockHeight = blockHeight;
    }

    public String getWorldName() { return worldName; }

    public void setWorldName(String worldName) { this.worldName = worldName; }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getBlockHeight() { return blockHeight; }

    public void setBlockHeight(double blockHeight) { this.blockHeight = blockHeight; }

    public HashMap<UUID, Hologram> getHolograms() {
        return holograms;
    }

    @Override
    public String toString() {
        return "RewardChest{" +
                "location=" + location +
                ", blockHeight=" + blockHeight +
                ", holograms=" + holograms +
                '}';
    }

}
