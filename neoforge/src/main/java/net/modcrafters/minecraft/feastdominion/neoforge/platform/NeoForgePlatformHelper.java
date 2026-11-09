package net.modcrafters.minecraft.feastdominion.neoforge.platform;

import net.modcrafters.minecraft.feastdominion.platform.services.PlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public Path configDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean modLoaded(String id) {
        return ModList.get().isLoaded(id);
    }
}
