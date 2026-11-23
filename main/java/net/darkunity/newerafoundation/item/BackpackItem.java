package net.darkunity.newerafoundation.item;

import net.darkunity.newerafoundation.block.entity.FurnaceTier;
import net.darkunity.newerafoundation.inventory.BackpackMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BackpackItem extends Item implements MenuProvider {
    private final FurnaceTier tier;

    public BackpackItem(Properties properties, FurnaceTier tier) {
        super(properties.stacksTo(1));
        this.tier = tier;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 0.5F, 1.0F);
            
            serverPlayer.openMenu(this, buf -> {
                buf.writeBlockPos(player.blockPosition()); // Просто заглушка для FriendlyByteBuf, если нужно
            });
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("item.newerafoundation." + tier.getSerializedName() + "_backpack");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        ItemStack stack = player.getMainHandItem().getItem() instanceof BackpackItem ? player.getMainHandItem() : player.getOffhandItem();
        return new BackpackMenu(id, playerInv, stack, this.tier);
    }

    public FurnaceTier getTier() { return this.tier; }
}