package net.modcrafters.minecraft.feastdominion.food;

import net.modcrafters.minecraft.feastdominion.group.GroupShares;
import net.modcrafters.minecraft.feastdominion.taste.Taste;
import net.modcrafters.minecraft.feastdominion.taste.TasteProfile;
import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IngredientTableTest {
    @Test
    void theNameIgnoresTheModAndATrailingItem() {
        assertEquals("lettuce", IngredientTable.pathName("pamhc2crops:lettuceitem"));
        assertEquals("lettuce", IngredientTable.pathName("croptopia:lettuce"));
        assertEquals("lettuceseed", IngredientTable.pathName("pamhc2crops:lettuceseeditem"));
        assertEquals("sweet_potato", IngredientTable.pathName("example:sweet_potato"));
        assertEquals("baked_sweet_potato", IngredientTable.pathName("croptopia:baked_sweet_potato"));
    }

    @Test
    void aSharedNameMatchesAnyModAndAnItemFileStillWins() {
        IngredientTable.Names names = IngredientTable.read("""
                {
                  "lettuce": {"vegetables": 1, "bitter": 0.35, "savory": 0.15},
                  "sweetpotato": {"vegetables": 1, "sweet": 0.45, "savory": 0.25},
                  "chile_pepper": {"vegetables": 1, "spicy": 0.8, "also": ["chilipepper", "chili_pepper"]}
                }
                """);
        Map<String, GroupShares> known = new HashMap<>();
        known.put("farmersdelight:cabbage", GroupShares.of(FoodGroup.FRUIT, 1F));
        IngredientTable.paint(known, names.groups(), List.of(
                "pamhc2crops:lettuceitem",
                "croptopia:lettuce",
                "pamhc2crops:lettuceseeditem",
                "example:sweet_potato",
                "pamhc2crops:chilipepperitem",
                "croptopia:baked_sweet_potato",
                "farmersdelight:cabbage"
        ));
        known.put("farmersdelight:cabbage", GroupShares.of(FoodGroup.VEGETABLES, 1F));

        assertEquals(100, known.get("pamhc2crops:lettuceitem").percent(FoodGroup.VEGETABLES));
        assertEquals(100, known.get("croptopia:lettuce").percent(FoodGroup.VEGETABLES));
        assertEquals(100, known.get("example:sweet_potato").percent(FoodGroup.VEGETABLES));
        assertEquals(100, known.get("pamhc2crops:chilipepperitem").percent(FoodGroup.VEGETABLES));
        assertNull(known.get("pamhc2crops:lettuceseeditem"));
        assertNull(known.get("croptopia:baked_sweet_potato"));
        assertEquals(100, known.get("farmersdelight:cabbage").percent(FoodGroup.VEGETABLES));
        assertEquals(0, known.get("farmersdelight:cabbage").percent(FoodGroup.FRUIT));

        TasteProfile chile = IngredientTable.find(names.tastes(), "croptopia:chile_pepper");
        assertEquals(List.of(Taste.SPICY), chile.shown());
        assertEquals(List.of(Taste.BITTER), IngredientTable.find(names.tastes(), "any:lettuce_item").shown());
    }

    @Test
    void theDefaultTableCoversBothModsNames() throws Exception {
        try (InputStream in = IngredientTableTest.class.getResourceAsStream(
                "/data/feast_dominion/feast/name/ingredients.json")) {
            IngredientTable.Names names = IngredientTable.read(new String(in.readAllBytes(), StandardCharsets.UTF_8));
            assertEquals(FoodGroup.VEGETABLES, group(names, "pamhc2crops:lettuceitem"));
            assertEquals(FoodGroup.VEGETABLES, group(names, "croptopia:lettuce"));
            assertEquals(FoodGroup.VEGETABLES, group(names, "example:sweet_potato"));
            assertEquals(FoodGroup.LEGUMES, group(names, "pamhc2crops:peanutitem"));
            assertEquals(FoodGroup.FRUIT, group(names, "pamhc2trees:almonditem"));
            assertEquals(List.of(Taste.SAVORY, Taste.SPICY), IngredientTable.find(names.tastes(), "pamhc2crops:chilipepperitem").shown());
            assertNull(IngredientTable.find(names.groups(), "croptopia:lettuce_seed"));
            assertNull(IngredientTable.find(names.groups(), "croptopia:baked_sweet_potato"));
            assertNull(IngredientTable.find(names.groups(), "any:salt"));
            assertEquals(List.of(Taste.SALTY), IngredientTable.find(names.tastes(), "croptopia:salt").shown());
            assertEquals(FoodGroup.MEAT, group(names, "aquaculture:atlantic_cod"));
            assertEquals(FoodGroup.MEAT, group(names, "aquaculture:tuna"));
            assertEquals(List.of(Taste.SALTY, Taste.SAVORY, Taste.FAT), IngredientTable.find(names.tastes(), "aquaculture:tuna").shown());
            assertEquals(List.of(Taste.SALTY, Taste.BITTER), IngredientTable.find(names.tastes(), "any:pufferfish").shown());
            assertEquals(FoodGroup.MEAT, group(names, "mariculture:herring"));
            assertEquals(FoodGroup.MEAT, group(names, "any:oyster"));
            assertEquals(FoodGroup.VEGETABLES, group(names, "aquaculture:algae"));
            assertNull(IngredientTable.find(names.groups(), "aquaculture:fish_fillet_cooked"));
        }
    }

    private static FoodGroup group(IngredientTable.Names names, String item) {
        GroupShares shares = IngredientTable.find(names.groups(), item);
        FoodGroup found = null;
        for (FoodGroup candidate : FoodGroup.values()) {
            if (shares.get(candidate) > 0.0F) {
                found = candidate;
            }
        }
        return found;
    }
}
