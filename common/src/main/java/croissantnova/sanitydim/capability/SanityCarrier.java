package croissantnova.sanitydim.capability;

/**
 * Implemented by {@code Player} via mixin. Replaces the Forge
 * capability lookup so the same accessor works on both loaders.
 */
public interface SanityCarrier
{
    Sanity sanitydim$getSanity();
}
