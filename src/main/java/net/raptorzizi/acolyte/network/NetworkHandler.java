package net.raptorzizi.acolyte.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToServer(
                PacketConfirmRecruit.TYPE,
                PacketConfirmRecruit.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        (pkt, ctx) -> {},
                        PacketConfirmRecruit::handle
                )
        );
    }

    public static <T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload>
    void sendToServer(T packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static <T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload>
    void sendToPlayer(ServerPlayer player, T packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }
}