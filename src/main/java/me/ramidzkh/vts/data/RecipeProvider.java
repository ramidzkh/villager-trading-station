package me.ramidzkh.vts.data;

import me.ramidzkh.vts.VillagerTradingStation;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends FabricRecipeProvider {

    public RecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    protected net.minecraft.data.recipes.RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup,
            RecipeOutput exporter) {
        return new net.minecraft.data.recipes.RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                shaped(RecipeCategory.MISC, VillagerTradingStation.TRADING_STATION_ITEM)
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
        };
    }

    @Override
    public String getName() {
        return "Recipes";
    }
}
