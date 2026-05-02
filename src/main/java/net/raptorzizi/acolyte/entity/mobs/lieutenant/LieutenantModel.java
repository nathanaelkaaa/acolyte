package net.raptorzizi.acolyte.entity.mobs.lieutenant;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;
import net.raptorzizi.acolyte.AcolyteMod;

public class LieutenantModel extends AbstractSpellCastingMobModel {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AcolyteMod.MOD_ID, "textures/entity/lieutenant.png");
    public static final ResourceLocation modelResource = ResourceLocation.fromNamespaceAndPath(AcolyteMod.MOD_ID, "geo/entity/lieutenant.geo.json");

    @Override
    public ResourceLocation getTextureResource(AbstractSpellCastingMob object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getModelResource(AbstractSpellCastingMob object) {
        return modelResource;
    }
}