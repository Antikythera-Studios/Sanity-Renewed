package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.client.SanityPostChain;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer
{
    @Inject(method = "render(Lnet/minecraft/client/DeltaTracker;Z)V",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V"))
    private void sanity_renewed$applyPostChain(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci)
    {
        if (!renderLevel) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        SanityPostChain.tickClock(partialTick);
        SanityPostChain.render(partialTick);
    }

    @Inject(method = "resize", at = @At("TAIL"))
    private void sanity_renewed$resize(int width, int height, CallbackInfo ci)
    {
        SanityPostChain.onResize(width, height);
    }
}
