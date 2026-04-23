package net.raptorzizi.acolyte.entity.mobs.wizards.archetype;

import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.raptorzizi.acolyte.AcolyteMod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ArchetypeUtils {

    // Attributs

    public static void captureBaseAttributes(
            LivingEntity entity,
            Map<Holder<Attribute>, Double> baseAttributeValues) {

        for (Holder<Attribute> attr : List.of(
                Attributes.MAX_HEALTH,
                Attributes.ATTACK_DAMAGE,
                Attributes.MOVEMENT_SPEED,
                Attributes.FOLLOW_RANGE,
                Attributes.ARMOR,
                Attributes.ATTACK_KNOCKBACK
        )) {
            var inst = entity.getAttribute(attr);
            if (inst != null) baseAttributeValues.put(attr, inst.getBaseValue());
        }
    }

    public static void resetAttr(
            LivingEntity entity,
            Map<Holder<Attribute>, Double> baseAttributeValues,
            Holder<Attribute> attr) {

        var inst = entity.getAttribute(attr);
        Double base = baseAttributeValues.get(attr);
        if (inst != null && base != null) inst.setBaseValue(base);
    }

    public static void applyProfileStats(
            LivingEntity entity,
            @Nullable ArchetypeProfile profile,
            Map<Holder<Attribute>, Double> baseAttributeValues) {

        resetAttr(entity, baseAttributeValues, Attributes.MAX_HEALTH);
        resetAttr(entity, baseAttributeValues, Attributes.ATTACK_DAMAGE);
        resetAttr(entity, baseAttributeValues, Attributes.MOVEMENT_SPEED);
        resetAttr(entity, baseAttributeValues, Attributes.FOLLOW_RANGE);
        resetAttr(entity, baseAttributeValues, Attributes.ARMOR);
        resetAttr(entity, baseAttributeValues, Attributes.ATTACK_KNOCKBACK);

        if (profile == null || profile.statOverrides == null) return;

        profile.statOverrides.forEach((key, value) -> {
            Holder<Attribute> attribute = switch (key) {
                case "max_health"       -> Attributes.MAX_HEALTH;
                case "attack_damage"    -> Attributes.ATTACK_DAMAGE;
                case "movement_speed"   -> Attributes.MOVEMENT_SPEED;
                case "follow_range"     -> Attributes.FOLLOW_RANGE;
                case "armor"            -> Attributes.ARMOR;
                case "attack_knockback" -> Attributes.ATTACK_KNOCKBACK;
                default -> null;
            };
            if (attribute != null) {
                var instance = entity.getAttribute(attribute);
                if (instance != null) instance.setBaseValue(value);
            }
        });

        if (profile.hasStatOverride("max_health")) {
            entity.setHealth(entity.getMaxHealth());
        }
    }

    // Equipement


    public static void applySlot(Mob entity, EquipmentSlot slot, @Nullable Item item) {
        if (item == null) {
            entity.setItemSlot(slot, ItemStack.EMPTY);
            return;
        }
        entity.setItemSlot(slot, new ItemStack(item));
        entity.setDropChance(slot, 0.0F);
    }

    // Skin / texture

    public static void syncProfileToClient(
            SynchedEntityData entityData,
            EntityDataAccessor<String> customSkinAccessor,
            @Nullable ArchetypeProfile profile) {

        if (profile != null && profile.customSkin != null) {
            entityData.set(customSkinAccessor, profile.customSkin.toString());
        } else {
            entityData.set(customSkinAccessor, "");
        }
    }

    public static ResourceLocation getTextureLocation(
            SynchedEntityData entityData,
            EntityDataAccessor<String> customSkinAccessor,
            EntityDataAccessor<String> biomeFolderAccessor,
            EntityDataAccessor<Integer> skinVariantAccessor,
            String prefix,
            ResourceLocation fallback) {

        String skinStr = entityData.get(customSkinAccessor);
        if (!skinStr.isEmpty()) {
            return ResourceLocation.parse(skinStr);
        }
        String folder = entityData.get(biomeFolderAccessor);
        int variant = entityData.get(skinVariantAccessor);

        if (folder == null || folder.isEmpty()) {
            return fallback;
        }
        return ResourceLocation.fromNamespaceAndPath(
                AcolyteMod.MOD_ID,
                "textures/entity/generic_skin/" + folder + "/"+ prefix + variant + ".png"
        );
    }
}