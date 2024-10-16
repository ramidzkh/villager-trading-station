package me.ramidzkh.vts.item;

import me.ramidzkh.vts.VillagerTradingStation;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class QuoteItem extends Item {

    public QuoteItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list,
            TooltipFlag tooltipFlag) {
        var quote = itemStack.get(VillagerTradingStation.QUOTE);

        if (quote == null) {
            return;
        }

        var a = quote.a();
        var b = quote.b();
        var result = quote.result();

        list.add(Component.literal("- Requires ").append(a.itemStack().getDisplayName()).append(" * ")
                .append(Integer.toString(a.count())));

        b.ifPresent(cost -> {
            list.add(Component.literal("- Requires ").append(cost.itemStack().getDisplayName()).append(" * ")
                    .append(Integer.toString(cost.count())));
        });

        list.add(Component.literal("- Trades for ").append(result.getDisplayName()).append(" * ")
                .append(Integer.toString(result.getCount())));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        var hand = player.getItemInHand(interactionHand);

        if (player.isShiftKeyDown()) {
            try (var transaction = Transaction.openOuter()) {
                var storage = PlayerInventoryStorage.of(player);

                if (player.getAbilities().instabuild || storage.extract(ItemVariant.of(hand), 1, transaction) == 1) {
                    storage.offerOrDrop(ItemVariant.of(Items.PAPER), 1, transaction);
                    transaction.commit();
                    return InteractionResultHolder.success(player.getItemInHand(interactionHand));
                }
            }
        }

        return InteractionResultHolder.pass(hand);
    }
}
