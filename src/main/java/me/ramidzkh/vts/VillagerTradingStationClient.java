package me.ramidzkh.vts;

import me.ramidzkh.vts.block.TradingStationBlockEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

@Environment(EnvType.CLIENT)
public interface VillagerTradingStationClient {

    static void initialize() {
        BlockEntityRendererRegistry.register(VillagerTradingStation.TRADING_STATION_BLOCK_ENTITY,
                context -> new TradingStationBlockEntityRenderer());
    }
}
