package me.ramidzkh.vts.block;

import me.ramidzkh.vts.VillagerTradingStation;
import me.ramidzkh.vts.item.QuoteItem;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MerchantScreenHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void onInscribeQuote(MinecraftServer server, ServerPlayer player,
            ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        var shopItem = buf.readVarInt();

        server.execute(() -> {
            if (player.containerMenu instanceof MerchantMenu menu) {
                if (!inscribe(player, shopItem, menu)) {
                    LOGGER.warn("Player {} sent a packet to inscribe a trade, but it could not be performed", player);
                }
            }
        });
    }

    public static boolean inscribe(Player player, int shopItem, MerchantMenu menu) {
        var cursor = menu.getCarried();

        if (cursor.is(VillagerTradingStation.QUOTE_CONVERTABLE)) {
            var offer = menu.getOffers().get(shopItem);

            var quote = new ItemStack(VillagerTradingStation.QUOTE_ITEM);
            var item = (QuoteItem) quote.getItem();
            item.setQuote(quote, new QuoteItem.Quote(offer.getCostA(), offer.getCostB(), offer.getResult()));

            if (!player.getAbilities().instabuild) {
                cursor.shrink(1);
            }

            if (cursor.isEmpty()) {
                menu.setCarried(quote);
            } else {
                try (var transaction = Transaction.openOuter()) {
                    PlayerInventoryStorage.of(player).offerOrDrop(ItemVariant.of(quote), 1, transaction);
                    transaction.commit();
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
