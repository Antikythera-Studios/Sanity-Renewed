package guivnf.sanity_renewed.entity.goal;

import guivnf.sanity_renewed.SanityProcessor;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

public class HurtByInsanePlayerGoal extends HurtByTargetGoal
{
    private final float m_sanityThreshold;

    public HurtByInsanePlayerGoal(PathfinderMob mob, float sanityThreshold)
    {
        super(mob);
        m_sanityThreshold = sanityThreshold;
    }

    public HurtByInsanePlayerGoal(PathfinderMob mob)
    {
        this(mob, -1f);
    }

    @Override
    public boolean canUse()
    {
        return SanityProcessor.isValidInsaneTarget(mob.getLastHurtByMob(), m_sanityThreshold) && super.canUse();
    }

    @Override
    public boolean canContinueToUse()
    {
        return SanityProcessor.isValidInsaneTarget(mob.getTarget(), m_sanityThreshold) && super.canContinueToUse();
    }
}
