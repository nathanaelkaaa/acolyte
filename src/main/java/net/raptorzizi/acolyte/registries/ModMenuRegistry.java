package net.raptorzizi.acolyte.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.raptorzizi.acolyte.AcolyteMod;
import net.raptorzizi.acolyte.gui.RecruitMenu;

public class ModMenuRegistry {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, AcolyteMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<RecruitMenu>> RECRUIT_MENU =
            MENUS.register("recruit_menu",
                    () -> IMenuTypeExtension.create(RecruitMenu::new)
            );

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}