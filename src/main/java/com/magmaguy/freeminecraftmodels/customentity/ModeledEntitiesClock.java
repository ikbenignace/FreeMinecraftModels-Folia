package com.magmaguy.freeminecraftmodels.customentity;

import com.magmaguy.easyminecraftgoals.NMSManager;
import com.magmaguy.easyminecraftgoals.internal.AbstractPacketBundle;
import com.magmaguy.freeminecraftmodels.utils.SchedulerUtil;

import java.util.ArrayList;

public class ModeledEntitiesClock {
    private static Object clockTask = null;

    private ModeledEntitiesClock() {
    }

    public static void start() {
        clockTask = SchedulerUtil.runTaskTimer(() -> tick(), 1, 1);
    }

    public static void shutdown() {
        SchedulerUtil.cancelTask(clockTask);
    }

    public static void tick() {
        AbstractPacketBundle abstractPacketBundle = NMSManager.getAdapter().createPacketBundle();
        new ArrayList<>(ModeledEntity.getLoadedModeledEntities()).forEach(modeledEntity -> modeledEntity.tick(abstractPacketBundle));
        abstractPacketBundle.send();
    }
}
