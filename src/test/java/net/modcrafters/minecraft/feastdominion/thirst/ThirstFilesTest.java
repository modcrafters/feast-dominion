package net.modcrafters.minecraft.feastdominion.thirst;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThirstFilesTest {
    @Test
    void aNumberIsReadAndAMissingKeyIsZero() {
        assertEquals(6, ThirstFiles.read("{\"sweet\":0.2,\"thirst\":6}"));
        assertEquals(-6, ThirstFiles.read("{\"thirst\":-6}"));
        assertEquals(0, ThirstFiles.read("{\"sweet\":0.2}"));
        assertEquals(6.0F, ThirstFiles.quench("{\"thirst\":6}"));
        assertEquals(2.0F, ThirstFiles.quench("{\"thirst\":6,\"quench\":2}"));
    }

    @Test
    void aDrinkStaysOnTheBar() {
        assertEquals(20, Thirst.changed(18, 6));
        assertEquals(0, Thirst.changed(2, -6));
        assertEquals(13, Thirst.changed(10, 3));
    }
}
