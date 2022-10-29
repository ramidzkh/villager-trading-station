package me.ramidzkh.vts.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class TradingStationBlockEntityRenderer implements BlockEntityRenderer<TradingStationBlockEntity> {

    @Override
    public void render(TradingStationBlockEntity tradingStation, float partialTicks, PoseStack matrices,
            MultiBufferSource multiBufferSource, int light, int overlay) {
        var variants = new ArrayList<ItemStack>();

        try (var transaction = Transaction.openOuter()) {
            for (var view : tradingStation.getStorage().iterable(transaction)) {
                if (!view.isResourceBlank()) {
                    var o = view.getResource().toStack();
                    o.enchant(Enchantments.KNOCKBACK, 1);
                    variants.add(o);
                }
            }
        }

        var angles = new float[variants.size()];

        for (var i = 0; i < angles.length; i++) {
            angles[i] = i * 360F / variants.size();
        }

        var time = ((int) tradingStation.getLevel().getGameTime()) + partialTicks;

        for (var i = 0; i < variants.size(); i++) {
            matrices.pushPose();
            matrices.translate(0.5F, 1.25F, 0.5F);
            matrices.mulPose(Vector3f.YP.rotationDegrees(angles[i] + time));
            matrices.translate(0.875F, 0F, 0.25F);
            matrices.mulPose(Vector3f.YP.rotationDegrees(90F));
            matrices.translate(0D, 0.075 * Mth.sin((time + i * 10) / 5), 0F);
            Minecraft.getInstance().getItemRenderer().renderStatic(variants.get(i), ItemTransforms.TransformType.GROUND,
                    light, overlay, matrices, multiBufferSource, 0);
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
