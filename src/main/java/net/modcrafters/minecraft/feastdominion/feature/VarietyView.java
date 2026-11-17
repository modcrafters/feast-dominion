package net.modcrafters.minecraft.feastdominion.feature;

import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;
import net.modcrafters.minecraft.feastdominion.wellness.Wellness;

import java.util.Arrays;
import java.util.List;

/** The latest variety numbers for the local player. The server sends them. */
public final class VarietyView {
    private static volatile Snapshot current = Snapshot.fresh();

    private VarietyView() {
    }

    public static Snapshot current() {
        return current;
    }

    public static void set(Snapshot snapshot) {
        current = snapshot;
    }

    public record Snapshot(
            int today,
            int week,
            int month,
            double hearts,
            int dayWeight,
            int weekWeight,
            int monthWeight,
            List<String> todayItems,
            List<String> weekItems,
            List<String> monthItems,
            int tasteMarks,
            int tasteMask
    ) {
        public static Snapshot fresh() {
            HealthSettings settings = HealthSettings.DEFAULT;
            int[] half = new int[FoodGroup.values().length];
            Arrays.fill(half, Wellness.POINTS / 2);
            return new Snapshot(
                    0,
                    0,
                    0,
                    settings.hearts(new Variety.Counts(0, 0, 0), half, 0),
                    settings.dayWeight(),
                    settings.weekWeight(),
                    settings.monthWeight(),
                    List.of(),
                    List.of(),
                    List.of(),
                    0,
                    0
            );
        }

        public static Snapshot of(
                Variety.Counts counts,
                HealthSettings settings,
                MealSlots.Windows meals,
                int[] groupPoints,
                int tasteMask
        ) {
            return new Snapshot(
                    Variety.marks(counts.today()),
                    Variety.marks(counts.week()),
                    Variety.marks(counts.month()),
                    settings.hearts(counts, groupPoints, Constitution.span(tasteMask)),
                    settings.dayWeight(),
                    settings.weekWeight(),
                    settings.monthWeight(),
                    meals.today(),
                    meals.week(),
                    meals.month(),
                    Constitution.marks(tasteMask),
                    tasteMask
            );
        }
    }
}
