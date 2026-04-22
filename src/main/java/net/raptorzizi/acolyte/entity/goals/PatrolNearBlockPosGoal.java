package net.raptorzizi.acolyte.entity.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PatrolNearBlockPosGoal extends WaterAvoidingRandomStrollGoal {

    private final Vec3 origin;
    private final float radiusSqr;

    public PatrolNearBlockPosGoal(PathfinderMob mob, BlockPos center, float radius, double speedModifier) {
        super(mob, speedModifier);
        this.origin = Vec3.atBottomCenterOf(center);
        this.radiusSqr = radius * radius;
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        Vec3 pos = super.getPosition();
        if (this.mob.position().distanceToSqr(this.origin) > this.radiusSqr) {
            pos = LandRandomPos.getPosTowards(this.mob, 8, 4, this.origin);
        }

        return pos;
    }
}