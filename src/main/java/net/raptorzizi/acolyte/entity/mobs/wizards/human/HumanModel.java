package net.raptorzizi.acolyte.entity.mobs.wizards.human;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.ResourceLocation;

public class HumanModel extends AbstractSpellCastingMobModel {

    @Override
    public ResourceLocation getTextureResource(AbstractSpellCastingMob object) {
        if (object instanceof HumanEntity Human) {
            return Human.getTextureLocation();
        }
        return HumanEntity.FALLBACK_TEXTURE;
    }
}