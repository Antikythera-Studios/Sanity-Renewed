package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.SanityProcessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity
{
    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void sanity_renewed$completeUsingItem(CallbackInfo ci)
    {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!(self instanceof ServerPlayer sp)) return;
        ItemStack stack = self.getUseItem();
        if (stack == null || stack.isEmpty()) return;
        SanityProcessor.handlePlayerUsedItem(sp, stack);
    }
}
