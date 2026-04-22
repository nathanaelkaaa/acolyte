package net.raptorzizi.acolyte.entity.goals;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.PathType;

import java.util.EnumSet;
import java.util.function.Supplier;

public class GenericStayGoal extends Goal {

    private final PathfinderMob mob;
    private final Supplier<Entity> ownerGetter;
    private final PathNavigation navigation;
    private float oldWaterCost;

    public GenericStayGoal(PathfinderMob mob, Supplier<Entity> ownerGetter) {
        this.mob         = mob;
        this.ownerGetter = ownerGetter;
        this.navigation  = mob.getNavigation();
        this.setFlags(EnumSet.noneOf(Flag.class));
    }

    @Override
    public boolean canUse() {
        return ownerGetter.get() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return ownerGetter.get() != null;
    }

    @Override
    public void start() {
        this.oldWaterCost = mob.getPathfindingMalus(PathType.WATER);
        mob.setPathfindingMalus(PathType.WATER, 0.0F);
        this.navigation.stop();
    }

    @Override
    public void stop() {
        mob.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        if (!this.navigation.isDone()) {
            this.navigation.stop();
        }
    }
}