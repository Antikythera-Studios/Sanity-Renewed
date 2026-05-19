package guivnf.sanity_renewed.client.render;

import guivnf.sanity_renewed.SanityMod;
import guivnf.sanity_renewed.entity.SneakingTerror;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RendererSneakingTerror extends RendererInnerEntity<SneakingTerror>
{
    public RendererSneakingTerror(EntityRendererProvider.Context ctx)
    {
        super(ctx, new DefaultedEntityGeoModel<>(ResourceLocation.fromNamespaceAndPath(SanityMod.MOD_ID, "sneaking_terror"), true));
        addRenderLayer(new CustomGlowingGeoLayer<>(this));
    }
}
