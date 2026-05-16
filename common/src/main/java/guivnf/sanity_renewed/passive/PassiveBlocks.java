package guivnf.sanity_renewed.passive;

import guivnf.sanity_renewed.block.BlockStateHelper;
import guivnf.sanity_renewed.capability.ISanity;
import guivnf.sanity_renewed.config.ConfigPassiveBlock;
import guivnf.sanity_renewed.config.ConfigProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PassiveBlocks implements IPassiveSanitySource
{
    @Override
    public float get(@NotNull ServerPlayer player, @NotNull ISanity cap, @NotNull ResourceLocation dim)
    {
        float result = 0;

        for (ConfigPassiveBlock block : ConfigProxy.getPassiveBlocks(dim))
        {
            if (block.m_sanity == 0.0f)
                continue;

            Block regBlock = null;
            if (!block.m_isTag)
            {
                regBlock = BuiltInRegistries.BLOCK.get(block.m_name);
                if (regBlock == null || regBlock.defaultBlockState().isAir())
                    continue;
            }

            boolean flag = false;
            for (float x = (float) player.position().x - block.m_rad; x < player.position().x + block.m_rad; ++x)
            {
                if (flag) break;
                for (float y = (float) player.position().y - block.m_rad; y < player.position().y + block.m_rad; ++y)
                {
                    if (flag) break;
                    for (float z = (float) player.position().z - block.m_rad; z < player.position().z + block.m_rad; ++z)
                    {
                        BlockPos posAt = new BlockPos((int) x, (int) y, (int) z);
                        BlockState stateAt = player.level().getBlockState(posAt);

                        boolean tagMatch = block.m_isTag && stateAt.getTags().anyMatch(tag -> tag.location().equals(block.m_name));
                        boolean blockMatch = regBlock != null && regBlock == stateAt.getBlock();
                        if (tagMatch || blockMatch)
                        {
                            // naturallyGend tracking via SanityLevelChunk capability is deferred;
                            // currently every match is treated as naturally generated.
                            boolean propMismatch = false;
                            for (Map.Entry<String, Boolean> entry : block.m_props.entrySet())
                            {
                                BooleanProperty prop = BlockStateHelper.getBooleanProperty(stateAt, entry.getKey());
                                if (prop != null && stateAt.getValue(prop) != entry.getValue())
                                {
                                    propMismatch = true;
                                    break;
                                }
                            }
                            if (propMismatch)
                                continue;

                            HitResult hit = player.level().clip(new ClipContext(
                                    player.getEyePosition(),
                                    posAt.getCenter(),
                                    ClipContext.Block.COLLIDER,
                                    ClipContext.Fluid.NONE,
                                    player));
                            if (hit.getType() == HitResult.Type.MISS)
                            {
                                result += block.m_sanity;
                                flag = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }
}
