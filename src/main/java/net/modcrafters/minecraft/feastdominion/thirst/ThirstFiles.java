package net.modcrafters.minecraft.feastdominion.thirst;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/** The thirst number on one authored file. Missing means the item does not change thirst. */
public final class ThirstFiles {
    private ThirstFiles() {
    }

    public static int read(String json) {
        return read(JsonParser.parseString(json).getAsJsonObject());
    }

    public static int read(JsonObject object) {
        return number(object, "thirst");
    }

    /** A file with thirst and no quench fills the reserve by the thirst it restores. */
    public static float quench(String json) {
        JsonObject object = JsonParser.parseString(json).getAsJsonObject();
        if (object.has("quench") && object.get("quench").isJsonPrimitive() && object.get("quench").getAsJsonPrimitive().isNumber()) {
            return object.get("quench").getAsFloat();
        }
        return Math.max(read(object), 0);
    }

    private static int number(JsonObject object, String key) {
        if (!object.has(key) || !object.get(key).isJsonPrimitive() || !object.get(key).getAsJsonPrimitive().isNumber()) {
            return 0;
        }
        return object.get(key).getAsInt();
    }
}
