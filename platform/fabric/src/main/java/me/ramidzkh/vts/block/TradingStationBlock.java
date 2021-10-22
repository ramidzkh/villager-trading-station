package me.ramidzkh.vts.block;

import com.google.common.base.Predicates;
import me.ramidzkh.vts.VillagerTradingStationFabric;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class TradingStationBlock extends BaseEntityBlock {

    public TradingStationBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        boolean success = false;

        if (level.getBlockEntity(blockPos) instanceof TradingStationBlockEntity tradingStation) {
            if (player.isShiftKeyDown()) {
                try (Transaction transaction = Transaction.openOuter()) {
                    success = StorageUtil.move(tradingStation.getQuotes(), PlayerInventoryStorage.of(player), Predicates.alwaysTrue(), 1, transaction) == 1;

                    if (success) {
                        transaction.commit();
                    }
                }
            } else {
                ItemStack hand = player.getItemInHand(interactionHand);

                try (Transaction transaction = Transaction.openOuter()) {
                    if (tradingStation.getQuotes().insert(ItemVariant.of(hand), 1, transaction) == 1) {
                        if (!player.getAbilities().instabuild) {
                            hand.shrink(1);
                        }

                        transaction.commit();
                        success = true;
                    }
                }
            }

        }

        return success ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (level.getBlockEntity(blockPos) instanceof TradingStationBlockEntity tradingStation) {
            tradingStation.drop(level, blockPos);
        }

        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return VillagerTradingStationFabric.BlockEntities.TRADING_STATION.create(blockPos, blockState);
    }
}
