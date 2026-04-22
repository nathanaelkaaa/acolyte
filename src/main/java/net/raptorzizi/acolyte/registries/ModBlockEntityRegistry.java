package net.raptorzizi.acolyte.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.raptorzizi.acolyte.AcolyteMod;
import net.raptorzizi.acolyte.block.tavern_spawn_marker.TavernSpawnMarkerBlockEntity;

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

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}