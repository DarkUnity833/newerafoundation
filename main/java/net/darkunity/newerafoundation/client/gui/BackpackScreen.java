package net.darkunity.newerafoundation.client.gui;

import net.darkunity.newerafoundation.inventory.BackpackMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {
    private static final ResourceLocation VANILLA_CONTAINER = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
    private static final int INTER_INV_GAP = 14; 

    public BackpackScreen(BackpackMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        
        int rows = menu.getTier().rows;
        int cols = menu.getTier().cols;

        this.imageWidth = Math.max(176, (cols * 18) + 16);
        this.imageHeight = 18 + (rows * 18) + INTER_INV_GAP + 94;

        this.titleLabelX = 8;
        this.titleLabelY = 6;
        
        // Убираем надпись "Инвентарь", выкидывая её координаты за экран
        this.inventoryLabelX = -1000;
        this.inventoryLabelY = -1000;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        // Рисуем наш кастомный серый фон
        renderCustomBackground(graphics, x, y, imageWidth, imageHeight);

        // Рисуем текстуру ячеек для каждого слота, который есть в меню
        // Это безопаснее, чем ручные циклы, так как координаты уже есть в Slot
        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.slots.get(i);
            // Берем текстуру одиночного слота из ванильной картинки
            graphics.blit(VANILLA_CONTAINER, x + slot.x - 1, y + slot.y - 1, 7, 17, 18, 18);
        }
    }

    private void renderCustomBackground(GuiGraphics graphics, int x, int y, int w, int h) {
        // Заливка основного цвета и создание эффекта объема (рамки)
        graphics.fill(x, y, x + w, y + h, 0xFFC6C6C6);
        graphics.fill(x, y, x + w, y + 1, 0xFFFFFFFF); // Верхняя белая линия
        graphics.fill(x, y, x + 1, y + h, 0xFFFFFFFF); // Левая белая линия
        graphics.fill(x + w - 1, y, x + w, y + h, 0xFF555555); // Правая темная
        graphics.fill(x, y + h - 1, x + w, y + h, 0xFF555555); // Нижняя темная
        graphics.fill(x + w - 2, y + 1, x + w - 1, y + h - 1, 0xFF8B8B8B); // Внутренняя тень справа
        graphics.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, 0xFF8B8B8B); // Внутренняя тень снизу
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        // Отрисовка затемнения фона (если нужно) и вызов renderBg/renderLabels
        super.render(graphics, mouseX, mouseY, partialTicks);
        // Отрисовка всплывающих подсказок над предметами
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}