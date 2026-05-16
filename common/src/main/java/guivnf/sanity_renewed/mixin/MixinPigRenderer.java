package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.client.render.layer.Blackout;
import guivnf.sanity_renewed.client.render.layer.BlackoutEyesLayer;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PigRenderer.class)
public abstract class MixinPigRenderer extends MobRenderer<Pig, PigModel<Pig>>
{
    public MixinPigRenderer(EntityRendererProvider.Context ctx, PigModel<Pig> model, float shadowRadius)
    {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;)V", at = @At("TAIL"))
    private void sanity_renewed$addBlackoutLayer(EntityRendererProvider.Context ctx, CallbackInfo ci)
    {
        this.addLayer(new BlackoutEyesLayer<>(this, Blackout.PIG_EYES));
    }

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Pig;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("TAIL"), cancellable = true)
    private void sanity_renewed$swapTexture(Pig entity, CallbackInfoReturnable<ResourceLocation> ci)
    {
        if (Blackout.isLocalPlayerInsane()) ci.setReturnValue(Blackout.PIG);
    }
}
