package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.capability.Sanity;
import guivnf.sanity_renewed.capability.SanityCarrier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class MixinPlayer implements SanityCarrier
{
    @Unique
    private final Sanity sanity_renewed$sanity = new Sanity();

    @Override
    public Sanity sanity_renewed$getSanity()
    {
        return sanity_renewed$sanity;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void sanity_renewed$writeSanity(CompoundTag tag, CallbackInfo ci)
    {
        CompoundTag sub = new CompoundTag();
        sanity_renewed$sanity.serializeNBT(sub);
        tag.put("sanity_renewed:sanity", sub);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void sanity_renewed$readSanity(CompoundTag tag, CallbackInfo ci)
    {
        if (tag.contains("sanity_renewed:sanity", 10))
        {
            sanity_renewed$sanity.deserializeNBT(tag.getCompound("sanity_renewed:sanity"));
        }
    }
}
