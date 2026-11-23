package net.darkunity.newerafoundation.client.gui;

import net.darkunity.newerafoundation.inventory.VoidBarrelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class VoidBarrelScreen extends AbstractContainerScreen<VoidBarrelMenu> {
    private static final ResourceLocation SLOTS = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    public VoidBarrelScreen(VoidBarrelMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 133;
        this.inventoryLabelY = 40;
    }

    @Override
    protected void renderBg(GuiGraphics g, float pt, int mx, int my) {
        int x = leftPos, y = topPos, w = imageWidth, h = imageHeight;
        g.fill(x, y, x + w, y + h, 0xFFC6C6C6);
        g.fill(x, y, x + w, y + 1, 0xFFFFFFFF);
        g.fill(x, y, x + 1, y + h, 0xFFFFFFFF);
        g.fill(x + w - 1, y, x + w, y + h, 0xFF555555);
        g.fill(x, y + h - 1, x + w, y + h, 0xFF555555);
        for (Slot s : menu.slots) g.blit(SLOTS, x + s.x - 1, y + s.y - 1, 7, 17, 18, 18);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        super.render(g, mx, my, pt);
        this.renderTooltip(g, mx, my);
    }
}
