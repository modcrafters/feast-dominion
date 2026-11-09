package net.modcrafters.minecraft.feastdominion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** {@code config/feast_dominion.properties}. Missing file, or a missing key, spends thirst and hunger in peaceful. */
public final class FeastConfig {
    public static final String FILE_NAME = "feast_dominion.properties";
    static final String KEY = "drain_in_peaceful";
    private static final String TEMPLATE = """
            # Peaceful difficulty.
            # true: thirst, hunger, and saturation keep falling. An empty stomach damages you down to five hearts.
            # false: vanilla. Thirst holds still, and hunger and saturation fill back up.
            drain_in_peaceful=true
            """;

    private static volatile boolean drainInPeaceful = true;

    private FeastConfig() {
    }

    public static boolean drainInPeaceful() {
        return drainInPeaceful;
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
                String suffix = text.endsWith("\n") ? TEMPLATE : "\n" + TEMPLATE;
                Files.writeString(file, text + suffix);
                text = Files.readString(file);
            }
            drainInPeaceful = parse(text);
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
}
