package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.SanityProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmBlock.class)
public abstract class MixinFarmBlock
{
    @Inject(method = "turnToDirt", at = @At("HEAD"))
    private static void sanity_renewed$turnToDirt(Entity entity, BlockState state, Level level, BlockPos pos, CallbackInfo ci)
    {
        if (entity instanceof ServerPlayer sp)
            SanityProcessor.handlePlayerTrampledFarmland(sp);
    }
}
