package net.raptorzizi.acolyte.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.raptorzizi.acolyte.AcolyteMod;
import net.raptorzizi.acolyte.entity.mobs.horn_merchant.HornMerchantEntity;
import net.raptorzizi.acolyte.entity.mobs.wizards.demon.DemonArcherEntity;
import net.raptorzizi.acolyte.entity.mobs.wizards.demon.DemonMageEntity;
import net.raptorzizi.acolyte.entity.mobs.wizards.demon.DemonWarriorEntity;
import net.raptorzizi.acolyte.entity.mobs.wizards.human.HumanMageEntity;
import net.raptorzizi.acolyte.entity.mobs.wizards.human.HumanWarriorEntity;
import net.raptorzizi.acolyte.entity.mobs.wizards.human.HumanArcherEntity;

public class ModEntityRegistry {

    private static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, AcolyteMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<DemonMageEntity>> DEMON_MAGE =
            ENTITIES.register("demon_mage", () -> EntityType.Builder.of(DemonMageEntity::new, MobCategory.MONSTER)
                    .sized(.6f, 1.8f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(AcolyteMod.MOD_ID, "demon_mage").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<DemonWarriorEntity>> DEMON_WARRIOR =
            ENTITIES.register("demon_warrior", () -> EntityType.Builder.of(DemonWarriorEntity::new, MobCategory.MONSTER)
                    .sized(.6f, 1.8f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(AcolyteMod.MOD_ID, "demon_warrior").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<DemonArcherEntity>> DEMON_ARCHER =
            ENTITIES.register("demon_archer", () -> EntityType.Builder.of(DemonArcherEntity::new, MobCategory.MONSTER)
                    .sized(.6f, 1.8f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(AcolyteMod.MOD_ID, "demon_archer").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<HumanMageEntity>> HUMAN_MAGE =
            ENTITIES.register("human_mage", () -> EntityType.Builder.of(HumanMageEntity::new, MobCategory.MONSTER)
                    .sized(.6f, 1.8f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(AcolyteMod.MOD_ID, "human_mage").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<HumanWarriorEntity>> HUMAN_WARRIOR =
            ENTITIES.register("human_warrior", () -> EntityType.Builder.of(HumanWarriorEntity::new, MobCategory.MONSTER)
                    .sized(.6f, 1.8f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(AcolyteMod.MOD_ID, "human_warrior").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<HumanArcherEntity>> HUMAN_ARCHER =
            ENTITIES.register("human_archer", () -> EntityType.Builder.of(HumanArcherEntity::new, MobCategory.MONSTER)
                    .sized(.6f, 1.8f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(AcolyteMod.MOD_ID, "human_archer").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<HornMerchantEntity>> HORN_MERCHANT =
            ENTITIES.register("horn_merchant", () -> EntityType.Builder.of(HornMerchantEntity::new, MobCategory.CREATURE)
                    .sized(.6f, 1.95f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(AcolyteMod.MOD_ID, "horn_merchant").toString()));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}