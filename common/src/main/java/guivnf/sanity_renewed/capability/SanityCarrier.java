package guivnf.sanity_renewed.capability;

/**
 * Implemented by {@code Player} via mixin. Replaces the Forge
 * capability lookup so the same accessor works on both loaders.
 */
public interface SanityCarrier
{
    Sanity sanity_renewed$getSanity();
}
