package guivnf.sanity_renewed.capability;

import guivnf.sanity_renewed.ICompoundTagSerializable;
import net.minecraft.resources.ResourceLocation;

public interface ISanity extends ICompoundTagSerializable
{
    float getSanity();

    void setSanity(float value);
}