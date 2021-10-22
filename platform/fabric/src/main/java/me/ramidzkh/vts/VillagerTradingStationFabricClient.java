package me.ramidzkh.vts;

import me.ramidzkh.vts.block.TradingStationBlockEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

@Environment(EnvType.CLIENT)
public interface VillagerTradingStationFabricClient {

    static void initialize() {
        BlockEntityRendererRegistry.register(VillagerTradingStationFabric.BlockEntities.TRADING_STATION, context -> new TradingStationBlockEntityRenderer());
    }
}
