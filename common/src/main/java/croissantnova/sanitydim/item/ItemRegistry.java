package croissantnova.sanitydim.item;

import croissantnova.sanitydim.SanityMod;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public final class ItemRegistry
{
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(SanityMod.MOD_ID, Registries.ITEM);

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(SanityMod.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<Item> GARLAND = ITEMS.register("garland", GarlandItem::new);

    public static final RegistrySupplier<CreativeModeTab> MAIN_TAB = TABS.register("main",
            () -> CreativeTabRegistry.create(
                    Component.translatable("itemGroup." + SanityMod.MOD_ID),
                    () -> GARLAND.get().getDefaultInstance()));

    private ItemRegistry() {}

    public static void init()
    {
        ITEMS.register();
        TABS.register();
    }

    public static ResourceLocation id(String path)
    {
        return new ResourceLocation(SanityMod.MOD_ID, path);
    }
}
