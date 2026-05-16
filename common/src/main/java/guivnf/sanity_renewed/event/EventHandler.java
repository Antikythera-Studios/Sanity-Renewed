package guivnf.sanity_renewed.event;

import guivnf.sanity_renewed.SanityProcessor;
import guivnf.sanity_renewed.command.SanityCommand;
import guivnf.sanity_renewed.passive.Jukebox;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class EventHandler
{
    private EventHandler() {}

    public static void register()
    {
        TickEvent.PLAYER_POST.register(EventHandler::onPlayerTick);
        TickEvent.SERVER_LEVEL_POST.register(SanityProcessor::tickLevel);

        EntityEvent.LIVING_HURT.register(EventHandler::onLivingHurt);
        EntityEvent.LIVING_DEATH.register(EventHandler::onLivingDeath);

        PlayerEvent.PLAYER_ADVANCEMENT.register((player, advancement) ->
                SanityProcessor.handlePlayerGotAdvancement(player, advancement));
        PlayerEvent.CHANGE_DIMENSION.register((player, oldDim, newDim) ->
                SanityProcessor.handlePlayerChangedDimensions(player));

        BlockEvent.BREAK.register((level, pos, state, player, xp) ->
        {
            if (player != null)
                SanityProcessor.handlePlayerMinedBlock(
                        player, pos, state, state.getBlock(),
                        player.hasCorrectToolForDrops(state));
            return EventResult.pass();
        });

        CommandRegistrationEvent.EVENT.register((dispatcher, registry, env) ->
                SanityCommand.register(dispatcher));

        LifecycleEvent.SERVER_STOPPING.register(server ->
        {
            Jukebox.JUKEBOXES.clear();
            Jukebox.UNSETTLING_JUKEBOXES.clear();
        });
    }

    private static void onPlayerTick(Player player)
    {
        if (player instanceof ServerPlayer sp)
            SanityProcessor.tickPlayer(sp);
    }

    private static EventResult onLivingHurt(LivingEntity entity, net.minecraft.world.damagesource.DamageSource source, float amount)
    {
        if (entity instanceof ServerPlayer sp)
        {
            SanityProcessor.handlePlayerHurt(sp, amount);
        }
        else if (entity instanceof Animal animal && source.getEntity() instanceof ServerPlayer sp)
        {
            SanityProcessor.handlePlayerHurtAnimal(sp, animal, amount);
        }
        return EventResult.pass();
    }

    private static EventResult onLivingDeath(LivingEntity entity, net.minecraft.world.damagesource.DamageSource source)
    {
        if (entity instanceof TamableAnimal ta && ta.getOwnerUUID() != null)
        {
            Level level = entity.level();
            if (level instanceof net.minecraft.server.level.ServerLevel sl)
            {
                ServerPlayer owner = sl.getServer().getPlayerList().getPlayer(ta.getOwnerUUID());
                if (owner != null)
                    SanityProcessor.handlePlayerPetDeath(owner, ta);
            }
        }
        return EventResult.pass();
    }
}
