package net.darkunity.newerafoundation.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Эндер-рюкзак: ПКМ открывает личное эндер-хранилище игрока (общее с ванильным эндер-сундуком). */
public class EnderBackpackItem extends Item {
    public EnderBackpackItem(Properties properties) { super(properties.stacksTo(1)); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            var inv = player.getEnderChestInventory();
            player.openMenu(new SimpleMenuProvider((id, pInv, p) -> ChestMenu.threeRows(id, pInv, inv),
                    Component.translatable("container.enderchest")));
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }
}
