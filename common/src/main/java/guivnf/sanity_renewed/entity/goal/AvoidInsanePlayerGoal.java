package guivnf.sanity_renewed.entity.goal;

import guivnf.sanity_renewed.capability.Sanity;
import guivnf.sanity_renewed.capability.SanityHolder;
import guivnf.sanity_renewed.client.render.layer.Blackout;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class AvoidInsanePlayerGoal extends AvoidEntityGoal<Player>
{
    private final TargetingConditions m_targetingConditions;

    public AvoidInsanePlayerGoal(PathfinderMob mob, float maxDistance, double walkSpeedModifier, double sprintSpeedModifier)
    {
        super(mob, Player.class, maxDistance, walkSpeedModifier, sprintSpeedModifier);
        m_targetingConditions = TargetingConditions.forNonCombat()
                .ignoreInvisibilityTesting()
                .ignoreLineOfSight()
                .range(maxDistance)
                .selector(ent ->
                {
                    if (!(ent instanceof Player player) || player.isCreative() || player.isSpectator())
                        return false;
                    Sanity s = SanityHolder.get(player);
                    return s != null && s.getSanity() >= Blackout.THRESHOLD;
                });
    }

    @Override
    public boolean canUse()
    {
        this.toAvoid = this.mob.level().getNearestEntity(
                this.mob.level().getEntitiesOfClass(this.avoidClass,
                        this.mob.getBoundingBox().inflate(this.maxDist, 3.0D, this.maxDist),
                        p -> true),
                m_targetingConditions, this.mob,
                this.mob.getX(), this.mob.getY(), this.mob.getZ());
        if (this.toAvoid == null) return false;

        Vec3 away = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.toAvoid.position());
        if (away == null) return false;
        if (this.toAvoid.distanceToSqr(away.x, away.y, away.z) < this.toAvoid.distanceToSqr(this.mob))
            return false;
        this.path = this.pathNav.createPath(away.x, away.y, away.z, 0);
        return this.path != null;
    }
}
