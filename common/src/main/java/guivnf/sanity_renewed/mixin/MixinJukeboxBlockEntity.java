package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.passive.Jukebox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(JukeboxSongPlayer.class)
public abstract class MixinJukeboxBlockEntity
{
    @Shadow
    @Final
    private BlockPos blockPos;

    @Inject(method = "play(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/Holder;)V",
            at = @At("TAIL"))
    private void sanity_renewed$play(LevelAccessor level, Holder<JukeboxSong> song, CallbackInfo ci)
    {
        if (level != null && !level.isClientSide())
            Jukebox.handleJukeboxStartedPlaying(blockPos, song);
    }

    @Inject(method = "stop(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/level/block/state/BlockState;)V",
            at = @At("HEAD"))
    private void sanity_renewed$stop(LevelAccessor level, BlockState state, CallbackInfo ci)
    {
        if (level != null && !level.isClientSide())
            Jukebox.handleJukeboxStoppedPlaying(blockPos);
    }
}
