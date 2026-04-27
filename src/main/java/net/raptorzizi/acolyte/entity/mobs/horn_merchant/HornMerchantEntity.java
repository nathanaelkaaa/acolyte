package net.raptorzizi.acolyte.entity.mobs.horn_merchant;

import io.redspace.ironsspellbooks.loot.SpellFilter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.raptorzizi.acolyte.player.AdditionalWanderingHornTrades;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class HornMerchantEntity extends AbstractVillager implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_ANIM  = RawAnimation.begin().thenLoop("animation.horn_merchant.idle");
    private static final RawAnimation WALK_ANIM  = RawAnimation.begin().thenLoop("animation.horn_merchant.walk");

    public HornMerchantEntity(EntityType<? extends HornMerchantEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder prepareAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal(this, Zombie.class, 8.0F, (double)0.5F, (double)0.5F));
        this.goalSelector.addGoal(1, new AvoidEntityGoal(this, Evoker.class, 12.0F, (double)0.5F, (double)0.5F));
        this.goalSelector.addGoal(1, new AvoidEntityGoal(this, Vindicator.class, 8.0F, (double)0.5F, (double)0.5F));
        this.goalSelector.addGoal(1, new AvoidEntityGoal(this, Vex.class, 8.0F, (double)0.5F, (double)0.5F));
        this.goalSelector.addGoal(1, new AvoidEntityGoal(this, Pillager.class, 15.0F, (double)0.5F, (double)0.5F));
        this.goalSelector.addGoal(1, new AvoidEntityGoal(this, Illusioner.class, 12.0F, (double)0.5F, (double)0.5F));
        this.goalSelector.addGoal(1, new AvoidEntityGoal(this, Zoglin.class, 10.0F, (double)0.5F, (double)0.5F));
        this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, (double)0.5F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.35));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    protected void updateTrades() {
        MerchantOffers offers = this.getOffers();
        RandomSource random = this.random;

        // Misc
        List<AdditionalWanderingHornTrades.MiscSellTrade.MiscEntry> miscPool = new ArrayList<>(AdditionalWanderingHornTrades.MiscSellTrade.ENTRIES);
        Collections.shuffle(miscPool);
        for (int i = 0; i < 2; i++) {
            offers.add(AdditionalWanderingHornTrades.MiscSellTrade.createOffer(miscPool.get(i)));
        }

        // Focus
        MerchantOffer focusOffer = new AdditionalWanderingHornTrades.FocusSellTrade().getOffer(this, random);
        if (focusOffer != null) {
            offers.add(focusOffer);
        }

        // Potions / Elixirs
        List<VillagerTrades.ItemListing> potionsPool = new ArrayList<>(List.of(
                new AdditionalWanderingHornTrades.PotionSellTrade(null),
                new AdditionalWanderingHornTrades.ExilirSellTrade(false,false)
        ));

        Collections.shuffle(potionsPool);
        potionsPool.stream().limit(1).forEach(trade -> {
            MerchantOffer offer = trade.getOffer(this, random);
            if (offer != null) offers.add(offer);
        });

        // Inks
        MerchantOffer inkOffer = new AdditionalWanderingHornTrades.RandomInkSellTrade().getOffer(this, random);
        if (inkOffer != null) offers.add(inkOffer);

        // Scrolls
        offers.add(new AdditionalWanderingHornTrades.RandomScrollTrade(new SpellFilter(), 0f, 0.25f).getOffer(this, random));
        if (random.nextFloat() < 0.7f) {
            offers.add(new AdditionalWanderingHornTrades.RandomScrollTrade(new SpellFilter(), 0.3f, 0.8f).getOffer(this, random));
        }

        // Curios / Weapons / Rune
        List<VillagerTrades.ItemListing> rarePool = new ArrayList<>(List.of(
                new AdditionalWanderingHornTrades.RandomCurioTrade(),
                new AdditionalWanderingHornTrades.WeaponsSellTrade(),
                new AdditionalWanderingHornTrades.RuneSellTrade(),
                new AdditionalWanderingHornTrades.RandomInkSellTrade(true)
        ));

        Collections.shuffle(rarePool);

        for (int i = 0; i < 1; i++) {
            MerchantOffer offer = rarePool.get(i).getOffer(this, random);
            if (offer != null) offers.add(offer);
        }

        offers.removeIf(Objects::isNull);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide && this.isAlive() && !this.isTrading() && !this.isBaby()) {
            this.setTradingPlayer(player);
            this.openTradingScreen(player, this.getDisplayName(), 1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        this.rewardTradeXp(offer);
        if (this.getTradingPlayer() != null) {
            ExperienceOrb.award((ServerLevel) this.level(),
                    this.getTradingPlayer().position(),
                    offer.getXp());
        }
    }

    @Override
    protected void rewardTradeXp(MerchantOffer merchantOffer) {
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {
    }

    @Override
    public SoundEvent getAmbientSound() { return SoundEvents.VILLAGER_AMBIENT; }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    public SoundEvent getDeathSound() { return SoundEvents.VILLAGER_DEATH; }

    @Override
    protected SoundEvent getTradeUpdatedSound(boolean yes) {
        return yes ? SoundEvents.VILLAGER_YES : SoundEvents.VILLAGER_NO;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.VILLAGER_YES;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "movement", 5, state -> {
            if (state.isMoving()) {
                return state.setAndContinue(WALK_ANIM);
            }
            return state.setAndContinue(IDLE_ANIM);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public int getVillagerXp() { return 0; }

    @Override
    public boolean showProgressBar() { return false; }

    @Nullable
    @Override
    public AbstractVillager getBreedOffspring(ServerLevel level, AgeableMob mate) {
        return null;
    }

    //Despawn

    private int despawnDelay = 0;

    public void setDespawnDelay(int delay) {
        this.despawnDelay = delay;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.maybeDespawn();
        }
    }

    private void maybeDespawn() {
        if (this.despawnDelay > 0 && --this.despawnDelay == 0) {
            this.discard();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("DespawnDelay", this.despawnDelay);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("DespawnDelay", 99)) {
            this.despawnDelay = compound.getInt("DespawnDelay");
        }
    }
}