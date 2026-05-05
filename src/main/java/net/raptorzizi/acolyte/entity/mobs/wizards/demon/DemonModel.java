package net.raptorzizi.acolyte.entity.mobs.wizards.demon;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;

public class DemonModel extends AbstractSpellCastingMobModel {

    @Override
    public ResourceLocation getTextureResource(AbstractSpellCastingMob object) {
        if (object instanceof DemonEntity demon) {
            return demon.getTextureLocation();
        }
        return DemonEntity.FALLBACK_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(AbstractSpellCastingMob animatable) {
        if (animatable instanceof DemonEntity demon) {
            return demon.currentAnimFile;
        }
        return AbstractSpellCastingMob.animationInstantCast;
    }
}