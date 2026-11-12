package net.modcrafters.minecraft.feastdominion.group;

import net.modcrafters.minecraft.feastdominion.Constants;
import net.modcrafters.minecraft.feastdominion.food.IngredientTable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Groups for every loaded item, built from the server data packs. */
public final class GroupIndex {
    private static Map<String, GroupShares> shares = Map.of();
    private static int resources;

    private GroupIndex() {
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
            shares = build(manager);
            resources = stamp;
            Constants.LOG.info("Food groups cover {} items", shares.size());
        } catch (RuntimeException ex) {
            shares = Map.of();
            resources = stamp;
            Constants.LOG.error("Food groups failed to load", ex);
        }
    }

    public static GroupShares shares(String id) {
        return shares.getOrDefault(id, GroupShares.EMPTY);
    }

    private static Map<String, GroupShares> build(ResourceManager manager) {
        Map<String, GroupShares> items = readGroups(manager, "feast/group", "feast/group/tag/");
        Map<String, GroupShares> tagGroups = readGroups(manager, "feast/group/tag", null);
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
        Map<String, GroupShares> authored = GroupTags.assign(Map.of(), tagGroups, tags, GroupTags.BROAD);
        IngredientTable.paint(authored, IngredientTable.load(manager).groups(), itemIds());
        authored.putAll(items);
        return GroupInference.infer(authored, recipes, tags, hunger());
    }

    private static Map<String, GroupShares> readGroups(ResourceManager manager, String folder, String skipPrefix) {
        Map<String, GroupShares> groups = new HashMap<>();
        manager.listResources(folder, path -> {
            String full = path.getPath();
            return full.endsWith(".json") && (skipPrefix == null || !full.startsWith(skipPrefix));
        }).forEach((location, resource) -> {
            String id = id(location.getPath(), folder.equals("feast/group") ? "feast/group/" : folder + "/");
            if (id.isEmpty()) {
                return;
            }
            try (Reader reader = resource.openAsReader()) {
                GroupShares read = GroupFiles.read(reader);
                if (read.any()) {
                    groups.put(id, read);
                }
            } catch (IOException | RuntimeException ignored) {
                // A bad file is skipped. The other ingredients still load.
            }
        });
        return groups;
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

    private static List<String> merged(List<Resource> stack) {
        List<String> values = new ArrayList<>();
        // The stack lists the highest priority pack first. Walk it from the bottom so a replace drops the lower packs.
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

    private static List<String> itemIds() {
        List<String> ids = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            ids.add(BuiltInRegistries.ITEM.getKey(item).toString());
        }
        return ids;
    }

    private static Map<String, Integer> hunger() {
        Map<String, Integer> hunger = new HashMap<>();
        for (Item item : BuiltInRegistries.ITEM) {
            FoodProperties food = item.getDefaultInstance().get(DataComponents.FOOD);
            if (food != null && food.nutrition() > 0) {
                hunger.put(BuiltInRegistries.ITEM.getKey(item).toString(), food.nutrition());
            }
        }
        return hunger;
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
