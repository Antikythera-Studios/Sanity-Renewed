package guivnf.sanity_renewed.passive;

import guivnf.sanity_renewed.capability.ISanity;
import guivnf.sanity_renewed.capability.Sanity;
import guivnf.sanity_renewed.capability.SanityHolder;
import guivnf.sanity_renewed.config.ConfigProxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class PlayerCompany implements IPassiveSanitySource
{
    @Override
    public float get(@NotNull ServerPlayer player, @NotNull ISanity cap, @NotNull ResourceLocation dim)
    {
        Vec3 offset = new Vec3(8.0d, 8.0d, 8.0d);
        float sane = ConfigProxy.getSanePlayerCompany(dim);
        float insane = ConfigProxy.getInsanePlayerCompany(dim);

        float result = 0;
        for (Player p : player.level().getNearbyPlayers(TargetingConditions.forNonCombat(), player, new AABB(
                player.position().subtract(offset),
                player.position().add(offset))))
        {
            Sanity s = SanityHolder.get(p);
            if (s == null) continue;
            if (s.getSanity() >= .5f)
                return insane;
            result = sane;
        }
        return result;
    }
}
