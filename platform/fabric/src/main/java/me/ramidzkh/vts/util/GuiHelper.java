package me.ramidzkh.vts.util;

import io.github.astrarre.gui.v1.api.comms.PacketHandler;
import io.github.astrarre.gui.v1.api.component.AGrid;
import io.github.astrarre.gui.v1.api.component.ARootPanel;
import io.github.astrarre.gui.v1.api.component.slot.ASlot;
import io.github.astrarre.gui.v1.api.component.slot.SlotKey;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayDeque;
import java.util.List;

public class GuiHelper {

    public static AGrid fillGrid(AGrid grid, PacketHandler communication, ARootPanel panel, List<SlotKey> key) {
        for(int row = 0; row < grid.cellsY; row++) {
            for(int column = 0; column < grid.cellsX; column++) {
                int index = grid.cellsX + (row * grid.cellsX) + column;
                grid.add(new ASlot(communication, panel, key.get(index)));
            }
        }
        return grid;
    }

    @SafeVarargs
    public static ArrayDeque<List<SlotKey>> fillDeque(Player player, List<SlotKey>... lists) {
        ArrayDeque<List<SlotKey>> deque = new ArrayDeque<>();
        List<SlotKey> playerKey = SlotKey.player(player, 0);
        for (List<SlotKey> key : lists) {
            playerKey.forEach(k -> k.linkAllPre(key));
            key.forEach(k -> k.linkAll(playerKey));

            deque.add(key);
        }
        deque.add(playerKey);

        return deque;
    }
}
