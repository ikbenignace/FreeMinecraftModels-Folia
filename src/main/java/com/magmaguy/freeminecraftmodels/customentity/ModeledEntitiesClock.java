package com.magmaguy.freeminecraftmodels.customentity;

import com.magmaguy.easyminecraftgoals.NMSManager;
import com.magmaguy.easyminecraftgoals.internal.AbstractPacketBundle;
import com.magmaguy.freeminecraftmodels.MetadataHandler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;

import java.util.ArrayList;

public class ModeledEntitiesClock {
    private static ScheduledTask clockTask = null;

    private ModeledEntitiesClock() {
    }

    public static void start() {
        clockTask = Bukkit.getGlobalRegionScheduler().runAtFixedRate(MetadataHandler.PLUGIN, (task) -> {
            tick();
        }, 1, 1);
    }

    public static void shutdown() {
        if (clockTask != null) clockTask.cancel();
    }

    public static void tick() {
        AbstractPacketBundle abstractPacketBundle = NMSManager.getAdapter().createPacketBundle();
        new ArrayList<>(ModeledEntity.getLoadedModeledEntities()).forEach(modeledEntity -> modeledEntity.tick(abstractPacketBundle));
        abstractPacketBundle.send();
    }
}
