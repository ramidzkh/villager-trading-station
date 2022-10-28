package me.ramidzkh.vts.mixins;

import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Supplier;

@Mixin(SensorType.class)
public interface SensorTypeAccessor {

    @Invoker("<init>")
    static <U extends Sensor<?>> SensorType<U> create(Supplier<U> factory) {
        throw new AssertionError();
    }
}
