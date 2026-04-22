package net.raptorzizi.acolyte.entity.goals;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.NeutralWizard;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class FlyCombatHelper {
    private final NeutralWizard mob;
    private final double radius;
    private final double speedModifier;

    private double xd, yd, zd;
    private int moveTime;

    public FlyCombatHelper(NeutralWizard mob, double radius, double speedModifier) {
        this.mob = mob;
        this.radius = radius;
        this.speedModifier = speedModifier;
    }

    public void reset() {
        xd = 0;
        yd = 0;
        zd = 0;
        moveTime = 0;
    }

    public void tick() {
        LivingEntity target = mob.getTarget();

        if (target == null) {
            int groundY = mob.level().getHeight(
                    Heightmap.Types.MOTION_BLOCKING, (int) mob.getX(), (int) mob.getZ());

            if (mob.getY() > groundY + 0.1) {
                Vec3 delta = mob.getDeltaMovement();
                mob.setDeltaMovement(
                        delta.x * 0.85,
                        Math.max(-0.15, delta.y - 0.02),
                        delta.z * 0.85
                );
                mob.setNoGravity(false);
            } else {
                mob.setDeltaMovement(0, 0, 0);
                reset();
            }
            return;
        }

        RandomSource random = mob.getRandom();
        Vec3 myPos = mob.position();
        Vec3 targetPos = target.position();
        double distToTarget = myPos.distanceTo(targetPos);

        moveTime--;

        if (moveTime <= 0) {
            moveTime = 40 + random.nextInt(80);

            xd = random.nextGaussian() * 0.3;
            yd = random.nextGaussian() * 0.15;
            zd = random.nextGaussian() * 0.3;

            if (distToTarget > radius * 0.8) {
                Vec3 toTarget = targetPos.subtract(myPos).normalize();
                xd = xd * 0.3 + toTarget.x * 0.4;
                yd = yd * 0.3 + toTarget.y * 0.2;
                zd = zd * 0.3 + toTarget.z * 0.4;
            }
        }
        Vec3 delta = mob.getDeltaMovement();

        double newDx = delta.x + (Math.signum(xd - delta.x)) * 0.02;
        double newDy = delta.y + (Math.signum(yd - delta.y)) * 0.01;
        double newDz = delta.z + (Math.signum(zd - delta.z)) * 0.02;

        double maxSpeed = 0.25;
        double horizontalSpeed = Math.sqrt(newDx * newDx + newDz * newDz);
        if (horizontalSpeed > maxSpeed) {
            double scale = maxSpeed / horizontalSpeed;
            newDx *= scale;
            newDz *= scale;
        }
        newDy = Math.max(-0.15, Math.min(0.15, newDy));

        mob.setDeltaMovement(newDx, newDy, newDz);

        float speed = (float) Math.sqrt(newDx * newDx + newDz * newDz);
        if (speed > 0.05F) {
            mob.yBodyRot = (float) Math.toDegrees(Math.atan2(newDz, newDx)) - 90.0F;
        }

        Vec3 current = mob.position();
        int groundY = mob.level().getHeight(
                Heightmap.Types.MOTION_BLOCKING, (int) current.x, (int) current.z);

        if (current.y < groundY + 2.0 && yd < 0) {
            yd = Math.abs(yd);
        }
        if (current.y > groundY + 12.0 && yd > 0) {
            yd = -Math.abs(yd);
        }

        mob.getLookControl().setLookAt(
                target.getX(), target.getEyeY(), target.getZ(),
                30.0F, 30.0F
        );
    }
}