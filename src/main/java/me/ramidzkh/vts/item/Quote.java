package me.ramidzkh.vts.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.Optional;

public record Quote(ItemCost a, Optional<ItemCost> b, ItemStack result) {

    // spotless:off
    public static final Codec<Quote> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemCost.CODEC.fieldOf("a").forGetter(Quote::a),
            ItemCost.CODEC.lenientOptionalFieldOf("b").forGetter(Quote::b),
            ItemStack.CODEC.fieldOf("result").forGetter(Quote::result)
    ).apply(instance, Quote::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Quote> STREAM_CODEC = StreamCodec.composite(
            ItemCost.STREAM_CODEC,
            Quote::a,
            ItemCost.OPTIONAL_STREAM_CODEC,
            Quote::b,
            ItemStack.STREAM_CODEC,
            Quote::result,
            Quote::new);
    // spotless:on

    // MerchantOffer.satisfiedBy
    public boolean satisfiedBy(MerchantOffer offer) {
        return satisfied(offer.getCostA(), this.a)
                && b.map(cost -> satisfied(offer.getCostB(), cost)).orElse(true);
    }

    private static boolean satisfied(ItemStack item, ItemCost cost) {
        return item.is(cost.item()) && cost.count() == item.getCount() && cost.components().test(item);
    }
}
