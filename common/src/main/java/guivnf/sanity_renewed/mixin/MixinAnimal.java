package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.SanityProcessor;
import guivnf.sanity_renewed.capability.Sanity;
import guivnf.sanity_renewed.capability.SanityHolder;
import guivnf.sanity_renewed.client.render.layer.Blackout;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
public abstract class MixinAnimal
{
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void sanity_renewed$mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> ci)
    {
        if (player.level().isClientSide()) return;
        Sanity s = SanityHolder.get(player);
        if (s != null && s.getSanity() >= Blackout.THRESHOLD)
            ci.setReturnValue(InteractionResult.PASS);
    }

    @Inject(method = "spawnChildFromBreeding", at = @At("HEAD"))
    private void sanity_renewed$spawnChildFromBreeding(ServerLevel level, Animal partner, CallbackInfo ci)
    {
        Animal self = (Animal)(Object)this;
        ServerPlayer cause = self.getLoveCause();
        if (cause == null) cause = partner.getLoveCause();
        if (cause != null)
            SanityProcessor.handlePlayerBredAnimals(cause);
    }
}
