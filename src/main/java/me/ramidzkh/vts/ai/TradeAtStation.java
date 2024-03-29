package me.ramidzkh.vts.ai;

import com.google.common.collect.ImmutableMap;
import me.ramidzkh.vts.VillagerTradingStation;
import me.ramidzkh.vts.block.TradingStationBlockEntity;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;

import java.util.Collections;

public class TradeAtStation extends Behavior<Villager> {

    private static final int CHECK_COOLDOWN = 20;
    private static final double DISTANCE = 1.73;
    private long lastCheck;
    private GlobalPos targetPos;

    public TradeAtStation() {
        super(ImmutableMap.of(VillagerTradingStation.STATION_SITE, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Villager villager) {
        if (level.getGameTime() - this.lastCheck < CHECK_COOLDOWN) {
            return false;
        }

        this.lastCheck = level.getGameTime();

        for (var pos : villager.getBrain().getMemory(VillagerTradingStation.STATION_SITE)
                .orElse(Collections.emptyList())) {
            if (pos.dimension() == level.dimension()
                    && pos.pos().closerToCenterThan(villager.position(), DISTANCE)
                    && level.getBlockEntity(pos.pos()) instanceof TradingStationBlockEntity tradingStation
                    && tradingStation.canInteract(villager)) {
                this.targetPos = pos;
                return true;
            }
        }

        return false;
    }

    @Override
    protected void start(ServerLevel level, Villager villager, long time) {
        var brain = villager.getBrain();

        if (level.getBlockEntity(targetPos.pos()) instanceof TradingStationBlockEntity tradingStation) {
            brain.setMemory(MemoryModuleType.LAST_WORKED_AT_POI, time);
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(targetPos.pos()));
            tradingStation.interact(villager);
        }
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Villager villager, long time) {
        var optional = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);

        if (optional.isEmpty()) {
            return false;
        }

        var pos = optional.get();
        return pos.dimension() == level.dimension() && pos.pos().closerToCenterThan(villager.position(), DISTANCE)
                && level.getBlockEntity(pos.pos()) instanceof TradingStationBlockEntity tradingStation
                && tradingStation.canInteract(villager);
    }
}
