package net.modcrafters.minecraft.feastdominion.taste;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Reader;
import java.util.Locale;

/** One authored file, naming the tastes on an ingredient or a tag. */
public final class TasteFiles {
    private TasteFiles() {
    }

    public static TasteProfile read(Reader reader) {
        return read(JsonParser.parseReader(reader).getAsJsonObject());
    }

    public static TasteProfile read(String json) {
        return read(JsonParser.parseString(json).getAsJsonObject());
    }

    private static TasteProfile read(JsonObject object) {
        float[] values = new float[Taste.values().length];
        float poison = 0.0F;
        for (Taste taste : Taste.values()) {
            String key = taste.name().toLowerCase(Locale.ROOT);
            if (object.has(key) && object.get(key).isJsonPrimitive()) {
                values[taste.ordinal()] = object.get(key).getAsFloat();
            }
        }
        if (object.has("poison") && object.get("poison").isJsonPrimitive()) {
            poison = object.get("poison").getAsFloat();
        }
        return TasteProfile.from(values, poison);
    }
}
