package net.darkunity.newerafoundation.inventory;

import net.darkunity.newerafoundation.block.entity.CustomBarrelBlockEntity;
import net.darkunity.newerafoundation.block.entity.FurnaceTier;
import net.darkunity.newerafoundation.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container; // Добавлен импорт
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import javax.annotation.Nullable;

public class CustomBarrelMenu extends AbstractContainerMenu {
    private final CustomBarrelBlockEntity blockEntity;
    private final FurnaceTier tier;

    public CustomBarrelMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), null);
    }

    public CustomBarrelMenu(int id, Inventory playerInv, BlockEntity entity, @Nullable FurnaceTier tier) {
        super(ModMenuTypes.BARREL_MENU.get(), id);
        
        if (entity instanceof CustomBarrelBlockEntity barrel) {
            this.blockEntity = barrel;
            this.tier = (tier != null) ? tier : barrel.getTier();
        } else {
            this.blockEntity = null;
            this.tier = FurnaceTier.COPPER;
        }

        if (this.blockEntity != null) {
            int cols = this.tier.cols;
            int rows = this.tier.rows;

            // 1. Слоты бочки (Верхняя часть)
            for (int j = 0; j < rows; ++j) {
                for (int k = 0; k < cols; ++k) {
                    this.addSlot(new Slot(this.blockEntity, k + j * cols, 8 + k * 18, 18 + j * 18));
                }
            }

            // 2. Инвентарь игрока (Нижняя часть)
            int containerWidth = cols * 18 + 14; 
            int playerInvX = (containerWidth - 162) / 2 + 1; 
            int playerInvY = 18 + rows * 18 + 14; 

            // Основной инвентарь
            for (int l = 0; l < 3; ++l) {
                for (int j1 = 0; j1 < 9; ++j1) {
                    this.addSlot(new Slot(playerInv, j1 + l * 9 + 9, playerInvX + j1 * 18, playerInvY + l * 18));
                }
            }
            // Хотбар
            for (int k1 = 0; k1 < 9; ++k1) {
                this.addSlot(new Slot(playerInv, k1, playerInvX + k1 * 18, playerInvY + 58));
            }
        }
    }
    
    // Геттер для получения контейнера в BlockEntity (нужен для звуков)
    public Container getContainer() {
        return this.blockEntity;
    }

    // Геттер для экрана
    public FurnaceTier getTier() { return this.tier; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            int containerSize = this.tier.getSlotCount();
            if (index < containerSize) {
                if (!this.moveItemStackTo(itemstack1, containerSize, this.slots.size(), true)) return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(itemstack1, 0, containerSize, false)) return ItemStack.EMPTY;

            if (itemstack1.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.blockEntity != null && this.blockEntity.stillValid(player);
    }
}