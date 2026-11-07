package net.modcrafters.minecraft.feastdominion.thirst;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WaterSourcesTest {
    @Test
    void saltWaterIsOceanAndBeach() throws Exception {
        try (InputStream in = WaterSourcesTest.class.getResourceAsStream("/data/feast_dominion/tags/worldgen/biome/salt_water.json")) {
            String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(json.contains("\"#minecraft:is_ocean\""));
            assertTrue(json.contains("\"#minecraft:is_beach\""));
        }
    }
}
