package net.modcrafters.minecraft.feastdominion.feature;

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
            List<String> monthItems
    ) {
        public static Snapshot fresh() {
            HealthSettings settings = HealthSettings.DEFAULT;
            return new Snapshot(
                    0,
                    0,
                    0,
                    settings.floor(),
                    settings.dayWeight(),
                    settings.weekWeight(),
                    settings.monthWeight(),
                    List.of(),
                    List.of(),
                    List.of()
            );
        }

        public static Snapshot of(Variety.Counts counts, HealthSettings settings, MealSlots.Windows meals) {
            return new Snapshot(
                    Variety.marks(counts.today()),
                    Variety.marks(counts.week()),
                    Variety.marks(counts.month()),
                    settings.hearts(counts),
                    settings.dayWeight(),
                    settings.weekWeight(),
                    settings.monthWeight(),
                    meals.today(),
                    meals.week(),
                    meals.month()
            );
        }
    }
}
