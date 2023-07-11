package me.ramidzkh.vts.data;

import me.ramidzkh.vts.VillagerTradingStation;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class BlockTagsProvider extends FabricTagProvider.BlockTagProvider {

    public BlockTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(ResourceKey.create(Registries.BLOCK, VillagerTradingStation.TRADING_STATION));
    }
}
