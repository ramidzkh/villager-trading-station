package me.ramidzkh.vts;

import me.ramidzkh.vts.ai.TradingStationSensor;
import me.ramidzkh.vts.block.*;
import me.ramidzkh.vts.item.Quote;
import me.ramidzkh.vts.item.QuoteItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.Util;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    ResourceLocation TRADING_STATION = id("trading_station");

    Block TRADING_STATION_BLOCK = Util.make(() -> {
        BlockBehaviour.Properties settings = BlockBehaviour.Properties.of()
                .mapColor(MapColor.FIRE)
                // .strength(2.0F, 3.0F)
                .sound(SoundType.WOOD)
                .strength(0.5F)
                .requiresCorrectToolForDrops();
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id("trading_station"));
        return Registry.register(BuiltInRegistries.BLOCK, key, new TradingStationBlock(settings.setId(key)));
    });

    BlockEntityType<TradingStationBlockEntity> TRADING_STATION_BLOCK_ENTITY = FabricBlockEntityTypeBuilder
            .create((pos, state) -> {
                return new TradingStationBlockEntity(VillagerTradingStation.TRADING_STATION_BLOCK_ENTITY, pos, state);
            }, TRADING_STATION_BLOCK).build();

    Item QUOTE_ITEM = Util.make(() -> {
        Item.Properties settings = new Item.Properties().stacksTo(1);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id("quote"));
        return Registry.register(BuiltInRegistries.ITEM, key, new QuoteItem(settings.setId(key)));
    });

    Item TRADING_STATION_ITEM = Util.make(() -> {
        Item.Properties settings = new Item.Properties().useBlockDescriptionPrefix();
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id("trading_station"));
        return Registry.register(BuiltInRegistries.ITEM, key,
                new BlockItem(TRADING_STATION_BLOCK, settings.setId(key)));
    });

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
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, TRADING_STATION, TRADING_STATION_BLOCK_ENTITY);

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
