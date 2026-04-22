package net.raptorzizi.acolyte.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;


public class ModArmorMaterialRegistry {
    public static final String MOD_ID = "acolyte";
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, MOD_ID);
    public static DeferredHolder<ArmorMaterial, ArmorMaterial> COSMETIC;


    private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(
            String name,
            EnumMap<ArmorItem.Type, Integer> defense,
            int enchantmentValue,
            Holder<SoundEvent> equipSound,
            Supplier<Ingredient> repairIngredient,
            float toughness,
            float knockbackResistance) {

        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(
                ResourceLocation.fromNamespaceAndPath(MOD_ID, name)
        ));

        return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(
                defense,
                enchantmentValue,
                equipSound,
                repairIngredient,
                layers,
                toughness,
                knockbackResistance
        ));
    }

    public static EnumMap<ArmorItem.Type, Integer> makeArmorMap(int helmet, int chestplate, int leggings, int boots) {
        EnumMap<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        map.put(ArmorItem.Type.BOOTS, boots);
        map.put(ArmorItem.Type.LEGGINGS, leggings);
        map.put(ArmorItem.Type.CHESTPLATE, chestplate);
        map.put(ArmorItem.Type.HELMET, helmet);
        return map;
    }

    public static void register(IEventBus eventBus) {
        ARMOR_MATERIALS.register(eventBus);
    }

    static {
        COSMETIC = register("cosmetic", makeArmorMap(0, 0, 0, 0), 15, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(Tags.Items.LEATHERS), 0.0F, 0.0F);
    }
}
