package net.modcrafters.minecraft.feastdominion;

import net.modcrafters.minecraft.feastdominion.feature.HealthSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** {@code config/feast_dominion.properties}. Missing file, or a missing key, spends thirst and hunger in peaceful. */
public final class FeastConfig {
    public static final String FILE_NAME = "feast_dominion.properties";
    static final String KEY = "drain_in_peaceful";
    static final String FLOOR = "health_floor_hearts";
    static final String CEILING = "health_ceiling_hearts";
    static final String WEIGHT_DAY = "health_weight_day";
    static final String WEIGHT_WEEK = "health_weight_week";
    static final String WEIGHT_MONTH = "health_weight_month";
    static final String DEBUG = "add_debug_commands";
    /** A run can force the debug command on without editing the properties file. */
    public static final String DEBUG_PROPERTY = "feast_dominion.add_debug_commands";
    private static final String TEMPLATE = """
            # Peaceful difficulty.
            # true: thirst, hunger, and saturation keep falling. An empty stomach damages you down to five hearts.
            # false: vanilla. Thirst holds still, and hunger and saturation fill back up.
            drain_in_peaceful=true
            """;
    private static final String HEALTH_TEMPLATE = """
            # Max health when Spice of Life: Carrot Edition is not installed.
            # Floor and ceiling are hearts. Weights are whole numbers for the day, the week, and the month.
            # A full set of windows is worth the sum of the weights.
            health_floor_hearts=7.5
            health_ceiling_hearts=20
            health_weight_day=50
            health_weight_week=30
            health_weight_month=20
            """;
    private static final String DEBUG_TEMPLATE = """
            # Testing commands. /feast set <hunger> <saturation> <thirst>
            # true registers the command for operators. A run can set feast_dominion.add_debug_commands=true.
            add_debug_commands=false
            """;

    private static volatile boolean drainInPeaceful = true;
    private static volatile boolean debugCommands = false;
    private static volatile HealthSettings health = HealthSettings.DEFAULT;

    private FeastConfig() {
    }

    public static boolean drainInPeaceful() {
        return drainInPeaceful;
    }

    public static HealthSettings health() {
        return health;
    }

    /** The properties file, unless the run set {@link #DEBUG_PROPERTY}. */
    public static boolean debugCommands() {
        String override = System.getProperty(DEBUG_PROPERTY);
        if (override != null) {
            return override.equalsIgnoreCase("true");
        }
        return debugCommands;
    }

    public static void load(Path directory) {
        try {
            Files.createDirectories(directory);
            Path file = directory.resolve(FILE_NAME);
            if (Files.notExists(file)) {
                Files.writeString(file, TEMPLATE);
            }
            String text = Files.readString(file);
            if (!text.contains(KEY)) {
                text = append(text, TEMPLATE);
                Files.writeString(file, text);
            }
            if (!text.contains(FLOOR)) {
                text = append(text, HEALTH_TEMPLATE);
                Files.writeString(file, text);
            }
            if (!text.contains(DEBUG)) {
                text = append(text, DEBUG_TEMPLATE);
                Files.writeString(file, text);
            }
            drainInPeaceful = parse(text);
            debugCommands = debugCommands(text);
            health = healthSettings(text);
        } catch (IOException e) {
            drainInPeaceful = true;
            Constants.LOG.error("Could not read {}. Peaceful difficulty keeps spending thirst and hunger.", FILE_NAME, e);
        }
    }

    static boolean parse(String text) {
        boolean value = true;
        for (String raw : text.split("\n", -1)) {
            String line = raw.strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int split = line.indexOf('=');
            if (split < 0) {
                continue;
            }
            String key = line.substring(0, split).strip();
            if (!KEY.equals(key)) {
                continue;
            }
            String setting = line.substring(split + 1).strip();
            if (setting.equalsIgnoreCase("true")) {
                value = true;
            } else if (setting.equalsIgnoreCase("false")) {
                value = false;
            }
        }
        return value;
    }

    static boolean debugCommands(String text) {
        boolean value = false;
        for (String raw : text.split("\n", -1)) {
            String line = raw.strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int split = line.indexOf('=');
            if (split < 0) {
                continue;
            }
            String key = line.substring(0, split).strip();
            if (!DEBUG.equals(key)) {
                continue;
            }
            String setting = line.substring(split + 1).strip();
            if (setting.equalsIgnoreCase("true")) {
                value = true;
            } else if (setting.equalsIgnoreCase("false")) {
                value = false;
            }
        }
        return value;
    }

    static HealthSettings healthSettings(String text) {
        Double floor = null;
        Double ceiling = null;
        Integer day = null;
        Integer week = null;
        Integer month = null;
        for (String raw : text.split("\n", -1)) {
            String line = raw.strip();
            if (line.isEmpty() || line.startsWith("#") || line.indexOf('=') < 0) {
                continue;
            }
            int split = line.indexOf('=');
            String key = line.substring(0, split).strip();
            String setting = line.substring(split + 1).strip();
            switch (key) {
                case FLOOR -> floor = hearts(key, setting, floor);
                case CEILING -> ceiling = hearts(key, setting, ceiling);
                case WEIGHT_DAY -> day = weight(key, setting, day);
                case WEIGHT_WEEK -> week = weight(key, setting, week);
                case WEIGHT_MONTH -> month = weight(key, setting, month);
                default -> {
                }
            }
        }
        HealthSettings defaults = HealthSettings.DEFAULT;
        return new HealthSettings(
                floor == null ? defaults.floor() : floor,
                ceiling == null ? defaults.ceiling() : ceiling,
                day == null ? defaults.dayWeight() : day,
                week == null ? defaults.weekWeight() : week,
                month == null ? defaults.monthWeight() : month
        );
    }

    private static String append(String text, String block) {
        return text.endsWith("\n") ? text + block : text + "\n" + block;
    }

    private static Double hearts(String key, String setting, Double current) {
        try {
            double value = Double.parseDouble(setting);
            if (Double.isFinite(value)) {
                return value;
            }
        } catch (NumberFormatException ignored) {
            // The message below covers this.
        }
        Constants.LOG.warn("Bad {} value '{}'. Keeping the previous number.", key, setting);
        return current;
    }

    private static Integer weight(String key, String setting, Integer current) {
        if (!setting.isEmpty() && setting.charAt(0) != '-' && setting.indexOf('.') < 0) {
            try {
                int value = Integer.parseInt(setting);
                if (value >= 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // The message below covers this.
            }
        }
        Constants.LOG.warn("Bad {} value '{}'. A weight is a whole number, zero or more.", key, setting);
        return current;
    }
}
