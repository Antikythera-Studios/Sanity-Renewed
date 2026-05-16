package guivnf.sanity_renewed.fabric;

import guivnf.sanity_renewed.SanityMod;
import net.fabricmc.api.ModInitializer;

public final class SanityFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        SanityMod.init();
    }
}
