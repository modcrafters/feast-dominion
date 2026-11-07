package net.modcrafters.minecraft.feastdominion.wellness;

public final class WellnessIcons {
    public enum Fill {
        EMPTY,
        HALF,
        FULL
    }

    private WellnessIcons() {
    }

    public static Fill fill(DietBand band) {
        return switch (band) {
            case LOW -> Fill.EMPTY;
            case MIDDLE -> Fill.HALF;
            case HIGH -> Fill.FULL;
        };
    }
}
