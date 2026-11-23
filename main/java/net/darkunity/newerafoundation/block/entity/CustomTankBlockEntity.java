package net.darkunity.newerafoundation.block.entity;

import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class CustomTankBlockEntity extends BlockEntity {
    private final FurnaceTier tier;
    public final FluidTank fluidTank;
    private int lavaGenTimer = 0;

    public CustomTankBlockEntity(BlockPos pos, BlockState state, FurnaceTier tier) {
        super(ModBlockEntities.TANK_TYPE.get(), pos, state);
        this.tier = tier;
        this.fluidTank = new FluidTank(tier.getTankCapacity()) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                if (level != null) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }
        };
    }

    public FluidStack getFluid() {
        return this.fluidTank.getFluid();
    }

    public int getCapacity() {
        return this.fluidTank.getCapacity();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CustomTankBlockEntity be) {
        if (level.isClientSide) return;

        // 1. УЛУЧШЕННАЯ ЛОГИКА СЛИВА ВНИЗ (Stacking)
        if (!be.fluidTank.isEmpty()) {
            BlockPos.MutableBlockPos targetPos = pos.below().mutable();
            CustomTankBlockEntity lastValidTank = null;

            while (level.getBlockEntity(targetPos) instanceof CustomTankBlockEntity belowTank) {
                if (belowTank.fluidTank.getFluidAmount() < belowTank.fluidTank.getCapacity()) {
                    lastValidTank = belowTank;
                }
                targetPos.move(Direction.DOWN);
            }

            if (lastValidTank != null) {
                FluidStack transfer = be.fluidTank.drain(1000, IFluidHandler.FluidAction.SIMULATE);
                int filled = lastValidTank.fluidTank.fill(transfer, IFluidHandler.FluidAction.EXECUTE);
                be.fluidTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
            }
        }

        // 2. Логика Магмового бака: Генерация лавы
        if (be.tier == FurnaceTier.MAGMA) {
            be.lavaGenTimer++;
            if (be.lavaGenTimer >= 1200) {
                be.lavaGenTimer = 0;
                BlockState floor = level.getBlockState(pos.below());
                if (floor.is(Blocks.MAGMA_BLOCK) || floor.getFluidState().is(FluidTags.LAVA)) {
                    be.fluidTank.fill(new FluidStack(Fluids.LAVA, 100), IFluidHandler.FluidAction.EXECUTE);
                }
            }
        }
    }

    /**
     * ПУСТОЕ ВЕДРО: Ищем откуда ЗАБРАТЬ жидкость (сверху вниз)
     */
    public IFluidHandler getFluidHandlerForInteraction() {
        if (level == null) return this.fluidTank;
        BlockPos.MutableBlockPos searchPos = this.worldPosition.above().mutable();
        CustomTankBlockEntity highestTankWithFluid = this;

        if (this.fluidTank.isEmpty()) {
            while (level.getBlockEntity(searchPos) instanceof CustomTankBlockEntity aboveTank) {
                if (!aboveTank.fluidTank.isEmpty()) return aboveTank.fluidTank;
                searchPos.move(Direction.UP);
            }
        } else {
            while (level.getBlockEntity(searchPos) instanceof CustomTankBlockEntity aboveTank) {
                if (!aboveTank.fluidTank.isEmpty()) highestTankWithFluid = aboveTank;
                else break;
                searchPos.move(Direction.UP);
            }
        }
        return highestTankWithFluid.fluidTank;
    }

    /**
     * ПОЛНОЕ ВЕДРО: Ищем куда ЗАЛИТЬ жидкость (снизу вверх)
     */
    public IFluidHandler getFluidHandlerForFilling() {
        if (level == null) return this.fluidTank;
        BlockPos.MutableBlockPos searchPos = this.worldPosition.mutable();
        
        // Ищем первый бак в колонне, в котором есть место, начиная с того, на который тыкнули
        while (level.getBlockEntity(searchPos) instanceof CustomTankBlockEntity tank) {
            if (tank.fluidTank.getFluidAmount() < tank.fluidTank.getCapacity()) {
                return tank.fluidTank;
            }
            searchPos.move(Direction.UP);
        }
        return this.fluidTank; 
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("FluidData", fluidTank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("LavaTimer", lavaGenTimer);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fluidTank.readFromNBT(registries, tag.getCompound("FluidData"));
        lavaGenTimer = tag.getInt("LavaTimer");
    }
}