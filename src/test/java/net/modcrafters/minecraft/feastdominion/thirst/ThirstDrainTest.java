package net.modcrafters.minecraft.feastdominion.thirst;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThirstDrainTest {
    @Test
    void normalRateEmptiesAPointEveryHalfMinuteOfGameTime() {
        ThirstDrain.Step step = hold(Thirst.POINTS, 0, ThirstDrain.Climate.NORMAL, ThirstDrain.TICKS_PER_POINT - 1);
        assertEquals(Thirst.POINTS, step.points());
        step = ThirstDrain.tick(step.points(), step.drain(), ThirstDrain.Climate.NORMAL);
        assertEquals(Thirst.POINTS - 1, step.points());
    }

    @Test
    void wetIsSlowerAndDryIsFaster() {
        assertEquals(Thirst.POINTS, hold(Thirst.POINTS, 0, ThirstDrain.Climate.WET, 799).points());
        assertEquals(Thirst.POINTS - 1, hold(Thirst.POINTS, 0, ThirstDrain.Climate.WET, 800).points());
        assertEquals(Thirst.POINTS, hold(Thirst.POINTS, 0, ThirstDrain.Climate.DRY, 479).points());
        assertEquals(Thirst.POINTS - 1, hold(Thirst.POINTS, 0, ThirstDrain.Climate.DRY, 480).points());
    }

    @Test
    void anEmptyBarStaysEmpty() {
        ThirstDrain.Step step = ThirstDrain.tick(0, 50, ThirstDrain.Climate.DRY);
        assertEquals(0, step.points());
        assertEquals(0, step.drain());
    }

    @Test
    void aBiomeOnBothListsIsDry() {
        assertEquals(ThirstDrain.Climate.DRY, ThirstDrain.climate(true, true));
        assertEquals(ThirstDrain.Climate.WET, ThirstDrain.climate(true, false));
        assertEquals(ThirstDrain.Climate.NORMAL, ThirstDrain.climate(false, false));
    }

    private static ThirstDrain.Step hold(int points, int drain, ThirstDrain.Climate climate, int ticks) {
        ThirstDrain.Step step = new ThirstDrain.Step(points, drain);
        for (int i = 0; i < ticks; i++) {
            step = ThirstDrain.tick(step.points(), step.drain(), climate);
        }
        return step;
    }
}
