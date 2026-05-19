package guivnf.sanity_renewed.passive;

import guivnf.sanity_renewed.capability.ISanity;
import guivnf.sanity_renewed.config.ConfigProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DirtPath implements IPassiveSanitySource
{
    private final Map<ServerPlayer, Vec3> m_playerToPos = new HashMap<>();

    @Override
    public float get(@NotNull ServerPlayer player, @NotNull ISanity cap, @NotNull ResourceLocation dim)
    {
        BlockState on = player.getBlockStateOn();
        BlockPos bpos = player.blockPosition();
        ServerLevel level = player.serverLevel();
        if (on.is(Blocks.DIRT_PATH) || on.is(BlockTags.WOOL_CARPETS) ||
                (on.isAir() && (level.getBlockState(bpos.below()).is(Blocks.DIRT_PATH) ||
                        level.getBlockState(bpos.below()).is(BlockTags.WOOL_CARPETS) ||
                        level.getBlockState(bpos.below().below()).is(Blocks.DIRT_PATH) ||
                        level.getBlockState(bpos.below().below()).is(BlockTags.WOOL_CARPETS))))
        {
            Vec3 pos = player.position();
            Vec3 prevPos = m_playerToPos.getOrDefault(player, Vec3.ZERO);
            if (!pos.equals(prevPos))
            {
                m_playerToPos.put(player, new Vec3(pos.x, pos.y, pos.z));
                return ConfigProxy.getDirtPath(dim);
            }
        }
        return 0;
    }
}
