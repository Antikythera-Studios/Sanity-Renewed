package guivnf.sanity_renewed.item.material;

import guivnf.sanity_renewed.SanityMod;
import guivnf.sanity_renewed.sound.SoundRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public final class FlowerArmorMaterial implements ArmorMaterial
{
    @Override
    public int getDurabilityForType(ArmorItem.@NotNull Type type)
    {
        return 0;
    }

    @Override
    public int getDefenseForType(ArmorItem.@NotNull Type type)
    {
        return 0;
    }

    @Override
    public int getEnchantmentValue()
    {
        return 0;
    }

    @Override
    public @NotNull SoundEvent getEquipSound()
    {
        return SoundRegistry.FLOWERS_EQUIP.get();
    }

    @Override
    public @NotNull Ingredient getRepairIngredient()
    {
        return Ingredient.of(ItemTags.SMALL_FLOWERS);
    }

    @Override
    public @NotNull String getName()
    {
        return SanityMod.MOD_ID + ":flower";
    }

    @Override
    public float getToughness()
    {
        return 0;
    }

    @Override
    public float getKnockbackResistance()
    {
        return 0;
    }
}
