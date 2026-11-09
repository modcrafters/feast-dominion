package net.modcrafters.minecraft.feastdominion.fabric.platform;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.modcrafters.minecraft.feastdominion.network.SaturationPayload;
import net.modcrafters.minecraft.feastdominion.platform.services.Packets;
import net.minecraft.server.level.ServerPlayer;

public class FabricPackets implements Packets {
    @Override
    public void saturation(ServerPlayer player, float saturation) {
        ServerPlayNetworking.send(player, new SaturationPayload(saturation));
    }
}
