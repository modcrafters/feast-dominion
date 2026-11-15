package net.modcrafters.minecraft.feastdominion.thirst;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/** Thirst saved on the player. A missing value is a full bar, and a missing quench is full for that bar. */
public record ThirstLevel(int points, int drain, float quench) {
    public ThirstLevel(int points) {
        this(points, 0, -1.0F);
    }

    public static final Codec<ThirstLevel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("points").forGetter(ThirstLevel::points),
            Codec.INT.optionalFieldOf("drain", 0).forGetter(ThirstLevel::drain),
            Codec.FLOAT.optionalFieldOf("quench", -1.0F).forGetter(ThirstLevel::quench)
    ).apply(instance, ThirstLevel::new));
    public static final MapCodec<ThirstLevel> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("points").forGetter(ThirstLevel::points),
            Codec.INT.optionalFieldOf("drain", 0).forGetter(ThirstLevel::drain),
            Codec.FLOAT.optionalFieldOf("quench", -1.0F).forGetter(ThirstLevel::quench)
    ).apply(instance, ThirstLevel::new));
    public static final StreamCodec<ByteBuf, ThirstLevel> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ThirstLevel::points,
            ByteBufCodecs.VAR_INT,
            ThirstLevel::drain,
            ByteBufCodecs.FLOAT,
            ThirstLevel::quench,
            ThirstLevel::new
    );

    public ThirstLevel {
        if (points < 0) {
            points = 0;
        } else if (points > Thirst.POINTS) {
            points = Thirst.POINTS;
        }
        if (drain < 0) {
            drain = 0;
        }
        quench = quench < 0.0F ? Quench.cap(points) : Quench.clamp(points, quench);
    }
}
