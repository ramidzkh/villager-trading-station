package me.ramidzkh.vts.block;

import me.ramidzkh.vts.VillagerTradingStation;
import me.ramidzkh.vts.item.Quote;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MerchantScreenHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void onInscribeQuote(TradeSelectionPayload payload, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if (context.player().containerMenu instanceof MerchantMenu menu) {
                if (!inscribe(context.player(), payload.shopItem(), menu)) {
                    LOGGER.warn("Player {} sent a packet to inscribe a trade, but it could not be performed",
                            context.player());
                }
            }
        });
    }

    public static boolean inscribe(Player player, int shopItem, MerchantMenu menu) {
        var cursor = menu.getCarried();

        if (cursor.is(VillagerTradingStation.QUOTE_CONVERTABLE)) {
            var offer = menu.getOffers().get(shopItem);

            var quote = new ItemStack(VillagerTradingStation.QUOTE_ITEM);
            quote.set(VillagerTradingStation.QUOTE,
                    new Quote(offer.getItemCostA(), offer.getItemCostB(), offer.getResult()));

            if (!player.hasInfiniteMaterials()) {
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
