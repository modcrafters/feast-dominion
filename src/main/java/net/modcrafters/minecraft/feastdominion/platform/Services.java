package net.modcrafters.minecraft.feastdominion.platform;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.platform.services.PlatformHelper;
import net.modcrafters.minecraft.feastdominion.platform.services.ThirstStore;

import java.util.ServiceLoader;

public final class Services {
    public static final PlatformHelper PLATFORM = load(PlatformHelper.class);
    public static final ThirstStore THIRST = load(ThirstStore.class);

    private Services() {
    }

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Failed to load service for " + clazz.getName()));
    }

    static {
        Constants.LOG.debug("Loaded platform {}", PLATFORM.getClass().getName());
    }
}
