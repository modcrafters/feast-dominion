package net.modcrafters.minecraft.feastdominion.thirst;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ThirstClimatesTest {
    @Test
    void wetAndDryListsAreEditableBiomeTags() throws Exception {
        String wet = resource("wet.json");
        String dry = resource("dry.json");
        assertTrue(wet.contains("\"#minecraft:is_jungle\""));
        assertTrue(wet.contains("\"minecraft:swamp\""));
        assertTrue(dry.contains("\"minecraft:desert\""));
        assertTrue(dry.contains("\"#minecraft:is_nether\""));
    }

    private static String resource(String name) throws Exception {
        try (InputStream in = ThirstClimatesTest.class.getResourceAsStream("/data/feast_dominion/tags/worldgen/biome/" + name)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
