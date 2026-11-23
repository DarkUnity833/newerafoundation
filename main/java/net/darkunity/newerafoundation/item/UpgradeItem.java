package net.darkunity.newerafoundation.item;

import net.darkunity.newerafoundation.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.Supplier;

public class UpgradeItem extends Item {
    private final String tier;

    public UpgradeItem(Properties properties, String tier) {
        super(properties);
        this.tier = tier;
    }

   @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Block target = getTargetBlock(state.getBlock());

        if (target != null && !state.is(target)) {
            if (!level.isClientSide) {
                BlockEntity oldBe = level.getBlockEntity(pos);
                CompoundTag tag = null;
                if (oldBe != null) {
                    tag = oldBe.saveWithFullMetadata(level.registryAccess());
                }

                // Пытаемся сохранить направление (у воронок это FACING, у печей HORIZONTAL_FACING)
                var facing = state.hasProperty(BlockStateProperties.FACING) ? 
                             state.getValue(BlockStateProperties.FACING) : 
                             (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? state.getValue(BlockStateProperties.HORIZONTAL_FACING) : null);

                BlockState newState = target.defaultBlockState();
                if (facing != null) {
                    if (newState.hasProperty(BlockStateProperties.FACING)) 
                        newState = newState.setValue(BlockStateProperties.FACING, facing);
                    else if (newState.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
                        newState = newState.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
                }
                
                level.setBlock(pos, newState, 3);

                BlockEntity newBe = level.getBlockEntity(pos);
                if (newBe != null && tag != null) {
                    newBe.loadWithComponents(tag, level.registryAccess());
                }

                level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);

                spawnUpgradeParticles((ServerLevel) level, pos, getParticleBlock());

                if (context.getPlayer() != null && !context.getPlayer().isCreative()) {
                    context.getItemInHand().shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private void spawnUpgradeParticles(ServerLevel level, BlockPos pos, Block particleBlock) {
        BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, particleBlock.defaultBlockState());
        level.sendParticles(particle, 
            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 
            15, 0.3, 0.3, 0.3, 0.1);
    }

    private Block getParticleBlock() {
        return switch (tier) {
            case "copper" -> Blocks.COPPER_BLOCK;
            case "iron" -> Blocks.IRON_BLOCK;
            case "gold" -> Blocks.GOLD_BLOCK;
            case "diamond" -> Blocks.DIAMOND_BLOCK;
            case "crystal" -> Blocks.AMETHYST_BLOCK;
            case "emerald" -> Blocks.EMERALD_BLOCK;
            case "obsidian" -> Blocks.OBSIDIAN;
            case "netherite" -> Blocks.NETHERITE_BLOCK;
            default -> Blocks.WHITE_STAINED_GLASS;
        };
    }

    private Block getTargetBlock(Block current) {
        return switch (tier) {
            case "copper" -> {
                if (current == Blocks.CHEST) yield ModBlocks.COPPER_CHEST.get();
                if (current == Blocks.FURNACE) yield ModBlocks.COPPER_FURNACE.get();
                if (current == Blocks.BLAST_FURNACE) yield ModBlocks.COPPER_BLAST_FURNACE.get();
                if (current == Blocks.SMOKER) yield ModBlocks.COPPER_SMOKER.get();
                if (current == Blocks.BARREL) yield ModBlocks.COPPER_BARREL.get();
                if (current == Blocks.HOPPER) yield ModBlocks.COPPER_HOPPER.get(); // Ваниль -> Медь
                if (current instanceof net.minecraft.world.level.block.ShulkerBoxBlock) yield ModBlocks.COPPER_SHULKER.get(); // Ваниль -> Медь
                yield null;
            }
            case "iron" -> {
                if (current == ModBlocks.COPPER_CHEST.get()) yield ModBlocks.IRON_CHEST.get();
                if (current == ModBlocks.COPPER_FURNACE.get()) yield ModBlocks.IRON_FURNACE.get();
                if (current == ModBlocks.COPPER_BLAST_FURNACE.get()) yield ModBlocks.IRON_BLAST_FURNACE.get();
                if (current == ModBlocks.COPPER_SMOKER.get()) yield ModBlocks.IRON_SMOKER.get();
                if (current == ModBlocks.COPPER_BARREL.get()) yield ModBlocks.IRON_BARREL.get();
                if (current == ModBlocks.COPPER_HOPPER.get()) yield ModBlocks.IRON_HOPPER.get(); // Медь -> Железо
                if (current == ModBlocks.COPPER_SHULKER.get()) yield ModBlocks.IRON_SHULKER.get();
                yield null;
            }
            case "gold" -> {
                if (current == ModBlocks.IRON_CHEST.get()) yield ModBlocks.GOLD_CHEST.get();
                if (current == ModBlocks.IRON_FURNACE.get()) yield ModBlocks.GOLD_FURNACE.get();
                if (current == ModBlocks.IRON_BLAST_FURNACE.get()) yield ModBlocks.GOLD_BLAST_FURNACE.get();
                if (current == ModBlocks.IRON_SMOKER.get()) yield ModBlocks.GOLD_SMOKER.get();
                if (current == ModBlocks.IRON_BARREL.get()) yield ModBlocks.GOLD_BARREL.get();
                if (current == ModBlocks.IRON_HOPPER.get()) yield ModBlocks.GOLD_HOPPER.get(); // Железо -> Золото
                if (current == ModBlocks.IRON_SHULKER.get()) yield ModBlocks.GOLD_SHULKER.get();
                yield null;
            }
            case "diamond" -> {
                if (current == ModBlocks.GOLD_CHEST.get()) yield ModBlocks.DIAMOND_CHEST.get();
                if (current == ModBlocks.GOLD_FURNACE.get()) yield ModBlocks.DIAMOND_FURNACE.get();
                if (current == ModBlocks.GOLD_BLAST_FURNACE.get()) yield ModBlocks.DIAMOND_BLAST_FURNACE.get();
                if (current == ModBlocks.GOLD_SMOKER.get()) yield ModBlocks.DIAMOND_SMOKER.get();
                if (current == ModBlocks.GOLD_BARREL.get()) yield ModBlocks.DIAMOND_BARREL.get();
                if (current == ModBlocks.GOLD_HOPPER.get()) yield ModBlocks.DIAMOND_HOPPER.get(); // Золото -> Алмаз
                if (current == ModBlocks.GOLD_SHULKER.get()) yield ModBlocks.DIAMOND_SHULKER.get();
                yield null;
            }
            case "crystal" -> {
                if (current == ModBlocks.DIAMOND_CHEST.get()) yield ModBlocks.CRYSTAL_CHEST.get();
                if (current == ModBlocks.DIAMOND_FURNACE.get()) yield ModBlocks.CRYSTAL_FURNACE.get();
                if (current == ModBlocks.DIAMOND_BLAST_FURNACE.get()) yield ModBlocks.CRYSTAL_BLAST_FURNACE.get();
                if (current == ModBlocks.DIAMOND_SMOKER.get()) yield ModBlocks.CRYSTAL_SMOKER.get();
                if (current == ModBlocks.DIAMOND_BARREL.get()) yield ModBlocks.CRYSTAL_BARREL.get();
                if (current == ModBlocks.DIAMOND_HOPPER.get()) yield ModBlocks.CRYSTAL_HOPPER.get(); // Алмаз -> Кристалл
                if (current == ModBlocks.DIAMOND_SHULKER.get()) yield ModBlocks.CRYSTAL_SHULKER.get();
                yield null;
            }
            case "emerald" -> {
                if (current == ModBlocks.CRYSTAL_CHEST.get()) yield ModBlocks.EMERALD_CHEST.get();
                if (current == ModBlocks.CRYSTAL_FURNACE.get()) yield ModBlocks.EMERALD_FURNACE.get();
                if (current == ModBlocks.CRYSTAL_BLAST_FURNACE.get()) yield ModBlocks.EMERALD_BLAST_FURNACE.get();
                if (current == ModBlocks.CRYSTAL_SMOKER.get()) yield ModBlocks.EMERALD_SMOKER.get();
                if (current == ModBlocks.CRYSTAL_BARREL.get()) yield ModBlocks.EMERALD_BARREL.get();
                if (current == ModBlocks.CRYSTAL_HOPPER.get()) yield ModBlocks.EMERALD_HOPPER.get(); // Кристалл -> Изумруд
                if (current == ModBlocks.CRYSTAL_SHULKER.get()) yield ModBlocks.EMERALD_SHULKER.get();
                yield null;
            }
            case "obsidian" -> {
                if (current == ModBlocks.EMERALD_CHEST.get()) yield ModBlocks.OBSIDIAN_CHEST.get();
                if (current == ModBlocks.EMERALD_FURNACE.get()) yield ModBlocks.OBSIDIAN_FURNACE.get();
                if (current == ModBlocks.EMERALD_BLAST_FURNACE.get()) yield ModBlocks.OBSIDIAN_BLAST_FURNACE.get();
                if (current == ModBlocks.EMERALD_SMOKER.get()) yield ModBlocks.OBSIDIAN_SMOKER.get();
                if (current == ModBlocks.EMERALD_BARREL.get()) yield ModBlocks.OBSIDIAN_BARREL.get();
                if (current == ModBlocks.EMERALD_HOPPER.get()) yield ModBlocks.OBSIDIAN_HOPPER.get(); // Изумруд -> Обсидиан
                if (current == ModBlocks.EMERALD_SHULKER.get()) yield ModBlocks.OBSIDIAN_SHULKER.get();
                yield null;
            }
            case "netherite" -> {
                if (current == ModBlocks.OBSIDIAN_CHEST.get()) yield ModBlocks.NETHERITE_CHEST.get();
                if (current == ModBlocks.OBSIDIAN_FURNACE.get()) yield ModBlocks.NETHERITE_FURNACE.get();
                if (current == ModBlocks.OBSIDIAN_BLAST_FURNACE.get()) yield ModBlocks.NETHERITE_BLAST_FURNACE.get();
                if (current == ModBlocks.OBSIDIAN_SMOKER.get()) yield ModBlocks.NETHERITE_SMOKER.get();
                if (current == ModBlocks.OBSIDIAN_BARREL.get()) yield ModBlocks.NETHERITE_BARREL.get();
                if (current == ModBlocks.OBSIDIAN_HOPPER.get()) yield ModBlocks.NETHERITE_HOPPER.get(); // Обсидиан -> Незерит
                if (current == ModBlocks.OBSIDIAN_SHULKER.get()) yield ModBlocks.NETHERITE_SHULKER.get();
                yield null;
            }
            default -> null;
        };
    }
}