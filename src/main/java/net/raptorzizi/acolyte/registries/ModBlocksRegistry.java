package net.raptorzizi.acolyte.registries;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.raptorzizi.acolyte.AcolyteMod;
import net.raptorzizi.acolyte.block.tavern_spawn_marker.TavernSpawnMarkerBlock;

import java.util.function.Supplier;

public class ModBlocksRegistry {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(AcolyteMod.MOD_ID);

    public static final DeferredBlock<Block> TAVERN_SPAWN_MARKER_OAK = registerBlock(
            "tavern_spawn_marker_oak",
            () -> new TavernSpawnMarkerBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
            )
    );

    public static final DeferredBlock<Block> TAVERN_SPAWN_MARKER_ACACIA = registerBlock(
            "tavern_spawn_marker_acacia",
            () -> new TavernSpawnMarkerBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_PLANKS)
            )
    );

    public static final DeferredBlock<Block> TAVERN_SPAWN_MARKER_SPRUCE = registerBlock(
            "tavern_spawn_marker_spruce",
            () -> new TavernSpawnMarkerBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS)
            )
    );

    public static final DeferredBlock<Block> TAVERN_SPAWN_MARKER_COBBLESTONE = registerBlock(
            "tavern_spawn_marker_cobblestone",
            () -> new TavernSpawnMarkerBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE)
            )
    );

    public static final DeferredBlock<Block> TAVERN_SPAWN_MARKER_SANDSTONE = registerBlock(
            "tavern_spawn_marker_sandstone",
            () -> new TavernSpawnMarkerBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_SANDSTONE)
            )
    );

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name,toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItemsRegistry.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}