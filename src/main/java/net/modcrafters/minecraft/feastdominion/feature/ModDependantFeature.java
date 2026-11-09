package net.modcrafters.minecraft.feastdominion.feature;

import net.modcrafters.minecraft.feastdominion.platform.Services;

import java.util.function.BooleanSupplier;

/**
 * A feature gated on another mod. {@code whenPresent} turns it on while that mod is loaded.
 * The opposite turns it on while that mod is absent.
 */
public abstract class ModDependantFeature {
    private final String modId;
    private final BooleanSupplier present;
    private final boolean whenPresent;

    protected ModDependantFeature(String modId) {
        this(modId, true);
    }

    protected ModDependantFeature(String modId, boolean whenPresent) {
        this(modId, () -> Services.PLATFORM.modLoaded(modId), whenPresent);
    }

    ModDependantFeature(String modId, BooleanSupplier present) {
        this(modId, present, true);
    }

    ModDependantFeature(String modId, BooleanSupplier present, boolean whenPresent) {
        this.modId = modId;
        this.present = present;
        this.whenPresent = whenPresent;
    }

    public final String modId() {
        return modId;
    }

    public final boolean enabled() {
        boolean loaded = present.getAsBoolean();
        return whenPresent ? loaded : !loaded;
    }

    public final void init() {
        if (enabled()) {
            onEnable();
        } else {
            onDisable();
        }
    }

    protected abstract void onEnable();

    protected void onDisable() {
    }
}
