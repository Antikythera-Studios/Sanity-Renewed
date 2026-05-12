package croissantnova.sanitydim.platform.forge;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public final class ConfigPlatformImpl
{
    private ConfigPlatformImpl() {}

    public static void registerConfig(String modId, ModConfig.Type type, ForgeConfigSpec spec, String fileName)
    {
        ModLoadingContext.get().registerConfig(type, spec, fileName);
    }

    public static void onConfigLoading(String modId, Runnable handler)
    {
        ModContainer container = ModList.get().getModContainerById(modId).orElse(null);
        if (container instanceof FMLModContainer fmc)
        {
            fmc.getEventBus().addListener((ModConfigEvent.Loading event) -> handler.run());
            fmc.getEventBus().addListener((ModConfigEvent.Reloading event) -> handler.run());
        }
    }

    public static Path getConfigDir()
    {
        return FMLPaths.CONFIGDIR.get();
    }
}
