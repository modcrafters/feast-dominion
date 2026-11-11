package net.modcrafters.minecraft.feastdominion.food;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaturationRoomTest {
    @Test
    void roomIsTwiceTheCurrentHunger() {
        assertEquals(0.0F, SaturationRoom.cap(0));
        assertEquals(20.0F, SaturationRoom.cap(10));
        assertEquals(40.0F, SaturationRoom.cap(20));
    }

    @Test
    void storedSaturationStopsAtThatCap() {
        assertEquals(16.0F, SaturationRoom.clamp(10, 16.0F));
        assertEquals(20.0F, SaturationRoom.clamp(10, 28.0F));
        assertEquals(40.0F, SaturationRoom.clamp(20, 40.0F));
        assertEquals(0.0F, SaturationRoom.clamp(0, 5.0F));
    }

    @Test
    void eatingFitsWhileAnyOfThatRoomIsLeft() {
        assertTrue(SaturationRoom.room(20, 20.0F));
        assertTrue(SaturationRoom.room(20, 39.0F));
        assertFalse(SaturationRoom.room(20, 40.0F));
        assertFalse(SaturationRoom.room(10, 20.0F));
        assertFalse(SaturationRoom.room(0, 0.0F));
    }

    @Test
    void fortySaturationStopsEating() {
        assertFalse(SaturationRoom.full(39.0F));
        assertTrue(SaturationRoom.full(40.0F));
    }
}
