package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.client.SanityPostChain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer
{
    @Inject(method = "render(FJZ)V",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V"))
    private void sanity_renewed$applyPostChain(float partialTick, long finishNano, boolean renderLevel, CallbackInfo ci)
    {
        if (!renderLevel) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        SanityPostChain.tickClock(partialTick);
        SanityPostChain.render(partialTick);
    }

    @Inject(method = "resize", at = @At("TAIL"))
    private void sanity_renewed$resize(int width, int height, CallbackInfo ci)
    {
        SanityPostChain.onResize(width, height);
    }
}
