package net.cjsah.plugin.mcspider

import kotlinx.coroutines.*
import net.cjsah.console.Console
import net.cjsah.console.Console.getBot
import net.cjsah.console.Permission
import net.cjsah.console.command.Command
import net.cjsah.console.command.CommandManager
import net.cjsah.console.command.argument.IntArgument
import net.cjsah.console.command.argument.StringArgument
import net.cjsah.console.command.source.CommandSource
import net.cjsah.console.plugin.Plugin
import net.cjsah.plugin.mcspider.GetBugs.getBugs
import net.cjsah.plugin.mcspider.GetServer.getServer
import net.cjsah.plugin.mcspider.GetVersion.getVersion

class McSpider : Plugin() {

    override fun onPluginLoad() {
        Config.init(this)
    }

    @DelicateCoroutinesApi
    override fun onBotStarted() {
        CommandManager.register { dispatcher ->
            dispatcher.register(CommandManager.literal("mcv").requires { source ->
                source.hasPermission(Permission.HELPER)
            }.executes("获取最新mc版本") { context ->
                context.source.sendFeedBack(getVersion(getBot()))
                Command.SUCCESSFUL
            })
        }

        CommandManager.register { dispatcher ->
            dispatcher.register(CommandManager.literal("server").then(CommandManager.argument("address", StringArgument.string()).requires { source ->
                        source.hasPermission(Permission.HELPER)
                    }.executes("获取某服务器的信息") { context ->
                        context.source.sendFeedBack(getServer(StringArgument.getString(context, "address")))
                        Command.SUCCESSFUL
                    }
                )
            )
        }

        CommandManager.register { dispatcher ->
            dispatcher.register(CommandManager.literal("mc").then(CommandManager.argument("value", IntArgument.integer(0)).requires { source: CommandSource<*> ->
                        source.hasPermission(Permission.HELPER)
                    }.executes("获取 mc bugs") { context ->
                        context.source.sendFeedBack(getBugs(IntArgument.getInteger(context, "value")))
                        Command.SUCCESSFUL
                    }
                )
            )
        }

        GlobalScope.launch(Dispatchers.Unconfined) {
            delay(1)
            while (true) {
                getVersion(getBot())
                delay(180000)
            }
        }

    }

    override fun onBotStopped() {
    }

    override fun onPluginUnload() {
    }
}