package croissantnova.sanitydim.mixin;

import croissantnova.sanitydim.capability.Sanity;
import croissantnova.sanitydim.capability.SanityHolder;
import croissantnova.sanitydim.client.render.layer.Blackout;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
public abstract class MixinAnimal
{
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void sanitydim$mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> ci)
    {
        if (player.level().isClientSide()) return;
        Sanity s = SanityHolder.get(player);
        if (s != null && s.getSanity() >= Blackout.THRESHOLD)
            ci.setReturnValue(InteractionResult.PASS);
    }
}
