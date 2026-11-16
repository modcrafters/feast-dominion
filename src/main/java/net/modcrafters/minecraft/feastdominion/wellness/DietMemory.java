package net.modcrafters.minecraft.feastdominion.wellness;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

/** The recent meals saved on the player. The seven counts are that list. */
public record DietMemory(List<DietLevels.Meal> meals) {
    public DietMemory {
        meals = List.copyOf(meals == null ? List.of() : meals);
    }

    public static DietMemory start() {
        return new DietMemory(List.of());
    }

    public int points(FoodGroup group) {
        return DietLevels.points(this.meals)[group.ordinal()];
    }

    public int median() {
        return Wellness.medianOf(DietLevels.points(this.meals));
    }

    public static final Codec<DietLevels.Meal> MEAL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("hunger", 0).forGetter(DietLevels.Meal::hunger),
            Codec.FLOAT.listOf().optionalFieldOf("shares", List.of()).forGetter(DietMemory::shareList)
    ).apply(instance, DietMemory::meal));

    public static final Codec<DietMemory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MEAL_CODEC.listOf().optionalFieldOf("meals", List.of()).forGetter(DietMemory::meals)
    ).apply(instance, DietMemory::new));

    public static final MapCodec<DietMemory> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MEAL_CODEC.listOf().optionalFieldOf("meals", List.of()).forGetter(DietMemory::meals)
    ).apply(instance, DietMemory::new));

    private static final StreamCodec<ByteBuf, DietLevels.Meal> MEAL_STREAM = StreamCodec.of(
            (buf, meal) -> {
                ByteBufCodecs.VAR_INT.encode(buf, meal.hunger());
                for (float share : meal.shares()) {
                    ByteBufCodecs.FLOAT.encode(buf, share);
                }
            },
            buf -> {
                int hunger = ByteBufCodecs.VAR_INT.decode(buf);
                float[] shares = new float[FoodGroup.values().length];
                for (int i = 0; i < shares.length; i++) {
                    shares[i] = ByteBufCodecs.FLOAT.decode(buf);
                }
                return new DietLevels.Meal(hunger, shares);
            }
    );

    public static final StreamCodec<ByteBuf, DietMemory> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, MEAL_STREAM),
            memory -> new ArrayList<>(memory.meals),
            DietMemory::new
    );

    private static List<Float> shareList(DietLevels.Meal meal) {
        List<Float> listed = new ArrayList<>(meal.shares().length);
        for (float share : meal.shares()) {
            listed.add(share);
        }
        return listed;
    }

    private static DietLevels.Meal meal(int hunger, List<Float> shares) {
        float[] values = new float[shares.size()];
        for (int i = 0; i < shares.size(); i++) {
            values[i] = shares.get(i);
        }
        return new DietLevels.Meal(hunger, values);
    }
}
