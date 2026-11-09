package net.modcrafters.minecraft.feastdominion.feature;

import org.junit.jupiter.api.Test;

import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModDependantFeatureTest {
    @Test
    void anAbsentModLeavesTheFeatureOff() {
        Probe feature = new Probe("other", () -> false);
        assertFalse(feature.enabled());
        feature.init();
        assertFalse(feature.started);
    }

    @Test
    void aPresentModTurnsTheFeatureOn() {
        Probe feature = new Probe("other", () -> true);
        assertTrue(feature.enabled());
        feature.init();
        assertTrue(feature.started);
    }

    @Test
    void anAbsentModCanTurnTheFeatureOn() {
        Probe feature = new Probe("other", () -> false, false);
        assertTrue(feature.enabled());
        feature.init();
        assertTrue(feature.started);
    }

    private static final class Probe extends ModDependantFeature {
        private boolean started;

        Probe(String modId, BooleanSupplier present) {
            super(modId, present);
        }

        Probe(String modId, BooleanSupplier present, boolean whenPresent) {
            super(modId, present, whenPresent);
        }

        @Override
        protected void onEnable() {
            started = true;
        }
    }
}
