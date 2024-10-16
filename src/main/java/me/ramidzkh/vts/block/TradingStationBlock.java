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

public class TradingStationBlock extends BaseEntityBlock {

    public static final MapCodec<TradingStationBlock> CODEC = simpleCodec(TradingStationBlock::new);

    private static final VoxelShape[] SHAPES = new VoxelShape[3];

    static {
        var shape = SHAPES[Direction.Axis.X.ordinal()] = Shapes.or(
                Shapes.box(0.4375, 0.21875, 0.4375, 0.5625, 0.84375, 0.5625),
                Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.0625, 0.9375),
                Shapes.box(0.421875, 0.9375, 0.21875, 0.578125, 1, 0.78125),
                Shapes.box(0.1875, 0.59375, 0.84375, 0.8125, 0.65625, 0.90625),
                Shapes.box(0.15625, 0.34375, 0.84375, 0.21875, 0.59375, 0.90625),
                Shapes.box(0.78125, 0.34375, 0.84375, 0.84375, 0.59375, 0.90625),
                Shapes.box(0.15625, 0.28125, 0.71875, 0.84375, 0.34375, 1.03125),
                Shapes.box(0.25, 0.0625, 0.25, 0.75, 0.125, 0.75),
                Shapes.box(0.1875, 0.59375, 0.09375, 0.8125, 0.65625, 0.15625),
                Shapes.box(0.15625, 0.34375, 0.09375, 0.21875, 0.59375, 0.15625),
                Shapes.box(0.78125, 0.34375, 0.09375, 0.84375, 0.59375, 0.15625),
                Shapes.box(0.15625, 0.28125, -0.03125, 0.84375, 0.34375, 0.28125));
        SHAPES[Direction.Axis.Z.ordinal()] = rotateShape(Direction.NORTH, Direction.WEST, shape);
    }

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
        return SHAPES[blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS).ordinal()];
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
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
        return VillagerTradingStation.TRADING_STATION_BLOCK_ENTITY.create(blockPos, blockState);
    }

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        var buffer = new VoxelShape[] { shape, Shapes.empty() };
        var times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;

        for (var i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1],
                    Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }
}
