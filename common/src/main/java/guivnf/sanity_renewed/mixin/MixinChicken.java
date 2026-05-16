package guivnf.sanity_renewed.mixin;

import guivnf.sanity_renewed.entity.goal.AvoidInsanePlayerGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chicken.class)
public abstract class MixinChicken extends Animal
{
    protected MixinChicken(EntityType<? extends Animal> type, Level level) { super(type, level); }

    @Inject(method = "registerGoals", at = @At("HEAD"))
    private void sanity_renewed$avoidInsane(CallbackInfo ci)
    {
        this.goalSelector.addGoal(-1, new AvoidInsanePlayerGoal(this, 6.0f, 1.5d, 1.6d));
    }
}
