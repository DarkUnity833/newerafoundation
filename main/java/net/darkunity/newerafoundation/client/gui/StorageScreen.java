package net.darkunity.newerafoundation.client.gui;

import net.darkunity.newerafoundation.inventory.StorageMenu;
import net.darkunity.newerafoundation.storage.CountFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.neoforged.neoforge.network.PacketDistributor;
import net.darkunity.newerafoundation.network.StorageSearchPayload;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.client.gui.screens.Screen;

public class StorageScreen extends AbstractContainerScreen<StorageMenu> {
    private static final ResourceLocation SLOTS = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    public StorageScreen(StorageMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 272;
        this.imageHeight = 222;
        this.inventoryLabelY = 129;
    }

    private boolean draggingScrollbar;

    private EditBox searchBox;
    private String searchText = "";
    private final Button[] sortButtons = new Button[3];

    /** Translated text if the language file has the key, otherwise a built-in fallback (en / ru). */
    private static Component tr(String key, String en, String ru, Object... args) {
        if (I18n.exists(key)) return Component.translatable(key, args);
        String code = Minecraft.getInstance().getLanguageManager().getSelected();
        String text = code != null && code.startsWith("ru") ? ru : en;
        return Component.literal(args.length > 0 ? String.format(text, args) : text);
    }

    @Override
    protected void init() {
        super.init();
        searchBox = new EditBox(font, leftPos + 190, topPos + 104, 74, 12, Component.literal("search"));
        searchBox.setMaxLength(64);
        searchBox.setValue(searchText);
        searchBox.setHint(tr("gui.newerafoundation.search_hint", "Search  @mod", "Поиск  @мод").copy().withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
        searchBox.setResponder(text -> {
            searchText = text;
            PacketDistributor.sendToServer(new StorageSearchPayload(menu.containerId, text));
        });
        addRenderableWidget(searchBox);
        if (!searchText.isEmpty()) PacketDistributor.sendToServer(new StorageSearchPayload(menu.containerId, searchText));

        String[][] tips = {
            {"Sort by name (click again to reverse)", "Сортировка по названию (повторный клик - в обратном порядке)"},
            {"Sort by amount (click again to reverse)", "Сортировка по количеству (повторный клик - в обратном порядке)"},
            {"Sort by source: Minecraft / mods (click again to reverse)", "Сортировка по источнику: Minecraft / моды (повторный клик - в обратном порядке)"}};
        for (int i = 0; i < 3; i++) {
            final int id = 10 + i;
            sortButtons[i] = Button.builder(Component.empty(), b -> click(id))
                    .bounds(leftPos - 30, topPos + 18 + i * 18, 28, 16)
                    .tooltip(Tooltip.create(tr("gui.newerafoundation.sort_" + i, tips[i][0], tips[i][1])))
                    .build();
            addRenderableWidget(sortButtons[i]);
        }
        updateSortButtons();
    }

    private void updateSortButtons() {
        boolean rev = menu.isSortReversed();
        String[] labels = {rev ? "Z-A" : "A-Z", rev ? "1-9" : "9-1", rev ? "Mod" : "MC"};
        for (int i = 0; i < 3; i++) {
            if (sortButtons[i] == null) continue;
            Component label = Component.literal(labels[i]);
            sortButtons[i].setMessage(menu.getSortMode() == i ? label.copy().withStyle(net.minecraft.ChatFormatting.YELLOW) : label);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateSortButtons();
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        if (scrollY != 0 && menu.getPages() > 1 && mx >= leftPos && mx <= leftPos + 190) {
            click(scrollY > 0 ? 0 : 1);
            return true;
        }
        return super.mouseScrolled(mx, my, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (searchBox != null && searchBox.isFocused() && key != 256) {
            searchBox.keyPressed(key, scan, mods);
            return true; // keep inventory/hotbar keys from firing while typing
        }
        return super.keyPressed(key, scan, mods);
    }

    private void click(int id) {
        if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
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
        // Vertical scrollbar: the catalog advances one row at a time instead of horizontal page tabs.
        int trackX = x + 174, trackY = y + 20, trackH = 108;
        g.fill(trackX, trackY, trackX + 6, trackY + trackH, 0xFF777777);
        g.fill(trackX + 1, trackY + 1, trackX + 5, trackY + trackH - 1, 0xFFB0B0B0);
        int range = Math.max(0, menu.getPages() - 1);
        int thumbY = trackY + (range == 0 ? 0 : (trackH - 18) * menu.getPage() / range);
        g.fill(trackX - 1, thumbY, trackX + 7, thumbY + 18, 0xFF404040);
        g.fill(trackX, thumbY + 1, trackX + 6, thumbY + 17, 0xFFD8D8D8);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mx, int my) {
        g.drawString(font, tr("block.newerafoundation.storage_terminal", "Storage Terminal", "Терминал хранения"), 8, 6, 0x404040, false);
        g.drawString(font, "Crafting", 190, 6, 0x404040, false);
        String total = CountFormat.exact(menu.getTotal()) + " " + tr("gui.newerafoundation.items", "items", "предм.").getString();
        g.drawString(font, total, 8, 129, 0x404040, false);
        renderCounts(g);
    }

    /** Draws 1-999 / 1K-999K / 1M-999M... on top of the showcase items (vanilla's number is hidden for these slots). */
    private void renderCounts(GuiGraphics g) {
        for (Slot slot : menu.slots) {
            if (!(slot instanceof StorageMenu.ViewSlot vs)) continue;
            long real = vs.realCount();
            if (real <= 1) continue;
            String text = CountFormat.compact(real);
            int w = font.width(text);
            float scale = w > 16 ? 16f / w : 1f;
            g.pose().pushPose();
            g.pose().translate(slot.x + 17 - w * scale, slot.y + 17 - 8 * scale, 300);
            g.pose().scale(scale, scale, 1f);
            g.drawString(font, text, 0, 0, 0xFFFFFF, true);
            g.pose().popPose();
        }
    }

    /** Hovering a showcase item shows the exact amount (1.923.652) instead of the abbreviated one. */
    @Override
    protected List<Component> getTooltipFromContainerItem(ItemStack stack) {
        List<Component> tip = new ArrayList<>(super.getTooltipFromContainerItem(stack));
        if (hoveredSlot instanceof StorageMenu.ViewSlot vs && vs.realCount() > 0)
            tip.add(tr("gui.newerafoundation.in_storage", "In storage: %s", "В хранилище: %s", CountFormat.exact(vs.realCount())).copy().withStyle(ChatFormatting.GRAY));
        return tip;
    }

    private boolean onScrollbar(double mouseY) {
        int trackY = topPos + 20, trackH = 108;
        int clamped = (int)Math.max(trackY, Math.min(trackY + trackH, mouseY));
        int desired = menu.getPages() <= 1 ? 0 : (int)Math.round((clamped - trackY) * (menu.getPages() - 1) / (double)trackH);
        if (desired < menu.getPage()) click(0);
        else if (desired > menu.getPage()) click(1);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= leftPos + 172 && mouseX <= leftPos + 184 && mouseY >= topPos + 18 && mouseY <= topPos + 130) {
            draggingScrollbar = true;
            onScrollbar(mouseY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingScrollbar && button == 0) { onScrollbar(mouseY); return true; }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && draggingScrollbar) { draggingScrollbar = false; return true; }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        super.render(g, mx, my, pt);
        this.renderTooltip(g, mx, my);
    }
}
