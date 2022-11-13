package me.ramidzkh.vts;

import me.ramidzkh.vts.block.TradingStationBlockEntityRenderer;
import me.ramidzkh.vts.block.TradingStationScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

@Environment(EnvType.CLIENT)
public interface VillagerTradingStationClient {

    static void initialize() {
        BlockEntityRendererRegistry.register(VillagerTradingStation.TRADING_STATION_BLOCK_ENTITY,
                context -> new TradingStationBlockEntityRenderer());
        MenuScreens.register(VillagerTradingStation.TRADING_STATION_MENU, TradingStationScreen::new);
    }
}
