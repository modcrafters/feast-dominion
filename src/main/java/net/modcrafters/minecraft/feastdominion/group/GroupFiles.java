package net.modcrafters.minecraft.feastdominion.group;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;

import java.io.Reader;
import java.util.Locale;

/** One authored file, naming the groups on a raw ingredient. */
public final class GroupFiles {
    private GroupFiles() {
    }

    public static GroupShares read(Reader reader) {
        JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
        float[] values = new float[FoodGroup.values().length];
        for (FoodGroup group : FoodGroup.values()) {
            String key = group.name().toLowerCase(Locale.ROOT);
            if (object.has(key) && object.get(key).isJsonPrimitive()) {
                values[group.ordinal()] = object.get(key).getAsFloat();
            }
        }
        return GroupShares.from(values);
    }
}
