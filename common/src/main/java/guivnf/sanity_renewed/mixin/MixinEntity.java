package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.capability.Sanity;
import guivnf.sanity_renewed.capability.SanityHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity
{
    @Shadow protected Vec3 stuckSpeedMultiplier;

    @Inject(method = "move",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;multiply(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
                    shift = At.Shift.BY,
                    by = 2))
    private void sanity_renewed$move(MoverType type, Vec3 pos, CallbackInfo ci)
    {
        if ((Object) this instanceof ServerPlayer)
        {
            Sanity s = SanityHolder.get((ServerPlayer) (Object) this);
            if (s != null)
                s.setStuckMotionMultiplier(this.stuckSpeedMultiplier);
        }
    }
}
