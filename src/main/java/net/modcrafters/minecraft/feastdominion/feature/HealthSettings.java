package net.modcrafters.minecraft.feastdominion.feature;

/** Floor, ceiling, and the three whole-number weights. */
public record HealthSettings(double floor, double ceiling, int dayWeight, int weekWeight, int monthWeight) {
    public static final HealthSettings DEFAULT = new HealthSettings(7.5, 20, 50, 30, 20);

    public double hearts(Variety.Counts counts) {
        return Variety.hearts(Variety.score(counts, dayWeight, weekWeight, monthWeight), floor, ceiling);
    }
}
