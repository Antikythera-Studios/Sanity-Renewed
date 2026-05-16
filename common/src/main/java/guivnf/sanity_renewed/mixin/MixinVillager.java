package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.capability.Sanity;
import guivnf.sanity_renewed.capability.SanityHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public abstract class MixinVillager
{
    @Shadow protected abstract void setUnhappy();

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void sanity_renewed$mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> ci)
    {
        if (player.level().isClientSide()) return;
        Sanity s = SanityHolder.get(player);
        if (s != null && s.getSanity() >= .6f)
        {
            this.setUnhappy();
            ci.setReturnValue(InteractionResult.PASS);
        }
    }
}
