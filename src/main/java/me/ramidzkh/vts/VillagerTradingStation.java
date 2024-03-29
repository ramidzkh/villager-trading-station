package me.ramidzkh.vts;

import me.ramidzkh.vts.ai.TradingStationSensor;
import me.ramidzkh.vts.block.MerchantScreenHandler;
import me.ramidzkh.vts.block.TradingStationBlock;
import me.ramidzkh.vts.block.TradingStationBlockEntity;
import me.ramidzkh.vts.block.TradingStationMenu;
import me.ramidzkh.vts.item.QuoteItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;
import java.util.Optional;

public interface VillagerTradingStation {

    String MOD_ID = "villager-trading-station";

    static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    ResourceLocation TRADING_STATION = id("trading_station");
    ResourceLocation INSCRIBE_QUOTE = id("inscribe_quote");

    Block TRADING_STATION_BLOCK = new TradingStationBlock(
            FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(0.5F).requiresTool());

    BlockEntityType<TradingStationBlockEntity> TRADING_STATION_BLOCK_ENTITY = FabricBlockEntityTypeBuilder
            .create((pos, state) -> {
                return new TradingStationBlockEntity(VillagerTradingStation.TRADING_STATION_BLOCK_ENTITY, pos, state);
            }, TRADING_STATION_BLOCK).build();

    Item QUOTE_ITEM = new QuoteItem(new Item.Properties().stacksTo(1));

    Item TRADING_STATION_ITEM = new BlockItem(TRADING_STATION_BLOCK, new Item.Properties());

    TagKey<Item> QUOTE_CONVERTABLE = TagKey.create(Registries.ITEM, id("quote_convertable"));

    MemoryModuleType<List<GlobalPos>> STATION_SITE = new MemoryModuleType<>(Optional.empty());
    SensorType<TradingStationSensor> STATION_SENSOR = new SensorType<>(TradingStationSensor::new);

    MenuType<TradingStationMenu> TRADING_STATION_MENU = new MenuType<>(TradingStationMenu::new,
            FeatureFlags.VANILLA_SET);

    static void initialize() {
        Registry.register(BuiltInRegistries.BLOCK, VillagerTradingStation.TRADING_STATION, TRADING_STATION_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, TRADING_STATION, TRADING_STATION_BLOCK_ENTITY);

        Registry.register(BuiltInRegistries.ITEM, id("quote"), QUOTE_ITEM);
        Registry.register(BuiltInRegistries.ITEM, TRADING_STATION, TRADING_STATION_ITEM);

        Registry.register(BuiltInRegistries.MEMORY_MODULE_TYPE, TRADING_STATION, STATION_SITE);
        Registry.register(BuiltInRegistries.SENSOR_TYPE, TRADING_STATION, STATION_SENSOR);

        Registry.register(BuiltInRegistries.MENU, TRADING_STATION, TRADING_STATION_MENU);

        ServerPlayNetworking.registerGlobalReceiver(VillagerTradingStation.INSCRIBE_QUOTE,
                MerchantScreenHandler::onInscribeQuote);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            entries.accept(TRADING_STATION_ITEM, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        });
    }
}
