package me.ramidzkh.vts.mixins;

import me.ramidzkh.vts.block.MerchantScreenHandler;
import me.ramidzkh.vts.VillagerTradingStation;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends AbstractContainerScreen<MerchantMenu> {

    @Shadow
    private int shopItem;

    public MerchantScreenMixin(MerchantMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Inject(method = "postButtonClick", at = @At("HEAD"), cancellable = true)
    private void postButtonClick(CallbackInfo callbackInfo) {
        if (MerchantScreenHandler.inscribe(minecraft.player, shopItem, menu)) {
            ClientPlayNetworking.send(VillagerTradingStation.INSCRIBE_QUOTE,
                    PacketByteBufs.create().writeVarInt(shopItem));
            callbackInfo.cancel();
        }
    }
}
