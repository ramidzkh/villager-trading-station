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

import java.util.ArrayDeque;
import java.util.List;

public class VillagerTradingClientPanel {

    public VillagerTradingClientPanel(PacketHandler packetHandler, ARootPanel rootPanel, TradingStationBlockEntity blockEntity, ArrayDeque<List<SlotKey>> listKey) {

        List<SlotKey> playerKey = listKey.pollLast(), inputKey = listKey.poll(), outputKey = listKey.poll(), quoteKey = listKey.poll();

        ACenteringPanel panel = new ACenteringPanel(rootPanel);

        panel.add(new AIcon(new ContainerBackgroundIcon(320, 240)));

        AGrid inputGrid = GuiHelper.fillGrid(new AGrid(3, 3), packetHandler, rootPanel, inputKey);
        AGrid outputGrid = GuiHelper.fillGrid(new AGrid(3, 3), packetHandler, rootPanel, outputKey);
        AGrid quoteGrid = GuiHelper.fillGrid(new AGrid(1, blockEntity.getQuoteContainer().getContainerSize()), packetHandler, rootPanel, quoteKey);

        panel.add(inputGrid.with(Transform3d.translate(60, 120, 0)));
        panel.add(outputGrid.with(Transform3d.translate(260, 120, 0)));
        panel.add(quoteGrid.with(Transform3d.translate(10, 120, 0)));

        rootPanel.add(ASlot.playerInv(packetHandler, rootPanel, playerKey));
    }
}
