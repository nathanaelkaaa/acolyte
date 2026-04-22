package net.raptorzizi.acolyte.entity.goals;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.IAnimatedAttacker;
import io.redspace.ironsspellbooks.entity.mobs.goals.WarlockAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.melee.AttackAnimationData;
import io.redspace.ironsspellbooks.network.SyncAnimationPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Version générique de KeeperAnimatedWarlockAttackGoal.
 * Fonctionne avec n'importe quelle entité qui implémente IAnimatedAttacker
 * et fournit ses AttackType via getAttackTypes().
 */
public class AnimatedMeleeGoal<T extends AbstractSpellCastingMob & IAnimatedAttacker> extends WarlockAttackGoal {
    /**
     * Interface à implémenter par l'entité pour fournir ses types d'attaque.
     */
    public interface IAnimatedMeleeEntity {
        AttackAnimationData[] getAttackTypes();
        default void playSwingSound() {}
        default void playImpactSound() {}
    }
    protected final T mob;
    protected final IAnimatedMeleeEntity animatedEntity;

    private int meleeAnimTimer = -1;
    private AttackAnimationData currentAttack;
    private AttackAnimationData nextAttack;
    private AttackAnimationData queueCombo;

    private boolean hasLunged;
    private boolean hasHitLunge;
    private Vec3 oldLungePos;

    public AnimatedMeleeGoal(T mob, double speedModifier, int minAttackInterval, int maxAttackInterval) {
        super(mob, speedModifier, minAttackInterval, maxAttackInterval);
        this.mob = mob;
        this.animatedEntity = (IAnimatedMeleeEntity) mob;
        this.wantsToMelee = true;
        this.nextAttack = randomizeNextAttack(0);
    }

    @Override
    protected float meleeBias() {
        return 1f;
    }

    @Override
    public boolean isActing() {
        return super.isActing() || meleeAnimTimer > 0;
    }

    @Override
    protected void handleAttackLogic(double distanceSquared) {
        float meleeRange = meleeRange();
        float distance = Mth.sqrt((float) distanceSquared);
        mob.getLookControl().setLookAt(target);

        if (meleeAnimTimer > 0) {
            forceFaceTarget();
            meleeAnimTimer--;

            if (currentAttack.isHitFrame(meleeAnimTimer - 4)) {
                animatedEntity.playSwingSound();
            } else if (currentAttack.isHitFrame(meleeAnimTimer)) {
                Vec3 lunge = target.position().subtract(mob.position()).normalize().scale(.55f);
                mob.push(lunge.x, lunge.y, lunge.z);

                if (distance <= meleeRange && Utils.hasLineOfSight(mob.level(), mob, target, true)) {
                    boolean hit = mob.doHurtTarget(target);
                    target.invulnerableTime = 0;
                    if (hit) {
                        animatedEntity.playImpactSound();
                        if (currentAttack.isSingleHit() && (mob.getRandom().nextFloat() < .75f || target.isBlocking())) {
                            queueCombo = randomizeNextAttack(0);
                        }
                    }
                }
            }

        } else if (queueCombo != null && target != null && !target.isDeadOrDying()) {
            nextAttack = queueCombo;
            queueCombo = null;
            doMeleeAction();

        } else if (meleeAnimTimer == 0) {
            nextAttack = randomizeNextAttack(distance);
            resetMeleeAttackInterval(distanceSquared);
            meleeAnimTimer = -1;

        } else {
            if (distance < meleeRange) {
                if (hasLineOfSight && --this.meleeAttackDelay == 0) {
                    doMeleeAction();
                } else if (this.meleeAttackDelay < 0) {
                    resetMeleeAttackInterval(distanceSquared);
                }
            } else if (--this.meleeAttackDelay < 0) {
                resetMeleeAttackInterval(distanceSquared);
                nextAttack = randomizeNextAttack(distance);
            }
        }
    }

    @Override
    protected void doMeleeAction() {
        currentAttack = nextAttack;
        if (currentAttack != null) {
            mob.swing(InteractionHand.MAIN_HAND);
            meleeAnimTimer = currentAttack.lengthInTicks;
            hasLunged = false;
            hasHitLunge = false;
            PacketDistributor.sendToPlayersTrackingEntity(mob,
                    new SyncAnimationPacket<>(getAnimationId(currentAttack), mob));
        }
    }

    @Override
    protected void doMovement(double distanceSquared) {
        float meleeRange = meleeRange();
        if (target.isDeadOrDying()) {
            mob.getNavigation().stop();
        } else if (distanceSquared > meleeRange * meleeRange) {
            mob.getNavigation().moveTo(target, this.speedModifier * 1.3f);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() || meleeAnimTimer > 0;
    }

    @Override
    public void stop() {
        super.stop();
        meleeAnimTimer = -1;
        queueCombo = null;
    }

    private AttackAnimationData randomizeNextAttack(float distance) {
        AttackAnimationData[] types = animatedEntity.getAttackTypes();
        if (types == null || types.length == 0) return null;
        return types[mob.getRandom().nextInt(types.length)];
    }

    /**
     * Retourne l'ID d'animation à envoyer au client via SyncAnimationPacket.
     * Par défaut utilise animationId du AttackAnimationData.
     * Peut être surchargé si l'entité utilise un enum nommé.
     */
    protected String getAnimationId(AttackAnimationData data) {
        return data.animationId;
    }

    private void forceFaceTarget() {
        double d0 = target.getX() - mob.getX();
        double d1 = target.getZ() - mob.getZ();
        float yRot = (float) (Mth.atan2(d1, d0) * (180F / (float) Math.PI)) - 90.0F;
        mob.setYBodyRot(yRot);
        mob.setYHeadRot(yRot);
        mob.setYRot(yRot);
    }
}