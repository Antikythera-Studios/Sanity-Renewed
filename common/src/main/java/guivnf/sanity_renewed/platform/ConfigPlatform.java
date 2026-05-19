package guivnf.sanity_renewed.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.nio.file.Path;

/**
 * Cross-loader config plumbing. The Architectury transformer rewrites calls to
 * these stub methods to call the {@code ConfigPlatformImpl} in the active loader's package.
 */
public final class ConfigPlatform
{
    private ConfigPlatform() {}

    @ExpectPlatform
    public static void registerCommonConfig(String modId, ModConfigSpec spec, String fileName)
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
