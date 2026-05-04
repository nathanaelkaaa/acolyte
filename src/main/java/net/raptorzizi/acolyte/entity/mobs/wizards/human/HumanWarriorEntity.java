package net.raptorzizi.acolyte.entity.mobs.wizards.human;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.NotIdioticNavigation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.raptorzizi.acolyte.AcolyteMod;
import net.raptorzizi.acolyte.entity.goals.SingleUseSpellGoal;
import net.raptorzizi.acolyte.entity.goals.WarriorMeleeGoal;
import software.bernie.geckolib.animation.*;

public class HumanWarriorEntity extends HumanEntity implements IAnimatedAttacker {

    RawAnimation animationToPlay = null;
    private final AnimationController<HumanWarriorEntity> meleeController = new AnimationController(this, "keeper_animations", 0, this::predicate);

    public HumanWarriorEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.lookControl = this.createLookControl();
        this.moveControl = this.createMoveControl();
    }

    @Override
    protected String getArchetypeName() {
        return "human/warrior";
    }

    protected LookControl createLookControl() {
        return new LookControl(this) {
            protected float rotateTowards(float pFrom, float pTo, float pMaxDelta) {
                return super.rotateTowards(pFrom, pTo, pMaxDelta * 2.5F);
            }

            protected boolean resetXRotOnTick() {
                return HumanWarriorEntity.this.getTarget() == null;
            }
        };
    }

    protected MoveControl createMoveControl() {
        return new MoveControl(this) {
            @Override
            public void strafe(float pForwards, float pStrafe) {
                super.strafe(pForwards, 0f);
            }

            protected float rotlerp(float pSourceAngle, float pTargetAngle, float pMaximumChange) {
                double d0 = this.wantedX - this.mob.getX();
                double d1 = this.wantedZ - this.mob.getZ();
                return d0 * d0 + d1 * d1 < (double)0.5F ? pSourceAngle : super.rotlerp(pSourceAngle, pTargetAngle, pMaximumChange * 0.25F);
            }
        };
    }

    @Override
    protected void registerArchetypeGoals() {
        if (selectedProfile == null) return;

        if (selectedProfile.barrageSpell != null) {
            int bMin = selectedProfile.barrageAttackInterval;
            int bMax = bMin + 20;
            this.goalSelector.addGoal(1, new SpellBarrageGoal(this,
                    selectedProfile.barrageSpell, 1, 1,
                    bMin, bMax,
                    selectedProfile.barrageProjectileCount));
        }

        if (selectedProfile.singleUseSpell != null) {
            int dMin = selectedProfile.singleUseSpellDelay;
            int dMax = dMin + 20;
            this.goalSelector.addGoal(2, new SingleUseSpellGoal(this,
                    selectedProfile.singleUseSpell, 1, 1, dMin, dMax));
        }

        int aMin = selectedProfile.attackInterval / 2;
        int aMax = aMin + 10;
        WarriorMeleeGoal meleeGoal = new WarriorMeleeGoal(this, 1.0f, aMin, aMax);
        meleeGoal.setComboChance(.3f);
        meleeGoal.setMeleeAttackInverval(aMin, aMax);
        meleeGoal.setMeleeMovespeedModifier(1.3f);

        this.goalSelector.addGoal(3, meleeGoal);
    }

    public static AttributeSupplier.Builder prepareAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0)
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.ENTITY_INTERACTION_RANGE, 3.5)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    // Animation

    public void playAnimation(String animationId) {
        try {
            this.animationToPlay = RawAnimation.begin().thenPlay(animationId);
        } catch (Exception var3) {
            IronsSpellbooks.LOGGER.error("Entity {} Failed to play animation: {}", this, animationId);
        }
    }

    private PlayState predicate(AnimationState<HumanWarriorEntity> state) {
        var controller = state.getController();
        if (animationToPlay != null) {
            controller.forceAnimationReset();
            controller.setAnimation(animationToPlay);
            animationToPlay = null;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(meleeController);
        super.registerControllers(registrar);
    }

    @Override
    public boolean isAnimating() {
        return meleeController.getAnimationState() != AnimationController.State.STOPPED
                || super.isAnimating();
    }

    protected PathNavigation createNavigation(Level pLevel) {
        return new NotIdioticNavigation(this, pLevel);
    }

    // Sons

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.EVOKER_AMBIENT; }
    @Override
    protected SoundEvent getHurtSound(DamageSource pSource) { return SoundEvents.EVOKER_HURT; }
    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.EVOKER_DEATH; }

    @Override
    protected boolean shouldDespawnInPeaceful() { return true; }
}