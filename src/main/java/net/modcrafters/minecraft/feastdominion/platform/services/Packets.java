package net.modcrafters.minecraft.feastdominion.platform.services;

import net.modcrafters.minecraft.feastdominion.network.VarietyPayload;
import net.minecraft.server.level.ServerPlayer;

public interface Packets {
    void saturation(ServerPlayer player, float saturation);

    void variety(ServerPlayer player, VarietyPayload payload);
}
