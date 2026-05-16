package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.SanityProcessor;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel
{
    @Inject(method = "wakeUpAllPlayers", at = @At("HEAD"))
    private void sanity_renewed$wakeUpAllPlayers(CallbackInfo ci)
    {
        SanityProcessor.handlePlayerSlept((ServerLevel)(Object)this);
    }
}
