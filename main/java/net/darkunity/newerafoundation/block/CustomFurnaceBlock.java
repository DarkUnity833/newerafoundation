package net.darkunity.newerafoundation.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkunity.newerafoundation.block.entity.CustomFurnaceBlockEntity;
import net.darkunity.newerafoundation.block.entity.FurnaceTier;
import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import javax.annotation.Nullable;

public class CustomFurnaceBlock extends AbstractFurnaceBlock {
    public static final MapCodec<CustomFurnaceBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> 
        instance.group(
            propertiesCodec(), 
            FurnaceTier.CODEC.fieldOf("tier").forGetter(b -> b.tier)
        ).apply(instance, CustomFurnaceBlock::new));

    private final FurnaceTier tier;

    public CustomFurnaceBlock(Properties props, FurnaceTier tier) {
        super(props);
        this.tier = tier;
    }

    @Override
    protected MapCodec<? extends AbstractFurnaceBlock> codec() { return CODEC; }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CustomFurnaceBlockEntity furnace) player.openMenu(furnace);
        return InteractionResult.CONSUME;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CustomFurnaceBlockEntity(pos, state, tier);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // Проверяем, что тип BlockEntity соответствует нашему типу печи
        if (type == ModBlockEntities.FURNACE_TYPE.get()) {
            // Если уровень клиентский, тика не будет (или можно добавить стандартный)
            // На сервере вызываем наш кастомный метод логики
            return level.isClientSide ? null : (lvl, pos, st, be) -> {
                if (be instanceof CustomFurnaceBlockEntity furnace) {
                    CustomFurnaceBlockEntity.serverTick(lvl, pos, st, furnace);
                }
            };
        }
        return null;
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {}
    
    public FurnaceTier getTier() { return tier; }
}