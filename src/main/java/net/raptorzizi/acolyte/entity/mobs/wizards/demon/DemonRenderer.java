package net.raptorzizi.acolyte.entity.mobs.wizards.demon;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DemonRenderer extends AbstractSpellCastingMobRenderer {

    public DemonRenderer(EntityRendererProvider.Context context) {
        super(context, new DemonModel());
    }
}
