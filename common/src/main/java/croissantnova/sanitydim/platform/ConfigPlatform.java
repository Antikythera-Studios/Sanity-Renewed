package croissantnova.sanitydim.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

/**
 * Cross-loader config plumbing. The Architectury transformer rewrites calls to
 * these stub methods to call the {@code ConfigPlatformImpl} in the active loader's package.
 */
public final class ConfigPlatform
{
    private ConfigPlatform() {}

    @ExpectPlatform
    public static void registerConfig(String modId, ModConfig.Type type, ForgeConfigSpec spec, String fileName)
    {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void onConfigLoading(String modId, Runnable handler)
    {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getConfigDir()
    {
        throw new AssertionError();
    }
}
