package me.ramidzkh.vts;

import me.ramidzkh.vts.block.TradingStationBlock;
import me.ramidzkh.vts.block.TradingStationBlockEntity;
import me.ramidzkh.vts.item.QuoteItem;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Registry;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;

import static me.ramidzkh.vts.VillagerTradingStation.id;

public interface VillagerTradingStationFabric {

    static void initialize() {
        Blocks.register();
        BlockEntities.register();
        Items.register();

        ServerPlayNetworking.registerGlobalReceiver(VillagerTradingStation.PacketIds.INSCRIBE_QUOTE, MerchantScreenHandler::onInscribeQuote);
    }

    interface Blocks {

        Block TRADING_STATION = new TradingStationBlock(FabricBlockSettings.of(Material.WOOD).strength(0.2F, 2.5F).requiresTool().breakByTool(FabricToolTags.SHEARS));

        private static void register() {
            Registry.register(Registry.BLOCK, VillagerTradingStation.BlockIds.TRADING_STATION, TRADING_STATION);
        }
    }

    interface BlockEntities {

        BlockEntityType<TradingStationBlockEntity> TRADING_STATION = FabricBlockEntityTypeBuilder.create((pos, state) -> {
            return new TradingStationBlockEntity(BlockEntities.TRADING_STATION, pos, state);
        }, Blocks.TRADING_STATION).build();

        private static void register() {
            Registry.register(Registry.BLOCK_ENTITY_TYPE, VillagerTradingStation.BlockEntityIds.TRADING_STATION, TRADING_STATION);

            ItemStorage.SIDED.registerForBlockEntity(TradingStationBlockEntity::getStorage, TRADING_STATION);
        }
    }

    interface Items {

        CreativeModeTab TAB = FabricItemGroupBuilder.build(id("tab"), () -> new ItemStack(Items.TRADING_STATION));

        Item QUOTE = new QuoteItem(new Item.Properties().stacksTo(1));

        Item TRADING_STATION = new BlockItem(Blocks.TRADING_STATION, new Item.Properties().tab(TAB));

        private static void register() {
            Registry.register(Registry.ITEM, VillagerTradingStation.ItemIds.QUOTE, QUOTE);

            Registry.register(Registry.ITEM, VillagerTradingStation.ItemIds.TRADING_STATION, TRADING_STATION);
        }
    }

    interface Tags {

        Tag<Item> QUOTE_CONVERTABLE = TagFactory.ITEM.create(VillagerTradingStation.TagIds.QUOTE_CONVERTABLE);
    }
}
