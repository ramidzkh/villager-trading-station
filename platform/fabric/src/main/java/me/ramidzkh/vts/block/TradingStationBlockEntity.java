package me.ramidzkh.vts.block;

import me.ramidzkh.vts.VillagerTradingStationFabric;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TradingStationBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    private final SimpleContainer inputs = new SimpleContainer(9);
    private final SimpleContainer outputs = new SimpleContainer(9);
    private final SimpleContainer quotes = new SimpleContainer(1);

    private final Storage<ItemVariant> inputStorage = InventoryStorage.of(inputs, null);
    private final Storage<ItemVariant> outputStorage = InventoryStorage.of(outputs, null);
    private final Storage<ItemVariant> quoteStorage = new FilteringStorage<>(InventoryStorage.of(quotes, null)) {
        @Override
        protected boolean canInsert(ItemVariant resource) {
            return resource.getItem() == VillagerTradingStationFabric.Items.QUOTE;
        }
    };
    private final Storage<ItemVariant> exposed = new CombinedStorage<>(List.of(
            new OneWayStorage<>(inputStorage, true, false),
            new OneWayStorage<>(outputStorage, false, true)
    ));

    public TradingStationBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);

        inputs.addListener(container -> {
            if (level instanceof ServerLevel) {
                sync();
            }
        });

        outputs.addListener(container -> {
            if (level instanceof ServerLevel) {
                sync();
            }
        });

        quotes.addListener(container -> {
            if (level instanceof ServerLevel) {
                sync();
            }
        });
    }

    public Storage<ItemVariant> getStorage(@SuppressWarnings("unused") Direction direction) {
        return exposed;
    }

    public Storage<ItemVariant> getQuotes() {
        return quoteStorage;
    }

    public void drop(Level level, BlockPos blockPos) {
        Containers.dropContents(level, blockPos, inputs);
        Containers.dropContents(level, blockPos, outputs);
        Containers.dropContents(level, blockPos, quotes);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);

        inputs.fromTag(compoundTag.getList("Inputs", NbtType.COMPOUND));
        outputs.fromTag(compoundTag.getList("Outputs", NbtType.COMPOUND));
        quotes.fromTag(compoundTag.getList("Quotes", NbtType.COMPOUND));
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.put("Inputs", inputs.createTag());
        compoundTag.put("Outputs", outputs.createTag());
        compoundTag.put("Quotes", quotes.createTag());

        return super.save(compoundTag);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return save(tag);
    }
}
