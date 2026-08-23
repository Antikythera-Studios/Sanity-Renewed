package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.SanityProcessor;
import guivnf.sanity_renewed.capability.Sanity;
import guivnf.sanity_renewed.capability.SanityHolder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.gameevent.GameEvent;
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

    @Inject(method = "thunderHit", at = @At("HEAD"))
    private void sanity_renewed$thunderHit(ServerLevel level, LightningBolt bolt, CallbackInfo ci)
    {
        if ((Object) this instanceof ServerPlayer sp)
            SanityProcessor.handlePlayerStruckByLightning(sp);
    }

    @Inject(method = "gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/Entity;)V",
            at = @At("HEAD"))
    private void sanity_renewed$gameEvent(Holder<GameEvent> event, Entity source, CallbackInfo ci)
    {
        if (event.value() == GameEvent.SHEAR.value() && source instanceof ServerPlayer sp)
            SanityProcessor.handlePlayerUsedShears(sp);
    }
}
