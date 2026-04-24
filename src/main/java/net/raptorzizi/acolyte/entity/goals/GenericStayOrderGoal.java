package net.raptorzizi.acolyte.entity.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.raptorzizi.acolyte.entity.mobs.wizards.human.IRecruitableCompanion;

import java.util.EnumSet;
import java.util.function.Supplier;

public class GenericStayOrderGoal extends Goal {

    private final PathfinderMob mob;
    private final Supplier<Entity> ownerGetter;

    public GenericStayOrderGoal(PathfinderMob mob, Supplier<Entity> ownerGetter) {
        this.mob = mob;
        this.ownerGetter = ownerGetter;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (!(mob instanceof IRecruitableCompanion companion)) return false;
        if (!companion.isRecruited()) return false;
        if (mob.isInWaterOrBubble()) return false;
        if (!mob.onGround()) return false;

        Entity owner = ownerGetter.get();
        if (owner instanceof LivingEntity livingOwner
                && mob.distanceToSqr(livingOwner) < 144.0
                && livingOwner.getLastHurtByMob() != null) return false;

        return companion.isOrderedToStay();
    }

    @Override
    public boolean canContinueToUse() {
        if (!(mob instanceof IRecruitableCompanion companion)) return false;
        if (mob.isInWaterOrBubble()) return false;
        return companion.isOrderedToStay();
    }

    @Override
    public void start() {
        mob.getNavigation().stop();
    }

    @Override
    public void stop() {}
}