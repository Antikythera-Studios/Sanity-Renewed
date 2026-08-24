package guivnf.sanity_renewed.passive;

import guivnf.sanity_renewed.capability.ISanity;
import guivnf.sanity_renewed.config.ConfigProxy;
import guivnf.sanity_renewed.entity.InnerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Monster implements IPassiveSanitySource
{
    @Override
    public float get(@NotNull ServerPlayer player, @NotNull ISanity cap, @NotNull ResourceLocation dim)
    {
        AABB box = new AABB(
                player.position().add(new Vec3(-8.0f, -8.0f, -8.0f)),
                player.position().add(new Vec3(8.0f, 8.0f, 8.0f)));

        float result = 0;
        float monster = ConfigProxy.getMonster(dim);
        if (monster != 0.0f)
        {
            List<net.minecraft.world.entity.monster.Monster> monstersAround = player.level().getEntities(
                    EntityTypeTest.forClass(net.minecraft.world.entity.monster.Monster.class),
                    box,
                    m -> player.hasLineOfSight(m)
                            && (!(m instanceof InnerEntity inner) || inner.isVisibleTo(player)));
            if (!monstersAround.isEmpty())
                result = monster;
            for (net.minecraft.world.entity.monster.Monster m : monstersAround)
            {
                if (m.getTarget() != null && m.getTarget().is(player))
                {
                    result *= 2;
                    break;
                }
            }
        }
        return result;
    }
}
