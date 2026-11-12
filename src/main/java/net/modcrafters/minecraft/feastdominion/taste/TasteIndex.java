package net.modcrafters.minecraft.feastdominion.taste;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.food.IngredientTable;
import net.modcrafters.minecraft.feastdominion.group.GroupInference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.modcrafters.minecraft.feastdominion.group.GroupRecipes;
import net.modcrafters.minecraft.feastdominion.group.GroupTags;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Tastes for every loaded item, built from the server data packs. */
public final class TasteIndex {
    private static Map<String, TasteProfile> profiles = Map.of();
    private static int resources;

    private TasteIndex() {
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
            profiles = build(manager);
            resources = stamp;
            Constants.LOG.info("Tastes cover {} items", profiles.size());
        } catch (RuntimeException ex) {
            profiles = Map.of();
            resources = stamp;
            Constants.LOG.error("Tastes failed to load", ex);
        }
    }

    public static TasteProfile profile(String id) {
        return profiles.getOrDefault(id, TasteProfile.EMPTY);
    }

    private static Map<String, TasteProfile> build(ResourceManager manager) {
        Map<String, TasteProfile> items = readProfiles(manager, "feast/taste/item");
        Map<String, TasteProfile> tagTastes = readProfiles(manager, "feast/taste/tag");
        Map<String, List<String>> rawTags = new HashMap<>();
        manager.listResourceStacks("tags/item", path -> path.getPath().endsWith(".json")).forEach((location, stack) -> {
            String tag = tagId(location.getNamespace(), location.getPath());
            if (!tag.isEmpty()) {
                rawTags.put(tag, merged(stack));
            }
        });
        List<GroupInference.Recipe> recipes = new ArrayList<>();
        manager.listResources("recipe", path -> path.getPath().endsWith(".json")).forEach((location, resource) -> {
            try (Reader reader = resource.openAsReader()) {
                recipes.add(GroupRecipes.read(location.toString(), text(reader)));
            } catch (IOException | RuntimeException ignored) {
                // One unreadable recipe does not stop the rest.
            }
        });
        Map<String, List<String>> tags = GroupTags.flatten(rawTags);
        Map<String, TasteProfile> authored = TasteInference.authored(Map.of(), tagTastes, tags);
        IngredientTable.paint(authored, IngredientTable.load(manager).tastes(), itemIds());
        authored.putAll(items);
        return TasteInference.infer(authored, recipes, tags);
    }

    private static List<String> itemIds() {
        List<String> ids = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            ids.add(BuiltInRegistries.ITEM.getKey(item).toString());
        }
        return ids;
    }

    private static Map<String, TasteProfile> readProfiles(ResourceManager manager, String folder) {
        Map<String, TasteProfile> profiles = new HashMap<>();
        manager.listResources(folder, path -> path.getPath().endsWith(".json")).forEach((location, resource) -> {
            String id = id(location.getPath(), folder + "/");
            if (id.isEmpty()) {
                return;
            }
            try (Reader reader = resource.openAsReader()) {
                TasteProfile read = TasteFiles.read(reader);
                if (read.any()) {
                    profiles.put(id, read);
                }
            } catch (IOException | RuntimeException ignored) {
                // A bad file is skipped. The other tastes still load.
            }
        });
        return profiles;
    }

    private static List<String> merged(List<Resource> stack) {
        List<String> values = new ArrayList<>();
        for (int i = stack.size() - 1; i >= 0; i--) {
            try (Reader reader = stack.get(i).openAsReader()) {
                GroupTags.File file = GroupTags.file(text(reader));
                if (file.replace()) {
                    values.clear();
                }
                values.addAll(file.values());
            } catch (IOException | RuntimeException ignored) {
                // A broken tag layer is skipped.
            }
        }
        return values;
    }

    private static String id(String path, String prefix) {
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

    private static String tagId(String namespace, String path) {
        String prefix = "tags/item/";
        if (!path.startsWith(prefix) || !path.endsWith(".json")) {
            return "";
        }
        return namespace + ":" + path.substring(prefix.length(), path.length() - ".json".length());
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
