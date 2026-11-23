package net.darkunity.newerafoundation.block;

import com.mojang.serialization.MapCodec;
import net.darkunity.newerafoundation.block.entity.VoidBarrelBlockEntity;
import net.darkunity.newerafoundation.inventory.VoidBarrelMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/** Бездонная бочка: уничтожает всё (воронки, трубы, правый клик предметом); последний предмет можно забрать в окне. */
public class VoidBarrelBlock extends BaseEntityBlock {
    public static final MapCodec<VoidBarrelBlock> CODEC = simpleCodec(VoidBarrelBlock::new);

    public VoidBarrelBlock(Properties properties) { super(properties); }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new VoidBarrelBlockEntity(pos, state); }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof VoidBarrelBlockEntity be) {
            be.voidItem(stack);
            stack.setCount(0);
            level.playSound(null, pos, SoundEvents.GENERIC_BURN, SoundSource.BLOCKS, 0.4f, 2.0f);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Пустая рука - открыть окно с последним удалённым предметом. */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof VoidBarrelBlockEntity be) {
            player.openMenu(new SimpleMenuProvider((id, inv, p) -> new VoidBarrelMenu(id, inv, be.trash),
                    Component.translatable("block.newerafoundation.void_barrel")));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
