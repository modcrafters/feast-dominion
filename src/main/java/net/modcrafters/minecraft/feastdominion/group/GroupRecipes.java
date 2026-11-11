package net.modcrafters.minecraft.feastdominion.group;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/** Reads the ingredient list and the result out of one recipe file. */
public final class GroupRecipes {
    private GroupRecipes() {
    }

    public static GroupInference.Recipe read(String id, String json) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            List<GroupInference.Slot> inputs = inputs(root);
            List<String> results = results(root.get("result"));
            String type = root.has("type") && root.get("type").isJsonPrimitive() ? root.get("type").getAsString() : "";
            return new GroupInference.Recipe(id, inputs, results, type);
        } catch (RuntimeException ex) {
            return new GroupInference.Recipe(id, List.of(), List.of(), "");
        }
    }

    private static List<GroupInference.Slot> inputs(JsonObject root) {
        List<GroupInference.Slot> inputs = new ArrayList<>();
        if (root.has("pattern") && root.has("key") && root.get("pattern").isJsonArray()) {
            JsonObject key = root.getAsJsonObject("key");
            for (JsonElement row : root.getAsJsonArray("pattern")) {
                String line = row.getAsString();
                for (int i = 0; i < line.length(); i++) {
                    String symbol = line.substring(i, i + 1);
                    if (" ".equals(symbol) || !key.has(symbol)) {
                        continue;
                    }
                    inputs.add(slot(key.get(symbol)));
                }
            }
            return inputs;
        }
        if (root.has("ingredients") && root.get("ingredients").isJsonArray()) {
            for (JsonElement element : root.getAsJsonArray("ingredients")) {
                inputs.add(slot(element));
            }
            return inputs;
        }
        if (root.has("ingredient")) {
            inputs.add(slot(root.get("ingredient")));
        }
        return inputs;
    }

    private static GroupInference.Slot slot(JsonElement element) {
        List<String> items = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        add(element, items, tags);
        return new GroupInference.Slot(items, tags);
    }

    private static void add(JsonElement element, List<String> items, List<String> tags) {
        if (element == null || element.isJsonNull()) {
            return;
        }
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                add(child, items, tags);
            }
            return;
        }
        if (element.isJsonPrimitive()) {
            addToken(element.getAsString(), items, tags);
            return;
        }
        if (!element.isJsonObject()) {
            return;
        }
        JsonObject object = element.getAsJsonObject();
        String type = object.has("type") ? object.get("type").getAsString() : "";
        if (type.contains("compound") && object.has("children")) {
            add(object.get("children"), items, tags);
            return;
        }
        if (type.contains("difference") && object.has("base")) {
            add(object.get("base"), items, tags);
            return;
        }
        if (object.has("tag")) {
            addToken("#" + object.get("tag").getAsString(), items, tags);
        }
        if (object.has("item")) {
            add(object.get("item"), items, tags);
        } else if (object.has("id") && !object.has("item")) {
            addToken(object.get("id").getAsString(), items, tags);
        }
    }

    private static void addToken(String token, List<String> items, List<String> tags) {
        if (token.startsWith("#")) {
            tags.add(token.substring(1));
        } else {
            items.add(token);
        }
    }

    private static List<String> results(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return List.of();
        }
        if (element.isJsonArray()) {
            List<String> ids = new ArrayList<>();
            for (JsonElement child : element.getAsJsonArray()) {
                ids.addAll(results(child));
            }
            return ids;
        }
        if (element.isJsonPrimitive()) {
            return List.of(element.getAsString());
        }
        JsonObject object = element.getAsJsonObject();
        if (object.has("item")) {
            return results(object.get("item"));
        }
        if (object.has("id") && object.get("id").isJsonPrimitive()) {
            return List.of(object.get("id").getAsString());
        }
        return List.of();
    }
}
