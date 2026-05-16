package guivnf.sanity_renewed.item;

import guivnf.sanity_renewed.client.ItemTooltipHelper;
import guivnf.sanity_renewed.item.material.FlowerArmorMaterial;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class GarlandItem extends ArmorItem
{
    public GarlandItem()
    {
        super(new FlowerArmorMaterial(), ArmorItem.Type.HELMET,
                new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @Nullable Level level,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag)
    {
        super.appendHoverText(stack, level, tooltip, flag);
        ItemTooltipHelper.showTooltipOnShift(tooltip, "garland");
    }
}
