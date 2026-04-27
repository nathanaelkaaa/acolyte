package net.raptorzizi.acolyte.entity.mobs.horn_merchant;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HornMerchantRenderer extends GeoEntityRenderer<HornMerchantEntity> {

    public HornMerchantRenderer(EntityRendererProvider.Context context) {
        super(context, new HornMerchantModel());
    }
}