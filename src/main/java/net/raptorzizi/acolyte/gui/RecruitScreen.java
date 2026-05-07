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

    private static final int GUI_W = 200;
    private static final int GUI_H = 217;
    private static final int GUI_H_TOTAL = 335;

    private static final int PORTRAIT_X = 7;
    private static final int PORTRAIT_Y = 17;
    private static final int PORTRAIT_W = 48;
    private static final int PORTRAIT_H = 64;

    private static final int FRAME_W = 50;
    private static final int FRAME_H = 70;
    private static final int FRAME_TEX_Y = 265;

    private static final int STATS_X      = PORTRAIT_X + PORTRAIT_W + 6;
    private static final int STAT_HP_Y    = 17;
    private static final int STAT_ARMOR_Y = 37;
    private static final int STAT_DURATION_Y = 57;
    private static final int STAT_SPELL_Y = 17;

    private static final int COLOR_TITLE  = 0x404040;
    private static final int COLOR_LABEL  = 0x6B4F2A;
    private static final int COLOR_VALUE  = 0x1A1A1A;


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
                    this.leftPos + 7, this.topPos + 92, 161, 16,
                    Component.translatable("gui.acolyte.recruit.contract_cost"),
                    btn -> onRecruitClicked(), 0, 233, 0, 249, 161, 16, 200, 335
            );
            this.addRenderableWidget(recruitButton);
        } else {
            Button followButton = new TexturedButton(
                    this.leftPos + 7, this.topPos + 92, 48, 16,
                    Component.translatable("gui.acolyte.recruit.follow"),
                    btn -> onOrderClicked(false), 0, 217, 48, 217, 48, 16, 200, 335);
            this.addRenderableWidget(followButton);

            Button stayButton = new TexturedButton(
                    this.leftPos + 62, this.topPos + 92, 48, 16,
                    Component.translatable("gui.acolyte.recruit.stay"),
                    btn -> onOrderClicked(true), 0, 217, 48, 217, 48, 16, 200, 335
            );
            this.addRenderableWidget(stayButton);

            Button unrecruitButton = new TexturedButton(
                    this.leftPos + 117, this.topPos + 92, 48, 16,
                    Component.translatable("gui.acolyte.recruit.unrecruit"),
                    btn -> onUnrecruitClicked(), 0, 217, 48, 217, 48, 16, 200, 335
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

        renderTierFrame(guiGraphics, x, y);

        int nameW = this.font.width(menu.displayName);
        guiGraphics.drawString(this.font,
                menu.displayName,
                x + (GUI_W - nameW) / 2, y + 7,
                COLOR_TITLE, false);

        renderStats(guiGraphics, x, y);
        renderSpellIcons(guiGraphics, x, y);

        if (!menu.isRecruited) {
            renderHireCost(guiGraphics);
        }
    }


    private void renderTierFrame(GuiGraphics guiGraphics, int x, int y) {
        int tier = Math.max(1, Math.min(4, menu.tier));
        int texU = (tier - 1) * FRAME_W;
        int frameX = x + PORTRAIT_X + (PORTRAIT_W - FRAME_W) / 2;
        int frameY = y + PORTRAIT_Y + (PORTRAIT_H - FRAME_H) / 2;
        guiGraphics.blit(SCREEN_TEXTURE, frameX, frameY, FRAME_W, FRAME_H,
                texU, FRAME_TEX_Y, FRAME_W, FRAME_H, GUI_W, GUI_H_TOTAL);
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
        int itemY = this.topPos + 92;

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


    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

    private void onRecruitClicked() {
        NetworkHandler.sendToServer(PacketConfirmRecruit.recruit(menu.entityId));
        this.onClose();
    }

    private void onOrderClicked(boolean stay) {
        NetworkHandler.sendToServer(PacketConfirmRecruit.order(menu.entityId, stay));
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
                    getX(), getY(), width, height,
                    u, v, texW, texH,
                    texTotalW, texTotalH);

            net.minecraft.client.gui.Font font = net.minecraft.client.Minecraft.getInstance().font;
            int textX = getX() + width / 2 - font.width(getMessage()) / 2;
            int textY = getY() + (height - 8) / 2;
            guiGraphics.drawString(font, getMessage(), textX, textY, 0x000000, false);
        }
    }
}