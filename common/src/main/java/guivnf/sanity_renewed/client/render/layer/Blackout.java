package guivnf.sanity_renewed.client.render.layer;

import guivnf.sanity_renewed.SanityMod;
import guivnf.sanity_renewed.capability.Sanity;
import guivnf.sanity_renewed.capability.SanityHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * Texture+threshold constants for the blackout-eye effect, plus the shared
 * "is the local player insane enough to see it" check that all the per-animal
 * layers use.
 */
public final class Blackout
{
    public static final float THRESHOLD = .7f;

    public static final ResourceLocation CHICKEN        = tex("chicken_blackout.png");
    public static final ResourceLocation CHICKEN_EYES   = tex("chicken_blackout_eyes.png");
    public static final ResourceLocation COW            = tex("cow_blackout.png");
    public static final ResourceLocation COW_EYES       = tex("cow_blackout_eyes.png");
    public static final ResourceLocation PIG            = tex("pig_blackout.png");
    public static final ResourceLocation PIG_EYES       = tex("pig_blackout_eyes.png");
    public static final ResourceLocation SHEEP          = tex("sheep_blackout.png");
    public static final ResourceLocation SHEEP_EYES     = tex("sheep_blackout_eyes.png");
    public static final ResourceLocation SHEEP_FUR      = tex("sheep_fur_blackout.png");

    private Blackout() {}

    private static ResourceLocation tex(String name)
    {
        return ResourceLocation.fromNamespaceAndPath(SanityMod.MOD_ID, "textures/entity/" + name);
    }

    public static boolean isLocalPlayerInsane()
    {
        Player p = Minecraft.getInstance().player;
        if (p == null || p.isCreative() || p.isSpectator()) return false;
        Sanity s = SanityHolder.get(p);
        return s != null && s.getSanity() >= THRESHOLD;
    }

    /**
     * Returns {@code replacement} when the local player is insane enough to see the
     * blackout variant, otherwise the original texture.
     */
    public static ResourceLocation pick(ResourceLocation original, ResourceLocation replacement)
    {
        return isLocalPlayerInsane() ? replacement : original;
    }
}
