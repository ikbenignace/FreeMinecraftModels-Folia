package com.magmaguy.freeminecraftmodels.utils;

import com.magmaguy.freeminecraftmodels.MetadataHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Method;

/**
 * Utility class for cross-server scheduler compatibility between Folia and Paper/Spigot
 */
public class SchedulerUtil {
    private static boolean isFolia = false;
    private static boolean foliaChecked = false;
    
    static {
        checkFolia();
    }
    
    private static void checkFolia() {
        if (foliaChecked) return;
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            isFolia = true;
        } catch (ClassNotFoundException e) {
            isFolia = false;
        }
        foliaChecked = true;
    }
    
    public static boolean isFolia() {
        return isFolia;
    }
    
    /**
     * Runs a task repeatedly at fixed intervals.
     * Uses appropriate scheduler based on server type.
     */
    public static Object runTaskTimer(Runnable task, long delay, long period) {
        if (isFolia) {
            return Bukkit.getGlobalRegionScheduler().runAtFixedRate(MetadataHandler.PLUGIN, (scheduledTask) -> task.run(), delay, period);
        } else {
            return Bukkit.getScheduler().runTaskTimer(MetadataHandler.PLUGIN, task, delay, period);
        }
    }
    
    /**
     * Runs a task on the region thread that owns the specified location.
     * Falls back to sync scheduler for Paper/Spigot.
     */
    public static void runTask(Location location, Runnable task) {
        if (isFolia) {
            Bukkit.getRegionScheduler().run(MetadataHandler.PLUGIN, location, (scheduledTask) -> task.run());
        } else {
            Bukkit.getScheduler().runTask(MetadataHandler.PLUGIN, task);
        }
    }
    
    /**
     * Runs a task on the region thread that owns the specified entity.
     * Falls back to sync scheduler for Paper/Spigot.
     */
    public static void runTask(Entity entity, Runnable task) {
        if (isFolia) {
            entity.getScheduler().run(MetadataHandler.PLUGIN, (scheduledTask) -> task.run(), null);
        } else {
            Bukkit.getScheduler().runTask(MetadataHandler.PLUGIN, task);
        }
    }
    
    /**
     * Runs a task asynchronously.
     * Uses AsyncScheduler for Folia, async scheduler for Paper/Spigot.
     */
    public static void runTaskAsync(Runnable task) {
        if (isFolia) {
            Bukkit.getAsyncScheduler().runNow(MetadataHandler.PLUGIN, (scheduledTask) -> task.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(MetadataHandler.PLUGIN, task);
        }
    }
    
    /**
     * Runs a task repeatedly at fixed intervals for a limited number of executions.
     * Uses appropriate scheduler based on server type.
     */
    public static Object runTaskTimerLimited(Location location, Runnable task, long delay, long period, int maxExecutions) {
        if (isFolia) {
            final int[] executionCount = {0};
            return Bukkit.getRegionScheduler().runAtFixedRate(MetadataHandler.PLUGIN, location, (scheduledTask) -> {
                executionCount[0]++;
                if (executionCount[0] > maxExecutions) {
                    scheduledTask.cancel();
                    return;
                }
                task.run();
            }, delay, period);
        } else {
            final int[] executionCount = {0};
            return Bukkit.getScheduler().runTaskTimer(MetadataHandler.PLUGIN, () -> {
                executionCount[0]++;
                if (executionCount[0] > maxExecutions) {
                    return; // BukkitTask will be cancelled externally
                }
                task.run();
            }, delay, period);
        }
    }
    
    /**
     * Cancels a scheduler task.
     * Handles both Folia ScheduledTask and Bukkit BukkitTask.
     */
    public static void cancelTask(Object task) {
        if (task == null) return;
        
        if (isFolia) {
            try {
                Method cancelMethod = task.getClass().getMethod("cancel");
                cancelMethod.invoke(task);
            } catch (Exception e) {
                MetadataHandler.PLUGIN.getLogger().warning("Failed to cancel Folia task: " + e.getMessage());
            }
        } else if (task instanceof BukkitTask) {
            ((BukkitTask) task).cancel();
        }
    }
}