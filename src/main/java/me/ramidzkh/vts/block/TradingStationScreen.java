package me.ramidzkh.vts.block;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.ramidzkh.vts.VillagerTradingStation;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TradingStationScreen extends AbstractContainerScreen<TradingStationMenu> {

    private static final ResourceLocation CONTAINER_LOCATION = VillagerTradingStation.id("textures/gui/station.png");

    public TradingStationScreen(TradingStationMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float tickDelta) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, tickDelta);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float tickDelta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, CONTAINER_LOCATION);
        var k = (this.width - this.imageWidth) / 2;
        var l = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, k, l, 0, 0, this.imageWidth, this.imageHeight);
    }
}
