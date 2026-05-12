package croissantnova.sanitydim.mixin;

import croissantnova.sanitydim.capability.Sanity;
import croissantnova.sanitydim.capability.SanityHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TemptGoal.class)
public abstract class MixinTemptGoal
{
    @Inject(method = "shouldFollow", at = @At("RETURN"), cancellable = true)
    private void sanitydim$shouldFollow(LivingEntity living, CallbackInfoReturnable<Boolean> ci)
    {
        if (living.level().isClientSide()) return;
        if (!(living instanceof ServerPlayer sp)) return;
        Sanity s = SanityHolder.get(sp);
        if (s != null && s.getSanity() >= .5f)
            ci.setReturnValue(false);
    }
}
