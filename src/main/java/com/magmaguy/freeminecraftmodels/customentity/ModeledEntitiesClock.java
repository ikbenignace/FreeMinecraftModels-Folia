package com.magmaguy.freeminecraftmodels.customentity;

import com.magmaguy.easyminecraftgoals.NMSManager;
import com.magmaguy.easyminecraftgoals.internal.AbstractPacketBundle;
import com.magmaguy.freeminecraftmodels.MetadataHandler;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class ModeledEntitiesClock {
    private static BukkitTask clock = null;

    private ModeledEntitiesClock() {
    }

    public static void start() {
        // Folia-compatible async scheduling for entity ticking
        clock = Bukkit.getServer().getAsyncScheduler().runAtFixedRate(MetadataHandler.PLUGIN, (task) -> tick(), 0, 50, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public static void shutdown() {
        if (clock != null) clock.cancel();
    }

    public static void tick() {
        AbstractPacketBundle abstractPacketBundle = NMSManager.getAdapter().createPacketBundle();
        new ArrayList<>(ModeledEntity.getLoadedModeledEntities()).forEach(modeledEntity -> modeledEntity.tick(abstractPacketBundle));
        abstractPacketBundle.send();
    }
}
