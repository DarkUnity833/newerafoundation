package net.darkunity.newerafoundation.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Общая логика тика для кастомных печей / коптилен / плавилен.
 *
 * Индексы ContainerData в AbstractFurnaceBlockEntity (1.21.1):
 * 0 = litTime, 1 = litDuration, 2 = cookingProgress, 3 = cookingTotalTime
 *
 * Магмовый тир (скорость относительно обычной печки):
 *  - под печью лава          -> без топлива, x2.5
 *  - под печью блок магмы    -> без топлива, x1.5
 *  - под печью огонь         -> без топлива, x1.0
 *  - ничего горячего нет     -> только от топлива внутри, x2.0
 */
public final class FurnaceHeat {
    private static final int LIT_TIME = 0;
    private static final int LIT_DURATION = 1;
    private static final int COOK_PROGRESS = 2;
    private static final int COOK_TOTAL = 3;

    private FurnaceHeat() {}

    private enum Heat { NONE, FIRE, MAGMA, LAVA }

    private static Heat detectHeat(Level level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        // Лава (и источник, и текучая) важнее всего
        if (below.getFluidState().is(FluidTags.LAVA)) return Heat.LAVA;
        // Заправщик лавы под печью: работает как лава, пока в нём есть топливо
        if (level.getBlockEntity(pos.below()) instanceof LavaFeederBlockEntity feeder && feeder.hasLava()) return Heat.LAVA;
        if (below.is(Blocks.MAGMA_BLOCK)) return Heat.MAGMA;
        if (below.is(BlockTags.FIRE)) return Heat.FIRE; // огонь и душевой огонь
        return Heat.NONE;
    }

    public static <T extends AbstractCookingRecipe> void tick(
            Level level, BlockPos pos, BlockState state,
            AbstractFurnaceBlockEntity be, ContainerData data,
            FurnaceTier tier, RecipeType<T> recipeType) {

        float speed = tier.getSpeed();
        Heat heat = Heat.NONE;

        if (tier == FurnaceTier.MAGMA) {
            heat = detectHeat(level, pos);
            speed = switch (heat) {
                case LAVA -> 2.5f;
                case MAGMA -> 1.5f;
                case FIRE -> 1.0f;
                case NONE -> 2.0f;
            };

            // Горячий блок под печью заменяет топливо: держим печь "горящей"
            // и не даём тратиться топливу, пока есть что плавить.
            if (heat != Heat.NONE && canSmelt(level, be, recipeType)) {
                if (level.getBlockEntity(pos.below()) instanceof LavaFeederBlockEntity feeder) feeder.burnTick();
                int lit = data.get(LIT_TIME);
                if (lit <= 0) {
                    data.set(LIT_TIME, 2);
                    data.set(LIT_DURATION, 2);
                } else {
                    // +1 компенсирует ванильный -1 в serverTick
                    data.set(LIT_TIME, lit + 1);
                }
            }
        }

        int progressBefore = data.get(COOK_PROGRESS);
        AbstractFurnaceBlockEntity.serverTick(level, pos, state, be);
        int progressAfter = data.get(COOK_PROGRESS);

        // Ванильный serverTick сравнивает isLit() до/после своей работы. Мы выставляем litTime
        // ДО него, поэтому он не видит смены и не обновляет blockstate LIT. Синхронизируем сами.
        boolean litNow = data.get(LIT_TIME) > 0;
        BlockState cur = level.getBlockState(pos);
        if (cur.hasProperty(AbstractFurnaceBlock.LIT) && cur.getValue(AbstractFurnaceBlock.LIT) != litNow) {
            level.setBlock(pos, cur.setValue(AbstractFurnaceBlock.LIT, litNow), 3);
            be.setChanged();
        }

        // Ускорение: добавляем дополнительные тики, только если печь реально продвинулась
        float extra = speed - 1.0f;
        if (extra > 0 && progressAfter > progressBefore) {
            int bonus = (int) extra;
            if (level.random.nextFloat() < (extra - bonus)) bonus++;
            if (bonus > 0) {
                int total = data.get(COOK_TOTAL);
                data.set(COOK_PROGRESS, Math.min(total - 1, progressAfter + bonus));
            }
        }
    }

    /** Есть ли во входном слоте то, что можно переплавить, и влезет ли результат в выходной слот. */
    private static <T extends AbstractCookingRecipe> boolean canSmelt(
            Level level, AbstractFurnaceBlockEntity be, RecipeType<T> recipeType) {
        ItemStack input = be.getItem(0);
        if (input.isEmpty()) return false;

        SingleRecipeInput recipeInput = new SingleRecipeInput(input);
        var holder = level.getRecipeManager().getRecipeFor(recipeType, recipeInput, level);
        if (holder.isEmpty()) return false;

        ItemStack result = holder.get().value().assemble(recipeInput, level.registryAccess());
        if (result.isEmpty()) return false;

        ItemStack output = be.getItem(2);
        if (output.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(output, result)) return false;
        return output.getCount() + result.getCount() <= output.getMaxStackSize();
    }
}
