package me.ramidzkh.vts.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class VillagerTradingStationDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
        var pack = dataGenerator.createPack();
        var blockTagsProvider = pack.addProvider(BlockTagsProvider::new);
        pack.addProvider((output, registriesFuture) -> new ItemTagsProvider(output, registriesFuture, blockTagsProvider));
        pack.addProvider(RecipeProvider::new);
        pack.addProvider(BlockLootTableProvider::new);

        pack.addProvider(ModelProvider::new);
    }
}
