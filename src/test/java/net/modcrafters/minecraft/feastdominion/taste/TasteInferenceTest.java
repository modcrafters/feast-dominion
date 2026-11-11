package net.modcrafters.minecraft.feastdominion.taste;

import net.modcrafters.minecraft.feastdominion.group.GroupInference;
import net.modcrafters.minecraft.feastdominion.group.GroupRecipes;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TasteInferenceTest {
    @Test
    void tastesCoverWhatTheIngredientsLeaveOpen() {
        TasteProfile covered = TasteProfile.cover(List.of(
                TasteProfile.of(Taste.SWEET, 0.5F),
                TasteProfile.of(Taste.SWEET, 0.5F),
                TasteProfile.of(Taste.SALTY, 1.0F)
        ));
        assertEquals(0.75F, covered.get(Taste.SWEET), 0.001F);
        assertEquals(1.0F, covered.get(Taste.SALTY), 0.001F);
        assertTrue(covered.shown(Taste.SWEET));
        assertFalse(TasteProfile.of(Taste.BITTER, 0.19F).shown(Taste.BITTER));
    }

    @Test
    void sugarSeasonsThePieAndTheSlice() {
        String pie = """
                {"type":"minecraft:crafting_shaped","key":{"a":"minecraft:apple","x":"minecraft:sugar","w":"minecraft:wheat"},"pattern":["a","x","w"],"result":{"id":"farmersdelight:apple_pie"}}
                """;
        String slice = """
                {"type":"farmersdelight:cutting","ingredients":[{"item":"farmersdelight:apple_pie"}],"tool":[{"tag":"c:tools/knife"}],"result":[{"item":{"id":"farmersdelight:apple_pie_slice","count":4}}]}
                """;
        Map<String, TasteProfile> known = TasteInference.infer(
                Map.of("minecraft:sugar", TasteFiles.read("{\"sweet\":1}")),
                List.of(recipe("apple_pie", pie), recipe("slice", slice)),
                Map.of()
        );
        assertEquals(List.of(Taste.SWEET), known.get("farmersdelight:apple_pie").shown());
        assertEquals(List.of(Taste.SWEET), known.get("farmersdelight:apple_pie_slice").shown());
    }

    @Test
    void heatClearsPoisonAndAddsSavory() {
        String steak = """
                {"type":"minecraft:smelting","ingredient":"minecraft:beef","result":{"id":"minecraft:cooked_beef"}}
                """;
        String stew = """
                {"type":"minecraft:crafting_shapeless","ingredients":["minecraft:beef","minecraft:carrot"],"result":{"id":"example:raw_stew"}}
                """;
        TasteProfile beef = TasteFiles.read("{\"savory\":0.55,\"fat\":0.35,\"poison\":0.25}");
        Map<String, TasteProfile> known = TasteInference.infer(
                Map.of(
                        "minecraft:beef", beef,
                        "minecraft:carrot", TasteFiles.read("{\"sweet\":0.5,\"savory\":0.1}")
                ),
                List.of(recipe("steak", steak), recipe("stew", stew)),
                Map.of()
        );
        TasteProfile cooked = known.get("minecraft:cooked_beef");
        assertEquals(0.75F, cooked.get(Taste.SAVORY), 0.001F);
        assertEquals(0.55F, cooked.get(Taste.FAT), 0.001F);
        assertEquals(0.0F, cooked.poison(), 0.001F);
        assertTrue(known.get("example:raw_stew").poisonous());
    }

    @Test
    void aSaltTagSeasonsMembersUnlessTheItemSaysOtherwise() {
        Map<String, TasteProfile> known = TasteInference.authored(
                Map.of("example:seasoned_salt", TasteProfile.of(Taste.SPICY, 1.0F)),
                Map.of("c:dusts/salt", TasteProfile.of(Taste.SALTY, 1.0F)),
                Map.of("c:dusts/salt", List.of("example:salt", "example:seasoned_salt"))
        );
        assertEquals(List.of(Taste.SALTY), known.get("example:salt").shown());
        assertEquals(List.of(Taste.SPICY), known.get("example:seasoned_salt").shown());
    }

    private static GroupInference.Recipe recipe(String id, String json) {
        return GroupRecipes.read(id, json);
    }
}
