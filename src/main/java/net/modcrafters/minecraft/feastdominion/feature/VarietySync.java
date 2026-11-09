package net.modcrafters.minecraft.feastdominion.feature;

import net.modcrafters.minecraft.feastdominion.network.VarietyPayload;
import net.modcrafters.minecraft.feastdominion.platform.Services;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Sends the variety page when its numbers change. */
public final class VarietySync {
    private static final Map<UUID, VarietyView.Snapshot> SENT = new HashMap<>();

    private VarietySync() {
    }

    public static void send(ServerPlayer player, VarietyView.Snapshot snapshot) {
        VarietyView.Snapshot sent = SENT.get(player.getUUID());
        if (snapshot.equals(sent)) {
            return;
        }
        SENT.put(player.getUUID(), snapshot);
        Services.PACKETS.variety(player, new VarietyPayload(snapshot));
    }

    public static void forget(ServerPlayer player) {
        SENT.remove(player.getUUID());
    }
}
