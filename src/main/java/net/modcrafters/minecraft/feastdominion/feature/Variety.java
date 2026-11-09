package net.modcrafters.minecraft.feastdominion.feature;

/** Recent foods and drinks, and the hearts they are worth. */
public final class Variety {
    public static final int DAY_CAP = 3;
    public static final int WEEK_CAP = 10;
    public static final int MONTH_CAP = 20;
    /** Today and the six days before it. */
    public static final int WEEK_DAYS = 7;
    /** Today and the twenty-seven days before it. */
    public static final int MONTH_DAYS = 28;

    private Variety() {
    }

    public record Counts(int today, int week, int month) {
    }

    public static Counts counts(FoodLog log, long today) {
        int day = 0;
        int week = 0;
        int month = 0;
        for (long eaten : log.lastEaten().values()) {
            long age = today - eaten;
            if (age < 0) {
                continue;
            }
            if (age == 0) {
                day++;
            }
            if (age < WEEK_DAYS) {
                week++;
            }
            if (age < MONTH_DAYS) {
                month++;
            }
        }
        return new Counts(day, week, month);
    }

    public static double fraction(int count, int cap) {
        if (cap <= 0) {
            return 0;
        }
        return Math.min(1.0, count / (double) cap);
    }

    /** Weights are whole numbers. A full set of windows is their sum, whatever that sum is. */
    public static double score(Counts counts, int dayWeight, int weekWeight, int monthWeight) {
        int sum = dayWeight + weekWeight + monthWeight;
        if (sum <= 0) {
            return 0;
        }
        double filled = dayWeight * fraction(counts.today(), DAY_CAP)
                + weekWeight * fraction(counts.week(), WEEK_CAP)
                + monthWeight * fraction(counts.month(), MONTH_CAP);
        return filled / sum;
    }

    public static double hearts(double score, double floor, double ceiling) {
        return floor + score * (ceiling - floor);
    }

    public static String formatHearts(double hearts) {
        double shown = Math.round(hearts * 100.0) / 100.0;
        if (shown == Math.floor(shown)) {
            return Integer.toString((int) shown);
        }
        String text = Double.toString(shown);
        if (text.endsWith(".0")) {
            return text.substring(0, text.length() - 2);
        }
        return text;
    }
}
