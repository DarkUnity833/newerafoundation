package net.darkunity.newerafoundation.inventory;

import net.darkunity.newerafoundation.block.entity.StorageCoreBlockEntity;
import net.darkunity.newerafoundation.storage.StorageNetwork;
import net.minecraft.core.BlockPos;
import net.darkunity.newerafoundation.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Терминал хранилища: 54 витринных слота (страница) + инвентарь игрока. Вся логика кликов на сервере. */
public class StorageMenu extends AbstractContainerMenu {
    public static final int ROWS = 6, COLS = 9, VIEW = ROWS * COLS;

    private final StorageCoreBlockEntity core; // null на клиенте
    // SimpleContainer clamps stack counts to getMaxStackSize() (64), so lift that limit for the showcase
    private final SimpleContainer view = new SimpleContainer(VIEW) {
        @Override public int getMaxStackSize() { return Integer.MAX_VALUE; }
        @Override public int getMaxStackSize(ItemStack stack) { return Integer.MAX_VALUE; } // setItem() limits by this one (item max, e.g. 64)
    };
    private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 3, 3);
    private final ResultContainer craftResult = new ResultContainer();
    private final List<ItemStack> shown = new ArrayList<>(); // эталоны (count 1) для витринных слотов
    private final DataSlot page = DataSlot.standalone();
    private final DataSlot pages = DataSlot.standalone();
    // DataSlot values travel as 16-bit shorts, so wide numbers are split into 16-bit pieces.
    private final DataSlot[] total = {DataSlot.standalone(), DataSlot.standalone(), DataSlot.standalone(), DataSlot.standalone()};
    private final DataSlot[] capacity = {DataSlot.standalone(), DataSlot.standalone(), DataSlot.standalone(), DataSlot.standalone()};
    private int quickCraftGuard;
    private final DataSlot sortMode = DataSlot.standalone();     // 0 = name, 1 = count, 2 = source
    private final DataSlot sortReversed = DataSlot.standalone(); // 0/1
    private String search = "";
    private final java.util.Map<net.minecraft.world.item.Item, String> nameCache = new java.util.HashMap<>();

    /** Showcase slot. On the client it reports a count of 1 (so vanilla draws no number); the screen draws the real count itself. */
    public static class ViewSlot extends Slot {
        private final boolean client;
        public ViewSlot(Container c, int index, int x, int y, boolean client) { super(c, index, x, y); this.client = client; }
        @Override public boolean mayPlace(ItemStack s) { return false; }
        @Override public boolean mayPickup(Player p) { return false; }
        @Override public ItemStack getItem() {
            ItemStack s = super.getItem();
            return client && s.getCount() != 1 ? s.copyWithCount(1) : s;
        }
        /** Real amount stored in the network for this entry (clamped to int by the stack). */
        public long realCount() { return super.getItem().getCount(); }
    }

    private static void putLong(DataSlot[] ds, long v) { for (int i = 0; i < 4; i++) ds[i].set((int) ((v >>> (16 * i)) & 0xFFFFL)); }
    private static long getLong(DataSlot[] ds) { long v = 0; for (int i = 0; i < 4; i++) v |= (ds[i].get() & 0xFFFFL) << (16 * i); return v; }

    public StorageMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, resolveCore(inv, buf));
    }

    private static StorageCoreBlockEntity resolveCore(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        return inv.player.level().getBlockEntity(pos) instanceof StorageCoreBlockEntity core ? core : null;
    }

    public StorageMenu(int id, Inventory inv, StorageCoreBlockEntity core) {
        super(ModMenuTypes.STORAGE_MENU.get(), id);
        this.core = core;
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                this.addSlot(new ViewSlot(view, c + r * COLS, 8 + c * 18, 18 + r * 18, inv.player.level().isClientSide));
        // Crafting is integrated into the same terminal screen, to the right of the catalog.
        this.addSlot(new ResultSlot(inv.player, craftSlots, craftResult, 0, 246, 62));
        for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) {
            this.addSlot(new Slot(craftSlots, c + r * 3, 190 + c * 18, 26 + r * 18) {
                @Override public void setChanged() { super.setChanged(); StorageMenu.this.slotsChanged(this.container); }
            });
        }
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++) this.addSlot(new Slot(inv, c + r * 9 + 9, 8 + c * 18, 140 + r * 18));
        for (int c = 0; c < 9; c++) this.addSlot(new Slot(inv, c, 8 + c * 18, 198));
        sortMode.set(1);
        addDataSlot(sortMode);
        addDataSlot(sortReversed);
        addDataSlot(page);
        addDataSlot(pages);
        for (DataSlot d : total) addDataSlot(d);
        for (DataSlot d : capacity) addDataSlot(d);
        refresh();
    }

    public int getSortMode() { return sortMode.get(); }
    public boolean isSortReversed() { return sortReversed.get() != 0; }
    public int getPage() { return page.get(); }
    public int getPages() { return Math.max(1, pages.get()); }
    public long getTotal() { return getLong(total); }
    public long getCapacity() { return getLong(capacity); }

    public void setSearch(String text) {
        String t = text == null ? "" : text.strip();
        if (t.length() > 64) t = t.substring(0, 64);
        t = t.toLowerCase(java.util.Locale.ROOT);
        if (!t.equals(search)) { search = t; page.set(0); }
    }

    private String nameOf(ItemStack proto) {
        return nameCache.computeIfAbsent(proto.getItem(), i -> proto.getHoverName().getString().toLowerCase(java.util.Locale.ROOT));
    }

    private static String namespaceOf(ItemStack proto) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(proto.getItem()).getNamespace();
    }

    private static String modNameOf(String ns) {
        return net.neoforged.fml.ModList.get().getModContainerById(ns).map(c -> c.getModInfo().getDisplayName()).orElse(ns).toLowerCase(java.util.Locale.ROOT);
    }

    /** Search like JEI: plain words match the item name/id, "@text" matches the mod id or mod name ("@minecraft", "@foundation"). */
    private List<StorageNetwork.CatalogEntry> filterAndSort(List<StorageNetwork.CatalogEntry> all) {
        List<String> words = new ArrayList<>(), mods = new ArrayList<>();
        for (String tok : search.split("\\s+")) {
            if (tok.isEmpty()) continue;
            if (tok.startsWith("@")) { if (tok.length() > 1) mods.add(tok.substring(1)); }
            else words.add(tok);
        }
        List<StorageNetwork.CatalogEntry> out = new ArrayList<>();
        for (StorageNetwork.CatalogEntry e : all) {
            if (!words.isEmpty()) {
                String name = nameOf(e.prototype);
                String id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(e.prototype.getItem()).getPath();
                boolean ok = true;
                for (String w : words) if (!name.contains(w) && !id.contains(w)) { ok = false; break; }
                if (!ok) continue;
            }
            if (!mods.isEmpty()) {
                String ns = namespaceOf(e.prototype);
                String modName = modNameOf(ns);
                boolean ok = true;
                for (String m : mods) if (!ns.contains(m) && !modName.contains(m)) { ok = false; break; }
                if (!ok) continue;
            }
            out.add(e);
        }
        Comparator<StorageNetwork.CatalogEntry> byName = Comparator.comparing((StorageNetwork.CatalogEntry e) -> nameOf(e.prototype));
        Comparator<StorageNetwork.CatalogEntry> cmp;
        switch (sortMode.get()) {
            case 0 -> cmp = byName;
            case 2 -> cmp = Comparator.comparingInt((StorageNetwork.CatalogEntry e) -> namespaceOf(e.prototype).equals("minecraft") ? 0 : 1)
                    .thenComparing(e -> namespaceOf(e.prototype)).thenComparing(byName);
            default -> cmp = Comparator.comparingLong((StorageNetwork.CatalogEntry e) -> e.count).reversed().thenComparing(byName);
        }
        if (sortReversed.get() != 0) cmp = cmp.reversed();
        out.sort(cmp);
        return out;
    }

    private void refresh() {
        if (core == null || core.getLevel() == null || core.getLevel().isClientSide) return;
        List<StorageNetwork.CatalogEntry> sorted = filterAndSort(StorageNetwork.getCatalog(core.getLevel(), core.getBlockPos()));
        int rowCount = Math.max(1, (sorted.size() + COLS - 1) / COLS);
        int maxOffset = Math.max(0, rowCount - ROWS);
        if (page.get() > maxOffset) page.set(maxOffset);
        if (page.get() < 0) page.set(0);
        pages.set(maxOffset + 1);
        putLong(total, StorageNetwork.countItems(core.getLevel(), core.getBlockPos()));
        putLong(capacity, StorageNetwork.totalCapacity(core.getLevel(), core.getBlockPos()));
        shown.clear();
        int start = page.get() * COLS;
        for (int i = 0; i < VIEW; i++) {
            int idx = start + i;
            if (idx < sorted.size()) {
                StorageNetwork.CatalogEntry e = sorted.get(idx);
                shown.add(e.prototype);
                // the stack carries the full amount (not limited to the max stack size); the client shows 1K/1M/... and the exact value in the tooltip
                view.setItem(i, e.prototype.copyWithCount((int) Math.min(e.count, Integer.MAX_VALUE)));
            } else {
                shown.add(ItemStack.EMPTY);
                view.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container != craftSlots || core == null || core.getLevel() == null || core.getLevel().isClientSide) return;
        if (core.getLevel() instanceof ServerLevel serverLevel) {
            var input = craftSlots.asCraftInput();
            var recipe = serverLevel.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, serverLevel);
            ItemStack result = recipe.map(holder -> holder.value().assemble(input, serverLevel.registryAccess())).orElse(ItemStack.EMPTY);
            craftResult.setItem(0, result);
        }
    }

    @Override
    public void broadcastChanges() {
        refresh();
        super.broadcastChanges();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) page.set(page.get() - 1);
        else if (id == 1) page.set(page.get() + 1);
        else if (id >= 10 && id <= 12) {
            int mode = id - 10;
            if (sortMode.get() == mode) sortReversed.set(sortReversed.get() == 0 ? 1 : 0);
            else { sortMode.set(mode); sortReversed.set(0); }
            page.set(0);
        }
        else return false;
        refresh();
        return true;
    }

    @Override
    public void clicked(int slotId, int button, ClickType type, Player player) {
        quickCraftGuard = 0;
        boolean isView = slotId >= 0 && slotId < VIEW;
        boolean shiftOnPlayer = slotId >= VIEW + 10 && type == ClickType.QUICK_MOVE;
        if (!isView && !shiftOnPlayer) { super.clicked(slotId, button, type, player); return; }
        if (core == null || player.level().isClientSide) return; // клиент ждёт синхронизации с сервера

        if (isView) {
            ItemStack carried = getCarried();
            if (!carried.isEmpty()) {
                // положить в хранилище: ЛКМ - всё, ПКМ - один предмет
                if (button == 0) {
                    setCarried(StorageNetwork.insert(player.level(), core.getBlockPos(), carried, false));
                } else {
                    ItemStack one = carried.copyWithCount(1);
                    if (StorageNetwork.insert(player.level(), core.getBlockPos(), one, false).isEmpty()) carried.shrink(1);
                }
            } else {
                ItemStack proto = shown.get(slotId);
                if (!proto.isEmpty()) {
                    if (type == ClickType.QUICK_MOVE) {
                        ItemStack out = StorageNetwork.extract(player.level(), core.getBlockPos(), proto, proto.getMaxStackSize(), false);
                        if (!out.isEmpty()) {
                            moveItemStackTo(out, VIEW + 10, slots.size(), true);
                            if (!out.isEmpty()) StorageNetwork.insert(player.level(), core.getBlockPos(), out, false); // вернуть то, что не влезло в инвентарь
                        }
                    } else {
                        int want = button == 0 ? proto.getMaxStackSize() : Math.max(1, proto.getMaxStackSize() / 2);
                        setCarried(StorageNetwork.extract(player.level(), core.getBlockPos(), proto, want, false));
                    }
                }
            }
        } else {
            Slot s = slots.get(slotId);
            if (s.hasItem()) {
                ItemStack st = s.getItem();
                s.set(StorageNetwork.insert(player.level(), core.getBlockPos(), st, false));
            }
        }
        refresh();
        broadcastChanges();
    }

    private boolean hasRoomInInventory(ItemStack stack) {
        int room = 0;
        for (int i = VIEW + 10; i < slots.size(); i++) {
            ItemStack in = slots.get(i).getItem();
            if (in.isEmpty()) room += Math.min(stack.getMaxStackSize(), slots.get(i).getMaxStackSize(stack));
            else if (ItemStack.isSameItemSameComponents(in, stack)) room += Math.max(0, Math.min(in.getMaxStackSize(), slots.get(i).getMaxStackSize(stack)) - in.getCount());
            if (room >= stack.getCount()) return true;
        }
        return false;
    }

    /**
     * Shift+click on the crafting result crafts one batch and moves it into the player's inventory. The vanilla click loop
     * calls this repeatedly while the same item keeps coming out, which crafts "everything" the grid allows.
     * Crafting stops (without losing items) as soon as the inventory has no room for the next batch.
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (player.level().isClientSide || index < VIEW || index >= VIEW + 10) return ItemStack.EMPTY;
        int invStart = VIEW + 10, invEnd = slots.size();
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack before = stack.copy();
        if (index == VIEW) {
            if (++quickCraftGuard > 4096 || !hasRoomInInventory(stack)) return ItemStack.EMPTY;
            stack.getItem().onCraftedBy(stack, player.level(), player);
            if (!moveItemStackTo(stack, invStart, invEnd, true)) return ItemStack.EMPTY;
            slot.onQuickCraft(stack, before);
        } else if (!moveItemStackTo(stack, invStart, invEnd, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        if (stack.getCount() == before.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return before;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide) {
            for (int i = 0; i < craftSlots.getContainerSize(); i++) {
                ItemStack stack = craftSlots.removeItemNoUpdate(i);
                if (!stack.isEmpty()) player.getInventory().placeItemBackInInventory(stack);
            }
            craftResult.clearContent();
        }
    }

    @Override public boolean stillValid(Player player) {
        return core == null || (!core.isRemoved() && player.distanceToSqr(
                core.getBlockPos().getX() + .5, core.getBlockPos().getY() + .5, core.getBlockPos().getZ() + .5) < 4096);
    }
}
