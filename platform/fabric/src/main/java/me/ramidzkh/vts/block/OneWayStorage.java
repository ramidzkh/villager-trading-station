package me.ramidzkh.vts.block;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;

public class OneWayStorage<T> extends FilteringStorage<T> {

    private final boolean insert;
    private final boolean extract;

    public OneWayStorage(Storage<T> backingStorage, boolean insert, boolean extract) {
        super(backingStorage);

        this.insert = insert;
        this.extract = extract;
    }

    @Override
    protected boolean canInsert(T resource) {
        return insert;
    }

    @Override
    protected boolean canExtract(T resource) {
        return extract;
    }
}
