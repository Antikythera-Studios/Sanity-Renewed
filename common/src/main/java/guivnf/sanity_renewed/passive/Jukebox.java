package guivnf.sanity_renewed.passive;

import guivnf.sanity_renewed.capability.ISanity;
import guivnf.sanity_renewed.config.ConfigProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Jukebox implements IPassiveSanitySource
{
    public static final List<BlockPos> JUKEBOXES = new ArrayList<>();
    public static final List<BlockPos> UNSETTLING_JUKEBOXES = new ArrayList<>();

    @Override
    public float get(@NotNull ServerPlayer player, @NotNull ISanity cap, @NotNull ResourceLocation dim)
    {
        for (BlockPos blockPos : UNSETTLING_JUKEBOXES)
        {
            if (player.getEyePosition().distanceTo(blockPos.getCenter()) <= 60)
                return ConfigProxy.getJukeboxUnsettling(dim);
        }
        for (BlockPos blockPos : JUKEBOXES)
        {
            if (player.getEyePosition().distanceTo(blockPos.getCenter()) <= 60)
                return ConfigProxy.getJukeboxPleasant(dim);
        }
        return 0;
    }

    public static boolean isMusicDiscUnsettling(Item disc)
    {
        return disc == Items.MUSIC_DISC_5
                || disc == Items.MUSIC_DISC_11
                || disc == Items.MUSIC_DISC_13;
    }

    public static void handleJukeboxStartedPlaying(BlockPos blockPos, ItemStack record)
    {
        if (!record.is(ItemTags.MUSIC_DISCS))
            return;

        boolean unsettling = isMusicDiscUnsettling(record.getItem());
        if (unsettling && !UNSETTLING_JUKEBOXES.contains(blockPos))
            UNSETTLING_JUKEBOXES.add(blockPos);
        else if (!unsettling)
            JUKEBOXES.add(blockPos);
    }

    public static void handleJukeboxStoppedPlaying(BlockPos blockPos, ItemStack record)
    {
        while (JUKEBOXES.remove(blockPos)) {/* drain */}
        while (UNSETTLING_JUKEBOXES.remove(blockPos)) {/* drain */}
    }
}
