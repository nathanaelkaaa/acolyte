package net.raptorzizi.acolyte.entity.mobs.wizards.human;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IRecruitableCompanion {

    default int getRecruitCost() {
        return 1;
    }

    default long getContractDurationTicks() {
        return 24000L; // Valeur par défaut (1 jour Minecraft)
    }

    @Nullable
    UUID getOwnerUUID();

    void setOwnerUUID(@Nullable UUID uuid);

    default boolean isRecruited() {
        return getOwnerUUID() != null;
    }

    void onUnRecruit();

    default boolean isOwnedBy(Player player) {
        return player.getUUID().equals(getOwnerUUID());
    }

    // Timer / Contrat

    long getContractEndTime();

    void setContractEndTime(long time);

    default boolean hasActiveContract() {
        return getContractEndTime() > 0;
    }

    default long getRemainingContractTicks(Level level) {
        if (!hasActiveContract()) return 0;
        return Math.max(0, getContractEndTime() - level.getGameTime());
    }

    default float getContractProgress(Level level) {
        if (!hasActiveContract()) return 0f;
        return (float) getRemainingContractTicks(level) / (float) getContractDurationTicks();
    }

    default Component getDisplayName() {
        return asEntity().getDisplayName();
    }


    CompanionOrder getCurrentOrder();

    void setCurrentOrder(CompanionOrder order);

    default boolean isFollowing() {
        return getCurrentOrder() == CompanionOrder.FOLLOW;
    }

    default boolean isStationary() {
        return getCurrentOrder() == CompanionOrder.STAY;
    }

    enum CompanionOrder {
        FOLLOW,
        STAY
    }

    // Actions

    void openRecruitScreen(ServerPlayer player);

    default void recruit(Player player, Level level) {
        setOwnerUUID(player.getUUID());
        setContractEndTime(level.getGameTime() + getContractDurationTicks());
        setCurrentOrder(CompanionOrder.FOLLOW);
        onRecruited(player, asEntity().getDisplayName().getString());
    }

    default void onRecruited(Player player, String entityName) {
        player.sendSystemMessage(
                Component.translatable(
                        "gui.acolyte.recruit.hired",
                        player.getDisplayName(),
                        Component.literal(entityName)
                )
        );
    }

    default void tickContract(Level level) {
        if (!level.isClientSide() && hasActiveContract()) {
            if (getRemainingContractTicks(level) <= 0) {
                onContractExpired();
            }
        }
    }

    default void onContractExpired() {
        UUID ownerUUID = getOwnerUUID();
        if (ownerUUID != null) {
            Player owner = asEntity().level().getPlayerByUUID(ownerUUID);
            if (owner != null) {
                owner.sendSystemMessage(
                        Component.translatable("gui.acolyte.recruit.contract_expired",
                                asEntity().getDisplayName())
                );
            }
        }
        setOwnerUUID(null);
        setContractEndTime(-1);
        asEntity().discard();
    }

    // Sérialisation

    default void serializeCompanion(CompoundTag tag) {
        if (getOwnerUUID() != null) {
            tag.putUUID("OwnerUUID", getOwnerUUID());
        }
        tag.putLong("ContractEndTime", getContractEndTime());
        tag.putString("CompanionOrder", getCurrentOrder().name());
    }

    default void deserializeCompanion(CompoundTag tag) {
        if (tag.hasUUID("OwnerUUID")) {
            setOwnerUUID(tag.getUUID("OwnerUUID"));
        } else {
            setOwnerUUID(null);
        }
        setContractEndTime(tag.contains("ContractEndTime") ? tag.getLong("ContractEndTime") : -1L);
        if (tag.contains("CompanionOrder")) {
            try {
                setCurrentOrder(CompanionOrder.valueOf(tag.getString("CompanionOrder")));
            } catch (IllegalArgumentException e) {
                setCurrentOrder(CompanionOrder.FOLLOW);
            }
        }
    }

    default LivingEntity asEntity() {
        return (LivingEntity) this;
    }
}