package net.darkunity.newerafoundation.inventory;

import net.darkunity.newerafoundation.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** 1 слот "только забрать" + инвентарь игрока. */
public class VoidBarrelMenu extends AbstractContainerMenu {
    private final Container trash;

    public VoidBarrelMenu(int id, Inventory inv, FriendlyByteBuf buf) { this(id, inv, new SimpleContainer(1)); }

    public VoidBarrelMenu(int id, Inventory inv, Container trash) {
        super(ModMenuTypes.VOID_BARREL_MENU.get(), id);
        this.trash = trash;
        this.addSlot(new Slot(trash, 0, 80, 20) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++) this.addSlot(new Slot(inv, c + r * 9 + 9, 8 + c * 18, 51 + r * 18));
        for (int c = 0; c < 9; c++) this.addSlot(new Slot(inv, c, 8 + c * 18, 109));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (index != 0 || !slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (!this.moveItemStackTo(stack, 1, this.slots.size(), true)) return ItemStack.EMPTY;
        slot.setChanged();
        return copy;
    }

    @Override
    public boolean stillValid(Player player) { return true; }
}
