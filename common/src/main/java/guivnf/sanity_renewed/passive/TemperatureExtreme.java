package guivnf.sanity_renewed.passive;

import guivnf.sanity_renewed.capability.ISanity;
import guivnf.sanity_renewed.compat.SurvivalCompat;
import guivnf.sanity_renewed.config.ConfigProxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class TemperatureExtreme implements IPassiveSanitySource
{
    @Override
    public float get(@NotNull ServerPlayer player, @NotNull ISanity cap, @NotNull ResourceLocation dim)
    {
        int lvl = SurvivalCompat.getTemperatureLevel(player);
        if (lvl == SurvivalCompat.TEMP_UNAVAILABLE)
            return 0;
        if (lvl == SurvivalCompat.TEMP_HOT && ConfigProxy.getAffectHot(dim))
            return ConfigProxy.getHot(dim);
        if (lvl == SurvivalCompat.TEMP_COLD && ConfigProxy.getAffectCold(dim))
            return ConfigProxy.getCold(dim);
        return 0;
    }
}
