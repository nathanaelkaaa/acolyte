package net.raptorzizi.acolyte;

import net.minecraft.resources.ResourceLocation;
import net.raptorzizi.acolyte.config.ModClientConfigs;
import net.raptorzizi.acolyte.config.ModCommonConfigs;
import net.raptorzizi.acolyte.config.ModServerConfigs;
import net.raptorzizi.acolyte.entity.mobs.wizards.archetype.ArchetypeLoader;
import net.raptorzizi.acolyte.network.NetworkHandler;
import net.raptorzizi.acolyte.registries.*;
import net.raptorzizi.acolyte.setup.ModGameRules;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(AcolyteMod.MOD_ID)
public class AcolyteMod {
    public static final String MOD_ID = "acolyte";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AcolyteMod(IEventBus modEventBus, ModContainer modContainer) {
        ModGameRules.init();
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(ArchetypeLoader.class);

        ModCreativeModeTabs.register(modEventBus);
        ModEntityRegistry.register(modEventBus);
        ModArmorMaterialRegistry.register(modEventBus);
        ModItemsRegistry.register(modEventBus);
        ModBlocksRegistry.register(modEventBus);
        ModBlockEntityRegistry.register(modEventBus);
        ModMenuRegistry.register(modEventBus);

        modEventBus.addListener(NetworkHandler::onRegisterPayloads);

        modContainer.registerConfig(ModConfig.Type.COMMON, ModCommonConfigs.SPEC, String.format("%s-common.toml", AcolyteMod.MOD_ID));
        modContainer.registerConfig(ModConfig.Type.SERVER, ModServerConfigs.SPEC, String.format("%s-server.toml", AcolyteMod.MOD_ID));
        modContainer.registerConfig(ModConfig.Type.CLIENT, ModClientConfigs.SPEC, String.format("%s-client.toml", AcolyteMod.MOD_ID));
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    public static ResourceLocation id(@NotNull String path) {
        return ResourceLocation.fromNamespaceAndPath(AcolyteMod.MOD_ID, path);
    }

}
