package me.ramidzkh.vts.mixins;

import com.mojang.datafixers.util.Pair;
import me.ramidzkh.vts.ai.StrollToStation;
import me.ramidzkh.vts.ai.TradeAtStation;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.OneShot;
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
    private static List<Pair<BehaviorControl<? super Villager>, Integer>> modify(
            List<Pair<BehaviorControl<? super Villager>, Integer>> list) {
        var copy = new ArrayList<Pair<BehaviorControl<? super Villager>, Integer>>();
        var oneshots = 0;

        for (var pair : list) {
            copy.add(pair);

            if (pair.getFirst() instanceof WorkAtPoi) {
                copy.add(Pair.of(new TradeAtStation(), 7));
            } else if (pair.getFirst() instanceof OneShot) {
                // After the 3rd oneshot, i.e. StrollToPoiList
                if (++oneshots == 3) {
                    // Note that @ModifyArg cannot capture the arguments of the target method like some other injectors
                    // can, inline f = 0.5f
                    copy.add(Pair.of(new StrollToStation(0.5f, 1, 6), 5));
                }
            }
        }

        return copy;
    }
}
