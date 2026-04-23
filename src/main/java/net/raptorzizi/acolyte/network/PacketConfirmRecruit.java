package net.raptorzizi.acolyte.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.raptorzizi.acolyte.AcolyteMod;
import net.raptorzizi.acolyte.entity.mobs.wizards.human.HumanEntity;
import net.raptorzizi.acolyte.entity.mobs.wizards.human.IRecruitableCompanion;

public record PacketConfirmRecruit(
        int entityId,
        boolean orderOnly,
        boolean stayOrder,
        boolean unrecruitOnly
) implements CustomPacketPayload {

    public PacketConfirmRecruit(int entityId, boolean orderOnly, boolean stayOrder) {
        this(entityId, orderOnly, stayOrder, false);
    }

    public static PacketConfirmRecruit unrecruit(int entityId) {
        return new PacketConfirmRecruit(entityId, false, false, true);
    }

    public static final Type<PacketConfirmRecruit> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AcolyteMod.MOD_ID, "confirm_recruit")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketConfirmRecruit> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> {
                        buf.writeInt(pkt.entityId());
                        buf.writeBoolean(pkt.orderOnly());
                        buf.writeBoolean(pkt.stayOrder());
                        buf.writeBoolean(pkt.unrecruitOnly());
                    },
                    buf -> new PacketConfirmRecruit(
                            buf.readInt(),
                            buf.readBoolean(),
                            buf.readBoolean(),
                            buf.readBoolean()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PacketConfirmRecruit pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer serverPlayer)) return;
            ServerLevel level = serverPlayer.serverLevel();

            Entity entity = level.getEntity(pkt.entityId());
            if (!(entity instanceof HumanEntity human)) return;

            IRecruitableCompanion companion = human;

            // Unrecruit
            if (pkt.unrecruitOnly()) {
                if (!companion.isOwnedBy(serverPlayer)) return;
                companion.onUnRecruit();
                return;
            }

            // Recruitment
            if (companion.isRecruited()) {
                if (!companion.isOwnedBy(serverPlayer)) {
                    serverPlayer.sendSystemMessage(
                            Component.translatable("gui.acolyte.recruit.already_hired")
                    );
                }
                return;
            }

            int cost = companion.getRecruitCost();

            if (!hasEnoughEmeralds(serverPlayer, cost)) {
                serverPlayer.sendSystemMessage(
                        Component.translatable(
                                "gui.acolyte.recruit.not_enough_emeralds", cost)
                );
                return;
            }

            removeEmeralds(serverPlayer, cost);
            companion.recruit(serverPlayer, level);
            human.openRecruitScreen(serverPlayer);
        });
    }

    private static boolean hasEnoughEmeralds(ServerPlayer player, int amount) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(Items.EMERALD)) {
                count += stack.getCount();
                if (count >= amount) return true;
            }
        }
        return false;
    }

    private static void removeEmeralds(ServerPlayer player, int amount) {
        int remaining = amount;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (remaining <= 0) break;
            if (stack.is(Items.EMERALD)) {
                int toRemove = Math.min(remaining, stack.getCount());
                stack.shrink(toRemove);
                remaining -= toRemove;
            }
        }
    }
}