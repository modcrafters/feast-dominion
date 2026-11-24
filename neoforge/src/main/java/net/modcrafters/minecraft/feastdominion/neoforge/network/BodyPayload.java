package net.modcrafters.minecraft.feastdominion.neoforge.network;

import net.modcrafters.minecraft.feastdominion.FeastIds;
import net.modcrafters.minecraft.feastdominion.taste.PlayerLean;
import net.modcrafters.minecraft.feastdominion.thirst.ThirstLevel;
import net.modcrafters.minecraft.feastdominion.wellness.DietMemory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Thirst, lean, and recent meals together. 1.20.6 sends this once the client has its player. */
public record BodyPayload(ThirstLevel thirst, PlayerLean lean, DietMemory memory) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<BodyPayload> TYPE = new CustomPacketPayload.Type<>(
            FeastIds.of("body")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, BodyPayload> STREAM_CODEC = StreamCodec.composite(
            ThirstLevel.STREAM_CODEC,
            BodyPayload::thirst,
            PlayerLean.STREAM_CODEC,
            BodyPayload::lean,
            DietMemory.STREAM_CODEC,
            BodyPayload::memory,
            BodyPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
