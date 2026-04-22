package net.raptorzizi.acolyte.registries;

import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.raptorzizi.acolyte.AcolyteMod;
import net.raptorzizi.acolyte.entity.mobs.wizards.demon.DemonSpawner;
import net.raptorzizi.acolyte.entity.mobs.wizards.human.HumanSpawner;
import net.raptorzizi.acolyte.item.armor.DemonHornItem;

import java.util.List;
import java.util.function.Supplier;

public class ModItemsRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AcolyteMod.MOD_ID);

    /**
     * Armor
     */

    public static final List<DeferredHolder<Item, Item>> DEMON_HORNS = List.of(
            ITEMS.register("demon_horn_0", () -> new DemonHornItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON), "demon_horn_0", "demon_horn_0")),
            ITEMS.register("demon_horn_1", () -> new DemonHornItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON), "demon_horn_1", "demon_horn_1")),
            ITEMS.register("demon_horn_2", () -> new DemonHornItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON), "demon_horn_2", "demon_horn_2")),
            ITEMS.register("demon_horn_3", () -> new DemonHornItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON), "demon_horn_3", "demon_horn_3"))
    );

    /**
     * Spawn eggs
     */

    public static final Supplier<DeferredSpawnEggItem> DEMON_MAGE_SPAWN_EGG =  ITEMS.register("demon_mage_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntityRegistry.DEMON_MAGE, 0x9b948c, 0x565351, ItemPropertiesHelper.material().stacksTo(64)));
    public static final Supplier<DeferredSpawnEggItem> DEMON_WARRIOR_SPAWN_EGG =  ITEMS.register("demon_warrior_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntityRegistry.DEMON_WARRIOR, 0x9b948c, 0x565351, ItemPropertiesHelper.material().stacksTo(64)));
    public static final Supplier<DeferredSpawnEggItem> DEMON_ARCHER_SPAWN_EGG =  ITEMS.register("demon_archer_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntityRegistry.DEMON_ARCHER, 0x9b948c, 0x565351, ItemPropertiesHelper.material().stacksTo(64)));

    public static final DeferredItem<Item> DEMON_SPAWN_EGG = ITEMS.register("demon_spawn_egg",
            () -> new Item(new Item.Properties()) {
                @Override
                public InteractionResult useOn(UseOnContext context) {
                    if (context.getLevel() instanceof ServerLevel serverLevel) {
                        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
                        DemonSpawner.spawnRandom(serverLevel, pos);
                        if (!context.getPlayer().isCreative()) {
                            context.getItemInHand().shrink(1);
                        }
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
                }
            }
    );

    public static final Supplier<DeferredSpawnEggItem> HUMAN_MAGE_SPAWN_EGG =  ITEMS.register("human_mage_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntityRegistry.HUMAN_MAGE, 0xdbac8a, 0x565351, ItemPropertiesHelper.material().stacksTo(64)));
    public static final Supplier<DeferredSpawnEggItem> HUMAN_WARRIOR_SPAWN_EGG =  ITEMS.register("human_warrior_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntityRegistry.HUMAN_WARRIOR, 0xdbac8a, 0x565351, ItemPropertiesHelper.material().stacksTo(64)));
    public static final Supplier<DeferredSpawnEggItem> HUMAN_ARCHER_SPAWN_EGG =  ITEMS.register("human_archer_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntityRegistry.HUMAN_ARCHER, 0xdbac8a, 0x565351, ItemPropertiesHelper.material().stacksTo(64)));

    public static final DeferredItem<Item> HUMAN_SPAWN_EGG = ITEMS.register("human_spawn_egg",
            () -> new Item(new Item.Properties()) {
                @Override
                public InteractionResult useOn(UseOnContext context) {
                    if (context.getLevel() instanceof ServerLevel serverLevel) {
                        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
                        HumanSpawner.spawnRandom(serverLevel, pos);
                        if (!context.getPlayer().isCreative()) {
                            context.getItemInHand().shrink(1);
                        }
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
                }
            }
    );

    public static void register(IEventBus eventBus)  {
        ITEMS.register(eventBus);
    }
}
