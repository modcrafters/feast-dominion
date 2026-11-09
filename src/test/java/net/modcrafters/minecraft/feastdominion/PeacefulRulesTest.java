package net.modcrafters.minecraft.feastdominion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PeacefulRulesTest {
    @Test
    void thirstHoldsOnlyForVanillaPeaceful() {
        assertTrue(PeacefulRules.thirstDrains(true, true));
        assertTrue(PeacefulRules.thirstDrains(true, false));
        assertTrue(PeacefulRules.thirstDrains(false, false));
        assertFalse(PeacefulRules.thirstDrains(false, true));
    }

    @Test
    void hungerFillsBackUpOnlyInVanillaPeaceful() {
        assertFalse(PeacefulRules.fillHunger(true));
        assertTrue(PeacefulRules.fillHunger(false));
    }

    @Test
    void emptyStomachKeepsStarvationDamage() {
        assertFalse(PeacefulRules.heal(true, 0));
        assertTrue(PeacefulRules.heal(true, 1));
        assertTrue(PeacefulRules.heal(false, 0));
    }

    @Test
    void anEmptySaturationDropsOneHungerAfterPeacefulSkipsIt() {
        assertTrue(PeacefulRules.hungerDropped(true, true, 4.1F, 0.0F, 20, 20));
        assertFalse(PeacefulRules.hungerDropped(true, true, 4.0F, 0.0F, 20, 20));
        assertFalse(PeacefulRules.hungerDropped(true, true, 8.0F, 0.1F, 20, 20));
        assertFalse(PeacefulRules.hungerDropped(false, true, 8.0F, 0.0F, 20, 20));
        assertFalse(PeacefulRules.hungerDropped(true, false, 8.0F, 0.0F, 20, 19));
        assertFalse(PeacefulRules.hungerDropped(true, true, 8.0F, 0.0F, 0, 0));
    }
}
