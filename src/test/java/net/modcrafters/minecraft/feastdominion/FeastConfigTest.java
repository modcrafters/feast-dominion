package net.modcrafters.minecraft.feastdominion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeastConfigTest {
    @Test
    void missingKeySpendsTheBars() {
        assertTrue(FeastConfig.parse("# nothing here\n"));
    }

    @Test
    void falseIsVanillaPeaceful() {
        assertFalse(FeastConfig.parse("""
                # Peaceful difficulty.
                drain_in_peaceful=false
                """));
        assertTrue(FeastConfig.parse("drain_in_peaceful=TRUE"));
    }
}
