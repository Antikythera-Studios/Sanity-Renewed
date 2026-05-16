package guivnf.sanity_renewed.passive;

import guivnf.sanity_renewed.capability.ISanity;
import guivnf.sanity_renewed.config.ConfigProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import org.jetbrains.annotations.NotNull;

public class InWaterOrRain implements IPassiveSanitySource
{
    @Override
    public float get(@NotNull ServerPlayer player, @NotNull ISanity cap, @NotNull ResourceLocation dim)
    {
        boolean inRain = isInRain(player);
        boolean inWater = !inRain && player.isInWater();
        if (inRain || (inWater && ConfigProxy.getAffectInWater(dim)))
            return ConfigProxy.getRaining(dim);

        return 0;
    }

    private static boolean isInRain(ServerPlayer player)
    {
        BlockPos pos = player.blockPosition();
        return player.level().isRainingAt(pos)
                || player.level().isRainingAt(BlockPos.containing(pos.getX(), player.getBoundingBox().maxY, pos.getZ()));
    }
}