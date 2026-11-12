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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Crop and seasoning tags in one table. The key is the tag id, and a row may name a group, a taste, or both. */
public final class TagTable {
    private TagTable() {
    }

    public record Tables(Map<String, GroupShares> groups, Map<String, TasteProfile> tastes) {
    }

    public static Tables load(ResourceManager manager) {
        Map<String, GroupShares> groups = new HashMap<>();
        Map<String, TasteProfile> tastes = new HashMap<>();
        manager.listResourceStacks("feast/tag", path -> path.getPath().endsWith(".json")).forEach((location, stack) -> {
            for (int i = stack.size() - 1; i >= 0; i--) {
                try (Reader reader = stack.get(i).openAsReader()) {
                    absorb(groups, tastes, text(reader));
                } catch (IOException | RuntimeException ignored) {
                    // A bad file is skipped. The other tags still load.
                }
            }
        });
        return new Tables(groups, tastes);
    }

    public static Tables read(String json) {
        Map<String, GroupShares> groups = new HashMap<>();
        Map<String, TasteProfile> tastes = new HashMap<>();
        absorb(groups, tastes, json);
        return new Tables(groups, tastes);
    }

    private static void absorb(Map<String, GroupShares> groups, Map<String, TasteProfile> tastes, String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
            if (!entry.getValue().isJsonObject()) {
                continue;
            }
            String text = entry.getValue().toString();
            String id = entry.getKey().toLowerCase(Locale.ROOT);
            GroupShares group = GroupFiles.read(text);
            TasteProfile taste = TasteFiles.read(text);
            if (group.any()) {
                groups.put(id, group);
            }
            if (taste.any()) {
                tastes.put(id, taste);
            }
        }
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
