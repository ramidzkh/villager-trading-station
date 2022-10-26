package me.ramidzkh.vts.data;

import me.ramidzkh.vts.VillagerTradingStation;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class RecipeProvider extends FabricRecipeProvider {

    public RecipeProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateRecipes(Consumer<FinishedRecipe> exporter) {
        ShapedRecipeBuilder.shaped(VillagerTradingStation.TRADING_STATION_ITEM)
                .pattern("GGG")
                .pattern("SCS")
                .pattern("INI")
                .define('G', Items.GOLD_NUGGET)
                .define('S', ItemTags.SLABS)
                .define('C', Items.COPPER_INGOT)
                .define('I', Items.RAW_IRON_BLOCK)
                .define('N', Items.NETHERITE_SCRAP)
                .unlockedBy("has_netherite_scrap", has(Items.NETHERITE_SCRAP))
                .save(exporter);
    }
}
