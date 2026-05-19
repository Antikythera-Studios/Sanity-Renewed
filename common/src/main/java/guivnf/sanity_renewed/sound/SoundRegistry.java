package guivnf.sanity_renewed.sound;

import guivnf.sanity_renewed.SanityMod;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public final class SoundRegistry
{
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(SanityMod.MOD_ID, Registries.SOUND_EVENT);

    public static final RegistrySupplier<SoundEvent> INSANITY          = register("insanity");
    public static final RegistrySupplier<SoundEvent> HEARTBEAT         = register("heartbeat");
    public static final RegistrySupplier<SoundEvent> SWISH             = register("swish");
    public static final RegistrySupplier<SoundEvent> FLOWERS_EQUIP     = register("flowers_equip");
    public static final RegistrySupplier<SoundEvent> INNER_ENTITY_HURT = register("inner_entity_hurt");

    private SoundRegistry() {}

    private static RegistrySupplier<SoundEvent> register(String name)
    {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(SanityMod.MOD_ID, name);
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void init()
    {
        SOUNDS.register();
    }
}
