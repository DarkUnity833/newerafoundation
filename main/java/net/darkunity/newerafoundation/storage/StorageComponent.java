package net.darkunity.newerafoundation.storage;

/** Marker for blocks that belong to the storage network graph. Inventories are external. */
public interface StorageComponent {
    default long storageCapacity() { return 0L; }
}
