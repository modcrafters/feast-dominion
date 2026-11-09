package net.modcrafters.minecraft.feastdominion.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;
import java.util.Map;

/** The day each food or drink was last consumed. Old entries stay. */
public record FoodLog(Map<String, Long> lastEaten) {
    public static final FoodLog EMPTY = new FoodLog(Map.of());
    public static final MapCodec<FoodLog> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, Codec.LONG).optionalFieldOf("eaten", Map.of()).forGetter(FoodLog::lastEaten)
    ).apply(instance, FoodLog::new));
    public static final Codec<FoodLog> CODEC = MAP_CODEC.codec();

    public FoodLog {
        lastEaten = Map.copyOf(lastEaten);
    }

    public FoodLog remember(String item, long day) {
        Map<String, Long> next = new HashMap<>(lastEaten);
        next.put(item, day);
        return new FoodLog(next);
    }
}
