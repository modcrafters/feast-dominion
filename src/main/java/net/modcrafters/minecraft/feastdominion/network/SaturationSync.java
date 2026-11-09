package net.modcrafters.minecraft.feastdominion.network;

import net.modcrafters.minecraft.feastdominion.compat.AppleSkin;
import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Sends saturation when it changes. AppleSkin already does this while it is loaded. */
public final class SaturationSync {
    private static final Map<UUID, Float> SENT = new HashMap<>();

    private SaturationSync() {
    }

    public static void tick(ServerPlayer player) {
        if (AppleSkin.loaded()) {
            return;
        }
        float saturation = player.getFoodData().getSaturationLevel();
        Float sent = SENT.get(player.getUUID());
        if (sent != null && sent.floatValue() == saturation) {
            return;
        }
        SENT.put(player.getUUID(), saturation);
        Services.PACKETS.saturation(player, saturation);
    }

    public static void forget(ServerPlayer player) {
        SENT.remove(player.getUUID());
    }
}
