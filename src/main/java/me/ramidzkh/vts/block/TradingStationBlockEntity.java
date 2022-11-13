package me.ramidzkh.vts.block;

import me.ramidzkh.vts.VillagerTradingStation;
import me.ramidzkh.vts.item.QuoteItem;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TradingStationBlockEntity extends BlockEntity implements ContainerListener, MenuProvider {

    private final SimpleContainer inputs = new SimpleContainer(9);
    private final SimpleContainer outputs = new SimpleContainer(9);
    private final SimpleContainer quotes = new SimpleContainer(3);

    private final Storage<ItemVariant> inputStorage = InventoryStorage.of(inputs, null);
    private final Storage<ItemVariant> outputStorage = InventoryStorage.of(outputs, null);
    private final Storage<ItemVariant> quoteStorage = new FilteringStorage<>(InventoryStorage.of(quotes, null)) {
        @Override
        protected boolean canInsert(ItemVariant resource) {
            return resource.isOf(VillagerTradingStation.QUOTE_ITEM);
        }
    };
    private final Storage<ItemVariant> exposed = new CombinedStorage<>(List.of(
            FilteringStorage.insertOnlyOf(inputStorage),
            FilteringStorage.extractOnlyOf(outputStorage)));

    public TradingStationBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);

        inputs.addListener(this);
        outputs.addListener(this);
        quotes.addListener(this);
    }

    public Storage<ItemVariant> getStorage() {
        return exposed;
    }

    public Storage<ItemVariant> getQuotes() {
        return quoteStorage;
    }

    public void interact(AbstractVillager villager) {
        for (var i = 0; i < quotes.getContainerSize(); i++) {
            var stack = quotes.getItem(i);

            if (!(stack.getItem()instanceof QuoteItem quoteItem)) {
                continue;
            }

            var quote = quoteItem.getQuote(stack);
            MerchantOffer myOffer = null;

            for (var offer : villager.getOffers()) {
                if (offer.isOutOfStock()) {
                    continue;
                }

                // MerchantOffer#satisfiedBy but exact amounts
                if (ItemStack.isSame(offer.getResult(), quote.result())
                        && offer.satisfiedBy(quote.a(), quote.b())
                        && offer.getCostA().getCount() == quote.a().getCount()
                        && offer.getCostB().getCount() == quote.b().getCount()) {
                    myOffer = offer;
                    break;
                }
            }

            if (myOffer != null) {
                try (var transaction = Transaction.openOuter()) {
                    if (inputStorage.extract(ItemVariant.of(quote.a()), quote.a().getCount(), transaction) == quote.a()
                            .getCount()
                            && (quote.b().isEmpty() || inputStorage.extract(ItemVariant.of(quote.b()),
                                    quote.b().getCount(), transaction) == quote.b().getCount())
                            && outputStorage.insert(ItemVariant.of(quote.result()), quote.result().getCount(),
                                    transaction) == quote.result().getCount()) {
                        villager.notifyTrade(myOffer);
                        transaction.commit();
                    }
                }
            }
        }
    }

    public boolean canInteract(AbstractVillager villager) {
        for (var i = 0; i < quotes.getContainerSize(); i++) {
            var stack = quotes.getItem(i);

            if (!(stack.getItem()instanceof QuoteItem quoteItem)) {
                continue;
            }

            var quote = quoteItem.getQuote(stack);

            for (var offer : villager.getOffers()) {
                if (offer.isOutOfStock()) {
                    continue;
                }

                // MerchantOffer#satisfiedBy but exact amounts
                if (ItemStack.isSame(offer.getResult(), quote.result())
                        && offer.satisfiedBy(quote.a(), quote.b())
                        && offer.getCostA().getCount() == quote.a().getCount()
                        && offer.getCostB().getCount() == quote.b().getCount()) {
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
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);

        inputs.fromTag(compoundTag.getList("Inputs", NbtType.COMPOUND));
        outputs.fromTag(compoundTag.getList("Outputs", NbtType.COMPOUND));
        quotes.fromTag(compoundTag.getList("Quotes", NbtType.COMPOUND));
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        compoundTag.put("Inputs", inputs.createTag());
        compoundTag.put("Outputs", outputs.createTag());
        compoundTag.put("Quotes", quotes.createTag());
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        var nbt = super.getUpdateTag();
        saveAdditional(nbt);
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
