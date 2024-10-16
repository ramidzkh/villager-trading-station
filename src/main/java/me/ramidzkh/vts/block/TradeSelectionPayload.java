package me.ramidzkh.vts.block;

import me.ramidzkh.vts.VillagerTradingStation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record TradeSelectionPayload(int shopItem) implements CustomPacketPayload {

    public static final Type<TradeSelectionPayload> TYPE = new Type<>(VillagerTradingStation.id("inscribe_quote"));
    public static final StreamCodec<FriendlyByteBuf, TradeSelectionPayload> CODEC = StreamCodec
            .composite(ByteBufCodecs.VAR_INT, TradeSelectionPayload::shopItem, TradeSelectionPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
