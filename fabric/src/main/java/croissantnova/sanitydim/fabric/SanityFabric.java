package croissantnova.sanitydim.fabric;

import croissantnova.sanitydim.SanityMod;
import net.fabricmc.api.ModInitializer;

public final class SanityFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        SanityMod.init();
    }
}
