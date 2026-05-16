package guivnf.sanity_renewed.passive;

import guivnf.sanity_renewed.capability.ISanity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import org.jetbrains.annotations.NotNull;

public interface IPassiveSanitySource
{
    float get(@NotNull ServerPlayer player, @NotNull ISanity cap, @NotNull ResourceLocation dim);
}