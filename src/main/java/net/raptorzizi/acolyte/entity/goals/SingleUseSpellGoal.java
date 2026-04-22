package net.raptorzizi.acolyte.entity.goals;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SingleUseSpellGoal extends Goal {

    private final PathfinderMob mob;
    private final IMagicEntity spellCastingMob;
    private final AbstractSpell spell;
    private final int spellLevel;
    private final float attackRadius;
    private final float attackRadiusSqr;
    private int delay;
    private LivingEntity target;

    public SingleUseSpellGoal(IMagicEntity spellCastingMob, AbstractSpell spell, int minLevel, int maxLevel, int minDelay, int maxDelay) {
        this.setFlags(EnumSet.noneOf(Goal.Flag.class));
        if (spellCastingMob instanceof PathfinderMob m) {
            this.mob = m;
        } else {
            throw new IllegalStateException("SingleUseSpellGoal requires a PathfinderMob.");
        }
        this.spellCastingMob = spellCastingMob;
        this.spell = spell;
        this.spellLevel = Utils.random.nextIntBetweenInclusive(minLevel, maxLevel);
        this.delay = Utils.random.nextIntBetweenInclusive(minDelay, maxDelay);
        this.attackRadius = 20f;
        this.attackRadiusSqr = attackRadius * attackRadius;
    }

    @Override
    public boolean canUse() {
        if (spellCastingMob.getHasUsedSingleAttack()) return false;

        target = mob.getTarget();
        if (target == null || !target.isAlive()) return false;
        if (spellCastingMob.isCasting()) return false;

        if (--delay > 0) return false;

        return mob.distanceToSqr(target) <= attackRadiusSqr;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        spellCastingMob.setHasUsedSingleAttack(true);
        mob.getLookControl().setLookAt(target, 45, 45);
        spellCastingMob.initiateCastSpell(spell, spellLevel);
    }
}