package guivnf.sanity_renewed.forge;

import guivnf.sanity_renewed.SanityMod;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(SanityMod.MOD_ID)
public final class SanityForge
{
    public SanityForge()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(SanityMod.MOD_ID, modBus);

        SanityMod.init();

        // Architectury's EntityRendererRegistry drains its registration queue from a
        // @SubscribeEvent for EntityRenderersEvent.RegisterRenderers. Mod load order
        // means our @SubscribeEvent vs. Architectury's is non-deterministic, so we
        // queue the registrations during construction (well before any event fires)
        // rather than chaining off the same event Architectury listens to.
        if (FMLEnvironment.dist == Dist.CLIENT)
            SanityMod.clientInit();
    }
}
