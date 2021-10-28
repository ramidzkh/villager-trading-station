package me.ramidzkh.vts.gui.client;

import io.github.astrarre.gui.v1.api.comms.PacketHandler;
import io.github.astrarre.gui.v1.api.component.ACenteringPanel;
import io.github.astrarre.gui.v1.api.component.AGrid;
import io.github.astrarre.gui.v1.api.component.AIcon;
import io.github.astrarre.gui.v1.api.component.ARootPanel;
import io.github.astrarre.gui.v1.api.component.slot.ASlot;
import io.github.astrarre.gui.v1.api.component.slot.SlotKey;
import io.github.astrarre.gui.v1.api.util.Transformed;
import io.github.astrarre.rendering.v1.api.plane.Transform2d;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.plane.icon.backgrounds.ContainerBackgroundIcon;
import io.github.astrarre.rendering.v1.api.space.Transform3d;
import me.ramidzkh.vts.block.TradingStationBlockEntity;
import me.ramidzkh.vts.gui.Renderstuff;
import me.ramidzkh.vts.util.GuiHelper;

import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class VillagerTradingClientPanel {

    public VillagerTradingClientPanel(PacketHandler packetHandler, ARootPanel rootPanel, TradingStationBlockEntity blockEntity, ArrayList<List<SlotKey>> listKey) {

        List<SlotKey> playerKey = listKey.get(0), inputKey = listKey.get(1), outputKey = listKey.get(2), quoteKey = listKey.get(3);

        ACenteringPanel panel = new ACenteringPanel(rootPanel);

        panel.add(new AIcon(new ContainerBackgroundIcon(180, 175)));

        AGrid inputGrid = GuiHelper.fillGrid(new AGrid(18, 3, 3), packetHandler, rootPanel, inputKey);
        AGrid outputGrid = GuiHelper.fillGrid(new AGrid(18,3, 3), packetHandler, rootPanel, outputKey);
        AGrid quoteGrid = GuiHelper.fillGrid(new AGrid(18,1, blockEntity.getQuoteContainer().getContainerSize()), packetHandler, rootPanel, quoteKey);

        panel.add(inputGrid.with(Transform3d.translate(35, 20, 0)));
        panel.add(outputGrid.with(Transform3d.translate(105, 20, 0)));
        panel.add(quoteGrid.with(Transform3d.translate(10, 20, 0)));
        for (Transformed<?> transformed : quoteGrid) {
            ((ASlot) transformed.component()).setIcon(new Renderstuff(18, 18, Icon.item(Items.PAPER)));
        }

        panel.add(ASlot.playerInv(packetHandler, rootPanel, playerKey).with(Transform3d.translate(9, 90, 100)));
        rootPanel.add(panel);
    }
}
