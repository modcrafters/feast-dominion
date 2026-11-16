package net.modcrafters.minecraft.feastdominion.feature;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Item ids for today, the week, and the month. Newest day first, then item id, cut at the cap. */
public final class MealSlots {
    public static final int TODAY = 3;
    public static final int WEEK = 10;
    public static final int MONTH = 20;

    private MealSlots() {
    }

    public record Windows(List<String> today, List<String> week, List<String> month) {
        public Windows {
            today = List.copyOf(today == null ? List.of() : today);
            week = List.copyOf(week == null ? List.of() : week);
            month = List.copyOf(month == null ? List.of() : month);
        }
    }

    public static Windows of(List<FoodLog.Eaten> eaten, long today) {
        List<FoodLog.Eaten> source = eaten == null ? List.of() : eaten;
        return new Windows(
                window(source, today, 1, TODAY),
                window(source, today, Variety.WEEK_DAYS, WEEK),
                window(source, today, Variety.MONTH_DAYS, MONTH));
    }

    private static List<String> window(List<FoodLog.Eaten> eaten, long today, int days, int cap) {
        List<FoodLog.Eaten> kept = new ArrayList<>();
        for (FoodLog.Eaten entry : eaten) {
            long age = today - entry.day();
            if (age >= 0 && age < days) {
                kept.add(entry);
            }
        }
        kept.sort(Comparator.comparingLong(FoodLog.Eaten::day).reversed().thenComparing(FoodLog.Eaten::item));
        List<String> items = new ArrayList<>();
        int limit = Math.min(cap, kept.size());
        for (int i = 0; i < limit; i++) {
            items.add(kept.get(i).item());
        }
        return List.copyOf(items);
    }
}
