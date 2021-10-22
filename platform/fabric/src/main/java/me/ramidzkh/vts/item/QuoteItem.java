package me.ramidzkh.vts.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuoteItem extends Item {

    public QuoteItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        ItemStack a = getA(itemStack);
        ItemStack b = getB(itemStack);
        ItemStack result = getResult(itemStack);

        list.add(new TextComponent("- Requires ").append(a.getHoverName()).append(" * ").append(Integer.toString(a.getCount())));

        if (!b.isEmpty()) {
            list.add(new TextComponent("- Requires ").append(b.getHoverName()).append(" * ").append(Integer.toString(b.getCount())));
        }

        list.add(new TextComponent("- Trades for ").append(result.getHoverName()).append(" * ").append(Integer.toString(result.getCount())));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack hand = player.getItemInHand(interactionHand);

        if (player.isShiftKeyDown()) {
            try (Transaction transaction = Transaction.openOuter()) {
                PlayerInventoryStorage storage = PlayerInventoryStorage.of(player);

                if (player.getAbilities().instabuild || storage.extract(ItemVariant.of(hand), 1, transaction) == 1) {
                    storage.offerOrDrop(ItemVariant.of(Items.PAPER), 1, transaction);
                    transaction.commit();
                    return InteractionResultHolder.success(player.getItemInHand(interactionHand));
                }
            }
        }

        return InteractionResultHolder.pass(hand);
    }

    public Quote getQuote(ItemStack stack) {
        return new Quote(getA(stack), getB(stack), getResult(stack));
    }

    public void setOffer(ItemStack stack, MerchantOffer offer) {
        CompoundTag a = offer.getCostA().save(new CompoundTag());
        CompoundTag b = offer.getCostB().save(new CompoundTag());
        CompoundTag result = offer.getResult().save(new CompoundTag());

        CompoundTag tag = stack.getOrCreateTag();
        tag.put("A", a);
        tag.put("B", b);
        tag.put("Result", result);
    }

    private ItemStack getA(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag != null) {
            return ItemStack.of(tag.getCompound("A"));
        } else {
            return ItemStack.EMPTY;
        }
    }

    private ItemStack getB(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag != null) {
            return ItemStack.of(tag.getCompound("B"));
        } else {
            return ItemStack.EMPTY;
        }
    }

    private ItemStack getResult(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag != null) {
            return ItemStack.of(tag.getCompound("Result"));
        } else {
            return ItemStack.EMPTY;
        }
    }

    public record Quote(ItemStack a, ItemStack b, ItemStack result) {
    }
}
