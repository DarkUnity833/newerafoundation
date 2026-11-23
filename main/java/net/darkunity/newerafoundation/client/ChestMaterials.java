package net.darkunity.newerafoundation.client;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

public class ChestMaterials {
    
    // Используем ResourceLocation.fromNamespaceAndPath для 1.21.1
    // Sheets.CHEST_SHEET — это стандартный атлас для всех сундуков
    public static Material getChestMaterial(String name) {
        return new Material(
            Sheets.CHEST_SHEET, 
            ResourceLocation.fromNamespaceAndPath("newerafoundation", "entity/chest/" + name)
        );
    }

    public static final Material COPPER = getChestMaterial("copper");
    public static final Material IRON = getChestMaterial("iron");
    public static final Material GOLD = getChestMaterial("gold");
    public static final Material DIAMOND = getChestMaterial("diamond");
    public static final Material NETHERITE = getChestMaterial("netherite");
    public static final Material CRYSTAL = getChestMaterial("crystal");
    public static final Material OBSIDIAN = getChestMaterial("obsidian");
    public static final Material EMERALD = getChestMaterial("emerald");

    // Метод для получения материала по тиру (удобно для рендерера)
    public static Material byTier(net.darkunity.newerafoundation.block.entity.FurnaceTier tier) {
        return switch (tier) {
            case COPPER -> COPPER;
            case IRON -> IRON;
            case GOLD -> GOLD;
            case DIAMOND -> DIAMOND;
            case NETHERITE -> NETHERITE;
            case CRYSTAL -> CRYSTAL;
            case OBSIDIAN -> OBSIDIAN;
            case EMERALD -> EMERALD;
            default -> IRON; // Фолбэк
        };
    }
}