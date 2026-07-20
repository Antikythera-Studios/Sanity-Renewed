package guivnf.sanity_renewed.net;

import guivnf.sanity_renewed.SanityMod;
import guivnf.sanity_renewed.capability.Sanity;
import guivnf.sanity_renewed.capability.SanityHolder;
import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class PacketHandler
{
    public static final ResourceLocation SANITY_SYNC = new ResourceLocation(SanityMod.MOD_ID, "sanity_sync");

    private PacketHandler() {}

    public static void initClient()
    {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SANITY_SYNC, (buf, ctx) ->
        {
            float sanity = buf.readFloat();
            float passive = buf.readFloat();
            ctx.queue(() ->
            {
                if (ctx.getEnvironment() != Env.CLIENT) return;
                Player player = ctx.getPlayer();
                if (player == null) return;
                Sanity s = SanityHolder.get(player);
                if (s == null) return;
                s.setSanity(sanity);
                s.setPassiveIncrease(passive);
                s.setDirty(false);
            });
        });
    }

    public static void sendSanityToPlayer(ServerPlayer player, Sanity sanity)
    {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        sanity.serialize(buf);
        NetworkManager.sendToPlayer(player, SANITY_SYNC, buf);
    }
}
