package net.raptorzizi.acolyte.entity.mobs.wizards.human;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.*;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardRecoverGoal;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.NotIdioticNavigation;
import io.redspace.ironsspellbooks.entity.mobs.wizards.priest.PriestEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.raptorzizi.acolyte.AcolyteMod;
import net.raptorzizi.acolyte.entity.goals.GenericStayOrderGoal;
import net.raptorzizi.acolyte.entity.mobs.wizards.archetype.ArchetypeLoader;
import net.raptorzizi.acolyte.entity.mobs.wizards.archetype.ArchetypeProfile;
import net.raptorzizi.acolyte.entity.mobs.wizards.archetype.ArchetypeUtils;
import net.raptorzizi.acolyte.gui.RecruitMenu;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

import static net.raptorzizi.acolyte.util.ModUtils.resolveAnimFile;
import static net.raptorzizi.acolyte.util.ModUtils.resolveBiomeFolder;

public abstract class HumanEntity extends AbstractSpellCastingMob implements IRecruitableCompanion,NeutralMob {

    @Nullable private UUID ownerUUID;
    public static final ResourceLocation FALLBACK_TEXTURE = ResourceLocation.fromNamespaceAndPath(AcolyteMod.MOD_ID, "textures/entity/generic_skin/plains/human0.png");
    private static final EntityDataAccessor<String> BIOME_FOLDER = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> SKIN_VARIANT = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> CUSTOM_SKIN = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.STRING);
    public ResourceLocation currentAnimFile = AbstractSpellCastingMob.animationInstantCast;
    private AbstractSpell lastInitiatedSpell = null;
    private int animResetDelay = 0;
    private static final String PREFIX = "human";
    private static final int PERSISTENT_ANGER_TIME_MIN = 200;
    private static final int PERSISTENT_ANGER_TIME_MAX = 400;
    private boolean orderedToStay = false;

    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME =
            SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);

    @Nullable
    private UUID persistentAngerTarget;

    @Nullable protected ArchetypeProfile selectedProfile;
    private final Map<Holder<Attribute>, Double> baseAttributeValues = new HashMap<>();
    private long contractEndTime = -1L;
    private final Supplier<Entity> ownerSupplier = () -> ownerUUID != null ? this.level().getPlayerByUUID(ownerUUID) : null;

    public HumanEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(SKIN_VARIANT, 0);
        pBuilder.define(CUSTOM_SKIN, "");
        pBuilder.define(BIOME_FOLDER, "plains");
        pBuilder.define(DATA_REMAINING_ANGER_TIME, 0);
    }

    private void syncProfileToClient() {
        ArchetypeUtils.syncProfileToClient(this.entityData, CUSTOM_SKIN, selectedProfile);
    }

    public int getSkinVariant()       {
        return this.entityData.get(SKIN_VARIANT);
    }
    public void setSkinVariant(int v) {
        this.entityData.set(SKIN_VARIANT, v);
    }
    protected int getSkinCount()      { return 3; }
    protected abstract String getArchetypeName();

    // Goals

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(2, new GenericStayOrderGoal(this, ownerSupplier));

        if (selectedProfile != null) {
            registerArchetypeGoals();
        }

        this.goalSelector.addGoal(6, new GenericFollowOwnerGoal(this, this::getOwner, (double)1.4F, 10.0F, 3.0F, false, 20.0F));
        this.goalSelector.addGoal(7, new PatrolNearLocationGoal(this, 30, .75f));
        this.goalSelector.addGoal(8, new WizardRecoverGoal(this));
        this.goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new GenericOwnerHurtByTargetGoal(this, ownerSupplier));
        this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, ownerSupplier));
        this.targetSelector.addGoal(3, new GenericCopyOwnerTargetGoal(this, ownerSupplier));
        this.targetSelector.addGoal(4, new GenericHurtByTargetGoal(this, entity -> isAlliedTo(entity)).setAlertOthers());
        this.targetSelector.addGoal(5, new GenericProtectOwnerTargetGoal(this, ownerSupplier));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false,
                (mob) -> !this.isRecruited()
                        && mob instanceof Enemy
                        && !(mob instanceof Creeper)));

    }

    protected abstract void registerArchetypeGoals();

    // Spawn

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
                                        MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
        captureBaseAttributes();
        RandomSource random = Utils.random;
        this.setSkinVariant(random.nextInt(getSkinCount()));
        this.selectedProfile = ArchetypeLoader.INSTANCE.rollProfile(
                getArchetypeName(), new Random(random.nextLong()), pLevel, this.blockPosition()
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
        applySlot(EquipmentSlot.HEAD,     selectedProfile.head);
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

    // Getter / Setter

    @Override public @Nullable UUID getOwnerUUID()           { return ownerUUID; }
    @Override public void setOwnerUUID(@Nullable UUID uuid)  { this.ownerUUID = uuid; }
    @Override public long getContractEndTime()               { return contractEndTime; }
    @Override public void setContractEndTime(long time)      { this.contractEndTime = time; }

    @Override
    public boolean isOrderedToStay() { return orderedToStay; }

    @Override
    public void setOrderedToStay(boolean stay) {
        this.orderedToStay = stay;
    }

    @Override
    public boolean isLeftHanded() {
        return false;
    }

    @Override
    public int getRecruitCost() {
        if (selectedProfile == null) return 1;

        return switch (selectedProfile.tier) {
            case 2 -> 7;
            case 3 -> 9;
            case 4 -> 11;
            default -> 5;
        };
    }

    @Override
    public long getContractDurationTicks() {
        if (selectedProfile == null) return 72000L;

        return switch (selectedProfile.tier) {
            case 2 -> 48000L; // 2 days
            case 3 -> 72000L; // 3 days
            case 4 -> 96000L; // 4 days
            default -> 24000L; // 1 days
        };
    }

    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, time);
    }

    public void startPersistentAngerTimer() {
        int time = PERSISTENT_ANGER_TIME_MIN +
                this.random.nextInt(PERSISTENT_ANGER_TIME_MAX - PERSISTENT_ANGER_TIME_MIN);
        this.setRemainingPersistentAngerTime(time);
    }

    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    // Interaction / AI

    @Override
    public void tick() {
        super.tick();
        tickContract(this.level());
        if (animResetDelay > 0 && --animResetDelay == 0) {
            currentAnimFile = AbstractSpellCastingMob.animationInstantCast;
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (isRecruited() && !isOwnedBy(player)) {
            return super.mobInteract(player, hand);
        }
        if (!this.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            openRecruitScreen(serverPlayer);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        NotIdioticNavigation nav = new NotIdioticNavigation(this, pLevel);
        nav.setCanOpenDoors(true);
        return nav;
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (super.isAlliedTo(entity)) return true;
        if (!this.isRecruited()) return false;
        if (entity instanceof Player player && this.ownerUUID != null
                && this.ownerUUID.equals(player.getUUID())) return true;
        if (entity instanceof HumanEntity other && other.isRecruited()) {
            return other.ownerUUID != null && other.ownerUUID.equals(this.ownerUUID);
        }
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity direct = source.getDirectEntity();
        Entity indirect = source.getEntity();
        if (indirect instanceof HumanEntity) return false;
        if (direct instanceof HumanEntity) return false;
        if (indirect instanceof PriestEntity) return false;
        if (direct instanceof PriestEntity) return false;

        if (indirect != null && isAlliedTo(indirect)) return false;
        if (direct != null && direct != indirect && isAlliedTo(direct)) return false;
        return super.hurt(source, amount);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target instanceof HumanEntity) return;
        if (target instanceof PriestEntity) return;
        if (target != null && isAlliedTo(target)) return;
        super.setTarget(target);
    }

    //Recruitment

    @Override
    public void openRecruitScreen(ServerPlayer player) {
        boolean isRecruited = this.isRecruited() && this.isOwnedBy(player);
        float progress = isRecruited ? this.getContractProgress(this.level()) : 0f;
        long totalDuration = this.getContractDurationTicks();
        long remaining = this.getRemainingContractTicks(this.level());
        int cost = this.getRecruitCost();
        float hp    = this.getHealth();
        float maxHp = (float) this.getAttributeValue(Attributes.MAX_HEALTH);
        float atk   = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float def   = (float) this.getAttributeValue(Attributes.ARMOR);
        List<ResourceLocation> spellIds = this.getAllProfileSpells().stream()
                .map(s -> s.getSpellResource()).toList();
        String name = this.getDisplayName().getString();
        int id = this.getId();
        int tier = (selectedProfile != null) ? selectedProfile.tier : 1;

        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal(name);
            }

            @Override
            public AbstractContainerMenu createMenu(
                    int containerId, Inventory inventory, Player p) {
                return new RecruitMenu(containerId, inventory,
                        id, hp, maxHp, atk, def, isRecruited, progress, name, spellIds, cost, remaining, totalDuration, tier);
            }
        }, buf -> {
            buf.writeInt(id);
            buf.writeFloat(hp);
            buf.writeFloat(maxHp);
            buf.writeFloat(atk);
            buf.writeFloat(def);
            buf.writeBoolean(isRecruited);
            buf.writeFloat(progress);
            buf.writeUtf(name);
            buf.writeInt(spellIds.size());
            for (ResourceLocation rid : spellIds) buf.writeResourceLocation(rid);
            buf.writeInt(cost);
            buf.writeLong(remaining);
            buf.writeLong(totalDuration);
            buf.writeInt(tier);
        });
    }

    @Override
    public void recruit(Player player, Level level) {
        IRecruitableCompanion.super.recruit(player, level);
        if (!level.isClientSide) {

            this.setOwnerUUID(player.getUUID());

            ((ServerLevel)level).sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    getX(), getY() + 1, getZ(), 10, 0.5, 0.5, 0.5, 0.05);
        }
    }

    @Override
    public void onUnRecruit() {
        if (!level().isClientSide) {
            MagicManager.spawnParticles(level(), ParticleTypes.CAMPFIRE_COSY_SMOKE, getX(), getY(), getZ(), 25, .4, .8, .4, .03, false);
            setOwnerUUID(null);
            setContractEndTime(-1L);
            setOrderedToStay(false);
        }
    }

    public void onContractExpired() {
        if (!level().isClientSide) {
            MagicManager.spawnParticles(level(), ParticleTypes.CAMPFIRE_COSY_SMOKE, getX(), getY(), getZ(), 25, .4, .8, .4, .03, false);
            setOwnerUUID(null);
            setContractEndTime(-1L);
            setOrderedToStay(false);
        }
    }


    @Override
    public void die(DamageSource pSource) {
        if (!level().isClientSide && isRecruited()) {
            Player owner = getOwner();
            if (owner instanceof ServerPlayer sp) {
                sp.sendSystemMessage(
                        Component.translatable("gui.acolyte.recruit.companion_died",
                                this.getDisplayName())
                );
            }
            setOwnerUUID(null);
            setContractEndTime(-1L);
        }
        super.die(pSource);
    }

    public List<AbstractSpell> getAllProfileSpells() {
        if (selectedProfile == null) return List.of();

        List<AbstractSpell> spells = new ArrayList<>();

        addSpellArray(spells, selectedProfile.attackSpells);
        addSpellArray(spells, selectedProfile.defenseSpells);
        addSpellArray(spells, selectedProfile.mobilitySpells);
        addSpellArray(spells, selectedProfile.utilitySpells);
        if (selectedProfile.barrageSpell   != null) spells.add(selectedProfile.barrageSpell);
        if (selectedProfile.singleUseSpell != null) spells.add(selectedProfile.singleUseSpell);

        return spells.stream().distinct().limit(8).toList();
    }

    private void addSpellArray(
            List<AbstractSpell> target,
            List<AbstractSpell> source) {
        if (source != null)
            for (var s : source) if (s != null) target.add(s);
    }

    // Sérialisation

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        serializeCompanion(pCompound);
        pCompound.putInt("SkinVariant", this.getSkinVariant());
        pCompound.putString("BiomeFolder", this.entityData.get(BIOME_FOLDER));
        this.addPersistentAngerSaveData(pCompound);
        pCompound.putBoolean("OrderedToStay", orderedToStay);
        if (this.selectedProfile != null && this.selectedProfile.profileId != null) {
            pCompound.putString("ArchetypeProfile", this.selectedProfile.profileId);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        deserializeCompanion(pCompound);
        this.setSkinVariant(pCompound.getInt("SkinVariant"));
        this.readPersistentAngerSaveData(this.level(), pCompound);
        if (pCompound.contains("BiomeFolder")) {
            this.entityData.set(BIOME_FOLDER, pCompound.getString("BiomeFolder"));
        }
        if (pCompound.contains("OrderedToStay")) {
            orderedToStay = pCompound.getBoolean("OrderedToStay");
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

    // Animation/Texture
    public ResourceLocation getTextureLocation() {
        return ArchetypeUtils.getTextureLocation(
                this.entityData, CUSTOM_SKIN, BIOME_FOLDER, SKIN_VARIANT, PREFIX, FALLBACK_TEXTURE
        );
    }

    @Override
    public void initiateCastSpell(AbstractSpell spell, int spellLevel) {
        currentAnimFile = resolveAnimFile(spell.getCastStartAnimation());
        lastInitiatedSpell = spell;
        super.initiateCastSpell(spell, spellLevel);
    }

    @Override
    public void castComplete() {
        if (lastInitiatedSpell != null && lastInitiatedSpell.getCastType() == CastType.LONG) {
            AnimationHolder finish = lastInitiatedSpell.getCastFinishAnimation();
            if (!finish.isPass) {
                currentAnimFile = resolveAnimFile(finish);
            }
        }
        animResetDelay = 20;
        super.castComplete();
    }
}