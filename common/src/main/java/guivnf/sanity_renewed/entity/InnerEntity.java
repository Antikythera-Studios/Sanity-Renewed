package guivnf.sanity_renewed.entity;

import guivnf.sanity_renewed.SanityProcessor;
import guivnf.sanity_renewed.capability.InnerEntityCapImpl;
import guivnf.sanity_renewed.capability.Sanity;
import guivnf.sanity_renewed.capability.SanityHolder;
import guivnf.sanity_renewed.config.ConfigProxy;
import guivnf.sanity_renewed.sound.SoundRegistry;
import net.minecraft.server.level.ServerPlayer;
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

    public boolean isVisibleTo(Player player)
    {
        if (player == null || isInvisibleTo(player))
            return false;

        if (ConfigProxy.getSaneSeeInnerEntities(player.level().dimension().location())
                || player.isCreative() || player.isSpectator())
            return true;

        if (m_data.getPlayerTargetUUID() != null && m_data.getPlayerTargetUUID().equals(player.getUUID()))
            return true;

        Sanity s = SanityHolder.get(player);
        return s != null && s.getSanity() >= SanityProcessor.SANITY_VISIBLE_THRESHOLD;
    }

    public boolean canBeAttackedBy(Player player)
    {
        if (player == null)
            return false;

        if (ConfigProxy.getSaneSeeInnerEntities(player.level().dimension().location())
                || player.isCreative() || player.isSpectator()
                || getTarget() == player)
            return true;

        Sanity s = SanityHolder.get(player);
        return s != null && s.getSanity() >= SanityProcessor.SANITY_TARGET_THRESHOLD;
    }

    @Override
    public boolean skipAttackInteraction(Entity entity)
    {
        if (entity instanceof Player player)
            return !canBeAttackedBy(player);
        return super.skipAttackInteraction(entity);
    }

    @Override
    public boolean hurt(@NotNull DamageSource damageSource, float amount)
    {
        if (!level().isClientSide && damageSource.getEntity() instanceof Player player && !canBeAttackedBy(player))
            return false;
        return super.hurt(damageSource, amount);
    }

    @Override
    public void die(@NotNull DamageSource damageSource)
    {
        if (!level().isClientSide && damageSource.getEntity() instanceof ServerPlayer player)
        {
            Sanity s = SanityHolder.get(player);
            if (s != null)
            {
                SanityProcessor.addSanity(s, ConfigProxy.getInnerEntityKill(player.level().dimension().location()), player);
                s.setInnerEntityKills(s.getInnerEntityKills() + 1);
            }
        }
        super.die(damageSource);
    }

    @Override
    public boolean isPushable()
    {
        return false;
    }

    @Override
    protected void doPush(@NotNull Entity entity)
    {
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
