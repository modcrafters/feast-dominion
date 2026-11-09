package net.modcrafters.minecraft.feastdominion.neoforge.platform;

import net.modcrafters.minecraft.feastdominion.network.SaturationPayload;
import net.modcrafters.minecraft.feastdominion.platform.services.Packets;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class NeoForgePackets implements Packets {
    @Override
    public void saturation(ServerPlayer player, float saturation) {
        PacketDistributor.sendToPlayer(player, new SaturationPayload(saturation));
    }
}
