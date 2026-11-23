package net.darkunity.newerafoundation.recipe; // Проверь, чтобы путь в папках совпадал!

import com.mojang.serialization.MapCodec;
import net.darkunity.newerafoundation.init.ModRecipes;
import net.darkunity.newerafoundation.item.BackpackItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class BackpackUpgradeRecipe extends ShapedRecipe {

    public BackpackUpgradeRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result) {
        super(group, category, pattern, result);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack result = super.assemble(input, registries);
        ItemStack oldBackpack = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof BackpackItem) {
                oldBackpack = stack;
                break;
            }
        }

        if (!oldBackpack.isEmpty()) {
            CustomData data = oldBackpack.get(DataComponents.CUSTOM_DATA);
            if (data != null) {
                result.set(DataComponents.CUSTOM_DATA, data);
            }
        }

        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.BACKPACK_UPGRADE_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<BackpackUpgradeRecipe> {
        // В 1.21.1 используем правильные названия полей из ShapedRecipe.Serializer
        public static final MapCodec<BackpackUpgradeRecipe> CODEC = ShapedRecipe.Serializer.CODEC.xmap(
            s -> new BackpackUpgradeRecipe(s.getGroup(), s.category(), s.pattern, s.getResultItem(null)),
            s -> new ShapedRecipe(s.getGroup(), s.category(), s.pattern, s.getResultItem(null))
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, BackpackUpgradeRecipe> STREAM_CODEC = ShapedRecipe.Serializer.STREAM_CODEC.map(
            s -> new BackpackUpgradeRecipe(s.getGroup(), s.category(), s.pattern, s.getResultItem(null)),
            s -> new ShapedRecipe(s.getGroup(), s.category(), s.pattern, s.getResultItem(null))
        );

        @Override
        public MapCodec<BackpackUpgradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BackpackUpgradeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}