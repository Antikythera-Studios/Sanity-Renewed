package guivnf.sanity_renewed.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Eye-glow layer for vanilla farm animals that only renders when the local player's
 * sanity is past {@link Blackout#THRESHOLD}. Built off the EyesLayer base.
 */
public final class BlackoutEyesLayer<T extends LivingEntity, M extends EntityModel<T>> extends EyesLayer<T, M>
{
    private final RenderType renderType;

    public BlackoutEyesLayer(RenderLayerParent<T, M> parent, ResourceLocation texture)
    {
        super(parent);
        this.renderType = RenderType.eyes(texture);
    }

    @Override
    public @NotNull RenderType renderType()
    {
        return renderType;
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffer, int packedLight,
                       T entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float age, float yaw, float pitch)
    {
        if (Blackout.isLocalPlayerInsane())
            super.render(pose, buffer, packedLight, entity, limbSwing, limbSwingAmount, partialTick, age, yaw, pitch);
    }
}
