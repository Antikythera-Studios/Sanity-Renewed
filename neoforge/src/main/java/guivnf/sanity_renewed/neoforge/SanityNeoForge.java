package guivnf.sanity_renewed.neoforge;

import guivnf.sanity_renewed.SanityMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(SanityMod.MOD_ID)
public final class SanityNeoForge
{
    public SanityNeoForge(IEventBus modBus, ModContainer container, Dist dist)
    {
        SanityMod.init();

        if (dist == Dist.CLIENT)
            SanityMod.clientInit();
    }
}
