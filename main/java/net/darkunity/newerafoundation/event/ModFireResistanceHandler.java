package net.darkunity.newerafoundation.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = "newerafoundation", bus = EventBusSubscriber.Bus.GAME)
public class ModFireResistanceHandler {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        // Проверяем, что появившаяся сущность — это выпавший предмет
        if (event.getEntity() instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getItem();
            
            // Проверяем, принадлежит ли предмет нашему моду по ID
            // Это автоматически охватит ВСЕ предметы с префиксом newerafoundation:
            if (stack.getItem().toString().contains("newerafoundation")) {
                // Делаем сущность предмета неуязвимой к огню и лаве
                itemEntity.setInvulnerable(true); 
                
                // Дополнительно: в 1.21.1 можно пометить сам стак как огнеупорный в полете
                // чтобы он не уничтожался DamageSource.LAVA
            }
        }
    }
}