package net.modcrafters.minecraft.feastdominion.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DrinkPoseTest {
    private static final float EPSILON = 0.00001F;

    @Test
    void shoulderStaysAttachedThroughoutTheDrink() {
        for (HumanoidArm arm : HumanoidArm.values()) {
            float side = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
            Vector3f shoulder = new Vector3f(-side * 5.0F / 16.0F, 2.0F / 16.0F, 0.0F);
            Vector3f anchor = pose(0.0F, arm).transformPosition(new Vector3f(shoulder));
            for (int frame = 0; frame <= 400; frame++) {
                Vector3f actual = pose(frame / 10.0F, arm).transformPosition(new Vector3f(shoulder));
                assertTrue(anchor.distance(actual) < EPSILON, "Shoulder moved at frame " + frame + " for " + arm);
            }
        }
    }

    @Test
    void handMakesALargeUpwardAndInwardArcThenReturnsToRest() {
        Vector3f hand = new Vector3f(-5.0F / 16.0F, 12.0F / 16.0F, 0.0F);
        Vector3f water = pose(14.0F, HumanoidArm.RIGHT).transformPosition(new Vector3f(hand));
        Vector3f mouth = pose(27.0F, HumanoidArm.RIGHT).transformPosition(new Vector3f(hand));
        assertTrue(mouth.y - water.y > 0.6F, "Hand should rise to the mouth");
        assertTrue(water.x - mouth.x > 0.2F, "Hand should move toward the center");
        assertTrue(mouth.distance(water) > 0.8F, "The hand should do the travelling");
        assertTrue(pose(0.0F, HumanoidArm.RIGHT).equals(pose(40.0F, HumanoidArm.RIGHT), EPSILON));
    }

    @Test
    void leftArmMirrorsTheRightArmThroughoutTheDrink() {
        for (int frame = 0; frame <= 400; frame++) {
            Vector3f right = pose(frame / 10.0F, HumanoidArm.RIGHT)
                    .transformPosition(new Vector3f(-5.0F / 16.0F, 12.0F / 16.0F, 0.0F));
            Vector3f left = pose(frame / 10.0F, HumanoidArm.LEFT)
                    .transformPosition(new Vector3f(5.0F / 16.0F, 12.0F / 16.0F, 0.0F));
            right.x = -right.x;
            assertTrue(right.distance(left) < EPSILON, "Hands differ at frame " + frame);
        }
    }

    private static Matrix4f pose(float elapsedTicks, HumanoidArm arm) {
        int wholeTicks = (int) elapsedTicks;
        PoseStack stack = new PoseStack();
        DrinkPose.place(stack, elapsedTicks - wholeTicks, 40 - wholeTicks, arm);
        return stack.last().pose();
    }
}
