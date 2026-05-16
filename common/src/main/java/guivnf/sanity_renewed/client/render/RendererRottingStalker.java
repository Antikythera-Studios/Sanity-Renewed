package guivnf.sanity_renewed.client.render;

import guivnf.sanity_renewed.SanityMod;
import guivnf.sanity_renewed.entity.RottingStalker;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RendererRottingStalker extends RendererInnerEntity<RottingStalker>
{
    public RendererRottingStalker(EntityRendererProvider.Context ctx)
    {
        super(ctx, new DefaultedEntityGeoModel<>(new ResourceLocation(SanityMod.MOD_ID, "rotting_stalker"), true));
        addRenderLayer(new CustomGlowingGeoLayer<>(this));
    }
}
