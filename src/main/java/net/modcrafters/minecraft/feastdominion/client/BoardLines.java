package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.wellness.DietBand;
import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;
import net.modcrafters.minecraft.feastdominion.wellness.Wellness;

import java.util.Locale;

/** The words on the wellbeing board, and whether the meals column is shown. */
public final class BoardLines {
    private BoardLines() {
    }

    /** The meals column is present only while the variety hearts are on. */
    public static boolean mealsColumn(boolean heartsOn) {
        return heartsOn;
    }

    /** True only when every point is high. Fourteen and above is high. */
    public static boolean wellFed(int[] points) {
        for (int point : points) {
            if (Wellness.band(point) != DietBand.HIGH) {
                return false;
            }
        }
        return true;
    }

    /** Six and below names that group's trouble. Fourteen and above is high. The rest is the middle. */
    public static String groupWord(FoodGroup group, int points) {
        DietBand band = Wellness.band(points);
        if (band == DietBand.LOW) {
            return "feast_dominion.wellbeing.trouble." + group.name().toLowerCase(Locale.ROOT);
        }
        if (band == DietBand.HIGH) {
            return "feast_dominion.wellbeing.band.high";
        }
        return "feast_dominion.wellbeing.band.middle";
    }
}
