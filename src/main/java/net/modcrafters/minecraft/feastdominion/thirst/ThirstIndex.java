package net.modcrafters.minecraft.feastdominion.thirst;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/** Thirst numbers for loaded items, read from the same item files as tastes. */
public final class ThirstIndex {
    private static final String FOLDER = "feast/taste/item";
    private static Map<String, Integer> values = Map.of();
    private static Map<String, Float> quench = Map.of();
    private static int resources;

    private ThirstIndex() {
    }

    public static void ensure(ResourceManager manager) {
        if (manager == null) {
            return;
        }
        int stamp = System.identityHashCode(manager);
        if (stamp == resources) {
            return;
        }
        try {
            Loaded loaded = read(manager);
            values = loaded.thirst();
            quench = loaded.quench();
            resources = stamp;
        } catch (RuntimeException ex) {
            values = Map.of();
            quench = Map.of();
            resources = stamp;
            Constants.LOG.error("Thirst numbers failed to load", ex);
        }
    }

    public static int points(String id) {
        return values.getOrDefault(id, 0);
    }

    public static float quench(String id) {
        return quench.getOrDefault(id, 0.0F);
    }

    private record Loaded(Map<String, Integer> thirst, Map<String, Float> quench) {
    }

    private static Loaded read(ResourceManager manager) {
        Map<String, Integer> found = new HashMap<>();
        Map<String, Float> reserves = new HashMap<>();
        manager.listResources(FOLDER, path -> path.getPath().endsWith(".json")).forEach((location, resource) -> {
            String id = id(location.getPath());
            if (id.isEmpty()) {
                return;
            }
            try (Reader reader = resource.openAsReader()) {
                String json = text(reader);
                int thirst = ThirstFiles.read(json);
                float reserve = ThirstFiles.quench(json);
                if (thirst != 0) {
                    found.put(id, thirst);
                }
                if (reserve > 0.0F) {
                    reserves.put(id, reserve);
                }
            } catch (IOException | RuntimeException ignored) {
                // A bad file is skipped. The other numbers still load.
            }
        });
        return new Loaded(found, reserves);
    }

    private static String id(String path) {
        String prefix = FOLDER + "/";
        if (!path.startsWith(prefix) || !path.endsWith(".json")) {
            return "";
        }
        String body = path.substring(prefix.length(), path.length() - ".json".length());
        int slash = body.indexOf('/');
        if (slash <= 0 || slash == body.length() - 1) {
            return "";
        }
        return body.substring(0, slash) + ":" + body.substring(slash + 1);
    }

    private static String text(Reader reader) throws IOException {
        StringBuilder text = new StringBuilder();
        char[] buffer = new char[2048];
        int read;
        while ((read = reader.read(buffer)) >= 0) {
            text.append(buffer, 0, read);
        }
        return text.toString();
    }
}
