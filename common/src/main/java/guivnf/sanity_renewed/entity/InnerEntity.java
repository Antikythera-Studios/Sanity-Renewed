package guivnf.sanity_renewed.entity;

import guivnf.sanity_renewed.capability.InnerEntityCapImpl;
import guivnf.sanity_renewed.capability.Sanity;
import guivnf.sanity_renewed.capability.SanityHolder;
import guivnf.sanity_renewed.config.ConfigProxy;
import guivnf.sanity_renewed.sound.SoundRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class InnerEntity extends Monster
{
    /**
     * Carries the inner entity's view/target state so it can be replicated to clients.
     * Replaces the Forge capability with an instance field — same semantics, simpler.
     */
    private final InnerEntityCapImpl m_data = new InnerEntityCapImpl();

    protected InnerEntity(EntityType<? extends Monster> entityType, Level level)
    {
        super(entityType, level);
    }

    public InnerEntityCapImpl getData()
    {
        return m_data;
    }

    @Override
    public boolean skipAttackInteraction(Entity entity)
    {
        if (entity instanceof Player player
                && !ConfigProxy.getSaneSeeInnerEntities(player.level().dimension().location())
                && !(player.isCreative() || player.isSpectator())
                && getTarget() != player)
        {
            Sanity s = SanityHolder.get(player);
            return s != null && s.getSanity() < .6f;
        }
        return super.skipAttackInteraction(entity);
    }

    @Override
    public boolean shouldDropExperience()
    {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource)
    {
        return SoundRegistry.INNER_ENTITY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundRegistry.INNER_ENTITY_HURT.get();
    }
}
