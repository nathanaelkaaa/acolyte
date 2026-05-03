package net.raptorzizi.acolyte.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.raptorzizi.acolyte.AcolyteMod;
import net.raptorzizi.acolyte.block.tavern_spawn_marker.TavernSpawnMarkerBlockEntity;
import net.raptorzizi.acolyte.block.demon_spawn_marker.DemonSpawnMarkerBlockEntity;

public class ModBlockEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AcolyteMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TavernSpawnMarkerBlockEntity>> TAVERN_SPAWN_MARKER_BE =
            BLOCK_ENTITIES.register("tavern_spawn_marker_be", () ->
                    BlockEntityType.Builder.of(
                            TavernSpawnMarkerBlockEntity::new,
                            ModBlocksRegistry.TAVERN_SPAWN_MARKER_OAK.get(),
                            ModBlocksRegistry.TAVERN_SPAWN_MARKER_COBBLESTONE.get(),
                            ModBlocksRegistry.TAVERN_SPAWN_MARKER_ACACIA.get(),
                            ModBlocksRegistry.TAVERN_SPAWN_MARKER_SPRUCE.get(),
                            ModBlocksRegistry.TAVERN_SPAWN_MARKER_SANDSTONE.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DemonSpawnMarkerBlockEntity>> DEMON_SPAWN_MARKER_BE =
            BLOCK_ENTITIES.register("demon_spawn_marker_be", () ->
                    BlockEntityType.Builder.of(
                            DemonSpawnMarkerBlockEntity::new,
                            ModBlocksRegistry.DEMON_SPAWN_MARKER_OAK.get(),
                            ModBlocksRegistry.DEMON_SPAWN_MARKER_SPRUCE.get(),
                            ModBlocksRegistry.DEMON_SPAWN_MARKER_BIRCH.get(),
                            ModBlocksRegistry.DEMON_SPAWN_MARKER_DARK_OAK.get(),
                            ModBlocksRegistry.DEMON_SPAWN_MARKER_ACACIA.get(),
                            ModBlocksRegistry.DEMON_SPAWN_MARKER_JUNGLE.get(),
                            ModBlocksRegistry.DEMON_SPAWN_MARKER_MANGROVE.get(),
                            ModBlocksRegistry.DEMON_SPAWN_MARKER_COBBLESTONE.get(),
                            ModBlocksRegistry.DEMON_SPAWN_MARKER_STONE_BRICKS.get()
                    ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}