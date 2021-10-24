package me.ramidzkh.vts.gui;

import io.github.astrarre.gui.v1.api.comms.PacketHandler;
import io.github.astrarre.gui.v1.api.component.slot.SlotKey;
import io.github.astrarre.gui.v1.api.server.ServerPanel;

import java.util.ArrayDeque;
import java.util.List;

public class VillagerTradingServerPanel {

    public VillagerTradingServerPanel(PacketHandler packetHandler, ServerPanel serverPanel, ArrayDeque<List<SlotKey>> listKey) {
        listKey.forEach(l -> l.forEach(k -> k.sync(packetHandler, serverPanel)));
    }
}
