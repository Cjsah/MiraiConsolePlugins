package net.cjsah.plugin.mcspider

import net.cjsah.console.command.Command
import net.cjsah.console.command.CommandManager
import net.cjsah.console.command.argument.IntArgument
import net.cjsah.console.command.argument.StringArgument
import net.cjsah.console.command.source.CommandSource
import net.cjsah.console.plugin.Plugin
import net.cjsah.plugin.mcspider.Utils.checkVersion
import net.cjsah.plugin.mcspider.Utils.getBugs
import net.cjsah.plugin.mcspider.Utils.getServer
import net.cjsah.plugin.mcspider.Utils.getVersion
import java.util.Timer
import java.util.TimerTask

class McSpider : Plugin() {

    private val timer = Timer()

    override fun onPluginLoad() {
        Config.init(this)
    }

    override fun onBotStarted() {
        CommandManager.register { dispatcher ->
            dispatcher.register(CommandManager.literal("mcv").requires { source ->
                source.canUse(this)
            }.executes("获取最新mc版本") { context ->
                context.getSource().sendFeedBack(getVersion())
                Command.SUCCESSFUL
            })
        }

        CommandManager.register { dispatcher ->
            dispatcher.register(CommandManager.literal("server")
                .then(CommandManager.argument("address", StringArgument.string()).requires { source ->
                    source.canUse(this)
                }.executes("获取某服务器的信息") { context ->
                    context.getSource().sendFeedBack(getServer(StringArgument.getString(context, "address")))
                    Command.SUCCESSFUL
                })
            )
        }

        CommandManager.register { dispatcher ->
            dispatcher.register(CommandManager.literal("mc")
                .then(CommandManager.argument("value", IntArgument.integer(0))
                    .requires { source: CommandSource<*> ->
                        source.canUse(this)
                    }.executes("获取 mc bugs") { context ->
                        context.getSource().sendFeedBack(getBugs(IntArgument.getInteger(context, "value")))
                        Command.SUCCESSFUL
                    }
                )
            )
        }

        timer.schedule(object : TimerTask() {
            override fun run() {
                checkVersion(this@McSpider)
            }
        }, 1, 180000)

    }

    override fun onBotStopped() {
        timer.cancel()
    }

    override fun onPluginUnload() {
    }
}