package me.ramidzkh.vts.ai;

import com.google.common.collect.ImmutableMap;
import me.ramidzkh.vts.VillagerTradingStation;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class StrollToStation extends Behavior<Villager> {

    private final float speedModifier;
    private final int closeEnoughDist;
    private final int maxDistanceFromPoi;
    private long nextOkStartTime;
    @Nullable
    private GlobalPos targetPos;

    public StrollToStation(float speedModifier, int closeEnoughDist, int maxDistanceFromPoi) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                VillagerTradingStation.STATION_SITE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.JOB_SITE,
                MemoryStatus.VALUE_PRESENT));
        this.speedModifier = speedModifier;
        this.closeEnoughDist = closeEnoughDist;
        this.maxDistanceFromPoi = maxDistanceFromPoi;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, Villager villager) {
        List<GlobalPos> list;
        Optional<List<GlobalPos>> sites = villager.getBrain().getMemory(VillagerTradingStation.STATION_SITE);
        Optional<GlobalPos> job = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        if (sites.isPresent() && job.isPresent() && !(list = sites.get()).isEmpty()) {
            this.targetPos = list.get(serverLevel.getRandom().nextInt(list.size()));
            return this.targetPos != null && serverLevel.dimension() == this.targetPos.dimension()
                    && job.get().pos().closerToCenterThan(villager.position(), this.maxDistanceFromPoi);
        }
        return false;
    }

    @Override
    protected void start(ServerLevel serverLevel, Villager villager, long time) {
        if (time > this.nextOkStartTime && this.targetPos != null) {
            villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                    new WalkTarget(this.targetPos.pos(), this.speedModifier, this.closeEnoughDist));
            this.nextOkStartTime = time + 100L;
        }
    }
}
