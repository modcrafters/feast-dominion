package net.modcrafters.minecraft.feastdominion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstantsTest {
    @Test
    void modId() {
        assertEquals("feast_dominion", Constants.MOD_ID);
    }
}
