package net.raptorzizi.acolyte.entity.mobs.horn_merchant;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.raptorzizi.acolyte.AcolyteMod;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class HornMerchantModel extends GeoModel<HornMerchantEntity> {

    @Override
    public ResourceLocation getModelResource(HornMerchantEntity entity) {
        return AcolyteMod.id("geo/entity/horn_merchant.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HornMerchantEntity entity) {
        return AcolyteMod.id("textures/entity/horn_merchant.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HornMerchantEntity entity) {
        return AcolyteMod.id("animations/horn_merchant.animation.json");
    }

    @Override
    public void setCustomAnimations(HornMerchantEntity entity, long instanceId,
                                    AnimationState<HornMerchantEntity> animationState) {
        GeoBone head = this.getAnimationProcessor().getBone("head");
        if (head == null) return;

        float partialTick = animationState.getPartialTick();

        float yaw = Mth.rotLerp(partialTick, entity.yHeadRotO, entity.yHeadRot)
                - Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
        float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());

        head.setRotY(-yaw   * (float)(Math.PI / 180.0));
        head.setRotX(-pitch * (float)(Math.PI / 180.0));
    }
}