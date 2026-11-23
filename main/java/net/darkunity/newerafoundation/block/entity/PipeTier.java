package net.darkunity.newerafoundation.block.entity;

/** Тиры труб: пропускная способность в mB за тик. */
public enum PipeTier {
    COPPER(50), IRON(125), GOLD(250), DIAMOND(500), NETHERITE(1000);

    public final int throughput;

    PipeTier(int throughput) { this.throughput = throughput; }

    public int capacity() { return Math.max(1000, throughput * 4); }
}
