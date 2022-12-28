package me.ramidzkh.vts.data;

import me.ramidzkh.vts.VillagerTradingStation;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static me.ramidzkh.vts.VillagerTradingStation.id;

public class ModelProvider extends FabricModelProvider {

    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        generator.blockStateOutput.accept(MultiVariantGenerator
                .multiVariant(VillagerTradingStation.TRADING_STATION_BLOCK, Variant.variant()
                        .with(VariantProperties.MODEL, id("block/trading_station")))
                .with(PropertyDispatch.property(BlockStateProperties.HORIZONTAL_AXIS)
                        .select(Direction.Axis.X, Variant.variant())
                        .select(Direction.Axis.Z, Variant.variant()
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))));
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        generator.generateFlatItem(VillagerTradingStation.QUOTE_ITEM, ModelTemplates.FLAT_ITEM);
    }
}
