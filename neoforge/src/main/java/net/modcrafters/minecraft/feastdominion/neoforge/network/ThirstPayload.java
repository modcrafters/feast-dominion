package net.modcrafters.minecraft.feastdominion.neoforge.network;

import net.modcrafters.minecraft.feastdominion.FeastIds;
import net.modcrafters.minecraft.feastdominion.thirst.ThirstLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Thirst for the client. 1.20.6 attachments do not sync themselves. */
public record ThirstPayload(ThirstLevel level) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ThirstPayload> TYPE = new CustomPacketPayload.Type<>(
            FeastIds.of("thirst")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ThirstPayload> STREAM_CODEC = StreamCodec.composite(
            ThirstLevel.STREAM_CODEC,
            ThirstPayload::level,
            ThirstPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
