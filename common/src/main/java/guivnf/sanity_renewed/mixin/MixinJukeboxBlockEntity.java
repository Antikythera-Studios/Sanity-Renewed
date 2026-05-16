package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.passive.Jukebox;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxBlockEntity.class)
public abstract class MixinJukeboxBlockEntity
{
    @Inject(method = "startPlaying", at = @At("TAIL"))
    private void sanity_renewed$startPlaying(CallbackInfo ci)
    {
        JukeboxBlockEntity self = (JukeboxBlockEntity)(Object)this;
        Level level = self.getLevel();
        if (level != null && !level.isClientSide())
            Jukebox.handleJukeboxStartedPlaying(self.getBlockPos(), self.getItem(0));
    }

    @Inject(method = "stopPlaying", at = @At("HEAD"))
    private void sanity_renewed$stopPlaying(CallbackInfo ci)
    {
        JukeboxBlockEntity self = (JukeboxBlockEntity)(Object)this;
        Level level = self.getLevel();
        if (level != null && !level.isClientSide())
            Jukebox.handleJukeboxStoppedPlaying(self.getBlockPos(), self.getItem(0));
    }
}
