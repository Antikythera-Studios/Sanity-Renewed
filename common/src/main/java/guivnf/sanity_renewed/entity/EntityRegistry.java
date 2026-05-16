package guivnf.sanity_renewed.entity;

import guivnf.sanity_renewed.SanityMod;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public final class EntityRegistry
{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(SanityMod.MOD_ID, Registries.ENTITY_TYPE);

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final RegistrySupplier<EntityType<RottingStalker>> ROTTING_STALKER =
            (RegistrySupplier) ENTITY_TYPES.register("rotting_stalker",
                    () -> EntityType.Builder.of(RottingStalker::new, MobCategory.MONSTER)
                            .sized(1f, 2.9f)
                            .fireImmune()
                            .build("rotting_stalker"));

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final RegistrySupplier<EntityType<SneakingTerror>> SNEAKING_TERROR =
            (RegistrySupplier) ENTITY_TYPES.register("sneaking_terror",
                    () -> EntityType.Builder.of(SneakingTerror::new, MobCategory.MONSTER)
                            .sized(1.3f, 4f)
                            .fireImmune()
                            .build("sneaking_terror"));

    public static final List<Supplier<EntityType<? extends InnerEntity>>> INNER_ENTITIES = Arrays.asList(
            ROTTING_STALKER::get,
            SNEAKING_TERROR::get);

    private EntityRegistry() {}

    public static void init()
    {
        ENTITY_TYPES.register();
    }

    public static void registerAttributes()
    {
        EntityAttributeRegistry.register(ROTTING_STALKER, RottingStalker::buildAttributes);
        EntityAttributeRegistry.register(SNEAKING_TERROR, SneakingTerror::buildAttributes);
    }
}
