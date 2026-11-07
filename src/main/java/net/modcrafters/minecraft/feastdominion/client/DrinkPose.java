package net.modcrafters.minecraft.feastdominion.client;

import net.modcrafters.minecraft.feastdominion.thirst.Drink;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Quaternionf;
import org.joml.Vector3f;
//? if >=26.1
import org.joml.Matrix4f;

/** A two-second reach, scoop, lift and sip, followed by a return to the empty-hand pose. */
public final class DrinkPose {
    // Camera-space hand directions, converted into rotations about the model's shoulder.
    private static final Quaternionf REACH = aim(0.12F, -0.30F, -0.95F, -8.0F);
    private static final Quaternionf SCOOP = aim(0.02F, -0.20F, -0.98F, -24.0F);
    private static final Quaternionf MOUTH = aim(-0.45F, 0.84F, 0.30F, -12.0F);
    private static final Quaternionf SIP = aim(-0.48F, 0.86F, 0.18F, -18.0F);

    private DrinkPose() {
    }

    public static void place(PoseStack pose, float partialTick, int remainingTicks, HumanoidArm arm) {
        float progress = Drink.held(partialTick, remainingTicks);
        float side = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        float reach = phase(progress, 0.0F, 0.26F);
        // Turn the wrist while the reach finishes, then lift with the palm held upward.
        float scoop = phase(progress, 0.20F, 0.38F);
        float lift = phase(progress, 0.36F, 0.66F);
        float settle = phase(progress, 0.84F, 1.0F);

        // One gentle tip at the mouth. The pulse starts and ends at rest, without a bob during the lift.
        float sip = Mth.sin(phase(progress, 0.66F, 0.84F) * (float) Math.PI);
        sip *= sip;
        Quaternionf rotation = new Quaternionf()
                .slerp(REACH, reach)
                .slerp(SCOOP, scoop)
                .slerp(MOUTH, lift)
                .slerp(SIP, sip)
                .slerp(new Quaternionf(), settle);
        // Reflect the motion for the left arm without reversing its pitch.
        rotation.y *= side;
        rotation.z *= side;

        // Vanilla's resting arm placement stays fixed throughout the drink.
        pose.translate(side * 0.64F, -0.60F, -0.72F);
        turn(pose, Axis.YP.rotationDegrees(side * 45.0F));
        pose.translate(side * -1.0F, 3.6F, 3.5F);
        turn(pose, Axis.ZP.rotationDegrees(side * 120.0F));
        turn(pose, Axis.XP.rotationDegrees(200.0F));
        turn(pose, Axis.YP.rotationDegrees(side * -135.0F));
        pose.translate(side * 5.6F, 0.0F, 0.0F);

        // ModelPart's arm attachment is (-/+5, 2, 0) pixels. Rotate around that
        // attachment, not the hand: the shoulder stays put and the hand traces the arc.
        float shoulderX = -side * 5.0F / 16.0F;
        float shoulderY = 2.0F / 16.0F;
        pose.translate(shoulderX, shoulderY, 0.0F);
        turn(pose, rotation);
        pose.translate(-shoulderX, -shoulderY, 0.0F);
    }

    private static Quaternionf aim(float x, float y, float z, float roll) {
        Quaternionf rest = new Quaternionf()
                .rotateY(45.0F * Mth.DEG_TO_RAD)
                .rotateZ(120.0F * Mth.DEG_TO_RAD)
                .rotateX(200.0F * Mth.DEG_TO_RAD)
                .rotateY(-135.0F * Mth.DEG_TO_RAD);
        Vector3f direction = rest.conjugate().transform(new Vector3f(x, y, z).normalize());
        // The unposed arm runs along local +Y. Twist around that axis to turn the palm.
        return new Quaternionf().rotationTo(new Vector3f(0.0F, 1.0F, 0.0F), direction)
                .rotateY(roll * Mth.DEG_TO_RAD);
    }

    /** Pose only the drinking arm; its shoulder position and the rest of the body stay vanilla. */
    public static void placeThirdPerson(ModelPart arm, ModelPart head, float progress, HumanoidArm hand) {
        float side = hand == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        Quaternionf resting = new Quaternionf().rotationZYX(arm.zRot, arm.yRot, arm.xRot);
        Quaternionf reach = pointArm(new Vector3f(side * 0.10F, 0.65F, -0.75F), side * -8.0F);
        Quaternionf scoop = pointArm(new Vector3f(side * 0.20F, 0.50F, -0.84F), side * -24.0F);

        // Model space has +Y downward and -Z forward. Follow the mouth, including head turns
        // and the existing crouched shoulder position, without moving the arm's attachment.
        Vector3f mouth = new Quaternionf().rotationZYX(head.zRot, head.yRot, head.xRot)
                .transform(new Vector3f(0.0F, -2.0F, -5.0F))
                .add(head.x - arm.x, head.y - arm.y, head.z - arm.z);
        Quaternionf atMouth = pointArm(mouth, side * -12.0F);
        Quaternionf sipping = pointArm(new Vector3f(mouth).add(0.0F, -0.6F, 0.0F), side * -18.0F);
        float sip = Mth.sin(phase(progress, 0.66F, 0.84F) * (float) Math.PI);
        Quaternionf rotation = new Quaternionf(resting)
                .slerp(reach, phase(progress, 0.0F, 0.26F))
                .slerp(scoop, phase(progress, 0.20F, 0.38F))
                .slerp(atMouth, phase(progress, 0.36F, 0.66F))
                .slerp(sipping, sip * sip)
                .slerp(resting, phase(progress, 0.84F, 1.0F));
        Vector3f angles = rotation.getEulerAnglesZYX(new Vector3f());
        arm.xRot = angles.x;
        arm.yRot = angles.y;
        arm.zRot = angles.z;
    }

    private static Quaternionf pointArm(Vector3f direction, float roll) {
        return new Quaternionf().rotationTo(new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f(direction).normalize())
                .rotateY(roll * Mth.DEG_TO_RAD);
    }

    private static float phase(float progress, float start, float end) {
        float t = Mth.clamp((progress - start) / (end - start), 0.0F, 1.0F);
        // Zero velocity and acceleration at phase boundaries keeps the short wrist movements soft.
        return t * t * t * (t * (t * 6.0F - 15.0F) + 10.0F);
    }

    private static void turn(PoseStack pose, Quaternionf rotation) {
        //? if >=26.1 {
        pose.mulPose(new Matrix4f().rotation(rotation));
        //?} else
        //pose.mulPose(rotation);
    }
}
