package me.ramidzkh.vts.mixins;

import com.google.common.collect.ImmutableList;
import me.ramidzkh.vts.VillagerTradingStation;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Villager.class)
public class VillagerMixin {

    @Shadow
    @Final
    @Mutable
    private static ImmutableList<MemoryModuleType<?>> MEMORY_TYPES;

    @Shadow
    @Final
    @Mutable
    private static ImmutableList<SensorType<? extends Sensor<? super Villager>>> SENSOR_TYPES;

    static {
        MEMORY_TYPES = ImmutableList.<MemoryModuleType<?>>builder()
                .addAll(MEMORY_TYPES)
                .add(VillagerTradingStation.STATION_SITE)
                .build();
        SENSOR_TYPES = ImmutableList.<SensorType<? extends Sensor<? super Villager>>>builder()
                .addAll(SENSOR_TYPES)
                .add(VillagerTradingStation.STATION_SENSOR)
                .build();
    }
}
