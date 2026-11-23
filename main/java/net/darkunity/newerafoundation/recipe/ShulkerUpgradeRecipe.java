package net.darkunity.newerafoundation.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkunity.newerafoundation.block.TieredShulkerBlock;
import net.darkunity.newerafoundation.init.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

/**
 * Shaped recipe for shulker tier upgrades: the contents (and custom name) of the shulker box
 * in the grid are moved to the crafted, upgraded shulker box.
 */
public class ShulkerUpgradeRecipe extends ShapedRecipe {
    private final ShapedRecipePattern pat;
    private final ItemStack res;

    public ShulkerUpgradeRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result) {
        super(group, category, pattern, result);
        this.pat = pattern;
        this.res = result;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack result = super.assemble(input, registries);
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            Block block = Block.byItem(stack.getItem());
            if (block instanceof ShulkerBoxBlock || block instanceof TieredShulkerBlock) {
                copy(stack, result, DataComponents.CONTAINER);
                copy(stack, result, DataComponents.CUSTOM_NAME);
                copy(stack, result, DataComponents.LOCK);
                break;
            }
        }
        return result;
    }

    private static <T> void copy(ItemStack from, ItemStack to, DataComponentType<T> type) {
        T value = from.get(type);
        if (value != null) {
            to.set(type, value);
        }
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SHULKER_UPGRADE_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<ShulkerUpgradeRecipe> {
        public static final MapCodec<ShulkerUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
                ShapedRecipePattern.MAP_CODEC.forGetter(r -> r.pat),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.res)
        ).apply(i, ShulkerUpgradeRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ShulkerUpgradeRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeUtf(recipe.getGroup());
                    buf.writeEnum(recipe.category());
                    ShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.pat);
                    ItemStack.STREAM_CODEC.encode(buf, recipe.res);
                },
                buf -> {
                    String group = buf.readUtf();
                    CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
                    ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buf);
                    ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                    return new ShulkerUpgradeRecipe(group, category, pattern, result);
                });

        @Override
        public MapCodec<ShulkerUpgradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShulkerUpgradeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
