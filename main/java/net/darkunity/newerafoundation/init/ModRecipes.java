package net.darkunity.newerafoundation.init;

import net.darkunity.newerafoundation.recipe.BackpackUpgradeRecipe;
import net.darkunity.newerafoundation.recipe.ShulkerUpgradeRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = 
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, "newerafoundation");

    public static final DeferredHolder<RecipeSerializer<?>, BackpackUpgradeRecipe.Serializer> BACKPACK_UPGRADE_SERIALIZER = 
            SERIALIZERS.register("backpack_upgrade", BackpackUpgradeRecipe.Serializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, ShulkerUpgradeRecipe.Serializer> SHULKER_UPGRADE_SERIALIZER =
            SERIALIZERS.register("shulker_upgrade", ShulkerUpgradeRecipe.Serializer::new);
}
