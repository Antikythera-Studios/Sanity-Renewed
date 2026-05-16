package guivnf.sanity_renewed.client;

import guivnf.sanity_renewed.SanityMod;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class ItemTooltipHelper
{
    private ItemTooltipHelper() {}

    public static void showTooltipOnShift(List<Component> components, String itemId)
    {
        if (Screen.hasShiftDown())
            components.add(Component.translatable("item." + SanityMod.MOD_ID + "." + itemId + ".tooltip"));
        else
            components.add(Component.translatable("item." + SanityMod.MOD_ID + ".tooltip.press_shift"));
    }
}
