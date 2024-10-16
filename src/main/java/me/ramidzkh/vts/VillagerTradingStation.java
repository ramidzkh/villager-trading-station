package me.ramidzkh.vts;

import me.ramidzkh.vts.ai.TradingStationSensor;
import me.ramidzkh.vts.block.*;
import me.ramidzkh.vts.item.Quote;
import me.ramidzkh.vts.item.QuoteItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.List;
import java.util.Optional;

public interface VillagerTradingStation {

    String MOD_ID = "villager-trading-station";

    static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    ResourceLocation TRADING_STATION = id("trading_station");

    Block TRADING_STATION_BLOCK = new TradingStationBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.FIRE)
            // .strength(2.0F, 3.0F)
            .sound(SoundType.WOOD)
            .strength(0.5F)
            .requiresCorrectToolForDrops());

    BlockEntityType<TradingStationBlockEntity> TRADING_STATION_BLOCK_ENTITY = BlockEntityType.Builder
            .of((pos, state) -> {
                return new TradingStationBlockEntity(VillagerTradingStation.TRADING_STATION_BLOCK_ENTITY, pos, state);
            }, TRADING_STATION_BLOCK).build();

    Item QUOTE_ITEM = new QuoteItem(new Item.Properties().stacksTo(1));

    Item TRADING_STATION_ITEM = new BlockItem(TRADING_STATION_BLOCK, new Item.Properties());

    TagKey<Item> QUOTE_CONVERTABLE = TagKey.create(Registries.ITEM, id("quote_convertable"));

    DataComponentType<Quote> QUOTE = DataComponentType.<Quote>builder()
            .persistent(Quote.CODEC)
            .networkSynchronized(Quote.STREAM_CODEC)
            .cacheEncoding()
            .build();

    MemoryModuleType<List<GlobalPos>> STATION_SITE = new MemoryModuleType<>(Optional.empty());
    SensorType<TradingStationSensor> STATION_SENSOR = new SensorType<>(TradingStationSensor::new);

    MenuType<TradingStationMenu> TRADING_STATION_MENU = new MenuType<>(TradingStationMenu::new,
            FeatureFlags.VANILLA_SET);

    static void initialize() {
        Registry.register(BuiltInRegistries.BLOCK, VillagerTradingStation.TRADING_STATION, TRADING_STATION_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, TRADING_STATION, TRADING_STATION_BLOCK_ENTITY);

        Registry.register(BuiltInRegistries.ITEM, id("quote"), QUOTE_ITEM);
        Registry.register(BuiltInRegistries.ITEM, TRADING_STATION, TRADING_STATION_ITEM);

        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id("quote"), QUOTE);

        Registry.register(BuiltInRegistries.MEMORY_MODULE_TYPE, TRADING_STATION, STATION_SITE);
        Registry.register(BuiltInRegistries.SENSOR_TYPE, TRADING_STATION, STATION_SENSOR);

        Registry.register(BuiltInRegistries.MENU, TRADING_STATION, TRADING_STATION_MENU);

        PayloadTypeRegistry.playC2S().register(TradeSelectionPayload.TYPE, TradeSelectionPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(TradeSelectionPayload.TYPE, MerchantScreenHandler::onInscribeQuote);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            entries.accept(TRADING_STATION_ITEM, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        });
    }
}
