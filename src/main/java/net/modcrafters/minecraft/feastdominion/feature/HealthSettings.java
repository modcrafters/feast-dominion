package net.modcrafters.minecraft.feastdominion.feature;

/** Floor, ceiling, and the three whole-number weights of the recent-food share. */
public record HealthSettings(double floor, double ceiling, int dayWeight, int weekWeight, int monthWeight) {
    public static final HealthSettings DEFAULT = new HealthSettings(7.5, 20, 50, 30, 20);

    /**
     * Group points are the wellbeing bars, 0 to 20. Taste span is 0 to 1.
     * Those two plus the recent-food windows fill the span from the floor to the ceiling.
     */
    public double hearts(Variety.Counts counts, int[] groupPoints, double tasteSpan) {
        double recent = Variety.score(counts, dayWeight, weekWeight, monthWeight);
        return Variety.hearts(Constitution.score(groupPoints, tasteSpan, recent), floor, ceiling);
    }
}
