package me.ramidzkh.vts.block;

import me.ramidzkh.vts.VillagerTradingStation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class TradingStationMenu extends AbstractContainerMenu {

    private final TradingStationRecord record;

    public TradingStationMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, null, new SimpleContainer(9), new SimpleContainer(9), new SimpleContainer(3));
    }

    public TradingStationMenu(int containerId, Inventory inventory, @Nullable TradingStationBlockEntity tradingStation,
            Container inputs, Container outputs,
            Container quotes) {
        super(VillagerTradingStation.TRADING_STATION_MENU, containerId);

        if (tradingStation != null && tradingStation.getLevel()instanceof ServerLevel serverLevel) {
            this.record = new TradingStationRecord(serverLevel, tradingStation.getBlockPos(), tradingStation);
        } else {
            this.record = null;
        }

        for (var j = 0; j < 3; j++) {
            this.addSlot(new Slot(quotes, j, 8, 17 + j * 18) {
                @Override
                public boolean mayPlace(ItemStack itemStack) {
                    return itemStack.is(VillagerTradingStation.QUOTE_ITEM);
                }
            });
        }

        for (var j = 0; j < 3; j++) {
            for (var k = 0; k < 3; k++) {
                this.addSlot(new Slot(inputs, k + j * 3, 44 + k * 18, 17 + j * 18));
            }
        }

        for (var j = 0; j < 3; j++) {
            for (var k = 0; k < 3; k++) {
                this.addSlot(new Slot(outputs, k + j * 3, 116 + k * 18, 17 + j * 18) {
                    @Override
                    public boolean mayPlace(ItemStack itemStack) {
                        return false;
                    }
                });
            }
        }

        for (var j = 0; j < 3; j++) {
            for (var k = 0; k < 9; k++) {
                this.addSlot(new Slot(inventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }

        for (var j = 0; j < 9; j++) {
            this.addSlot(new Slot(inventory, j, 8 + j * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        var newStack = ItemStack.EMPTY;
        var slot = getSlot(invSlot);

        if (slot != null && slot.hasItem()) {
            var originalStack = slot.getItem();
            newStack = originalStack.copy();

            if (invSlot < 21 ? !this.moveItemStackTo(originalStack, 21, 57, true)
                    : !this.moveItemStackTo(originalStack, 0, 21, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (originalStack.getCount() == newStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, originalStack);
        }

        return newStack;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.record == null) {
            return true;
        }

        var pos = this.record.pos;

        if (this.record.level.getBlockEntity(pos) != record.tradingStation) {
            return false;
        }

        return !(player.distanceToSqr((double) pos.getX() + 0.5, (double) pos.getY() + 0.5,
                (double) pos.getZ() + 0.5) > 64.0);
    }

    private record TradingStationRecord(ServerLevel level, BlockPos pos, TradingStationBlockEntity tradingStation) {
    }
}
