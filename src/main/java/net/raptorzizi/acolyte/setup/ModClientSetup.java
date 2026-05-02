package net.raptorzizi.acolyte.setup;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.raptorzizi.acolyte.AcolyteMod;
import net.raptorzizi.acolyte.entity.mobs.horn_merchant.HornMerchantRenderer;
import net.raptorzizi.acolyte.entity.mobs.lieutenant.LieutenantRenderer;
import net.raptorzizi.acolyte.entity.mobs.wizards.demon.DemonRenderer;
import net.raptorzizi.acolyte.entity.mobs.wizards.human.HumanRenderer;
import net.raptorzizi.acolyte.gui.RecruitScreen;
import net.raptorzizi.acolyte.registries.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(
        modid = AcolyteMod.MOD_ID,
        value = Dist.CLIENT
)
public class ModClientSetup {

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuRegistry.RECRUIT_MENU.get(), RecruitScreen::new);
    }

    @SubscribeEvent
    public static void rendererRegister(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityRegistry.DEMON_MAGE.get(), DemonRenderer::new);
        event.registerEntityRenderer(ModEntityRegistry.DEMON_WARRIOR.get(), DemonRenderer::new);
        event.registerEntityRenderer(ModEntityRegistry.DEMON_ARCHER.get(), DemonRenderer::new);
        event.registerEntityRenderer(ModEntityRegistry.HUMAN_MAGE.get(), HumanRenderer::new);
        event.registerEntityRenderer(ModEntityRegistry.HUMAN_WARRIOR.get(), HumanRenderer::new);
        event.registerEntityRenderer(ModEntityRegistry.HUMAN_ARCHER.get(), HumanRenderer::new);
        event.registerEntityRenderer(ModEntityRegistry.HORN_MERCHANT.get(), HornMerchantRenderer::new);
        event.registerEntityRenderer(ModEntityRegistry.LIEUTENANT.get(), LieutenantRenderer::new);
    }

}