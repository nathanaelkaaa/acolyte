package net.raptorzizi.acolyte.entity.mobs.lieutenant;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.NotIdioticNavigation;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.raptorzizi.acolyte.AcolyteMod;
import net.raptorzizi.acolyte.entity.mobs.wizards.demon.DemonEntity;
import net.raptorzizi.acolyte.entity.mobs.wizards.demon.DemonWarriorEntity;
import net.raptorzizi.acolyte.registries.ModEntityRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;

public class LieutenantEntity extends AbstractSpellCastingMob implements Enemy, IAnimatedAttacker {

    public enum AttackType {
        Double_Slash(43, "sword_double_slash", 13, 29),
        Single_Upward(26, "sword_single_upward", 13),
        Single_Horizontal(28, "sword_single_horizontal", 12),
        Single_Horizontal_Fast(24, "sword_single_horizontal_fast", 12),
        Single_Stab(21, "sword_stab", 11),
        Lunge(76, "sword_lunge", 56, 57, 58, 59, 60, 61, 62, 63, 64);

        AttackType(int lengthInTicks, String animationId, int... attackTimestamps) {
            this.data = new AttackAnimationData(lengthInTicks, animationId, attackTimestamps);
        }

        public final AttackAnimationData data;
    }

    public boolean isMeleeing;

    private final List<UUID> summonedDemons = new ArrayList<>();
    private static final int MAX_SUMMONED_DEMONS = 2;
    private static final int SUMMON_CHECK_INTERVAL = 12 * 20;

