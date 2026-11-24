package net.modcrafters.minecraft.feastdominion.neoforge.network;

import net.modcrafters.minecraft.feastdominion.FeastIds;
import net.modcrafters.minecraft.feastdominion.taste.PlayerLean;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** The lean for the client. 1.20.6 attachments do not sync themselves. */
public record LeanPayload(PlayerLean lean) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LeanPayload> TYPE = new CustomPacketPayload.Type<>(
            FeastIds.of("lean")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, LeanPayload> STREAM_CODEC = StreamCodec.composite(
            PlayerLean.STREAM_CODEC,
            LeanPayload::lean,
            LeanPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
