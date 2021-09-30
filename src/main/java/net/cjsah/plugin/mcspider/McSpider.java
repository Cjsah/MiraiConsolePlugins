package net.cjsah.plugin.mcspider;

import net.cjsah.console.Console;
import net.cjsah.console.Permission;
import net.cjsah.console.command.Command;
import net.cjsah.console.command.CommandManager;
import net.cjsah.console.command.argument.IntArgument;
import net.cjsah.console.command.argument.StringArgument;
import net.cjsah.console.plugin.Plugin;

public class McSpider extends Plugin {
    @Override
    protected void onPluginLoad() {
        Config.INSTANCE.init(this);
    }

    @Override
    public void onBotStarted() {
        CommandManager.register(dispatcher ->
                dispatcher.register(CommandManager.literal("mcv").requires(source -> source.hasPermission(Permission.HELPER)).executes("获取最新mc版本", context -> {
                    context.getSource().sendFeedBack(GetVersion.INSTANCE.getVersion(Console.INSTANCE.getBot()));
                    return Command.SUCCESSFUL;
                }))
        );

        CommandManager.register(dispatcher ->
                dispatcher.register(CommandManager.literal("server").then(CommandManager.argument("address", StringArgument.string()).requires(source -> source.hasPermission(Permission.HELPER)).executes("获取某服务器的信息", context -> {
                    context.getSource().sendFeedBack(GetServer.INSTANCE.getServer(StringArgument.getString(context, "address")));
                    return Command.SUCCESSFUL;
                })))
        );

        CommandManager.register(dispatcher ->
                dispatcher.register(CommandManager.literal("mc").then(CommandManager.argument("value", IntArgument.integer(0)).requires(source -> source.hasPermission(Permission.HELPER)).executes("获取 mc bugs", context -> {
                    context.getSource().sendFeedBack(GetBugs.INSTANCE.getBugs(IntArgument.getInteger(context, "value")));
                    return Command.SUCCESSFUL;
                })))
        );

        GetVersion.INSTANCE.scope();
    }

    @Override
    public void onBotStopped() {

    }

    @Override
    public void onPluginUnload() {

    }
}
