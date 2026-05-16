package guivnf.sanity_renewed.fabric;

import guivnf.sanity_renewed.SanityMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class SanityFabricClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        SanityMod.clientInit();
    }
}
