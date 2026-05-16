package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.SanityProcessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownEgg.class)
public abstract class MixinThrownEgg extends ThrowableItemProjectile
{
    private MixinThrownEgg(EntityType<? extends ThrowableItemProjectile> type, Level level)
    {
        super(type, level);
    }

    @Inject(method = "onHit", at = @At(value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void sanity_renewed$onHit(HitResult hitResult, CallbackInfo ci)
    {
        if (getOwner() instanceof ServerPlayer player)
            SanityProcessor.handlePlayerSpawnedChicken(player);
    }
}
