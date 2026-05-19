package guivnf.sanity_renewed.platform.neoforge;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.nio.file.Path;

public final class ConfigPlatformImpl
{
    private ConfigPlatformImpl() {}

    public static void registerCommonConfig(String modId, ModConfigSpec spec, String fileName)
    {
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, spec, fileName);
    }

    public static void onConfigLoading(String modId, Runnable handler)
    {
        ModContainer container = ModList.get().getModContainerById(modId).orElse(null);
        if (container != null)
        {
            container.getEventBus().addListener((ModConfigEvent.Loading event) -> handler.run());
            container.getEventBus().addListener((ModConfigEvent.Reloading event) -> handler.run());
        }
    }

    public static Path getConfigDir()
    {
        return FMLPaths.CONFIGDIR.get();
    }
}
