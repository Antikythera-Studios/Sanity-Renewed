package guivnf.sanity_renewed.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import guivnf.sanity_renewed.entity.InnerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RendererInnerEntity<T extends InnerEntity & GeoAnimatable> extends GeoEntityRenderer<T>
{
    private final Minecraft m_mc = Minecraft.getInstance();

    public RendererInnerEntity(EntityRendererProvider.Context renderManager, GeoModel<T> model)
    {
        super(renderManager, model);
    }

    public boolean shouldRender(T entity)
    {
        return entity != null && m_mc.player != null && entity.isVisibleTo(m_mc.player);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight)
    {
        if (shouldRender(entity))
            super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource,
                                    float partialTick)
    {
        return RenderType.entityTranslucent(texture, false);
    }
}
