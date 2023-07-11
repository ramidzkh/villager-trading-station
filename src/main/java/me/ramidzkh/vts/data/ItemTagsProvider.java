package me.ramidzkh.vts.data;

import me.ramidzkh.vts.VillagerTradingStation;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ItemTagsProvider extends FabricTagProvider.ItemTagProvider {

    public ItemTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture,
            @Nullable BlockTagProvider blockTagProvider) {
        super(output, registriesFuture, blockTagProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        getOrCreateTagBuilder(VillagerTradingStation.QUOTE_CONVERTABLE).add(Items.PAPER,
                VillagerTradingStation.QUOTE_ITEM);
    }
}
