package net.raptorzizi.acolyte.entity.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Items;

import java.util.EnumSet;

public class UniversalRangedGoal<T extends PathfinderMob & RangedAttackMob> extends Goal {

    private final T mob;
    private final double speedMod;
    private final int attackInterval;
    private final float attackRadius;
    private int attackCooldown = -1;
    private int seeTime = 0;

    public UniversalRangedGoal(T mob, double speedMod, int attackInterval, float attackRadius) {
        this.mob = mob;
        this.speedMod = speedMod;
        this.attackInterval = attackInterval;
        this.attackRadius = attackRadius;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    private boolean isHoldingRangedWeapon() {
        return mob.isHolding(Items.BOW) || mob.isHolding(Items.CROSSBOW);
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive() && isHoldingRangedWeapon();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() || mob.isUsingItem();
    }

    @Override
    public void stop() {
        seeTime = 0;
        attackCooldown = -1;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        double distSqr = mob.distanceToSqr(target);
        boolean hasLos = mob.getSensing().hasLineOfSight(target);

        if (hasLos) seeTime = Math.min(seeTime + 1, 60);
        else         seeTime = Math.max(seeTime - 1, -60);

        if (distSqr > attackRadius * attackRadius) {
            if (mob.tickCount % 5 == 0) mob.getNavigation().moveTo(target, speedMod);
        } else {
            mob.getNavigation().stop();
        }
        mob.getLookControl().setLookAt(target, 30f, 30f);

        if (mob.isUsingItem()) {
            int ticks = mob.getTicksUsingItem();
            if (!hasLos && seeTime <= -60) {
                mob.stopUsingItem();
            } else if (ticks >= attackInterval) {
                mob.stopUsingItem();
                mob.performRangedAttack(target, BowItem.getPowerForTime(ticks));
                attackCooldown = attackInterval;
            }
        } else if (--attackCooldown <= 0 && hasLos && seeTime >= 0) {
            mob.startUsingItem(InteractionHand.MAIN_HAND);
        }
    }
}
