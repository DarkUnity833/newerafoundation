package net.darkunity.newerafoundation.block.entity;

import net.neoforged.neoforge.fluids.FluidStack;
import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class LavaFeederBlockEntity extends BlockEntity {
    public static final int CAPACITY = 4000;
    /** 1 mB лавы на 20 тиков плавки: ведро (1000 mB) = 20000 тиков, как у ванильной лавы. */
    private static final int TICKS_PER_MB = 20;

    public final FluidTank tank;
    private int burnCounter = 0;

    public LavaFeederBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LAVA_FEEDER_TYPE.get(), pos, state);
        this.tank = new FluidTank(CAPACITY, fs -> fs.getFluid() == Fluids.LAVA) {
            @Override
            protected void onContentsChanged() { setChanged(); }
        };
    }

    /** Снаружи лаву можно только залить: забрать (ведром, трубой, мод-трубой) нельзя. */
    public final IFluidHandler inputOnly = new IFluidHandler() {
        @Override public int getTanks() { return 1; }
        @Override public FluidStack getFluidInTank(int t) { return tank.getFluid(); }
        @Override public int getTankCapacity(int t) { return tank.getCapacity(); }
        @Override public boolean isFluidValid(int t, FluidStack s) { return tank.isFluidValid(s); }
        @Override public int fill(FluidStack r, FluidAction a) { return tank.fill(r, a); }
        @Override public FluidStack drain(FluidStack r, FluidAction a) { return FluidStack.EMPTY; }
        @Override public FluidStack drain(int m, FluidAction a) { return FluidStack.EMPTY; }
    };

    public boolean hasLava() { return tank.getFluidAmount() > 0; }

    /** Вызывается печью на каждом тике, пока она реально плавит. */
    public void burnTick() {
        if (++burnCounter >= TICKS_PER_MB) {
            burnCounter = 0;
            tank.drain(1, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("FluidData", tank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("Burn", burnCounter);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tank.readFromNBT(registries, tag.getCompound("FluidData"));
        burnCounter = tag.getInt("Burn");
    }
}
