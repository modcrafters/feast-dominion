package net.modcrafters.minecraft.feastdominion.client;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DrinkBodyPoseTest {
    @Test
    void shoulderStaysFixedAndNormalPoseIsRestored() {
        for (HumanoidArm hand : HumanoidArm.values()) {
            ModelPart arm = arm(hand);
            ModelPart head = part();
            Vector3f shoulder = new Vector3f(arm.x, arm.y, arm.z);
            for (int frame = 0; frame <= 400; frame++) {
                arm.xRot = 0.3F;
                arm.yRot = -0.1F;
                arm.zRot = 0.15F;
                Quaternionf resting = rotation(arm);
                DrinkPose.placeThirdPerson(arm, head, frame / 400.0F, hand);
                assertEquals(shoulder.x, arm.x);
                assertEquals(shoulder.y, arm.y);
                assertEquals(shoulder.z, arm.z);
                if (frame == 0 || frame == 400) {
                    assertTrue(Math.abs(resting.dot(rotation(arm))) > 0.99999F);
                }
            }
        }
    }

    @Test
    void handRisesFromTheWaterTowardTheMouthOnBothSides() {
        Vector3f[] tips = new Vector3f[2];
        for (HumanoidArm hand : HumanoidArm.values()) {
            ModelPart arm = arm(hand);
            ModelPart head = part();
            DrinkPose.placeThirdPerson(arm, head, 0.35F, hand);
            Vector3f water = tip(arm);
            DrinkPose.placeThirdPerson(arm, head, 0.66F, hand);
            Vector3f mouth = tip(arm);
            // Model +Y points down. The hand must rise and move toward the face.
            assertTrue(water.y - mouth.y > 8.0F);
            assertTrue(Math.abs(mouth.x) < Math.abs(water.x));
            assertTrue(mouth.z < -3.0F);
            tips[hand == HumanoidArm.RIGHT ? 0 : 1] = mouth;
        }
        tips[0].x = -tips[0].x;
        assertTrue(tips[0].distance(tips[1]) < 0.0001F);
    }

    @Test
    void sipFollowsTheHeadWhileCrouchingAndTurning() {
        for (float pitch : new float[] {-0.5F, 0.0F, 0.8F}) {
            for (HumanoidArm hand : HumanoidArm.values()) {
                ModelPart head = part();
                head.xRot = pitch;
                head.yRot = 0.5F;
                head.y = 4.2F;
                ModelPart arm = arm(hand);
                arm.y += 3.2F;
                Vector3f mouth = rotation(head).transform(new Vector3f(0.0F, -2.0F, -5.0F))
                        .add(head.x, head.y, head.z);
                DrinkPose.placeThirdPerson(arm, head, 0.66F, hand);
                Vector3f towardMouth = mouth.sub(arm.x, arm.y, arm.z).normalize();
                Vector3f armDirection = tip(arm).sub(arm.x, arm.y, arm.z).normalize();
                assertTrue(towardMouth.dot(armDirection) > 0.999F);
            }
        }
    }

    private static ModelPart part() {
        return new ModelPart(List.of(), Map.of());
    }

    private static ModelPart arm(HumanoidArm hand) {
        ModelPart arm = part();
        arm.x = hand == HumanoidArm.RIGHT ? -5.0F : 5.0F;
        arm.y = 2.0F;
        return arm;
    }

    private static Quaternionf rotation(ModelPart part) {
        return new Quaternionf().rotationZYX(part.zRot, part.yRot, part.xRot);
    }

    private static Vector3f tip(ModelPart arm) {
        return rotation(arm).transform(new Vector3f(0.0F, 10.0F, 0.0F)).add(arm.x, arm.y, arm.z);
    }
}
