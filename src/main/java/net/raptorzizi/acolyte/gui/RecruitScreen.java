package net.raptorzizi.acolyte.gui;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.raptorzizi.acolyte.AcolyteMod;
import net.raptorzizi.acolyte.network.NetworkHandler;
import net.raptorzizi.acolyte.network.PacketConfirmRecruit;


@OnlyIn(Dist.CLIENT)
public class RecruitScreen extends AbstractContainerScreen<RecruitMenu> {

    private static final ResourceLocation SCREEN_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AcolyteMod.MOD_ID,
                    "textures/gui/recruit_screen.png");

    private static final int GUI_W = 176;
    private static final int GUI_H = 232;
    private static final int GUI_H_TOTAL = 280;

    private static final int PORTRAIT_X    = 7;
    private static final int PORTRAIT_Y    = 17;
    private static final int PORTRAIT_W = 48;
    private static final int PORTRAIT_H = 64;

    private static final int STATS_X      = PORTRAIT_X + PORTRAIT_W + 6;
    private static final int STAT_HP_Y    = 17;
    private static final int STAT_ATK_Y   = 37;
    private static final int STAT_ARMOR_Y = 57;
    private static final int STAT_DURATION_Y = 77;
    private static final int STAT_SPELL_Y = 17;

    private static final int COLOR_TITLE  = 0x404040;
    private static final int COLOR_LABEL  = 0x6B4F2A;
    private static final int COLOR_VALUE  = 0x1A1A1A;

    private static final int BAR_X_OFFSET = 7;
    private static final int BAR_W        = 48;
    private static final int BAR_H        = 15;
    private static final int COLOR_BAR_BG  = 0xFF2A1F14;
    private static final int COLOR_BAR_HI  = 0xFF4CAF50;
    private static final int COLOR_BAR_MID = 0xFFFFC107;
    private static final int COLOR_BAR_LOW = 0xFFF44336;

    public RecruitScreen(RecruitMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth  = GUI_W;
        this.imageHeight = GUI_H;
    }

    @Override
    protected void init() {
        super.init();

        if (!menu.isRecruited) {
            Button recruitButton = new TexturedButton(
                    this.leftPos + 7, this.topPos + 107, 161, 16,
                    Component.translatable("gui.acolyte.recruit.contract_cost"),
                    btn -> onRecruitClicked(), 0, 248, 0, 264, 161, 16, 176, 280
            );
            this.addRenderableWidget(recruitButton);
        } else {
            Button followButton = new TexturedButton(
                    this.leftPos + 7, this.topPos + 107, 48, 16,
                    Component.translatable("gui.acolyte.recruit.follow"),
                    btn -> onOrderClicked(false), 0, 232, 48, 232, 48, 16, 176, 280);
            this.addRenderableWidget(followButton);

            Button stayButton = new TexturedButton(
                    this.leftPos + 62, this.topPos + 107, 48, 16,
                    Component.translatable("gui.acolyte.recruit.stay"),
                    btn -> onOrderClicked(true), 0, 232, 48, 232, 48, 16, 176, 280
            );
            this.addRenderableWidget(stayButton);

            Button unrecruitButton = new TexturedButton(
                    this.leftPos + 117, this.topPos + 107, 48, 16,
                    Component.translatable("gui.acolyte.recruit.unrecruit").withStyle(net.minecraft.ChatFormatting.RED),
                    btn -> onUnrecruitClicked(), 0, 232, 48, 232, 48, 16, 176, 280
            );
            this.addRenderableWidget(unrecruitButton);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        guiGraphics.blit(SCREEN_TEXTURE, x, y, 0, 0, GUI_W, GUI_H, GUI_W, GUI_H_TOTAL);

        if (this.minecraft != null && this.minecraft.level != null) {
            net.minecraft.world.entity.Entity entity =
                    this.minecraft.level.getEntity(menu.entityId);
            if (entity instanceof LivingEntity living) {
                int portraitCenterX = x + PORTRAIT_X + PORTRAIT_W / 2;
                int scale = 24;
                InventoryScreen.renderEntityInInventoryFollowsMouse(
                        guiGraphics,
                        portraitCenterX - PORTRAIT_W / 2,
                        y + PORTRAIT_Y,
                        portraitCenterX + PORTRAIT_W / 2,
                        y + PORTRAIT_Y + PORTRAIT_H,
                        scale,
                        0.0625f,
                        mouseX, mouseY,
                        living
                );
            }
        }

        int nameW = this.font.width(menu.displayName);
        guiGraphics.drawString(this.font,
                menu.displayName,
                x + (GUI_W - nameW) / 2, y + 7,
                COLOR_TITLE, false);

        renderStats(guiGraphics, x, y);
        renderSpellIcons(guiGraphics, x, y);

        if (!menu.isRecruited) {
            renderHireCost(guiGraphics);
        } else {
            renderContractZone(guiGraphics, x, y);
        }
    }


    private void renderStats(GuiGraphics guiGraphics, int x, int y) {
        int sx = x + STATS_X;

        guiGraphics.drawString(this.font,
                Component.translatable("gui.acolyte.recruit.stat.hp"),
                sx + 18, y + STAT_HP_Y + 1, COLOR_LABEL, false);
        guiGraphics.drawString(this.font,
                String.format("%.1f", menu.health),
                sx + 18, y + STAT_HP_Y + 10, COLOR_VALUE, false);

        guiGraphics.drawString(this.font,
                Component.translatable("gui.acolyte.recruit.stat.attack"),
                sx + 18, y + STAT_ATK_Y + 1, COLOR_LABEL, false);
        guiGraphics.drawString(this.font,
                String.format("%.1f", menu.attackDamage),
                sx + 18, y + STAT_ATK_Y + 10, COLOR_VALUE, false);

        guiGraphics.drawString(this.font,
                Component.translatable("gui.acolyte.recruit.stat.armor"),
                sx + 18, y + STAT_ARMOR_Y + 1, COLOR_LABEL, false);
        guiGraphics.drawString(this.font,
                String.format("%.0f", menu.armor),
                sx + 18, y + STAT_ARMOR_Y + 10, COLOR_VALUE, false);

        guiGraphics.drawString(this.font,
                Component.translatable("gui.acolyte.recruit.contract_duration"),
                sx + 18, y + STAT_DURATION_Y + 1, COLOR_LABEL, false);

        String timeText;
        if (menu.isRecruited) {
            timeText = formatTicksToTime(menu.remainingTicks);
        } else {
            timeText = formatSimplifiedDuration(menu.totalDurationTicks);
        }

        guiGraphics.drawString(this.font, timeText, sx + 18, y + STAT_DURATION_Y + 10, COLOR_VALUE, false);

        guiGraphics.drawString(this.font,
                Component.translatable("gui.acolyte.recruit.stat.spells"),
                x + 130, y + STAT_SPELL_Y + 1, COLOR_LABEL, false);

    }

    private String formatSimplifiedDuration(long ticks) {
        int mcDays = (int) (ticks / 24000);

        if (mcDays >= 1) {
            return mcDays + " " + (mcDays > 1 ? "days" : "day");
        } else {
            return (ticks / 1200) + " mins";
        }
    }

    private String formatTicksToTime(long ticks) {
        long totalSeconds = ticks / 20;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void renderHireCost(GuiGraphics guiGraphics) {
        int itemX = this.leftPos + 140;
        int itemY = this.topPos + 107;

        ItemStack emerald = new ItemStack(Items.EMERALD, menu.recruitCost);
        guiGraphics.renderItem(emerald, itemX, itemY);
        guiGraphics.renderItemDecorations(this.font, emerald, itemX, itemY);
    }

    private void renderSpellIcons(GuiGraphics guiGraphics, int x, int y) {
        var spellIds = menu.spellIds;
        for (int i = 0; i < Math.min(spellIds.size(), 8); i++) {
            int row  = i / 2;
            int col  = i % 2;
            int sx   = x + 131 + col * 18 ;
            int sy   = y + 28  + row * 18 ;

            var spell = SpellRegistry
                    .getSpell(spellIds.get(i));
            if (spell != null && spell != SpellRegistry.none()) {
                guiGraphics.blit(
                        spell.getSpellIconResource(),
                        sx, sy,
                        0, 0, 16, 16, 16, 16
                );
            }
        }
    }

    private void renderContractZone(GuiGraphics guiGraphics, int x, int y) {
        int barX  = x + BAR_X_OFFSET;
        int barY  = y + 87;
        float prog = Math.max(0f, Math.min(1f, menu.contractProgress));

        guiGraphics.fill(barX, barY, barX + BAR_W, barY + BAR_H, COLOR_BAR_BG);

        int fillColor = prog > 0.5f ? COLOR_BAR_HI
                : prog > 0.25f ? COLOR_BAR_MID
                : COLOR_BAR_LOW;
        int fillW = (int)(BAR_W * prog);
        if (fillW > 0) {
            guiGraphics.fill(barX, barY, barX + fillW, barY + BAR_H, fillColor);
        }

        guiGraphics.renderOutline(barX, barY, BAR_W, BAR_H, 0xFF000000);
        String pct  = String.format("%d%%", (int)(prog * 100));
        int    pctW = this.font.width(pct);
        guiGraphics.drawString(this.font, pct,
                barX + (BAR_W - pctW) / 2, barY + 4, 0xFFFFFF, true);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

    private void onRecruitClicked() {
        NetworkHandler.sendToServer(new PacketConfirmRecruit(menu.entityId, false, false));
        this.onClose();
    }

    private void onOrderClicked(boolean stay) {
        NetworkHandler.sendToServer(new PacketConfirmRecruit(menu.entityId, true, stay));
        this.onClose();
    }

    private void onUnrecruitClicked() {
        NetworkHandler.sendToServer(PacketConfirmRecruit.unrecruit(menu.entityId));
        this.onClose();
    }

    private class TexturedButton extends Button {

        private final int texNormalX, texNormalY;
        private final int texHoverX,  texHoverY;
        private final int texW, texH;
        private final int texTotalW, texTotalH;

        protected TexturedButton(int x, int y, int w, int h, Component text, OnPress onPress,
                                 int texNormalX, int texNormalY,
                                 int texHoverX,  int texHoverY,
                                 int texW, int texH,
                                 int texTotalW, int texTotalH) {
            super(x, y, w, h, text, onPress, DEFAULT_NARRATION);
            this.texNormalX = texNormalX; this.texNormalY = texNormalY;
            this.texHoverX  = texHoverX;  this.texHoverY  = texHoverY;
            this.texW       = texW;       this.texH       = texH;
            this.texTotalW  = texTotalW;  this.texTotalH  = texTotalH;
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int u = this.isHovered ? texHoverX : texNormalX;
            int v = this.isHovered ? texHoverY : texNormalY;

            guiGraphics.blit(SCREEN_TEXTURE,
                    getX(), getY(), width, height,  // destination
                    u, v, texW, texH,               // source
                    texTotalW, texTotalH);

            guiGraphics.drawCenteredString(
                    net.minecraft.client.Minecraft.getInstance().font,
                    getMessage(),
                    getX() + width / 2,
                    getY() + (height - 8) / 2,
                    0xFFFFFF
            );
        }
    }
}