package me.ramidzkh.vts.block;

import com.mojang.serialization.MapCodec;
import me.ramidzkh.vts.VillagerTradingStation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TradingStationBlock extends BaseEntityBlock {

    public static final MapCodec<TradingStationBlock> CODEC = simpleCodec(TradingStationBlock::new);

    private static final Map<Direction.Axis, VoxelShape> SHAPES = Shapes.rotateHorizontalAxis(Shapes.or(
            Block.box(-0.5, 4.5, 2.5, 4.5, 5.5, 13.5),
            Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0),
            Block.box(1.5, 5.5, 2.5, 2.5, 9.5, 3.5),
            Block.box(1.5, 5.5, 12.5, 2.5, 9.5, 13.5),
            Block.box(1.5, 9.5, 3.0, 2.5, 10.5, 13.0),
            Block.box(3.5, 15.0, 6.8, 12.5, 16.0, 9.3),
            Block.box(4.0, 1.0, 4.0, 12.0, 2.0, 12.0),
            Block.box(7.0, 3.5, 7.0, 9.0, 13.5, 9.0),
            Block.box(11.5, 4.5, 2.5, 16.5, 5.5, 13.5),
            Block.box(13.5, 5.5, 2.5, 14.5, 9.5, 3.5),
            Block.box(13.5, 5.5, 12.5, 14.5, 9.5, 13.5),
            Block.box(13.5, 9.5, 3.0, 14.5, 10.5, 13.0)));

    public TradingStationBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_AXIS);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_AXIS,
                blockPlaceContext.getHorizontalDirection().getAxis());
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos,
            CollisionContext collisionContext) {
        return SHAPES.get(blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS));
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState blockState) {
        return Shapes.empty();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player,
            BlockHitResult blockHitResult) {
        if (!level.isClientSide()
                && level.getBlockEntity(blockPos) instanceof TradingStationBlockEntity tradingStation) {
            player.openMenu(tradingStation);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return VillagerTradingStation.TRADING_STATION_BLOCK_ENTITY.create(blockPos, blockState);
    }
}
