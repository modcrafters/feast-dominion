package net.modcrafters.minecraft.feastdominion.thirst;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DrinkingTest {
    @Test
    void freshWaterAddsOneDrinkAndStopsAtFull() {
        assertEquals(16, Drink.after(10, false));
        assertEquals(Thirst.POINTS, Drink.after(18, false));
    }

    @Test
    void saltWaterTakesOneDrinkAndStopsAtEmpty() {
        assertEquals(4, Drink.after(10, true));
        assertEquals(0, Drink.after(2, true));
    }

    @Test
    void emptyHandOnAWaterFaceStartsADrink() {
        assertEquals(Drink.Decision.DRINK, Drink.decide(true, true, true, false));
    }

    @Test
    void occupiedHandOrDryFaceOrCreativePasses() {
        assertEquals(Drink.Decision.PASS, Drink.decide(false, true, true, false));
        assertEquals(Drink.Decision.PASS, Drink.decide(true, false, true, false));
        assertEquals(Drink.Decision.PASS, Drink.decide(true, true, false, false));
    }

    @Test
    void holdingTheClickDoesNotStartAnotherSip() {
        assertEquals(Drink.Decision.WAITING, Drink.decide(true, true, true, true));
        assertEquals(40, Drink.HOLD);
        assertEquals(0.0F, Drink.held(0.0F, 40));
        assertEquals(1.0F, Drink.held(0.0F, 0));
    }
}
