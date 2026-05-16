package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.client.render.layer.Blackout;
import guivnf.sanity_renewed.client.render.layer.BlackoutEyesLayer;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SheepRenderer.class)
public abstract class MixinSheepRenderer extends MobRenderer<Sheep, SheepModel<Sheep>>
{
    public MixinSheepRenderer(EntityRendererProvider.Context ctx, SheepModel<Sheep> model, float shadowRadius)
    {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;)V", at = @At("TAIL"))
    private void sanity_renewed$addBlackoutLayer(EntityRendererProvider.Context ctx, CallbackInfo ci)
    {
        this.addLayer(new BlackoutEyesLayer<>(this, Blackout.SHEEP_EYES));
    }

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Sheep;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("TAIL"), cancellable = true)
    private void sanity_renewed$swapTexture(Sheep entity, CallbackInfoReturnable<ResourceLocation> ci)
    {
        if (Blackout.isLocalPlayerInsane()) ci.setReturnValue(Blackout.SHEEP);
    }
}
