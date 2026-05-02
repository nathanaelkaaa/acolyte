package net.raptorzizi.acolyte.entity.mobs.lieutenant;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.goals.WarlockAttackGoal;
import io.redspace.ironsspellbooks.network.SyncAnimationPacket;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class LieutenantAnimatedWarlockAttackGoal extends WarlockAttackGoal {
    final LieutenantEntity lieutenant;

    private static final int STOMP_COOLDOWN = 120;
    private static final int BLOOD_STEP_COOLDOWN = 180;
    private static final float BLOOD_STEP_MIN_DISTANCE = 8f;

    private int stompCooldown = 0;
    private int bloodStepCooldown = 0;

    public LieutenantAnimatedWarlockAttackGoal(LieutenantEntity abstractSpellCastingMob, double pSpeedModifier, int minAttackInterval, int maxAttackInterval) {
        super(abstractSpellCastingMob, pSpeedModifier, minAttackInterval, maxAttackInterval);
        lieutenant = abstractSpellCastingMob;
        nextAttack = randomizeNextAttack(0);
        this.wantsToMelee = true;
    }

    int meleeAnimTimer = -1;
    public LieutenantEntity.AttackType currentAttack;
    public LieutenantEntity.AttackType nextAttack;
    public LieutenantEntity.AttackType queueCombo;
    private boolean hasLunged;
    private boolean hasHitLunge;
    private Vec3 oldLungePos;

    @Override
    public boolean isActing() {
        return super.isActing() || meleeAnimTimer > 0;
    }

    @Override
    protected void handleAttackLogic(double distanceSquared) {
        var meleeRange = meleeRange();
        float distance = Mth.sqrt((float) distanceSquared);
        lieutenant.isMeleeing = meleeAnimTimer > 0;

        if (stompCooldown > 0) stompCooldown--;
        if (bloodStepCooldown > 0) bloodStepCooldown--;

        mob.getLookControl().setLookAt(target);

        // Blood Step si la target est trop loin et qu'on n'est pas en train de frapper
        if (meleeAnimTimer < 0 && !spellCastingMob.isCasting() && bloodStepCooldown <= 0 && distance > BLOOD_STEP_MIN_DISTANCE) {
            spellCastingMob.initiateCastSpell(SpellRegistry.BLOOD_STEP_SPELL.get(), 1);
            bloodStepCooldown = BLOOD_STEP_COOLDOWN;
            return;
        }

        if (meleeAnimTimer > 0) {
            forceFaceTarget();
            meleeAnimTimer--;
            if (currentAttack.data.isHitFrame(meleeAnimTimer - 4)) {
                if (currentAttack != LieutenantEntity.AttackType.Lunge) {
                    playSwingSound();
                }
            } else if (currentAttack.data.isHitFrame(meleeAnimTimer)) {
                if (currentAttack != LieutenantEntity.AttackType.Lunge) {
                    Vec3 lunge = target.position().subtract(mob.position()).normalize().scale(.55f);
                    mob.push(lunge.x, lunge.y, lunge.z);
                    if (distance <= meleeRange && Utils.hasLineOfSight(mob.level(), mob, target, true)) {
                        boolean flag = this.mob.doHurtTarget(target);
                        target.invulnerableTime = 0;
                        if (flag) {
                            playImpactSound();
                            if (currentAttack.data.isSingleHit() && ((mob.getRandom().nextFloat() < .75f) || target.isBlocking())) {
                                queueCombo = randomizeNextAttack(0);
                            }
                        }
                    }
                } else {
                    if (!hasLunged) {
                        Vec3 lunge = target.position().subtract(mob.position()).normalize().multiply(2.4, .5, 2.4).add(0, 0.15, 0);
                        mob.push(lunge.x, lunge.y, lunge.z);
                        oldLungePos = mob.position();
                        mob.getNavigation().stop();
                        hasLunged = true;
                        playSwingSound();
                    }
                    if (!hasHitLunge && distance <= meleeRange * .45f) {
                        if (this.mob.doHurtTarget(target)) {
                            playImpactSound();
                        }
                        Vec3 knockback = oldLungePos.subtract(target.position());
                        target.knockback(1, knockback.x, knockback.z);
                        hasHitLunge = true;
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
            if (distance < meleeRange * (nextAttack == LieutenantEntity.AttackType.Lunge ? 3 : 1)) {
                if (hasLineOfSight && --this.meleeAttackDelay == 0) {
                    // Stomp remplace une attaque de mêlée si le cooldown est écoulé
                    if (stompCooldown <= 0 && !spellCastingMob.isCasting()) {
                        spellCastingMob.initiateCastSpell(SpellRegistry.STOMP_SPELL.get(), 1);
                        stompCooldown = STOMP_COOLDOWN;
                        resetMeleeAttackInterval(distanceSquared);
                    } else {
                        doMeleeAction();
                    }
                } else if (this.meleeAttackDelay < 0) {
                    resetMeleeAttackInterval(distanceSquared);
                }
            } else if (--this.meleeAttackDelay < 0) {
                resetMeleeAttackInterval(distanceSquared);
                nextAttack = randomizeNextAttack(distance);
            }
        }
    }

    private LieutenantEntity.AttackType randomizeNextAttack(float distance) {
        var meleeRange = meleeRange();
        int i;
        if (distance < meleeRange * 1.5f) {
            i = LieutenantEntity.AttackType.values().length - 1;
        } else if (mob.getRandom().nextFloat() < .25f && distance > meleeRange * 2.5f) {
            return LieutenantEntity.AttackType.Lunge;
        } else {
            i = LieutenantEntity.AttackType.values().length;
        }
        return LieutenantEntity.AttackType.values()[mob.getRandom().nextInt(i)];
    }

    private void forceFaceTarget() {
        if (hasLunged)
            return;
        double d0 = target.getX() - mob.getX();
        double d1 = target.getZ() - mob.getZ();
        float yRot = (float) (Mth.atan2(d1, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
        mob.setYBodyRot(yRot);
        mob.setYHeadRot(yRot);
        mob.setYRot(yRot);
    }

    @Override
    protected void doMeleeAction() {
        currentAttack = nextAttack;
        if (currentAttack != null) {
            this.mob.swing(InteractionHand.MAIN_HAND);
            meleeAnimTimer = currentAttack.data.lengthInTicks;
            hasLunged = false;
            hasHitLunge = false;
            PacketDistributor.sendToPlayersTrackingEntity(lieutenant, new SyncAnimationPacket<>(currentAttack.toString(), lieutenant));
        }
    }

    @Override
    protected void doMovement(double distanceSquared) {
        var meleeRange = meleeRange();
        if (target.isDeadOrDying()) {
            this.mob.getNavigation().stop();
        } else if (distanceSquared > meleeRange * meleeRange) {
            this.mob.getNavigation().moveTo(this.target, this.speedModifier * 1.3f);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() || meleeAnimTimer > 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.meleeAnimTimer = -1;
        this.queueCombo = null;
    }

    public void playSwingSound() {
        mob.playSound(SoundRegistry.KEEPER_SWING.get(), 1, Mth.randomBetweenInclusive(mob.getRandom(), 9, 13) * .1f);
    }

    public void playImpactSound() {
        mob.playSound(SoundRegistry.KEEPER_SWORD_IMPACT.get(), 1, Mth.randomBetweenInclusive(mob.getRandom(), 9, 13) * .1f);
    }
}
