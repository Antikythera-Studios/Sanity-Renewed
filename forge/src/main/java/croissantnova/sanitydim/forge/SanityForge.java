package croissantnova.sanitydim.forge;

import croissantnova.sanitydim.SanityMod;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            modBus.addListener((FMLClientSetupEvent event) -> SanityMod.clientInit());
        }
    }
}
