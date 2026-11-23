package net.darkunity.newerafoundation.inventory;

import net.darkunity.newerafoundation.block.entity.FurnaceTier;
import net.darkunity.newerafoundation.block.entity.TieredShulkerBlockEntity;
import net.darkunity.newerafoundation.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class TieredShulkerMenu extends AbstractContainerMenu {
    private final TieredShulkerBlockEntity box;
    private final FurnaceTier tier;

    public TieredShulkerMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, inv.player.level().getBlockEntity(buf.readBlockPos()), null);
    }

    public TieredShulkerMenu(int id, Inventory inv, BlockEntity be, @Nullable FurnaceTier given) {
        super(ModMenuTypes.TIERED_SHULKER_MENU.get(), id);
        if (be instanceof TieredShulkerBlockEntity b) {
            box = b;
            tier = given != null ? given : b.getTier();
            int cols = tier.cols, rows = tier.rows;
            for (int y = 0; y < rows; y++)
                for (int x = 0; x < cols; x++)
                    addSlot(new Slot(box, x + y * cols, 8 + x * 18, 18 + y * 18));
            int py = 18 + rows * 18 + 14;
            for (int y = 0; y < 3; y++)
                for (int x = 0; x < 9; x++)
                    addSlot(new Slot(inv, x + y * 9 + 9, 8 + x * 18, py + y * 18));
            for (int x = 0; x < 9; x++)
                addSlot(new Slot(inv, x, 8 + x * 18, py + 58));
            // server side only: starts the lid animation / sound (like ChestMenu does for chests)
            if (!inv.player.level().isClientSide) {
                box.startOpen(inv.player);
            }
        } else {
            box = null;
            tier = FurnaceTier.COPPER;
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (box != null && !player.level().isClientSide) {
            box.stopOpen(player);
        }
    }

    public Container getContainer() {
        return box;
    }

    public FurnaceTier getTier() {
        return tier;
    }

    @Override
    public ItemStack quickMoveStack(Player p, int index) {
        ItemStack empty = ItemStack.EMPTY;
        if (index < 0 || index >= slots.size()) return empty;
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) return empty;
        ItemStack stack = slot.getItem(), copy = stack.copy();
        int n = tier.getSlotCount();
        if (index < n) {
            if (!moveItemStackTo(stack, n, slots.size(), true)) return empty;
        } else if (!moveItemStackTo(stack, 0, n, false)) return empty;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        return copy;
    }

    @Override
    public boolean stillValid(Player p) {
        return box != null && box.stillValid(p);
    }
}
