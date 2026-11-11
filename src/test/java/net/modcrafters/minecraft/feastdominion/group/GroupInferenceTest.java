package net.modcrafters.minecraft.feastdominion.group;

import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroupInferenceTest {
    @Test
    void hungerIsTheWeightAndAGroupWithoutHungerCountsAsOne() {
        assertEquals(5, GroupWeight.of(5, true));
        assertEquals(4, GroupWeight.of(4, false));
        assertEquals(1, GroupWeight.of(0, true));
        assertEquals(0, GroupWeight.of(0, false));
    }

    @Test
    void aHamburgerSplitsByHunger() {
        GroupShares bread = GroupShares.of(FoodGroup.GRAIN, 1F);
        GroupShares patty = GroupShares.of(FoodGroup.MEAT, 1F);
        GroupShares leaf = GroupShares.of(FoodGroup.VEGETABLES, 1F);
        GroupShares tomato = GroupShares.of(FoodGroup.VEGETABLES, 1F);
        GroupShares onion = GroupShares.of(FoodGroup.VEGETABLES, 1F);
        GroupShares burger = GroupInference.mix(List.of(
                new GroupInference.Part(bread, 5),
                new GroupInference.Part(patty, 4),
                new GroupInference.Part(leaf, 1),
                new GroupInference.Part(tomato, 1),
                new GroupInference.Part(onion, 2)
        ));
        assertEquals(38, burger.percent(FoodGroup.GRAIN));
        assertEquals(31, burger.percent(FoodGroup.MEAT));
        assertEquals(31, burger.percent(FoodGroup.VEGETABLES));
        assertEquals(0, burger.percent(FoodGroup.FRUIT));
    }

    @Test
    void cookingAndCuttingKeepTheIngredientGroup() {
        String beef = """
                {"type":"minecraft:smelting","ingredient":"minecraft:beef","result":{"id":"minecraft:cooked_beef"}}
                """;
        String bacon = """
                {"type":"farmersdelight:cutting","ingredients":[{"item":"minecraft:porkchop"}],"tool":[{"tag":"c:tools/knife"}],"result":[{"item":{"id":"farmersdelight:bacon","count":2}}]}
                """;
        Map<String, GroupShares> known = GroupInference.infer(
                Map.of(
                        "minecraft:beef", GroupShares.of(FoodGroup.MEAT, 1F),
                        "minecraft:porkchop", GroupShares.of(FoodGroup.MEAT, 1F)
                ),
                List.of(recipe("cooked_beef", beef), recipe("bacon", bacon)),
                Map.of(),
                Map.of("minecraft:beef", 3, "minecraft:porkchop", 3)
        );
        assertEquals(100, known.get("minecraft:cooked_beef").percent(FoodGroup.MEAT));
        assertEquals(100, known.get("farmersdelight:bacon").percent(FoodGroup.MEAT));
    }

    @Test
    void breadAndAPieAreInferredFromRawIngredients() {
        String bread = """
                {"type":"minecraft:crafting_shaped","key":{"#":"minecraft:wheat"},"pattern":["###"],"result":{"id":"minecraft:bread"}}
                """;
        String pie = """
                {"type":"minecraft:crafting_shapeless","ingredients":["minecraft:pumpkin","minecraft:sugar","#minecraft:eggs"],"result":{"id":"minecraft:pumpkin_pie"}}
                """;
        Map<String, GroupShares> known = GroupInference.infer(
                Map.of(
                        "minecraft:wheat", GroupShares.of(FoodGroup.GRAIN, 1F),
                        "minecraft:pumpkin", GroupShares.of(FoodGroup.VEGETABLES, 1F),
                        "minecraft:egg", GroupShares.of(FoodGroup.EGGS, 1F)
                ),
                List.of(recipe("bread", bread), recipe("pie", pie)),
                Map.of("minecraft:eggs", List.of("minecraft:egg", "minecraft:blue_egg")),
                Map.of("minecraft:bread", 5)
        );
        assertEquals(100, known.get("minecraft:bread").percent(FoodGroup.GRAIN));
        assertEquals(50, known.get("minecraft:pumpkin_pie").percent(FoodGroup.VEGETABLES));
        assertEquals(50, known.get("minecraft:pumpkin_pie").percent(FoodGroup.EGGS));
    }

    @Test
    void aSandwichUsesTheInferredBreadAndThePattyHunger() {
        String bread = """
                {"type":"minecraft:crafting_shaped","key":{"#":"minecraft:wheat"},"pattern":["###"],"result":{"id":"minecraft:bread"}}
                """;
        String burger = """
                {"type":"minecraft:crafting_shapeless","ingredients":[
                  {"item":"minecraft:bread"},
                  {"item":"farmersdelight:beef_patty"},
                  {"tag":"c:foods/leafy_green"},
                  {"item":"farmersdelight:tomato"},
                  {"item":"farmersdelight:onion"}
                ],"result":{"id":"farmersdelight:hamburger"}}
                """;
        Map<String, GroupShares> known = GroupInference.infer(
                Map.of(
                        "minecraft:wheat", GroupShares.of(FoodGroup.GRAIN, 1F),
                        "farmersdelight:beef_patty", GroupShares.of(FoodGroup.MEAT, 1F),
                        "farmersdelight:cabbage_leaf", GroupShares.of(FoodGroup.VEGETABLES, 1F),
                        "farmersdelight:tomato", GroupShares.of(FoodGroup.VEGETABLES, 1F),
                        "farmersdelight:onion", GroupShares.of(FoodGroup.VEGETABLES, 1F)
                ),
                List.of(recipe("bread", bread), recipe("burger", burger)),
                Map.of("c:foods/leafy_green", List.of("farmersdelight:cabbage", "farmersdelight:cabbage_leaf")),
                Map.of(
                        "minecraft:bread", 5,
                        "farmersdelight:beef_patty", 4,
                        "farmersdelight:cabbage_leaf", 1,
                        "farmersdelight:tomato", 1,
                        "farmersdelight:onion", 2
                )
        );
        GroupShares hamburger = known.get("farmersdelight:hamburger");
        assertEquals(38, hamburger.percent(FoodGroup.GRAIN));
        assertEquals(31, hamburger.percent(FoodGroup.MEAT));
        assertEquals(31, hamburger.percent(FoodGroup.VEGETABLES));
    }

    @Test
    void aSliceKeepsThePieAfterSugarDropsOut() {
        String pie = """
                {"type":"minecraft:crafting_shaped","key":{"a":"minecraft:apple","x":"minecraft:sugar","w":"minecraft:wheat"},"pattern":["a","x","w"],"result":{"id":"farmersdelight:apple_pie"}}
                """;
        String slice = """
                {"type":"farmersdelight:cutting","ingredients":[{"item":"farmersdelight:apple_pie"}],"tool":[{"tag":"c:tools/knife"}],"result":[{"item":{"id":"farmersdelight:apple_pie_slice","count":4}}]}
                """;
        Map<String, GroupShares> known = GroupInference.infer(
                Map.of(
                        "minecraft:apple", GroupShares.of(FoodGroup.FRUIT, 1F),
                        "minecraft:wheat", GroupShares.of(FoodGroup.GRAIN, 1F)
                ),
                List.of(recipe("apple_pie", pie), recipe("slice", slice)),
                Map.of(),
                Map.of("minecraft:apple", 4)
        );
        assertEquals(80, known.get("farmersdelight:apple_pie").percent(FoodGroup.FRUIT));
        assertEquals(20, known.get("farmersdelight:apple_pie").percent(FoodGroup.GRAIN));
        assertEquals(80, known.get("farmersdelight:apple_pie_slice").percent(FoodGroup.FRUIT));
        assertEquals(20, known.get("farmersdelight:apple_pie_slice").percent(FoodGroup.GRAIN));
    }

    @Test
    void anAuthoredItemIsKept() {
        String smelt = """
                {"type":"minecraft:smelting","ingredient":{"item":"minecraft:beef"},"result":{"id":"minecraft:cooked_beef"}}
                """;
        GroupShares authored = GroupShares.of(FoodGroup.FRUIT, 1F);
        Map<String, GroupShares> known = GroupInference.infer(
                Map.of(
                        "minecraft:beef", GroupShares.of(FoodGroup.MEAT, 1F),
                        "minecraft:cooked_beef", authored
                ),
                List.of(recipe("cooked_beef", smelt)),
                Map.of(),
                Map.of()
        );
        assertEquals(100, known.get("minecraft:cooked_beef").percent(FoodGroup.FRUIT));
        assertEquals(0, known.get("minecraft:cooked_beef").percent(FoodGroup.MEAT));
    }

    @Test
    void aTagFileListsItsItems() {
        List<String> eggs = GroupTags.items("""
                {"values":["minecraft:egg",{"id":"#c:eggs","required":false},"minecraft:brown_egg"]}
                """);
        assertEquals(List.of("minecraft:egg", "#c:eggs", "minecraft:brown_egg"), eggs);
        Map<String, List<String>> flat = GroupTags.flatten(Map.of(
                "c:eggs", List.of("minecraft:egg", "#minecraft:eggs"),
                "minecraft:eggs", List.of("minecraft:egg", "minecraft:blue_egg")
        ));
        assertEquals(List.of("minecraft:egg", "minecraft:blue_egg"), flat.get("c:eggs"));
        assertTrue(flat.get("minecraft:eggs").contains("minecraft:blue_egg"));
    }

    private static GroupInference.Recipe recipe(String id, String json) {
        return GroupRecipes.read(id, json);
    }
}
