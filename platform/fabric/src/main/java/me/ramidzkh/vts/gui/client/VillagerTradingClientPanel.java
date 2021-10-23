package me.ramidzkh.vts.gui.client;

import io.github.astrarre.gui.v1.api.comms.PacketHandler;
import io.github.astrarre.gui.v1.api.component.ACenteringPanel;
import io.github.astrarre.gui.v1.api.component.AGrid;
import io.github.astrarre.gui.v1.api.component.AIcon;
import io.github.astrarre.gui.v1.api.component.ARootPanel;
import io.github.astrarre.gui.v1.api.component.slot.ASlot;
import io.github.astrarre.gui.v1.api.component.slot.SlotKey;
import io.github.astrarre.rendering.v1.api.plane.icon.backgrounds.ContainerBackgroundIcon;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import me.ramidzkh.vts.block.TradingStationBlockEntity;
import me.ramidzkh.vts.util.GuiHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class VillagerTradingClientPanel {

    public VillagerTradingClientPanel(PacketHandler packetHandler, ARootPanel rootPanel, TradingStationBlockEntity blockEntity, Player player) {
        Container input = blockEntity.getInputContainer();
        Container output = blockEntity.getOutputContainer();
        Container quote = blockEntity.getQuoteContainer();

        List<SlotKey> playerKey = SlotKey.player(player, 0);
        List<SlotKey> inputKey = SlotKey.inv(input, 1);
        List<SlotKey> outputKey = SlotKey.inv(output, 2);
        List<SlotKey> quoteKey = SlotKey.inv(quote, 3);

        ACenteringPanel panel = new ACenteringPanel(rootPanel);

        panel.add(new AIcon(new ContainerBackgroundIcon(320, 240)));

        AGrid inputGrid = GuiHelper.fillGrid(new AGrid(3, 3), packetHandler, rootPanel, inputKey);
        AGrid outputGrid = GuiHelper.fillGrid(new AGrid(3, 3), packetHandler, rootPanel, outputKey);
        AGrid quoteGrid = GuiHelper.fillGrid(new AGrid(1, quote.getContainerSize()), packetHandler, rootPanel, quoteKey);

        panel.add(inputGrid.with(Transform3d.translate(60, 0, 0)));
        panel.add(outputGrid.with(Transform3d.translate(260, 0, 0)));
        panel.add(quoteGrid.with(Transform3d.translate(10, 0, 0)));

        rootPanel.add(ASlot.playerInv(packetHandler, rootPanel, playerKey));
    }
}
