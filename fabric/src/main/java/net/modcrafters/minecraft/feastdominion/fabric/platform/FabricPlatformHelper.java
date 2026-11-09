package net.modcrafters.minecraft.feastdominion.fabric.platform;

import net.fabricmc.loader.api.FabricLoader;
import net.modcrafters.minecraft.feastdominion.platform.services.PlatformHelper;

import java.nio.file.Path;

public class FabricPlatformHelper implements PlatformHelper {
    @Override
    public Path configDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean modLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }
}
