package net.raptorzizi.acolyte.item.armor;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.ExtendedArmorItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.raptorzizi.acolyte.entity.armor.DemonHornModel;
import net.raptorzizi.acolyte.registries.ModArmorMaterialRegistry;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class DemonHornsItem extends ExtendedArmorItem {

    private final String modelName;
    private final String textureName;

    public DemonHornsItem(Type slot, Properties settings, String modelName, String textureName) {
        super(ModArmorMaterialRegistry.COSMETIC, slot, settings,
                new AttributeContainer(AttributeRegistry.MAX_MANA, 125, AttributeModifier.Operation.ADD_VALUE),
                new AttributeContainer(AttributeRegistry.MANA_REGEN, 0.35, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

        this.modelName = modelName;
        this.textureName = textureName;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public GeoArmorRenderer<?> supplyRenderer() {
        return new GenericCustomArmorRenderer<>(new DemonHornModel(modelName, textureName));
    }
}