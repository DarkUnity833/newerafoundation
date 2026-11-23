package net.darkunity.newerafoundation.init;

import net.darkunity.newerafoundation.inventory.CustomBarrelMenu;
import net.darkunity.newerafoundation.inventory.CustomChestMenu;
import net.darkunity.newerafoundation.inventory.BackpackMenu; // Добавлен импорт
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = 
            DeferredRegister.create(Registries.MENU, "newerafoundation");

    public static final DeferredHolder<MenuType<?>, MenuType<CustomBarrelMenu>> BARREL_MENU = 
            MENUS.register("barrel_menu", () -> IMenuTypeExtension.create(CustomBarrelMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<CustomChestMenu>> CHEST_MENU = 
            MENUS.register("chest_menu", () -> IMenuTypeExtension.create(CustomChestMenu::new));

    // --- ДОБАВЛЕНО: Меню для бэкпаков ---
    public static final DeferredHolder<MenuType<?>, MenuType<net.darkunity.newerafoundation.inventory.TieredShulkerMenu>> TIERED_SHULKER_MENU =
            MENUS.register("tiered_shulker_menu", () -> IMenuTypeExtension.create(net.darkunity.newerafoundation.inventory.TieredShulkerMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<BackpackMenu>> BACKPACK_MENU = 
            MENUS.register("backpack_menu", () -> IMenuTypeExtension.create(BackpackMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<net.darkunity.newerafoundation.inventory.VoidBarrelMenu>> VOID_BARREL_MENU =
            MENUS.register("void_barrel_menu", () -> IMenuTypeExtension.create(net.darkunity.newerafoundation.inventory.VoidBarrelMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<net.darkunity.newerafoundation.inventory.StorageMenu>> STORAGE_MENU =
            MENUS.register("storage_menu", () -> IMenuTypeExtension.create(net.darkunity.newerafoundation.inventory.StorageMenu::new));
}
