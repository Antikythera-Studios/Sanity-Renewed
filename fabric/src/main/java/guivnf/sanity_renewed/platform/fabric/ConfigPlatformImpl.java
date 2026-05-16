package guivnf.sanity_renewed.platform.fabric;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

public final class ConfigPlatformImpl
{
    private ConfigPlatformImpl() {}

    public static void registerConfig(String modId, ModConfig.Type type, ForgeConfigSpec spec, String fileName)
    {
        ForgeConfigRegistry.INSTANCE.register(modId, type, spec, fileName);
    }

    public static void onConfigLoading(String modId, Runnable handler)
    {
        ModConfigEvents.loading(modId).register(config -> handler.run());
        ModConfigEvents.reloading(modId).register(config -> handler.run());
    }

    public static Path getConfigDir()
    {
        return FabricLoader.getInstance().getConfigDir();
    }
}
