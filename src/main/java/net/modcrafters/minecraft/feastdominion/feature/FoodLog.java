package net.modcrafters.minecraft.feastdominion.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.modcrafters.minecraft.feastdominion.group.GroupShares;
import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;

import java.util.ArrayList;
import java.util.List;

/** Hunger eaten in each group, kept by the day it was eaten. */
public record FoodLog(List<FoodLog.Day> days) {
    public static final FoodLog EMPTY = new FoodLog(List.of());

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

    private static final Codec<float[]> HUNGER = Codec.FLOAT.listOf().xmap(FoodLog::hungerOf, FoodLog::hungerList);

    private static final Codec<Day> DAY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("day").forGetter(Day::day),
            HUNGER.fieldOf("hunger").forGetter(Day::hunger)
    ).apply(instance, Day::new));

    public static final MapCodec<FoodLog> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DAY_CODEC.listOf().optionalFieldOf("days", List.of()).forGetter(FoodLog::days)
    ).apply(instance, FoodLog::new));
    public static final Codec<FoodLog> CODEC = MAP_CODEC.codec();

    public FoodLog {
        days = List.copyOf(days == null ? List.of() : days);
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
        return new FoodLog(next);
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
