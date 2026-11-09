package net.modcrafters.minecraft.feastdominion.compat;

import net.modcrafters.minecraft.feastdominion.platform.Services;

/** AppleSkin already draws saturation, on the hunger shanks and on food tooltips, and keeps the client value current. */
public final class AppleSkin {
    public static final String MOD_ID = "appleskin";

    private AppleSkin() {
    }

    public static boolean loaded() {
        return Services.PLATFORM.modLoaded(MOD_ID);
    }
}
