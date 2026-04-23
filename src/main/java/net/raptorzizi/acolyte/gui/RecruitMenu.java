package net.raptorzizi.acolyte.gui;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.raptorzizi.acolyte.entity.mobs.wizards.human.IRecruitableCompanion;
import net.raptorzizi.acolyte.registries.ModMenuRegistry;

import java.util.ArrayList;
import java.util.List;

public class RecruitMenu extends AbstractContainerMenu {

    public final int entityId;
    public final float health;
    public final float maxHealth;
    public final float attackDamage;
    public final float armor;
    public final boolean isRecruited;
    public final float contractProgress;
    public final String displayName;
    public final List<ResourceLocation> spellIds;
    public final int recruitCost;
    public final long remainingTicks;
    public final long totalDurationTicks;

    // Constructeur server
    public RecruitMenu(int containerId, Inventory playerInventory,
                       int entityId, float health, float maxHealth, float attackDamage, float armor,
                       boolean isRecruited, float contractProgress, String displayName, List<ResourceLocation> spellIds,
                       int recruitCost, long remainingTicks, long totalDurationTicks) {
        super(ModMenuRegistry.RECRUIT_MENU.get(), containerId);
        this.entityId         = entityId;
        this.health           = health;
        this.maxHealth        = maxHealth;
        this.attackDamage     = attackDamage;
        this.armor            = armor;
        this.isRecruited      = isRecruited;
        this.contractProgress = contractProgress;
        this.displayName      = displayName;
        this.spellIds = spellIds;
        this.recruitCost      = recruitCost;
        this.remainingTicks   = remainingTicks;
        this.totalDurationTicks = totalDurationTicks;
        addPlayerInventory(playerInventory);
    }

    // Constructeur client
    public RecruitMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(ModMenuRegistry.RECRUIT_MENU.get(), containerId);
        this.entityId         = buf.readInt();
        this.health           = buf.readFloat();
        this.maxHealth        = buf.readFloat();
        this.attackDamage     = buf.readFloat();
        this.armor            = buf.readFloat();
        this.isRecruited      = buf.readBoolean();
        this.contractProgress = buf.readFloat();
        this.displayName      = buf.readUtf();
        int count = buf.readInt();
        List<ResourceLocation> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) ids.add(buf.readResourceLocation());
        this.spellIds = ids;
        this.recruitCost      = buf.readInt();
        this.remainingTicks   = buf.readLong();
        this.totalDurationTicks = buf.readLong();
        addPlayerInventory(playerInventory);
    }

    // AbstractContainerMenu

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        150 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory,
                    col,
                    8 + col * 18,
                    208));
        }
    }
}