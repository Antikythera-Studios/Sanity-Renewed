package guivnf.sanity_renewed.passive;

import guivnf.sanity_renewed.capability.ISanity;
import guivnf.sanity_renewed.config.ConfigProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.JukeboxSongs;
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

    public static boolean isSongUnsettling(Holder<JukeboxSong> song)
    {
        return song.is(JukeboxSongs.FIVE)
                || song.is(JukeboxSongs.ELEVEN)
                || song.is(JukeboxSongs.THIRTEEN);
    }

    public static void handleJukeboxStartedPlaying(BlockPos blockPos, Holder<JukeboxSong> song)
    {
        if (song == null) return;
        boolean unsettling = isSongUnsettling(song);
        if (unsettling && !UNSETTLING_JUKEBOXES.contains(blockPos))
            UNSETTLING_JUKEBOXES.add(blockPos);
        else if (!unsettling)
            JUKEBOXES.add(blockPos);
    }

    public static void handleJukeboxStoppedPlaying(BlockPos blockPos)
    {
        while (JUKEBOXES.remove(blockPos)) {/* drain */}
        while (UNSETTLING_JUKEBOXES.remove(blockPos)) {/* drain */}
    }
}
