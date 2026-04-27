package net.raptorzizi.acolyte.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.raptorzizi.acolyte.AcolyteMod;

import static io.redspace.ironsspellbooks.registries.CreativeTabRegistry.EQUIPMENT_TAB;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AcolyteMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MATERIALS_TAB = CREATIVE_MODE_TAB.register("spellbook_materials",
            () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + AcolyteMod.MOD_ID + ".creative_tab"))
            .icon(() -> new ItemStack(ModItemsRegistry.DEMON_HORNS.getFirst()))
            .displayItems((enabledFeatures, entries) -> {
                /*entries.accept(ModBlocksRegistry.TAVERN_SPAWN_MARKER_OAK.get());
                entries.accept(ModBlocksRegistry.TAVERN_SPAWN_MARKER_COBBLESTONE.get());
                entries.accept(ModBlocksRegistry.TAVERN_SPAWN_MARKER_ACACIA.get());
                entries.accept(ModBlocksRegistry.TAVERN_SPAWN_MARKER_SPRUCE.get());
                entries.accept(ModBlocksRegistry.TAVERN_SPAWN_MARKER_SANDSTONE.get());*/
                ModItemsRegistry.DEMON_HORNS.forEach(horn -> entries.accept(horn.get()));
                entries.accept(ModItemsRegistry.DEMON_HORN.get());
                entries.accept(ModItemsRegistry.DEMON_MAGE_SPAWN_EGG.get());
                entries.accept(ModItemsRegistry.DEMON_WARRIOR_SPAWN_EGG.get());
                entries.accept(ModItemsRegistry.DEMON_ARCHER_SPAWN_EGG.get());
                entries.accept(ModItemsRegistry.DEMON_SPAWN_EGG.get());
                entries.accept(ModItemsRegistry.HUMAN_MAGE_SPAWN_EGG.get());
                entries.accept(ModItemsRegistry.HUMAN_WARRIOR_SPAWN_EGG.get());
                entries.accept(ModItemsRegistry.HUMAN_ARCHER_SPAWN_EGG.get());
                entries.accept(ModItemsRegistry.HUMAN_SPAWN_EGG.get());
                entries.accept(ModItemsRegistry.HORN_MERCHANT_SPAWN_EGG.get());
            })
            .withTabsBefore(EQUIPMENT_TAB.getKey())
            .build());


    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
