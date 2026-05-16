package guivnf.sanity_renewed.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import guivnf.sanity_renewed.capability.Sanity;
import guivnf.sanity_renewed.capability.SanityHolder;
import guivnf.sanity_renewed.config.DimensionConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Collections;

public final class SanityCommand
{
    private SanityCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("sanity")
                .requires(stack -> stack.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 100f))
                                .executes(ctx -> setSanity(ctx.getSource(),
                                        Collections.singleton((ServerPlayer) ctx.getSource().getEntityOrException()),
                                        FloatArgumentType.getFloat(ctx, "value"))))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 100f))
                                        .executes(ctx -> setSanity(ctx.getSource(),
                                                EntityArgument.getPlayers(ctx, "targets"),
                                                FloatArgumentType.getFloat(ctx, "value"))))))
                .then(Commands.literal("add")
                        .then(Commands.argument("value", FloatArgumentType.floatArg(-100f, 100f))
                                .executes(ctx -> addSanity(ctx.getSource(),
                                        Collections.singleton((ServerPlayer) ctx.getSource().getEntityOrException()),
                                        FloatArgumentType.getFloat(ctx, "value"))))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("value", FloatArgumentType.floatArg(-100f, 100f))
                                        .executes(ctx -> addSanity(ctx.getSource(),
                                                EntityArgument.getPlayers(ctx, "targets"),
                                                FloatArgumentType.getFloat(ctx, "value"))))))
                .then(Commands.literal("config")
                        .then(Commands.literal("reload")
                                .executes(ctx -> reloadConfig(ctx.getSource())))));
    }

    private static int setSanity(CommandSourceStack stack, Collection<? extends ServerPlayer> targets, float value)
    {
        for (ServerPlayer player : targets)
        {
            Sanity s = SanityHolder.get(player);
            if (s != null) s.setSanity((100f - value) / 100f);
        }
        if (targets.size() == 1)
            stack.sendSuccess(() -> Component.translatable("commands.sanity.set.success.single", targets.iterator().next().getDisplayName(), value), true);
        else
            stack.sendSuccess(() -> Component.translatable("commands.sanity.set.success.multiple", value, targets.size()), true);
        return (int) value;
    }

    private static int addSanity(CommandSourceStack stack, Collection<? extends ServerPlayer> targets, float value)
    {
        for (ServerPlayer player : targets)
        {
            Sanity s = SanityHolder.get(player);
            if (s != null) s.setSanity(s.getSanity() - value / 100f);
        }
        if (targets.size() == 1)
            stack.sendSuccess(() -> Component.translatable("commands.sanity.add.success.single", value, targets.iterator().next().getDisplayName()), true);
        else
            stack.sendSuccess(() -> Component.translatable("commands.sanity.add.success.multiple", targets.size(), value), true);
        return (int) value;
    }

    private static int reloadConfig(CommandSourceStack stack)
    {
        DimensionConfig.init();
        stack.sendSuccess(() -> Component.translatable("commands.sanity.config.reload"), true);
        return 1;
    }
}
