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
import net.raptorzizi.acolyte.item.armor.*;
import net.raptorzizi.acolyte.item.consumables.DarkHornItem;
import net.raptorzizi.acolyte.item.consumables.DemonHornItem;

import java.util.List;
import java.util.function.Supplier;

public class ModItemsRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AcolyteMod.MOD_ID);

    public static final DeferredItem<Item> DEMON_HORN = ITEMS.register("demon_horn",
            () -> new DemonHornItem(new Item.Properties().food(ModFoodProperties.DEMON_HORN)));
    public static final DeferredItem<Item> DARK_HORN = ITEMS.register("dark_horn",
            () -> new DarkHornItem(new Item.Properties().food(ModFoodProperties.DARK_HORN)));

    /**
     * Armor
     */

    public static final DeferredItem<Item> DEMON_HORN_0 = ITEMS.register("demon_horns_0",
            () -> new DemonHornsItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON), "demon_horns_0", "demon_horns_0"));

    public static final List<DeferredHolder<Item, Item>> DEMON_HORNS = List.of(
            ITEMS.register("demon_horns_1", () -> new DemonHornsItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON), "demon_horns_1", "demon_horns_1")),
            ITEMS.register("demon_horns_2", () -> new DemonHornsItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON), "demon_horns_2", "demon_horns_2")),
            ITEMS.register("demon_horns_3", () -> new DemonHornsItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON), "demon_horns_3", "demon_horns_3"))
    );

    public static final List<DeferredHolder<Item, Item>> ARCHER_TIER1 = List.of(
            ITEMS.register("archer_tier1_chestplate", () -> new ArcherArmorItem(ArmorItem.Type.CHESTPLATE, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON),"archer_armor", "archer_armor_tier1"))
    );

    public static final List<DeferredHolder<Item, Item>> ARCHER_TIER2 = List.of(
            ITEMS.register("archer_tier2_helmet", () -> new ArcherArmorItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON),"archer_armor", "archer_armor_tier2")),
            ITEMS.register("archer_tier2_chestplate", () -> new ArcherArmorItem(ArmorItem.Type.CHESTPLATE, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON),"archer_armor", "archer_armor_tier2"))
    );

    public static final List<DeferredHolder<Item, Item>> ARCHER_TIER3 = List.of(
            ITEMS.register("archer_tier3_helmet", () -> new ArcherArmorItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON),"archer_armor", "archer_armor_tier3")),
            ITEMS.register("archer_tier3_chestplate", () -> new ArcherArmorItem(ArmorItem.Type.CHESTPLATE, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON),"archer_armor", "archer_armor_tier3"))
    );

    public static final List<DeferredHolder<Item, Item>> WARRIOR_TIER1 = List.of(
            ITEMS.register("warrior_tier1_helmet", () -> new WarriorArmorItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON),"warrior_armor", "warrior_armor_tier1"))
    );

    public static final List<DeferredHolder<Item, Item>> WARRIOR_TIER2 = List.of(
            ITEMS.register("warrior_tier2_helmet", () -> new WarriorArmorItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON),"warrior_armor", "warrior_armor_tier2")),
            ITEMS.register("warrior_tier2_chestplate", () -> new WarriorArmorItem(ArmorItem.Type.CHESTPLATE, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON),"warrior_armor", "warrior_armor_tier2"))
    );

    public static final List<DeferredHolder<Item, Item>> WARRIOR_TIER3 = List.of(
            ITEMS.register("warrior_tier3_helmet", () -> new WarriorArmorItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON),"warrior_armor", "warrior_armor_tier3")),
            ITEMS.register("warrior_tier3_chestplate", () -> new WarriorArmorItem(ArmorItem.Type.CHESTPLATE, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON),"warrior_armor", "warrior_armor_tier3"))
    );

    public static final List<DeferredHolder<Item, Item>> WIZARD_TIER1 = List.of(
            ITEMS.register("wizard_tier1_helmet", () -> new WizardArmorItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON),"wizard_armor", "wizard_armor_tier1"))
    );

    public static final List<DeferredHolder<Item, Item>> WIZARD_TIER2 = List.of(
            ITEMS.register("wizard_tier2_helmet", () -> new WizardArmorItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON),"wizard_armor", "wizard_armor_tier2")),
            ITEMS.register("wizard_tier2_chestplate", () -> new WizardArmorItem(ArmorItem.Type.CHESTPLATE, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON),"wizard_armor", "wizard_armor_tier2"))
    );

    public static final List<DeferredHolder<Item, Item>> WIZARD_TIER3 = List.of(
            ITEMS.register("wizard_tier3_helmet", () -> new WizardArmorItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON),"wizard_armor", "wizard_armor_tier3")),
            ITEMS.register("wizard_tier3_chestplate", () -> new WizardArmorItem(ArmorItem.Type.CHESTPLATE, ItemPropertiesHelper.hidden(1).rarity(Rarity.COMMON),"wizard_armor", "wizard_armor_tier3"))
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

    public static final Supplier<DeferredSpawnEggItem> HORN_MERCHANT_SPAWN_EGG =  ITEMS.register("horn_merchant_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntityRegistry.HORN_MERCHANT, 0x3e4d7b, 0xfafafa, ItemPropertiesHelper.material().stacksTo(64)));

    public static final Supplier<DeferredSpawnEggItem> LIEUTENANT_SPAWN_EGG = ITEMS.register("lieutenant_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntityRegistry.LIEUTENANT, 0x1a1a2e, 0xb22222, ItemPropertiesHelper.material().stacksTo(64)));

    public static void register(IEventBus eventBus)  {
        ITEMS.register(eventBus);
    }
}
