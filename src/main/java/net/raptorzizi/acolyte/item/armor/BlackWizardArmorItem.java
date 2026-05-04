package net.raptorzizi.acolyte.item.armor;

import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.ExtendedArmorItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.raptorzizi.acolyte.entity.armor.CosmeticModel;
import net.raptorzizi.acolyte.registries.ModArmorMaterialRegistry;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BlackWizardArmorItem extends ExtendedArmorItem {

    private final String modelName;
    private final String textureName;

    public BlackWizardArmorItem(Type slot, Properties settings, String modelName, String textureName) {
        super(ModArmorMaterialRegistry.COSMETIC, slot, settings);

        this.modelName = modelName;
        this.textureName = textureName;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public GeoArmorRenderer<?> supplyRenderer() {
        return new GenericCustomArmorRenderer<>(new CosmeticModel(modelName, textureName));
    }
}