package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.SanityProcessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingHook.class)
public abstract class MixinFishingHook
{
    @Shadow private int nibble;

    @Shadow public abstract Player getPlayerOwner();

    @Inject(method = "retrieve", at = @At("HEAD"))
    private void sanity_renewed$retrieve(ItemStack stack, CallbackInfoReturnable<Integer> ci)
    {
        if (nibble > 0 && getPlayerOwner() instanceof ServerPlayer sp)
            SanityProcessor.handlePlayerFishedItem(sp);
    }
}
