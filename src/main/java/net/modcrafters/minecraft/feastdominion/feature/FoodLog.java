package net.modcrafters.minecraft.feastdominion.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.modcrafters.minecraft.feastdominion.group.GroupShares;
import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;

import java.util.ArrayList;
import java.util.List;

/** Hunger eaten in each group, and the items, kept by the day they were eaten. */
public record FoodLog(List<FoodLog.Day> days, List<FoodLog.Eaten> eaten) {
    public static final FoodLog EMPTY = new FoodLog(List.of(), List.of());

    public record Day(long day, float[] hunger) {
        public Day {
            float[] copy = new float[FoodGroup.values().length];
            for (int i = 0; i < copy.length; i++) {
                float value = hunger != null && i < hunger.length ? hunger[i] : 0.0F;
                copy[i] = value < 0.0F ? 0.0F : value;
            }
            hunger = copy;
        }

        private Day plus(float[] add) {
            float[] next = this.hunger.clone();
            for (int i = 0; i < next.length && i < add.length; i++) {
                next[i] += add[i];
            }
            return new Day(this.day, next);
        }
    }

    public record Eaten(String item, long day) {
    }

    private static final Codec<float[]> HUNGER = Codec.FLOAT.listOf().xmap(FoodLog::hungerOf, FoodLog::hungerList);

    private static final Codec<Day> DAY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("day").forGetter(Day::day),
            HUNGER.fieldOf("hunger").forGetter(Day::hunger)
    ).apply(instance, Day::new));

    private static final Codec<Eaten> EATEN_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("item").forGetter(Eaten::item),
            Codec.LONG.fieldOf("day").forGetter(Eaten::day)
    ).apply(instance, Eaten::new));

    public static final MapCodec<FoodLog> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DAY_CODEC.listOf().optionalFieldOf("days", List.of()).forGetter(FoodLog::days),
            EATEN_CODEC.listOf().optionalFieldOf("eaten", List.of()).forGetter(FoodLog::eaten)
    ).apply(instance, FoodLog::new));
    public static final Codec<FoodLog> CODEC = MAP_CODEC.codec();

    public FoodLog {
        days = List.copyOf(days == null ? List.of() : days);
        eaten = List.copyOf(eaten == null ? List.of() : eaten);
    }

    /** Adds the meal to that day and drops days older than a month. */
    public FoodLog remember(long day, GroupShares shares, int hunger) {
        if (hunger <= 0 || shares == null || !shares.any()) {
            return this;
        }
        float[] add = new float[FoodGroup.values().length];
        for (FoodGroup group : FoodGroup.values()) {
            add[group.ordinal()] = shares.get(group) * hunger;
        }
        List<Day> next = new ArrayList<>();
        boolean found = false;
        for (Day entry : this.days) {
            long age = day - entry.day();
            if (entry.day() == day) {
                next.add(entry.plus(add));
                found = true;
            } else if ((age >= 0 && age < Variety.MONTH_DAYS) || entry.day() > day) {
                next.add(entry);
            }
        }
        if (!found) {
            next.add(new Day(day, add));
        }
        return new FoodLog(next, this.eaten);
    }

    /**
     * Remembers this item on that day.
     * A blank item is ignored. The same item replaces its day. An item a month old or older is dropped.
     */
    public FoodLog eat(long day, String item) {
        if (item == null || item.isBlank()) {
            return this;
        }
        List<Eaten> next = new ArrayList<>();
        boolean found = false;
        for (Eaten entry : this.eaten) {
            if (item.equals(entry.item())) {
                if (!found) {
                    next.add(new Eaten(item, day));
                    found = true;
                }
                continue;
            }
            long age = day - entry.day();
            if (age < Variety.MONTH_DAYS) {
                next.add(entry);
            }
        }
        if (!found) {
            next.add(new Eaten(item, day));
        }
        return new FoodLog(this.days, next);
    }

    private static float[] hungerOf(List<Float> listed) {
        float[] hunger = new float[listed.size()];
        for (int i = 0; i < listed.size(); i++) {
            hunger[i] = listed.get(i);
        }
        return hunger;
    }

    private static List<Float> hungerList(float[] hunger) {
        List<Float> listed = new ArrayList<>(hunger.length);
        for (float value : hunger) {
            listed.add(value);
        }
        return listed;
    }
}
