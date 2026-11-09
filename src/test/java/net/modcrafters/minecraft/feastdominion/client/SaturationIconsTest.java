package net.modcrafters.minecraft.feastdominion.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SaturationIconsTest {
    @Test
    void emptySaturationDrawsNothing() {
        for (int fromRight = 0; fromRight < 10; fromRight++) {
            assertEquals(SaturationIcons.Mark.NONE, SaturationIcons.mark(0.0F, fromRight));
        }
    }

    @Test
    void fullSaturationFillsEveryShank() {
        for (int fromRight = 0; fromRight < 10; fromRight++) {
            assertEquals(SaturationIcons.Mark.FULL, SaturationIcons.mark(20.0F, fromRight));
        }
    }

    @Test
    void aLittleSaturationIsAFaintRightmostMark() {
        assertEquals(SaturationIcons.Mark.TRACE, SaturationIcons.mark(0.1F, 0));
        assertEquals(SaturationIcons.Mark.NONE, SaturationIcons.mark(0.1F, 1));
    }

    @Test
    void onePointIsTheQuarterStep() {
        assertEquals(SaturationIcons.Mark.QUARTER, SaturationIcons.mark(1.0F, 0));
    }

    @Test
    void moreThanOnePointIsTheHalfStep() {
        assertEquals(SaturationIcons.Mark.HALF, SaturationIcons.mark(1.2F, 0));
    }

    @Test
    void twoPointsFillTheRightmostShank() {
        assertEquals(SaturationIcons.Mark.FULL, SaturationIcons.mark(2.0F, 0));
        assertEquals(SaturationIcons.Mark.NONE, SaturationIcons.mark(2.0F, 1));
    }

    @Test
    void threePointsFillOneShankAndLeaveAQuarterOnTheNext() {
        assertEquals(SaturationIcons.Mark.FULL, SaturationIcons.mark(3.0F, 0));
        assertEquals(SaturationIcons.Mark.QUARTER, SaturationIcons.mark(3.0F, 1));
        assertEquals(SaturationIcons.Mark.NONE, SaturationIcons.mark(3.0F, 2));
    }

    @Test
    void saturationAboveTheBarClampsToTenShanks() {
        assertEquals(SaturationIcons.Mark.FULL, SaturationIcons.mark(40.0F, 9));
        assertEquals(10, SaturationIcons.count(40.0F));
    }

    @Test
    void countIsTheMarksUntilTheRowRunsOut() {
        assertEquals(0, SaturationIcons.count(0.0F));
        assertEquals(1, SaturationIcons.count(0.1F));
        assertEquals(1, SaturationIcons.count(2.0F));
        assertEquals(2, SaturationIcons.count(3.0F));
        assertEquals(4, SaturationIcons.count(8.0F));
        assertEquals(3, SaturationIcons.count(5.0F));
        assertEquals(1, SaturationIcons.count(1.0F));
        assertEquals(7, SaturationIcons.count(12.8F));
        assertEquals(SaturationIcons.Mark.FULL, SaturationIcons.mark(12.8F, 5));
        assertEquals(SaturationIcons.Mark.QUARTER, SaturationIcons.mark(12.8F, 6));
    }

    @Test
    void saturationPastAFullBarStartsAnotherLayer() {
        assertEquals(SaturationIcons.Mark.NONE, SaturationIcons.extra(20.0F, 0));
        assertEquals(SaturationIcons.Mark.FULL, SaturationIcons.extra(22.0F, 0));
        assertEquals(SaturationIcons.Mark.NONE, SaturationIcons.extra(22.0F, 1));
        assertEquals(SaturationIcons.Mark.FULL, SaturationIcons.mark(40.0F, 9));
    }
}
