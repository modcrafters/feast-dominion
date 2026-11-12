package net.modcrafters.minecraft.feastdominion.food;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.modcrafters.minecraft.feastdominion.group.GroupFiles;
import net.modcrafters.minecraft.feastdominion.group.GroupShares;
import net.modcrafters.minecraft.feastdominion.taste.TasteFiles;
import net.modcrafters.minecraft.feastdominion.taste.TasteProfile;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Raw ingredients keyed by the item's own name, so any mod's lettuce matches the lettuce row.
 * An item file still wins over this table.
 */
public final class IngredientTable {
    private IngredientTable() {
    }

    public record Names(Map<String, GroupShares> groups, Map<String, TasteProfile> tastes) {
    }

    public static Names load(ResourceManager manager) {
        Map<String, GroupShares> groups = new HashMap<>();
        Map<String, TasteProfile> tastes = new HashMap<>();
        manager.listResourceStacks("feast/name", path -> path.getPath().endsWith(".json")).forEach((location, stack) -> {
            for (int i = stack.size() - 1; i >= 0; i--) {
                try (Reader reader = stack.get(i).openAsReader()) {
                    absorb(groups, tastes, text(reader));
                } catch (IOException | RuntimeException ignored) {
                    // A bad row is skipped. The rest of the table still loads.
                }
            }
        });
        return new Names(groups, tastes);
    }

    public static Names read(String json) {
        Map<String, GroupShares> groups = new HashMap<>();
        Map<String, TasteProfile> tastes = new HashMap<>();
        absorb(groups, tastes, json);
        return new Names(groups, tastes);
    }

    /** A name row replaces whatever a crop tag already wrote. */
    public static <T> void paint(Map<String, T> known, Map<String, T> byName, Iterable<String> itemIds) {
        if (byName.isEmpty()) {
            return;
        }
        for (String itemId : itemIds) {
            T value = find(byName, itemId);
            if (value != null) {
                known.put(itemId, value);
            }
        }
    }

    public static <T> T find(Map<String, T> byName, String itemId) {
        for (String key : keys(itemId)) {
            T value = byName.get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private static void absorb(Map<String, GroupShares> groups, Map<String, TasteProfile> tastes, String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
            if (!entry.getValue().isJsonObject()) {
                continue;
            }
            JsonObject body = entry.getValue().getAsJsonObject();
            String text = body.toString();
            GroupShares group = GroupFiles.read(text);
            TasteProfile taste = TasteFiles.read(text);
            List<String> names = new ArrayList<>();
            names.add(entry.getKey());
            if (body.has("also") && body.get("also").isJsonArray()) {
                for (JsonElement alias : body.getAsJsonArray("also")) {
                    if (alias.isJsonPrimitive()) {
                        names.add(alias.getAsString());
                    }
                }
            }
            for (String name : names) {
                put(groups, name, group);
                put(tastes, name, taste);
            }
        }
    }

    private static <T> void put(Map<String, T> table, String name, T value) {
        if (value == null || name.isEmpty()) {
            return;
        }
        if (value instanceof GroupShares shares && !shares.any()) {
            return;
        }
        if (value instanceof TasteProfile taste && !taste.any()) {
            return;
        }
        String key = name.toLowerCase(Locale.ROOT);
        table.put(key, value);
        table.put(key.replace("_", ""), value);
    }

    private static List<String> keys(String itemId) {
        String name = pathName(itemId);
        List<String> keys = new ArrayList<>();
        addKey(keys, name);
        if (name.startsWith("pam") && name.length() > 3) {
            addKey(keys, name.substring(3));
        }
        return keys;
    }

    private static void addKey(List<String> keys, String name) {
        if (!keys.contains(name)) {
            keys.add(name);
        }
        String flat = name.replace("_", "");
        if (!keys.contains(flat)) {
            keys.add(flat);
        }
    }

    /** The path after the mod id, with a trailing {@code item} taken off. */
    static String pathName(String itemId) {
        int colon = itemId.lastIndexOf(':');
        String path = colon >= 0 ? itemId.substring(colon + 1) : itemId;
        int slash = path.lastIndexOf('/');
        if (slash >= 0) {
            path = path.substring(slash + 1);
        }
        path = path.toLowerCase(Locale.ROOT);
        if (path.endsWith("_item") && path.length() > "_item".length()) {
            path = path.substring(0, path.length() - "_item".length());
        } else if (path.endsWith("item") && path.length() > "item".length()) {
            path = path.substring(0, path.length() - "item".length());
        }
        return path;
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
