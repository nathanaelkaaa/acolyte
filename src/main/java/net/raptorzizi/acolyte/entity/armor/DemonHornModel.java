package net.raptorzizi.acolyte.entity.armor;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.ExtendedArmorItem;
import net.minecraft.resources.ResourceLocation;
import net.raptorzizi.acolyte.AcolyteMod;
import software.bernie.geckolib.model.GeoModel;

public class DemonHornModel extends GeoModel<ExtendedArmorItem> {

    private final ResourceLocation model;
    private final ResourceLocation texture;

    public DemonHornModel(String modelName, String textureName) {
        this.model = ResourceLocation.fromNamespaceAndPath(AcolyteMod.MOD_ID, "geo/" + modelName + ".geo.json");
        this.texture = ResourceLocation.fromNamespaceAndPath(AcolyteMod.MOD_ID, "textures/models/armor/" + textureName + ".png");
    }

    @Override
    public ResourceLocation getModelResource(ExtendedArmorItem object) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(ExtendedArmorItem object) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(ExtendedArmorItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");
    }
}