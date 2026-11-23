package net.darkunity.newerafoundation.inventory;

import net.darkunity.newerafoundation.block.entity.FurnaceTier;
import net.darkunity.newerafoundation.init.ModMenuTypes;
import net.darkunity.newerafoundation.item.BackpackItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BackpackMenu extends AbstractContainerMenu {
    private final ItemStack backpackStack;
    private final ItemStackHandler inventory;
    private final FurnaceTier tier;
    private final HolderLookup.Provider registries;

    public BackpackMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.getMainHandItem(), inv.player.getMainHandItem().getItem() instanceof BackpackItem b ? b.getTier() : FurnaceTier.COPPER);
    }

    public BackpackMenu(int id, Inventory playerInv, ItemStack backpackStack, FurnaceTier tier) {
        super(ModMenuTypes.BACKPACK_MENU.get(), id);
        this.backpackStack = backpackStack;
        this.tier = tier;
        this.registries = playerInv.player.registryAccess();
        
        this.inventory = new ItemStackHandler(tier.getSlotCount()) {
            @Override
            protected void onContentsChanged(int slot) {
                saveToStack();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return !(stack.getItem() instanceof BackpackItem);
            }
        };
        
        loadFromStack();

        int cols = tier.cols;
        int rows = tier.rows;
        int xOffset = (cols < 9) ? (176 - (cols * 18)) / 2 : 8;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int index = j + i * cols;
                if (index < inventory.getSlots()) {
                    this.addSlot(new SlotItemHandler(inventory, index, xOffset + j * 18, 18 + i * 18));
                }
            }
        }

        int playerInvY = 18 + (rows * 18) + 14;
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(playerInv, j1 + l * 9 + 9, 8 + j1 * 18, playerInvY + l * 18));
            }
        }
        for (int k1 = 0; k1 < 9; ++k1) {
            this.addSlot(new Slot(playerInv, k1, 8 + k1 * 18, playerInvY + 58));
        }
    }

    public FurnaceTier getTier() {
        return this.tier;
    }

    private void loadFromStack() {
        CustomData customData = backpackStack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("Inventory")) {
                CompoundTag invTag = tag.getCompound("Inventory");
                invTag.putInt("Size", this.tier.getSlotCount());
                inventory.deserializeNBT(this.registries, invTag);
            }
        }
    }

    private void saveToStack() {
        if (backpackStack.isEmpty()) return;
        
        CompoundTag invNbt = inventory.serializeNBT(this.registries);
        invNbt.putInt("Size", this.tier.getSlotCount());
        
        backpackStack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> 
            data.update(tag -> tag.put("Inventory", invNbt))
        );
    }

    @Override
    public void removed(Player player) {
        saveToStack();
        super.removed(player);
        if (!player.level().isClientSide) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), 
                SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 0.5F, 0.8F);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return !backpackStack.isEmpty() && (player.getMainHandItem() == backpackStack || player.getOffhandItem() == backpackStack);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            
            int containerSize = this.inventory.getSlots();
            
            if (index < containerSize) {
                if (!this.moveItemStackTo(itemstack1, containerSize, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (itemstack1.getItem() instanceof BackpackItem || !this.moveItemStackTo(itemstack1, 0, containerSize, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }
}