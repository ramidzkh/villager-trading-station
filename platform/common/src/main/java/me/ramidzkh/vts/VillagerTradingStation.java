package me.ramidzkh.vts;

import net.minecraft.resources.ResourceLocation;

public interface VillagerTradingStation {

    String MOD_ID = "villager-trading-station";

    static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    interface BlockIds {

        ResourceLocation TRADING_STATION = id("trading_station");
    }

    interface BlockEntityIds {

        ResourceLocation TRADING_STATION = BlockIds.TRADING_STATION;
    }

    interface ItemIds {

        ResourceLocation QUOTE = id("quote");

        ResourceLocation TRADING_STATION = BlockIds.TRADING_STATION;
    }

    interface TagIds {

        ResourceLocation QUOTE_CONVERTABLE = id("quote_convertable");
    }

    interface PacketIds {

        ResourceLocation INSCRIBE_QUOTE = id("inscribe_quote");
    }
}
