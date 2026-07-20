package guivnf.sanity_renewed.passive;

import guivnf.sanity_renewed.capability.ISanity;
import guivnf.sanity_renewed.config.ConfigProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import org.jetbrains.annotations.NotNull;

public class Lightness implements IPassiveSanitySource
{
    @Override
    public float get(@NotNull ServerPlayer player, @NotNull ISanity cap, @NotNull ResourceLocation dim)
    {
        if (player.level().getMaxLocalRawBrightness(BlockPos.containing(player.getEyePosition())) >= ConfigProxy.getLightnessThreshold(dim))
            return ConfigProxy.getLightness(dim);

        return 0;
    }
}