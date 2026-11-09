package net.modcrafters.minecraft.feastdominion.feature;

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
            int monthWeight
    ) {
        public static Snapshot fresh() {
            HealthSettings settings = HealthSettings.DEFAULT;
            return new Snapshot(0, 0, 0, settings.floor(), settings.dayWeight(), settings.weekWeight(), settings.monthWeight());
        }

        public static Snapshot of(Variety.Counts counts, HealthSettings settings) {
            return new Snapshot(
                    counts.today(),
                    counts.week(),
                    counts.month(),
                    settings.hearts(counts),
                    settings.dayWeight(),
                    settings.weekWeight(),
                    settings.monthWeight()
            );
        }
    }
}
