package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.SanityProcessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractVillager.class)
public abstract class MixinAbstractVillager
{
    @Inject(method = "notifyTrade", at = @At("HEAD"))
    private void sanity_renewed$notifyTrade(MerchantOffer offer, CallbackInfo ci)
    {
        AbstractVillager self = (AbstractVillager)(Object)this;
        Player p = self.getTradingPlayer();
        if (p instanceof ServerPlayer sp)
            SanityProcessor.handlePlayerTradedWithVillager(sp);
    }
}
