package net.raptorzizi.acolyte.setup;

import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.raptorzizi.acolyte.AcolyteMod;
import net.raptorzizi.acolyte.entity.mobs.wizards.demon.DemonArcherEntity;
import net.raptorzizi.acolyte.entity.mobs.wizards.human.HumanArcherEntity;
import net.raptorzizi.acolyte.entity.mobs.wizards.human.HumanMageEntity;
import net.raptorzizi.acolyte.entity.mobs.wizards.human.HumanWarriorEntity;
import net.raptorzizi.acolyte.util.ModUtils;
import net.raptorzizi.acolyte.entity.mobs.wizards.demon.DemonMageEntity;
import net.raptorzizi.acolyte.entity.mobs.wizards.demon.DemonWarriorEntity;
import net.raptorzizi.acolyte.registries.ModEntityRegistry;

@EventBusSubscriber(modid = AcolyteMod.MOD_ID)
public class ModCommonSetup {

    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {

        event.put(ModEntityRegistry.DEMON_MAGE.get(), DemonMageEntity.prepareAttributes().build());
        event.put(ModEntityRegistry.DEMON_WARRIOR.get(), DemonWarriorEntity.prepareAttributes().build());
        event.put(ModEntityRegistry.DEMON_ARCHER.get(), DemonArcherEntity.prepareAttributes().build());
        event.put(ModEntityRegistry.HUMAN_MAGE.get(), HumanMageEntity.prepareAttributes().build());
        event.put(ModEntityRegistry.HUMAN_WARRIOR.get(), HumanWarriorEntity.prepareAttributes().build());
        event.put(ModEntityRegistry.HUMAN_ARCHER.get(), HumanArcherEntity.prepareAttributes().build());
    }

    @SubscribeEvent
    public static void spawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntityRegistry.DEMON_MAGE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, serverLevelAccessor, spawnType, blockPos, random) -> ModUtils.checkDemonMageSpawnRules(serverLevelAccessor, spawnType, blockPos, random), RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(ModEntityRegistry.DEMON_WARRIOR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, serverLevelAccessor, spawnType, blockPos, random) -> ModUtils.checkDemonWarriorSpawnRules(serverLevelAccessor, spawnType, blockPos, random), RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(ModEntityRegistry.DEMON_ARCHER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, serverLevelAccessor, spawnType, blockPos, random) -> ModUtils.checkDemonArcherSpawnRules(serverLevelAccessor, spawnType, blockPos, random), RegisterSpawnPlacementsEvent.Operation.OR);
    }
}