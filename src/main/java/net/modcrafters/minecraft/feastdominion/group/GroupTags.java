package net.modcrafters.minecraft.feastdominion.group;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Item tags as they appear in recipe files, flattened to item ids. */
public final class GroupTags {
    /**
     * Broad crop tags, applied before any narrower tag. Nuts come last so a nut that is also filed under fruits keeps the nut profile.
     */
    public static final List<String> BROAD = List.of("c:vegetables", "c:fruits", "c:grain", "c:nuts");

    private GroupTags() {
    }

    /**
     * A tag file covers every member. A later narrow tag replaces a broad one, and an item file replaces both.
     */
    public static <T> Map<String, T> assign(
            Map<String, T> items,
            Map<String, T> tagValues,
            Map<String, List<String>> tagMembers,
            Collection<String> broadTags
    ) {
        Map<String, T> known = new HashMap<>();
        for (String id : broadTags) {
            apply(known, id, tagValues, tagMembers);
        }
        List<String> rest = new ArrayList<>();
        for (String id : tagValues.keySet()) {
            if (!broadTags.contains(id)) {
                rest.add(id);
            }
        }
        rest.sort(String::compareTo);
        for (String id : rest) {
            apply(known, id, tagValues, tagMembers);
        }
        known.putAll(items);
        return known;
    }

    private static <T> void apply(
            Map<String, T> known,
            String id,
            Map<String, T> tagValues,
            Map<String, List<String>> tagMembers
    ) {
        T value = tagValues.get(id);
        if (value == null) {
            return;
        }
        for (String item : tagMembers.getOrDefault(id, List.of())) {
            known.put(item, value);
        }
    }

    public record File(boolean replace, List<String> values) {
    }

    public static List<String> items(String json) {
        return file(json).values();
    }

    public static File file(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        List<String> values = new ArrayList<>();
        if (root.has("values") && root.get("values").isJsonArray()) {
            for (JsonElement element : root.getAsJsonArray("values")) {
                if (element.isJsonPrimitive()) {
                    values.add(element.getAsString());
                } else if (element.isJsonObject() && element.getAsJsonObject().has("id")) {
                    values.add(element.getAsJsonObject().get("id").getAsString());
                }
            }
        }
        boolean replace = root.has("replace") && root.get("replace").isJsonPrimitive() && root.get("replace").getAsBoolean();
        return new File(replace, values);
    }

    public static Map<String, List<String>> flatten(Map<String, List<String>> raw) {
        Map<String, List<String>> done = new HashMap<>();
        for (String id : raw.keySet()) {
            expand(id, raw, done, new HashSet<>());
        }
        return done;
    }

    private static List<String> expand(
            String id,
            Map<String, List<String>> raw,
            Map<String, List<String>> done,
            Set<String> stack
    ) {
        List<String> cached = done.get(id);
        if (cached != null) {
            return cached;
        }
        if (!stack.add(id)) {
            return List.of();
        }
        List<String> items = new ArrayList<>();
        for (String entry : raw.getOrDefault(id, List.of())) {
            if (entry.startsWith("#")) {
                for (String item : expand(entry.substring(1), raw, done, stack)) {
                    if (!items.contains(item)) {
                        items.add(item);
                    }
                }
            } else if (!items.contains(entry)) {
                items.add(entry);
            }
        }
        stack.remove(id);
        List<String> frozen = List.copyOf(items);
        done.put(id, frozen);
        return frozen;
    }
}
