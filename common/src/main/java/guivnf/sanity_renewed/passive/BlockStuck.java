package guivnf.sanity_renewed.passive;

import guivnf.sanity_renewed.capability.IPersistentSanity;
import guivnf.sanity_renewed.capability.ISanity;
import guivnf.sanity_renewed.config.ConfigProxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

public class BlockStuck implements IPassiveSanitySource
{
    @Override
    public float get(@NotNull ServerPlayer player, @NotNull ISanity cap, @NotNull ResourceLocation dim)
    {
        if (cap instanceof IPersistentSanity ps && ps.getStuckMotionMultiplier() != Vec3.ZERO)
        {
            ps.setStuckMotionMultiplier(Vec3.ZERO);
            return ConfigProxy.getBlockStuck(dim);
        }

        return 0;
    }
}