    public LieutenantEntity(EntityType<? extends LieutenantEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        xpReward = 25;
        this.lookControl = createLookControl();
        this.moveControl = createMoveControl();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new LieutenantAnimatedWarlockAttackGoal(this, 1f, 10, 30));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, true, (entity) -> !(entity instanceof DemonEntity)));
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new BodyRotationControl(this);
    }

    protected LookControl createLookControl() {
        return new LookControl(this) {
            @Override
            protected float rotateTowards(float pFrom, float pTo, float pMaxDelta) {
                return super.rotateTowards(pFrom, pTo, pMaxDelta * 2.5f);
            }
        };
    }

    protected MoveControl createMoveControl() {
        return new MoveControl(this) {
            @Override
            protected float rotlerp(float pSourceAngle, float pTargetAngle, float pMaximumChange) {
                double d0 = this.wantedX - this.mob.getX();
                double d1 = this.wantedZ - this.mob.getZ();
                if (d0 * d0 + d1 * d1 < .5f) {
                    return pSourceAngle;
                } else {
                    return super.rotlerp(pSourceAngle, pTargetAngle, pMaximumChange * .25f);
                }
            }
        };
    }

    protected SoundEvent getAmbientSound() {
        return SoundRegistry.KEEPER_IDLE.get();
    }

    @Override
    public void playAmbientSound() {
        this.playSound(getAmbientSound(), 1, Mth.randomBetweenInclusive(getRandom(), 5, 10) * .1f);
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundRegistry.KEEPER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SoundRegistry.KEEPER_DEATH.get();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
        this.populateDefaultEquipmentSlots(Utils.random, pDifficulty);
        return pSpawnData;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemRegistry.KEEPER_FLAMBERGE));
    }

    public static AttributeSupplier.Builder prepareAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.MAX_HEALTH, 180.0)
                .add(Attributes.FOLLOW_RANGE, 25.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0)
                .add(Attributes.STEP_HEIGHT, 1)
                .add(Attributes.ENTITY_INTERACTION_RANGE, 3.5)
                .add(Attributes.MOVEMENT_SPEED, .19);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        Entity attacker = pSource.getEntity();
        if (attacker instanceof DemonEntity || attacker instanceof LieutenantEntity) return false;
        if (pSource.getDirectEntity() instanceof Projectile) {
            pAmount *= .75f;
        }
        if (tickCount < 10 && pSource.is(DamageTypes.IN_WALL)) {
            Utils.doMobBreakSuffocatingBlocks(this);
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(SoundRegistry.KEEPER_STEP.get(), .25f, 1f);
    }

    @Override
    protected float nextStep() {
        return moveDist + .8f;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        return super.isInvulnerableTo(pSource) || pSource.is(DamageTypeTags.IS_FALL);
    }

    private final AnimationController<LieutenantEntity> meleeController = new AnimationController<>(this, "lieutenant_animations", 0, this::predicate);
    RawAnimation animationToPlay = null;

    @Override
    public void playAnimation(String animationId) {
        try {
            var attackType = AttackType.valueOf(animationId);
            animationToPlay = RawAnimation.begin().thenPlay(attackType.data.animationId);
        } catch (Exception ignored) {
            AcolyteMod.LOGGER.error("Entity {} Failed to play animation: {}", this, animationId);
        }
    }

    private PlayState predicate(AnimationState<LieutenantEntity> animationEvent) {
        var controller = animationEvent.getController();
        if (this.animationToPlay != null) {
            controller.forceAnimationReset();
            controller.setAnimation(animationToPlay);
            animationToPlay = null;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(meleeController);
        super.registerControllers(controllerRegistrar);
    }

    @Override
    public boolean isAnimating() {
        return meleeController.getAnimationState() != AnimationController.State.STOPPED || super.isAnimating();
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new NotIdioticNavigation(this, pLevel);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.isAggressive() && this.tickCount % SUMMON_CHECK_INTERVAL == 0 && level() instanceof ServerLevel serverLevel) {
            long aliveCount = summonedDemons.stream()
                    .filter(uuid -> serverLevel.getEntity(uuid) instanceof DemonWarriorEntity d && d.isAlive())
                    .count();
            if (aliveCount < MAX_SUMMONED_DEMONS) {
                spawnDemonWarrior(this.random.nextBoolean());
            }
        }
    }

    public void spawnDemonWarrior(boolean left) {
        if (level() instanceof ServerLevel serverLevel) {
            DemonWarriorEntity warrior = new DemonWarriorEntity(ModEntityRegistry.DEMON_WARRIOR.get(), level());
            float angle = (left ? -90 : 90) * Mth.DEG_TO_RAD;
            Vec3 offset = this.getForward().multiply(3, 0, 3).yRot(angle);
            Vec3 spawn = Utils.moveToRelativeGroundLevel(level(), Utils.raycastForBlock(level(), this.getEyePosition(), this.position().add(offset), ClipContext.Fluid.NONE).getLocation(), 4);
            warrior.moveTo(spawn.add(0, 0.1, 0));
            warrior.setYRot(this.getYRot());
            warrior.finalizeSpawn(serverLevel, level().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
            level().addFreshEntity(warrior);
            summonedDemons.add(warrior.getUUID());
        }
    }

    @Override
    public void die(DamageSource pDamageSource) {
        super.die(pDamageSource);
        if (level() instanceof ServerLevel serverLevel) {
            summonedDemons.forEach(uuid -> {
                if (serverLevel.getEntity(uuid) instanceof DemonWarriorEntity warrior) warrior.kill();
            });
            summonedDemons.clear();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        ListTag list = new ListTag();
        for (UUID uuid : summonedDemons) {
            list.add(NbtUtils.createUUID(uuid));
        }
        pCompound.put("summonedDemons", list);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        summonedDemons.clear();
        ListTag list = pCompound.getList("summonedDemons", 11);
        for (int i = 0; i < list.size(); i++) {
            summonedDemons.add(NbtUtils.loadUUID(list.get(i)));
        }
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        return super.isAlliedTo(pEntity) || pEntity instanceof DemonEntity || pEntity instanceof LieutenantEntity || pEntity.getType().is(ModTags.INFERNAL_ALLIES);
    }
}
