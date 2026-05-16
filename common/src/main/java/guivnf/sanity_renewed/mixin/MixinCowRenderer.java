package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.client.render.layer.Blackout;
import guivnf.sanity_renewed.client.render.layer.BlackoutEyesLayer;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CowRenderer.class)
public abstract class MixinCowRenderer extends MobRenderer<Cow, CowModel<Cow>>
{
    public MixinCowRenderer(EntityRendererProvider.Context ctx, CowModel<Cow> model, float shadowRadius)
    {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;)V", at = @At("TAIL"))
    private void sanity_renewed$addBlackoutLayer(EntityRendererProvider.Context ctx, CallbackInfo ci)
    {
        this.addLayer(new BlackoutEyesLayer<>(this, Blackout.COW_EYES));
    }

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Cow;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("TAIL"), cancellable = true)
    private void sanity_renewed$swapTexture(Cow entity, CallbackInfoReturnable<ResourceLocation> ci)
    {
        if (Blackout.isLocalPlayerInsane()) ci.setReturnValue(Blackout.COW);
    }
}
