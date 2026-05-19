package guivnf.sanity_renewed.platform.fabric;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.nio.file.Path;

public final class ConfigPlatformImpl
{
    private ConfigPlatformImpl() {}

    public static void registerCommonConfig(String modId, ModConfigSpec spec, String fileName)
    {
        NeoForgeConfigRegistry.INSTANCE.register(modId, ModConfig.Type.COMMON, spec, fileName);
    }

    public static void onConfigLoading(String modId, Runnable handler)
    {
        NeoForgeModConfigEvents.loading(modId).register(config -> handler.run());
        NeoForgeModConfigEvents.reloading(modId).register(config -> handler.run());
    }

    public static Path getConfigDir()
    {
        return FabricLoader.getInstance().getConfigDir();
    }
}
