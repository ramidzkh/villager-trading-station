package me.ramidzkh.vts.data;

import me.ramidzkh.vts.VillagerTradingStation;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static me.ramidzkh.vts.VillagerTradingStation.id;

public class ModelProvider extends FabricModelProvider {

    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        generator.blockStateOutput.accept(MultiVariantGenerator
                .dispatch(VillagerTradingStation.TRADING_STATION_BLOCK)
                .with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_AXIS)
                        .select(Direction.Axis.X, BlockModelGenerators.plainVariant(id("block/trading_station")))
                        .select(Direction.Axis.Z, BlockModelGenerators.plainVariant(id("block/trading_station"))
                                .with(BlockModelGenerators.Y_ROT_90))));
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        generator.generateFlatItem(VillagerTradingStation.QUOTE_ITEM, ModelTemplates.FLAT_ITEM);
    }
}
