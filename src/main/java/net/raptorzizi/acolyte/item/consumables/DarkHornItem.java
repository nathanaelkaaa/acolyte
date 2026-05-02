package net.raptorzizi.acolyte.item.consumables;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class DarkHornItem extends Item {

    public DarkHornItem(Properties pProperties) {
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
            MobEffectInstance mana = new MobEffectInstance(MobEffectRegistry.INSTANT_MANA,0,3);
            if (mana.getEffect().value().isInstantenous()) {
                mana.getEffect().value().applyInstantenousEffect(
                        pLivingEntity, pLivingEntity, pLivingEntity, mana.getAmplifier(), 1.0
                );
            } else {
                pLivingEntity.addEffect(mana);
            }
            pLivingEntity.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 6000, 4, false, true, true));
        }
        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }
}