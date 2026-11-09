package net.modcrafters.minecraft.feastdominion;

import net.modcrafters.minecraft.feastdominion.platform.Services;

public final class FeastDominion {
    private FeastDominion() {
    }

    public static void init() {
        FeastConfig.load(Services.PLATFORM.configDirectory());
        Constants.LOG.info("{} common init on {}", Constants.MOD_NAME, Services.PLATFORM.getClass().getName());
    }
}
