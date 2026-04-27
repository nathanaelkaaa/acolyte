package net.raptorzizi.acolyte.registries;

import io.redspace.ironsspellbooks.registries.PotionRegistry;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.raptorzizi.acolyte.AcolyteMod;

@EventBusSubscriber(modid = AcolyteMod.MOD_ID)
public class ModPotionRegistry {

    @SubscribeEvent
    public static void addRecipes(RegisterBrewingRecipesEvent event) {
        event.getBuilder().addMix(
                Potions.AWKWARD,
                ModItemsRegistry.DEMON_HORN.get(),
                PotionRegistry.INSTANT_MANA_TWO
        );
    }
}