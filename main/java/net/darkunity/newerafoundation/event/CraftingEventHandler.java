package net.darkunity.newerafoundation.event;

import net.darkunity.newerafoundation.item.BackpackItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = "newerafoundation", bus = EventBusSubscriber.Bus.GAME)
public class CraftingEventHandler {

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();

        // Проверяем, что скрафченный предмет — это рюкзак
        if (result.getItem() instanceof BackpackItem) {
            ItemStack oldBackpack = ItemStack.EMPTY;

            // Просматриваем сетку крафта (инвентарь верстака)
            for (int i = 0; i < event.getInventory().getContainerSize(); i++) {
                ItemStack stack = event.getInventory().getItem(i);
                
                // Ищем в сетке любой рюкзак, который послужил ингредиентом
                if (stack.getItem() instanceof BackpackItem && stack != result) {
                    oldBackpack = stack;
                    break;
                }
            }

            // Если нашли старый рюкзак, копируем его содержимое в новый
            if (!oldBackpack.isEmpty()) {
                CustomData oldData = oldBackpack.get(DataComponents.CUSTOM_DATA);
                if (oldData != null) {
                    // Копируем все данные (инвентарь, настройки и т.д.)
                    result.set(DataComponents.CUSTOM_DATA, oldData);
                }
            }
        }
    }
}