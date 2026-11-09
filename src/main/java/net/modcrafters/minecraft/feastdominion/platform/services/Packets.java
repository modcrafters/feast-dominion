package net.modcrafters.minecraft.feastdominion.platform.services;

import net.minecraft.server.level.ServerPlayer;

public interface Packets {
    void saturation(ServerPlayer player, float saturation);
}
