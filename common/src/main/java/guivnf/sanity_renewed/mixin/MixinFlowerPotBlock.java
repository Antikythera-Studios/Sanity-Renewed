package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.SanityProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowerPotBlock.class)
public abstract class MixinFlowerPotBlock
{
    @Inject(method = "use", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;)V"))
    private void sanity_renewed$use(BlockState state, Level level, BlockPos pos, Player player,
                               InteractionHand hand, BlockHitResult hit,
                               CallbackInfoReturnable<InteractionResult> ci)
    {
        if (player instanceof ServerPlayer sp && sp.getItemInHand(hand).is(ItemTags.FLOWERS))
            SanityProcessor.handlePlayerPottedFlower(sp);
    }
}
