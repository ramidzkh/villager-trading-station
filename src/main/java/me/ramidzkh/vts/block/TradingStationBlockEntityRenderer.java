package me.ramidzkh.vts.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class TradingStationBlockEntityRenderer implements BlockEntityRenderer<TradingStationBlockEntity> {

    @Override
    public void render(TradingStationBlockEntity tradingStation, float partialTicks, PoseStack matrices,
            MultiBufferSource multiBufferSource, int light, int overlay) {
        var variants = new ArrayList<ItemStack>();

        for (var view : tradingStation.getItemStorage(null)) {
            if (!view.isResourceBlank()) {
                var o = view.getResource().toStack();
                // Gives glint
                o.enchant(Enchantments.KNOCKBACK, 1);
                variants.add(o);
            }
        }

        var time = tradingStation.getLevel().getGameTime() + partialTicks;

        for (var i = 0; i < variants.size(); i++) {
            matrices.pushPose();
            matrices.translate(0.5F, 1.25F, 0.5F);
            matrices.mulPose(Axis.YP.rotationDegrees(i * 360F / variants.size() + time));
            matrices.translate(0.875F, 0F, 0.25F);
            matrices.mulPose(Axis.YP.rotationDegrees(90F));
            matrices.translate(0D, 0.075 * Mth.sin((time + i * 10) / 5), 0F);
            Minecraft.getInstance().getItemRenderer().renderStatic(variants.get(i), ItemDisplayContext.GROUND,
                    light, overlay, matrices, multiBufferSource, tradingStation.getLevel(), 0);
            matrices.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen(TradingStationBlockEntity blockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(TradingStationBlockEntity blockEntity, Vec3 vec3) {
        return true;
    }
}
