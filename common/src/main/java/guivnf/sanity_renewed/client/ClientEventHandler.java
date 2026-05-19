package guivnf.sanity_renewed.client;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientTickEvent;

/**
 * Aggregates all client-side event wiring. Called once from {@link guivnf.sanity_renewed.SanityMod#clientInit()}.
 */
public final class ClientEventHandler
{
    private ClientEventHandler() {}

    public static void register()
    {
        ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register(level ->
        {
            SanityPostChain.invalidate();
            SanityClientState.INSTANCE.onLevelChange();
            SanityAmbience.reset();
        });

        ClientTickEvent.CLIENT_POST.register(mc ->
        {
            if (mc.level == null) return;
            SanityClientState.INSTANCE.tick(1f);
            SanityAmbience.tick();
        });

        ClientGuiEvent.RENDER_HUD.register((graphics, deltaTracker) ->
                SanityHud.renderAll(graphics, deltaTracker.getGameTimeDeltaPartialTick(false)));
    }
}
