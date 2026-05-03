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
import net.raptorzizi.acolyte.block.demon_spawn_marker.DemonSpawnMarkerBlock;

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

    public static final DeferredBlock<Block> DEMON_SPAWN_MARKER_OAK = registerBlock(
            "demon_spawn_marker_oak",
            () -> new DemonSpawnMarkerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
    );
    public static final DeferredBlock<Block> DEMON_SPAWN_MARKER_SPRUCE = registerBlock(
            "demon_spawn_marker_spruce",
            () -> new DemonSpawnMarkerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS))
    );
    public static final DeferredBlock<Block> DEMON_SPAWN_MARKER_BIRCH = registerBlock(
            "demon_spawn_marker_birch",
            () -> new DemonSpawnMarkerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BIRCH_PLANKS))
    );
    public static final DeferredBlock<Block> DEMON_SPAWN_MARKER_DARK_OAK = registerBlock(
            "demon_spawn_marker_dark_oak",
            () -> new DemonSpawnMarkerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_OAK_PLANKS))
    );
    public static final DeferredBlock<Block> DEMON_SPAWN_MARKER_ACACIA = registerBlock(
            "demon_spawn_marker_acacia",
            () -> new DemonSpawnMarkerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_PLANKS))
    );
    public static final DeferredBlock<Block> DEMON_SPAWN_MARKER_JUNGLE = registerBlock(
            "demon_spawn_marker_jungle",
            () -> new DemonSpawnMarkerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_PLANKS))
    );
    public static final DeferredBlock<Block> DEMON_SPAWN_MARKER_MANGROVE = registerBlock(
            "demon_spawn_marker_mangrove",
            () -> new DemonSpawnMarkerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MANGROVE_PLANKS))
    );
    public static final DeferredBlock<Block> DEMON_SPAWN_MARKER_COBBLESTONE = registerBlock(
            "demon_spawn_marker_cobblestone",
            () -> new DemonSpawnMarkerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE))
    );
    public static final DeferredBlock<Block> DEMON_SPAWN_MARKER_STONE_BRICKS = registerBlock(
            "demon_spawn_marker_stone_bricks",
            () -> new DemonSpawnMarkerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS))
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