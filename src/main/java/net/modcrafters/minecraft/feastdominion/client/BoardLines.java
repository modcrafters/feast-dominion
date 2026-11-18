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

    /** Low and full rows bob in black. The middle stays still. */
    public static boolean marked(int points) {
        return Wellness.band(points) != DietBand.MIDDLE;
    }

    /** The taste row bobs only when every taste has shown up. A narrow month has no curse. */
    public static boolean tasteFull(int marks) {
        return marks >= Wellness.POINTS;
    }

    /**
     * The effect on this group, or null when there is nothing to say.
     * A lone full group has no buff until every group is high.
     */
    public static String effect(FoodGroup group, int points, boolean wellFed) {
        DietBand band = Wellness.band(points);
        if (band == DietBand.LOW) {
            return trouble(group);
        }
        if (band == DietBand.HIGH && wellFed) {
            return "feast_dominion.wellbeing.well_fed.effect";
        }
        return null;
    }

    /** The first line of a group tooltip. {@code argument} is the low curse named by a quiet row. */
    public static RowTip groupTip(FoodGroup group, int points, boolean wellFed) {
        DietBand band = Wellness.band(points);
        if (band == DietBand.LOW) {
            return new RowTip(trouble(group), null);
        }
        if (band == DietBand.HIGH && wellFed) {
            return new RowTip("feast_dominion.wellbeing.well_fed.effect", null);
        }
        if (band == DietBand.HIGH) {
            return new RowTip("feast_dominion.wellbeing.group.full", null);
        }
        return new RowTip("feast_dominion.wellbeing.group.quiet", trouble(group));
    }

    /** The first line of the thirst tooltip. Empty thirst is Weakness. */
    public static RowTip thirstTip(int points) {
        if (points <= 0) {
            return new RowTip("feast_dominion.wellbeing.thirst.empty", null);
        }
        return new RowTip("feast_dominion.wellbeing.thirst.held", null);
    }

    private static String trouble(FoodGroup group) {
        return "feast_dominion.wellbeing.trouble." + group.name().toLowerCase(Locale.ROOT);
    }

    /** A tooltip line, and an optional second translation argument. */
    public record RowTip(String key, String argument) {
    }
}
