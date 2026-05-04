package net.raptorzizi.acolyte.entity.goals;

import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.wizards.GenericAnimatedWarlockAttackGoal;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.AxeItem;

import java.util.List;

public class WarriorMeleeGoal<T extends AbstractSpellCastingMob & IAnimatedAttacker>
        extends GenericAnimatedWarlockAttackGoal<T> {

    public static final List<AttackAnimationData> SWORD_MOVESET = List.of(
            new AttackAnimationData(8,  "simple_sword_lunge_stab",              6),
            new AttackAnimationData(10, "simple_sword_stab_alternate",          8),
            new AttackAnimationData(10, "simple_sword_horizontal_cross_swipe",  8),
            new AttackAnimationData(20, "simple_sword_downstrike",             16)
    );

    public static final List<AttackAnimationData> AXE_MOVESET = List.of(
            new AttackAnimationData(20, "simple_sword_downstrike",             16),
            new AttackAnimationData(10, "simple_sword_horizontal_cross_swipe",  8),
            new AttackAnimationData(38, "sword_slash_stab",                   13, 33)
    );

    public WarriorMeleeGoal(T mob, float speedModifier, int minInterval, int maxInterval) {
        super(mob, speedModifier, minInterval, maxInterval);
    }

    @Override
    public void tick() {
        setMoveset(mob.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof AxeItem
                ? AXE_MOVESET : SWORD_MOVESET);
        super.tick();
        this.wantsToMelee = true;
    }

    @Override
    protected void doMovement(double distanceSquared) {
        if (target == null || target.isDeadOrDying()) {
            mob.getNavigation().stop();
            return;
        }
        mob.getLookControl().setLookAt(target);
        float meleeRange = meleeRange();
        if (distanceSquared > meleeRange * meleeRange) {
            if (mob.tickCount % 5 == 0) {
                mob.getNavigation().moveTo(target, meleeMoveSpeedModifier);
            }
        } else {
            mob.getNavigation().stop();
        }
    }

    @Override
    protected void doSpellAction() {
    }
}
