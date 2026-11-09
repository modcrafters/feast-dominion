package net.modcrafters.minecraft.feastdominion.network;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.feature.VarietyView;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** The three windows, the hearts they are worth, and the weights, for the local player. */
public record VarietyPayload(VarietyView.Snapshot snapshot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<VarietyPayload> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "variety")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, VarietyPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                VarietyView.Snapshot snapshot = payload.snapshot();
                buf.writeVarInt(snapshot.today());
                buf.writeVarInt(snapshot.week());
                buf.writeVarInt(snapshot.month());
                buf.writeDouble(snapshot.hearts());
                buf.writeVarInt(snapshot.dayWeight());
                buf.writeVarInt(snapshot.weekWeight());
                buf.writeVarInt(snapshot.monthWeight());
            },
            buf -> new VarietyPayload(new VarietyView.Snapshot(
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readDouble(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt()
            ))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
