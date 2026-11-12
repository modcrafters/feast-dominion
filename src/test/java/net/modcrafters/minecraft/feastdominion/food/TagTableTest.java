package net.modcrafters.minecraft.feastdominion.food;

import net.modcrafters.minecraft.feastdominion.taste.Taste;
import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TagTableTest {
    @Test
    void cropTagsShareOneFile() throws Exception {
        try (InputStream in = TagTableTest.class.getResourceAsStream("/data/feast_dominion/feast/tag/tags.json")) {
            TagTable.Tables tables = TagTable.read(new String(in.readAllBytes(), StandardCharsets.UTF_8));
            assertEquals(100, tables.groups().get("c:vegetables").percent(FoodGroup.VEGETABLES));
            assertEquals(100, tables.groups().get("c:peanuts").percent(FoodGroup.LEGUMES));
            assertEquals(100, tables.groups().get("c:nuts").percent(FoodGroup.FRUIT));
            assertEquals(List.of(Taste.SAVORY, Taste.FAT), tables.tastes().get("c:nuts").shown());
            assertEquals(List.of(Taste.SALTY), tables.tastes().get("c:dusts/salt").shown());
            assertNull(tables.groups().get("c:salts"));
        }
    }
}
