package me.davidml16.arewards.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class RepeatingTask implements Runnable {

    private int taskId;

    public RepeatingTask(JavaPlugin plugin, int arg1, int arg2) {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, arg1, arg2);
    }

    public void canncel() {
        Bukkit.getScheduler().cancelTask(taskId);
    }

}
