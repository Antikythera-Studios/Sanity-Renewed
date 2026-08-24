package guivnf.sanity_renewed.capability;

import net.minecraft.world.phys.Vec3;

import java.util.Map;

public interface IPersistentSanity
{
    int[] getActiveSourcesCooldowns();

    Map<Integer, Integer> getItemCooldowns();

    Map<Integer, Integer> getBrokenBlocksCooldowns();

    void setEnderManAngerTimer(int value);

    int getEnderManAngerTimer();

    void setGarlandTimer(int value);

    int getGarlandTimer();

    void setInnerEntityKills(int value);

    int getInnerEntityKills();

    void setInnerEntityKillDecay(int value);

    int getInnerEntityKillDecay();

    void setStuckMotionMultiplier(Vec3 multiplier);

    Vec3 getStuckMotionMultiplier();
}