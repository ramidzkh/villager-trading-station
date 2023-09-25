package me.ramidzkh.vts;

import me.ramidzkh.vts.block.TradingStationBlockEntityRenderer;
import me.ramidzkh.vts.block.TradingStationScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

@Environment(EnvType.CLIENT)
public interface VillagerTradingStationClient {

    static void initialize() {
        BlockEntityRenderers.register(VillagerTradingStation.TRADING_STATION_BLOCK_ENTITY,
                context -> new TradingStationBlockEntityRenderer());
        MenuScreens.register(VillagerTradingStation.TRADING_STATION_MENU, TradingStationScreen::new);
    }
}
