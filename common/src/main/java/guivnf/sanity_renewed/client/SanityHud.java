package guivnf.sanity_renewed.client;

import com.mojang.blaze3d.systems.RenderSystem;
import guivnf.sanity_renewed.SanityMod;
import guivnf.sanity_renewed.config.ConfigProxy;
import guivnf.sanity_renewed.config.SanityIndicatorLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Draws the three sanity HUD overlays. Reads everything from {@link SanityClientState}.
 * Pure rendering; no state of its own.
 *
 * <p>The indicator sprite is a 33x24 tile arranged in a 4-column grid on a 256x128 atlas:
 *   col 0 base bg | col 1 flash bg | col 2 brain mask | col 3 brain flash
 * with the upward-passive variant in row 1 and the downward-passive variant in row 2.
 */
public final class SanityHud
{
    public static final ResourceLocation INDICATOR_TEX =
            ResourceLocation.fromNamespaceAndPath(SanityMod.MOD_ID, "textures/sanity_indicator.png");
    public static final ResourceLocation BLOOD_TENDRILS_TEX =
            ResourceLocation.fromNamespaceAndPath(SanityMod.MOD_ID, "textures/overlay/blood_tendrils.png");

    private static final int ATLAS_W = 256;
    private static final int ATLAS_H = 128;
    private static final int SPRITE_W = 33;
    private static final int SPRITE_H = 24;

    private SanityHud() {}

    public static void renderAll(GuiGraphics g, float partialTick)
    {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer p = mc.player;
        if (p == null || p.isCreative() || p.isSpectator()) return;

        SanityClientState st = SanityClientState.INSTANCE;
        ResourceLocation dim = p.level().dimension().location();

        if (ConfigProxy.getRenderIndicator(dim))
            renderIndicator(g, p, st, dim);

        if (st.hint() != null && st.sanity() >= .5f && ConfigProxy.getRenderHint(dim))
            renderHint(g, mc.font, st, dim);

        if (st.bloodTendrilAlpha() > 0f)
            renderBloodTendrils(g, st.bloodTendrilAlpha());
    }

    // --- indicator -------------------------------------------------------

    private static void renderIndicator(GuiGraphics g, LocalPlayer player, SanityClientState st, ResourceLocation dim)
    {
        float scale = ConfigProxy.getIndicatorScale(dim);
        if (scale <= 0f) return;

        int scw = g.guiWidth();
        int sch = g.guiHeight();
        SanityIndicatorLocation loc = ConfigProxy.getIndicatorLocation(dim);

        g.pose().pushPose();
        applyLocationTranslate(g, loc, scw, sch, !player.getOffhandItem().isEmpty());
        g.pose().scale(scale, scale, 1f);

        int x = 0, y = 0;
        if (loc == SanityIndicatorLocation.HOTBAR_LEFT || loc == SanityIndicatorLocation.BOTTOM_RIGHT)
        {
            x = -SPRITE_W; y = -SPRITE_H;
        }
        else if (loc == SanityIndicatorLocation.HOTBAR_RIGHT || loc == SanityIndicatorLocation.BOTTOM_LEFT)
        {
            y = -SPRITE_H;
        }
        else if (loc == SanityIndicatorLocation.TOP_RIGHT)
        {
            x = -SPRITE_W;
        }

        if (ConfigProxy.getTwitchIndicator(dim))
            y += st.indicatorJitterY();

        float sanity = st.sanity();
        int brainTop = Math.round(sanity * (SPRITE_H - 2)) + 1;

        // base background
        g.blit(INDICATOR_TEX, x, y, 0, 0, SPRITE_W, SPRITE_H, ATLAS_W, ATLAS_H);

        // flash overlay when sanity recently changed
        if (st.flashTimer() > 0 && ((int) st.flashTimer() / 3) % 2 == 0)
        {
            g.blit(INDICATOR_TEX, x, y, SPRITE_W, 0, SPRITE_W, SPRITE_H, ATLAS_W, ATLAS_H);
            if (st.flashGain() > 0)
            {
                int flashOffset = Math.round((sanity - st.flashGain()) * (SPRITE_H - 2)) + 1;
                g.blit(INDICATOR_TEX,
                        x, y + flashOffset,
                        SPRITE_W * 3, flashOffset,
                        SPRITE_W, SPRITE_H - flashOffset,
                        ATLAS_W, ATLAS_H);
            }
        }

        // brain
        g.blit(INDICATOR_TEX,
                x, y + brainTop,
                SPRITE_W * 2, brainTop,
                SPRITE_W, SPRITE_H - brainTop,
                ATLAS_W, ATLAS_H);

        // arrow indicating passive direction
        renderArrow(g, st, x, y, brainTop);

        g.pose().popPose();
    }

