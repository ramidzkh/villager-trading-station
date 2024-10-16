package me.ramidzkh.vts.block;

import me.ramidzkh.vts.VillagerTradingStation;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TradingStationBlockEntity extends BlockEntity
        implements SidedStorageBlockEntity, ContainerListener, MenuProvider {

    private final SimpleContainer inputs = new SimpleContainer(9);
    private final SimpleContainer outputs = new SimpleContainer(9);
    private final SimpleContainer quotes = new SimpleContainer(3);

    private final Storage<ItemVariant> inputStorage = InventoryStorage.of(inputs, null);
    private final Storage<ItemVariant> outputStorage = InventoryStorage.of(outputs, null);
    private final Storage<ItemVariant> exposed = new CombinedStorage<>(List.of(
            FilteringStorage.insertOnlyOf(inputStorage),
            FilteringStorage.extractOnlyOf(outputStorage)));

    public TradingStationBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);

        inputs.addListener(this);
        outputs.addListener(this);
        quotes.addListener(this);
    }

    @Override
    public @NotNull Storage<ItemVariant> getItemStorage(Direction side) {
        return exposed;
    }

    public void interact(AbstractVillager villager) {
        for (var i = 0; i < quotes.getContainerSize(); i++) {
            var quote = quotes.getItem(i).get(VillagerTradingStation.QUOTE);

            if (quote == null) {
                continue;
            }

            MerchantOffer myOffer = null;

            for (var offer : villager.getOffers()) {
                if (offer.isOutOfStock()) {
                    continue;
                }

                if (quote.satisfiedBy(offer)) {
                    myOffer = offer;
                    break;
                }
            }

            if (myOffer != null) {
                try (var transaction = Transaction.openOuter()) {
                    if (inputStorage.extract(ItemVariant.of(quote.a().itemStack()), quote.a().count(),
                            transaction) != quote.a().count()) {
                        continue;
                    }

                    if (quote.b().isPresent()) {
                        var cost = quote.b().get();

                        if (inputStorage.extract(ItemVariant.of(cost.itemStack()), cost.count(), transaction) != cost
                                .count()) {
                            continue;
                        }
                    }

                    if (outputStorage.insert(ItemVariant.of(quote.result()), quote.result().getCount(),
                            transaction) != quote.result().getCount()) {
                        continue;
                    }

                    villager.notifyTrade(myOffer);
                    transaction.commit();
                }
            }
        }
    }

    public boolean canInteract(AbstractVillager villager) {
        for (var i = 0; i < quotes.getContainerSize(); i++) {
            var quote = quotes.getItem(i).get(VillagerTradingStation.QUOTE);

            if (quote == null) {
                continue;
            }

            for (var offer : villager.getOffers()) {
                if (offer.isOutOfStock()) {
                    continue;
                }

                if (quote.satisfiedBy(offer)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void drop(Level level, BlockPos blockPos) {
        Containers.dropContents(level, blockPos, inputs);
        Containers.dropContents(level, blockPos, outputs);
        Containers.dropContents(level, blockPos, quotes);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);

        inputs.fromTag(compoundTag.getList("Inputs", Tag.TAG_COMPOUND), provider);
        outputs.fromTag(compoundTag.getList("Outputs", Tag.TAG_COMPOUND), provider);
        quotes.fromTag(compoundTag.getList("Quotes", Tag.TAG_COMPOUND), provider);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);

        compoundTag.put("Inputs", inputs.createTag(provider));
        compoundTag.put("Outputs", outputs.createTag(provider));
        compoundTag.put("Quotes", quotes.createTag(provider));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        var nbt = super.getUpdateTag(provider);
        saveAdditional(nbt, provider);
        return nbt;
    }

    @Override
    public void containerChanged(Container container) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().blockChanged(getBlockPos());
        }
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new TradingStationMenu(containerId, inventory, this, inputs, outputs, quotes);
    }
}
