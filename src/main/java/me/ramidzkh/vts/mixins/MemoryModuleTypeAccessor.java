package me.ramidzkh.vts.mixins;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(MemoryModuleType.class)
public interface MemoryModuleTypeAccessor {

    @Invoker("<init>")
    static <U> MemoryModuleType<U> create(Optional<Codec<U>> optional) {
        throw new AssertionError();
    }
}
