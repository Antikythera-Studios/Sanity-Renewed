package guivnf.sanity_renewed.capability;

import java.util.UUID;

public interface IInnerEntityCap
{
    boolean hasTarget();

    void setHasTarget(boolean value);

    UUID getPlayerTargetUUID();

    void setPlayerTargetUUID(UUID value);
}