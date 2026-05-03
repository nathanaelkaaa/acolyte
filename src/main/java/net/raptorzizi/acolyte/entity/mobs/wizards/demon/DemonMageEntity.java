package net.raptorzizi.acolyte.entity.mobs.wizards.demon;


import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class DemonMageEntity extends DemonEntity {

    public DemonMageEntity(EntityType<? extends AbstractSpellCastingMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected String getArchetypeName() {
        return "demon/mage";
    }

    @Override
    protected void registerArchetypeGoals() {
        if (selectedProfile == null) return;

        if (selectedProfile.barrageSpell != null) {
            int bMin = selectedProfile.barrageAttackInterval;
            int bMax = bMin + 20;
            this.goalSelector.addGoal(1, new SpellBarrageGoal(this,
                    selectedProfile.barrageSpell, 1, 1,
                    bMin, bMax,
                    selectedProfile.barrageProjectileCount));
        }

        int sMin = selectedProfile.attackInterval;
        int sMax = sMin + 20;
        WizardAttackGoal goal = new WizardAttackGoal(this, 1.25f, sMin, sMax)
                .setSpellQuality(selectedProfile.spellsQuality, selectedProfile.spellsQuality + 0.1f)
                .setSpells(
                        selectedProfile.attackSpells,
                        selectedProfile.defenseSpells,
                        selectedProfile.mobilitySpells,
                        selectedProfile.utilitySpells
                );

        if (selectedProfile.singleUseSpell != null) {
            int dMin = selectedProfile.singleUseSpellDelay;
            int dMax = dMin + 20;
            goal.setSingleUseSpell(selectedProfile.singleUseSpell, dMin, dMax, 1, 1);
        }

        this.goalSelector.addGoal(2, goal);
    }

    public static AttributeSupplier.Builder prepareAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0)
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected boolean shouldDespawnInPeaceful() { return true; }
}