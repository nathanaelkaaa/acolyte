package net.raptorzizi.acolyte.entity.mobs.wizards.human;

import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.raptorzizi.acolyte.entity.goals.SingleUseSpellGoal;
import software.bernie.geckolib.animation.*;

public class HumanArcherEntity extends HumanEntity implements RangedAttackMob {

    private int attackAnimationTick = 0;

    private static final EntityDataAccessor<Boolean> IS_CHARGING_BOW =
            SynchedEntityData.defineId(HumanArcherEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_RELEASING_BOW =
            SynchedEntityData.defineId(HumanArcherEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimationController<HumanArcherEntity> bowController =
            new AnimationController<>(this, "bow_controller", 2, this::bowPredicate);

    public HumanArcherEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(IS_CHARGING_BOW, false);
        pBuilder.define(IS_RELEASING_BOW, false);
    }

    @Override
    protected String getArchetypeName() {
        return "archer";
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

        int aMin = selectedProfile.attackInterval;
        int aMax = aMin + 20;
        this.goalSelector.addGoal(3, new RangedBowAttackGoal<>(this, 1.0, aMin, 15.0f));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
        if (selectedProfile == null || selectedProfile.mainhand == null) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
            this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        }
        super.populateDefaultEquipmentSlots(pRandom, pDifficulty);
    }

    private PlayState bowPredicate(AnimationState<HumanArcherEntity> state) {
        boolean releasing = this.entityData.get(IS_RELEASING_BOW);
        boolean charging  = this.entityData.get(IS_CHARGING_BOW);

        if (releasing) {
            SpellAnimations.ANIMATION_INSTANT_CAST.getForMob().ifPresent(anim -> {
                state.getController().forceAnimationReset();
                state.getController().setAnimation(anim);
            });
            return PlayState.CONTINUE;
        }
        if (charging) {
            SpellAnimations.BOW_CHARGE_ANIMATION.getForMob().ifPresent(
                    anim -> state.getController().setAnimation(anim)
            );
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        ItemStack arrow = new ItemStack(Items.ARROW);
        AbstractArrow arrowEntity = ProjectileUtil.getMobArrow(this, arrow, power, null);

        double dx = target.getX() - this.getX();
        double dy = target.getY(0.3333) - arrowEntity.getY();
        double dz = target.getZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);

        arrowEntity.shoot(dx, dy + dist * 0.2, dz, 1.6f, 14 - this.level().getDifficulty().getId() * 4f);
        this.level().addFreshEntity(arrowEntity);
        this.playSound(SoundEvents.ARROW_SHOOT, 1.0f,
                1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));

        this.entityData.set(IS_RELEASING_BOW, true);
        this.entityData.set(IS_CHARGING_BOW, false);
        this.attackAnimationTick = 15;
    }

    public static AttributeSupplier.Builder prepareAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0)
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            LivingEntity target = this.getTarget();

            if (attackAnimationTick > 0) {
                attackAnimationTick--;
                if (attackAnimationTick == 0) {
                    this.entityData.set(IS_RELEASING_BOW, false);
                    this.stopUsingItem();
                }
            } else if (target != null) {
                this.entityData.set(IS_CHARGING_BOW, true);
                if (!this.isUsingItem()) {
                    this.startUsingItem(
                            this.getItemInHand(InteractionHand.MAIN_HAND).is(Items.BOW)
                                    ? InteractionHand.MAIN_HAND
                                    : InteractionHand.OFF_HAND
                    );
                }
            } else {
                this.entityData.set(IS_CHARGING_BOW, false);
                this.entityData.set(IS_RELEASING_BOW, false);
                this.stopUsingItem();
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(bowController);
        super.registerControllers(registrar);
    }

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem weapon) {
        return weapon instanceof BowItem;
    }

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.EVOKER_AMBIENT; }
    @Override
    protected SoundEvent getHurtSound(DamageSource pSource) { return SoundEvents.EVOKER_HURT; }
    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.EVOKER_DEATH; }
    @Override
    protected boolean shouldDespawnInPeaceful() { return true; }
}