package me.ramidzkh.vts;

import me.ramidzkh.vts.block.TradingStationScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.MenuScreens;

@Environment(EnvType.CLIENT)
public interface VillagerTradingStationClient {

    static void initialize() {
        MenuScreens.register(VillagerTradingStation.TRADING_STATION_MENU, TradingStationScreen::new);
    }
}
