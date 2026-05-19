package guivnf.sanity_renewed.item.material;

import guivnf.sanity_renewed.SanityMod;
import guivnf.sanity_renewed.sound.SoundRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;

public final class FlowerArmorMaterial
{
    private FlowerArmorMaterial() {}

    public static final Holder<ArmorMaterial> HOLDER = Holder.direct(new ArmorMaterial(
            new EnumMap<>(ArmorItem.Type.class),
            0,
            SoundRegistry.FLOWERS_EQUIP,
            () -> Ingredient.of(ItemTags.SMALL_FLOWERS),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(SanityMod.MOD_ID, "flower"), "", false)),
            0.0F,
            0.0F
    ));
}
