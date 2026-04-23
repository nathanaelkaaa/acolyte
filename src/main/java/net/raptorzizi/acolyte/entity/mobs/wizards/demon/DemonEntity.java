package net.raptorzizi.acolyte.entity.mobs.wizards.demon;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.NeutralWizard;
import io.redspace.ironsspellbooks.entity.mobs.goals.*;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.raptorzizi.acolyte.AcolyteMod;
import net.raptorzizi.acolyte.entity.mobs.wizards.archetype.ArchetypeLoader;
import net.raptorzizi.acolyte.entity.mobs.wizards.archetype.ArchetypeProfile;
import net.raptorzizi.acolyte.entity.mobs.wizards.archetype.ArchetypeUtils;
import net.raptorzizi.acolyte.entity.mobs.wizards.human.HumanEntity;
import net.raptorzizi.acolyte.registries.ModItemsRegistry;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static net.raptorzizi.acolyte.util.ModUtils.resolveBiomeFolder;

public abstract class DemonEntity extends AbstractSpellCastingMob implements Enemy {


    public static final ResourceLocation FALLBACK_TEXTURE = ResourceLocation.fromNamespaceAndPath(AcolyteMod.MOD_ID, "textures/entity/generic_skin/plains/skin0.png");
    private static final EntityDataAccessor<String> BIOME_FOLDER = SynchedEntityData.defineId(DemonEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> SKIN_VARIANT = SynchedEntityData.defineId(DemonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> CUSTOM_SKIN = SynchedEntityData.defineId(DemonEntity.class, EntityDataSerializers.STRING);

    @Nullable
    protected ArchetypeProfile selectedProfile;
    private final Map<Holder<Attribute>, Double> baseAttributeValues = new HashMap<>();


    public DemonEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(SKIN_VARIANT, 0);
        pBuilder.define(CUSTOM_SKIN, "");
        pBuilder.define(BIOME_FOLDER, "plains");
    }

    private void syncProfileToClient() {
        ArchetypeUtils.syncProfileToClient(this.entityData, CUSTOM_SKIN, selectedProfile);
    }

    public int getSkinVariant() {
        return this.entityData.get(SKIN_VARIANT);
    }
    public void setSkinVariant(int v) {
        this.entityData.set(SKIN_VARIANT, v);
    }
    protected int getSkinCount() {
        return 3;
    }
    protected abstract String getArchetypeName();
    @Override
    public boolean isLeftHanded() {return false;}
    // Goals

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        if (selectedProfile != null) {
            registerArchetypeGoals();
        }

        this.goalSelector.addGoal(8, new PatrolNearLocationGoal(this, 30, .75f));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new WizardRecoverGoal(this));

        registerTargetGoals();
    }

    private void registerTargetGoals() {
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, HumanEntity.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, NeutralWizard.class, true));
        this.targetSelector.addGoal(5, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Villager.class, true));
    }

    protected abstract void registerArchetypeGoals();

    //Spawn

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
                                        MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
        captureBaseAttributes();
        RandomSource random = Utils.random;
        this.setSkinVariant(random.nextInt(getSkinCount()));
        this.selectedProfile = ArchetypeLoader.INSTANCE.rollProfile(
                getArchetypeName(), new Random(random.nextLong())
        );
        applyProfileStats();
        this.xpReward = selectedProfile != null ? selectedProfile.xpReward : 15;
        syncProfileToClient();
        this.goalSelector.removeAllGoals(x -> true);
        this.targetSelector.removeAllGoals(x -> true);
        registerGoals();
        this.populateDefaultEquipmentSlots(random, pDifficulty);

        if (selectedProfile != null && selectedProfile.customName != null) {
            this.setCustomName(Component.literal(selectedProfile.customName));
            this.setCustomNameVisible(true);
        } else {
            this.setCustomName(null);
            this.setCustomNameVisible(false);
        }

        String folder = resolveBiomeFolder(pLevel, this.blockPosition());
        this.entityData.set(BIOME_FOLDER, folder);

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
        if (selectedProfile == null) return;

        if (selectedProfile.head == null) {
            List<DeferredHolder<Item, Item>> horns = ModItemsRegistry.DEMON_HORNS;
            Item randomHorn = horns.get(pRandom.nextInt(horns.size())).get();
            applySlot(EquipmentSlot.HEAD, randomHorn);
        } else {
            applySlot(EquipmentSlot.HEAD, selectedProfile.head);
        }
        applySlot(EquipmentSlot.CHEST,    selectedProfile.chest);
        applySlot(EquipmentSlot.LEGS,     selectedProfile.legs);
        applySlot(EquipmentSlot.FEET,     selectedProfile.feet);
        if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            applySlot(EquipmentSlot.MAINHAND, selectedProfile.mainhand);
        }
    }

    protected void applySlot(EquipmentSlot slot, @Nullable Item item) {
        ArchetypeUtils.applySlot(this, slot, item);
    }

    private void captureBaseAttributes() {
        ArchetypeUtils.captureBaseAttributes(this, baseAttributeValues);
    }

    protected void applyProfileStats() {
        ArchetypeUtils.applyProfileStats(this, selectedProfile, baseAttributeValues);
    }

    // Sérialisation

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("SkinVariant", this.getSkinVariant());
        pCompound.putString("BiomeFolder", this.entityData.get(BIOME_FOLDER));
        if (this.selectedProfile != null && this.selectedProfile.profileId != null) {
            pCompound.putString("ArchetypeProfile", this.selectedProfile.profileId);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setSkinVariant(pCompound.getInt("SkinVariant"));
        if (pCompound.contains("BiomeFolder")) {
            this.entityData.set(BIOME_FOLDER, pCompound.getString("BiomeFolder"));
        }
        if (pCompound.contains("ArchetypeProfile")) {
            String profileId = pCompound.getString("ArchetypeProfile");
            this.selectedProfile = ArchetypeLoader.INSTANCE.getProfileById(getArchetypeName(), profileId);
            applyProfileStats();
            syncProfileToClient();
        }
        this.goalSelector.removeAllGoals(x -> true);
        this.targetSelector.removeAllGoals(x -> true);
        registerGoals();
    }

    public ResourceLocation getTextureLocation() {
        return ArchetypeUtils.getTextureLocation(
                this.entityData, CUSTOM_SKIN, BIOME_FOLDER, SKIN_VARIANT, FALLBACK_TEXTURE
        );
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pSource) { return SoundEvents.VILLAGER_HURT; }
}