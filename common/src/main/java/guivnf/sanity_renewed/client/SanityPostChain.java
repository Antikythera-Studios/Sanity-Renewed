package guivnf.sanity_renewed.client;

import guivnf.sanity_renewed.SanityMod;
import guivnf.sanity_renewed.capability.Sanity;
import guivnf.sanity_renewed.capability.SanityHolder;
import guivnf.sanity_renewed.config.ConfigProxy;
import guivnf.sanity_renewed.mixin.AccessorPostChain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.io.IOException;

/**
 * Lazily compiles the {@code sanity_renewed:shaders/post/insanity.json} post-chain and
 * runs it once per frame when the local player is insane enough to warrant
 * desaturation + chromatic aberration. All uniforms are read straight off the
 * Sanity field so this class holds no mutable state of its own.
 */
public final class SanityPostChain
{
    private static final ResourceLocation CHAIN_ID = new ResourceLocation(SanityMod.MOD_ID, "shaders/post/insanity.json");
    private static final float SANITY_FADE_START = .4f;
    private static final float SANITY_FADE_END   = .8f;

    private static PostChain CHAIN;
    private static float TIME_SECONDS;

    private SanityPostChain() {}

    public static void invalidate()
    {
        if (CHAIN != null)
        {
            CHAIN.close();
            CHAIN = null;
        }
    }

    public static void onResize(int w, int h)
    {
        if (CHAIN != null)
            CHAIN.resize(w, h);
    }

    public static void tickClock(float partialTick)
    {
        TIME_SECONDS += partialTick / 20f;
    }

    public static void render(float partialTick)
    {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer p = mc.player;
        if (p == null || p.isCreative() || p.isSpectator()) return;
        if (!ConfigProxy.getRenderPost(p.level().dimension().location())) return;

        Sanity s = SanityHolder.get(p);
        if (s == null) return;

        float sanity = s.getSanity();
        if (sanity < SANITY_FADE_START) return;
        float fade = Mth.clamp(Mth.inverseLerp(sanity, SANITY_FADE_START, SANITY_FADE_END), 0f, 1f);

        PostChain chain = ensureChain(mc);
        if (chain == null) return;

        applyUniforms(chain, fade);
        chain.process(partialTick);
    }

    private static PostChain ensureChain(Minecraft mc)
    {
        if (CHAIN != null) return CHAIN;
        try
        {
            CHAIN = new PostChain(
                    mc.getTextureManager(),
                    mc.getResourceManager(),
                    mc.getMainRenderTarget(),
                    CHAIN_ID);
            CHAIN.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
            return CHAIN;
        }
        catch (IOException e)
        {
            SanityMod.LOGGER.error("Failed to load post chain {}", CHAIN_ID, e);
            CHAIN = null;
            return null;
        }
    }

    private static void applyUniforms(PostChain chain, float fade)
    {
        for (PostPass pass : ((AccessorPostChain) chain).sanity_renewed$getPasses())
        {
            String name = pass.getName();
            if (name.endsWith("insanity"))
            {
                set(pass, "DesaturateFactor", fade * .69f);
                set(pass, "SpreadFactor",     fade * 1.43f);
            }
            else if (name.endsWith("chromatical"))
            {
                set(pass, "TimeTotal", TIME_SECONDS);
                set(pass, "Factor",    fade * .1f);
            }
        }
    }

    private static void set(PostPass pass, String uniform, float value)
    {
        var u = pass.getEffect().safeGetUniform(uniform);
        if (u != null) u.set(value);
    }
}
