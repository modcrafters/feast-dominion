package net.modcrafters.minecraft.feastdominion.thirst;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThirstIconsTest {
    @Test
    void fullBarIsTenFullDroplets() {
        for (int fromRight = 0; fromRight < 10; fromRight++) {
            assertEquals(ThirstIcons.Fill.FULL, ThirstIcons.fill(Thirst.POINTS, fromRight));
        }
    }

    @Test
    void emptyBarIsTenEmptyDroplets() {
        for (int fromRight = 0; fromRight < 10; fromRight++) {
            assertEquals(ThirstIcons.Fill.EMPTY, ThirstIcons.fill(0, fromRight));
        }
    }

    @Test
    void onePointIsAHalfDropletOnTheRight() {
        assertEquals(ThirstIcons.Fill.HALF, ThirstIcons.fill(1, 0));
        assertEquals(ThirstIcons.Fill.EMPTY, ThirstIcons.fill(1, 1));
    }

    @Test
    void nineteenPointsLeavesTheLeftDropletHalfFull() {
        assertEquals(ThirstIcons.Fill.FULL, ThirstIcons.fill(19, 8));
        assertEquals(ThirstIcons.Fill.HALF, ThirstIcons.fill(19, 9));
    }
}
