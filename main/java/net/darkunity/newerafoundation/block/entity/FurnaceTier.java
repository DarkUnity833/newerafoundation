package net.darkunity.newerafoundation.block.entity;

import net.minecraft.util.StringRepresentable;
import com.mojang.serialization.Codec;

public enum FurnaceTier implements StringRepresentable {

    WOOL("wool", 8, 4, 2, 1.0f, 0),
    LEATHER("leather", 16, 8, 2, 1.0f, 0),
    COPPER("copper", 45, 9, 5, 1.5f, 4000),
    IRON("iron", 54, 9, 6, 2.0f, 8000),
    GOLD("gold", 81, 9, 9, 3.0f, 12000),
    DIAMOND("diamond", 108, 12, 9, 4.0f, 16000),
    EMERALD("emerald", 120, 12, 10, 5.0f, 20000),
    CRYSTAL("crystal", 144, 12, 12, 6.0f, 32000),
    OBSIDIAN("obsidian", 156, 12, 13, 8.0f, 64000),
    NETHERITE("netherite", 210, 14, 15, 10.0f, 128000),
    MAGMA("magma", 27, 9, 3, 4.0f, 8000);

    public static final Codec<FurnaceTier> CODEC = StringRepresentable.fromEnum(FurnaceTier::values);

    private final String name;
    private final int slotCount;
    public final int cols;
    public final int rows;
    public final float speed;
    private final int tankCapacity; // Объём бака в mB

    FurnaceTier(String name, int slotCount, int cols, int rows, float speed, int tankCapacity) {
        this.name = name;
        this.slotCount = slotCount;
        this.cols = cols;
        this.rows = rows;
        this.speed = speed;
        this.tankCapacity = tankCapacity;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public int getSlotCount() {
        return this.slotCount;
    }

    public float getSpeed() {
        return this.speed;
    }

    public int getTankCapacity() {
        return this.tankCapacity;
    }

    public static FurnaceTier byName(String name) {
        if (name == null || name.isEmpty()) {
            return COPPER;
        }

        for (FurnaceTier tier : values()) {
            if (tier.name.equalsIgnoreCase(name)) {
                return tier;
            }
        }
        return COPPER;
    }
}