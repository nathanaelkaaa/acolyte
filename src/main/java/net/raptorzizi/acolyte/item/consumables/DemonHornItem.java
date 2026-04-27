package net.raptorzizi.acolyte.item.consumables;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;

public class DemonHornItem extends Item {

    public DemonHornItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.EAT;
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity entity) {
        return 24;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        pPlayer.startUsingItem(pHand);
        return InteractionResultHolder.consume(pPlayer.getItemInHand(pHand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (!pLevel.isClientSide) {
            MobEffectInstance effect = new MobEffectInstance(MobEffectRegistry.INSTANT_MANA);
            if (effect.getEffect().value().isInstantenous()) {
                effect.getEffect().value().applyInstantenousEffect(
                        pLivingEntity, pLivingEntity, pLivingEntity, effect.getAmplifier(), 1.0
                );
            } else {
                pLivingEntity.addEffect(effect);
            }
        }
        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }
}