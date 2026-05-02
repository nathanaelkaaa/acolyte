package net.raptorzizi.acolyte.registries;

import io.redspace.ironsspellbooks.registries.PotionRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
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

        ItemStack awkwardStack = new ItemStack(Items.POTION);
        awkwardStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.AWKWARD));

        ItemStack ominousBottle = new ItemStack(Items.OMINOUS_BOTTLE);
        ominousBottle.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, 4);

        event.getBuilder().addRecipe(new BrewingRecipe(
                DataComponentIngredient.of(false, awkwardStack),
                Ingredient.of(ModItemsRegistry.DARK_HORN.get()),
                ominousBottle
        ));
    }
}
