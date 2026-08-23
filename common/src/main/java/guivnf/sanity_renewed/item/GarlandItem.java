package guivnf.sanity_renewed.item;

import guivnf.sanity_renewed.client.ItemTooltipHelper;
import guivnf.sanity_renewed.item.material.FlowerArmorMaterial;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class GarlandItem extends ArmorItem
{
    public GarlandItem()
    {
        super(FlowerArmorMaterial.HOLDER, ArmorItem.Type.HELMET,
                new Properties().stacksTo(1).durability(150));
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repair)
    {
        return false;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag)
    {
        super.appendHoverText(stack, context, tooltip, flag);
        ItemTooltipHelper.showTooltipOnShift(tooltip, "garland");
    }
}
