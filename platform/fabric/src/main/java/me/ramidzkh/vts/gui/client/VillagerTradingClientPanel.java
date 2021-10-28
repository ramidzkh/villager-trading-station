package me.ramidzkh.vts.gui.client;

import io.github.astrarre.gui.v1.api.comms.PacketHandler;
import io.github.astrarre.gui.v1.api.component.*;
import io.github.astrarre.gui.v1.api.component.slot.ASlot;
import io.github.astrarre.gui.v1.api.component.slot.SlotKey;
import io.github.astrarre.rendering.v1.api.plane.icon.backgrounds.ContainerBackgroundIcon;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import io.github.astrarre.rendering.v1.api.util.Axis2d;
import me.ramidzkh.vts.util.GuiHelper;

import java.util.ArrayList;
import java.util.List;

public class VillagerTradingClientPanel {

    public VillagerTradingClientPanel(PacketHandler packetHandler, ARootPanel rootPanel, ArrayList<List<SlotKey>> listKey) {

        List<SlotKey> playerKey = listKey.get(0), inputKey = listKey.get(1), outputKey = listKey.get(2), quoteKey = listKey.get(3);

        ACenteringPanel panel = new ACenteringPanel(rootPanel);

        panel.add(new AIcon(new ContainerBackgroundIcon(180, 175)));

        AGrid inputGrid = GuiHelper.fillGrid(new AGrid(18, 3, 3), packetHandler, rootPanel, inputKey);
        AGrid outputGrid = GuiHelper.fillGrid(new AGrid(18,3, 3), packetHandler, rootPanel, outputKey);
        AList quoteGrid = GuiHelper.fillList(new AList(Axis2d.Y), packetHandler, rootPanel, quoteKey);

        panel.add(inputGrid.with(Transform3d.translate(35, 20, 0)));
        panel.add(outputGrid.with(Transform3d.translate(105, 20, 0)));
        panel.add(quoteGrid.with(Transform3d.translate(10, 20, 0)));

        panel.add(ASlot.playerInv(packetHandler, rootPanel, playerKey).with(Transform3d.translate(9, 90, 100)));
        rootPanel.add(panel);
    }
}
