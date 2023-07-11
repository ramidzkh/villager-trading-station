package me.ramidzkh.vts.mixins;

import com.mojang.datafixers.util.Pair;
import me.ramidzkh.vts.ai.StrollToStation;
import me.ramidzkh.vts.ai.TradeAtStation;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.ai.behavior.WorkAtPoi;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.List;

@Mixin(VillagerGoalPackages.class)
public abstract class VillagerGoalPackagesMixin {

    @ModifyArg(method = "getWorkPackage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/RunOne;<init>(Ljava/util/List;)V"), index = 0)
    private static List<Pair<Behavior<? super Villager>, Integer>> modify(
            List<Pair<Behavior<? super Villager>, Integer>> list) {
        var copy = new ArrayList<Pair<Behavior<? super Villager>, Integer>>();

        for (var pair : list) {
            copy.add(pair);

            if (pair.getFirst() instanceof WorkAtPoi) {
                // Trading is equivalent to working
                copy.add(Pair.of(new TradeAtStation(), pair.getSecond()));
            } else if (pair.getSecond() == 5) { // StrollToPoi(should be List but whatever)
                // Note that @ModifyArg cannot capture the arguments of the target method like some other injectors can
                // Inline f = 0.5f
                copy.add(Pair.of(new StrollToStation(0.5f, 1, 6), 5));
            }
        }

        return copy;
    }
}
