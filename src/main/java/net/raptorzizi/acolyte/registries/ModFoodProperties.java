package net.raptorzizi.acolyte.registries;

import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {

    public static final FoodProperties DEMON_HORN = new FoodProperties.Builder()
            .nutrition(3)
            .saturationModifier(0)
            .alwaysEdible()
            .build();
}