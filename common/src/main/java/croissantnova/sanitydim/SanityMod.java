package croissantnova.sanitydim;

import croissantnova.sanitydim.config.ConfigManager;
import croissantnova.sanitydim.item.ItemRegistry;
import croissantnova.sanitydim.sound.SoundRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SanityMod
{
    public static final String MOD_ID = "sanitydim";
    public static final String MOD_NAME = "Sanity: Descent Into Madness";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    static
    {
        ConfigManager.init();
    }

    private SanityMod() {}

    public static void init()
    {
        ConfigManager.register();
        SoundRegistry.init();
        ItemRegistry.init();
        croissantnova.sanitydim.net.PacketHandler.init();
        croissantnova.sanitydim.event.EventHandler.register();
        LOGGER.info("[{}] common init complete", MOD_ID);
    }

    public static void clientInit()
    {
        LOGGER.info("[{}] client init complete", MOD_ID);
    }
}
