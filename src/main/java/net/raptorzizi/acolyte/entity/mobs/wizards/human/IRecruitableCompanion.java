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

    @Nullable UUID getOwnerUUID();
    void setOwnerUUID(@Nullable UUID uuid);


    default boolean isRecruited() { return getOwnerUUID() != null; }

    default boolean isOwnedBy(Player player) {
        return player.getUUID().equals(getOwnerUUID());
    }

    @Nullable
    default Player getOwner() {
        UUID uuid = getOwnerUUID();
        if (uuid == null) return null;
        return asEntity().level().getPlayerByUUID(uuid);
    }

    boolean isOrderedToStay();
    void setOrderedToStay(boolean stay);

    long getContractEndTime();
    void setContractEndTime(long time);

    default int getRecruitCost() { return 1; }
    default long getContractDurationTicks() { return 24000L; }

    default boolean hasActiveContract() { return getContractEndTime() > 0; }

    default long getRemainingContractTicks(Level level) {
        if (!hasActiveContract()) return 0;
        return Math.max(0, getContractEndTime() - level.getGameTime());
    }

    default float getContractProgress(Level level) {
        if (!hasActiveContract()) return 0f;
        return (float) getRemainingContractTicks(level) / (float) getContractDurationTicks();
    }

    void onUnRecruit();

    default void onContractExpired() {
        Player owner = getOwner();
        if (owner != null) {
            owner.sendSystemMessage(
                    Component.translatable("gui.acolyte.recruit.contract_expired",
                            asEntity().getDisplayName())
            );
        }
        setOwnerUUID(null);
        setContractEndTime(-1L);
        asEntity().discard();
    }

    default void tickContract(Level level) {
        if (!level.isClientSide() && hasActiveContract()) {
            if (getRemainingContractTicks(level) <= 0) {
                onContractExpired();
            }
        }
    }

    void openRecruitScreen(ServerPlayer player);

    default void recruit(Player player, Level level) {
        setOwnerUUID(player.getUUID());
        setContractEndTime(level.getGameTime() + getContractDurationTicks());
        player.sendSystemMessage(
                Component.translatable(
                        "gui.acolyte.recruit.hired",
                        player.getDisplayName(),
                        asEntity().getDisplayName()
                )
        );
    }

    default void serializeCompanion(CompoundTag tag) {
        if (getOwnerUUID() != null) tag.putUUID("OwnerUUID", getOwnerUUID());
        tag.putLong("ContractEndTime", getContractEndTime());
    }

    default void deserializeCompanion(CompoundTag tag) {
        setOwnerUUID(tag.hasUUID("OwnerUUID") ? tag.getUUID("OwnerUUID") : null);
        setContractEndTime(tag.contains("ContractEndTime") ? tag.getLong("ContractEndTime") : -1L);
    }

    default LivingEntity asEntity() { return (LivingEntity) this; }
}