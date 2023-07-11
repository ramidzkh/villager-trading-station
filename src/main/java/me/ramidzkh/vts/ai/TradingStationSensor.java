package me.ramidzkh.vts.ai;

import com.google.common.collect.ImmutableSet;
import me.ramidzkh.vts.VillagerTradingStation;
import me.ramidzkh.vts.block.TradingStationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.npc.Villager;

import java.util.ArrayList;
import java.util.Set;

public class TradingStationSensor extends Sensor<Villager> {

    private static final int SCAN_RATE = 40;

    public TradingStationSensor() {
        super(SCAN_RATE);
    }

    @Override
    protected void doTick(ServerLevel serverLevel, Villager villager) {
        var dimension = serverLevel.dimension();
        var list = new ArrayList<GlobalPos>();

        for (var pos : BlockPos.withinManhattan(villager.blockPosition(), 4, 2, 4)) {
            if (serverLevel.getBlockEntity(pos) instanceof TradingStationBlockEntity tradingStation
                    && tradingStation.canInteract(villager)) {
                list.add(GlobalPos.of(dimension, pos.immutable()));
            }
        }

        var brain = villager.getBrain();

        if (!list.isEmpty()) {
            brain.setMemory(VillagerTradingStation.STATION_SITE, list);
        } else {
            brain.eraseMemory(VillagerTradingStation.STATION_SITE);
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(VillagerTradingStation.STATION_SITE);
    }
}
