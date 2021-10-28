package me.ramidzkh.vts.util;

import io.github.astrarre.gui.v1.api.comms.PacketHandler;
import io.github.astrarre.gui.v1.api.component.AGrid;
import io.github.astrarre.gui.v1.api.component.AList;
import io.github.astrarre.gui.v1.api.component.ARootPanel;
import io.github.astrarre.gui.v1.api.component.slot.ASlot;
import io.github.astrarre.gui.v1.api.component.slot.SlotKey;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import me.ramidzkh.vts.gui.QuoteIcon;
import net.minecraft.world.item.Items;

import java.util.List;

public class GuiHelper {

    public static AGrid fillGrid(AGrid grid, PacketHandler communication, ARootPanel panel, List<SlotKey> key) {
        for(int row = 0; row < grid.cellsY; row++) {
            for(int column = 0; column < grid.cellsX; column++) {
                int index = (grid.cellsX * row) + column;
                grid.add(new ASlot(communication, panel, key.get(index)));
            }
        }
        return grid;
    }

    public static AList fillList(AList list, PacketHandler communication, ARootPanel panel, List<SlotKey> key) {
        for(int i = 0; i < key.size(); i++) {
            list.add(new ASlot(communication, panel, key.get(i)).setIcon(new QuoteIcon(18, 18, Icon.item(Items.PAPER).offset(1, 1))));
        }
        return list;
    }
}
