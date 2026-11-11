package net.modcrafters.minecraft.feastdominion.wellness;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

/** The seven counts saved on the player, plus the ticks toward the next faded point. */
public record DietMemory(int[] points, int drain) {
    public DietMemory {
        int[] copy = new int[FoodGroup.values().length];
        for (int i = 0; i < copy.length; i++) {
            int value = points != null && i < points.length ? points[i] : Wellness.POINTS / 2;
            copy[i] = DietLevels.clamp(value);
        }
        points = copy;
        if (drain < 0) {
            drain = 0;
        }
    }

    public static DietMemory start() {
        return new DietMemory(DietLevels.starting(), 0);
    }

    public int points(FoodGroup group) {
        return this.points[group.ordinal()];
    }

    public int median() {
        return Wellness.medianOf(this.points);
    }

    public static final Codec<DietMemory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("meat", Wellness.POINTS / 2).forGetter(memory -> memory.points[FoodGroup.MEAT.ordinal()]),
            Codec.INT.optionalFieldOf("dairy", Wellness.POINTS / 2).forGetter(memory -> memory.points[FoodGroup.DAIRY.ordinal()]),
            Codec.INT.optionalFieldOf("eggs", Wellness.POINTS / 2).forGetter(memory -> memory.points[FoodGroup.EGGS.ordinal()]),
            Codec.INT.optionalFieldOf("grain", Wellness.POINTS / 2).forGetter(memory -> memory.points[FoodGroup.GRAIN.ordinal()]),
            Codec.INT.optionalFieldOf("vegetables", Wellness.POINTS / 2).forGetter(memory -> memory.points[FoodGroup.VEGETABLES.ordinal()]),
            Codec.INT.optionalFieldOf("fruit", Wellness.POINTS / 2).forGetter(memory -> memory.points[FoodGroup.FRUIT.ordinal()]),
            Codec.INT.optionalFieldOf("legumes", Wellness.POINTS / 2).forGetter(memory -> memory.points[FoodGroup.LEGUMES.ordinal()]),
            Codec.INT.optionalFieldOf("drain", 0).forGetter(DietMemory::drain)
    ).apply(instance, DietMemory::of));

    public static final MapCodec<DietMemory> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("meat", Wellness.POINTS / 2).forGetter(memory -> memory.points[FoodGroup.MEAT.ordinal()]),
            Codec.INT.optionalFieldOf("dairy", Wellness.POINTS / 2).forGetter(memory -> memory.points[FoodGroup.DAIRY.ordinal()]),
            Codec.INT.optionalFieldOf("eggs", Wellness.POINTS / 2).forGetter(memory -> memory.points[FoodGroup.EGGS.ordinal()]),
            Codec.INT.optionalFieldOf("grain", Wellness.POINTS / 2).forGetter(memory -> memory.points[FoodGroup.GRAIN.ordinal()]),
            Codec.INT.optionalFieldOf("vegetables", Wellness.POINTS / 2).forGetter(memory -> memory.points[FoodGroup.VEGETABLES.ordinal()]),
            Codec.INT.optionalFieldOf("fruit", Wellness.POINTS / 2).forGetter(memory -> memory.points[FoodGroup.FRUIT.ordinal()]),
            Codec.INT.optionalFieldOf("legumes", Wellness.POINTS / 2).forGetter(memory -> memory.points[FoodGroup.LEGUMES.ordinal()]),
            Codec.INT.optionalFieldOf("drain", 0).forGetter(DietMemory::drain)
    ).apply(instance, DietMemory::of));

    public static final StreamCodec<ByteBuf, DietMemory> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.VAR_INT),
            DietMemory::listed,
            ByteBufCodecs.VAR_INT,
            DietMemory::drain,
            DietMemory::fromList
    );

    private static DietMemory of(
            int meat,
            int dairy,
            int eggs,
            int grain,
            int vegetables,
            int fruit,
            int legumes,
            int drain
    ) {
        return new DietMemory(new int[]{meat, dairy, eggs, grain, vegetables, fruit, legumes}, drain);
    }

    private static List<Integer> listed(DietMemory memory) {
        List<Integer> listed = new ArrayList<>(memory.points.length);
        for (int point : memory.points) {
            listed.add(point);
        }
        return listed;
    }

    private static DietMemory fromList(List<Integer> listed, int drain) {
        int[] points = new int[listed.size()];
        for (int i = 0; i < listed.size(); i++) {
            points[i] = listed.get(i);
        }
        return new DietMemory(points, drain);
    }
}
