package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.capability.Sanity;
import guivnf.sanity_renewed.capability.SanityHolder;
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
    private void sanity_renewed$shouldFollow(LivingEntity living, CallbackInfoReturnable<Boolean> ci)
    {
        if (living.level().isClientSide()) return;
        if (!(living instanceof ServerPlayer sp)) return;
        Sanity s = SanityHolder.get(sp);
        if (s != null && s.getSanity() >= .5f)
            ci.setReturnValue(false);
    }
}
