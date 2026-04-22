package net.raptorzizi.acolyte.entity.mobs.wizards.human;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class HumanRenderer extends AbstractSpellCastingMobRenderer {

    public HumanRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanModel());
    }
}
