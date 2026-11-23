package net.darkunity.newerafoundation.storage;

import net.darkunity.newerafoundation.block.StorageAnalyzerBlock;
import net.darkunity.newerafoundation.block.StorageCableBlock;
import net.darkunity.newerafoundation.block.StorageCoreBlock;
import net.darkunity.newerafoundation.block.StorageTerminalBlock;
import net.darkunity.newerafoundation.block.entity.StorageCoreBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Graph traversal and inventory operations for the storage network. Items stay in their original inventories. */
public final class StorageNetwork {
    public static final int MAX_BLOCKS = 2048;
    private StorageNetwork() {}

    public record InventoryRef(BlockPos pos, Direction side, IItemHandler handler) {}
    public static final class CatalogEntry {
        public final ItemStack prototype;
        public long count;
        CatalogEntry(ItemStack stack, long count) { this.prototype = stack.copyWithCount(1); this.count = count; }
    }

    private static boolean isNetwork(BlockState state) {
        return state.getBlock() instanceof StorageCoreBlock || state.getBlock() instanceof StorageAnalyzerBlock
                || state.getBlock() instanceof StorageCableBlock || state.getBlock() instanceof StorageTerminalBlock;
    }

    /** Find the core by traversing only network components; storage containers are endpoints, never bridges. */
    public static StorageCoreBlockEntity findCore(Level level, BlockPos start) {
        ArrayDeque<BlockPos> queue = new ArrayDeque<>(); Set<BlockPos> seen = new HashSet<>();
        queue.add(start); seen.add(start); int visited = 0;
        while (!queue.isEmpty() && visited++ < MAX_BLOCKS) {
            BlockPos pos = queue.removeFirst();
            if (level.getBlockState(pos).getBlock() instanceof StorageCoreBlock && level.getBlockEntity(pos) instanceof StorageCoreBlockEntity core) return core;
            for (Direction d : Direction.values()) { BlockPos next = pos.relative(d); if (seen.add(next) && isNetwork(level.getBlockState(next))) queue.addLast(next); }
        }
        return null;
    }

    /** The analyzer must be directly attached to the core; the cable network starts on its far side. */
    public static boolean hasAnalyzer(Level level, BlockPos corePos) {
        if (level == null) return false;
        for (Direction d : Direction.values())
            if (level.getBlockState(corePos.relative(d)).getBlock() instanceof StorageAnalyzerBlock) return true;
        return false;
    }

    /** Inventory endpoints are accepted only if an analyzer is connected to the core's graph. */
    public static List<InventoryRef> getInventories(Level level, BlockPos corePos) {
        List<InventoryRef> result = new ArrayList<>();
        if (level == null || level.isClientSide) return result;
        ArrayDeque<BlockPos> queue = new ArrayDeque<>(); Set<BlockPos> seen = new HashSet<>(); Set<BlockPos> endpoints = new HashSet<>();
        if (!hasAnalyzer(level, corePos)) return result;
        for (Direction d : Direction.values()) {
            BlockPos analyzerPos = corePos.relative(d);
            if (level.getBlockState(analyzerPos).getBlock() instanceof StorageAnalyzerBlock && seen.add(analyzerPos)) queue.addLast(analyzerPos);
        }
        boolean analyzerFound = !queue.isEmpty(); int visited = 0;
        while (!queue.isEmpty() && visited++ < MAX_BLOCKS) {
            BlockPos pos = queue.removeFirst(); BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof StorageAnalyzerBlock || state.getBlock() instanceof StorageCableBlock) {
                for (Direction d : Direction.values()) {
                    BlockPos target = pos.relative(d); BlockState targetState = level.getBlockState(target);
                    if (targetState.getBlock() instanceof StorageCoreBlock) continue;
                    if (isNetwork(targetState)) { if (seen.add(target)) queue.addLast(target); continue; }
                    if (!endpoints.contains(target)) {
                        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, target, d.getOpposite());
                        if (handler != null) { endpoints.add(target.immutable()); result.add(new InventoryRef(target.immutable(), d.getOpposite(), handler)); }
                    }
                }
            } else {
                for (Direction d : Direction.values()) { BlockPos next = pos.relative(d); if (!(level.getBlockState(next).getBlock() instanceof StorageCoreBlock) && seen.add(next) && isNetwork(level.getBlockState(next))) queue.addLast(next); }
            }
        }
        return analyzerFound ? result : List.of();
    }

    public static List<CatalogEntry> getCatalog(Level level, BlockPos corePos) {
        Map<ItemStack, CatalogEntry> merged = new LinkedHashMap<>();
        for (InventoryRef ref : getInventories(level, corePos)) {
            IItemHandler handler = ref.handler();
            for (int slot=0; slot<handler.getSlots(); slot++) {
                ItemStack stack = handler.getStackInSlot(slot); if (stack.isEmpty()) continue;
                CatalogEntry match = null;
                for (CatalogEntry entry : merged.values()) if (ItemStack.isSameItemSameComponents(entry.prototype, stack)) { match = entry; break; }
                if (match == null) merged.put(stack.copyWithCount(1), new CatalogEntry(stack, stack.getCount())); else match.count += stack.getCount();
            }
        }
        List<CatalogEntry> out = new ArrayList<>(merged.values()); out.sort(Comparator.comparingLong((CatalogEntry e) -> e.count).reversed()); return out;
    }

    public static long countItems(Level level, BlockPos corePos) { long n=0; for (CatalogEntry e : getCatalog(level,corePos)) n+=e.count; return n; }
    public static long countSlots(Level level, BlockPos corePos) { long n=0; for (InventoryRef r : getInventories(level,corePos)) n+=r.handler().getSlots(); return n; }

    public static ItemStack insert(Level level, BlockPos corePos, ItemStack input, boolean simulate) {
        if (input.isEmpty()) return ItemStack.EMPTY; ItemStack remaining = input.copy();
        for (InventoryRef ref : getInventories(level, corePos)) {
            IItemHandler h = ref.handler();
            for (int i=0; i<h.getSlots() && !remaining.isEmpty(); i++) remaining = h.insertItem(i, remaining, simulate);
            if (remaining.isEmpty()) return ItemStack.EMPTY;
        }
        return remaining;
    }

    public static ItemStack extract(Level level, BlockPos corePos, ItemStack prototype, int amount, boolean simulate) {
        if (prototype.isEmpty() || amount <= 0) return ItemStack.EMPTY;
        ItemStack result = ItemStack.EMPTY; int needed = amount;
        for (InventoryRef ref : getInventories(level, corePos)) {
            IItemHandler h=ref.handler();
            for (int i=0; i<h.getSlots() && needed>0; i++) {
                ItemStack stack=h.getStackInSlot(i); if (stack.isEmpty() || !ItemStack.isSameItemSameComponents(stack,prototype)) continue;
                ItemStack taken=h.extractItem(i, Math.min(needed, stack.getCount()), simulate);
                if (!taken.isEmpty()) { if (result.isEmpty()) result=taken.copy(); else result.grow(taken.getCount()); needed-=taken.getCount(); }
            }
            if (needed<=0) break;
        }
        return result;
    }

    /** Legacy capacity API: now reports the number of physical slots exposed by connected inventories. */
    public static long totalCapacity(Level level, BlockPos corePos) { return countSlots(level, corePos) * 64L; }
}
