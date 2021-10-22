package me.ramidzkh.vts;

import me.ramidzkh.vts.block.TradingStationBlock;
import me.ramidzkh.vts.block.TradingStationBlockEntity;
import me.ramidzkh.vts.item.QuoteItem;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;

import static me.ramidzkh.vts.VillagerTradingStation.id;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public interface VillagerTradingStationFabric {

    static void initialize() {
        Blocks.register();
        BlockEntities.register();
        Items.register();

        ServerPlayNetworking.registerGlobalReceiver(VillagerTradingStation.PacketIds.INSCRIBE_QUOTE, MerchantScreenHandler::onInscribeQuote);

        CommandRegistrationCallback.EVENT.register
((      dispatcher
,       dedicated
)->     dispatcher
.       register
(       literal
(       "testing"
).      then
(       argument
(       "pos"
,       BlockPosArgument
.       blockPos
()).    then
(       argument
(       "villager"
,       EntityArgument
.       entity
()).    executes
(       context
->{     BlockPos pos
=       BlockPosArgument
.       getLoadedBlockPos
(       context
,       "pos"
);      Entity villager
=       EntityArgument
.       getEntity
(       context,
        "villager"
);((    TradingStationBlockEntity
)       context
.       getSource
().     getLevel
().     getBlockEntity
(       pos
)).     interact
(                                                                                                                                                                                                               (AbstractVillager)
        villager
);      return 1
;})))));
    }

    interface Blocks {

        Block TRADING_STATION = new TradingStationBlock(FabricBlockSettings.of(Material.WOOD).strength(0.2F, 2.5F).requiresTool().breakByTool(FabricToolTags.HOES));

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

            ItemStorage.SIDED.registerForBlockEntity((tradingStationBlock, direction) -> tradingStationBlock.getStorage(), TRADING_STATION);
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
