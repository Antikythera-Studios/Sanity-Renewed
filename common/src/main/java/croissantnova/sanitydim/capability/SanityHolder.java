package croissantnova.sanitydim.capability;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public final class SanityHolder
{
    private SanityHolder() {}

    @Nullable
    public static Sanity get(Player player)
    {
        if (player instanceof SanityCarrier carrier)
        {
            return carrier.sanitydim$getSanity();
        }
        return null;
    }
}
