package net.modcrafters.minecraft.feastdominion.network;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** The player's saturation. Vanilla only sends it when the value hits zero. */
public record SaturationPayload(float saturation) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SaturationPayload> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "saturation")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, SaturationPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            SaturationPayload::saturation,
            SaturationPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
