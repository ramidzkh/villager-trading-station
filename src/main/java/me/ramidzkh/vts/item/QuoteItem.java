package me.ramidzkh.vts.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class QuoteItem extends Item {

    public QuoteItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        var hand = player.getItemInHand(interactionHand);

        if (player.isShiftKeyDown()) {
            try (var transaction = Transaction.openOuter()) {
                var storage = PlayerInventoryStorage.of(player);

                if (player.getAbilities().instabuild || storage.extract(ItemVariant.of(hand), 1, transaction) == 1) {
                    storage.offerOrDrop(ItemVariant.of(Items.PAPER), 1, transaction);
                    transaction.commit();
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }
}