    private static void renderArrow(GuiGraphics g, SanityClientState st, int x, int y, int brainTop)
    {
        float p = st.passiveIncrease();
        if (p == 0f) return;

        float absp = Math.abs(p);
        boolean strong = absp >= SanityClientState.passiveThreshold();
        float maxArrowTimer = strong ? 23.99f : 15.99f;
        float arrowTimer = Mth.clamp(st.arrowTimer(), 0f, maxArrowTimer);

        int os;
        if (strong)
        {
            os = (arrowTimer >= 12f && arrowTimer <= 15f) || (arrowTimer >= 0f && arrowTimer <= 3f)
                    ? 0 : (((int) arrowTimer / 3) % 2 == 0 ? 2 : 1);
            os *= arrowTimer > 12f ? 1 : -1;
        }
        else
        {
            os = ((int) arrowTimer / 4) % 2;
            os *= arrowTimer > 8f ? 1 : -1;
        }

        int rowU  = strong ? SPRITE_W * 2 : 0;
        int rowU2 = strong ? SPRITE_W * 3 : SPRITE_W;
        int rowV  = p > 0 ? SPRITE_H : SPRITE_H * 2;

        g.blit(INDICATOR_TEX, x, y + os, rowU, rowV, SPRITE_W, SPRITE_H, ATLAS_W, ATLAS_H);
        g.blit(INDICATOR_TEX,
                x, y + brainTop,
                rowU2, rowV + brainTop - os,
                SPRITE_W, SPRITE_H - brainTop + os,
                ATLAS_W, ATLAS_H);
    }

    private static void applyLocationTranslate(GuiGraphics g, SanityIndicatorLocation loc, int scw, int sch, boolean hasOffhand)
    {
        switch (loc)
        {
            case HOTBAR_LEFT   -> g.pose().translate(scw / 2f - 97f - (hasOffhand ? 29f : 0f), sch - 5f, 0f);
            case HOTBAR_RIGHT  -> g.pose().translate(scw / 2f + 97f, sch - 5f, 0f);
            case TOP_LEFT      -> g.pose().translate(5f, 5f, 0f);
            case TOP_RIGHT     -> g.pose().translate(scw - 5f, 5f, 0f);
            case BOTTOM_LEFT   -> g.pose().translate(5f, sch - 5f, 0f);
            case BOTTOM_RIGHT  -> g.pose().translate(scw - 5f, sch - 5f, 0f);
        }
    }

    // --- hint subtitle ---------------------------------------------------

    private static void renderHint(GuiGraphics g, Font font, SanityClientState st, ResourceLocation dim)
    {
        MutableComponent hint = st.hint();
        if (hint == null) return;

        float t = st.hintTimer();
        float maxT = st.hintMaxTimer();
        float blink = ((int) t % 10) / 10f;
        float blinkInv = ((int) t / 10) % 2 == 0 ? blink : 1f - blink;
        boolean fading = t >= maxT - 9f || t < 10f;
        int opacity = Mth.clamp(
                (int) (Mth.lerp(blinkInv, fading ? 0f : .5f, 1f) * 0xFF),
                0x10, 0xEF) << 24;

        g.pose().pushPose();
        g.pose().translate(g.guiWidth() / 2d, g.guiHeight() / 2d, 0d);
        g.pose().scale(2f, 2f, 1f);

        float tx = -font.width(hint) / 2f;
        float ty = -font.lineHeight / 2f;
        if (ConfigProxy.getTwitchHint(dim))
        {
            tx += st.hintJitterX();
            ty += st.hintJitterY();
        }

        RenderSystem.enableBlend();
        g.drawString(font, hint, (int) tx, (int) ty, 0xFFFFFF | opacity, true);
        RenderSystem.disableBlend();

        g.pose().popPose();
    }

    // --- blood tendrils --------------------------------------------------

    private static void renderBloodTendrils(GuiGraphics g, float alpha)
    {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        g.blit(BLOOD_TENDRILS_TEX, 0, 0, 0f, 0f,
                g.guiWidth(), g.guiHeight(),
                g.guiWidth(), g.guiHeight());
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }
}
