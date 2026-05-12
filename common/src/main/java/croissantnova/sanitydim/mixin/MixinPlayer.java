package croissantnova.sanitydim.mixin;

import croissantnova.sanitydim.capability.Sanity;
import croissantnova.sanitydim.capability.SanityCarrier;
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
    private final Sanity sanitydim$sanity = new Sanity();

    @Override
    public Sanity sanitydim$getSanity()
    {
        return sanitydim$sanity;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void sanitydim$writeSanity(CompoundTag tag, CallbackInfo ci)
    {
        CompoundTag sub = new CompoundTag();
        sanitydim$sanity.serializeNBT(sub);
        tag.put("sanitydim:sanity", sub);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void sanitydim$readSanity(CompoundTag tag, CallbackInfo ci)
    {
        if (tag.contains("sanitydim:sanity", 10))
        {
            sanitydim$sanity.deserializeNBT(tag.getCompound("sanitydim:sanity"));
        }
    }
}
