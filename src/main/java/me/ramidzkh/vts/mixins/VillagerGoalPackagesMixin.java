package me.ramidzkh.vts.mixins;

import com.mojang.datafixers.util.Pair;
import me.ramidzkh.vts.VillagerTradingStation;
import me.ramidzkh.vts.ai.TradeAtStation;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.List;

@Mixin(VillagerGoalPackages.class)
public abstract class VillagerGoalPackagesMixin {

    @ModifyArg(method = "getWorkPackage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/RunOne;<init>(Ljava/util/List;)V"), index = 0)
    private static List<Pair<BehaviorControl<? super Villager>, Integer>> modify(
            List<Pair<BehaviorControl<? super Villager>, Integer>> list) {
        var copy = new ArrayList<Pair<BehaviorControl<? super Villager>, Integer>>();
        var added = false;

        for (var pair : list) {
            copy.add(pair);

            if (pair.getFirst() instanceof WorkAtPoi) {
                // Trading is equivalent to working
                copy.add(Pair.of(new TradeAtStation(), pair.getSecond()));
            } else if (pair.getSecond() == 5 && !added) {
                // STATION_SITE is equivalent to SECONDARY_JOB_SITE
                // Note that @ModifyArg cannot capture the arguments of the target method like some other injectors can
                // Inline f = 0.5f
                copy.add(Pair.of(StrollToPoiList.create(VillagerTradingStation.STATION_SITE, 0.5f, 1, 6,
                        MemoryModuleType.JOB_SITE), 5));
                added = true;
            }
        }

        return copy;
    }
}
