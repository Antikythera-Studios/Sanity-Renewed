package guivnf.sanity_renewed.passive;

import guivnf.sanity_renewed.capability.ISanity;
import guivnf.sanity_renewed.compat.SurvivalCompat;
import guivnf.sanity_renewed.config.ConfigProxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalInt;

public class Thirsty implements IPassiveSanitySource
{
    @Override
    public float get(@NotNull ServerPlayer player, @NotNull ISanity cap, @NotNull ResourceLocation dim)
    {
        if (!ConfigProxy.getAffectThirsty(dim))
            return 0;
        OptionalInt thirst = SurvivalCompat.getThirst(player);
        if (thirst.isEmpty())
            return 0;
        return thirst.getAsInt() <= ConfigProxy.getThirstyThreshold(dim)
                ? ConfigProxy.getThirsty(dim)
                : 0;
    }
}
